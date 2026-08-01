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
				<h1><@t key="page.skill.title">微语 Skills - 可复用技能包</@t></h1>
				<p class="lead"><@t key="page.skill.subtitle">将客服经验沉淀为标准化技能，让 AI Agent 像人类专家一样处理客户问题</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="/assets/images/element/medal.svg" class="h-200px" alt="<@t key='alt.skill.icon'>Skills 技能图标</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Page Banner END -->

<!-- =======================
What is Skill START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.skill.what.title">什么是微语 Skills？</@t></h2>
			</div>
		</div>

		<div class="row g-4 justify-content-center">
			<div class="col-lg-8">
				<div class="bg-light p-4 rounded-3">
					<p><@t key="page.skill.what.desc">微语 Skills 是可复用的技能模块，将客服领域的最佳实践、业务知识和操作流程封装为标准化技能包。每个 Skill 包含提示词模板、知识库引用、工具调用链和业务规则，可以被 AI Agent 按需加载和执行。</@t></p>
					<p class="mb-0"><@t key="page.skill.what.desc2">Skills 是微语"经验沉淀"的核心载体——优秀的客服处理流程、问题解决方案、客户沟通技巧都可以被固化下来，持续复用和优化。</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
What is Skill END -->

<!-- =======================
Features START -->
<section>
	<div class="container">
		<!-- Title -->
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.skill.features.title">技能体系</@t></h2>
				<p class="mb-0"><@t key="page.skill.features.desc">覆盖客服全场景的标准化技能包</@t></p>
			</div>
		</div>

		<!-- Feature list -->
		<div class="row g-4">
			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-headset fs-5"></i>
					</div>
					<h5><@t key="page.skill.feature.reception.title">智能接待</@t></h5>
					<p class="mb-0"><@t key="page.skill.feature.reception.desc">自动问候、意图识别、问题分类、路由分配，一站式客户接待流程</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-search fs-5"></i>
					</div>
					<h5><@t key="page.skill.feature.qa.title">智能问答</@t></h5>
					<p class="mb-0"><@t key="page.skill.feature.qa.desc">知识库检索 + RAG 增强生成，精准回答产品、售后、政策等常见问题</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-ticket-perforated fs-5"></i>
					</div>
					<h5><@t key="page.skill.feature.ticket.title">工单处理</@t></h5>
					<p class="mb-0"><@t key="page.skill.feature.ticket.desc">自动建单、智能分配、SLA 监控、满意度回访，工单全生命周期管理</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-cart-check fs-5"></i>
					</div>
					<h5><@t key="page.skill.feature.order.title">订单服务</@t></h5>
					<p class="mb-0"><@t key="page.skill.feature.order.desc">查询订单状态、处理退换货、物流跟踪，打通电商服务全链路</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-file-earmark-text fs-5"></i>
					</div>
					<h5><@t key="page.skill.feature.summary.title">会话小结</@t></h5>
					<p class="mb-0"><@t key="page.skill.feature.summary.desc">自动生成会话摘要、提取关键信息、情绪分析，沉淀服务经验</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-puzzle fs-5"></i>
					</div>
					<h5><@t key="page.skill.feature.custom.title">自定义技能</@t></h5>
					<p class="mb-0"><@t key="page.skill.feature.custom.desc">支持企业自定义技能，灵活编排提示词、知识库和工具调用链</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Skill Architecture START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.skill.arch.title">技能架构</@t></h2>
				<p class="mb-0"><@t key="page.skill.arch.desc">Skills 由多层能力组合而成，灵活编排</@t></p>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100 border-0 shadow-sm">
					<div class="card-body">
						<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mx-auto mb-3">
							<i class="bi bi-chat-left-quote fs-4"></i>
						</div>
						<h5 class="card-title"><@t key="page.skill.arch.prompt.title">提示词层</@t></h5>
						<p class="card-text small"><@t key="page.skill.arch.prompt.desc">结构化 System Prompt，定义角色、规则、输出格式</@t></p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100 border-0 shadow-sm">
					<div class="card-body">
						<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mx-auto mb-3">
							<i class="bi bi-database fs-4"></i>
						</div>
						<h5 class="card-title"><@t key="page.skill.arch.knowledge.title">知识层</@t></h5>
						<p class="card-text small"><@t key="page.skill.arch.knowledge.desc">关联知识库、FAQ、文档语料，提供领域知识支撑</@t></p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100 border-0 shadow-sm">
					<div class="card-body">
						<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mx-auto mb-3">
							<i class="bi bi-tools fs-4"></i>
						</div>
						<h5 class="card-title"><@t key="page.skill.arch.tool.title">工具层</@t></h5>
						<p class="card-text small"><@t key="page.skill.arch.tool.desc">绑定 MCP Tools / Function Calling，执行实际操作</@t></p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100 border-0 shadow-sm">
					<div class="card-body">
						<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mx-auto mb-3">
							<i class="bi bi-diagram-3 fs-4"></i>
						</div>
						<h5 class="card-title"><@t key="page.skill.arch.workflow.title">流程层</@t></h5>
						<p class="card-text small"><@t key="page.skill.arch.workflow.desc">编排多个 Skill 形成工作流，处理复杂业务场景</@t></p>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Skill Architecture END -->

<!-- =======================
Self-Evolution START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.skill.evo.title">技能自进化</@t></h2>
			</div>
		</div>

		<div class="row g-4 justify-content-center">
			<div class="col-lg-8">
				<div class="row g-4">
					<div class="col-md-6">
						<div class="card border h-100">
							<div class="card-body">
								<h5 class="card-title"><i class="bi bi-star-fill text-warning me-2"></i><@t key="page.skill.evo.learn.title">自动学习</@t></h5>
								<p class="card-text mb-0"><@t key="page.skill.evo.learn.desc">从高评分会话中自动提取优秀回复，沉淀为新技能或补充到知识库</@t></p>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="card border h-100">
							<div class="card-body">
								<h5 class="card-title"><i class="bi bi-graph-up-arrow text-success me-2"></i><@t key="page.skill.evo.optimize.title">持续优化</@t></h5>
								<p class="card-text mb-0"><@t key="page.skill.evo.optimize.desc">根据客户满意度、解决率等指标，自动调整技能策略和优先级</@t></p>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="card border h-100">
							<div class="card-body">
								<h5 class="card-title"><i class="bi bi-exclamation-triangle-fill text-danger me-2"></i><@t key="page.skill.evo.alert.title">异常标记</@t></h5>
								<p class="card-text mb-0"><@t key="page.skill.evo.alert.desc">低分会话自动标记，提醒人工复核并优化相应技能</@t></p>
							</div>
						</div>
					</div>
					<div class="col-md-6">
						<div class="card border h-100">
							<div class="card-body">
								<h5 class="card-title"><i class="bi bi-people-fill text-primary me-2"></i><@t key="page.skill.evo.collab.title">人机协作</@t></h5>
								<p class="card-text mb-0"><@t key="page.skill.evo.collab.desc">人工审核 + AI 建议，确保技能质量的同时保持快速迭代</@t></p>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Self-Evolution END -->

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
