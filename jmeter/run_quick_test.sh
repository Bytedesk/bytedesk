#!/bin/bash

# Bytedesk快速登录功能验证测试
# 使用方法: ./run_quick_test.sh [JMeter安装路径]

# 设置默认JMeter路径
DEFAULT_JMETER_PATH="/usr/local/apache-jmeter-5.5/bin"
JMETER_PATH=${1:-$DEFAULT_JMETER_PATH}

# 测试配置
TEST_PLAN="quick_test.jmx"
RESULTS_DIR="quick_results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# 创建结果目录
mkdir -p "$RESULTS_DIR"

echo "=========================================="
echo "Bytedesk快速登录功能验证测试"
echo "时间: $(date)"
echo "JMeter路径: $JMETER_PATH"
echo "测试计划: $TEST_PLAN"
echo "=========================================="

# 检查JMeter是否存在
if [ ! -f "$JMETER_PATH/jmeter" ]; then
    echo "错误: 找不到JMeter可执行文件: $JMETER_PATH/jmeter"
    echo "请确保JMeter已正确安装，或通过参数指定正确的路径"
    echo "使用方法: $0 [JMeter安装路径]"
    exit 1
fi

# 检查测试计划文件是否存在
if [ ! -f "$TEST_PLAN" ]; then
    echo "错误: 找不到测试计划文件: $TEST_PLAN"
    exit 1
fi

echo "开始执行快速验证测试..."

# 执行JMeter测试
"$JMETER_PATH/jmeter" -n \
    -t "$TEST_PLAN" \
    -l "$RESULTS_DIR/quick_test_${TIMESTAMP}.jtl"

# 检查测试执行结果
if [ $? -eq 0 ]; then
    echo "=========================================="
    echo "快速验证测试完成!"
    echo "结果文件: $RESULTS_DIR/quick_test_${TIMESTAMP}.jtl"
    echo "=========================================="
    
    # 显示测试结果摘要
    echo "测试结果摘要:"
    echo "------------------------------------------"
    if [ -f "$RESULTS_DIR/quick_test_${TIMESTAMP}.jtl" ]; then
        total_requests=$(grep -c "HTTP" "$RESULTS_DIR/quick_test_${TIMESTAMP}.jtl" 2>/dev/null || echo "0")
        success_requests=$(grep -c "200" "$RESULTS_DIR/quick_test_${TIMESTAMP}.jtl" 2>/dev/null || echo "0")
        failed_requests=$((total_requests - success_requests))
        
        echo "总请求数: $total_requests"
        echo "成功请求: $success_requests"
        echo "失败请求: $failed_requests"
        
        if [ $failed_requests -eq 0 ]; then
            echo "✅ 所有请求都成功了！登录功能正常工作。"
        else
            echo "❌ 有 $failed_requests 个请求失败，请检查系统配置。"
            echo "详细错误信息请查看结果文件。"
        fi
    fi
else
    echo "=========================================="
    echo "快速验证测试失败!"
    echo "请检查错误信息并重试"
    echo "=========================================="
    exit 1
fi

echo ""
echo "测试完成时间: $(date)"
echo ""
echo "如果快速测试通过，可以运行完整性能测试:"
echo "  ./run_login_test.sh" 