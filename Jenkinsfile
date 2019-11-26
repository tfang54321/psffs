node {
       stage('Checkout') {
          git 'https://github.com/tfang54321/psffs.git' // checks out Dockerfile
       }

    stage('build Image') {
              gradlew build docker
          }

  stage('run Image') {
               docker run -p 8091:5000 -t springio/gs-spring-boot-docker-psffs1126
           }

}
