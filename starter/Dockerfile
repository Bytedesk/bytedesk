# https://spring.io/guides/topicals/spring-boot-docker
# https://hub.docker.com/r/jackning/bytedesk
# maven
# https://hub.docker.com/_/eclipse-temurin
FROM eclipse-temurin:17-jdk
# 华为毕昇SDK https://hub.docker.com/r/openeuler/bisheng-jdk/tags?name=17
# FROM openeuler/bisheng-jdk:17.0.10-oe2203sp3
# maintainer
LABEL maintainer="270580156@qq.com"
# Set the working directory in the container
WORKDIR /app 
# COPY target/*.jar bytedesk-starter.jar
COPY target/*.jar app.jar
# Expose the port your application listens on
EXPOSE 9003
# Specify the command to run your application
ENTRYPOINT ["java","-jar","app.jar"]
