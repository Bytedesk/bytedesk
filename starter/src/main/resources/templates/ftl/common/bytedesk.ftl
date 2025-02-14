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
      show: false,
      text: '需要帮助么',
    },
    bubbleConfig: {
      show: true,
      icon: '👋',
      title: '需要帮助么',
      subtitle: '点击我，与我对话'
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