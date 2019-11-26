node {
    stage 'Building image'
    git 'https://github.com/tfang54321/psffs.git' // checks out Dockerfile

  gradlew build docker
     docker run -p 8091:5000 -t springio/gs-spring-boot-docker-psffs1126
}
