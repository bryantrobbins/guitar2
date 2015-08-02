// Grab command-line args
autName = args[0]
dbId = args[1]
suiteId = args[2]

// Set properties
Properties props = new Properties();
props.setProperty("autName", autName);
props.setProperty("dbId", dbId);
props.setProperty("suiteId", suiteId);

props.setProperty("mongo.host", "gollum.cs.umd.edu")
props.setProperty("mongo.port", "27017")

props.setProperty("jenkins.host", "gollum.cs.umd.edu")
props.setProperty("jenkins.port", "7777")
props.setProperty("jenkins.path", "jenkins")
props.setProperty("jenkins.username", "testrunner")
props.setProperty("jenkins.password", "holdyourpeace")

// Write out properties
filename = args[0] + "_guitar.properties"
String comment = "Properties for " + autName + " running suite " + suiteId + " to DB " + dbId
output = new FileOutputStream(filename);

props.store(output, comment);
println("Properties written to " + filename)
