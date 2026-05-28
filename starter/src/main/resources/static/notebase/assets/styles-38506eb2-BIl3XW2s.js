import{t as e}from"./graphlib-Df0RkNx9.js";import{A as t,C as n,Ct as r,J as i,R as a,St as o,T as s,W as c,_ as l,b as u,bt as d,p as f,s as p,xt as m,z as h}from"./mermaid-59c9be08-BDok3yXs.js";import{B as g,b as _}from"./index-BZEy-cig.js";import{t as v}from"./channel-CoZG7tiH.js";import{t as y}from"./index-bf99f535-C1yMcqmD.js";function b(e){return typeof e==`string`?new m([document.querySelectorAll(e)],[document.documentElement]):new m([r(e)],o)}function x(e,t){return!!e.children(t).length}function S(e){return w(e.v)+`:`+w(e.w)+`:`+w(e.name)}var C=/:/g;function w(e){return e?String(e).replace(C,`\\:`):``}function T(e,t){t&&e.attr(`style`,t)}function E(e,t,n){t&&e.attr(`class`,t).attr(`class`,n+` `+e.attr(`class`))}function D(e,t){var n=t.graph();if(_(n)){var r=n.transition;if(g(r))return r(e)}return e}function O(e,t){var n=e.append(`foreignObject`).attr(`width`,`100000`),r=n.append(`xhtml:div`);r.attr(`xmlns`,`http://www.w3.org/1999/xhtml`);var i=t.label;switch(typeof i){case`function`:r.insert(i);break;case`object`:r.insert(function(){return i});break;default:r.html(i)}T(r,t.labelStyle),r.style(`display`,`inline-block`),r.style(`white-space`,`nowrap`);var a=r.node().getBoundingClientRect();return n.attr(`width`,a.width).attr(`height`,a.height),n}var k={},A=function(e){let t=Object.keys(e);for(let n of t)k[n]=e[n]},j=async function(e,n,r,i,a,o){let c=i.select(`[id="${r}"]`),d=Object.keys(e);for(let r of d){let i=e[r],d=`default`;i.classes.length>0&&(d=i.classes.join(` `)),d+=` flowchart-label`;let m=u(i.styles),h=i.text===void 0?i.id:i.text,g;if(s.info(`vertex`,i,i.labelType),i.labelType===`markdown`)s.info(`vertex`,i,i.labelType);else if(f(l().flowchart.htmlLabels))g=O(c,{label:h}).node(),g.parentNode.removeChild(g);else{let e=a.createElementNS(`http://www.w3.org/2000/svg`,`text`);e.setAttribute(`style`,m.labelStyle.replace(`color:`,`fill:`));let t=h.split(p.lineBreakRegex);for(let n of t){let t=a.createElementNS(`http://www.w3.org/2000/svg`,`tspan`);t.setAttributeNS(`http://www.w3.org/XML/1998/namespace`,`xml:space`,`preserve`),t.setAttribute(`dy`,`1em`),t.setAttribute(`x`,`1`),t.textContent=n,e.appendChild(t)}g=e}let _=0,v=``;switch(i.type){case`round`:_=5,v=`rect`;break;case`square`:v=`rect`;break;case`diamond`:v=`question`;break;case`hexagon`:v=`hexagon`;break;case`odd`:v=`rect_left_inv_arrow`;break;case`lean_right`:v=`lean_right`;break;case`lean_left`:v=`lean_left`;break;case`trapezoid`:v=`trapezoid`;break;case`inv_trapezoid`:v=`inv_trapezoid`;break;case`odd_right`:v=`rect_left_inv_arrow`;break;case`circle`:v=`circle`;break;case`ellipse`:v=`ellipse`;break;case`stadium`:v=`stadium`;break;case`subroutine`:v=`subroutine`;break;case`cylinder`:v=`cylinder`;break;case`group`:v=`rect`;break;case`doublecircle`:v=`doublecircle`;break;default:v=`rect`}let y=await t(h,l());n.setNode(i.id,{labelStyle:m.labelStyle,shape:v,labelText:y,labelType:i.labelType,rx:_,ry:_,class:d,style:m.style,id:i.id,link:i.link,linkTarget:i.linkTarget,tooltip:o.db.getTooltip(i.id)||``,domId:o.db.lookUpDomId(i.id),haveCallback:i.haveCallback,width:i.type===`group`?500:void 0,dir:i.dir,type:i.type,props:i.props,padding:l().flowchart.padding}),s.info(`setNode`,{labelStyle:m.labelStyle,labelType:i.labelType,shape:v,labelText:y,rx:_,ry:_,class:d,style:m.style,id:i.id,domId:o.db.lookUpDomId(i.id),width:i.type===`group`?500:void 0,type:i.type,dir:i.dir,props:i.props,padding:l().flowchart.padding})}},M=async function(e,r,a){s.info(`abc78 edges = `,e);let o=0,c={},d,f;if(e.defaultStyle!==void 0){let t=u(e.defaultStyle);d=t.style,f=t.labelStyle}for(let a of e){o++;let m=`L-`+a.start+`-`+a.end;c[m]===void 0?(c[m]=0,s.info(`abc78 new entry`,m,c[m])):(c[m]++,s.info(`abc78 new entry`,m,c[m]));let h=m+`-`+c[m];s.info(`abc78 new link id to be used is`,m,h,c[m]);let g=`LS-`+a.start,_=`LE-`+a.end,v={style:``,labelStyle:``};switch(v.minlen=a.length||1,a.type===`arrow_open`?v.arrowhead=`none`:v.arrowhead=`normal`,v.arrowTypeStart=`arrow_open`,v.arrowTypeEnd=`arrow_open`,a.type){case`double_arrow_cross`:v.arrowTypeStart=`arrow_cross`;case`arrow_cross`:v.arrowTypeEnd=`arrow_cross`;break;case`double_arrow_point`:v.arrowTypeStart=`arrow_point`;case`arrow_point`:v.arrowTypeEnd=`arrow_point`;break;case`double_arrow_circle`:v.arrowTypeStart=`arrow_circle`;case`arrow_circle`:v.arrowTypeEnd=`arrow_circle`;break}let y=``,b=``;switch(a.stroke){case`normal`:y=`fill:none;`,d!==void 0&&(y=d),f!==void 0&&(b=f),v.thickness=`normal`,v.pattern=`solid`;break;case`dotted`:v.thickness=`normal`,v.pattern=`dotted`,v.style=`fill:none;stroke-width:2px;stroke-dasharray:3;`;break;case`thick`:v.thickness=`thick`,v.pattern=`solid`,v.style=`stroke-width: 3.5px;fill:none;`;break;case`invisible`:v.thickness=`invisible`,v.pattern=`solid`,v.style=`stroke-width: 0;fill:none;`;break}if(a.style!==void 0){let e=u(a.style);y=e.style,b=e.labelStyle}v.style=v.style+=y,v.labelStyle=v.labelStyle+=b,a.interpolate===void 0?e.defaultInterpolate===void 0?v.curve=n(k.curve,i):v.curve=n(e.defaultInterpolate,i):v.curve=n(a.interpolate,i),a.text===void 0?a.style!==void 0&&(v.arrowheadStyle=`fill: #333`):(v.arrowheadStyle=`fill: #333`,v.labelpos=`c`),v.labelType=a.labelType,v.label=await t(a.text.replace(p.lineBreakRegex,`
`),l()),a.style===void 0&&(v.style=v.style||`stroke: #333; stroke-width: 1.5px;fill:none;`),v.labelStyle=v.labelStyle.replace(`color:`,`fill:`),v.id=h,v.classes=`flowchart-link `+g+` `+_,r.setEdge(a.start,a.end,v,o)}},N={setConf:A,addVertices:j,addEdges:M,getClasses:function(e,t){return t.db.getClasses()},draw:async function(t,n,r,i){s.info(`Drawing flowchart`);let o=i.db.getDirection();o===void 0&&(o=`TD`);let{securityLevel:c,flowchart:u}=l(),f=u.nodeSpacing||50,p=u.rankSpacing||50,m;c===`sandbox`&&(m=d(`#i`+n));let g=d(c===`sandbox`?m.nodes()[0].contentDocument.body:`body`),_=c===`sandbox`?m.nodes()[0].contentDocument:document,v=new e({multigraph:!0,compound:!0}).setGraph({rankdir:o,nodesep:f,ranksep:p,marginx:0,marginy:0}).setDefaultEdgeLabel(function(){return{}}),x,S=i.db.getSubGraphs();s.info(`Subgraphs - `,S);for(let e=S.length-1;e>=0;e--)x=S[e],s.info(`Subgraph - `,x),i.db.addVertex(x.id,{text:x.title,type:x.labelType},`group`,void 0,x.classes,x.dir);let C=i.db.getVertices(),w=i.db.getEdges();s.info(`Edges`,w);let T=0;for(T=S.length-1;T>=0;T--){x=S[T],b(`cluster`).append(`text`);for(let e=0;e<x.nodes.length;e++)s.info(`Setting up subgraphs`,x.nodes[e],x.id),v.setParent(x.nodes[e],x.id)}await j(C,v,n,g,_,i),await M(w,v);let E=g.select(`[id="${n}"]`);if(await y(g.select(`#`+n+` g`),v,[`point`,`circle`,`cross`],`flowchart`,n),h.insertTitle(E,`flowchartTitleText`,u.titleTopMargin,i.db.getDiagramTitle()),a(v,E,u.diagramPadding,u.useMaxWidth),i.db.indexNodes(`subGraph`+T),!u.htmlLabels){let e=_.querySelectorAll(`[id="`+n+`"] .edgeLabel .label`);for(let t of e){let e=t.getBBox(),n=_.createElementNS(`http://www.w3.org/2000/svg`,`rect`);n.setAttribute(`rx`,0),n.setAttribute(`ry`,0),n.setAttribute(`width`,e.width),n.setAttribute(`height`,e.height),t.insertBefore(n,t.firstChild)}}Object.keys(C).forEach(function(e){let t=C[e];if(t.link){let r=d(`#`+n+` [id="`+e+`"]`);if(r){let e=_.createElementNS(`http://www.w3.org/2000/svg`,`a`);e.setAttributeNS(`http://www.w3.org/2000/svg`,`class`,t.classes.join(` `)),e.setAttributeNS(`http://www.w3.org/2000/svg`,`href`,t.link),e.setAttributeNS(`http://www.w3.org/2000/svg`,`rel`,`noopener`),c===`sandbox`?e.setAttributeNS(`http://www.w3.org/2000/svg`,`target`,`_top`):t.linkTarget&&e.setAttributeNS(`http://www.w3.org/2000/svg`,`target`,t.linkTarget);let n=r.insert(function(){return e},`:first-child`),i=r.select(`.label-container`);i&&n.append(function(){return i.node()});let a=r.select(`.label`);a&&n.append(function(){return a.node()})}}})}},P=(e,t)=>{let n=v;return c(n(e,`r`),n(e,`g`),n(e,`b`),t)},F=e=>`.label {
    font-family: ${e.fontFamily};
    color: ${e.nodeTextColor||e.textColor};
  }
  .cluster-label text {
    fill: ${e.titleColor};
  }
  .cluster-label span,p {
    color: ${e.titleColor};
  }

  .label text,span,p {
    fill: ${e.nodeTextColor||e.textColor};
    color: ${e.nodeTextColor||e.textColor};
  }

  .node rect,
  .node circle,
  .node ellipse,
  .node polygon,
  .node path {
    fill: ${e.mainBkg};
    stroke: ${e.nodeBorder};
    stroke-width: 1px;
  }
  .flowchart-label text {
    text-anchor: middle;
  }
  // .flowchart-label .text-outer-tspan {
  //   text-anchor: middle;
  // }
  // .flowchart-label .text-inner-tspan {
  //   text-anchor: start;
  // }

  .node .katex path {
    fill: #000;
    stroke: #000;
    stroke-width: 1px;
  }

  .node .label {
    text-align: center;
  }
  .node.clickable {
    cursor: pointer;
  }

  .arrowheadPath {
    fill: ${e.arrowheadColor};
  }

  .edgePath .path {
    stroke: ${e.lineColor};
    stroke-width: 2.0px;
  }

  .flowchart-link {
    stroke: ${e.lineColor};
    fill: none;
  }

  .edgeLabel {
    background-color: ${e.edgeLabelBackground};
    rect {
      opacity: 0.5;
      background-color: ${e.edgeLabelBackground};
      fill: ${e.edgeLabelBackground};
    }
    text-align: center;
  }

  /* For html labels only */
  .labelBkg {
    background-color: ${P(e.edgeLabelBackground,.5)};
    // background-color: 
  }

  .cluster rect {
    fill: ${e.clusterBkg};
    stroke: ${e.clusterBorder};
    stroke-width: 1px;
  }

  .cluster text {
    fill: ${e.titleColor};
  }

  .cluster span,p {
    color: ${e.titleColor};
  }
  /* .cluster div {
    color: ${e.titleColor};
  } */

  div.mermaidTooltip {
    position: absolute;
    text-align: center;
    max-width: 200px;
    padding: 2px;
    font-family: ${e.fontFamily};
    font-size: 12px;
    background: ${e.tertiaryColor};
    border: 1px solid ${e.border2};
    border-radius: 2px;
    pointer-events: none;
    z-index: 100;
  }

  .flowchartTitleText {
    text-anchor: middle;
    font-size: 18px;
    fill: ${e.textColor};
  }
`;export{T as a,x as c,E as i,b as l,F as n,D as o,O as r,S as s,N as t};