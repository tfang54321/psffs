node {
       stage('Checkout') {
          git 'https://github.com/tfang54321/psffs.git' // checks out Dockerfile
       }

    stage('build Image') {
             gradlew build docker
          }



}
