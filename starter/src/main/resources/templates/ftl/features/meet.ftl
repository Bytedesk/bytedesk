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
				<h1><@t key="page.meet.title">微语会议</@t></h1>
				<p class="lead"><@t key="page.meet.subtitle">开源自建会议系统：会议室管理、邀请链接、参会记录，打开浏览器即可开会，数据不出服务器</@t></p>
				<div class="d-flex flex-wrap gap-2 mt-4">
					<a href="https://www.weiyuai.cn/meet/" class="btn btn-primary" target="_blank">
						<i class="bi bi-box-arrow-up-right me-1"></i><@t key="page.meet.cta.demo">在线演示</@t>
					</a>
					<a href="${docBaseUrl}docs/meet/intro" class="btn btn-outline-primary" target="_blank">
						<@t key="page.meet.cta.docs">查看文档</@t>
					</a>
				</div>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="/assets/images/element/online.svg" class="h-200px" alt="<@t key='alt.meet.icon'>微语会议插图</@t>">
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
				<h2><@t key="page.meet.features.title">核心功能</@t></h2>
				<p class="mb-0"><@t key="page.meet.features.desc">当前 0.1.0 Preview 从音频会议起步：创建会议室、分享邀请链接、加入会议、自动留下参会记录</@t></p>
			</div>
		</div>

		<!-- Feature list -->
		<div class="row g-4">
			<!-- Feature item -->
			<div class="col-sm-6 col-lg-3">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-door-open fs-5"></i>
					</div>
					<h5><@t key="page.meet.feature.room.title">会议室管理</@t></h5>
					<p class="mb-0"><@t key="page.meet.feature.room.desc">每个会议室都有名称、会议号、类型和描述；首页列表清晰展示，新建、删除一键完成，只显示自己创建和参与过的会议</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-sm-6 col-lg-3">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-link-45deg fs-5"></i>
					</div>
					<h5><@t key="page.meet.feature.invite.title">一键邀请链接</@t></h5>
					<p class="mb-0"><@t key="page.meet.feature.invite.desc">每个会议室都有专属邀请链接，复制即可发送；同事打开链接、登录后即可加入，会议室内自动显示昵称</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-sm-6 col-lg-3">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-mic-fill fs-5"></i>
					</div>
					<h5><@t key="page.meet.feature.audio.title">音频会议</@t></h5>
					<p class="mb-0"><@t key="page.meet.feature.audio.desc">打开网页即可加入/退出会议，无需安装客户端；一键静音/取消静音，实时显示参会成员列表</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-sm-6 col-lg-3">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-clipboard-check fs-5"></i>
					</div>
					<h5><@t key="page.meet.feature.record.title">参会记录</@t></h5>
					<p class="mb-0"><@t key="page.meet.feature.record.desc">自动记录谁参加了会议、何时加入离开、参会多久、是否为主持人；意外掉线也会自动补全记录</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-sm-6 col-lg-3">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-speedometer2 fs-5"></i>
					</div>
					<h5><@t key="page.meet.feature.admin.title">管理后台</@t></h5>
					<p class="mb-0"><@t key="page.meet.feature.admin.desc">配套 meetAdmin 管理后台：会议室管理、参会记录查询、按角色的权限控制、服务实时监控</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-sm-6 col-lg-3">
				<div class="card card-body h-100">
					<div class="icon-lg bg-secondary bg-opacity-10 text-secondary rounded-circle mb-3">
						<i class="bi bi-mic-mute-fill fs-5"></i>
					</div>
					<h5><@t key="page.meet.feature.mic.title">麦克风引导</@t></h5>
					<p class="mb-0"><@t key="page.meet.feature.mic.desc">加入会议前自动检查麦克风是否可用；权限被拒绝、浏览器不支持等情况，给出明确的提示和解决办法</@t></p>
				</div>
			</div>

			<!-- Feature item: 视频会议（即将上线） -->
			<div class="col-sm-6 col-lg-3">
				<div class="card card-body h-100">
					<div class="d-flex align-items-center justify-content-between mb-3">
						<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle">
							<i class="bi bi-camera-video-fill fs-5"></i>
						</div>
						<span class="badge bg-warning text-dark"><@t key="page.meet.badge.upcoming">即将上线</@t></span>
					</div>
					<h5><@t key="page.meet.feature.video.title">视频会议</@t></h5>
					<p class="mb-0"><@t key="page.meet.feature.video.desc">高清音视频通话，多人会议，白板协作，会议录制与回放</@t></p>
				</div>
			</div>

			<!-- Feature item: 屏幕共享（即将上线） -->
			<div class="col-sm-6 col-lg-3">
				<div class="card card-body h-100">
					<div class="d-flex align-items-center justify-content-between mb-3">
						<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle">
							<i class="bi bi-display fs-5"></i>
						</div>
						<span class="badge bg-warning text-dark"><@t key="page.meet.badge.upcoming">即将上线</@t></span>
					</div>
					<h5><@t key="page.meet.feature.screenshare.title">屏幕共享</@t></h5>
					<p class="mb-0"><@t key="page.meet.feature.screenshare.desc">分享桌面与应用窗口，远程演示，提升沟通效率</@t></p>
				</div>
			</div>

			<!-- Feature item: 视频客服（即将上线） -->
			<div class="col-sm-6 col-lg-3">
				<div class="card card-body h-100">
					<div class="d-flex align-items-center justify-content-between mb-3">
						<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle">
							<i class="bi bi-headset fs-5"></i>
						</div>
						<span class="badge bg-warning text-dark"><@t key="page.meet.badge.upcoming">即将上线</@t></span>
					</div>
					<h5><@t key="page.meet.feature.videoservice.title">视频客服</@t></h5>
					<p class="mb-0"><@t key="page.meet.feature.videoservice.desc">面对面视频沟通，远程协助，快速诊断解决客户问题</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-sm-6 col-lg-3">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-translate fs-5"></i>
					</div>
					<h5><@t key="page.meet.feature.i18n.title">多语言支持</@t></h5>
					<p class="mb-0"><@t key="page.meet.feature.i18n.desc">会议客户端与管理后台均支持简体中文、繁体中文、English 三种语言</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Why START -->
<section class="bg-body-tertiary">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.meet.why.title">为什么选择微语会议</@t></h2>
				<p class="mb-0"><@t key="page.meet.why.desc">会议开在自己的服务器上，数据不经过第三方</@t></p>
			</div>
		</div>

		<div class="row g-4 align-items-center">
			<div class="col-lg-6">
				<ul class="list-group list-group-borderless">
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.meet.why.opensource">开源自建：系统跑在自己的服务器上，数据不出门，隐私可控</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.meet.why.installfree">免安装：浏览器直接开会，同事凭邀请链接即可加入</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.meet.why.account">一号通行：复用公司微语账号，无需重复注册</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.meet.why.audit">有据可查：参会记录自动留存，考勤、复盘不靠人工</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.meet.why.safe">放心分享：邀请链接需登录后才能进入，管理功能按角色授权</@t>
					</li>
				</ul>
			</div>

			<div class="col-lg-6 text-center">
				<img src="/assets/images/element/07.svg" class="img-fluid" alt="<@t key='alt.meet.advantage'>微语会议优势插图</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Why END -->

<!-- =======================
Roadmap START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.meet.roadmap.title">路线图</@t></h2>
				<p class="mb-0"><@t key="page.meet.roadmap.desc">能力分阶段上线，视频会议、屏幕共享即将到来</@t></p>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-sm-6 col-md-4 col-lg-3">
				<div class="border rounded-3 p-3 text-center h-100">
					<i class="bi bi-calendar2-event text-warning fs-3"></i>
					<p class="mb-0 mt-2"><@t key="page.meet.roadmap.schedule">预定会议与快速会议</@t></p>
				</div>
			</div>
			<div class="col-sm-6 col-md-4 col-lg-3">
				<div class="border rounded-3 p-3 text-center h-100">
					<i class="bi bi-shield-lock text-danger fs-3"></i>
					<p class="mb-0 mt-2"><@t key="page.meet.roadmap.password">会议密码与主持人控制</@t></p>
				</div>
			</div>
			<div class="col-sm-6 col-md-4 col-lg-3">
				<div class="border rounded-3 p-3 text-center h-100">
					<i class="bi bi-bar-chart-line text-secondary fs-3"></i>
					<p class="mb-0 mt-2"><@t key="page.meet.roadmap.stats">参会实时统计</@t></p>
				</div>
			</div>
			<div class="col-sm-6 col-md-4 col-lg-3">
				<div class="border rounded-3 p-3 text-center h-100">
					<i class="bi bi-phone text-primary fs-3"></i>
					<p class="mb-0 mt-2"><@t key="page.meet.roadmap.mobile">手机、平板等移动端</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Roadmap END -->

<!-- =======================
Quick Start START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.meet.quickstart.title">快速开始</@t></h2>
				<p class="mb-0"><@t key="page.meet.quickstart.desc">两种方式，马上开始您的第一次会议</@t></p>
			</div>
		</div>

		<div class="row g-4">
			<!-- Demo -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.meet.quickstart.demo.title">方式一：直接使用在线演示</@t></h5>
						<p class="card-text"><@t key="page.meet.quickstart.demo.desc">打开会议客户端演示并登录，新建一个会议室，复制邀请链接发给同事；同事打开链接、登录后点击「加入会议」，允许使用麦克风即可开始通话</@t></p>
						<a href="https://www.weiyuai.cn/meet/" class="btn btn-primary btn-sm" target="_blank"><@t key="page.meet.quickstart.demo.btn">打开在线演示</@t></a>
						<a href="https://www.weiyuai.cn/meetadmin" class="btn btn-outline-primary btn-sm ms-1" target="_blank"><@t key="page.meet.quickstart.admin.btn">管理后台演示</@t></a>
					</div>
				</div>
			</div>

			<!-- Deploy -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.meet.quickstart.deploy.title">方式二：部署到自己的服务器</@t></h5>
						<p class="card-text"><@t key="page.meet.quickstart.deploy.desc">参考项目部署文档，使用 Docker 即可快速启动；会议数据存储在您自己的服务器上</@t></p>
						<a href="${docBaseUrl}docs/meet/deploy" class="btn btn-primary btn-sm" target="_blank"><@t key="page.meet.quickstart.deploy.btn">查看部署文档</@t></a>
					</div>
				</div>
			</div>
		</div>

		<!-- Mic note -->
		<div class="row mt-4">
			<div class="col-12">
				<div class="alert alert-info d-flex align-items-start" role="alert">
					<i class="bi bi-info-circle-fill me-2 mt-1"></i>
					<div><@t key="page.meet.note">浏览器要求在 HTTPS 或 localhost 环境下才能使用麦克风。如果无法开启麦克风，按页面提示操作即可；正式使用建议为服务配置 HTTPS</@t></div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Quick Start END -->

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
