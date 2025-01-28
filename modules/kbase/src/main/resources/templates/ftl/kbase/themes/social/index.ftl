<!DOCTYPE html>
<html lang="en">
<head>
	<title>Social - Network, Community and Event Theme</title>

	<!-- Meta Tags -->
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta name="author" content="Webestica.com">
	<meta name="description" content="Bootstrap 5 based Social Media Network and Community Theme">

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
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap">

	<!-- Plugins CSS -->
	<link rel="stylesheet" type="text/css" href="assets/vendor/font-awesome/css/all.min.css">
	<link rel="stylesheet" type="text/css" href="assets/vendor/bootstrap-icons/bootstrap-icons.css">
  
	<!-- Theme CSS -->
	<link rel="stylesheet" type="text/css" href="assets/css/style.css">
	 
</head>

<body>

<!-- =======================
Header START -->
<header class="navbar-light fixed-top header-static bg-mode">

	<!-- Logo Nav START -->
	<nav class="navbar navbar-expand-lg">
		<div class="container">
			<!-- Logo START -->
			<a class="navbar-brand" href="index.html">
        <img class="light-mode-item navbar-brand-item" src="assets/images/logo.svg" alt="logo">
				<img class="dark-mode-item navbar-brand-item" src="assets/images/logo.svg" alt="logo">
			</a>
			<!-- Logo END -->

			<!-- Responsive navbar toggler -->
			<button class="navbar-toggler ms-auto icon-md btn btn-light p-0" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-animation">
          <span></span>
          <span></span>
          <span></span>
        </span>
      </button>

			<!-- Main navbar START -->
			<div class="collapse navbar-collapse" id="navbarCollapse">

        <!-- Nav Search START -->
        <div class="nav mt-3 mt-lg-0 flex-nowrap align-items-center px-4 px-lg-0">
          <div class="nav-item w-100">
            <form class="rounded position-relative">
              <input class="form-control ps-5 bg-light" type="search" placeholder="Search..." aria-label="Search">
              <button class="btn bg-transparent px-2 py-0 position-absolute top-50 start-0 translate-middle-y" type="submit"><i class="bi bi-search fs-5"> </i></button>
            </form>
          </div>
        </div>
        <!-- Nav Search END -->

				<ul class="navbar-nav navbar-nav-scroll ms-auto">
					<!-- Nav item 1 Demos -->
					<li class="nav-item dropdown">
						<a class="nav-link dropdown-toggle" href="#" id="homeMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Demo</a>
						<ul class="dropdown-menu" aria-labelledby="homeMenu">
							<li> <a class="dropdown-item" href="index.html">Home default</a></li>
							<li> <a class="dropdown-item" href="index-classic.html">Home classic</a></li>
							<li> <a class="dropdown-item" href="index-post.html">Home post</a></li>
							<li> <a class="dropdown-item" href="index-video.html">Home video</a></li>
							<li> <a class="dropdown-item" href="index-event.html">Home event</a></li>
							<li> <a class="dropdown-item" href="landing.html">Landing page</a></li>
							<li> <a class="dropdown-item" href="app-download.html">App download</a></li>
              <li class="dropdown-divider"></li>
							<li> 
								<a class="dropdown-item" href="https://themes.getbootstrap.com/store/webestica/" target="_blank">
									<i class="text-success fa-fw bi bi-cloud-download-fill me-2"></i>Buy Social!
								</a> 
							</li>
						</ul>
					</li>
					<!-- Nav item 2 Pages -->
					<li class="nav-item dropdown">
						<a class="nav-link dropdown-toggle" href="#" id="pagesMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Pages</a>
						<ul class="dropdown-menu" aria-labelledby="pagesMenu">
							<li> <a class="dropdown-item" href="albums.html">Albums</a></li>
							<li> <a class="dropdown-item" href="celebration.html">Celebration</a></li>
              <li> <a class="dropdown-item" href="messaging.html">Messaging</a></li>
							<!-- Dropdown submenu -->
							<li class="dropdown-submenu dropend"> 
                <a class="dropdown-item dropdown-toggle" href="#!">Profile</a>
								<ul class="dropdown-menu" data-bs-popper="none">
									<li> <a class="dropdown-item" href="my-profile.html">Feed</a> </li>
									<li> <a class="dropdown-item" href="my-profile-about.html">About</a> </li>
									<li> <a class="dropdown-item" href="my-profile-connections.html">Connections</a> </li>
									<li> <a class="dropdown-item" href="my-profile-media.html">Media</a> </li>
									<li> <a class="dropdown-item" href="my-profile-videos.html">Videos</a> </li>
									<li> <a class="dropdown-item" href="my-profile-events.html">Events</a> </li>
									<li> <a class="dropdown-item" href="my-profile-activity.html">Activity</a> </li>
								</ul>
							</li>
							<li> <a class="dropdown-item" href="events.html">Events</a></li>
							<li> <a class="dropdown-item" href="events-2.html">Events 2</a></li>
							<li> <a class="dropdown-item" href="event-details.html">Event details</a></li>
							<li> <a class="dropdown-item" href="event-details-2.html">Event details 2</a></li>
							<li> <a class="dropdown-item" href="groups.html">Groups</a></li>
							<li> <a class="dropdown-item" href="group-details.html">Group details</a></li>
							<li> <a class="dropdown-item" href="post-videos.html">Post videos</a></li>
							<li> <a class="dropdown-item" href="post-video-details.html">Post video details</a></li>
							<li> <a class="dropdown-item" href="post-details.html">Post details</a></li>
							<li> <a class="dropdown-item" href="video-details.html">Video details</a></li>
              <li> <a class="dropdown-item" href="blog.html">Blog</a></li>
							<li> <a class="dropdown-item" href="blog-details.html">Blog details</a></li>
              
							<!-- Dropdown submenu levels -->
							<li class="dropdown-divider"></li>
							<li class="dropdown-submenu dropend">
								<a class="dropdown-item dropdown-toggle" href="#">Dropdown levels</a>
								<ul class="dropdown-menu dropdown-menu-end" data-bs-popper="none">
									<li> <a class="dropdown-item" href="#">Dropdown item</a> </li>
									<li> <a class="dropdown-item" href="#">Dropdown item</a> </li>
									<!-- dropdown submenu open left -->
									<li class="dropdown-submenu dropstart">
										<a class="dropdown-item dropdown-toggle" href="#">Dropdown (start)</a>
										<ul class="dropdown-menu dropdown-menu-end" data-bs-popper="none">
											<li> <a class="dropdown-item" href="#">Dropdown item</a> </li>
											<li> <a class="dropdown-item" href="#">Dropdown item</a> </li>
										</ul>
									</li>
									<li> <a class="dropdown-item" href="#">Dropdown item</a> </li>
								</ul>
							</li>
						</ul>
					</li>

					<!-- Nav item 3 Post -->
					<li class="nav-item dropdown">
						<a class="nav-link dropdown-toggle" href="#" id="postMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Account </a>
						<ul class="dropdown-menu" aria-labelledby="postMenu">
              <li> <a class="dropdown-item" href="create-page.html">Create a page</a></li>
							<li> <a class="dropdown-item" href="settings.html">Settings</a> </li>
							<li> <a class="dropdown-item" href="notifications.html">Notifications</a> </li>
							<li> <a class="dropdown-item" href="help.html">Help center</a> </li>
							<li> <a class="dropdown-item" href="help-details.html">Help details</a> </li>
              <!-- dropdown submenu open left -->
              <li class="dropdown-submenu dropstart">
                <a class="dropdown-item dropdown-toggle" href="#">Authentication</a>
                <ul class="dropdown-menu dropdown-menu-end" data-bs-popper="none">
                  <li> <a class="dropdown-item" href="sign-in.html">Sign in</a> </li>
                  <li> <a class="dropdown-item" href="sign-up.html">Sing up</a> </li>
                  <li> <a class="dropdown-item" href="forgot-password.html">Forgot password</a> </li>
                  <li class="dropdown-divider"></li>
                  <li> <a class="dropdown-item" href="sign-in-advance.html">Sign in advance</a> </li>
                  <li> <a class="dropdown-item" href="sign-up-advance.html">Sing up advance</a> </li>
                  <li> <a class="dropdown-item" href="forgot-password-advance.html">Forgot password advance</a> </li>
                </ul>
              </li>
              <li> <a class="dropdown-item" href="error-404.html">Error 404</a> </li>
              <li> <a class="dropdown-item" href="offline.html">Offline</a> </li>
              <li> <a class="dropdown-item" href="privacy-and-terms.html">Privacy & terms</a> </li>
						</ul>
					</li>

					<!-- Nav item 4 Mega menu -->
					<li class="nav-item">
						<a class="nav-link" href="my-profile-connections.html">My network</a>
					</li>
				</ul>
			</div>
			<!-- Main navbar END -->

			<!-- Nav right START -->
			<ul class="nav flex-nowrap align-items-center ms-sm-3 list-unstyled">
				<li class="nav-item ms-2">
					<a class="nav-link icon-md btn btn-light p-0" href="messaging.html">
						<i class="bi bi-chat-left-text-fill fs-6"> </i>
					</a>
				</li>
        <li class="nav-item ms-2">
					<a class="nav-link icon-md btn btn-light p-0" href="settings.html">
						<i class="bi bi-gear-fill fs-6"> </i>
					</a>
				</li>
        <li class="nav-item dropdown ms-2">
					<a class="nav-link icon-md btn btn-light p-0" href="#" id="notifDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false" data-bs-auto-close="outside">
            <span class="badge-notif animation-blink"></span>
						<i class="bi bi-bell-fill fs-6"> </i>
					</a>
          <div class="dropdown-menu dropdown-animation dropdown-menu-end dropdown-menu-size-md p-0 shadow-lg border-0" aria-labelledby="notifDropdown">
            <div class="card">
              <div class="card-header d-flex justify-content-between align-items-center">
                <h6 class="m-0">Notifications <span class="badge bg-danger bg-opacity-10 text-danger ms-2">4 new</span></h6>
                <a class="small" href="#">Clear all</a>
              </div>
              <div class="card-body p-0">
                <ul class="list-group list-group-flush list-unstyled p-2">
                  <!-- Notif item -->
                  <li>
                    <div class="list-group-item list-group-item-action rounded badge-unread d-flex border-0 mb-1 p-3">
                      <div class="avatar text-center d-none d-sm-inline-block">
                        <img class="avatar-img rounded-circle" src="assets/images/avatar/01.jpg" alt="">
                      </div>
                      <div class="ms-sm-3">
                        <div class=" d-flex">
                        <p class="small mb-2"><b>Judy Nguyen</b> sent you a friend request.</p>
                        <p class="small ms-3 text-nowrap">Just now</p>
                      </div>
                      <div class="d-flex">
                        <button class="btn btn-sm py-1 btn-primary me-2">Accept </button>
                        <button class="btn btn-sm py-1 btn-danger-soft">Delete </button>
                      </div>
                    </div>
                  </div>
                  </li>
                  <!-- Notif item -->
                  <li>
                    <div class="list-group-item list-group-item-action rounded badge-unread d-flex border-0 mb-1 p-3 position-relative">
                      <div class="avatar text-center d-none d-sm-inline-block">
                        <img class="avatar-img rounded-circle" src="assets/images/avatar/02.jpg" alt="">
                      </div>
                      <div class="ms-sm-3 d-flex">
                        <div>
                          <p class="small mb-2">Wish <b>Amanda Reed</b> a happy birthday (Nov 12)</p>
                          <button class="btn btn-sm btn-outline-light py-1 me-2">Say happy birthday 🎂</button>
                        </div>
                        <p class="small ms-3">2min</p>
                      </div>
                    </div>
                  </li>
                  <!-- Notif item -->
                  <li>
                    <a href="#" class="list-group-item list-group-item-action rounded d-flex border-0 mb-1 p-3">
                      <div class="avatar text-center d-none d-sm-inline-block">
                        <div class="avatar-img rounded-circle bg-success"><span class="text-white position-absolute top-50 start-50 translate-middle fw-bold">WB</span></div>
                      </div>
                      <div class="ms-sm-3">
                        <div class="d-flex">
                          <p class="small mb-2">Webestica has 15 like and 1 new activity</p>
                          <p class="small ms-3">1hr</p>
                        </div>
                      </div>
                    </a>
                  </li>
                  <!-- Notif item -->
                  <li>
                    <a href="#" class="list-group-item list-group-item-action rounded d-flex border-0 p-3 mb-1">
                      <div class="avatar text-center d-none d-sm-inline-block">
                        <img class="avatar-img rounded-circle" src="assets/images/logo/12.svg" alt="">
                      </div>
                      <div class="ms-sm-3 d-flex">
                        <p class="small mb-2"><b>Bootstrap in the news:</b> The search giant’s parent company, Alphabet, just joined an exclusive club of tech stocks.</p>
                        <p class="small ms-3">4hr</p>
                      </div>
                    </a>
                  </li>
                </ul>
              </div>
              <div class="card-footer text-center">
                <a href="#" class="btn btn-sm btn-primary-soft">See all incoming activity</a>
              </div>
            </div>
          </div>
				</li>
        <!-- Notification dropdown END -->

        <li class="nav-item ms-2 dropdown">
					<a class="nav-link btn icon-md p-0" href="#" id="profileDropdown" role="button" data-bs-auto-close="outside" data-bs-display="static" data-bs-toggle="dropdown" aria-expanded="false">
						<img class="avatar-img rounded-2" src="assets/images/avatar/07.jpg" alt="">
					</a>
          <ul class="dropdown-menu dropdown-animation dropdown-menu-end pt-3 small me-md-n3" aria-labelledby="profileDropdown">
            <!-- Profile info -->
            <li class="px-3">
              <div class="d-flex align-items-center position-relative">
                <!-- Avatar -->
                <div class="avatar me-3">
                  <img class="avatar-img rounded-circle" src="assets/images/avatar/07.jpg" alt="avatar">
                </div>
                <div>
                  <a class="h6 stretched-link" href="#">Lori Ferguson</a>
                  <p class="small m-0">Web Developer</p>
                </div>
              </div>
              <a class="dropdown-item btn btn-primary-soft btn-sm my-2 text-center" href="my-profile.html">View profile</a>
            </li>
            <!-- Links -->
            <li><a class="dropdown-item" href="settings.html"><i class="bi bi-gear fa-fw me-2"></i>Settings & Privacy</a></li>
            <li> 
              <a class="dropdown-item" href="https://support.webestica.com/" target="_blank">
                <i class="fa-fw bi bi-life-preserver me-2"></i>Support
              </a> 
            </li>
            <li> 
              <a class="dropdown-item" href="docs/index.html" target="_blank">
                <i class="fa-fw bi bi-card-text me-2"></i>Documentation
              </a> 
            </li>
            <li class="dropdown-divider"></li>
            <li><a class="dropdown-item bg-danger-soft-hover" href="sign-in-advance.html"><i class="bi bi-power fa-fw me-2"></i>Sign Out</a></li>
            <li> <hr class="dropdown-divider"></li>
            <!-- Dark mode options START -->
						<li>
							<div class="modeswitch-item theme-icon-active d-flex justify-content-center gap-3 align-items-center p-2 pb-0">
								<span>Mode:</span>
								<button type="button" class="btn btn-modeswitch nav-link text-primary-hover mb-0" data-bs-theme-value="light" data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Light">
									<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-sun fa-fw mode-switch" viewBox="0 0 16 16">
										<path d="M8 11a3 3 0 1 1 0-6 3 3 0 0 1 0 6zm0 1a4 4 0 1 0 0-8 4 4 0 0 0 0 8zM8 0a.5.5 0 0 1 .5.5v2a.5.5 0 0 1-1 0v-2A.5.5 0 0 1 8 0zm0 13a.5.5 0 0 1 .5.5v2a.5.5 0 0 1-1 0v-2A.5.5 0 0 1 8 13zm8-5a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1 0-1h2a.5.5 0 0 1 .5.5zM3 8a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1 0-1h2A.5.5 0 0 1 3 8zm10.657-5.657a.5.5 0 0 1 0 .707l-1.414 1.415a.5.5 0 1 1-.707-.708l1.414-1.414a.5.5 0 0 1 .707 0zm-9.193 9.193a.5.5 0 0 1 0 .707L3.05 13.657a.5.5 0 0 1-.707-.707l1.414-1.414a.5.5 0 0 1 .707 0zm9.193 2.121a.5.5 0 0 1-.707 0l-1.414-1.414a.5.5 0 0 1 .707-.707l1.414 1.414a.5.5 0 0 1 0 .707zM4.464 4.465a.5.5 0 0 1-.707 0L2.343 3.05a.5.5 0 1 1 .707-.707l1.414 1.414a.5.5 0 0 1 0 .708z"/>
										<use href="#"></use>
									</svg>
								</button>
								<button type="button" class="btn btn-modeswitch nav-link text-primary-hover mb-0" data-bs-theme-value="dark" data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Dark">
									<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-moon-stars fa-fw mode-switch" viewBox="0 0 16 16">
										<path d="M6 .278a.768.768 0 0 1 .08.858 7.208 7.208 0 0 0-.878 3.46c0 4.021 3.278 7.277 7.318 7.277.527 0 1.04-.055 1.533-.16a.787.787 0 0 1 .81.316.733.733 0 0 1-.031.893A8.349 8.349 0 0 1 8.344 16C3.734 16 0 12.286 0 7.71 0 4.266 2.114 1.312 5.124.06A.752.752 0 0 1 6 .278zM4.858 1.311A7.269 7.269 0 0 0 1.025 7.71c0 4.02 3.279 7.276 7.319 7.276a7.316 7.316 0 0 0 5.205-2.162c-.337.042-.68.063-1.029.063-4.61 0-8.343-3.714-8.343-8.29 0-1.167.242-2.278.681-3.286z"/>
										<path d="M10.794 3.148a.217.217 0 0 1 .412 0l.387 1.162c.173.518.579.924 1.097 1.097l1.162.387a.217.217 0 0 1 0 .412l-1.162.387a1.734 1.734 0 0 0-1.097 1.097l-.387 1.162a.217.217 0 0 1-.412 0l-.387-1.162A1.734 1.734 0 0 0 9.31 6.593l-1.162-.387a.217.217 0 0 1 0-.412l1.162-.387a1.734 1.734 0 0 0 1.097-1.097l.387-1.162zM13.863.099a.145.145 0 0 1 .274 0l.258.774c.115.346.386.617.732.732l.774.258a.145.145 0 0 1 0 .274l-.774.258a1.156 1.156 0 0 0-.732.732l-.258.774a.145.145 0 0 1-.274 0l-.258-.774a1.156 1.156 0 0 0-.732-.732l-.774-.258a.145.145 0 0 1 0-.274l.774-.258c.346-.115.617-.386.732-.732L13.863.1z"/>
										<use href="#"></use>
									</svg>
								</button>
								<button type="button" class="btn btn-modeswitch nav-link text-primary-hover mb-0 active" data-bs-theme-value="auto" data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Auto">
									<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-circle-half fa-fw mode-switch" viewBox="0 0 16 16">
										<path d="M8 15A7 7 0 1 0 8 1v14zm0 1A8 8 0 1 1 8 0a8 8 0 0 1 0 16z"/>
										<use href="#"></use>
									</svg>
								</button>
							</div>
						</li> 
						<!-- Dark mode options END-->
          </ul>
				</li>
			  <!-- Profile START -->
        
			</ul>
			<!-- Nav right END -->
		</div>
	</nav>
	<!-- Logo Nav END -->
</header>
<!-- =======================
Header END -->

<!-- **************** MAIN CONTENT START **************** -->
<main>
  
    <!-- Container START -->
    <div class="container">
      <!-- Main content START -->

        <!-- Help search START -->
        <div class="row align-items-center pt-5 pb-5 pb-lg-3">
          <div class="col-md-3">
            <!-- SVG START -->
            <figure class="m-0 d-none d-md-block">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 500 500"><g id="freepik--Table--inject-3"><path d="M484.72,456c0,.15-106,.26-236.71.26S11.28,456.14,11.28,456s106-.26,236.73-.26S484.72,455.85,484.72,456Z" style="fill:#263238"></path></g><g id="freepik--speech-bubbles-2--inject-2"><path d="M393.55,253.45l4.31-7.34a27.1,27.1,0,1,1,4.72,8.49h0Z" style="fill:#fff"></path><path d="M414.46,231.9h4.11a3.49,3.49,0,0,1,3.59-3.51,3.33,3.33,0,0,1,3.23,2.12,3.67,3.67,0,0,1-.3,3.33c-.69,1.15-2.22,2.2-5,1.71v9.28h4.07v-5.44a7.94,7.94,0,0,0,5.71-7.76c0-6-5.4-7.41-7.71-7.41C420.4,224.22,414.38,224.64,414.46,231.9Z" style="fill:#263238"></path><rect x="420.08" y="246.74" width="4.13" height="3.86" style="fill:#263238"></rect><path d="M452.11,305.63a23.63,23.63,0,1,0-38.49,22.53,3,3,0,0,0,.35.27l0,0a27.41,27.41,0,0,0,18.9,4.74,21,21,0,0,0,6.24-1.74A23.66,23.66,0,0,0,452.11,305.63Z" style="fill:#fff"></path><path d="M421.19,305.93h3.59a3.06,3.06,0,0,1,3.14-3.07,2.89,2.89,0,0,1,2.81,1.86,3.22,3.22,0,0,1-.25,2.91c-.61,1-2,1.92-4.37,1.49v8.1h3.55v-4.74a6.94,6.94,0,0,0,5-6.78c0-5.26-4.72-6.48-6.73-6.48C426.38,299.22,421.12,299.59,421.19,305.93Z" style="fill:#263238"></path><rect x="426.1" y="318.89" width="3.61" height="3.37" style="fill:#263238"></rect><path d="M401.1,310a4.79,4.79,0,1,1-4.78-4.78A4.78,4.78,0,0,1,401.1,310Z" style="fill:#fff"></path><path d="M401.1,310c-.05,0,0-.42-.25-1.14a4.77,4.77,0,0,0-1.75-2.51,4.56,4.56,0,1,0,0,7.31,4.77,4.77,0,0,0,1.75-2.51c.21-.72.2-1.15.25-1.15s0,.11,0,.31a3.66,3.66,0,0,1-.09.89,4.73,4.73,0,0,1-1.73,2.75,4.92,4.92,0,0,1-4.71.77,5,5,0,0,1,0-9.43,4.92,4.92,0,0,1,4.71.77,4.73,4.73,0,0,1,1.73,2.75,3.61,3.61,0,0,1,.09.89C401.13,309.87,401.12,310,401.1,310Z" style="fill:#263238"></path><path d="M407.74,175.29a24.36,24.36,0,1,0-39.68,23.24l.36.28,0,0a28.22,28.22,0,0,0,19.48,4.89,21.51,21.51,0,0,0,6.44-1.8A24.4,24.4,0,0,0,407.74,175.29Z" style="fill:#fff"></path><path d="M375.86,175.61h3.71a3.15,3.15,0,0,1,3.23-3.17,3,3,0,0,1,2.9,1.91,3.32,3.32,0,0,1-.26,3c-.63,1-2,2-4.5,1.54v8.35h3.66v-4.89a7.16,7.16,0,0,0,5.14-7c0-5.41-4.86-6.67-6.94-6.67C381.21,168.69,375.79,169.07,375.86,175.61Z" style="fill:#263238"></path><rect x="380.93" y="188.97" width="3.72" height="3.47" style="fill:#263238"></rect><path d="M367.55,208.1a4.93,4.93,0,1,1-4.93-4.94A4.93,4.93,0,0,1,367.55,208.1Z" style="fill:#fff"></path><path d="M367.55,208.1c-.05,0,0-.44-.25-1.18a4.93,4.93,0,0,0-1.81-2.6,4.71,4.71,0,1,0,0,7.55,5,5,0,0,0,1.81-2.59c.21-.74.2-1.19.25-1.18s0,.1,0,.31a3.43,3.43,0,0,1-.1.91,4.86,4.86,0,0,1-1.78,2.84,5.06,5.06,0,0,1-4.85.79,5.15,5.15,0,0,1,0-9.71,5.09,5.09,0,0,1,4.85.79,4.89,4.89,0,0,1,1.78,2.84,3.43,3.43,0,0,1,.1.91C367.58,208,367.57,208.1,367.55,208.1Z" style="fill:#263238"></path></g><g id="freepik--speech-bubbles-1--inject-2"><path d="M172.51,133.93h-67a17.49,17.49,0,0,0-17.49,17.48v33.07A17.49,17.49,0,0,0,105.55,202h49.8l8.6,14.26L172,202h.51A17.48,17.48,0,0,0,190,184.48V151.41A17.48,17.48,0,0,0,172.51,133.93Z" style="fill:#fff"></path><path d="M176,151.89c0,.15-16.56.26-37,.26s-37-.11-37-.26,16.56-.26,37-.26S176,151.75,176,151.89Z" style="fill:#263238"></path><path d="M176,159.92c0,.14-16.56.26-37,.26s-37-.12-37-.26,16.56-.26,37-.26S176,159.77,176,159.92Z" style="fill:#263238"></path><path d="M176,167.94c0,.15-16.56.26-37,.26s-37-.11-37-.26,16.56-.26,37-.26S176,167.8,176,167.94Z" style="fill:#263238"></path><path d="M176,176c0,.14-16.56.26-37,.26s-37-.12-37-.26,16.56-.26,37-.26S176,175.83,176,176Z" style="fill:#263238"></path><path d="M176,184c0,.15-16.56.27-37,.27s-37-.12-37-.27,16.56-.26,37-.26S176,183.85,176,184Z" style="fill:#263238"></path><path d="M224.69,70.12H149.12a13.18,13.18,0,0,0-13.18,13.17v26.58a13.18,13.18,0,0,0,13.18,13.18h54.11l8.6,14.26,8-14.26h4.81a13.18,13.18,0,0,0,13.18-13.18V83.29A13.18,13.18,0,0,0,224.69,70.12Z" style="fill:#fff"></path><path d="M226,88.75c0,.14-16.56.26-37,.26s-37-.12-37-.26,16.56-.26,37-.26S226,88.61,226,88.75Z" style="fill:#263238"></path><path d="M226,96.77c0,.15-16.56.26-37,.26s-37-.11-37-.26,16.56-.26,37-.26S226,96.63,226,96.77Z" style="fill:#263238"></path><path d="M226,104.8c0,.14-16.56.26-37,.26s-37-.12-37-.26,16.56-.26,37-.26S226,104.66,226,104.8Z" style="fill:#263238"></path></g><g id="freepik--character-2--inject-2"><path d="M386,281.92c-.95-1.13-2.19-2.16-2.5-3.61s.38-2.78.68-4.17a8.51,8.51,0,0,0-5.35-9.49c-2.16-.74-4.93-.8-6-2.81s.29-4.29,1.09-6.38a12.77,12.77,0,0,0-.86-10.74,18.62,18.62,0,0,0-.91-2.16,16.84,16.84,0,0,0-14.45-9.3,11.74,11.74,0,0,0-8.91-9,10.34,10.34,0,0,0-1.08-4.68c-2.11-4.11-6.6-6.6-11.16-7.34a21.21,21.21,0,0,0-18.31,6.07c-2.52,2.59-4.56,6-8,7.16-3,1-6.32.1-9.47.51-4.22.55-7.84,3.55-10,7.21s-3.06,7.94-3.56,12.17c-.46,3.88-.76,8.14-3.45,11s-6.78,3.35-10,5.31c-6.14,3.7-8.13,12.85-4.08,18.76,1.29,1.89,3.15,3.62,3.3,5.9.13,2.08-1.21,3.93-1.84,5.92-1.62,5.12,2.57,11.21,7.93,11.52,2.71.15,5.61-.9,8,.31s3.31,4.11,4.85,6.32A11.6,11.6,0,0,0,306.09,314c2-1,3.88-2.59,6.09-2.44s3.95,2,6.12,2.45c2.85.55,5.55-1.54,7.24-3.89s2.87-5.13,5-7.06c2.83-2.52,6.81-3.14,10.4-4.38a29.63,29.63,0,0,0,13.17-9.27l-.35,20c3,0,4.7-1,5.8-2.57a8.25,8.25,0,0,0,2,1,9.13,9.13,0,0,0,9.88-3.09c1.15-1.54,1.87-3.51,3.5-4.51,1.43-.88,3.22-.81,4.87-1.11A10.57,10.57,0,0,0,386,281.92Z" style="fill:#263238"></path><path d="M318.94,235.23h0c-4.14,2.61-13.54,14.1-13.72,17.48l1,68.73c-.16,5.17-1,9.61,2,13.81h0c8.38,10,27.77,11.23,32.2,3a8,8,0,0,0,.71-1.66,15.89,15.89,0,0,0,.39-1.69,54.86,54.86,0,0,0,1-11.43c.58-6.72.78-9.48.78-9.41s14.36-1.31,18.67-19.59c2.15-9.1,2.8-24.22,2.94-36.61a20.34,20.34,0,0,0-1.47-7.1c-5.33-14.26-17.59-21.18-32.32-19.92C327.39,231.15,321.74,232.8,318.94,235.23Z" style="fill:#ffbf9d"></path><path d="M343.74,283.69c0-.14,1.61-.3,4.2-.44.66,0,1.29-.1,1.43-.54a3.29,3.29,0,0,0-.29-2c-.5-1.62-1-3.33-1.56-5.11-2.16-7.27-3.66-13.23-3.37-13.31s2.28,5.74,4.43,13q.78,2.69,1.48,5.13a3.82,3.82,0,0,1,.15,2.58,1.65,1.65,0,0,1-1.15.88,4,4,0,0,1-1.12.07C345.35,284,343.74,283.84,343.74,283.69Z" style="fill:#263238"></path><path d="M343.26,314.05A47.18,47.18,0,0,1,319,305.6s5.28,13.27,23.93,12.91Z" style="fill:#ff9a6c"></path><path d="M342.2,289.77a4.64,4.64,0,0,0-4-2,4.2,4.2,0,0,0-3,1.35,2.65,2.65,0,0,0-.43,3,3.11,3.11,0,0,0,3.28,1.1,9.61,9.61,0,0,0,3.41-1.71,3,3,0,0,0,.79-.7.85.85,0,0,0,.06-.95" style="fill:#ff9a6c"></path><path d="M336.09,284.9c.42,0,.22,2.78,2.45,4.93s5.2,2.05,5.19,2.44c0,.18-.7.49-2,.43a6.89,6.89,0,0,1-4.35-1.88,6.07,6.07,0,0,1-1.87-4.12C335.54,285.54,335.9,284.87,336.09,284.9Z" style="fill:#263238"></path><path d="M335.63,257.14c-.31.68-2.83.16-5.85.3s-5.51.77-5.86.12c-.16-.31.31-.93,1.34-1.53a10.18,10.18,0,0,1,8.92-.32C335.25,256.23,335.76,256.82,335.63,257.14Z" style="fill:#263238"></path><path d="M361.09,257.13c-.58.54-2.63-.21-5.15-.43s-4.69.05-5.14-.6c-.19-.31.19-.86,1.16-1.34a8.1,8.1,0,0,1,4.26-.64,8,8,0,0,1,4,1.47C361.1,256.25,361.35,256.87,361.09,257.13Z" style="fill:#263238"></path><path d="M308.94,276.6c.1-1.37-1.25-4.74-2.6-5.05-3.59-.84-10.16-1-10.91,8.19-1,12.61,11.85,11,11.91,10.67S308.6,281.36,308.94,276.6Z" style="fill:#ffbf9d"></path><path d="M303.87,285.33c-.05-.05-.23.13-.62.27a2.17,2.17,0,0,1-1.64-.1c-1.3-.59-2.25-2.8-2.12-5.08a7.23,7.23,0,0,1,.87-3.09,2.52,2.52,0,0,1,1.78-1.56,1.12,1.12,0,0,1,1.25.71c.14.37,0,.61.11.64s.28-.19.23-.72a1.47,1.47,0,0,0-.44-.83,1.66,1.66,0,0,0-1.19-.42,3.11,3.11,0,0,0-2.45,1.77,7.4,7.4,0,0,0-1,3.45c-.13,2.55,1,5,2.76,5.71a2.41,2.41,0,0,0,2-.17C303.82,285.63,303.91,285.35,303.87,285.33Z" style="fill:#ff9a6c"></path><path d="M303.88,252.71c0-.63-.23.59,0,0Z" style="fill:#455a64"></path><path d="M355.57,235c-4.69-3.16-8.55-6.81-20-6.83-8.85,0-15.33,5.14-22.3,11.22-9,7.81-14.63,20.55-10.86,31.82h0l3.77,1.47,3.57-8.65c6.16-13.4,19-24.67,32.8-25.33a28,28,0,0,1,3.82.06c9.14.92,18.51,17.56,18.51,17.56S366.57,242.42,355.57,235Z" class="fill-primary"></path><path d="M306.19,272.63c.2,2.61.6,5,.8,7.59.09,1.11.37,2.46,1.43,2.76a1.88,1.88,0,0,0,2-1,5.36,5.36,0,0,0,.56-2.28c.78-7.93,1.44-14,2.22-21.91C310.89,261,308.55,266.47,306.19,272.63Z" style="fill:#263238"></path><path d="M304.1,292.51s.07.25-.09.66a2.54,2.54,0,0,1-1.32,1.3,3.59,3.59,0,0,1-2.73.07,3.91,3.91,0,0,1-2.39-2.35,4.1,4.1,0,0,1,.32-3.33,3.79,3.79,0,0,1,2.16-1.68,3.58,3.58,0,0,1,1.83-.1c.43.1.63.22.62.25a5.25,5.25,0,0,0-2.33.2,3.49,3.49,0,0,0-1.86,1.56,3.69,3.69,0,0,0-.25,2.93,3.51,3.51,0,0,0,2.06,2.07,3.32,3.32,0,0,0,2.42,0,2.5,2.5,0,0,0,1.28-1C304,292.74,304.05,292.5,304.1,292.51Z" style="fill:#e0e0e0"></path><path d="M330.68,241.87s10,2.75,13.35,1.7a10,10,0,0,0,5.45-4.08S341.74,236.07,330.68,241.87Z" style="fill:#263238"></path><path d="M386,267.47a12.43,12.43,0,0,0,1.05-1.27,6.87,6.87,0,0,0,1.06-4.33,8.13,8.13,0,0,0-3.4-5.72A12.33,12.33,0,0,0,376.5,254l-.44,0,.19-.4a23.7,23.7,0,0,0,2.35-6.82,12.59,12.59,0,0,0-.39-5.75,9.94,9.94,0,0,0-3-4.42,15,15,0,0,0-8.45-3.32,31.2,31.2,0,0,0-6.27.18l-1.71.22a2.45,2.45,0,0,1-.6.05,2.77,2.77,0,0,1,.59-.16c.38-.08,1-.19,1.69-.31a27.74,27.74,0,0,1,6.33-.34,15,15,0,0,1,8.73,3.32,10.31,10.31,0,0,1,3.17,4.62,12.83,12.83,0,0,1,.42,6,24.21,24.21,0,0,1-2.4,7l-.25-.37A12.71,12.71,0,0,1,385,255.8a8.4,8.4,0,0,1,3.46,6,6.85,6.85,0,0,1-1.24,4.45A4.15,4.15,0,0,1,386,267.47Z" style="fill:#263238"></path><path d="M282.49,288.74h-.27l-.8,0c-.71,0-1.75-.1-3.08-.25a20.76,20.76,0,0,1-4.77-1.09,11.53,11.53,0,0,1-5.24-3.79,12,12,0,0,1-1.91-7.57,42,42,0,0,1,.43-4.36,23.91,23.91,0,0,1,.95-4.56,8,8,0,0,1,2.84-4c1.44-1.06,3.32-1.12,5-1.1s3.51.07,4.9-.93a6.24,6.24,0,0,0,2.23-4.51,34.37,34.37,0,0,0-.2-5.36,28.15,28.15,0,0,1,.22-5.29,27.4,27.4,0,0,1,3.07-9.47,23.89,23.89,0,0,1,5.67-7,22,22,0,0,1,6.82-3.88,21.39,21.39,0,0,1,11.23-1,15.22,15.22,0,0,1,4,1.27l-.26-.07-.76-.26a20.7,20.7,0,0,0-3-.74,21.61,21.61,0,0,0-11.07,1.1,22,22,0,0,0-6.68,3.87,23.86,23.86,0,0,0-5.52,6.9,27.05,27.05,0,0,0-3,9.32,27.94,27.94,0,0,0-.21,5.19,36.11,36.11,0,0,1,.2,5.44,6.72,6.72,0,0,1-2.44,4.88,5.66,5.66,0,0,1-2.58.94,19.6,19.6,0,0,1-2.64.08,6.46,6.46,0,0,0-7.39,4.77,23.45,23.45,0,0,0-.95,4.47,41.52,41.52,0,0,0-.45,4.31,11.69,11.69,0,0,0,1.78,7.34,11.16,11.16,0,0,0,5.05,3.72,20.9,20.9,0,0,0,4.69,1.17c1.32.18,2.35.27,3.05.34l.8.08A.84.84,0,0,1,282.49,288.74Z" style="fill:#263238"></path><path d="M314.52,239.41a7.12,7.12,0,0,1-.81,1.13c-.27.34-.61.77-1,1.29s-.8,1.09-1.23,1.76a49.61,49.61,0,0,0-2.82,4.82,51.34,51.34,0,0,0-2.66,6.28,52.12,52.12,0,0,0-1.71,6.61,47.47,47.47,0,0,0-.69,5.53c-.07.8-.07,1.52-.1,2.15s0,1.18,0,1.62a6.93,6.93,0,0,1,0,1.39,7.91,7.91,0,0,1-.16-1.38c0-.45-.07-1-.07-1.63s0-1.36,0-2.16a42.77,42.77,0,0,1,.59-5.61,47.19,47.19,0,0,1,1.69-6.69,48.54,48.54,0,0,1,2.72-6.33,41.74,41.74,0,0,1,2.95-4.8c.44-.67.91-1.23,1.3-1.73a14.2,14.2,0,0,1,1.05-1.25A6.66,6.66,0,0,1,314.52,239.41Z" style="fill:#263238"></path><path d="M324.61,234.63a10.43,10.43,0,0,1-1.21,1.16c-.41.35-.9.79-1.44,1.34s-1.19,1.12-1.84,1.83a59.88,59.88,0,0,0-4.41,5.11,64.31,64.31,0,0,0-4.55,6.89,65.46,65.46,0,0,0-3.55,7.46,59.83,59.83,0,0,0-2.1,6.42c-.28.92-.44,1.78-.62,2.52s-.31,1.39-.41,1.92a11.85,11.85,0,0,1-.36,1.64,8.88,8.88,0,0,1,.17-1.67c.06-.54.16-1.2.31-1.95s.3-1.62.55-2.55a55.05,55.05,0,0,1,2-6.5,55.58,55.58,0,0,1,8.17-14.47,54.45,54.45,0,0,1,4.53-5.08c.67-.7,1.34-1.27,1.9-1.79s1.08-.94,1.5-1.28A10.55,10.55,0,0,1,324.61,234.63Z" style="fill:#263238"></path><path d="M289.25,294s-.08.08-.27.2a5,5,0,0,1-.86.48,8.39,8.39,0,0,1-3.68.79,10,10,0,0,1-5.68-1.78,10.84,10.84,0,0,1-4.21-6.61,12.55,12.55,0,0,1-.15-4.63,13.78,13.78,0,0,1,1.69-4.74,16.52,16.52,0,0,1,3.4-4.17,24.46,24.46,0,0,1,4.74-3.16,18.12,18.12,0,0,0,4.77-3.35,12.36,12.36,0,0,0,2.83-5.31,29.54,29.54,0,0,0,.89-6.29c.11-2.16.1-4.38.32-6.62a21.46,21.46,0,0,1,1.5-6.5,11.13,11.13,0,0,1,4.09-5,19.86,19.86,0,0,1,5.74-2.48c2-.56,3.9-1,5.73-1.56a16.83,16.83,0,0,0,5-2.34,7.17,7.17,0,0,0,1-.85,10.16,10.16,0,0,0,.91-.95,18,18,0,0,0,1.45-2.15,22.93,22.93,0,0,1,2.78-4.17,11.59,11.59,0,0,1,3.8-2.67,11,11,0,0,1,12.83,2.78,10.9,10.9,0,0,1,2,3.19,6.19,6.19,0,0,1,.4,1.25s-.14-.43-.5-1.21a11.26,11.26,0,0,0-2.07-3.1,11.39,11.39,0,0,0-4.94-3.1,11,11,0,0,0-7.6.53,11.35,11.35,0,0,0-3.65,2.6,23.28,23.28,0,0,0-2.7,4.12,18.12,18.12,0,0,1-1.48,2.21,9.78,9.78,0,0,1-.94,1,7.12,7.12,0,0,1-1.07.89,17.43,17.43,0,0,1-5.15,2.43c-1.86.59-3.8,1-5.74,1.58a19.5,19.5,0,0,0-5.59,2.42,10.68,10.68,0,0,0-3.91,4.8,20.94,20.94,0,0,0-1.45,6.34c-.22,2.21-.21,4.41-.32,6.59a30.22,30.22,0,0,1-.92,6.4,12.91,12.91,0,0,1-2.95,5.52,18.94,18.94,0,0,1-4.91,3.43,23.77,23.77,0,0,0-4.67,3.08,16.45,16.45,0,0,0-3.33,4,13.32,13.32,0,0,0-1.66,4.59,12.25,12.25,0,0,0,.11,4.49,10.55,10.55,0,0,0,4,6.45,9.81,9.81,0,0,0,5.51,1.82,8.69,8.69,0,0,0,3.64-.69C288.87,294.28,289.23,294,289.25,294Z" style="fill:#455a64"></path><path d="M358.18,271.85a2.49,2.49,0,0,1-2.39,2.52,2.38,2.38,0,0,1-2.58-2.24,2.5,2.5,0,0,1,2.4-2.52A2.38,2.38,0,0,1,358.18,271.85Z" style="fill:#263238"></path><path d="M360.29,264.25c-.31.33-2.2-1.06-4.88-1.05s-4.64,1.36-4.94,1c-.14-.15.17-.73,1-1.36a6.88,6.88,0,0,1,3.95-1.23,6.56,6.56,0,0,1,3.88,1.25C360.16,263.52,360.44,264.11,360.29,264.25Z" style="fill:#263238"></path><path d="M335,271.85a2.49,2.49,0,0,1-2.4,2.52,2.38,2.38,0,0,1-2.57-2.24,2.49,2.49,0,0,1,2.39-2.52A2.38,2.38,0,0,1,335,271.85Z" style="fill:#263238"></path><path d="M334.9,264.25c-.31.33-2.2-1.06-4.88-1.05s-4.64,1.36-4.93,1c-.14-.15.17-.73,1-1.36a6.91,6.91,0,0,1,3.95-1.23,6.6,6.6,0,0,1,3.89,1.25C334.77,263.52,335.05,264.11,334.9,264.25Z" style="fill:#263238"></path><path d="M380.39,348.17c-11.57-14.09-21.58-16.09-21.58-16.09l.07,50.25L395.78,429l15-38.41S388.23,357.71,380.39,348.17Z" class="fill-primary"></path><path d="M342.42,328.1l16.54,4s8.54,49.53,5.44,62.34c-2.63,10.9-11.09,61.2-11.09,61.2h-102L285.5,357.5s.2-21.58-5.08-31.55c3.2-.77,14-2.76,23.33-4.51,0,0,6.2,7.74,20.8,10S342.42,328.1,342.42,328.1Z" class="fill-primary"></path><path d="M362.67,369.59H298.6c-5.6,32.76-19.24,45.33-43.28,64.3l-4,21.77h102l5.92-34.21-.35-12.59Z" style="fill:#455a64"></path><path d="M282.88,325.47s-24.36,1.95-33.11,41.87c-4,18.06-9,39.22-10.39,53.44-1,10.32,2.65,24.58,12.56,26.94L270,450c8.05.12,13.57-6.41,15.26-13.42L288,425.46c1.94-13.62-8.43-14.8-14.26-14.68l12.82-50.7,2.49-24.54Z" class="fill-primary"></path><path d="M366.12,359.42a3.72,3.72,0,0,1,.11.56l.24,1.61c0,.35.11.74.17,1.16s.09.9.14,1.4c.1,1,.23,2.13.28,3.4a85.68,85.68,0,0,1,.06,8.88A106.38,106.38,0,0,1,366,387.25c-.56,3.81-1.26,7.42-2,10.69s-1.42,6.18-2,8.64-1.13,4.42-1.5,5.78c-.18.66-.33,1.18-.44,1.57a2.19,2.19,0,0,1-.18.54,4.8,4.8,0,0,1,.11-.56l.36-1.58,1.39-5.82c.57-2.46,1.26-5.38,1.94-8.65s1.39-6.86,1.94-10.67,1-7.45,1.11-10.78.17-6.33,0-8.85c0-1.26-.13-2.4-.21-3.39,0-.5-.08-1-.11-1.4s-.1-.81-.14-1.16c-.07-.67-.13-1.21-.17-1.62A2.72,2.72,0,0,1,366.12,359.42Z" style="fill:#263238"></path><path d="M254.52,424.79a4.52,4.52,0,0,1,.19-1,16.62,16.62,0,0,1,.94-2.62A17.89,17.89,0,0,1,270,410.75a16.54,16.54,0,0,1,2.78-.09,4.74,4.74,0,0,1,1,.12c0,.09-1.46-.09-3.77.24a18.46,18.46,0,0,0-14.09,10.26A36.45,36.45,0,0,0,254.52,424.79Z" style="fill:#263238"></path><path d="M260.15,404.9a1.75,1.75,0,0,1,.59,0,11,11,0,0,1,1.61.07,18.31,18.31,0,0,1,5.14,1.29,18.82,18.82,0,0,1,4.56,2.7,12.34,12.34,0,0,1,1.2,1.08,2,2,0,0,1,.38.45c-.05.05-.66-.52-1.74-1.31a21.38,21.38,0,0,0-9.58-3.94C261,405,260.15,405,260.15,404.9Z" style="fill:#263238"></path><path d="M285.38,353.21a5,5,0,0,1-.06-1.07c0-.69-.05-1.68-.13-2.9A46.46,46.46,0,0,0,280.06,331c-.56-1.08-1.05-1.95-1.4-2.54a4.59,4.59,0,0,1-.5-.94,5.76,5.76,0,0,1,.63.86c.39.57.91,1.42,1.51,2.49a42,42,0,0,1,5.16,18.4c.05,1.23.05,2.22,0,2.91A6.19,6.19,0,0,1,285.38,353.21Z" style="fill:#263238"></path><g style="opacity:0.30000000000000004"><path d="M365,392.94c6.22-10.53,6.35-24.21.93-35.16a98.24,98.24,0,0,1-1.11,35.12"></path></g><path d="M280.69,445.61s-.42.51-1.33,1.26A12.09,12.09,0,0,1,274.9,449a23.74,23.74,0,0,1-7.34.65c-2.78-.2-5.85-.53-9-1a46.38,46.38,0,0,1-8.83-2,11.19,11.19,0,0,1-3.24-2.16,17.56,17.56,0,0,1-2.27-2.58,24,24,0,0,1-2.49-4.32c-.23-.55-.41-1-.52-1.26a2.09,2.09,0,0,0-.2-.44,2.43,2.43,0,0,0,.12.47c.09.3.25.73.46,1.29A21.72,21.72,0,0,0,244,442a16.79,16.79,0,0,0,2.27,2.65,11.31,11.31,0,0,0,3.32,2.26,44.41,44.41,0,0,0,8.91,2.09c3.21.48,6.28.79,9.08,1a23.58,23.58,0,0,0,7.44-.74,11.86,11.86,0,0,0,4.48-2.28,7.32,7.32,0,0,0,1-1A1.9,1.9,0,0,0,280.69,445.61Z" style="fill:#263238"></path><path d="M284.72,365.93c-.11,0-2.68,9.95-5.74,22.28s-5.47,22.35-5.36,22.37,2.67-9.94,5.74-22.28S284.82,366,284.72,365.93Z" style="fill:#263238"></path><path d="M287.82,422.23a3.76,3.76,0,0,0,.12-.82,13,13,0,0,0,.05-2.27A6.51,6.51,0,0,0,286.9,416a9.57,9.57,0,0,0-1.25-1.57,8.57,8.57,0,0,0-1.73-1.29,21.34,21.34,0,0,0-7.1-2.29,17.76,17.76,0,0,0-2.25-.2,2.59,2.59,0,0,0-.83,0c0,.06,1.18.11,3,.44a24,24,0,0,1,6.94,2.35,8,8,0,0,1,1.66,1.23,9.84,9.84,0,0,1,1.22,1.49,6.5,6.5,0,0,1,1.12,3,18.65,18.65,0,0,1,.07,2.23A3.63,3.63,0,0,0,287.82,422.23Z" style="fill:#263238"></path><path d="M337.1,420.83l-.05,0Z" style="fill:#ffbf9d"></path><path d="M336.83,421l-.1,0Z" style="fill:#ffbf9d"></path><path d="M337.05,420.85l-.22.11Z" style="fill:#ffbf9d"></path><path d="M337.19,420.78l-.09.05Z" style="fill:#ffbf9d"></path><path d="M360.08,441.81c-1.95-3.7-17.24-13.46-17.24-13.46l4.14-1,4.57,1.94a11.19,11.19,0,0,0,2,4.28c1.68,2.39,4.53-.09,4.53-.09l-1.93-9.25-9-5.44s-8.5,1.3-9.88,1.95l-3.62,3.32c-1.59,1.25-6.19,6.17-7.44,6.32s-40.72-8.73-40.72-8.73h0c-5.41,4.28-8.26,12.79-9.12,18.93,0,0-1,3,.08,4.66l48.45,3.8,10.26,2.83a72.4,72.4,0,0,0,8.43,2.9c3.42.72,5.72-.38,5.81-2.27s-2.56-2.1-2.56-2.1a52.49,52.49,0,0,1-6.6-2.9,11.53,11.53,0,0,1-3.72-4.09l3.66.44s7.78,5.4,10.7,7,3.5-1.32,3.74-2.21S345.8,439,345.8,439s8.53,5.26,10.07,6.59,3.81,1.72,4.3.83S362,445.51,360.08,441.81Z" style="fill:#ffbf9d"></path><path d="M336.73,421l-.08,0Z" style="fill:#ffbf9d"></path><path d="M336.6,421.07Z" style="fill:#ffbf9d"></path><path d="M336.65,421l0,0Z" style="fill:#ffbf9d"></path><path d="M318,442.66a2,2,0,0,1,0-.6,3.7,3.7,0,0,1,.05-.7,4.81,4.81,0,0,1,.16-.92,13,13,0,0,1,2.48-4.78,27.66,27.66,0,0,1,3.74-3.88,10.3,10.3,0,0,1,1.76-1.36,21.44,21.44,0,0,1-1.59,1.55,34.75,34.75,0,0,0-3.62,3.91,14.08,14.08,0,0,0-2.52,4.62C318,441.8,318.06,442.66,318,442.66Z" style="fill:#ff9a6c"></path><path d="M342.49,428.06a4.47,4.47,0,0,1-2.61-.23,4.41,4.41,0,0,1-2.22-1.39c.06-.09,1,.61,2.33,1.05S342.49,428,342.49,428.06Z" style="fill:#ff9a6c"></path><path d="M345,438.19a24.87,24.87,0,0,1-3.9-1.4,24,24,0,0,1-3.75-1.74,22.38,22.38,0,0,1,3.89,1.4A25,25,0,0,1,345,438.19Z" style="fill:#ff9a6c"></path><path d="M336.83,443.37a9.28,9.28,0,0,1-2-.71,8.73,8.73,0,0,1-1.94-.83,5,5,0,0,1,2.07.5A4.92,4.92,0,0,1,336.83,443.37Z" style="fill:#ff9a6c"></path><path d="M343.77,438.87a8.5,8.5,0,0,0-1.51,1.32,8.7,8.7,0,0,0-.87,1.8s-.09-.24,0-.63a3.23,3.23,0,0,1,1.79-2.35C343.51,438.85,343.76,438.83,343.77,438.87Z" style="fill:#ff9a6c"></path><path d="M347.36,433.21c.05.1-.62.46-1.06,1.23a11.75,11.75,0,0,0-.56,1.53,1.81,1.81,0,0,1,.25-1.71C346.5,433.38,347.34,433.12,347.36,433.21Z" style="fill:#ff9a6c"></path><path d="M346.14,423.11s-.18-.15-.29-.48a2.73,2.73,0,0,1,0-1.35,2.54,2.54,0,0,1,.65-1.18c.24-.25.44-.34.47-.3a7.07,7.07,0,0,0-.77,1.57A6.93,6.93,0,0,0,346.14,423.11Z" style="fill:#ff9a6c"></path><path d="M276,446.66a8.12,8.12,0,0,0,.06-1.11c0-.36,0-.79.05-1.3s.05-1.09.12-1.72a38.71,38.71,0,0,1,2.12-9.71,35.32,35.32,0,0,1,2.29-5c.45-.71.84-1.41,1.28-2l.62-.89.63-.78c1.6-2,2.84-3,2.78-3.06a5.76,5.76,0,0,0-.87.7,10.61,10.61,0,0,0-.95.9A17.17,17.17,0,0,0,282.9,424l-.64.78c-.22.28-.43.58-.65.88-.47.6-.87,1.31-1.33,2a33,33,0,0,0-2.35,5,35.61,35.61,0,0,0-2,9.82c-.06.64-.05,1.22-.07,1.73s0,.95,0,1.31A5.46,5.46,0,0,0,276,446.66Z" style="fill:#263238"></path><g style="opacity:0.30000000000000004"><path d="M309.51,376.34v1.32l-7.37.48-3.74-29.68s-1.58-10-2.61-13.63-5.4-10.88-5.4-10.88l3.24-.61L300,333.62Z"></path></g><path d="M310.48,376.3s-3.27-27.57-6.36-38.58-7.89-14.93-7.89-14.93l-4.75.89s4.73,5.75,6.55,15.89,5.25,37.06,5.25,37.06Z" style="fill:#455a64"></path><path d="M310.48,376.3l-.45,0-1.36.09-5.38.32h-.11l0-.11c-.69-5-1.74-12.57-3-21.81-.68-4.63-1.39-9.69-2.33-15a42.07,42.07,0,0,0-2.22-8,34.74,34.74,0,0,0-4-7.63l-.28-.36-.26-.35.41-.07,4.75-.9.13,0,.08.08a20.47,20.47,0,0,1,3.63,4.64,37.58,37.58,0,0,1,2.52,5.09,57.78,57.78,0,0,1,3,10.22c1.36,6.62,2.24,12.6,3,17.6s1.21,9,1.54,11.83c.16,1.37.27,2.44.36,3.21,0,.34.06.62.08.83s0,.29,0,.29a1.6,1.6,0,0,1-.06-.29c0-.21-.08-.48-.13-.82-.11-.76-.26-1.83-.45-3.19-.39-2.78-.94-6.81-1.7-11.81s-1.7-11-3.08-17.56a58,58,0,0,0-3-10.12,36.56,36.56,0,0,0-2.5-5,20.46,20.46,0,0,0-3.52-4.51l.22.05-4.75.9.15-.42.3.38a35,35,0,0,1,4.1,7.75,43.52,43.52,0,0,1,2.24,8.08c.92,5.35,1.62,10.4,2.26,15,1.28,9.27,2.22,16.86,2.82,21.84l-.12-.1,5.32-.18,1.39,0Z" style="fill:#263238"></path><path d="M358.81,332.08s1.1,30.13-2.67,45.58l-7.2-.23s5.35-37.47,4.64-46.81A10.68,10.68,0,0,1,358.81,332.08Z" style="fill:#455a64"></path><path d="M358.81,332.08s-.4-.25-1.26-.6a11.73,11.73,0,0,0-4-.75l.11-.12c.27,4.21-.24,10.79-1,18.87s-2,17.7-3.46,28l-.25-.3h.19l7,.23-.26.2a114.16,114.16,0,0,0,2.35-17.85c.35-5.48.48-10.39.54-14.49s.05-7.4,0-9.66c0-1.11,0-2,0-2.61s0-.89,0-.91a5,5,0,0,1,.08.91c0,.63.06,1.5.1,2.6.07,2.27.14,5.57.13,9.67s-.11,9-.44,14.52a110.86,110.86,0,0,1-2.29,17.94l-.05.2h-.21l-7-.23h-.47l0-.29c1.44-10.28,2.65-19.88,3.53-27.95s1.43-14.64,1.26-18.82v-.12h.12a11.17,11.17,0,0,1,4,.87,7.07,7.07,0,0,1,.93.5Z" style="fill:#263238"></path><path d="M351.3,373.29a2.15,2.15,0,1,0,2.15-2.16A2.15,2.15,0,0,0,351.3,373.29Z" style="fill:#e0e0e0"></path><path d="M353.45,375.47a2.18,2.18,0,1,1,2.18-2.18A2.19,2.19,0,0,1,353.45,375.47Zm0-4.31a2.13,2.13,0,1,0,2.13,2.13A2.13,2.13,0,0,0,353.45,371.16Z" style="fill:#263238"></path><circle cx="306.38" cy="372.57" r="2.15" style="fill:#e0e0e0"></circle><path d="M306.38,374.75a2.18,2.18,0,1,1,2.18-2.18A2.19,2.19,0,0,1,306.38,374.75Zm0-4.31a2.13,2.13,0,1,0,2.13,2.13A2.13,2.13,0,0,0,306.38,370.44Z" style="fill:#263238"></path><path d="M349.39,395.35a1.64,1.64,0,0,1,0,.33v1c0,.85,0,2.1-.16,3.71a28.17,28.17,0,0,1-1,5.81,19,19,0,0,1-3.8,6.81c-3.61,4.59-10.14,8.11-17.76,9.08a29,29,0,0,1-5.9.08,16.74,16.74,0,0,1-6-1.57,20.58,20.58,0,0,1-9.08-9,29.14,29.14,0,0,1-3.09-12.48,29.52,29.52,0,0,1,0-3.12,4.73,4.73,0,0,1,.4-1.55,2.4,2.4,0,0,1,1.17-1.16,7.4,7.4,0,0,1,3-.22l2.9.12,20.26.83,13.65.6,3.71.2,1,.06a1.14,1.14,0,0,1,.33,0h-1.3l-3.72-.09-13.66-.44-20.26-.73-2.89-.1a7.08,7.08,0,0,0-2.82.19,2.52,2.52,0,0,0-1.27,2.3,28.08,28.08,0,0,0,0,3.05,28.6,28.6,0,0,0,3,12.26,20.12,20.12,0,0,0,8.84,8.81,16.28,16.28,0,0,0,5.79,1.54,29.42,29.42,0,0,0,5.79-.06c7.52-.94,13.95-4.36,17.54-8.84a19.11,19.11,0,0,0,3.8-6.66,28.91,28.91,0,0,0,1.09-5.75c.15-1.6.21-2.85.26-3.7,0-.4,0-.72.05-1A1.68,1.68,0,0,1,349.39,395.35Z" style="fill:#263238"></path><g style="opacity:0.30000000000000004"><path d="M293.49,423.42a13.25,13.25,0,0,0-2.91-10.29,13.76,13.76,0,0,0-9.71-5,178.35,178.35,0,0,1,2.76-37.69l-10,40s13.92.19,14.19,11.79Z"></path></g><g style="opacity:0.30000000000000004"><path d="M304.12,271.12a56.47,56.47,0,0,1,8.37-23.1c.49-.77,1-1.74.55-2.53A1.65,1.65,0,0,0,311,245a4.29,4.29,0,0,0-1.66,1.59,31.69,31.69,0,0,0-5.11,24.24"></path></g><path d="M476.94,386.37,462.8,455.82H340a2.45,2.45,0,0,1-2.45-2.45h0a2.44,2.44,0,0,1,2.45-2.45h24.72l13.12-64.6a2.66,2.66,0,0,1,2.59-2.14l93.86-1A2.68,2.68,0,0,1,476.94,386.37Z" style="fill:#e0e0e0"></path><path d="M476.94,386.37a1.76,1.76,0,0,0,0-.21,2.48,2.48,0,0,0,0-.63,2.69,2.69,0,0,0-1.3-2,2.54,2.54,0,0,0-.81-.32,3.79,3.79,0,0,0-1-.06h-2.21l-5.53.05-35.48.34-24.74.24-13.86.15-7.27.07-3.72,0c-.31,0-.61,0-.95,0a2.88,2.88,0,0,0-2.29,1.69,4.2,4.2,0,0,0-.26.95l-.19.93c-.13.62-.25,1.25-.38,1.88-.26,1.25-.51,2.52-.77,3.79q-.78,3.81-1.57,7.73-1.59,7.82-3.26,16c-2.21,10.89-4.51,22.21-6.89,33.88l.17-.13H340.41a4.52,4.52,0,0,0-1,.05,2.64,2.64,0,0,0-1.61,1.08,2.64,2.64,0,0,0,.91,3.8,2.7,2.7,0,0,0,1.15.31h65.24l57.66,0h.1l0-.1c4.33-21.35,7.84-38.72,10.28-50.75L476,391.22c.32-1.58.56-2.79.72-3.62s.23-1.23.23-1.23l-.27,1.23-.75,3.61c-.66,3.18-1.62,7.85-2.86,13.86-2.47,12-6,29.39-10.39,50.73l.13-.11-57.66,0H339.92a2.38,2.38,0,0,1-1-.27,2.29,2.29,0,0,1-.79-3.31,2.28,2.28,0,0,1,1.4-.93,3.68,3.68,0,0,1,.89-.05h24.45l0-.13,6.88-33.89q1.65-8.16,3.24-16,.79-3.92,1.57-7.73c.25-1.27.51-2.54.76-3.79.13-.63.26-1.26.38-1.88l.19-.93a4.63,4.63,0,0,1,.24-.89,2.54,2.54,0,0,1,1.21-1.27,2.45,2.45,0,0,1,.85-.24c.28,0,.61,0,.92,0l3.71,0,7.27-.08L406,384l24.74-.29,35.48-.44,5.53-.07,2.21,0a4.87,4.87,0,0,1,.95.05,2.77,2.77,0,0,1,.79.31,2.69,2.69,0,0,1,1.3,2A3.39,3.39,0,0,1,476.94,386.37Z" style="fill:#263238"></path><path d="M420.36,418.15a4.06,4.06,0,1,0,4.05-4A4.05,4.05,0,0,0,420.36,418.15Z" style="fill:#fff"></path><path d="M476.83,386.93h-6.44l-17.56.09-57.92.41-8.38.07h-2.06a5.45,5.45,0,0,0-1.05.07,2.92,2.92,0,0,0-1,.4,2.86,2.86,0,0,0-1.24,1.69c-.16.68-.27,1.32-.41,2-1,5.23-2,10.28-3,15.09l-5,25.55c-1.42,7.26-2.58,13.15-3.38,17.23-.38,2-.69,3.6-.9,4.68-.09.53-.17.94-.22,1.22s-.07.42-.07.42,0-.14.1-.41l.26-1.21c.22-1.08.55-2.66,1-4.68.83-4.07,2-9.95,3.49-17.21s3.19-15.92,5.12-25.53c1-4.81,2-9.85,3-15.08.13-.65.26-1.33.4-2a2.55,2.55,0,0,1,2-1.85,5.27,5.27,0,0,1,1-.06l2.06,0,8.38-.06,57.92-.51,17.56-.19,4.77-.07,1.25,0Z" style="fill:#263238"></path></g><g id="freepik--character-1--inject-2"><path d="M277.54,202.76a8.12,8.12,0,0,0,9.61-13,8.44,8.44,0,0,0-.88-16A10.11,10.11,0,0,0,275.77,160a11.35,11.35,0,0,0-12.64-6A8.08,8.08,0,0,0,251,163.54" style="fill:#263238"></path><path d="M278.14,204.28a34.43,34.43,0,0,0-18.27-43l-1.62-.73c-18-6.89-33.89,4.55-40.28,22.72l-28,62.63h0l14.44,46.43,21.58-23,10.49-12.13s18,5,25.84-11.77C266.11,237.3,272.52,220,278.14,204.28Z" style="fill:#ae7461"></path><path d="M236.65,257.2a44.77,44.77,0,0,1-19.17-16.42s-1.76,13.92,15.74,20Z" style="fill:#6f4439"></path><path d="M266.81,217.61a2.64,2.64,0,0,1-3.42,1.38,2.52,2.52,0,0,1-1.5-3.29,2.65,2.65,0,0,1,3.42-1.38A2.53,2.53,0,0,1,266.81,217.61Z" style="fill:#263238"></path><path d="M270.8,210.89c-.44.18-1.65-2-4.24-3.15s-5.08-.73-5.22-1.17c-.07-.2.49-.63,1.59-.86a7.24,7.24,0,0,1,4.35.55,7,7,0,0,1,3.2,2.9C271,210.13,271,210.82,270.8,210.89Z" style="fill:#263238"></path><path d="M243,207.45a2.64,2.64,0,0,1-3.41,1.38,2.52,2.52,0,0,1-1.51-3.28,2.66,2.66,0,0,1,3.42-1.39A2.52,2.52,0,0,1,243,207.45Z" style="fill:#263238"></path><path d="M245.13,199.89c-.44.18-1.65-2-4.24-3.15s-5.08-.72-5.22-1.17c-.07-.2.49-.63,1.59-.86a7.24,7.24,0,0,1,4.35.55,7,7,0,0,1,3.2,2.9C245.33,199.13,245.34,199.82,245.13,199.89Z" style="fill:#263238"></path><path d="M245.87,223.33c0-.15,1.79.3,4.54,1.14.7.23,1.38.38,1.7,0a3.73,3.73,0,0,0,.44-2.16q.15-2.82.32-5.91c.51-8.4,1.21-15.18,1.55-15.15s.19,6.84-.33,15.24c-.15,2.06-.28,4-.41,5.91a4.24,4.24,0,0,1-.83,2.75,1.82,1.82,0,0,1-1.52.47,4.76,4.76,0,0,1-1.2-.35C247.44,224.21,245.81,223.49,245.87,223.33Z" style="fill:#263238"></path><path d="M249.89,191.43c-.63.5-2.53-1.21-5.22-2.53s-5.2-1.91-5.14-2.7c0-.37.79-.75,2-.83a9.07,9.07,0,0,1,4.49,1,8.76,8.76,0,0,1,3.41,3C250.07,190.38,250.19,191.19,249.89,191.43Z" style="fill:#263238"></path><path d="M275.25,201.87c-.81.34-2.67-1.24-5.23-2.42s-4.92-1.75-5.14-2.59c-.09-.4.52-.83,1.72-1a9.22,9.22,0,0,1,4.71,1,9,9,0,0,1,3.65,3.07C275.6,201,275.62,201.69,275.25,201.87Z" style="fill:#263238"></path><path d="M213.28,200.9a5,5,0,0,0-2.95-6.63c-3.28-1.12-7.58-.91-10.3,5.67-4.83,11.69,7.9,14.13,8.06,13.8S211.3,205.81,213.28,200.9Z" style="fill:#ae7461"></path><path d="M207.36,208.29s-.26.06-.67.08a2.25,2.25,0,0,1-1.54-.6c-1-1-1.27-3.35-.46-5.48a7.15,7.15,0,0,1,1.78-2.68,2.51,2.51,0,0,1,2.17-.93,1.11,1.11,0,0,1,1,1c0,.39-.15.6-.09.64s.33-.09.43-.61a1.38,1.38,0,0,0-.15-.92,1.68,1.68,0,0,0-1-.77,3.09,3.09,0,0,0-2.87.94,7.38,7.38,0,0,0-2,3c-.9,2.39-.61,5.09.88,6.28a2.37,2.37,0,0,0,2,.46C207.22,208.57,207.39,208.33,207.36,208.29Z" style="fill:#6f4439"></path><path d="M279.38,176a4.86,4.86,0,0,0,0-1,8.17,8.17,0,0,0-4.26-5.85,31.15,31.15,0,0,0-3.27-3.52,11,11,0,0,0-16.71-8.4l-1.52-.41A11.24,11.24,0,0,0,236,154.2a8.16,8.16,0,0,0-13.27,7c-2.61.25-5.31,1.51-6.43,3.87s0,5.36,2.29,6.07c-2.89,5.33-5.23,12.1-7.53,18-1.62,4.12,2.44,8.84,1.55,11.73-4.51,14.79,3.55,9.13,18.36-19,.39-.09.79-.2,1.21-.33.85-.27,1.31-.9,2.12-1.28s2,.3,2.93.83a8.06,8.06,0,0,0,10-1.82c.28,1.66,2.3,2.5,4,2.25s3.12-1.23,4.65-1.94,3.42-1.09,4.85-.19c.79.5,1.36,1.37,2.25,1.68,1.58.55,3.11-.89,4-2.29.76-1.17,1.58-2.54,2.8-3.08a9.31,9.31,0,0,1,3.3.48c.55.33,1.07.71,1.59,1a5.91,5.91,0,0,0,.79.41,9.3,9.3,0,0,1,.86.9c4,4.76,3.66,17.31,3.66,17.31C283.38,189.3,282.26,182.09,279.38,176Z" style="fill:#263238"></path><path d="M246.28,157.5s.08-.1.27-.25a7.83,7.83,0,0,1,.87-.59,9.47,9.47,0,0,1,9.46.08,9.78,9.78,0,0,1,4.39,5.84l-.34-.18a14.47,14.47,0,0,1,3.66-.83,10.87,10.87,0,0,1,8.21,2.48,8.45,8.45,0,0,1,3.07,6.29,7.56,7.56,0,0,1-1.42,4.53,5.44,5.44,0,0,1-.89,1c-.23.19-.35.28-.37.27s.48-.42,1.09-1.34a7.66,7.66,0,0,0,1.22-4.42,8.18,8.18,0,0,0-3-5.93,10.52,10.52,0,0,0-7.86-2.32,14.43,14.43,0,0,0-3.52.79l-.27.1-.07-.27a9.39,9.39,0,0,0-4.12-5.58,9.08,9.08,0,0,0-5.47-1.32,9.8,9.8,0,0,0-3.67,1C246.71,157.23,246.31,157.53,246.28,157.5Z" style="fill:#fff"></path><path d="M221,161.16s.06,0,.18-.12.31-.17.57-.28a7.06,7.06,0,0,1,2.39-.5,8,8,0,0,1,3.74.81,7.41,7.41,0,0,1,3.56,3.53l-.36,0a10,10,0,0,1,7.37-3.61,12.55,12.55,0,0,1,4.7.6,15.2,15.2,0,0,1,4.4,2.38,25.17,25.17,0,0,1,6.08,7.1l-.38-.05a11.25,11.25,0,0,1,7.65-3.1,10.81,10.81,0,0,1,6.23,1.95,11,11,0,0,1,3,3.1,8.28,8.28,0,0,1,.54,1c.11.24.16.37.14.37s-.26-.47-.82-1.29a11.34,11.34,0,0,0-3-2.93,10.61,10.61,0,0,0-6.05-1.79,10.91,10.91,0,0,0-7.32,3l-.22.21-.16-.26a25.22,25.22,0,0,0-6-6.95A15,15,0,0,0,243,162a12.16,12.16,0,0,0-4.51-.6,9.64,9.64,0,0,0-7.07,3.38l-.21.27-.14-.31a7.14,7.14,0,0,0-3.33-3.4,7.87,7.87,0,0,0-3.59-.87A8.75,8.75,0,0,0,221,161.16Z" style="fill:#fff"></path><path d="M270.26,175a3.67,3.67,0,0,1-.4.35,10,10,0,0,0-1,1.14c-.42.52-.9,1.2-1.48,2a7.81,7.81,0,0,1-2.41,2.33,2.63,2.63,0,0,1-2,.13,9.64,9.64,0,0,1-1.9-1,4.2,4.2,0,0,0-2-.74,5.7,5.7,0,0,0-2.26.54c-.76.32-1.52.73-2.31,1.11a8.72,8.72,0,0,1-2.55,1.06,5.91,5.91,0,0,1-2.91-.29,4.64,4.64,0,0,1-1.35-.72,2.17,2.17,0,0,1-.86-1.39l.45.12a7,7,0,0,1-3.78,2.59,8.81,8.81,0,0,1-4.8-.12,11,11,0,0,1-1.87-.77,4.28,4.28,0,0,0-1.68-.75,2.59,2.59,0,0,0-1.68.42,13,13,0,0,1-1.76.76,9.84,9.84,0,0,1-7.18.1,9.12,9.12,0,0,1-4.36-4.75,9.42,9.42,0,0,1-.54-5.52l.14.21a5,5,0,0,1-3.34-2.59,4.78,4.78,0,0,1,.46-5,4.24,4.24,0,0,1,.34-.39l.14-.12a5.6,5.6,0,0,0-1.13,2.37,4.5,4.5,0,0,0,.44,3,4.7,4.7,0,0,0,3.16,2.37l.18,0,0,.18a9.12,9.12,0,0,0,.58,5.28,8.75,8.75,0,0,0,4.16,4.49,9.47,9.47,0,0,0,6.82-.15,10.56,10.56,0,0,0,1.69-.74,3.12,3.12,0,0,1,2-.48,2.83,2.83,0,0,1,1,.32l.84.49a10.24,10.24,0,0,0,1.78.73,8.28,8.28,0,0,0,4.52.12,7,7,0,0,0,2-.92,7.12,7.12,0,0,0,1.53-1.47l.37-.47.08.59c.12.85,1,1.41,1.87,1.71a5.46,5.46,0,0,0,2.67.28,8.22,8.22,0,0,0,2.43-1c.79-.38,1.55-.78,2.34-1.1a6.16,6.16,0,0,1,2.45-.55,4.68,4.68,0,0,1,2.2.82,9.12,9.12,0,0,0,1.81,1,2.23,2.23,0,0,0,1.76-.08,7.66,7.66,0,0,0,2.35-2.19c.6-.76,1.1-1.42,1.55-1.93a8.53,8.53,0,0,1,1.11-1.1A2.2,2.2,0,0,1,270.26,175Z" style="fill:#455a64"></path><path d="M26,395.05s-5.16,13.76-7,17.46S19,456,19,456h117.7l1.66-35L81.69,386.83Z" style="fill:#263238"></path><path d="M276.11,339.33l46.33,8.74s-.88,20-.29,19.54-64.94-.61-64.94-.61Z" style="fill:#ae7461"></path><path d="M321.62,348l32.11,9.91,14.11,9.91L380.4,381.4s-.14,3.77-4.89,1.39a44.93,44.93,0,0,1-9.35-6.84l9.35,17.17s-3.77,3.77-6.84-1.67S361,379,361,379l-2.79-2.09s2.93,5.16,2.93,6.56-2.93,9.22-2.93,9.22L353.59,399a2.88,2.88,0,0,1-2.58-3.21,9.92,9.92,0,0,1,1.89-5l-5.59,4.89s-1.95-1.12-.28-4.75l2.1-2.23.69-3.49-1.67-2.66s-4.33.28-7.68-4.74-8.52-4-15.78-8.38-6.56-9.08-6.56-9.08Z" style="fill:#ae7461"></path><path d="M338.86,360.27a14.56,14.56,0,0,0,1.22,3.86,15.86,15.86,0,0,0,2.51,3.6,36.16,36.16,0,0,0,4,3.65,22.84,22.84,0,0,1,4.05,3.74,5.12,5.12,0,0,1,1,4.37,3.27,3.27,0,0,1-2.11,2.18,2.24,2.24,0,0,1-.83.09c-.19,0-.29-.05-.29-.07a5.08,5.08,0,0,0,1.06-.21,3.14,3.14,0,0,0,1.82-2.08,4.81,4.81,0,0,0-1-4,24.54,24.54,0,0,0-4-3.62,33.34,33.34,0,0,1-4-3.73,15.24,15.24,0,0,1-2.49-3.76,10.3,10.3,0,0,1-.85-2.91A3.14,3.14,0,0,1,338.86,360.27Z" style="fill:#6f4439"></path><path d="M365.86,375.19a3.09,3.09,0,0,1-.87-.38c-.54-.27-1.31-.68-2.26-1.21-1.89-1.05-4.47-2.57-7.25-4.34-2.23-1.41-4.25-2.77-5.85-3.89l.19,0c-1.44.44-2.62.79-3.45,1a5.7,5.7,0,0,1-1.32.3,6,6,0,0,1,1.24-.53l3.4-1.19.09,0,.09.06,5.88,3.79c2.79,1.77,5.33,3.33,7.17,4.46A28.55,28.55,0,0,1,365.86,375.19Z" style="fill:#6f4439"></path><path d="M358,375.74a4,4,0,0,1-2.56,1.61,4,4,0,0,1-3-.14c0-.16,1.35.05,2.89-.37S357.94,375.62,358,375.74Z" style="fill:#6f4439"></path><path d="M352.67,379a36.28,36.28,0,0,1,2.09,5.86,3.54,3.54,0,0,1-.28,2.51c-.32.7-.66,1.26-.9,1.76a8.35,8.35,0,0,0-.53,1.69,2.49,2.49,0,0,1,.19-1.84c.2-.54.53-1.13.8-1.8a3.15,3.15,0,0,0,.22-2.19A38.31,38.31,0,0,1,352.67,379Z" style="fill:#6f4439"></path><path d="M22.75,388.14l41.3-44.57s54.34-93.31,65.15-101.91c27-21.51,57.7-10.25,57.7-10.25l10.94-4.15,18.42,46.79s-6.83,10-7.51,12S126.15,412,126.15,412L96.89,453s-45.36-8.5-64.78-33.27C23.24,408.35,22.75,388.14,22.75,388.14Z" style="fill:#455a64"></path><path d="M220.83,273l6.06-5.17L227,283.6l10.47,14.8s-30.66,36.92-37,43.08-56.75,66-56.75,66-2.67,28.4-6.06,36.45-26.3,5.93-26.3,5.93l11.38-32.59,4.73-23.52L184.71,318l22.46-29.41Z" style="fill:#455a64"></path><path d="M205.5,323.68c0,.94,31.15,30.72,31.15,30.72s2.25,5.64,4.82,6.58,15.41,6.89,15.41,6.89L277,339s-10.17-9.4-13.06-11-9.84-9.66-9.84-9.66l-23.32-28-17.89,9.13Z" style="fill:#455a64"></path><path d="M207.17,288.63a1.37,1.37,0,0,1,.14-.28l.45-.78c.44-.72,1-1.72,1.78-3,1.59-2.6,3.82-6.25,6.54-10.67l.23.31-16.94,4.52-.25.07-.07-.25c-.57-2.18-1.17-4.45-1.78-6.77-2.9-11.13-5.52-21.2-7.43-28.55l-2.2-8.66-.57-2.37a6.3,6.3,0,0,1-.17-.83,4.88,4.88,0,0,1,.27.81c.17.58.39,1.35.67,2.33.57,2.09,1.37,5,2.35,8.63,1.95,7.34,4.63,17.39,7.58,28.51.61,2.32,1.21,4.59,1.78,6.77l-.31-.19,17-4.42.52-.14-.29.45c-2.77,4.39-5.06,8-6.69,10.57l-1.87,2.91-.51.75C207.25,288.55,207.18,288.64,207.17,288.63Z" style="fill:#263238"></path><path d="M227,283a3.43,3.43,0,0,1-.55.52l-1.62,1.36-5.85,4.75-.42.34.06-.54c.32-3,.69-6.51,1.09-10.26.12-1.09.24-2.17.35-3.2l.44.21c-3.6,3.42-6.79,6.41-9.08,8.54l-2.73,2.49a6.68,6.68,0,0,1-1,.86,7.2,7.2,0,0,1,.91-1l2.61-2.6c2.24-2.2,5.38-5.24,9-8.67l.51-.49-.08.7c-.11,1-.23,2.11-.35,3.21-.42,3.74-.82,7.24-1.16,10.25l-.36-.2,6-4.61,1.7-1.26A3,3,0,0,1,227,283Z" style="fill:#263238"></path><path d="M230.78,305.29s-.08.16-.27.43-.53.69-.87,1.15c-.77,1-1.9,2.43-3.3,4.18-2.82,3.51-6.77,8.3-11.25,13.5s-8.63,9.82-11.68,13.12c-1.52,1.65-2.76,3-3.64,3.89l-1,1a2.6,2.6,0,0,1-.38.34,1.63,1.63,0,0,1,.3-.41l.94-1.1,3.52-4c3-3.37,7.08-8,11.56-13.22s8.47-9.95,11.35-13.39l3.43-4.07,1-1.1A2.07,2.07,0,0,1,230.78,305.29Z" style="fill:#263238"></path><path d="M262.52,328.22a7.28,7.28,0,0,1-.77,1.15c-.52.73-1.3,1.76-2.28,3-2,2.51-4.77,5.9-8.08,9.46s-6.49,6.62-8.85,8.76c-1.18,1.07-2.15,1.92-2.84,2.49a8.28,8.28,0,0,1-1.09.85,6.7,6.7,0,0,1,1-1l2.72-2.61c2.29-2.21,5.41-5.29,8.72-8.84s6.14-6.9,8.18-9.34l2.4-2.9A7.28,7.28,0,0,1,262.52,328.22Z" style="fill:#263238"></path><path d="M106.69,440.31s.08-.15.25-.41l.75-1.16,2.94-4.42,10.9-16.2c9.22-13.68,22-32.52,36-53.44s26.54-39.94,34.87-54.18c4.29-7.05,8-12.6,10.81-16.27,1.38-1.85,2.51-3.25,3.29-4.17l.9-1.05a4,4,0,0,1,.32-.35l-.28.38-.85,1.08c-.76.95-1.86,2.37-3.22,4.23-2.74,3.7-6.41,9.27-10.65,16.34C184.46,325,171.91,344,158,365s-26.83,39.73-36.12,53.35l-11,16.11-3,4.37-.8,1.13C106.8,440.18,106.69,440.31,106.69,440.31Z" style="fill:#263238"></path><path d="M109,390.67s-.16-.09-.43-.3l-1.16-.94c-1-.82-2.42-2-4.15-3.58-3.45-3.08-8.1-7.49-13-12.63s-9-10-11.87-13.66c-1.44-1.81-2.58-3.3-3.35-4.34-.35-.49-.63-.89-.87-1.21s-.29-.43-.27-.45a2.65,2.65,0,0,1,.36.38c.25.31.56.69.95,1.15l3.47,4.24c2.94,3.56,7.11,8.4,12,13.54s9.44,9.56,12.83,12.7l4,3.71c.43.41.8.74,1.09,1A2.26,2.26,0,0,1,109,390.67Z" style="fill:#263238"></path><path d="M172.8,246.21c-.05,0-.44-.65-1.33-1.75a17.79,17.79,0,0,0-4.51-3.84,29.6,29.6,0,0,0-8.21-3.2A37.63,37.63,0,0,0,148,236.17a23.85,23.85,0,0,0-10.34,2.74,24.15,24.15,0,0,0-6.83,5.51,26.52,26.52,0,0,0-3.34,4.93c-.66,1.25-1,2-1,2a2,2,0,0,1,.17-.54c.14-.35.33-.87.65-1.51A23.54,23.54,0,0,1,148,235.65,36.69,36.69,0,0,1,158.87,237a29.23,29.23,0,0,1,8.29,3.34,17.07,17.07,0,0,1,4.47,4,13.22,13.22,0,0,1,.91,1.36A3.49,3.49,0,0,1,172.8,246.21Z" style="fill:#263238"></path><path d="M122.74,259.49s-1.76,61.83,0,91.13c.54,8.95,3.86,14.86,4.55,15.06,1.8.53,38.1-2.54,38.1-2.54s.29-11.09,3-15.26-4.28-2.42-4.28-2.42,15-76.21,13.12-90.24" style="fill:#455a64"></path><path d="M177.26,255.22s.1.6.18,1.75.07,2.86-.06,5.07c-.21,4.42-.88,10.85-2,18.88-2.19,16.07-6,38.58-11,64.59l-.31-.29a13.29,13.29,0,0,1,2.82-.37,4.22,4.22,0,0,1,1.47.21,1.59,1.59,0,0,1,.68.46,1.28,1.28,0,0,1,.26.83,3.37,3.37,0,0,1-.53,1.43c-.25.43-.47.8-.68,1.22a16.64,16.64,0,0,0-.94,2.65,54.46,54.46,0,0,0-1.51,11.5v.23l-.23,0L150,364.65c-4.47.34-8.88.67-13.24,1-2.18.14-4.34.28-6.5.35-.53,0-1.07,0-1.61,0a5.78,5.78,0,0,1-.83,0l-.43,0c-.11,0-.41-.17-.46-.26a11.67,11.67,0,0,1-1.62-2.83,37.06,37.06,0,0,1-2.76-12.16q-.15-3-.3-6c-.06-2-.13-4-.19-5.89-.38-15.54-.28-29.53-.15-41.27s.36-21.25.53-27.81c.1-3.27.18-5.82.23-7.56,0-.85.06-1.51.08-2s0-.67,0-.67,0,.23,0,.67,0,1.12,0,2c0,1.75-.07,4.29-.13,7.56-.11,6.57-.27,16.07-.36,27.81s-.14,25.73.26,41.25c.07,1.94.13,3.91.2,5.89s.2,4,.3,6a36.34,36.34,0,0,0,2.74,12,11.63,11.63,0,0,0,1.51,2.68.32.32,0,0,0,.1.09s0,0,.12,0l.36,0a6.68,6.68,0,0,0,.78,0c.53,0,1.06,0,1.6,0,2.14-.08,4.3-.22,6.48-.36,4.35-.28,8.76-.61,13.23-.95l15.46-1.25-.24.25a55.11,55.11,0,0,1,1.54-11.61,17.84,17.84,0,0,1,1-2.74,9.13,9.13,0,0,0,1.16-2.45.78.78,0,0,0-.61-.8,3.87,3.87,0,0,0-1.29-.18,13,13,0,0,0-2.7.35l-.37.09.07-.38c5-26,8.93-48.49,11.2-64.53,1.13-8,1.85-14.44,2.11-18.85.16-2.2.16-3.91.14-5.06,0-.57-.06-1-.07-1.3S177.26,255.22,177.26,255.22Z" style="fill:#263238"></path><path d="M165.29,345.84a8.93,8.93,0,0,1-1.57.32c-1,.17-2.49.39-4.32.61-3.66.45-8.73.91-14.34,1.15s-10.71.22-14.39.08c-1.84-.07-3.33-.15-4.35-.24a9.69,9.69,0,0,1-1.59-.18,9,9,0,0,1,1.6,0l4.35.07c3.67.05,8.75,0,14.36-.23s10.66-.64,14.32-1l4.33-.44A10.38,10.38,0,0,1,165.29,345.84Z" style="fill:#263238"></path><polygon points="127.29 365.68 148.63 455.54 169.98 455.54 164.01 363.4 127.29 365.68" style="fill:#ae7461"></polygon><path d="M168.08,442.83l21.28,3.72,19.15,5.79c1.78.61,2.91,1.63,2.2,3.86h0l-62.08-.66V444.75Z" style="fill:#ae7461"></path><path d="M202.12,456.11a11.07,11.07,0,0,0,0-1.22A3.08,3.08,0,0,0,200,452.6a24,24,0,0,0-4.89-.69l-6.09-.62c-4.32-.46-8.23-.9-11.06-1.24l-3.35-.44a5.3,5.3,0,0,1-1.22-.23,6.53,6.53,0,0,1,1.24,0l3.37.27c2.83.25,6.75.64,11.07,1.09l6.09.66,2.66.31a8.23,8.23,0,0,1,2.31.52,3.24,3.24,0,0,1,2.12,2.6,2.89,2.89,0,0,1-.05.94A1.43,1.43,0,0,1,202.12,456.11Z" style="fill:#6f4439"></path><g style="opacity:0.30000000000000004"><path d="M165.29,382.36l7.51-32.77V339.33l-2.82-1.8,6.6-67.22-12.44,75.15,1.5-.13c1.71-.14,3.84-.54,3.43,1.12l-2.34,5.14-2.6,13.61Z"></path></g><path d="M235.75,224.22c-2.37,2.48-3.07,6.05-1.1,8.86a5,5,0,0,0,7,.88,9,9,0,0,0,2.53-3.46C241.87,228.66,238.08,226.06,235.75,224.22Z" style="fill:#6f4439"></path><path d="M240.9,234.55a2.5,2.5,0,0,1-1,.37,5.45,5.45,0,0,1-2.88-.27,5.66,5.66,0,0,1-3.14-2.92,6.53,6.53,0,0,1-.12-5.23,8,8,0,0,1,1.46-2.4l.15-.17.19.14c2.43,1.8,4.55,3.39,6,4.55a20.74,20.74,0,0,1,2.35,1.92,18.77,18.77,0,0,1-2.54-1.66c-1.55-1.08-3.71-2.62-6.15-4.4l.34,0a7.6,7.6,0,0,0-1.36,2.25,6.14,6.14,0,0,0,.08,4.84,5.34,5.34,0,0,0,2.83,2.78,5.6,5.6,0,0,0,2.72.41C240.53,234.64,240.88,234.51,240.9,234.55Z" style="fill:#263238"></path></g></svg>
            </figure>
            <!-- SVG END -->
          </div>
          <!-- Card START -->
          <div class="col-md-6 text-center">
            <!-- Title -->
            <h1>Hi Sam, we're here to help.</h1>
            <p class="mb-4">Search here to get answers to your questions.</p>
            <!-- Search form START -->
            <form class="rounded position-relative">
              <input class="form-control ps-5" type="search" placeholder="Search..." aria-label="Search">
              <button class="btn bg-transparent px-2 py-0 position-absolute top-50 start-0 translate-middle-y" type="submit"><i class="bi bi-search fs-5 ps-1"> </i></button>
            </form>
            <!-- Search form END -->
          </div>
          <div class="col-md-3">
            <!-- SVG START -->
            <figure class="m-0 d-none d-md-block">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 500 500"><g id="freepik--Plant--inject-2"><path d="M74.26,342.83c5.24-2.7,11.4-3.05,17.21-2.09,3.12.52,6.37,1.45,8.5,3.8s2.52,6.47.08,8.48c-1.45,1.2-3.47,1.38-5.35,1.36-4.55-.05-9.6-.85-13.23,1.88-2.08,1.57-3.46,4.16-5.93,4.93s-5.17-.68-6.45-2.87a10,10,0,0,1-.73-7.39A11.28,11.28,0,0,1,74.26,342.83Z" class="fill-primary"></path><path d="M62.27,319.69c3.2,4.57,9.42,3.05,12.31.27s4.1-6.84,5.21-10.69c2.53-8.81,5.09-18,3.2-27a14.93,14.93,0,0,0-3.45-7.34,7.84,7.84,0,0,0-7.43-2.48c-3.14.83-5.08,3.9-6.49,6.83a64.33,64.33,0,0,0-6.37,27.64c0,4.45.51,9.12,3,12.8" class="fill-primary"></path><path d="M53.48,326.25c3.48-4.13,4.14-10.14,2.59-15.31s-5-9.56-9.06-13.15a48.81,48.81,0,0,0-16.23-9.57,17.2,17.2,0,0,0-7.3-1.24,7.66,7.66,0,0,0-6.13,3.7c-1.71,3.26-.07,7.21,1.7,10.44a123.66,123.66,0,0,0,10.66,16.23c3.2,4.12,6.86,8.18,11.73,10S51.37,329,54,325.66" class="fill-primary"></path><path d="M62.06,388.63a10.36,10.36,0,0,0,.29-2.07c.14-1.52.33-3.41.55-5.67a81.17,81.17,0,0,1,1.27-8.32,65.1,65.1,0,0,1,3-9.81,36.33,36.33,0,0,1,5-8.88,18.63,18.63,0,0,1,6.34-5.26A17.67,17.67,0,0,1,83.87,347a10.14,10.14,0,0,0,2.07-.3,9.45,9.45,0,0,0-2.1,0,16.31,16.31,0,0,0-5.57,1.44,18.74,18.74,0,0,0-6.68,5.31,35.56,35.56,0,0,0-5.19,9.06,60,60,0,0,0-3,10,70.8,70.8,0,0,0-1.11,8.42c-.18,2.41-.24,4.36-.28,5.71A9.23,9.23,0,0,0,62.06,388.63Z" style="fill:#263238"></path><path d="M62.45,384.27a4.45,4.45,0,0,0,0-1v-2.76c0-2.39,0-5.85.12-10.12.21-8.55,1-17.75,2.37-30.72,1.34-12.19,3.15-26.39,5.06-35.61.4-2.1.82-4,1.2-5.64s.69-3.09,1-4.25.48-2,.65-2.67a4,4,0,0,0,.17-1,3.58,3.58,0,0,0-.34.9c-.21.69-.47,1.57-.8,2.64s-.72,2.56-1.13,4.22-.89,3.53-1.34,5.62a315.74,315.74,0,0,0-5.32,33c-1.42,13-2.06,24.83-2.12,33.39,0,4.29,0,7.76.15,10.15.07,1.12.13,2,.17,2.75A4.1,4.1,0,0,0,62.45,384.27Z" style="fill:#263238"></path><path d="M62.5,361.45a18.15,18.15,0,0,0,0-2.88,78.38,78.38,0,0,0-.88-7.8,102.25,102.25,0,0,0-2.53-11.36A92.88,92.88,0,0,0,54.31,326a71.64,71.64,0,0,0-14-21.73,36.68,36.68,0,0,0-3.28-3.05c-.51-.42-.95-.85-1.41-1.18l-1.27-.88A18,18,0,0,0,32,297.57c-.14.18,3.43,2.29,7.94,7.08A75.57,75.57,0,0,1,53.53,326.3a107.1,107.1,0,0,1,4.78,13.32c1.24,4.18,2.07,8,2.69,11.25s.94,5.89,1.15,7.73A20.42,20.42,0,0,0,62.5,361.45Z" style="fill:#263238"></path><polygon points="40.45 401.71 42.73 433.04 82.98 433.04 85.27 401.71 40.45 401.71" style="fill:#263238"></polygon><rect x="36.56" y="389.36" width="53.51" height="12.35" style="fill:#263238"></rect><polygon points="36.56 431.84 36.56 435.26 39.07 435.26 89.19 435.26 89.19 431.84 36.56 431.84" style="fill:#455a64"></polygon><polygon points="41.54 441.29 39.07 435.26 87.27 435.26 84.39 441.29 41.54 441.29" style="fill:#455a64"></polygon><path d="M84.52,412a1.22,1.22,0,0,1-.19.28c-.16.22-.35.48-.6.8l-2.34,3-.11.14-.1-.14-5.76-8h.33l-5.67,8-.18.25-.2-.23-6.72-8h.39c-.12.14-.25.3-.37.46l-6,7.54-.2.25-.18-.26-5.68-8,.33,0c-2.79,3.19-5.17,5.89-7,8l-.12.14-.09-.16c-.93-1.54-1.66-2.78-2.19-3.67-.23-.4-.42-.72-.56-1a1.41,1.41,0,0,1-.18-.35s.09.1.24.31l.62.94,2.3,3.61-.22,0,6.87-8.06.18-.2.15.22,5.73,8H56.6l6-7.55.37-.46.2-.24.19.23c2.36,2.81,4.62,5.52,6.71,8h-.38l5.73-8,.17-.23.16.23,5.64,8.06h-.2L83.63,413l.65-.76Q84.51,411.93,84.52,412Z" style="fill:#e0e0e0"></path><path d="M85.83,401.5c0,.14-10.09.25-22.52.25s-22.53-.11-22.53-.25,10.08-.25,22.53-.25S85.83,401.36,85.83,401.5Z" style="fill:#455a64"></path><path d="M87.86,435.6c0,.14-11,.25-24.48.25s-24.48-.11-24.48-.25,11-.25,24.48-.25S87.86,435.46,87.86,435.6Z" style="fill:#263238"></path></g><g id="freepik--Chair--inject-2"><path d="M363.1,309.11h93.53a15.13,15.13,0,0,1,15.13,15.13V441.86a0,0,0,0,1,0,0H348a0,0,0,0,1,0,0V324.24A15.13,15.13,0,0,1,363.1,309.11Z" style="fill:#455a64"></path><path d="M461.34,309.79c.15,0,.26,29.55.26,66s-.11,66-.26,66-.26-29.55-.26-66S461.2,309.79,461.34,309.79Z" style="fill:#263238"></path></g><g id="freepik--Character--inject-2"><path d="M400.36,165a105.06,105.06,0,0,1,18.82,55.34c.58,13.3-1.21,27.47,5.1,39.19,9.06,16.82,28.65,16.25,34.65,34.38,2.61,7.89-2.2,25.24-3.5,33.45-1.47,9.32,4.23,17.15,2.76,26.48-1.35,8.55-7.75,14.65-15.43,18.63-6.84,3.54-13.16,3.64-20.27.69-13.56-5.61-24.84-11.53-34.49-22.57-13.56-15.5-17.66-37-20-57.43a503.87,503.87,0,0,1,3.07-135.75" style="fill:#263238"></path><path d="M429.35,375.07a3.59,3.59,0,0,1,.43-.11c.29-.07.73-.14,1.29-.31a22,22,0,0,0,10.86-6.83,12.81,12.81,0,0,0,2.42-4.29,23.54,23.54,0,0,0,.85-5.49,54.09,54.09,0,0,0-1.3-12.74c-.87-4.54-2-9.38-2.34-14.63a30.3,30.3,0,0,1,.57-8,51.29,51.29,0,0,1,2.54-8,85.74,85.74,0,0,0,2.86-8.13,20.74,20.74,0,0,0,.7-8.7,19.67,19.67,0,0,0-3.25-8.08,37.08,37.08,0,0,0-5.67-6.42c-4.13-3.86-8.6-7-12.42-10.62a70.35,70.35,0,0,1-16.18-22.34,68.47,68.47,0,0,1-3.72-10c-.48-1.49-.76-2.93-1.1-4.23s-.49-2.52-.71-3.6c-.31-2.19-.55-3.88-.61-5,0-.56-.08-1-.1-1.32s0-.45,0-.45,0,.15.07.45.09.75.16,1.31c.09,1.15.36,2.84.7,5,.23,1.08.48,2.28.75,3.59s.64,2.71,1.14,4.2a69.73,69.73,0,0,0,3.77,9.9,70.68,70.68,0,0,0,16.17,22.14c3.8,3.54,8.28,6.71,12.45,10.59a37.85,37.85,0,0,1,5.75,6.51,20.16,20.16,0,0,1,3.33,8.3,21.2,21.2,0,0,1-.71,8.91c-.78,2.84-1.87,5.53-2.88,8.17a52.13,52.13,0,0,0-2.54,7.9,29.77,29.77,0,0,0-.57,7.89c.27,5.18,1.43,10,2.29,14.57a54.48,54.48,0,0,1,1.24,12.84,24.56,24.56,0,0,1-.9,5.58,13.53,13.53,0,0,1-2.51,4.37,20.72,20.72,0,0,1-6.32,4.95,20.24,20.24,0,0,1-4.75,1.8A11.69,11.69,0,0,1,429.35,375.07Z" style="fill:#455a64"></path><path d="M243.16,410.8l48-22.79s18.69-90.75,22.23-97.56,18.3-12.7,18.3-12.7l-2.47,122.88L324.82,429l-67.68,6.41Z" class="fill-primary"></path><path d="M202.21,432.3c-.27,0-1.6.82-.49,2.93s9.25,9.08,11.64,6.65-1.26-5.39-1.26-5.39Z" style="fill:#ffbe9d"></path><path d="M212.44,436.58s9.82,1.24,13.59,2.68c4.11,1.56,9.11.49,12.47-1.78a35.49,35.49,0,0,0,5.32-4.34l12.52-.2a15,15,0,0,0,2.44-15.51,16.83,16.83,0,0,0-2.86-4.89c-1.49-1.61-3.84-2.91-7.9-3.61l-14.87,3.21s-23-1.65-27.14.29c0,0-5.15,3.05-6.74,4.33-.6.49-2.43,6.35,1.32,9.4,0,0-3.26,2.59-1.49,4.92l.84,1s-2.19,2.84-1.06,4.08,11,7,14.51,5.64.61-4.65-.95-5.25" style="fill:#ffbe9d"></path><path d="M203.06,421.09a10.83,10.83,0,0,1,1.22.44c.77.29,1.9.71,3.32,1.16.7.23,1.48.46,2.33.68s1.76.41,2.71.72a5.79,5.79,0,0,1,2.7,1.63,3.31,3.31,0,0,1,.72,3.29,2.58,2.58,0,0,1-1.21,1.31,4.7,4.7,0,0,1-1.64.46,13.87,13.87,0,0,1-3.13-.17,28,28,0,0,1-5.08-1.32c-1.41-.5-2.52-1-3.29-1.3a6.53,6.53,0,0,1-1.17-.56,10.54,10.54,0,0,1,1.23.41c.78.28,1.91.71,3.32,1.17a30,30,0,0,0,5,1.23,13.82,13.82,0,0,0,3,.14,4.19,4.19,0,0,0,1.49-.42,2.16,2.16,0,0,0,1-1.1A2.93,2.93,0,0,0,215,426a5.47,5.47,0,0,0-2.51-1.53c-.92-.31-1.84-.51-2.68-.75s-1.63-.49-2.33-.73c-1.42-.49-2.53-1-3.29-1.29A6.83,6.83,0,0,1,203.06,421.09Z" style="fill:#eb996e"></path><path d="M203.94,414a8.43,8.43,0,0,1,1.21.44c.78.3,1.9.73,3.3,1.19s3.09,1,5,1.43a6.9,6.9,0,0,1,2.91,1.18,2.43,2.43,0,0,1,.85,1.51,3.11,3.11,0,0,1-.25,1.75,4.19,4.19,0,0,1-2.57,2.18,6.75,6.75,0,0,1-3.13.25,30.08,30.08,0,0,1-5-1.46l-3.31-1.16a9.24,9.24,0,0,1-1.2-.47,7.63,7.63,0,0,1,1.25.32l3.35,1a34,34,0,0,0,5,1.36,6.49,6.49,0,0,0,2.95-.26,3.83,3.83,0,0,0,2.33-2,2.33,2.33,0,0,0-.49-2.79,6.65,6.65,0,0,0-2.74-1.11c-1.91-.48-3.6-1-5-1.52s-2.5-1-3.26-1.32A8.06,8.06,0,0,1,203.94,414Z" style="fill:#eb996e"></path><path d="M208.67,416a12.45,12.45,0,0,1-.45-2,11,11,0,0,1-.49-2,3.53,3.53,0,0,1,.89,1.87A3.32,3.32,0,0,1,208.67,416Z" style="fill:#eb996e"></path><path d="M243.3,416.32,234.55,413s-24.29-11.34-24.7-10.72-1.73.54-1.44,3.5,14.44,8.94,14.89,10-.3,6.22,4.93,9.93,9,3.8,9,3.8" style="fill:#ffbe9d"></path><path d="M237.23,429.42H237l-.6-.06a14.55,14.55,0,0,1-2.27-.55,22.21,22.21,0,0,1-7.42-4.27,10.46,10.46,0,0,1-3-5.11,23.22,23.22,0,0,1-.52-3.27,1.82,1.82,0,0,0-.09-.37.72.72,0,0,0-.23-.24,7.92,7.92,0,0,0-.71-.46c-.49-.29-1-.57-1.54-.85-2.1-1.13-4.35-2.26-6.61-3.56a39.45,39.45,0,0,1-3.37-2.13,12.28,12.28,0,0,1-1.58-1.32,4,4,0,0,1-.67-.84,1.64,1.64,0,0,1-.2-.55,2.47,2.47,0,0,1,0-.54,3.83,3.83,0,0,1,.37-2.11,2.57,2.57,0,0,1,.77-.77,1.71,1.71,0,0,0,.34-.29s0,0,.08-.09a.25.25,0,0,1,.17-.05.76.76,0,0,1,.17,0l.27.07c1.31.4,2.52.93,3.73,1.43,2.4,1,4.69,2,6.87,3,4.37,2,8.27,3.78,11.55,5.32.82.37,1.59.76,2.33,1.08l2.14.84,3.43,1.36,2.16.87.55.24c.13,0,.19.08.19.09s-.07,0-.2-.06l-.57-.19-2.19-.81L236.89,414l-2.16-.81c-.76-.31-1.53-.69-2.35-1.06l-11.58-5.23c-2.19-1-4.48-2-6.88-3-1.2-.49-2.42-1-3.68-1.41l-.22-.05s-.08,0-.08,0,0,0,0,0,0-.06,0-.1l0,0c.28.18.07.06.13.1l0,0a1.83,1.83,0,0,1-.42.38,2.05,2.05,0,0,0-.66.65,3.48,3.48,0,0,0-.32,1.89,1.87,1.87,0,0,0,.21.9,3.85,3.85,0,0,0,.6.76,11.72,11.72,0,0,0,1.53,1.27c1.08.78,2.21,1.46,3.33,2.11,2.25,1.3,4.48,2.44,6.59,3.57.53.29,1,.57,1.55.88a8.63,8.63,0,0,1,.75.48,1,1,0,0,1,.34.39,2,2,0,0,1,.1.45,25.93,25.93,0,0,0,.49,3.24,10.28,10.28,0,0,0,2.86,5,22.74,22.74,0,0,0,7.27,4.33,16.07,16.07,0,0,0,2.23.61c.26.06.46.08.6.1Z" style="fill:#eb996e"></path><path d="M210.44,430.56a3,3,0,0,1,.93.27,9.41,9.41,0,0,1,2.29,1.32,8.39,8.39,0,0,1,1.32,1.29,2.73,2.73,0,0,1,.71,2,2.26,2.26,0,0,1-1.62,1.75,4.32,4.32,0,0,1-2.54-.08,46.55,46.55,0,0,1-8.06-3.23,24.15,24.15,0,0,1-2.26-1.34,3.86,3.86,0,0,1-.77-.58,4.72,4.72,0,0,1,.85.44c.54.31,1.32.74,2.31,1.22a56.46,56.46,0,0,0,8,3.1,4,4,0,0,0,2.32.09,1.9,1.9,0,0,0,1.37-1.42,2.45,2.45,0,0,0-.6-1.78,8.33,8.33,0,0,0-1.24-1.27A13.89,13.89,0,0,0,210.44,430.56Z" style="fill:#eb996e"></path><g style="opacity:0.30000000000000004"><path d="M324.48,429c-7.86-26.51-14.79-60.65-22.65-87.16-2.88,24.47.72,56.78,5.22,81,.53,2.84.77,5.62,3,7.49L324.48,429"></path></g><path d="M311.61,410.8c-.11,0-.44-1.73-1.51-4.38a31.09,31.09,0,0,0-2.25-4.46,33.83,33.83,0,0,0-3.7-4.86,32.94,32.94,0,0,0-4.58-4.05,29.8,29.8,0,0,0-4.28-2.58c-2.56-1.26-4.28-1.72-4.25-1.82a5.38,5.38,0,0,1,1.22.29,10.15,10.15,0,0,1,1.39.46,18.21,18.21,0,0,1,1.8.74,28,28,0,0,1,4.4,2.52,32,32,0,0,1,4.69,4.08,32.35,32.35,0,0,1,3.72,5,28.6,28.6,0,0,1,2.19,4.57c.27.67.43,1.3.6,1.85a10.28,10.28,0,0,1,.36,1.43A6.39,6.39,0,0,1,311.61,410.8Z" style="fill:#263238"></path><path d="M326.59,276.51S301.46,332.66,301.46,343s18.83,81.93,18.83,81.93c-.18,2.85-.91,17.12-.91,17.12l105.59.37h0l1.61-22.7-8.24-21.63,8.23-61.15s5.54-35.5,6-45.43c.62-12.42-41.46-26.82-40.31-28.54L385,253.21,343.39,287l-8.23-23.76-9.86,9.31Z" class="fill-primary"></path><path d="M395.74,291.45C384.31,310.78,363.4,346,363.4,346l-37.48,42.74s-2.45,21.71,2.45,28.37,13.31,11.21,13.31,11.21,9.89,0,22.46-10.51,47.24-53.94,47.24-53.94l11.91-14.37s14.2-34.88,11.55-52.63c-1-6.85-3.94-9.69-7.82-12.83C417.25,276.17,402.13,280.64,395.74,291.45Z" class="fill-primary"></path><path d="M427.7,337.4a3.09,3.09,0,0,1-.18.4c-.13.28-.32.65-.55,1.14-.5,1-1.26,2.45-2.3,4.29-2.07,3.69-5.29,8.9-9.49,15.19-2.11,3.14-4.46,6.55-7.05,10.17s-5.53,7.35-7.95,11.71-4.62,9.12-7.27,13.88a87.68,87.68,0,0,1-9.53,13.91,55.74,55.74,0,0,1-12.62,11.16,39.74,39.74,0,0,1-14.62,5.66,32.77,32.77,0,0,1-14.16-.6,26.83,26.83,0,0,1-6-2.43,27.39,27.39,0,0,1-4.94-3.34,22.41,22.41,0,0,1-3.72-3.93,22.1,22.1,0,0,1-2.46-4.16,26.32,26.32,0,0,1-2-7.44,34.38,34.38,0,0,1-.17-4.87c0-.54.06-1,.08-1.27a3,3,0,0,1,0-.43,2.85,2.85,0,0,1,0,.43c0,.31,0,.73,0,1.27a37,37,0,0,0,.26,4.84,26.58,26.58,0,0,0,2.08,7.35,22.35,22.35,0,0,0,2.46,4.08,21.91,21.91,0,0,0,3.7,3.85,27.06,27.06,0,0,0,4.89,3.27,26.74,26.74,0,0,0,5.91,2.37,32.55,32.55,0,0,0,14,.54,39.34,39.34,0,0,0,14.42-5.62A55.45,55.45,0,0,0,383,407.75a86.77,86.77,0,0,0,9.47-13.82c2.65-4.73,4.79-9.53,7.3-13.87a130.12,130.12,0,0,1,8-11.73c2.6-3.6,5-7,7.09-10.12,4.24-6.25,7.5-11.43,9.62-15.08,1.07-1.83,1.86-3.26,2.38-4.24l.61-1.12A1.93,1.93,0,0,1,427.7,337.4Z" style="fill:#263238"></path><path d="M324,386.83c0,.07-.53.4-1.22,1.17a6,6,0,0,0-.68,7.36c.53.88,1,1.31,1,1.37s-.63-.28-1.29-1.16a5.9,5.9,0,0,1,.73-7.83A3,3,0,0,1,324,386.83Z" style="fill:#263238"></path><path d="M330.93,398.17a3,3,0,0,1,.59.14l.7.19,1,.18a13.93,13.93,0,0,0,6.18-.58,22.63,22.63,0,0,0,8-4.68,60.24,60.24,0,0,0,7.45-8.65,52.29,52.29,0,0,1,7.69-8.59c1.36-1.14,2.69-2.15,3.8-3.24a10.65,10.65,0,0,0,2.5-3.45,6.29,6.29,0,0,0-.57-5.9,4.64,4.64,0,0,0-1.81-1.35,3.07,3.07,0,0,1,.58.17,3.83,3.83,0,0,1,1.39,1.06,6.46,6.46,0,0,1,.76,6.16,11.12,11.12,0,0,1-2.54,3.62c-1.13,1.12-2.47,2.16-3.8,3.3a53.4,53.4,0,0,0-7.58,8.54,59.05,59.05,0,0,1-7.56,8.7,22.63,22.63,0,0,1-8.16,4.66,13.59,13.59,0,0,1-6.33.43l-1-.23c-.28-.08-.51-.18-.69-.24A2.71,2.71,0,0,1,330.93,398.17Z" style="fill:#263238"></path><path d="M400.83,285.51a5.42,5.42,0,0,1-.42.6c-.33.42-.75,1-1.28,1.67-1.14,1.42-2.71,3.54-4.63,6.18a202.28,202.28,0,0,0-13.33,21.67c-4.72,8.76-8.62,16.89-11.61,22.69-1.48,2.91-2.7,5.25-3.59,6.85L365,347a3.67,3.67,0,0,1-.39.62,3.88,3.88,0,0,1,.29-.67l.94-1.89,3.43-6.91c2.9-5.85,6.76-14,11.48-22.78a182,182,0,0,1,13.5-21.64c2-2.61,3.57-4.71,4.76-6.09l1.36-1.61A3.18,3.18,0,0,1,400.83,285.51Z" style="fill:#263238"></path><g style="opacity:0.30000000000000004"><path d="M322.8,396.44c4.87,2.64,10.23,2.36,15.67,1.3a24.32,24.32,0,0,0,14-8.38A24.29,24.29,0,0,1,346,402.14c-3.46,3.39-8.48,5.35-13.24,4.48s-9.58-5.35-10-10.18"></path></g><g style="opacity:0.30000000000000004"><path d="M368.9,367.58a21.49,21.49,0,0,1,3,16.12c4.13-6.32,5.1-14.32,4-21.79s-4.15-14.52-7.53-21.27c0,0-3.72,6.3-3.76,7-.18,3.64,1.16,10.48,1.86,14.05a7.81,7.81,0,0,1,2.27,7.06"></path></g><g style="opacity:0.30000000000000004"><path d="M339.32,422.6c9.94,7.75,24.69,8.11,35.56,1.75,10.47-6.13,16.92-17.17,22.9-27.72,9.27-16.34,20.57-36.55,27.19-54.13,0,0-18.55,25.31-24.14,36.22A180.05,180.05,0,0,1,388.17,400a54.56,54.56,0,0,1-47.88,23.33"></path></g><path d="M319.38,442a1,1,0,0,1,0-.31c0-.24,0-.54,0-.91,0-.85.07-2,.13-3.47.14-3,.33-7.3.57-12.43V425c-1.79-6-3.89-13.28-6.07-21.14-2.88-10.42-5.31-19.9-6.95-26.79-.81-3.44-1.45-6.24-1.85-8.18-.19-.93-.34-1.68-.45-2.23a6.68,6.68,0,0,1-.13-.79,4.09,4.09,0,0,1,.23.76l.55,2.22c.49,2,1.17,4.74,2,8.13,1.73,6.87,4.22,16.33,7.1,26.74,2.17,7.86,4.24,15.14,6,21.15v.07c-.31,5.13-.57,9.4-.76,12.44-.1,1.45-.18,2.61-.24,3.46,0,.36-.06.66-.08.9A1.05,1.05,0,0,1,319.38,442Z" style="fill:#263238"></path><path d="M425,283.46c0,.08-1.08-.3-2.81-.73a33.9,33.9,0,0,0-6.89-1,33.18,33.18,0,0,0-6.94.54c-1.76.33-2.83.63-2.85.55a2.58,2.58,0,0,1,.72-.32,20.62,20.62,0,0,1,2.05-.59,27.22,27.22,0,0,1,7-.7,27.63,27.63,0,0,1,7,1.12,17,17,0,0,1,2,.72C424.78,283.3,425,283.42,425,283.46Z" style="fill:#263238"></path><path d="M391.57,265a2.94,2.94,0,0,1-.27.52l-.85,1.43c-.75,1.24-1.86,3-3.27,5.19-2.83,4.33-6.88,10.24-11.67,16.52-3.85,5.07-7.58,9.62-10.63,13.18l-.22.25-.13-.3c-1.46-3.18-2.66-5.82-3.53-7.73-.39-.88-.7-1.6-.95-2.15a4,4,0,0,1-.29-.77,4.1,4.1,0,0,1,.41.72c.27.54.61,1.24,1.05,2.1.91,1.89,2.17,4.5,3.68,7.66l-.34-.05c3-3.6,6.69-8.16,10.54-13.22,4.78-6.28,8.86-12.14,11.77-16.42l3.41-5.09.93-1.38A2.21,2.21,0,0,1,391.57,265Z" style="fill:#263238"></path><path d="M415.88,426.27c-.15,0,.31-5.21,1-11.6s1.4-11.55,1.54-11.53-.32,5.2-1,11.59S416,426.28,415.88,426.27Z" style="fill:#263238"></path><path d="M343.5,424.9a74.48,74.48,0,0,1,1.19,8.72,70.84,70.84,0,0,1,.67,8.77,77.12,77.12,0,0,1-1.19-8.72A75.84,75.84,0,0,1,343.5,424.9Z" style="fill:#263238"></path><path d="M396.91,310.69a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M386.48,285.27a1.28,1.28,0,0,0-1.94-.77l-.06.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M362.55,305.74a1.28,1.28,0,0,0-1.94-.77l-.06,0a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M336.68,295.46a1.27,1.27,0,0,0-1.93-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M332.11,285.33a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.22,1.22,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M334.1,273.63a1.28,1.28,0,0,0-1.94-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M319.77,289.73a1.28,1.28,0,0,0-1.94-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M353,294.37a1.27,1.27,0,0,0-1.93-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M324.86,315.24a1.28,1.28,0,0,0-1.94-.77l-.07,0a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M299.14,375.26a1.28,1.28,0,0,0-1.94-.77l-.06.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M308.32,340.64a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M376,279.86a1.28,1.28,0,0,0-1.94-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M408.24,348.78a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.22,1.22,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M420.81,289.47a1.27,1.27,0,0,0-1.93-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M415.88,328.31a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.22,1.22,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M387.67,356.36a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M388.86,333a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.22,1.22,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M405.87,289.67a1.27,1.27,0,0,0-1.93-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M383,267.05a1.27,1.27,0,0,0-1.93-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M382.13,303.52a1.27,1.27,0,0,0-1.94-.77l-.06.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M352.89,414.77A1.27,1.27,0,0,0,351,414l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M319.34,396.47a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M292.21,405.44a1.28,1.28,0,0,0-1.94-.77l-.06.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M289.31,420.89a1.28,1.28,0,0,0-1.94-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M314.74,426.06a1.28,1.28,0,0,0-1.94-.77l-.06.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M306.24,391.46a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.22,1.22,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M337.44,400.26a1.27,1.27,0,0,0-1.94-.77l-.06.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M411.61,391.41a1.26,1.26,0,0,0-1.93-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M365.72,435.38a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M385.29,410.9a1.27,1.27,0,0,0-1.93-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M396.92,430.81A1.28,1.28,0,0,0,395,430l-.06.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M364.57,326.55a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M390.9,377.38a1.28,1.28,0,0,0-1.94-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M382.36,396.63a1.28,1.28,0,0,0-1.94-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M410.43,412.09a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M333.62,434.42a1.27,1.27,0,0,0-1.94-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M269.62,428.57a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.22,1.22,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M326,418.06a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.22,1.22,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M272,406.63a1.27,1.27,0,0,0-1.94-.77l-.06.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M362.2,370.33a1.28,1.28,0,0,0-1.94-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M343.32,315.24a1.28,1.28,0,0,0-1.94-.77l-.07,0a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M338.6,363a1.28,1.28,0,0,0-1.94-.77l-.06.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M330.92,347.43a1.28,1.28,0,0,0-1.94-.77l-.06,0a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M364.89,396.41a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.22,1.22,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M340.94,386.8A1.27,1.27,0,0,0,339,386l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M351.19,334.05a1.27,1.27,0,0,0-1.93-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M314.74,371.4a1.28,1.28,0,0,0-1.94-.77l-.06.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M371,354.26a1.28,1.28,0,0,0-1.94-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M423.19,309.5a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M402,275.61a1.28,1.28,0,0,0-1.94-.77l-.07.06a1.21,1.21,0,1,0,2,.71Z" style="fill:#fff"></path><path d="M360.94,279.86a1.28,1.28,0,0,0-1.94-.77l-.07.05a1.21,1.21,0,1,0,2,.72Z" style="fill:#fff"></path><path d="M319.05,158.21c-7.87,5-11,15.51-9.52,21.72.76,3.13,2.18,6.28,1.56,9.43-.53,2.73-2.5,4.92-4,7.28a21.92,21.92,0,0,0,27.4,31.66" style="fill:#263238"></path><path d="M314.39,199.2c-19.16-42.14,26.42-63.31,51.28-56.29,23.37,6.6,33.34,18.23,37.19,33.86s3.66,32.58-2.47,47.47c-3,7.34-8.46,14.8-16.35,15.72" style="fill:#263238"></path><path d="M336.84,302.11c-.33-20.14-.57-47.79-.57-47.68S319,251.56,315.37,229c-1.78-11.23-1.21-29.64-.27-44.69.86-13.55,9.83-30.17,23.33-28.74l42.15,10.65a7.93,7.93,0,0,1,7.1,8.06h0l-2.74,87.17" style="fill:#ffbe9d"></path><path d="M322.64,198.08a2.87,2.87,0,0,0,2.74,2.93,2.75,2.75,0,0,0,3-2.56,2.89,2.89,0,0,0-2.74-2.94A2.76,2.76,0,0,0,322.64,198.08Z" style="fill:#263238"></path><path d="M352.56,198.08A2.87,2.87,0,0,0,355.3,201a2.75,2.75,0,0,0,3-2.56,2.87,2.87,0,0,0-2.74-2.93A2.75,2.75,0,0,0,352.56,198.08Z" style="fill:#263238"></path><path d="M319.52,192.82c.35.39,2.6-1.16,5.71-1s5.34,1.74,5.7,1.37c.17-.16-.18-.85-1.15-1.61a7.9,7.9,0,0,0-4.54-1.57,7.66,7.66,0,0,0-4.56,1.31C319.7,192,319.35,192.64,319.52,192.82Z" style="fill:#263238"></path><path d="M349.1,193.56c.36.38,2.55-1.26,5.66-1.26s5.41,1.54,5.74,1.16c.17-.17-.2-.85-1.2-1.57a7.9,7.9,0,0,0-4.6-1.4,7.58,7.58,0,0,0-4.5,1.48C349.24,192.71,348.92,193.39,349.1,193.56Z" style="fill:#263238"></path><path d="M338.42,217.56c0-.18-1.92-.51-5.06-.92-.8-.08-1.55-.24-1.68-.78a4,4,0,0,1,.53-2.36c.74-1.92,1.53-3.94,2.35-6.06,3.27-8.62,5.63-15.72,5.28-15.85s-3.28,6.76-6.54,15.39l-2.26,6.1a4.58,4.58,0,0,0-.41,3.12,2,2,0,0,0,1.31,1.16,5.08,5.08,0,0,0,1.35.19C336.45,217.72,338.41,217.73,338.42,217.56Z" style="fill:#263238"></path><path d="M336.27,254.43a57.47,57.47,0,0,0,30.24-8.06s-7.64,15.7-30.24,13.59Z" style="fill:#eb996e"></path><path d="M348.33,189.13c.3.85,3.42.44,7.07.89s6.61,1.43,7.1.67c.22-.36-.3-1.16-1.49-2a12.41,12.41,0,0,0-10.8-1.19C348.87,188.06,348.19,188.73,348.33,189.13Z" style="fill:#263238"></path><path d="M320.24,185c.55.71,2.7,0,5.28-.06s4.77.48,5.27-.26c.22-.36-.12-1.07-1.08-1.74a7.3,7.3,0,0,0-4.29-1.16,7.38,7.38,0,0,0-4.21,1.42C320.3,183.91,320,184.64,320.24,185Z" style="fill:#263238"></path><path d="M383.81,204c.33-.16,12.09-5.36,12.23,8.16s-13.47,10.87-13.5,10.48S383.81,204,383.81,204Z" style="fill:#ffbe9d"></path><path d="M386.62,217.46c.06,0,.24.16.64.34a2.38,2.38,0,0,0,1.76,0c1.42-.54,2.6-2.82,2.64-5.26a7.57,7.57,0,0,0-.69-3.36,2.7,2.7,0,0,0-1.79-1.79,1.19,1.19,0,0,0-1.38.66c-.18.38-.09.65-.16.67s-.3-.22-.19-.78a1.49,1.49,0,0,1,.52-.85,1.76,1.76,0,0,1,1.3-.36,3.31,3.31,0,0,1,2.48,2.07,8,8,0,0,1,.86,3.76c-.06,2.72-1.43,5.29-3.38,5.87a2.54,2.54,0,0,1-2.16-.33C386.65,217.79,386.58,217.48,386.62,217.46Z" style="fill:#eb996e"></path><path d="M333.93,148.88c-7.52,3.89-10.62,19.27-7.51,27.15s11.5,11.72,19.77,13.53,16.38,2,24.54-.24c.56,5.48,3.85,11.11,9.17,12.56,7.05,1.91,14.15-4.54,15.72-11.67s-.79-14.47-3.11-21.4" style="fill:#263238"></path><path d="M393.58,197.93s-.34.4-1.05,1a35.32,35.32,0,0,1-3.11,2.35,19.24,19.24,0,0,1-5.26,2.55,11.37,11.37,0,0,1-7.17-.11,11.09,11.09,0,0,1-5.43-4.69,15.07,15.07,0,0,1-1.95-5.52,20.14,20.14,0,0,1-.21-3.92,4.37,4.37,0,0,1,.17-1.42c.15,0,0,2.05.58,5.24a15.52,15.52,0,0,0,2,5.24,10.67,10.67,0,0,0,5.09,4.34,10.94,10.94,0,0,0,6.71.14,20.22,20.22,0,0,0,5.15-2.31C391.88,199.08,393.5,197.82,393.58,197.93Z" style="fill:#263238"></path><path d="M339.34,226.24a12.78,12.78,0,0,0,1.68,3.29,4.36,4.36,0,0,0,3.11,1.81,4,4,0,0,0,3.32-1.81,6.31,6.31,0,0,0,.92-3.78c-.05-1.19-.6-2.61-1.78-2.75a2.9,2.9,0,0,0-1.43.32l-6.22,2.52" style="fill:#eb996e"></path><path d="M341.82,231.35c0-.19,1.4.36,3.32-.41a4.72,4.72,0,0,0,2.51-2.27,5,5,0,0,0,.08-4.07,4.71,4.71,0,0,0-.54-.93c-.25-.26-.21-.25-.47-.21s-.67.26-1,.39l-1,.42c-1.33.54-2.54,1-3.55,1.39-2,.76-3.33,1.16-3.39,1s1.12-.8,3.08-1.74c1-.47,2.15-1,3.46-1.61l1-.45c.37-.15.64-.31,1.14-.47a1.46,1.46,0,0,1,.92,0,1.7,1.7,0,0,1,.69.54,5.69,5.69,0,0,1,.51,6.15,5.27,5.27,0,0,1-3.16,2.55,4.93,4.93,0,0,1-2.69.11C342.09,231.63,341.8,231.42,341.82,231.35Z" style="fill:#263238"></path><path d="M399,194.73c-1.38-.1-12.55,1.35-11.67,22.1.82,19.39,14.42,15.12,18.76,7.82C410.17,217.74,412.55,195.68,399,194.73Z" style="fill:#455a64"></path><path d="M405.87,197.18a3.37,3.37,0,0,1-.05-.54c0-.41,0-.93-.06-1.59,0-1.43-.1-3.43-.17-5.91-.1-5.09-.24-12.18-.41-20.35l.25.24h-5.75l.26-.26c0,1.54.08,3.17.13,4.83.19,7.78.37,15,.52,21.11v.18l-.17,0a10.41,10.41,0,0,0-5.93,3.82,14,14,0,0,0-2.37,5,22.06,22.06,0,0,0-.61,3.55c0,.41-.05.73-.06.94s0,.33,0,.33a1.39,1.39,0,0,1,0-.33c0-.21,0-.53,0-.95a18.6,18.6,0,0,1,.5-3.6,14,14,0,0,1,2.33-5.16,10.78,10.78,0,0,1,6.11-4l-.17.22c-.17-6.07-.38-13.32-.6-21.11-.05-1.65-.09-3.28-.13-4.83v-.27h6.27v.25c.09,8.18.17,15.26.23,20.36,0,2.48,0,4.48,0,5.91,0,.66,0,1.18,0,1.58A2.15,2.15,0,0,1,405.87,197.18Z" style="fill:#455a64"></path><path d="M399,168.78s-5.84-13.66-19.75-20.41a83.17,83.17,0,0,0-16.67-6.18s8.12-1.92,21.25,3.92c6,2.66,12,6.11,16,11.3,2.52,3.29,5.68,9.72,5.67,11.37Z" style="fill:#455a64"></path><path d="M363,334.25s.08-.1.25-.27.43-.43.75-.78a37.11,37.11,0,0,0,2.7-3.2,52.26,52.26,0,0,0,3.65-5.6c1.3-2.27,2.66-4.95,4.13-7.94,2.95-6,6.36-13.22,9.9-21.52,1.76-4.15,3.56-8.56,5.36-13.21l1.34-3.52a20.44,20.44,0,0,1,1.61-3.55,7.34,7.34,0,0,1,2.9-2.76,4.12,4.12,0,0,1,2-.51,2.07,2.07,0,0,1,1.06.38,2.27,2.27,0,0,1,.67.9l0,.07,0,.08a3.46,3.46,0,0,1-.54,1.33,2.1,2.1,0,0,1-1.25.82,4.53,4.53,0,0,1-2.79-.51,4.42,4.42,0,0,1-2.47-4.85,3.89,3.89,0,0,1,1.63-2.44,4.54,4.54,0,0,1,2.84-.81,3.13,3.13,0,0,1,2.62,1.45,1.51,1.51,0,0,1-.1,1.63,2.88,2.88,0,0,1-1.27.92,4.17,4.17,0,0,1-4.79-1.36,3.51,3.51,0,0,1-.65-2.52,4.33,4.33,0,0,1,1-2.36,3.62,3.62,0,0,1,2.25-1.24,2.88,2.88,0,0,1,2.43.84,1.8,1.8,0,0,1,.48,1.27,1.68,1.68,0,0,1-.58,1.23,2.8,2.8,0,0,1-2.44.66,3.07,3.07,0,0,1-1.95-1.56,6.43,6.43,0,0,1-.73-2.31,8.18,8.18,0,0,1,.53-4.66,3.47,3.47,0,0,1,1.71-1.69,3.13,3.13,0,0,1,2.37.08,2.25,2.25,0,0,1,1.47,1.85,2.06,2.06,0,0,1-1.27,2,2.52,2.52,0,0,1-2.3,0,3.18,3.18,0,0,1-1.43-1.76,4.39,4.39,0,0,1-.13-2.18,9.92,9.92,0,0,1,.46-2.08,3.81,3.81,0,0,1,1.21-1.77,4.85,4.85,0,0,1,1.89-.93,2,2,0,0,1,1.1,0,2.33,2.33,0,0,1,.91.58,2.62,2.62,0,0,1,.59.88,1.47,1.47,0,0,1,0,1.11,1.8,1.8,0,0,1-1.79,1,3.26,3.26,0,0,1-1.88-.63,5.16,5.16,0,0,1-2-3.15A5.31,5.31,0,0,1,395,248a4.05,4.05,0,0,1,1.13-1.37,3.65,3.65,0,0,1,1.59-.69,2.57,2.57,0,0,1,1.7.24,1.77,1.77,0,0,1,.92,1.41,2.49,2.49,0,0,1-1.88,2.4,2.72,2.72,0,0,1-2.72-1,4.36,4.36,0,0,1-1-2.56,16,16,0,0,1,1-4.78c.41-1.44.78-2.76,1.12-3.94.67-2.35,1.19-4.18,1.56-5.44.17-.61.31-1.08.41-1.41s.16-.48.16-.48,0,.17-.11.49-.21.82-.36,1.43c-.34,1.27-.84,3.11-1.47,5.46l-1.07,3.95c-.34,1.45-1,3-.93,4.7a4.16,4.16,0,0,0,1,2.38,2.43,2.43,0,0,0,2.4.9,2.16,2.16,0,0,0,1.62-2,1.43,1.43,0,0,0-.74-1.13,2.27,2.27,0,0,0-1.46-.19,3.27,3.27,0,0,0-1.44.63,3.76,3.76,0,0,0-1,1.25,5,5,0,0,0-.37,3.3,4.78,4.78,0,0,0,1.86,2.89,2.93,2.93,0,0,0,1.64.55,1.37,1.37,0,0,0,1.38-.73,1.53,1.53,0,0,0-.53-1.52,2,2,0,0,0-.73-.47,1.72,1.72,0,0,0-.87,0,3.8,3.8,0,0,0-2.77,2.43,9.09,9.09,0,0,0-.43,2,3.89,3.89,0,0,0,.12,2,2.63,2.63,0,0,0,1.2,1.49,2.09,2.09,0,0,0,1.87,0,1.57,1.57,0,0,0,1-1.49,1.77,1.77,0,0,0-1.17-1.43,2.72,2.72,0,0,0-2-.07,3,3,0,0,0-1.45,1.45,7.77,7.77,0,0,0-.47,4.37,6,6,0,0,0,.67,2.13,2.58,2.58,0,0,0,1.61,1.31,2.28,2.28,0,0,0,2-.53,1.24,1.24,0,0,0,.07-1.79,2.39,2.39,0,0,0-2-.67,3.07,3.07,0,0,0-1.92,1.08,3.44,3.44,0,0,0-.36,4.23,3.67,3.67,0,0,0,4.18,1.18,2.49,2.49,0,0,0,1-.75,1,1,0,0,0,.06-1.08,2.63,2.63,0,0,0-2.19-1.17,3.75,3.75,0,0,0-3.93,2.83A3.86,3.86,0,0,0,395,274a3.93,3.93,0,0,0,2.45.48,2,2,0,0,0,1.4-1.75l0,.15a1.5,1.5,0,0,0-1.31-1,3.57,3.57,0,0,0-1.77.46,6.8,6.8,0,0,0-2.71,2.58,19.53,19.53,0,0,0-1.57,3.45l-1.36,3.53c-1.8,4.64-3.62,9-5.4,13.2-3.56,8.3-7,15.54-10,21.5-1.49,3-2.87,5.65-4.2,7.92a51.29,51.29,0,0,1-3.73,5.57A26.29,26.29,0,0,1,363,334.25Z" style="fill:#fff"></path><path d="M411.09,185a13.12,13.12,0,0,0-.91-3.7,14.57,14.57,0,0,0-7.18-7.19,19.39,19.39,0,0,0-6.92-1.69,32.56,32.56,0,0,0-8.21.62c-5.73,1.06-11.75,3.53-18.49,4.55a37.7,37.7,0,0,1-9.9,0,39.84,39.84,0,0,1-8.9-2.42,39.28,39.28,0,0,1-13-8.56,35,35,0,0,1-6.28-8.4,11.9,11.9,0,0,1-.74-1.47l-.48-1.11c-.12-.27-.21-.5-.29-.68a.86.86,0,0,1-.08-.24,1,1,0,0,1,.13.22l.33.66.53,1.08a12.54,12.54,0,0,0,.78,1.45,36.41,36.41,0,0,0,6.36,8.23,39,39,0,0,0,21.67,10.73,37.07,37.07,0,0,0,9.76,0c6.65-1,12.68-3.46,18.48-4.5a32.76,32.76,0,0,1,8.32-.58,19.45,19.45,0,0,1,7.05,1.79,14.59,14.59,0,0,1,7.21,7.44,10.91,10.91,0,0,1,.69,2.78l0,.74A.86.86,0,0,1,411.09,185Z" style="fill:#455a64"></path><path d="M396,163.45a4.87,4.87,0,0,1-.72.12c-.47.07-1.17.17-2.06.33-1.78.29-4.33.86-7.47,1.64a79.48,79.48,0,0,1-11.19,2.17,56.23,56.23,0,0,1-13.94-.31,59,59,0,0,1-13.47-3.62A57,57,0,0,1,337,158.53c-1.34-.91-2.54-1.75-3.56-2.55s-1.88-1.49-2.54-2.11l-1.52-1.43c-.34-.33-.52-.51-.5-.53a3.91,3.91,0,0,1,.57.45l1.58,1.35c.68.6,1.59,1.24,2.59,2s2.24,1.59,3.58,2.47a60.33,60.33,0,0,0,23.46,8.67,57.39,57.39,0,0,0,13.83.34,81.69,81.69,0,0,0,11.14-2c3.15-.73,5.73-1.25,7.53-1.49.9-.12,1.6-.19,2.08-.22A3.68,3.68,0,0,1,396,163.45Z" style="fill:#455a64"></path><path d="M397.48,230.45a1.91,1.91,0,0,1-.39-.12,6.36,6.36,0,0,1-1-.53,9.18,9.18,0,0,1-2.93-3.12,15.07,15.07,0,0,1-1.8-6.09,43.67,43.67,0,0,1,0-7.76,47.28,47.28,0,0,1,1.28-7.65,22.07,22.07,0,0,1,2.23-5.93,9.16,9.16,0,0,1,3-3.11,5.48,5.48,0,0,1,1-.52c.25-.09.39-.13.39-.11s-.51.23-1.32.8a9.59,9.59,0,0,0-2.74,3.12,22.5,22.5,0,0,0-2.08,5.87,50,50,0,0,0-1.24,7.58,46.75,46.75,0,0,0,0,7.66,15.21,15.21,0,0,0,1.64,6,9.36,9.36,0,0,0,2.72,3.14C397,230.21,397.51,230.4,397.48,230.45Z" style="fill:#263238"></path><rect x="339.89" y="228.29" width="16.16" height="6.09" rx="2.85" transform="translate(-38.04 71.34) rotate(-11.1)" style="fill:#455a64"></rect><path d="M354.47,228.88,401.92,214a2.24,2.24,0,0,1,2.8,1.45h0a2.25,2.25,0,0,1-1.55,2.85l-48.52,13.1Z" style="fill:#455a64"></path><path d="M389.54,222.34a10.62,10.62,0,0,1,1.57-.55l4.34-1.32c1.82-.56,4.06-1.16,6.42-1.93a4.3,4.3,0,0,0,1.55-1,2.34,2.34,0,0,0,.67-1.65,1.44,1.44,0,0,0-1-1.36,3.63,3.63,0,0,0-2,.06l-14.23,4.58-4.33,1.34A9.91,9.91,0,0,1,381,221a10.71,10.71,0,0,1,1.55-.61l4.28-1.5c3.63-1.23,8.61-2.94,14.21-4.73a4.1,4.1,0,0,1,2.28,0,2,2,0,0,1,1.32,1.84,2.84,2.84,0,0,1-.82,2A4.8,4.8,0,0,1,402,219c-2.45.77-4.64,1.31-6.49,1.83L391.17,222A8.87,8.87,0,0,1,389.54,222.34Z" style="fill:#263238"></path><path d="M312,186c1.62,5.7-4.17,10.59-6.21,16.16-2,5.38-.3,11.41,2.13,16.61s5.63,10.12,6.87,15.71c3.07,13.8-9.41,28.67-7,42.6.86,5.08,2.91,10.38,4.68,15.21a20.1,20.1,0,0,1,0,14.13c-2.7,8-7.28,11.75-10.5,19.53-1.65,4-1.52,8.44-1.44,12.74s.06,8.8-1.86,12.66-6.3,6.84-10.46,5.72a15.33,15.33,0,0,0,18.32,3.49c5.47-2.92,8.53-8.84,11.15-14.46,12.68-27.21,19.39-29.51,19-59.53,0-1.93-.38-32-.38-32a30,30,0,0,1-7.49-3.15c-14.16-9.78-14.68-27.2-14.57-40.71" style="fill:#263238"></path><path d="M340.3,277.08v11.33s-8.71,44.45-11.33,69.14-3.05,31.21-3.05,31.21,6.25,9.75,19.32-6.81,15.4-19.17,20.63-19.46c0,0-6.39-69.43-6.1-72a10.49,10.49,0,0,1,2.32-4.94Z" style="fill:#ffbe9d"></path><path d="M357.44,251.52S343.21,270.4,342.05,273s-1.75,4.06-1.75,4.06l21.79,8.43,21.21-23.24,7.84-20.63-2.33-24.4s-4.64-.29-5.51,3.78a30.57,30.57,0,0,0-.59,7.84l-6.5-5.23-10.49-3.19s-2.47,4.06,1.6,6.1,5.23,2,5.23,2l3.66,6.39-1.6,7.16-7.42-7a4.23,4.23,0,0,0-5.4-.34l-4.8,3.48-4.49-2L343.79,244l-3.49,3.19s2.33,4.36,7,2.33l4.65-2Z" style="fill:#ffbe9d"></path><path d="M340.3,280.91a4.71,4.71,0,0,1,0-.61c0-.4.05-1,.14-1.77a20.82,20.82,0,0,1,.55-2.85,12.64,12.64,0,0,1,1.52-3.66c3.26-5,8.39-12,14.79-20.61l0,.26-5.52-4.06.19,0-2.87,1.27a19.86,19.86,0,0,1-3.06,1.19,5,5,0,0,1-3.4-.39,6.26,6.26,0,0,1-2.56-2.44l-.08-.14.13-.12c3.74-3.52,7.91-7.07,12.19-10.87l.13-.11.14.08,3.87,2.29h-.25l4.52-3c.79-.51,1.5-1,2.35-1.52a2.82,2.82,0,0,1,2.92.25l8.81,7.45-.41.14c.48-2.15,1-4.48,1.52-6.83l0,.18-3.66-6.39.23.13a7.29,7.29,0,0,1-1.81-.49c-.57-.21-1.14-.45-1.71-.7s-1.13-.53-1.69-.8c-.29-.14-.57-.33-.85-.49a5.29,5.29,0,0,1-.76-.66,3.68,3.68,0,0,1-1-2.67,5.89,5.89,0,0,1,.77-2.72l.1-.17.2.06,10.48,3.2h.05l0,0,6.5,5.23-.41.22a31.06,31.06,0,0,1,.55-7.73,4.71,4.71,0,0,1,2.12-3.22,6.74,6.74,0,0,1,3.7-.93H389l0,.22c.82,8.67,1.59,16.85,2.3,24.4v0l0,.05c-2.9,7.6-5.54,14.5-7.88,20.61v0l0,0L367,280.31l-4.46,4.83c-.51.53-.9.95-1.17,1.24l-.41.41s.12-.16.37-.45l1.13-1.27,4.4-4.89,16.34-18,0,.05,7.8-20.64v.1c-.73-7.55-1.51-15.72-2.35-24.4l.23.23a6.16,6.16,0,0,0-3.4.86,4.18,4.18,0,0,0-1.89,2.9,30,30,0,0,0-.54,7.59l0,.58-.45-.36L376,223.83l.09,0-10.48-3.19.29-.12a5.45,5.45,0,0,0-.69,2.46,3.17,3.17,0,0,0,.86,2.31,5.37,5.37,0,0,0,.67.58c.27.15.51.31.79.45.56.27,1.11.55,1.67.79a14.69,14.69,0,0,0,3.31,1.15l.16,0,.06.1,3.66,6.39.05.09,0,.1c-.53,2.35-1,4.68-1.53,6.83l-.09.42-.33-.28-8.78-7.43a2.31,2.31,0,0,0-2.4-.22c-.74.44-1.54,1-2.3,1.49l-4.53,3-.12.08-.13-.07-3.87-2.3.28,0c-4.28,3.78-8.46,7.33-12.21,10.83l.05-.26a5.77,5.77,0,0,0,2.36,2.26,4.51,4.51,0,0,0,3.12.36,19.8,19.8,0,0,0,3-1.15l2.87-1.26.11,0,.09.06,5.51,4.08.15.11-.11.15c-6.43,8.61-11.61,15.55-14.91,20.5a13,13,0,0,0-1.55,3.59,23.18,23.18,0,0,0-.59,2.82c-.11.77-.16,1.37-.19,1.76S340.3,280.91,340.3,280.91Z" style="fill:#eb996e"></path><path d="M367.26,255.89c-.1,0,.28-1.54.44-4a9.18,9.18,0,0,0-.68-4.2,11.23,11.23,0,0,0-3.47-3.9,86.21,86.21,0,0,0-8.07-5.49l-2.55-1.5a4.9,4.9,0,0,1-.92-.59,3.6,3.6,0,0,1,1,.41c.64.29,1.55.75,2.65,1.36a66.86,66.86,0,0,1,8.2,5.4,11.23,11.23,0,0,1,3.58,4.11,9,9,0,0,1,.62,4.42,18.81,18.81,0,0,1-.49,2.94A3.89,3.89,0,0,1,367.26,255.89Z" style="fill:#eb996e"></path><path d="M382.51,246.83a19.52,19.52,0,0,1,.37-2.62,41,41,0,0,0,.45-6.3,41.84,41.84,0,0,0-.56-6.31,20.32,20.32,0,0,1-.41-2.6,10,10,0,0,1,.77,2.53,30.89,30.89,0,0,1,.11,12.75A10,10,0,0,1,382.51,246.83Z" style="fill:#eb996e"></path></g><g id="freepik--speech-bubble--inject-2"><path d="M285.4,172.73,275,154.36a49.21,49.21,0,1,0-11.79,13.12l0,0Z" style="fill:#fff"></path><path d="M222.47,128.63a4,4,0,1,1-4-4A4,4,0,0,1,222.47,128.63Z" style="fill:#263238"></path><path d="M238.43,128.63a4,4,0,1,1-4-4A4,4,0,0,1,238.43,128.63Z" style="fill:#263238"></path><path d="M254.36,126.63a4,4,0,1,1-5.45-1.46A4,4,0,0,1,254.36,126.63Z" style="fill:#263238"></path></g><g id="freepik--Table--inject-2"><path d="M482,442.14c0,.15-101.66.26-227,.26s-227-.11-227-.26,101.63-.26,227.05-.26S482,442,482,442.14Z" style="fill:#263238"></path></g><g id="freepik--Device--inject-2"><path d="M353.55,442.39a26.84,26.84,0,0,1,21.39-12.08c14.61-.5,21.91,12.08,21.91,12.08Z" style="fill:#455a64"></path><path d="M381.55,442a16.23,16.23,0,0,1-.43-2.18,14.09,14.09,0,0,0-5.79-8.52,18.5,18.5,0,0,1-1.87-1.2,5.22,5.22,0,0,1,2.11.82,12.39,12.39,0,0,1,6,8.82A5.27,5.27,0,0,1,381.55,442Z" style="fill:#263238"></path><path d="M344.51,441.86v-.1a5.6,5.6,0,0,0-5.61-5.51H214.56a5.61,5.61,0,0,0-5.61,5.61H344.51Z" style="fill:#455a64"></path><path d="M106.47,224c0,2.62,2,158.78,2,158.78l116,27.91H234l-6.1-218h-8.72Z" style="fill:#263238"></path><path d="M131,376.29,95.05,442.17h32.86c-5.88-.58,52,0,52,0l-52-13.79,16.27-36.95-1.39-15.14Z" style="fill:#263238"></path><polygon points="131.8 388.45 127.91 265.65 163.23 262.27 167.58 393.38 154.97 394.02 131.8 388.45" style="fill:#263238"></polygon><path d="M172.52,398.24s0-.19,0-.57,0-.95,0-1.68c0-1.5,0-3.69,0-6.52,0-5.71-.05-14-.08-24.43,0-20.87-.05-50.24-.09-84.3l.25.25H130.85l.26-.26v5.66c0,33.22,0,64.13-.05,89.89v0l0,.05c-10.82,19.83-19.79,36.29-26.07,47.79-3.14,5.74-5.61,10.24-7.3,13.32-.84,1.52-1.49,2.68-1.93,3.49s-.69,1.18-.69,1.18.21-.42.63-1.21,1.06-2,1.87-3.53c1.67-3.09,4.1-7.61,7.2-13.37l25.9-47.88,0,.11c0-25.76,0-56.67,0-89.89v-5.92h42.18v.25c0,34.06-.07,63.43-.09,84.3,0,10.42-.06,18.72-.08,24.43,0,2.83,0,5,0,6.52,0,.73,0,1.29,0,1.68S172.52,398.24,172.52,398.24Z" style="fill:#455a64"></path><path d="M144.18,391.43c.13.05-4.87,11.62-11.17,25.82s-11.52,25.68-11.65,25.62,4.87-11.62,11.17-25.83S144.05,391.37,144.18,391.43Z" style="fill:#455a64"></path><path d="M224.42,410.73s0-.19,0-.56,0-.94,0-1.65c0-1.47-.05-3.6-.08-6.34,0-5.5-.17-13.46-.36-23.3-.37-19.68-1.26-46.85-2.35-76.86s-1.92-57.21-2.2-76.89c-.15-9.84-.22-17.81-.19-23.31,0-2.74,0-4.87,0-6.34,0-.71,0-1.25,0-1.65s0-.56,0-.56,0,.19,0,.56,0,.94,0,1.65c0,1.47,0,3.6.08,6.34,0,5.5.16,13.46.36,23.3C220.11,244.8,221,272,222.09,302s1.92,57.19,2.2,76.87c.15,9.84.21,17.81.19,23.31,0,2.74,0,4.87,0,6.34,0,.71,0,1.26,0,1.65S224.42,410.73,224.42,410.73Z" style="fill:#455a64"></path><path d="M166.19,279.83c.15,0,.26,26.12.26,58.33s-.11,58.33-.26,58.33-.26-26.11-.26-58.33S166.05,279.83,166.19,279.83Z" style="fill:#455a64"></path><path d="M103.7,431.89a4.39,4.39,0,0,1-.5-.7c-.17-.24-.36-.53-.58-.86a11.61,11.61,0,0,1-.72-1.22,30.77,30.77,0,0,1-3.17-8.45,41.86,41.86,0,0,1-.62-13.37,53.5,53.5,0,0,1,4.34-15.81,32.83,32.83,0,0,1,4.53-7.21,18.21,18.21,0,0,1,1.33-1.56c.46-.5.9-1,1.36-1.48,1-.91,1.87-1.84,2.86-2.63a46.1,46.1,0,0,1,5.81-4.21c1-.54,1.91-1.11,2.85-1.56s1.83-.93,2.73-1.25a31.28,31.28,0,0,1,8.72-2.32,16.47,16.47,0,0,1,2.45-.15,5.16,5.16,0,0,1,.86,0,6.66,6.66,0,0,1-.85.09,21.58,21.58,0,0,0-2.43.25,32.77,32.77,0,0,0-8.61,2.46c-.89.33-1.75.83-2.69,1.26s-1.84,1-2.81,1.58a46.15,46.15,0,0,0-5.72,4.21c-1,.78-1.87,1.71-2.82,2.61-.45.47-.89,1-1.34,1.46a19.38,19.38,0,0,0-1.3,1.55,32.5,32.5,0,0,0-4.46,7.11,53.61,53.61,0,0,0-4.33,15.64,42.24,42.24,0,0,0,.5,13.24,32.32,32.32,0,0,0,3,8.43,10.64,10.64,0,0,0,.68,1.24l.53.89A6.44,6.44,0,0,1,103.7,431.89Z" style="fill:#455a64"></path></g></svg>
            </figure>
            <!-- SVG END -->
          </div>
        </div>
        <!-- Help search START -->

        <!-- Popular questions START -->
        <div class="row align-items-center">
          <div class="col-12">
            <div class="card card-body p-4">
              <div class="text-center mb-4">
                <h4>Popular questions</h4>
              </div>
              <!-- Questions List START -->
              <div class="list-group list-group-horizontal gap-2 flex-wrap justify-content-center mb-0 border-0">
                <a class="btn btn-light btn-sm fw-light" href="help-details.html"> How can we help?</a>
                <a class="btn btn-light btn-sm fw-light" href="help-details.html"> How to edit my Profile?</a>
                <a class="btn btn-light btn-sm fw-light" href="help-details.html"> How much should I offer the sellers? </a>
                <a class="btn btn-light btn-sm fw-light" href="help-details.html"> Installation Guide? </a>
                <a class="btn btn-light btn-sm fw-light" href="help-details.html"> Additional Options and Services? </a>
                <a class="btn btn-light btn-sm fw-light" href="help-details.html"> What's are the difference between a social?</a>
                <a class="btn btn-primary-soft btn-sm fw-light" href="#!">View all question</a>
              </div>
              <!-- Questions list END -->
            </div>
            <!-- Popular questions END -->
          </div>
        </div>

        <!-- Recommended topics START -->
        <div class="py-5">
          <!-- Titles -->
          <h4 class="text-center mb-4">Recommended topics</h4>
          <!-- Row START -->
          <div class="row g-4">

            <div class="col-md-4">
              <!-- Get started START -->
              <div class="card h-100">
                <!-- Title START -->
                <div class="card-header pb-0 border-0">
                  <i class="bi bi-emoji-smile fs-1 text-success"></i>
                  <h5 class="card-title mb-0 mt-2">Get started </h5>
                </div>
                <!-- Title END -->
                <!-- List START -->
                <div class="card-body">
                  <ul class="nav flex-column">
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Gulp and Customization</a></li>
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Color Scheme and Logo Settings</a></li>
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Dark mode, RTL Version and Lazy Load</a></li>
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Sources, Credits and Changelog</a></li>
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Updates and Support</a></li>
                  </ul>
                </div>
                <!-- List END -->
              </div> 
              <!-- Get started END --> 
            </div>

            <div class="col-md-4">
              <!-- Account Setup START -->
              <div class="card h-100">
                <!-- Title START -->
                <div class="card-header pb-0 border-0">
                  <i class="bi bi-layers fs-1 text-warning"></i>
                  <h5 class="card-title mb-0 mt-2">Account Setup:</h5>
                </div>
                <!-- Title END -->
                <!-- List START -->
                <div class="card-body">
                  <ul class="nav flex-column">
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Connecting to your Account</a></li>
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Edit your profile information</a></li>
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Connecting to other Social Media Accounts</a></li>
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Adding your profile picture</a></li>
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Describing your store</a></li>
                  </ul>
                </div>
                <!-- List END -->
              </div>  
              <!-- Account Setup END -->
            </div>

            <div class="col-md-4">
              <!-- Other Topics START -->
              <div class="card h-100">
                <!-- Title START -->
                <div class="card-header pb-0 border-0">
                  <i class="bi bi-info-circle fs-1 text-primary"></i>
                  <h5 class="card-title mb-0 mt-2">Other Topics </h5>
                </div>
                <!-- Title END -->
                <!-- List START -->
                <div class="card-body">
                  <ul class="nav flex-column">
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Security & Privacy</a></li>
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Author, Publisher & Admin Guides</a></li>
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Pricing plans</a></li>
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Sales Tax & Regulatory Fees</a></li>
                    <li class="nav-item"><a class="nav-link d-flex" href="help-details.html"><i class="fa-solid fa-angle-right text-primary pt-1 fa-fw me-2"></i>Promotions & Deals</a></li>
                  </ul>
                </div>
                <!-- List END -->
              </div>  
              <!-- Other Topics END -->
            </div>

          </div>
          <!-- Row END -->
        </div>
        <!-- Recommended topics END -->

        <!-- Popular article START -->
        <div class="pb-5"> 
          <!-- Title -->
          <h4 class="text-center mb-4">Popular articles</h4>
          <!-- Row START -->
          <div class="row">
            <div class="col-lg-8 mx-auto">
              <div class="vstack gap-4">
                <!-- Article START -->
                <div class="card card-body">
                  <div class="d-md-flex justify-content-between align-items-start">
                    <!-- Title -->
                    <div class="mb-2 mb-md-0">
                      <h5 class="mb-1"> <a href="#!">Get started with node.js</a></h5>
                      <p class="small mb-0">Satisfied conveying a dependent contented he gentleman agreeable do be.</p>
                    </div>
                    <!-- Button -->
                    <div class="btn-group" role="group" aria-label="Basic radio toggle button group">
                      <!-- Yes button -->
                      <input type="radio" class="btn-check" name="article1" id="articleup1">
                      <label class="btn btn-outline-light btn-sm mb-0" for="articleup1"><i class="fa-solid fa-thumbs-up me-1"></i> 457</label>
                      <!-- No button -->
                      <input type="radio" class="btn-check" name="article1" id="articledown1" checked>
                      <label class="btn btn-outline-light btn-sm mb-0" for="articledown1"> 01 <i class="fa-solid fa-thumbs-down ms-1"></i></label>
                    </div>
                  </div>
                </div>  
                <!-- Article END -->

                <!-- Article START -->
                <div class="card card-body">
                  <div class="d-md-flex justify-content-between align-items-start">
                    <!-- Title -->
                    <div class="mb-2 mb-md-0">
                      <h5 class="mb-1"> <a href="#!">Upgrade Gulp 3 to Gulp 4 the gulpfile.js workflow</a></h5>
                      <p class="small mb-0">Required his you put the outlived answered position. </p>
                    </div>
                    <!-- Button -->
                    <div class="btn-group" role="group" aria-label="Basic radio toggle button group">
                      <!-- Yes button -->
                      <input type="radio" class="btn-check" name="article2" id="articleup2">
                      <label class="btn btn-outline-light btn-sm mb-0" for="articleup2"><i class="fa-solid fa-thumbs-up me-1"></i> 256</label>
                      <!-- No button -->
                      <input type="radio" class="btn-check" name="article2" id="articledown2">
                      <label class="btn btn-outline-light btn-sm mb-0" for="articledown2"> 02 <i class="fa-solid fa-thumbs-down ms-1"></i></label>
                    </div>
                  </div>
                </div>  
                <!-- Article END -->

                <!-- Article START -->
                <div class="card card-body">
                  <div class="d-md-flex justify-content-between align-items-start">
                    <!-- Title -->
                    <div class="mb-2 mb-md-0">
                      <h5 class="mb-1"> <a href="#!">6 Reasons to use Bootstrap 5 for better UI development</a></h5>
                      <p class="small mb-0">As it so contrasted oh estimating instrument. Size like body some one had. </p>
                    </div>
                    <!-- Button -->
                    <div class="btn-group" role="group" aria-label="Basic radio toggle button group">
                      <!-- Yes button -->
                      <input type="radio" class="btn-check" name="article3" id="articleup3" checked>
                      <label class="btn btn-outline-light btn-sm mb-0" for="articleup3"><i class="fa-solid fa-thumbs-up me-1"></i> 675</label>
                      <!-- No button -->
                      <input type="radio" class="btn-check" name="article3" id="articledown3">
                      <label class="btn btn-outline-light btn-sm mb-0" for="articledown3"> 10 <i class="fa-solid fa-thumbs-down ms-1"></i></label>
                    </div>
                  </div>
                </div>  
                <!-- Article END -->

                <!-- Article START -->
                <div class="card card-body">
                  <div class="d-md-flex justify-content-between align-items-start">
                    <!-- Title -->
                    <div class="mb-3 mb-md-0">
                      <h5 class="mb-1"> <a href="#!">A beginner's guide to browser-sync</a></h5>
                      <p class="small mb-0">Started several mistake joy say painful removed reached end.  </p>
                    </div>
                    <!-- Button -->
                    <div class="btn-group" role="group" aria-label="Basic radio toggle button group">
                      <!-- Yes button -->
                      <input type="radio" class="btn-check" name="article4" id="articleup4">
                      <label class="btn btn-outline-light btn-sm mb-0" for="articleup4"><i class="fa-solid fa-thumbs-up me-1"></i> 325</label>
                      <!-- No button -->
                      <input type="radio" class="btn-check" name="article4" id="articledown4">
                      <label class="btn btn-outline-light btn-sm mb-0" for="articledown4"> 06 <i class="fa-solid fa-thumbs-down ms-1"></i></label>
                    </div>
                  </div>
                </div>  
                <!-- Article END -->
                <!-- Button -->
                <div class="text-center">
                  <a href="#!" role="button" class="btn btn-loader btn-primary-soft mb-0" data-bs-toggle="button" aria-pressed="true">
                    <span class="load-text"> Load more </span>
                    <div class="load-icon">
                      <div class="spinner-grow spinner-grow-sm" role="status">
                        <span class="visually-hidden">Loading...</span>
                      </div>
                    </div>
                  </a>
                </div>
              </div>
            </div>
          </div>
          <!-- Row END -->
        </div>
        <!-- Popular article END -->
    </div>
  <!-- Container END -->

</main>

<!-- **************** MAIN CONTENT END **************** -->

<!-- footer START -->
<footer class="bg-mode py-3">
  <div class="container">
    <div class="row">
      <div class="col-md-6">
        <!-- Footer nav START -->
        <ul class="nav justify-content-center justify-content-md-start lh-1">
          <li class="nav-item">
            <a class="nav-link" href="my-profile-about.html">About</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" target="_blank" href="https://support.webestica.com/login">Support </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" target="_blank" href="docs/index.html">Docs </a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="privacy-and-terms.html">Privacy & terms</a>
          </li>
        </ul>
        <!-- Footer nav START -->
      </div>
      <div class="col-md-6">
        <!-- Copyright START -->
        <p class="text-center text-md-end mb-0">©2023 <a class="text-body" target="_blank" href="https://www.webestica.com"> Webestica </a>All rights reserved.</p>
        <!-- Copyright END -->
      </div>
    </div>
  </div>
</footer>
<!-- footer END -->

<!-- =======================
JS libraries, plugins and custom scripts -->

<!-- Bootstrap JS -->
<script src="assets/vendor/bootstrap/dist/js/bootstrap.bundle.min.js"></script>

<!-- Theme Functions -->
<script src="assets/js/functions.js"></script>

</body>
</html>