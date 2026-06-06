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
				<h1><@t key="page.ai.title">AI Agent - 智能代理平台</@t></h1>
				<p class="lead"><@t key="page.ai.subtitle">对接主流大模型，打造企业智能助手</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="/assets/images/element/online.svg" class="h-200px" alt="<@t key='alt.ai.icon'>AI Agent图标</@t>">
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
				<h2><@t key="page.ai.features.title">核心功能</@t></h2>
				<p class="mb-0"><@t key="page.ai.features.desc">AI技术赋能企业业务流程</@t></p>
			</div>
		</div>

		<!-- Feature list -->
		<div class="row g-4">
			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-cpu fs-5"></i>
					</div>
					<h5><@t key="page.ai.feature.llm.title">多模型支持</@t></h5>
					<p class="mb-0"><@t key="page.ai.feature.llm.desc">对接Ollama、DeepSeek、智谱、通义千问等主流大模型，灵活选择最适合的AI能力</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-server fs-5"></i>
					</div>
					<h5><@t key="page.ai.feature.deploy.title">私有部署</@t></h5>
					<p class="mb-0"><@t key="page.ai.feature.deploy.desc">支持本地部署Ollama等开源大模型，数据安全完全可控，无需担心隐私泄露</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-chat-quote fs-5"></i>
					</div>
					<h5><@t key="page.ai.feature.assistant.title">智能对话</@t></h5>
					<p class="mb-0"><@t key="page.ai.feature.assistant.desc">自然语言理解，上下文记忆，多轮对话，提供类人化的交互体验</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-book fs-5"></i>
					</div>
					<h5><@t key="page.ai.feature.rag.title">知识库问答</@t></h5>
					<p class="mb-0"><@t key="page.ai.feature.rag.desc">基于RAG技术，结合企业知识库提供精准答案，减少幻觉提升可靠性</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-diagram-3 fs-5"></i>
					</div>
					<h5><@t key="page.ai.feature.agent.title">智能体编排</@t></h5>
					<p class="mb-0"><@t key="page.ai.feature.agent.desc">Function Calling、MCP协议支持，构建复杂的AI智能体工作流</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-code-slash fs-5"></i>
					</div>
					<h5><@t key="page.ai.feature.api.title">API接口</@t></h5>
					<p class="mb-0"><@t key="page.ai.feature.api.desc">标准化API接口，方便集成到现有业务系统，快速实现AI能力</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
AI Agents START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.ai.agents.title">微语 AI Agent 矩阵</@t></h2>
				<p class="mb-0"><@t key="page.ai.agents.desc">围绕客服、售前、售中、售后和运营构建企业数字员工，让服务成为增长引擎</@t></p>
			</div>
		</div>

		<div class="row g-4">
			<!-- Agent item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100 border-0 shadow-sm">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-headset fs-5"></i>
					</div>
					<h5><@t key="page.ai.agent.service.title">客服助手 Agent</@t></h5>
					<p class="mb-0"><@t key="page.ai.agent.service.desc">辅助人工客服检索知识、生成回复建议、总结会话、识别情绪与SLA风险，并推荐下一步服务动作。</@t></p>
				</div>
			</div>

			<!-- Agent item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100 border-0 shadow-sm">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-person-plus fs-5"></i>
					</div>
					<h5><@t key="page.ai.agent.presales.title">售前 Agent</@t></h5>
					<p class="mb-0"><@t key="page.ai.agent.presales.desc">识别访客需求、推荐商品或方案、收集线索、判断购买意向，并将高价值客户平滑转交人工跟进。</@t></p>
				</div>
			</div>

			<!-- Agent item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100 border-0 shadow-sm">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-cart-check fs-5"></i>
					</div>
					<h5><@t key="page.ai.agent.sales.title">售中 Agent</@t></h5>
					<p class="mb-0"><@t key="page.ai.agent.sales.desc">围绕报价说明、下单引导、支付提醒、订单查询和履约协同，减少从咨询到成交之间的流失。</@t></p>
				</div>
			</div>

			<!-- Agent item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100 border-0 shadow-sm">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-tools fs-5"></i>
					</div>
					<h5><@t key="page.ai.agent.aftersales.title">售后 Agent</@t></h5>
					<p class="mb-0"><@t key="page.ai.agent.aftersales.desc">处理订单、物流、退款、退换货、质保、投诉和工单跟进，提高售后响应效率与客户满意度。</@t></p>
				</div>
			</div>

			<!-- Agent item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100 border-0 shadow-sm">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-graph-up-arrow fs-5"></i>
					</div>
					<h5><@t key="page.ai.agent.operations.title">运营 Agent</@t></h5>
					<p class="mb-0"><@t key="page.ai.agent.operations.desc">汇聚会话、标签、订单和工单数据，自动生成客户分群、触达策略和复购运营建议。</@t></p>
				</div>
			</div>

			<!-- Agent item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100 border-0 shadow-sm">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-arrow-repeat fs-5"></i>
					</div>
					<h5><@t key="page.ai.agent.loop.title">服务增长闭环</@t></h5>
					<p class="mb-0"><@t key="page.ai.agent.loop.desc">会话沉淀客户资产，客户资产驱动个性化服务，服务结果反哺知识、流程和运营策略。</@t></p>
				</div>
			</div>
		</div>

		<div class="row g-4 mt-4 align-items-center">
			<div class="col-lg-6">
				<h4><@t key="page.ai.agent.plan.title">分阶段建设规划</@t></h4>
				<p><@t key="page.ai.agent.plan.desc">微语将以可配置提示词、知识库检索、工具调用、工作流编排和数据分析为基础，逐步开放可落地的企业智能体能力。</@t></p>
				<ul class="list-group list-group-borderless">
					<li class="list-group-item d-flex px-0">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.ai.agent.plan.context">统一会话、访客、会员、订单、工单和知识库上下文</@t>
					</li>
					<li class="list-group-item d-flex px-0">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.ai.agent.plan.tools">通过权限可控的工具调用连接真实业务系统</@t>
					</li>
					<li class="list-group-item d-flex px-0">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.ai.agent.plan.workflow">用工作流承接转人工、建工单、回访、触达和复盘动作</@t>
					</li>
					<li class="list-group-item d-flex px-0">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.ai.agent.plan.analytics">持续统计响应效率、转化率、满意度、复购率和服务质量</@t>
					</li>
				</ul>
			</div>
			<div class="col-lg-6">
				<div class="p-4 bg-white rounded-3 shadow-sm">
					<h5><@t key="page.ai.agent.value.title">从客服工具到数字员工</@t></h5>
					<p class="mb-0"><@t key="page.ai.agent.value.desc">AI Agent 不只是自动回复机器人，而是能够理解客户、调用知识、执行流程、沉淀经验并辅助增长的业务执行者。企业可以先从客服助手和知识库问答开始，再逐步扩展到售前转化、售中协同、售后工单和全域运营。</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
AI Agents END -->

<!-- =======================
Models START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.ai.models.title">支持的大模型</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Model card -->
			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100">
					<div class="card-body">
						<h5 class="card-title">Ollama</h5>
						<p class="card-text small"><@t key="page.ai.model.ollama">本地部署，开源免费，支持Llama、Qwen等多种模型</@t></p>
					</div>
				</div>
			</div>

			<!-- Model card -->
			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100">
					<div class="card-body">
						<h5 class="card-title">DeepSeek</h5>
						<p class="card-text small"><@t key="page.ai.model.deepseek">国产优秀大模型，性价比高，推理能力强</@t></p>
					</div>
				</div>
			</div>

			<!-- Model card -->
			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.ai.model.zhipu.name">智谱AI</@t></h5>
						<p class="card-text small"><@t key="page.ai.model.zhipu">清华技术背景，中文理解能力出色</@t></p>
					</div>
				</div>
			</div>

			<!-- Model card -->
			<div class="col-md-6 col-lg-3">
				<div class="card text-center h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.ai.model.qwen.name">通义千问</@t></h5>
						<p class="card-text small"><@t key="page.ai.model.qwen">阿里云出品，多模态能力强大</@t></p>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Models END -->

<!-- =======================
Use Cases START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.ai.usecases.title">应用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Use case 1 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.ai.usecase.customer.title">智能客服</@t></h5>
						<p class="card-text"><@t key="page.ai.usecase.customer.desc">7x24小时自动回复客户咨询，结合知识库提供准确答案，人工坐席兜底</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 2 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.ai.usecase.writing.title">内容创作</@t></h5>
						<p class="card-text"><@t key="page.ai.usecase.writing.desc">AI写作助手，自动生成文档、报告、公告等内容，提升创作效率</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 3 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.ai.usecase.analysis.title">数据分析</@t></h5>
						<p class="card-text"><@t key="page.ai.usecase.analysis.desc">自然语言查询数据，自动生成报表和可视化图表，洞察业务趋势</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 4 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.ai.usecase.automation.title">流程自动化</@t></h5>
						<p class="card-text"><@t key="page.ai.usecase.automation.desc">智能体自动执行重复性任务，工作流编排提升业务效率</@t></p>
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
