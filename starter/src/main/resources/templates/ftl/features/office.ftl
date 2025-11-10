<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<#--  Header  -->
	<#include "../common/meta_office.ftl" />
	<#include "../common/header_js.ftl" />
	<#include "../common/header_css.ftl" />
	
</head>

<body>

<#--  导航  -->
<#include "../common/header_nav.ftl" />

<!-- **************** MAIN CONTENT START **************** -->
<main>

<#include "../common/banner.ftl" />

<!-- =======================
Hero Section START -->
<section class="position-relative pb-0 pb-sm-5">
	<div class="container">
		<!-- Title -->
		<div class="row mb-5">
			<div class="col-lg-10 mx-auto text-center">
				<h1 class="display-5 fw-bold mb-4">微语AI文档</h1>
				<p class="lead mb-4">革命性的AI驱动文档编辑体验，支持Word、Excel、PPT等多种格式，让您的文档创作更加智能高效</p>
				<div class="d-flex flex-wrap justify-content-center gap-3">
					<a href="#features" class="btn btn-primary btn-lg">立即体验</a>
					<a href="#demo" class="btn btn-outline-primary btn-lg">观看演示</a>
				</div>
			</div>
		</div>

		<!-- Feature highlights -->
		<div class="row g-4 mb-5">
			<div class="col-md-4 text-center">
				<div class="icon-xl bg-primary bg-opacity-10 rounded-circle mx-auto mb-3">
					<i class="fas fa-robot text-primary fa-2x"></i>
				</div>
				<h5>AI智能对话</h5>
				<p>通过自然语言对话，轻松编辑和修改文档内容</p>
			</div>
			<div class="col-md-4 text-center">
				<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
					<i class="fas fa-file-alt text-success fa-2x"></i>
				</div>
				<h5>多格式支持</h5>
				<p>支持DOCX、XLSX、PPTX等主流办公文档格式</p>
			</div>
			<div class="col-md-4 text-center">
				<div class="icon-xl bg-warning bg-opacity-10 rounded-circle mx-auto mb-3">
					<i class="fas fa-microphone text-warning fa-2x"></i>
				</div>
				<h5>语音识别</h5>
				<p>上传语音文件自动转换为文字，提升录入效率</p>
			</div>
		</div>
	</div>
</section>

<!-- =======================
Core Features START -->
<section id="features" class="bg-body-tertiary py-5">
	<div class="container">
		<!-- Title -->
		<div class="row mb-5">
			<div class="col-lg-8 mx-auto text-center">
				<h2 class="fw-bold">核心功能特性</h2>
				<p class="mb-0">融合前沿AI技术，为您提供全方位的文档编辑解决方案</p>
			</div>
		</div>

		<div class="row g-4">

			<!-- AI对话编辑 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-primary bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-comments text-primary fa-2x"></i>
					</div>
					<h5 class="mb-3">AI对话编辑</h5>
					<p class="mb-0">通过自然语言对话方式，指导AI修改文档内容、调整格式、优化结构，让编辑变得如同聊天般简单</p>
				</div>
			</div>

			<!-- 多格式支持 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-file-alt text-success fa-2x"></i>
					</div>
					<h5 class="mb-3">多格式支持</h5>
					<p class="mb-0">完美兼容Microsoft Office格式：Word (.docx)、Excel (.xlsx)、PowerPoint (.pptx)，无缝对接现有工作流程</p>
				</div>
			</div>

			<!-- 语音转文字 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-warning bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-microphone text-warning fa-2x"></i>
					</div>
					<h5 class="mb-3">语音转文字</h5>
					<p class="mb-0">支持多种音频格式上传，高精度语音识别技术，快速将会议录音、采访音频转换为文字内容</p>
				</div>
			</div>

			<!-- 智能文字校对 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-info bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-spell-check text-info fa-2x"></i>
					</div>
					<h5 class="mb-3">智能文档校对</h5>
					<p class="mb-0">专业校对引擎驱动，自动纠正错字和语法错误，智能标点修正与风格统一，全面提升文档专业度</p>
				</div>
			</div>

			<!-- 内容扩展 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-purple bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-expand-arrows-alt fa-2x" style="color: #6f42c1;"></i>
					</div>
					<h5 class="mb-3">智能内容扩展</h5>
					<p class="mb-0">基于上下文理解，智能扩展文章内容，丰富表达层次，让简短的要点变成详细的论述</p>
				</div>
			</div>

			<!-- 文字改写 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-secondary bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-pen-fancy text-secondary fa-2x"></i>
					</div>
					<h5 class="mb-3">智能文字改写</h5>
					<p class="mb-0">多种改写风格选择：正式、轻松、学术、商务等，保持原意的同时优化表达方式</p>
				</div>
			</div>

			<!-- 格式化输出 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-dark bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-align-left text-body fa-2x"></i>
					</div>
					<h5 class="mb-3">智能格式化</h5>
					<p class="mb-0">自动调整段落结构、标题层级、列表格式，生成美观专业的文档布局</p>
				</div>
			</div>

			<!-- 逐段编辑 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-danger bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-edit text-danger fa-2x"></i>
					</div>
					<h5 class="mb-3">精准逐段编辑</h5>
					<p class="mb-0">支持选中特定段落进行针对性修改，保持其他内容不变，实现精确的局部编辑</p>
				</div>
			</div>

			<!-- 样式定制 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-palette text-success fa-2x"></i>
					</div>
					<h5 class="mb-3">样式智能定制</h5>
					<p class="mb-0">通过对话方式调整字体、颜色、间距、对齐方式等样式属性，所见即所得的编辑体验</p>
				</div>
			</div>

		</div>
	</div>
</section>

<!-- =======================
AI 文生文场景 START -->
<section class="py-5">
	<div class="container">
		<!-- Title -->
		<div class="row mb-5">
			<div class="col-lg-8 mx-auto text-center">
				<h2 class="fw-bold">AI文生文场景</h2>
				<p class="mb-0">围绕实际办公写作高频需求，提供从采集到成文的全流程智能能力</p>
			</div>
		</div>

		<div class="row g-4">
			<!-- 会议纪要自动生成 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-primary bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-clipboard-list text-primary fa-2x"></i>
					</div>
					<h5 class="mb-3">会议纪要自动生成</h5>
					<p class="mb-0">语音识别+AI处理，自动提炼要点、分条罗列结论与待办，生成规范会议纪要</p>
				</div>
			</div>

			<!-- 公文格式套用 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-warning bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-file-signature text-warning fa-2x"></i>
					</div>
					<h5 class="mb-3">公文格式套用</h5>
					<p class="mb-0">一键套用红头文件与公文模板，智能校验版式与要素，确保规范与合规</p>
				</div>
			</div>

			<!-- 智能写稿助手 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-feather-alt text-success fa-2x"></i>
					</div>
					<h5 class="mb-3">智能写稿助手</h5>
					<p class="mb-0">基于企业知识库与行业资料，自动生成初稿并给出优化建议，显著提升成文质量与效率</p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
AI 文生文场景 END -->

<!-- =======================
Use Cases START -->
<section class="py-5">
	<div class="container">
		<div class="row mb-5">
			<div class="col-lg-8 mx-auto text-center">
				<h2 class="fw-bold">应用场景</h2>
				<p class="mb-0">适用于各种文档创作和编辑需求</p>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-6 col-lg-3">
				<div class="text-center p-4">
					<div class="icon-xl bg-primary bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-briefcase text-primary fa-2x"></i>
					</div>
					<h5>商务办公</h5>
					<p class="mb-0">报告撰写、合同起草、会议纪要整理</p>
				</div>
			</div>
			<div class="col-md-6 col-lg-3">
				<div class="text-center p-4">
					<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-graduation-cap text-success fa-2x"></i>
					</div>
					<h5>学术研究</h5>
					<p class="mb-0">论文写作、文献整理、学术报告</p>
				</div>
			</div>
			<div class="col-md-6 col-lg-3">
				<div class="text-center p-4">
					<div class="icon-xl bg-warning bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-newspaper text-warning fa-2x"></i>
					</div>
					<h5>内容创作</h5>
					<p class="mb-0">文章写作、新闻稿件、营销文案</p>
				</div>
			</div>
			<div class="col-md-6 col-lg-3">
				<div class="text-center p-4">
					<div class="icon-xl bg-info bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-chalkboard-teacher text-info fa-2x"></i>
					</div>
					<h5>教育培训</h5>
					<p class="mb-0">课件制作、教案编写、学习笔记</p>
				</div>
			</div>
		</div>
	</div>
</section>

<!-- =======================
Demo Section START -->
<section id="demo" class="bg-body-tertiary py-5">
	<div class="container">
		<div class="row g-4 align-items-center">
			<div class="col-lg-6">
				<h2 class="fw-bold mb-4">AI对话编辑演示</h2>
				<p class="mb-4">看看我们的AI是如何通过简单的对话来帮助您编辑文档的：</p>
				
				<div class="bg-body rounded-3 p-4 mb-4 shadow-sm">
					<div class="d-flex align-items-start mb-3">
						<div class="bg-primary rounded-circle p-2 me-3">
							<i class="fas fa-user text-white"></i>
						</div>
						<div class="flex-grow-1">
							<strong>用户:</strong> "请帮我将这段文字改写得更加正式一些"
						</div>
					</div>
					<div class="d-flex align-items-start">
						<div class="bg-success rounded-circle p-2 me-3">
							<i class="fas fa-robot text-white"></i>
						</div>
						<div class="flex-grow-1">
							<strong>AI:</strong> "我已经将您选中的段落改写为更正式的表达方式，保持了原意但使用了更加专业的词汇和句式结构。"
						</div>
					</div>
				</div>

				<div class="d-flex gap-3">
					<a href="#" class="btn btn-primary">开始使用</a>
					<a href="#" class="btn btn-outline-primary">查看更多示例</a>
				</div>
			</div>

			<div class="col-lg-6">
				<div class="position-relative">
					<!-- Placeholder for demo video or image -->
					<div class="bg-gradient-primary rounded-3 p-5 text-center text-white">
						<i class="fas fa-play-circle fa-5x mb-3 opacity-75"></i>
						<h4>观看功能演示</h4>
						<p class="mb-0">点击播放查看完整的编辑流程</p>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

<!-- =======================
Benefits START -->
<section class="py-5">
	<div class="container">
		<div class="row mb-5">
			<div class="col-lg-8 mx-auto text-center">
				<h2 class="fw-bold">为什么选择微语AI文档？</h2>
				<p class="mb-0">基于行业最佳实践，为您提供无与伦比的文档编辑体验</p>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-6 col-lg-4">
				<div class="d-flex align-items-start">
					<div class="flex-shrink-0 bg-primary bg-opacity-10 rounded-circle p-3 me-3">
						<i class="fas fa-lightning-bolt text-primary"></i>
					</div>
					<div>
						<h5>效率提升10倍</h5>
						<p class="mb-0">AI辅助编辑大幅减少重复性工作，让您专注于创意和内容质量</p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-4">
				<div class="d-flex align-items-start">
					<div class="flex-shrink-0 bg-success bg-opacity-10 rounded-circle p-3 me-3">
						<i class="fas fa-shield-alt text-success"></i>
					</div>
					<div>
						<h5>数据安全保障</h5>
						<p class="mb-0">企业级安全防护，文档内容端到端加密，保护您的隐私和商业机密</p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-4">
				<div class="d-flex align-items-start">
					<div class="flex-shrink-0 bg-warning bg-opacity-10 rounded-circle p-3 me-3">
						<i class="fas fa-cogs text-warning"></i>
					</div>
					<div>
						<h5>无缝集成</h5>
						<p class="mb-0">与现有办公软件完美兼容，无需改变工作习惯即可享受AI编辑便利</p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-4">
				<div class="d-flex align-items-start">
					<div class="flex-shrink-0 bg-info bg-opacity-10 rounded-circle p-3 me-3">
						<i class="fas fa-language text-info"></i>
					</div>
					<div>
						<h5>多语言支持</h5>
						<p class="mb-0">支持中文、英文等多种语言的文档编辑和处理</p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-4">
				<div class="d-flex align-items-start">
					<div class="flex-shrink-0 bg-purple bg-opacity-10 rounded-circle p-3 me-3">
						<i class="fas fa-cloud" style="color: #6f42c1;"></i>
					</div>
					<div>
						<h5>云端协作</h5>
						<p class="mb-0">支持团队实时协作编辑，版本控制和评论功能让团队工作更高效</p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-4">
				<div class="d-flex align-items-start">
					<div class="flex-shrink-0 bg-secondary bg-opacity-10 rounded-circle p-3 me-3">
						<i class="fas fa-mobile-alt text-secondary"></i>
					</div>
					<div>
						<h5>跨平台访问</h5>
						<p class="mb-0">支持Web、桌面端、移动端多平台使用，随时随地编辑文档</p>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

<!-- =======================
Pricing START -->
<section class="bg-body-tertiary py-5">
	<div class="container">
		<div class="row mb-5">
			<div class="col-lg-8 mx-auto text-center">
				<h2 class="fw-bold">选择适合您的方案</h2>
				<p class="mb-0">灵活的定价方案，满足个人用户和企业客户的不同需求</p>
			</div>
		</div>

		<div class="row g-4">
			<!-- 免费版 -->
			<div class="col-lg-4">
				<div class="bg-body rounded-3 p-4 h-100 shadow-sm text-center">
					<h4 class="mb-3">免费版</h4>
					<div class="display-6 fw-bold text-primary mb-3">免费试用</div>
					<p class="text-muted mb-4">适合个人用户体验</p>
					<ul class="list-unstyled mb-4">
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>基础AI编辑功能</li>
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>月处理文档10份</li>
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>基础格式支持</li>
						<li class="mb-2"><i class="fas fa-times text-muted me-2"></i>语音转文字</li>
						<li class="mb-2"><i class="fas fa-times text-muted me-2"></i>高级AI功能</li>
					</ul>
					<a href="#" class="btn btn-outline-primary w-100">立即开始</a>
				</div>
			</div>

			<!-- 专业版 -->
			<div class="col-lg-4">
				<div class="bg-body rounded-3 p-4 h-100 shadow border-primary position-relative">
					<div class="position-absolute top-0 start-50 translate-middle">
						<span class="badge bg-primary">推荐</span>
					</div>
					<div class="text-center">
						<h4 class="mb-3">专业版</h4>
						<div class="display-6 fw-bold text-primary mb-3">价格面议</div>
						<p class="text-muted mb-4">适合专业用户和小团队</p>
						<ul class="list-unstyled mb-4">
							<li class="mb-2"><i class="fas fa-check text-success me-2"></i>全部AI编辑功能</li>
							<li class="mb-2"><i class="fas fa-check text-success me-2"></i>无限处理文档</li>
							<li class="mb-2"><i class="fas fa-check text-success me-2"></i>全格式支持</li>
							<li class="mb-2"><i class="fas fa-check text-success me-2"></i>语音转文字</li>
							<li class="mb-2"><i class="fas fa-check text-success me-2"></i>高级AI功能</li>
							<li class="mb-2"><i class="fas fa-check text-success me-2"></i>优先技术支持</li>
						</ul>
						<a href="#" class="btn btn-primary w-100">立即订阅</a>
					</div>
				</div>
			</div>

			<!-- 企业版 -->
			<div class="col-lg-4">
				<div class="bg-body rounded-3 p-4 h-100 shadow-sm text-center">
					<h4 class="mb-3">企业版</h4>
					<div class="display-6 fw-bold text-primary mb-3">定制</div>
					<p class="text-muted mb-4">适合大型企业和机构</p>
					<ul class="list-unstyled mb-4">
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>专业版全部功能</li>
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>私有化部署</li>
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>定制开发</li>
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>专属客服</li>
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>SLA保障</li>
					</ul>
					<a href="mailto:270580156@qq.com" class="btn btn-outline-primary w-100">联系销售</a>
				</div>
			</div>
		</div>
	</div>
</section>

<!-- =======================
CTA Section START -->
<section class="overflow-hidden py-5">
	<div class="container">
		<div class="row g-4 align-items-center">
			<div class="col-md-7 position-relative">
				<!-- SVG decoration -->
				<figure class="position-absolute top-50 start-0 translate-middle-y ms-n8">
					<svg width="632.6px" height="540.4px" viewBox="0 0 632.6 540.4">
						<path class="fill-primary opacity-1" d="M531.4,46.9c46.3,27.4,81.4,79.8,91.1,136.2c9.7,56.8-6.4,117.7-38.3,166s-79.4,84.2-138.6,119.3 c-59.6,35.1-130.6,69.7-201.5,62.1c-70.5-7.7-141.4-57.6-185.4-126.5C14.4,335.5-2.9,247.2,23.7,179.5 c26.2-68.1,96.7-116.5,161.6-140.2c64.9-24.2,124.5-24.6,183.3-23.4C427,17.1,485.1,19.5,531.4,46.9z"/>
					</svg>
				</figure>

				<!-- Image -->
				<img src="/assets/images/element/08.svg" class="position-relative" alt="AI文档">
			</div>

			<div class="col-md-5 position-relative z-index-9">
				<!-- Title -->
				<h2 class="fw-bold mb-4">开启智能文档编辑新时代</h2>
				<p class="mb-4">立即体验微语AI文档，让人工智能成为您的文档编辑助手。提升工作效率，释放创造力。</p>
				
				<!-- CTA buttons -->
				<div class="d-flex flex-column flex-sm-row gap-3">
					<a href="#" class="btn btn-primary btn-lg">免费试用</a>
					<a href="#" class="btn btn-outline-primary btn-lg">观看演示</a>
				</div>

				<!-- Features list -->
				<div class="mt-4">
					<div class="d-flex align-items-center mb-2">
						<i class="fas fa-check-circle text-success me-2"></i>
						<span>无需信用卡，立即开始</span>
					</div>
					<div class="d-flex align-items-center mb-2">
						<i class="fas fa-check-circle text-success me-2"></i>
						<span>30天免费试用期</span>
					</div>
					<div class="d-flex align-items-center">
						<i class="fas fa-check-circle text-success me-2"></i>
						<span>专业技术支持</span>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

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
