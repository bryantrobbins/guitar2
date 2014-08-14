sudo apt-get update
sudo apt-get -q -y install curl
sudo apt-get -q -y install openjdk-6-jdk
#sudo apt-get -q -y install mongodb

# Get latest jenkins war
wget http://mirrors.jenkins-ci.org/war/latest/jenkins.war

# Start jenkins
mkdir jenkins
mv jenkins.war jenkins

cat > /etc/init.d/jenkins <<'eos'
ESC="Jenkins CI Server"
NAME=jenkins
PIDFILE=/var/run/$NAME.pid
RUN_AS=root
COMMAND="/usr/bin/java -- -jar /home/vagrant/jenkins/jenkins.war"

export JAVA_HOME=/usr/lib/jvm/java-6-openjdk-i386
export JENKINS_HOME=/home/vagrant/jenkins/home

d_start() {
        start-stop-daemon --start --quiet --background --make-pidfile --pidfile $PIDFILE --chuid $RUN_AS --exec $COMMAND
}

d_stop() {
        start-stop-daemon --stop --quiet --pidfile $PIDFILE
        if [ -e $PIDFILE ]
                then rm $PIDFILE
        fi
}

case $1 in
        start)
        echo -n "Starting $DESC: $NAME"
        d_start
        echo "."
        ;;
        stop)
        echo -n "Stopping $DESC: $NAME"
        d_stop
        echo "."
        ;;
        restart)
        echo -n "Restarting $DESC: $NAME"
        d_stop
        sleep 1
        d_start
        echo "."
        ;;
        *)
        echo "usage: $NAME {start|stop|restart}"
        exit 1
        ;;
esac

exit 0
eos

# Prep service
sudo chmod 755 /etc/init.d/jenkins

# Start service
sudo service jenkins start

# Wait for Jenkins ready
output=$(curl --silent http://localhost:8080)
grepOut=$(echo $output | grep "Welcome to Jenkins")

until [[ -n $grepOut ]]; do
        printf '.'
        sleep 5
        output=$(curl --silent http://localhost:8080)
        grepOut=$(echo $output | grep "Welcome to Jenkins")
done

# Get the CLI jar
wget http://localhost:8080/jnlpJars/jenkins-cli.jar
cli="java -jar /home/vagrant/jenkins-cli.jar -s http://localhost:8080"

# Update plugin definitions
curl  -L http://updates.jenkins-ci.org/update-center.json | sed '1d;$d' | curl -X POST -H 'Accept: application/json' -d @- http://localhost:8080/updateCenter/byId/default/postBack

# Update the SSH slaves plugin
$cli install-plugin ssh-slaves
$cli install-plugin xvfb

# Restart
sudo service jenkins restart

# Write out groovy script for slave and job config
cat > node.groovy <<'ends'
import jenkins.model.*
import hudson.model.*
import hudson.slaves.*
import hudson.plugins.sshslaves.SSHLauncher
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.Credentials
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import static com.cloudbees.plugins.credentials.CredentialsScope.SYSTEM

// Get the default domain
Domain onlyDom = SystemCredentialsProvider.getInstance().getDomainCredentialsMap().keySet().iterator().next()

// Create the credential object
Credentials cred = new UsernamePasswordCredentialsImpl(
        SYSTEM,
        null,
        "",
        "vagrant",
        "vagrant")

// Add credential
SystemCredentialsProvider.getInstance().addCredentials(onlyDom, cred);

// Add slave nodes
SSHLauncher launcher1 = new SSHLauncher("192.168.33.11",22,cred.getId(),"","","","",null)
Jenkins.instance.addNode(new DumbSlave("jslave1","","/home/vagrant","1",Node.Mode.NORMAL,"xvfb",launcher1,new RetentionStrategy.Always(),new LinkedList()))
SSHLauncher launcher2 = new SSHLauncher("192.168.33.12",22,cred.getId(),"","","","",null)
Jenkins.instance.addNode(new DumbSlave("jslave2","","/home/vagrant","1",Node.Mode.NORMAL,"xvfb",launcher2,new RetentionStrategy.Always(),new LinkedList()))
SSHLauncher launcher3 = new SSHLauncher("192.168.33.13",22,cred.getId(),"","","","",null)
Jenkins.instance.addNode(new DumbSlave("jslave3","","/home/vagrant","1",Node.Mode.NORMAL,"xvfb",launcher3,new RetentionStrategy.Always(),new LinkedList()))
ends


# Wait for Jenkins ready
output=$(curl --silent http://localhost:8080)
grepOut=$(echo $output | grep "Welcome to Jenkins")

until [[ -n $grepOut ]]; do
        printf '.'
        sleep 5
        output=$(curl --silent http://localhost:8080)
        grepOut=$(echo $output | grep "Welcome to Jenkins")
done

# Add slaves to Jenkins
$cli groovy node.groovy

# Add jobs


