<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta name="author" content="微语AI">
<meta name="description" content="微语AI客户管理 - 革命性的AI驱动客户关系管理系统，整合多渠道客户数据，智能分析客户行为，提升销售转化率和客户满意度。支持AI客户画像、销售漏斗分析、营销自动化等核心功能。">
<meta name="keywords" content="微语,AI,客户管理,Scrm,CRM,客户关系管理,销售漏斗,营销自动化,客户画像,销售管理,客户分析">

<!-- Dark mode -->
<script>
	const storedTheme = localStorage.getItem('theme')
 
	const getPreferredTheme = () => {
		if (storedTheme) {
			return storedTheme
		}
		return window.matchMedia('(prefers-color-scheme: light)').matches ? 'light' : 'light'
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
			const btnToActive = document.querySelector('[data-bs-theme-value="' + theme + '"]')
			const svgOfActiveBtn = btnToActive.querySelector('.theme-icon use').getAttribute('href')

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
<link rel="shortcut icon" href="/assets/images/favicon.ico">

<!-- Google Font -->
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Heebo:wght@400;500;700&family=Roboto:wght@400;500;700&display=swap">

<!-- Plugins CSS -->
<link rel="stylesheet" type="text/css" href="/assets/vendor/font-awesome/css/all.min.css">
<link rel="stylesheet" type="text/css" href="/assets/vendor/bootstrap-icons/bootstrap-icons.css">
<link rel="stylesheet" type="text/css" href="/assets/vendor/tiny-slider/tiny-slider.css">
<link rel="stylesheet" type="text/css" href="/assets/vendor/glightbox/css/glightbox.css">

<!-- Theme CSS -->
<link rel="stylesheet" type="text/css" href="/assets/css/style.css">

<!-- Open Graph / Facebook -->
<meta property="og:type" content="website">
<meta property="og:url" content="https://www.weiyuai.cn/scrm">
<meta property="og:title" content="微语AI客户管理 - 智能客户关系管理系统">
<meta property="og:description" content="革命性的AI驱动客户关系管理系统，整合多渠道客户数据，智能分析客户行为，提升销售转化率和客户满意度">
<meta property="og:image" content="https://www.weiyuai.cn/assets/images/logo.png">

<!-- Twitter -->
<meta property="twitter:card" content="summary_large_image">
<meta property="twitter:url" content="https://www.weiyuai.cn/scrm">
<meta property="twitter:title" content="微语AI客户管理 - 智能客户关系管理系统">
<meta property="twitter:description" content="革命性的AI驱动客户关系管理系统，整合多渠道客户数据，智能分析客户行为，提升销售转化率和客户满意度">
<meta property="twitter:image" content="https://www.weiyuai.cn/assets/images/logo.png">

<title>微语AI客户管理 - 智能客户关系管理系统 | 微语AI</title>