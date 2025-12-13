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
				<h1>微语AI客户管理</h1>
				<p class="lead">革命性的AI驱动客户关系管理系统，整合多渠道客户数据，智能分析客户行为，提升销售转化率和客户满意度</p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="/assets/images/element/09.svg" class="h-200px" alt="AI客户管理图标">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Page Banner END -->

<!-- =======================
Core Features START -->
<section class="bg-body">
	<div class="container">
		<!-- Title -->
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2>核心功能特性</h2>
				<p class="mb-0">融合前沿AI技术，为您提供全方位的客户关系管理解决方案</p>
			</div>
		</div>

		<!-- Feature list -->
		<div class="row g-4">
			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-person-badge fs-5"></i>
					</div>
					<h5>AI客户画像</h5>
					<p class="mb-0">基于多维度数据分析，自动生成客户标签、偏好分析、价值评估，帮助精准定位目标客户群体</p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-diagram-3 fs-5"></i>
					</div>
					<h5>多渠道数据整合</h5>
					<p class="mb-0">统一管理来自官网、微信、电话、邮件等多渠道的客户数据，构建完整的客户视图</p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-funnel fs-5"></i>
					</div>
					<h5>销售漏斗管理</h5>
					<p class="mb-0">可视化销售流程管理，跟踪商机进展，智能预测成单概率，优化销售转化路径</p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-robot fs-5"></i>
					</div>
					<h5>智能营销自动化</h5>
					<p class="mb-0">基于客户行为触发智能营销活动，个性化内容推送，自动化邮件、短信营销</p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-arrow-repeat fs-5"></i>
					</div>
					<h5>客户生命周期管理</h5>
					<p class="mb-0">从潜客培育到成交转化，从老客维护到复购提升，全流程客户价值最大化</p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-graph-up fs-5"></i>
					</div>
					<h5>数据分析报表</h5>
					<p class="mb-0">丰富的数据可视化图表，客户行为分析、销售业绩统计，支持自定义报表</p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Core Features END -->

<!-- =======================
Advantages START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2>产品优势</h2>
				<p class="mb-0">为什么选择微语AI客户管理？</p>
			</div>
		</div>

		<div class="row g-4 align-items-center">
			<!-- Left content -->
			<div class="col-lg-6">
				<ul class="list-group list-group-borderless">
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<strong>销售效率提升3倍：</strong>&nbsp;AI智能分析和自动化流程大幅提升销售团队工作效率
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<strong>客户转化率提升50%：</strong>&nbsp;精准的客户画像和智能营销策略显著提升转化效果
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<strong>数据安全保障：</strong>&nbsp;企业级数据加密和权限管理，保护客户隐私和商业机密
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<strong>灵活集成：</strong>&nbsp;支持与现有ERP、财务系统无缝对接，快速部署上线
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<strong>移动办公：</strong>&nbsp;支持手机APP和微信小程序，随时随地管理客户关系
					</li>
					<li class="list-group-item d-flex">
						<i class="bi bi-check-circle-fill text-success me-2"></i>
						<strong>团队协作：</strong>&nbsp;支持多人协作、权限分级、工作流审批等团队管理功能
					</li>
				</ul>
			</div>

			<!-- Right image -->
			<div class="col-lg-6 text-center">
				<img src="/assets/images/element/07.svg" class="img-fluid" alt="AI客户管理优势插图">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Advantages END -->

<!-- =======================
Use Cases START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2>应用场景</h2>
				<p class="mb-0">适用于各行各业的客户关系管理需求</p>
			</div>
		</div>

		<div class="row g-4">
			<!-- Use case item -->
			<div class="col-md-6 col-lg-3">
				<div class="card card-body text-center h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mx-auto mb-3">
						<i class="bi bi-shop fs-5"></i>
					</div>
					<h5>电商零售</h5>
					<p class="mb-0">客户购买行为分析、复购预测、精准营销</p>
				</div>
			</div>

			<!-- Use case item -->
			<div class="col-md-6 col-lg-3">
				<div class="card card-body text-center h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mx-auto mb-3">
						<i class="bi bi-building fs-5"></i>
					</div>
					<h5>B2B企业</h5>
					<p class="mb-0">大客户关系维护、商机跟踪、销售管理</p>
				</div>
			</div>

			<!-- Use case item -->
			<div class="col-md-6 col-lg-3">
				<div class="card card-body text-center h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mx-auto mb-3">
						<i class="bi bi-mortarboard fs-5"></i>
					</div>
					<h5>教育培训</h5>
					<p class="mb-0">学员管理、课程推荐、续费提醒</p>
				</div>
			</div>

			<!-- Use case item -->
			<div class="col-md-6 col-lg-3">
				<div class="card card-body text-center h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mx-auto mb-3">
						<i class="bi bi-heart-pulse fs-5"></i>
					</div>
					<h5>医疗健康</h5>
					<p class="mb-0">患者档案管理、预约提醒、健康咨询</p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Use Cases END -->

<!-- =======================
Demo Section START -->
<section class="bg-light">
	<div class="container">
		<div class="row g-4 align-items-center">
			<div class="col-lg-6">
				<h2 class="fw-bold mb-4">AI客户分析演示</h2>
				<p class="mb-4">看看我们的AI是如何智能分析客户数据，提供精准的客户洞察：</p>
				
				<div class="card card-body mb-4">
					<div class="d-flex align-items-start mb-3">
						<div class="icon-md bg-primary rounded-circle me-3 d-flex align-items-center justify-content-center">
							<i class="bi bi-person text-white"></i>
						</div>
						<div class="flex-grow-1">
							<strong>销售:</strong> "这个客户的成交概率如何？"
						</div>
					</div>
					<div class="d-flex align-items-start">
						<div class="icon-md bg-success rounded-circle me-3 d-flex align-items-center justify-content-center">
							<i class="bi bi-robot text-white"></i>
						</div>
						<div class="flex-grow-1">
							<strong>AI:</strong> "基于该客户的互动频次、页面浏览行为和同类客户转化数据，预测成交概率为78%，建议在3天内跟进，推荐产品A方案。"
						</div>
					</div>
				</div>

				<div class="d-flex gap-3">
					<a href="/pages/contact.html" class="btn btn-primary">开始使用</a>
					<a href="/pages/download.html" class="btn btn-outline-primary">查看更多示例</a>
				</div>
			</div>

			<div class="col-lg-6 text-center">
				<img src="/assets/images/element/08.svg" class="img-fluid" alt="AI客户分析演示">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Demo Section END -->

<!-- =======================
Pricing START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2>选择适合您的方案</h2>
				<p class="mb-0">灵活的定价方案，满足中小企业和大型企业的不同需求</p>
			</div>
		</div>

		<div class="row g-4">
			<!-- 基础版 -->
			<div class="col-lg-4">
				<div class="card card-body h-100 text-center">
					<h4 class="mb-3">基础版</h4>
					<div class="display-6 fw-bold text-primary mb-3">价格面议</div>
					<p class="text-muted mb-4">适合10人以下小团队</p>
					<ul class="list-unstyled mb-4 text-start">
						<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>1000个客户档案</li>
						<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>基础客户画像</li>
						<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>销售漏斗管理</li>
						<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>基础报表分析</li>
						<li class="mb-2 text-muted"><i class="bi bi-x-circle me-2"></i>AI营销自动化</li>
						<li class="mb-2 text-muted"><i class="bi bi-x-circle me-2"></i>高级数据分析</li>
					</ul>
					<a href="/pages/contact.html" class="btn btn-outline-primary mt-auto">立即开始</a>
				</div>
			</div>

			<!-- 专业版 -->
			<div class="col-lg-4">
				<div class="card card-body h-100 border-primary position-relative shadow">
					<div class="position-absolute top-0 start-50 translate-middle">
						<span class="badge bg-primary">推荐</span>
					</div>
					<div class="text-center">
						<h4 class="mb-3 mt-2">专业版</h4>
						<div class="display-6 fw-bold text-primary mb-3">价格面议</div>
						<p class="text-muted mb-4">适合50人以下中型团队</p>
						<ul class="list-unstyled mb-4 text-start">
							<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>10000个客户档案</li>
							<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>AI客户画像分析</li>
							<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>智能营销自动化</li>
							<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>高级报表分析</li>
							<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>API接口支持</li>
							<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>优先技术支持</li>
						</ul>
						<a href="/pages/contact.html" class="btn btn-primary mt-auto">立即订阅</a>
					</div>
				</div>
			</div>

			<!-- 企业版 -->
			<div class="col-lg-4">
				<div class="card card-body h-100 text-center">
					<h4 class="mb-3">企业版</h4>
					<div class="display-6 fw-bold text-primary mb-3">定制</div>
					<p class="text-muted mb-4">适合大型企业和集团</p>
					<ul class="list-unstyled mb-4 text-start">
						<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>无限客户档案</li>
						<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>私有化部署</li>
						<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>定制开发</li>
						<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>专属客服</li>
						<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>SLA保障</li>
						<li class="mb-2"><i class="bi bi-check-circle text-success me-2"></i>专业培训服务</li>
					</ul>
					<a href="mailto:270580156@qq.com" class="btn btn-outline-primary mt-auto">联系销售</a>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Pricing END -->

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