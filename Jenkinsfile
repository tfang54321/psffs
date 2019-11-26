pipeline {
agent any
stages {
stage('Checkout') {
steps {

 git 'https://github.com/tfang54321/psffs.git'
}
}

stage('assign') {
steps {
git update-index --chmod=+x gradlew

}
}
stage('Compile') {
steps {

sh './gradlew  clean compileJava'
}
}

}
}
