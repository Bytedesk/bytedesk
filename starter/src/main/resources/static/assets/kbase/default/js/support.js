/**
 * 
 */
var support = {
  /**
   * 
   */
  created: function() {
    // 单独窗口形式
    data.adminUid = utils.getUrlParam("uid") == null ? "201808221551193" : utils.getUrlParam("uid");
    //
    data.uid = localStorage.uid;
    data.username = localStorage.username;
    data.password = localStorage.password;
    if (data.password === undefined || data.password === null) {
      data.password = data.username;
    }
    var tokenLocal = localStorage.getItem(data.token);
    if (tokenLocal != null) {
      data.passport.token = JSON.parse(tokenLocal);
    }
    // TODO: 获取浏览器信息，提交给服务器
    // console.log("adminUid: " + data.adminUid);
  },
  /**
   *
   */
  mounted: function() {
    // console.log("mount");
    if (
      data.passport.token.access_token !== null &&
      data.passport.token.access_token !== undefined &&
      data.passport.token.access_token !== ""
    ) {
      //
      httpapi.login();
    } else if (
      data.username !== null &&
      data.username !== undefined &&
      data.username !== ""
    ) {
      if (data.username === utils.getUrlParam("username")) {
        // 非第一次使用自定义用户名
        httpapi.login();
      } else {
        // 保存自定义用户名到服务器
        // console.log("save username 1");
        httpapi.requestUsername();
      }
    } else if (utils.getUrlParam("username")) {
      // 保存自定义用户名到服务器
      // console.log("save username 2");
      httpapi.requestUsername();
    } else {
      httpapi.requestUsername();
    }
  }
};