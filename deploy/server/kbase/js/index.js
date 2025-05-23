/**
 * 
 */
$(document).ready(function () {
  //
  data.page = "index";
  
  // ie Ajax cross domain requests
  $.support.cors = true;
  // 使ie支持startsWith
  if (!String.prototype.startsWith) {
    String.prototype.startsWith = function(searchString, position) {
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
  //
  // layui.use(['layer', 'form', 'upload'], function(){
  //   var layer = layui.layer;
  //   var form = layui.form;
  //   var upload = layui.upload;
  // });
  NProgress.start();
  // 初始化
  support.created();
  support.mounted();
  //
  // $("#supportAdminUid").val(data.adminUid);
  // $("#supportSearchForm").attr("action", "search");

});