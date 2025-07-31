#!/bin/bash

# Bytedesk登录性能测试执行脚本
# 使用方法: ./run_login_test.sh [JMeter安装路径]

# 设置默认JMeter路径
DEFAULT_JMETER_PATH="/usr/local/apache-jmeter-5.5/bin"
JMETER_PATH=${1:-$DEFAULT_JMETER_PATH}

# 测试配置
TEST_PLAN="01_login.jmx"
RESULTS_DIR="results"
REPORT_DIR="reports"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# 创建结果目录
mkdir -p "$RESULTS_DIR"
mkdir -p "$REPORT_DIR"

echo "=========================================="
echo "Bytedesk登录性能测试开始"
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

# 检查测试数据文件是否存在
if [ ! -f "users.csv" ]; then
    echo "错误: 找不到测试数据文件: users.csv"
    exit 1
fi

echo "开始执行测试..."

# 执行JMeter测试
"$JMETER_PATH/jmeter" -n \
    -t "$TEST_PLAN" \
    -l "$RESULTS_DIR/login_test_${TIMESTAMP}.jtl" \
    -e -o "$REPORT_DIR/login_test_${TIMESTAMP}"

# 检查测试执行结果
if [ $? -eq 0 ]; then
    echo "=========================================="
    echo "测试执行完成!"
    echo "结果文件: $RESULTS_DIR/login_test_${TIMESTAMP}.jtl"
    echo "报告目录: $REPORT_DIR/login_test_${TIMESTAMP}"
    echo "=========================================="
    
    # 显示测试结果摘要
    echo "测试结果摘要:"
    echo "------------------------------------------"
    if [ -f "$RESULTS_DIR/login_test_${TIMESTAMP}.jtl" ]; then
        echo "总请求数: $(grep -c "200" "$RESULTS_DIR/login_test_${TIMESTAMP}.jtl" 2>/dev/null || echo "0")"
        echo "成功请求: $(grep -c "200" "$RESULTS_DIR/login_test_${TIMESTAMP}.jtl" 2>/dev/null || echo "0")"
        echo "失败请求: $(grep -v "200" "$RESULTS_DIR/login_test_${TIMESTAMP}.jtl" | grep -c "HTTP" 2>/dev/null || echo "0")"
    fi
else
    echo "=========================================="
    echo "测试执行失败!"
    echo "请检查错误信息并重试"
    echo "=========================================="
    exit 1
fi

echo ""
echo "测试完成时间: $(date)"
echo "可以在浏览器中打开 $REPORT_DIR/login_test_${TIMESTAMP}/index.html 查看详细报告" 