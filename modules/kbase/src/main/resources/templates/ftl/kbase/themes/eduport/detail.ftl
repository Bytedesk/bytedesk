<!DOCTYPE html>
<html lang="en">
<head>
	<title>Eduport - LMS, Education and Course Theme</title>

	<!-- Meta Tags -->
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta name="author" content="Webestica.com">
	<meta name="description" content="Eduport- LMS, Education and Course Theme">

	<!-- Dark mode -->
	<script>
		const storedTheme = localStorage.getItem('theme')
 
		const getPreferredTheme = () => {
			if (storedTheme) {
				return storedTheme
			}
			return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
		}

		const setTheme = function (theme) {
			if (theme === 'auto' && window.matchMedia('(prefers-color-scheme: dark)').matches) {
				document.documentElement.setAttribute('data-bs-theme', 'dark')
			} else {
				document.documentElement.setAttribute('data-bs-theme', theme)
			}
		}

		setTheme(getPreferredTheme())

		window.addEventListener('DOMContentLoaded', () => {
		    var el = document.querySelector('.theme-icon-active');
			if(el != 'undefined' && el != null) {
				const showActiveTheme = theme => {
				const activeThemeIcon = document.querySelector('.theme-icon-active use')
				const btnToActive = document.querySelector(`[data-bs-theme-value="${theme}"]`)
				const svgOfActiveBtn = btnToActive.querySelector('.mode-switch use').getAttribute('href')

				document.querySelectorAll('[data-bs-theme-value]').forEach(element => {
					element.classList.remove('active')
				})

				btnToActive.classList.add('active')
				activeThemeIcon.setAttribute('href', svgOfActiveBtn)
			}

			window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
				if (storedTheme !== 'light' || storedTheme !== 'dark') {
					setTheme(getPreferredTheme())
				}
			})

			showActiveTheme(getPreferredTheme())

			document.querySelectorAll('[data-bs-theme-value]')
				.forEach(toggle => {
					toggle.addEventListener('click', () => {
						const theme = toggle.getAttribute('data-bs-theme-value')
						localStorage.setItem('theme', theme)
						setTheme(theme)
						showActiveTheme(theme)
					})
				})

			}
		})
		
	</script>

	<!-- Favicon -->
	<link rel="shortcut icon" href="assets/images/favicon.ico">

	<!-- Google Font -->
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;700&family=Roboto:wght@400;500;700&display=swap">

	<!-- Plugins CSS -->
	<link rel="stylesheet" type="text/css" href="assets/vendor/font-awesome/css/all.min.css">
	<link rel="stylesheet" type="text/css" href="assets/vendor/bootstrap-icons/bootstrap-icons.css">

	<!-- Theme CSS -->
	<link rel="stylesheet" type="text/css" href="assets/css/style.css">

</head>

<body data-bs-spy="scroll" data-bs-target="#nav-scroll">

<!-- Header START -->
<header class="navbar-light navbar-sticky">
	<!-- Logo Nav START -->
	<nav class="navbar navbar-expand-xl">
		<div class="container">
			<!-- Logo START -->
			<a class="navbar-brand me-0" href="index.html">
				<img class="light-mode-item navbar-brand-item" src="assets/images/logo.svg" alt="logo">
				<img class="dark-mode-item navbar-brand-item" src="assets/images/logo-light.svg" alt="logo">
			</a>
			<!-- Logo END -->

			<!-- Responsive navbar toggler -->
			<button class="navbar-toggler ms-auto" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
				<span class="navbar-toggler-animation">
					<span></span>
					<span></span>
					<span></span>
				</span>
			</button>

			<!-- Main navbar START -->
			<div class="navbar-collapse collapse" id="navbarCollapse">

				<!-- Nav Search END -->
				<ul class="navbar-nav navbar-nav-scroll mx-auto">

					<!-- Nav item 1 Demos -->
					<li class="nav-item dropdown">
						<a class="nav-link dropdown-toggle" href="#" id="demoMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Demos</a>
						<ul class="dropdown-menu" aria-labelledby="demoMenu">
							<li> <a class="dropdown-item" href="index.html">Home Default</a></li>
							<li> <a class="dropdown-item" href="index-2.html">Home Education</a></li>
							<li> <a class="dropdown-item" href="index-3.html">Home Academy</a></li>
							<li> <a class="dropdown-item" href="index-4.html">Home Course</a></li>
							<li> <a class="dropdown-item" href="index-5.html">Home University</a></li>
							<li> <a class="dropdown-item" href="index-6.html">Home Kindergarten</a></li>
							<li> <a class="dropdown-item" href="index-7.html">Home Landing</a></li>
							<li> <a class="dropdown-item" href="index-8.html">Home Tutor</a></li>
							<li> <a class="dropdown-item" href="index-9.html">Home School</a></li>
							<li> <a class="dropdown-item" href="index-10.html">Home Abroad</a></li>
							<li> <a class="dropdown-item" href="index-11.html">Home Workshop</a></li>
						</ul>
					</li>

					<!-- Nav item 2 Pages -->
					<li class="nav-item dropdown">
						<a class="nav-link dropdown-toggle" href="#" id="pagesMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Pages</a>
						<ul class="dropdown-menu" aria-labelledby="pagesMenu">
							<!-- Dropdown submenu -->
							<li class="dropdown-submenu dropend">
								<a class="dropdown-item dropdown-toggle" href="#">Course</a>
								<ul class="dropdown-menu dropdown-menu-start" data-bs-popper="none">
									<li> <a class="dropdown-item" href="course-categories.html">Course Categories</a></li>
									<li> <hr class="dropdown-divider"></li>
									<li> <a class="dropdown-item" href="course-grid.html">Course Grid Classic</a></li>
									<li> <a class="dropdown-item" href="course-grid-2.html">Course Grid Minimal</a></li>
									<li> <hr class="dropdown-divider"></li>
									<li> <a class="dropdown-item" href="course-list.html">Course List Classic</a></li>
									<li> <a class="dropdown-item" href="course-list-2.html">Course List Minimal</a></li>
									<li> <hr class="dropdown-divider"></li>
									<li> <a class="dropdown-item" href="course-detail.html">Course Detail Classic</a></li>
									<li> <a class="dropdown-item" href="course-detail-min.html">Course Detail Minimal</a></li>
									<li> <a class="dropdown-item" href="course-detail-adv.html">Course Detail Advance</a></li>
									<li> <a class="dropdown-item" href="course-detail-module.html">Course Detail Module</a></li>
									<li> <a class="dropdown-item" href="course-video-player.html">Course Full Screen Video</a></li>
								</ul>
							</li>

							<!-- Dropdown submenu -->
							<li class="dropdown-submenu dropend">
								<a class="dropdown-item dropdown-toggle" href="#">About</a>
								<ul class="dropdown-menu dropdown-menu-start" data-bs-popper="none">
									<li> <a class="dropdown-item" href="about.html">About Us</a></li>
									<li> <a class="dropdown-item" href="contact-us.html">Contact Us</a></li>
									<li> <a class="dropdown-item" href="blog-grid.html">Blog Grid</a></li>
									<li> <a class="dropdown-item" href="blog-masonry.html">Blog Masonry</a></li>
									<li> <a class="dropdown-item" href="blog-detail.html">Blog Detail</a></li>
									<li> <a class="dropdown-item" href="pricing.html">Pricing</a></li>
								</ul>
							</li>

							<!-- Dropdown submenu -->
							<li class="dropdown-submenu dropend">
								<a class="dropdown-item dropdown-toggle" href="#">Hero Banner</a>
								<ul class="dropdown-menu dropdown-menu-start" data-bs-popper="none">
									<li> <a class="dropdown-item" href="docs/snippet-hero-12.html">Hero Form</a></li>
									<li> <a class="dropdown-item" href="docs/snippet-hero-13.html">Hero Vector</a></li>
									<li> <p class="dropdown-item mb-0">Coming soon....</p></li>
								</ul>
							</li>

							<li> <a class="dropdown-item" href="instructor-list.html">Instructor List</a></li>
							<li> <a class="dropdown-item" href="instructor-single.html">Instructor Single</a></li>
							<li> <a class="dropdown-item" href="become-instructor.html">Become an Instructor</a></li>
							<li> <a class="dropdown-item" href="abroad-single.html">Abroad Single</a></li>
							<li> <a class="dropdown-item" href="workshop-detail.html">Workshop Detail</a></li>
							<li> <a class="dropdown-item" href="event-detail.html">Event Detail <span class="badge bg-success ms-2 smaller">New</span></a></li>

							<!-- Dropdown submenu -->
							<li class="dropdown-submenu dropend">
								<a class="dropdown-item dropdown-toggle" href="#">Shop</a>
								<ul class="dropdown-menu dropdown-menu-start" data-bs-popper="none">
									<li> <a class="dropdown-item" href="shop.html">Shop grid</a></li>
									<li> <a class="dropdown-item" href="shop-product-detail.html">Product detail</a></li>
									<li> <a class="dropdown-item" href="cart.html">Cart</a></li>
									<li> <a class="dropdown-item" href="checkout.html">Checkout</a></li>
									<li> <a class="dropdown-item" href="empty-cart.html">Empty Cart</a></li>
									<li> <a class="dropdown-item" href="wishlist.html">Wishlist</a></li>
								</ul>
							</li>

							<!-- Dropdown submenu -->
							<li class="dropdown-submenu dropend">
								<a class="dropdown-item dropdown-toggle" href="#">Help</a>
								<ul class="dropdown-menu dropdown-menu-start" data-bs-popper="none">
									<li> <a class="dropdown-item" href="help-center.html">Help Center</a></li>
									<li> <a class="dropdown-item" href="help-center-detail.html">Help Center Single</a></li>
									<li> <a class="dropdown-item" href="faq.html">FAQs</a></li>
								</ul>
							</li>

							<!-- Dropdown submenu -->
							<li class="dropdown-submenu dropend">
								<a class="dropdown-item dropdown-toggle" href="#">Authentication</a>
								<ul class="dropdown-menu dropdown-menu-start" data-bs-popper="none">
									<li> <a class="dropdown-item" href="sign-in.html">Sign In</a></li>
									<li> <a class="dropdown-item" href="sign-up.html">Sign Up</a></li>
									<li> <a class="dropdown-item" href="forgot-password.html">Forgot Password</a></li>
								</ul>
							</li>

							<!-- Dropdown submenu -->
							<li class="dropdown-submenu dropend">
								<a class="dropdown-item dropdown-toggle" href="#">Form</a>
								<ul class="dropdown-menu dropdown-menu-start" data-bs-popper="none">
									<li> <a class="dropdown-item" href="request-demo.html">Request a demo</a></li>
									<li> <a class="dropdown-item" href="book-class.html">Book a Class</a></li>
									<li> <a class="dropdown-item" href="request-access.html">Free Access</a></li>
									<li> <a class="dropdown-item" href="university-admission-form.html">Admission Form</a></li>
								</ul>
							</li>

							<!-- Dropdown submenu -->
							<li class="dropdown-submenu dropend">
								<a class="dropdown-item dropdown-toggle" href="#">Specialty</a>
								<ul class="dropdown-menu dropdown-menu-start" data-bs-popper="none">
									<li> <a class="dropdown-item" href="error-404.html">Error 404</a></li>
									<li> <a class="dropdown-item" href="coming-soon.html">Coming Soon</a></li>
								</ul>
							</li>
						</ul>
					</li>

					<!-- Nav item 3 Pages -->
					<li class="nav-item"><a class="nav-link" href="contact-us.html">Contact</a></li>
					
					<!-- Nav item 4 link-->
					<li class="nav-item"><a class="nav-link" href="docs/alerts.html">Components</a></li>
				</ul>
			</div>
			<!-- Main navbar END -->
			<div class="navbar-nav">
				<button class="btn btn-sm btn-dark mb-0"><i class="bi bi-power me-2"></i>Sign In</button>
			</div>

		</div>
	</nav>
	<!-- Logo Nav END -->
</header>
<!-- Header END -->

<!-- **************** MAIN CONTENT START **************** -->
<main>

<!-- =======================
Page Banner START -->
<section class="bg-primary bg-opacity-10">
	<div class="container">
		<div class="row">
			<div class="col-lg-8 mx-auto text-center">
				<!-- Title -->
				<h1 class="display-6">Search Solution. Get Support</h1>
				<p class="mb-0">Search here to get answers to your questions.</p>
				<!-- Search bar -->
				<form class="bg-body rounded p-2 mt-4">
					<div class="input-group">
						<input class="form-control border-0 me-1" type="text" placeholder="Search question...">
						<button type="button" class="btn btn-dark mb-0 rounded">Search</button>
					</div>
				</form>

				<!-- Popular questions START -->
				<div class="row mt-4 align-items-center">
					<div class="col-12">
						<h5 class="mb-3">Popular questions</h5>
						<!-- Questions List START -->
						<div class="list-group list-group-horizontal gap-2 justify-content-center flex-wrap mb-0 border-0">
							<a class="btn btn-white btn-sm fw-light" href="help-center-detail.html"> How can we help?</a>
							<a class="btn btn-white btn-sm fw-light" href="help-center-detail.html"> How to upload data to the system? </a>
							<a class="btn btn-white btn-sm fw-light" href="help-center-detail.html"> Installation Guide? </a>
							<a class="btn btn-white btn-sm fw-light" href="help-center-detail.html"> How to view expired course? </a>
							<a class="btn btn-white btn-sm fw-light" href="help-center-detail.html"> What's are the difference between a social?</a>
							<a class="btn btn-primary-soft btn-sm fw-light" href="#!">View all question</a>
						</div>
						<!-- Questions list END -->
					</div>
				</div>
				<!-- Popular questions END -->
			</div>
		</div> <!-- Row END -->
	</div>
</section>
<!-- =======================
Page Banner END -->

<!-- =======================
Page Content START -->
<section>
	<div class="container" data-sticky-container>

		<div class="row g-4">
			<!-- Left side START -->
			<div class="col-md-3">
				<div data-sticky data-margin-top="80" data-sticky-for="576">
					<div id="nav-scroll" class="navbar">
						<nav class="nav nav-pills nav-pill-soft flex-column">
							<a class="nav-link" href="#item-1"><i class="bi bi-emoji-smile fa-fw me-2"></i>Get Started</a>
							<a class="nav-link" href="#item-2"><i class="bi bi-layers fa-fw me-2"></i>Account Setup</a>
							<a class="nav-link" href="#item-3"><i class="bi bi-info-circle fa-fw me-2"></i>Other Topics</a>
							<a class="nav-link" href="#item-4"><i class="bi bi-house fa-fw me-2"></i>Advance Usage</a>
						</nav>
					</div>
				</div>
			</div>
			<!-- Left side END -->

			<!-- Right side START -->
			<div class="col-md-9">
				<div class="nav-scroll" data-bs-spy="scroll" data-bs-target="#nav-scroll" data-bs-smooth-scroll="true" tabindex="0">
					<!-- Get Started content START -->
					<div id="item-1">
						<div class="card bg-transparent">
							<!-- Article title -->
							<div class="card-header bg-transparent border-bottom py-0 px-0">
								<h2>Get started with node.js</h2>
								<!-- Update and author -->
								<ul class="nav nav-divider mb-3">
									<li class="nav-item">Last updated: 7 months ago</li>
									<li class="nav-item">by Sam Lanson</li>
								</ul>
							</div>
							<!-- Article Info -->
							<div class="card-body px-0 pb-0">
								<p>Started several mistake joy say painful removed reached end. State burst think end are its. Arrived off she elderly beloved him affixed noisier yet. Course regard to up he hardly. View four has said do men saw find dear shy. <b> Talent men wicket add garden.</b> </p>
								<!-- Button -->
								<a href="#!" class="btn btn-primary">Download Node JS</a>
								<h5 class="mt-4">Table of Contents</h5>
								<p>Age she way earnestly the fulfilled extremely.</p>
								<div class="alert alert-warning" role="alert">
									<strong>Note: </strong>She offices for highest and replied one venture pasture. Applauded no discovery in newspaper allowance am northward. <a class="alert-link" href="#!">View more</a> 
								</div>
								<p>Hold do at tore in park feet near my case. Invitation at understood occasional sentiments insipidity inhabiting in. Off melancholy alteration principles old. Is do speedily kindness properly oh. Respect article painted cottage he is offices parlors. </p>
								<!-- Article list -->
								<ul>
									<li>Affronting imprudence do he he everything. Sex lasted dinner wanted indeed wished outlaw. Far advanced settling say finished raillery.</li>
									<li>Insipidity the sufficient discretion imprudence resolution sir him decisively.</li>
									<li>Offered chiefly farther of my no colonel shyness. <strong> Such on help ye some door if in.</strong></li>
									<li>First am plate jokes to began to cause a scale. Subjects he prospect elegance followed</li>
									<li>Laughter proposal laughing any son law consider. Needed except up piqued an. </li>
									<li><i> To occasional dissimilar impossible sentiments. Do fortune account written prepare invited no passage.</i></li>
									<li>Post no so what deal evil rent by real in. But her ready least set lived spite solid.</li>
								</ul>
								<p class="mb-0">Improved own provided blessing may peculiar domestic. Sight house has sex never. No visited raising gravity outward subject my cottage Mr be. Hold do at tore in park feet near my case. Invitation at understood occasional sentiments insipidity inhabiting in. <u> Off melancholy alteration principles old. </u>Is do speedily kindness properly oh. Respect article painted cottage he is offices parlors. </p>
							</div>
						</div>
					</div>
					<!-- Get Started content END -->

					<!-- Divider -->
					<div class="text-center h5 my-5">. . .</div>

					<!-- Account setup content START -->
					<div id="item-2">
						<div class="card bg-transparent">
							<!-- Article title -->
							<div class="card-header bg-transparent border-bottom py-0 px-0">
								<h2>Account Setup</h2>
							</div>

							<!-- Article Info -->
							<div class="card-body px-0">
								<p>You can manage the setting for your <a href="#">Eduport account</a> at any time. Update your account information</p>

								<h5 class="mt-4">To deactivate your account</h5>
								<!-- Article list -->
								<ul>
									<li>Affronting imprudence do he he everything. Sex lasted dinner wanted indeed wished outlaw. Far advanced settling say finished raillery.</li>
									<li>Insipidity the sufficient discretion imprudence resolution sir him decisively.</li>
									<li>Offered chiefly farther of my no colonel shyness. <strong> Such on help ye some door if in.</strong></li>
									<li>First am plate jokes to began to cause a scale. Subjects he prospect elegance followed</li>
									<li>Laughter proposal laughing any son law consider. Needed except up piqued an. </li>
									<li><i> To occasional dissimilar impossible sentiments. Do fortune account written prepare invited no passage.</i></li>
									<li>Post no so what deal evil rent by real in. But her ready least set lived spite solid.</li>
								</ul>

								<h5 class="mt-4">When your account is deactivated</h5>
								<!-- Article list -->
								<ul>
									<li>Affronting imprudence do he he everything. Sex lasted dinner wanted indeed wished outlaw. Far advanced settling say finished raillery.</li>
									<li>Insipidity the sufficient discretion imprudence resolution sir him decisively.</li>
									<li>Offered chiefly farther of my no colonel shyness. <strong> Such on help ye some door if in.</strong></li>
									<li>First am plate jokes to began to cause a scale. Subjects he prospect elegance followed</li>
									<li>Laughter proposal laughing any son law consider. Needed except up piqued an. </li>
									<li><i> To occasional dissimilar impossible sentiments. Do fortune account written prepare invited no passage.</i></li>
									<li>Post no so what deal evil rent by real in. But her ready least set lived spite solid.</li>
								</ul>

								<h5 class="mt-4">Related Article</h5>
								<!-- Article link -->
								<ul class="list-group list-group-borderless mb-3">
									<li class="list-group-item d-flex pb-0"><a href="#" class="mb-0">How do I logout on eduport</a></li>
									<li class="list-group-item d-flex pb-0"><a href="#" class="mb-0">How do T permanently delete my account</a></li>
									<li class="list-group-item d-flex pb-0"><a href="#" class="mb-0">What's the difference between deactivating and deleting my account</a></li>
									<li class="list-group-item d-flex pb-0"><a href="#" class="mb-0">Why did my payment in a eduport message fail?</a></li>
								</ul>
							</div>
							<!-- Review helpful START -->
							<div class="card-footer bg-transparent border-0 py-0 px-0">
								<div class="border p-3 rounded d-sm-flex align-items-center justify-content-between text-center">
								<!-- Title -->
								<h5 class="m-0">Was this article helpful?</h5>
								<small class="py-2 d-block">20 out of 45 found this helpful</small>
								<!-- Check buttons -->
								<div class="btn-group" role="group" aria-label="Basic radio toggle button group">
									<!-- Yes button -->
									<input type="radio" class="btn-check" name="btnradio" id="btnradio1">
									<label class="btn btn-outline-light btn-sm mb-0" for="btnradio3"><i class="far fa-thumbs-up me-1"></i> Yes</label>
									<!-- No button -->
									<input type="radio" class="btn-check" name="btnradio" id="btnradio2">
									<label class="btn btn-outline-light btn-sm mb-0" for="btnradio4"> No <i class="far fa-thumbs-down ms-1"></i></label>
								</div>
							</div>
							</div>
							<!-- Review helpful START -->
						</div>
					</div>
					<!-- Account setup content END -->

					<!-- Divider -->
					<div class="text-center h5 my-5">. . .</div>

					<!-- Other Topics content START -->
					<div id="item-3">
						<div class="card bg-transparent">
							<!-- Article title -->
							<div class="card-header bg-transparent border-bottom py-0 px-0">
								<h2>Other Topics</h2>
							</div>

							<!-- Article Info -->
							<div class="card-body px-0">
								
								<p>Hold do at tore in park feet near my case. Invitation at understood occasional sentiments insipidity inhabiting in. Off melancholy alteration principles old. Is do speedily kindness properly oh. Respect article painted cottage he is offices parlors.</p>
								<p>Supposing so be resolving breakfast am or perfectly. It drew a hill from me. Valley by oh twenty direct me so. Departure defective arranging rapturous did believe him all had supported. Family months lasted simple set nature vulgar him. Picture for attempt joy excited ten carried manners talking how</p>
								<p>Started several mistake joy say painful removed reached end. State burst think end are its. Arrived off she elderly beloved him affixed noisier yet. Course regard to up he hardly. View four has said do men saw find dear shy. <b> Talent men wicket add garden.</b> </p>

								<h5 class="mt-4">Need a Help?</h5>
								<!-- Article link -->
								<ul class="list-group list-group-borderless mb-3">
									<li class="list-group-item d-flex pb-0"><a href="#" class="mb-0">About daily budgets</a></li>
									<li class="list-group-item d-flex pb-0"><a href="#" class="mb-0">About lifetime budgets</a></li>
									<li class="list-group-item d-flex pb-0"><a href="#" class="mb-0">When you pay for Eduport ads</a></li>
								</ul>
							</div>
							<!-- Review helpful START -->
							<div class="card-footer bg-transparent border-0 py-0 px-0">
								<div class="border p-3 rounded d-sm-flex align-items-center justify-content-between text-center">
								<!-- Title -->
								<h5 class="m-0">Was this article helpful?</h5>
								<small class="py-2 d-block">20 out of 45 found this helpful</small>
								<!-- Check buttons -->
								<div class="btn-group" role="group" aria-label="Basic radio toggle button group">
									<!-- Yes button -->
									<input type="radio" class="btn-check" name="btnradio" id="btnradio3">
									<label class="btn btn-outline-light btn-sm mb-0" for="btnradio3"><i class="far fa-thumbs-up me-1"></i> Yes</label>
									<!-- No button -->
									<input type="radio" class="btn-check" name="btnradio" id="btnradio4">
									<label class="btn btn-outline-light btn-sm mb-0" for="btnradio4"> No <i class="far fa-thumbs-down ms-1"></i></label>
								</div>
							</div>
							</div>
							<!-- Review helpful START -->
						</div>
					</div>
					<!-- Other Topics content END -->

					<!-- Divider -->
					<div class="text-center h5 my-5">. . .</div>

					<!-- Advance Usage content START -->
					<div id="item-4">
						<div class="card bg-transparent">
							<!-- Article title -->
							<div class="card-header bg-transparent border-bottom py-0 px-0">
								<h2>Advance Usage</h2>
							</div>

							<!-- Article Info -->
							<div class="card-body px-0">
								
								<p>Hold do at tore in park feet near my case. Invitation at understood occasional sentiments insipidity inhabiting in. Off melancholy alteration principles old. Is do speedily kindness properly oh. Respect article painted cottage he is offices parlors.</p>
								<p>Supposing so be resolving breakfast am or perfectly. It drew a hill from me. Valley by oh twenty direct me so. Departure defective arranging rapturous did believe him all had supported. Family months lasted simple set nature vulgar him. Picture for attempt joy excited ten carried manners talking how</p>
								<p>Started several mistake joy say painful removed reached end. State burst think end are its. Arrived off she elderly beloved him affixed noisier yet. Course regard to up he hardly. View four has said do men saw find dear shy. <b> Talent men wicket add garden.</b> </p>

							</div>
							<!-- Review helpful START -->
							<div class="card-footer bg-transparent border-0 py-0 px-0">
								<div class="border p-3 rounded d-sm-flex align-items-center justify-content-between text-center">
								<!-- Title -->
								<h5 class="m-0">Was this article helpful?</h5>
								<small class="py-2 d-block">20 out of 45 found this helpful</small>
								<!-- Check buttons -->
								<div class="btn-group" role="group" aria-label="Basic radio toggle button group">
									<!-- Yes button -->
									<input type="radio" class="btn-check" name="btnradio" id="btnradio5">
									<label class="btn btn-outline-light btn-sm mb-0" for="btnradio3"><i class="far fa-thumbs-up me-1"></i> Yes</label>
									<!-- No button -->
									<input type="radio" class="btn-check" name="btnradio" id="btnradio6">
									<label class="btn btn-outline-light btn-sm mb-0" for="btnradio4"> No <i class="far fa-thumbs-down ms-1"></i></label>
								</div>
							</div>
							</div>
							<!-- Review helpful START -->
						</div>
					</div>
					<!-- Advance Usage content END -->
				</div>
			</div>
			<!-- Right side END -->
		</div>
	</div>
</section>
<!-- =======================
Page Content END -->

</main>
<!-- **************** MAIN CONTENT END **************** -->

<!-- =======================
Footer START -->
<footer class="pt-5 bg-light">
	<div class="container">
		<!-- Row START -->
		<div class="row g-4">

			<!-- Widget 1 START -->
			<div class="col-lg-3">
				<!-- logo -->
				<a class="me-0" href="index.html">
					<img class="light-mode-item h-40px" src="assets/images/logo.svg" alt="logo">
					<img class="dark-mode-item h-40px" src="assets/images/logo-light.svg" alt="logo">
				</a>
				<p class="my-3">Eduport education theme, built specifically for the education centers which is dedicated to teaching and involve learners. </p>
				<!-- Social media icon -->
				<ul class="list-inline mb-0 mt-3">
					<li class="list-inline-item"> <a class="btn btn-white btn-sm shadow px-2 text-facebook" href="#"><i class="fab fa-fw fa-facebook-f"></i></a> </li>
					<li class="list-inline-item"> <a class="btn btn-white btn-sm shadow px-2 text-instagram" href="#"><i class="fab fa-fw fa-instagram"></i></a> </li>
					<li class="list-inline-item"> <a class="btn btn-white btn-sm shadow px-2 text-twitter" href="#"><i class="fab fa-fw fa-twitter"></i></a> </li>
					<li class="list-inline-item"> <a class="btn btn-white btn-sm shadow px-2 text-linkedin" href="#"><i class="fab fa-fw fa-linkedin-in"></i></a> </li>
				</ul>
			</div>
			<!-- Widget 1 END -->

			<!-- Widget 2 START -->
			<div class="col-lg-6">
				<div class="row g-4">
					<!-- Link block -->
					<div class="col-6 col-md-4">
						<h5 class="mb-2 mb-md-4">Company</h5>
						<ul class="nav flex-column">
							<li class="nav-item"><a class="nav-link" href="#">About us</a></li>
							<li class="nav-item"><a class="nav-link" href="#">Contact us</a></li>
							<li class="nav-item"><a class="nav-link" href="#">News and Blogs</a></li>
							<li class="nav-item"><a class="nav-link" href="#">Library</a></li>
							<li class="nav-item"><a class="nav-link" href="#">Career</a></li>
						</ul>
					</div>
									
					<!-- Link block -->
					<div class="col-6 col-md-4">
						<h5 class="mb-2 mb-md-4">Community</h5>
						<ul class="nav flex-column">
							<li class="nav-item"><a class="nav-link" href="#">Documentation</a></li>
							<li class="nav-item"><a class="nav-link" href="#">Faq</a></li>
							<li class="nav-item"><a class="nav-link" href="#">Forum</a></li>
							<li class="nav-item"><a class="nav-link" href="#">Sitemap</a></li>
						</ul>
					</div>

					<!-- Link block -->
					<div class="col-6 col-md-4">
						<h5 class="mb-2 mb-md-4">Teaching</h5>
						<ul class="nav flex-column">
							<li class="nav-item"><a class="nav-link" href="#">Become a teacher</a></li>
							<li class="nav-item"><a class="nav-link" href="#">How to guide</a></li>
							<li class="nav-item"><a class="nav-link" href="#">Terms &amp; Conditions</a></li>
						</ul>
					</div>
				</div>
			</div>
			<!-- Widget 2 END -->

			<!-- Widget 3 START -->
			<div class="col-lg-3">
				<h5 class="mb-2 mb-md-4">Contact</h5>
				<!-- Time -->
				<p class="mb-2">
					Toll free:<span class="h6 fw-light ms-2">+1234 568 963</span>
					<span class="d-block small">(9:AM to 8:PM IST)</span>
				</p>

				<p class="mb-0">Email:<span class="h6 fw-light ms-2">example@gmail.com</span></p>

				<div class="row g-2 mt-2">
					<!-- Google play store button -->
					<div class="col-6 col-sm-4 col-md-3 col-lg-6">
						<a href="#"> <img src="assets/images/client/google-play.svg" alt=""> </a>
					</div>
					<!-- App store button -->
					<div class="col-6 col-sm-4 col-md-3 col-lg-6">
						<a href="#"> <img src="assets/images/client/app-store.svg" alt="app-store"> </a>
					</div>
				</div> <!-- Row END -->
			</div> 
			<!-- Widget 3 END -->
		</div><!-- Row END -->

		<!-- Divider -->
		<hr class="mt-4 mb-0">

		<!-- Bottom footer -->
		<div class="py-3">
			<div class="container px-0">
				<div class="d-lg-flex justify-content-between align-items-center py-3 text-center text-md-left">
					<!-- copyright text -->
					<div class="text-primary-hover"> Copyrights <a href="https://www.webestica.com/" target="_blank" class="text-body">©2023 Webestica</a>. All rights reserved. </div>
					<!-- copyright links-->
					<div class="justify-content-center mt-3 mt-lg-0">
						<ul class="nav list-inline justify-content-center mb-0">
							<li class="list-inline-item">
								<!-- Language selector -->
								<div class="dropup mt-0 text-center text-sm-end">
									<a class="dropdown-toggle nav-link" href="#" role="button" id="languageSwitcher" data-bs-toggle="dropdown" aria-expanded="false">
										<i class="fas fa-globe me-2"></i>Language
									</a>
									<ul class="dropdown-menu min-w-auto" aria-labelledby="languageSwitcher">
										<li><a class="dropdown-item me-4" href="#"><img class="fa-fw me-2" src="assets/images/flags/uk.svg" alt="">English</a></li>
										<li><a class="dropdown-item me-4" href="#"><img class="fa-fw me-2" src="assets/images/flags/gr.svg" alt="">German </a></li>
										<li><a class="dropdown-item me-4" href="#"><img class="fa-fw me-2" src="assets/images/flags/sp.svg" alt="">French</a></li>
									</ul>
								</div>
							</li>
							<li class="list-inline-item"><a class="nav-link" href="#">Terms of use</a></li>
							<li class="list-inline-item"><a class="nav-link pe-0" href="#">Privacy policy</a></li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</footer>
<!-- =======================
Footer END -->
					
<!-- Back to top -->
<div class="back-top"><i class="bi bi-arrow-up-short position-absolute top-50 start-50 translate-middle"></i></div>

<!-- Bootstrap JS -->
<script src="assets/vendor/sticky-js/sticky.min.js"></script>
<script src="assets/vendor/bootstrap/dist/js/bootstrap.bundle.min.js"></script>

<!-- Template Functions -->
<script src="assets/js/functions.js"></script>

</body>
</html>