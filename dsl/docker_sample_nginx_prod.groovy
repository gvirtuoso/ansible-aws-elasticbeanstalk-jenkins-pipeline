String projectOwner = "gvirtuoso"
String projectName = "docker-sample-nginx"

String baseBranch = "master"
String baseEnv = "prod"

String jobDeployName = new StringBuilder()
    .append(baseEnv)
    .append("_")
    .append("deploy")
    .append("_")
    .append(projectName)
    .toString()
 
 String jobDeployDisplayName = new StringBuilder()
    .append(baseEnv.toUpperCase())
    .append(" - ")
    .append("Deploy")
    .append(" - ")
    .append(projectOwner)
    .append("/")
    .append(projectName)
    .toString()

String jobDeployDescription = jobDeployDisplayName

job(jobDeployName) {
    displayName(jobDeployDisplayName)
    description(jobDeployDescription)
    logRotator {
        numToKeep(10)
        artifactNumToKeep(1)
    }
    parameters {
        string{
            name("project_infra_automation_branch_name")
            description("Branch name for ansible-aws-elasticbeanstalk-jenkins-pipeline")
            defaultValue(baseBranch)
            trim(true)
        }
        string{
            name("project_app_branch_name")
            description("Branch name for "+ projectName)
            defaultValue(baseBranch)
            trim(true)
        }
    }
    scm {
        git {
            remote {
                github(projectOwner + "/ansible-aws-elasticbeanstalk-jenkins-pipeline", "https")
            }
            branch("*/\${project_infra_automation_branch_name}")
        }
    }
    wrappers {
        timestamps()
    }
    steps {
        ansiblePlaybookBuilder {
            playbook("006.deploy-docker-sample-nginx.yml")
            inventory {
                inventoryPath {
                    path("inventory")
                }
            }
            forks(5)
            additionalParameters('-e "branch=\${project_app_branch_name}" -e "env='+ baseEnv +'"')
        }
    }
    quietPeriod(10)
    publishers {
        wsCleanup()
    }
}

String jobSCMCheckerName = new StringBuilder()
    .append(baseEnv)
    .append("_")
    .append("scm_checker")
    .append("_")
    .append(projectName)
    .toString()

String jobSCMCheckerDisplayName = new StringBuilder()
    .append(baseEnv.toUpperCase())
    .append(" - ")
    .append("SCM Checker")
    .append(" - ")
    .append(projectOwner)
    .append("/")
    .append(projectName)
    .toString()

String jobSCMCheckerDescription = jobSCMCheckerDisplayName

job(jobSCMCheckerName) {
    displayName(jobSCMCheckerDisplayName)
    description(jobSCMCheckerDescription)
    logRotator {
        numToKeep(10)
        artifactNumToKeep(1)
    }
    scm {
        git {
            remote {
                github(projectOwner + "/" + projectName, "https")
            }
            branch("*/"+baseBranch)
        }
    }
    triggers {
        pollSCM {
            scmpoll_spec("* * * * *")
        }
    }
    wrappers {
        timestamps()
    }
    publishers {
        downstream(jobDeployName, "SUCCESS")
        wsCleanup()
    }
}
