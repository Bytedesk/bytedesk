#!/bin/bash

# Helm 模板验证脚本
# 用于验证 Helm Chart 模板是否正确

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

# 验证 Helm Chart 结构
validate_chart_structure() {
    print_info "验证 Helm Chart 结构..."
    
    # 检查必需文件
    required_files=("Chart.yaml" "values.yaml")
    for file in "${required_files[@]}"; do
        if [ ! -f "$file" ]; then
            print_error "缺少必需文件: $file"
            exit 1
        fi
    done
    
    # 检查 templates 目录
    if [ ! -d "templates" ]; then
        print_error "缺少 templates 目录"
        exit 1
    fi
    
    print_success "Chart 结构验证通过"
}

# 验证 Helm Chart 语法
validate_chart_syntax() {
    print_info "验证 Helm Chart 语法..."
    
    # 使用 helm lint 检查语法
    if helm lint . > /dev/null 2>&1; then
        print_success "Helm Chart 语法验证通过"
    else
        print_error "Helm Chart 语法验证失败"
        print_info "运行 'helm lint .' 查看详细错误信息"
        exit 1
    fi
}

# 验证模板渲染
validate_template_rendering() {
    print_info "验证模板渲染..."
    
    # 使用默认值渲染模板
    if helm template test-release . --dry-run > /dev/null 2>&1; then
        print_success "模板渲染验证通过"
    else
        print_error "模板渲染验证失败"
        print_info "运行 'helm template test-release . --dry-run' 查看详细错误信息"
        exit 1
    fi
}

# 验证特定环境的模板
validate_environment_templates() {
    print_info "验证环境特定模板..."
    
    # 检查是否有环境特定的 values 文件
    for env_file in values-*.yaml; do
        if [ -f "$env_file" ]; then
            print_info "验证 $env_file..."
            if helm template test-release . -f "$env_file" --dry-run > /dev/null 2>&1; then
                print_success "$env_file 验证通过"
            else
                print_error "$env_file 验证失败"
                print_info "运行 'helm template test-release . -f $env_file --dry-run' 查看详细错误信息"
                exit 1
            fi
        fi
    done
}

# 显示生成的 YAML
show_generated_yaml() {
    print_info "显示生成的 YAML（前 50 行）..."
    echo "=========================================="
    helm template test-release . --dry-run | head -50
    echo "=========================================="
    print_info "使用 'helm template test-release . --dry-run' 查看完整的 YAML"
}

# 主函数
main() {
    print_info "开始 Helm Chart 验证..."
    
    # 检查是否在正确的目录
    if [ ! -f "Chart.yaml" ]; then
        print_error "请在 Helm Chart 根目录运行此脚本"
        exit 1
    fi
    
    # 检查依赖
    check_helm
    
    # 执行验证
    validate_chart_structure
    validate_chart_syntax
    validate_template_rendering
    validate_environment_templates
    
    print_success "所有验证通过！"
    
    # 询问是否显示生成的 YAML
    read -p "是否显示生成的 YAML？(y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        show_generated_yaml
    fi
}

# 运行主函数
main "$@" 