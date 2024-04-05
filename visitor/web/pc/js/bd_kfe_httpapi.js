/**
 * bytedesk.com
 */
/**
 * @apiDefine User 用户
 *
 * 用户相关接口
 */
/**
 * @apiDefine Group 群组
 *
 * 群组相关接口
 */
/**
 * @apiDefine SubDomainClientParam
 * @apiParam {String} subDomain 企业号，测试可填写 'vip'，上线请填写真实企业号
 * @apiParam {String} client 固定写死为 'web'
 */
/**
 * @apiDefine UserResultSuccess
 * @apiSuccess {String} uid 用户唯一uid.
 * @apiSuccess {String} username  用户名.
 * @apiSuccess {String} nickname  昵称.
 */
/**
 * @apiDefine ResponseResultSuccess
 * @apiSuccess {String} message 返回提示
 * @apiSuccess {Number} status_code 状态码
 * @apiSuccess {String} data 返回内容
 */
/**
 * @apiDefine Social 社交
 *
 * 社交关系相关接口
 */
var bd_kfe_httpapi = {
  /**
   * @api {get} /visitor/api/username 生成默认访客账号
   * @apiName requestUsername
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission none
   * 
   * @apiUse SubDomainClientParam
   * 
   * @apiDescription 1. 首先判断是否已经注册过；
   * 2. 如果已经注册过，则直接调用登录接口；
   * 3. 如果没有注册过，则从服务器请求用户名；
   * 4. FIXME: 暂未考虑浏览器不支持localStorage的情况
   *
   * @apiUse UserResultSuccess
   */
  requestUsername: function () {
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/visitor/api/username",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        subDomain: bd_kfe_data.subDomain,
        client: bd_kfe_data.client
      },
      success: function (response) {
        // 登录
        bd_kfe_data.uid = response.data.uid;
        bd_kfe_data.username = response.data.username;
        bd_kfe_data.password = response.data.username;
        bd_kfe_data.nickname = response.data.nickname;
        // TODO: 判断浏览器是否支持localStorage, 如果不支持则使用cookie
        // 本地存储
        localStorage.bd_kfe_uid = bd_kfe_data.uid;
        localStorage.bd_kfe_username = bd_kfe_data.username;
        // localStorage.bd_kfe_password = bd_kfe_data.password;
        localStorage.bd_kfe_nickname = bd_kfe_data.nickname;
        // 登录
        bd_kfe_httpapi.login();
      },
      error: function (error) {
        //Do Something to handle error
        bd_kfe_utils.printLog(error);
      }
    });
    // }
  },
  /**
   * @api {post} /visitor/api/register/user 自定义用户名生成访客账号
   * @apiName registerUser
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission none
   * 
   * @apiParam {String} username 用户名
   * @apiParam {String} nickname 昵称
   * @apiParam {String} password 密码
   * @apiUse SubDomainClientParam
   * 
   * @apiDescription 开发者在需要跟自己业务系统账号对接的情况下，
   * 可以通过自定义用户名生成访客账号
   *
   * @apiUse UserResultSuccess
   */
  registerUser: function () {
    // 
    // var selfuser = bd_kfe_utils.getUrlParam("selfuser");
    // if (selfuser !== "1") {
    //   // 非自定义用户名，直接返回。只有为1的时候，继续
    //   return
    // }
    //
    var username = bd_kfe_utils.getUrlParam("username");
    var nickname = bd_kfe_utils.getUrlParam("nickname") == null
      ? username : bd_kfe_utils.getUrlParam("nickname");
    var avatar = bd_kfe_utils.getUrlParam("avatar") === null
      ? "" : bd_kfe_utils.getUrlParam("avatar");
    // bd_kfe_utils.printLog("username self:" + username + nickname + avatar);
    //
    var password = username; // 用户名作为默认密码
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/visitor/api/register/user",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      type: "post",
      data: JSON.stringify({
        username: username,
        nickname: nickname,
        password: password,
        avatar: avatar,
        subDomain: bd_kfe_data.subDomain,
        client: bd_kfe_data.client
      }),
      success: function (response) {
        bd_kfe_utils.printLog("registerUser success: " + JSON.stringify(response));
        //
        if (response.status_code === 200) {
          // 登录
          bd_kfe_data.uid = response.data.uid;
          bd_kfe_data.username = response.data.username;
          bd_kfe_data.password = password;
          bd_kfe_data.nickname = response.data.nickname;
          bd_kfe_data.avatar = response.data.avatar
          // 本地存储
          localStorage.bd_kfe_uid = bd_kfe_data.uid;
          localStorage.bd_kfe_username = bd_kfe_data.username;
          // localStorage.bd_kfe_password = bd_kfe_data.password;
          localStorage.bd_kfe_nickname = bd_kfe_data.nickname;
        } else {
          // 账号已经存在
          bd_kfe_data.uid = response.data
          bd_kfe_data.username = username + '@' + bd_kfe_data.subDomain;
          bd_kfe_data.password = password;
          bd_kfe_data.nickname = nickname;
          bd_kfe_data.avatar = avatar
          // 本地存储
          localStorage.bd_kfe_uid = bd_kfe_data.uid;
          localStorage.bd_kfe_username = bd_kfe_data.username;
          // localStorage.bd_kfe_password = bd_kfe_data.password;
          localStorage.bd_kfe_nickname = bd_kfe_data.nickname;
        }
        // 登录
        bd_kfe_httpapi.login();
      },
      error: function (error) {
        //Do Something to handle error
        bd_kfe_utils.printLog(error);
      }
    });
  },
  /**
   * @api {post} /oauth/token 登录
   * @apiName login
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission none
   * 
   * @apiHeader {String} Authorization 值固定写死为: 'Basic Y2xpZW50OnNlY3JldA=='
   * 
   * @apiParam {String} username 用户名
   * @apiParam {String} password 密码
   * 
   * @apiDescription 登录
   *
   * @apiSuccess {String} access_token 访问令牌
   * @apiSuccess {Number} expires_in 过期时间
   * @apiSuccess {String} jti
   * @apiSuccess {String} refresh_token 刷新令牌
   * @apiSuccess {String} scope 固定值：'all'
   * @apiSuccess {String} token_type 固定值：'bearer'
   */
  login: function () {
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/visitor/token", //"/oauth/token",
      type: "post",
      data: {
        "username": bd_kfe_data.username,
        "password": bd_kfe_data.password,
        "grant_type": "password",
        "scope": "all"
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Basic Y2xpZW50OnNlY3JldA==');
      },
      success: function (response) {
        bd_kfe_utils.printLog("login success: " + JSON.stringify(response));
        // 本地存储
        bd_kfe_data.passport.token = response;
        localStorage.bd_kfe_access_token = response.access_token
        // 建立长连接
        // 放在此处容易一起bug：Uncaught Error: InvalidStateError: The connection has not been established yet
        // 要在请求会话成功之后，才能够建议长链接，长链接成功之后，再subscribeTopic
        // bd_kfe_stompapi.byteDeskConnect();
        // TODO: 判断是否处于会话状态，如果没有，则请求会话
        if (bd_kfe_data.thread.id === 0) {
          bd_kfe_httpapi.requestThread();
        }
        // 请求指纹
        // bd_kfe_httpapi.fingerPrint2();
        // bd_kfe_httpapi.getTopAnswers();
        // 加载未读数目
        // bd_kfe_httpapi.getUnreadCount(bd_kfe_data.uni_wid);
        // 拉取快捷按钮
        // bd_kfe_httpapi.getQuickButtons()
        // 防止长连接断开，则定时刷新聊天记录
        // TODO: 智能调节时长，如果长时间没有未读消息，则拉取时间间隔逐渐加长
        // setInterval(function () {
        //   bd_kfe_httpapi.loadMessagesUnread()
        // }, 5000);
      },
      error: function (error) {
        //Do Something to handle error
        bd_kfe_utils.printLog(error);
      }
    });
  },
  // 为减轻/oauth/token接口压力，调用此接口判断token是否过期，如果能正常访问，则未过期，不需要重新调用/oauth/token
  isTokenValid: function() {
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/user/token/valide",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
      },
      success: function (response) {
        console.log('isTokenValid success:', response);
      },
      error: function (error) {
        console.log('isTokenValid error:', error);
        // token过期
        app.login()
      }
    });
  },
  /**
   * 获取设备指纹
   */
  fingerPrint2: function () {
    // #获取全部
    var deviceInfo = DeviceInfo.getDeviceInfo({ domain: '' })
    // bd_kfe_utils.printLog('fingerprint2:' + JSON.stringify(deviceInfo));
    // var url = bd_kfe_data.websiteUrl //window.location.href;
    // url = url.endsWith("#") ? url.substring(0, url.length - 1) : url;
    // console.log('fingerPrint2 url:', bd_kfe_data.websiteUrl, bd_kfe_data.refererUrl)
    // 
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/api/fingerprint2/browser",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      type: "post",
      data: JSON.stringify({
        browserInfo: encodeURI(deviceInfo.browserInfo),
        deviceType: encodeURI(deviceInfo.deviceType),
        fingerprint: encodeURI(deviceInfo.fingerprint),
        language: encodeURI(deviceInfo.language),
        os: encodeURI(deviceInfo.os),
        osVersion: encodeURI(deviceInfo.osVersion),
        referrer: encodeURI(bd_kfe_data.refererUrl),
        url: encodeURI(bd_kfe_data.websiteUrl),
        client: bd_kfe_data.client
      }),
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        // bd_kfe_utils.printLog("fingerprint2: " + JSON.stringify(response));
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
      }
    });
  },
  /**
   * 通知服务器，访客浏览网页中
   * 修改为POST请求方式
   */
  browse: function () {
    //
    if (!bd_kfe_data.thread.tid || bd_kfe_data.thread.tid.trim().length == 0) {
      bd_kfe_utils.printLog("thread tid is null");
      return
    }
    // 
    var url = bd_kfe_data.websiteUrl //window.location.href;
    url = url.endsWith("#") ? url.substring(0, url.length - 1) : url;
    //
    // console.log('browse tid:', bd_kfe_data.thread.tid)
    // console.log('browse url: ', url);
    // console.log('browse preUrl: ', bd_kfe_data.refererUrl);
    // console.log('browse title:', bd_kfe_data.websiteTitle)
    // 
    if (bd_kfe_data.websiteTitle && bd_kfe_data.websiteTitle.length > 6) {
      bd_kfe_data.websiteTitle = bd_kfe_data.websiteTitle.substring(0, 6)
    }
    // console.log('browse title2:', bd_kfe_data.websiteTitle)
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/thread/set/url",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      type: "post",
      data: JSON.stringify({
        tid: bd_kfe_data.thread.tid,
        preUrl: encodeURI(bd_kfe_data.refererUrl),
        url: encodeURI(url),
        title: encodeURI(bd_kfe_data.websiteTitle)
      }),
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        // console.log('response: ', response)
        bd_kfe_utils.printLog("browse:" + JSON.stringify(response.data));
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
      }
    });
    // var keywords = document.getElementsByName("keywords")[0].content;
    // var description = document.getElementsByName("description")[0].content;
    // $.ajax({
    //   url: bd_kfe_data.HTTP_HOST +
    //     "/api/browse/notify",
    //   contentType: "application/json; charset=utf-8",
    //   dataType: "json",
    //   type: "post",
    //   data: JSON.stringify({
    //     adminUid: bd_kfe_data.adminUid,
    //     client: bd_kfe_data.client,
    //     sessionId: bd_kfe_data.sessionId,
    //     referrer: encodeURI(bd_kfe_data.refererUrl),
    //     url: encodeURI(url),
    //     title: encodeURI(bd_kfe_data.websiteTitle)
    //   }),
    //   beforeSend: function (xhr) {
    //     xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
    //   },
    //   success: function (response) {
    //     // bd_kfe_utils.printLog("browse:" + JSON.stringify(response.data));
    //   },
    //   error: function (error) {
    //     bd_kfe_utils.printLog(error);
    //   }
    // });
  },
  acceptInviteBrowse: function () {
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/browse/invite/accept",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      type: "post",
      data: JSON.stringify({
        biid: bd_kfe_data.browseInviteBIid,
        client: bd_kfe_data.client
      }),
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("browse invite accept:" + response.data);
      },
      error: function (error) {
        //Do Something to handle error
        bd_kfe_utils.printLog(error);
      }
    });
  },
  rejectInviteBrowse: function () {
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/browse/invite/reject",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      type: "post",
      data: JSON.stringify({
        biid: bd_kfe_data.browseInviteBIid,
        client: bd_kfe_data.client
      }),
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("browse invite reject:" + response.data);
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
      }
    });
  },
  /**
   * @api {get} /api/thread/request 请求会话
   * @apiName requestThread
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} wId 工作组唯一wid
   * @apiParam {String} type 区分工作组会话 'workGroup'、指定坐席会话 'appointed'
   * @apiParam {String} aId 指定客服uid, 只有当type === 'appointed'时有效
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 请求会话
   *
   * @apiUse ResponseResultSuccess
   */
  requestThread: function (isManual = false) {
    // bd_kfe_utils.printLog('start request thread');
    // 如果是预加载，则直接返回，不请求会话
    if (bd_kfe_data.preload === "1") {
      if (bd_kfe_data.type === 'workGroup') {
        bd_kfe_httpapi.getWorkGroupStatus(bd_kfe_data.workGroupWid)
        // 
        bd_kfe_httpapi.getPrechatSettings()
      } else {
        bd_kfe_httpapi.getUserStatus(bd_kfe_data.agentUid)
      }
      // 
      bd_kfe_stompapi.byteDeskConnect()
      return
    }
    if (isManual) {
      // 手动点击 ‘联系客服’，显示loading状态
      $("#bytedesk_loading").show()
    }
    // 优先显示机器人分类
    if (bd_kfe_data.v2robot === "1") {
      bd_kfe_httpapi.requestWorkGroupThreadV2()
      return
    }
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/thread/request",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        wId: bd_kfe_data.workGroupWid,
        type: bd_kfe_data.type,
        aId: bd_kfe_data.agentUid,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        // bd_kfe_utils.printLog("message:" + response);
        bd_kfe_httpapi.dealWithThread(response, isManual);
        // 请求指纹
        // bd_kfe_httpapi.fingerPrint2();
        
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
        // token过期
        bd_kfe_httpapi.login();
      }
    });
    // 请求指纹
    // bd_kfe_httpapi.fingerPrint2();
  },
  requestWorkGroupThreadV2: function () {
    // 
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/v2/thread/workGroup",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        wId: bd_kfe_data.workGroupWid,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.bd_kfe_access_token);
      },
      success: function (response) {
        // console.log('message:', response);
        // app.$indicator.close();
        bd_kfe_httpapi.dealWithThread(response, false)
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
        // token过期
        bd_kfe_httpapi.login();
      }
    });
  },
  /**
   * @api {get} /api/thread/request/agent 请求人工客服
   * @apiName requestAgent
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} wId 工作组唯一wid
   * @apiParam {String} type 区分工作组会话 'workGroup'、指定坐席会话 'appointed'
   * @apiParam {String} aId 指定客服uid, 只有当type === 'appointed'时有效
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 请求人工客服，不管此工作组是否设置为默认机器人，只要有人工客服在线，则可以直接对接人工
   *
   * @apiUse ResponseResultSuccess
   */
  requestAgent: function () {
    // bd_kfe_utils.printLog('start request agent thread');
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/thread/request/agent",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        wId: bd_kfe_data.workGroupWid,
        type: bd_kfe_data.type,
        // aId: bd_kfe_data.agentUid,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("message:" + JSON.stringify(response));
        //
        $("#bytedesk_loading").hide()
        var message = response.data;
        if (response.status_code === 200) {
          //
          bd_kfe_utils.pushToMessageArray(message);
          // 1. 保存thread
          bd_kfe_data.thread = message.thread;
          // 3. 加载聊天记录
          // bd_kfe_httpapi.loadHistoryMessages();
          bd_kfe_httpapi.showLoadHistoryMessage();
          // 4. 设置窗口左上角标题
          if (bd_kfe_data.thread.appointed) {
            // bd_kfe_data.title = bd_kfe_data.thread.agent.nickname;
            //
            bd_kfe_data.agentAvatar = bd_kfe_data.thread.agent.avatar;
            bd_kfe_data.agentNickname = bd_kfe_data.thread.agent.nickname;
            bd_kfe_data.agentDescription = bd_kfe_data.thread.agent.description;
            //
          } else {
            // bd_kfe_data.title = bd_kfe_data.thread.workGroup.nickname;
            //
            bd_kfe_data.agentAvatar = message.user.avatar;
            bd_kfe_data.agentNickname = message.user.nickname;
            bd_kfe_data.agentDescription = message.user.description
          }
          $('#bytedesk_agent_avatar').attr('src', bd_kfe_data.agentAvatar);
          $('#bytedesk_agent_nickname').text(bd_kfe_data.agentNickname);
          $('#bytedesk_agent_description').text(bd_kfe_data.agentDescription);
          // 设置当前为人工客服
          bd_kfe_data.isRobot = false;
          // 防止会话超时自动关闭，重新标记本地打开会话
          bd_kfe_data.isThreadClosed = false;
          // 隐藏请求人工按钮
          bd_kfe_utils.toggleRequestAgentTip(false);
        } else if (response.status_code === 201) {
          // message.content = "继续之前会话";
          // bd_kfe_utils.pushToMessageArray(message);
          // 1. 保存thread
          bd_kfe_data.thread = message.thread;
          // 3. 加载聊天记录
          // bd_kfe_httpapi.loadHistoryMessages();
          bd_kfe_httpapi.showLoadHistoryMessage();
          // 4. 头像、标题、描述
          if (bd_kfe_data.thread.appointed) {
            // bd_kfe_data.title = bd_kfe_data.thread.agent.nickname;
            //
            bd_kfe_data.agentAvatar = bd_kfe_data.thread.agent.avatar;
            bd_kfe_data.agentNickname = bd_kfe_data.thread.agent.nickname;
            bd_kfe_data.agentDescription = bd_kfe_data.thread.agent.description;
          } else {
            // bd_kfe_data.title = bd_kfe_data.thread.workGroup.nickname;
            //
            bd_kfe_data.agentAvatar = message.user.avatar;
            bd_kfe_data.agentNickname = message.user.nickname;
            bd_kfe_data.agentDescription = message.user.description
          }
          $('#bytedesk_agent_avatar').attr('src', bd_kfe_data.agentAvatar);
          $('#bytedesk_agent_nickname').text(bd_kfe_data.agentNickname);
          $('#bytedesk_agent_description').text(bd_kfe_data.agentDescription);
          // 设置当前为人工客服
          bd_kfe_data.isRobot = false;
          // 防止会话超时自动关闭，重新标记本地打开会话
          bd_kfe_data.isThreadClosed = false;
          // 隐藏请求人工按钮
          bd_kfe_utils.toggleRequestAgentTip(false);
        } else if (response.status_code === 202) {
          // 排队
          bd_kfe_utils.pushToMessageArray(message);
          // 1. 保存thread
          bd_kfe_data.thread = message.thread;
          // 防止会话超时自动关闭，重新标记本地打开会话
          bd_kfe_data.isThreadClosed = false;
        } else if (response.status_code === 203) {
          // 当前非工作时间，请自助查询或留言
          bd_kfe_utils.pushToMessageArray(message);
          // 1. 保存thread
          bd_kfe_data.thread = message.thread;
          // 4. 设置窗口左上角标题
          if (bd_kfe_data.thread.appointed) {
            // bd_kfe_data.title = bd_kfe_data.thread.agent.nickname;
            bd_kfe_data.agentNickname = bd_kfe_data.thread.agent.nickname;
          } else {
            // bd_kfe_data.title = bd_kfe_data.thread.workGroup.nickname;
            bd_kfe_data.agentNickname = bd_kfe_data.thread.workGroup.nickname;
          }
          $('#bytedesk_agent_nickname').text(bd_kfe_data.agentNickname);
          // 设置当前为人工客服
          bd_kfe_data.isRobot = false;
          if (message.sessionType === 'form') {
            // 显示留言界面
            bd_kfe_utils.switchLeaveMessage();
          } else {
            console.log('聊天页面留言')
          }
          // 隐藏请求人工按钮
          bd_kfe_utils.toggleRequestAgentTip(false);
        } else if (response.status_code === 204) {
          // 当前无客服在线，请自助查询或留言
          bd_kfe_utils.pushToMessageArray(message);
          // 1. 保存thread
          bd_kfe_data.thread = message.thread;
          // 4. 设置窗口左上角标题
          if (bd_kfe_data.thread.appointed) {
            // bd_kfe_data.title = bd_kfe_data.thread.agent.nickname;
            bd_kfe_data.agentAvatar = bd_kfe_data.thread.agent.avatar;
            bd_kfe_data.agentNickname = bd_kfe_data.thread.agent.nickname;
            bd_kfe_data.agentDescription = bd_kfe_data.thread.agent.description
          } else {
            // bd_kfe_data.title = bd_kfe_data.thread.workGroup.nickname;
            bd_kfe_data.agentAvatar = bd_kfe_data.thread.workGroup.avatar
            bd_kfe_data.agentNickname = bd_kfe_data.thread.workGroup.nickname;
            bd_kfe_data.agentDescription = bd_kfe_data.thread.workGroup.description
          }
          $('#bytedesk_agent_avatar').attr('src', bd_kfe_data.agentAvatar);
          $('#bytedesk_agent_nickname').text(bd_kfe_data.agentNickname);
          $('#bytedesk_agent_description').text(bd_kfe_data.agentDescription)
          //
          if (message.sessionType === 'form') {
            // 显示留言界面
            bd_kfe_utils.switchLeaveMessage();
          } else {
            console.log('聊天页面留言')
          }
        } else if (response.status_code === 205) {
          // 前置选择
          bd_kfe_data.questionnaireItemItems = message.questionnaire.questionnaireItems[0].questionnaireItemItems;
          // 插入业务路由，相当于咨询前提问问卷（选择 或 填写表单）
          bd_kfe_utils.pushToMessageArray(message);
          // 1. 保存thread
          bd_kfe_data.thread = message.thread;
        } else if (response.status_code === 206) {
          // 返回机器人初始欢迎语 + 欢迎问题列表
          bd_kfe_utils.pushToMessageArray(message);
          // 1. 保存thread
          bd_kfe_data.thread = message.thread;
          // 2. 设置当前状态为机器人问答
          bd_kfe_data.isRobot = true;
          // 3. 设置左上角头像为机器人头像 和 昵称
          bd_kfe_data.agentAvatar = message.user.avatar
          bd_kfe_data.agentNickname = message.user.nickname
          bd_kfe_data.agentDescription = message.user.description
          //
          $('#bytedesk_agent_avatar').attr('src', bd_kfe_data.agentAvatar);
          $('#bytedesk_agent_nickname').text(bd_kfe_data.agentNickname);
          $('#bytedesk_agent_description').text(bd_kfe_data.agentDescription);
        } else if (response.status_code === -1) {
          bd_kfe_httpapi.login();
        } else if (response.status_code === -2) {
          // sid 或 wid 错误
          // alert("siteId或者工作组id错误");
        } else if (response.status_code === -3) {
          // alert("您已经被禁言");
        }
        bd_kfe_utils.scrollToBottom();
        // 建立长连接
        bd_kfe_stompapi.byteDeskConnect();
        // 请求指纹
        // bd_kfe_httpapi.fingerPrint2();
        // 订阅会话消息，处理断开重连的情况
        // 长链接成功之后，再subscribeTopic, 否则：InvalidStateError: The connection has not been established yet
        // if (
        //   bd_kfe_data.thread.tid !== null &&
        //   bd_kfe_data.thread.tid !== undefined &&
        //   bd_kfe_data.thread.tid !== ""
        // ) {
        //   bd_kfe_stompapi.subscribeTopic(bd_kfe_data.threadTopic());
        // }
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
        // token过期
        bd_kfe_httpapi.login();
      }
    });
    // // 请求指纹
    // bd_kfe_httpapi.fingerPrint2();
  },
  /**
   * 处理返回结果
   * @param {*} response 
   */
  dealWithThread: function (response, isManual = false) {
    // bd_kfe_utils.printLog("dealWithThread:" + JSON.stringify(response));
    //
    $("#bytedesk_loading").hide()
    var message = response.data;
    if (response.status_code === 200) {
      //
      bd_kfe_utils.pushToMessageArray(message);
      // 1. 保存thread
      bd_kfe_data.thread = message.thread;
      // 3. 加载聊天记录
      // bd_kfe_httpapi.loadHistoryMessages();
      bd_kfe_httpapi.showLoadHistoryMessage();
      // 4. 设置窗口左上角标题
      if (bd_kfe_data.thread.appointed) {
        // bd_kfe_data.title = bd_kfe_data.thread.agent.nickname;
        //
        bd_kfe_data.agentAvatar = bd_kfe_data.thread.agent.avatar;
        bd_kfe_data.agentNickname = bd_kfe_data.thread.agent.nickname;
        bd_kfe_data.agentDescription = bd_kfe_data.thread.agent.description
      } else {
        // bd_kfe_data.title = bd_kfe_data.thread.workGroup.nickname;
        //
        bd_kfe_data.agentAvatar = message.user.avatar;
        bd_kfe_data.agentNickname = message.user.nickname;
        bd_kfe_data.agentDescription = message.user.description;
      }
      $('#bytedesk_agent_avatar').attr('src', bd_kfe_data.agentAvatar);
      $('#bytedesk_agent_nickname').text(bd_kfe_data.agentNickname);
      $('#bytedesk_agent_description').text(bd_kfe_data.agentDescription)
      // 设置当前为人工客服
      bd_kfe_data.isRobot = false;
      // 防止会话超时自动关闭，重新标记本地打开会话
      bd_kfe_data.isThreadClosed = false;
      // 显示商品信息
      bd_kfe_stompapi.appendCommodityInfo();
    } else if (response.status_code === 201) {
      // message.content = "继续之前会话";
      bd_kfe_utils.pushToMessageArray(message);
      // 1. 保存thread
      bd_kfe_data.thread = message.thread;
      // 3. 加载聊天记录
      // bd_kfe_httpapi.loadHistoryMessages();
      bd_kfe_httpapi.showLoadHistoryMessage();
      // 4. 头像、标题、描述
      if (bd_kfe_data.thread.appointed) {
        // bd_kfe_data.title = bd_kfe_data.thread.agent.nickname;
        //
        bd_kfe_data.agentAvatar = bd_kfe_data.thread.agent.avatar;
        bd_kfe_data.agentNickname = bd_kfe_data.thread.agent.nickname;
        bd_kfe_data.agentDescription = bd_kfe_data.thread.agent.description;
      } else {
        // bd_kfe_data.title = bd_kfe_data.thread.workGroup.nickname;
        //
        bd_kfe_data.agentAvatar = message.user.avatar;
        bd_kfe_data.agentNickname = message.user.nickname;
        bd_kfe_data.agentDescription = message.user.description
      }
      $('#bytedesk_agent_avatar').attr('src', bd_kfe_data.agentAvatar);
      $('#bytedesk_agent_nickname').text(bd_kfe_data.agentNickname);
      $('#bytedesk_agent_description').text(bd_kfe_data.agentDescription);
      // 设置当前为人工客服
      bd_kfe_data.isRobot = false;
      // 防止会话超时自动关闭，重新标记本地打开会话
      bd_kfe_data.isThreadClosed = false;
      // 显示商品信息
      bd_kfe_stompapi.appendCommodityInfo();
    } else if (response.status_code === 202) {
      // 排队
      bd_kfe_utils.pushToMessageArray(message);
      // 1. 保存thread
      bd_kfe_data.thread = message.thread;
      // 防止会话超时自动关闭，重新标记本地打开会话
      bd_kfe_data.isThreadClosed = false;
    } else if (response.status_code === 203) {
      // 当前非工作时间，请自助查询或留言
      bd_kfe_utils.pushToMessageArray(message);
      // 1. 保存thread
      bd_kfe_data.thread = message.thread;
      // 4. 设置窗口左上角标题
      if (bd_kfe_data.thread.appointed) {
        // bd_kfe_data.title = bd_kfe_data.thread.agent.nickname;
        //
        bd_kfe_data.agentAvatar = bd_kfe_data.thread.agent.avatar;
        bd_kfe_data.agentNickname = bd_kfe_data.thread.agent.nickname;
        bd_kfe_data.agentDescription = bd_kfe_data.thread.agent.description
      } else {
        // bd_kfe_data.title = bd_kfe_data.thread.workGroup.nickname;
        //
        bd_kfe_data.agentAvatar = bd_kfe_data.thread.workGroup.avatar
        bd_kfe_data.agentNickname = bd_kfe_data.thread.workGroup.nickname;
        bd_kfe_data.agentDescription = bd_kfe_data.thread.workGroup.description
      }
      $('#bytedesk_agent_avatar').attr('src', bd_kfe_data.agentAvatar);
      $('#bytedesk_agent_nickname').text(bd_kfe_data.agentNickname);
      $('#bytedesk_agent_description').text(bd_kfe_data.agentDescription)
      // 设置当前为人工客服
      bd_kfe_data.isRobot = false;
      //
      if (message.sessionType === 'form') {
        // 显示留言界面
        bd_kfe_utils.switchLeaveMessage();
      } else {
        console.log('聊天页面留言')
      }
    } else if (response.status_code === 204) {
      // 当前无客服在线，请自助查询或留言
      bd_kfe_utils.pushToMessageArray(message);
      // 1. 保存thread
      bd_kfe_data.thread = message.thread;
      // 4. 设置窗口左上角标题
      if (bd_kfe_data.thread.appointed) {
        // bd_kfe_data.title = bd_kfe_data.thread.agent.nickname;
        //
        bd_kfe_data.agentAvatar = bd_kfe_data.thread.agent.avatar;
        bd_kfe_data.agentNickname = bd_kfe_data.thread.agent.nickname;
        bd_kfe_data.agentDescription = bd_kfe_data.thread.agent.description
      } else {
        // bd_kfe_data.title = bd_kfe_data.thread.workGroup.nickname;
        //
        bd_kfe_data.agentAvatar = bd_kfe_data.thread.workGroup.avatar
        bd_kfe_data.agentNickname = bd_kfe_data.thread.workGroup.nickname;
        bd_kfe_data.agentDescription = bd_kfe_data.thread.workGroup.description
      }
      $('#bytedesk_agent_avatar').attr('src', bd_kfe_data.agentAvatar);
      $('#bytedesk_agent_nickname').text(bd_kfe_data.agentNickname);
      $('#bytedesk_agent_description').text(bd_kfe_data.agentDescription)
      if (message.sessionType === 'form') {
        // 显示留言界面
        bd_kfe_utils.switchLeaveMessage();
      } else {
        console.log('聊天页面留言')
      }
      $("#bytedesk-leavemsg-tip").text(message.content);
    } else if (response.status_code === 205) {
      // 前置选择
      bd_kfe_data.questionnaireItemItems = message.questionnaire.questionnaireItems[0].questionnaireItemItems;
      // 插入业务路由，相当于咨询前提问问卷（选择 或 填写表单）
      bd_kfe_utils.pushToMessageArray(message);
      // 1. 保存thread
      bd_kfe_data.thread = message.thread;
    } else if (response.status_code === 206) {
      // 返回机器人初始欢迎语 + 欢迎问题列表
      bd_kfe_utils.pushToMessageArray(message);
      // 1. 保存thread
      bd_kfe_data.thread = message.thread;
      // 2. 设置当前状态为机器人问答
      bd_kfe_data.isRobot = true;
      // 3. 设置左上角头像为机器人头像 和 昵称
      bd_kfe_data.agentAvatar = message.user.avatar
      bd_kfe_data.agentNickname = message.user.nickname
      bd_kfe_data.agentDescription = message.user.description
      //
      $('#bytedesk_agent_avatar').attr('src', bd_kfe_data.agentAvatar);
      $('#bytedesk_agent_nickname').text(bd_kfe_data.agentNickname);
      $('#bytedesk_agent_description').text(bd_kfe_data.agentDescription);
      // 显示人工按钮
      bd_kfe_utils.toggleRequestAgentTip(true);
      // 拉取转人工关键词
      bd_kfe_httpapi.getTransferWords();
    } else if (response.status_code === -1) {
      bd_kfe_httpapi.login();
    } else if (response.status_code === -2) {
      // alert("siteId或者工作组id错误");
    } else if (response.status_code === -3) {
      // alert("您已经被禁言");
    }
    bd_kfe_utils.scrollToBottom();
    // 手动点击请求会话的忽略掉
    if (!isManual) {
      // 获取聊天设置
      if (message.thread != null && message.thread != undefined) {
        bd_kfe_httpapi.dealWithWorkGroup(message.thread.workGroup)
      }
      // 拉取快捷按钮
      bd_kfe_httpapi.getQuickButtons()
      // 防止长连接断开，则定时刷新聊天记录
      // TODO: 智能调节时长，如果长时间没有未读消息，则拉取时间间隔逐渐加长
      setInterval(function () {
        bd_kfe_httpapi.loadMessagesUnread()
      }, 5000);
      // 首次拉取一次未读消息
      bd_kfe_httpapi.getMessagesUnread()
      // 建立长连接
      bd_kfe_stompapi.byteDeskConnect();
    }
    // 通知当前网址
    bd_kfe_httpapi.browse()
    // 订阅会话消息，处理断开重连的情况
    // 长链接成功之后，再subscribeTopic, 否则：InvalidStateError: The connection has not been established yet
    // if (
    //   bd_kfe_data.thread.tid !== null &&
    //   bd_kfe_data.thread.tid !== undefined &&
    //   bd_kfe_data.thread.tid !== ""
    // ) {
    //   bd_kfe_stompapi.subscribeTopic(bd_kfe_data.threadTopic());
    // }
  },
  /**
   * @api {get} /api/rate/do 满意度评价
   * @apiName rate
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} uId 管理员uid
   * @apiParam {String} wId 工作组唯一wid
   * @apiParam {String} type 区分工作组会话 'workGroup'、指定坐席会话 'appointed'
   * @apiParam {String} aId 指定客服uid, 只有当type === 'appointed'时有效
   * @apiParam {String} tid 会话tid
   * @apiParam {String} score 分数
   * @apiParam {String} note 备注
   * @apiParam {String} invite 是否邀请评价
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 满意度评价
   *
   * @apiUse ResponseResultSuccess
   */
  rate: function () {
    bd_kfe_data.rateContent = $("#suggestcontent").val();
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/rate/do",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      type: "post",
      data: JSON.stringify({
        tid: bd_kfe_data.thread.tid,
        score: bd_kfe_data.rateScore,
        note: bd_kfe_data.rateContent,
        invite: bd_kfe_data.isInviteRate,
        client: bd_kfe_data.client
      }),
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("rate: " + response.data);
        bd_kfe_data.isRated = true;
        //
        if (response.status_code === 200) {
          // alert("评价成功");
          $("#bytedesk_main").show();
          $("#bytedesk_rate").hide();
        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
        alert(error);
      }
    });
  },
  /**
   * @api {get} /api/thread/visitor/close 关闭当前窗口
   * @apiName closeWebPage
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} tid 会话tid
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 关闭当前窗口
   *
   * @apiUse ResponseResultSuccess
   */
  closeWebPage: function () {
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/thread/visitor/close",
      type: "post",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      data: JSON.stringify({
        tid: bd_kfe_data.thread.tid,
        client: bd_kfe_data.client
      }),
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("close thread: " + response.data);
        // 关闭当前窗口
        if (navigator.userAgent.indexOf("MSIE") > 0) {
          if (navigator.userAgent.indexOf("MSIE 6.0") > 0) {
            window.opener = null;
            window.close();
          } else {
            window.open("", "_top");
            window.top.close();
          }
        } else if (navigator.userAgent.indexOf("Firefox") > 0) {
          window.location.href = "about:blank ";
          window.opener = null;
          window.open("", "_self", "");
          window.close();
        } else {
          window.opener = null;
          window.open("", "_self", "");
          window.close();
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
        alert(error);
      }
    });
  },
  // 显示加载更多历史消息
  showLoadHistoryMessage() {
    if (bd_kfe_data.history !== "1") {
      return
    }
    $("#bytedesk_more").show();
  },
  /**
   * @api {get} /api/messages/user 加载更多聊天记录
   * @apiName loadHistoryMessages
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} wId 工作组唯一wid
   * @apiParam {String} type 区分工作组会话 'workGroup'、指定坐席会话 'appointed'
   * @apiParam {String} aId 指定客服uid, 只有当type === 'appointed'时有效
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 加载更多聊天记录
   * TODO: 访客端暂时不开放聊天记录
   *
   * @apiUse ResponseResultSuccess
   */
  loadHistoryMessages: function (isPrepend = false) {
    // 
    if (bd_kfe_data.history !== "1") {
      return
    }
    // 
    var loadingText = "加载中..."
    if (bd_kfe_data.lang === "en") {
      loadingText = "Loading..."
    }
    $("#bytedesk_more").text(loadingText)
    // 
    let uni_wid = bd_kfe_data.type === 'appointed' ? bd_kfe_data.agentUid : bd_kfe_data.workGroupWid;
    // 
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/messages/user/wid",
      type: "get",
      data: {
        wid: uni_wid,
        page: bd_kfe_data.page,
        size: 20,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog('loadHistoryMessages: ' + JSON.stringify(response.data));
        var loadingText = "加载更多消息"
        if (bd_kfe_data.lang === "en") {
          loadingText = "Loading More"
        }
        $("#bytedesk_more").text(loadingText)
        // 
        if (response.status_code === 200) {
          bd_kfe_data.page += 1;
          // 是否还有更多历史记录
          bd_kfe_data.hasMoreHistoryMessage = !response.data.last
          if (bd_kfe_data.hasMoreHistoryMessage) {
            $('#bytedesk_more').show()
          } else {
            $('#bytedesk_more').hide()
          }
          //
          var length = response.data.content.length
          for (var i = 0; i < length; i++) {
            var message = response.data.content[i];
            // if (message.type === 'notification_form_request' ||
            //   message.type === 'notification_form_result') {
            //   // 暂时忽略表单消息
            // }
            if (message.type === 'notification_thread_reentry') {
              // 连续的 ‘继续会话’ 消息，只显示最后一条
              if (i + 1 < length) {
                var nextmsg = response.data.content[i + 1];
                if (nextmsg.type === 'notification_thread_reentry') {
                  continue
                } else {
                  bd_kfe_utils.pushToMessageArray(message, true);
                }
              }
            } else {
              bd_kfe_utils.pushToMessageArray(message, true);
            }
          }
          // for (var i = length - 1; i >= 0 ; i--) {
          //   var message = response.data.content[i];
          //   if (message.type === 'notification_form_request' ||
          //     message.type === 'notification_form_result') {
          //     // TODO: 暂时忽略表单消息
          //   } else {
          //     bd_kfe_utils.pushToMessageArray(message, isPrepend);
          //   }
          // }
          // bd_kfe_utils.scrollToBottom();
        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
        $('#bytedesk_more').hide()
      }
    });
  },
  /**
   * @api {get} /api/answer/top 获取热门问题
   * @apiName getTopAnswers
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} uid 工作组唯一wid
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 获取热门问题
   *
   * @apiUse ResponseResultSuccess
   */
  getTopAnswers: function () {
    // 如果是预加载，则直接返回，不请求会话
    if (bd_kfe_data.preload === "1" || bd_kfe_data.column !== '2') {
      return
    }
    if (bd_kfe_data.type === 'workGroup') {
      // 如果是技能组，则请求技能组接口
      bd_kfe_httpapi.getTopAnswersWorkGroup()
      return
    }
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/answer/top",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        uid: bd_kfe_data.adminUid,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("fetch answers success:" + JSON.stringify(response.data));
        // 
        if (response.status_code === 200) {
          bd_kfe_data.answers = response.data;
          bd_kfe_utils.pushAnswers(bd_kfe_data.answers.content);
        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("fetch answers error:" + error);
      }
    });
  },
  // 根据技能组获取
  getTopAnswersWorkGroup: function () {
    // 如果是预加载，则直接返回，不请求会话
    if (bd_kfe_data.preload === "1" || bd_kfe_data.column !== '2') {
      return
    }
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/answer/top/workgroup",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        wid: bd_kfe_data.workGroupWid,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("getTopAnswersWorkGroup success:" + JSON.stringify(response.data));
        // 
        if (response.status_code === 200) {
          bd_kfe_data.answers = response.data;
          bd_kfe_utils.pushAnswers(bd_kfe_data.answers.content);
        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("getTopAnswersWorkGroup error:" + error);
      }
    });
  },
  // 请求机器人消息
  appendQueryMessage: function (content) {
    //
    var localId = bd_kfe_utils.guid();
    var message = {
      mid: localId,
      type: bd_kfe_data.MESSAGE_TYPE_ROBOT,
      content: content,
      imageUrl: content,
      createdAt: bd_kfe_utils.currentTimestamp(),
      localId: localId,
      status: bd_kfe_data.MESSAGE_STATUS_STORED,
      user: {
        uid: bd_kfe_data.my_uid(),
        username: bd_kfe_data.my_username(),
        nickname: bd_kfe_data.my_nickname(),
        avatar: bd_kfe_data.my_avatar()
      }
    };
    bd_kfe_utils.pushToMessageArray(message);
    //
  },
  // 机器人回复消息
  appendReplyMessage: function (aid, content) {
    //
    var localId = bd_kfe_utils.guid();
    var message = {
      mid: localId,
      type: bd_kfe_data.MESSAGE_TYPE_ROBOT,
      content: content,
      imageUrl: content,
      createdAt: bd_kfe_utils.currentTimestamp(),
      localId: localId,
      status: bd_kfe_data.MESSAGE_STATUS_STORED,
      answer: {
        aid: aid
      },
      answers: [],
      user: {
        uid: bd_kfe_data.thread.agent.uid,
        username: bd_kfe_data.thread.agent.username,
        nickname: bd_kfe_data.thread.agent.nickname,
        avatar: bd_kfe_data.thread.agent.avatar
      }
    };
    // bd_kfe_utils.pushToMessageArray(message);
    bd_kfe_utils.pushRightAnswerToMessageArray(message)

    return message.mid
  },
  /**
   * @api {get} /api/answer/query 根据问题qid请求智能问答答案
   * @apiName queryAnswer
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} uid 管理员uid
   * @apiParam {String} tid 会话tid
   * @apiParam {String} aid 问题aid
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 请求会话
   *
   * @apiUse ResponseResultSuccess
   */
  queryAnswer: function (aid, question, answer) {
    // bd_kfe_utils.printLog(question + ',' + answer)
    // 首先本地显示
    bd_kfe_httpapi.appendQueryMessage(decodeURIComponent(question))
    var mid = bd_kfe_httpapi.appendReplyMessage(aid, decodeURIComponent(answer))
    // 
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/v2/answer/query",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        tid: bd_kfe_data.thread.tid,
        aid: aid,
        mid: mid,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("query answer success:" + JSON.stringify(response.data));
        if (response.status_code === 200) {
          //
          // var queryMessage = response.data.query;
          // var replyMessage = response.data.reply;
          //
          // bd_kfe_utils.pushToMessageArray(queryMessage);
          // bd_kfe_utils.pushRightAnswerToMessageArray(replyMessage);
          //
          bd_kfe_utils.scrollToBottom();
        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("query answers error:" + error);
      }
    });
  },
  // 
  queryCategory: function (cid, name) {
    // console.log('category:', cid, name);
    // 首先本地显示
    bd_kfe_httpapi.appendQueryMessage(name)
    // 
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/api/v2/answer/category",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        tid: bd_kfe_data.thread.tid,
        cid: cid,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("queryCategory success:" + JSON.stringify(response));
        if (response.status_code === 200) {
          //
          let replyMessage = response.data.reply;
          //
          bd_kfe_utils.pushToMessageArray(replyMessage);
          bd_kfe_utils.scrollToBottom()
        } else {
          alert(response.message)
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("query categories error:" + error);
      }
    });
  },
  /**
   * @api {get} /api/answer/message 输入内容，请求智能答案
   * @apiName messageAnswer
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} uid 管理员uid
   * @apiParam {String} tid 会话tid
   * @apiParam {String} content 内容
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 输入内容，请求智能答案
   *
   * @apiUse ResponseResultSuccess
   */
  messageAnswer: function (content) {
    // 首先本地显示
    bd_kfe_httpapi.appendQueryMessage(content)

    // 包含’人工‘二字
    if (content.indexOf('人工') !== -1) {
      // 请求人工客服
      bd_kfe_httpapi.requestAgent()
      return;
    }
    // 自定义转人工关键词
    for (let i = 0; i < bd_kfe_data.transferWords.length; i++) {
      const transferword = bd_kfe_data.transferWords[i]
      if (transferword.type === 'contains') {
        // 包含
        if (content.indexOf(transferword.content) !== -1) {
          // 请求人工客服
          bd_kfe_httpapi.requestAgent()
          return
        }
      } else if (transferword.type === 'match') {
        // 完全匹配
        if (content === transferword.content) {
          // 请求人工客服
          bd_kfe_httpapi.requestAgent()
          return
        }
      }
    }
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        // "/api/v2/answer/message",
        "/api/elastic/robot/message",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        wid: bd_kfe_data.workGroupWid,
        content: content,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("message answer success:" + JSON.stringify(response));
        if (response.status_code === 200) {
          // 正确匹配到答案
          // var queryMessage = response.data.query;
          var replyMessage = response.data.reply;
          // 答案中添加 '有帮助'、'无帮助'，访客点击可反馈答案是否有用
          // bd_kfe_utils.pushToMessageArray(queryMessage);
          bd_kfe_utils.pushRightAnswerToMessageArray(replyMessage);
          bd_kfe_utils.scrollToBottom();
        } else if (response.status_code === 201) {
          // 未匹配到答案
          // var queryMessage = response.data.query;
          var replyMessage = response.data.reply;
          // bd_kfe_utils.pushToMessageArray(queryMessage);
          bd_kfe_utils.pushNoAnswerToMessageArray(replyMessage);
          bd_kfe_utils.scrollToBottom();
        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("query answers error:" + error);
      }
    });
  },
  // 输入联想
  previewAnswer: function (content) {
    // TODO: bug遮挡内容，正式环境暂时不调用
    if (bd_kfe_data.IS_PRODUCTION) {
      return
    }
    $('#bytedesk_faq_preview').html("");
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/elastic/robot/preview",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        wid: bd_kfe_data.workGroupWid,
        content: content,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        // bd_kfe_utils.printLog("preview answer success:" + JSON.stringify(response.data));
        $('#bytedesk_faq_preview').html("");
        // preview answer success:{"totalHits":1,"totalHitsRelation":"EQUAL_TO","maxScore":1.7009872,"scrollId":null,
        // "searchHits":[{"id":"211","score":1.7009872,"sortValues":[],
        // "content":{"id":211,"aid":"202206061702191","question":"问题1","answer":"答案1","similars":"","keywords":"","channels":"uniapp,flutter_android,flutter_ios,web_pc,web_h5,android,ios,wechat_mp,wechat_mini,wechat_kefu","orderNo":0,"workGroupWids":"201812200005351,201807171659201,201809061716221,201808101819291","uid":"201808221551193"},
        //  "highlightFields":{"question":["<span style=\"color:red;\">问题</span>1"]}}],"aggregations":null,"empty":false}
        if (response.data.totalHits > 0) {
          // 界面显示提示列表
          $('#bytedesk_faq_preview').show()
          //
          for (let i = 0; i < response.data.searchHits.length && i < 4; i++) {
            const element = response.data.searchHits[i];
            var question = element.content.question
            var answer = element.content.answer
            // console.log('question:', question, ' answer:', answer)
            // 界面显示提示列表
            var item = "<span class=\"bytedesk_faq_preview-item\" onclick=\"bd_kfe_utils.handleFaqPreviewItemClicked(\'" + encodeURIComponent(question) + "\',\'" + encodeURIComponent(answer) + "\')\" >" + question + "</span>";
            $("#bytedesk_faq_preview").append(item)
            // 
            // var highlightFields = element.highlightFields
            // if (highlightFields.question != undefined && highlightFields.question.length > 0) {
            //   for (let j = 0; j < highlightFields.question.length; j++) {
            //     const hignelement = highlightFields.question[j];
            //     console.log('hignelement:', hignelement)
            //     // TODO: 界面显示提示列表
            //   }
            // }
          }
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("query answers error:" + error);
      }
    });
  },
  // queryAnswer和messageAnswer的合体升级版
  robotQueryAnswer: function (aid, question) {
    // 插入本地
    var localId = bd_kfe_utils.guid();
    var message = {
      mid: localId,
      type: bd_kfe_data.MESSAGE_TYPE_TEXT,
      content: question,
      imageUrl: question,
      createdAt: bd_kfe_utils.currentTimestamp(),
      localId: localId,
      status: bd_kfe_data.MESSAGE_STATUS_STORED,
      // answers: [],
      user: {
        uid: bd_kfe_data.my_uid(),
        username: bd_kfe_data.my_username(),
        nickname: bd_kfe_data.my_nickname(),
        avatar: bd_kfe_data.my_avatar()
      }
    };
    bd_kfe_utils.pushToMessageArray(message);
    // 从服务器请求答案
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/robot/query",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        type: 'aid',
        mid: localId,
        tid: bd_kfe_data.thread.tid,
        aid: aid,
        question: question,
        uid: bd_kfe_data.adminUid,
        keyword: '',
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("robot query answer success:" + JSON.stringify(response.data));
        if (response.status_code === 200) {
          //
          // var replyMessage = response.data.reply;
          // bd_kfe_utils.pushRightAnswerToMessageArray(replyMessage);
          //
          bd_kfe_utils.scrollToBottom();
        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("query answers error:" + error);
      }
    });
  },
  // queryAnswer和messageAnswer的合体升级版
  robotMessageAnswer: function (keyword) {
    // 插入本地
    var localId = bd_kfe_utils.guid();
    var message = {
      mid: localId,
      type: bd_kfe_data.MESSAGE_TYPE_TEXT,
      content: keyword,
      imageUrl: keyword,
      createdAt: bd_kfe_utils.currentTimestamp(),
      localId: localId,
      status: bd_kfe_data.MESSAGE_STATUS_STORED,
      // answers: [],
      user: {
        uid: bd_kfe_data.my_uid(),
        username: bd_kfe_data.my_username(),
        nickname: bd_kfe_data.my_nickname(),
        avatar: bd_kfe_data.my_avatar()
      }
    };
    bd_kfe_utils.pushToMessageArray(message);
    // 从服务器请求答案
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/robot/query",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        type: 'keyword',
        mid: localId,
        tid: bd_kfe_data.thread.tid,
        aid: '',
        question: '',
        uid: bd_kfe_data.adminUid,
        keyword: keyword,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("robot msg answer success:" + JSON.stringify(response.data));
        if (response.status_code === 200) {
          //
          // var replyMessage = response.data.reply;
          // bd_kfe_utils.pushRightAnswerToMessageArray(replyMessage);
          //
          bd_kfe_utils.scrollToBottom();
        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("query answers error:" + error);
      }
    });
  },
  /**
   * @api {post} /api/answer/rate 评价智能问答结果(TODO，未上线)
   * @apiName rateAnswer
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} aId 指定客服uid, 只有当type === 'appointed'时有效
   * @apiParam {String} rate 是否有用: true or false
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 评价智能问答结果，是否有用
   *
   * @apiUse ResponseResultSuccess
   */
  rateAnswer: function (aid, mid, rate) {
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/answer/rate",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      type: "post",
      data: JSON.stringify({
        aid: aid + '', // 转换为字符串
        mid: mid + '', // 转换为字符串
        rate: rate,
        client: bd_kfe_data.client
      }),
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("success:" + JSON.stringify(response.data));
        if (response.status_code === 200) {
          //
          var message = response.data;
          bd_kfe_utils.pushToMessageArray(message);
          // TODO: 评价之后，toggle修改界面，让用户看得出来评价状态
        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("query answers error:" + JSON.stringify(error));
      }
    });
  },
  /**
   * 
   */
  sendMobile: function () {
    var mobile = $.trim($("#bytedesk_input-mobile").val());
    if (mobile.length !== 11) {
      alert('请正确天下手机号');
      return;
    }
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/crm/collect",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      type: "post",
      data: JSON.stringify({
        adminUid: bd_kfe_data.adminUid,
        visitorUid: bd_kfe_data.uid,
        threadTid: bd_kfe_data.thread.tid,
        name: bd_kfe_data.nickname, // TODO: 弹窗收集称呼
        mobile: mobile,
        client: bd_kfe_data.client
      }),
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("success:" + response.data);
        if (response.status_code === 200) {
          //
          // var message = response.data;
          // bd_kfe_utils.pushToMessageArray(message);
          var localId = bd_kfe_utils.guid();
          var message = {
            mid: localId,
            type: 'notification',
            content: '呼叫成功，我们会尽快联系您',
            createdAt: bd_kfe_utils.currentTimestamp(),
            localId: localId,
            status: 'stored',
            user: {
              uid: '',
              username: bd_kfe_data.username,
              nickname: bd_kfe_data.nickname,
              avatar: bd_kfe_data.avatar
            }
          };
          bd_kfe_utils.pushToMessageArray(message);

        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("query answers error:" + error);
      }
    });
  },
  /**
   * @api {post} /api/leavemsg/save 留言
   * @apiName leaveMessage
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} wId 工作组唯一wid
   * @apiParam {String} aId 指定客服uid, 只有当type === 'appointed'时有效 
   * @apiParam {String} type 区分工作组会话 'workGroup'、指定坐席会话 'appointed'
   * @apiParam {String} mobile 手机
   * @apiParam {String} email 邮箱
   * @apiParam {String} content 留言内容
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 留言
   *
   * @apiUse ResponseResultSuccess
   */
  leaveMessage: function () {
    var mobile = $("#bytedesk_leavemsg_mobile").val();
    // var email = $("#leavemsgemail").val();
    var content = $("#bytedesk_leavemsg_content").val();
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/leavemsg/save",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      type: "post",
      data: JSON.stringify({
        // uid: bd_kfe_data.adminUid,
        wid: bd_kfe_data.workGroupWid,
        aid: bd_kfe_data.agentUid,
        type: bd_kfe_data.type,
        mobile: mobile,
        email: '',
        content: content,
        client: bd_kfe_data.client
      }),
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("leave message: " + response.data);
        if (response.status_code === 200) {
          var langText = "留言成功"
          if (bd_kfe_data.lang === "en") {
            langText = "leaveMessage success"
          }
          alert(langText);
          // $("#bytedesk_chat").show();
          // $("#bytedesk_leave").hide();
          // $("#bytedesk_main").show();
        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
        var langText = "留言失败"
        if (bd_kfe_data.lang === "en") {
          langText = "leaveMessage failed"
        }
        alert(langText);
      }
    });
  },
  /**
   * @api {get} /api/status/workGroup 获取工作组当前在线状态
   * @apiName getWorkGroupStatus
   * @apiGroup WorkGroup
   * @apiVersion 1.5.6
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} wid 工作组唯一wid
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 只要工作组内至少有一个客服在线，则返回为online，否则为offline
   *
   * @apiUse ResponseResultSuccess
   */
  getWorkGroupStatus: function (wid) {
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/status/workGroup",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        wid: wid,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        // get workGroup status success:{"wid":"201807171659201","status":"online"}
        // bd_kfe_utils.printLog("get workGroup status success:" + JSON.stringify(response.data));
        if (response.status_code === 200) {
          var status = response.data.status;
          // status:online
          window.parent.postMessage({ msg: 'bytedesk-status', type: 'workGroup', uuid: wid, status: status }, '*')
          bd_kfe_utils.printLog('status:' + status)
          // 
        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("get workGroup status error:" + error);
      }
    });
  },
  /**
   * @api {get} /api/status/agent 获取用户当前在线状态
   * @apiName getUserStatus
   * @apiGroup User
   * @apiVersion 1.5.6
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} uid 用户唯一uid
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 在线返回为online，否则为offline
   *
   * @apiUse ResponseResultSuccess
   */
  getUserStatus: function (uid) {
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/status/agent",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        uid: uid,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        // get user status success:{"uid":"201808221551193","status":"online"}
        // bd_kfe_utils.printLog("get user status success:" + JSON.stringify(response.data));
        //
        if (response.status_code === 200) {
          var status = response.data.status;
          // status:online
          window.parent.postMessage({ msg: 'bytedesk-status', type: 'agent', uuid: uid, status: status }, '*')
          bd_kfe_utils.printLog('status:' + status);

        } else {
          alert(response.message);
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("get user status error:" + error);
      }
    });
  },
  /**
   * @api {get} /api/messages/unreadCoun 获取未读消息数目
   * @apiName getUnreadCount
   * @apiGroup User
   * @apiVersion 1.5.6
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} wid 技能组或指定客服唯一id
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 数字
   *
   * @apiUse ResponseResultSuccess
   */
  getUnreadCount: function (wid) {
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/messages/unreadCount",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        wid: wid,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("get unread count success:" + response.data);
      },
      error: function (error) {
        bd_kfe_utils.printLog("get unreadCount error:" + error);
      }
    });
  },
  /**
   * @api {get} /api/messages/unreadCoun/visitor 访客端-查询访客所有未读消息数目
   * @apiName getUnreadCountVisitor
   * @apiGroup User
   * @apiVersion 1.5.6
   * @apiPermission afterLogin
   * 
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} client 固定写死为 'web'
   * 
   * @apiDescription 数字
   *
   * @apiUse ResponseResultSuccess
   */
  getUnreadCountVisitor: function () {
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/messages/unreadCount/visitor",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog("get unread count visitor success:", response.data);
      },
      error: function (error) {
        bd_kfe_utils.printLog("get unreadCount visitor error:", error);
      }
    });
  },
  /**
   * 技能组设置
   */
  getPrechatSettings: function () {
    //
    if (bd_kfe_data.type !== 'workGroup') {
      return
    }
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/visitor/api/prechat/settings",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        wid: bd_kfe_data.workGroupWid,
        client: bd_kfe_data.client
      },
      success: function (response) {
        // bd_kfe_utils.printLog("fetch pre setting success:" + JSON.stringify(response.data));
        console.log("fetch pre setting success:", response.data)
        if (response.status_code === 200) {
          var workGroup = response.data
          bd_kfe_httpapi.dealWithWorkGroup(workGroup)
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("fetch pre setting error:", error);
      }
    });
  },
  dealWithWorkGroup: function (workGroup) {
    console.log('dealWithWorkGroup:', workGroup)
    if (workGroup.wid === null) {
      return
    }
    // 如果是预加载，则直接返回，不请求会话
    if (bd_kfe_data.preload === "1") {
      // 是否自动弹窗
      if (workGroup.autoPop) {
        // 触发点击icon动作
        window.parent.postMessage("bytedesk-popup", '*');
      }
      return
    }
    bd_kfe_data.workGroupNickname = workGroup.nickname;
    bd_kfe_data.workGroupAvatar = workGroup.avatar;
    bd_kfe_data.workGroupDescription = workGroup.description;
    // 是否自动弹窗
    if (workGroup.autoPop) {
      // 触发点击icon动作
      window.parent.postMessage("bytedesk-popup", '*');
    }
    // 显示置顶语
    if (workGroup.showTopTip) {
      $('#bytedesk_toptip').html(workGroup.topTip)
    }
    // 显示手机号输入框
    if (workGroup.showCollectMobile) {
      $("#bytedesk_message-input").show()
    }
    // 询前表单
    if (workGroup.showForm) {
      // TODO: 后台自定义显示字段
      $("#bytedesk_form-name-div").show();
      $("#bytedesk_form-mobile-div").show();
      bd_kfe_utils.switchForm()
    }
    // 隐藏logo
    if (workGroup.hideLogo) {
      $("#bytedesk_logo").hide()
    }
    // 显示右侧栏，TODO: 右侧参数带去掉
    if (workGroup.showRightColumn && (bd_kfe_data.column === "2")) {
      // console.log('showRightColumn')
      bd_kfe_utils.showRightColumn(workGroup.workTabs)
    } else if (bd_kfe_data.column === "2") {
      console.log('默认显示faq-2')
      $("#bytedesk_right").show();
      $("#bytedesk_right_tab").append("<button class='bytedesk_right_tablinks'>常见问题</button>")
      $("#bytedesk_right_tabcontent").append("<div id='faq' class='bytedesk_right_tabcontent'>" +
        "<div id=\"bytedesk_question\"></div>" +
        "</div >")
      bd_kfe_httpapi.getTopAnswers();
      //
      bd_kfe_utils.rightTabInit()
    }
  },
  /**
   * 拉取技能组-快捷按钮
   * FIXME: 显示遮挡对话内容
   */
  getQuickButtons: function () {
    // 如果是预加载，则直接返回，不请求会话
    if (bd_kfe_data.preload === "1") {
      return
    }
    // 目前仅支持技能组
    if (bd_kfe_data.type !== 'workGroup') {
      return
    }
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/api/quickbutton/query/workGroup",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        wid: bd_kfe_data.workGroupWid,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        // bd_kfe_utils.printLog("getQuickButtons success:" + JSON.stringify(response.data));
        //
        if (response.data.length > 0) {
          $('#bytedesk_quick_question').show()
          $(".footer").css("height", "135px");
        }
        for (let i = 0; i < response.data.length && i < 4; i++) {
          const element = response.data[i];
          var item = "<span class=\"bytedesk_quick_question-item\" onclick=\"bd_kfe_utils.handleQuickbuttonClick(\'" + element.type + "\',\'" + element.title + "\',\'" + element.content + "\')\" >" + element.title + "</span>";
          $("#bytedesk_quick_question").append(item)
        }
      },
      error: function (error) {
        bd_kfe_utils.printLog("getQuickButtons error:", error);
      }
    });
  },
  /**
   * 拉取技能组-转人工关键词
   */
  getTransferWords: function () {
    // 如果是预加载，则直接返回，不请求会话
    if (bd_kfe_data.preload === "1") {
      return
    }
    // 目前仅支持技能组
    if (bd_kfe_data.type !== 'workGroup') {
      return
    }
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/api/transferword/query/workGroup",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        wid: bd_kfe_data.workGroupWid,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        console.log('getTransferWords:', response)
        bd_kfe_data.transferWords = response.data
        //
      },
      error: function (error) {
        bd_kfe_utils.printLog("getQuickButtons error:", error);
      }
    });
  },
  //  根据topic拉取历史消息
  loadHistoryMessagesByTopic: function (topic) {
    // 如果是预加载，则直接返回，不请求会话
    if (bd_kfe_data.preload === "1") {
      return
    }
    //
    // if (this.isRequestAgent || this.isManulRequestThread || this.loadHistory === '0') {
    //   return;
    // }
    $.ajax({
      url: bd_kfe_data.HTTP_HOST +
        "/api/messages/topic",
      type: "get",
      data: {
        topic: topic,
        page: bd_kfe_data.page,
        size: 10,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog('loadHistoryMessagesByTopic: ' + JSON.stringify(response));
        //
        if (response.status_code === 200) {
          for (let i = 0; i < response.data.content.length; i++) {
            const message = response.data.content[i]
            // bd_kfe_utils.printLog('message:', message);
            // bd_kfe_utils.pushToMessageArray(message, true)
            if (message.type === 'notification_form_request' ||
              message.type === 'notification_form_result') {
              // 暂时忽略表单消息
            } if (message.type === 'notification_thread_reentry') {
              // 连续的 ‘继续会话’ 消息，只显示最后一条
              if (i + 1 < length) {
                var nextmsg = response.data.content[i + 1];
                if (nextmsg.type === 'notification_thread_reentry') {
                  continue
                } else {
                  bd_kfe_utils.pushToMessageArray(message, true)
                }
              }
            } else {
              bd_kfe_utils.pushToMessageArray(message, true)
            }
          }
        }
        bd_kfe_utils.scrollToBottom()
        // app.$previewRefresh()
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
      }
    });
  },
  // 加载最新10条消息，用于定时拉取最新消息
  loadLatestMessage: function () {
    // 如果是预加载，则直接返回，不请求会话
    if (bd_kfe_data.preload === "1") {
      return
    }
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/api/messages/topic",
      type: "get",
      data: {
        topic: bd_kfe_data.thread.topic,
        page: 0,
        size: 10,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog('loadLatestMessage: ' + JSON.stringify(response));
        //
        if (response.status_code === 200) {
          for (let i = 0; i < response.data.content.length; i++) {
            const message = response.data.content[i]
            // bd_kfe_utils.printLog('message:', message);
            // bd_kfe_utils.pushToMessageArray(message, true)
            if (message.type === 'notification_form_request' ||
              message.type === 'notification_form_result') {
              // 暂时忽略表单消息
            } if (message.type === 'notification_thread_reentry') {
              // 连续的 ‘继续会话’ 消息，只显示最后一条
              if (i + 1 < length) {
                var nextmsg = response.data.content[i + 1];
                if (nextmsg.type === 'notification_thread_reentry') {
                  continue
                } else {
                  bd_kfe_utils.pushToMessageArray(message, true)
                }
              }
            } else {
              bd_kfe_utils.pushToMessageArray(message, true)
            }
          }
        }
        bd_kfe_utils.scrollToBottom()
        // app.$previewRefresh()
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
      }
    });
  },
  // 拉取未读消息
  loadMessagesUnread: function () {
    // 如果长链接正常，则直接返回
    if (bd_kfe_data.isConnected) {
      return
    }
    // 如果是机器人，则直接返回
    if (bd_kfe_data.isRobot) {
      return
    }
    //
    bd_kfe_httpapi.getMessagesUnread();
  },
  // 
  getMessagesUnread: function () {
    // 如果是预加载，则直接返回，不请求会话
    if (bd_kfe_data.preload === "1") {
      return
    }
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/api/messages/unread/message/visitor/schedule",
      type: "get",
      data: {
        page: 0,
        size: 10,
        client: bd_kfe_data.client
      },
      beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
      },
      success: function (response) {
        bd_kfe_utils.printLog('loadMessagesUnread: ' + JSON.stringify(response));
        //
        if (response.status_code === 200) {
          for (let i = 0; i < response.data.content.length; i++) {
            const message = response.data.content[i]
            // bd_kfe_utils.printLog('message:', message);
            // bd_kfe_utils.pushToMessageArray(message, true)
            if (message.type === 'notification_form_request' ||
              message.type === 'notification_form_result') {
              // 暂时忽略表单消息
            } if (message.type === 'notification_thread_reentry') {
              // 连续的 ‘继续会话’ 消息，只显示最后一条
              if (i + 1 < length) {
                var nextmsg = response.data.content[i + 1];
                if (nextmsg.type === 'notification_thread_reentry') {
                  continue
                } else {
                  bd_kfe_utils.pushToMessageArray(message, true)
                }
              }
            } else {
              bd_kfe_utils.pushToMessageArray(message, true)
            }
          }
        }
        // bd_kfe_utils.scrollToBottom()
      },
      error: function (error) {
        bd_kfe_utils.printLog(error);
      }
    });
  },

  // 动态从服务器加载域名
  getBaseUrl: function () {
    if (!bd_kfe_data.IS_PRODUCTION) {
      // 测试环境返回
      return
    }
    //
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/visitor/api/wechatUrl",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        client: 'web'
      },
      success: function (response) {
        console.log('get base url:', response);
        bd_kfe_data.CHAT_URL = 'https://' + response.data.url
      },
      error: function (error) {
        console.log("get base url error:", error);
      }
    });
  },
  //
  getShortUrl: function (url, successCallback, failedCallback) {
    // 
    $.ajax({
      url: bd_kfe_data.HTTP_HOST + "/url/to/short",
      contentType: "application/json; charset=utf-8",
      type: "get",
      data: {
        url: url,
        uid: bd_kfe_data.adminUid,
        client: 'web'
      },
      success: function (response) {
        console.log("get short url success:", response.data);
        if (!bd_kfe_data.IS_PRODUCTION) {
          bd_kfe_data.shortCode = bd_kfe_data.HTTP_HOST + "/chat/" + response.data
        } else {
          bd_kfe_data.shortCode = "http://url.chainsnow.com/chat/" + response.data
        }
        successCallback(bd_kfe_data.shortCode)
      },
      error: function (error) {
        console.log("get short url error:", error);
        failedCallback(error);
      }
    });
  },
  /**
   * @api {get} /api/answer/init 请求机器人问答
   * @apiName requestRobot
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission afterLogin
   *
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} wId 工作组唯一wid
   * @apiParam {String} type 区分工作组会话 'workGroup'、指定坐席会话 'appointed'
   * @apiParam {String} aId 指定客服uid, 只有当type === 'appointed'时有效
   * @apiParam {String} client 固定写死为 'web'
   *
   * @apiDescription 加载常见问题
   *
   * @apiUse ResponseResultSuccess
   */
  // requestRobot: function () {
  //   // bd_kfe_utils.printLog("自助答疑");
  //   bd_kfe_httpapi.initAnswer();
  // },
  /**
 * @api {get} /api/answer/init 加载常见问题
 * @apiName initAnswer
 * @apiGroup User
 * @apiVersion 1.4.7
 * @apiPermission afterLogin
 * 
 * @apiParam {String} access_token 访问令牌
 * @apiParam {String} wId 工作组唯一wid
 * @apiParam {String} type 区分工作组会话 'workGroup'、指定坐席会话 'appointed'
 * @apiParam {String} aId 指定客服uid, 只有当type === 'appointed'时有效
 * @apiParam {String} client 固定写死为 'web'
 * 
 * @apiDescription 加载常见问题
 *
 * @apiUse ResponseResultSuccess
 */
  // initAnswer: function () {
  //   $.ajax({
  //     url: bd_kfe_data.HTTP_HOST +
  //     "/api/answer/init",
  //     type: "get",
  //     data: {
  //       uid: bd_kfe_data.adminUid,
  //       tid: bd_kfe_data.thread.tid,
  //       client: bd_kfe_data.client
  //     },
  //     beforeSend: function (xhr) {
  //       xhr.setRequestHeader('Authorization', 'Bearer ' + bd_kfe_data.passport.token.access_token);
  //     },
  //     success:function(response){
  //       bd_kfe_utils.printLog("init answer success:", response.data);
  //       if (response.status_code === 200) {
  //         //
  //         var queryMessage = response.data;
  //         //
  //         bd_kfe_utils.pushToMessageArray(queryMessage);
  //         bd_kfe_utils.scrollToBottom();
  //       } else {
  //         alert(response.message);
  //       }
  //     },
  //     error: function(error) {
  //       bd_kfe_utils.printLog("query answers error:", error);
  //     }
  //   });
  // },
  /**
   * @api {get} /api/thread/questionnaire 选择问卷答案
   * @apiName chooseQuestionnaire
   * @apiGroup User
   * @apiVersion 1.4.7
   * @apiPermission afterLogin
   *
   * @apiParam {String} access_token 访问令牌
   * @apiParam {String} tId 会话唯一tid
   * @apiParam {String} itemQid 选择qid
   * @apiParam {String} client 固定写死为 'web'
   *
   * @apiDescription 选择问卷答案
   *
   * @apiUse ResponseResultSuccess
   */
  // chooseQuestionnaire: function (itemQid) {
  //   bd_kfe_utils.printLog("choose questionnaire: " + itemQid);
  //   // 留学: 意向国家 qid = '201810061551181'
  //   // 移民：意向国家 qid = '201810061551183'
  //   // 语培：意向类别 qid = '201810061551182'
  //   // 其他：意向类别 qid = '201810061551184'
  //   // 院校：意向院校 qid = '201810061551185'
  //   //
  //   var workGroups = [];
  //   for (var i = 0; i < bd_kfe_data.questionnaireItemItems.length; i++) {
  //       var item = bd_kfe_data.questionnaireItemItems[i];
  //       if (item.qid == itemQid) {
  //         bd_kfe_utils.printLog('qid:' + item.qid + ' == ' + itemQid);
  //         workGroups = item.workGroups;
  //         break;
  //       }
  //   }
  //   //
  //   var message =  {
  //       mid: bd_kfe_utils.guid(),
  //       type: 'workGroup',
  //       content: '选择工作组',
  //       workGroups: workGroups,
  //       createdAt: bd_kfe_utils.currentTimestamp(),
  //       status: 'stored',
  //       user: {
  //           uid: 'uid',
  //           username: '系统用户',
  //           nickname: '系统通知',
  //           avatar: 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/admin_default_avatar.png',
  //           visitor: false
  //       }
  //   };
  //   bd_kfe_utils.pushToMessageArray(message);
  // },
  //
  // var json = {
  //   "mid": mid,
  //   "timestamp": this.currentTimestamp(),
  //   "client": this.client,
  //   "version": "1",
  //   "type": 'robot_result',
  //   "user": {
  //     "uid": this.robotUser.uid,
  //     "nickname": this.robotUser.nickname,
  //     "avatar": this.robotUser.avatar,
  //     "extra": {
  //       "agent": true
  //     }
  //   },
  //   "text": {
  //     "content": content
  //   },
  //   "answer": {
  //     "aid": aid
  //   },
  //   "answers": [],
  //   "thread": {
  //     "tid": this.thread.tid,
  //     "type": this.thread.type,
  //     "content": content,
  //     "nickname": this.thread_nickname(),
  //     "avatar": this.thread.visitor.avatar,
  //     "topic": this.threadTopic,
  //     "client": this.client,
  //     "timestamp": this.currentTimestamp(),
  //     "unreadCount": 0
  //   }
  // };
  // 插入本地
  // this.onMessageReceived(json)


};

