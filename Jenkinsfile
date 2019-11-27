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

bat  'C:\apps\gradle-5.6.3\bin\gradle  build docker'
}
}
}
}



