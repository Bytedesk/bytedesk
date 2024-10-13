# https://spring.io/guides/topicals/spring-boot-docker
# https://hub.docker.com/r/jackning/bytedesk
# maven
# # Use a base image with Java installed
FROM eclipse-temurin:17-jdk
# https://hub.docker.com/_/openjdk
# FROM openjdk:17
# VOLUME /tmp
# 
# Set the working directory in the container
WORKDIR /app
# 
# Copy the JAR file into the container at /app
# COPY target/*.jar bytedesk-starter-0.4.4.jar
COPY target/*.jar app.jar
# 
# Specify the command to run your application
# ENTRYPOINT ["java","-jar","/bytedesk-starter-0.4.4.jar"]
# CMD ["java", "-jar", "app.jar"]
ENTRYPOINT ["java","-jar","/app.jar"]

# 编译
# docker build -t jackning/bytedesk .
# 查看镜像
# docker images
# 运行
# docker run --name bytedesk -p 9003:9003 jackning/bytedesk
# 查看日志
# docker logs -f bytedesk
