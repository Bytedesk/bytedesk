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
<section class="position-relative pt-4 pt-sm-5 pb-0 pb-sm-5">
	<div class="container">
		<!-- Title -->
		<div class="row mb-4">
			<div class="col-lg-8 mx-auto text-center">
				<#include "./common/macro/i18n.ftl" />
				<h2><@t key="section.suite.title">做智能客服界的"张雪机车"</@t></h2>
				<p class="mb-0"><@t key="section.suite.desc">开源、免费，私有部署，所有数据存储在您自己的服务器上</@t></p>
			</div>
		</div>

		<div class="row g-4">

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/coding.svg" alt="<@t key='alt.suite.team'>企业IM图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="/features/team.html" class="stretched-link"><@t key="section.suite.item.team.title">企业IM +</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.team.desc">AI群聊助手、AI会话总结、聊天记录监控，为中大型团队设计，支持数万人同时在线</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/data-science.svg" alt="<@t key='alt.suite.service'>在线客服图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="/features/service.html" class="stretched-link"><@t key="section.suite.item.service.title">在线客服</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.service.desc">多渠道对接、多账号聚合、私域管理，AI意图识别与智能质检，支持人工坐席兜底。来自<a href="http://www.weikefu.net" target="_blank">萝卜丝智能客服</a></@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/online.svg" alt="<@t key='alt.suite.ai'>AI Agent图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="/features/ai.html" class="stretched-link"><@t key="section.suite.item.ai.title">AI Agent</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.ai.desc">对接Ollama/DeepSeek/智谱/通义千问等大模型，支持私有部署与API调用</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/engineering.svg" alt="<@t key='alt.suite.kbase'>企业知识库图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="/features/kbase.html" class="stretched-link"><@t key="section.suite.item.kbase.title">企业知识库</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.kbase.desc">AI写作助手、AI知识库问答，内部知识库、文档管理、帮助文档、内容公告一站管理</@t></span>
				</div>
			</div>

			<!-- Item: 微语文档 -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<!-- Image -->
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/abc.svg" alt="<@t key='alt.suite.office'>微语文档图标</@t>">
					</div>
					<!-- Title -->
					<h5 class="mb-1"><a href="/features/office.html" class="stretched-link"><@t key="section.suite.item.office.title">微语文档</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.office.desc">Word、Excel、PPT、PDF、Markdown 五类文档 + AI 助手，起草、润色、排版一句话完成</@t></span>
				</div>
			</div>

			
			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/profit.svg" alt="<@t key='alt.suite.voc'>客户之声图标</@t>">
					</div>
					<h5 class="mb-1"><a href="/features/voc.html" class="stretched-link"><@t key="section.suite.item.voc.title">客户之声</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.voc.desc">AI回复助手，社交媒体评论抓取、第三方评论同步、意见反馈、投诉建议、调查问卷</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/medical.svg" alt="<@t key='alt.suite.ticket'>工单系统图标</@t>">
					</div>
					<h5 class="mb-1"><a href="/features/ticket.html" class="stretched-link"><@t key="section.suite.item.ticket.title">工单系统</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.ticket.desc">AI工单助手：自动创建、智能分配流转、超时提醒、自动关闭，支持多部门协同</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/artist.svg" alt="<@t key='alt.suite.workflow'>工作流图标</@t>">
					</div>
					<h5 class="mb-1"><a href="/features/workflow.html" class="stretched-link"><@t key="section.suite.item.workflow.title">工作流</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.workflow.desc">可视化流程编排与自定义工作流，AI智能体自动执行数据操作，支持MCP工具调用</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/home.svg" alt="<@t key='alt.suite.kanban'>任务管理图标</@t>">
					</div>
					<h5 class="mb-1"><a href="/features/kanban.html" class="stretched-link"><@t key="section.suite.item.kanban.title">任务管理</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.kanban.desc">看板任务管理、日历与待办事项，支持团队协作、进度跟踪与到期提醒</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/data-science.svg" alt="<@t key='alt.suite.call'>呼叫中心图标</@t>">
					</div>
					<h5 class="mb-1"><a href="/features/callcenter.html" class="stretched-link"><@t key="section.suite.item.call.title">呼叫中心</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.call.desc">AI智能外呼、语音识别与合成，多线路接入、通话录音、质检分析，支持FreeSWITCH</@t></span>
				</div>
			</div>

			<!-- Item: 微语会议（已合并视频会议/视频客服） -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/contact.svg" alt="<@t key='alt.suite.meet'>微语会议图标</@t>">
					</div>
					<h5 class="mb-1"><a href="/features/meet.html" class="stretched-link"><@t key="section.suite.item.meet.title">微语会议</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.meet.desc">音视频会议、屏幕共享、视频客服、远程协助、参会记录，浏览器直接开会免安装</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/profit.svg" alt="<@t key='alt.suite.scrm'>客户管理图标</@t>">
					</div>
					<h5 class="mb-1"><a href="/features/scrm.html" class="stretched-link"><@t key="section.suite.item.scrm.title">客户管理</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.scrm.desc">AI客户画像、生命周期管理、营销自动化、销售漏斗分析，多渠道数据统一管理</@t></span>
				</div>
			</div>

			<!-- Item -->
			<div class="col-sm-6 col-md-4 col-xl-3">
				<div class="bg-primary bg-opacity-10 rounded-3 text-center p-3 h-100 position-relative btn-transition">
					<div class="icon-xl bg-body mx-auto rounded-circle mb-3">
						<img src="/assets/images/element/engineering.svg" alt="<@t key='alt.suite.open'>开放平台图标</@t>">
					</div>
					<h5 class="mb-1"><a href="/features/open.html" class="stretched-link"><@t key="section.suite.item.open.title">开放平台</@t></a></h5>
					<span class="mb-0"><@t key="section.suite.item.open.desc">开放API接口，第三方集成，开发者工具与SDK，插件生态与API文档</@t></span>
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
				<p><@t key="section.custom.wechat">微信咨询请备注：微语</@t></p>
				<!-- Download button -->
				<div class="row">
					<!-- Google play store button -->
					<div class="col-6 col-sm-4 col-md-6 col-lg-4">
						<a href="mailto:${(i18n['section.contact.email'])!'270580156@qq.com'}">${(i18n['section.contact.email'])!'270580156@qq.com'}</a>
					</div>
					<!-- App store button -->
					<div class="col-6 col-sm-4 col-md-6 col-lg-4">
						<#--  <a href="#"><img src="/assets/images/element/app-store.svg" class="btn-transition" alt="app-store"></a>  -->
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
				<img src="/assets/images/element/07.svg" class="position-relative" alt="微语定制服务插图">
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