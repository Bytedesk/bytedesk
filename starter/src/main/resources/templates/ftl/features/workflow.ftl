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
				<h1><@t key="page.workflow.title">智能工作流平台</@t></h1>
				<p class="lead"><@t key="page.workflow.subtitle">AI驱动的流程自动化与编排引擎</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="/assets/images/element/artist.svg" class="h-200px" alt="<@t key='alt.workflow.icon'>工作流图标</@t>">
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
				<h2><@t key="page.workflow.features.title">核心功能</@t></h2>
				<p class="mb-0"><@t key="page.workflow.features.desc">强大的流程自动化能力</@t></p>
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
					<h5><@t key="page.workflow.feature.agent.title">AI智能体</@t></h5>
					<p class="mb-0"><@t key="page.workflow.feature.agent.desc">构建AI智能体，自动执行复杂业务逻辑，提升工作效率</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-diagram-3 fs-5"></i>
					</div>
					<h5><@t key="page.workflow.feature.visual.title">可视化编排</@t></h5>
					<p class="mb-0"><@t key="page.workflow.feature.visual.desc">拖拽式流程设计器，无需编码即可创建复杂工作流</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-code-slash fs-5"></i>
					</div>
					<h5><@t key="page.workflow.feature.function.title">Function Calling</@t></h5>
					<p class="mb-0"><@t key="page.workflow.feature.function.desc">支持大模型Function Calling，让AI调用真实业务接口</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-plug fs-5"></i>
					</div>
					<h5><@t key="page.workflow.feature.mcp.title">MCP协议</@t></h5>
					<p class="mb-0"><@t key="page.workflow.feature.mcp.desc">支持Model Context Protocol，实现AI与外部系统无缝集成</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-lightning fs-5"></i>
					</div>
					<h5><@t key="page.workflow.feature.trigger.title">多种触发器</@t></h5>
					<p class="mb-0"><@t key="page.workflow.feature.trigger.desc">时间触发、事件触发、API触发、人工触发等多种启动方式</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-database fs-5"></i>
					</div>
					<h5><@t key="page.workflow.feature.data.title">数据处理</@t></h5>
					<p class="mb-0"><@t key="page.workflow.feature.data.desc">数据转换、条件判断、循环处理，灵活的数据操作能力</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Capabilities START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.workflow.capabilities.title">工作流能力</@t></h2>
			</div>
		</div>

		<div class="row g-4 align-items-center">
			<div class="col-lg-6">
				<h4><@t key="page.workflow.capabilities.subtitle">丰富的节点类型</@t></h4>
				<ul class="list-group list-group-borderless">
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.workflow.capability.start">开始/结束节点：定义流程起止</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.workflow.capability.ai">AI节点：调用大模型进行智能处理</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.workflow.capability.api">API节点：调用内外部接口</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.workflow.capability.condition">条件节点：根据条件分支流转</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.workflow.capability.loop">循环节点：批量处理数据</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.workflow.capability.approval">审批节点：人工审批流程</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.workflow.capability.notify">通知节点：消息推送提醒</@t>
					</li>
				</ul>
			</div>

			<div class="col-lg-6 text-center">
				<img src="/assets/images/element/07.svg" class="img-fluid" alt="<@t key='alt.workflow.capability'>工作流能力插图</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Capabilities END -->

<!-- =======================
Use Cases START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.workflow.usecases.title">应用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Use case 1 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.workflow.usecase.approval.title">审批流程</@t></h5>
						<p class="card-text"><@t key="page.workflow.usecase.approval.desc">请假、报销、采购审批等业务流程自动化，多级审批、并行审批</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 2 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.workflow.usecase.data.title">数据同步</@t></h5>
						<p class="card-text"><@t key="page.workflow.usecase.data.desc">系统间数据自动同步、转换、清洗，保证数据一致性</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 3 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.workflow.usecase.alert.title">告警处理</@t></h5>
						<p class="card-text"><@t key="page.workflow.usecase.alert.desc">系统异常自动检测、智能分析、自动恢复或通知相关人员</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 4 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.workflow.usecase.marketing.title">营销自动化</@t></h5>
						<p class="card-text"><@t key="page.workflow.usecase.marketing.desc">客户行为触发营销活动、自动跟进、个性化推荐</@t></p>
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
