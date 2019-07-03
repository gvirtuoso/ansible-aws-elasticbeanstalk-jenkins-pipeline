String projectOwner = "gvirtuoso"
String projectName = "docker-sample-nginx"

String baseBranch = "develop"
String baseEnv = "dev"

String jobDeployName = new StringBuilder()
    .append("deploy")
    .append("_")
    .append(projectName)
    .append("_")
    .append(baseEnv)
    .toString()
 
 String jobDeployDisplayName = new StringBuilder()
    .append("Deploy")
    .append(" - ")
    .append(projectOwner)
    .append("/")
    .append(projectName)
    .toString()

String jobDeployDescription = new StringBuilder()
    .append("Deploy ")
    .append(projectOwner)
    .append("/")
    .append(projectName)
    .append(" on ")
    .append(baseEnv.toUpperCase())
    .toString()

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
            description("Branch name for devops-infrastructure")
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
                github(projectOwner + "/devops-infrastructure", "https")
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
    publishers {
        wsCleanup()
    }
}

String jobSCMCheckerName = new StringBuilder()
    .append("scm_checker")
    .append("_")
    .append(projectName)
    .append("_")
    .append(baseEnv)
    .toString()

String jobSCMCheckerDisplayName = new StringBuilder()
    .append("SCM Checker")
    .append(" - ")
    .append(projectOwner)
    .append("/")
    .append(projectName)
    .append(" on ")
    .append(baseEnv.toUpperCase())
    .toString()

String jobSCMCheckerDescription = new StringBuilder()
    .append("Source Control Management checker for ")
    .append(projectOwner)
    .append("/")
    .append(projectName)
    .toString()

job(jobSCMCheckerName) {
    displayName(jobSCMCheckerDisplayName)
    description(jobSCMCheckerDescription)
    label("scm_checker")
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
    quietPeriod(300)
    wrappers {
        timestamps()
    }
    publishers {
        downstream(jobDeployName, "SUCCESS")
        wsCleanup()
    }
}
