<#include "./macro/i18n.ftl" />
<#assign inviteText><@t key="chat.invite.text">需要帮助么</@t></#assign>
<#assign bubbleTitle><@t key="chat.bubble.title">需要帮助么</@t></#assign>
<#assign bubbleSubtitle><@t key="chat.bubble.subtitle">点击我，与我对话</@t></#assign>
<!--微语·智能客服代码 开始 -->
<#--  <script src="https://www.weiyuai.cn/chat/assets/js/float/index.min.js"></script>
<script>
window.ChatFloat({
chatUrl: 'https://www.weiyuai.cn/chat?org=df_org_uid&t=1&sid=df_wg_uid&',
});
</script>  -->
<!--./微语·智能客服代码 结束 -->


<!-- bytedesk.com -->
<script src="https://www.weiyuai.cn/embed/bytedesk-web.js"></script>
<script>
  const bytedeskThemePalette = {
    light: {
      backgroundColor: '#0066FF',
      textColor: '#ffffff'
    },
    dark: {
      backgroundColor: '#3B82F6',
      textColor: '#EAF2FF'
    }
  };

  const resolveBytedeskThemeMode = () => {
    const bsTheme = document.documentElement.getAttribute('data-bs-theme');
    if (bsTheme === 'dark' || bsTheme === 'light') {
      return bsTheme;
    }
    return 'system';
  };

  const resolveBytedeskTheme = () => {
    const mode = resolveBytedeskThemeMode();
    const paletteKey = mode === 'dark' ? 'dark' : 'light';
    return {
      mode,
      backgroundColor: bytedeskThemePalette[paletteKey].backgroundColor,
      textColor: bytedeskThemePalette[paletteKey].textColor
    };
  };

  const getThemeSignature = (theme) => [theme.mode, theme.backgroundColor, theme.textColor].join('|');

  const baseConfig = {
    baseUrl: 'https://www.weiyuai.cn/chat',
    placement: 'bottom-right',
    autoPopup: false,
    inviteConfig: {
      show: false,
      text: '${inviteText?trim}',
      delay: 1000, // 首次弹出延迟时间, 单位: 毫秒
      loop: true, // 是否启用循环
      loopDelay: 10000, // 循环间隔, 单位: 毫秒
      loopCount: 3, // 循环次数, 设置为0表示无限循环
    },
    bubbleConfig: {
      show: true,
      icon: '👋',
      title: '${bubbleTitle?trim}',
      subtitle: '${bubbleSubtitle?trim}'
    },
    theme: resolveBytedeskTheme(),
    window: {
      width: '380'
    },
    chatConfig: {
      org: 'df_org_uid',
      t: '1',
      sid: 'df_wg_uid'
    }
  };
  let bytedesk = null;
  let currentThemeSignature = '';
  let themeSyncScheduled = false;

  const mountBytedesk = () => {
    const theme = resolveBytedeskTheme();
    const config = {
      ...baseConfig,
      theme
    };

    if (bytedesk && typeof bytedesk.destroy === 'function') {
      bytedesk.destroy();
    }

    bytedesk = new BytedeskWeb(config);
    bytedesk.init();
    currentThemeSignature = getThemeSignature(theme);
  };

  const syncBytedeskThemeImmediately = () => {
    const nextTheme = resolveBytedeskTheme();
    const nextSignature = getThemeSignature(nextTheme);
    if (nextSignature === currentThemeSignature) {
      return;
    }
    mountBytedesk();
  };

  mountBytedesk();

  const themeObserver = new MutationObserver(() => {
    if (themeSyncScheduled) {
      return;
    }
    themeSyncScheduled = true;
    requestAnimationFrame(() => {
      themeSyncScheduled = false;
      syncBytedeskThemeImmediately();
    });
  });

  themeObserver.observe(document.documentElement, {
    attributes: true,
    attributeFilter: ['data-bs-theme']
  });
</script>