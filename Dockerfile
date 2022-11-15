FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD target/protools-back-office-0.0.2.jar protools-back-office-0.0.2.jar
ENTRYPOINT ["java","-jar","/protools-back-office-0.0.2.jar"]
