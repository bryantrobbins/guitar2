import hudson.model.*;
import hudson.security.*;
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

println "--> setting agent port for jnlp"
Jenkins.instance.setSlaveAgentPort(50000)

// Enable use of Jenkins database for users
def realm = new HudsonPrivateSecurityRealm(false, false, null)
Jenkins.instance.setSecurityRealm(realm)

// Enable Full Control for logged-in users
def strat = new FullControlOnceLoggedInAuthorizationStrategy()
Jenkins.instance.setAuthorizationStrategy(strat)

// Create a user
Jenkins.instance.securityRealm.createAccount("admin", "cua84paper")
