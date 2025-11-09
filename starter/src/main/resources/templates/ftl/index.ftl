<!DOCTYPE html>
<html lang="${lang! 'zh-CN'}">
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
				<#include "./common/macro/i18n.ftl" />
				<h2><@t key="section.suite.title">微语开源套件：N合一</@t></h2>
				<p class="mb-0"><@t key="section.suite.desc">开源、免费，私有部署，所有数据存储在您自己的服务器上</@t></p>
			</div>
		</div>

		<div class="row g-4">

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/coding.svg" alt="<@t key='alt.suite.team'>企业IM图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/docs/zh-CN/docs/modules/team" class="stretched-link" target="_blank"><@t key="section.suite.item.team.title">企业IM +</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.team.desc">AI群聊助手，AI会话总结，聊天记录监控。按照中大型团队架构使用规模设计，支持数万人同时在线</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/data-science.svg" alt="<@t key='alt.suite.service'>在线客服图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/docs/zh-CN/docs/modules/service" class="stretched-link" target="_blank"><@t key="section.suite.item.service.title">在线客服</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.service.desc">多渠道对接、多账号聚合，私域管理，AI意图识别，AI智能质检、支持人工坐席兜底，数十项在线客服通用功能。
						来自<a href="http://www.weikefu.net" target="_blank">萝卜丝智能客服(原微客服)</a>.
					</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/online.svg" alt="<@t key='alt.suite.ai'>AI Agent图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/docs/zh-CN/docs/modules/ai" class="stretched-link" target="_blank"><@t key="section.suite.item.ai.title">AI Agent</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.ai.desc">对接Ollama/DeepSeek/智谱/Qwen通义千问等大模型，支持私有部署/Api调用大模型.</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/engineering.svg" alt="<@t key='alt.suite.kbase'>企业知识库图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/docs/zh-CN/docs/modules/kbase" class="stretched-link" target="_blank"><@t key="section.suite.item.kbase.title">企业知识库/帮助中心</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.kbase.desc">内部客服中心，AI写作助手，AI知识库问答，自动撰写内容，内部知识库管理、文档管理、对外知识库、帮助文档、内容公告</@t></span>
				</div>
			</div>

			
			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/profit.svg" alt="<@t key='alt.suite.voc'>客户之声图标</@t>">
					</div>
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/docs/zh-CN/docs/modules/voc" class="stretched-link" target="_blank">客户之声</a></h5>
					<span class="mb-0"><@t key="section.suite.item.voc.desc">AI回复助手，社交媒体评论抓取、第三方平台评论同步、意见反馈、服务投诉、调查问卷</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/medical.svg" alt="<@t key='alt.suite.ticket'>工单系统图标</@t>">
					</div>
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/docs/zh-CN/docs/modules/ticket" class="stretched-link" target="_blank">工单系统</a></h5>
					<span class="mb-0"><@t key="section.suite.item.ticket.desc">AI工单助手，自动创建工单，自动分配工单，自动关闭工单</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/artist.svg" alt="<@t key='alt.suite.workflow'>工作流图标</@t>">
					</div>
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/docs/zh-CN/docs/modules/workflow" class="stretched-link" target="_blank">工作流</a></h5>
					<span class="mb-0"><@t key="section.suite.item.workflow.desc">AI智能体，自动执行数据操作，自定义工作流、流程编排，Function Calling/Mcp</@t></span>
				</div>
			</div>

			<!-- Item -->
			<#--  <div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/home.svg" alt="项目管理图标">
					</div>
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/docs/zh-CN/docs/modules/kanban" class="stretched-link" target="_blank">项目管理</a></h5>
					<span class="mb-0">看板、日报、任务管理</span>
				</div>
			</div>  -->

			<!-- Item -->
			<#--  <div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/coding.svg" alt="插件模块图标">
					</div>
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/docs/zh-CN/docs/category/plugins" class="stretched_link" target="_blank">插件模块</a></h5>
					<span class="mb-0">扩展能力，支持第三方插件开发和集成，自定义业务流程、API对接，插件商店，一键安装</span>
				</div>
			</div>  -->

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/data-science.svg" alt="<@t key='alt.suite.call'>呼叫中心图标</@t>">
					</div>
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/docs/zh-CN/docs/plugins/freeswitch" class="stretched_link" target="_blank">呼叫中心</a></h5>
					<span class="mb-0"><@t key="section.suite.item.call.desc">AI智能外呼，自动语音识别，智能语音合成，多线路接入，通话录音，质检分析，支持FreeSWITCH</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/online.svg" alt="<@t key='alt.suite.video'>视频客服图标</@t>">
					</div>
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/docs/zh-CN/docs/plugins/webrtc" class="stretched_link" target="_blank">视频客服</a></h5>
					<span class="mb-0"><@t key="section.suite.item.video.desc">WebRTC视频通话，屏幕共享，远程协助，视频录制，AI视频分析，支持移动端和PC端</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/profit.svg" alt="<@t key='alt.suite.scrm'>客户管理Scrm图标</@t>">
					</div>
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/scrm.html" class="stretched_link" target="_blank">客户管理Scrm</a></h5>
					<span class="mb-0"><@t key="section.suite.item.scrm.desc">AI智能客户画像，客户生命周期管理，营销自动化，销售漏斗分析，多渠道客户数据统一管理</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="assets/images/element/engineering.svg" alt="<@t key='alt.suite.open'>开放平台图标</@t>">
					</div>
					<h5 class="mb-1"><a href="https://www.weiyuai.cn/docs/zh-CN/docs/modules/open" class="stretched_link" target="_blank">开放平台</a></h5>
					<span class="mb-0"><@t key="section.suite.item.open.desc">API接口开放，第三方集成，开发者工具，SDK支持，插件生态，开放API文档</@t></span>
				</div>
			</div>
			
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
				<h2><@t key="section.custom.title">支持定制</@t></h2>
				<p><@t key="section.custom.desc">如果您有定制需求或其他合作事宜，请与我们联系.</@t></p>
				<p><@t key="section.custom.wechat">添加微信请备注：微语</@t></p>
				<!-- Download button -->
				<div class="row">
					<!-- Google play store button -->
					<div class="col-6 col-sm-4 col-md-6 col-lg-4">
						<a href="mailto:${(i18n['section.contact.email'])!'270580156@qq.com'}">${(i18n['section.contact.email'])!'270580156@qq.com'}</a>
					</div>
					<!-- App store button -->
					<div class="col-6 col-sm-4 col-md-6 col-lg-4">
						<#--  <a href="#"><img src="assets/images/element/app-store.svg" class="btn-transition" alt="app-store"></a>  -->
						<a href="/assets/images/qrcode/wechat.png" target="_blank">
							<img src="/assets/images/qrcode/wechat.png" alt="微语微信联系二维码"/>
						</a>
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
				<img src="assets/images/element/07.svg" class="position-relative" alt="微语定制服务插图">
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