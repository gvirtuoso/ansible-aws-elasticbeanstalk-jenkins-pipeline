import jenkins.*
import jenkins.model.*
import jenkins.security.*
import jenkins.security.s2m.*
import hudson.*
import hudson.markup.*
import hudson.model.*
import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import javaposse.jobdsl.plugin.GlobalJobDslSecurityConfiguration

def jenkinsInstance = Jenkins.getInstance()

// Changing Markup Formatter
jenkinsInstance.setMarkupFormatter(new RawHtmlMarkupFormatter(true))

// Disable all JNLP protocols except for JNLP4
Set<String> agentProtocolsList = ['JNLP4-connect', 'Ping']
jenkinsInstance.setAgentProtocols(agentProtocolsList)

// CSRF Protection
jenkinsInstance.setCrumbIssuer(new DefaultCrumbIssuer(true))

// Disabling CLi over remoting
jenkinsInstance.getDescriptor("jenkins.CLI").get().setEnabled(false)

// Enabling agent -> Master access control
jenkinsInstance.injector.getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false);

// Saving previously settings
jenkinsInstance.save()

// Changing DSL script security
GlobalConfiguration.all().get(GlobalJobDslSecurityConfiguration.class).useScriptSecurity=false
GlobalConfiguration.all().get(GlobalJobDslSecurityConfiguration.class).save()
