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
				<h1><@t key="page.ai.title">AI Agent - 智能代理平台</@t></h1>
				<p class="lead"><@t key="page.ai.subtitle">对接主流大模型，打造企业智能助手</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="assets/images/element/online.svg" class="h-200px" alt="<@t key='alt.ai.icon'>AI Agent图标</@t>">
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
Models START -->
<section class="bg-light">
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
