<!DOCTYPE html>
<html lang="${(lang)!'zh-CN'}">
<head>
	<#--  Header  -->
	<#include "../common/meta_termshell.ftl" />
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
				<h1 class="mb-3">Termshell</h1>
				<h4 class="mb-3">随身 SSH 终端 — 手机管理服务器</h4>
				<p class="mb-4">开源免费的 Android SSH 客户端，支持密码/私钥认证、交互式 Shell、常用命令，随时随地安全连接您的远程服务器。</p>
				<a href="https://www.weiyuai.cn/download/termshell-android.apk" class="btn btn-primary" target="_blank">📱 立即下载 Android 版</a>
			</div>
			<div class="col-md-7">
				<img src="/assets/images/element/05.svg" alt="Termshell SSH 终端客户端">
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
				<p class="mb-0">专为运维人员打造，让您在手机上像桌面终端一样操作服务器</p>
			</div>
		</div>

		<div class="row g-4">

			<!-- 安全连接 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/coding.svg" alt="SSH 安全连接">
					</div>
					<h5 class="mb-1">🔐 安全连接</h5>
					<span class="mb-0">支持密码认证与 OpenSSH 私钥认证。首次连接 host key 指纹校验，私钥/密码使用平台 Keychain/Keystore 加密存储，杜绝明文泄露。</span>
				</div>
			</div>

			<!-- 交互式终端 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/online.svg" alt="交互式终端">
					</div>
					<h5 class="mb-1">⌨️ 交互式 Shell</h5>
					<span class="mb-0">基于 xterm.js 引擎，完整 PTY 支持。ANSI 颜色、光标控制、Tab 补全、Ctrl+C 中断、方向键历史，完全模拟桌面终端体验。</span>
				</div>
			</div>

			<!-- 后台保活 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/data-science.svg" alt="后台保活">
					</div>
					<h5 class="mb-1">⏱️ 后台保活</h5>
					<span class="mb-0">离开终端页不自动断开，会话在后台保持。再次进入同一主机时恢复屏幕历史。空闲超时（默认 10 分钟）自动安全回收，兼顾便捷与节流。</span>
				</div>
			</div>

			<!-- 多会话 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/engineering.svg" alt="多会话管理">
					</div>
					<h5 class="mb-1">🗂️ 多主机管理</h5>
					<span class="mb-0">同时连接多台服务器，每个会话独立保活。主机列表一目了然，在线状态绿点提示，长按编辑/删除，管理更高效。</span>
				</div>
			</div>

			<!-- 常用命令 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/profit.svg" alt="常用命令">
					</div>
					<h5 class="mb-1">📋 常用命令库</h5>
					<span class="mb-0">保存高频命令，按标题/标签分类。终端底部输入栏实时模糊匹配，选中即发送。一键添加当前命令到库，告别手机端繁琐输入。</span>
				</div>
			</div>

			<!-- 横竖屏切换 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/artist.svg" alt="横竖屏切换">
					</div>
					<h5 class="mb-1">🔄 横竖屏切换</h5>
					<span class="mb-0">右上角一键切换横屏。横屏下终端自动重排行列数，适配 vim/htop/top 等全屏程序，充分利用手机宽屏优势。</span>
				</div>
			</div>

			<!-- 开源免费 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/home.svg" alt="开源免费">
					</div>
					<h5 class="mb-1">🆓 开源 & 隐私</h5>
					<span class="mb-0">纯 Dart 实现，无原生依赖，无第三方服务器中转。您的密码/私钥仅存储在设备本地加密区，绝不离开手机。代码开源，可审计可定制。</span>
				</div>
			</div>

			<!-- 技术栈 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/medical.svg" alt="技术栈">
					</div>
					<h5 class="mb-1">⚙️ 技术栈</h5>
					<span class="mb-0">Flutter 3.x + dartssh2（纯 Dart SSH 协议）+ xterm 4.x（终端渲染）+ GetX（状态管理）+ flutter_secure_storage（安全存储）。轻量高效，启动迅速。</span>
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
				<h2>下载 Termshell</h2>
				<p>当前仅提供 Android 版本。iOS 版本开发中，敬请期待。</p>
				<div class="d-flex gap-3 align-items-center mt-3">
					<a href="https://www.weiyuai.cn/download/termshell-android.apk" class="btn btn-lg btn-primary" target="_blank">
						📱 下载 Android APK
					</a>
				</div>
				<p class="mt-2 small text-muted">版本 1.0.0 · 适用于 Android 5.0+</p>
			</div>

			<div class="col-md-7 text-md-end position-relative">
				<!-- SVG decoration -->
				<figure class="position-absolute top-50 end-0 translate-middle-y me-n8">
					<svg width="632.6px" height="540.4px" viewBox="0 0 632.6 540.4">
						<path class="fill-primary opacity-1" d="M531.4,46.9c46.3,27.4,81.4,79.8,91.1,136.2c9.7,56.8-6.4,117.7-38.3,166s-79.4,84.2-138.6,119.3 c-59.6,35.1-130.6,69.7-201.5,62.1c-70.5-7.7-141.4-57.6-185.4-126.5C14.4,335.5-2.9,247.2,23.7,179.5 c26.2-68.1,96.7-116.5,161.6-140.2c64.9-24.2,124.5-24.6,183.3-23.4C427,17.1,485.1,19.5,531.4,46.9z"/>
					</svg>
				</figure>

				<img src="/assets/images/element/07.svg" class="position-relative" alt="Termshell 下载">
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
				<p class="mb-0">三步开始使用 Termshell</p>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-4">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<div class="d-flex align-items-center mb-3">
						<span class="fs-1 fw-bold text-primary me-3">1</span>
						<h5 class="mb-0">添加主机</h5>
					</div>
					<p class="mb-0">填写服务器地址、端口、用户名，选择密码或私钥认证。私钥/密码加密保存在设备本地。</p>
				</div>
			</div>
			<div class="col-md-4">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<div class="d-flex align-items-center mb-3">
						<span class="fs-1 fw-bold text-primary me-3">2</span>
						<h5 class="mb-0">一键连接</h5>
					</div>
					<p class="mb-0">点击主机立即连接，首次自动校验 host key 指纹。5 秒内进入交互式终端。</p>
				</div>
			</div>
			<div class="col-md-4">
				<div class="bg-primary bg-opacity-10 rounded-3 p-4 h-100">
					<div class="d-flex align-items-center mb-3">
						<span class="fs-1 fw-bold text-primary me-3">3</span>
						<h5 class="mb-0">管理服务器</h5>
					</div>
					<p class="mb-0">执行任何命令，使用常用命令快速输入，切换横屏查看全屏程序。来回切换不中断连接。</p>
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
