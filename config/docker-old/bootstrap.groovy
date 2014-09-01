import hudson.model.*;
import jenkins.model.*;

def installPlugin(pid, version)
{
    def url = "http://updates.jenkins-ci.org/download/plugins/" + pid + "/" + version + "/" + pid + ".hpi"
    def loc = System.getenv()["JENKINS_HOME"] + "/plugins/"

		new File(loc).mkdirs()

    println "Installing plugin ${pid} to ${loc}"

    def file = new FileOutputStream(loc + pid + ".hpi")
    def out = new BufferedOutputStream(file)
    out << new URL(url).openStream()
    out.close()
}

installPlugin("scm-api", "0.2")
installPlugin("git-client", "1.10.1")
installPlugin("git", "2.2.5")
