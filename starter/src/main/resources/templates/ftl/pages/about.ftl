<!DOCTYPE html>
<html lang="${(lang)!'zh-CN'}">
<head>
	<#--  Header  -->
	<#include "../common/meta_about.ftl" />
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

<#include "../common/banner.ftl" />

<!-- =======================
About START -->
<section class="overflow-hidden pt-4 pt-sm-5">
	<div class="container">
		<div class="row g-4 align-items-center">
            <div class="col-md-5 position-relative z-index-9">
				<!-- Title -->
				<h2><@t key="page.about.title">关于我们</@t></h2>
				<p><@t key="page.about.desc">微语（Bytedesk）由北京微语天下科技有限公司开发维护，是 AI 驱动的全渠道智能客服与团队协作平台，覆盖即时通讯、在线/工单客服、呼叫中心、音视频、会议、知识库等场景，支持私有化部署与多租户。</@t></p>
				<p class="mb-0"><@t key="page.about.desc2">提供社区版（免费开源、可商用）、企业版、平台版与源码版多种版本，价格公开透明、一口价不议价；支持30天免费试用与7天无理由退款。开发者为 OPC（一人公司），如有建议或反馈，</@t><a href="https://www.weiyuai.cn/assets/images/qrcode/wechat.png" target="_blank"><@t key="page.about.wechat.link">欢迎扫码联系微信</@t></a><@t key="page.about.wechat.suffix">，备注：微语。</@t></p>
				<!-- WeChat QR 联系方式 -->
				<div class="mt-3">
					<a href="https://www.weiyuai.cn/assets/images/qrcode/wechat.png" target="_blank" class="d-inline-block">
						<img src="https://www.weiyuai.cn/assets/images/qrcode/wechat.png" alt="<@t key='page.about.wechat.alt'>微信二维码，扫码添加好友咨询</@t>" style="width:120px;height:auto;" loading="lazy"/>
					</a>
				</div>
				<!-- Download button -->
				<div class="row">
					<!-- Google play store button -->
					<div class="col-6 col-sm-4 col-md-6 col-lg-4">
						<#--  <a href="mailto:270580156@qq.com">contact us</a>  -->
						<#--  <a href="#"><img src="/assets/images/element/google-play.svg" class="btn-transition" alt="google-play"></a>  -->
					</div>
					<!-- App store button -->
					<div class="col-6 col-sm-4 col-md-6 col-lg-4">
						<#--  <a href="#"><img src="/assets/images/element/app-store.svg" class="btn-transition" alt="app-store"></a>  -->
					</div>
				</div>
			</div>

			<div class="col-md-7 text-md-end position-relative">
				<!-- SVG decoration -->
				<figure class="position-absolute top-50 end-0 translate-middle-y me-n8">
					<svg width="632.6px" height="540.4px" viewBox="0 0 632.6 540.4">
						<path class="fill-primary opacity-1" d="M531.4,46.9c46.3,27.4,81.4,79.8,91.1,136.2c9.7,56.8-6.4,117.7-38.3,166s-79.4,84.2-138.6,119.3 c-59.6,35.1-130.6,69.7-201.5,62.1c-70.5-7.7-141.4-57.6-185.4-126.5C14.4,335.5-2.9,247.2,23.7,179.5 c26.2-68.1,96.7-116.5,161.6-140.2c64.9-24.2,124.5-24.6,183.3-23.4C427,17.1,485.1,19.5,531.4,46.9z"/>
					</svg>
				</figure>

				<!-- Image -->
				<img src="/assets/images/element/07.svg" class="position-relative" alt="<@t key='alt.about.illustration'>公司介绍插图</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
About END -->

<!-- =======================
Versions & Pricing START -->
<section class="pt-0 pb-4 pb-sm-5">
	<div class="container">
		<!-- Title -->
		<div class="row mb-4">
			<div class="col-lg-8 mx-auto text-center">
				<h2><@t key="page.about.version.title">版本与价格</@t></h2>
				<p class="mb-0"><@t key="page.about.version.subtitle">多种版本满足不同规模企业需求：买断为永久授权，按年付费需每年续费，价格均不含税点</@t></p>
			</div>
		</div>

		<div class="row g-4">
			<!-- Item: 社区版 -->
			<div class="col-sm-6 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/coding.svg" alt="<@t key='alt.about.version.community'>社区版图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><@t key="page.about.version.community">社区版</@t></h5>
					<div class="mb-2"><span class="fs-4 fw-bold"><@t key="page.about.version.community.price">免费</@t></span></div>
					<p class="small mb-0"><@t key="page.about.version.community.desc">单租户、单机版，基本功能开源，可商用，用户/客服/机器人/知识库数量不限，保留微语logo，不支持品牌自定义</@t></p>
				</div>
			</div>

			<!-- Item: 企业版 -->
			<div class="col-sm-6 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/online.svg" alt="<@t key='alt.about.version.enterprise'>企业版图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><@t key="page.about.version.enterprise">企业版</@t></h5>
					<div class="mb-2">
						<span class="fs-4 fw-bold"><@t key="page.about.version.enterprise.price">¥49,800</@t></span>
						<span class="small d-block"><@t key="page.about.version.enterprise.yearly">或 ¥22,800/年</@t></span>
					</div>
					<p class="small mb-0"><@t key="page.about.version.enterprise.desc">单租户，支持集群，功能完整，提供私有化部署包（不含源码），数量不限，支持品牌自定义</@t></p>
				</div>
			</div>

			<!-- Item: 平台版 -->
			<div class="col-sm-6 col-xl-3">
				<div class="bg-warning bg-opacity-25 rounded-3 text-center p-3 h-100 position-relative">
					<span class="badge bg-warning text-dark position-absolute top-0 start-50 translate-middle"><@t key="page.about.version.popular">最受欢迎</@t></span>
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3 mt-2">
						<img src="/assets/images/element/rocket.svg" alt="<@t key='alt.about.version.platform'>平台版图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><@t key="page.about.version.platform">平台版</@t></h5>
					<div class="mb-2">
						<span class="fs-4 fw-bold"><@t key="page.about.version.platform.price">¥79,800</@t></span>
						<span class="small d-block"><@t key="page.about.version.platform.yearly">或 ¥32,800/年</@t></span>
					</div>
					<p class="small mb-0"><@t key="page.about.version.platform.desc">多租户，支持集群，功能完整，提供私有化部署包（不含源码），数量不限，支持品牌自定义</@t></p>
				</div>
			</div>

			<!-- Item: 源码版 -->
			<div class="col-sm-6 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/engineering.svg" alt="<@t key='alt.about.version.source'>源码版图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><@t key="page.about.version.source">源码版</@t></h5>
					<div class="mb-2"><span class="fs-4 fw-bold"><@t key="page.about.version.source.price">按模块定价</@t></span></div>
					<p class="small mb-0"><@t key="page.about.version.source.desc">包含完整源码与高级功能，支持按模块单独购买，含平台版全部功能，可自行修改源码，支持定制化开发</@t></p>
				</div>
			</div>
		</div>

		<!-- Note + more link -->
		<div class="row mt-3">
			<div class="col-12 text-center">
				<p class="small text-body-secondary mb-2"><@t key="page.about.version.note">买断价格为永久授权；如需发票，普票加收1%税点，专票加收1%或3%税点（可选）；三个月内升级版本，已支付费用可抵扣差价</@t></p>
				<a href="${pricingUrl!'https://www.weiyuai.cn/docs/zh-CN/docs/payment'}" class="btn btn-sm btn-outline-primary" target="_blank"><@t key="page.about.version.more">查看完整价格与服务条款</@t></a>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Versions & Pricing END -->

<!-- =======================
Service START -->
<section class="pt-0 pb-4 pb-sm-5">
	<div class="container">
		<!-- Title -->
		<div class="row mb-4">
			<div class="col-lg-8 mx-auto text-center">
				<h2><@t key="page.about.service.title">服务承诺</@t></h2>
				<p class="mb-0"><@t key="page.about.service.subtitle">所有价格公开透明，无隐藏收费，一口价、不议价</@t></p>
			</div>
		</div>

		<div class="row g-4">
			<!-- Item: 价格透明 -->
			<div class="col-sm-6 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/profit.svg" alt="<@t key='alt.about.service.pricing'>价格透明图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><@t key="page.about.service.pricing.title">价格透明</@t></h5>
					<p class="small mb-0"><@t key="page.about.service.pricing.desc">所有价格公开透明，无隐藏收费，一口价、不议价</@t></p>
				</div>
			</div>

			<!-- Item: 试用保障 -->
			<div class="col-sm-6 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/medal.svg" alt="<@t key='alt.about.service.trial'>试用保障图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><@t key="page.about.service.trial.title">试用保障</@t></h5>
					<p class="small mb-0"><@t key="page.about.service.trial.desc">提供30天免费试用，付款后支持7天无理由退款（已交付源码或授权license除外）</@t></p>
				</div>
			</div>

			<!-- Item: 升级维护 -->
			<div class="col-sm-6 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/data-science.svg" alt="<@t key='alt.about.service.upgrade'>升级维护图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><@t key="page.about.service.upgrade.title">升级维护</@t></h5>
					<p class="small mb-0"><@t key="page.about.service.upgrade.desc">一年内免费升级，一年后可选15%/年维护费，包含bug修复与版本升级</@t></p>
				</div>
			</div>

			<!-- Item: 定制开发 -->
			<div class="col-sm-6 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/idea.svg" alt="<@t key='alt.about.service.custom'>定制开发图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><@t key="page.about.service.custom.title">定制开发</@t></h5>
					<p class="small mb-0"><@t key="page.about.service.custom.desc">可提供定制开发服务，按 ¥5,000/人天 计算，满足个性化业务需求</@t></p>
				</div>
			</div>
		</div>

		<!-- Copyright -->
		<div class="row mt-4">
			<div class="col-12 text-center">
				<p class="small text-body-secondary mb-0"><@t key="page.about.copyright">版权所有：北京微语天下科技有限公司</@t></p>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Service END -->

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