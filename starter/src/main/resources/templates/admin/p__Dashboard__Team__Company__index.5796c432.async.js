"use strict";(self.webpackChunkadmin=self.webpackChunkadmin||[]).push([[2701],{77154:function(e,t,n){var i=n(39825);t.Z=function(){var e=(0,i.Z)().isDarkMode;return{leftSiderStyle:{borderRight:e?"1px solid #333":"1px solid #ccc",background:e?"#141414":"#f5f5f5"},leftSiderWidth:250,headerStyle:{background:e?"#141414":"#fff"},rightSiderStyle:{borderLeft:e?"1px solid #333":"1px solid #ccc",background:e?"#141414":"#f5f5f5"},contentStyle:{minHeight:120,background:e?"#141414":"#f5f5f5",height:"100vh"}}}},85485:function(e,t,n){n.r(t);var i=n(77154),l=n(21612),o=n(50136),d=(n(67294),n(86745)),r=n(85893),c=(l.Z.Header,l.Z.Footer,l.Z.Sider),f=l.Z.Content,s=[{label:(0,r.jsx)(d.FormattedMessage,{id:"info",defaultMessage:"Info"}),key:"info"}];t.default=function(){var e=(0,d.useNavigate)(),t=(0,i.Z)(),n=t.leftSiderStyle,a=t.contentStyle;return(0,r.jsxs)(l.Z,{children:[(0,r.jsx)(c,{style:n,children:(0,r.jsx)(o.Z,{mode:"inline",onClick:function(t){console.log("menu click ",t.key),e("/team/company/".concat(t.key))},defaultSelectedKeys:["info"],defaultOpenKeys:["info"],items:s})}),(0,r.jsx)(l.Z,{children:(0,r.jsx)(f,{style:a,children:(0,r.jsx)(d.Outlet,{})})})]})}}}]);