"use strict";(self.webpackChunkadmin=self.webpackChunkadmin||[]).push([[5957],{50484:function(e,t,n){n.d(t,{Z:function(){return i}});var r=n(17082),o=n(44194),a={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"}},{tag:"path",attrs:{d:"M464 688a48 48 0 1096 0 48 48 0 10-96 0zm24-112h48c4.4 0 8-3.6 8-8V296c0-4.4-3.6-8-8-8h-48c-4.4 0-8 3.6-8 8v272c0 4.4 3.6 8 8 8z"}}]},name:"exclamation-circle",theme:"outlined"},s=n(64423),l=function(e,t){return o.createElement(s.Z,(0,r.Z)((0,r.Z)({},e),{},{ref:t,icon:a}))};var i=o.forwardRef(l)},4391:function(e,t,n){var r=n(17082),o=n(44194),a=n(14018),s=n(64423),l=function(e,t){return o.createElement(s.Z,(0,r.Z)((0,r.Z)({},e),{},{ref:t,icon:a.Z}))},i=o.forwardRef(l);t.Z=i},8091:function(e,t,n){var r=n(17082),o=n(51190),a=n(19753),s=n(44194),l=n(84686),i=n(5986),c=n(31549),u=["fieldProps","children","params","proFieldProps","mode","valueEnum","request","showSearch","options"],d=["fieldProps","children","params","proFieldProps","mode","valueEnum","request","options"],p=function(e,t){var n=e.fieldProps,d=e.children,p=e.params,f=e.proFieldProps,g=e.mode,m=e.valueEnum,h=e.request,v=e.showSearch,y=e.options,x=(0,o.Z)(e,u),w=(0,s.useContext)(l.Z);return(0,c.jsx)(i.Z,(0,r.Z)((0,r.Z)({valueEnum:(0,a.h)(m),request:h,params:p,valueType:"select",filedConfig:{customLightMode:!0},fieldProps:(0,r.Z)({options:y,mode:g,showSearch:v,getPopupContainer:w.getPopupContainer},n),ref:t,proFieldProps:f},x),{},{children:d}))},f=s.forwardRef((function(e,t){var n=e.fieldProps,u=e.children,p=e.params,f=e.proFieldProps,g=e.mode,m=e.valueEnum,h=e.request,v=e.options,y=(0,o.Z)(e,d),x=(0,r.Z)({options:v,mode:g||"multiple",labelInValue:!0,showSearch:!0,suffixIcon:null,autoClearSearchValue:!0,optionLabelProp:"label"},n),w=(0,s.useContext)(l.Z);return(0,c.jsx)(i.Z,(0,r.Z)((0,r.Z)({valueEnum:(0,a.h)(m),request:h,params:p,valueType:"select",filedConfig:{customLightMode:!0},fieldProps:(0,r.Z)({getPopupContainer:w.getPopupContainer},x),ref:t,proFieldProps:f},y),{},{children:u}))})),g=s.forwardRef(p);g.SearchSelect=f,g.displayName="ProFormComponent",t.Z=g},43688:function(e,t,n){var r=n(42601),o=n(17082),a=n(51190),s=n(66744),l=n(47807),i=n(25438),c=n(36490),u=n(44194),d=n(5986),p=n(31549),f=["fieldProps","proFieldProps"],g=["fieldProps","proFieldProps"],m="text",h=function(e){var t=(0,s.Z)(e.open||!1,{value:e.open,onChange:e.onOpenChange}),n=(0,r.Z)(t,2),a=n[0],c=n[1];return(0,p.jsx)(l.Z.Item,{shouldUpdate:!0,noStyle:!0,children:function(t){var n,r=t.getFieldValue(e.name||[]);return(0,p.jsx)(i.Z,(0,o.Z)((0,o.Z)({getPopupContainer:function(e){return e&&e.parentNode?e.parentNode:e},onOpenChange:function(e){return c(e)},content:(0,p.jsxs)("div",{style:{padding:"4px 0"},children:[null===(n=e.statusRender)||void 0===n?void 0:n.call(e,r),e.strengthText?(0,p.jsx)("div",{style:{marginTop:10},children:(0,p.jsx)("span",{children:e.strengthText})}):null]}),overlayStyle:{width:240},placement:"rightTop"},e.popoverProps),{},{open:a,children:e.children}))}})},v=function(e){var t=e.fieldProps,n=e.proFieldProps,r=(0,a.Z)(e,f);return(0,p.jsx)(d.Z,(0,o.Z)({valueType:m,fieldProps:t,filedConfig:{valueType:m},proFieldProps:n},r))};v.Password=function(e){var t=e.fieldProps,n=e.proFieldProps,s=(0,a.Z)(e,g),l=(0,u.useState)(!1),i=(0,r.Z)(l,2),f=i[0],v=i[1];return null!=t&&t.statusRender&&s.name?(0,p.jsx)(h,{name:s.name,statusRender:null==t?void 0:t.statusRender,popoverProps:null==t?void 0:t.popoverProps,strengthText:null==t?void 0:t.strengthText,open:f,onOpenChange:v,children:(0,p.jsx)("div",{children:(0,p.jsx)(d.Z,(0,o.Z)({valueType:"password",fieldProps:(0,o.Z)((0,o.Z)({},(0,c.Z)(t,["statusRender","popoverProps","strengthText"])),{},{onBlur:function(e){var n;null==t||null===(n=t.onBlur)||void 0===n||n.call(t,e),v(!1)},onClick:function(e){var n;null==t||null===(n=t.onClick)||void 0===n||n.call(t,e),v(!0)}}),proFieldProps:n,filedConfig:{valueType:m}},s))})}):(0,p.jsx)(d.Z,(0,o.Z)({valueType:"password",fieldProps:t,proFieldProps:n,filedConfig:{valueType:m}},s))},v.displayName="ProFormComponent",t.Z=v},11607:function(e,t,n){var r=n(17082),o=n(51190),a=n(44194),s=n(5986),l=n(31549),i=["fieldProps","proFieldProps"],c=function(e,t){var n=e.fieldProps,a=e.proFieldProps,c=(0,o.Z)(e,i);return(0,l.jsx)(s.Z,(0,r.Z)({ref:t,valueType:"textarea",fieldProps:n,proFieldProps:a},c))};t.Z=a.forwardRef(c)},58314:function(e,t,n){n.d(t,{Az:function(){return d},Xc:function(){return m},jX:function(){return v},tu:function(){return f}});var r=n(90819),o=n.n(r),a=n(73193),s=n.n(a),l=n(89933),i=n.n(l),c=n(18850),u=n(79434);function d(e){return p.apply(this,arguments)}function p(){return(p=i()(o()().mark((function e(t){return o()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,u.request)("/api/v1/article/query/org",{method:"GET",params:s()(s()({},t),{},{client:c.bVn})}));case 1:case"end":return e.stop()}}),e)})))).apply(this,arguments)}function f(e){return g.apply(this,arguments)}function g(){return(g=i()(o()().mark((function e(t){return o()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,u.request)("/api/v1/article/create",{method:"POST",data:s()(s()({},t),{},{client:c.bVn})}));case 1:case"end":return e.stop()}}),e)})))).apply(this,arguments)}function m(e){return h.apply(this,arguments)}function h(){return(h=i()(o()().mark((function e(t){return o()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,u.request)("/api/v1/article/update",{method:"POST",data:s()(s()({},t),{},{client:c.bVn})}));case 1:case"end":return e.stop()}}),e)})))).apply(this,arguments)}function v(e){return y.apply(this,arguments)}function y(){return(y=i()(o()().mark((function e(t){return o()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return e.abrupt("return",(0,u.request)("/api/v1/article/delete",{method:"POST",data:s()(s()({},t),{},{client:c.bVn})}));case 1:case"end":return e.stop()}}),e)})))).apply(this,arguments)}},92543:function(e,t,n){n.d(t,{Z:function(){return P}});var r=n(90819),o=n.n(r),a=n(89933),s=n.n(a),l=n(45332),i=n.n(l),c=n(50484),u=n(83730),d=n(80093),p=n(12265),f=n(38227),g=n(40751),m=n(5634),h=n(44194),v=n(6965),y=n(60346),x=n(32078),w=n(63476),Z=n(79720),C=n(79434),j=n(85495),b=n(31549),P=function(e){var t=e.type,n=(0,C.useIntl)(),r=(0,h.useState)(!0),a=i()(r,2),l=a[0],P=a[1],M=(0,y.u)((function(e){return e.currentOrg})),S=(0,j.j)((function(e){return e.currentKbase})),k=(0,h.useState)(!1),T=i()(k,2),F=T[0],O=T[1],E=(0,w.v)((function(e){return{categoryTreeOptions:e.categoryTreeOptions,setCategoryResult:e.setCategoryResult,currentCategory:e.currentCategory,setCurrentCategoryUid:e.setCurrentCategoryUid}})),R=E.categoryTreeOptions,$=E.setCategoryResult,q=E.currentCategory,A=E.setCurrentCategoryUid,I=(0,Z.Z)(),N=I.translateString,V=I.translateStringTranct,z=u.Z.useModal(),D=i()(z,2),U=D[0],G=D[1],X=function(){var e=s()(o()().mark((function e(t){var r;return o()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:if(console.log("handleSubmit: ",t),l?x.yw.loading(n.formatMessage({id:"updating"})):x.yw.loading(n.formatMessage({id:"creating"})),!l){e.next=8;break}return e.next=5,(0,v.yr)(t);case 5:e.t0=e.sent,e.next=11;break;case 8:return e.next=10,(0,v.k4)(t);case 10:e.t0=e.sent;case 11:r=e.t0,console.log("createCategory response: ",r),200===r.code?(x.yw.destroy(),l?x.yw.success(n.formatMessage({id:"update.success"})):x.yw.success(n.formatMessage({id:"create.success"})),O(!1),B()):(x.yw.destroy(),x.yw.error(r.message));case 14:case"end":return e.stop()}}),e)})));return function(t){return e.apply(this,arguments)}}(),B=function(){var e=s()(o()().mark((function e(){var r,a;return o()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return console.log("getCategories"),x.yw.success(n.formatMessage({id:"loading"})),r={pageNumber:0,pageSize:50,type:t,kbUid:null==S?void 0:S.uid,orgUid:null==M?void 0:M.uid},e.next=5,(0,v.XS)(r);case 5:a=e.sent,console.log("queryCategories response: ",a),200===a.code?($(a),x.yw.destroy()):(x.yw.destroy(),x.yw.error(a.message));case 8:case"end":return e.stop()}}),e)})));return function(){return e.apply(this,arguments)}}(),L=function(){var e=s()(o()().mark((function e(){return o()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:console.log("handleEditCategory: ",q),P(!0),O(!0);case 3:case"end":return e.stop()}}),e)})));return function(){return e.apply(this,arguments)}}(),K=function(){var e=s()(o()().mark((function e(t){return o()().wrap((function(e){for(;;)switch(e.prev=e.next){case 0:console.log("handleDeleteCategory: ",t);case 1:case"end":return e.stop()}}),e)})));return function(t){return e.apply(this,arguments)}}();(0,h.useEffect)((function(){B()}),[S]);return(0,b.jsxs)("div",{children:[(0,b.jsxs)(d.Z,{gap:"small",wrap:"wrap",style:{marginBottom:5},children:[(0,b.jsx)(p.ZP,{type:"primary",size:"small",onClick:function(){P(!1),O(!0)},disabled:(null==S?void 0:S.type)!=t,children:n.formatMessage({id:"create"})}),""!==(null==q?void 0:q.uid)&&"all"!==(null==q?void 0:q.uid)&&(0,b.jsx)(p.ZP,{size:"small",onClick:L,children:n.formatMessage({id:"edit"})}),""!==(null==q?void 0:q.uid)&&"all"!==(null==q?void 0:q.uid)&&(0,b.jsx)(p.ZP,{onClick:function(){return function(e){U.confirm({title:n.formatMessage({id:"deleteTip"}),icon:(0,b.jsx)(c.Z,{}),content:"".concat(n.formatMessage({id:"deleteAffirm",defaultMessage:"Delete"}),"【").concat(N(e.name),"】？"),onOk:function(){K(e)},onCancel:function(){},okText:n.formatMessage({id:"ok"}),cancelText:n.formatMessage({id:"cancel"})})}(q)},size:"small",style:{float:"right"},danger:!0,children:n.formatMessage({id:"pages.robot.delete",defaultMessage:"Delete"})})]}),(0,b.jsx)(f.Z,{defaultSelectedKeys:[null==q?void 0:q.uid],onSelect:function(e,t){console.log("selected",e,t),0!==e.length&&A(e[0].toString())},treeData:R,blockNode:!0,titleRender:function(e){return(0,b.jsx)(g.Z,{title:N(e.title),children:V(e.title)})}}),F&&(0,b.jsx)(m.Z,{open:F,type:t,isEdit:l,onCancel:function(){O(!1)},onSubmit:X}),G]})}},54031:function(e,t,n){n.r(t),n.d(t,{default:function(){return G}});var r=n(66964),o=n(64476),a=n(18850),s=n(92543),l=n(73193),i=n.n(l),c=n(84176),u=n.n(c),d=n(90819),p=n.n(d),f=n(89933),g=n.n(f),m=n(45332),h=n.n(m),v=n(32078),y=n(58314),x=n(60346),w=n(4391),Z=n(31816),C=n(52519),j=n(46639),b=n(12265),P=n(44194),M=n(63476),S=n(5728),k=n(8091),T=n(43688),F=n(11607),O=n(47807),E=n(61411),R=n(62599),$=n(31549),q=function(e){var t=e.isEdit,n=e.helpcenter,r=e.open,o=e.onClose,s=e.onSubmit,l=O.Z.useForm(),c=h()(l,1)[0],u=(0,x.u)((function(e){return e.currentOrg})),d=(0,M.v)((function(e){return e.categorySelectOptions}));(0,P.useEffect)((function(){t?c.setFieldsValue({type:null==n?void 0:n.type,title:null==n?void 0:n.title,content:null==n?void 0:n.contentHtml}):c.resetFields()}),[r]);return(0,$.jsx)($.Fragment,{children:(0,$.jsx)(E.Z,{title:t?"修改":"新建",onClose:o,open:r,extra:(0,$.jsxs)(R.Z,{children:[(0,$.jsx)(b.ZP,{onClick:o,children:"取消"}),(0,$.jsx)(b.ZP,{onClick:function(){console.log("handleSubmit"),c.validateFields().then((function(e){console.log(e),s(i()(i()(i()({},n),e),{},{orgUid:null==u?void 0:u.uid}))})).catch((function(e){console.log("Form errors:",e),v.yw.error("请检查表单填写")}))},type:"primary",children:"保存"})]}),children:(0,$.jsxs)(S.A,{form:c,initialValues:i()({},n),submitter:{render:function(){return null}},children:[(0,$.jsx)(k.Z,{label:"分类",name:"categoryUid",rules:[{required:!0,message:"请选择分类"}],options:d,fieldProps:{allowClear:!0,placeholder:"请选择分类",onChange:function(e){console.log("category selected ".concat(e))}}}),(0,$.jsx)(k.Z,{label:"类型",name:"type",rules:[{required:!0,message:"请选择类型"}],options:[{label:"文本",value:a.PYi},{label:"图片",value:a.Qm,disabled:!0},{label:"视频",value:a.tVi,disabled:!0},{label:"音频",value:a.Yu1,disabled:!0},{label:"文件",value:a.Qn,disabled:!0}],fieldProps:{allowClear:!0,placeholder:"请选择类型",onChange:function(e){console.log("type selected ".concat(e))}}}),(0,$.jsx)(T.Z,{label:"标题",name:"title",rules:[{required:!0,message:"请输入标题"}]}),(0,$.jsx)(F.Z,{label:"内容",name:"content"})]})})})},A=n(79434),I=n(85495),N=(n(66393),["current"]),V=[{dataIndex:"index",valueType:"indexBorder",width:48},{title:(0,$.jsx)(A.FormattedMessage,{id:"title",defaultMessage:"Title"}),dataIndex:"title",copyable:!0},{title:(0,$.jsx)(A.FormattedMessage,{id:"type",defaultMessage:"Type"}),dataIndex:"type",hideInSearch:!0},{title:(0,$.jsx)(A.FormattedMessage,{id:"updatedAt",defaultMessage:"updatedAt"}),key:"updatedAt",dataIndex:"updatedAt",hideInSearch:!0,width:180}],z=function(){var e=(0,A.useIntl)(),t=(0,P.useRef)(),n=(0,P.useState)(!0),r=h()(n,2),o=r[0],s=(r[1],(0,P.useState)()),l=h()(s,2),c=l[0],d=(l[1],(0,P.useState)(!1)),f=h()(d,2),m=f[0],S=f[1],k=(0,x.u)((function(e){return e.currentOrg})),T=(0,I.j)((function(e){return e.currentKbase})),F=(0,M.v)((function(e){return e.currentCategory})),O=function(){var n=g()(p()().mark((function n(r){var o;return p()().wrap((function(n){for(;;)switch(n.prev=n.next){case 0:return console.log(r),n.next=3,(0,y.jX)(r);case 3:o=n.sent,console.log("delete response:",o),200===o.code?(null==t||t.current.reload(),v.yw.success(e.formatMessage({id:"delete.success",defaultMessage:"Delete Success"}))):v.yw.error(e.formatMessage({id:"delete.error",defaultMessage:"Delete Error"}));case 6:case"end":return n.stop()}}),n)})));return function(e){return n.apply(this,arguments)}}(),E=[].concat(V,[{title:e.formatMessage({id:"actions",defaultMessage:"Actions"}),valueType:"option",key:"option",width:150,render:function(t,n,r,o){return[(0,$.jsx)("a",{onClick:function(){D()},children:e.formatMessage({id:"edit",defaultMessage:"Edit"})},"edit"),(0,$.jsx)("a",{onClick:function(){!function(e){console.log("handlePreviewArticle",e),window.open("/helpcenter/".concat(null==T?void 0:T.uid,"/article/").concat(null==e?void 0:e.uid,".html"))}(n)},children:e.formatMessage({id:"preview",defaultMessage:"Preview"})},"preview"),(0,$.jsx)(j.Z,{title:e.formatMessage({id:"deleteTip",defaultMessage:"Delete Tip"}),description:"".concat(e.formatMessage({id:"deleteAffirm",defaultMessage:"Delete"}),"【").concat(null==n?void 0:n.title,"】？"),onConfirm:function(){return O(n)},okText:e.formatMessage({id:"ok"}),cancelText:e.formatMessage({id:"cancel"}),children:(0,$.jsx)(b.ZP,{size:"small",type:"link",danger:!0,children:e.formatMessage({id:"delete",defaultMessage:"Delete"})})},"delete")]}}]),R=function(){var n=g()(p()().mark((function n(r){var a,s;return p()().wrap((function(n){for(;;)switch(n.prev=n.next){case 0:if(console.log("handleSubmitDrawer",r),o?v.yw.loading(e.formatMessage({id:"updating"})):v.yw.loading(e.formatMessage({id:"creating"})),!o){n.next=8;break}return n.next=5,(0,y.Xc)(r);case 5:n.t0=n.sent,n.next=11;break;case 8:return n.next=10,(0,y.tu)(r);case 10:n.t0=n.sent;case 11:a=n.t0,console.log("createArticle response:",r,a),200===a.code?(v.yw.destroy(),v.yw.loading(e.formatMessage({id:"create.success"})),S(!1),null===(s=t.current)||void 0===s||s.reloadAndRest()):(v.yw.destroy(),v.yw.error(a.message));case 14:case"end":return n.stop()}}),n)})));return function(e){return n.apply(this,arguments)}}();(0,P.useEffect)((function(){var e;null===(e=t.current)||void 0===e||e.reloadAndRest()}),[T,F]);var z=function(){console.log("handlePreviewKbase"),window.open("/helpcenter/".concat(null==T?void 0:T.uid))},D=function(){console.log("handleCreateArticle"),window.open("/admin/doceditor")},U=(0,P.useMemo)((function(){return(null==T?void 0:T.type)!==a.VX||""===(null==T?void 0:T.uid)}),[T]);return(0,$.jsxs)($.Fragment,{children:[(0,$.jsx)(C.Z,{columns:E,actionRef:t,cardBordered:!0,request:function(){var t=g()(p()().mark((function t(n,r,o){var a,s,l;return p()().wrap((function(t){for(;;)switch(t.prev=t.next){case 0:return console.log("request:",n,r,o),v.yw.loading(e.formatMessage({id:"loading",defaultMessage:"Loading"})),n.current,a=u()(n,N),s=i()({pageNumber:n.current-1,categoryUid:"all"===(null==F?void 0:F.uid)?"":null==F?void 0:F.uid,kbUid:null==T?void 0:T.uid,orgUid:null==k?void 0:k.uid},a),t.next=6,(0,y.Az)(s);case 6:return l=t.sent,console.log("getAllArticles response:",s,l),v.yw.destroy(),200===l.code||v.yw.error(l.message),t.abrupt("return",{data:l.data.content,success:!0,total:l.data.totalElements});case 11:case"end":return t.stop()}}),t)})));return function(e,n,r){return t.apply(this,arguments)}}(),rowKey:"uid",search:{labelWidth:"auto"},pagination:{showQuickJumper:!0,onChange:function(e){console.log("page:",e)}},dateFormatter:"string",headerTitle:"帮助中心",toolBarRender:function(){return[(0,$.jsx)(b.ZP,{icon:(0,$.jsx)(w.Z,{}),type:"primary",onClick:z,disabled:U,children:e.formatMessage({id:"preview",defaultMessage:"Preview"})},"button"),(0,$.jsx)(b.ZP,{icon:(0,$.jsx)(Z.Z,{}),type:"primary",onClick:D,disabled:U,children:e.formatMessage({id:"create",defaultMessage:"Create"})},"button")]}}),(0,$.jsx)(q,{isEdit:o,open:m,helpcenter:c,onClose:function(){S(!1)},onSubmit:R})]})},D=o.Z.Sider,U=o.Z.Content,G=function(){var e=(0,r.Z)(),t=e.leftSiderStyle,n=e.contentStyle;return(0,$.jsx)("div",{children:(0,$.jsxs)(o.Z,{children:[(0,$.jsx)(D,{style:t,children:(0,$.jsx)(s.Z,{type:a.VX})}),(0,$.jsx)(o.Z,{children:(0,$.jsx)(U,{style:n,children:(0,$.jsx)(z,{})})})]})})}},80093:function(e,t,n){n.d(t,{Z:function(){return j}});var r=n(44194),o=n(51865),a=n.n(o),s=n(82252),l=n(74511),i=n(19735),c=n(2645),u=n(53681);const d=["wrap","nowrap","wrap-reverse"],p=["flex-start","flex-end","start","end","center","space-between","space-around","space-evenly","stretch","normal","left","right"],f=["center","start","end","flex-start","flex-end","self-start","self-end","baseline","normal","stretch"];var g=function(e,t){return a()(Object.assign(Object.assign(Object.assign({},((e,t)=>{const n=!0===t.wrap?"wrap":t.wrap;return{[`${e}-wrap-${n}`]:n&&d.includes(n)}})(e,t)),((e,t)=>{const n={};return f.forEach((r=>{n[`${e}-align-${r}`]=t.align===r})),n[`${e}-align-stretch`]=!t.align&&!!t.vertical,n})(e,t)),((e,t)=>{const n={};return p.forEach((r=>{n[`${e}-justify-${r}`]=t.justify===r})),n})(e,t)))};const m=e=>{const{componentCls:t}=e;return{[t]:{display:"flex","&-vertical":{flexDirection:"column"},"&-rtl":{direction:"rtl"},"&:empty":{display:"none"}}}},h=e=>{const{componentCls:t}=e;return{[t]:{"&-gap-small":{gap:e.flexGapSM},"&-gap-middle":{gap:e.flexGap},"&-gap-large":{gap:e.flexGapLG}}}},v=e=>{const{componentCls:t}=e,n={};return d.forEach((e=>{n[`${t}-wrap-${e}`]={flexWrap:e}})),n},y=e=>{const{componentCls:t}=e,n={};return f.forEach((e=>{n[`${t}-align-${e}`]={alignItems:e}})),n},x=e=>{const{componentCls:t}=e,n={};return p.forEach((e=>{n[`${t}-justify-${e}`]={justifyContent:e}})),n};var w=(0,c.I$)("Flex",(e=>{const{paddingXS:t,padding:n,paddingLG:r}=e,o=(0,u.IX)(e,{flexGapSM:t,flexGap:n,flexGapLG:r});return[m(o),h(o),v(o),y(o),x(o)]}),(()=>({})),{resetStyle:!1}),Z=function(e,t){var n={};for(var r in e)Object.prototype.hasOwnProperty.call(e,r)&&t.indexOf(r)<0&&(n[r]=e[r]);if(null!=e&&"function"==typeof Object.getOwnPropertySymbols){var o=0;for(r=Object.getOwnPropertySymbols(e);o<r.length;o++)t.indexOf(r[o])<0&&Object.prototype.propertyIsEnumerable.call(e,r[o])&&(n[r[o]]=e[r[o]])}return n};const C=r.forwardRef(((e,t)=>{const{prefixCls:n,rootClassName:o,className:c,style:u,flex:d,gap:p,children:f,vertical:m=!1,component:h="div"}=e,v=Z(e,["prefixCls","rootClassName","className","style","flex","gap","children","vertical","component"]),{flex:y,direction:x,getPrefixCls:C}=r.useContext(i.E_),j=C("flex",n),[b,P,M]=w(j),S=null!=m?m:null==y?void 0:y.vertical,k=a()(c,o,null==y?void 0:y.className,j,P,M,g(j,e),{[`${j}-rtl`]:"rtl"===x,[`${j}-gap-${p}`]:(0,l.n)(p),[`${j}-vertical`]:S}),T=Object.assign(Object.assign({},null==y?void 0:y.style),u);return d&&(T.flex=d),p&&!(0,l.n)(p)&&(T.gap=p),b(r.createElement(h,Object.assign({ref:t,className:k,style:T},(0,s.Z)(v,["justify","wrap","align"])),f))}));var j=C}}]);