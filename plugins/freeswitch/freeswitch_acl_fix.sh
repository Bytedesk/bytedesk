#!/bin/bash

# FreeSwitch ESL ACL 修复工具
# 专门用于解决 "rude-rejection" 问题
# 作者: Bytedesk Team
# 日期: 2025-06-08

set -e  # 脚本遇到错误时立即退出

# 配置变量
FREESWITCH_CONF_DIR="/usr/local/freeswitch/conf"
EVENT_SOCKET_CONF="$FREESWITCH_CONF_DIR/autoload_configs/event_socket.conf.xml"
ACL_CONF="$FREESWITCH_CONF_DIR/autoload_configs/acl.conf.xml"
BACKUP_DIR="/tmp/freeswitch_backup_$(date +%Y%m%d_%H%M%S)"

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

# 检查是否为root用户
check_root() {
    if [[ $EUID -ne 0 ]]; then
        print_error "此脚本需要root权限运行"
        echo "请使用: sudo $0"
        exit 1
    fi
}

# 检查FreeSwitch是否安装
check_freeswitch() {
    print_info "检查FreeSwitch安装状态..."
    
    if ! command -v fs_cli &> /dev/null; then
        print_warning "fs_cli命令未找到，尝试查找FreeSwitch安装位置..."
    fi
    
    # 查找可能的配置文件位置
    POSSIBLE_PATHS=(
        "/usr/local/freeswitch/conf"
        "/etc/freeswitch"
        "/opt/freeswitch/conf"
        "/usr/share/freeswitch/conf"
    )
    
    for path in "${POSSIBLE_PATHS[@]}"; do
        if [[ -d "$path/autoload_configs" ]]; then
            FREESWITCH_CONF_DIR="$path"
            EVENT_SOCKET_CONF="$path/autoload_configs/event_socket.conf.xml"
            ACL_CONF="$path/autoload_configs/acl.conf.xml"
            print_success "找到FreeSwitch配置目录: $path"
            break
        fi
    done
    
    if [[ ! -d "$FREESWITCH_CONF_DIR" ]]; then
        print_error "未找到FreeSwitch配置目录"
        exit 1
    fi
}

# 备份现有配置
backup_config() {
    print_info "备份现有配置到: $BACKUP_DIR"
    mkdir -p "$BACKUP_DIR"
    
    if [[ -f "$EVENT_SOCKET_CONF" ]]; then
        cp "$EVENT_SOCKET_CONF" "$BACKUP_DIR/"
        print_success "已备份event_socket.conf.xml"
    fi
    
    if [[ -f "$ACL_CONF" ]]; then
        cp "$ACL_CONF" "$BACKUP_DIR/"
        print_success "已备份acl.conf.xml"
    fi
}

# 修复event_socket.conf.xml
fix_event_socket_config() {
    print_info "修复event_socket.conf.xml配置..."
    
    cat > "$EVENT_SOCKET_CONF" << 'EOF'
<configuration name="event_socket.conf" description="Socket Client">
  <settings>
    <param name="nat-map" value="false"/>
    <param name="listen-ip" value="0.0.0.0"/>
    <param name="listen-port" value="8021"/>
    <param name="password" value="bytedesk123"/>
    
    <!-- ACL限制已移除，允许所有连接 -->
    <!-- 生产环境建议重新添加IP限制 -->
    <!-- <param name="apply-inbound-acl" value="loopback.auto"/> -->
    <!-- <param name="apply-inbound-acl" value="lan"/> -->
    
    <!--<param name="stop-on-bind-error" value="true"/>-->
  </settings>
</configuration>
EOF
    
    print_success "event_socket.conf.xml配置已修复"
}

# 修复acl.conf.xml (添加宽松的ACL配置)
fix_acl_config() {
    print_info "修复acl.conf.xml配置..."
    
    cat > "$ACL_CONF" << 'EOF'
<configuration name="acl.conf" description="Network Lists">
  <network-lists>
    <!-- 允许所有连接的ACL配置 (用于解决连接问题) -->
    <list name="bytedesk_open" default="allow">
      <node type="allow" cidr="0.0.0.0/0"/>
      <node type="allow" cidr="::/0"/>
    </list>
    
    <!-- 保守的局域网配置 -->
    <list name="lan" default="allow">
      <node type="allow" cidr="192.168.0.0/16"/>
      <node type="allow" cidr="10.0.0.0/8"/>
      <node type="allow" cidr="172.16.0.0/12"/>
      <node type="allow" cidr="127.0.0.0/8"/>
    </list>

    <!-- 域配置 -->
    <list name="domains" default="deny">
      <node type="allow" domain="$${domain}"/>
    </list>
  </network-lists>
</configuration>
EOF
    
    print_success "acl.conf.xml配置已修复"
}

# 重新加载FreeSwitch配置
reload_freeswitch() {
    print_info "重新加载FreeSwitch配置..."
    
    # 尝试重新加载event_socket模块
    if command -v fs_cli &> /dev/null; then
        print_info "使用fs_cli重新加载event_socket模块..."
        if fs_cli -x "reload mod_event_socket"; then
            print_success "event_socket模块重新加载成功"
            return 0
        else
            print_warning "模块重新加载失败，将尝试重启FreeSwitch服务"
        fi
    fi
    
    # 尝试重启FreeSwitch服务
    print_info "重启FreeSwitch服务..."
    if systemctl restart freeswitch; then
        print_success "FreeSwitch服务重启成功"
        sleep 3  # 等待服务完全启动
    elif service freeswitch restart; then
        print_success "FreeSwitch服务重启成功"
        sleep 3
    else
        print_error "无法重启FreeSwitch服务"
        return 1
    fi
}

# 测试修复结果
test_fix() {
    print_info "测试修复结果..."
    
    # 等待服务启动
    sleep 2
    
    # 测试本地连接
    print_info "测试本地ESL连接..."
    if timeout 5 bash -c "echo '' | telnet localhost 8021" 2>/dev/null | grep -q "Content-Type"; then
        response=$(timeout 3 bash -c "echo '' | telnet localhost 8021" 2>/dev/null)
        if echo "$response" | grep -q "rude-rejection"; then
            print_error "修复失败：仍然收到rude-rejection错误"
            return 1
        else
            print_success "本地ESL连接测试成功"
        fi
    else
        print_error "无法连接到本地ESL端口"
        return 1
    fi
    
    # 检查端口监听状态
    if netstat -tlnp | grep ":8021 " | grep -q LISTEN; then
        print_success "端口8021正在监听"
    else
        print_warning "端口8021未在监听状态"
    fi
    
    # 显示FreeSwitch进程状态
    if pgrep freeswitch > /dev/null; then
        print_success "FreeSwitch进程运行中"
    else
        print_error "FreeSwitch进程未运行"
        return 1
    fi
}

# 显示修复后的连接测试命令
show_test_commands() {
    print_info "修复完成！请使用以下命令测试连接："
    echo ""
    echo "1. 本地测试:"
    echo "   telnet localhost 8021"
    echo ""
    echo "2. 远程测试 (从客户端):"
    echo "   telnet $(hostname -I | awk '{print $1}') 8021"
    echo ""
    echo "3. 使用fs_cli测试:"
    echo "   fs_cli -H localhost -P 8021 -p bytedesk123"
    echo ""
    echo "4. 查看FreeSwitch日志:"
    echo "   tail -f /usr/local/freeswitch/log/freeswitch.log"
    echo ""
    
    if [[ -n "$BACKUP_DIR" ]]; then
        print_info "配置备份位置: $BACKUP_DIR"
        echo "如需恢复原配置，请运行:"
        echo "   cp $BACKUP_DIR/*.xml $FREESWITCH_CONF_DIR/autoload_configs/"
    fi
}

# 主函数
main() {
    echo "======================================="
    echo "  FreeSwitch ESL ACL 修复工具"
    echo "  解决 rude-rejection 连接问题"
    echo "======================================="
    echo ""
    
    check_root
    check_freeswitch
    backup_config
    fix_event_socket_config
    fix_acl_config
    
    if reload_freeswitch; then
        if test_fix; then
            print_success "修复成功完成！"
            show_test_commands
        else
            print_error "修复验证失败，请检查日志"
            exit 1
        fi
    else
        print_error "FreeSwitch重启失败"
        exit 1
    fi
}

# 脚本入口
main "$@"
