package edu.umd.cs.guitar.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A client for working with the Jenkins Remote REST API.
 */
public class JenkinsClient {

    /**
     * A Logger.
     */
    private static Logger logger = LogManager.getLogger(JenkinsClient.class);

    /**
     * We will wait this long between successive calls checking status.
     */
    private static final long DEFAULT_POLLING_INTERVAL = 30000;

    /**
     * HTTP Page not found code.
     */
    private static final int HTTP_NOT_FOUND = 404;

    /**
     * HTTP Internal Server Error code.
     */
    private static final int HTTP_INTERNAL_SERVER_ERROR = 500;

    /**
     * Number of retries per HTTP request.
     */
    private static final int MAX_RETRIES = 5;

    /**
     * The host of the Jenkins master.
     */
    private String host;

    /**
     * The Jenkins port.
     */
    private String port;

    /**
     * The path to the jenkins installation.
     */
    private String path;

    /**
     * The Jenkins user to use for checking status.
     */
    private String user;

    /**
     * The password for the Jenkins user above.
     */
    private String password;

    /**
     * A default constructor with Jenkins server details.
     *
     * @param hostVal     the Jenkins host
     * @param portVal     the port of the Jenkins server
     * @param pathVal     the path to the Jenkins installation
     * @param userVal     a user on the Jenkins machine
     * @param passwordVal the user's password
     */
    public JenkinsClient(final String hostVal, final String portVal,
                         final String pathVal, final String userVal,
                         final String passwordVal) {
        this.host = hostVal;
        this.port = portVal;
        this.path = pathVal;
        this.user = userVal;
        this.password = passwordVal;
    }

    /**
     * Helper method for carrying out an authenticated HTTP Request given URL
     * and Body.
     *
     * @param url  the URL to be requested
     * @param body the body of the request
     * @return the response body
     * @throws IOException if response cannot be read
     */

    private String makeHttpRequest(final String url, final String body) throws
            IOException {
        // This usage of HttpClient based on example:
        // http://hc.apache.org/httpcomponents-client-ga/httpclient/examples
        // /org/apache/http/examples/client/ClientAuthentication.java
        // Basic auth usage based on second example:
        // http://hc.apache.org/httpcomponents-client-ga/httpclient/examples
        // /org/apache/http/examples/client
        // /ClientPreemptiveBasicAuthentication.java

        // Content to be returned
        String ret = null;

        // Register handler for Basic Auth on Jenkins server
        HttpHost targetHost = new HttpHost(host, Integer.parseInt(port),
                "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(targetHost.getHostName(),
                targetHost.getPort()), new UsernamePasswordCredentials(user,
                password));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider).build();
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local
        // auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        // Add AuthCache to the execution context
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        try {
            HttpPost httppost = new HttpPost(url);

            // Add payload, if available
            if (body != null) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("json", body));
                httppost.setEntity(new UrlEncodedFormEntity(nvps));
                logger.debug(httppost.getEntity().toString());
            }

            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(targetHost,
                    httppost, localContext);
            try {
                HttpEntity entity = response.getEntity();
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println(response.getStatusLine().getStatusCode());
                if (entity != null) {
                    System.out.println("Response content length: "
                            + entity.getContentLength());
                }

                if ((response.getStatusLine().getStatusCode()
                        == HTTP_NOT_FOUND) || (response.getStatusLine()
                        .getStatusCode() == HTTP_INTERNAL_SERVER_ERROR)) {
                    ret = null;
                } else {
                    ret = EntityUtils.toString(entity);
                }

                EntityUtils.consume(entity);
            } catch (NoHttpResponseException e) {
                logger.error("HTTP Error: ", e);
                ret = null;
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }

        return ret;
    }

    /**
     * Return the status of a previously submitted job given job name and
     * build number.
     *
     * @param jobName     the job name
     * @param buildNumber the build number
     * @return job JSON content, which includes status
     */
    public String getJobStatus(final String jobName, final String buildNumber) {
        String buildUrl = "http://" + host + ":" + port + "/" + path + "/job/"
                + jobName + "/build/" + buildNumber + "/api/json";

        try {
            String buildStatusJson = this.makeHttpRequest(buildUrl, null);
            JsonElement buildElement = new JsonParser().parse(buildStatusJson);
            JsonObject buildObject = buildElement.getAsJsonObject();
            return buildObject.getAsJsonPrimitive("result").toString();

        } catch (IOException e) {
            // We can safely ignore this exception as we will retry later
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to submit Jenkins job given job name and job parameters.
     *
     * @param jobName the job name
     * @param params  the job parameters as a String, String Map
     * @throws IOException if valid HTTP Request cannot be constructed
     * @throws InterruptedException if sleeping gets interrupted
     */
    public void submitJob(final String jobName, final Map<String,
            String> params) throws
            IOException, InterruptedException {
        String urlBuild = "http://" + host + ":" + port + "/" + path + "/job/"
                + jobName + "/build";

        StringBuffer payload = null;

        if (params != null) {
            // Build the Jenkins payload
            payload = new StringBuffer();
            payload.append("{\"parameter\": [");

            boolean comma = false;
            for (Entry<String, String> pair : params.entrySet()) {
                if (comma) {
                    payload.append(",");
                } else {
                    comma = true;
                }
                payload.append("{\"name\": \"");
                payload.append(pair.getKey());
                payload.append("\", \"value\": \"");
                payload.append(pair.getValue());
                payload.append("\"}");
            }

            payload.append("], \"\": \"\"}");
            logger.debug(payload);
        }

        String pString = null;

        if (payload != null) {
            pString = payload.toString();
        }


        // Try the call
        String ret = null;
        int numRetries = 0;
        while ((ret == null) && (numRetries < MAX_RETRIES)) {
            if (numRetries > 0) {
                Thread.sleep(DEFAULT_POLLING_INTERVAL);
            }
            ret = this.makeHttpRequest(urlBuild, pString);
            numRetries++;
        }
    }
}
