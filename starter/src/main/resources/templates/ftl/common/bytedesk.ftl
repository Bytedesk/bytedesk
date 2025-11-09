<#include "./macro/i18n.ftl" />
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
  const config = {
    baseUrl: 'https://www.weiyuai.cn/chat',
    placement: 'bottom-right',
    autoPopup: false,
    inviteConfig: {
  show: true,
  text: '<@t key="chat.invite.text">需要帮助么</@t>',
      delay: 1000, // 首次弹出延迟时间, 单位: 毫秒
      loop: true, // 是否启用循环
      loopDelay: 10000, // 循环间隔, 单位: 毫秒
      loopCount: 3, // 循环次数, 设置为0表示无限循环
    },
    bubbleConfig: {
  show: true,
  icon: '👋',
  title: '<@t key="chat.bubble.title">需要帮助么</@t>',
  subtitle: '<@t key="chat.bubble.subtitle">点击我，与我对话</@t>'
    },
    theme: {
      mode: 'system',
      backgroundColor: '#0066FF',
      textColor: '#ffffff'
    },
    window: {
      width: '380'
    },
    chatConfig: {
      org: 'df_org_uid',
      t: '1',
      sid: 'df_wg_uid'
    }
  };
  const bytedesk = new BytedeskWeb(config);
  bytedesk.init();
</script>