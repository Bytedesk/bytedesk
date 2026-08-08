import{L as e,p as t}from"./merge-EqqsqeB_.js";import{t as n}from"./graphlib-DIIHTR5P.js";import{A as r,C as i,Ct as a,J as o,R as s,St as c,T as l,W as u,_ as d,b as f,bt as p,p as m,s as h,xt as g,z as _}from"./mermaid-59c9be08-D85uPSJ-.js";import{t as v}from"./channel-CxlHuL4e.js";import{t as y}from"./index-bf99f535-BGuF_Jd_.js";function b(e){return typeof e==`string`?new g([document.querySelectorAll(e)],[document.documentElement]):new g([a(e)],c)}function x(e,t){return!!e.children(t).length}function S(e){return w(e.v)+`:`+w(e.w)+`:`+w(e.name)}var C=/:/g;function w(e){return e?String(e).replace(C,`\\:`):``}function T(e,t){t&&e.attr(`style`,t)}function E(e,t,n){t&&e.attr(`class`,t).attr(`class`,n+` `+e.attr(`class`))}function D(n,r){var i=r.graph();if(t(i)){var a=i.transition;if(e(a))return a(n)}return n}function O(e,t){var n=e.append(`foreignObject`).attr(`width`,`100000`),r=n.append(`xhtml:div`);r.attr(`xmlns`,`http://www.w3.org/1999/xhtml`);var i=t.label;switch(typeof i){case`function`:r.insert(i);break;case`object`:r.insert(function(){return i});break;default:r.html(i)}T(r,t.labelStyle),r.style(`display`,`inline-block`),r.style(`white-space`,`nowrap`);var a=r.node().getBoundingClientRect();return n.attr(`width`,a.width).attr(`height`,a.height),n}var k={},A=function(e){let t=Object.keys(e);for(let n of t)k[n]=e[n]},j=async function(e,t,n,i,a,o){let s=i.select(`[id="${n}"]`),c=Object.keys(e);for(let n of c){let i=e[n],c=`default`;i.classes.length>0&&(c=i.classes.join(` `)),c+=` flowchart-label`;let u=f(i.styles),p=i.text===void 0?i.id:i.text,g;if(l.info(`vertex`,i,i.labelType),i.labelType===`markdown`)l.info(`vertex`,i,i.labelType);else if(m(d().flowchart.htmlLabels))g=O(s,{label:p}).node(),g.parentNode.removeChild(g);else{let e=a.createElementNS(`http://www.w3.org/2000/svg`,`text`);e.setAttribute(`style`,u.labelStyle.replace(`color:`,`fill:`));let t=p.split(h.lineBreakRegex);for(let n of t){let t=a.createElementNS(`http://www.w3.org/2000/svg`,`tspan`);t.setAttributeNS(`http://www.w3.org/XML/1998/namespace`,`xml:space`,`preserve`),t.setAttribute(`dy`,`1em`),t.setAttribute(`x`,`1`),t.textContent=n,e.appendChild(t)}g=e}let _=0,v=``;switch(i.type){case`round`:_=5,v=`rect`;break;case`square`:v=`rect`;break;case`diamond`:v=`question`;break;case`hexagon`:v=`hexagon`;break;case`odd`:v=`rect_left_inv_arrow`;break;case`lean_right`:v=`lean_right`;break;case`lean_left`:v=`lean_left`;break;case`trapezoid`:v=`trapezoid`;break;case`inv_trapezoid`:v=`inv_trapezoid`;break;case`odd_right`:v=`rect_left_inv_arrow`;break;case`circle`:v=`circle`;break;case`ellipse`:v=`ellipse`;break;case`stadium`:v=`stadium`;break;case`subroutine`:v=`subroutine`;break;case`cylinder`:v=`cylinder`;break;case`group`:v=`rect`;break;case`doublecircle`:v=`doublecircle`;break;default:v=`rect`}let y=await r(p,d());t.setNode(i.id,{labelStyle:u.labelStyle,shape:v,labelText:y,labelType:i.labelType,rx:_,ry:_,class:c,style:u.style,id:i.id,link:i.link,linkTarget:i.linkTarget,tooltip:o.db.getTooltip(i.id)||``,domId:o.db.lookUpDomId(i.id),haveCallback:i.haveCallback,width:i.type===`group`?500:void 0,dir:i.dir,type:i.type,props:i.props,padding:d().flowchart.padding}),l.info(`setNode`,{labelStyle:u.labelStyle,labelType:i.labelType,shape:v,labelText:y,rx:_,ry:_,class:c,style:u.style,id:i.id,domId:o.db.lookUpDomId(i.id),width:i.type===`group`?500:void 0,type:i.type,dir:i.dir,props:i.props,padding:d().flowchart.padding})}},M=async function(e,t,n){l.info(`abc78 edges = `,e);let a=0,s={},c,u;if(e.defaultStyle!==void 0){let t=f(e.defaultStyle);c=t.style,u=t.labelStyle}for(let n of e){a++;let p=`L-`+n.start+`-`+n.end;s[p]===void 0?(s[p]=0,l.info(`abc78 new entry`,p,s[p])):(s[p]++,l.info(`abc78 new entry`,p,s[p]));let m=p+`-`+s[p];l.info(`abc78 new link id to be used is`,p,m,s[p]);let g=`LS-`+n.start,_=`LE-`+n.end,v={style:``,labelStyle:``};switch(v.minlen=n.length||1,v.arrowhead=n.type===`arrow_open`?`none`:`normal`,v.arrowTypeStart=`arrow_open`,v.arrowTypeEnd=`arrow_open`,n.type){case`double_arrow_cross`:v.arrowTypeStart=`arrow_cross`;case`arrow_cross`:v.arrowTypeEnd=`arrow_cross`;break;case`double_arrow_point`:v.arrowTypeStart=`arrow_point`;case`arrow_point`:v.arrowTypeEnd=`arrow_point`;break;case`double_arrow_circle`:v.arrowTypeStart=`arrow_circle`;case`arrow_circle`:v.arrowTypeEnd=`arrow_circle`}let y=``,b=``;switch(n.stroke){case`normal`:y=`fill:none;`,c!==void 0&&(y=c),u!==void 0&&(b=u),v.thickness=`normal`,v.pattern=`solid`;break;case`dotted`:v.thickness=`normal`,v.pattern=`dotted`,v.style=`fill:none;stroke-width:2px;stroke-dasharray:3;`;break;case`thick`:v.thickness=`thick`,v.pattern=`solid`,v.style=`stroke-width: 3.5px;fill:none;`;break;case`invisible`:v.thickness=`invisible`,v.pattern=`solid`,v.style=`stroke-width: 0;fill:none;`}if(n.style!==void 0){let e=f(n.style);y=e.style,b=e.labelStyle}v.style=v.style+=y,v.labelStyle=v.labelStyle+=b,v.curve=n.interpolate===void 0?e.defaultInterpolate===void 0?i(k.curve,o):i(e.defaultInterpolate,o):i(n.interpolate,o),n.text===void 0?n.style!==void 0&&(v.arrowheadStyle=`fill: #333`):(v.arrowheadStyle=`fill: #333`,v.labelpos=`c`),v.labelType=n.labelType,v.label=await r(n.text.replace(h.lineBreakRegex,`
`),d()),n.style===void 0&&(v.style=v.style||`stroke: #333; stroke-width: 1.5px;fill:none;`),v.labelStyle=v.labelStyle.replace(`color:`,`fill:`),v.id=m,v.classes=`flowchart-link `+g+` `+_,t.setEdge(n.start,n.end,v,a)}},N={setConf:A,addVertices:j,addEdges:M,getClasses:function(e,t){return t.db.getClasses()},draw:async function(e,t,r,i){l.info(`Drawing flowchart`);let a=i.db.getDirection();a===void 0&&(a=`TD`);let{securityLevel:o,flowchart:c}=d(),u=c.nodeSpacing||50,f=c.rankSpacing||50,m;o===`sandbox`&&(m=p(`#i`+t));let h=p(o===`sandbox`?m.nodes()[0].contentDocument.body:`body`),g=o===`sandbox`?m.nodes()[0].contentDocument:document,v=new n({multigraph:!0,compound:!0}).setGraph({rankdir:a,nodesep:u,ranksep:f,marginx:0,marginy:0}).setDefaultEdgeLabel(function(){return{}}),x,S=i.db.getSubGraphs();l.info(`Subgraphs - `,S);for(let e=S.length-1;e>=0;e--)x=S[e],l.info(`Subgraph - `,x),i.db.addVertex(x.id,{text:x.title,type:x.labelType},`group`,void 0,x.classes,x.dir);let C=i.db.getVertices(),w=i.db.getEdges();l.info(`Edges`,w);let T=0;for(T=S.length-1;T>=0;T--){x=S[T],b(`cluster`).append(`text`);for(let e=0;e<x.nodes.length;e++)l.info(`Setting up subgraphs`,x.nodes[e],x.id),v.setParent(x.nodes[e],x.id)}await j(C,v,t,h,g,i),await M(w,v);let E=h.select(`[id="${t}"]`),D=h.select(`#`+t+` g`);if(await y(D,v,[`point`,`circle`,`cross`],`flowchart`,t),_.insertTitle(E,`flowchartTitleText`,c.titleTopMargin,i.db.getDiagramTitle()),s(v,E,c.diagramPadding,c.useMaxWidth),i.db.indexNodes(`subGraph`+T),!c.htmlLabels){let e=g.querySelectorAll(`[id="`+t+`"] .edgeLabel .label`);for(let t of e){let e=t.getBBox(),n=g.createElementNS(`http://www.w3.org/2000/svg`,`rect`);n.setAttribute(`rx`,0),n.setAttribute(`ry`,0),n.setAttribute(`width`,e.width),n.setAttribute(`height`,e.height),t.insertBefore(n,t.firstChild)}}Object.keys(C).forEach(function(e){let n=C[e];if(n.link){let r=p(`#`+t+` [id="`+e+`"]`);if(r){let e=g.createElementNS(`http://www.w3.org/2000/svg`,`a`);e.setAttributeNS(`http://www.w3.org/2000/svg`,`class`,n.classes.join(` `)),e.setAttributeNS(`http://www.w3.org/2000/svg`,`href`,n.link),e.setAttributeNS(`http://www.w3.org/2000/svg`,`rel`,`noopener`),o===`sandbox`?e.setAttributeNS(`http://www.w3.org/2000/svg`,`target`,`_top`):n.linkTarget&&e.setAttributeNS(`http://www.w3.org/2000/svg`,`target`,n.linkTarget);let t=r.insert(function(){return e},`:first-child`),i=r.select(`.label-container`);i&&t.append(function(){return i.node()});let a=r.select(`.label`);a&&t.append(function(){return a.node()})}}})}},P=(e,t)=>{let n=v,r=n(e,`r`),i=n(e,`g`),a=n(e,`b`);return u(r,i,a,t)},F=e=>`.label {
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