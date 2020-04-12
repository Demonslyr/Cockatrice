node {
    stage('setup'){
        checkout scm
        currentBuild.description = "${Branch}"
        appName = "servatrice"
        dockerRepo = "atriarchsystems"
        dockerCredId = "AtriarchDockerID"
        dockerfilePathFromRoot = "./Dockerfile"// this is the path from the base directory
        k8sDeployYamlPath = "./service.yaml"
        k8sDeployName = ""
        imageVersion = "v1.0.${env.BUILD_NUMBER}"
    }
    stage('build'){
       def buildout = sh(returnStdout: true, script: "docker build -t ${appName} -f ${dockerfilePathFromRoot} ./src")
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
        //def deployout = sh(returnStdout: true, script: " export IMAGE_VERSION=${imageVersion} && envsubst < ${k8sDeployYamlPath} | kubectl apply -f -")
        println deployout        
    }                
}
