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
<section class="bg-light" style="padding-top: 15rem; padding-bottom: 3rem;">
	<div class="container">
		<div class="row g-4 g-md-5 position-relative">
			<!-- Main content START -->
			<div class="col-lg-8">
				<!-- Title -->
				<h1><@t key="page.open.title">开放平台 - API与SDK</@t></h1>
				<p class="lead"><@t key="page.open.subtitle">强大的API接口和SDK工具，快速集成微语能力到您的应用</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="/assets/images/element/engineering.svg" class="h-200px" alt="<@t key='alt.open.icon'>开放平台图标</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Page Banner END -->

<!-- =======================
Features START -->
<section>
	<div class="container">
		<!-- Title -->
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.open.features.title">核心能力</@t></h2>
				<p class="mb-0"><@t key="page.open.features.desc">丰富的API接口和开发工具</@t></p>
			</div>
		</div>

		<!-- Feature list -->
		<div class="row g-4">
			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-code-square fs-5"></i>
					</div>
					<h5><@t key="page.open.feature.api.title">RESTful API</@t></h5>
					<p class="mb-0"><@t key="page.open.feature.api.desc">标准化的REST API接口，支持所有核心功能的调用和集成</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-box-seam fs-5"></i>
					</div>
					<h5><@t key="page.open.feature.sdk.title">多语言SDK</@t></h5>
					<p class="mb-0"><@t key="page.open.feature.sdk.desc">提供Java、Python、JavaScript等主流语言SDK，快速上手</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-book fs-5"></i>
					</div>
					<h5><@t key="page.open.feature.doc.title">完善文档</@t></h5>
					<p class="mb-0"><@t key="page.open.feature.doc.desc">详细的API文档、代码示例、最佳实践，助您快速集成</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-shield-check fs-5"></i>
					</div>
					<h5><@t key="page.open.feature.auth.title">安全认证</@t></h5>
					<p class="mb-0"><@t key="page.open.feature.auth.desc">OAuth 2.0、API Key等多种认证方式，保障API调用安全</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-plug fs-5"></i>
					</div>
					<h5><@t key="page.open.feature.webhook.title">Webhook回调</@t></h5>
					<p class="mb-0"><@t key="page.open.feature.webhook.desc">事件驱动的消息推送，实时获取系统状态变化</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-speedometer2 fs-5"></i>
					</div>
					<h5><@t key="page.open.feature.sandbox.title">沙箱环境</@t></h5>
					<p class="mb-0"><@t key="page.open.feature.sandbox.desc">独立的测试环境，安全验证集成逻辑，无需担心影响生产数据</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
API Categories START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.open.categories.title">API分类</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Category 1 -->
			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100">
					<div class="card-body">
						<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mx-auto mb-3">
							<i class="bi bi-chat-dots fs-4"></i>
						</div>
						<h5 class="card-title"><@t key="page.open.category.message.title">消息API</@t></h5>
						<p class="card-text small"><@t key="page.open.category.message.desc">发送消息、接收消息、消息管理</@t></p>
					</div>
				</div>
			</div>

			<!-- Category 2 -->
			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100">
					<div class="card-body">
						<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mx-auto mb-3">
							<i class="bi bi-people fs-4"></i>
						</div>
						<h5 class="card-title"><@t key="page.open.category.user.title">用户API</@t></h5>
						<p class="card-text small"><@t key="page.open.category.user.desc">用户管理、权限控制、组织架构</@t></p>
					</div>
				</div>
			</div>

			<!-- Category 3 -->
			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100">
					<div class="card-body">
						<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mx-auto mb-3">
							<i class="bi bi-robot fs-4"></i>
						</div>
						<h5 class="card-title"><@t key="page.open.category.ai.title">AI API</@t></h5>
						<p class="card-text small"><@t key="page.open.category.ai.desc">AI对话、知识库问答、智能体调用</@t></p>
					</div>
				</div>
			</div>

			<!-- Category 4 -->
			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100">
					<div class="card-body">
						<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mx-auto mb-3">
							<i class="bi bi-graph-up fs-4"></i>
						</div>
						<h5 class="card-title"><@t key="page.open.category.analytics.title">数据API</@t></h5>
						<p class="card-text small"><@t key="page.open.category.analytics.desc">数据统计、报表导出、业务分析</@t></p>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
API Categories END -->

<!-- =======================
Use Cases START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.open.usecases.title">应用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Use case 1 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.open.usecase.integration.title">系统集成</@t></h5>
						<p class="card-text"><@t key="page.open.usecase.integration.desc">将微语能力集成到现有OA、ERP、CRM等业务系统中</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 2 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.open.usecase.custom.title">定制开发</@t></h5>
						<p class="card-text"><@t key="page.open.usecase.custom.desc">基于API开发专属功能，打造符合企业需求的解决方案</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 3 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.open.usecase.automation.title">流程自动化</@t></h5>
						<p class="card-text"><@t key="page.open.usecase.automation.desc">通过API实现业务流程自动化，提升工作效率</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 4 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.open.usecase.third.title">第三方应用</@t></h5>
						<p class="card-text"><@t key="page.open.usecase.third.desc">构建基于微语平台的第三方应用和插件生态</@t></p>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Use Cases END -->

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
