package com.bytedesk.call.config;

public class CallI18nConsts {

	private CallI18nConsts() {
	}

	public static final String ACTION_QUERY_BY_ORG = "组织查询";
	public static final String ACTION_QUERY_BY_USER = "用户查询";
	public static final String ACTION_QUERY_DETAIL = "查询详情";
	public static final String ACTION_CREATE = "新建";
	public static final String ACTION_UPDATE = "更新";
	public static final String ACTION_DELETE = "删除";
	public static final String ACTION_EXPORT = "导出";

	public static final String EXCEL_LABEL_NAME = "标签名称";
	public static final String EXCEL_LABEL_TYPE = "类型";
	public static final String EXCEL_LABEL_COLOR = "颜色";
	public static final String EXCEL_LABEL_CREATED_AT = "创建时间";

	public static final String ERROR_PERMISSION_CREATE_TAG = "无权限创建该层级的标签数据";
	public static final String ERROR_PERMISSION_UPDATE_TAG = "无权限更新该标签数据";
	public static final String ERROR_PERMISSION_DELETE_TAG = "无权限删除该标签数据";
	public static final String ERROR_OPTIMISTIC_LOCK_TEMPLATE = "无法处理乐观锁冲突: {}";
	public static final String ERROR_OPTIMISTIC_LOCK_PREFIX = "无法处理乐观锁冲突: ";

	public static final String HEALTH_STATUS_CONNECTABLE = "可连接";
	public static final String HEALTH_STATUS_CONNECTION_FAILED = "连接失败";
	public static final String HEALTH_STATUS_CHECK_EXCEPTION = "检查异常";
	public static final String HEALTH_MESSAGE_ESL_PORT_REACHABLE = "Call ESL端口可访问";
	public static final String HEALTH_MESSAGE_ESL_PORT_UNREACHABLE = "无法连接到Call ESL端口";
	public static final String HEALTH_TROUBLESHOOT_NETWORK = "检查Call服务状态和网络连接";
	public static final String HEALTH_MESSAGE_EXCEPTION_DURING_CHECK = "健康检查过程中发生异常";
	public static final String HEALTH_LOG_CONNECTION_FAILED = "Call健康检查连接失败: {}";
	public static final String HEALTH_MESSAGE_CONNECT_SUCCESS = "连接成功";
	public static final String HEALTH_MESSAGE_ACL_REJECTED = "连接被拒绝 - ACL限制";
	public static final String HEALTH_TROUBLESHOOT_ACL = "检查Call的ACL配置";
	public static final String HEALTH_MESSAGE_WAIT_AUTH = "服务正常 - 等待认证";
	public static final String HEALTH_MESSAGE_READ_RESPONSE_FAILED_PREFIX = "连接成功但读取响应失败: ";
	public static final String HEALTH_MESSAGE_CONNECT_FAILED_PREFIX = "连接失败: ";
	public static final String HEALTH_TROUBLESHOOT_SERVICE_AND_PORT = "检查Call服务状态和端口配置";
	public static final String HEALTH_TROUBLESHOOT_FIREWALL = "检查网络连接和防火墙设置";

	public static final String CONNECTION_TEST_START = "开始Call连接测试...";
	public static final String CONNECTION_TEST_TARGET = "测试连接到Call ESL: {}:{}";
	public static final String CONNECTION_TEST_NETWORK_SUCCESS = "✅ 网络连接成功: {}:{}";
	public static final String CONNECTION_TEST_RESPONSE_RECEIVED = "收到Call响应: {}";
	public static final String CONNECTION_TEST_ACL_REJECTED = "❌ Call ESL拒绝连接 - Access Control List (ACL) 限制";
	public static final String CONNECTION_TEST_SOLUTION = "解决方案:";
	public static final String CONNECTION_TEST_SOLUTION_STEP_1 = "1. 修改Call的 event_socket.conf.xml 文件";
	public static final String CONNECTION_TEST_SOLUTION_STEP_2 = "2. 在ACL配置中添加允许当前IP地址的规则";
	public static final String CONNECTION_TEST_SOLUTION_STEP_3 = "3. 或者移除 apply-inbound-acl 参数以允许所有连接";
	public static final String CONNECTION_TEST_WAIT_AUTH = "✅ Call ESL服务正常，等待认证";
	public static final String CONNECTION_TEST_READ_TIMEOUT = "⚠️ 读取Call响应超时，但连接已建立";
	public static final String CONNECTION_TEST_READ_FAILED = "❌ 读取Call响应失败: {}";
	public static final String CONNECTION_TEST_CONNECT_FAILED = "❌ 连接失败: {}";
	public static final String CONNECTION_TEST_POSSIBLE_REASON = "可能的原因:";
	public static final String CONNECTION_TEST_REASON_SERVICE_NOT_RUNNING = "1. Call服务未运行";
	public static final String CONNECTION_TEST_REASON_PORT_NOT_OPEN = "2. 端口{}未开放";
	public static final String CONNECTION_TEST_REASON_FIREWALL = "3. 防火墙阻止了连接";
	public static final String CONNECTION_TEST_REASON_NETWORK_TIMEOUT = "1. 网络连接超时";
	public static final String CONNECTION_TEST_REASON_SERVER_INVALID = "2. 服务器地址不正确: {}";
	public static final String CONNECTION_TEST_REASON_ROUTE = "3. 路由或网络配置问题";
	public static final String CONNECTION_TEST_CURRENT_CONFIG = "当前配置: 服务器={}, 端口={}, 密码={}";
	public static final String CONNECTION_TEST_FINISH = "Call连接测试完成";
	public static final String CONNECTION_DIAGNOSTIC_HEADER = "=== Call ESL 连接诊断信息 ===";
	public static final String CONNECTION_DIAGNOSTIC_SERVER = "服务器地址: {}";
	public static final String CONNECTION_DIAGNOSTIC_PORT = "ESL端口: {}";
	public static final String CONNECTION_DIAGNOSTIC_PASSWORD = "ESL密码: {}";
	public static final String CONNECTION_DIAGNOSTIC_ENABLED = "启用状态: {}";
	public static final String CONNECTION_DIAGNOSTIC_GUIDE = "\n=== 故障排除指南 ===";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_SERVICE = "1. 检查Call服务状态:";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_SERVICE_CMD_1 = "   sudo systemctl status freeswitch";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_SERVICE_CMD_2 = "   或 ps aux | grep freeswitch";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_PORT = "\n2. 检查端口监听:";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_PORT_CMD_1 = "   netstat -tlnp | grep 8021";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_PORT_CMD_2 = "   或 ss -tlnp | grep 8021";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_TELNET = "\n3. 测试端口连通性:";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_TELNET_CMD = "   telnet {} {}";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_CONFIG = "\n4. 检查Call配置:";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_CONFIG_FILE = "   配置文件: conf/autoload_configs/event_socket.conf.xml";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_ACL_FILE = "   ACL配置: conf/autoload_configs/acl.conf.xml";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_LOG = "\n5. 查看Call日志:";
	public static final String CONNECTION_DIAGNOSTIC_CHECK_LOG_CMD = "   tail -f /usr/local/freeswitch/log/freeswitch.log";
	public static final String CONNECTION_DIAGNOSTIC_FOOTER = "=====================================";

	public static final String ESL_ACTION_CONNECTION_ISSUE_RETRY = "ESL action={} 执行时检测到连接问题，尝试重连后重试: {}";
	public static final String ESL_CLOSE_RECONNECT_IGNORED = "ESL close during reconnect ignored: {}";
	public static final String ESL_CONNECTION_READY = "ESL连接可用: {}:{}";
	public static final String ESL_RECONNECT_FAILED = "ESL重连失败 attempt={}/{}: {}";
	public static final String ESL_RECONNECT_INTERRUPTED = "ESL重连被中断";
	public static final String ESL_CONNECT_FAILED_MAX_RETRIES = "ESL连接失败，已达到最大重试次数";
	public static final String ESL_ACTION_EMPTY_RESPONSE = "ESL action={} 返回空响应";
	public static final String ESL_ACTION_SUCCESS = "ESL action={} 成功: {}";
	public static final String ESL_ACTION_FAILED = "ESL action={} 失败: {}";

	public static final String CONFIG_ESL_CONNECTED = "Call ESL连接成功，服务器: {}:{}";
	public static final String CONFIG_ESL_CONNECTED_UNSTABLE = "ESL连接建立但无法发送命令，连接可能不稳定";
	public static final String CONFIG_ESL_CONNECT_ATTEMPT_FAILED = "第{}次ESL连接失败: {}";
	public static final String CONFIG_ESL_ACL_REJECTED_REASON = "Call ESL拒绝连接 - 可能的原因:";
	public static final String CONFIG_ESL_PASSWORD_WRONG = "1. ESL密码错误 (当前密码: {})";
	public static final String CONFIG_ESL_IP_NOT_ALLOWED = "2. IP地址不在Call的访问控制列表(ACL)中";
	public static final String CONFIG_ESL_SOCKET_CONFIG_RESTRICTED = "3. Call的event_socket.conf.xml配置限制了外部连接";
	public static final String CONFIG_ESL_FIREWALL_BLOCKED = "4. 防火墙阻止了连接";
	public static final String CONFIG_ESL_ALL_ATTEMPTS_REJECTED = "所有连接尝试都被拒绝，请检查Call的ESL配置";
	public static final String CONFIG_ESL_NETWORK_ISSUE_REASON = "网络连接问题 - 可能的原因:";
	public static final String CONFIG_ESL_SERVICE_NOT_RUNNING = "1. Call服务未运行";
	public static final String CONFIG_ESL_PORT_BLOCKED = "2. 端口{}未开放或被防火墙阻止";
	public static final String CONFIG_ESL_NETWORK_TIMEOUT = "3. 网络连接超时";
	public static final String CONFIG_ESL_WAIT_RETRY = "等待{}毫秒后重试...";
	public static final String CONFIG_ESL_RETRY_INTERRUPTED = "连接重试被中断";
	public static final String CONFIG_ESL_FINAL_FAILURE = "Call ESL连接最终失败，已尝试{}次";
	public static final String CONFIG_ESL_SKIP_FILTER_REGISTER = "跳过ESL事件过滤器注册（bytedesk.call.freeswitch.enableEventFilters=false）";

	public static final String TASK_DEMO_CUSTOMER_LIST_NAME = "默认客户列表";
	public static final String TASK_DEMO_CUSTOMER_LIST_DESCRIPTION = "默认客户列表，用于外呼任务预览。";
	public static final String TASK_DEMO_CUSTOMER_LIST_TAGS = "demo,outbound,seed";
	public static final String TASK_DEMO_NAME = "默认外呼任务";
	public static final String TASK_DEMO_DESCRIPTION = "用于预览每日外呼调度的种子任务。";
	public static final String TASK_CREATE_FAILED = "Create call_task failed";
	public static final String TASK_UPDATE_FAILED = "Update call_task failed";
	public static final String TASK_NOT_FOUND = "CallTask not found";
	public static final String TASK_CUSTOMER_LIST_NOT_FOUND = "CallCustomerList not found";
	public static final String TASK_DEMO_INIT_SKIPPED = "Skip demo outbound task initialization because customer list was not created. orgUid={}";
	public static final String TASK_DEMO_CUSTOMER_DESCRIPTION = "演示用客户。";
	public static final String TASK_DEMO_CUSTOMER_TITLE = "跟进联系人";
	public static final String TASK_DEMO_CUSTOMER_ADDRESS = "演示地址";
	public static final String TASK_DEMO_CUSTOMER_REMARK = "演示用客户记录，用于外呼任务初始化。";
}
