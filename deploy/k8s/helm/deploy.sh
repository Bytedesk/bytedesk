#!/bin/bash

# ByteDesk Helm Chart 部署脚本

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 Helm 是否安装
check_helm() {
    if ! command -v helm &> /dev/null; then
        print_error "Helm 未安装，请先安装 Helm"
        print_info "安装命令: curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash"
        exit 1
    fi
    print_success "Helm 已安装: $(helm version --short)"
}

# 检查 kubectl 是否安装
check_kubectl() {
    if ! command -v kubectl &> /dev/null; then
        print_error "kubectl 未安装，请先安装 kubectl"
        exit 1
    fi
    print_success "kubectl 已安装: $(kubectl version --client --short)"
}

# 检查集群连接
check_cluster() {
    if ! kubectl cluster-info &> /dev/null; then
        print_error "无法连接到 Kubernetes 集群"
        exit 1
    fi
    print_success "已连接到 Kubernetes 集群"
}

# 显示使用说明
show_usage() {
    echo "ByteDesk Helm Chart 部署脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -n, --namespace NAME     指定命名空间 (默认: bytedesk)"
    echo "  -r, --release NAME       指定 release 名称 (默认: bytedesk)"
    echo "  -f, --values FILE        指定 values 文件"
    echo "  -v, --version VERSION    指定镜像版本"
    echo "  -d, --dry-run            试运行模式"
    echo "  -u, --upgrade            升级现有部署"
    echo "  -h, --help               显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0                                    # 使用默认配置部署"
    echo "  $0 -n production -r bytedesk-prod     # 部署到生产环境"
    echo "  $0 -f values-prod.yaml -v v1.0.0      # 使用自定义配置和版本"
    echo "  $0 -u -v v1.0.1                       # 升级到新版本"
}

# 默认参数
NAMESPACE="bytedesk"
RELEASE_NAME="bytedesk"
VALUES_FILE=""
IMAGE_VERSION="latest"
DRY_RUN=false
UPGRADE=false

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        -n|--namespace)
            NAMESPACE="$2"
            shift 2
            ;;
        -r|--release)
            RELEASE_NAME="$2"
            shift 2
            ;;
        -f|--values)
            VALUES_FILE="$2"
            shift 2
            ;;
        -v|--version)
            IMAGE_VERSION="$2"
            shift 2
            ;;
        -d|--dry-run)
            DRY_RUN=true
            shift
            ;;
        -u|--upgrade)
            UPGRADE=true
            shift
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            print_error "未知参数: $1"
            show_usage
            exit 1
            ;;
    esac
done

# 主函数
main() {
    print_info "开始 ByteDesk Helm Chart 部署..."
    
    # 检查依赖
    check_helm
    check_kubectl
    check_cluster
    
    # 构建 Helm 命令
    HELM_CMD="helm"
    
    if [ "$DRY_RUN" = true ]; then
        HELM_CMD="$HELM_CMD --dry-run"
        print_warning "运行在试运行模式"
    fi
    
    # 添加 values 文件
    if [ -n "$VALUES_FILE" ]; then
        if [ ! -f "$VALUES_FILE" ]; then
            print_error "Values 文件不存在: $VALUES_FILE"
            exit 1
        fi
        HELM_CMD="$HELM_CMD -f $VALUES_FILE"
        print_info "使用 values 文件: $VALUES_FILE"
    fi
    
    # 设置镜像版本
    HELM_CMD="$HELM_CMD --set bytedesk.image.tag=$IMAGE_VERSION"
    print_info "镜像版本: $IMAGE_VERSION"
    
    # 检查是否已存在 release
    if helm list -n "$NAMESPACE" | grep -q "^$RELEASE_NAME"; then
        if [ "$UPGRADE" = true ]; then
            print_info "升级现有 release: $RELEASE_NAME"
            $HELM_CMD upgrade "$RELEASE_NAME" . -n "$NAMESPACE"
        else
            print_warning "Release $RELEASE_NAME 已存在"
            print_info "使用 -u 参数进行升级，或使用不同的 release 名称"
            exit 1
        fi
    else
        print_info "安装新 release: $RELEASE_NAME"
        $HELM_CMD install "$RELEASE_NAME" . -n "$NAMESPACE" --create-namespace
    fi
    
    if [ "$DRY_RUN" = false ]; then
        print_success "部署完成！"
        print_info "查看状态: kubectl get pods -n $NAMESPACE"
        print_info "查看服务: kubectl get svc -n $NAMESPACE"
        print_info "查看日志: kubectl logs -f deployment/$RELEASE_NAME-bytedesk -n $NAMESPACE"
    fi
}

# 运行主函数
main "$@" 