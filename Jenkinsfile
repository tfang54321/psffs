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

sh  './gradlew  build docker'
}
}
}
}



