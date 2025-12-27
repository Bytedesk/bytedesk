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
				<h1><@t key="page.callcenter.title">智能呼叫中心</@t></h1>
				<p class="lead"><@t key="page.callcenter.subtitle">AI赋能的云呼叫中心解决方案</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="/assets/images/element/data-science.svg" class="h-200px" alt="<@t key='alt.callcenter.icon'>呼叫中心图标</@t>">
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
				<h2><@t key="page.callcenter.features.title">核心功能</@t></h2>
				<p class="mb-0"><@t key="page.callcenter.features.desc">专业的电话呼叫中心系统</@t></p>
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
					<h5><@t key="page.callcenter.feature.aioutbound.title">AI智能外呼</@t></h5>
					<p class="mb-0"><@t key="page.callcenter.feature.aioutbound.desc">AI自动外呼，智能对话，意图识别，减少人工成本，提升外呼效率</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-mic fs-5"></i>
					</div>
					<h5><@t key="page.callcenter.feature.asr.title">语音识别 (ASR)</@t></h5>
					<p class="mb-0"><@t key="page.callcenter.feature.asr.desc">实时语音转文字，支持多种方言，为后续分析提供基础</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-volume-up fs-5"></i>
					</div>
					<h5><@t key="page.callcenter.feature.tts.title">语音合成 (TTS)</@t></h5>
					<p class="mb-0"><@t key="page.callcenter.feature.tts.desc">文字转语音播报，多种音色选择，自然流畅的语音表达</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-telephone-forward fs-5"></i>
					</div>
					<h5><@t key="page.callcenter.feature.multiline.title">多线路接入</@t></h5>
					<p class="mb-0"><@t key="page.callcenter.feature.multiline.desc">支持SIP线路、运营商专线等多种接入方式，灵活配置</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-record-circle fs-5"></i>
					</div>
					<h5><@t key="page.callcenter.feature.recording.title">通话录音</@t></h5>
					<p class="mb-0"><@t key="page.callcenter.feature.recording.desc">全程录音存储，支持回放、下载，满足合规与质检需求</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-clipboard-data fs-5"></i>
					</div>
					<h5><@t key="page.callcenter.feature.qa.title">质检分析</@t></h5>
					<p class="mb-0"><@t key="page.callcenter.feature.qa.desc">AI自动质检，情绪识别，关键词检测，服务质量评估</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Technology START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.callcenter.tech.title">技术支持</@t></h2>
			</div>
		</div>

		<div class="row g-4 align-items-center">
			<div class="col-lg-6">
				<h4><@t key="page.callcenter.tech.subtitle">基于FreeSWITCH</@t></h4>
				<ul class="list-group list-group-borderless">
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.callcenter.tech.stable">稳定可靠的开源软交换平台</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.callcenter.tech.scalable">支持大规模并发通话</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.callcenter.tech.flexible">灵活的呼叫路由策略</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.callcenter.tech.integration">完善的API接口，易于集成</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.callcenter.tech.codec">支持多种音频编解码格式</@t>
					</li>
				</ul>
			</div>

			<div class="col-lg-6 text-center">
				<img src="/assets/images/element/07.svg" class="img-fluid" alt="<@t key='alt.callcenter.tech'>呼叫中心技术插图</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Technology END -->

<!-- =======================
Use Cases START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.callcenter.usecases.title">应用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Use case 1 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.callcenter.usecase.sales.title">销售外呼</@t></h5>
						<p class="card-text"><@t key="page.callcenter.usecase.sales.desc">AI外呼筛选意向客户，人工跟进高质量线索，提升销售效率</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 2 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.callcenter.usecase.service.title">客户服务</@t></h5>
						<p class="card-text"><@t key="page.callcenter.usecase.service.desc">400热线、售后支持、投诉受理，提供专业客户服务</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 3 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.callcenter.usecase.collection.title">催收提醒</@t></h5>
						<p class="card-text"><@t key="page.callcenter.usecase.collection.desc">账单提醒、逾期催收、还款通知等自动化外呼</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 4 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.callcenter.usecase.survey.title">满意度调查</@t></h5>
						<p class="card-text"><@t key="page.callcenter.usecase.survey.desc">服务后自动回访，收集客户反馈，持续改进服务质量</@t></p>
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
