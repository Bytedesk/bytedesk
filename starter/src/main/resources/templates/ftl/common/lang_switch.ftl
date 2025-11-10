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
        const currentPath = window.location.pathname;
        const isStaticHTML = currentPath.endsWith('.html');
        
        if (isStaticHTML) {
            // 静态 HTML 模式：跳转到对应语言目录
            let newPath;
            
            // 移除当前语言前缀（如果有）
            const cleanPath = currentPath.replace(/^\/(zh-CN|zh-TW|en)\//, '/');
            
            // 根据目标语言添加前缀
            if (lang === 'zh-CN') {
                // 简体中文使用根目录
                newPath = cleanPath;
            } else {
                // 其他语言使用语言目录
                newPath = '/' + lang + cleanPath;
            }
            
            window.location.href = newPath;
        } else {
            // 动态 FTL 模式：使用查询参数
            const url = new URL(window.location);
            url.searchParams.set('lang', lang);
            window.location.href = url.toString();
        }
    }
});
</script>
