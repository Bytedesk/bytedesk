<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<#--  Header  -->
	<#include "./common/meta_about.ftl" />
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
Download START -->
<section class="overflow-hidden">
	<div class="container">
		<div class="row g-4 align-items-center">
			<div class="col-md-5 position-relative z-index-9">
				<!-- Title -->
				<h2>关于我们</h2>
				<p>公司成立于2013年6月，目前团队规模数十人(其中80%是技术人员)，并在不断成长扩充中，成员多来自中科院、北邮、北航等名校，曾就职于腾讯、阿里等名企.
					公司成立至今已经积累了 神州数码、神州专车、中国移动咪咕动漫等大客户，同时也有屈臣氏、大集汇商城、海猫全球购、比呀比海淘、 不乱买海淘、聚来宝商城、漫骆驼动漫商城、
					8天在线云超市、itibar商城、欧咖跨境电商、趣旅网等..
				</p>
				<!-- Download button -->
				<div class="row">
					<!-- Google play store button -->
					<div class="col-6 col-sm-4 col-md-6 col-lg-4">
						<#--  <a href="mailto:270580156@qq.com">contact us</a>  -->
						<#--  <a href="#"><img src="assets/images/element/google-play.svg" class="btn-transition" alt="google-play"></a>  -->
					</div>
					<!-- App store button -->
					<div class="col-6 col-sm-4 col-md-6 col-lg-4">
						<#--  <a href="#"><img src="assets/images/element/app-store.svg" class="btn-transition" alt="app-store"></a>  -->
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
				<img src="assets/images/element/07.svg" class="position-relative" alt="微语公司介绍插图">
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