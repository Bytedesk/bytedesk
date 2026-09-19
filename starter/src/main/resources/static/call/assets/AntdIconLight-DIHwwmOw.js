import{a as e}from"./rolldown-runtime-DkW27tQK.js";import{Ht as t,Nn as n}from"./Button-BD5au0Oj.js";var r=e(n()),i=(0,r.createContext)({}),a=`data-rc-order`,o=`data-rc-priority`,s=`rc-util-key`,c=new Map;function l(){return!!(typeof window<`u`&&window.document&&window.document.createElement)}function u(e,t){if(!e||!t)return!1;if(e.contains)return e.contains(t);let n=t;for(;n;){if(n===e)return!0;n=n.parentNode}return!1}function d({mark:e}={}){return e?e.startsWith(`data-`)?e:`data-${e}`:s}function f(e){return e.attachTo?e.attachTo:document.querySelector(`head`)||document.body}function p(e){return e===`queue`?`prependQueue`:e?`prepend`:`append`}function m(e){return Array.from((c.get(e)||e).children).filter(e=>e.tagName===`STYLE`)}function h(e,t={}){if(!l())return null;let{csp:n,prepend:r,priority:i=0}=t,s=p(r),c=s===`prependQueue`,u=document.createElement(`style`);u.setAttribute(a,s),c&&i&&u.setAttribute(o,`${i}`),n?.nonce&&(u.nonce=n.nonce),u.innerHTML=e;let d=f(t),{firstChild:h}=d;if(r){if(c){let e=(t.styles||m(d)).filter(e=>{if(![`prepend`,`prependQueue`].includes(e.getAttribute(a)))return!1;let t=Number(e.getAttribute(o)||0);return i>=t});if(e.length)return d.insertBefore(u,e[e.length-1].nextSibling),u}d.insertBefore(u,h)}else d.appendChild(u);return u}function g(e,t={}){let{styles:n}=t;return n||=m(f(t)),n.find(n=>n.getAttribute(d(t))===e)}function _(e,t){let n=c.get(e);if(!n||!u(document,n)){let n=h(``,t);if(!n)return;let{parentNode:r}=n;c.set(e,r),e.removeChild(n)}}function v(e,t,n={}){if(!l())return null;let r=f(n),i=m(r),a={...n,styles:i};_(r,a);let o=g(t,a);if(o)return a.csp?.nonce&&o.nonce!==a.csp.nonce&&(o.nonce=a.csp.nonce),o.innerHTML!==e&&(o.innerHTML=e),o;let s=h(e,a);return s?.setAttribute(d(a),t),s}function y(e){return e?.getRootNode?.()}function b(e){let t=y(e);return typeof ShadowRoot<`u`&&t instanceof ShadowRoot?t:null}var x={};function S(e,t){e||x[t]||(x[t]=!0)}function C(e){return e.replace(/-(.)/g,(e,t)=>t.toUpperCase())}function w(e,t){S(e,`[@ant-design/icons] ${t}`)}function T(e){return typeof e==`object`&&typeof e.name==`string`&&typeof e.theme==`string`&&(typeof e.icon==`object`||typeof e.icon==`function`)}function E(e={}){return Object.keys(e).reduce((t,n)=>{let r=e[n];switch(n){case`class`:t.className=r,delete t.class;break;default:delete t[n],t[C(n)]=r}return t},{})}function D(e,t,n){return n?r.createElement(e.tag,{key:t,...E(e.attrs),...n},(e.children||[]).map((n,r)=>D(n,`${t}-${e.tag}-${r}`))):r.createElement(e.tag,{key:t,...E(e.attrs)},(e.children||[]).map((n,r)=>D(n,`${t}-${e.tag}-${r}`)))}var O=`
.anticon {
  display: inline-flex;
  align-items: center;
  color: inherit;
  font-style: normal;
  line-height: 0;
  text-align: center;
  text-transform: none;
  vertical-align: -0.125em;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.anticon > * {
  line-height: 1;
}

.anticon svg {
  display: inline-block;
  vertical-align: inherit;
}

.anticon::before {
  display: none;
}

.anticon .anticon-icon {
  display: block;
}

.anticon[tabindex] {
  cursor: pointer;
}

.anticon-spin {
  -webkit-animation: loadingCircle 1s infinite linear;
  animation: loadingCircle 1s infinite linear;
}

@-webkit-keyframes loadingCircle {
  100% {
    -webkit-transform: rotate(360deg);
    transform: rotate(360deg);
  }
}

@keyframes loadingCircle {
  100% {
    -webkit-transform: rotate(360deg);
    transform: rotate(360deg);
  }
}
`,k=e=>{let{csp:t,prefixCls:n,layer:a,zeroRuntime:o}=(0,r.useContext)(i),s=O;n&&(s=s.replace(/anticon/g,n)),a&&(s=`@layer ${a} {\n${s}\n}`),(0,r.useEffect)(()=>{if(o)return;let n=e.current,r=b(n);v(s,`@ant-design-icons`,{prepend:!a,csp:t,attachTo:r})},[])},A=e=>{let{icon:t,className:n,onClick:i,style:a,primaryColor:o,secondaryColor:s,...c}=e,l=r.useRef(null);if(k(l),w(T(t),`icon should be icon definiton, but got ${t}`),!T(t))return null;let u=t;return D(u.icon,`svg-${u.name}`,{className:n,onClick:i,style:a,"data-icon":u.name,width:`1em`,height:`1em`,fill:`currentColor`,"aria-hidden":`true`,...c,ref:l})};A.displayName=`IconReact`;function j(){return j=Object.assign?Object.assign.bind():function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var r in n)Object.prototype.hasOwnProperty.call(n,r)&&(e[r]=n[r])}return e},j.apply(this,arguments)}var M=r.forwardRef((e,n)=>{let{className:a,icon:o,spin:s,rotate:c,tabIndex:l,onClick:u,twoToneColor:d,...f}=e,{prefixCls:p=`anticon`,rootClassName:m}=r.useContext(i),h=t(m,p,{[`${p}-${o.name}`]:!!o.name,[`${p}-spin`]:!!s||o.name===`loading`},a),g=l;g===void 0&&u&&(g=-1);let _=c?{msTransform:`rotate(${c}deg)`,transform:`rotate(${c}deg)`}:void 0;return r.createElement(`span`,j({role:`img`,"aria-label":o.name},f,{ref:n,tabIndex:g,onClick:u,className:h}),r.createElement(A,{icon:o,style:_}))});export{i as n,M as t};