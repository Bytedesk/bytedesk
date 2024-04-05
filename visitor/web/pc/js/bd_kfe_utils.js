/**
 * bytedesk.com
 */
var bd_kfe_utils = {
  // 只有在调试模式才会输出
  log: function (content) {
    if (!bd_kfe_data.IS_PRODUCTION) {
      bd_kfe_utils.printLog(content)
    }
  },
  //
  switchEmoji: function () {
    if (!bd_kfe_data.disabled) {
      bd_kfe_data.show_emoji = !bd_kfe_data.show_emoji;
    }
  },
  switchAgent: function () {
    bd_kfe_data.showLeaveMessage = false;
    $("#bytedesk_main").show();
    $("#bytedesk_leave").hide();
    $("#bytedesk_rate").hide();
    bd_kfe_data.isRobot = false;
    bd_kfe_httpapi.requestThread();
  },
  switchLeaveMessage: function () {
    bd_kfe_utils.printLog("switch leave message");
    bd_kfe_data.showLeaveMessage = true;
    $("#bytedesk_main").hide();
    $("#bytedesk_leave").show();
    $("#bytedesk_rate").hide();
  },
  hideLeaveMessage: function () {
    bd_kfe_data.showLeaveMessage = false;
    $("#bytedesk_main").show();
    $("#bytedesk_leave").hide();
  },
  switchForm: function () {
    $("#bytedesk_main").hide();
    $("#bytedesk_leave").hide();
    $("#bytedesk_rate").hide();
    $("#bytedesk_form").show();
  },
  showMessage: function () {
    $("#bytedesk_main").show();
    $("#bytedesk_form").hide();
    $("#bytedesk_leave").hide();
    $("#bytedesk_rate").hide();
  },
  // switchRobot: function () {
  //   bd_kfe_utils.printLog("switch robot");
  //   bd_kfe_data.showLeaveMessage = false;
  //   $("#bytedesk_main").show();
  //   $("#bytedesk_leave").hide();
  //   $("#bytedesk_rate").hide();
  //   bd_kfe_data.isRobot = true;
  //   bd_kfe_httpapi.requestRobot();
  // },
  switchEmotion: function () {
    // bd_kfe_utils.printLog("switch emotion");
    bd_kfe_data.show_emoji = !bd_kfe_data.show_emoji;
    if (bd_kfe_data.show_emoji) {
      $("#bytedesk_input-emoji-box").show();
      bd_kfe_utils.printLog('show')
    } else {
      $("#bytedesk_input-emoji-box").hide();
      bd_kfe_utils.printLog('hide')
    }
  },
  switchRate: function () {
    // bd_kfe_utils.printLog("switch rate");
    // bd_kfe_data.rateDialogVisible = true;
    $("#bytedesk_main").hide();
    $("#bytedesk_leave").hide();
    $("#bytedesk_rate").show();
  },
  hideRate: function () {
    // bd_kfe_data.rateDialogVisible = false;
    $("#bytedesk_main").show();
    $("#bytedesk_rate").hide();
  },
  showUploadImageDialog: function () {
    // bd_kfe_utils.printLog("show upload dialog");
    if (bd_kfe_data.isRobot) {
      alert("自助服务暂不支持图片");
      return;
    }
    if (bd_kfe_data.isThreadClosed) {
      alert("会话已经结束");
      return;
    }
    $('input[id=imagefile]').click();
  },
  showUploadVideoDialog: function () {
    // bd_kfe_utils.printLog("show upload dialog");
    if (bd_kfe_data.isRobot) {
      alert("自助服务暂不支持视频");
      return;
    }
    if (bd_kfe_data.isThreadClosed) {
      alert("会话已经结束");
      return;
    }
    $('input[id=videofile]').click();
  },
  showUploadFileDialog: function () {
    // bd_kfe_utils.printLog("show upload dialog");
    if (bd_kfe_data.isRobot) {
      alert("自助服务暂不支持图片");
      return;
    }
    if (bd_kfe_data.isThreadClosed) {
      alert("会话已经结束");
      return;
    }
    $('input[id=filefile]').click();
  },
  clearMessages: function () {
    bd_kfe_utils.printLog("clearMessages");
  },
  emotionUrl: function (file) {
    return bd_kfe_data.emojiBaseUrl + file;
  },
  // emotionClicked: function(emotion) {
  //   bd_kfe_data.inputContent += emotion;
  //   bd_kfe_data.show_emoji = false;
  // },
  imageClicked: function (imageUrl) {
    // bd_kfe_utils.printLog('image clicked:', imageUrl)
    bd_kfe_data.currentImageUrl = imageUrl;
    window.open(imageUrl);
    // bd_kfe_data.imageDialogVisible = true;
  },
  fileClicked: function (fileUrl) {
    window.open(fileUrl);
  },
  voiceClicked: function (voiceUrl) {
    bd_kfe_data.currentVoiceUrl = voiceUrl;
    window.open(voiceUrl);
  },
  videoClicked: function (videoOrShortUrl) {
    // bd_kfe_data.currentVoiceUrl = videoOrShortUrl;
    window.open(videoOrShortUrl);
  },
  //
  is_self: function (message) {
    return message.user.uid === bd_kfe_data.uid;
  },
  // 发送状态
  is_sending: function (message) {
    return message.status === "sending";
  },
  is_stored: function (message) {
    return message.status === "stored";
  },
  is_received: function (message) {
    return message.status === "received";
  },
  is_error: function (message) {
    return message.status === "error";
  },
  is_read: function (message) {
    return message.status === "readCount";
  },
  // 消息类型
  is_type_text: function (message) {
    return (
      message.type === "text" ||
      message.type === "notification_thread" ||
      message.type === "notification_auto_close"
    );
  },
  is_type_robot: function (message) {
    return message.type === "robot";
  },
  is_type_robot_v2(message) {
    return message.type === 'robotv2'
  },
  is_type_robot_result(message) {
    return message.type === 'robot_result'
  },
  is_type_robot_result_not_found(message) {
    return message.type === 'robot_result_not_found'
  },
  is_type_image: function (message) {
    return message.type === "image";
  },
  is_type_file: function (message) {
    return message.type === "file";
  },
  is_type_voice: function (message) {
    return message.type === "voice";
  },
  is_type_video: function (message) {
    return message.type === "video" || message.type === 'shortvideo';
  },
  is_type_card(message) {
    return message.type === 'card'
  },
  is_type_commodity: function (message) {
    return message.type === 'commodity';
  },
  is_type_questionnaire: function (message) {
    return message.type === "questionnaire";
  },
  is_type_company: function (message) {
    return message.type === "company";
  },
  is_type_workGroup: function (message) {
    return message.type === "workGroup";
  },
  is_type_form_request: function (message) {
    return message.type === "notification_form_request";
  },
  is_type_form_result: function (message) {
    return message.type === "notification_form_result";
  },
  is_type_notification: function (message) {
    return (
      message.type !== "notification_preview" &&
      message.type !== "notification_thread" &&
      message.type !== "notification_form_request" &&
      message.type !== "notification_form_result" &&
      message.type !== 'notification_thread_reentry' &&
      message.type !== 'notification_offline' &&
      message.type !== 'notification_non_working_time' &&
      message.type !== 'notification_queue' &&
      // message.type !== "notification_auto_close" &&
      message.type.startsWith("notification")
    );
  },
  is_type_close: function (message) {
    return message.type === 'notification_auto_close'
      || message.type === 'notification_agent_close'
  },
  is_type_notification_agent_close: function (message) {
    return message.type === 'notification_agent_close'
  },
  is_type_notification_visitor_close: function (message) {
    return message.type === 'notification_visitor_close'
  },
  is_type_notification_auto_close: function (message) {
    return message.type === 'notification_auto_close'
  },
  is_type_notification_connect: function (message) {
    return message.type === 'notification_connect'
  },
  is_type_notification_disconnect: function (message) {
    return message.type === 'notification_disconnect'
  },
  is_type_notification_thread_reentry: function (message) {
    return message.type === 'notification_thread_reentry'
  },
  is_type_notification_offline: function (message) {
    return message.type === 'notification_offline'
  },
  is_type_notification_non_working_time: function (message) {
    return message.type === 'notification_non_working_time'
  },
  is_type_notification_queue: function (message) {
    return message.type === 'notification_queue'
  },
  is_type_notification_queue_accept: function (message) {
    return message.type === 'notification_queue_accept'
  },
  is_type_notification_invite_rate: function (message) {
    return message.type === 'notification_invite_rate'
  },
  is_type_notification_rate_result: function (message) {
    return message.type === 'notification_rate_result'
  },
  is_type_notification_rate_helpful: function (message) {
    return message.type === 'notification_rate_helpful'
  },
  is_type_notification_rate_helpless: function (message) {
    return message.type === 'notification_rate_helpless'
  },
  // 识别链接, FIXME: 对于不带http(s)前缀的url，会被识别为子链接，点击链接无法跳出
  replaceUrl: function (content) {
    if (!content) {
      return content;
    }
    const urlPattern = /(https?:\/\/|www\.)[a-zA-Z_0-9\-@]+(\.\w[a-zA-Z_0-9\-:]+)+(\/[()~#&\-=?+%/.\w]+)?/g;
    return content.replace(urlPattern, (url) => {
      // bd_kfe_utils.printLog('url:', url)
      return `<a href="${url}" target="_blank">${url}</a>`;
    })
  },
  //  在发送信息之后，将输入的内容中属于表情的部分替换成emoji图片标签
  //  再经过v-html 渲染成真正的图片
  replaceFace: function (content) {
    if (content === null || content === undefined) {
      return "";
    }
    // 识别链接
    let replaceUrl = bd_kfe_utils.replaceUrl(content)
    //
    var emotionMap = bd_kfe_data.emotionMap;
    var reg = /\[[\u4E00-\u9FA5NoOK]+\]/g;
    var matchresult = replaceUrl.match(reg);
    var result = replaceUrl;
    if (matchresult) {
      for (var i = 0; i < matchresult.length; i++) {
        result = result.replace(
          matchresult[i],
          "<img height='25px' width='25px' style='margin-bottom:4px;' src='" +
          bd_kfe_data.emotionBaseUrl +
          emotionMap[matchresult[i]] +
          "'>"
        );
      }
    }
    return result;
  },
  //
  scrollToBottom: function () {
    // bd_kfe_utils.printLog('scrollToBottom')
    // 聊天记录滚动到最底部
    $("#bytedesk_message_ul").animate(
      { scrollTop: $("#bytedesk_message_ul")[0].scrollHeight },
      "fast"
    );
  },
  //
  pushToMessageArray: function (message, isPrepend = false) {
    // 本地发送的消息
    var contains = false;
    if (message.status === 'sending') {
      bd_kfe_data.messages.push(message);
    } else {
      // 如果最后一条为系统结束提示，再来一条系统结束提示 则忽略
      if (bd_kfe_data.messages.length > 0) {
        var msg = bd_kfe_data.messages[bd_kfe_data.messages.length - 1];
        if (bd_kfe_utils.is_type_close(msg) && bd_kfe_utils.is_type_close(message)) {
          return
        }
      }
      // 根据mid判断是否已经存在此条消息
      for (var i = bd_kfe_data.messages.length - 1; i >= 0; i--) {
        var msg = bd_kfe_data.messages[i];
        if (msg.mid === message.mid) {
          bd_kfe_data.messages.splice(i, 1);
          bd_kfe_data.messages.push(message);
          contains = true;
        }
      }
    }
    if (!contains) {
      bd_kfe_data.messages.push(message);
    } else {
      return;
    }
    // 
    if (bd_kfe_utils.is_type_commodity(message)) {
      var content = "<div id='goods' class='bytedesk_goods_info'>" +
        "<div class='bytedesk_goods_pic'>" +
        "<img id='bytedesk_goods_pic' width='50px' height='50px' src='" + JSON.parse(message.content).imageUrl + "'/>" +
        "</div>" +
        "<div class='bytedesk_goods_desc'>" +
        "<div id='bytedesk_goods_name' class='bytedesk_goods_name'>" + JSON.parse(message.content).title + "</div>" +
        "<div class='bytedesk_goods_more'>" +
        "<div id='bytedesk_goods_price' class='bytedesk_goods_price'>￥" + JSON.parse(message.content).price + "</div>" +
        "<div id='bytedesk_goods_sendlink' class='bytedesk_goods_sendlink' onclick='bd_kfe_stompapi.sendCommodityMessageSync()'>发送链接</div>" +
        "</div>" +
        "</div>" +
        "</div>";
      if (isPrepend) {
        $("#bytedesk_message_li").prepend(
          "<li><p class='bytedesk_timestamp'>" +
          "<span>" +
          message.createdAt +
          "</span><br/>" +
          "<span>" +
          content +
          "</span>" +
          "</p></li>"
        );
      } else {
        $("#bytedesk_message_li").append(
          "<li><p class='bytedesk_timestamp'>" +
          "<span>" +
          message.createdAt +
          "</span><br/>" +
          "<span>" +
          content +
          "</span>" +
          "</p></li>"
        );
      }
    }
    else if (bd_kfe_utils.is_type_notification_agent_close(message)) {
      var langText = "联系客服"
      var contentText = "客服关闭会话"
      if (bd_kfe_data.lang === "en") {
        langText = "Contact Agent"
        contentText = "agent close thread"
      }
      if (isPrepend) {
        $("#bytedesk_message_li").prepend(
          "<li><p class='bytedesk_timestamp'>" +
          "<span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.requestThread(true)'>" + langText + "</span><br/>" +
          "<span>" +
          contentText +
          "</span>" +
          "</p></li>"
        );
      } else {
        $("#bytedesk_message_li").append(
          "<li><p class='bytedesk_timestamp'>" +
          "<span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.requestThread(true)'>" + langText + "</span><br/>" +
          "<span>" +
          contentText +
          "</span>" +
          "</p></li>"
        );
      }
    }
    else if (bd_kfe_utils.is_type_notification_visitor_close(message)) {
      var langText = "联系客服"
      var contentText = "访客关闭会话"
      if (bd_kfe_data.lang === "en") {
        langText = "Contact Agent"
        contentText = "visitor close thread"
      }
      if (isPrepend) {
        $("#bytedesk_message_li").prepend(
          "<li><p class='bytedesk_timestamp'>" +
          "<span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.requestThread(true)'>" + langText + "</span><br/>" +
          "<span>" +
          contentText +
          "</span>" +
          "</p></li>"
        );
      } else {
        $("#bytedesk_message_li").append(
          "<li><p class='bytedesk_timestamp'>" +
          "<span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.requestThread(true)'>" + langText + "</span><br/>" +
          "<span>" +
          contentText +
          "</span>" +
          "</p></li>"
        );
      }
    }
    else if (bd_kfe_utils.is_type_notification_auto_close(message)) {
      var langText = "联系客服"
      var contentText = "长时间没有对话，系统自动关闭会话"
      if (bd_kfe_data.lang === "en") {
        langText = "Contact Agent"
        contentText = "system close thread"
      }
      if (isPrepend) {
        $("#bytedesk_message_li").prepend(
          "<li><p class='bytedesk_timestamp'>" +
          "<span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.requestThread(true)'>" + langText + "</span><br/>" +
          "<span>" +
          contentText +
          "</span>" +
          "</p></li>"
        );
      } else {
        $("#bytedesk_message_li").append(
          "<li><p class='bytedesk_timestamp'>" +
          "<span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.requestThread(true)'>" + langText + "</span><br/>" +
          "<span>" +
          contentText +
          "</span>" +
          "</p></li>"
        );
      }
    }
    else if (bd_kfe_utils.is_type_notification_queue_accept(message)) {
      var langText = "接入会话"
      if (bd_kfe_data.lang === "en") {
        langText = "start chat"
      }
      if (isPrepend) {
        $("#bytedesk_message_li").prepend(
          "<li><p class='bytedesk_timestamp'>" +
          "<span>" +
          message.createdAt +
          "</span><br/>" +
          "<span>" +
          langText +
          "</span>" +
          "</p></li>"
        );
      } else {
        $("#bytedesk_message_li").append(
          "<li><p class='bytedesk_timestamp'>" +
          "<span>" +
          message.createdAt +
          "</span><br/>" +
          "<span>" +
          langText +
          "</span>" +
          "</p></li>"
        );
      }
    }
    else if (bd_kfe_utils.is_type_notification_invite_rate(message)) {
      var langText = "邀请评价"
      if (bd_kfe_data.lang === "en") {
        langText = "invite rate"
      }
      if (isPrepend) {
        $("#bytedesk_message_li").prepend(
          "<li><p class='bytedesk_timestamp'>" +
          "<span>" +
          message.createdAt +
          "</span><br/>" +
          "<span>" +
          langText +
          "</span>" +
          "</p></li>"
        );
      } else {
        $("#bytedesk_message_li").append(
          "<li><p class='bytedesk_timestamp'>" +
          "<span>" +
          message.createdAt +
          "</span><br/>" +
          "<span>" +
          langText +
          "</span>" +
          "</p></li>"
        );
      }
    }
    else if (bd_kfe_utils.is_type_notification_rate_result(message)) {
      var langText = "已评价"
      if (bd_kfe_data.lang === "en") {
        langText = "rated"
      }
      if (isPrepend) {
        $("#bytedesk_message_li").prepend(
          "<li><p class='bytedesk_timestamp'>" +
          "<span>" +
          message.createdAt +
          "</span><br/>" +
          "<span>" +
          langText +
          "</span>" +
          "</p></li>"
        );
      } else {
        $("#bytedesk_message_li").append(
          "<li><p class='bytedesk_timestamp'>" +
          "<span>" +
          message.createdAt +
          "</span><br/>" +
          "<span>" +
          langText +
          "</span>" +
          "</p></li>"
        );
      }
    }
    else if (bd_kfe_utils.is_type_notification_rate_helpful(message)) {
      if (isPrepend) {
        $("#bytedesk_message_li").prepend(
          "<li><p class='bytedesk_timestamp'>" +
          "<span>" +
          message.createdAt +
          "</span><br/>" +
          "<span>" +
          message.content +
          "</span>" +
          "</p></li>"
        );
      } else {
        $("#bytedesk_message_li").append(
          "<li><p class='bytedesk_timestamp'>" +
          "<span>" +
          message.createdAt +
          "</span><br/>" +
          "<span>" +
          message.content +
          "</span>" +
          "</p></li>"
        );
      }
    }
    else if (bd_kfe_utils.is_type_notification_rate_helpless(message)) {
      var langText = "人工客服"
      if (bd_kfe_data.lang === "en") {
        langText = "Contact Agent"
      }
      if (isPrepend) {
        $("#bytedesk_message_li").prepend(
          "<li><p class='bytedesk_timestamp'>" +
          "<span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.requestAgent()'>" + langText + "</span>" +
          "</p></li>"
        );
      } else {
        $("#bytedesk_message_li").append(
          "<li><p class='bytedesk_timestamp'>" +
          "<span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.requestAgent()'>" + langText + "</span>" +
          "</p></li>"
        );
      }
    }
    else if (bd_kfe_utils.is_type_notification(message)) {
      if (isPrepend) {
        $("#bytedesk_message_li").prepend(
          "<li><p class='bytedesk_timestamp'>" +
          "<span>" +
          message.createdAt +
          "</span><br/>" +
          "<span>" +
          message.content +
          "</span>" +
          "</p></li>"
        );
      } else {
        $("#bytedesk_message_li").append(
          "<li><p class='bytedesk_timestamp'>" +
          "<span>" +
          message.createdAt +
          "</span><br/>" +
          "<span>" +
          message.content +
          "</span>" +
          "</p></li>"
        );
      }
    } else {
      //
      var content =
        "<p class='bytedesk_timestamp'><span>" + message.createdAt + "</span><br/></p>";
      content +=
        "<img class='bytedesk_avatar' width='30' height='30' src='" +
        message.user.avatar +
        "'/>";
      // 暂不显示客服昵称
      // if (!bd_kfe_utils.is_self(message)) {
      //   content += "<div class='bytedesk_nickname'>" + message.user.nickname + "</div>";
      // }
      if (bd_kfe_utils.is_type_text(message)) {
        var question = "";
        if (message.answers != undefined) {
          for (var j = 0; j < message.answers.length; j++) {
            var answer = message.answers[j];
            question += ("<br/><span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.queryAnswer(" + answer.aid + ",\"" + encodeURIComponent(answer.question) + "\"," + "\"" + encodeURIComponent(answer.answer) + "\")'>" + answer.question + "</span>");
          }
        }
        // TODO: 点击消息内容，弹窗用户选择：复制消息内容 or 撤回消息(3分钟之内)
        content += "<div class='bytedesk_text' id='content-" + message.mid + "'>" +
          bd_kfe_utils.replaceFace(message.content) + question +
          "</div>";
        if (bd_kfe_utils.is_self(message)) {
          if (message.status === "received") {
            var langText = "送达"
            if (bd_kfe_data.lang === "en") {
              langText = "received"
            }
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'>" + langText + "</div>";
          } else if (message.status === "read") {
            var langText = "已读"
            if (bd_kfe_data.lang === "en") {
              langText = "read"
            }
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'>" + langText + "</div>";
          } else {
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'></div>";
          }
        }
      } else if (bd_kfe_utils.is_type_image(message)) {
        content +=
          "<div class='bytedesk_image-bubble' id='content-" + message.mid + "'>" +
          "<img src='" +
          message.imageUrl +
          "' alt='[图片]' class='bytedesk_image' onclick=\"bd_kfe_utils.imageClicked('" +
          encodeURI(message.imageUrl) +
          "')\"/>" +
          "</div>";
        if (bd_kfe_utils.is_self(message)) {
          if (message.status === "received") {
            var langText = "送达"
            if (bd_kfe_data.lang === "en") {
              langText = "received"
            }
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'>" + langText + "</div>";
          } else if (message.status === "read") {
            var langText = "已读"
            if (bd_kfe_data.lang === "en") {
              langText = "read"
            }
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'>" + langText + "</div>";
          } else {
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'></div>";
          }
        }
      } else if (bd_kfe_utils.is_type_file(message)) {
        content +=
          "<div class='bytedesk_text' id='content-" + message.mid + "'>" +
          "<span><a href='" +
          encodeURI(message.fileUrl) +
          "' target='_blank'>查看文件</a></span >" +
          "</div>";
        if (bd_kfe_utils.is_self(message)) {
          if (message.status === "received") {
            var langText = "送达"
            if (bd_kfe_data.lang === "en") {
              langText = "received"
            }
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'>" + langText + "</div>";
          } else if (message.status === "read") {
            var langText = "已读"
            if (bd_kfe_data.lang === "en") {
              langText = "read"
            }
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'>" + langText + "</div>";
          } else {
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'></div>";
          }
        }
      } else if (bd_kfe_utils.is_type_voice(message)) {
        content += "<audio controls>" +
          "<source src=\"" + message.voiceUrl + "\" type = \"audio/ogg\">" +
          "<source src=\"" + message.voiceUrl + "\" type=\"audio/mpeg\">" +
          "Your browser does not support the audio element." +
          "</audio>";
        if (bd_kfe_utils.is_self(message)) {
          if (message.status === "received") {
            var langText = "送达"
            if (bd_kfe_data.lang === "en") {
              langText = "received"
            }
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'>" + langText + "</div>";
          } else if (message.status === "read") {
            var langText = "已读"
            if (bd_kfe_data.lang === "en") {
              langText = "read"
            }
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'>" + langText + "</div>";
          } else {
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'></div>";
          }
        }
      } else if (bd_kfe_utils.is_type_video(message)) {
        content += "<div class='bytedesk_video-bubble' id='content-" + message.mid + "'>" +
          "<video width=\"220\" height=\"200\" controls>" +
          "<source src=\"" + message.videoOrShortUrl + "\" type = \"video/mp4\">" +
          "<source src=\"" + message.videoOrShortUrl + "\" type=\"video/ogg\">" +
          "Your browser does not support the video tag." +
          "</video>" +
          "</div>";
        if (bd_kfe_utils.is_self(message)) {
          if (message.status === "received") {
            var langText = "送达"
            if (bd_kfe_data.lang === "en") {
              langText = "received"
            }
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'>" + langText + "</div>";
          } else if (message.status === "read") {
            var langText = "已读"
            if (bd_kfe_data.lang === "en") {
              langText = "read"
            }
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'>" + langText + "</div>";
          } else {
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'></div>";
          }
        }
      } else if (bd_kfe_utils.is_type_card(message)) {
        content += "<div class='bytedesk_text' id='content-" + message.mid + "'>" +
          "<a href=" + JSON.parse(message.content).content + " target=\"_blank\">" + JSON.parse(message.content).name + "</a>" +
          "</div>";
        if (bd_kfe_utils.is_self(message)) {
          if (message.status === "received") {
            var langText = "送达"
            if (bd_kfe_data.lang === "en") {
              langText = "received"
            }
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'>" + langText + "</div>";
          } else if (message.status === "read") {
            var langText = "已读"
            if (bd_kfe_data.lang === "en") {
              langText = "read"
            }
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'>" + langText + "</div>";
          } else {
            content += "<div class='bytedesk_status' id='status-" + message.mid + "'></div>";
          }
        }
      } else if (bd_kfe_utils.is_type_robot(message)) {
        // bd_kfe_utils.printLog("robot:", message.content);
        // TODO: 添加 ‘有帮助’ 和 ‘无帮助’
        let flag = false
        if (message.content !== undefined && message.content !== null && message.content.length > 0) {
          flag = true
        }
        var question = "";
        if (message.answers != undefined) {
          for (var j = 0; j < message.answers.length; j++) {
            var answer = message.answers[j];
            question += ((flag ? "<br/>" : "") + "<span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.queryAnswer(" + answer.aid + ",\"" + encodeURIComponent(answer.question) + "\"," + "\"" + encodeURIComponent(answer.answer) + "\")'>" + answer.question + "</span>");
          }
        }
        //
        content +=
          "<div class='bytedesk_text'>" +
          (flag ? "<span>" + message.content + "</span>" : "") +
          question +
          "</div>";
      } else if (bd_kfe_utils.is_type_robot_v2(message)) {
        var categories = "";
        if (message.categories != undefined) {
          for (var j = 0; j < message.categories.length; j++) {
            var category = message.categories[j];
            categories += "<br/><span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.queryCategory(" + category.cid + ",\"" + category.name + "\")'>" + category.name + "</span>";
          }
        }
        //
        content +=
          "<div class='bytedesk_text'>" +
          "<span>" + message.content + "</span>" +
          categories +
          "</div>";
      } else if (bd_kfe_utils.is_type_form_request(message)) {
        content += "<div class='bytedesk_text' id='content-" + message.mid + "'>" +
          "请求表单" +
          "</div>";
      } else if (bd_kfe_utils.is_type_form_result(message)) {
        content += "<div class='bytedesk_text'>" +
          "<div>姓名：" + JSON.parse(message.content).form.realname + "</div>" +
          "<div>手机：" + JSON.parse(message.content).form.mobile + "</div>" +
          "<div>邮箱：" + JSON.parse(message.content).form.email + "</div>" +
          "<div>年龄：" + JSON.parse(message.content).form.age + "</div>" +
          "<div>工作：" + JSON.parse(message.content).form.job + "</div>" +
          "</div>";
      } else if (bd_kfe_utils.is_type_notification_thread_reentry(message)) {
        var question = "";
        if (message.answers != undefined) {
          for (var j = 0; j < message.answers.length; j++) {
            var answer = message.answers[j];
            question += ("<br/><span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.queryAnswer(" + answer.aid + ",\"" + encodeURIComponent(answer.question) + "\"," + "\"" + encodeURIComponent(answer.answer) + "\")'>" + answer.question + "</span>");
          }
        }
        content += "<div class='bytedesk_text' id='content-" + message.mid + "'>" +
          message.content + question +
          "</div>";
      } else if (bd_kfe_utils.is_type_notification_offline(message)) {
        var question = "";
        if (message.answers != undefined) {
          for (var j = 0; j < message.answers.length; j++) {
            var answer = message.answers[j];
            question += ("<br/><span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.queryAnswer(" + answer.aid + ",\"" + encodeURIComponent(answer.question) + "\"," + "\"" + encodeURIComponent(answer.answer) + "\")'>" + answer.question + "</span>");
          }
        }
        content += "<div class='bytedesk_text' id='content-" + message.mid + "'>" +
          message.content + question +
          "</div>";
      } else if (bd_kfe_utils.is_type_notification_non_working_time(message)) {
        var question = "";
        if (message.answers != undefined) {
          for (var j = 0; j < message.answers.length; j++) {
            var answer = message.answers[j];
            question += ("<br/><span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.queryAnswer(" + answer.aid + ",\"" + encodeURIComponent(answer.question) + "\"," + "\"" + encodeURIComponent(answer.answer) + "\")'>" + answer.question + "</span>");
          }
        }
        content += "<div class='bytedesk_text' id='content-" + message.mid + "'>" +
          message.content + question +
          "</div>";
      } else if (bd_kfe_utils.is_type_notification_queue(message)) {
        var question = "";
        if (message.answers != undefined) {
          for (var j = 0; j < message.answers.length; j++) {
            var answer = message.answers[j];
            question += ("<br/><span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.queryAnswer(" + answer.aid + ",\"" + encodeURIComponent(answer.question) + "\"," + "\"" + encodeURIComponent(answer.answer) + "\")'>" + answer.question + "</span>");
          }
        }
        content += "<div class='bytedesk_text' id='content-" + message.mid + "'>" +
          message.content + question +
          "</div>";
      }
      if (bd_kfe_utils.is_self(message)) {
        if (isPrepend) {
          $("#bytedesk_message_li").prepend("<li><div class='self'>" + content + "</div></li>");
        } else {
          $("#bytedesk_message_li").append("<li><div class='self'>" + content + "</div></li>");
        }
      } else {
        if (isPrepend) {
          $("#bytedesk_message_li").prepend("<li><div class='other' id='other" + message.mid + "'>" + content + "</div></li>");
        } else {
          $("#bytedesk_message_li").append("<li><div class='other' id='other" + message.mid + "'>" + content + "</div></li>");
        }
      }
    }
    //
    if (!isPrepend) {
      bd_kfe_utils.scrollToBottom();
    }
  },
  // 收到机器人正确答案
  pushRightAnswerToMessageArray: function (message) {
    // 本地发送的消息
    if (message.status === bd_kfe_data.MESSAGE_STATUS_SENDING) {
      bd_kfe_data.messages.push(message);
      return;
    }
    //
    var contains = false;
    for (var i = bd_kfe_data.messages.length - 1; i >= 0; i--) {
      var msg = bd_kfe_data.messages[i];
      // 根据localId替换本地消息，也即更新本地消息状态
      if (msg.mid === message.mid) {
        bd_kfe_data.messages.splice(i, 1);
        bd_kfe_data.messages.push(message);
        contains = true;
      }
    }
    if (!contains) {
      bd_kfe_data.messages.push(message);
    } else {
      return;
    }
    //
    var content = "<p class='bytedesk_timestamp'><span>" + message.createdAt + "</span><br/></p>";
    content += "<img class='bytedesk_avatar' width='30' height='30' src='" + message.user.avatar + "'/>";
    // if (!bd_kfe_utils.is_self(message)) {
    //   content += "<div class='bytedesk_nickname'>" + message.user.nickname + "</div>";
    // }
    let flag = false
    if (message.content !== undefined && message.content !== null && message.content.length > 0) {
      flag = true
    }
    // 添加 ‘有帮助’ 和 ‘无帮助’
    var question = "";
    for (var j = 0; j < message.answers.length; j++) {
      var answer = message.answers[j];
      question += ((flag ? "<br/>" : "") + "<span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.queryAnswer(" + answer.aid + ",\"" + encodeURIComponent(answer.question) + "\"," + "\"" + encodeURIComponent(answer.answer) + "\")'>" + answer.question + "</span>");
    }
    //
    var langHelpfullText = "有帮助"
    var langHelplessText = "无帮助"
    if (bd_kfe_data.lang === "en") {
      langHelpfullText = "Helpfull"
      langHelplessText = "Helpless"
    }
    var isHelpfull = "<br/><span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.rateAnswer(\"" + message.answer.aid + "\",\"" + message.mid + "\",true)'>" + langHelpfullText + "</span>" +
      "<span style='color:#007bff; cursor: pointer; margin-left: 5px;' onclick='bd_kfe_httpapi.rateAnswer(\"" + message.answer.aid + "\",\"" + message.mid + "\",false)'>" + langHelplessText + "</span>";
    content +=
      "<div class='bytedesk_text' id='content-" + message.mid + "'>" +
      (flag ? "<span>" + message.content + "</span>" : "") +
      question + isHelpfull +
      "</div>";
    //
    var rate = "";
    // "<div class='bytedesk_robot-rate'>" +
    //   "<p class='bytedesk_robot-rate-agree' id='rate-" + message.mid + "'><i class='iconfont icon-agree'></i></p>" +
    //   "<p class='bytedesk_robot-rate-disagree' id='rate-" + message.mid + "'><i class='iconfont icon-disagree'></i></p>" +
    // "</div>";
    //
    $("#bytedesk_message_li").append("<li><div class='other'>" + content + "</div>" + rate + "</li>");
    bd_kfe_utils.scrollToBottom();
  },
  // 未匹配到机器人答案
  pushNoAnswerToMessageArray: function (message) {
    // 本地发送的消息
    if (message.status === 'sending') {
      bd_kfe_data.messages.push(message);
      return;
    }
    //
    var contains = false;
    for (var i = bd_kfe_data.messages.length - 1; i >= 0; i--) {
      var msg = bd_kfe_data.messages[i];
      // 根据localId替换本地消息，也即更新本地消息状态
      if (msg.mid === message.mid) {
        bd_kfe_data.messages.splice(i, 1);
        bd_kfe_data.messages.push(message);
        contains = true;
      }
    }
    if (!contains) {
      bd_kfe_data.messages.push(message);
    } else {
      return;
    }
    //
    var content = "<p class='bytedesk_timestamp'><span>" + message.createdAt + "</span><br/></p>";
    content +=
      "<img class='bytedesk_avatar' width='30' height='30' src='" +
      message.user.avatar +
      "'/>";
    // if (!bd_kfe_utils.is_self(message)) {
    //   content += "<div class='bytedesk_nickname'>" + message.user.nickname + "</div>";
    // }
    // TODO: 回答内容中添加 '人工客服' 字样，访客点击可直接联系人工客服
    var question = "";
    for (var j = 0; j < message.answers.length; j++) {
      var answer = message.answers[j];
      question += "<br/><span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.queryAnswer(" + answer.aid + ",\"" + encodeURIComponent(answer.question) + "\"," + "\"" + encodeURIComponent(answer.answer) + "\")'>" + answer.question + "</span>";
    }
    //
    var langText = "人工客服"
    if (bd_kfe_data.lang === "en") {
      langText = "Contact Agent"
    }
    var contactAgent = "<br/><span style='color:#007bff; cursor: pointer;' onclick='bd_kfe_httpapi.requestAgent()'>" + langText + "</span>";
    content +=
      "<div class='bytedesk_text'>" +
      "<span>" + message.content + "</span>" +
      question + contactAgent +
      "</div>";
    $("#bytedesk_message_li").append("<li><div class='other'>" + content + "</div></li>");
    bd_kfe_utils.scrollToBottom();
  },
  browserTitleNotification() {
    var langText = "(新消息)"
    if (bd_kfe_data.lang === "en") {
      langText = "(New Message)"
    }
    document.title = langText + bd_kfe_data.browserTitle;
  },
  restoreBrowserTitle() {
    document.title = bd_kfe_data.browserTitle;
  },
  requestNotification() {
    // 如果浏览器支持通知，而且用户之前没有授权过通知或者拒绝过通知
    // let app = this;
    if (window.Notification && Notification.permission !== "granted") {
      //
      Notification.requestPermission(function (status) {
        if (status === "granted") {
          // var notification = new Notification('通知', {
          //   body: '消息开启成功'
          // })
        } else {
          // alert(app.$t('requestPermission'))
        }
      });
    } else {
      // bd_kfe_utils.printLog('已经授权或浏览器不支持通知')
    }
  },
  // 显示通知
  // http://javascript.ruanyifeng.com/bom/notification.html
  showNotification(content) {
    // this.notificationGranted(result)
    var langText = "萝卜丝"
    if (bd_kfe_data.lang === "en") {
      langText = "Bytedesk"
    }
    var notification = new Notification(langText, {
      // dir: 'auto',
      // lang: 'zh-CN',
      body: content,
      // tag: 'tag',
      icon: "https://cdn.bytedesk.com/assets/icon/64.png",
    });
    // notification.title // '收到新邮件'
    // notification.body // '您总共有3封未读邮件。'
    //
    notification.onshow = function () {
      // bd_kfe_utils.printLog('Notification shown')
      // 自动关闭
      // setTimeout(notification.close.bind(notification), 5000)
    };
    //
    notification.onclick = function () {
      // bd_kfe_utils.printLog('notification click')
      // notification.close() //不需要手动关闭，点击之后自会关闭
      // TODO: 点击通知，打开当前对话窗口
    };
    //
    notification.onclose = function () {
      // bd_kfe_utils.printLog('notification close')
    };
    //
    notification.onerror = function () {
      // bd_kfe_utils.printLog('notification error')
    };
  },
  // 获取url参数
  getUrlParam: function (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg); //匹配目标参数
    if (r != null) return decodeURIComponent(r[2]);
    return null; //返回参数值
  },
  // 对话界面右侧-常见问题列表
  pushAnswers: function (answers) {
    if (answers.length > 0) {
      $("#bytedesk_question-null").remove()
    }
    var content = "";
    for (var i = 0; i < answers.length; i++) {
      var answer = answers[i];
      content +=
        "<li class='bytedesk_question' onclick='bd_kfe_httpapi.queryAnswer(" + answer.aid + ",\"" + encodeURIComponent(answer.question) + "\"," + "\"" + encodeURIComponent(answer.answer) + "\")'>" +
        answer.question +
        "</li>";
    }
    $("#bytedesk_question").append(content);
  },
  //
  updateConnection: function (isConnected) {
    if (isConnected) {
      $("#bytedesk_connected-image").attr(
        "src",
        "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/util/connected.png"
      );
    } else {
      $("#bytedesk_connected-image").attr(
        "src",
        "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/util/disconnected.png"
      );
    }
  },
  //
  onKeyUp: function (event) {
    var key = event.keyCode || window.event.keyCode;
    // bd_kfe_utils.printLog("onKeyUp:", key);
    if (key === 13) {
      bd_kfe_stompapi.sendTextMessage();
    }
    // 发送消息预知
    bd_kfe_stompapi.onInputChange();
  },
  //
  toggleInputTip: function (show) {
    if (show) {
      // $("#bytedesk_message_tip").show();
      if (bd_kfe_data.lang === "en") {
        $('#bytedesk_agent_description').text("inputing");
      } else {
        $('#bytedesk_agent_description').text("对方正在输入");
      }
    } else {
      // $("#bytedesk_message_tip").hide();
      $('#bytedesk_agent_description').text(bd_kfe_data.agentDescription);
    }
  },
  showRightColumn: function (workTabs) {
    // $("#bytedesk_left").css("width", "calc(75% - 5px);");
    $("#bytedesk_right").show();
    if (workTabs.length === 0) {
      console.log('默认显示faq-0')
      $("#bytedesk_right_tab").append("<button class='bytedesk_right_tablinks'>常见问题</button>")
      $("#bytedesk_right_tabcontent").append("<div id='faq' class='bytedesk_right_tabcontent'>" +
        "<div id=\"bytedesk_question\"></div>" +
        "</div >")
      bd_kfe_httpapi.getTopAnswers();
    }
    //
    var atLeastOneFlag = false
    for (let i = 0; i < workTabs.length; i++) {
      const workTab = workTabs[i];
      if (workTab.published) {
        atLeastOneFlag = true
        $("#bytedesk_right_tab").append("<button class='bytedesk_right_tablinks' onclick='bd_kfe_utils.switchRightTab(event, \"" + workTab.wid + "\")'>" + workTab.title + "</button>")
        if (workTab.type === 'faq') {
          $("#bytedesk_right_tabcontent").append("<div id='" + workTab.wid + "' class='bytedesk_right_tabcontent'>" +
            "<div id=\"bytedesk_question\"></div>" +
            "</div >")
          bd_kfe_httpapi.getTopAnswers();
        } else if (workTab.type === 'card') {
          $("#bytedesk_right_tabcontent").append("<div id='" + workTab.wid + "' class='bytedesk_right_tabcontent'>TODO:CARD</div >")
        } else if (workTab.type === 'url') {
          $("#bytedesk_right_tabcontent").append("<div id='" + workTab.wid + "' class='bytedesk_right_tabcontent'><iframe id=\"bytedesk_right_iframePanel\" class=\"bytedesk_right_fusion\" src=\"" + workTab.content + "\"></iframe></div >")
        } else if (workTab.type === 'custom') {
          $("#bytedesk_right_tabcontent").append("<div id='" + workTab.wid + "' class='bytedesk_right_tabcontent'>" + workTab.content + "</div >")
        } else {
          $("#bytedesk_right_tabcontent").append("<div id='" + workTab.wid + "' class='bytedesk_right_tabcontent'><h3>" + workTab.title + "</h3><p>未知类型.</p></div >")
        }
      }
    }
    if (!atLeastOneFlag) {
      console.log('默认显示faq-1')
      $("#bytedesk_right_tab").append("<button class='bytedesk_right_tablinks'>常见问题</button>")
      $("#bytedesk_right_tabcontent").append("<div id='faq' class='bytedesk_right_tabcontent'>" +
        "<div id=\"bytedesk_question\"></div>" +
        "</div >")
      bd_kfe_httpapi.getTopAnswers();
    }
    //
    bd_kfe_utils.rightTabInit()
  },
  switchRightTab: function (evt, cityName) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("bytedesk_right_tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
      tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("bytedesk_right_tablinks");
    for (i = 0; i < tablinks.length; i++) {
      tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(cityName).style.display = "block";
    evt.currentTarget.className += " active";
  },
  rightTabInit: function () {
    var tabs = document.getElementsByClassName("bytedesk_right_tablinks");
    if (tabs.length > 0) {
      tabs[0].className += " active"
    }
    //
    var tabcontents = document.getElementsByClassName("bytedesk_right_tabcontent");
    for (var i = 1; i < tabcontents.length; i++) {
      tabcontents[i].style.display = "none";
    }
  },
  dragRightColumnInit: function () {
    var resize = document.getElementById("bytedesk_resize");
    var left = document.getElementById("bytedesk_left");
    var right = document.getElementById("bytedesk_right");
    var box = document.getElementById("bytedesk_main");
    resize.onmousedown = function (e) {
      var startX = e.clientX;
      resize.left = resize.offsetLeft;
      document.onmousemove = function (e) {
        var endX = e.clientX;

        var moveLen = resize.left + (endX - startX);
        var maxT = box.clientWidth - resize.offsetWidth;
        if (moveLen < 150) moveLen = 150;
        if (moveLen > maxT - 150) moveLen = maxT - 150;

        resize.style.left = moveLen;
        left.style.width = moveLen + "px";
        right.style.width = (box.clientWidth - moveLen - 5) + "px";
      }
      document.onmouseup = function (evt) {
        document.onmousemove = null;
        document.onmouseup = null;
        resize.releaseCapture && resize.releaseCapture();
      }
      resize.setCapture && resize.setCapture();
      return false;
    }
  },
  //
  toggleRequestAgentTip: function (show) {
    if (show) {
      // 显示转人工按钮
      $("#bytedesk_message_agent").show();
      // 隐藏上传图片按钮
      $("#bytedesk_upload_image").hide();
      $("#bytedesk_upload_video").hide();
      $("#bytedesk_upload_file").hide();
      $("#bytedesk_message_rate").hide();
    } else {
      // 隐藏转人工按钮
      $("#bytedesk_message_agent").hide();
      // 显示上传图片按钮
      $("#bytedesk_upload_image").show();
      $("#bytedesk_upload_video").show();
      $("#bytedesk_upload_file").show();
      $("#bytedesk_message_rate").show();
    }
  },
  //
  rateStarChoose: function (index) {
    if (index == 1) {
      if ($("#ratestar1").attr("src") == "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png") {
        $("#ratestar2").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_unselected.png");
        $("#ratestar3").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_unselected.png");
        $("#ratestar4").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_unselected.png");
        $("#ratestar5").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_unselected.png");
      } else {
        $("#ratestar1").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
      }
      $("#ratescore").text("恶劣");
      bd_kfe_data.rateScore = 1;
    } else if (index == 2) {
      if ($("#ratestar2").attr("src") == "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png") {
        $("#ratestar3").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_unselected.png");
        $("#ratestar4").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_unselected.png");
        $("#ratestar5").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_unselected.png");
      } else {
        $("#ratestar1").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
        $("#ratestar2").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
      }
      $("#ratescore").text("较差");
      bd_kfe_data.rateScore = 2;
    } else if (index == 3) {
      if ($("#ratestar3").attr("src") == "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png") {
        $("#ratestar4").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_unselected.png");
        $("#ratestar5").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_unselected.png");
      } else {
        $("#ratestar2").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
        $("#ratestar3").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
      }
      $("#ratescore").text("一般");
      bd_kfe_data.rateScore = 3;
    } else if (index == 4) {
      if ($("#ratestar4").attr("src") == "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png") {
        $("#ratestar5").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_unselected.png");
      } else {
        $("#ratestar1").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
        $("#ratestar2").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
        $("#ratestar3").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
        $("#ratestar4").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
      }
      $("#ratescore").text("较好");
      bd_kfe_data.rateScore = 4;
    } else if (index == 5) {
      if ($("#ratestar5").attr("src") == "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png") {
      } else {
        $("#ratestar1").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
        $("#ratestar2").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
        $("#ratestar3").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
        $("#ratestar4").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
        $("#ratestar5").attr("src", "https://cdn.bytedesk.com/assets/img/rate/ratestar_selected.png");
      }
      $("#ratescore").text("非常满意");
      bd_kfe_data.rateScore = 5;
    }
  },
  //
  guid: function () {
    function s4() {
      return Math.floor((1 + Math.random()) * 0x10000)
        .toString(16)
        .substring(1);
    }
    let timestamp = moment(new Date()).format('YYYYMMDDHHmmss')
    return timestamp + s4() + s4() + s4() + s4() + s4() + s4() + s4() + s4()
  },
  currentTimestamp: function () {
    return moment().format('YYYY-MM-DD HH:mm:ss');
  },
  timeUuid: function () {
    return moment(new Date()).format('YYYYMMDDHHmmss')
  },
  //
  isMobile: function () {
    function Android() {
      return navigator.userAgent.match(/Android/i);
    }
    function BlackBerry() {
      return navigator.userAgent.match(/BlackBerry/i);
    }
    function iOS() {
      return navigator.userAgent.match(/iPhone|iPad|iPod/i);
    }
    function Opera() {
      return navigator.userAgent.match(/Opera Mini/i);
    }
    function Windows() {
      return navigator.userAgent.match(/IEMobile/i);
    }
    return Android() || BlackBerry() || iOS() || Opera() || Windows();
  },
  //
  emotionClicked: function (emotionText) {
    bd_kfe_utils.printLog('imageclicked:' + emotionText);
    var originalText = $("#bytedesk_input_textarea").val();
    $("#bytedesk_input_textarea").val(originalText + emotionText);
    $("#bytedesk_input-emoji-box").hide();
    bd_kfe_data.show_emoji = false;
  },
  // // 播放提示音
  playAudio: function () {
    // 判断是否禁止播放提示音，如果是，则直接返回
    if (bd_kfe_data.voiceMuted) {
      return
    }
    var audio = document.getElementById('audioPlay')
    // 浏览器支持 audio
    audio.play()
  },
  // 上传并发送文件
  uploadVideo: function (file) {
    // TODO: 先在界面显示文件，并显示上传loading
    // console.log('uploadVideo:', file.name)
    //
    var filename = bd_kfe_utils.timeUuid() + "_" + bd_kfe_data.username + "_" + file.name;
    var formdata = new FormData();
    formdata.append("file_name", filename);
    formdata.append("username", bd_kfe_data.username);
    formdata.append("file", file);
    formdata.append("client", bd_kfe_data.client);
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/visitor/api/upload/video",
      contentType: false,
      cache: false,
      processData: false,
      mimeTypes: "multipart/form-data",
      type: "post",
      data: formdata,
      success: function (response) {
        bd_kfe_utils.printLog('upload video response:' + JSON.stringify(response.data))
        var fileUrl = response.data;
        bd_kfe_stompapi.sendVideoMessage(fileUrl);
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
      }
    });
  },
  // 上传并发送文件
  uploadFile: function (file) {
    // TODO: 先在界面显示文件，并显示上传loading
    // console.log('uploadFile:', file.name)
    //
    var filename = bd_kfe_utils.timeUuid() + "_" + bd_kfe_data.username + "_" + file.name;
    var formdata = new FormData();
    formdata.append("file_name", filename);
    formdata.append("username", bd_kfe_data.username);
    formdata.append("file", file);
    formdata.append("client", bd_kfe_data.client);
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/visitor/api/upload/file",
      contentType: false,
      cache: false,
      processData: false,
      mimeTypes: "multipart/form-data",
      type: "post",
      data: formdata,
      success: function (response) {
        bd_kfe_utils.printLog('upload response:' + JSON.stringify(response.data))
        var fileUrl = response.data;
        if (file.type.startsWith("image")) {
          bd_kfe_stompapi.sendImageMessage(fileUrl);
        } else if (file.type.startsWith("audio")) {
          bd_kfe_stompapi.sendVoiceMessage(fileUrl);
        } else if (file.type.startsWith("video")) {
          bd_kfe_stompapi.sendVideoMessage(fileUrl);
        } else {
          bd_kfe_stompapi.sendFileMessage(fileUrl);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
      }
    });
  },
  // 上传并发送图片
  uploadImage: function (file) {
    // TODO: 先在界面显示图片，并显示上传loading
    // console.log('uploadImage:', file.name)
    // FIXME: 压缩图片, 图片无法正常显示
    bd_kfe_utils.compressImage(file, function (newFile) {
      //
      var filename = bd_kfe_utils.timeUuid() + "_" + bd_kfe_data.username + "_" + file.name;
      var formdata = new FormData();
      formdata.append("file_name", filename);
      formdata.append("username", bd_kfe_data.username);
      formdata.append("file", newFile);
      // formdata.append("file", file);
      formdata.append("client", bd_kfe_data.client);
      //
      $.ajax({
        url: bd_kfe_data.HTTP_HOST + "/visitor/api/upload/image",
        contentType: false,
        cache: false,
        processData: false,
        mimeTypes: "multipart/form-data",
        type: "post",
        data: formdata,
        success: function (response) {
          bd_kfe_utils.printLog('upload response:' + JSON.stringify(response.data))
          var imageUrl = response.data;
          bd_kfe_stompapi.sendImageMessage(imageUrl)
        },
        error: function (error) {
          bd_kfe_utils.printLog(error);
        }
      });
    })

  },
  // 监听输入框粘贴图片，支持qq、微信等截图
  imgReader: function (item) {
    var file = item.getAsFile();
    bd_kfe_utils.uploadImage(file)
  },
  compressImage(file, callback) {
    // 判断文件大小
    if (file.size > 1024 * 1024 * 1) {
      bd_kfe_utils.printLog('图片大于1M进行压缩')
      //
      bd_kfe_utils.canvasDataURL(file, function (blob) {
        //
        var newFile = new File([blob], file.name, {
          type: file.type
        })
        var isLt1M;
        if (file.size < newFile.size) {
          isLt1M = file.size
        } else {
          isLt1M = newFile.size
        }
        bd_kfe_utils.printLog('file.size:' + (file.size / 1024 / 1024))
        bd_kfe_utils.printLog('newFile.size: ' + (newFile.size / 1024 / 1024))
        // 
        if (file.size < newFile.size) {
          callback(file)
        }
        callback(newFile)
      })
    } else {
      bd_kfe_utils.printLog('图片小于1M直接上传')
      // 
      return callback(file)
    }
  },
  // 图片压缩方法
  canvasDataURL: function (file, callback) {
    console.log('canvasDataURL')
    //
    var reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = function (e) {
      const img = new Image()
      const quality = 0.3 // 图像质量
      const canvas = document.createElement('canvas')
      const drawer = canvas.getContext('2d')
      img.src = this.result
      img.onload = function () {
        canvas.width = img.width
        canvas.height = img.height
        drawer.drawImage(img, 0, 0, canvas.width, canvas.height)
        // convertBase64UrlToBlob(canvas.toDataURL(file.type, quality), callback);
        return bd_kfe_utils.convertBase64UrlToBlob(canvas.toDataURL("image/jpeg", quality), callback);
      }
    }
  },
  // 将以base64的图片url数据转换为Blob
  convertBase64UrlToBlob: function (urlData, callback) {
    console.log('convertBase64UrlToBlob')
    // 
    var bytes = window.atob(urlData.split(',')[1]);        //去掉url的头，并转换为byte  
    //处理异常,将ascii码小于0的转换为大于0  
    var ab = new ArrayBuffer(bytes.length);
    var ia = new Uint8Array(ab);
    for (var i = 0; i < bytes.length; i++) {
      ia[i] = bytes.charCodeAt(i);
    }
    var blob = new Blob([ab], { type: 'image/jpeg' });
    callback(blob)
  },
  escapeHTML: function (content) {
    return content.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  },
  /**
   * 这里因为浏览器会把Ajax返回的二进制数据当做文本数据，
   * 所以写个str2bytes方法把接收到的文本数据按字节一个个做与运算来还原成二进制byte
   */
  str2bytes: function (str) {
    var bytes = [];
    for (var i = 0, len = str.length; i < len; ++i) {
      var c = str.charCodeAt(i);
      var byte = c & 0xff;
      bytes.push(byte);
    }
    return bytes;
  },
  handleQuickbuttonClick: function (type, title, content) {
    // bd_kfe_utils.printLog(type, title, content);
    if (type === 'url') {
      window.open(content)
    } else {
      // type === 'question'
      // type === 'event'
      var localId = bd_kfe_utils.guid();
      var message = {
        mid: localId,
        type: 'text',
        content: title,
        createdAt: bd_kfe_utils.currentTimestamp(),
        localId: localId,
        status: 'stored',
        user: {
          uid: bd_kfe_data.my_uid(),
          username: bd_kfe_data.my_username(),
          nickname: bd_kfe_data.my_nickname(),
          avatar: bd_kfe_data.my_avatar()
        }
      };
      bd_kfe_utils.pushToMessageArray(message);
      //
      var localId2 = bd_kfe_utils.guid();
      var message2 = {
        mid: localId2,
        type: 'text',
        content: content,
        createdAt: bd_kfe_utils.currentTimestamp(),
        localId: localId,
        status: 'stored',
        user: {
          uid: '',
          username: '',
          nickname: bd_kfe_data.agentNickname,
          avatar: bd_kfe_data.agentAvatar
        }
      };
      bd_kfe_utils.pushToMessageArray(message2);
    }
  },
  // 
  handleFaqPreviewItemClicked(question, answer) {
    $('#bytedesk_faq_preview').hide()
    //
    var localId = bd_kfe_utils.guid();
    var message = {
      mid: localId,
      type: 'text',
      content: decodeURIComponent(question),
      createdAt: bd_kfe_utils.currentTimestamp(),
      localId: localId,
      status: 'stored',
      user: {
        uid: bd_kfe_data.my_uid(),
        username: bd_kfe_data.my_username(),
        nickname: bd_kfe_data.my_nickname(),
        avatar: bd_kfe_data.my_avatar()
      }
    };
    bd_kfe_utils.pushToMessageArray(message);
    //
    var localId2 = bd_kfe_utils.guid();
    var message2 = {
      mid: localId2,
      type: 'text',
      content: decodeURIComponent(answer),
      createdAt: bd_kfe_utils.currentTimestamp(),
      localId: localId,
      status: 'stored',
      user: {
        uid: '',
        username: '',
        nickname: '系统',
        avatar: 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/admin_default_avatar.png'
      }
    };
    bd_kfe_utils.pushToMessageArray(message2);
  },
  // 请求浏览器-获取经纬度
  getLocation: function () {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(showPosition);
    }
    else {
      bd_kfe_utils.printLog("该浏览器不支持获取地理位置。");
    }
  },
  showPosition: function (position) {
    bd_kfe_utils.printLog('position: ' + JSON.stringify(position))
    bd_kfe_utils.printLog("纬度: " + position.coords.latitude + "经度: " + position.coords.longitude)
  },
  //
  showQrcode: function (h5Url) {
    if (document.getElementById('bytedesk_qrcode_h5')) {
      // 防止生成多张图片
      document.getElementById('bytedesk_qrcode_h5').innerHTML = ''
    }
    // console.log('bd_kfe_utils.qrcode()')
    new QRCode('bytedesk_qrcode_h5', {
      text: h5Url,
      width: 200,
      height: 200,
      colorDark: '#000000',
      colorLight: '#ffffff',
      correctLevel: QRCode.CorrectLevel.H
    })
  },
  // TODO: 下载聊天记录
  download: function (filename, text) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();
    document.body.removeChild(element);
  },
  // funDownload: function (content, filename) {
  //   // 创建隐藏的可下载链接
  //   var eleLink = document.createElement('a');
  //   eleLink.download = filename;
  //   eleLink.style.display = 'none';
  //   // 字符内容转变成blob地址
  //   var blob = new Blob([content]);
  //   eleLink.href = URL.createObjectURL(blob);
  //   // 触发点击
  //   document.body.appendChild(eleLink);
  //   eleLink.click();
  //   // 然后移除
  //   document.body.removeChild(eleLink);
  // },
  printLog: function (content) {
    if (!bd_kfe_data.IS_PRODUCTION) {
      console.log(content)
    }
  }
  // // 长按弹出上拉操作面板
  // onTouchStart: function (content, e) {
  //   console.log('onTouchStart:', content, e)
  //   bd_kfe_data.toucheX = e.targetTouches[0].screenX
  //   bd_kfe_data.toucheY = e.targetTouches[0].screenY
  //   // 开启定时器前先清除定时器，防止重复触发
  //   bd_kfe_data.timeOutEvent && clearTimeout(bd_kfe_data.timeOutEvent)
  //   // 显示上拉面板
  //   bd_kfe_data.timeOutEvent = setTimeout(() => {
  //     // 复制内容
  //     bd_kfe_utils.copyToClip(content)
  //   }, 500)
  //   e.preventDefault() // 阻止系统默认事件
  // },
  // onTouchMove: function (e) {
  //   console.log('onTouchMove')
  //   const moveX = e.targetTouches[0].screenX
  //   const moveY = e.targetTouches[0].screenY
  //   // 解决vivo机型，手指没有move，touchmove事件仍然会调用而导致setTimeout被clear
  //   if (bd_kfe_data.toucheX !== moveX || bd_kfe_data.toucheY !== moveY) {
  //     // 手指滑动，清除定时器，中断长按逻辑
  //     bd_kfe_data.timeOutEvent && clearTimeout(bd_kfe_data.timeOutEvent)
  //   }
  // },
  // onTouchEnd: function () {
  //   console.log('onTouchEnd')
  //   // 清除定时器，结束长按逻辑
  //   bd_kfe_data.timeOutEvent && clearTimeout(bd_kfe_data.timeOutEvent)
  //   // 若手指离开屏幕，时间小于我们设置的长按时间，则为点击事件
  // },
  // copyToClip: function (content) {
  //   //
  //   content = content.replaceAll("amp;", "");
  //   //
  //   var aux = document.createElement("input");
  //   aux.setAttribute("value", content);
  //   document.body.appendChild(aux);
  //   aux.select();
  //   document.execCommand("copy");
  //   document.body.removeChild(aux);
  //   //
  //   alert('复制成功');
  // },
  // processEmotion: function (body) {
  //   var emotionMap = {
  //     "[微笑]": "appkefu_f001.png",
  //     "[撇嘴]": "appkefu_f002.png",
  //     "[色]": "appkefu_f003.png",
  //     "[发呆]": "appkefu_f004.png",
  //     "[得意]": "appkefu_f005.png",
  //     "[流泪]": "appkefu_f006.png",
  //     "[害羞]": "appkefu_f007.png",
  //     "[闭嘴]": "appkefu_f008.png",
  //     "[睡]": "appkefu_f009.png",
  //     "[大哭]": "appkefu_f010.png",

  //     "[尴尬]": "appkefu_f011.png",
  //     "[发怒]": "appkefu_f012.png",
  //     "[调皮]": "appkefu_f013.png",
  //     "[呲牙]": "appkefu_f014.png",
  //     "[惊讶]": "appkefu_f015.png",
  //     "[难过]": "appkefu_f016.png",
  //     "[酷]": "appkefu_f017.png",
  //     "[冷汗]": "appkefu_f018.png",
  //     "[抓狂]": "appkefu_f019.png",
  //     "[吐]": "appkefu_f020.png",

  //     "[偷笑]": "appkefu_f021.png",
  //     "[愉快]": "appkefu_f022.png",
  //     "[白眼]": "appkefu_f023.png",
  //     "[傲慢]": "appkefu_f024.png",
  //     "[饥饿]": "appkefu_f025.png",
  //     "[困]": "appkefu_f026.png",
  //     "[惊恐]": "appkefu_f027.png",
  //     "[流汗]": "appkefu_f028.png",
  //     "[憨笑]": "appkefu_f029.png",
  //     "[悠闲]": "appkefu_f030.png",

  //     "[奋斗]": "appkefu_f031.png",
  //     "[咒骂]": "appkefu_f032.png",
  //     "[疑问]": "appkefu_f033.png",
  //     "[嘘]": "appkefu_f034.png",
  //     "[晕]": "appkefu_f035.png",
  //     "[疯了]": "appkefu_f036.png",
  //     "[衰]": "appkefu_f037.png",
  //     "[骷髅]": "appkefu_f038.png",
  //     "[敲打]": "appkefu_f039.png",
  //     "[再见]": "appkefu_f040.png",

  //     "[擦汗]": "appkefu_f041.png",
  //     "[抠鼻]": "appkefu_f042.png",
  //     "[鼓掌]": "appkefu_f043.png",
  //     "[糗大了]": "appkefu_f044.png",
  //     "[坏笑]": "appkefu_f045.png",
  //     "[左哼哼]": "appkefu_f046.png",
  //     "[右哼哼]": "appkefu_f047.png",
  //     "[哈欠]": "appkefu_f048.png",
  //     "[鄙视]": "appkefu_f049.png",
  //     "[委屈]": "appkefu_f050.png",

  //     ///////////////////////////////
  //     "[快哭]": "appkefu_f051.png",
  //     "[阴险]": "appkefu_f052.png",
  //     "[亲亲]": "appkefu_f053.png",
  //     "[吓]": "appkefu_f054.png",
  //     "[可怜]": "appkefu_f055.png",
  //     "[菜刀]": "appkefu_f056.png",
  //     "[西瓜]": "appkefu_f057.png",
  //     "[啤酒]": "appkefu_f058.png",
  //     "[篮球]": "appkefu_f059.png",
  //     "[乒乓]": "appkefu_f050.png",

  //     "[咖啡]": "appkefu_f061.png",
  //     "[饭]": "appkefu_f062.png",
  //     "[猪头]": "appkefu_f063.png",
  //     "[玫瑰]": "appkefu_f064.png",
  //     "[凋谢]": "appkefu_f065.png",
  //     "[嘴唇]": "appkefu_f066.png",
  //     "[爱心]": "appkefu_f067.png",
  //     "[心碎]": "appkefu_f068.png",
  //     "[蛋糕]": "appkefu_f069.png",
  //     "[闪电]": "appkefu_f070.png",

  //     "[炸弹]": "appkefu_f071.png",
  //     "[刀]": "appkefu_f072.png",
  //     "[足球]": "appkefu_f073.png",
  //     "[瓢虫]": "appkefu_f074.png",
  //     "[便便]": "appkefu_f075.png",
  //     "[月亮]": "appkefu_f076.png",
  //     "[太阳]": "appkefu_f077.png",
  //     "[礼物]": "appkefu_f078.png",
  //     "[拥抱]": "appkefu_f079.png",
  //     "[强]": "appkefu_f080.png",

  //     "[弱]": "appkefu_f081.png",
  //     "[握手]": "appkefu_f082.png",
  //     "[胜利]": "appkefu_f083.png",
  //     "[抱拳]": "appkefu_f084.png",
  //     "[勾引]": "appkefu_f085.png",
  //     "[拳头]": "appkefu_f086.png",
  //     "[差劲]": "appkefu_f087.png",
  //     "[爱你]": "appkefu_f088.png",
  //     "[No]": "appkefu_f089.png",
  //     "[OK]": "appkefu_f080.png",

  //     "[爱情]": "appkefu_f091.png",
  //     "[飞吻]": "appkefu_f092.png",
  //     "[跳跳]": "appkefu_f093.png",
  //     "[发抖]": "appkefu_f094.png",
  //     "[怄火]": "appkefu_f095.png",
  //     "[转圈]": "appkefu_f096.png",
  //     "[磕头]": "appkefu_f097.png",
  //     "[回头]": "appkefu_f098.png",
  //     "[跳绳]": "appkefu_f099.png",
  //     "[投降]": "appkefu_f100.png",

  //     "[激动]": "appkefu_f101.png",
  //     "[乱舞]": "appkefu_f102.png",
  //     "[献吻]": "appkefu_f103.png",
  //     "[左太极]": "appkefu_f104.png",
  //     "[右太极]": "appkefu_f105.png"
  //   };
  //   var reg = /\[[\u4E00-\u9FA5NoOK]+\]/g;
  //   var matchresult = body.match(reg);
  //   var result = body;
  //   if (matchresult) {
  //     for (var i = 0; i < matchresult.length; i++) {
  //       result = result.replace(
  //         matchresult[i],
  //         "<img height='25px' width='25px' src = 'https://cdn.bytedesk.com/assets/img/emo/" +
  //         emotionMap[matchresult[i]] +
  //         "'>"
  //       );
  //     }
  //   }
  //   return result;
  // },
};
