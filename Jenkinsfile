node {
 
  def mvnHome = tool 'gradle61' 


stage('Checkout') {


 git 'https://github.com/tfang54321/psffs.git'
mvnHome = tool 'gradle61' 

}


stage('buildimage') {


bat  "'${mvnHome}/bin/gradle'  build docker"

}
}
}



