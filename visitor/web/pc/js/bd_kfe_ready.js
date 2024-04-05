/**
 * client js
 * @version 1.2.0
 * @author www.kefux.com
 * @date 2018/11/12
 */
var bd_kfe_kefu = {
  //
  created: function () {
    // 嵌入窗口形式
    bd_kfe_data.adminUid = bd_kfe_utils.getUrlParam("uid");
    bd_kfe_data.workGroupWid = bd_kfe_utils.getUrlParam("wid");
    bd_kfe_data.subDomain = bd_kfe_utils.getUrlParam("sub");
    bd_kfe_data.type = bd_kfe_utils.getUrlParam("type");
    bd_kfe_data.agentUid = bd_kfe_utils.getUrlParam("aid");
    bd_kfe_data.closable = bd_kfe_utils.getUrlParam("closable");
    bd_kfe_data.preload = bd_kfe_utils.getUrlParam("preload");
    bd_kfe_data.column = bd_kfe_utils.getUrlParam("column");
    bd_kfe_data.history = bd_kfe_utils.getUrlParam("history");
    bd_kfe_data.postscript = bd_kfe_utils.getUrlParam("postscript");
    bd_kfe_data.lang = bd_kfe_utils.getUrlParam("lang");
    bd_kfe_data.color = bd_kfe_utils.getUrlParam("color");
    bd_kfe_data.background = bd_kfe_utils.getUrlParam("background");
    bd_kfe_data.nickname = bd_kfe_utils.getUrlParam("nickname") === null ? '' : bd_kfe_utils.getUrlParam("nickname");
    bd_kfe_data.avatar = bd_kfe_utils.getUrlParam("avatar") === null ? '' : bd_kfe_utils.getUrlParam("avatar");
    // bd_kfe_data.uni_wid = bd_kfe_data.type === 'appointed' ? bd_kfe_data.agentUid : bd_kfe_data.workGroupWid;
    bd_kfe_data.v2robot = bd_kfe_utils.getUrlParam("v2robot") === null ? "0" : bd_kfe_utils.getUrlParam("v2robot");
    // 加载静音记忆
    if (localStorage.voiceMuted === "true") {
      $('#bytedesk_sound').hide()
      $('#bytedesk_mute').show()
      bd_kfe_data.voiceMuted = true
    }
  },
  mounted: function () {
    // 是否自定义用户名
    bd_kfe_data.selfuser = bd_kfe_utils.getUrlParam("selfuser");
    if (bd_kfe_data.selfuser === "1") {
      // 之前未启用自定义用户名，初次启用自定义用户名
      if (localStorage.bd_kfe_selfuser !== "1") {
        // 初次启用自定义用户名
        localStorage.bd_kfe_selfuser = "1";
        // TODO: 调用自定义用户注册流程
        bd_kfe_httpapi.registerUser()
      } else {
        // 之前已经启用了自定义用户名
        // TODO: 判断用户名是否跟之前一样
        bd_kfe_data.passport.token.access_token = localStorage.bd_kfe_access_token
        // bd_kfe_utils.printLog('access_token:' + bd_kfe_data.passport.token.access_token)
        bd_kfe_data.uid = localStorage.bd_kfe_uid;
        bd_kfe_data.username = localStorage.bd_kfe_username;
        bd_kfe_data.nickname = localStorage.bd_kfe_nickname;
        bd_kfe_data.password = bd_kfe_data.username;
        // 
        var username = bd_kfe_utils.getUrlParam("username");
        if (bd_kfe_data.username === username) {
          // 同一个用户，直接调用登录流程
          if (bd_kfe_data.passport.token.access_token !== null
            && bd_kfe_data.passport.token.access_token !== undefined
            && bd_kfe_data.passport.token.access_token !== '') {
            // 本地存储有token，直接请求会话
            // token非空，之前已经登录过，在requestThread中判断token是否过期
            bd_kfe_httpapi.requestThread()
          } else if (
            bd_kfe_data.username !== null &&
            bd_kfe_data.username !== undefined &&
            bd_kfe_data.username !== ""
          ) {
            // 首次登录
            bd_kfe_httpapi.login();
          }
        } else {
          // 切换了用户名，重新调用注册流程
          bd_kfe_httpapi.registerUser();
        }
      }
    } else {
      // 未启用自定义用户名
      if (localStorage.bd_kfe_selfuser === "1") {
        // 之前使用自定义用户名
        // 标记使用非自定义用户名
        localStorage.bd_kfe_selfuser = "0"; // 注意：不能提取到if之前
        // 新注册匿名用户
        bd_kfe_httpapi.requestUsername();
      } else {
        // 一直使用非自定义用户名
        // 标记使用非自定义用户名
        localStorage.bd_kfe_selfuser = "0"; // 注意：不能提取到if之前
        // 匿名用户流程
        bd_kfe_data.passport.token.access_token = localStorage.bd_kfe_access_token
        // bd_kfe_utils.printLog('access_token:' + bd_kfe_data.passport.token.access_token)
        bd_kfe_data.uid = localStorage.bd_kfe_uid;
        bd_kfe_data.username = localStorage.bd_kfe_username;
        bd_kfe_data.nickname = localStorage.bd_kfe_nickname;
        bd_kfe_data.password = bd_kfe_data.username;
        // 
        if (bd_kfe_data.passport.token.access_token !== null
          && bd_kfe_data.passport.token.access_token !== undefined
          && bd_kfe_data.passport.token.access_token !== '') {
          // 本地存储有token，直接请求会话
          bd_kfe_httpapi.requestThread()
        } else if (
          bd_kfe_data.username !== null &&
          bd_kfe_data.username !== undefined &&
          bd_kfe_data.username !== ""
        ) {
          bd_kfe_httpapi.login();
        } else {
          bd_kfe_httpapi.requestUsername();
        }
      }
    }
    bd_kfe_httpapi.getBaseUrl();
  }
};

(function () {
  // ie ajax cross domain requests
  $.support.cors = true;
  // 使ie支持startsWith
  if (!String.prototype.startsWith) {
    String.prototype.startsWith = function (searchString, position) {
      position = position || 0;
      return this.indexOf(searchString, position) === position;
    };
  }
  // 使ie支持includes
  if (!String.prototype.includes) {
    String.prototype.includes = function (str) {
      var returnValue = false;
      if (this.indexOf(str) !== -1) {
        returnValue = true;
      }
      return returnValue;
    };
  }
  // 使ie支持endsWith
  if (!String.prototype.endsWith) {
    String.prototype.endsWith = function (suffix) {
      return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };
  }
  // 移动端，TODO: 跳转h5客服页面
  if (bd_kfe_utils.isMobile()) {
    //
  }
  // 初始化参数
  bd_kfe_kefu.created();
  //
  if (bd_kfe_data.color != null && bd_kfe_data.color.startsWith('#')) {
    $("#pageChat .header").css("color", bd_kfe_data.color);
  }
  // bd_kfe_utils.printLog('background:' + bd_kfe_data.background);
  if (bd_kfe_data.background != null && bd_kfe_data.background.startsWith('#')) {
    $("#pageChat .header").css("background-color", bd_kfe_data.background);
  }
  // 屏幕宽度小于340的时候，隐藏右侧栏
  // bd_kfe_utils.printLog("window width: " + $(window).width())
  // if ($(window).width() <= 450) {
  if (bd_kfe_data.closable === "1") {
    $("#bytedesk_close").css("display", "block")
    $("#bytedesk_max").css("display", "block")
    $("#pageChat").css("border-radius", "10px");
  }
  if (bd_kfe_data.column === "1") {
    $("#bytedesk_left").css("width", "100%");
    //   $("#bytedesk_main").css("padding-right", "0px");
    //   $("#bytedesk_right").hide();
  }
  // if (bd_kfe_data.column === "2") {
  //   bd_kfe_utils.showRightColumn()
  // }
  $('input[id=imagefile]').change(function (result) {
    bd_kfe_utils.printLog("upload:" + $(this).val(), $(this));
    bd_kfe_utils.uploadImage($('input[id=imagefile]')[0].files[0])
  });
  $('input[id=videofile]').change(function (result) {
    bd_kfe_utils.printLog("upload:" + $(this).val(), $(this));
    bd_kfe_utils.uploadVideo($('input[id=videofile]')[0].files[0])
  });
  $('input[id=filefile]').change(function (result) {
    bd_kfe_utils.printLog("upload file:" + $(this).val(), $(this));
    bd_kfe_utils.uploadFile($('input[id=filefile]')[0].files[0])
  });
  //
  $('#bytedesk_max').click(function () {
    window.parent.postMessage("bytedesk-max", '*');
  });
  $('#bytedesk_minus').click(function () {
    // document.getElementById("bytedesk_app-wrapper").style.display = 'none';
    // document.getElementById("bytedesk_start").style.display = '';
    window.parent.postMessage("bytedesk-minus", '*');
  });
  $('#bytedesk_close').click(function () {
    // document.getElementById("bytedesk_app-wrapper").style.display = 'none';
    // document.getElementById("bytedesk_start").style.display = '';
    // top.postMessage("子页面消息收到", 'http://a.com/main.html')
    window.parent.postMessage("bytedesk-close", '*');
  });
  $('#bytedesk_sound').click(function () {
    // 禁止播放提示音
    $('#bytedesk_sound').hide()
    $('#bytedesk_mute').show()
    bd_kfe_data.voiceMuted = true
    localStorage.voiceMuted = true
  });
  $('#bytedesk_mute').click(function () {
    // 播放提示音
    $('#bytedesk_mute').hide()
    $('#bytedesk_sound').show()
    bd_kfe_data.voiceMuted = false
    localStorage.voiceMuted = false
  });
  // 二维码
  $('#bytedesk_qrcode').click(function () {
    $('#bytedesk_qrcode_overlay').show()
    $('#bytedesk_qrcode_container').show()
    // 显示二维码
    // var pcUrl = window.location.href
    // TODO: 修改参数，自行拼装，添加username参数，用户同步过去
    // var h5Url = pcUrl.replace('/pc/', '/h5/')
    // console.log('pcUrl:', pcUrl, 'h5Url:', h5Url)
    var h5Url = bd_kfe_data.getH5Url()
    bd_kfe_httpapi.getShortUrl(h5Url, function (response) {
      var shortUrl = response
      console.log('shortUrl:', shortUrl)
      // URL过长，转成短码
      bd_kfe_utils.showQrcode(shortUrl)
    }, function (error) {
      console.log('error:', error)
      alert(error)
    })
    console.log('h5Url:', h5Url)
    // bd_kfe_utils.showQrcode(h5Url)
  });
  $('#bytedesk_qrcode_overlay').click(function () {
    $('#bytedesk_qrcode_overlay').hide()
    $('#bytedesk_qrcode_container').hide()
  });
  $('#bytedesk_qrcode_close').click(function () {
    $('#bytedesk_qrcode_overlay').hide()
    $('#bytedesk_qrcode_container').hide()
  });
  // 表情
  $('#bytedesk_input-emoji').click(function () {
    bd_kfe_utils.switchEmotion();
  });
  // $('#bytedesk_jietu').click(function () {
  //   alert('直接使用QQ/微信截图，Ctrl+V粘贴到输入框，即可直接发送截图')
  // });
  $('#bytedesk_upload_image').click(function () {
    // 上传图片
    bd_kfe_utils.showUploadImageDialog();
  });
  $('#bytedesk_upload_video').click(function () {
    // 上传视频
    bd_kfe_utils.showUploadVideoDialog();
  });
  $('#bytedesk_upload_file').click(function () {
    // 上传文件
    bd_kfe_utils.showUploadFileDialog();
  });
  $('#bytedesk_more').click(function () {
    // 加载更多聊天记录
    bd_kfe_httpapi.loadHistoryMessages(true)
  })
  $('#bytedesk_message_rate').click(function () {
    // 满意度评价
    $('#bytedesk_rate').show();
    $('#bytedesk_main').hide();
  });
  $('#bytedesk_rate-close').click(function () {
    // 关闭满意度评价界面
    $('#bytedesk_rate').hide();
    $('#bytedesk_main').show();
  });
  $('#bytedesk_leave_close').click(function () {
    $('#bytedesk_leave').hide();
    $('#bytedesk_main').show();
  });
  $('#bytedesk_message_agent').click(function () {
    // bd_kfe_utils.printLog('request agent')
    $("#bytedesk_loading").show()
    bd_kfe_httpapi.requestAgent()
  });
  $('#bytedesk_input_pc_send').click(function () {
    // bd_kfe_utils.printLog('send text message');
    bd_kfe_stompapi.sendTextMessage();
  });
  $('#bytedesk_input-mobile_send_btn').click(function () {
    // bd_kfe_utils.printLog('collect mobile');
    bd_kfe_httpapi.sendMobile();
  });
  $('#bytedesk_quick_question_arrow').click(function () {
    if ($('#bytedesk_quick_question_arrow').text() === '↓') {
      $('#bytedesk_quick_question_arrow').text('↑');
      $('.bytedesk_quick_question-item').hide()
    } else {
      $('#bytedesk_quick_question_arrow').text('↓');
      $('.bytedesk_quick_question-item').show()
    }
  });
  // 监听浏览器显示或隐藏当前页面
  bd_kfe_data.browserTitle = document.title;
  document.addEventListener('visibilitychange', function () {
    if (document.visibilityState === 'hidden') {
      bd_kfe_data.browserTabHidden = true
    } else if (document.visibilityState === 'visible') {
      bd_kfe_data.browserTabHidden = false;
      document.title = bd_kfe_data.browserTitle;
    }
  })
  // 监听输入框粘贴图片，支持qq、微信等截图
  document.getElementById('bytedesk_input_textarea').addEventListener('paste', function (e) {
    // 添加到事件对象中的访问系统剪贴板的接口
    var clipboardData = e.clipboardData,
      i = 0,
      items, item, types;
    if (clipboardData) {
      items = clipboardData.items;
      if (!items) {
        return;
      }
      item = items[0];
      // 保存在剪贴板中的数据类型
      types = clipboardData.types || [];
      for (; i < types.length; i++) {
        if (types[i] === 'Files') {
          item = items[i];
          break;
        }
      }
      // 判断是否为图片数据
      if (item && item.kind === 'file' && item.type.match(/^image\//i)) {
        bd_kfe_utils.imgReader(item);
      }
    }
  });
  // FIXME: 去掉之后，输入框内会有多余空格？
  $("#bytedesk_input_textarea").val("");
  // 禁止选中对话框内容
  if (document.attachEvent) {
    //ie的事件监听，拖拽div时禁止选中内容，firefox与chrome已在css中设置过-moz-user-select: none; -webkit-user-select: none;
    g('pageChat').attachEvent('onselectstart', function () {
      return false;
    });
  }
  // 拖动对话窗口
  // 鼠标按下
  $("#dragHeader").mousedown(function (event) {
    // https://blog.csdn.net/u012557814/article/details/117123075
    // bd_kfe_utils.printLog('mousedown event:' + event.clientX + ' y:' + event.clientY)
    window.parent.postMessage({ msg: 'bytedesk-mousedown', mouseX: event.clientX, mouseY: event.clientY }, '*')
  });
  // 鼠标移动更新窗口位置
  $(document).mousemove(function (event) {
    // https://blog.csdn.net/u012557814/article/details/117123075
    // console.log(event)
    // window.parent.postMessage("bytedesk-mousemove", '*');
    // bd_kfe_utils.printLog('mousedown event:' + event.clientX + ' y:' + event.clientY)
    window.parent.postMessage({ msg: 'bytedesk-mousemove', mouseX: event.clientX, mouseY: event.clientY }, '*')
  });
  // 鼠标离开
  $("#dragHeader").mouseup(function (event) {
    window.parent.postMessage("bytedesk-mouseup", '*');
  });
  // 监听网络状态
  window.addEventListener("online", function () {
    // console.log("网络恢复");
    var langText = "请在此输入文本内容或者粘贴QQ/微信截图或拖拽图片发送"
    if (bd_kfe_data.lang === "en") {
      langText = "Please Input Text"
    }
    $("#bytedesk_input_textarea").attr("placeholder", langText)
    // 拉取未读消息
    bd_kfe_httpapi.loadMessagesUnread()
  });
  window.addEventListener("offline", function () {
    // console.log("网络已断开");
    var langText = "网络已断开"
    if (bd_kfe_data.lang === "en") {
      langText = "Network Disconnected"
    }
    $("#bytedesk_input_textarea").attr("placeholder", langText)
  });
  // 全局鼠标离开
  window.addEventListener('mouseup', (event) => {
    window.parent.postMessage("bytedesk-mouseup", '*');
  })
  // 监听从iframe外发送消息，修改title颜色等
  // https://stackoverflow.com/questions/25098021/securityerror-blocked-a-frame-with-origin-from-accessing-a-cross-origin-frame
  window.addEventListener('message', event => {
    // bd_kfe_utils.printLog('iframe received: ' + event.data);
    // var title = document.getElementById("bytedesk_title")
    // title.style.setProperty('background-color', event.data)
    // IMPORTANT: check the origin of the data! 
  })
  bd_kfe_kefu.mounted();
  //
  bd_kfe_data.browserTitle = document.title;
  // 监听浏览器显示或隐藏当前页面
  document.addEventListener("visibilitychange", () => {
    // bd_kfe_utils.printLog('visibilitychange:' + document.visibilityState)
    if (document.visibilityState === "hidden") {
      // bd_kfe_utils.printLog('隐藏')
      bd_kfe_data.hidden = true;
      // TODO: 在隐藏当前tab之后，收到新消息时需要页面tab标题滚动
    } else if (document.visibilityState === "visible") {
      // bd_kfe_utils.printLog('显示')
      bd_kfe_data.hidden = false;
      // this.shouldScrollBrowserTitle = false
      bd_kfe_utils.restoreBrowserTitle();
    }
  },
    false
  );
  // 拖拽上传发送文件
  var oDragWrap = document.body;
  //拖进
  oDragWrap.addEventListener(
    "dragenter",
    function (e) {
      e.preventDefault();
      // console.log('dragenter:')
      // console.log(e)
    },
    false
  );
  //拖离
  oDragWrap.addEventListener(
    "dragleave",
    function (e) {
      e.preventDefault();
      // dragleaveHandler(e);
      // console.log('dragleave:')
      // console.log(e)
    },
    false
  );
  //拖来拖去 , 一定要注意dragover事件一定要清除默认事件
  //不然会无法触发后面的drop事件
  oDragWrap.addEventListener(
    "dragover",
    function (e) {
      e.preventDefault();
      // console.log('dragover:')
      // console.log(e)
    },
    false
  );
  //扔
  oDragWrap.addEventListener(
    "drop",
    function (e) {
      dropHandler(e);
      // console.log('drop:')
      // console.log(e)
    },
    false
  );
  var dropHandler = function (e) {
    // 将本地图片拖拽到页面中后要进行的处理都在这
    e.preventDefault(); //获取文件列表
    // console.log('dropHandler:')
    // console.log(e)
    // 
    var fileList = e.dataTransfer.files;
    // 检测是否是拖拽文件到页面的操作
    if (fileList.length == 0) {
      return;
    }
    // 检测文件是不是图片
    if (fileList[0].type.indexOf("image") === -1) {
      return;
    }
    // 上传文件，并发送
    let file = fileList[0]
    if (/\.(gif|jpg|jpeg|png|webp|GIF|JPG|PNG|WEBP)$/.test(file.name)) {
      // 发送图片
      bd_kfe_utils.uploadImage(file)
      return
    }
    // 发送其他文件
    bd_kfe_utils.uploadFile(file)
  };
  // 初始化拖动
  bd_kfe_utils.dragRightColumnInit();
  // 
  bd_kfe_utils.requestNotification();
  //
  bd_kfe_stompapi.delaySendPreviewMessage = _.debounce(bd_kfe_stompapi.sendPreviewMessage, 500)
  // 判断是否携带参数websiteurl，只有嵌入式才会携带
  if (bd_kfe_utils.getUrlParam("websiteurl") === null) {
    // 当前网页 和 来源地址
    // bd_kfe_utils.printLog('index websiteUrl:' + window.location.href + ' index refererUrl:' + document.referrer)
    bd_kfe_data.websiteUrl = window.location.href
    bd_kfe_data.websiteTitle = document.title
    bd_kfe_data.refererUrl = document.referrer
  } else {
    // 当前网页 和 来源地址
    // bd_kfe_utils.printLog('websiteUrl 2:' + bd_kfe_utils.getUrlParam("websiteurl") + ' refererUrl 2:' + bd_kfe_utils.getUrlParam("refererurl"))
    bd_kfe_data.websiteUrl = bd_kfe_utils.getUrlParam("websiteurl")
    bd_kfe_data.websiteTitle = bd_kfe_utils.getUrlParam("websitetitle")
    bd_kfe_data.refererUrl = bd_kfe_utils.getUrlParam("refererurl")
  }
  // bd_kfe_utils.printLog('index websiteUrl:' + bd_kfe_data.websiteUrl + ' index refererUrl:' + bd_kfe_data.refererUrl)
})();