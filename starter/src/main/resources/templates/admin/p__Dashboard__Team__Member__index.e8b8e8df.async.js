(self.webpackChunkadmin=self.webpackChunkadmin||[]).push([[7960],{11475:function(ce,H,e){"use strict";e.d(H,{Z:function(){return w}});var d=e(1413),o=e(67294),P={icon:{tag:"svg",attrs:{viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z"}},{tag:"path",attrs:{d:"M464 688a48 48 0 1096 0 48 48 0 10-96 0zm24-112h48c4.4 0 8-3.6 8-8V296c0-4.4-3.6-8-8-8h-48c-4.4 0-8 3.6-8 8v272c0 4.4 3.6 8 8 8z"}}]},name:"exclamation-circle",theme:"outlined"},S=P,c=e(91146),I=function(y,R){return o.createElement(c.Z,(0,d.Z)((0,d.Z)({},y),{},{ref:R,icon:S}))},A=o.forwardRef(I),w=A},51042:function(ce,H,e){"use strict";var d=e(1413),o=e(67294),P=e(42110),S=e(91146),c=function(w,m){return o.createElement(S.Z,(0,d.Z)((0,d.Z)({},w),{},{ref:m,icon:P.Z}))},I=o.forwardRef(c);H.Z=I},5966:function(ce,H,e){"use strict";var d=e(97685),o=e(1413),P=e(91),S=e(21770),c=e(8232),I=e(55241),A=e(97435),w=e(67294),m=e(32486),y=e(85893),R=["fieldProps","proFieldProps"],z=["fieldProps","proFieldProps"],W="text",q=function(h){var D=h.fieldProps,j=h.proFieldProps,B=(0,P.Z)(h,R);return(0,y.jsx)(m.Z,(0,o.Z)({valueType:W,fieldProps:D,filedConfig:{valueType:W},proFieldProps:j},B))},De=function(h){var D=(0,S.Z)(h.open||!1,{value:h.open,onChange:h.onOpenChange}),j=(0,d.Z)(D,2),B=j[0],K=j[1];return(0,y.jsx)(c.Z.Item,{shouldUpdate:!0,noStyle:!0,children:function(Ee){var re,l=Ee.getFieldValue(h.name||[]);return(0,y.jsx)(I.Z,(0,o.Z)((0,o.Z)({getPopupContainer:function(s){return s&&s.parentNode?s.parentNode:s},onOpenChange:function(s){return K(s)},content:(0,y.jsxs)("div",{style:{padding:"4px 0"},children:[(re=h.statusRender)===null||re===void 0?void 0:re.call(h,l),h.strengthText?(0,y.jsx)("div",{style:{marginTop:10},children:(0,y.jsx)("span",{children:h.strengthText})}):null]}),overlayStyle:{width:240},placement:"rightTop"},h.popoverProps),{},{open:B,children:h.children}))}})},ie=function(h){var D=h.fieldProps,j=h.proFieldProps,B=(0,P.Z)(h,z),K=(0,w.useState)(!1),v=(0,d.Z)(K,2),Ee=v[0],re=v[1];return D!=null&&D.statusRender&&B.name?(0,y.jsx)(De,{name:B.name,statusRender:D==null?void 0:D.statusRender,popoverProps:D==null?void 0:D.popoverProps,strengthText:D==null?void 0:D.strengthText,open:Ee,onOpenChange:re,children:(0,y.jsx)("div",{children:(0,y.jsx)(m.Z,(0,o.Z)({valueType:"password",fieldProps:(0,o.Z)((0,o.Z)({},(0,A.Z)(D,["statusRender","popoverProps","strengthText"])),{},{onBlur:function(f){var s;D==null||(s=D.onBlur)===null||s===void 0||s.call(D,f),re(!1)},onClick:function(f){var s;D==null||(s=D.onClick)===null||s===void 0||s.call(D,f),re(!0)}}),proFieldProps:j,filedConfig:{valueType:W}},B))})}):(0,y.jsx)(m.Z,(0,o.Z)({valueType:"password",fieldProps:D,proFieldProps:j,filedConfig:{valueType:W}},B))},fe=q;fe.Password=ie,fe.displayName="ProFormComponent",H.Z=fe},90672:function(ce,H,e){"use strict";var d=e(1413),o=e(91),P=e(67294),S=e(32486),c=e(85893),I=["fieldProps","proFieldProps"],A=function(m,y){var R=m.fieldProps,z=m.proFieldProps,W=(0,o.Z)(m,I);return(0,c.jsx)(S.Z,(0,d.Z)({ref:y,valueType:"textarea",fieldProps:R,proFieldProps:z},W))};H.Z=P.forwardRef(A)},19054:function(ce,H,e){"use strict";var d=e(1413),o=e(91),P=e(67294),S=e(32486),c=e(85893),I=["fieldProps","request","params","proFieldProps"],A=function(y,R){var z=y.fieldProps,W=y.request,q=y.params,De=y.proFieldProps,ie=(0,o.Z)(y,I);return(0,c.jsx)(S.Z,(0,d.Z)({valueType:"treeSelect",fieldProps:z,ref:R,request:W,params:q,filedConfig:{customLightMode:!0},proFieldProps:De},ie))},w=P.forwardRef(A);H.Z=w},84125:function(ce,H,e){"use strict";e.d(H,{D$:function(){return W},EP:function(){return fe},hS:function(){return De},z_:function(){return m}});var d=e(15009),o=e.n(d),P=e(97857),S=e.n(P),c=e(99289),I=e.n(c),A=e(85615),w=e(86745);function m(j){return y.apply(this,arguments)}function y(){return y=I()(o()().mark(function j(B){return o()().wrap(function(v){for(;;)switch(v.prev=v.next){case 0:return v.abrupt("return",(0,w.request)("/api/v1/mem/query/org",{method:"GET",params:S()(S()({},B),{},{client:A.bV})}));case 1:case"end":return v.stop()}},j)})),y.apply(this,arguments)}function R(){return z.apply(this,arguments)}function z(){return z=_asyncToGenerator(_regeneratorRuntime().mark(function j(){return _regeneratorRuntime().wrap(function(K){for(;;)switch(K.prev=K.next){case 0:return K.abrupt("return",request("/api/v1/mem/query",{method:"GET",params:{client:HTTP_CLIENT}}));case 1:case"end":return K.stop()}},j)})),z.apply(this,arguments)}function W(j){return q.apply(this,arguments)}function q(){return q=I()(o()().mark(function j(B){return o()().wrap(function(v){for(;;)switch(v.prev=v.next){case 0:return v.abrupt("return",(0,w.request)("/api/v1/mem/create",{method:"POST",data:S()(S()({},B),{},{client:A.bV})}));case 1:case"end":return v.stop()}},j)})),q.apply(this,arguments)}function De(j){return ie.apply(this,arguments)}function ie(){return ie=I()(o()().mark(function j(B){return o()().wrap(function(v){for(;;)switch(v.prev=v.next){case 0:return v.abrupt("return",(0,w.request)("/api/v1/mem/update",{method:"POST",data:S()(S()({},B),{},{client:A.bV})}));case 1:case"end":return v.stop()}},j)})),ie.apply(this,arguments)}function fe(j){return te.apply(this,arguments)}function te(){return te=I()(o()().mark(function j(B){return o()().wrap(function(v){for(;;)switch(v.prev=v.next){case 0:return v.abrupt("return",(0,w.request)("/api/v1/member/delete",{method:"POST",data:S()(S()({},B),{},{client:A.bV})}));case 1:case"end":return v.stop()}},j)})),te.apply(this,arguments)}function h(j){return D.apply(this,arguments)}function D(){return D=_asyncToGenerator(_regeneratorRuntime().mark(function j(B){return _regeneratorRuntime().wrap(function(v){for(;;)switch(v.prev=v.next){case 0:return v.abrupt("return",request("/api/v1/member/filter",{method:"GET",params:_objectSpread(_objectSpread({},B),{},{client:HTTP_CLIENT})}));case 1:case"end":return v.stop()}},j)})),D.apply(this,arguments)}},77154:function(ce,H,e){"use strict";var d=e(39825);function o(){var P=(0,d.Z)(),S=P.isDarkMode,c=250,I={borderRight:S?"1px solid #333":"1px solid #ccc",background:S?"#141414":"#f5f5f5"},A={background:S?"#141414":"#fff"},w={borderLeft:S?"1px solid #333":"1px solid #ccc",background:S?"#141414":"#f5f5f5"},m={minHeight:120,background:S?"#141414":"#f5f5f5"};return{leftSiderStyle:I,leftSiderWidth:c,headerStyle:A,rightSiderStyle:w,contentStyle:m}}H.Z=o},79041:function(ce,H,e){"use strict";e.r(H),e.d(H,{default:function(){return ze}});var d=e(21612),o=e(67294),P=e(77154),S=e(97857),c=e.n(S),I=e(13769),A=e.n(I),w=e(15009),m=e.n(w),y=e(99289),R=e.n(y),z=e(5574),W=e.n(z),q=e(84125),De=e(64599),ie=e.n(De),fe=e(19632),te=e.n(fe),h=e(85615),D=e(73445),j=e(782),B=e(71381),K=(0,D.Ue)()((0,j.mW)((0,j.tJ)((0,B.n)(function(L,i){return{departmentResult:{data:{content:[]}},currentDepartment:{uid:"",nickname:""},insertDepartment:function(t){L(function(E){var g=E.departmentResult.data.content;g.push(t)})},setDepartmentResult:function(t){var E={uid:h.zB,name:h.zB};L({departmentResult:c()(c()({},t),{},{data:{content:[E].concat(te()(t.data.content))}})});var g=i().currentDepartment;if(g.uid===""){var U;((U=t.data)===null||U===void 0||(U=U.content)===null||U===void 0?void 0:U.length)>0&&L({currentDepartment:t.data.content[0]})}},setCurrentDepartment:function(t){var E=i().departmentResult.data.content,g=E.findIndex(function(ae){return ae.uid===t.uid});if(g!==-1){var U=[].concat(te()(E.slice(0,g)),[t],te()(E.slice(g+1))),G=c()(c()({},i().departmentResult),{},{data:{content:U}});L({departmentResult:G,currentDepartment:t})}else console.warn("Department with the specified uid not found."),L({currentDepartment:t})},deleteCurrentDepartment:function(t){var E=i().departmentResult.data.content,g=E.findIndex(function(U){return U.uid===t});g!==-1?L({departmentResult:c()(c()({},i().departmentResult),{},{data:{content:[].concat(te()(E.slice(0,g)),te()(E.slice(g+1)))}})}):console.warn("Department not found in cache:",t),i().currentDepartment.uid===t&&L({currentDepartment:{uid:""}})},setCurrentDepUid:function(t){var E,g,U=(E=i().departmentResult)===null||E===void 0||(E=E.data)===null||E===void 0||(E=E.content)===null||E===void 0?void 0:E.find(function(ae){return ae.uid===t});if(U){L({currentDepartment:U});return}var G=function ae(Y){var ge=ie()(Y),ee;try{for(ge.s();!(ee=ge.n()).done;){var k=ee.value;if(k.uid===t){L({currentDepartment:k});return}k.children&&k.children.length>0&&ae(k.children)}}catch(ue){ge.e(ue)}finally{ge.f()}};G(((g=i().departmentResult)===null||g===void 0||(g=g.data)===null||g===void 0?void 0:g.content)||[])},deleteDepartmentCache:function(){return L({},!0)}}}),{name:h.xY}))),v=e(87676),Ee=e(11475),re=e(51042),l=e(53719),f=e(85576),s=e(66309),p=e(14726),X=e(11443),r=e(34994),T=e(19054),M=e(5966),a=e(86745),x=e(85265),me=e(42075),n=e(85893),Q=function(i){var b=i.isEdit,t=i.member,E=i.open,g=i.onClose,U=i.onSubmit,G=r.A.useForm(),ae=W()(G,1),Y=ae[0],ge=(0,a.useIntl)(),ee=K(function(de){return de.departmentResult}),k=(0,v.u)(function(de){return de.currentOrg});(0,o.useEffect)(function(){t&&Y?Y.setFieldsValue({depUid:(t==null?void 0:t.departments.length)>0?t==null?void 0:t.departments[0].uid:"",nickname:t==null?void 0:t.nickname,email:t==null?void 0:t.email,mobile:t==null?void 0:t.mobile,jobNo:t==null?void 0:t.jobNo,seatNo:t==null?void 0:t.seatNo,telephone:t==null?void 0:t.telephone}):Y!=null&&!b&&(console.log("form resetFields"),Y.resetFields())},[t]);var ue=function de(u,le){if(u.name.startsWith(h.Vo)?le.title=ge.formatMessage({id:u.name,defaultMessage:u.name}):le.title=u.name,le.value=u.uid,u.children)for(var J=0;J<u.children.length;J++){var F=u.children[J],se={title:"",value:"",children:[]};de(F,se),le.children.push(se)}},Ze=(0,o.useMemo)(function(){for(var de=[],u=0;u<ee.data.content.length;u++)if(ee.data.content[u].name!==h.zB){var le={title:"",value:"",children:[]};ue(ee.data.content[u],le),de.push(le)}return de},[ee]),Te=function(){console.log("handleSubmit"),Y.validateFields().then(function(u){console.log("Form values:",u);var le={uid:t==null?void 0:t.uid,nickname:u.nickname,email:u.email,mobile:u.mobile,jobNo:u.jobNo,seatNo:u.seatNo,telephone:u.telephone,depUid:u.depUid,orgUid:k==null?void 0:k.uid};console.log("memberObject:",le),U(le)}).catch(function(u){console.log("Form errors:",u)})};return(0,n.jsx)("div",{children:(0,n.jsx)(x.Z,{title:b?"\u7F16\u8F91\u6210\u5458":"\u65B0\u5EFA\u6210\u5458",onClose:g,open:E,extra:(0,n.jsxs)(me.Z,{children:[(0,n.jsx)(p.ZP,{onClick:g,children:"\u53D6\u6D88"}),(0,n.jsx)(p.ZP,{onClick:Te,type:"primary",children:"\u4FDD\u5B58"})]}),children:(0,n.jsxs)(r.A,{form:Y,name:"memForm",initialValues:{depUid:(t==null?void 0:t.departments.length)>0?t==null?void 0:t.departments[0].uid:"",nickname:t==null?void 0:t.nickname,email:t==null?void 0:t.email,mobile:t==null?void 0:t.mobile,jobNo:t==null?void 0:t.jobNo,seatNo:t==null?void 0:t.seatNo,telephone:t==null?void 0:t.telephone},submitter:!1,children:[(0,n.jsx)(T.Z,{label:"\u90E8\u95E8",name:"depUid",width:"md",placeholder:"\u8BF7\u9009\u62E9\u90E8\u95E8",allowClear:!0,rules:[{required:!0,message:"\u8BF7\u9009\u62E9\u90E8\u95E8"}],fieldProps:{treeDefaultExpandAll:!0,dropdownStyle:{maxHeight:400,overflow:"auto"},treeData:Ze}}),(0,n.jsx)(M.Z,{label:"\u59D3\u540D",name:"nickname",rules:[{required:!0,message:"\u8BF7\u8F93\u5165\u59D3\u540D"}]}),(0,n.jsx)(M.Z,{label:"\u624B\u673A",name:"mobile",disabled:b,rules:[{required:!0,message:"\u8BF7\u8F93\u5165\u624B\u673A\u53F7"}]}),(0,n.jsx)(M.Z,{label:"\u90AE\u7BB1",name:"email",disabled:b,rules:[{type:"email",message:"\u8BF7\u8F93\u5165\u6B63\u786E\u7684\u90AE\u7BB1\u5730\u5740"}]}),(0,n.jsx)(M.Z,{label:"\u5DE5\u53F7",name:"jobNo"}),(0,n.jsx)(M.Z,{label:"\u5EA7\u4F4D\u53F7",name:"seatNo"}),(0,n.jsx)(M.Z,{label:"\u5206\u673A\u53F7",name:"telephone"})]})})})},Ce=Q,_=e(80049),ne=["current"],Se=function(){var i=(0,o.useRef)(),b=(0,X.Z)(),t=(0,o.useRef)(!1),E=(0,o.useState)(!0),g=W()(E,2),U=g[0],G=g[1],ae=(0,o.useState)(),Y=W()(ae,2),ge=Y[0],ee=Y[1],k=(0,o.useState)(!1),ue=W()(k,2),Ze=ue[0],Te=ue[1],de=(0,v.u)(function(C){return C.currentOrg}),u=K(function(C){return C.currentDepartment}),le=f.Z.useModal(),J=W()(le,2),F=J[0],se=J[1],be=[{dataIndex:"index",valueType:"indexBorder",width:48},{title:(0,n.jsx)(a.FormattedMessage,{id:"name",defaultMessage:"Name"}),dataIndex:"nickname"},{title:(0,n.jsx)(a.FormattedMessage,{id:"jobNo",defaultMessage:"JobNum"}),dataIndex:"jobNo"},{title:(0,n.jsx)(a.FormattedMessage,{id:"email",defaultMessage:"Email"}),dataIndex:"email",render:function(Z,O){return(0,n.jsx)("a",{href:"mailto:".concat(O.email),children:Z})}},{title:(0,n.jsx)(a.FormattedMessage,{id:"telephone",defaultMessage:"Telephone"}),dataIndex:"telephone"},{title:(0,n.jsx)(a.FormattedMessage,{id:"seatNo",defaultMessage:"SeatNum"}),dataIndex:"seatNo"},{title:(0,n.jsx)(a.FormattedMessage,{id:"department",defaultMessage:"Department"}),render:function(Z,O){return O.departments.length>0?O.departments[0].name.startsWith(h.Vo)?(0,n.jsx)(s.Z,{children:(0,n.jsx)(a.FormattedMessage,{id:O.departments[0].name})}):(0,n.jsx)(s.Z,{children:O.departments[0].name}):""}},{title:(0,n.jsx)(a.FormattedMessage,{id:"status",defaultMessage:"Status"}),dataIndex:"inviteAccepted",render:function(Z,O){return O.status}},{title:(0,n.jsx)(a.FormattedMessage,{id:"mobile",defaultMessage:"Mobile"}),dataIndex:"mobile",render:function(Z,O){return(0,n.jsx)("a",{href:"tel:".concat(O.mobile),children:O.mobile})}}],Oe=function(Z){F.confirm({title:b.formatMessage({id:"deleteTip"}),icon:(0,n.jsx)(Ee.Z,{}),content:"".concat(b.formatMessage({id:"deleteAfirm",defaultMessage:"Delete"}),"\u3010").concat(Z.nickname,"\u3011\uFF1F"),onOk:function(){$e(Z)},onCancel:function(){},okText:b.formatMessage({id:"ok"}),cancelText:b.formatMessage({id:"cancel"})})},_e=[].concat(be,[{title:b.formatMessage({id:"actions",defaultMessage:"Actions"}),valueType:"option",key:"option",render:function(Z,O,he,je){return[(0,n.jsx)("a",{onClick:function(){console.log("editable:",O),$(O)},children:b.formatMessage({id:"edit",defaultMessage:"Edit"})},"editable"),(0,n.jsx)(p.ZP,{type:"link",onClick:function(){return Oe(O)},danger:!0,children:b.formatMessage({id:"delete",defaultMessage:"Delete"})},"delete")]}}]);(0,o.useEffect)(function(){console.log("currentDepDid:",u),i.current.reload()},[u]);var $e=function(){var C=R()(m()().mark(function Z(O){var he;return m()().wrap(function(Pe){for(;;)switch(Pe.prev=Pe.next){case 0:return console.log("delete mem:",O),Pe.next=3,(0,q.EP)(O);case 3:he=Pe.sent,console.log("deleteMember:",he),he.code===200?(_.yw.success(b.formatMessage({id:"delete.success",defaultMessage:"Delete success"})),i.current.reload()):_.yw.error(he.message);case 6:case"end":return Pe.stop()}},Z)}));return function(O){return C.apply(this,arguments)}}(),N=function(){ee(void 0),G(!1),Te(!0)},$=function(Z){ee(Z),G(!0),Te(!0)},V=function(){Te(!1)},oe=function(){var C=R()(m()().mark(function Z(O){var he,je;return m()().wrap(function(ye){for(;;)switch(ye.prev=ye.next){case 0:if(console.log("handleSubmitDrawer:",O),!U){ye.next=9;break}return ye.next=4,(0,q.hS)(O);case 4:he=ye.sent,console.log("updateMember:",he),he.code===200?(_.yw.success(b.formatMessage({id:"update.success",defaultMessage:"update success"})),i.current.reload(),V()):_.yw.error(he.message),ye.next=14;break;case 9:return ye.next=11,(0,q.D$)(O);case 11:je=ye.sent,console.log("createMember:",je),je.code===200?(_.yw.success(b.formatMessage({id:"create.success",defaultMessage:"create success"})),i.current.reload(),V()):_.yw.error(je.message);case 14:case"end":return ye.stop()}},Z)}));return function(O){return C.apply(this,arguments)}}(),Ue=function(){var Z;return u!=null&&(Z=u.name)!==null&&Z!==void 0&&Z.startsWith("i18n.")?b.formatMessage({id:u==null?void 0:u.name,defaultMessage:u==null?void 0:u.name}):u==null?void 0:u.name};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsx)(l.Z,{columns:_e,actionRef:i,cardBordered:!0,request:function(){var C=R()(m()().mark(function Z(O,he,je){var Pe,ye,Je,Ae;return m()().wrap(function(Re){for(;;)switch(Re.prev=Re.next){case 0:if(console.log("request:",O,he,je),!t.current){Re.next=4;break}return console.log("isLoading: 1",t.current),Re.abrupt("return");case 4:return t.current=!0,_.yw.loading("\u6B63\u5728\u52A0\u8F7D"),Pe=O.current,ye=A()(O,ne),Je=c()({pageNumber:O.current-1,orgUid:de.uid,depUid:u.uid===h.zB?"":u.uid},ye),Re.next=10,(0,q.z_)(Je);case 10:return Ae=Re.sent,console.log("queryMembersByOrg:",Je,Ae),Ae.code===200||_.yw.error(Ae.message),t.current=!1,_.yw.destroy(),Re.abrupt("return",{data:Ae.data.content,success:!0,total:Ae.data.totalElements});case 16:case"end":return Re.stop()}},Z)}));return function(Z,O,he){return C.apply(this,arguments)}}(),rowKey:"uid",search:{labelWidth:"auto"},pagination:{showQuickJumper:!0,onChange:function(Z){console.log("page:",Z)}},headerTitle:Ue(),toolBarRender:function(){return[(0,n.jsx)(p.ZP,{icon:(0,n.jsx)(re.Z,{}),type:"primary",onClick:function(){_.yw.warning("\u8BE5\u529F\u80FD\u6682\u672A\u5F00\u653E")},children:b.formatMessage({id:"import",defaultMessage:"Import"})},"button"),(0,n.jsx)(p.ZP,{icon:(0,n.jsx)(re.Z,{}),onClick:function(){console.log("new"),N()},type:"primary",children:b.formatMessage({id:"create",defaultMessage:"Create"})},"button")]}}),(0,n.jsx)(Ce,{isEdit:U,member:ge,open:Ze,onClose:V,onSubmit:oe}),se]})},Me=Se,Fe=e(86250),Ie=e(66593);function pe(L){return xe.apply(this,arguments)}function xe(){return xe=R()(m()().mark(function L(i){return m()().wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",(0,a.request)("/api/v1/dep/query/org",{method:"GET",params:c()(c()({},i),{},{client:h.bV})}));case 1:case"end":return t.stop()}},L)})),xe.apply(this,arguments)}function Be(L){return we.apply(this,arguments)}function we(){return we=R()(m()().mark(function L(i){return m()().wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",(0,a.request)("/api/v1/dep/create",{method:"POST",data:c()(c()({},i),{},{client:h.bV})}));case 1:case"end":return t.stop()}},L)})),we.apply(this,arguments)}var Ne=e(90672),Le=e(8232),We=function(i){var b=i.open,t=i.onCancel,E=i.onSubmit,g=Le.Z.useForm(),U=W()(g,1),G=U[0],ae=(0,a.useIntl)(),Y=(0,o.useState)(""),ge=W()(Y,2),ee=ge[0],k=ge[1],ue=K(function(J){return J.departmentResult}),Ze=(0,v.u)(function(J){return J.currentOrg}),Te=function J(F,se){if(F.name.startsWith(h.Vo)?se.title=ae.formatMessage({id:F.name,defaultMessage:F.name}):se.title=F.name,se.value=F.uid,F.children)for(var be=0;be<F.children.length;be++){var Oe=F.children[be],_e={title:"",value:"",children:[]};J(Oe,_e)}},de=function(F){console.log("onParentSelectChange:",F),k(F||"")},u=(0,o.useMemo)(function(){for(var J=[],F=0;F<ue.data.content.length;F++)if(ue.data.content[F].name!==h.zB){var se={title:"",value:"",children:[]};Te(ue.data.content[F],se),J.push(se)}return J},[ue]),le=function(){G.validateFields().then(function(){var F=R()(m()().mark(function se(be){var Oe;return m()().wrap(function($e){for(;;)switch($e.prev=$e.next){case 0:console.log("handleSaveDep:",be),Oe={uid:"",name:be.nickname,description:be.description,parentUid:ee||void 0,orgUid:Ze.uid},E(Oe);case 3:case"end":return $e.stop()}},se)}));return function(se){return F.apply(this,arguments)}}()).catch(function(F){console.log("Failed:",F),_.yw.error("\u521B\u5EFA\u90E8\u95E8\u5931\u8D25")})};return(0,n.jsx)("div",{children:(0,n.jsx)(f.Z,{title:"\u521B\u5EFA\u90E8\u95E8",open:b,onOk:le,onCancel:t,children:(0,n.jsxs)(r.A,{form:G,name:"depForm",style:{maxWidth:400},submitter:{render:function(){return null}},children:[(0,n.jsx)(T.Z,{label:"\u4E0A\u7EA7\u90E8\u95E8",name:"parentUid",allowClear:!0,fieldProps:{treeData:u,placeholder:"\u8BF7\u9009\u62E9\u4E0A\u7EA7\u90E8\u95E8(\u53EF\u9009)",allowClear:!0,treeDefaultExpandAll:!0,onChange:de,dropdownStyle:{maxHeight:400,overflow:"auto"}}}),(0,n.jsx)(M.Z,{label:"\u90E8\u95E8\u540D\u79F0",name:"nickname",rules:[{required:!0,message:"\u8BF7\u8F93\u5165\u540D\u79F0!"}]}),(0,n.jsx)(Ne.Z,{label:"\u90E8\u95E8\u7B80\u4ECB",name:"description"})]})})})},Ke=We,ve=function(){var i=(0,X.Z)(),b=(0,o.useState)(!1),t=W()(b,2),E=t[0],g=t[1],U=(0,v.u)(function(N){return N.currentOrg}),G=f.Z.useModal(),ae=W()(G,2),Y=ae[0],ge=ae[1],ee=K(function(N){return{currentDepartment:N.currentDepartment,departmentResult:N.departmentResult,insertDepartment:N.insertDepartment,setDepartmentResult:N.setDepartmentResult,setCurrentDepUid:N.setCurrentDepUid}}),k=ee.currentDepartment,ue=ee.departmentResult,Ze=ee.insertDepartment,Te=ee.setDepartmentResult,de=ee.setCurrentDepUid,u=function($){Y.confirm({title:i.formatMessage({id:"deleteTip"}),icon:(0,n.jsx)(Ee.Z,{}),content:"".concat(i.formatMessage({id:"deleteAfirm",defaultMessage:"Delete"}),"\u3010").concat($.name,"\u3011\uFF1F"),onOk:function(){},onCancel:function(){},okText:i.formatMessage({id:"ok"}),cancelText:i.formatMessage({id:"cancel"})})},le=function N($,V){if($.name.startsWith(h.Vo)?V.title=i.formatMessage({id:$.name,defaultMessage:$.name}):V.title=$.name,V.key=$.uid,$.children)for(var oe=0;oe<$.children.length;oe++){var Ue=$.children[oe],C={title:"",key:"",children:[]};N(Ue,C),V.children.push(C)}},J=(0,o.useMemo)(function(){for(var N=[],$=0;$<ue.data.content.length;$++){var V={title:"",key:"",children:[]};le(ue.data.content[$],V),N.push(V)}return N},[ue]),F=function(){var N=R()(m()().mark(function $(){var V,oe;return m()().wrap(function(C){for(;;)switch(C.prev=C.next){case 0:return V={pageNumber:0,pageSize:50,orgUid:U.uid},C.next=3,pe(V);case 3:oe=C.sent,console.log("queryDepartmentsByOrg:",oe),oe.code===200?Te(oe):_.yw.error(oe.message);case 6:case"end":return C.stop()}},$)}));return function(){return N.apply(this,arguments)}}();(0,o.useEffect)(function(){F()},[]);var se=function(){g(!0)},be=function(){var N=R()(m()().mark(function $(V){var oe;return m()().wrap(function(C){for(;;)switch(C.prev=C.next){case 0:return console.log("saveDep",V),_.yw.loading(i.formatMessage({id:"creating"})),C.next=4,Be(V);case 4:oe=C.sent,console.log("createDepartment:",oe),oe.code===200?(_.yw.destroy(),_.yw.success(i.formatMessage({id:"create.success",defaultMessage:"create success"})),Ze(oe.data),g(!1)):(_.yw.destroy(),_.yw.error(oe.message));case 7:case"end":return C.stop()}},$)}));return function(V){return N.apply(this,arguments)}}(),Oe=function(){g(!1)},_e=function(){console.log("new dep"),se()},$e=function($,V){console.log("selected",$,V),$.length!==0&&de($[0].toString())};return(0,n.jsxs)(n.Fragment,{children:[(0,n.jsxs)(Fe.Z,{gap:"small",wrap:"wrap",children:[(0,n.jsx)(p.ZP,{type:"primary",size:"small",onClick:_e,children:i.formatMessage({id:"create",defaultMessage:"Create"})}),(0,n.jsx)(p.ZP,{size:"small",onClick:F,children:i.formatMessage({id:"refresh",defaultMessage:"Refresh"})}),(0,n.jsx)(p.ZP,{onClick:function(){return u(k)},size:"small",style:{float:"right"},danger:!0,disabled:(k==null?void 0:k.uid)==="",children:i.formatMessage({id:"pages.robot.delete",defaultMessage:"Delete"})})]}),(0,n.jsx)(Ie.Z,{defaultExpandedKeys:[k.uid],defaultSelectedKeys:[k.uid],onSelect:$e,treeData:J,blockNode:!0}),(0,n.jsx)(Ke,{open:E,onCancel:Oe,onSubmit:be}),ge]})},Ve=ve,He=d.Z.Sider,Ge=d.Z.Content,ke=function(){var i=(0,P.Z)(),b=i.leftSiderStyle,t=i.leftSiderWidth,E=i.contentStyle;return(0,n.jsxs)(d.Z,{children:[(0,n.jsx)(He,{width:t,style:b,children:(0,n.jsx)(Ve,{})}),(0,n.jsx)(d.Z,{children:(0,n.jsx)(Ge,{style:E,children:(0,n.jsx)(Me,{})})})]})},ze=ke},86250:function(ce,H,e){"use strict";e.d(H,{Z:function(){return re}});var d=e(67294),o=e(93967),P=e.n(o),S=e(98423),c=e(98065),I=e(53124),A=e(27036),w=e(45503);const m=["wrap","nowrap","wrap-reverse"],y=["flex-start","flex-end","start","end","center","space-between","space-around","space-evenly","stretch","normal","left","right"],R=["center","start","end","flex-start","flex-end","self-start","self-end","baseline","normal","stretch"],z=(l,f)=>{const s=f.wrap===!0?"wrap":f.wrap;return{[`${l}-wrap-${s}`]:s&&m.includes(s)}},W=(l,f)=>{const s={};return R.forEach(p=>{s[`${l}-align-${p}`]=f.align===p}),s[`${l}-align-stretch`]=!f.align&&!!f.vertical,s},q=(l,f)=>{const s={};return y.forEach(p=>{s[`${l}-justify-${p}`]=f.justify===p}),s};function De(l,f){return P()(Object.assign(Object.assign(Object.assign({},z(l,f)),W(l,f)),q(l,f)))}var ie=De;const fe=l=>{const{componentCls:f}=l;return{[f]:{display:"flex","&-vertical":{flexDirection:"column"},"&-rtl":{direction:"rtl"},"&:empty":{display:"none"}}}},te=l=>{const{componentCls:f}=l;return{[f]:{"&-gap-small":{gap:l.flexGapSM},"&-gap-middle":{gap:l.flexGap},"&-gap-large":{gap:l.flexGapLG}}}},h=l=>{const{componentCls:f}=l,s={};return m.forEach(p=>{s[`${f}-wrap-${p}`]={flexWrap:p}}),s},D=l=>{const{componentCls:f}=l,s={};return R.forEach(p=>{s[`${f}-align-${p}`]={alignItems:p}}),s},j=l=>{const{componentCls:f}=l,s={};return y.forEach(p=>{s[`${f}-justify-${p}`]={justifyContent:p}}),s},B=()=>({});var K=(0,A.I$)("Flex",l=>{const{paddingXS:f,padding:s,paddingLG:p}=l,X=(0,w.TS)(l,{flexGapSM:f,flexGap:s,flexGapLG:p});return[fe(X),te(X),h(X),D(X),j(X)]},B,{resetStyle:!1}),v=function(l,f){var s={};for(var p in l)Object.prototype.hasOwnProperty.call(l,p)&&f.indexOf(p)<0&&(s[p]=l[p]);if(l!=null&&typeof Object.getOwnPropertySymbols=="function")for(var X=0,p=Object.getOwnPropertySymbols(l);X<p.length;X++)f.indexOf(p[X])<0&&Object.prototype.propertyIsEnumerable.call(l,p[X])&&(s[p[X]]=l[p[X]]);return s},re=d.forwardRef((l,f)=>{const{prefixCls:s,rootClassName:p,className:X,style:r,flex:T,gap:M,children:a,vertical:x=!1,component:me="div"}=l,n=v(l,["prefixCls","rootClassName","className","style","flex","gap","children","vertical","component"]),{flex:Q,direction:Ce,getPrefixCls:_}=d.useContext(I.E_),ne=_("flex",s),[Se,Me,Fe]=K(ne),Ie=x!=null?x:Q==null?void 0:Q.vertical,pe=P()(X,p,Q==null?void 0:Q.className,ne,Me,Fe,ie(ne,l),{[`${ne}-rtl`]:Ce==="rtl",[`${ne}-gap-${M}`]:(0,c.n)(M),[`${ne}-vertical`]:Ie}),xe=Object.assign(Object.assign({},Q==null?void 0:Q.style),r);return T&&(xe.flex=T),M&&!(0,c.n)(M)&&(xe.gap=M),Se(d.createElement(me,Object.assign({ref:f,className:pe,style:xe},(0,S.Z)(n,["justify","wrap","align"])),a))})},66309:function(ce,H,e){"use strict";e.d(H,{Z:function(){return X}});var d=e(67294),o=e(93967),P=e.n(o),S=e(98423),c=e(98787),I=e(69760),A=e(96159),w=e(45353),m=e(53124),y=e(85982),R=e(10274),z=e(14747),W=e(45503),q=e(27036);const De=r=>{const{paddingXXS:T,lineWidth:M,tagPaddingHorizontal:a,componentCls:x,calc:me}=r,n=me(a).sub(M).equal(),Q=me(T).sub(M).equal();return{[x]:Object.assign(Object.assign({},(0,z.Wf)(r)),{display:"inline-block",height:"auto",marginInlineEnd:r.marginXS,paddingInline:n,fontSize:r.tagFontSize,lineHeight:r.tagLineHeight,whiteSpace:"nowrap",background:r.defaultBg,border:`${(0,y.bf)(r.lineWidth)} ${r.lineType} ${r.colorBorder}`,borderRadius:r.borderRadiusSM,opacity:1,transition:`all ${r.motionDurationMid}`,textAlign:"start",position:"relative",[`&${x}-rtl`]:{direction:"rtl"},"&, a, a:hover":{color:r.defaultColor},[`${x}-close-icon`]:{marginInlineStart:Q,fontSize:r.tagIconSize,color:r.colorTextDescription,cursor:"pointer",transition:`all ${r.motionDurationMid}`,"&:hover":{color:r.colorTextHeading}},[`&${x}-has-color`]:{borderColor:"transparent",[`&, a, a:hover, ${r.iconCls}-close, ${r.iconCls}-close:hover`]:{color:r.colorTextLightSolid}},"&-checkable":{backgroundColor:"transparent",borderColor:"transparent",cursor:"pointer",[`&:not(${x}-checkable-checked):hover`]:{color:r.colorPrimary,backgroundColor:r.colorFillSecondary},"&:active, &-checked":{color:r.colorTextLightSolid},"&-checked":{backgroundColor:r.colorPrimary,"&:hover":{backgroundColor:r.colorPrimaryHover}},"&:active":{backgroundColor:r.colorPrimaryActive}},"&-hidden":{display:"none"},[`> ${r.iconCls} + span, > span + ${r.iconCls}`]:{marginInlineStart:n}}),[`${x}-borderless`]:{borderColor:"transparent",background:r.tagBorderlessBg}}},ie=r=>{const{lineWidth:T,fontSizeIcon:M,calc:a}=r,x=r.fontSizeSM;return(0,W.TS)(r,{tagFontSize:x,tagLineHeight:(0,y.bf)(a(r.lineHeightSM).mul(x).equal()),tagIconSize:a(M).sub(a(T).mul(2)).equal(),tagPaddingHorizontal:8,tagBorderlessBg:r.defaultBg})},fe=r=>({defaultBg:new R.C(r.colorFillQuaternary).onBackground(r.colorBgContainer).toHexString(),defaultColor:r.colorText});var te=(0,q.I$)("Tag",r=>{const T=ie(r);return De(T)},fe),h=function(r,T){var M={};for(var a in r)Object.prototype.hasOwnProperty.call(r,a)&&T.indexOf(a)<0&&(M[a]=r[a]);if(r!=null&&typeof Object.getOwnPropertySymbols=="function")for(var x=0,a=Object.getOwnPropertySymbols(r);x<a.length;x++)T.indexOf(a[x])<0&&Object.prototype.propertyIsEnumerable.call(r,a[x])&&(M[a[x]]=r[a[x]]);return M},j=d.forwardRef((r,T)=>{const{prefixCls:M,style:a,className:x,checked:me,onChange:n,onClick:Q}=r,Ce=h(r,["prefixCls","style","className","checked","onChange","onClick"]),{getPrefixCls:_,tag:ne}=d.useContext(m.E_),Se=Be=>{n==null||n(!me),Q==null||Q(Be)},Me=_("tag",M),[Fe,Ie,pe]=te(Me),xe=P()(Me,`${Me}-checkable`,{[`${Me}-checkable-checked`]:me},ne==null?void 0:ne.className,x,Ie,pe);return Fe(d.createElement("span",Object.assign({},Ce,{ref:T,style:Object.assign(Object.assign({},a),ne==null?void 0:ne.style),className:xe,onClick:Se})))}),B=e(98719);const K=r=>(0,B.Z)(r,(T,M)=>{let{textColor:a,lightBorderColor:x,lightColor:me,darkColor:n}=M;return{[`${r.componentCls}${r.componentCls}-${T}`]:{color:a,background:me,borderColor:x,"&-inverse":{color:r.colorTextLightSolid,background:n,borderColor:n},[`&${r.componentCls}-borderless`]:{borderColor:"transparent"}}}});var v=(0,q.bk)(["Tag","preset"],r=>{const T=ie(r);return K(T)},fe);function Ee(r){return typeof r!="string"?r:r.charAt(0).toUpperCase()+r.slice(1)}const re=(r,T,M)=>{const a=Ee(M);return{[`${r.componentCls}${r.componentCls}-${T}`]:{color:r[`color${M}`],background:r[`color${a}Bg`],borderColor:r[`color${a}Border`],[`&${r.componentCls}-borderless`]:{borderColor:"transparent"}}}};var l=(0,q.bk)(["Tag","status"],r=>{const T=ie(r);return[re(T,"success","Success"),re(T,"processing","Info"),re(T,"error","Error"),re(T,"warning","Warning")]},fe),f=function(r,T){var M={};for(var a in r)Object.prototype.hasOwnProperty.call(r,a)&&T.indexOf(a)<0&&(M[a]=r[a]);if(r!=null&&typeof Object.getOwnPropertySymbols=="function")for(var x=0,a=Object.getOwnPropertySymbols(r);x<a.length;x++)T.indexOf(a[x])<0&&Object.prototype.propertyIsEnumerable.call(r,a[x])&&(M[a[x]]=r[a[x]]);return M};const p=d.forwardRef((r,T)=>{const{prefixCls:M,className:a,rootClassName:x,style:me,children:n,icon:Q,color:Ce,onClose:_,bordered:ne=!0,visible:Se}=r,Me=f(r,["prefixCls","className","rootClassName","style","children","icon","color","onClose","bordered","visible"]),{getPrefixCls:Fe,direction:Ie,tag:pe}=d.useContext(m.E_),[xe,Be]=d.useState(!0),we=(0,S.Z)(Me,["closeIcon","closable"]);d.useEffect(()=>{Se!==void 0&&Be(Se)},[Se]);const Ne=(0,c.o2)(Ce),Le=(0,c.yT)(Ce),We=Ne||Le,Ke=Object.assign(Object.assign({backgroundColor:Ce&&!We?Ce:void 0},pe==null?void 0:pe.style),me),ve=Fe("tag",M),[Ve,He,Ge]=te(ve),ke=P()(ve,pe==null?void 0:pe.className,{[`${ve}-${Ce}`]:We,[`${ve}-has-color`]:Ce&&!We,[`${ve}-hidden`]:!xe,[`${ve}-rtl`]:Ie==="rtl",[`${ve}-borderless`]:!ne},a,x,He,Ge),ze=g=>{g.stopPropagation(),_==null||_(g),!g.defaultPrevented&&Be(!1)},[,L]=(0,I.Z)((0,I.w)(r),(0,I.w)(pe),{closable:!1,closeIconRender:g=>{const U=d.createElement("span",{className:`${ve}-close-icon`,onClick:ze},g);return(0,A.wm)(g,U,G=>({onClick:ae=>{var Y;(Y=G==null?void 0:G.onClick)===null||Y===void 0||Y.call(G,ae),ze(ae)},className:P()(G==null?void 0:G.className,`${ve}-close-icon`)}))}}),i=typeof Me.onClick=="function"||n&&n.type==="a",b=Q||null,t=b?d.createElement(d.Fragment,null,b,n&&d.createElement("span",null,n)):n,E=d.createElement("span",Object.assign({},we,{ref:T,className:ke,style:Ke}),t,L,Ne&&d.createElement(v,{key:"preset",prefixCls:ve}),Le&&d.createElement(l,{key:"status",prefixCls:ve}));return Ve(i?d.createElement(w.Z,{component:"Tag"},E):E)});p.CheckableTag=j;var X=p},64599:function(ce,H,e){var d=e(96263);function o(P,S){var c=typeof Symbol!="undefined"&&P[Symbol.iterator]||P["@@iterator"];if(!c){if(Array.isArray(P)||(c=d(P))||S&&P&&typeof P.length=="number"){c&&(P=c);var I=0,A=function(){};return{s:A,n:function(){return I>=P.length?{done:!0}:{done:!1,value:P[I++]}},e:function(z){throw z},f:A}}throw new TypeError(`Invalid attempt to iterate non-iterable instance.
In order to be iterable, non-array objects must have a [Symbol.iterator]() method.`)}var w=!0,m=!1,y;return{s:function(){c=c.call(P)},n:function(){var z=c.next();return w=z.done,z},e:function(z){m=!0,y=z},f:function(){try{!w&&c.return!=null&&c.return()}finally{if(m)throw y}}}}ce.exports=o,ce.exports.__esModule=!0,ce.exports.default=ce.exports}}]);