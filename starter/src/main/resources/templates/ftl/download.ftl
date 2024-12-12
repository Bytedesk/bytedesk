<!DOCTYPE html>
<html lang="en">
<head>
	<#--  Header  -->
	<#include "./common/header_meta.ftl" />
	<#include "./common/header_js.ftl" />
	<#include "./common/header_css.ftl" />
	
</head>

<body>

<#--  导航  -->
<#include "./common/header_nav.ftl" />

<!-- **************** MAIN CONTENT START **************** -->
<main>

<#include "./common/banner.ftl" />

<!-- =======================
Listed course START -->
<section class="position-relative pb-0 pb-sm-5">
	<div class="container">
		<!-- Title -->
		<div class="row mb-4">
			<div class="col-lg-8 mx-auto text-center">
				<h2>免费下载</h2>
				<p class="mb-0">开源、开放、安全、支持自定义、支持二次开发.</p>
			</div>
		</div>

		<div class="row g-4">

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/coding.svg" alt="">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/download/weiyu-server.zip" class="stretched-link" target="_blank">服务器端</a></h5>
					<#--  <span class="mb-0">即将开放下载</span>  -->
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/data-science.svg" alt="">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/download/weiyu-x64.exe" class="stretched-link" target="_blank">Windows客户端</a></h5>
					<#--  <span class="mb-0">即将开放下载.</span>  -->
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/online.svg" alt="">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/download/weiyu-universal.dmg" class="stretched-link" target="_blank">Mac客户端</a></h5>
					<#--  <span class="mb-0">即将开放下载.</span>  -->
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/profit.svg" alt="">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/download/weiyu-x86_64.AppImage" class="stretched-link" target="_blank">Linux客户端</a></h5>
					<#--  <span class="mb-0">即将开放下载.</span>  -->
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/engineering.svg" alt="">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/download/weiyu-android.apk" class="stretched-link" target="_blank">Android安卓</a></h5>
					<a href="assets/qr/qr_android.png" target="_blank">
						<img src="assets/qr/qr_android.png" style="height: 100px "/>
					</a>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/profit.svg" alt="">
					</div>
					<h5 class="mb-1"><a href="https://apps.apple.com/cn/app/%E5%BE%AE%E8%AF%AD/id6470106586" class="stretched-link" target="_blank">iPhone苹果</a></h5>
					<#--  <span class="mb-0">即将开放下载</span>  -->
					<a href="assets/qr/qr_ios.png" target="_blank">
						<img src="assets/qr/qr_ios.png" style="height: 100px "/>
					</a>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/profit.svg" alt="">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/download" class="stretched-link" target="_blank">所有下载</a></h5>
					<#--  <h5 class="mb-1"><a href="https://pan.baidu.com/s/1lEoBGKh-iQJvfndnhn8r-A?pwd=jnk2" class="stretched-link" target="_blank">更多去网盘下载</a></h5>  -->
					<#--  <span class="mb-0">即将开放下载.</span>  -->
				</div>
			</div>

			<!-- Item -->
			<#--  <div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/home.svg" alt="">
					</div>
					<h5 class="mb-1"><a href="#" class="stretched-link">Open APIs</a></h5>
					<span class="mb-0">We also supply many apis for developers</span>
				</div>
			</div>  -->

			<!-- Item -->
			<#--  <div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/artist.svg" alt="">
					</div>
					<h5 class="mb-1"><a href="#" class="stretched-link">Omni Channel</a></h5>
					<span class="mb-0">We supply multi channel plugins, you can choose the one you need</span>
				</div>
			</div>  -->
		</div>
	</div>
</section>
<!-- =======================
Listed course END -->

<!-- =======================
Download START -->
<section class="overflow-hidden">
	<div class="container">
		<div class="row g-4 align-items-center">
			<div class="col-md-5 position-relative z-index-9">
				<!-- Title -->
				<h2>支持定制</h2>
				<p>如果您有定制需求，请与我们联系.</p>
				<!-- Download button -->
				<div class="row">
					<!-- Google play store button -->
					<div class="col-6 col-sm-4 col-md-6 col-lg-4">
						<a href="mailto:270580156@qq.com">contact us</a>
						<#--  <a href="#"><img src="assets/images/element/google-play.svg" class="btn-transition" alt="google-play"></a>  -->
					</div>
					<!-- App store button -->
					<div class="col-6 col-sm-4 col-md-6 col-lg-4">
						<#--  <a href="#"><img src="assets/images/element/app-store.svg" class="btn-transition" alt="app-store"></a>  -->
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
				<img src="assets/images/element/07.svg" class="position-relative" alt="">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Download END -->

<#include "./common/action_box.ftl" />

</main>
<!-- **************** MAIN CONTENT END **************** -->

<!-- ======================= Footer START -->
<#include "./common/footer_nav.ftl" />
<!-- ======================= Footer END -->

<#include "./common/footer_js.ftl" />

<#-- livechat code 客服代码  -->
<#include "./common/bytedesk.ftl" />

<#-- trace code 统计代码  -->
<#include "./common/track.ftl" />

</body>
</html>