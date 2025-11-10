<!-- Language Switch Script -->
<script>
document.addEventListener('DOMContentLoaded', function() {
    const langLinks = document.querySelectorAll('#langMenu + .dropdown-menu a');
    
    langLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const targetLang = this.getAttribute('data-lang');
            switchLanguage(targetLang);
        });
    });
    
    function switchLanguage(lang) {
        // Store language preference
        localStorage.setItem('language', lang);
        
        const currentPath = window.location.pathname;
        
        // Remove current language prefix (if any)
        const cleanPath = currentPath.replace(/^\/(zh-CN|zh-TW|en)\//, '/');
        
        // Build new path with target language
        let newPath;
        if (lang === 'zh-CN') {
            // Simplified Chinese uses root directory
            newPath = cleanPath;
        } else {
            // Other languages use /lang/ directory
            newPath = '/' + lang + cleanPath;
        }
        
        // Clear redirect flag to allow redirect after language change
        sessionStorage.removeItem('langRedirected');
        
        // Redirect to new language version
        window.location.href = newPath;
    }
});
</script>
