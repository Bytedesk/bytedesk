<!DOCTYPE html>
<html lang="${lang! 'zh-CN'}">
<head>
	<#--  Header  -->
	<#include "../common/header_meta.ftl" />
	<#include "../common/header_js.ftl" />
	<#include "../common/header_css.ftl" />
	<#-- i18n macro -->
	<#include "../common/macro/i18n.ftl" />
	
</head>

<body>

<#--  导航  -->
<#include "../common/header_nav.ftl" />

<!-- **************** MAIN CONTENT START **************** -->
<main>

<!-- =======================
Page Banner START -->
<section class="bg-primary bg-opacity-10" style="padding-top: 6rem; padding-bottom: 3rem;">
	<div class="container">
		<div class="row g-4 g-md-5 position-relative">
			<!-- Main content START -->
			<div class="col-lg-8">
				<!-- Title -->
				<h1><@t key="page.mcp.title">MCP 服务 - Model Context Protocol</@t></h1>
				<p class="lead"><@t key="page.mcp.subtitle">开源 MCP 服务，基于 MCP 协议对外开放微语核心能力，让 AI Agent 无缝调用客服、知识库、工单等业务接口</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="/assets/images/element/engineering.svg" class="h-200px" alt="<@t key='alt.mcp.icon'>MCP 服务图标</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Page Banner END -->

<!-- =======================
What is MCP START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.mcp.what.title">什么是 MCP？</@t></h2>
			</div>
		</div>

		<div class="row g-4 justify-content-center">
			<div class="col-lg-8">
				<div class="bg-light p-4 rounded-3">
					<p><@t key="page.mcp.what.desc">MCP（Model Context Protocol）是一种开放协议，允许 AI 应用程序通过标准化的接口连接到外部工具和数据源。微语将客服、知识库、工单等核心能力封装为 MCP Server，第三方 AI Agent（如 Claude、Codex、ChatGPT 等）可以通过 MCP 协议直接调用微语业务能力。</@t></p>
					<p class="mb-0"><@t key="page.mcp.what.desc2">通过 MCP 协议，微语不仅仅是客服系统，更是一个可被 AI Agent 编程的业务平台。</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
What is MCP END -->

<!-- =======================
Features START -->
<section>
	<div class="container">
		<!-- Title -->
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.mcp.features.title">核心能力</@t></h2>
				<p class="mb-0"><@t key="page.mcp.features.desc">通过 MCP 协议对外开放的微语服务</@t></p>
			</div>
		</div>

		<!-- Feature list -->
		<div class="row g-4">
			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-chat-dots fs-5"></i>
					</div>
					<h5><@t key="page.mcp.feature.kbase.title">知识库查询</@t></h5>
					<p class="mb-0"><@t key="page.mcp.feature.kbase.desc">AI Agent 可实时检索企业知识库，获取精准答案，提升智能问答准确率</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-ticket-perforated fs-5"></i>
					</div>
					<h5><@t key="page.mcp.feature.ticket.title">工单操作</@t></h5>
					<p class="mb-0"><@t key="page.mcp.feature.ticket.desc">支持创建、查询、更新工单，AI Agent 可自动生成和管理服务工单</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-headset fs-5"></i>
					</div>
					<h5><@t key="page.mcp.feature.service.title">客服查询</@t></h5>
					<p class="mb-0"><@t key="page.mcp.feature.service.desc">查询会话记录、客服状态、工作组信息，辅助 AI 做出智能路由决策</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-people fs-5"></i>
					</div>
					<h5><@t key="page.mcp.feature.user.title">用户信息</@t></h5>
					<p class="mb-0"><@t key="page.mcp.feature.user.desc">获取访客、会员、客服等用户画像信息，实现千人千面的智能服务</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-shield-lock fs-5"></i>
					</div>
					<h5><@t key="page.mcp.feature.auth.title">安全认证</@t></h5>
					<p class="mb-0"><@t key="page.mcp.feature.auth.desc">Bearer Token 认证机制，支持工具白名单、权限控制和审计日志</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-diagram-3 fs-5"></i>
					</div>
					<h5><@t key="page.mcp.feature.extend.title">持续扩展</@t></h5>
					<p class="mb-0"><@t key="page.mcp.feature.extend.desc">呼叫中心、数据分析、订单管理等更多 MCP 工具逐步开放</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Use Cases START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.mcp.usecases.title">应用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Use case 1 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<div class="d-flex align-items-center mb-3">
							<div class="icon-md bg-primary bg-opacity-10 text-primary rounded-circle me-3">
								<i class="bi bi-terminal fs-5"></i>
							</div>
							<h5 class="card-title mb-0"><@t key="page.mcp.usecase.claude.title">Claude Code 集成</@t></h5>
						</div>
						<p class="card-text mb-0"><@t key="page.mcp.usecase.claude.desc">在 Claude Code 中直接调用微语 MCP Server，实现用自然语言查询知识库、创建工单等操作，让开发与客服无缝衔接</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 2 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<div class="d-flex align-items-center mb-3">
							<div class="icon-md bg-success bg-opacity-10 text-success rounded-circle me-3">
								<i class="bi bi-code-square fs-5"></i>
							</div>
							<h5 class="card-title mb-0"><@t key="page.mcp.usecase.codex.title">Codex / Copilot 集成</@t></h5>
						</div>
						<p class="card-text mb-0"><@t key="page.mcp.usecase.codex.desc">在 VS Code / JetBrains 等 IDE 中，通过 MCP 协议让 AI 编程助手直接操作微语系统，实现代码级业务集成</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 3 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<div class="d-flex align-items-center mb-3">
							<div class="icon-md bg-warning bg-opacity-10 text-warning rounded-circle me-3">
								<i class="bi bi-robot fs-5"></i>
							</div>
							<h5 class="card-title mb-0"><@t key="page.mcp.usecase.agent.title">自定义 AI Agent</@t></h5>
						</div>
						<p class="card-text mb-0"><@t key="page.mcp.usecase.agent.desc">企业可基于微语 MCP Server 构建专属 AI Agent，打通内部系统，实现自动化客服、智能运营等场景</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 4 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<div class="d-flex align-items-center mb-3">
							<div class="icon-md bg-info bg-opacity-10 text-info rounded-circle me-3">
								<i class="bi bi-graph-up-arrow fs-5"></i>
							</div>
							<h5 class="card-title mb-0"><@t key="page.mcp.usecase.automation.title">业务流程自动化</@t></h5>
						</div>
						<p class="card-text mb-0"><@t key="page.mcp.usecase.automation.desc">结合工作流引擎，通过 MCP 工具调用实现客服-工单-知识库全流程自动化</@t></p>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Use Cases END -->

<!-- =======================
Quick Start START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.mcp.quickstart.title">快速开始</@t></h2>
			</div>
		</div>

		<div class="row g-4 justify-content-center">
			<div class="col-lg-8">
				<!-- Step 1 -->
				<div class="d-flex mb-4">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle flex-shrink-0 me-3 d-flex align-items-center justify-content-center">
						<span class="fs-5 fw-bold">1</span>
					</div>
					<div>
						<h5><@t key="page.mcp.quickstart.step1.title">获取 Access Token</@t></h5>
						<p class="mb-0"><@t key="page.mcp.quickstart.step1.desc">在微语管理后台生成 MCP API Token，配置工具权限白名单</@t></p>
					</div>
				</div>

				<!-- Step 2 -->
				<div class="d-flex mb-4">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle flex-shrink-0 me-3 d-flex align-items-center justify-content-center">
						<span class="fs-5 fw-bold">2</span>
					</div>
					<div>
						<h5><@t key="page.mcp.quickstart.step2.title">配置 MCP Client</@t></h5>
						<p class="mb-0"><@t key="page.mcp.quickstart.step2.desc">在 Claude Desktop、Codex 或其他 MCP 客户端中配置微语 MCP Server 连接地址</@t></p>
					</div>
				</div>

				<!-- Step 3 -->
				<div class="d-flex mb-4">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle flex-shrink-0 me-3 d-flex align-items-center justify-content-center">
						<span class="fs-5 fw-bold">3</span>
					</div>
					<div>
						<h5><@t key="page.mcp.quickstart.step3.title">开始调用</@t></h5>
						<p class="mb-0"><@t key="page.mcp.quickstart.step3.desc">用自然语言描述需求，AI Agent 将自动选择合适的 MCP 工具完成操作</@t></p>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Quick Start END -->

<#include "../common/action_box.ftl" />

</main>
<!-- **************** MAIN CONTENT END **************** -->

<!-- ======================= Footer START -->
<#include "../common/footer_nav.ftl" />
<!-- ======================= Footer END -->

<#include "../common/footer_js.ftl" />

<#-- livechat code 客服代码  -->
<#include "../common/bytedesk.ftl" />

<#-- trace code 统计代码  -->
<#include "../common/track.ftl" />

</body>
</html>
