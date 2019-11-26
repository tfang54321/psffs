pipeline {
agent any
stages {
stage('Checkout') {
steps {

 git 'https://github.com/tfang54321/psffs.git'
}
}


stage('buildimage') {
steps {
sh 'chmod a+x gradlew'
sh './gradlew  build docker'
}
}
}
}



