<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<#--  Header  -->
	<#include "./common/meta_scrm.ftl" />
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
Hero Section START -->
<section class="position-relative pb-0 pb-sm-5">
	<div class="container">
		<!-- Title -->
		<div class="row mb-5">
			<div class="col-lg-10 mx-auto text-center">
				<h1 class="display-5 fw-bold mb-4">微语AI客户管理Scrm</h1>
				<p class="lead mb-4">革命性的AI驱动客户关系管理系统，整合多渠道客户数据，智能分析客户行为，提升销售转化率和客户满意度</p>
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
					<i class="fas fa-users text-primary fa-2x"></i>
				</div>
				<h5>AI客户画像</h5>
				<p>基于大数据和AI算法，自动生成精准的客户画像和标签</p>
			</div>
			<div class="col-md-4 text-center">
				<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
					<i class="fas fa-chart-line text-success fa-2x"></i>
				</div>
				<h5>销售漏斗分析</h5>
				<p>可视化销售流程，智能预测转化率，优化销售策略</p>
			</div>
			<div class="col-md-4 text-center">
				<div class="icon-xl bg-warning bg-opacity-10 rounded-circle mx-auto mb-3">
					<i class="fas fa-magic text-warning fa-2x"></i>
				</div>
				<h5>营销自动化</h5>
				<p>智能营销活动编排，个性化内容推送，提升营销ROI</p>
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
				<p class="mb-0">融合前沿AI技术，为您提供全方位的客户关系管理解决方案</p>
			</div>
		</div>

		<div class="row g-4">

			<!-- AI客户画像 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-primary bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-user-circle text-primary fa-2x"></i>
					</div>
					<h5 class="mb-3">AI客户画像</h5>
					<p class="mb-0">基于多维度数据分析，自动生成客户标签、偏好分析、价值评估，帮助精准定位目标客户群体</p>
				</div>
			</div>

			<!-- 多渠道数据整合 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-project-diagram text-success fa-2x"></i>
					</div>
					<h5 class="mb-3">多渠道数据整合</h5>
					<p class="mb-0">统一管理来自官网、微信、电话、邮件等多渠道的客户数据，构建完整的客户视图</p>
				</div>
			</div>

			<!-- 销售漏斗管理 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-warning bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-funnel-dollar text-warning fa-2x"></i>
					</div>
					<h5 class="mb-3">销售漏斗管理</h5>
					<p class="mb-0">可视化销售流程管理，跟踪商机进展，智能预测成单概率，优化销售转化路径</p>
				</div>
			</div>

			<!-- 智能营销自动化 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-info bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-robot text-info fa-2x"></i>
					</div>
					<h5 class="mb-3">智能营销自动化</h5>
					<p class="mb-0">基于客户行为触发智能营销活动，个性化内容推送，自动化邮件、短信营销</p>
				</div>
			</div>

			<!-- 客户生命周期管理 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-purple bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-sync-alt fa-2x" style="color: #6f42c1;"></i>
					</div>
					<h5 class="mb-3">客户生命周期管理</h5>
					<p class="mb-0">从潜客培育到成交转化，从老客维护到复购提升，全流程客户价值最大化</p>
				</div>
			</div>

			<!-- 数据分析报表 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-secondary bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-chart-bar text-secondary fa-2x"></i>
					</div>
					<h5 class="mb-3">数据分析报表</h5>
					<p class="mb-0">丰富的数据可视化图表，客户行为分析、销售业绩统计，支持自定义报表</p>
				</div>
			</div>

			<!-- 客户服务记录 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-dark bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-history text-body fa-2x"></i>
					</div>
					<h5 class="mb-3">客户服务记录</h5>
					<p class="mb-0">完整记录客户服务历史，支持通话录音、聊天记录、邮件往来等全记录</p>
				</div>
			</div>

			<!-- 智能客户分群 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-danger bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-layer-group text-danger fa-2x"></i>
					</div>
					<h5 class="mb-3">智能客户分群</h5>
					<p class="mb-0">基于AI算法自动识别客户群体特征，支持自定义分群规则，精准营销投放</p>
				</div>
			</div>

			<!-- 任务提醒系统 -->
			<div class="col-sm-6 col-lg-4">
				<div class="bg-body rounded-3 text-center p-4 h-100 shadow-sm">
					<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-bell text-success fa-2x"></i>
					</div>
					<h5 class="mb-3">任务提醒系统</h5>
					<p class="mb-0">智能提醒跟进任务，客户生日祝福，合同到期提醒，确保重要事项不遗漏</p>
				</div>
			</div>

		</div>
	</div>
</section>

<!-- =======================
Use Cases START -->
<section class="py-5">
	<div class="container">
		<div class="row mb-5">
			<div class="col-lg-8 mx-auto text-center">
				<h2 class="fw-bold">应用场景</h2>
				<p class="mb-0">适用于各行各业的客户关系管理需求</p>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-6 col-lg-3">
				<div class="text-center p-4">
					<div class="icon-xl bg-primary bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-store text-primary fa-2x"></i>
					</div>
					<h5>电商零售</h5>
					<p class="mb-0">客户购买行为分析、复购预测、精准营销</p>
				</div>
			</div>
			<div class="col-md-6 col-lg-3">
				<div class="text-center p-4">
					<div class="icon-xl bg-success bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-building text-success fa-2x"></i>
					</div>
					<h5>B2B企业</h5>
					<p class="mb-0">大客户关系维护、商机跟踪、销售管理</p>
				</div>
			</div>
			<div class="col-md-6 col-lg-3">
				<div class="text-center p-4">
					<div class="icon-xl bg-warning bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-graduation-cap text-warning fa-2x"></i>
					</div>
					<h5>教育培训</h5>
					<p class="mb-0">学员管理、课程推荐、续费提醒</p>
				</div>
			</div>
			<div class="col-md-6 col-lg-3">
				<div class="text-center p-4">
					<div class="icon-xl bg-info bg-opacity-10 rounded-circle mx-auto mb-3">
						<i class="fas fa-heartbeat text-info fa-2x"></i>
					</div>
					<h5>医疗健康</h5>
					<p class="mb-0">患者档案管理、预约提醒、健康咨询</p>
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
				<h2 class="fw-bold mb-4">AI客户分析演示</h2>
				<p class="mb-4">看看我们的AI是如何智能分析客户数据，提供精准的客户洞察：</p>
				
				<div class="bg-body rounded-3 p-4 mb-4 shadow-sm">
					<div class="d-flex align-items-start mb-3">
						<div class="bg-primary rounded-circle p-2 me-3">
							<i class="fas fa-user text-white"></i>
						</div>
						<div class="flex-grow-1">
							<strong>销售:</strong> "这个客户的成交概率如何？"
						</div>
					</div>
					<div class="d-flex align-items-start">
						<div class="bg-success rounded-circle p-2 me-3">
							<i class="fas fa-robot text-white"></i>
						</div>
						<div class="flex-grow-1">
							<strong>AI:</strong> "基于该客户的互动频次、页面浏览行为和同类客户转化数据，预测成交概率为78%，建议在3天内跟进，推荐产品A方案。"
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
						<p class="mb-0">点击播放查看完整的客户管理流程</p>
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
				<h2 class="fw-bold">为什么选择微语AI客户管理Scrm？</h2>
				<p class="mb-0">基于行业最佳实践，为您提供无与伦比的客户管理体验</p>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-6 col-lg-4">
				<div class="d-flex align-items-start">
					<div class="flex-shrink-0 bg-primary bg-opacity-10 rounded-circle p-3 me-3">
						<i class="fas fa-rocket text-primary"></i>
					</div>
					<div>
						<h5>销售效率提升3倍</h5>
						<p class="mb-0">AI智能分析和自动化流程大幅提升销售团队工作效率</p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-4">
				<div class="d-flex align-items-start">
					<div class="flex-shrink-0 bg-success bg-opacity-10 rounded-circle p-3 me-3">
						<i class="fas fa-target text-success"></i>
					</div>
					<div>
						<h5>客户转化率提升50%</h5>
						<p class="mb-0">精准的客户画像和智能营销策略显著提升转化效果</p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-4">
				<div class="d-flex align-items-start">
					<div class="flex-shrink-0 bg-warning bg-opacity-10 rounded-circle p-3 me-3">
						<i class="fas fa-shield-alt text-warning"></i>
					</div>
					<div>
						<h5>数据安全保障</h5>
						<p class="mb-0">企业级数据加密和权限管理，保护客户隐私和商业机密</p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-4">
				<div class="d-flex align-items-start">
					<div class="flex-shrink-0 bg-info bg-opacity-10 rounded-circle p-3 me-3">
						<i class="fas fa-plug text-info"></i>
					</div>
					<div>
						<h5>灵活集成</h5>
						<p class="mb-0">支持与现有ERP、财务系统无缝对接，快速部署上线</p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-4">
				<div class="d-flex align-items-start">
					<div class="flex-shrink-0 bg-purple bg-opacity-10 rounded-circle p-3 me-3">
						<i class="fas fa-mobile-alt" style="color: #6f42c1;"></i>
					</div>
					<div>
						<h5>移动办公</h5>
						<p class="mb-0">支持手机APP和微信小程序，随时随地管理客户关系</p>
					</div>
				</div>
			</div>

			<div class="col-md-6 col-lg-4">
				<div class="d-flex align-items-start">
					<div class="flex-shrink-0 bg-secondary bg-opacity-10 rounded-circle p-3 me-3">
						<i class="fas fa-users text-secondary"></i>
					</div>
					<div>
						<h5>团队协作</h5>
						<p class="mb-0">支持多人协作、权限分级、工作流审批等团队管理功能</p>
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
				<p class="mb-0">灵活的定价方案，满足中小企业和大型企业的不同需求</p>
			</div>
		</div>

		<div class="row g-4">
			<!-- 基础版 -->
			<div class="col-lg-4">
				<div class="bg-body rounded-3 p-4 h-100 shadow-sm text-center">
					<h4 class="mb-3">基础版</h4>
					<div class="display-6 fw-bold text-primary mb-3">价格面议</div>
					<p class="text-muted mb-4">适合10人以下小团队</p>
					<ul class="list-unstyled mb-4">
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>1000个客户档案</li>
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>基础客户画像</li>
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>销售漏斗管理</li>
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>基础报表分析</li>
						<li class="mb-2"><i class="fas fa-times text-muted me-2"></i>AI营销自动化</li>
						<li class="mb-2"><i class="fas fa-times text-muted me-2"></i>高级数据分析</li>
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
						<p class="text-muted mb-4">适合50人以下中型团队</p>
						<ul class="list-unstyled mb-4">
							<li class="mb-2"><i class="fas fa-check text-success me-2"></i>10000个客户档案</li>
							<li class="mb-2"><i class="fas fa-check text-success me-2"></i>AI客户画像分析</li>
							<li class="mb-2"><i class="fas fa-check text-success me-2"></i>智能营销自动化</li>
							<li class="mb-2"><i class="fas fa-check text-success me-2"></i>高级报表分析</li>
							<li class="mb-2"><i class="fas fa-check text-success me-2"></i>API接口支持</li>
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
					<p class="text-muted mb-4">适合大型企业和集团</p>
					<ul class="list-unstyled mb-4">
						<li class="mb-2"><i class="fas fa-check text-success me-2"></i>无限客户档案</li>
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
				<img src="assets/images/element/09.svg" class="position-relative" alt="AI客户管理">
			</div>

			<div class="col-md-5 position-relative z-index-9">
				<!-- Title -->
				<h2 class="fw-bold mb-4">开启智能客户管理新时代</h2>
				<p class="mb-4">立即体验微语AI客户管理Scrm，让人工智能成为您的销售助手。提升转化率，增强客户黏性。</p>
				
				<!-- CTA buttons -->
				<div class="d-flex flex-column flex-sm-row gap-3">
					<a href="#" class="btn btn-primary btn-lg">免费试用</a>
					<a href="#" class="btn btn-outline-primary btn-lg">预约演示</a>
				</div>

				<!-- Features list -->
				<div class="mt-4">
					<div class="d-flex align-items-center mb-2">
						<i class="fas fa-check-circle text-success me-2"></i>
						<span>14天免费试用</span>
					</div>
					<div class="d-flex align-items-center mb-2">
						<i class="fas fa-check-circle text-success me-2"></i>
						<span>专业实施服务</span>
					</div>
					<div class="d-flex align-items-center">
						<i class="fas fa-check-circle text-success me-2"></i>
						<span>7×24小时技术支持</span>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

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