#!/bin/bash

echo "⚠️  PVC 和数据清理脚本"
echo "================================"
echo "此脚本将清理所有 PVC 和相关数据，包括："
echo "- MySQL 数据库数据"
echo "- Redis 缓存数据"
echo "- Elasticsearch 索引数据"
echo "- MinIO 文件数据"
echo "- Artemis 消息队列数据"
echo "- Zipkin 追踪数据"
echo "- 上传文件数据"
echo ""

# 确认清理操作
echo "⚠️  警告：此操作不可逆，所有数据将被永久删除！"
read -p "确认要清理所有数据吗？(输入 'yes' 确认): " confirm

if [[ "$confirm" != "yes" ]]; then
    echo "清理操作已取消"
    exit 0
fi

# 再次确认
echo ""
echo "⚠️  最后确认："
echo "您即将删除 bytedesk 命名空间中的所有 PVC 和数据"
read -p "输入 'DELETE' 进行最终确认: " final_confirm

if [[ "$final_confirm" != "DELETE" ]]; then
    echo "清理操作已取消"
    exit 0
fi

echo ""
echo "开始清理 PVC 和数据..."

# 检查 kubectl
if ! command -v kubectl &> /dev/null; then
    echo "错误: kubectl 未安装"
    exit 1
fi

# 检查命名空间是否存在
if ! kubectl get namespace bytedesk &>/dev/null; then
    echo "命名空间 bytedesk 不存在，无需清理"
    exit 0
fi

# 停止相关 Pod（可选）
echo "停止相关 Pod..."
kubectl scale deployment --all --replicas=0 -n bytedesk --ignore-not-found=true

# 等待 Pod 停止
echo "等待 Pod 停止..."
sleep 10

# 删除 PVC
echo "删除 PVC..."
kubectl delete pvc mysql-pvc redis-pvc uploads-pvc elasticsearch-pvc artemis-pvc zipkin-pvc minio-pvc -n bytedesk --ignore-not-found=true

# 等待删除完成
echo "等待 PVC 删除完成..."
sleep 10

# 检查 PVC 是否已删除
echo "检查 PVC 删除状态..."
kubectl get pvc -n bytedesk

echo ""
echo "✅ PVC 清理完成！"
echo ""
echo "注意："
echo "- 所有数据已被永久删除"
echo "- 如需重新部署，请运行 deploy.sh"
echo "- 建议在清理前备份重要数据" 