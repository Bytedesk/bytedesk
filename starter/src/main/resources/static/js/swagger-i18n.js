// 等待 Swagger UI 完全加载
window.onload = function() {
    setTimeout(function() {
        addLanguageSwitcher();
    }, 1000);
};

function addLanguageSwitcher() {
    // 创建语言切换容器
    var languageSwitcher = document.createElement('div');
    languageSwitcher.className = 'language-switcher';
    languageSwitcher.style.position = 'absolute';
    languageSwitcher.style.top = '10px';
    languageSwitcher.style.right = '10px';
    languageSwitcher.style.zIndex = '9999';
    
    // 创建语言按钮
    var languages = [
        { code: 'en', name: 'English' },
        { code: 'zh_CN', name: '简体中文' },
        { code: 'zh_TW', name: '繁體中文' }
    ];
    
    languages.forEach(function(lang) {
        var button = document.createElement('button');
        button.textContent = lang.name;
        button.style.margin = '0 5px';
        button.style.padding = '5px 10px';
        button.style.backgroundColor = '#3498db';
        button.style.color = 'white';
        button.style.border = 'none';
        button.style.borderRadius = '4px';
        button.style.cursor = 'pointer';
        
        // 添加点击事件
        button.onclick = function() {
            window.location.href = '/language/switch?lang=' + lang.code + '&redirectUrl=' + encodeURIComponent(window.location.pathname + window.location.search);
        };
        
        languageSwitcher.appendChild(button);
    });
    
    // 将语言切换器添加到 Swagger UI 界面
    var topbar = document.querySelector('.topbar');
    if (topbar) {
        topbar.appendChild(languageSwitcher);
    }
}
