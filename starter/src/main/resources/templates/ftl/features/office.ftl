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
				<h1><@t key="page.office.title">微语文档</@t></h1>
				<p class="lead"><@t key="page.office.subtitle">像使用微软 Office 一样编辑文档，像拥有秘书一样使用 AI</@t></p>
				<div class="d-flex flex-wrap gap-2 mt-4">
					<a href="${docBaseUrl}docs/office/intro" class="btn btn-primary" target="_blank">
						<i class="bi bi-box-arrow-up-right me-1"></i><@t key="page.office.cta.docs">查看文档</@t>
					</a>
				</div>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="/assets/images/element/09.svg" class="h-200px" alt="<@t key='alt.office.icon'>微语文档插图</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Page Banner END -->

<!-- =======================
Highlights START -->
<section class="position-relative pb-0 pb-sm-5">
	<div class="container">
		<!-- Title -->
		<div class="row mb-5">
			<div class="col-lg-10 mx-auto text-center">
				<h1 class="display-5 fw-bold mb-4"><@t key="page.office.title">微语文档</@t></h1>
				<p class="lead mb-4"><@t key="page.office.subtitle">像使用微软 Office 一样编辑文档，像拥有秘书一样使用 AI</@t></p>
			</div>
		</div>

		<!-- Feature highlights -->
		<div class="row g-4 mb-5">
			<div class="col-md-4 text-center">
				<div class="icon-xl bg-primary bg-opacity-10 rounded-circle mx-auto mb-3">
					<i class="bi bi-pencil-square text-primary fs-2"></i>
				</div>
				<h5><@t key="page.office.highlight.edit.title">熟悉的编辑体验</@t></h5>
				<p><@t key="page.office.highlight.edit.desc">创建、编辑、保存都和平时一样，五类常用文档装进同一个工作台</@t></p>
			</div>
			<div class="col-md-4 text-center">
				<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
					<i class="bi bi-robot text-success fs-2"></i>
				</div>
				<h5><@t key="page.office.highlight.ai.title">AI 助手随叫随到</@t></h5>
				<p><@t key="page.office.highlight.ai.desc">一句话从零起草初稿、润色文字、美化排版，批量修改统一处理</@t></p>
			</div>
			<div class="col-md-4 text-center">
				<div class="icon-xl bg-warning bg-opacity-10 rounded-circle mx-auto mb-3">
					<i class="bi bi-arrow-repeat text-warning fs-2"></i>
				</div>
				<h5><@t key="page.office.highlight.compat.title">Office / WPS 兼容</@t></h5>
				<p><@t key="page.office.highlight.compat.desc">直接使用微软 Office 真实格式，不经过中间转换，格式不会乱</@t></p>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Highlights END -->

<!-- =======================
Supported Types START -->
<section id="features" class="bg-body-tertiary py-5">
	<div class="container">
		<!-- Title -->
		<div class="row mb-5">
			<div class="col-lg-8 mx-auto text-center">
				<h2 class="fw-bold"><@t key="page.office.types.title">支持的文档类型</@t></h2>
				<p class="mb-0"><@t key="page.office.types.desc">文字文档、电子表格、演示文稿、PDF 和 Markdown，五类常用文档装进同一个工作台</@t></p>
			</div>
		</div>

		<div class="row g-4">

			<!-- Word -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-primary bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-file-earmark-word text-primary fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.type.word.title">文字文档 Word</@t></h5>
					<p class="mb-0"><@t key="page.office.type.word.desc">撰写通知、方案、报告、周报，支持样式、目录、图表、公式、修订与批注</@t></p>
				</div>
			</div>

			<!-- Excel -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-file-earmark-excel text-success fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.type.excel.title">电子表格 Excel</@t></h5>
					<p class="mb-0"><@t key="page.office.type.excel.desc">数据记录与计算，支持公式、图表、数据透视表、条件格式</@t></p>
				</div>
			</div>

			<!-- PowerPoint -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-warning bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-file-earmark-ppt text-warning fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.type.ppt.title">演示文稿 PowerPoint</@t></h5>
					<p class="mb-0"><@t key="page.office.type.ppt.desc">制作汇报、培训、宣讲 PPT，支持母版、版式、智能参考线</@t></p>
				</div>
			</div>

			<!-- PDF -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-danger bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-file-earmark-pdf text-danger fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.type.pdf.title">PDF 编辑</@t></h5>
					<p class="mb-0"><@t key="page.office.type.pdf.desc">直接修改文字与图片、添加批注、填写表单、页面增删，还能把 PDF 转成 Word/Excel/PPT</@t></p>
				</div>
			</div>

			<!-- Markdown -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-info bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-markdown text-info fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.type.markdown.title">Markdown</@t></h5>
					<p class="mb-0"><@t key="page.office.type.markdown.desc">轻量笔记与文档，适合写说明、README 等</@t></p>
				</div>
			</div>

		</div>
	</div>
</section>
<!-- =======================
Supported Types END -->

<!-- =======================
AI Assistant START -->
<section class="py-5">
	<div class="container">
		<!-- Title -->
		<div class="row mb-5">
			<div class="col-lg-8 mx-auto text-center">
				<h2 class="fw-bold"><@t key="page.office.ai.title">AI 助手能帮您做什么</@t></h2>
				<p class="mb-0"><@t key="page.office.ai.desc">打开文档侧边的 AI 助手面板，用大白话下达指令即可</@t></p>
			</div>
		</div>

		<div class="row g-4">
			<!-- 从零起草 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-primary bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-pencil-square text-primary fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.ai.draft.title">从零起草</@t></h5>
					<p class="mb-0"><@t key="page.office.ai.draft.desc">说一句"帮我写一份项目周报""列一个活动策划提纲"，AI 直接为您写出初稿</@t></p>
				</div>
			</div>

			<!-- 续写与填模板 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-input-cursor-text text-success fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.ai.continue.title">续写与填模板</@t></h5>
					<p class="mb-0"><@t key="page.office.ai.continue.desc">接着现有内容往下写；自动找出文档里的占位符并填写</@t></p>
				</div>
			</div>

			<!-- 润色全文 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-warning bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-stars text-warning fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.ai.polish.title">润色全文</@t></h5>
					<p class="mb-0"><@t key="page.office.ai.polish.desc">使语气更专业、表达更清晰流畅</@t></p>
				</div>
			</div>

			<!-- AI 排版 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-info bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-sliders text-info fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.ai.format.title">AI 排版</@t></h5>
					<p class="mb-0"><@t key="page.office.ai.format.desc">自动修正标题层级、统一列表格式、去除多余的加粗和斜体、补齐首行缩进——只调整格式，不改动任何文字内容</@t></p>
				</div>
			</div>

			<!-- 修改选中内容 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-danger bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-textarea-t text-danger fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.ai.selection.title">修改选中内容</@t></h5>
					<p class="mb-0"><@t key="page.office.ai.selection.desc">选中一段文字后，一键润色、精简、扩写、修正语法和错别字</@t></p>
				</div>
			</div>

			<!-- 批量修改 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-secondary bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-list-check text-secondary fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.ai.batch.title">批量修改</@t></h5>
					<p class="mb-0"><@t key="page.office.ai.batch.desc">把多处修改意见加入队列，一次发送，AI 统一处理</@t></p>
				</div>
			</div>

			<!-- 总结要点 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-primary bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-card-text text-primary fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.ai.summarize.title">总结要点</@t></h5>
					<p class="mb-0"><@t key="page.office.ai.summarize.desc">长文档一键总结；支持生成配图、联网搜索、图片搜索</@t></p>
				</div>
			</div>

			<!-- 修订追踪 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-file-diff text-success fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.ai.track.title">修订追踪</@t></h5>
					<p class="mb-0"><@t key="page.office.ai.track.desc">AI 的改动以"修订"形式标记，可以在审阅中逐一接受或拒绝</@t></p>
				</div>
			</div>

			<!-- 版本快照 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-warning bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-clock-history text-warning fs-2"></i>
					</div>
					<h5 class="mb-3"><@t key="page.office.ai.snapshot.title">版本快照</@t></h5>
					<p class="mb-0"><@t key="page.office.ai.snapshot.desc">每轮 AI 修改前自动保存快照，不满意可一键回滚</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
AI Assistant END -->

<!-- =======================
Use Cases START -->
<section class="py-5">
	<div class="container">
		<div class="row mb-5">
			<div class="col-lg-8 mx-auto text-center">
				<h2 class="fw-bold"><@t key="page.office.usecases.title">应用场景</@t></h2>
				<p class="mb-0"><@t key="page.office.usecases.desc">适用于各种文档创作和编辑需求</@t></p>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-6 col-lg-3">
				<div class="text-center p-4">
					<div class="icon-xl bg-primary bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-briefcase text-primary fs-2"></i>
					</div>
					<h5><@t key="page.office.usecase.business.title">商务办公</@t></h5>
					<p class="mb-0"><@t key="page.office.usecase.business.desc">报告撰写、合同起草、会议纪要整理</@t></p>
				</div>
			</div>
			<div class="col-md-6 col-lg-3">
				<div class="text-center p-4">
					<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-mortarboard text-success fs-2"></i>
					</div>
					<h5><@t key="page.office.usecase.research.title">学术研究</@t></h5>
					<p class="mb-0"><@t key="page.office.usecase.research.desc">论文写作、文献整理、学术报告</@t></p>
				</div>
			</div>
			<div class="col-md-6 col-lg-3">
				<div class="text-center p-4">
					<div class="icon-xl bg-warning bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-newspaper text-warning fs-2"></i>
					</div>
					<h5><@t key="page.office.usecase.content.title">内容创作</@t></h5>
					<p class="mb-0"><@t key="page.office.usecase.content.desc">文章写作、新闻稿件、营销文案</@t></p>
				</div>
			</div>
			<div class="col-md-6 col-lg-3">
				<div class="text-center p-4">
					<div class="icon-xl bg-info bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="bi bi-easel text-info fs-2"></i>
					</div>
					<h5><@t key="page.office.usecase.education.title">教育培训</@t></h5>
					<p class="mb-0"><@t key="page.office.usecase.education.desc">课件制作、教案编写、学习笔记</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>

<!-- =======================
Office Compat START -->
<section id="compat" class="bg-body-tertiary py-5">
	<div class="container">
		<div class="row g-4 align-items-center">
			<div class="col-lg-6">
				<h2 class="fw-bold mb-4"><@t key="page.office.compat.title">和微软 Office / WPS 兼容吗</@t></h2>
				<p class="mb-4"><@t key="page.office.compat.desc">兼容。微语文档直接使用微软 Office 的真实格式（.docx/.xlsx/.pptx），不经过中间转换</@t></p>

				<ul class="list-group list-group-borderless">
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.office.compat.real">用 Word/Excel/PowerPoint 打开微语文档编辑过的文件，格式不会乱</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.office.compat.incremental">改了什么就只重写什么，未改动的内容原样保留</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.office.compat.pagination">分页位置与 Word 保持一致，所见即所得</@t>
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<@t key="page.office.compat.wps">在 WPS 中编辑保存的文档，也可以直接打开并继续编辑</@t>
					</li>
				</ul>
			</div>

			<div class="col-lg-6 text-center">
				<img src="/assets/images/element/08.svg" class="img-fluid" alt="<@t key='alt.office.compat'>微语文档兼容性插图</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Office Compat END -->

<!-- =======================
FAQ START -->
<section class="py-5">
	<div class="container">
		<div class="row mb-5">
			<div class="col-lg-8 mx-auto text-center">
				<h2 class="fw-bold"><@t key="page.office.faq.title">常见问题</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-6">
				<div class="border rounded-3 p-4 h-100">
					<h5><@t key="page.office.faq.ai.q">AI 功能需要额外配置吗？</@t></h5>
					<p class="mb-0"><@t key="page.office.faq.ai.a">需要在 AI 设置中配置大模型服务。支持主流大模型（OpenAI、Claude、Gemini、DeepSeek、Kimi、GLM、通义千问等），也支持自定义 OpenAI 兼容接口接入</@t></p>
				</div>
			</div>

			<div class="col-md-6">
				<div class="border rounded-3 p-4 h-100">
					<h5><@t key="page.office.faq.cloud.q">我的文档会上传到云端吗？</@t></h5>
					<p class="mb-0"><@t key="page.office.faq.cloud.a">文档保存在本地，或同步到您私有部署的微语服务器；只有使用 AI 功能时，相关内容才会发送给您配置的大模型服务</@t></p>
				</div>
			</div>

			<div class="col-md-6">
				<div class="border rounded-3 p-4 h-100">
					<h5><@t key="page.office.faq.ocr.q">PDF 转换支持扫描件吗？</@t></h5>
					<p class="mb-0"><@t key="page.office.faq.ocr.a">支持。在 macOS 和 Windows 上可通过系统 OCR 识别扫描件，并转换为可编辑的 Word/Excel/PPT</@t></p>
				</div>
			</div>

			<div class="col-md-6">
				<div class="border rounded-3 p-4 h-100">
					<h5><@t key="page.office.faq.platform.q">有哪些平台？</@t></h5>
					<p class="mb-0"><@t key="page.office.faq.platform.a">提供 macOS、Windows、Linux 桌面客户端，支持浅色/深色主题</@t></p>
				</div>
			</div>
		</div>

		<!-- Tips -->
		<div class="row mt-4">
			<div class="col-12">
				<div class="alert alert-info d-flex align-items-start" role="alert">
					<i class="bi bi-info-circle-fill me-2 mt-1"></i>
					<div><@t key="page.office.tip">小贴士：AI 修改前会自动生成版本快照，大胆尝试也不用担心改坏文档；开启"修订追踪"后，AI 的所有改动都会留痕</@t></div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
FAQ END -->

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
