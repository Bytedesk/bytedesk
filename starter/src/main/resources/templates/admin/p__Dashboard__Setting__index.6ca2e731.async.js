"use strict";(self.webpackChunkadmin=self.webpackChunkadmin||[]).push([[7469],{77154:function(e,s,t){var i=t(39825);s.Z=function(){var e=(0,i.Z)().isDarkMode;return{leftSiderStyle:{borderRight:e?"1px solid #333":"1px solid #ccc",background:e?"#141414":"#f5f5f5"},leftSiderWidth:250,headerStyle:{background:e?"#141414":"#fff"},rightSiderStyle:{borderLeft:e?"1px solid #333":"1px solid #ccc",background:e?"#141414":"#f5f5f5"},contentStyle:{minHeight:120,background:e?"#141414":"#f5f5f5",height:"100vh"}}}},35675:function(e,s,t){t.r(s);var i=t(77154),l=t(86745),r=t(21612),a=t(50136),n=(t(67294),t(96974)),d=t(85893),f=r.Z.Sider,c=r.Z.Content,o=[{label:(0,d.jsx)(l.FormattedMessage,{id:"settings.profile",defaultMessage:"Profile"}),key:"profile"},{label:(0,d.jsx)(l.FormattedMessage,{id:"settings.basic",defaultMessage:"Basic"}),key:"basic"},{label:(0,d.jsx)(l.FormattedMessage,{id:"settings.realname",defaultMessage:"Realname"}),key:"realname"},{label:(0,d.jsx)(l.FormattedMessage,{id:"settings.server",defaultMessage:"Server"}),key:"server"}];s.default=function(){var e=(0,i.Z)().leftSiderStyle,s=(0,n.s0)();return(0,d.jsxs)(r.Z,{children:[(0,d.jsx)(f,{style:e,children:(0,d.jsx)(a.Z,{mode:"inline",onClick:function(e){console.log("menu click ",e),s("/setting/"+e.key)},defaultSelectedKeys:["profile"],items:o})}),(0,d.jsx)(r.Z,{children:(0,d.jsx)(c,{children:(0,d.jsx)(n.j3,{})})})]})}}}]);