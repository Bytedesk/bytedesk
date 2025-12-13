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
				<h1><@t key="page.service.title">在线客服 - 智能客户服务平台</@t></h1>
				<p class="lead"><@t key="page.service.subtitle">多渠道对接、AI智能辅助，提供7x24小时优质客户服务</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="/assets/images/element/data-science.svg" class="h-200px" alt="<@t key='alt.service.icon'>在线客服图标</@t>">
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
				<h2><@t key="page.service.features.title">核心功能</@t></h2>
				<p class="mb-0"><@t key="page.service.features.desc">全方位智能客服解决方案</@t></p>
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
					<h5><@t key="page.service.feature.multichannel.title">多渠道接入</@t></h5>
					<p class="mb-0"><@t key="page.service.feature.multichannel.desc">支持网页、微信、小程序、APP等多种渠道，统一管理客户咨询</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-robot fs-5"></i>
					</div>
					<h5><@t key="page.service.feature.ai.title">AI智能助手</@t></h5>
					<p class="mb-0"><@t key="page.service.feature.ai.desc">AI自动回复、意图识别、情绪分析，提升服务效率和质量</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-people fs-5"></i>
					</div>
					<h5><@t key="page.service.feature.agent.title">智能分配</@t></h5>
					<p class="mb-0"><@t key="page.service.feature.agent.desc">智能路由分配客服，支持技能组、VIP优先级等多种分配策略</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-graph-up fs-5"></i>
					</div>
					<h5><@t key="page.service.feature.analytics.title">数据分析</@t></h5>
					<p class="mb-0"><@t key="page.service.feature.analytics.desc">实时监控服务数据，客户满意度分析，客服绩效评估</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-shield-check fs-5"></i>
					</div>
					<h5><@t key="page.service.feature.quality.title">智能质检</@t></h5>
					<p class="mb-0"><@t key="page.service.feature.quality.desc">AI自动质检会话记录，识别服务风险，提升服务规范性</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-database fs-5"></i>
					</div>
					<h5><@t key="page.service.feature.crm.title">客户管理</@t></h5>
					<p class="mb-0"><@t key="page.service.feature.crm.desc">完整客户画像，会话历史记录，私域流量沉淀</@t></p>
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
				<h2><@t key="page.service.advantages.title">产品优势</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Advantage 1 -->
			<div class="col-md-6 col-lg-3">
				<div class="text-center">
					<div class="icon-xl bg-primary bg-opacity-10 text-primary rounded-circle mx-auto mb-3">
						<i class="bi bi-lightning-charge fs-3"></i>
					</div>
					<h5><@t key="page.service.advantage.fast.title">快速响应</@t></h5>
					<p><@t key="page.service.advantage.fast.desc">AI秒级回复，人工坐席快速接入</@t></p>
				</div>
			</div>

			<!-- Advantage 2 -->
			<div class="col-md-6 col-lg-3">
				<div class="text-center">
					<div class="icon-xl bg-success bg-opacity-10 text-success rounded-circle mx-auto mb-3">
						<i class="bi bi-coin fs-3"></i>
					</div>
					<h5><@t key="page.service.advantage.cost.title">降本增效</@t></h5>
					<p><@t key="page.service.advantage.cost.desc">AI处理80%常见问题，降低人力成本</@t></p>
				</div>
			</div>

			<!-- Advantage 3 -->
			<div class="col-md-6 col-lg-3">
				<div class="text-center">
					<div class="icon-xl bg-warning bg-opacity-10 text-warning rounded-circle mx-auto mb-3">
						<i class="bi bi-clock-history fs-3"></i>
					</div>
					<h5><@t key="page.service.advantage.24x7.title">全天候服务</@t></h5>
					<p><@t key="page.service.advantage.24x7.desc">7x24小时在线，不错过任何咨询</@t></p>
				</div>
			</div>

			<!-- Advantage 4 -->
			<div class="col-md-6 col-lg-3">
				<div class="text-center">
					<div class="icon-xl bg-info bg-opacity-10 text-info rounded-circle mx-auto mb-3">
						<i class="bi bi-emoji-smile fs-3"></i>
					</div>
					<h5><@t key="page.service.advantage.satisfaction.title">提升满意度</@t></h5>
					<p><@t key="page.service.advantage.satisfaction.desc">专业服务，快速解决问题</@t></p>
				</div>
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
				<h2><@t key="page.service.usecases.title">应用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Use case 1 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.service.usecase.ecommerce.title">电商行业</@t></h5>
						<p class="card-text"><@t key="page.service.usecase.ecommerce.desc">售前咨询、订单查询、售后服务，全流程客户服务支持</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 2 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.service.usecase.finance.title">金融行业</@t></h5>
						<p class="card-text"><@t key="page.service.usecase.finance.desc">业务咨询、产品介绍、投诉处理，合规安全的服务体验</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 3 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.service.usecase.education.title">教育培训</@t></h5>
						<p class="card-text"><@t key="page.service.usecase.education.desc">课程咨询、报名服务、学员答疑，提升招生转化率</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 4 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.service.usecase.saas.title">SaaS企业</@t></h5>
						<p class="card-text"><@t key="page.service.usecase.saas.desc">产品试用、技术支持、续费服务，提升客户留存率</@t></p>
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
