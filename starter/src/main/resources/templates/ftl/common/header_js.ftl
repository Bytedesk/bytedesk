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
        if (theme === 'auto') {
            // For auto mode, check system preference
            const preferredTheme = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
            document.documentElement.setAttribute('data-bs-theme', preferredTheme)
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
            if (btnToActive) {
                const svgOfActiveBtn = btnToActive.querySelector('.mode-switch use').getAttribute('href')

                document.querySelectorAll('[data-bs-theme-value]').forEach(element => {
                    element.classList.remove('active')
                })

                btnToActive.classList.add('active')
                activeThemeIcon.setAttribute('href', svgOfActiveBtn)
            }
        }

        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
            if (storedTheme !== 'light' && storedTheme !== 'dark') {
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

<!-- Language Detection and Auto-redirect -->
<script>
    (function() {
        // Get stored language preference
        const storedLang = localStorage.getItem('language');
        const currentPath = window.location.pathname;
        
        // Check if this is a static HTML page (ends with .html)
        const isStaticPage = currentPath.endsWith('.html');
        
        // Extract current language from path (e.g., /zh-TW/index.html -> zh-TW)
        const pathLangMatch = currentPath.match(/^\/(zh-CN|zh-TW|en)\//);
        const currentLang = pathLangMatch ? pathLangMatch[1] : 'zh-CN'; // default is zh-CN (root)
        
        // Determine target language
        let targetLang = storedLang;
        
        // If no stored preference, use browser language
        if (!targetLang) {
            const browserLang = navigator.language || navigator.userLanguage;
            if (browserLang.startsWith('zh-TW') || browserLang.startsWith('zh-HK')) {
                targetLang = 'zh-TW';
            } else if (browserLang.startsWith('zh')) {
                targetLang = 'zh-CN';
            } else if (browserLang.startsWith('en')) {
                targetLang = 'en';
            } else {
                targetLang = 'zh-CN'; // default fallback
            }
            // Store the detected language
            localStorage.setItem('language', targetLang);
        }
        
        // Only redirect if:
        // 1. This is a static HTML page
        // 2. Current language doesn't match target language
        // 3. Not already redirected (prevent redirect loop)
        if (isStaticPage && currentLang !== targetLang && !sessionStorage.getItem('langRedirected')) {
            // Mark that we've redirected in this session
            sessionStorage.setItem('langRedirected', 'true');
            
            // Build new path
            let newPath;
            // Remove current language prefix
            const cleanPath = currentPath.replace(/^\/(zh-CN|zh-TW|en)\//, '/');
            
            if (targetLang === 'zh-CN') {
                // zh-CN uses root directory
                newPath = cleanPath;
            } else {
                // Other languages use /lang/ directory
                newPath = '/' + targetLang + cleanPath;
            }
            
            // Redirect to target language version
            window.location.href = newPath;
        }
        
        // Update language dropdown display on page load
        window.addEventListener('DOMContentLoaded', function() {
            const langMenuButton = document.getElementById('langMenu');
            if (langMenuButton) {
                const langNames = {
                    'zh-CN': '中文简体',
                    'zh-TW': '繁體中文',
                    'en': 'English'
                };
                langMenuButton.textContent = langNames[currentLang] || langNames['zh-CN'];
            }
        });
    })();
</script>
