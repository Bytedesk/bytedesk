<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-29 15:40:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-08 21:12:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# TODO:

- 修改用户名、头像、昵称、密码
<!-- - 注册登录 -->
<!-- - 权限、角色管理: @PreAuthorize("hasRole('ROLE_ADMIN')")使用 -->
- 消息支持超长文本
- 服务器当前状态页面
- 简版工单-类似泡米网工单模块
- 增加对接渠道：安卓、iOS、网页、公众号、抖音、Facebook等
- 对接：twitter、微博等社交平台，实时回复社交平台账号评论 或 @消息
- 配置对接第三方知识库问答，如：dify、fastgpt、网易qanything等知识库问答，支持权限数量配置，在客户端可见并可对话
- 混淆加密proguard - socket/agent jar
- 支持国产数据库
- 支持国产操作系统
<!-- - 支持更多的聊天场景，如：vscode 插件 -->
- 压力测试, [jmeter](https://jmeter.apache.org/)
- 压测客服排队，同时10000个用户请求，测试排队情况
- 付费解决方案：集群、统计、知识星球、付费群
- 后端创建 model，通过命令一键生成相关 api 文件，前端通过配置直接生成 CRUD 页面，并可生成统计 chart 页面
- 支持二级缓存，升级萝卜丝bytedesk-server4支持二级缓存
<!-- - 单独打包h2数据库版本，无需依赖mysql/redis等第三方 -->
- 后台发布公告，客户端接收
- 后台操作日志，管理后台/客户端登录日志
- 当前登录用户、在线时长、登录次数、登录IP、登录设备、登录地址
<!-- - 兼容postgre/oracle等 -->
- MQTT服务器参考[bifroMQ](https://bifromq.io/zh-Hans/docs/test_report/report/),并进行压测
- support plugins
- support docker deploy
- agent service tip support multi-language
- helpdesk knowledge support system, multi-language
- 英文版对标 [Rocket.Chat](https://www.rocket.chat/)
- 主动给用户生成个性化头像，并将头像文件存储本地
- 类似[集简云](https://www.jijyun.cn/) 对接各大平台
- 敏感词过滤，参考：[sensitive-word](https://github.com/houbb/sensitive-word?tab=readme-ov-file)，[sensitive](https://github.com/houbb/sensitive)
- 服务器端支持双击运行，并打开界面。exe4j
- 支持docker部署
- 国际化，支持更多语言：日语、韩语、中文简体、中文繁体、英文、法文、德文、西班牙文、俄文、阿拉伯文、希伯来文

## 客服模块

- 拦截骚扰用户
- 被禁ip/ip段
- 技能组对话
- 支持多语言设置
