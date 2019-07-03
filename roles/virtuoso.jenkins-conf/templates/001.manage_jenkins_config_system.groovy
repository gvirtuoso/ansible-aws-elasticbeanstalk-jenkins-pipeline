import jenkins.model.*
import jenkins.security.*
import hudson.model.*

// ----------------------------------------------
// -- Getting Jenkins Instance
// ----------------------------------------------
def jenkinsInstance = Jenkins.getInstance()

// ----------------------------------------------
// -- Basic Configuration
// ----------------------------------------------
jenkinsInstance.setMode(Node.Mode.NORMAL)
jenkinsInstance.setNumExecutors(6)

// ----------------------------------------------
// -- Global Properties
// ----------------------------------------------
def globalNodeProperties = jenkinsInstance.getGlobalNodeProperties()
def envVarsNodePropertyList = globalNodeProperties.getAll(hudson.slaves.EnvironmentVariablesNodeProperty.class)
def newEnvVarsNodeProperty = null
def envVars = null

if ( envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0 ) {
  newEnvVarsNodeProperty = new hudson.slaves.EnvironmentVariablesNodeProperty();
  globalNodeProperties.add(newEnvVarsNodeProperty)
  envVars = newEnvVarsNodeProperty.getEnvVars()
} else {
  envVars = envVarsNodePropertyList.get(0).getEnvVars()
  envVars.clear()
}

envVars.put("AWS_PROFILE", "{{ profile_name }}")
envVars.put("JAVA_HOME", "/usr/lib/jvm/default-java") 

// ----------------------------------------------
// -- Administrative Monitors Configuration
// ----------------------------------------------
for(AdministrativeMonitor monitor : jenkinsInstance.administrativeMonitors) {
  if(monitor instanceof UpdateCenter.CoreUpdateMonitor) {
    monitor.disable(true)
  }
  if(monitor instanceof UpdateSiteWarningsMonitor) {
    monitor.disable(true)
  }
}

// ----------------------------------------------
// -- Saving Jenkins modifications
// ----------------------------------------------
jenkinsInstance.save()
