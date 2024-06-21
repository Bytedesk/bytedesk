"use strict";(self.webpackChunkadmin=self.webpackChunkadmin||[]).push([[8390],{58638:function(M,f,e){e.d(f,{Z:function(){return E}});var g=e(1413),a=e(67294),T={icon:{tag:"svg",attrs:{"fill-rule":"evenodd",viewBox:"64 64 896 896",focusable:"false"},children:[{tag:"path",attrs:{d:"M880 912H144c-17.7 0-32-14.3-32-32V144c0-17.7 14.3-32 32-32h360c4.4 0 8 3.6 8 8v56c0 4.4-3.6 8-8 8H184v656h656V520c0-4.4 3.6-8 8-8h56c4.4 0 8 3.6 8 8v360c0 17.7-14.3 32-32 32zM770.87 199.13l-52.2-52.2a8.01 8.01 0 014.7-13.6l179.4-21c5.1-.6 9.5 3.7 8.9 8.9l-21 179.4c-.8 6.6-8.9 9.4-13.6 4.7l-52.4-52.4-256.2 256.2a8.03 8.03 0 01-11.3 0l-42.4-42.4a8.03 8.03 0 010-11.3l256.1-256.3z"}}]},name:"export",theme:"outlined"},d=T,y=e(91146),c=function(b,p){return a.createElement(y.Z,(0,g.Z)((0,g.Z)({},b),{},{ref:p,icon:d}))},v=a.forwardRef(c),E=v},14101:function(M,f,e){e.d(f,{Nm:function(){return S},fk:function(){return E}});var g=e(15009),a=e.n(g),T=e(97857),d=e.n(T),y=e(99289),c=e.n(y),v=e(86745);function E(r){return _.apply(this,arguments)}function _(){return _=c()(a()().mark(function r(n){return a()().wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",(0,v.request)("/api/v1/thread/org",{method:"GET",params:d()({},n)}));case 1:case"end":return t.stop()}},r)})),_.apply(this,arguments)}function b(r){return p.apply(this,arguments)}function p(){return p=_asyncToGenerator(_regeneratorRuntime().mark(function r(n){return _regeneratorRuntime().wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",request("/api/v1/thread/update",{method:"POST",data:_objectSpread({},n)}));case 1:case"end":return t.stop()}},r)})),p.apply(this,arguments)}function S(r){return R.apply(this,arguments)}function R(){return R=c()(a()().mark(function r(n){return a()().wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",(0,v.request)("/api/v1/thread/close",{method:"POST",data:d()({},n)}));case 1:case"end":return t.stop()}},r)})),R.apply(this,arguments)}function B(r){return j.apply(this,arguments)}function j(){return j=_asyncToGenerator(_regeneratorRuntime().mark(function r(n){return _regeneratorRuntime().wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",request("/api/v1/thread/delete",{method:"POST",data:_objectSpread({},n)}));case 1:case"end":return t.stop()}},r)})),j.apply(this,arguments)}function U(r){return x.apply(this,arguments)}function x(){return x=_asyncToGenerator(_regeneratorRuntime().mark(function r(n){return _regeneratorRuntime().wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.abrupt("return",request("/api/v1/thread/export",{method:"GET",params:_objectSpread({},n)}));case 1:case"end":return t.stop()}},r)})),x.apply(this,arguments)}},57014:function(M,f,e){e.r(f),e.d(f,{default:function(){return k}});var g=e(15009),a=e.n(g),T=e(99289),d=e.n(T),y=e(5574),c=e.n(y),v=e(80049),E=e(14101),_=e(87676),b=e(85615),p=e(64529),S=e(782),R=e(71381),B=(0,p.Ue)()((0,S.mW)((0,S.tJ)((0,R.n)(function(C,G){return{threadResult:{data:{content:[]}},setThreadResult:function(A){C({threadResult:A})},deleteThreadCache:function(){return C({},!0)}}}),{name:b.Di}))),j=e(58638),U=e(96853),x=e(86745),r=e(14726),n=e(67294),l=e(85893),t=[{dataIndex:"index",valueType:"indexBorder",width:48},{title:"\u5185\u5BB9",dataIndex:"content",copyable:!0},{title:"\u7C7B\u578B",dataIndex:"type",copyable:!0},{title:"\u672A\u8BFB\u6570",dataIndex:"unreadCount"},{title:"\u72B6\u6001",dataIndex:"status",copyable:!0},{title:"\u6765\u6E90",dataIndex:"client",copyable:!0},{title:"\u66F4\u65B0\u65F6\u95F4",key:"updatedAt",dataIndex:"updatedAt",sorter:!0,hideInSearch:!0}],Z=function(){var G=(0,x.useIntl)(),O=(0,n.useRef)(),A=(0,n.useState)(1),W=c()(A,2),z=W[0],N=W[1],F=(0,n.useState)(10),K=c()(F,2),I=K[0],H=K[1],L=(0,_.u)(function(u){return u.orgCurrent}),$=B(function(u){return{threadResult:u.threadResult,setThreadResult:u.setThreadResult}}),D=$.threadResult,J=$.setThreadResult,V=[].concat(t,[{title:"\u64CD\u4F5C",valueType:"option",key:"option",render:function(o,s,i,m){return[(0,l.jsx)("a",{onClick:function(){},children:"\u4FEE\u6539"},"editable")]}}]),w=function(){var u=d()(a()().mark(function o(){var s,i;return a()().wrap(function(h){for(;;)switch(h.prev=h.next){case 0:return s={pageNumber:z-1,pageSize:I,orgUid:L.uid},h.next=3,(0,E.fk)(s);case 3:i=h.sent,console.log("getAllThreads response:",s,i),i.code===200?J(i):v.yw.error(i.message);case 6:case"end":return h.stop()}},o)}));return function(){return u.apply(this,arguments)}}();(0,n.useEffect)(function(){w()},[]),(0,n.useEffect)(function(){w()},[z,I]),(0,n.useEffect)(function(){console.log("threadResult:",D),O.current.reload()},[D]);var Q=function(){var u=d()(a()().mark(function o(){var s;return a()().wrap(function(m){for(;;)switch(m.prev=m.next){case 0:console.log("handleExportExcel"),s=localStorage.getItem(b.LA),window.open("/api/v1/thread/export?uid="+L.uid+"pageNumber=0&pageSize=20&accessToken="+s);case 3:case"end":return m.stop()}},o)}));return function(){return u.apply(this,arguments)}}();return(0,l.jsx)(U.Z,{columns:V,actionRef:O,cardBordered:!0,request:function(){var u=d()(a()().mark(function o(s,i,m){return a()().wrap(function(P){for(;;)switch(P.prev=P.next){case 0:return console.log("request:",s,i,m),N(s.current),H(s.pageSize),P.abrupt("return",Promise.resolve({data:D.data.content,success:!0,total:D.data.totalElements}));case 4:case"end":return P.stop()}},o)}));return function(o,s,i){return u.apply(this,arguments)}}(),rowKey:"uid",search:{labelWidth:"auto"},pagination:{pageSize:I,showQuickJumper:!0,onChange:function(o){console.log("page:",o)}},dateFormatter:"string",headerTitle:"\u4F1A\u8BDD\u5217\u8868",toolBarRender:function(){return[(0,l.jsx)(r.ZP,{icon:(0,l.jsx)(j.Z,{}),type:"primary",onClick:Q,children:G.formatMessage({id:"export",defaultMessage:"Export"})},"button")]}})},k=Z}}]);