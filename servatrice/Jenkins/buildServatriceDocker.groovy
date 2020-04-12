node {
    stage('setup'){
        checkout scm
        currentBuild.description = "${Branch}"
        appName = "servatrice"
        dockerRepo = "atriarchsystems"
        dockerCredId = "AtriarchDockerID"
        servatriceCredId = "CockatriceDBId"
        dockerfilePathFromRoot = "./Dockerfile"// this is the path from the base directory
        k8sDeployYamlPath = "./service.yaml"
        k8sDeployName = ""
        imageVersion = "v1.0.${env.BUILD_NUMBER}"
    }
    stage('build'){
       def buildout = sh(returnStdout: true, script: "docker build -t ${appName} -f ${dockerfilePathFromRoot} .")
       println buildout
    }
    stage('push'){
        def tagout = sh(returnStdout: true, script: "docker tag ${appName} ${dockerRepo}/${appName}:${imageVersion}")
        println tagout
        withCredentials([usernamePassword(usernameVariable: "DOCKER_USER",passwordVariable: "DOCKER_PASS", credentialsId: dockerCredId)]){
            def loginout = sh(returnStdout: true, script: "echo ${DOCKER_PASS} | docker login --username ${DOCKER_USER} --password-stdin")
            println loginout
            def pushout = sh(returnStdout: true, script: "docker push ${dockerRepo}/${appName}:${imageVersion}")
            println pushout
        }
    }
    stage('deploy'){
        withCredentials([usernamePassword(usernameVariable: "servatriceUser",passwordVariable: "servatricePass", credentialsId: servatriceCredId)]){
            def secretout = sh(returnStdout: true, script: "kubectl create secret -n servatrice generic servatrice-db --from-literal=servatrice-db-string='mysql://${servatriceUser}:${servatricePass}@servatrice-db-mysql.fun.cluster.local' --dry-run -o yaml | kubectl apply -n servatrice -f -")
            println secretout
        }
        def deployout = sh(returnStdout: true, script: "export IMAGE_VERSION=${imageVersion} && envsubst < ${k8sDeployYamlPath} | kubectl apply -n servatrice -f -")
        println deployout        
    }                
}
