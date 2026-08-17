<!DOCTYPE html>
<html lang="${(lang)!'zh-CN'}">
<head>
	<#--  Header  -->
	<#include "../common/meta_clipper.ftl" />
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
				<h1 class="mb-3"><@t key="page.clipper.title">微语剪藏</@t> <span class="fs-4 text-body">(notebase Web Clipper)</span></h1>
				<h4 class="mb-3"><@t key="page.clipper.subtitle">网页内容，一键剪藏到知识库</@t></h4>
				<p class="mb-4"><@t key="page.clipper.desc">开源的 Chrome 网页剪藏插件。浏览网页时，选中内容或通过右键菜单，随时将网页内容保存同步到微语 notebase 知识库，自动转换为 Markdown，支持编辑标题、内容与标签，可存草稿或直接发布。</@t></p>
				<a href="https://www.weiyuai.cn/download/notebase_chrome_extension_latest.zip" class="btn btn-primary" target="_blank">🧩 <@t key="page.clipper.downloadBtn">下载 zip 安装包</@t></a>
			</div>
			<div class="col-md-7">
				<img src="/assets/images/element/05.svg" alt="<@t key='alt.clipper.hero'>微语剪藏浏览器插件插图</@t>">
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
				<h2><@t key="page.clipper.features.title">核心功能</@t></h2>
				<p class="mb-0"><@t key="page.clipper.features.desc">像专业剪藏工具一样，把网页上有价值的内容沉淀进你的知识库</@t></p>
			</div>
		</div>

		<div class="row g-4">

			<!-- 选中即存 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/idea.svg" alt="<@t key='alt.clipper.feature.selection'>选中即存图标</@t>">
					</div>
					<h5 class="mb-1">📌 <@t key="page.clipper.feature.selection.title">选中即存</@t></h5>
					<span class="mb-0"><@t key="page.clipper.feature.selection.desc">在网页中选中文字后，自动出现「保存到知识库」悬浮按钮，点击即可保存，无需离开当前页面。</@t></span>
				</div>
			</div>

			<!-- 右键菜单 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/coding.svg" alt="<@t key='alt.clipper.feature.contextmenu'>右键菜单剪藏图标</@t>">
					</div>
					<h5 class="mb-1">🖱️ <@t key="page.clipper.feature.contextmenu.title">右键菜单剪藏</@t></h5>
					<span class="mb-0"><@t key="page.clipper.feature.contextmenu.desc">支持保存选中内容、整个页面、链接与图片。整页抓取自动识别正文容器，剔除导航、脚本等干扰元素。</@t></span>
				</div>
			</div>

			<!-- 一键快速保存 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/rocket.svg" alt="<@t key='alt.clipper.feature.quick'>一键快速保存图标</@t>">
					</div>
					<h5 class="mb-1">⚡ <@t key="page.clipper.feature.quick.title">一键快速保存</@t></h5>
					<span class="mb-0"><@t key="page.clipper.feature.quick.desc">右键菜单「一键保存选中到默认知识库」，无需打开面板直接保存；点击系统通知可跳转到编辑页。</@t></span>
				</div>
			</div>

			<!-- 浏览器侧边栏 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/online.svg" alt="<@t key='alt.clipper.feature.sidepanel'>浏览器侧边栏图标</@t>">
					</div>
					<h5 class="mb-1">📂 <@t key="page.clipper.feature.sidepanel.title">浏览器侧边栏</@t></h5>
					<span class="mb-0"><@t key="page.clipper.feature.sidepanel.desc">在浏览器右侧栏常驻打开完整剪藏面板（Chrome 114+），随标签页切换自动刷新；也可设置为点击图标直接打开侧边栏。</@t></span>
				</div>
			</div>

			<!-- 知识库选择 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/data-science.svg" alt="<@t key='alt.clipper.feature.kbase'>知识库选择图标</@t>">
					</div>
					<h5 class="mb-1">🗃️ <@t key="page.clipper.feature.kbase.title">知识库选择</@t></h5>
					<span class="mb-0"><@t key="page.clipper.feature.kbase.desc">登录后自动拉取组织下的 notebase 知识库列表，保存时可选择目标知识库，切换即设为默认。</@t></span>
				</div>
			</div>

			<!-- Markdown 编辑 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/engineering.svg" alt="<@t key='alt.clipper.feature.markdown'>Markdown 编辑图标</@t>">
					</div>
					<h5 class="mb-1">📝 <@t key="page.clipper.feature.markdown.title">Markdown 编辑</@t></h5>
					<span class="mb-0"><@t key="page.clipper.feature.markdown.desc">剪藏内容自动转为 Markdown，保存前可编辑标题、内容，添加标签（支持中英文逗号分隔），规范沉淀。</@t></span>
				</div>
			</div>

			<!-- 草稿/发布 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/profit.svg" alt="<@t key='alt.clipper.feature.draft'>草稿发布图标</@t>">
					</div>
					<h5 class="mb-1">🗂️ <@t key="page.clipper.feature.draft.title">草稿 / 发布</@t></h5>
					<span class="mb-0"><@t key="page.clipper.feature.draft.desc">默认保存为草稿，可一键切换为直接发布；「最近保存」记录最近 10 条剪藏，可一键跳转到 notebase 编辑。</@t></span>
				</div>
			</div>

			<!-- 多语言 & 主题 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/artist.svg" alt="<@t key='alt.clipper.feature.misc'>多语言与主题图标</@t>">
					</div>
					<h5 class="mb-1">🎨 <@t key="page.clipper.feature.misc.title">多语言 & 主题</@t></h5>
					<span class="mb-0"><@t key="page.clipper.feature.misc.desc">简体中文 / English 双语界面，浅色 / 深色 / 跟随系统主题，弹窗、侧边栏、设置页与页内面板统一适配。</@t></span>
				</div>
			</div>

		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Usage START -->
<section class="position-relative pt-0 pt-sm-4 pb-0 pb-sm-5">
	<div class="container">
		<div class="row mb-4">
			<div class="col-lg-8 mx-auto text-center">
				<h2><@t key="page.clipper.usage.title">多种剪藏方式</@t></h2>
				<p class="mb-0"><@t key="page.clipper.usage.desc">无论是一段金句、一篇长文、一个链接还是一张图片，都能随手保存</@t></p>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-sm-6 col-lg-3">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<h6 class="mb-2"><@t key="page.clipper.usage.selection.title">保存选中内容</@t></h6>
					<p class="mb-0 small"><@t key="page.clipper.usage.selection.desc">选中文字 → 点击「保存到知识库」悬浮按钮 → 面板中确认保存</@t></p>
				</div>
			</div>
			<div class="col-sm-6 col-lg-3">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<h6 class="mb-2"><@t key="page.clipper.usage.page.title">保存整个页面</@t></h6>
					<p class="mb-0 small"><@t key="page.clipper.usage.page.desc">右键 →「微语剪藏」→「保存整页」，自动抓取正文并转 Markdown</@t></p>
				</div>
			</div>
			<div class="col-sm-6 col-lg-3">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<h6 class="mb-2"><@t key="page.clipper.usage.link.title">保存链接 / 图片</@t></h6>
					<p class="mb-0 small"><@t key="page.clipper.usage.link.desc">右键链接或图片 →「微语剪藏」→「保存链接」/「保存图片」</@t></p>
				</div>
			</div>
			<div class="col-sm-6 col-lg-3">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<h6 class="mb-2"><@t key="page.clipper.usage.popup.title">弹窗快存</@t></h6>
					<p class="mb-0 small"><@t key="page.clipper.usage.popup.desc">点击扩展图标 → 一键「保存选中内容」或「保存整页」</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Usage END -->

<!-- =======================
Download Section START -->
<section class="overflow-hidden">
	<div class="container">
		<div class="row g-4 align-items-center">
			<div class="col-md-5 position-relative z-index-9">
				<h2><@t key="page.clipper.download.title">下载微语剪藏</@t></h2>
				<p><@t key="page.clipper.download.desc">开源免费。适用于 Chrome 114+ 及 Edge、Brave 等 Chromium 内核浏览器。</@t></p>
				<div class="d-flex gap-3 align-items-center mt-3">
					<a href="https://www.weiyuai.cn/download/notebase_chrome_extension_latest.zip" class="btn btn-lg btn-primary" target="_blank">
						🧩 <@t key="page.clipper.download.btn">下载 zip 安装包</@t>
					</a>
				</div>
				<p class="mt-2 small text-body"><@t key="page.clipper.download.note">版本 0.2.0 · 开源免费 · 支持 Chrome 114+</@t></p>
			</div>

			<div class="col-md-7 text-md-end position-relative">
				<!-- SVG decoration -->
				<figure class="position-absolute top-50 end-0 translate-middle-y me-n8">
					<svg width="632.6px" height="540.4px" viewBox="0 0 632.6 540.4">
						<path class="fill-primary opacity-1" d="M531.4,46.9c46.3,27.4,81.4,79.8,91.1,136.2c9.7,56.8-6.4,117.7-38.3,166s-79.4,84.2-138.6,119.3 c-59.6,35.1-130.6,69.7-201.5,62.1c-70.5-7.7-141.4-57.6-185.4-126.5C14.4,335.5-2.9,247.2,23.7,179.5 c26.2-68.1,96.7-116.5,161.6-140.2c64.9-24.2,124.5-24.6,183.3-23.4C427,17.1,485.1,19.5,531.4,46.9z"/>
					</svg>
				</figure>

				<img src="/assets/images/element/07.svg" class="position-relative" alt="<@t key='alt.clipper.download'>微语剪藏下载插图</@t>">
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
				<h2><@t key="page.clipper.install.title">安装步骤</@t></h2>
				<p class="mb-0"><@t key="page.clipper.install.desc">三步开启剪藏之旅</@t></p>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-4">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<div class="d-flex align-items-center mb-3">
						<span class="fs-1 fw-bold text-primary me-3">1</span>
						<h5 class="mb-0"><@t key="page.clipper.install.step1.title">下载并解压</@t></h5>
					</div>
					<p class="mb-0"><@t key="page.clipper.install.step1.desc">点击上方按钮下载 zip 安装包，解压到本地任意目录。</@t></p>
				</div>
			</div>
			<div class="col-md-4">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<div class="d-flex align-items-center mb-3">
						<span class="fs-1 fw-bold text-primary me-3">2</span>
						<h5 class="mb-0"><@t key="page.clipper.install.step2.title">加载扩展</@t></h5>
					</div>
					<p class="mb-0"><@t key="page.clipper.install.step2.desc">打开 chrome://extensions/，开启右上角「开发者模式」，点击「加载已解压的扩展程序」，选择解压后的目录。建议将扩展固定到工具栏，方便随时使用。</@t></p>
				</div>
			</div>
			<div class="col-md-4">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<div class="d-flex align-items-center mb-3">
						<span class="fs-1 fw-bold text-primary me-3">3</span>
						<h5 class="mb-0"><@t key="page.clipper.install.step3.title">登录配置</@t></h5>
					</div>
					<p class="mb-0"><@t key="page.clipper.install.step3.desc">右键扩展图标进入设置页（或弹窗右上角 ⚙），使用微语账号登录，确认服务器地址与 notebase 网页地址，选择默认知识库即可开始剪藏。</@t></p>
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
