/* eslint-disable no-undef */
// 反馈服务测试配置
// 此文件会在页面加载时自动设置反馈服务的基本配置

(function() {
  // 基本配置 - 只启用本地存储和 Google Analytics
  if (typeof window !== 'undefined') {
    // 设置默认的 GitHub 仓库
    window.__GITHUB_REPO__ = 'bytedesk/bytedesk';
    
    // 如果需要启用其他服务，请取消注释并填入正确的值：
    
    // window.__FORMSPREE_ID__ = 'your_formspree_id_here';
    // window.__GITHUB_TOKEN__ = 'your_github_token_here';
    // window.__FEEDBACK_API__ = 'https://your-api.com/feedback';
    
    console.log('反馈服务配置已加载');
  }
})();
