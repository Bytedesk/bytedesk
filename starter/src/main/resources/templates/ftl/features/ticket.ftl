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
<section class="bg-light pt-5 pb-5">
	<div class="container">
		<div class="row g-4 g-md-5 position-relative">
			<!-- Main content START -->
			<div class="col-lg-8">
				<!-- Title -->
				<h1><@t key="page.ticket.title">智能工单系统</@t></h1>
				<p class="lead"><@t key="page.ticket.subtitle">高效的工单管理与协作平台</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="assets/images/element/medical.svg" class="h-200px" alt="<@t key='alt.ticket.icon'>工单系统图标</@t>">
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
				<h2><@t key="page.ticket.features.title">核心功能</@t></h2>
				<p class="mb-0"><@t key="page.ticket.features.desc">AI赋能的智能工单管理</@t></p>
			</div>
		</div>

		<!-- Feature list -->
		<div class="row g-4">
			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-robot fs-5"></i>
					</div>
					<h5><@t key="page.ticket.feature.aiauto.title">AI自动化</@t></h5>
					<p class="mb-0"><@t key="page.ticket.feature.aiauto.desc">AI工单助手自动创建工单、智能分配、自动回复、自动关闭</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-diagram-3 fs-5"></i>
					</div>
					<h5><@t key="page.ticket.feature.workflow.title">工作流引擎</@t></h5>
					<p class="mb-0"><@t key="page.ticket.feature.workflow.desc">自定义工单流转流程，SLA管理，自动升级与提醒</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-tags fs-5"></i>
					</div>
					<h5><@t key="page.ticket.feature.classify.title">智能分类</@t></h5>
					<p class="mb-0"><@t key="page.ticket.feature.classify.desc">AI自动识别工单类型、优先级、紧急程度，精准路由</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-people fs-5"></i>
					</div>
					<h5><@t key="page.ticket.feature.collab.title">协作处理</@t></h5>
					<p class="mb-0"><@t key="page.ticket.feature.collab.desc">多人协作、转派、抄送、内部备注，高效解决复杂问题</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-clock-history fs-5"></i>
					</div>
					<h5><@t key="page.ticket.feature.track.title">全程跟踪</@t></h5>
					<p class="mb-0"><@t key="page.ticket.feature.track.desc">记录工单完整生命周期，状态变更历史，操作日志</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-graph-up fs-5"></i>
					</div>
					<h5><@t key="page.ticket.feature.analytics.title">数据分析</@t></h5>
					<p class="mb-0"><@t key="page.ticket.feature.analytics.desc">工单统计报表、响应时效分析、人员绩效评估</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Workflow START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.ticket.workflow.title">工单处理流程</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-3">
				<div class="text-center">
					<div class="icon-xl bg-primary text-white rounded-circle mx-auto mb-3">1</div>
					<h5><@t key="page.ticket.workflow.step1.title">创建工单</@t></h5>
					<p class="small"><@t key="page.ticket.workflow.step1.desc">客户/员工提交问题</@t></p>
				</div>
			</div>

			<div class="col-md-3">
				<div class="text-center">
					<div class="icon-xl bg-success text-white rounded-circle mx-auto mb-3">2</div>
					<h5><@t key="page.ticket.workflow.step2.title">智能分派</@t></h5>
					<p class="small"><@t key="page.ticket.workflow.step2.desc">AI自动分配处理人</@t></p>
				</div>
			</div>

			<div class="col-md-3">
				<div class="text-center">
					<div class="icon-xl bg-warning text-white rounded-circle mx-auto mb-3">3</div>
					<h5><@t key="page.ticket.workflow.step3.title">协作处理</@t></h5>
					<p class="small"><@t key="page.ticket.workflow.step3.desc">团队协同解决问题</@t></p>
				</div>
			</div>

			<div class="col-md-3">
				<div class="text-center">
					<div class="icon-xl bg-info text-white rounded-circle mx-auto mb-3">4</div>
					<h5><@t key="page.ticket.workflow.step4.title">关闭工单</@t></h5>
					<p class="small"><@t key="page.ticket.workflow.step4.desc">确认解决并归档</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Workflow END -->

<!-- =======================
Use Cases START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.ticket.usecases.title">应用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Use case 1 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.ticket.usecase.support.title">客户支持</@t></h5>
						<p class="card-text"><@t key="page.ticket.usecase.support.desc">售后服务请求、技术支持、故障报修，提供专业及时的客户服务</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 2 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.ticket.usecase.it.title">IT运维</@t></h5>
						<p class="card-text"><@t key="page.ticket.usecase.it.desc">IT设备报修、系统故障、权限申请、资产管理等IT服务场景</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 3 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.ticket.usecase.hr.title">HR服务</@t></h5>
						<p class="card-text"><@t key="page.ticket.usecase.hr.desc">员工请假、报销、入职离职、证明开具等人事服务流程</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 4 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.ticket.usecase.admin.title">行政服务</@t></h5>
						<p class="card-text"><@t key="page.ticket.usecase.admin.desc">物业报修、会议室预订、办公用品申领等行政支持</@t></p>
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
