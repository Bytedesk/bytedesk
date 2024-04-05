/**
 * bytedesk.com
 */
/**
 * @apiDefine Message 消息
 *
 * 发送消息相关
 */
var bd_kfe_stompapi = {
  /**
   * 必须添加前缀 '/topic/'
   * @param topic
   */
  subscribeTopic: function (topic) {
    bd_kfe_utils.printLog("subscribeTopic:" + topic)
    // TODO: 3. 显示送达、已读状态
    // 防止重复订阅
    if (bd_kfe_data.subscribedTopics.indexOf(topic) !== -1) {
      return;
    }
    bd_kfe_data.subscribedTopics.push(topic);
    //
    bd_kfe_data.stompClient.subscribe("/topic/" + topic, function (message) {
      // bd_kfe_utils.printLog('message :', message, 'body:', message.body);
      var messageObject = JSON.parse(message.body);
      //
      if ((messageObject.type === "text"
        || messageObject.type === 'robot'
        || messageObject.type === 'robot_result'
        || messageObject.type === 'image'
        || messageObject.type === 'file'
        || messageObject.type === 'voice'
        || messageObject.type === 'video'
        || messageObject.type === 'commodity'
        || messageObject.type === 'card'
        || messageObject.type === 'choice')
        // && messageObject.user.uid !== bd_kfe_data.uid // 区分非当前用户发送的消息
      ) {
        // 新protobuf转换json
        messageObject.createdAt = messageObject.timestamp;
        if (messageObject.type === "text") {
          messageObject.content = messageObject.text.content;
        } else if (messageObject.type === "robot") {
          messageObject.content = messageObject.text.content;
        } else if (messageObject.type === "robot_result") {
          messageObject.content = messageObject.text.content;
        } else if (messageObject.type === "image") {
          messageObject.imageUrl = messageObject.image.imageUrl;
        } else if (messageObject.type === "file") {
          messageObject.fileUrl = messageObject.file.fileUrl;
        } else if (messageObject.type === "voice") {
          messageObject.voiceUrl = messageObject.voice.voiceUrl;
          messageObject.length = messageObject.voice.length;
        } else if (messageObject.type === "video") {
          messageObject.videoOrShortUrl = messageObject.video.videoOrShortUrl;
        } else if (messageObject.type === "commodity") {
          messageObject.content = messageObject.text.content;
        } else if (messageObject.type === "card") {
          messageObject.content = messageObject.text.content;
        } else if (messageObject.type === "choice") {
          messageObject.content = messageObject.text.content;
        }
        //
        var mid = messageObject.mid;
        // 发送消息回执：消息送达、消息已读
        // bd_kfe_stompapi.sendReceiptMessage(mid, "received");
        bd_kfe_stompapi.sendReceiptMessage(mid, "read");
        //
        if (bd_kfe_data.browserTabHidden) {
          var langText = "收到新客服消息"
          if (bd_kfe_data.lang === "en") {
            langText = "Received New Message"
          }
          document.title = langText;
        }
        // 播放提示语
        if (messageObject.user.uid !== bd_kfe_data.uid) {
          bd_kfe_utils.playAudio();
          // 设置左上角头像为客服头像 和 昵称
          // TODO: 优化协议，避免每次收到消息都设置
          $('#bytedesk_agent_avatar').attr('src', messageObject.user.avatar);
          $('#bytedesk_agent_nickname').text(messageObject.user.nickname);
          $('#bytedesk_agent_description').text(messageObject.user.description);
          // 通知custom.js
          window.parent.postMessage({ msg: 'bytedesk-message', type: messageObject.type, content: messageObject.content }, '*')
        }
      } else if (messageObject.type === "notification_browse_invite") {
        bd_kfe_data.browseInviteBIid = messageObject.browseInvite.bIid;
        // 客服邀请您参加会话
        // bd_kfe_httpapi.acceptInviteBrowse();
        // bd_kfe_httpapi.rejectInviteBrowse();
      } else if (messageObject.type === "notification_queue") {
        // 排队
        // 1. 保存thread
        // bd_kfe_data.thread = messageObject.thread;
        // 2. 订阅会话消息
        // bd_kfe_stompapi.subscribeTopic(bd_kfe_data.threadTopic());
        // 防止会话超时自动关闭，重新标记本地打开会话
        bd_kfe_data.isThreadClosed = false;
      } else if (messageObject.type === "notification_queue_accept") {
        // 接入访客
        messageObject.createdAt = messageObject.timestamp;
        var langText = "接入会话"
        if (bd_kfe_data.lang === "en") {
          langText = "Accept By Agent"
        }
        messageObject.content = langText; //messageObject.text.content;
        // // 1. 保存thread
        // bd_kfe_data.thread = messageObject.thread;
        // // 2. 订阅会话消息
        // bd_kfe_stompapi.subscribeTopic(bd_kfe_data.threadTopic());
        // // 防止会话超时自动关闭，重新标记本地打开会话
        bd_kfe_data.isThreadClosed = false;
        bd_kfe_data.isRobot = false;
      } else if (messageObject.type === "notification_invite_rate") {
        // 邀请评价
        messageObject.createdAt = messageObject.timestamp;
        messageObject.content = messageObject.extra.content;
        bd_kfe_data.isInviteRate = true;
        $("#bytedesk_main").hide();
        $("#bytedesk_leave").hide();
        $("#bytedesk_rate").show();
      } else if (messageObject.type === 'notification_rate_result') {
        // 访客评价结果
        messageObject.createdAt = messageObject.timestamp;
        messageObject.content = messageObject.extra.content;
      } else if (
        messageObject.type === "notification_agent_close" ||
        messageObject.type === "notification_auto_close"
      ) {
        // 新protobuf转换json
        messageObject.createdAt = messageObject.timestamp;
        messageObject.content = messageObject.text.content;
        // TODO: 会话关闭，添加按钮方便用户点击重新请求会话
        bd_kfe_data.isThreadClosed = true;
      } else if (messageObject.type === "notification_preview") {
        // 监听客服端输入状态
        if (messageObject.user.uid !== bd_kfe_data.uid) {
          bd_kfe_data.inputTipVisible = true;
          bd_kfe_utils.toggleInputTip(true);
          setTimeout(function () {
            bd_kfe_data.inputTipVisible = false;
            bd_kfe_utils.toggleInputTip(false);
          }, 5000);
        }
      } else if (messageObject.type === 'notification_receipt') {
        // {"mid":"45f9266c-c29d-a6e6-1c3a-f03677689993","type":"notification_receipt","user":{"uid":"201808221551193"},"status":"read"}
        // 消息状态：送达 received、已读 read
        if (messageObject.user.uid !== bd_kfe_data.uid) {
          // bd_kfe_utils.printLog('消息状态：送达、已读');
          var langText = "已读"
          if (bd_kfe_data.lang === "en") {
            langText = "read"
          }
          if ($("#status-" + messageObject.receipt.mid).text() == langText) {
            return;
          }
          if (messageObject.receipt.status == 'received') {
            var langText = "送达"
            if (bd_kfe_data.lang === "en") {
              langText = "received"
            }
            $("#status-" + messageObject.receipt.mid).text(langText);
          } else if (messageObject.receipt.status == 'read') {
            var langText = "已读"
            if (bd_kfe_data.lang === "en") {
              langText = "read"
            }
            $("#status-" + messageObject.receipt.mid).text(langText);
          }
        }
      } else if (messageObject.type === 'notification_recall') {
        // {"type":"notification_recall","mid":"ad58c6c9-acfd-23cf-52da-a2a0d2a30907","user":{"uid":"201808221551193"}}
        // TODO: 隐藏本条消息
        // bd_kfe_utils.printLog('消息撤回:', messageObject.recall.mid);
        var langText = "消息已撤回"
        if (bd_kfe_data.lang === "en") {
          langText = "message recalled"
        }
        $("#content-" + messageObject.recall.mid).text(langText);
        // $("#other" + messageObject.mid).hide();
      } else if (messageObject.type === 'notification_form_request') {
        // bd_kfe_utils.printLog('表单消息')
        let formContent = messageObject.extra.content
        bd_kfe_utils.printLog('form:' + formContent)
        if (formContent.indexOf('姓名') !== -1) {
          $("#bytedesk_form-name-div").show();
        }
        if (formContent.indexOf('手机') !== -1) {
          $("#bytedesk_form-mobile-div").show();
        }
        if (formContent.indexOf('邮箱') !== -1) {
          $("#bytedesk_form-email-div").show();
        }
        if (formContent.indexOf('年龄') !== -1) {
          $("#bytedesk_form-age-div").show();
        }
        if (formContent.indexOf('职业') !== -1) {
          $("#bytedesk_form-job-div").show();
        }
        bd_kfe_utils.switchForm()
      } else if (messageObject.type === 'notification_form_result') {
        // bd_kfe_utils.printLog('表单结果')
        messageObject.createdAt = messageObject.timestamp;
        messageObject.content = messageObject.form.content;
      }
      //
      if (
        messageObject.type !== "notification_preview" &&
        messageObject.type !== "notification_receipt" &&
        messageObject.type !== "notification_recall" &&
        // messageObject.type !== "notification_form_request" &&
        // messageObject.type !== "notification_form_result" &&
        messageObject.type !== "notification_connect" &&
        messageObject.type !== "notification_disconnect"
      ) {
        bd_kfe_data.isRobot = false;
        bd_kfe_utils.pushToMessageArray(messageObject);
        bd_kfe_utils.scrollToBottom();
        // 如果当前对话窗口非浏览器活跃tab, 则弹窗通知提示，更改窗口标题
        if (bd_kfe_data.hidden && !bd_kfe_utils.is_type_close(messageObject)) {
          var langText = "新客服消息"
          if (bd_kfe_data.lang === "en") {
            langText = "New Message"
          }
          bd_kfe_utils.showNotification(langText);
          bd_kfe_utils.browserTitleNotification();
        }
      } else {

      }
      // and acknowledge it
      // FIXME: PRECONDITION_FAILED - unknown delivery tag 8
      // message.ack()
      // }, {ack: 'client'});
    });
  },
  // /**
  //  * 订阅一对一会话, 这里用来监听消息发送回执
  //  * 必须携带前缀 '/user/'
  //  */
  // subscribeQueue: function () {
  //   bd_kfe_data.stompClient.subscribe("/user/queue/ack", function (message) {
  //     // TODO: mid对应的消息成功发送到服务器, 也即发送成功, body: {"mid":"dc5bbafd-af4f-861b-3f6a-cdca4e82d31a"}
  //     bd_kfe_utils.printLog("ack body:", message.body);
  //   });
  // },
  // 输入框变化
  onInputChange: function () {
    // 机器人对话的时候，无需发送输入状态
    if (bd_kfe_data.isRobot) {
      // TODO: 显示输入联想
      var content = $.trim($("#bytedesk_input_textarea").val());
      bd_kfe_httpapi.previewAnswer(content)
      return
    }
    if (bd_kfe_data.isThreadClosed) {
      return;
    }
    bd_kfe_stompapi.delaySendPreviewMessage()
  },
  // 发送预知消息
  sendPreviewMessage: function () {
    var localId = bd_kfe_utils.guid();
    var content = $.trim($("#bytedesk_input_textarea").val());
    var json = {
      "mid": localId,
      "timestamp": bd_kfe_utils.currentTimestamp(),
      "client": bd_kfe_data.client,
      "version": "1",
      "type": "notification_preview",
      "user": {
        "uid": bd_kfe_data.my_uid(),
        "username": bd_kfe_data.my_username(),
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "extra": {
          "agent": false
        }
      },
      "preview": {
        "content": content === undefined ? " " : content
      },
      "thread": {
        "tid": bd_kfe_data.thread.tid,
        "type": bd_kfe_data.thread.type,
        "content": content,
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "topic": bd_kfe_data.threadTopic(),
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "unreadCount": 0
      }
    };
    bd_kfe_stompapi.doSendMessage(json)
  },
  sendTextMessage: function () {
    // 获取输入框内值
    var content = $.trim($("#bytedesk_input_textarea").val());
    content = bd_kfe_utils.escapeHTML(content);
    if (content.length === 0) {
      var langText = "消息不能为空"
      if (bd_kfe_data.lang === "en") {
        langText = "Message Should Not be Null"
      }
      alert(langText);
      return;
    }
    if (content.length >= 500) {
      var langText = "消息长度太长，请分多次发送"
      if (bd_kfe_data.lang === "en") {
        langText = "Message Too Long"
      }
      alert(langText);
      return;
    }
    // bd_kfe_utils.printLog('send text:', content);
    if (bd_kfe_data.isRobot) {
      //
      bd_kfe_httpapi.messageAnswer(content);
    } else {
      //
      bd_kfe_stompapi.sendTextMessageSync(content)
    }
    // 清空输入框
    $("#bytedesk_input_textarea").val("");
  },
  sendTextMessageSync(content) {
    // if (bd_kfe_data.isThreadClosed) {
    //   alert("会话已经结束");
    //   return;
    // }
    // TODO: 1. 本地显示发送状态，
    // TODO: 2. 待发送成功之后，隐藏状态
    var localId = bd_kfe_utils.guid();
    var message = {
      mid: localId,
      type: bd_kfe_data.MESSAGE_TYPE_TEXT,
      content: content,
      imageUrl: content,
      createdAt: bd_kfe_utils.currentTimestamp(),
      localId: localId,
      status: bd_kfe_data.MESSAGE_STATUS_SENDING,
      user: {
        uid: bd_kfe_data.my_uid(),
        username: bd_kfe_data.my_username(),
        nickname: bd_kfe_data.my_nickname(),
        avatar: bd_kfe_data.my_avatar()
      }
    };
    bd_kfe_utils.pushToMessageArray(message);
    // 发送/广播会话消息
    var json = {
      "mid": localId,
      "timestamp": bd_kfe_utils.currentTimestamp(),
      "client": bd_kfe_data.client,
      "version": "1",
      "type": bd_kfe_data.MESSAGE_TYPE_TEXT,
      "user": {
        "uid": bd_kfe_data.my_uid(),
        "username": bd_kfe_data.my_username(),
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "extra": {
          "agent": false
        }
      },
      "text": {
        "content": content
      },
      "thread": {
        "tid": bd_kfe_data.thread.tid,
        "type": bd_kfe_data.thread.type,
        "content": content,
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "topic": bd_kfe_data.threadTopic(),
        "client": bd_kfe_data.client,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "unreadCount": 0
      }
    };
    bd_kfe_stompapi.doSendMessage(json)
  },
  sendImageMessage: function (imageUrl) {
    //
    if (bd_kfe_data.isRobot) {
      var langText = "自助服务暂不支持图片"
      if (bd_kfe_data.lang === "en") {
        langText = "Robot Not Allow Image"
      }
      alert(langText);
      return;
    }
    //
    // if (bd_kfe_data.isThreadClosed) {
    //   alert("会话已经结束");
    //   return;
    // }
    var localId = bd_kfe_utils.guid();
    var message = {
      mid: localId,
      type: 'image',
      content: imageUrl,
      imageUrl: imageUrl,
      createdAt: bd_kfe_utils.currentTimestamp(),
      localId: localId,
      status: 'sending',
      user: {
        uid: bd_kfe_data.my_uid(),
        username: bd_kfe_data.my_username(),
        nickname: bd_kfe_data.my_nickname(),
        avatar: bd_kfe_data.my_avatar()
      }
    };
    bd_kfe_utils.pushToMessageArray(message);
    // 发送/广播会话消息
    var json = {
      "mid": localId,
      "timestamp": bd_kfe_utils.currentTimestamp(),
      "client": bd_kfe_data.client,
      "version": "1",
      "type": "image",
      "user": {
        "uid": bd_kfe_data.my_uid(),
        "username": bd_kfe_data.my_username(),
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "extra": {
          "agent": false
        }
      },
      "image": {
        "imageUrl": imageUrl
      },
      "thread": {
        "tid": bd_kfe_data.thread.tid,
        "type": bd_kfe_data.thread.type,
        "content": "[图片]",
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "topic": bd_kfe_data.threadTopic(),
        "client": bd_kfe_data.client,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "unreadCount": 0
      }
    };
    bd_kfe_stompapi.doSendMessage(json)
    // bd_kfe_data.stompClient.send("/app/" + bd_kfe_data.threadTopic(), {},
    //   JSON.stringify(json)
    // );
  },
  sendVoiceMessage: function (voiceUrl) {
    //
    if (bd_kfe_data.isRobot) {
      var langText = "自助服务暂不支持语音"
      if (bd_kfe_data.lang === "en") {
        langText = "Robot Not Allow Voice"
      }
      alert(langText);
      return;
    }
    //
    // if (bd_kfe_data.isThreadClosed) {
    //   alert("会话已经结束");
    //   return;
    // }
    var localId = bd_kfe_utils.guid();
    var message = {
      mid: localId,
      type: 'voice',
      content: voiceUrl,
      voiceUrl: voiceUrl,
      createdAt: bd_kfe_utils.currentTimestamp(),
      localId: localId,
      status: 'sending',
      user: {
        uid: bd_kfe_data.my_uid(),
        username: bd_kfe_data.my_username(),
        nickname: bd_kfe_data.my_nickname(),
        avatar: bd_kfe_data.my_avatar()
      }
    };
    bd_kfe_utils.pushToMessageArray(message);
    // 发送/广播会话消息
    var json = {
      "mid": localId,
      "timestamp": bd_kfe_utils.currentTimestamp(),
      "client": bd_kfe_data.client,
      "version": "1",
      "type": "voice",
      "user": {
        "uid": bd_kfe_data.my_uid(),
        "username": bd_kfe_data.my_username(),
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "extra": {
          "agent": false
        }
      },
      "voice": {
        "voiceUrl": voiceUrl
      },
      "thread": {
        "tid": bd_kfe_data.thread.tid,
        "type": bd_kfe_data.thread.type,
        "content": "[语音]",
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "topic": bd_kfe_data.threadTopic(),
        "client": bd_kfe_data.client,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "unreadCount": 0
      }
    };
    bd_kfe_stompapi.doSendMessage(json)
    // bd_kfe_data.stompClient.send("/app/" + bd_kfe_data.threadTopic(), {},
    //   JSON.stringify(json)
    // );
  },
  sendVideoMessage: function (videoUrl) {
    //
    if (bd_kfe_data.isRobot) {
      var langText = "自助服务暂不支持视频"
      if (bd_kfe_data.lang === "en") {
        langText = "Robot Not Allow Video"
      }
      alert(langText);
      return;
    }
    //
    // if (bd_kfe_data.isThreadClosed) {
    //   alert("会话已经结束");
    //   return;
    // }
    var localId = bd_kfe_utils.guid();
    var message = {
      mid: localId,
      type: 'video',
      content: videoUrl,
      videoUrl: videoUrl,
      createdAt: bd_kfe_utils.currentTimestamp(),
      localId: localId,
      status: 'sending',
      user: {
        uid: bd_kfe_data.my_uid(),
        username: bd_kfe_data.my_username(),
        nickname: bd_kfe_data.my_nickname(),
        avatar: bd_kfe_data.my_avatar()
      }
    };
    bd_kfe_utils.pushToMessageArray(message);
    // 发送/广播会话消息
    var json = {
      "mid": localId,
      "timestamp": bd_kfe_utils.currentTimestamp(),
      "client": bd_kfe_data.client,
      "version": "1",
      "type": "video",
      "user": {
        "uid": bd_kfe_data.my_uid(),
        "username": bd_kfe_data.my_username(),
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "extra": {
          "agent": false
        }
      },
      "video": {
        "videoOrShortUrl": videoUrl
      },
      "thread": {
        "tid": bd_kfe_data.thread.tid,
        "type": bd_kfe_data.thread.type,
        "content": "[视频]",
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "topic": bd_kfe_data.threadTopic(),
        "client": bd_kfe_data.client,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "unreadCount": 0
      }
    };
    bd_kfe_stompapi.doSendMessage(json)
  },
  sendFileMessage: function (fileUrl) {
    //
    if (bd_kfe_data.isRobot) {
      var langText = "自助服务暂不支持文件"
      if (bd_kfe_data.lang === "en") {
        langText = "Robot Not Allow File"
      }
      alert(langText);
      return;
    }
    //
    // if (bd_kfe_data.isThreadClosed) {
    //   alert("会话已经结束");
    //   return;
    // }
    var localId = bd_kfe_utils.guid();
    var message = {
      mid: localId,
      type: 'file',
      content: fileUrl,
      fileUrl: fileUrl,
      createdAt: bd_kfe_utils.currentTimestamp(),
      localId: localId,
      status: 'sending',
      user: {
        uid: bd_kfe_data.my_uid(),
        username: bd_kfe_data.my_username(),
        nickname: bd_kfe_data.my_nickname(),
        avatar: bd_kfe_data.my_avatar()
      }
    };
    bd_kfe_utils.pushToMessageArray(message);
    // 发送/广播会话消息
    var json = {
      "mid": localId,
      "timestamp": bd_kfe_utils.currentTimestamp(),
      "client": bd_kfe_data.client,
      "version": "1",
      "type": "file",
      "user": {
        "uid": bd_kfe_data.my_uid(),
        "username": bd_kfe_data.my_username(),
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "extra": {
          "agent": false
        }
      },
      "file": {
        "fileUrl": fileUrl
      },
      "thread": {
        "tid": bd_kfe_data.thread.tid,
        "type": bd_kfe_data.thread.type,
        "content": "[文件]",
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "topic": bd_kfe_data.threadTopic(),
        "client": bd_kfe_data.client,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "unreadCount": 0
      }
    };
    bd_kfe_stompapi.doSendMessage(json)
    // bd_kfe_data.stompClient.send("/app/" + bd_kfe_data.threadTopic(), {},
    //   JSON.stringify(json)
    // );
  },
  /**
   * 消息回执：收到消息之后回复给消息发送方
   * 消息content字段存放status: 1. received, 2. read
   */
  sendReceiptMessage: function (mid, status) {
    var localId = bd_kfe_utils.guid();
    var json = {
      "mid": localId,
      "timestamp": bd_kfe_utils.currentTimestamp(),
      "client": bd_kfe_data.client,
      "version": "1",
      "type": "notification_receipt",
      "user": {
        "uid": bd_kfe_data.my_uid(),
        "username": bd_kfe_data.my_username(),
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "extra": {
          "agent": false
        }
      },
      "receipt": {
        "mid": mid,
        "status": status
      },
      "thread": {
        "tid": bd_kfe_data.thread.tid,
        "type": bd_kfe_data.thread.type,
        // "content": content,
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "topic": bd_kfe_data.threadTopic(),
        "client": bd_kfe_data.client,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "unreadCount": 0
      }
    };
    bd_kfe_stompapi.doSendMessage(json)
    // bd_kfe_data.stompClient.send(
    //   "/app/" + bd_kfe_data.threadTopic(), {},
    //   JSON.stringify(json)
    // );
    // 收到消息后，向服务器发送回执
  },
  /**
   * 消息撤回
   */
  sendRecallMessage: function (mid) {
    var localId = bd_kfe_utils.guid();
    var json = {
      "mid": localId,
      "timestamp": bd_kfe_utils.currentTimestamp(),
      "client": bd_kfe_data.client,
      "version": "1",
      "type": "notification_recall",
      "user": {
        "uid": bd_kfe_data.my_uid(),
        "username": bd_kfe_data.my_username(),
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "extra": {
          "agent": false
        }
      },
      "recall": {
        "mid": mid
      },
      "thread": {
        "tid": bd_kfe_data.thread.tid,
        "type": bd_kfe_data.thread.type,
        // "content": content,
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "topic": bd_kfe_data.threadTopic(),
        "client": bd_kfe_data.client,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "unreadCount": 0
      }
    };
    bd_kfe_stompapi.doSendMessage(json)
    // bd_kfe_data.stompClient.send(
    //   "/app/" + bd_kfe_data.threadTopic(), {},
    //   JSON.stringify(json)
    // );
    // 收到消息后，向服务器发送回执
  },
  /**
   * 提交表单
   */
  sendFormMessage: function () {
    //
    var name = $.trim($("#bytedesk_form-name").val());
    var mobile = $.trim($("#bytedesk_form-mobile").val());
    var email = $.trim($("#bytedesk_form-email").val());
    var age = $.trim($("#bytedesk_form-age").val());
    var job = $.trim($("#bytedesk_form-job").val());
    if (age.length > 0 && isNaN(age)) {
      var langText = "年龄必须为数字"
      if (bd_kfe_data.lang === "en") {
        langText = "Age should be Number"
      }
      alert(langText);
      return;
    }
    //
    let formContent = JSON.stringify({
      'form': {
        'realname': name,
        'mobile': mobile,
        'email': email,
        'age': age,
        'job': job,
      }
    })
    var localId = bd_kfe_utils.guid();
    var json = {
      "mid": localId,
      "timestamp": bd_kfe_utils.currentTimestamp(),
      "client": bd_kfe_data.client,
      "version": "1",
      "type": "notification_form_result",
      "user": {
        "uid": bd_kfe_data.my_uid(),
        "username": bd_kfe_data.my_username(),
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "extra": {
          "agent": false
        }
      },
      "form": {
        "content": formContent
      },
      "thread": {
        "tid": bd_kfe_data.thread.tid,
        "type": bd_kfe_data.thread.type,
        "content": "[表单]",
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "topic": bd_kfe_data.threadTopic(),
        "client": bd_kfe_data.client,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "unreadCount": 0
      }
    };
    bd_kfe_stompapi.doSendMessage(json)
    // bd_kfe_data.stompClient.send(
    //   "/app/" + bd_kfe_data.threadTopic(), {},
    //   JSON.stringify(json)
    // );
    //
    bd_kfe_utils.showMessage()
    $("#bytedesk_form-name-div").hide();
    $("#bytedesk_form-mobile-div").hide();
    $("#bytedesk_form-email-div").hide();
    $("#bytedesk_form-age-div").hide();
    $("#bytedesk_form-job-div").hide();
  },
  sendBrowseMessage: function () {
    // 会话未关闭，进行中，直接返回
    if (!bd_kfe_data.isThreadClosed) {
      return;
    }
    var content = {
      referrer: encodeURI(document.referrer),
      url: encodeURI(window.location.href),
      title: encodeURI(document.title)
    };
    // 发送/广播会话消息
    var json = {
      "mid": bd_kfe_utils.guid(),
      "timestamp": bd_kfe_utils.currentTimestamp(),
      "client": bd_kfe_data.client,
      "version": "1",
      "type": "notification_notice",
      "user": {
        "uid": bd_kfe_data.my_uid(),
        "username": bd_kfe_data.my_username(),
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "extra": {
          "agent": false
        }
      },
      "notice": {
        "topic": window.adminUid,
        "type": "notification_browse",
        "content": JSON.stringify(content)
      }
    };
    bd_kfe_data.stompClient.send("/app/" + window.adminUid, {},
      JSON.stringify(json)
    );
    //
    setTimeout(function () {
      bd_kfe_stompapi.sendBrowseMessage();
    }, 10000);
  },
  sendCommodityMessageSync: function () {
    let goods = bd_kfe_utils.getUrlParam("goods")
    if (goods !== "1") {
      return
    }
    //
    let jsonContent = bd_kfe_stompapi.commodityInfo();
    // 发送商品信息
    var json = {
      "mid": bd_kfe_utils.guid(),
      "timestamp": bd_kfe_utils.currentTimestamp(),
      "client": bd_kfe_data.client,
      "version": "1",
      "type": 'commodity',
      "status": "sending",
      "user": {
        "uid": bd_kfe_data.my_uid(),
        "username": bd_kfe_data.my_username(),
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "extra": {
          "agent": false
        }
      },
      "text": {
        "content": jsonContent
      },
      "thread": {
        "tid": bd_kfe_data.thread.tid,
        "type": bd_kfe_data.thread.type,
        "content": "[商品]",
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "topic": bd_kfe_data.threadTopic(),
        "client": bd_kfe_data.client,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "unreadCount": 0
      }
    };
    bd_kfe_stompapi.doSendMessage(json)
  },
  appendCommodityInfo: function () {
    let goods = bd_kfe_utils.getUrlParam("goods")
    if (goods !== "1") {
      return
    }
    let jsonContent = bd_kfe_stompapi.commodityInfo();
    // 发送商品信息
    var json = {
      "mid": bd_kfe_utils.guid(),
      // "timestamp": bd_kfe_utils.currentTimestamp(),
      "createdAt": bd_kfe_utils.currentTimestamp(),
      "client": bd_kfe_data.client,
      "version": "1",
      "type": 'commodity',
      "status": "sending",
      "user": {
        "uid": bd_kfe_data.my_uid(),
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "avatar": bd_kfe_data.my_avatar(),
        "extra": {
          "agent": false
        }
      },
      "content": jsonContent,
      "thread": {
        "tid": bd_kfe_data.thread.tid,
        "type": bd_kfe_data.thread.type,
        "content": "[商品]",
        "nickname": bd_kfe_data.my_nickname(),
        "avatar": bd_kfe_data.my_avatar(),
        "topic": bd_kfe_data.threadTopic(),
        "client": bd_kfe_data.client,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "unreadCount": 0
      }
    };
    bd_kfe_utils.pushToMessageArray(json)
  },
  commodityInfo: function () {
    //
    let commodidy = {
      "id": bd_kfe_utils.getUrlParam("goods_id"),
      "title": bd_kfe_utils.getUrlParam("goods_title"),
      "content": bd_kfe_utils.getUrlParam("goods_content"),
      "price": bd_kfe_utils.getUrlParam("goods_price"),
      "url": bd_kfe_utils.getUrlParam("goods_url"),
      "imageUrl": bd_kfe_utils.getUrlParam("goods_imageUrl"),
      "categoryCode": bd_kfe_utils.getUrlParam("goods_categoryCode"),
      "type": "commodity"
    }
    return JSON.stringify(commodidy)
  },
  // 重新发送
  sendMessageJsonRest: function (mid, type, content) {
    //
    var json;
    if (type === 'text') {
      json = {
        "mid": mid,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "client": bd_kfe_data.client,
        "version": "1",
        "type": type,
        "status": "sending",
        "user": {
          "uid": bd_kfe_data.my_uid(),
          "username": bd_kfe_data.my_username(),
          "nickname": bd_kfe_data.my_nickname(),
          "avatar": bd_kfe_data.my_avatar(),
          "extra": {
            "agent": false
          }
        },
        "text": {
          "content": content
        },
        "thread": {
          "tid": bd_kfe_data.thread.tid,
          "type": bd_kfe_data.thread.type,
          "content": content,
          "nickname": bd_kfe_data.my_nickname(),
          "avatar": bd_kfe_data.my_avatar(),
          "topic": bd_kfe_data.threadTopic(),
          "client": bd_kfe_data.client,
          "timestamp": bd_kfe_utils.currentTimestamp(),
          "unreadCount": 0
        }
      };
    } else if (type === 'image') {
      json = {
        "mid": mid,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "client": bd_kfe_data.client,
        "version": "1",
        "type": type,
        "status": "sending",
        "user": {
          "uid": bd_kfe_data.my_uid(),
          "username": bd_kfe_data.my_username(),
          "nickname": bd_kfe_data.my_nickname(),
          "avatar": bd_kfe_data.my_avatar(),
          "extra": {
            "agent": false
          }
        },
        "image": {
          "imageUrl": content
        },
        "thread": {
          "tid": bd_kfe_data.thread.tid,
          "type": bd_kfe_data.thread.type,
          "content": '图片',
          "nickname": bd_kfe_data.my_nickname(),
          "avatar": bd_kfe_data.my_avatar(),
          "topic": bd_kfe_data.threadTopic(),
          "client": bd_kfe_data.client,
          "timestamp": bd_kfe_utils.currentTimestamp(),
          "unreadCount": 0
        }
      };
    } else if (type === 'file') {
      json = {
        "mid": mid,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "client": bd_kfe_data.client,
        "version": "1",
        "type": type,
        "status": "sending",
        "user": {
          "uid": bd_kfe_data.my_uid(),
          "username": bd_kfe_data.my_username(),
          "nickname": bd_kfe_data.my_nickname(),
          "avatar": bd_kfe_data.my_avatar(),
          "extra": {
            "agent": false
          }
        },
        "file": {
          "fileUrl": content
        },
        "thread": {
          "tid": bd_kfe_data.thread.tid,
          "type": bd_kfe_data.thread.type,
          "content": '[文件]',
          "nickname": bd_kfe_data.my_nickname(),
          "avatar": bd_kfe_data.my_avatar(),
          "topic": bd_kfe_data.threadTopic(),
          "client": bd_kfe_data.client,
          "timestamp": bd_kfe_utils.currentTimestamp(),
          "unreadCount": 0
        }
      };
    } else if (type === 'voice') {
      json = {
        "mid": mid,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "client": bd_kfe_data.client,
        "version": "1",
        "type": type,
        "status": "sending",
        "user": {
          "uid": bd_kfe_data.my_uid(),
          "username": bd_kfe_data.my_username(),
          "nickname": bd_kfe_data.my_nickname(),
          "avatar": bd_kfe_data.my_avatar(),
          "extra": {
            "agent": false
          }
        },
        "voice": {
          "voiceUrl": content,
          "length": '0', // TODO:替换为真实值
          "format": 'wav',
        },
        "thread": {
          "tid": bd_kfe_data.thread.tid,
          "type": bd_kfe_data.thread.type,
          "content": '[语音]',
          "nickname": bd_kfe_data.my_nickname(),
          "avatar": bd_kfe_data.my_avatar(),
          "topic": bd_kfe_data.threadTopic(),
          "client": bd_kfe_data.client,
          "timestamp": bd_kfe_utils.currentTimestamp(),
          "unreadCount": 0
        }
      };
    } else if (type === 'video') {
      json = {
        "mid": mid,
        "timestamp": bd_kfe_utils.currentTimestamp(),
        "client": bd_kfe_data.client,
        "version": "1",
        "type": type,
        "status": "sending",
        "user": {
          "uid": bd_kfe_data.my_uid(),
          "username": bd_kfe_data.my_username(),
          "nickname": bd_kfe_data.my_nickname(),
          "avatar": bd_kfe_data.my_avatar(),
          "extra": {
            "agent": false
          }
        },
        "video": {
          "videoOrShortUrl": content
        },
        "thread": {
          "tid": bd_kfe_data.thread.tid,
          "type": bd_kfe_data.thread.type,
          "content": '[视频]',
          "nickname": bd_kfe_data.my_nickname(),
          "avatar": bd_kfe_data.my_avatar(),
          "topic": bd_kfe_data.threadTopic(),
          "client": bd_kfe_data.client,
          "timestamp": bd_kfe_utils.currentTimestamp(),
          "unreadCount": 0
        }
      };
    }
    bd_kfe_stompapi.sendMessageRest2(mid, JSON.stringify(json))
  },
  // 发送消息
  doSendMessage: function (jsonObject) {
    bd_kfe_utils.printLog('doSendMessage: ' + JSON.stringify(jsonObject))
    //
    if (bd_kfe_data.isConnected) {
      // 发送长连接消息
      bd_kfe_data.stompClient.send("/app/" + bd_kfe_data.threadTopic(), {},
        JSON.stringify(jsonObject)
      );
    } else {
      // 调用rest接口发送消息
      bd_kfe_stompapi.sendMessageRest(JSON.stringify(jsonObject))
    }
    // 先插入本地
    // this.onMessageReceived(jsonObject)
  },
  // 在长连接断开的情况下，发送消息
  sendMessageRest: function (json) {
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/api/messages/send",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      type: "post",
      data: JSON.stringify({ json: json, }),
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog(response)
        // 更新消息发送状态
        // bd_kfe_utils.printLog("send rest message: ", response.data);
        // let message = JSON.parse(response.data)
        // for (let i = app.messages.length - 1; i >= 0; i--) {
        //   const msg = app.messages[i]
        //   // bd_kfe_utils.printLog('mid:', msg.mid, message.mid)
        //   if (msg.mid === message.mid) {
        //     // 可更新顺序 read > received > stored > sending, 前面的状态可更新后面的
        //     if (app.messages[i].status === 'read' ||
        //       app.messages[i].status === 'received') {
        //       return
        //     }
        //     Vue.set(app.messages[i], 'status', 'stored')
        //     return;
        //   }
        // }
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
        // token过期
        app.login()
      }
    });
  },
  sendMessageRest2: function (mid, json) {
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/api/messages/send",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      type: "post",
      data: JSON.stringify({ json: json, }),
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog(mid + ':' + response)
        // 更新消息发送状态
        // for (let i = app.messages.length - 1; i >= 0; i--) {
        //   const msg = app.messages[i]
        //   // bd_kfe_utils.printLog('mid:', msg.mid, message.mid)
        //   if (msg.mid === mid) {
        //     // 可更新顺序 read > received > stored > sending, 前面的状态可更新后面的
        //     if (app.messages[i].status === 'read' ||
        //       app.messages[i].status === 'received') {
        //       return
        //     }
        //     Vue.set(app.messages[i], 'status', 'stored')
        //     return;
        //   }
        // }
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
        // token过期
        bd_kfe_httpapi.login()
      }
    });
  },
  /**
   * http://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#websocket-stomp-authentication
   */
  byteDeskConnect: function () {
    // bd_kfe_utils.printLog('start stomp connection');
    var socket = new SockJS(
      bd_kfe_data.STOMP_HOST +
      "/stomp/?access_token=" +
      bd_kfe_data.passport.token.access_token
    );
    bd_kfe_data.stompClient = Stomp.over(socket);
    bd_kfe_data.stompClient.reconnect_delay = 1000;
    // client will send heartbeats every 10000ms, default 10000
    bd_kfe_data.stompClient.heartbeat.outgoing = 20000;
    // client does not want to receive heartbeats from the server, default 10000
    bd_kfe_data.stompClient.heartbeat.incoming = 20000;
    // to disable logging, set it to an empty function:
    if (bd_kfe_data.IS_PRODUCTION) {
      bd_kfe_data.stompClient.debug = function (value) { }
    }
    // 连接bytedesk，如果后台开启了登录，需要登录之后才行
    bd_kfe_data.stompClient.connect({}, function (frame) {
      bd_kfe_utils.printLog('stompConnected: ' + frame + " username：" + frame.headers['user-name']);
      bd_kfe_data.isConnected = true;
      // bd_kfe_utils.updateConnection(true);
      // 订阅回执
      // bd_kfe_stompapi.subscribeQueue();
      // 获取 websocket 连接的 sessionId
      // socket._transport.url: ws://127.0.0.1:8000/stomp/817/3sycd2aj/websocket?access_token=xxx
      // bd_kfe_utils.printLog("socket._transport.url:" + socket._transport.url);
      // FIXME: Uncaught TypeError: Cannot read property '1' of null
      var paths = /\/([^\/]+)\/websocket/.exec(socket._transport.url)
      if (paths != null && paths.length > 1) {
        bd_kfe_data.sessionId = paths[1];
        // bd_kfe_utils.printLog("session id: " + bd_kfe_data.sessionId);
      }
      // 更新浏览记录
      // bd_kfe_httpapi.browse()
      // 订阅会话消息，处理断开重连的情况
      bd_kfe_stompapi.subscribeTopic(bd_kfe_data.uid);
      if (
        bd_kfe_data.thread.topic !== "" &&
        bd_kfe_data.thread.topic !== null &&
        bd_kfe_data.thread.topic !== undefined
      ) {
        bd_kfe_stompapi.subscribeTopic(bd_kfe_data.threadTopic());
      }
      // 发送附言
      if (bd_kfe_data.preload !== "1") {
        // 非预加载
        if (bd_kfe_data.postscript !== null && bd_kfe_data.postscript !== undefined && bd_kfe_data.postscript !== '') {
          var postcontent = bd_kfe_data.postScriptPrefix + bd_kfe_data.postscript
          bd_kfe_stompapi.sendTextMessageSync(postcontent)
        }
      }
      //
      // setTimeout(function () {
      //   bd_kfe_utils.printLog("sendBrowseMessage...");
      //   bd_kfe_stompapi.sendBrowseMessage();
      // }, 10000);
    },
      function (error) {
        bd_kfe_utils.printLog("连接断开【" + error + "】");
        bd_kfe_data.isConnected = false;
        // 为断开重连做准备
        bd_kfe_data.subscribedTopics = [];
        // 10秒后重新连接，实际效果：每10秒重连一次，直到连接成功
        setTimeout(function () {
          bd_kfe_utils.printLog("reconnecting...");
          bd_kfe_stompapi.byteDeskConnect();
        }, 5000);
      }
    );
  },
};
