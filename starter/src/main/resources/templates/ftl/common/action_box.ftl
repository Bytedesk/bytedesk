<!-- =======================
Action box START -->
<#include "./macro/i18n.ftl" />
<section>
	<div class="container">
		<div class="row">
			<div class="col-12">
				<div class="bg-light p-4 p-sm-5 rounded-3 position-relative overflow-hidden">
					<!-- SVG decoration -->
					<figure class="position-absolute top-0 start-0 d-none d-lg-block ms-n7">
						<svg width="294.5px" height="261.6px" viewBox="0 0 294.5 261.6" style="enable-background:new 0 0 294.5 261.6;">
							<path class="fill-warning opacity-5" d="M280.7,84.9c-4.6-9.5-10.1-18.6-16.4-27.2c-18.4-25.2-44.9-45.3-76-54.2c-31.7-9.1-67.7-0.2-93.1,21.6 C82,36.4,71.9,50.6,65.4,66.3c-4.6,11.1-9.5,22.3-17.2,31.8c-6.8,8.3-15.6,15-22.8,23C10.4,137.6-0.1,157.2,0,179 c0.1,28,11.4,64.6,40.4,76.7c23.9,10,50.7-3.1,75.4-4.7c23.1-1.5,43.1,10.4,65.5,10.6c53.4,0.6,97.8-42,109.7-90.4 C298.5,140.9,293.4,111.5,280.7,84.9z"/>
						</svg>
					</figure>
					<!-- SVG decoration -->
					<figure class="position-absolute top-50 start-50 translate-middle">
						<svg width="453px" height="211px">
							<path class="fill-orange" d="M16.002,8.001 C16.002,12.420 12.420,16.002 8.001,16.002 C3.582,16.002 -0.000,12.420 -0.000,8.001 C-0.000,3.582 3.582,-0.000 8.001,-0.000 C12.420,-0.000 16.002,3.582 16.002,8.001 Z"/>
							<path class="fill-warning" d="M176.227,203.296 C176.227,207.326 172.819,210.593 168.614,210.593 C164.409,210.593 161.000,207.326 161.000,203.296 C161.000,199.266 164.409,196.000 168.614,196.000 C172.819,196.000 176.227,199.266 176.227,203.296 Z"/>
							<path class="fill-primary" d="M453.002,65.001 C453.002,69.420 449.420,73.002 445.001,73.002 C440.582,73.002 437.000,69.420 437.000,65.001 C437.000,60.582 440.582,57.000 445.001,57.000 C449.420,57.000 453.002,60.582 453.002,65.001 Z"/>
						</svg>
					</figure>
					<!-- SVG image -->
					<img src="/assets/images/element/09.svg" class="position-absolute bottom-0 end-0 z-index-1 d-none d-lg-block me-n3" alt="">
					<!-- SVG decoration -->
					<figure class="position-absolute top-0 end-0 mt-5 me-n5 d-none d-sm-block">
						<svg 	width="285px" height="272px">
							<path class="fill-info opacity-4" d="M142.500,-0.000 C221.200,-0.000 285.000,60.889 285.000,136.000 C285.000,211.111 221.200,272.000 142.500,272.000 C63.799,272.000 -0.000,211.111 -0.000,136.000 C-0.000,60.889 63.799,-0.000 142.500,-0.000 Z"/>
						</svg>
					</figure>

					<div class="row g-4 justify-content-center align-items-center position-relative">
						<div class="col-lg-3 text-center text-lg-start ps-0">
							<!-- Image -->
							<img src="/assets/images/element/08.svg" alt="">
						</div>
						<!-- Title -->
						<div class="col-lg-6 text-center">
							<span class="h6 fw-light"><@t key="action.open">开源、开放</@t></span>
							<h3 class="mb-0 mt-2"><@t key="action.title">开始免费使用吧</@t></h3>
							<!-- Button -->
							<p style="margin-top: 1rem;">
								<a href="https://www.weiyuai.cn/admin" class="btn btn-primary" target="_blank">》<@t key="action.register">免费注册</@t></a>
							</p>
							<!-- WeChat QR 提示添加好友咨询 -->
							<div class="mt-3">
								<p class="mb-2 small text-body-emphasis"><@t key="action.wechat.tip">微信咨询，备注：微语</@t></p>
								<a href="/assets/images/qrcode/wechat.png" target="_blank" class="d-inline-block">
									<img src="/assets/images/qrcode/wechat.png" alt="<@t key='action.wechat.alt'>微信二维码，扫码添加好友咨询</@t>" style="width:120px;height:auto;" loading="lazy"/>
								</a>
							</div>
						</div>
						<!-- Content and input -->
						<div class="col-lg-3 text-center text-lg-end z-index-9">
							<a href="https://www.weiyuai.cn/download.html" class="btn btn-warning mb-0" target="_blank"><@t key="action.download">去下载</@t></a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- =======================
Action box END -->
