<#include "./macro/i18n.ftl" />
<#-- Language prefix for links -->
<#assign langPrefix = "">
<#if lang?? && lang != "zh-CN">
	<#assign langPrefix = "/" + lang>
</#if>
<footer class="bg-dark text-white pt-5">
	<div class="container">
		<!-- Row START -->
		<div class="row g-4 justify-content-between">

			<!-- Widget 1 START -->
			<div class="col-md-6 col-lg-4">
				<!-- logo -->
				<a class="me-0 text-white" href="index.html" target="_blank">
					<#--  <img class="light-mode-item h-40px" src="/assets/images/logo.svg" alt="logo">  -->
					<#--  <img class="dark-mode-item h-40px" src="/assets/images/logo-light.svg" alt="logo">  -->
				</a>

				<p class="my-4"><h3 class="text-white">${(i18n['brand.title'])! '微语 - 重复工作自动化'}</h3></p>
				<p><h5 class="text-white-50">${(i18n['brand.subtitle'])! '开源办公软件'}</h5></p>
				<#--  开源即时通讯平台、开源智能客服系统、开源AI应用创新平台  -->
				<!-- Newsletter -->
				<#--  <form class="row row-cols-lg-auto g-2">
					<div class="col-12">
						<input type="email" class="form-control" placeholder="Enter your email address">
					</div>
					<div class="col-12">
						<button type="submit" class="btn btn-dark m-0">Subscribe</button>
					</div>
				</form>  -->
				
				<!-- 联系方式 -->
				<div class="mt-3">
					<h5 class="text-white"><@t key="footer.contactus">联系我们</@t></h5>
					<p><a href="mailto:${(i18n['section.contact.email'])!'270580156@qq.com'}" class="text-white-50" target="_blank">${(i18n['section.contact.email'])!'270580156@qq.com'}</a></p>
					<p class="text-white-50"><@t key="section.custom.wechat">微信咨询请备注：微语</@t></p>
					<a href="/assets/images/qrcode/wechat.png" target="_blank">
						<img src="/assets/images/qrcode/wechat.png" style="height: 80px;" alt="微语微信联系方式二维码"/>
					</a>
				</div>
			</div>
			<!-- Widget 1 END -->

			<!-- Widget 2 START -->
			<div class="col-md-6 col-lg-2">
				<h5 class="mb-3 text-white"><@t key="footer.quicklinks">快速链接</@t></h5>
				<ul class="nav flex-column">
					<li class="nav-item"><a class="nav-link text-white-50" href="https://www.weiyuai.cn/docs/zh-CN/" target="_blank"><@t key="nav.docs">文档</@t></a></li>
					<li class="nav-item"><a class="nav-link text-white-50" href="https://www.weiyuai.cn/blog/df_org_uid_df_kb_bg_uid/index.html" target="_blank"><@t key="nav.blog">博客</@t></a></li>
					<li class="nav-item"><a class="nav-link text-white-50" href="https://www.weiyuai.cn/helpcenter/df_org_uid_df_kb_hc_uid/index.html" target="_blank"><@t key="nav.helpcenter">帮助中心</@t></a></li>
					<li class="nav-item"><a class="nav-link text-white-50" href="${langPrefix}/pages/download.html"><@t key="nav.download">下载</@t></a></li>
					<li class="nav-item"><a class="nav-link text-white-50" href="${langPrefix}/pages/about.html"><@t key="nav.about">关于</@t></a></li>
					<li class="nav-item"><a class="nav-link text-white-50" href="${langPrefix}/pages/contact.html"><@t key="nav.contact">联系</@t></a></li>
					<li class="nav-item"><a class="nav-link text-white-50" href="https://github.com/Bytedesk/bytedesk" target="_blank"><@t key="nav.github">Github</@t></a></li>
				</ul>
				
				<!-- 微信公众号 -->
				<div class="row mt-4">
					<div class="col-6">
						<h6 class="mb-2 text-white"><@t key="footer.serviceAccount">服务号</@t></h6>
						<a href="/assets/images/qrcode/weiyu/qrcode_1280.jpg" target="_blank">
							<img src="/assets/images/qrcode/weiyu/qrcode_1280.jpg" style="height: 80px;" alt="微语服务号"/>
						</a>
					</div>
					<div class="col-6">
						<h6 class="mb-2 text-white"><@t key="footer.subscribeAccount">订阅号</@t></h6>
						<a href="/assets/images/qrcode/weiyuai/qrcode_1280.jpg" target="_blank">
							<img src="/assets/images/qrcode/weiyuai/qrcode_1280.jpg" style="height: 80px;" alt="微语订阅号"/>
						</a>
					</div>
				</div>
			</div>
			
			<!-- Widget 3 START - 微语开源套件 -->
			<div class="col-md-12 col-lg-6">
				<h5 class="mb-3 text-white"><@t key="footer.suite">微语开源套件</@t></h5>
				<div class="row g-3">
					<!-- 第一行 -->
					<div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/team.html" class="text-white-50 d-inline-block mb-1"><@t key="suite.team">企业IM</@t></a>
					</div>
					<div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/service.html" class="text-white-50 d-inline-block mb-1"><@t key="suite.service">在线客服</@t></a>
					</div>
					<div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/ai.html" class="text-white-50 d-inline-block mb-1"><@t key="suite.ai">AI Agent</@t></a>
					</div>
					<div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/kbase.html" class="text-white-50 d-inline-block mb-1"><@t key="suite.kbase">企业知识库</@t></a>
					</div>

					<!-- 第二行 -->
					<div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/voc.html" class="text-white-50 d-inline-block mb-1"><@t key="suite.voc">客户之声</@t></a>
					</div>
					<div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/ticket.html" class="text-white-50 d-inline-block mb-1"><@t key="suite.ticket">工单系统</@t></a>
					</div>
					<div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/workflow.html" class="text-white-50 d-inline-block mb-1"><@t key="suite.workflow">工作流</@t></a>
					</div>
					<#--  <div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/kanban.html" class="text-white-50 d-inline-block mb-1">项目管理</a>
					</div>  -->

					<!-- 第三行 -->
					<#--  <div class="col-6 col-lg-3">
						<a href="https://www.weiyuai.cn/docs/zh-CN/docs/category/plugins" class="text-white-50 d-inline-block mb-1" target="_blank">插件模块</a>
					</div>  -->
					<div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/callcenter.html" class="text-white-50 d-inline-block mb-1"><@t key="suite.callcenter">呼叫中心</@t></a>
					</div>
					<div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/video.html" class="text-white-50 d-inline-block mb-1"><@t key="suite.videocall">视频会议</@t></a>
					</div>
					<div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/scrm.html" class="text-white-50 d-inline-block mb-1"><@t key="suite.scrm">客户管理</@t></a>
					</div>

					<#--  第四行  -->
					<div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/open.html" class="text-white-50 d-inline-block mb-1"><@t key="suite.open">开放平台</@t></a>
					</div>
					<div class="col-6 col-lg-3">
						<a href="${langPrefix}/features/office.html" class="text-white-50 d-inline-block mb-1"><@t key="suite.office">AI文档</@t></a>
					</div>
				</div>
			</div>
		</div><!-- Row END -->

		<hr class="border-secondary"> <!-- Divider -->

		<!-- Bottom footer -->
		<div class="row">
			<div class="col-12">
				<div class="d-md-flex justify-content-between align-items-center pt-2 pb-4 text-center">
					<!-- copyright text -->
					<div class="text-white-50"> ${(i18n['footercopyright'])! 'Copyrights ©2013~2025 北京微语天下科技有限公司'}</div>
					<!-- copyright links-->
					<div class="nav justify-content-center mt-3 mt-md-0">
						<ul class="list-inline mb-0">
							<li class="list-inline-item"><a class="nav-link text-white-50" href="${langPrefix}/pages/terms.html"><@t key="footer.agreement">用户协议</@t></a></li>
							<li class="list-inline-item"><a class="nav-link text-white-50" href="${langPrefix}/pages/privacy.html"><@t key="footer.privacy">隐私条款</@t></a></li>
							<li class="list-inline-item"><a class="nav-link text-white-50" href="https://beian.miit.gov.cn/" target="_blank">京ICP备17041763号-20</a></li>
							<li class="list-inline-item"><a class="nav-link text-white-50" href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=44030502008688" target="_blank">粤公网安备 44030502008688号</a></li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</footer>