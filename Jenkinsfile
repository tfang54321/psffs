pipeline {
 
  def mvnHome = tool 'gradle61' 
agent any
stages {
stage('Checkout') {
steps {

 git 'https://github.com/tfang54321/psffs.git'
mvnHome = tool 'gradle61' 
}
}


stage('buildimage') {
steps {

bat  "'${mvnHome}/bin/gradle'  build docker"
}
}
}
}



