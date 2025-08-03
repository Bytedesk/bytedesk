#!/bin/bash

# 微语系统 Kubernetes 部署脚本
set -e

echo "开始部署微语系统..."

# 检查 kubectl
if ! command -v kubectl &> /dev/null; then
    echo "错误: kubectl 未安装"
    exit 1
fi

# 创建命名空间
echo "创建命名空间..."
kubectl apply -f namespace.yaml

# 应用密钥
echo "应用密钥配置..."
kubectl apply -f secret.yaml

# 部署持久化存储
echo "部署持久化存储..."
kubectl apply -f pvc-mysql.yaml
kubectl apply -f pvc-redis.yaml
kubectl apply -f pvc-uploads.yaml

# 部署中间件
echo "部署中间件..."
kubectl apply -f mysql-deployment.yaml
kubectl apply -f mysql-service.yaml
kubectl apply -f redis-deployment.yaml
kubectl apply -f redis-service.yaml

# 等待中间件就绪
echo "等待中间件就绪..."
kubectl wait --for=condition=ready pod -l app=mysql -n bytedesk --timeout=300s
kubectl wait --for=condition=ready pod -l app=redis -n bytedesk --timeout=300s

# 部署微语应用
echo "部署微语应用..."
kubectl apply -f bytedesk-deployment.yaml
kubectl apply -f bytedesk-service.yaml

echo "部署完成！"
echo "查看状态: kubectl get pods -n bytedesk"
echo "查看服务: kubectl get svc -n bytedesk" 