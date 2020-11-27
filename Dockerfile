FROM openjdk:8 
ENV JAVA_APP_JAR=app.jar
COPY target/csw_eureka_server-25.1.2.jar app.jar
EXPOSE 8761

ENTRYPOINT ["java","-jar","/app.jar"]
