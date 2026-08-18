<!DOCTYPE html>
<html lang="${(lang)!'zh-CN'}">
<head>
	<#--  Header  -->
	<#include "../common/meta_liangshibao.ftl" />
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
Hero Banner START -->
<section class="position-relative">

	<!-- SVG decoration -->
	<figure class="position-absolute top-50 end-0 translate-middle-y mt-n8">
		<svg class="rtl-flip" width="1360.5px" height="793px" viewBox="0 0 1360.5 793" style="enable-background:new 0 0 1360.5 793;" xml:space="preserve">
			<path class="fill-primary opacity-1" d="M33.5,766.3c75.3-24.2,124.5-20.3,155.2-62.8c35.4-49,53.1-184.7,138-191.2s100.9,55.6,208.8-21.2 s44.5-134.3,166.4-174.9c121.8-40.6,177,80.1,279.6,36s122.1-248.4,178.8-290.9c49.3-37,171.2-56.7,200.2-61.1v793H33.5 C33.5,793-41.9,790.4,33.5,766.3z"/>
		</svg>
	</figure>

	<div class="container position-relative" style="margin-top: 0; padding-top: 6rem;">
		<div class="row align-items-center">
			<div class="col-md-5">
				<h1 class="mb-3">良师宝</h1>
				<h4 class="mb-3">K12 学习助手 — 英语点读 · 古诗词朗诵 · 高考题库</h4>
				<p class="mb-4">面向 K12 学生的开源学习助手，英语听力点读、必背古诗词朗诵、高考题库刷题，支持多宝宝档案、家庭共享学习记录、跨设备云同步，随时随地陪孩子学习成长。</p>
				<div class="d-flex gap-3 align-items-center flex-wrap">
					<a href="https://www.weiyuai.cn/download/liangshibao-android.apk" class="btn btn-primary" target="_blank">📱 立即下载 Android 版</a>
					<a href="https://liangshibao.com/" class="btn btn-outline-primary" target="_blank">🌐 访问官网</a>
				</div>
			</div>
			<div class="col-md-7">
				<img src="/assets/images/element/05.svg" alt="良师宝 K12 学习助手客户端">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Hero Banner END -->

<!-- =======================
Features START -->
<section class="position-relative pt-4 pt-sm-5 pb-0 pb-sm-5">
	<div class="container">
		<div class="row mb-4">
			<div class="col-lg-8 mx-auto text-center">
				<h2>核心功能</h2>
				<p class="mb-0">覆盖听、说、读、练的完整学习闭环，为每个孩子打造专属的学习空间</p>
			</div>
		</div>

		<div class="row g-4">

			<!-- 英语听力点读 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/online.svg" alt="英语听力点读">
					</div>
					<h5 class="mb-1">📖 英语听力点读</h5>
					<span class="mb-0">覆盖主流教材的英语听力内容，点击即可播放原声，支持循环、倍速播放，让孩子在磨耳朵中提升语感。</span>
				</div>
			</div>

			<!-- 古诗词朗诵 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/artist.svg" alt="古诗词朗诵">
					</div>
					<h5 class="mb-1">🎙️ 古诗词朗诵</h5>
					<span class="mb-0">收录必背古诗词，配标准普通话朗诵音频，逐句对照、反复跟读，轻松完成背诵任务。</span>
				</div>
			</div>

			<!-- 高考题库刷题 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/data-science.svg" alt="高考题库刷题">
					</div>
					<h5 class="mb-1">📚 高考题库刷题</h5>
					<span class="mb-0">海量高考真题与模拟题，分科分类练习，自动记录做题进度与错题，精准查漏补缺。</span>
				</div>
			</div>

			<!-- 多宝宝档案 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/profit.svg" alt="多宝宝档案">
					</div>
					<h5 class="mb-1">👶 多宝宝档案</h5>
					<span class="mb-0">一个家庭可创建多个宝宝档案，分别记录学习进度、收藏与背诵情况，互不干扰。</span>
				</div>
			</div>

			<!-- 家庭共享 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/home.svg" alt="家庭共享">
					</div>
					<h5 class="mb-1">👨‍👩‍👧 家庭共享</h5>
					<span class="mb-0">学习记录在家庭成员间共享，家长随时查看孩子的学习进度，共同陪伴成长。</span>
				</div>
			</div>

			<!-- 跨设备云同步 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/coding.svg" alt="跨设备云同步">
					</div>
					<h5 class="mb-1">☁️ 跨设备云同步</h5>
					<span class="mb-0">收藏、已背过、做题记录等学习数据云端同步，更换设备不丢失，随时随地继续学习。</span>
				</div>
			</div>

			<!-- 隐私安全 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/contact.svg" alt="隐私安全">
					</div>
					<h5 class="mb-1">🔒 隐私安全</h5>
					<span class="mb-0">学习数据按用户隔离存储，仅对家庭成员可见，不开设公开社区，守护孩子隐私。</span>
				</div>
			</div>

			<!-- 技术栈 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/engineering.svg" alt="技术栈">
					</div>
					<h5 class="mb-1">⚙️ 技术栈</h5>
					<span class="mb-0">Flutter 3.x + flutter_bloc 状态管理 + just_audio / audio_service 全局播放，Cupertino 风格，iOS / Android 双端一致体验。</span>
				</div>
			</div>

		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Download Section START -->
<section class="overflow-hidden">
	<div class="container">
		<div class="row g-4 align-items-center">
			<div class="col-md-5 position-relative z-index-9">
				<h2>下载良师宝</h2>
				<p>当前提供 Android 版本。iOS 版本开发中，敬请期待。</p>
				<div class="d-flex gap-3 align-items-center mt-3 flex-wrap">
					<a href="https://www.weiyuai.cn/download/liangshibao-android.apk" class="btn btn-lg btn-primary" target="_blank">
						📱 下载 Android APK
					</a>
					<a href="https://liangshibao.com/" class="btn btn-lg btn-outline-primary" target="_blank">
						🌐 访问官网
					</a>
				</div>
				<p class="mt-2 small text-muted">版本 1.0.0 · 适用于 Android 5.0+ · 官网：<a href="https://liangshibao.com/" target="_blank" rel="noopener">liangshibao.com</a></p>
			</div>

			<div class="col-md-7 text-md-end position-relative">
				<!-- SVG decoration -->
				<figure class="position-absolute top-50 end-0 translate-middle-y me-n8">
					<svg width="632.6px" height="540.4px" viewBox="0 0 632.6 540.4">
						<path class="fill-primary opacity-1" d="M531.4,46.9c46.3,27.4,81.4,79.8,91.1,136.2c9.7,56.8-6.4,117.7-38.3,166s-79.4,84.2-138.6,119.3 c-59.6,35.1-130.6,69.7-201.5,62.1c-70.5-7.7-141.4-57.6-185.4-126.5C14.4,335.5-2.9,247.2,23.7,179.5 c26.2-68.1,96.7-116.5,161.6-140.2c64.9-24.2,124.5-24.6,183.3-23.4C427,17.1,485.1,19.5,531.4,46.9z"/>
					</svg>
				</figure>

				<img src="/assets/images/element/07.svg" class="position-relative" alt="良师宝 下载">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Download Section END -->

<!-- =======================
Getting Started START -->
<section class="position-relative pt-0 pt-sm-5 pb-4 pb-sm-5">
	<div class="container">
		<div class="row mb-4">
			<div class="col-lg-8 mx-auto text-center">
				<h2>快速上手</h2>
				<p class="mb-0">三步开始使用良师宝</p>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-4">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<div class="d-flex align-items-center mb-3">
						<span class="fs-1 fw-bold text-primary me-3">1</span>
						<h5 class="mb-0">下载安装</h5>
					</div>
					<p class="mb-0">下载 Android APK 并安装，使用手机验证码一键登录，无需绑定组织。</p>
				</div>
			</div>
			<div class="col-md-4">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<div class="d-flex align-items-center mb-3">
						<span class="fs-1 fw-bold text-primary me-3">2</span>
						<h5 class="mb-0">添加宝宝档案</h5>
					</div>
					<p class="mb-0">创建宝宝档案，邀请家庭成员加入，共享学习记录。</p>
				</div>
			</div>
			<div class="col-md-4">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<div class="d-flex align-items-center mb-3">
						<span class="fs-1 fw-bold text-primary me-3">3</span>
						<h5 class="mb-0">开始学习</h5>
					</div>
					<p class="mb-0">选择英语点读、古诗词朗诵或题库刷题，开启每日学习，记录自动同步。</p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Getting Started END -->

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
