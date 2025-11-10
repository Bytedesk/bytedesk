<!DOCTYPE html>
<html lang="${lang! 'zh-CN'}">
<head>
	<#--  Header  -->
	<#include "./common/header_meta.ftl" />
	<#include "./common/header_js.ftl" />
	<#include "./common/header_css.ftl" />
	<#-- i18n macro -->
	<#include "./common/macro/i18n.ftl" />
	
</head>

<body>

<#--  导航  -->
<#include "./common/header_nav.ftl" />

<!-- **************** MAIN CONTENT START **************** -->
<main>

<!-- =======================
Page Banner START -->
<section class="bg-light py-5">
	<div class="container">
		<div class="row g-4 g-md-5 position-relative">
			<!-- Main content START -->
			<div class="col-lg-8">
				<!-- Title -->
				<h1><@t key="page.kanban.title">任务管理 - 看板系统</@t></h1>
				<p class="lead"><@t key="page.kanban.subtitle">敏捷项目管理与团队协作平台</@t></p>
			</div>
			<!-- Main content END -->

			<!-- Image -->
			<div class="col-lg-4 text-center">
				<img src="assets/images/element/home.svg" class="h-200px" alt="<@t key='alt.kanban.icon'>任务管理图标</@t>">
			</div>
		</div>
	</div>
</section>
<!-- =======================
Page Banner END -->

<!-- =======================
Features START -->
<section>
	<div class="container">
		<!-- Title -->
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.kanban.features.title">核心功能</@t></h2>
				<p class="mb-0"><@t key="page.kanban.features.desc">高效的任务与项目管理</@t></p>
			</div>
		</div>

		<!-- Feature list -->
		<div class="row g-4">
			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-primary bg-opacity-10 text-primary rounded-circle mb-3">
						<i class="bi bi-kanban fs-5"></i>
					</div>
					<h5><@t key="page.kanban.feature.board.title">看板视图</@t></h5>
					<p class="mb-0"><@t key="page.kanban.feature.board.desc">可视化任务流转，拖拽式操作，清晰展示工作进度</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-success bg-opacity-10 text-success rounded-circle mb-3">
						<i class="bi bi-check2-square fs-5"></i>
					</div>
					<h5><@t key="page.kanban.feature.task.title">任务管理</@t></h5>
					<p class="mb-0"><@t key="page.kanban.feature.task.desc">创建任务、分配负责人、设置优先级、截止时间、标签分类</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-warning bg-opacity-10 text-warning rounded-circle mb-3">
						<i class="bi bi-calendar-event fs-5"></i>
					</div>
					<h5><@t key="page.kanban.feature.calendar.title">日历视图</@t></h5>
					<p class="mb-0"><@t key="page.kanban.feature.calendar.desc">日历展示任务时间线，轻松掌握工作安排和截止日期</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-info bg-opacity-10 text-info rounded-circle mb-3">
						<i class="bi bi-list-check fs-5"></i>
					</div>
					<h5><@t key="page.kanban.feature.todo.title">待办事项</@t></h5>
					<p class="mb-0"><@t key="page.kanban.feature.todo.desc">个人待办清单、今日计划、重要事项提醒，高效时间管理</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-danger bg-opacity-10 text-danger rounded-circle mb-3">
						<i class="bi bi-arrow-down-up fs-5"></i>
					</div>
					<h5><@t key="page.kanban.feature.subtask.title">子任务</@t></h5>
					<p class="mb-0"><@t key="page.kanban.feature.subtask.desc">大任务拆分为子任务，细化执行步骤，更好把控进度</@t></p>
				</div>
			</div>

			<!-- Feature item -->
			<div class="col-md-6 col-lg-4">
				<div class="card card-body h-100">
					<div class="icon-lg bg-purple bg-opacity-10 text-purple rounded-circle mb-3">
						<i class="bi bi-people fs-5"></i>
					</div>
					<h5><@t key="page.kanban.feature.collab.title">团队协作</@t></h5>
					<p class="mb-0"><@t key="page.kanban.feature.collab.desc">任务评论、@提醒、文件附件、活动日志，促进团队沟通</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Features END -->

<!-- =======================
Views START -->
<section class="bg-light">
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.kanban.views.title">多种视图</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<div class="col-md-4">
				<div class="text-center">
					<div class="icon-xl bg-primary bg-opacity-10 text-primary rounded-circle mx-auto mb-3">
						<i class="bi bi-kanban fs-4"></i>
					</div>
					<h5><@t key="page.kanban.view.board.title">看板视图</@t></h5>
					<p class="small"><@t key="page.kanban.view.board.desc">敏捷开发必备，可视化任务流</@t></p>
				</div>
			</div>

			<div class="col-md-4">
				<div class="text-center">
					<div class="icon-xl bg-success bg-opacity-10 text-success rounded-circle mx-auto mb-3">
						<i class="bi bi-list-ul fs-4"></i>
					</div>
					<h5><@t key="page.kanban.view.list.title">列表视图</@t></h5>
					<p class="small"><@t key="page.kanban.view.list.desc">紧凑高效，适合批量操作</@t></p>
				</div>
			</div>

			<div class="col-md-4">
				<div class="text-center">
					<div class="icon-xl bg-warning bg-opacity-10 text-warning rounded-circle mx-auto mb-3">
						<i class="bi bi-calendar3 fs-4"></i>
					</div>
					<h5><@t key="page.kanban.view.calendar.title">日历视图</@t></h5>
					<p class="small"><@t key="page.kanban.view.calendar.desc">时间维度，把控截止日期</@t></p>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Views END -->

<!-- =======================
Use Cases START -->
<section>
	<div class="container">
		<div class="row mb-4">
			<div class="col-12 text-center">
				<h2><@t key="page.kanban.usecases.title">应用场景</@t></h2>
			</div>
		</div>

		<div class="row g-4">
			<!-- Use case 1 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.kanban.usecase.project.title">项目管理</@t></h5>
						<p class="card-text"><@t key="page.kanban.usecase.project.desc">软件开发、产品迭代、活动策划等项目的任务分解与进度跟踪</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 2 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.kanban.usecase.team.title">团队协作</@t></h5>
						<p class="card-text"><@t key="page.kanban.usecase.team.desc">跨部门协作任务、团队工作计划、资源协调与分配</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 3 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.kanban.usecase.personal.title">个人效率</@t></h5>
						<p class="card-text"><@t key="page.kanban.usecase.personal.desc">个人待办管理、时间规划、目标追踪，提升个人生产力</@t></p>
					</div>
				</div>
			</div>

			<!-- Use case 4 -->
			<div class="col-md-6">
				<div class="card border h-100">
					<div class="card-body">
						<h5 class="card-title"><@t key="page.kanban.usecase.process.title">流程管理</@t></h5>
						<p class="card-text"><@t key="page.kanban.usecase.process.desc">需求管理、Bug跟踪、测试用例管理等研发流程</@t></p>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Use Cases END -->

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
