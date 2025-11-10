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
				<h1><@t key="page.kbase.title">企业知识库 / 帮助中心</@t></h1>
				<p class="lead"><@t key="page.kbase.subtitle">AI赋能的企业知识管理与服务平台</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="assets/images/element/engineering.svg" class="h-200px" alt="<@t key='alt.kbase.icon'>知识库图标</@t>">
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
				<h2><@t key="page.kbase.features.title">核心功能</@t></h2>
				<p class="mb-0"><@t key="page.kbase.features.desc">全方位的知识管理解决方案</@t></p>
			</div>
		</div>

		<!-- Feature list -->
		<div class="row g-4">
			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-folder fs-5"></i>
					</div>
					<h5><@t key="page.kbase.feature.manage.title">知识管理</@t></h5>
					<p class="mb-0"><@t key="page.kbase.feature.manage.desc">文档分类管理、版本控制、权限设置，构建结构化的企业知识体系</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-robot fs-5"></i>
					</div>
					<h5><@t key="page.kbase.feature.aiwriter.title">AI写作助手</@t></h5>
					<p class="mb-0"><@t key="page.kbase.feature.aiwriter.desc">自动撰写文档内容、智能润色、多语言翻译，大幅提升内容创作效率</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-chat-left-dots fs-5"></i>
					</div>
					<h5><@t key="page.kbase.feature.aiqa.title">AI知识问答</@t></h5>
					<p class="mb-0"><@t key="page.kbase.feature.aiqa.desc">基于知识库内容的智能问答，快速定位答案，提升知识检索效率</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-search fs-5"></i>
					</div>
					<h5><@t key="page.kbase.feature.search.title">全文搜索</@t></h5>
					<p class="mb-0"><@t key="page.kbase.feature.search.desc">强大的全文检索能力，支持模糊搜索、高亮显示、搜索建议</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-globe fs-5"></i>
					</div>
					<h5><@t key="page.kbase.feature.public.title">对外发布</@t></h5>
					<p class="mb-0"><@t key="page.kbase.feature.public.desc">支持将知识库发布为公开帮助中心，为客户提供自助服务</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-people fs-5"></i>
					</div>
					<h5><@t key="page.kbase.feature.collab.title">协作编辑</@t></h5>
					<p class="mb-0"><@t key="page.kbase.feature.collab.desc">多人协作编辑、评论反馈、变更追踪，促进知识共建共享</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Scenarios START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.kbase.scenarios.title">应用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-lg-6">
				<div class="card h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.kbase.scenario.internal.title">内部知识库</@t></h5>
						<ul>
							<li><@t key="page.kbase.scenario.internal.item1">技术文档、开发规范、API文档</@t></li>
							<li><@t key="page.kbase.scenario.internal.item2">培训资料、操作手册、FAQ</@t></li>
							<li><@t key="page.kbase.scenario.internal.item3">制度流程、规章制度、通知公告</@t></li>
							<li><@t key="page.kbase.scenario.internal.item4">项目文档、会议纪要、知识沉淀</@t></li>
						</ul>
					</div>
				</div>
			</div>

			<div class="col-lg-6">
				<div class="card h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.kbase.scenario.external.title">对外帮助中心</@t></h5>
						<ul>
							<li><@t key="page.kbase.scenario.external.item1">产品使用说明、功能介绍</@t></li>
							<li><@t key="page.kbase.scenario.external.item2">常见问题解答、故障排查</@t></li>
							<li><@t key="page.kbase.scenario.external.item3">视频教程、图文教程</@t></li>
							<li><@t key="page.kbase.scenario.external.item4">更新日志、版本说明</@t></li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Scenarios END -->

<!-- =======================
Benefits START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.kbase.benefits.title">核心价值</@t></h2>
			</div>
		</div>

		<div class="row g-4 align-items-center">
			<div class="col-lg-6">
				<ul class="list-group list-group-borderless">
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.kbase.benefit.efficiency">减少重复性咨询，提升客服效率和客户满意度</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.kbase.benefit.knowledge">沉淀企业知识资产，避免知识流失</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.kbase.benefit.onboarding">加速新员工培训，降低学习成本</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.kbase.benefit.selfservice">提供自助服务，降低人工支持成本</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.kbase.benefit.ai">AI技术赋能，提升内容质量和检索效率</@t>
					</li>
				</ul>
			</div>

			<div class="col-lg-6 text-center">
				<img src="assets/images/element/07.svg" class="img-fluid" alt="<@t key='alt.kbase.benefit'>知识库价值插图</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Benefits END -->

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
