"use strict";(self.webpackChunkadmin=self.webpackChunkadmin||[]).push([[79],{51042:function(H,j,e){var i=e(1413),o=e(67294),O=e(42110),a=e(91146),F=function(h,D){return o.createElement(a.Z,(0,i.Z)((0,i.Z)({},h),{},{ref:D,icon:O.Z}))},E=o.forwardRef(F);j.Z=E},64317:function(H,j,e){var i=e(1413),o=e(91),O=e(22270),a=e(67294),F=e(66758),E=e(43889),C=e(85893),h=["fieldProps","children","params","proFieldProps","mode","valueEnum","request","showSearch","options"],D=["fieldProps","children","params","proFieldProps","mode","valueEnum","request","options"],T=function(s,l){var c=s.fieldProps,v=s.children,r=s.params,b=s.proFieldProps,w=s.mode,I=s.valueEnum,$=s.request,G=s.showSearch,V=s.options,Q=(0,o.Z)(s,h),X=(0,a.useContext)(F.Z);return(0,C.jsx)(E.Z,(0,i.Z)((0,i.Z)({valueEnum:(0,O.h)(I),request:$,params:r,valueType:"select",filedConfig:{customLightMode:!0},fieldProps:(0,i.Z)({options:V,mode:w,showSearch:G,getPopupContainer:X.getPopupContainer},c),ref:l,proFieldProps:b},Q),{},{children:v}))},Z=a.forwardRef(function(m,s){var l=m.fieldProps,c=m.children,v=m.params,r=m.proFieldProps,b=m.mode,w=m.valueEnum,I=m.request,$=m.options,G=(0,o.Z)(m,D),V=(0,i.Z)({options:$,mode:b||"multiple",labelInValue:!0,showSearch:!0,suffixIcon:null,autoClearSearchValue:!0,optionLabelProp:"label"},l),Q=(0,a.useContext)(F.Z);return(0,C.jsx)(E.Z,(0,i.Z)((0,i.Z)({valueEnum:(0,O.h)(w),request:I,params:v,valueType:"select",filedConfig:{customLightMode:!0},fieldProps:(0,i.Z)({getPopupContainer:Q.getPopupContainer},V),ref:s,proFieldProps:r},G),{},{children:c}))}),y=a.forwardRef(T),d=Z,S=y;S.SearchSelect=d,S.displayName="ProFormComponent",j.Z=S},90672:function(H,j,e){var i=e(1413),o=e(91),O=e(67294),a=e(43889),F=e(85893),E=["fieldProps","proFieldProps"],C=function(D,T){var Z=D.fieldProps,y=D.proFieldProps,d=(0,o.Z)(D,E);return(0,F.jsx)(a.Z,(0,i.Z)({ref:T,valueType:"textarea",fieldProps:Z,proFieldProps:y},d))};j.Z=O.forwardRef(C)},40401:function(H,j,e){e.d(j,{kh:function(){return Z},pf:function(){return D},y:function(){return d}});var i=e(15009),o=e.n(i),O=e(97857),a=e.n(O),F=e(99289),E=e.n(F),C=e(85615),h=e(86745);function D(l){return T.apply(this,arguments)}function T(){return T=E()(o()().mark(function l(c){return o()().wrap(function(r){for(;;)switch(r.prev=r.next){case 0:return r.abrupt("return",(0,h.request)("/api/v1/faq/query/org",{method:"GET",params:a()(a()({},c),{},{client:C.bV})}));case 1:case"end":return r.stop()}},l)})),T.apply(this,arguments)}function Z(l){return y.apply(this,arguments)}function y(){return y=E()(o()().mark(function l(c){return o()().wrap(function(r){for(;;)switch(r.prev=r.next){case 0:return r.abrupt("return",(0,h.request)("/api/v1/faq/create",{method:"POST",data:a()(a()({},c),{},{client:C.bV})}));case 1:case"end":return r.stop()}},l)})),y.apply(this,arguments)}function d(l){return S.apply(this,arguments)}function S(){return S=E()(o()().mark(function l(c){return o()().wrap(function(r){for(;;)switch(r.prev=r.next){case 0:return r.abrupt("return",(0,h.request)("/api/v1/faq/update",{method:"POST",data:a()(a()({},c),{},{client:C.bV})}));case 1:case"end":return r.stop()}},l)})),S.apply(this,arguments)}function m(l){return s.apply(this,arguments)}function s(){return s=_asyncToGenerator(_regeneratorRuntime().mark(function l(c){return _regeneratorRuntime().wrap(function(r){for(;;)switch(r.prev=r.next){case 0:return r.abrupt("return",request("/api/v1/faq/delete",{method:"POST",data:_objectSpread(_objectSpread({},c),{},{client:HTTP_CLIENT})}));case 1:case"end":return r.stop()}},l)})),s.apply(this,arguments)}},19177:function(H,j,e){e.r(j),e.d(j,{default:function(){return he}});var i=e(77154),o=e(21612),O=e(97857),a=e.n(O),F=e(13769),E=e.n(F),C=e(15009),h=e.n(C),D=e(99289),T=e.n(D),Z=e(5574),y=e.n(Z),d=e(80049),S=e(40401),m=e(87676),s=e(51042),l=e(96853),c=e(14726),v=e(67294),r=e(89102),b=e(85615),w=e(34994),I=e(64317),$=e(5966),G=e(90672),V=e(8232),Q=e(85265),X=e(42075),t=e(85893),oe=function(f){var x=f.isEdit,n=f.quickreply,B=f.open,N=f.onClose,k=f.onSubmit,J=V.Z.useForm(),ee=y()(J,1),A=ee[0],z=(0,m.u)(function(_){return _.orgCurrent}),Y=(0,r.v)(function(_){return _.categorySelectOptions});(0,v.useEffect)(function(){if(x){var _;A.setFieldsValue({type:n==null?void 0:n.type,title:n==null?void 0:n.title,content:n==null?void 0:n.content,categoryUid:n==null||(_=n.category)===null||_===void 0?void 0:_.uid})}else A.resetFields()},[B]);var re=function(g){console.log("category selected ".concat(g))},U=function(g){console.log("type selected ".concat(g))},te=function(){console.log("handleSubmit"),A.validateFields().then(function(g){console.log(g),k(a()(a()(a()({},n),g),{},{orgUid:z==null?void 0:z.uid}))}).catch(function(g){console.log("Form errors:",g),d.yw.error("\u8BF7\u68C0\u67E5\u8868\u5355\u586B\u5199")})};return(0,t.jsx)(t.Fragment,{children:(0,t.jsx)(Q.Z,{title:x?"\u4FEE\u6539":"\u65B0\u5EFA",onClose:N,open:B,extra:(0,t.jsxs)(X.Z,{children:[(0,t.jsx)(c.ZP,{onClick:N,children:"\u53D6\u6D88"}),(0,t.jsx)(c.ZP,{onClick:te,type:"primary",children:"\u4FDD\u5B58"})]}),children:(0,t.jsxs)(w.A,{form:A,initialValues:a()({},n),submitter:{render:function(){return null}},children:[(0,t.jsx)(I.Z,{label:"\u5206\u7C7B",name:"categoryUid",rules:[{required:!0,message:"\u8BF7\u9009\u62E9\u5206\u7C7B"}],options:Y,fieldProps:{allowClear:!0,placeholder:"\u8BF7\u9009\u62E9\u5206\u7C7B",onChange:re}}),(0,t.jsx)(I.Z,{label:"\u7C7B\u578B",name:"type",rules:[{required:!0,message:"\u8BF7\u9009\u62E9\u7C7B\u578B"}],options:[{label:"\u6587\u672C",value:b.PY},{label:"\u56FE\u7247",value:b.Qm},{label:"\u89C6\u9891",value:b.tV},{label:"\u97F3\u9891",value:b.ai},{label:"\u6587\u4EF6",value:b.Qn}],fieldProps:{allowClear:!0,placeholder:"\u8BF7\u9009\u62E9\u7C7B\u578B",onChange:U}}),(0,t.jsx)($.Z,{label:"\u6807\u9898",name:"title",rules:[{required:!0,message:"\u8BF7\u8F93\u5165\u6807\u9898"}]}),(0,t.jsx)(G.Z,{label:"\u5185\u5BB9",name:"content"})]})})})},se=oe,le=["current"],ue=[{dataIndex:"index",valueType:"indexBorder",width:48},{title:"\u540D\u79F0",dataIndex:"title",copyable:!0},{title:"\u5185\u5BB9",dataIndex:"content"},{title:"\u7C7B\u578B",dataIndex:"type",hideInSearch:!0},{title:"\u5206\u7C7B",dataIndex:"category",hideInSearch:!0,render:function(f,x){var n;return(n=x.category)===null||n===void 0?void 0:n.name}},{title:"\u66F4\u65B0\u65F6\u95F4",key:"updatedAt",dataIndex:"updatedAt",sorter:!0,hideInSearch:!0,width:180}],ie=function(){var f=(0,v.useRef)(),x=(0,v.useState)(!0),n=y()(x,2),B=n[0],N=n[1],k=(0,v.useState)(),J=y()(k,2),ee=J[0],A=J[1],z=(0,v.useState)(!1),Y=y()(z,2),re=Y[0],U=Y[1],te=(0,m.u)(function(u){return u.orgCurrent}),_=[].concat(ue,[{title:"\u64CD\u4F5C",valueType:"option",key:"option",width:100,render:function(M,P,R,W){return[(0,t.jsx)("a",{onClick:function(){_e(P)},children:"\u4FEE\u6539"},"editable")]}}]),g=(0,r.v)(function(u){return u.currentCategory}),fe=function(){A(void 0),N(!1),U(!0)},_e=function(M){A(M),N(!0),U(!0)},ge=function(){var u=T()(h()().mark(function M(P){var R,W;return h()().wrap(function(p){for(;;)switch(p.prev=p.next){case 0:if(console.log("handleSubmitDrawer",P),B?d.yw.loading("\u6B63\u5728\u4FEE\u6539\u5E38\u89C1\u95EE\u9898"):d.yw.loading("\u6B63\u5728\u521B\u5EFA\u5E38\u89C1\u95EE\u9898"),!B){p.next=8;break}return p.next=5,(0,S.y)(P);case 5:p.t0=p.sent,p.next=11;break;case 8:return p.next=10,(0,S.kh)(P);case 10:p.t0=p.sent;case 11:R=p.t0,console.log("createFaq response:",P,R),R.code===200?(d.yw.destroy(),d.yw.success("\u5E38\u89C1\u95EE\u9898\u521B\u5EFA\u6210\u529F"),U(!1),(W=f.current)===null||W===void 0||W.reloadAndRest()):(d.yw.destroy(),d.yw.error(R.message));case 14:case"end":return p.stop()}},M)}));return function(P){return u.apply(this,arguments)}}(),Pe=function(){U(!1)};return(0,v.useEffect)(function(){var u;console.log("currentCategory:",g),(u=f.current)===null||u===void 0||u.reloadAndRest()},[g]),(0,t.jsxs)(t.Fragment,{children:[(0,t.jsx)(l.Z,{columns:_,actionRef:f,cardBordered:!0,request:function(){var u=T()(h()().mark(function M(P,R,W){var ne,p,ae,L;return h()().wrap(function(K){for(;;)switch(K.prev=K.next){case 0:return console.log("request:",P,R,W),d.yw.loading("\u6B63\u5728\u52A0\u8F7D\u5E38\u89C1\u95EE\u9898"),ne=P.current,p=E()(P,le),ae=a()({pageNumber:P.current-1,orgUid:te.uid,categoryUid:g.uid==="all"?"":g.uid},p),K.next=6,(0,S.pf)(ae);case 6:return L=K.sent,console.log("getAllFaqs response:",ae,L),d.yw.destroy(),L.code===200||d.yw.error(L.message),K.abrupt("return",{data:L.data.content,success:!0,total:L.data.totalElements});case 11:case"end":return K.stop()}},M)}));return function(M,P,R){return u.apply(this,arguments)}}(),rowKey:"uid",search:{labelWidth:"auto"},pagination:{showQuickJumper:!0,onChange:function(M){console.log("page:",M)}},dateFormatter:"string",headerTitle:"\u5E38\u89C1\u95EE\u9898",toolBarRender:function(){return[(0,t.jsx)(c.ZP,{icon:(0,t.jsx)(s.Z,{}),type:"primary",onClick:fe,children:"\u521B\u5EFA"},"button")]}}),(0,t.jsx)(se,{isEdit:B,open:re,quickreply:ee,onClose:Pe,onSubmit:ge})]})},de=ie,ce=e(52670),pe=o.Z.Sider,me=o.Z.Content,ve=function(){var f=(0,i.Z)(),x=f.leftSiderStyle,n=f.contentStyle;return(0,t.jsx)("div",{children:(0,t.jsxs)(o.Z,{children:[(0,t.jsx)(pe,{style:x,children:(0,t.jsx)(ce.Z,{type:b.OG})}),(0,t.jsx)(o.Z,{children:(0,t.jsx)(me,{style:n,children:(0,t.jsx)(de,{})})})]})})},he=ve}}]);