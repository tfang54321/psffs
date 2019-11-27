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

bat  'C:\apps\gradle-6.0.1\bin\gradle  build docker'
}
}
}
}



