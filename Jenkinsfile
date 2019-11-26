pipeline {
agent any
stages {
stage('Checkout') {
steps {

 git 'https://github.com/tfang54321/psffs.git'
}
}
stage('Compile') {
steps {
git update-index --chmod=+x gradlew
sh './gradlew  clean compileJava'
}
}

}
}
