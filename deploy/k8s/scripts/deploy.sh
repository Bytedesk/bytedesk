#!/bin/bash

echo "开始部署微语系统..."

# 检查 kubectl
if ! command -v kubectl &> /dev/null; then
    echo "错误: kubectl 未安装"
    exit 1
fi

# 创建命名空间
echo "创建命名空间..."
kubectl apply -f ./namespace.yaml

# 应用密钥
echo "应用密钥配置..."
kubectl apply -f ./secret.yaml

# 部署持久化存储
echo "部署持久化存储..."
kubectl apply -f ./mysql-pvc.yaml
kubectl apply -f ./redis-pvc.yaml
kubectl apply -f ./uploads-pvc.yaml
kubectl apply -f ./elasticsearch-pvc.yaml
kubectl apply -f ./artemis-pvc.yaml
kubectl apply -f ./zipkin-pvc.yaml
kubectl apply -f ./minio-pvc.yaml

# 部署中间件（先部署Pod，触发PVC绑定）
echo "部署中间件..."
kubectl apply -f ./mysql-deployment.yaml
kubectl apply -f ./mysql-service.yaml
kubectl apply -f ./redis-deployment.yaml
kubectl apply -f ./redis-service.yaml
kubectl apply -f ./elasticsearch-deployment.yaml
kubectl apply -f ./elasticsearch-service.yaml
kubectl apply -f ./artemis-deployment.yaml
kubectl apply -f ./artemis-service.yaml
kubectl apply -f ./zipkin-deployment.yaml
kubectl apply -f ./zipkin-service.yaml
kubectl apply -f ./minio-deployment.yaml
kubectl apply -f ./minio-service.yaml

# 等待 PVC 绑定（在Pod部署后等待）
echo "等待 PVC 绑定..."
kubectl wait --for=condition=bound pvc/mysql-pvc -n bytedesk --timeout=120s
kubectl wait --for=condition=bound pvc/redis-pvc -n bytedesk --timeout=120s
kubectl wait --for=condition=bound pvc/elasticsearch-pvc -n bytedesk --timeout=120s
kubectl wait --for=condition=bound pvc/artemis-pvc -n bytedesk --timeout=120s
kubectl wait --for=condition=bound pvc/zipkin-pvc -n bytedesk --timeout=120s
kubectl wait --for=condition=bound pvc/minio-pvc -n bytedesk --timeout=120s

# 等待中间件就绪（增加重试机制）
echo "等待中间件就绪..."
for i in {1..10}; do
    echo "尝试 $i/10..."
    
    # 检查 Pod 状态
    mysql_status=$(kubectl get pods -l app=mysql -n bytedesk -o jsonpath='{.items[0].status.phase}' 2>/dev/null || echo "Unknown")
    redis_status=$(kubectl get pods -l app=redis -n bytedesk -o jsonpath='{.items[0].status.phase}' 2>/dev/null || echo "Unknown")
    elasticsearch_status=$(kubectl get pods -l app=elasticsearch -n bytedesk -o jsonpath='{.items[0].status.phase}' 2>/dev/null || echo "Unknown")
    artemis_status=$(kubectl get pods -l app=artemis -n bytedesk -o jsonpath='{.items[0].status.phase}' 2>/dev/null || echo "Unknown")
    zipkin_status=$(kubectl get pods -l app=zipkin -n bytedesk -o jsonpath='{.items[0].status.phase}' 2>/dev/null || echo "Unknown")
    minio_status=$(kubectl get pods -l app=minio -n bytedesk -o jsonpath='{.items[0].status.phase}' 2>/dev/null || echo "Unknown")
    
    echo "MySQL 状态: $mysql_status"
    echo "Redis 状态: $redis_status"
    echo "Elasticsearch 状态: $elasticsearch_status"
    echo "Artemis 状态: $artemis_status"
    echo "Zipkin 状态: $zipkin_status"
    echo "MinIO 状态: $minio_status"
    
    if [[ "$mysql_status" == "Running" && "$redis_status" == "Running" && "$elasticsearch_status" == "Running" && "$artemis_status" == "Running" && "$zipkin_status" == "Running" && "$minio_status" == "Running" ]]; then
        echo "中间件启动成功！"
        break
    fi
    
    if [[ $i -eq 10 ]]; then
        echo "警告: 中间件启动超时，继续部署主应用..."
    else
        sleep 30
    fi
done

# 部署微语应用
echo "部署微语应用..."
kubectl apply -f ./configmap.yaml
kubectl apply -f ./bytedesk-deployment.yaml
kubectl apply -f ./bytedesk-service.yaml

# 部署 Ingress（可选，需要集群支持 Ingress Controller）
echo "部署 Ingress..."
kubectl apply -f ./ingress.yaml

echo "部署完成！"
echo "查看状态: kubectl get pods -n bytedesk"
echo "查看服务: kubectl get svc -n bytedesk"
echo "查看 Ingress: kubectl get ingress -n bytedesk"
echo "查看日志: kubectl logs -f deployment/mysql -n bytedesk"
echo ""
echo "注意："
echo "- 如需外部访问，请确保集群已安装 Ingress Controller（如 NGINX Ingress Controller）"
echo "- 请修改 ingress.yaml 中的域名 'weiyu.example.com' 为您的实际域名"
echo "- 如需本地测试，可使用: kubectl port-forward svc/bytedesk-service 9003:9003 -n bytedesk"
