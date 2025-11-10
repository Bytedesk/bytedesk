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
				<h1><@t key="page.team.title">企业IM - 智能即时通讯平台</@t></h1>
				<p class="lead"><@t key="page.team.subtitle">面向中大型团队打造的企业级即时通讯解决方案</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="assets/images/element/coding.svg" class="h-200px" alt="<@t key='alt.team.icon'>企业IM图标</@t>">
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
				<h2><@t key="page.team.features.title">核心功能</@t></h2>
				<p class="mb-0"><@t key="page.team.features.desc">为企业团队协作提供全方位支持</@t></p>
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
					<h5><@t key="page.team.feature.chat.title">即时通讯</@t></h5>
					<p class="mb-0"><@t key="page.team.feature.chat.desc">支持单聊、群聊、文件传输、语音视频通话等完整IM功能，支持数万人同时在线</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-robot fs-5"></i>
					</div>
					<h5><@t key="page.team.feature.ai.title">AI智能助手</@t></h5>
					<p class="mb-0"><@t key="page.team.feature.ai.desc">AI群聊助手自动回答常见问题，AI会话总结提炼关键信息，提升沟通效率</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-people fs-5"></i>
					</div>
					<h5><@t key="page.team.feature.org.title">组织架构</@t></h5>
					<p class="mb-0"><@t key="page.team.feature.org.desc">完善的组织架构管理，支持部门、角色、权限配置，灵活适配企业组织结构</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-shield-check fs-5"></i>
					</div>
					<h5><@t key="page.team.feature.security.title">安全可靠</@t></h5>
					<p class="mb-0"><@t key="page.team.feature.security.desc">端到端加密，聊天记录监控，敏感信息过滤，满足企业安全合规要求</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-clock-history fs-5"></i>
					</div>
					<h5><@t key="page.team.feature.history.title">消息存档</@t></h5>
					<p class="mb-0"><@t key="page.team.feature.history.desc">完整的聊天记录存储与检索，支持历史消息搜索，满足审计需求</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-phone fs-5"></i>
					</div>
					<h5><@t key="page.team.feature.multiplatform.title">多平台支持</@t></h5>
					<p class="mb-0"><@t key="page.team.feature.multiplatform.desc">支持Web、Windows、Mac、Linux、iOS、Android等全平台，随时随地沟通</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Advantages START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.team.advantages.title">产品优势</@t></h2>
			</div>
		</div>

		<div class="row g-4 align-items-center">
			<!-- Left content -->
			<div class="col-lg-6">
				<ul class="list-group list-group-borderless">
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.team.advantage.scalable">高并发架构设计，支持数万人同时在线，轻松应对大规模团队协作</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.team.advantage.opensource">完全开源，支持私有部署，数据100%掌控在您手中</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.team.advantage.customizable">灵活的二次开发能力，可根据企业需求深度定制</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.team.advantage.integrated">与其他企业系统无缝集成，打造统一工作平台</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.team.advantage.ai">AI技术赋能，智能助手提升沟通效率和质量</@t>
					</li>
				</ul>
			</div>

			<!-- Right image -->
			<div class="col-lg-6 text-center">
				<img src="assets/images/element/07.svg" class="img-fluid" alt="<@t key='alt.team.advantage'>企业IM优势插图</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Advantages END -->

<!-- =======================
Use Cases START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.team.usecases.title">应用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Use case 1 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.team.usecase.internal.title">企业内部协作</@t></h5>
						<p class="card-text"><@t key="page.team.usecase.internal.desc">部门间沟通、项目协作、工作汇报、通知公告等日常办公场景</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 2 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.team.usecase.remote.title">远程办公</@t></h5>
						<p class="card-text"><@t key="page.team.usecase.remote.desc">分布式团队实时沟通，跨地域协作，在线会议，远程培训</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 3 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.team.usecase.support.title">内部支持</@t></h5>
						<p class="card-text"><@t key="page.team.usecase.support.desc">IT支持、HR咨询、行政服务等内部支持场景，提升员工体验</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 4 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.team.usecase.emergency.title">应急指挥</@t></h5>
						<p class="card-text"><@t key="page.team.usecase.emergency.desc">突发事件快速响应，紧急通知即时触达，协同处置提升效率</@t></p>
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
