"use strict";(self.webpackChunkadmin=self.webpackChunkadmin||[]).push([[9290],{26978:function(e,n,r){r.d(n,{Z:function(){return c}});var a=r(11757),t=r(44194),s={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M832 464h-68V240c0-70.7-57.3-128-128-128H388c-70.7 0-128 57.3-128 128v224h-68c-17.7 0-32 14.3-32 32v384c0 17.7 14.3 32 32 32h640c17.7 0 32-14.3 32-32V496c0-17.7-14.3-32-32-32zM332 240c0-30.9 25.1-56 56-56h248c30.9 0 56 25.1 56 56v224H332V240zm460 600H232V536h560v304zM484 701v53c0 4.4 3.6 8 8 8h40c4.4 0 8-3.6 8-8v-53a48.01 48.01 0 10-56 0z"}}]},name:"lock",theme:"outlined"},o=r(54183),i=function(e,n){return t.createElement(o.Z,(0,a.Z)({},e,{ref:n,icon:s}))};var c=t.forwardRef(i)},40795:function(e,n,r){r.d(n,{Z:function(){return c}});var a=r(11757),t=r(44194),s={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M744 62H280c-35.3 0-64 28.7-64 64v768c0 35.3 28.7 64 64 64h464c35.3 0 64-28.7 64-64V126c0-35.3-28.7-64-64-64zm-8 824H288V134h448v752zM472 784a40 40 0 1080 0 40 40 0 10-80 0z"}}]},name:"mobile",theme:"outlined"},o=r(54183),i=function(e,n){return t.createElement(o.Z,(0,a.Z)({},e,{ref:n,icon:s}))};var c=t.forwardRef(i)},58087:function(e,n,r){var a=r(11757),t=r(44194),s=r(15323),o=r(54183),i=function(e,n){return t.createElement(o.Z,(0,a.Z)({},e,{ref:n,icon:s.Z}))},c=t.forwardRef(i);n.Z=c},59908:function(e,n,r){r.d(n,{Z:function(){return c}});var a=r(11757),t=r(44194),s={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M400 317.7h73.9V656c0 4.4 3.6 8 8 8h60c4.4 0 8-3.6 8-8V317.7H624c6.7 0 10.4-7.7 6.3-12.9L518.3 163a8 8 0 00-12.6 0l-112 141.7c-4.1 5.3-.4 13 6.3 13zM878 626h-60c-4.4 0-8 3.6-8 8v154H214V634c0-4.4-3.6-8-8-8h-60c-4.4 0-8 3.6-8 8v198c0 17.7 14.3 32 32 32h684c17.7 0 32-14.3 32-32V634c0-4.4-3.6-8-8-8z"}}]},name:"upload",theme:"outlined"},o=r(54183),i=function(e,n){return t.createElement(o.Z,(0,a.Z)({},e,{ref:n,icon:s}))};var c=t.forwardRef(i)},59129:function(e,n,r){var a=r(17082),t=r(72298),s=r(48921),o=r(73457),i=r(42601),c=r(16309),l=r(52288),u=r(75365),d=r(44194),f=r(69088),p=r(31549),g=["rules","name","phoneName","fieldProps","onTiming","captchaTextRender","captchaProps"],m=d.forwardRef((function(e,n){var r=c.Z.useFormInstance(),f=(0,d.useState)(e.countDown||60),m=(0,i.Z)(f,2),h=m[0],v=m[1],x=(0,d.useState)(!1),w=(0,i.Z)(x,2),Z=w[0],b=w[1],y=(0,d.useState)(),M=(0,i.Z)(y,2),k=M[0],j=M[1],P=(e.rules,e.name,e.phoneName),C=e.fieldProps,F=e.onTiming,S=e.captchaTextRender,T=void 0===S?function(e,n){return e?"".concat(n," 秒后重新获取"):"获取验证码"}:S,U=e.captchaProps,I=(0,o.Z)(e,g),V=function(){var e=(0,s.Z)((0,t.Z)().mark((function e(n){return(0,t.Z)().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.prev=0,j(!0),e.next=4,I.onGetCaptcha(n);case 4:j(!1),b(!0),e.next=13;break;case 8:e.prev=8,e.t0=e.catch(0),b(!1),j(!1),console.log(e.t0);case 13:case"end":return e.stop()}}),e,null,[[0,8]])})));return function(n){return e.apply(this,arguments)}}();return(0,d.useImperativeHandle)(n,(function(){return{startTiming:function(){return b(!0)},endTiming:function(){return b(!1)}}})),(0,d.useEffect)((function(){var n=0,r=e.countDown;return Z&&(n=window.setInterval((function(){v((function(e){return e<=1?(b(!1),clearInterval(n),r||60):e-1}))}),1e3)),function(){return clearInterval(n)}}),[Z]),(0,d.useEffect)((function(){F&&F(h)}),[h,F]),(0,p.jsxs)("div",{style:(0,a.Z)((0,a.Z)({},null==C?void 0:C.style),{},{display:"flex",alignItems:"center"}),ref:n,children:[(0,p.jsx)(l.Z,(0,a.Z)((0,a.Z)({},C),{},{style:(0,a.Z)({flex:1,transition:"width .3s",marginRight:8},null==C?void 0:C.style)})),(0,p.jsx)(u.ZP,(0,a.Z)((0,a.Z)({style:{display:"block"},disabled:Z,loading:k},U),{},{onClick:(0,s.Z)((0,t.Z)().mark((function e(){var n;return(0,t.Z)().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(e.prev=0,!P){e.next=9;break}return e.next=4,r.validateFields([P].flat(1));case 4:return n=r.getFieldValue([P].flat(1)),e.next=7,V(n);case 7:e.next=11;break;case 9:return e.next=11,V("");case 11:e.next=16;break;case 13:e.prev=13,e.t0=e.catch(0),console.log(e.t0);case 16:case"end":return e.stop()}}),e,null,[[0,13]])}))),children:T(Z,h)}))]})})),h=(0,f.G)(m);n.Z=h},4499:function(e,n,r){var a=r(42601),t=r(17082),s=r(73457),o=r(46015),i=r(16309),c=r(98036),l=r(36490),u=r(44194),d=r(35175),f=r(31549),p=["fieldProps","proFieldProps"],g=["fieldProps","proFieldProps"],m="text",h=function(e){var n=(0,o.Z)(e.open||!1,{value:e.open,onChange:e.onOpenChange}),r=(0,a.Z)(n,2),s=r[0],l=r[1];return(0,f.jsx)(i.Z.Item,{shouldUpdate:!0,noStyle:!0,children:function(n){var r,a=n.getFieldValue(e.name||[]);return(0,f.jsx)(c.Z,(0,t.Z)((0,t.Z)({getPopupContainer:function(e){return e&&e.parentNode?e.parentNode:e},onOpenChange:function(e){return l(e)},content:(0,f.jsxs)("div",{style:{padding:"4px 0"},children:[null===(r=e.statusRender)||void 0===r?void 0:r.call(e,a),e.strengthText?(0,f.jsx)("div",{style:{marginTop:10},children:(0,f.jsx)("span",{children:e.strengthText})}):null]}),overlayStyle:{width:240},placement:"rightTop"},e.popoverProps),{},{open:s,children:e.children}))}})},v=function(e){var n=e.fieldProps,r=e.proFieldProps,a=(0,s.Z)(e,p);return(0,f.jsx)(d.Z,(0,t.Z)({valueType:m,fieldProps:n,filedConfig:{valueType:m},proFieldProps:r},a))};v.Password=function(e){var n=e.fieldProps,r=e.proFieldProps,o=(0,s.Z)(e,g),i=(0,u.useState)(!1),c=(0,a.Z)(i,2),p=c[0],v=c[1];return null!=n&&n.statusRender&&o.name?(0,f.jsx)(h,{name:o.name,statusRender:null==n?void 0:n.statusRender,popoverProps:null==n?void 0:n.popoverProps,strengthText:null==n?void 0:n.strengthText,open:p,onOpenChange:v,children:(0,f.jsx)("div",{children:(0,f.jsx)(d.Z,(0,t.Z)({valueType:"password",fieldProps:(0,t.Z)((0,t.Z)({},(0,l.Z)(n,["statusRender","popoverProps","strengthText"])),{},{onBlur:function(e){var r;null==n||null===(r=n.onBlur)||void 0===r||r.call(n,e),v(!1)},onClick:function(e){var r;null==n||null===(r=n.onClick)||void 0===r||r.call(n,e),v(!0)}}),proFieldProps:r,filedConfig:{valueType:m}},o))})}):(0,f.jsx)(d.Z,(0,t.Z)({valueType:"password",fieldProps:n,proFieldProps:r,filedConfig:{valueType:m}},o))},v.displayName="ProFormComponent",n.Z=v},32350:function(e,n,r){var a=r(17082),t=r(73457),s=r(44194),o=r(35175),i=r(31549),c=["fieldProps","proFieldProps"],l=function(e,n){var r=e.fieldProps,s=e.proFieldProps,l=(0,t.Z)(e,c);return(0,i.jsx)(o.Z,(0,a.Z)({ref:n,valueType:"textarea",fieldProps:r,proFieldProps:s},l))};n.Z=s.forwardRef(l)},88678:function(e,n,r){r.d(n,{Z:function(){return w}});var a=r(90819),t=r.n(a),s=r(89933),o=r.n(s),i=r(45332),c=r.n(i),l=r(55735),u=r(41159);function d(){return f.apply(this,arguments)}function f(){return(f=o()(t()().mark((function e(){return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,u.request)("/kaptcha/api/v1/get",{method:"GET",params:{client:l.bVn}}));case 1:case"end":return e.stop()}}),e)})))).apply(this,arguments)}function p(e,n){return g.apply(this,arguments)}function g(){return(g=o()(t()().mark((function e(n,r){return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,u.request)("/kaptcha/api/v1/check",{method:"POST",data:{captchaUid:n,captchaCode:r,client:l.bVn}}));case 1:case"end":return e.stop()}}),e)})))).apply(this,arguments)}var m=r(58087),h=r(52288),v=r(44194),x=r(31549),w=function(e){var n=e.onKaptchaChange,r=e.onKaptchaCheck,a=(0,u.useIntl)(),s=(0,v.useState)(),i=c()(s,2),l=i[0],f=i[1],g=(0,v.useState)(),w=c()(g,2),Z=w[0],b=w[1],y=function(){var e=o()(t()().mark((function e(){var n;return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,d();case 2:200===(n=e.sent).code&&(f(n.data.captchaUid),b(n.data.captchaImage));case 4:case"end":return e.stop()}}),e)})));return function(){return e.apply(this,arguments)}}(),M=function(){var e=o()(t()().mark((function e(n,a){var s;return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,p(n,a);case 2:s=e.sent,console.log("checkCaptcha response",s),200===s.code?r&&r(!0):r&&r(!1);case 5:case"end":return e.stop()}}),e)})));return function(n,r){return e.apply(this,arguments)}}();(0,v.useEffect)((function(){y()}),[]);return(0,x.jsx)(x.Fragment,{children:Z&&(0,x.jsxs)(x.Fragment,{children:[(0,x.jsx)(h.Z,{onChange:function(e){n&&(n(l,e.target.value),e.target.value&&""!==e.target.value&&4===e.target.value.trim().length?M(l,e.target.value):r&&r(!1))},prefix:(0,x.jsx)(m.Z,{}),placeholder:a.formatMessage({id:"captcha",defaultMessage:"captcha"}),style:{width:"65%",float:"left",height:40},allowClear:!0}),(0,x.jsx)("img",{src:Z,alt:"captcha",onClick:y})]})})}},6631:function(e,n,r){r.d(n,{G:function(){return f}});var a=r(73193),t=r.n(a),s=(r(44194),r(30348)),o=r(55735),i=r(28977),c=r.n(i),l=r(86803),u=r(36310),d=r(31549),f=function(e){var n=e.children,r=e.onSuccess,a=e.onError,i={file:null,fileName:"test.png",fileType:"image/png",isAvatar:"true",kbType:o.IrL,categoryUid:"",kbUid:"",client:o.bVn},f={name:"file",accept:"image/*",action:(0,u.M$)(),headers:{Authorization:"Bearer "+localStorage.getItem(o.LA8)},data:i,showUploadList:!1,beforeUpload:function(e){console.log("beforeUpload",e);var n=c()(new Date).format("YYYYMMDDHHmmss")+"_"+e.name;return i.file=e,i.fileName=n,i.fileType=e.type,console.log("beforeUpload",i),!0},onChange:function(e){if("uploading"!==e.file.status&&console.log("not uploading:",e.file),"done"===e.file.status)if(console.log("response: ",e.file.response),200===e.file.response.code){var n=e.file.response.data.fileUrl;r(n),l.yw.success("".concat(e.file.name," 上传成功"))}else a(e.file),l.yw.error("".concat(e.file.name," 上传失败"));else"error"===e.file.status&&(l.yw.error("".concat(e.file.name," 上传失败")),a(e.file))}};return(0,d.jsx)(s.Z,t()(t()({},f),{},{children:n}))}},76750:function(e,n,r){var a=r(55735),t=r(65461),s=r(41159);n.Z=function(){var e=(0,s.useIntl)();return{translateString:function(n){return null==n?n:n&&(null!=n&&n.startsWith(a.VoP)||null!=n&&n.startsWith("ROLE_"))?e.formatMessage({id:n,defaultMessage:n}):n},translateStringTranct:function(n){return null==n?n:null!=n&&n.startsWith(a.VoP)||null!=n&&n.startsWith("ROLE_")?(0,t.aS)(e.formatMessage({id:n,defaultMessage:n}),10):(0,t.aS)(n,10)}}}},87416:function(e,n,r){r.r(n),r.d(n,{default:function(){return N}});var a=r(90819),t=r.n(a),s=r(73193),o=r.n(s),i=r(89933),c=r.n(i),l=r(45332),u=r.n(l),d=r(44194),f=r(84288),p=r(75365),g=r(69169),m=r(4499),h=r(32350),v=r(41159),x=r(59908),w=r(51239),Z=r(67318),b=r(86803),y=r(6631),M=r(76750),k=r(65461),j=r(53530),P=r(31549),C=function(e){var n=e.open,r=e.onClose,a=(0,v.useIntl)(),s=(0,M.Z)().translateString;return(0,P.jsx)(P.Fragment,{children:(0,P.jsx)(j.Z,{title:a.formatMessage({id:"pages.settings.reset.password",defaultMessage:"重置密码"}),forceRender:!0,open:n,footer:null,onCancel:function(){r()},children:(0,P.jsxs)(g.A,{initialValues:{oldPassword:"",newPassword:"",confirmPassword:""},onFinish:function(){var e=c()(t()().mark((function e(n){var a,o;return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(console.log("changePassword:",n),!(n.newPassword.trim().length<6)){e.next=4;break}return b.yw.error("密码最小长度不能小于6"),e.abrupt("return");case 4:if(n.newPassword===n.confirmPassword){e.next=7;break}return b.yw.error("两次输入密码不一致"),e.abrupt("return");case 7:return a={oldPassword:n.oldPassword,newPassword:n.newPassword},e.next=10,(0,Z.Cp)(a);case 10:o=e.sent,console.log("changePassword response:",o),200===o.code?(b.yw.success("Password changed successfully!"),r()):b.yw.error(s(o.message));case 13:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}(),children:[(0,P.jsx)(m.Z.Password,{name:"oldPassword",label:"原密码(手机号直接登录用户，可以留空)"}),(0,P.jsx)(m.Z.Password,{name:"newPassword",label:"新密码"}),(0,P.jsx)(m.Z.Password,{name:"confirmPassword",label:"确认密码"})]})})})},F=r(45739),S=r(88678),T=r(40612),U=r(55735),I=r(40795),V=r(26978),R=r(59129),E=function(e){var n=e.open,r=e.onSubmit,a=e.onClose,s=(0,v.useIntl)(),o=g.A.useForm(),i=u()(o,1)[0],l=(0,M.Z)().translateString,f=(0,w.L)((function(e){return{userInfo:e.userInfo,deviceUid:e.deviceUid}})),p=f.userInfo,h=f.deviceUid,x=(0,T.u)((function(e){return e.currentOrg})),y=(0,d.useRef)(),k=(0,d.useState)(""),C=u()(k,2),E=C[0],A=C[1],z=(0,d.useState)(""),N=u()(z,2),q=N[0],L=N[1],H=(0,d.useState)(!1),O=u()(H,2),G=O[0],B=O[1],K=function(){var e=c()(t()().mark((function e(n,r){return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:console.log("captchaUid",n," captchaValue",r),A(n),L(r);case 3:case"end":return e.stop()}}),e)})));return function(n,r){return e.apply(this,arguments)}}(),D=function(){var e=c()(t()().mark((function e(n){return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:console.log("captcha check result",n),B(n);case 2:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}(),Y=function(){var e=c()(t()().mark((function e(){return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:i.validateFields().then(function(){var e=c()(t()().mark((function e(n){var s,o;return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(console.log("changeEmail:",n),(null==p?void 0:p.email)!==n.email){e.next=4;break}return b.yw.error("Email is not changed!"),e.abrupt("return");case 4:return s={email:n.email,code:n.code,platform:U.iw8},e.next=7,(0,Z.Uk)(s);case 7:o=e.sent,console.log("changeEmail response:",o),200===o.code?(b.yw.success("Email changed successfully!"),r(n.email),a()):b.yw.error(l(o.message));case 10:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}());case 1:case"end":return e.stop()}}),e)})));return function(){return e.apply(this,arguments)}}(),W=function(){setTimeout((function(){var e;console.log("endCaptchaTiming"),null===(e=y.current)||void 0===e||e.endTiming()}),2)};return(0,P.jsx)(P.Fragment,{children:(0,P.jsx)(j.Z,{title:s.formatMessage({id:"pages.settings.reset.email",defaultMessage:"重置邮箱"}),forceRender:!0,open:n,footer:null,onCancel:function(){a()},children:(0,P.jsxs)(g.A,{form:i,onFinish:function(){var e=c()(t()().mark((function e(n){return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:console.log("changeEmail:",n),Y();case 2:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}(),children:[(0,P.jsx)(m.Z,{fieldProps:{size:"large",prefix:(0,P.jsx)(I.Z,{})},name:"email",placeholder:s.formatMessage({id:"pages.login.email.placeholder",defaultMessage:"邮箱"}),rules:[{required:!0,message:(0,P.jsx)(v.FormattedMessage,{id:"pages.login.email.required",defaultMessage:"请输入邮箱！"})},{pattern:/^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/,message:"邮箱格式不正确"},{max:50,message:"邮箱不得超过50字符"}]}),(0,P.jsx)(g.A.Item,{name:"captchaCode",rules:[],children:(0,P.jsx)(S.Z,{onKaptchaChange:K,onKaptchaCheck:D})}),(0,P.jsx)(R.Z,{fieldProps:{size:"large",prefix:(0,P.jsx)(V.Z,{})},captchaProps:{size:"large",disabled:!G},placeholder:s.formatMessage({id:"pages.login.captcha.placeholder",defaultMessage:"请输入验证码"}),captchaTextRender:function(e,n){return e?"".concat(n," ").concat(s.formatMessage({id:"pages.getCaptchaSecondText",defaultMessage:"获取验证码"})):s.formatMessage({id:"pages.login.phoneLogin.getVerificationCode",defaultMessage:"获取验证码"})},phoneName:"email",name:"code",rules:[{required:!0,message:(0,P.jsx)(v.FormattedMessage,{id:"pages.login.captcha.required",defaultMessage:"请输入验证码！"})}],fieldRef:y,onGetCaptcha:function(){var e=c()(t()().mark((function e(n){var r,a;return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(console.log("email:",n),!n){e.next=18;break}if((null==p?void 0:p.email)!==n){e.next=6;break}return b.yw.error("Email is not changed!"),W(),e.abrupt("return");case 6:return r={email:n,type:U.Iju,captchaUid:E,captchaCode:q,deviceUid:h,userUid:null==p?void 0:p.uid,orgUid:null==x?void 0:x.uid,platform:U.iw8},e.next=9,(0,F.O8)(r);case 9:if(a=e.sent,console.log("sendEmailCode",a),200===a.code){e.next=15;break}return b.yw.error(a.message),W(),e.abrupt("return");case 15:b.yw.success(a.message),e.next=19;break;case 18:b.yw.error("手机号格式错误");case 19:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}()})]})})})},A=function(e){var n=e.open,r=e.onSubmit,a=e.onClose,s=(0,v.useIntl)(),o=g.A.useForm(),i=u()(o,1)[0],l=(0,M.Z)().translateString,f=(0,w.L)((function(e){return{userInfo:e.userInfo,deviceUid:e.deviceUid}})),p=f.userInfo,h=f.deviceUid,x=(0,T.u)((function(e){return e.currentOrg})),y=(0,d.useRef)(),k=(0,d.useState)(""),C=u()(k,2),E=C[0],A=C[1],z=(0,d.useState)(""),N=u()(z,2),q=N[0],L=N[1],H=(0,d.useState)(!1),O=u()(H,2),G=O[0],B=O[1],K=function(){var e=c()(t()().mark((function e(n,r){return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:console.log("captchaUid",n," captchaValue",r),A(n),L(r);case 3:case"end":return e.stop()}}),e)})));return function(n,r){return e.apply(this,arguments)}}(),D=function(){var e=c()(t()().mark((function e(n){return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:console.log("captcha check result",n),B(n);case 2:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}(),Y=function(){var e=c()(t()().mark((function e(){return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:i.validateFields().then(function(){var e=c()(t()().mark((function e(n){var s,o;return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(console.log("changeMobile:",n),(null==p?void 0:p.mobile)!==n.mobile){e.next=4;break}return b.yw.error("mobile is not changed"),e.abrupt("return");case 4:return s={mobile:n.mobile,code:n.code,platform:U.iw8},e.next=7,(0,Z.KF)(s);case 7:o=e.sent,console.log("changeMobile response:",o),200===o.code?(b.yw.success("Mobile changed successfully!"),r(n.mobile),a()):b.yw.error(l(o.message));case 10:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}());case 1:case"end":return e.stop()}}),e)})));return function(){return e.apply(this,arguments)}}(),W=function(){setTimeout((function(){var e;console.log("endCaptchaTiming"),null===(e=y.current)||void 0===e||e.endTiming()}),2)};return(0,P.jsx)(P.Fragment,{children:(0,P.jsx)(j.Z,{title:s.formatMessage({id:"pages.settings.reset.mobile",defaultMessage:"重置手机号"}),forceRender:!0,open:n,footer:null,onCancel:function(){a()},children:(0,P.jsxs)(g.A,{form:i,onFinish:function(){var e=c()(t()().mark((function e(n){return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:console.log("changeMobile:",n),Y();case 2:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}(),children:[(0,P.jsx)(m.Z,{fieldProps:{size:"large",prefix:(0,P.jsx)(I.Z,{})},name:"mobile",placeholder:s.formatMessage({id:"pages.login.phoneNumber.placeholder",defaultMessage:"手机号"}),rules:[{required:!0,message:(0,P.jsx)(v.FormattedMessage,{id:"pages.login.phoneNumber.required",defaultMessage:"请输入手机号！"})},{pattern:/^1\d{10}$/,message:(0,P.jsx)(v.FormattedMessage,{id:"pages.login.phoneNumber.invalid",defaultMessage:"手机号格式错误！"})}]}),(0,P.jsx)(g.A.Item,{name:"captchaCode",rules:[],children:(0,P.jsx)(S.Z,{onKaptchaChange:K,onKaptchaCheck:D})}),(0,P.jsx)(R.Z,{fieldProps:{size:"large",prefix:(0,P.jsx)(V.Z,{})},captchaProps:{size:"large",disabled:!G},placeholder:s.formatMessage({id:"pages.login.captcha.placeholder",defaultMessage:"请输入验证码"}),captchaTextRender:function(e,n){return e?"".concat(n," ").concat(s.formatMessage({id:"pages.getCaptchaSecondText",defaultMessage:"获取验证码"})):s.formatMessage({id:"pages.login.phoneLogin.getVerificationCode",defaultMessage:"获取验证码"})},phoneName:"mobile",name:"code",rules:[{required:!0,message:(0,P.jsx)(v.FormattedMessage,{id:"pages.login.captcha.required",defaultMessage:"请输入验证码！"})}],fieldRef:y,onGetCaptcha:function(){var e=c()(t()().mark((function e(n){var r,a;return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(console.log("mobile:",n),!n||11!==n.length){e.next=18;break}if((null==p?void 0:p.mobile)!==n){e.next=6;break}return b.yw.error("mobile is not changed"),W(),e.abrupt("return");case 6:return r={mobile:n,type:U.BG8,captchaUid:E,captchaCode:q,deviceUid:h,userUid:null==p?void 0:p.uid,orgUid:null==x?void 0:x.uid,platform:U.iw8},e.next=9,(0,F.Y7)(r);case 9:if(a=e.sent,console.log("sendMobileCode",a),200===a.code){e.next=15;break}return b.yw.error(a.message),W(),e.abrupt("return");case 15:b.yw.success(a.message),e.next=19;break;case 18:b.yw.error("手机号格式错误");case 19:case"end":return e.stop()}}),e)})));return function(n){return e.apply(this,arguments)}}()})]})})})},z={labelCol:{span:8},wrapperCol:{span:8}},N=function(){var e=(0,v.useIntl)(),n=g.A.useForm(),r=u()(n,1)[0],a=(0,M.Z)().translateString,s=(0,w.L)((function(e){return{userInfo:e.userInfo,setUserInfo:e.setUserInfo}})),i=s.userInfo,l=s.setUserInfo,j=(0,d.useState)(""),F=u()(j,2),S=F[0],T=F[1],U=(0,d.useState)(!1),I=u()(U,2),V=I[0],R=I[1],N=(0,d.useState)(!1),q=u()(N,2),L=q[0],H=q[1],O=(0,d.useState)(!1),G=u()(O,2),B=G[0],K=G[1],D=function(){var n=c()(t()().mark((function n(s){var c,u;return t()().wrap((function(n){for(;;)switch(n.prev=n.next){case 0:return c=o()(o()(o()({},i),s),{},{avatar:S}),console.log(c),n.next=4,(0,Z.ck)(c);case 4:u=n.sent,console.log("updateProfile response:",u),200===u.code?(b.yw.success(e.formatMessage({id:"update.success"})),u.data.username!==i.username?(0,k.NZ)():(l(u.data),r.setFieldsValue({uid:u.data.uid,username:u.data.username,nickname:a(u.data.nickname),email:u.data.email,mobile:u.data.mobile,description:a(u.data.description)}))):b.yw.error(u.message);case 7:case"end":return n.stop()}}),n)})));return function(e){return n.apply(this,arguments)}}();(0,d.useEffect)((function(){i&&T(i.avatar)}),[i]);var Y=function(){var e=c()(t()().mark((function e(){var n;return t()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,(0,Z.Ai)();case 2:n=e.sent,console.log("handleRefreshProfile getProfile response:",n),200===n.code?(l(n.data),r.setFieldsValue({uid:n.data.uid,username:n.data.username,nickname:a(n.data.nickname),email:n.data.email,mobile:n.data.mobile,description:a(n.data.description)})):b.yw.error(n.message);case 5:case"end":return e.stop()}}),e)})));return function(){return e.apply(this,arguments)}}();return(0,d.useEffect)((function(){Y()}),[]),(0,P.jsxs)(P.Fragment,{children:[(0,P.jsxs)(g.A,o()(o()({},z),{},{form:r,onFinish:D,children:[(0,P.jsx)(m.Z,{name:"uid",label:"Uid",rules:[{required:!0}],readonly:!0}),(0,P.jsx)(g.A.Item,{name:"avatar",valuePropName:"fileList",getValueFromEvent:function(e){return Array.isArray(e)?e:null==e?void 0:e.fileList},label:e.formatMessage({id:"pages.robot.tab.avatar",defaultMessage:"Avatar"}),children:(0,P.jsxs)(y.G,{onSuccess:function(e){console.log("handleUploadSuccess:",e),T(e)},onError:function(e){console.log("handleUploadError:",e)},children:[(0,P.jsx)(f.C,{src:S}),(0,P.jsx)(p.ZP,{icon:(0,P.jsx)(x.Z,{}),children:e.formatMessage({id:"pages.robot.upload",defaultMessage:"Upload"})})]},"avatar")}),(0,P.jsx)(m.Z,{name:"username",label:e.formatMessage({id:"username.change.tip",defaultMessage:"Username"}),rules:[{required:!0}]}),(0,P.jsx)(p.ZP,{onClick:function(){R(!0)},children:e.formatMessage({id:"pages.settings.reset.password",defaultMessage:"重置密码"})}),(0,P.jsx)(m.Z,{name:"nickname",label:e.formatMessage({id:"nickname",defaultMessage:"Nickname"}),rules:[{required:!0}]}),(0,P.jsx)(m.Z,{name:"email",label:i.emailVerified?e.formatMessage({id:"email.verified",defaultMessage:"Email Verified"}):e.formatMessage({id:"email.unverified",defaultMessage:"email unverified"}),rules:[{type:"email"}],readonly:!0}),(0,P.jsx)(p.ZP,{onClick:function(){H(!0)},children:e.formatMessage({id:"pages.settings.reset.email",defaultMessage:"重置邮箱"})}),(0,P.jsx)(m.Z,{name:"mobile",label:i.mobileVerified?e.formatMessage({id:"mobile.verified",defaultMessage:"Mobile Verified"}):e.formatMessage({id:"mobile.unverified",defaultMessage:"mobile unverified"}),readonly:!0}),(0,P.jsx)(p.ZP,{onClick:function(){K(!0)},children:e.formatMessage({id:"pages.settings.reset.mobile",defaultMessage:"重置手机号"})}),(0,P.jsx)(h.Z,{name:"description",label:e.formatMessage({id:"description",defaultMessage:"Description"})})]})),V&&(0,P.jsx)(C,{open:V,onClose:function(){R(!1)}}),L&&(0,P.jsx)(E,{open:L,onSubmit:function(e){H(!1),i.email=e,l(i),r.setFieldValue("email",e)},onClose:function(){H(!1)}}),B&&(0,P.jsx)(A,{open:B,onSubmit:function(e){K(!1),i.mobile=e,l(i),r.setFieldValue("mobile",e)},onClose:function(){K(!1)}})]})}}}]);