"use strict";(self.webpackChunkadmin=self.webpackChunkadmin||[]).push([[2005],{77154:function(f,r,e){var i=e(39825);function t(){var d=(0,i.Z)(),l=d.isDarkMode,n=250,c={borderRight:l?"1px solid #333":"1px solid #ccc",background:l?"#141414":"#f5f5f5"},u={background:l?"#141414":"#fff"},a={borderLeft:l?"1px solid #333":"1px solid #ccc",background:l?"#141414":"#f5f5f5"},o={minHeight:120,background:l?"#141414":"#f5f5f5",height:"100vh"};return{leftSiderStyle:c,leftSiderWidth:n,headerStyle:u,rightSiderStyle:a,contentStyle:o}}r.Z=t},39340:function(f,r,e){e.r(r);var i=e(77154),t=e(21612),d=e(50136),l=e(67294),n=e(85893),c=t.Z.Header,u=t.Z.Footer,a=t.Z.Sider,o=t.Z.Content,_=[{label:"\u5F85\u63A5\u6536",key:"waiting"},{label:"\u5904\u7406\u4E2D",key:"processing"},{label:"\u5DF2\u62D2\u7EDD",key:"reject"},{label:"\u5DF2\u5B8C\u6210",key:"completed"}],S=function(){var s=(0,i.Z)(),y=s.leftSiderStyle,h=s.contentStyle,E=function(k){console.log("menu click ",k.key)};return(0,n.jsxs)(t.Z,{children:[(0,n.jsx)(a,{style:y,children:(0,n.jsx)(d.Z,{mode:"inline",onClick:E,defaultSelectedKeys:["article"],defaultOpenKeys:["article","file"],items:_})}),(0,n.jsx)(t.Z,{children:(0,n.jsx)(o,{style:h,children:"\u5DE5\u5355\u7BA1\u7406\u540E\u53F0"})})]})};r.default=S}}]);