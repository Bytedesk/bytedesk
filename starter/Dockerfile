# https://spring.io/guides/topicals/spring-boot-docker
# https://hub.docker.com/r/jackning/bytedesk
# maven
# https://hub.docker.com/_/eclipse-temurin/tags?name=21-jdk
FROM eclipse-temurin:21-jdk
# maintainer
LABEL maintainer="270580156@qq.com"
# ===============================
# LibreOffice：文件预览（Office 转 PDF）依赖（可选层）。
# 默认 false——官方镜像已精简，Docker 部署的文件预览推荐 gotenberg sidecar（gotenberg/gotenberg:8，自带 CJK 字体）：
#   .env 设 BYTEDESK_PREVIEW_CONVERT_ENABLED=true + BYTEDESK_PREVIEW_CONVERT_MODE=remote，并 ./start.sh gotenberg
# 自建全量镜像（单容器、无 sidecar）可 --build-arg INSTALL_LIBREOFFICE=true（jodconverter-local-lo 在容器内以
# 子进程方式启动 soffice，宿主机上安装的 LibreOffice 对容器不可见）；直跑 jar 部署则直接在宿主机安装 LibreOffice。
# fonts-noto-cjk 避免中文文档转换乱码；fonts-liberation 提供常见西文度量兼容字体
# ===============================
ARG INSTALL_LIBREOFFICE=false
RUN if [ "$INSTALL_LIBREOFFICE" = "true" ]; then \
        apt-get update -qq && \
        DEBIAN_FRONTEND=noninteractive apt-get install -y -qq --no-install-recommends \
            libreoffice-core libreoffice-writer libreoffice-calc libreoffice-impress \
            fonts-noto-cjk fonts-liberation fonts-dejavu-core && \
        rm -rf /var/lib/apt/lists/*; \
    fi
# Set the working directory in the container
WORKDIR /app 
# COPY target/*.jar bytedesk-starter.jar
COPY target/*.jar app.jar
# Expose the port your application listens on
EXPOSE 9003
# Specify the command to run your application
ENTRYPOINT ["java","-jar","app.jar"]
