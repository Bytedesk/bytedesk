/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-09 17:42:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-09 17:46:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
(function() {
  // 获取当前脚本的URL
  const currentScript = document.currentScript;
  const scriptSrc = currentScript.src;
  const baseUrl = scriptSrc.substring(0, scriptSrc.lastIndexOf('/'));

  // 默认配置
  const defaultConfig = {
    position: {
      placement: 'bottom-right',
      margin: {
        bottom: 20,
        right: 20,
        left: 20
      }
    },
    theme: {
      preset: 'blue'
    },
    text: {
      title: 'Hello there.',
      subtitle: 'How can we help?',
      bubbleMessage: {
        show: true,
        icon: '👋',
        title: 'Want to chat about ByteDesk?',
        subtitle: "I'm an AI chatbot here to help you find your way."
      },
      tabLabels: {
        home: 'Home',
        messages: 'Messages',
        help: 'Help',
        news: 'News'
      }
    },
    tabs: {
      home: true,
      messages: true,
      help: false,
      news: false
    },
    showSupport: true,
    chatConfig: {
      org: 'df_org_uid',
      t: 0,
      sid: 'df_ag_uid'
    }
  };

  // 合并配置
  const config = window.BytedeskConfig ? 
    { ...defaultConfig, ...window.BytedeskConfig } : 
    defaultConfig;

  // 创建容器
  const container = document.createElement('div');
  container.id = 'bytedesk-container';
  document.body.appendChild(container);

  // 加载样式
  const style = document.createElement('link');
  style.rel = 'stylesheet';
  style.href = `${baseUrl}/styles.css`;
  document.head.appendChild(style);

  // 加载React和主应用
  const script = document.createElement('script');
  script.src = `${baseUrl}/main.js`;
  script.async = true;
  script.onload = () => {
    // 初始化聊天组件
    window.BytedeskWidget.init(container, config);
  };
  document.body.appendChild(script);
})(); 