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
				<h1><@t key="page.cli.title">微语 CLI - 命令行工具</@t></h1>
				<p class="lead"><@t key="page.cli.subtitle">开源命令行工具，在终端中直接操作微语系统，查询知识库、管理工单、查看会话，效率翻倍</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="/assets/images/element/coding.svg" class="h-200px" alt="<@t key='alt.cli.icon'>CLI 命令行工具图标</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Page Banner END -->

<!-- =======================
What is CLI START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.cli.what.title">什么是微语 CLI？</@t></h2>
			</div>
		</div>

		<div class="row g-4 justify-content-center">
			<div class="col-lg-8">
				<div class="bg-light p-4 rounded-3">
					<p><@t key="page.cli.what.desc">微语 CLI（Command Line Interface）是一个命令行工具，让你无需打开浏览器就能高效操作微语系统。你可以通过简单的命令查询知识库文章、创建和管理工单、查看在线客服会话状态、搜索用户信息等。</@t></p>
					<p class="mb-0"><@t key="page.cli.what.desc2">CLI 工具还支持管道操作和脚本集成，可以轻松融入 CI/CD 流水线、自动化脚本、DevOps 工作流中。无论是开发调试还是日常运维，CLI 都是你的效率利器。</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
What is CLI END -->

<!-- =======================
Features START -->
<section>
	<div class="container">
		<!-- Title -->
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.cli.features.title">核心命令</@t></h2>
				<p class="mb-0"><@t key="page.cli.features.desc">丰富的命令行操作覆盖客服核心场景</@t></p>
			</div>
		</div>

		<!-- Feature list -->
		<div class="row g-4">
			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-book fs-5"></i>
					</div>
					<h5><@t key="page.cli.feature.kbase.title">知识库管理</@t></h5>
					<p class="mb-0"><@t key="page.cli.feature.kbase.desc">命令行搜索、创建、更新知识库文章，支持全文检索和分类浏览</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-ticket-detailed fs-5"></i>
					</div>
					<h5><@t key="page.cli.feature.ticket.title">工单操作</@t></h5>
					<p class="mb-0"><@t key="page.cli.feature.ticket.desc">创建、分配、流转、关闭工单，支持批量操作和格式化输出</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-chat-square-text fs-5"></i>
					</div>
					<h5><@t key="page.cli.feature.session.title">会话查询</@t></h5>
					<p class="mb-0"><@t key="page.cli.feature.session.desc">实时查看在线会话、历史对话记录，支持按关键字和时间范围过滤</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-person-badge fs-5"></i>
					</div>
					<h5><@t key="page.cli.feature.user.title">用户搜索</@t></h5>
					<p class="mb-0"><@t key="page.cli.feature.user.desc">快速查找访客、会员、客服信息，支持多维度的用户画像查询</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-pipe fs-5"></i>
					</div>
					<h5><@t key="page.cli.feature.pipeline.title">管道与脚本</@t></h5>
					<p class="mb-0"><@t key="page.cli.feature.pipeline.desc">支持管道操作和 JSON 输出，轻松结合 jq、grep 等工具进行数据处理</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-gear fs-5"></i>
					</div>
					<h5><@t key="page.cli.feature.config.title">配置管理</@t></h5>
					<p class="mb-0"><@t key="page.cli.feature.config.desc">支持多环境配置切换、Token 管理、自动补全和别名设置</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->


<!-- =======================
Use Cases START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.cli.usecases.title">适用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100">
					<div class="card-body">
						<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mx-auto mb-3">
							<i class="bi bi-wrench fs-4"></i>
						</div>
						<h5 class="card-title"><@t key="page.cli.usecase.devops.title">DevOps 集成</@t></h5>
						<p class="card-text small"><@t key="page.cli.usecase.devops.desc">将 CLI 嵌入 CI/CD 流水线，自动化处理和响应运维事件</@t></p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100">
					<div class="card-body">
						<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mx-auto mb-3">
							<i class="bi bi-terminal-split fs-4"></i>
						</div>
						<h5 class="card-title"><@t key="page.cli.usecase.power.title">高效运维</@t></h5>
						<p class="card-text small"><@t key="page.cli.usecase.power.desc">批量处理工单、快速检索知识，告别页面点击的繁琐操作</@t></p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100">
					<div class="card-body">
						<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mx-auto mb-3">
							<i class="bi bi-code-slash fs-4"></i>
						</div>
						<h5 class="card-title"><@t key="page.cli.usecase.script.title">脚本自动化</@t></h5>
						<p class="card-text small"><@t key="page.cli.usecase.script.desc">编写 Shell 脚本实现定时报告、自动响应、数据导出等</@t></p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100">
					<div class="card-body">
						<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mx-auto mb-3">
							<i class="bi bi-lightning-charge fs-4"></i>
						</div>
						<h5 class="card-title"><@t key="page.cli.usecase.quick.title">快速查詢</@t></h5>
						<p class="card-text small"><@t key="page.cli.usecase.quick.desc">无需打开浏览器，终端内一键查询所需信息</@t></p>
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
