"use strict";(self.webpackChunkadmin=self.webpackChunkadmin||[]).push([[2157],{51042:function(U,O,e){var l=e(1413),n=e(67294),P=e(42110),p=e(91146),f=function(h,o){return n.createElement(p.Z,(0,l.Z)((0,l.Z)({},h),{},{ref:o,icon:P.Z}))},_=n.forwardRef(f);O.Z=_},64317:function(U,O,e){var l=e(1413),n=e(91),P=e(22270),p=e(67294),f=e(66758),_=e(32486),g=e(85893),h=["fieldProps","children","params","proFieldProps","mode","valueEnum","request","showSearch","options"],o=["fieldProps","children","params","proFieldProps","mode","valueEnum","request","options"],i=function(d,u){var m=d.fieldProps,a=d.children,t=d.params,b=d.proFieldProps,T=d.mode,B=d.valueEnum,F=d.request,Z=d.showSearch,r=d.options,A=(0,n.Z)(d,h),I=(0,p.useContext)(f.Z);return(0,g.jsx)(_.Z,(0,l.Z)((0,l.Z)({valueEnum:(0,P.h)(B),request:F,params:t,valueType:"select",filedConfig:{customLightMode:!0},fieldProps:(0,l.Z)({options:r,mode:T,showSearch:Z,getPopupContainer:I.getPopupContainer},m),ref:u,proFieldProps:b},A),{},{children:a}))},v=p.forwardRef(function(E,d){var u=E.fieldProps,m=E.children,a=E.params,t=E.proFieldProps,b=E.mode,T=E.valueEnum,B=E.request,F=E.options,Z=(0,n.Z)(E,o),r=(0,l.Z)({options:F,mode:b||"multiple",labelInValue:!0,showSearch:!0,suffixIcon:null,autoClearSearchValue:!0,optionLabelProp:"label"},u),A=(0,p.useContext)(f.Z);return(0,g.jsx)(_.Z,(0,l.Z)((0,l.Z)({valueEnum:(0,P.h)(T),request:B,params:a,valueType:"select",filedConfig:{customLightMode:!0},fieldProps:(0,l.Z)({getPopupContainer:A.getPopupContainer},r),ref:d,proFieldProps:t},Z),{},{children:m}))}),y=p.forwardRef(i),j=v,R=y;R.SearchSelect=j,R.displayName="ProFormComponent",O.Z=R},5966:function(U,O,e){var l=e(97685),n=e(1413),P=e(91),p=e(21770),f=e(8232),_=e(55241),g=e(97435),h=e(67294),o=e(32486),i=e(85893),v=["fieldProps","proFieldProps"],y=["fieldProps","proFieldProps"],j="text",R=function(a){var t=a.fieldProps,b=a.proFieldProps,T=(0,P.Z)(a,v);return(0,i.jsx)(o.Z,(0,n.Z)({valueType:j,fieldProps:t,filedConfig:{valueType:j},proFieldProps:b},T))},E=function(a){var t=(0,p.Z)(a.open||!1,{value:a.open,onChange:a.onOpenChange}),b=(0,l.Z)(t,2),T=b[0],B=b[1];return(0,i.jsx)(f.Z.Item,{shouldUpdate:!0,noStyle:!0,children:function(Z){var r,A=Z.getFieldValue(a.name||[]);return(0,i.jsx)(_.Z,(0,n.Z)((0,n.Z)({getPopupContainer:function(c){return c&&c.parentNode?c.parentNode:c},onOpenChange:function(c){return B(c)},content:(0,i.jsxs)("div",{style:{padding:"4px 0"},children:[(r=a.statusRender)===null||r===void 0?void 0:r.call(a,A),a.strengthText?(0,i.jsx)("div",{style:{marginTop:10},children:(0,i.jsx)("span",{children:a.strengthText})}):null]}),overlayStyle:{width:240},placement:"rightTop"},a.popoverProps),{},{open:T,children:a.children}))}})},d=function(a){var t=a.fieldProps,b=a.proFieldProps,T=(0,P.Z)(a,y),B=(0,h.useState)(!1),F=(0,l.Z)(B,2),Z=F[0],r=F[1];return t!=null&&t.statusRender&&T.name?(0,i.jsx)(E,{name:T.name,statusRender:t==null?void 0:t.statusRender,popoverProps:t==null?void 0:t.popoverProps,strengthText:t==null?void 0:t.strengthText,open:Z,onOpenChange:r,children:(0,i.jsx)("div",{children:(0,i.jsx)(o.Z,(0,n.Z)({valueType:"password",fieldProps:(0,n.Z)((0,n.Z)({},(0,g.Z)(t,["statusRender","popoverProps","strengthText"])),{},{onBlur:function(I){var c;t==null||(c=t.onBlur)===null||c===void 0||c.call(t,I),r(!1)},onClick:function(I){var c;t==null||(c=t.onClick)===null||c===void 0||c.call(t,I),r(!0)}}),proFieldProps:b,filedConfig:{valueType:j}},T))})}):(0,i.jsx)(o.Z,(0,n.Z)({valueType:"password",fieldProps:t,proFieldProps:b,filedConfig:{valueType:j}},T))},u=R;u.Password=d,u.displayName="ProFormComponent",O.Z=u},90672:function(U,O,e){var l=e(1413),n=e(91),P=e(67294),p=e(32486),f=e(85893),_=["fieldProps","proFieldProps"],g=function(o,i){var v=o.fieldProps,y=o.proFieldProps,j=(0,n.Z)(o,_);return(0,f.jsx)(p.Z,(0,l.Z)({ref:i,valueType:"textarea",fieldProps:v,proFieldProps:y},j))};O.Z=P.forwardRef(g)},9465:function(U,O,e){e.d(O,{Ck:function(){return j},qL:function(){return o},z4:function(){return v}});var l=e(15009),n=e.n(l),P=e(97857),p=e.n(P),f=e(99289),_=e.n(f),g=e(85615),h=e(86745);function o(u){return i.apply(this,arguments)}function i(){return i=_()(n()().mark(function u(m){return n()().wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",(0,h.request)("/api/v1/quickbutton/query/org",{method:"GET",params:p()(p()({},m),{},{client:g.bV})}));case 1:case"end":return t.stop()}},u)})),i.apply(this,arguments)}function v(u){return y.apply(this,arguments)}function y(){return y=_()(n()().mark(function u(m){return n()().wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",(0,h.request)("/api/v1/quickbutton/create",{method:"POST",data:p()(p()({},m),{},{client:g.bV})}));case 1:case"end":return t.stop()}},u)})),y.apply(this,arguments)}function j(u){return R.apply(this,arguments)}function R(){return R=_()(n()().mark(function u(m){return n()().wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",(0,h.request)("/api/v1/quickbutton/update",{method:"POST",data:p()(p()({},m),{},{client:g.bV})}));case 1:case"end":return t.stop()}},u)})),R.apply(this,arguments)}function E(u){return d.apply(this,arguments)}function d(){return d=_asyncToGenerator(_regeneratorRuntime().mark(function u(m){return _regeneratorRuntime().wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",request("/api/v1/quickbutton/delete",{method:"POST",data:_objectSpread(_objectSpread({},m),{},{client:HTTP_CLIENT})}));case 1:case"end":return t.stop()}},u)})),d.apply(this,arguments)}},73803:function(U,O,e){var l=e(85615),n=e(16854),P=e(86745);function p(){var f=(0,P.useIntl)(),_=function(o){return o&&o.startsWith(l.Vo)?f.formatMessage({id:o,defaultMessage:o}):o},g=function(o){return o!=null&&o.startsWith(l.Vo)?(0,n.aS)(f.formatMessage({id:o}),10):(0,n.aS)(o,10)};return{translateString:_,translateStringTranct:g}}O.Z=p},78163:function(U,O,e){e.r(O),e.d(O,{default:function(){return oe}});var l=e(97857),n=e.n(l),P=e(13769),p=e.n(P),f=e(15009),_=e.n(f),g=e(99289),h=e.n(g),o=e(5574),i=e.n(o),v=e(80049),y=e(9465),j=e(87676),R=e(51042),E=e(53719),d=e(14726),u=e(67294),m=e(85615),a=e(34994),t=e(64317),b=e(5966),T=e(90672),B=e(8232),F=e(85265),Z=e(42075),r=e(85893),A=function(M){var z=M.isEdit,S=M.quickbutton,G=M.open,H=M.onClose,J=M.onSubmit,N=B.Z.useForm(),X=i()(N,1),L=X[0],K=(0,j.u)(function(Q){return Q.currentOrg});(0,u.useEffect)(function(){z?L.setFieldsValue({type:S==null?void 0:S.type,title:S==null?void 0:S.title,content:S==null?void 0:S.content}):L.resetFields()},[G]);var q=function(){console.log("handleSubmit"),L.validateFields().then(function(w){console.log(w),J(n()(n()(n()({},S),w),{},{orgUid:K==null?void 0:K.uid}))}).catch(function(w){console.log("Form errors:",w),v.yw.error("\u8BF7\u68C0\u67E5\u8868\u5355\u586B\u5199")})};return(0,r.jsx)("div",{children:(0,r.jsx)(F.Z,{title:z?"\u4FEE\u6539":"\u65B0\u5EFA",onClose:H,open:G,extra:(0,r.jsxs)(Z.Z,{children:[(0,r.jsx)(d.ZP,{onClick:H,children:"\u53D6\u6D88"}),(0,r.jsx)(d.ZP,{onClick:q,type:"primary",children:"\u4FDD\u5B58"})]}),children:(0,r.jsxs)(a.A,{form:L,initialValues:{},submitter:{render:function(){return null}},children:[(0,r.jsx)(t.Z,{label:"\u7C7B\u578B",name:"type",rules:[{required:!0,message:"\u8BF7\u9009\u62E9\u7C7B\u578B"}],options:[{label:"\u95EE\u7B54",value:m.kn},{label:"\u7F51\u5740",value:m.us}]}),(0,r.jsx)(b.Z,{label:"\u6807\u9898",name:"title",rules:[{required:!0,message:"\u8BF7\u8F93\u5165\u6807\u9898"}]}),(0,r.jsx)(T.Z,{label:"\u5185\u5BB9",name:"content"})]})})})},I=A,c=e(86745),ne=e(73803),ae=["current"],se=function(){var M=(0,c.useIntl)(),z=(0,ne.Z)(),S=z.translateStringTranct,G=(0,u.useRef)(),H=(0,u.useState)(!0),J=i()(H,2),N=J[0],X=J[1],L=(0,u.useState)(),K=i()(L,2),q=K[0],Q=K[1],w=(0,u.useState)(!1),re=i()(w,2),ie=re[0],Y=re[1],le=(0,j.u)(function(C){return C.currentOrg}),de=[{dataIndex:"index",valueType:"indexBorder",width:48},{title:(0,r.jsx)(c.FormattedMessage,{id:"title",defaultMessage:"Title"}),dataIndex:"title",copyable:!0,render:function(x,s){return S(s==null?void 0:s.title)}},{title:(0,r.jsx)(c.FormattedMessage,{id:"content",defaultMessage:"Content"}),dataIndex:"content",render:function(x,s){return S(s==null?void 0:s.content)}},{title:(0,r.jsx)(c.FormattedMessage,{id:"type",defaultMessage:"Type"}),dataIndex:"type",hideInSearch:!0,render:function(x,s){return(0,r.jsx)(r.Fragment,{children:M.formatMessage({id:s==null?void 0:s.type,defaultMessage:s==null?void 0:s.type})})}},{title:(0,r.jsx)(c.FormattedMessage,{id:"updatedAt",defaultMessage:"updatedAt"}),key:"updatedAt",dataIndex:"updatedAt",sorter:!0,hideInSearch:!0,width:180},{title:M.formatMessage({id:"actions",defaultMessage:"Actions"}),valueType:"option",key:"option",width:100,render:function(x,s,W,$){return[(0,r.jsx)("a",{onClick:function(){ce(s)},children:M.formatMessage({id:"edit",defaultMessage:"Edit"})},"editable")]}}],pe=function(){Q(void 0),X(!1),Y(!0)},ce=function(x){Q(x),X(!0),Y(!0)},_e=function(){var C=h()(_()().mark(function x(s){var W,$;return _()().wrap(function(D){for(;;)switch(D.prev=D.next){case 0:if(console.log("handleSubmitDrawer",s),N?v.yw.loading(M.formatMessage({id:"updating"})):v.yw.loading(M.formatMessage({id:"creating"})),!N){D.next=8;break}return D.next=5,(0,y.Ck)(s);case 5:D.t0=D.sent,D.next=11;break;case 8:return D.next=10,(0,y.z4)(s);case 10:D.t0=D.sent;case 11:W=D.t0,console.log("createQuickbutton response:",s,W),W.code===200?(v.yw.destroy(),v.yw.loading(M.formatMessage({id:"create.success"})),Y(!1),($=G.current)===null||$===void 0||$.reloadAndRest()):(v.yw.destroy(),v.yw.error(W.message));case 14:case"end":return D.stop()}},x)}));return function(s){return C.apply(this,arguments)}}(),ve=function(){Y(!1)};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(E.Z,{columns:de,actionRef:G,cardBordered:!0,request:function(){var C=h()(_()().mark(function x(s,W,$){var ee,D,te,k;return _()().wrap(function(V){for(;;)switch(V.prev=V.next){case 0:return console.log("request:",s,W,$),v.yw.loading(M.formatMessage({id:"loading",defaultMessage:"Loading"})),ee=s.current,D=p()(s,ae),te=n()({pageNumber:s.current-1,orgUid:le.uid},D),V.next=6,(0,y.qL)(te);case 6:return k=V.sent,console.log("getAllQuickbuttons response:",te,k),v.yw.destroy(),k.code===200||v.yw.error(k.message),V.abrupt("return",{data:k.data.content,success:!0,total:k.data.totalElements});case 11:case"end":return V.stop()}},x)}));return function(x,s,W){return C.apply(this,arguments)}}(),rowKey:"uid",search:{labelWidth:"auto"},pagination:{showQuickJumper:!0,onChange:function(x){console.log("page:",x)}},dateFormatter:"string",headerTitle:M.formatMessage({id:"menu.knowledge.quickbutton",defaultMessage:"QuickButton"}),toolBarRender:function(){return[(0,r.jsx)(d.ZP,{icon:(0,r.jsx)(R.Z,{}),type:"primary",onClick:pe,children:M.formatMessage({id:"create",defaultMessage:"Create"})},"button")]}}),(0,r.jsx)(I,{isEdit:N,open:ie,quickbutton:q,onClose:ve,onSubmit:_e})]})},oe=se}}]);