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
				<h1><@t key="page.video.title">视频会议 / 视频客服</@t></h1>
				<p class="lead"><@t key="page.video.subtitle">基于WebRTC的实时音视频通信解决方案</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="/assets/images/element/online.svg" class="h-200px" alt="<@t key='alt.video.icon'>视频会议图标</@t>">
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
				<h2><@t key="page.video.features.title">核心功能</@t></h2>
				<p class="mb-0"><@t key="page.video.features.desc">全场景音视频通信能力</@t></p>
			</div>
		</div>

		<!-- Feature list -->
		<div class="row g-4">
			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-camera-video fs-5"></i>
					</div>
					<h5><@t key="page.video.feature.meeting.title">视频会议</@t></h5>
					<p class="mb-0"><@t key="page.video.feature.meeting.desc">高清音视频通话，支持多人会议，屏幕共享，白板协作</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-headset fs-5"></i>
					</div>
					<h5><@t key="page.video.feature.service.title">视频客服</@t></h5>
					<p class="mb-0"><@t key="page.video.feature.service.desc">面对面沟通，提升服务体验，支持远程协助和问题诊断</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-display fs-5"></i>
					</div>
					<h5><@t key="page.video.feature.screenshare.title">屏幕共享</@t></h5>
					<p class="mb-0"><@t key="page.video.feature.screenshare.desc">分享桌面、应用窗口，远程演示，提升沟通效率</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-cursor fs-5"></i>
					</div>
					<h5><@t key="page.video.feature.remote.title">远程协助</@t></h5>
					<p class="mb-0"><@t key="page.video.feature.remote.desc">远程控制、远程操作，快速解决客户技术问题</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-record-circle fs-5"></i>
					</div>
					<h5><@t key="page.video.feature.recording.title">视频录制</@t></h5>
					<p class="mb-0"><@t key="page.video.feature.recording.desc">会议录制、回放，便于后续回顾和知识沉淀</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-robot fs-5"></i>
					</div>
					<h5><@t key="page.video.feature.ai.title">AI视频分析</@t></h5>
					<p class="mb-0"><@t key="page.video.feature.ai.desc">人脸识别、表情分析、语音转文字、会议纪要自动生成</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Platform START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.video.platform.title">全平台支持</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-6 col-lg-3">
				<div class="text-center">
					<div class="icon-xl bg-primary bg-opacity-10 text-primary rounded-circle mx-auto mb-3">
						<i class="bi bi-phone fs-4"></i>
					</div>
					<h5><@t key="page.video.platform.mobile.title">移动端</@t></h5>
					<p class="small"><@t key="page.video.platform.mobile.desc">iOS / Android 原生应用</@t></p>
				</div>
			</div>

			<div class="col-md-6 col-lg-3">
				<div class="text-center">
					<div class="icon-xl bg-success bg-opacity-10 text-success rounded-circle mx-auto mb-3">
						<i class="bi bi-laptop fs-4"></i>
					</div>
					<h5><@t key="page.video.platform.desktop.title">桌面端</@t></h5>
					<p class="small"><@t key="page.video.platform.desktop.desc">Windows / Mac / Linux</@t></p>
				</div>
			</div>

			<div class="col-md-6 col-lg-3">
				<div class="text-center">
					<div class="icon-xl bg-warning bg-opacity-10 text-warning rounded-circle mx-auto mb-3">
						<i class="bi bi-browser-chrome fs-4"></i>
					</div>
					<h5><@t key="page.video.platform.web.title">Web端</@t></h5>
					<p class="small"><@t key="page.video.platform.web.desc">无需安装，浏览器直接使用</@t></p>
				</div>
			</div>

			<div class="col-md-6 col-lg-3">
				<div class="text-center">
					<div class="icon-xl bg-info bg-opacity-10 text-info rounded-circle mx-auto mb-3">
						<i class="bi bi-code-slash fs-4"></i>
					</div>
					<h5><@t key="page.video.platform.sdk.title">SDK集成</@t></h5>
					<p class="small"><@t key="page.video.platform.sdk.desc">提供SDK，快速集成到应用</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Platform END -->

<!-- =======================
Advantages START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.video.advantages.title">产品优势</@t></h2>
			</div>
		</div>

		<div class="row g-4 align-items-center">
			<div class="col-lg-6">
				<ul class="list-group list-group-borderless">
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.video.advantage.quality">高清音视频，智能降噪，自适应码率</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.video.advantage.lowlatency">低延迟传输，流畅的实时通信体验</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.video.advantage.webrtc">基于WebRTC标准，安全可靠</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.video.advantage.scalable">弹性伸缩，支持大规模并发</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.video.advantage.private">支持私有化部署，数据安全可控</@t>
					</li>
				</ul>
			</div>

			<div class="col-lg-6 text-center">
				<img src="/assets/images/element/07.svg" class="img-fluid" alt="<@t key='alt.video.advantage'>视频会议优势插图</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Advantages END -->

<!-- =======================
Use Cases START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.video.usecases.title">应用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Use case 1 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.video.usecase.meeting.title">远程会议</@t></h5>
						<p class="card-text"><@t key="page.video.usecase.meeting.desc">团队协作会议、跨地域沟通、在线培训、远程面试</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 2 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.video.usecase.service.title">在线客服</@t></h5>
						<p class="card-text"><@t key="page.video.usecase.service.desc">视频客服、远程技术支持、产品演示、售前咨询</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 3 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.video.usecase.education.title">在线教育</@t></h5>
						<p class="card-text"><@t key="page.video.usecase.education.desc">远程教学、直播课堂、一对一辅导、互动答疑</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 4 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.video.usecase.medical.title">远程医疗</@t></h5>
						<p class="card-text"><@t key="page.video.usecase.medical.desc">在线问诊、远程会诊、医患沟通、健康咨询</@t></p>
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
