var BytedeskWeb=function(){"use strict";var E=Object.defineProperty;var C=(g,c,t)=>c in g?E(g,c,{enumerable:!0,configurable:!0,writable:!0,value:t}):g[c]=t;var b=(g,c,t)=>C(g,typeof c!="symbol"?c+"":c,t);class g{constructor(t){b(this,"config");b(this,"bubble",null);b(this,"window",null);b(this,"isVisible",!1);b(this,"isDragging",!1);b(this,"windowState","normal");this.config={...this.getDefaultConfig(),...t}}getDefaultConfig(){return{baseUrl:"https://www.weiyuai.cn/chat",placement:"bottom-right",marginBottom:20,marginSide:20,autoPopup:!1,tabsConfig:{home:!1,messages:!0,help:!1,news:!1},bubbleConfig:{show:!0,icon:"👋",title:"需要帮助吗？",subtitle:"点击开始对话"},showSupport:!0,chatParams:{org:"df_org_uid",t:"2",sid:"df_rt_uid"},animation:{enabled:!0,duration:300,type:"ease"},theme:{mode:"system",textColor:"#ffffff",backgroundColor:"#0066FF"},window:{width:380,height:640},draggable:!1,locale:"zh-cn"}}init(){this.createBubble(),this.setupMessageListener(),this.setupResizeListener(),this.preload(),this.config.autoPopup&&setTimeout(()=>{this.showChat()},this.config.autoPopupDelay||1e3)}createBubble(){var o,a,r,m,p;const t=document.createElement("div");t.style.cssText=`
      position: fixed;
      ${this.config.placement==="bottom-left"?"left":"right"}: ${this.config.marginSide}px;
      bottom: ${this.config.marginBottom}px;
      display: flex;
      flex-direction: column;
      align-items: ${this.config.placement==="bottom-left"?"flex-start":"flex-end"};
      gap: 10px;
      z-index: 9999;
    `;let e=null;if((o=this.config.bubbleConfig)!=null&&o.show){e=document.createElement("div"),e.style.cssText=`
        background: white;
        padding: 12px 16px;
        border-radius: 8px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
        max-width: 220px;
        margin-bottom: 8px;
        opacity: 0;
        transform: translateY(10px);
        transition: all 0.3s ease;
        position: relative;
      `;const s=document.createElement("div");s.style.cssText=`
        display: flex;
        align-items: center;
        gap: 8px;
      `;const l=document.createElement("span");l.textContent=((a=this.config.bubbleConfig)==null?void 0:a.icon)||"",l.style.fontSize="20px",s.appendChild(l);const d=document.createElement("div"),h=document.createElement("div");h.textContent=((r=this.config.bubbleConfig)==null?void 0:r.title)||"",h.style.fontWeight="bold",h.style.marginBottom="4px",d.appendChild(h);const n=document.createElement("div");n.textContent=((m=this.config.bubbleConfig)==null?void 0:m.subtitle)||"",n.style.fontSize="0.9em",n.style.opacity="0.8",d.appendChild(n),s.appendChild(d),e.appendChild(s);const f=document.createElement("div");f.style.cssText=`
        position: absolute;
        bottom: -6px;
        ${this.config.placement==="bottom-left"?"left: 24px":"right: 24px"};
        width: 12px;
        height: 12px;
        background: white;
        transform: rotate(45deg);
        box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
      `;const w=document.createElement("div");w.style.cssText=`
        position: absolute;
        bottom: 0;
        ${this.config.placement==="bottom-left"?"left: 18px":"right: 18px"};
        width: 24px;
        height: 12px;
        background: white;
      `,e.appendChild(f),e.appendChild(w),t.appendChild(e),setTimeout(()=>{e&&(e.style.opacity="1",e.style.transform="translateY(0)")},500)}this.bubble=document.createElement("button"),this.bubble.style.cssText=`
      background-color: ${(p=this.config.theme)==null?void 0:p.backgroundColor};
      width: 60px;
      height: 60px;
      border-radius: 30px;
      border: none;
      cursor: ${this.config.draggable?"move":"pointer"};
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
      transition: all 0.3s ease;
      outline: none;
      position: relative;
      user-select: none;
    `;const i=document.createElement("div");if(i.innerHTML=`
      <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 14.663 3.04094 17.0829 4.73812 18.875L2.72681 21.1705C2.44361 21.4937 2.67314 22 3.10288 22H12Z" 
              fill="white"/>
      </svg>
    `,i.style.cssText=`
      display: flex;
      align-items: center;
      justify-content: center;
    `,this.bubble.appendChild(i),this.bubble.addEventListener("mouseenter",()=>{this.bubble.style.transform="scale(1.1)"}),this.bubble.addEventListener("mouseleave",()=>{this.bubble.style.transform="scale(1)"}),t.appendChild(this.bubble),this.config.draggable){let s=0,l=0,d=0,h=0;this.bubble.addEventListener("mousedown",n=>{n.button===0&&(this.isDragging=!0,s=n.clientX,l=n.clientY,d=t.offsetLeft,h=t.offsetTop,t.style.transition="none")}),document.addEventListener("mousemove",n=>{if(!this.isDragging)return;n.preventDefault();const f=n.clientX-s,w=n.clientY-l,u=d+f,x=h+w,y=window.innerHeight-t.offsetHeight;u<=window.innerWidth/2?(t.style.left=`${Math.max(0,u)}px`,t.style.right="auto",this.config.placement="bottom-left"):(t.style.right=`${Math.max(0,window.innerWidth-u-t.offsetWidth)}px`,t.style.left="auto",this.config.placement="bottom-right"),t.style.bottom=`${Math.min(Math.max(0,window.innerHeight-x-t.offsetHeight),y)}px`}),document.addEventListener("mouseup",()=>{this.isDragging&&(this.isDragging=!1,t.style.transition="all 0.3s ease",this.config.marginSide=parseInt(this.config.placement==="bottom-left"?t.style.left:t.style.right)||20,this.config.marginBottom=parseInt(t.style.bottom||"20"))})}this.bubble.addEventListener("click",()=>{if(!this.isDragging){console.log("bubble click");const s=this.bubble.messageElement;s instanceof HTMLElement&&(s.style.display="none"),this.showChat()}}),this.bubble.messageElement=e,document.body.appendChild(t)}getSupportText(){const t=this.config.locale||"zh-cn",e={"zh-cn":"微语技术支持",en:"Powered by Weiyuai","ja-JP":"Weiyuaiによる技術支援","ko-KR":"Weiyuai 기술 지원"};return e[t]||e["zh-cn"]}createChatWindow(){var m,p,s,l,d,h;this.window=document.createElement("div");const t=window.innerWidth<=768,e=window.innerWidth,i=window.innerHeight,o=Math.min(((m=this.config.window)==null?void 0:m.width)||e*.9,e*.9),a=Math.min(((p=this.config.window)==null?void 0:p.height)||i*.9,i*.9);t?this.window.style.cssText=`
        position: fixed;
        left: 0;
        bottom: 0;
        width: 100%;
        height: 90vh;
        display: none;
        z-index: 10000;
        border-top-left-radius: 12px;
        border-top-right-radius: 12px;
        overflow: hidden;
        transition: all ${(s=this.config.animation)==null?void 0:s.duration}ms ${(l=this.config.animation)==null?void 0:l.type};
      `:this.window.style.cssText=`
        position: fixed;
        ${this.config.placement==="bottom-right"?"right":"left"}: ${this.config.marginSide}px;
        bottom: ${this.config.marginBottom}px;
        width: ${o}px;
        height: ${a}px;
        border-radius: 12px;
        box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        display: none;
        overflow: hidden;
        z-index: 10000;
        transition: all ${(d=this.config.animation)==null?void 0:d.duration}ms ${(h=this.config.animation)==null?void 0:h.type};
      `;const r=document.createElement("iframe");if(r.style.cssText=`
      width: 100%;
      height: ${this.config.showSupport?"calc(100% - 30px)":"100%"};
      border: none;
    `,r.src=this.generateChatUrl(),console.log("iframe.src: ",r.src),this.window.appendChild(r),this.config.showSupport){const n=document.createElement("div");n.style.cssText=`
        height: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #666;
        font-size: 12px;
        line-height: 30px;
      `,n.innerHTML=`
        <a href="https://ai.bytedesk.com" 
           target="_blank" 
           style="
             color: #666;
             text-decoration: none;
             display: flex;
             align-items: center;
             height: 100%;
           ">
          ${this.getSupportText()}
        </a>
      `,this.window.appendChild(n)}document.body.appendChild(this.window)}generateChatUrl(t=!1,e="messages"){console.log("this.config: ",this.config,e);const i=new URLSearchParams;return Object.entries(this.config.chatParams||{}).forEach(([o,a])=>{i.append(o,String(a))}),Object.entries(this.config.browseParams||{}).forEach(([o,a])=>{i.append(o,String(a))}),Object.entries(this.config.theme||{}).forEach(([o,a])=>{i.append(o,String(a))}),i.append("lang",this.config.locale||"zh-cn"),t&&i.append("preload","1"),`${this.config.baseUrl}?${i.toString()}`}setupMessageListener(){window.addEventListener("message",t=>{switch(t.data.type){case"CLOSE_CHAT_WINDOW":this.hideChat();break;case"MAXIMIZE_WINDOW":this.toggleMaximize();break;case"MINIMIZE_WINDOW":this.minimizeWindow();break;case"RECEIVE_MESSAGE":console.log("RECEIVE_MESSAGE");break;case"INVITE_VISITOR":console.log("INVITE_VISITOR");break;case"INVITE_VISITOR_ACCEPT":console.log("INVITE_VISITOR_ACCEPT");break;case"INVITE_VISITOR_REJECT":console.log("INVITE_VISITOR_REJECT");break}})}preload(){console.log("preload");const t=this.generateChatUrl(!0);console.log("preLoadUrl: ",t);const e=document.createElement("iframe");e.src=t,e.style.display="none",document.body.appendChild(e)}showChat(){if(this.window||this.createChatWindow(),this.window){const t=window.innerWidth<=768;if(this.window.style.display="block",this.setupResizeListener(),t&&this.window&&(this.window.style.transform="translateY(100%)",requestAnimationFrame(()=>{this.window&&(this.window.style.transform="translateY(0)")})),this.isVisible=!0,this.bubble){this.bubble.style.display="none";const e=this.bubble.messageElement;e instanceof HTMLElement&&(e.style.display="none")}}}hideChat(){var t;if(this.window&&(window.innerWidth<=768?(this.window.style.transform="translateY(100%)",setTimeout(()=>{this.window&&(this.window.style.display="none")},((t=this.config.animation)==null?void 0:t.duration)||300)):this.window.style.display="none",this.isVisible=!1,this.bubble)){this.bubble.style.display="inline-flex";const i=this.bubble.messageElement;i instanceof HTMLElement&&(i.style.display="block")}}minimizeWindow(){this.window&&(this.windowState="minimized",this.window.style.display="none")}toggleMaximize(){!this.window||window.innerWidth<=768||(this.windowState=this.windowState==="maximized"?"normal":"maximized",this.setupResizeListener())}setupResizeListener(){const t=()=>{var r,m;if(!this.window||!this.isVisible)return;const i=window.innerWidth<=768,o=window.innerWidth,a=window.innerHeight;if(i)Object.assign(this.window.style,{left:"0",bottom:"0",width:"100%",height:"90vh",borderTopLeftRadius:"12px",borderTopRightRadius:"12px",borderBottomLeftRadius:"0",borderBottomRightRadius:"0"});else{let p=this.windowState==="maximized"?o:Math.min(((r=this.config.window)==null?void 0:r.width)||o*.9,o*.9),s=this.windowState==="maximized"?a:Math.min(((m=this.config.window)==null?void 0:m.height)||a*.9,a*.9);const l=this.config.placement==="bottom-right"?this.config.marginSide:void 0,d=this.config.placement==="bottom-left"?this.config.marginSide:void 0;Object.assign(this.window.style,{width:`${p}px`,height:`${s}px`,right:l?`${l}px`:"auto",left:d?`${d}px`:"auto",bottom:`${this.config.marginBottom}px`,borderRadius:this.windowState==="maximized"?"0":"12px"})}};let e;window.addEventListener("resize",()=>{clearTimeout(e),e=window.setTimeout(t,100)}),t()}destroy(){var e;const t=(e=this.bubble)==null?void 0:e.parentElement;t&&document.body.contains(t)&&(document.body.removeChild(t),this.bubble=null),this.window&&document.body.contains(this.window)&&(document.body.removeChild(this.window),this.window=null),window.removeEventListener("resize",this.setupResizeListener.bind(this))}}return g}();
