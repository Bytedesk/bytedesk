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
				<h1><@t key="page.voc.title">客户之声 (VoC)</@t></h1>
				<p class="lead"><@t key="page.voc.subtitle">聆听客户声音，驱动产品优化与服务提升</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="assets/images/element/profit.svg" class="h-200px" alt="<@t key='alt.voc.icon'>客户之声图标</@t>">
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
				<h2><@t key="page.voc.features.title">核心功能</@t></h2>
				<p class="mb-0"><@t key="page.voc.features.desc">全渠道收集客户反馈，AI智能分析</@t></p>
			</div>
		</div>

		<!-- Feature list -->
		<div class="row g-4">
			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-chat-square-text fs-5"></i>
					</div>
					<h5><@t key="page.voc.feature.social.title">社交媒体监听</@t></h5>
					<p class="mb-0"><@t key="page.voc.feature.social.desc">自动抓取微博、小红书、抖音等社交平台评论，实时掌握用户声音</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-robot fs-5"></i>
					</div>
					<h5><@t key="page.voc.feature.aireply.title">AI回复助手</@t></h5>
					<p class="mb-0"><@t key="page.voc.feature.aireply.desc">AI智能生成回复内容，帮助客服快速响应用户评论和反馈</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-star fs-5"></i>
					</div>
					<h5><@t key="page.voc.feature.review.title">第三方评论同步</@t></h5>
					<p class="mb-0"><@t key="page.voc.feature.review.desc">同步应用商店、电商平台等第三方评价，统一管理用户反馈</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-envelope fs-5"></i>
					</div>
					<h5><@t key="page.voc.feature.feedback.title">意见反馈</@t></h5>
					<p class="mb-0"><@t key="page.voc.feature.feedback.desc">在线反馈表单，收集用户建议和问题，跟踪处理进度</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-exclamation-triangle fs-5"></i>
					</div>
					<h5><@t key="page.voc.feature.complaint.title">服务投诉</@t></h5>
					<p class="mb-0"><@t key="page.voc.feature.complaint.desc">投诉受理与处理流程，确保问题得到妥善解决，提升客户满意度</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-clipboard-check fs-5"></i>
					</div>
					<h5><@t key="page.voc.feature.survey.title">调查问卷</@t></h5>
					<p class="mb-0"><@t key="page.voc.feature.survey.desc">创建和发布满意度调查、需求调研，数据可视化分析</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Analysis START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.voc.analysis.title">智能分析</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-6 col-lg-3">
				<div class="text-center">
					<div class="icon-xl bg-primary bg-opacity-10 text-primary rounded-circle mx-auto mb-3">
						<i class="bi bi-bar-chart fs-4"></i>
					</div>
					<h5><@t key="page.voc.analysis.sentiment.title">情感分析</@t></h5>
					<p class="small"><@t key="page.voc.analysis.sentiment.desc">自动识别用户情绪倾向</@t></p>
				</div>
			</div>

			<div class="col-md-6 col-lg-3">
				<div class="text-center">
					<div class="icon-xl bg-success bg-opacity-10 text-success rounded-circle mx-auto mb-3">
						<i class="bi bi-tags fs-4"></i>
					</div>
					<h5><@t key="page.voc.analysis.tag.title">智能标签</@t></h5>
					<p class="small"><@t key="page.voc.analysis.tag.desc">自动分类反馈类型</@t></p>
				</div>
			</div>

			<div class="col-md-6 col-lg-3">
				<div class="text-center">
					<div class="icon-xl bg-warning bg-opacity-10 text-warning rounded-circle mx-auto mb-3">
						<i class="bi bi-graph-up fs-4"></i>
					</div>
					<h5><@t key="page.voc.analysis.trend.title">趋势分析</@t></h5>
					<p class="small"><@t key="page.voc.analysis.trend.desc">发现问题变化趋势</@t></p>
				</div>
			</div>

			<div class="col-md-6 col-lg-3">
				<div class="text-center">
					<div class="icon-xl bg-info bg-opacity-10 text-info rounded-circle mx-auto mb-3">
						<i class="bi bi-lightbulb fs-4"></i>
					</div>
					<h5><@t key="page.voc.analysis.insight.title">洞察建议</@t></h5>
					<p class="small"><@t key="page.voc.analysis.insight.desc">AI生成改进建议</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Analysis END -->

<!-- =======================
Use Cases START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.voc.usecases.title">应用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Use case 1 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.voc.usecase.product.title">产品优化</@t></h5>
						<p class="card-text"><@t key="page.voc.usecase.product.desc">收集用户对产品功能的反馈，发现痛点和需求，指导产品迭代方向</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 2 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.voc.usecase.service.title">服务改进</@t></h5>
						<p class="card-text"><@t key="page.voc.usecase.service.desc">监控服务质量投诉，及时发现并解决服务问题，提升客户满意度</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 3 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.voc.usecase.reputation.title">品牌监测</@t></h5>
						<p class="card-text"><@t key="page.voc.usecase.reputation.desc">实时监测社交媒体舆情，快速响应负面评论，保护品牌形象</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 4 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.voc.usecase.market.title">市场调研</@t></h5>
						<p class="card-text"><@t key="page.voc.usecase.market.desc">通过问卷调查了解市场需求，为新产品开发提供决策依据</@t></p>
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
