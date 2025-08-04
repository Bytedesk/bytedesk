#!/bin/bash

echo "修复镜像拉取问题，使用国内镜像源..."

# 删除现有的deployment
echo "删除现有的deployment..."
kubectl delete deployment mysql redis elasticsearch artemis zipkin minio -n bytedesk

# 等待删除完成
sleep 5

# 使用国内镜像源重新部署
echo "使用国内镜像源重新部署..."

# MySQL - 使用阿里云镜像
kubectl patch deployment mysql -n bytedesk --patch '{"spec":{"template":{"spec":{"containers":[{"name":"mysql","image":"registry.cn-hangzhou.aliyuncs.com/google_containers/mysql:8.0"}]}}}}' 2>/dev/null || echo "MySQL deployment not found, will create new one"

# Redis - 使用阿里云镜像
kubectl patch deployment redis -n bytedesk --patch '{"spec":{"template":{"spec":{"containers":[{"name":"redis","image":"registry.cn-hangzhou.aliyuncs.com/google_containers/redis:7-alpine"}]}}}}' 2>/dev/null || echo "Redis deployment not found, will create new one"

# Elasticsearch - 使用阿里云镜像
kubectl patch deployment elasticsearch -n bytedesk --patch '{"spec":{"template":{"spec":{"containers":[{"name":"elasticsearch","image":"registry.cn-hangzhou.aliyuncs.com/google_containers/elasticsearch:8.11.0"}]}}}}' 2>/dev/null || echo "Elasticsearch deployment not found, will create new one"

# 重新应用deployment文件（如果patch失败）
echo "重新应用deployment文件..."
kubectl apply -f ./mysql-deployment.yaml
kubectl apply -f ./redis-deployment.yaml
kubectl apply -f ./elasticsearch-deployment.yaml
kubectl apply -f ./artemis-deployment.yaml
kubectl apply -f ./zipkin-deployment.yaml
kubectl apply -f ./minio-deployment.yaml

echo "镜像修复完成！"
echo "查看Pod状态: kubectl get pods -n bytedesk" 