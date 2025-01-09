var BytedeskWeb=function(){"use strict";var v=Object.defineProperty;var C=(f,p,t)=>p in f?v(f,p,{enumerable:!0,configurable:!0,writable:!0,value:t}):f[p]=t;var g=(f,p,t)=>C(f,typeof p!="symbol"?p+"":p,t);class f{constructor(t){g(this,"config");g(this,"bubble",null);g(this,"window",null);g(this,"inviteDialog",null);g(this,"isVisible",!1);g(this,"isDragging",!1);g(this,"windowState","normal");g(this,"loopCount",0);g(this,"loopTimer",null);this.config={...this.getDefaultConfig(),...t}}getDefaultConfig(){return{baseUrl:"https://www.weiyuai.cn/chat",placement:"bottom-right",marginBottom:20,marginSide:20,autoPopup:!1,tabsConfig:{home:!1,messages:!0,help:!1,news:!1},bubbleConfig:{show:!0,icon:"👋",title:"需要帮助吗？",subtitle:"点击开始对话"},showSupport:!0,chatParams:{org:"df_org_uid",t:"2",sid:"df_rt_uid"},animation:{enabled:!0,duration:300,type:"ease"},theme:{mode:"system",textColor:"#ffffff",backgroundColor:"#0066FF"},window:{width:380,height:640},draggable:!1,locale:"zh-cn"}}init(){var t;this.createBubble(),this.createInviteDialog(),this.setupMessageListener(),this.setupResizeListener(),this.preload(),this.config.autoPopup&&setTimeout(()=>{this.showChat()},this.config.autoPopupDelay||1e3),(t=this.config.inviteParams)!=null&&t.show&&setTimeout(()=>{this.showInviteDialog()},this.config.inviteParams.delay||3e3)}createBubble(){var o,s,r,n,d;const t=document.createElement("div");t.style.cssText=`
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
      `;const l=document.createElement("div");l.style.cssText=`
        display: flex;
        align-items: center;
        gap: 8px;
      `;const h=document.createElement("span");h.textContent=((s=this.config.bubbleConfig)==null?void 0:s.icon)||"",h.style.fontSize="20px",l.appendChild(h);const c=document.createElement("div"),m=document.createElement("div");m.textContent=((r=this.config.bubbleConfig)==null?void 0:r.title)||"",m.style.fontWeight="bold",m.style.marginBottom="4px",c.appendChild(m);const a=document.createElement("div");a.textContent=((n=this.config.bubbleConfig)==null?void 0:n.subtitle)||"",a.style.fontSize="0.9em",a.style.opacity="0.8",c.appendChild(a),l.appendChild(c),e.appendChild(l);const b=document.createElement("div");b.style.cssText=`
        position: absolute;
        bottom: -6px;
        ${this.config.placement==="bottom-left"?"left: 24px":"right: 24px"};
        width: 12px;
        height: 12px;
        background: white;
        transform: rotate(45deg);
        box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
      `;const u=document.createElement("div");u.style.cssText=`
        position: absolute;
        bottom: 0;
        ${this.config.placement==="bottom-left"?"left: 18px":"right: 18px"};
        width: 24px;
        height: 12px;
        background: white;
      `,e.appendChild(b),e.appendChild(u),t.appendChild(e),setTimeout(()=>{e&&(e.style.opacity="1",e.style.transform="translateY(0)")},500)}this.bubble=document.createElement("button"),this.bubble.style.cssText=`
      background-color: ${(d=this.config.theme)==null?void 0:d.backgroundColor};
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
    `,this.bubble.appendChild(i),this.bubble.addEventListener("mouseenter",()=>{this.bubble.style.transform="scale(1.1)"}),this.bubble.addEventListener("mouseleave",()=>{this.bubble.style.transform="scale(1)"}),t.appendChild(this.bubble),this.config.draggable){let l=0,h=0,c=0,m=0;this.bubble.addEventListener("mousedown",a=>{a.button===0&&(this.isDragging=!0,l=a.clientX,h=a.clientY,c=t.offsetLeft,m=t.offsetTop,t.style.transition="none")}),document.addEventListener("mousemove",a=>{if(!this.isDragging)return;a.preventDefault();const b=a.clientX-l,u=a.clientY-h,w=c+b,x=m+u,y=window.innerHeight-t.offsetHeight;w<=window.innerWidth/2?(t.style.left=`${Math.max(0,w)}px`,t.style.right="auto",this.config.placement="bottom-left"):(t.style.right=`${Math.max(0,window.innerWidth-w-t.offsetWidth)}px`,t.style.left="auto",this.config.placement="bottom-right"),t.style.bottom=`${Math.min(Math.max(0,window.innerHeight-x-t.offsetHeight),y)}px`}),document.addEventListener("mouseup",()=>{this.isDragging&&(this.isDragging=!1,t.style.transition="all 0.3s ease",this.config.marginSide=parseInt(this.config.placement==="bottom-left"?t.style.left:t.style.right)||20,this.config.marginBottom=parseInt(t.style.bottom||"20"))})}this.bubble.addEventListener("click",()=>{if(!this.isDragging){console.log("bubble click");const l=this.bubble.messageElement;l instanceof HTMLElement&&(l.style.display="none"),this.showChat()}}),this.bubble.messageElement=e,document.body.appendChild(t)}getSupportText(){const t=this.config.locale||"zh-cn",e={"zh-cn":"微语技术支持",en:"Powered by Weiyuai","ja-JP":"Weiyuaiによる技術支援","ko-KR":"Weiyuai 기술 지원"};return e[t]||e["zh-cn"]}createChatWindow(){var n,d,l,h,c,m;this.window=document.createElement("div");const t=window.innerWidth<=768,e=window.innerWidth,i=window.innerHeight,o=Math.min(((n=this.config.window)==null?void 0:n.width)||e*.9,e*.9),s=Math.min(((d=this.config.window)==null?void 0:d.height)||i*.9,i*.9);t?this.window.style.cssText=`
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
        transition: all ${(l=this.config.animation)==null?void 0:l.duration}ms ${(h=this.config.animation)==null?void 0:h.type};
      `:this.window.style.cssText=`
        position: fixed;
        ${this.config.placement==="bottom-right"?"right":"left"}: ${this.config.marginSide}px;
        bottom: ${this.config.marginBottom}px;
        width: ${o}px;
        height: ${s}px;
        border-radius: 12px;
        box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        display: none;
        overflow: hidden;
        z-index: 10000;
        transition: all ${(c=this.config.animation)==null?void 0:c.duration}ms ${(m=this.config.animation)==null?void 0:m.type};
      `;const r=document.createElement("iframe");if(r.style.cssText=`
      width: 100%;
      height: ${this.config.showSupport?"calc(100% - 30px)":"100%"};
      border: none;
    `,r.src=this.generateChatUrl(),console.log("iframe.src: ",r.src),this.window.appendChild(r),this.config.showSupport){const a=document.createElement("div");a.style.cssText=`
        height: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #666;
        font-size: 12px;
        line-height: 30px;
      `,a.innerHTML=`
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
      `,this.window.appendChild(a)}document.body.appendChild(this.window)}generateChatUrl(t=!1,e="messages"){console.log("this.config: ",this.config,e);const i=new URLSearchParams;return Object.entries(this.config.chatParams||{}).forEach(([o,s])=>{i.append(o,String(s))}),Object.entries(this.config.browseParams||{}).forEach(([o,s])=>{i.append(o,String(s))}),Object.entries(this.config.theme||{}).forEach(([o,s])=>{i.append(o,String(s))}),i.append("lang",this.config.locale||"zh-cn"),t&&i.append("preload","1"),`${this.config.baseUrl}?${i.toString()}`}setupMessageListener(){window.addEventListener("message",t=>{switch(t.data.type){case"CLOSE_CHAT_WINDOW":this.hideChat();break;case"MAXIMIZE_WINDOW":this.toggleMaximize();break;case"MINIMIZE_WINDOW":this.minimizeWindow();break;case"RECEIVE_MESSAGE":console.log("RECEIVE_MESSAGE");break;case"INVITE_VISITOR":console.log("INVITE_VISITOR");break;case"INVITE_VISITOR_ACCEPT":console.log("INVITE_VISITOR_ACCEPT");break;case"INVITE_VISITOR_REJECT":console.log("INVITE_VISITOR_REJECT");break}})}preload(){console.log("preload");const t=this.generateChatUrl(!0);console.log("preLoadUrl: ",t);const e=document.createElement("iframe");e.src=t,e.style.display="none",document.body.appendChild(e)}showChat(){if(this.window||this.createChatWindow(),this.window){const t=window.innerWidth<=768;if(this.window.style.display="block",this.setupResizeListener(),t&&this.window&&(this.window.style.transform="translateY(100%)",requestAnimationFrame(()=>{this.window&&(this.window.style.transform="translateY(0)")})),this.isVisible=!0,this.bubble){this.bubble.style.display="none";const e=this.bubble.messageElement;e instanceof HTMLElement&&(e.style.display="none")}}this.hideInviteDialog()}hideChat(){var t;if(this.window&&(window.innerWidth<=768?(this.window.style.transform="translateY(100%)",setTimeout(()=>{this.window&&(this.window.style.display="none")},((t=this.config.animation)==null?void 0:t.duration)||300)):this.window.style.display="none",this.isVisible=!1,this.bubble)){this.bubble.style.display="inline-flex";const i=this.bubble.messageElement;i instanceof HTMLElement&&(i.style.display="block")}}minimizeWindow(){this.window&&(this.windowState="minimized",this.window.style.display="none")}toggleMaximize(){!this.window||window.innerWidth<=768||(this.windowState=this.windowState==="maximized"?"normal":"maximized",this.setupResizeListener())}setupResizeListener(){const t=()=>{var r,n;if(!this.window||!this.isVisible)return;const i=window.innerWidth<=768,o=window.innerWidth,s=window.innerHeight;if(i)Object.assign(this.window.style,{left:"0",bottom:"0",width:"100%",height:"90vh",borderTopLeftRadius:"12px",borderTopRightRadius:"12px",borderBottomLeftRadius:"0",borderBottomRightRadius:"0"});else{let d=this.windowState==="maximized"?o:Math.min(((r=this.config.window)==null?void 0:r.width)||o*.9,o*.9),l=this.windowState==="maximized"?s:Math.min(((n=this.config.window)==null?void 0:n.height)||s*.9,s*.9);const h=this.config.placement==="bottom-right"?this.config.marginSide:void 0,c=this.config.placement==="bottom-left"?this.config.marginSide:void 0;Object.assign(this.window.style,{width:`${d}px`,height:`${l}px`,right:h?`${h}px`:"auto",left:c?`${c}px`:"auto",bottom:`${this.config.marginBottom}px`,borderRadius:this.windowState==="maximized"?"0":"12px"})}};let e;window.addEventListener("resize",()=>{clearTimeout(e),e=window.setTimeout(t,100)}),t()}destroy(){var e;const t=(e=this.bubble)==null?void 0:e.parentElement;t&&document.body.contains(t)&&(document.body.removeChild(t),this.bubble=null),this.window&&document.body.contains(this.window)&&(document.body.removeChild(this.window),this.window=null),window.removeEventListener("resize",this.setupResizeListener.bind(this)),this.loopTimer&&(window.clearTimeout(this.loopTimer),this.loopTimer=null),this.inviteDialog&&document.body.contains(this.inviteDialog)&&(document.body.removeChild(this.inviteDialog),this.inviteDialog=null)}createInviteDialog(){var s,r;if(!((s=this.config.inviteParams)!=null&&s.show))return;if(this.inviteDialog=document.createElement("div"),this.inviteDialog.style.cssText=`
      position: fixed;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      background: white;
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
      z-index: 10001;
      display: none;
      max-width: 300px;
      text-align: center;
    `,this.config.inviteParams.icon){const n=document.createElement("div");n.style.cssText=`
        font-size: 32px;
        margin-bottom: 12px;
      `,n.textContent=this.config.inviteParams.icon,this.inviteDialog.appendChild(n)}const t=document.createElement("div");t.style.cssText=`
      margin-bottom: 16px;
      color: #333;
    `,t.textContent=this.config.inviteParams.text||"需要帮助吗？点击开始对话",this.inviteDialog.appendChild(t);const e=document.createElement("div");e.style.cssText=`
      display: flex;
      gap: 10px;
      justify-content: center;
    `;const i=document.createElement("button");i.textContent="开始对话",i.style.cssText=`
      padding: 8px 16px;
      background: ${((r=this.config.theme)==null?void 0:r.backgroundColor)||"#0066FF"};
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `,i.onclick=()=>{var n,d;this.hideInviteDialog(),this.showChat(),(d=(n=this.config.inviteParams)==null?void 0:n.onAccept)==null||d.call(n)};const o=document.createElement("button");o.textContent="稍后再说",o.style.cssText=`
      padding: 8px 16px;
      background: #f5f5f5;
      color: #666;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `,o.onclick=()=>{var n,d;this.hideInviteDialog(),(d=(n=this.config.inviteParams)==null?void 0:n.onReject)==null||d.call(n),this.handleInviteLoop()},e.appendChild(i),e.appendChild(o),this.inviteDialog.appendChild(e),document.body.appendChild(this.inviteDialog)}handleInviteLoop(){const{loop:t,loopDelay:e=3e3,loopCount:i=1/0}=this.config.inviteParams||{};!t||this.loopCount>=i-1||(this.loopTimer&&window.clearTimeout(this.loopTimer),this.loopTimer=window.setTimeout(()=>{this.loopCount++,this.showInviteDialog()},e))}showInviteDialog(){var t,e;this.inviteDialog&&(this.inviteDialog.style.display="block",(e=(t=this.config.inviteParams)==null?void 0:t.onOpen)==null||e.call(t))}hideInviteDialog(){var t,e;this.inviteDialog&&(this.inviteDialog.style.display="none",(e=(t=this.config.inviteParams)==null?void 0:t.onClose)==null||e.call(t))}}return f}();
