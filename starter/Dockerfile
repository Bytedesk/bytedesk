# https://spring.io/guides/topicals/spring-boot-docker
# maven
FROM eclipse-temurin:17-jdk
VOLUME /tmp
COPY target/*.jar starter-0.3.0-BYTEDESK.jar
ENTRYPOINT ["java","-jar","/starter-0.3.0-BYTEDESK.jar"]
# 编译
# docker build -t jackning/bytedesk .
# 运行
# docker run -p 9003:9003 jackning/bytedesk
