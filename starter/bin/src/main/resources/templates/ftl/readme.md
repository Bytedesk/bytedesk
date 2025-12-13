# FreeMarker Templates Directory

本目录包含所有 FreeMarker 模板文件，按功能分类组织。

## 目录结构

```
ftl/
├── index.ftl              # 首页
├── home.ftl               # 主页
├── default.ftl            # 默认页面
├── dev.ftl                # 开发页面
├── readme.md              # 本文档
├── common/                # 通用组件
│   ├── header_meta.ftl    # HTML头部元数据
│   ├── header_nav.ftl     # 导航栏
│   ├── footer_nav.ftl     # 页脚导航
│   ├── action_box.ftl     # 行动号召组件
│   ├── bytedesk.ftl       # 微语组件
│   └── ...
├── features/              # 产品功能页面
│   ├── team.ftl           # 企业IM - 团队协作
│   ├── ai.ftl             # AI Agent - 智能代理
│   ├── kbase.ftl          # 知识库 - 帮助中心
│   ├── voc.ftl            # VOC - 客户之声
│   ├── ticket.ftl         # 工单系统
│   ├── workflow.ftl       # 工作流平台
│   ├── kanban.ftl         # 任务管理 - 看板
│   ├── callcenter.ftl     # 呼叫中心
│   ├── video.ftl          # 视频会议
│   ├── office.ftl         # 在线客服
│   └── scrm.ftl           # SCRM - 客户管理
├── pages/                 # 静态页面
│   ├── about.ftl          # 关于我们
│   ├── contact.ftl        # 联系我们
│   ├── download.ftl       # 下载中心
│   ├── privacy.ftl        # 隐私政策
│   └── terms.ftl          # 服务条款
├── plan/                  # 计划/套餐页面
│   ├── ai.ftl             # AI计划
│   ├── cs.ftl             # 客服计划
│   ├── im-com.ftl         # 企业IM计划
│   └── im-social.ftl      # 社交IM计划
├── error/                 # 错误页面
│   ├── 403.ftl            # 403禁止访问
│   ├── 404.ftl            # 404页面未找到
│   └── error.ftl          # 通用错误页
└── i18n/                  # 国际化资源
    ├── messages_zh-CN.properties    # 简体中文
    ├── messages_zh-TW.properties    # 繁体中文
    ├── messages_en.properties       # 英文
    ├── messages_meta_zh-CN.properties
    ├── messages_meta_zh-TW.properties
    └── messages_meta_en.properties
```

## 页面分类说明

### features/ - 产品功能页面
包含所有产品功能的详细介绍页面，每个页面展示：
- 功能特点（6个核心特性）
- 技术优势/平台支持
- 应用场景（4个典型用例）

**访问路径**: `/{feature-name}` 或 `/{feature-name}.html`
- 例如：`/team`、`/ai.html`、`/kbase`

### pages/ - 静态页面
包含网站的静态信息页面：
- 关于我们
- 联系方式
- 下载中心
- 法律条款（隐私政策、服务条款）

**访问路径**: `/{page-name}` 或 `/{page-name}.html`
- 例如：`/about`、`/contact.html`、`/privacy`

### plan/ - 计划/套餐页面
包含各产品线的定价计划和套餐介绍。

### common/ - 通用组件
可复用的页面组件和布局模板。

## Controller 映射

### PageRouteController.java
- **静态页面**: `handlePageRoutes()` - 处理 pages/ 目录下的模板
- **功能页面**: `handleFeatureRoutes()` - 处理 features/ 目录下的模板

### PageTemplateController.java
- **静态化**: `/temp/static` - 生成所有页面的静态HTML文件（支持多语言）

## 国际化支持

所有页面都支持多语言：
- 简体中文 (zh-CN)
- 繁体中文 (zh-TW)
- English (en)

使用宏标签: `<@t key="page.team.title">默认文本</@t>`

## 开发指南

### 添加新功能页面

1. 在 `features/` 目录创建新的 `.ftl` 文件
2. 在 `i18n/messages_*.properties` 添加国际化文本
3. 在 `PageRouteController.java` 的 `handleFeatureRoutes()` 方法中添加路由
4. 在 `PageTemplateController.java` 的 `staticize()` 方法中添加静态化调用
5. 更新 `index.ftl` 添加功能卡片链接

### 添加新静态页面

1. 在 `pages/` 目录创建新的 `.ftl` 文件
2. 添加国际化文本（如需要）
3. 在 `PageRouteController.java` 的 `handlePageRoutes()` 方法中添加路由
4. 在 `PageTemplateController.java` 中添加静态化调用（如需要）

## 注意事项

- 所有模板文件必须使用 UTF-8 编码
- 页面标题、描述等SEO信息应通过i18n配置
- 公共组件修改会影响所有引用该组件的页面
- 静态化生成的HTML文件位于 `target/classes/templates/` 目录

---

## Legacy

- [eduport](http://localhost:8903/eduport/index-7.html)

