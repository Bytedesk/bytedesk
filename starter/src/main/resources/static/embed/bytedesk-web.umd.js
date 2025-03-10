(function(f,d){typeof exports=="object"&&typeof module<"u"?module.exports=d():typeof define=="function"&&define.amd?define(d):(f=typeof globalThis<"u"?globalThis:f||self,f.BytedeskWeb=d())})(this,function(){"use strict";var z=Object.defineProperty;var W=(f,d,t)=>d in f?z(f,d,{enumerable:!0,configurable:!0,writable:!0,value:t}):f[d]=t;var r=(f,d,t)=>W(f,typeof d!="symbol"?d+"":d,t);class f{constructor(t){r(this,"config");r(this,"bubble",null);r(this,"window",null);r(this,"inviteDialog",null);r(this,"contextMenu",null);r(this,"hideTimeout",null);r(this,"isVisible",!1);r(this,"isDragging",!1);r(this,"windowState","normal");r(this,"loopCount",0);r(this,"loopTimer",null);this.config={...this.getDefaultConfig(),...t}}getDefaultConfig(){return{isDebug:!1,isPreload:!1,baseUrl:"https://cdn.weiyuai.cn/chat",placement:"bottom-right",marginBottom:20,marginSide:20,autoPopup:!1,tabsConfig:{home:!1,messages:!0,help:!1,news:!1},bubbleConfig:{show:!0,icon:"👋",title:"需要帮助吗？",subtitle:"点击开始对话"},buttonConfig:{show:!0,width:60,height:60,onClick:()=>{this.showChat()}},showSupport:!0,chatConfig:{org:"df_org_uid",t:"2",sid:"df_rt_uid"},animation:{enabled:!0,duration:300,type:"ease"},theme:{mode:"system",textColor:"#ffffff",backgroundColor:"#0066FF"},window:{width:380,height:640},draggable:!1,locale:"zh-cn"}}init(){var t;this.createBubble(),this.createInviteDialog(),this.setupMessageListener(),this.setupResizeListener(),this.preload(),this.config.autoPopup&&setTimeout(()=>{this.showChat()},this.config.autoPopupDelay||1e3),(t=this.config.inviteConfig)!=null&&t.show&&setTimeout(()=>{this.showInviteDialog()},this.config.inviteConfig.delay||3e3)}createBubble(){var h,u,g,p,y,b,T,M,I,k,D,$;const t=document.createElement("div");t.style.cssText=`
      position: fixed;
      ${this.config.placement==="bottom-left"?"left":"right"}: ${this.config.marginSide}px;
      bottom: ${this.config.marginBottom}px;
      display: flex;
      flex-direction: column;
      align-items: ${this.config.placement==="bottom-left"?"flex-start":"flex-end"};
      gap: 10px;
      z-index: 9999;
    `;let e=null;if((h=this.config.bubbleConfig)!=null&&h.show){e=document.createElement("div"),e.style.cssText=`
        background: ${((u=this.config.theme)==null?void 0:u.mode)==="dark"?"#1f2937":"white"};
        color: ${((g=this.config.theme)==null?void 0:g.mode)==="dark"?"#e5e7eb":"#1f2937"};
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
      `;const x=document.createElement("span");x.textContent=((p=this.config.bubbleConfig)==null?void 0:p.icon)||"",x.style.fontSize="20px",l.appendChild(x);const w=document.createElement("div"),m=document.createElement("div");m.textContent=((y=this.config.bubbleConfig)==null?void 0:y.title)||"",m.style.fontWeight="bold",m.style.color=((b=this.config.theme)==null?void 0:b.mode)==="dark"?"#e5e7eb":"#1f2937",m.style.marginBottom="4px",w.appendChild(m);const c=document.createElement("div");c.textContent=((T=this.config.bubbleConfig)==null?void 0:T.subtitle)||"",c.style.fontSize="0.9em",c.style.color=((M=this.config.theme)==null?void 0:M.mode)==="dark"?"#9ca3af":"#4b5563",w.appendChild(c),l.appendChild(w),e.appendChild(l);const C=document.createElement("div");C.style.cssText=`
        position: absolute;
        bottom: -6px;
        ${this.config.placement==="bottom-left"?"left: 24px":"right: 24px"};
        width: 12px;
        height: 12px;
        background: ${((I=this.config.theme)==null?void 0:I.mode)==="dark"?"#1f2937":"white"};
        transform: rotate(45deg);
        box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
      `;const v=document.createElement("div");v.style.cssText=`
        position: absolute;
        bottom: 0;
        ${this.config.placement==="bottom-left"?"left: 18px":"right: 18px"};
        width: 24px;
        height: 12px;
        background: ${((k=this.config.theme)==null?void 0:k.mode)==="dark"?"#1f2937":"white"};
      `,e.appendChild(C),e.appendChild(v),t.appendChild(e),setTimeout(()=>{e&&(e.style.opacity="1",e.style.transform="translateY(0)")},500)}this.bubble=document.createElement("button");const n=this.config.buttonConfig||{},i=n.width||60,o=n.height||60,a=Math.min(i,o)/2;this.bubble.style.cssText=`
      background-color: ${(D=this.config.theme)==null?void 0:D.backgroundColor};
      width: ${i}px;
      height: ${o}px;
      border-radius: ${a}px;
      border: none;
      cursor: ${this.config.draggable?"move":"pointer"};
      display: ${n.show===!1?"none":"flex"};
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
      transition: all 0.3s ease;
      outline: none;
      position: relative;
      user-select: none;
    `;const s=document.createElement("div");if(s.style.cssText=`
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    `,n.icon){const l=document.createElement("span");l.textContent=n.icon,l.style.fontSize=`${o*.4}px`,s.appendChild(l)}else{const l=document.createElement("div");l.innerHTML=`
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 14.663 3.04094 17.0829 4.73812 18.875L2.72681 21.1705C2.44361 21.4937 2.67314 22 3.10288 22H12Z" 
                fill="white"/>
        </svg>
      `,s.appendChild(l)}if(n.text){const l=document.createElement("span");l.textContent=n.text,l.style.cssText=`
        color: ${(($=this.config.theme)==null?void 0:$.textColor)||"#ffffff"};
        font-size: ${o*.25}px;
        white-space: nowrap;
      `,s.appendChild(l)}if(this.bubble.appendChild(s),this.bubble.addEventListener("mouseenter",()=>{this.bubble.style.transform="scale(1.1)"}),this.bubble.addEventListener("mouseleave",()=>{this.bubble.style.transform="scale(1)"}),t.appendChild(this.bubble),this.config.draggable){let l=0,x=0,w=0,m=0;this.bubble.addEventListener("mousedown",c=>{c.button===0&&(this.isDragging=!0,l=c.clientX,x=c.clientY,w=t.offsetLeft,m=t.offsetTop,t.style.transition="none")}),document.addEventListener("mousemove",c=>{if(!this.isDragging)return;c.preventDefault();const C=c.clientX-l,v=c.clientY-x,E=w+C,S=m+v,L=window.innerHeight-t.offsetHeight;E<=window.innerWidth/2?(t.style.left=`${Math.max(0,E)}px`,t.style.right="auto",this.config.placement="bottom-left"):(t.style.right=`${Math.max(0,window.innerWidth-E-t.offsetWidth)}px`,t.style.left="auto",this.config.placement="bottom-right"),t.style.bottom=`${Math.min(Math.max(0,window.innerHeight-S-t.offsetHeight),L)}px`}),document.addEventListener("mouseup",()=>{this.isDragging&&(this.isDragging=!1,t.style.transition="all 0.3s ease",this.config.marginSide=parseInt(this.config.placement==="bottom-left"?t.style.left:t.style.right)||20,this.config.marginBottom=parseInt(t.style.bottom||"20"))})}this.bubble.addEventListener("click",()=>{if(!this.isDragging){console.log("bubble click");const l=this.bubble.messageElement;l instanceof HTMLElement&&(l.style.display="none"),this.showChat()}}),this.bubble.messageElement=e,document.body.appendChild(t),this.bubble.addEventListener("contextmenu",l=>{this.showContextMenu(l)}),document.addEventListener("click",()=>{this.hideContextMenu()})}getSupportText(){const t=this.config.locale||"zh-cn",e={"zh-cn":"微语技术支持",en:"Powered by Weiyuai","ja-JP":"Weiyuaiによる技術支援","ko-KR":"Weiyuai 기술 지원"};return e[t]||e["zh-cn"]}createChatWindow(){var s,h,u,g,p,y;this.window=document.createElement("div");const t=window.innerWidth<=768,e=window.innerWidth,n=window.innerHeight,i=Math.min(((s=this.config.window)==null?void 0:s.width)||e*.9,e*.9),o=Math.min(((h=this.config.window)==null?void 0:h.height)||n*.9,n*.9);t?this.window.style.cssText=`
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
        transition: all ${(u=this.config.animation)==null?void 0:u.duration}ms ${(g=this.config.animation)==null?void 0:g.type};
      `:this.window.style.cssText=`
        position: fixed;
        ${this.config.placement==="bottom-right"?"right":"left"}: ${this.config.marginSide}px;
        bottom: ${this.config.marginBottom}px;
        width: ${i}px;
        height: ${o}px;
        border-radius: 12px;
        box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        display: none;
        overflow: hidden;
        z-index: 10000;
        transition: all ${(p=this.config.animation)==null?void 0:p.duration}ms ${(y=this.config.animation)==null?void 0:y.type};
      `;const a=document.createElement("iframe");if(a.style.cssText=`
      width: 100%;
      height: ${this.config.showSupport?"calc(100% - 30px)":"100%"};
      border: none;
    `,a.src=this.generateChatUrl(),console.log("iframe.src: ",a.src),this.window.appendChild(a),this.config.showSupport){const b=document.createElement("div");b.style.cssText=`
        height: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #666;
        font-size: 12px;
        line-height: 30px;
        background: #ffffff;
      `,b.innerHTML=`
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
      `,this.window.appendChild(b)}document.body.appendChild(this.window)}generateChatUrl(t=!1,e="messages"){console.log("this.config: ",this.config,e);const n=new URLSearchParams;return Object.entries(this.config.chatConfig||{}).forEach(([i,o])=>{n.append(i,String(o))}),Object.entries(this.config.browseConfig||{}).forEach(([i,o])=>{n.append(i,String(o))}),Object.entries(this.config.theme||{}).forEach(([i,o])=>{n.append(i,String(o))}),n.append("lang",this.config.locale||"zh-cn"),t&&n.append("preload","1"),`${this.config.baseUrl}?${n.toString()}`}setupMessageListener(){window.addEventListener("message",t=>{switch(t.data.type){case"CLOSE_CHAT_WINDOW":this.hideChat();break;case"MAXIMIZE_WINDOW":this.toggleMaximize();break;case"MINIMIZE_WINDOW":this.minimizeWindow();break;case"RECEIVE_MESSAGE":console.log("RECEIVE_MESSAGE");break;case"INVITE_VISITOR":console.log("INVITE_VISITOR");break;case"INVITE_VISITOR_ACCEPT":console.log("INVITE_VISITOR_ACCEPT");break;case"INVITE_VISITOR_REJECT":console.log("INVITE_VISITOR_REJECT");break}})}preload(){if(console.log("preload"),this.config.isPreload){const t=this.generateChatUrl(!0);console.log("preLoadUrl: ",t);const e=document.createElement("iframe");e.src=t,e.style.display="none",document.body.appendChild(e)}}showChat(){var t,e;if(this.window||this.createChatWindow(),this.window){const n=window.innerWidth<=768;if(this.window.style.display="block",this.setupResizeListener(),n&&this.window&&(this.window.style.transform="translateY(100%)",requestAnimationFrame(()=>{this.window&&(this.window.style.transform="translateY(0)")})),this.isVisible=!0,this.bubble){this.bubble.style.display="none";const i=this.bubble.messageElement;i instanceof HTMLElement&&(i.style.display="none")}}this.hideInviteDialog(),(e=(t=this.config).onShowChat)==null||e.call(t)}hideChat(){var t,e,n,i,o;if(this.window){if(window.innerWidth<=768?(this.window.style.transform="translateY(100%)",setTimeout(()=>{this.window&&(this.window.style.display="none")},((t=this.config.animation)==null?void 0:t.duration)||300)):this.window.style.display="none",this.isVisible=!1,this.bubble){this.bubble.style.display=((e=this.config.buttonConfig)==null?void 0:e.show)===!1?"none":"inline-flex";const s=this.bubble.messageElement;s instanceof HTMLElement&&(s.style.display=((n=this.config.bubbleConfig)==null?void 0:n.show)===!1?"none":"block")}(o=(i=this.config).onHideChat)==null||o.call(i)}}minimizeWindow(){this.window&&(this.windowState="minimized",this.window.style.display="none",this.hideChat())}toggleMaximize(){this.window&&window.open(this.generateChatUrl(),"_blank")}setupResizeListener(){const t=()=>{var a,s;if(!this.window||!this.isVisible)return;const n=window.innerWidth<=768,i=window.innerWidth,o=window.innerHeight;if(n)Object.assign(this.window.style,{left:"0",bottom:"0",width:"100%",height:"90vh",borderTopLeftRadius:"12px",borderTopRightRadius:"12px",borderBottomLeftRadius:"0",borderBottomRightRadius:"0"});else{let h=this.windowState==="maximized"?i:Math.min(((a=this.config.window)==null?void 0:a.width)||i*.9,i*.9),u=this.windowState==="maximized"?o:Math.min(((s=this.config.window)==null?void 0:s.height)||o*.9,o*.9);const g=this.config.placement==="bottom-right"?this.config.marginSide:void 0,p=this.config.placement==="bottom-left"?this.config.marginSide:void 0;Object.assign(this.window.style,{width:`${h}px`,height:`${u}px`,right:g?`${g}px`:"auto",left:p?`${p}px`:"auto",bottom:`${this.config.marginBottom}px`,borderRadius:this.windowState==="maximized"?"0":"12px"})}};let e;window.addEventListener("resize",()=>{clearTimeout(e),e=window.setTimeout(t,100)}),t()}destroy(){var e;const t=(e=this.bubble)==null?void 0:e.parentElement;t&&document.body.contains(t)&&(document.body.removeChild(t),this.bubble=null),this.window&&document.body.contains(this.window)&&(document.body.removeChild(this.window),this.window=null),window.removeEventListener("resize",this.setupResizeListener.bind(this)),this.loopTimer&&(window.clearTimeout(this.loopTimer),this.loopTimer=null),this.inviteDialog&&document.body.contains(this.inviteDialog)&&(document.body.removeChild(this.inviteDialog),this.inviteDialog=null),this.contextMenu&&document.body.contains(this.contextMenu)&&(document.body.removeChild(this.contextMenu),this.contextMenu=null),this.hideTimeout&&(clearTimeout(this.hideTimeout),this.hideTimeout=null)}createInviteDialog(){var o,a;if(!((o=this.config.inviteConfig)!=null&&o.show))return;if(this.inviteDialog=document.createElement("div"),this.inviteDialog.style.cssText=`
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
    `,this.config.inviteConfig.icon){const s=document.createElement("div");s.style.cssText=`
        font-size: 32px;
        margin-bottom: 12px;
      `,s.textContent=this.config.inviteConfig.icon,this.inviteDialog.appendChild(s)}const t=document.createElement("div");t.style.cssText=`
      margin-bottom: 16px;
      color: #333;
    `,t.textContent=this.config.inviteConfig.text||"需要帮助吗？点击开始对话",this.inviteDialog.appendChild(t);const e=document.createElement("div");e.style.cssText=`
      display: flex;
      gap: 10px;
      justify-content: center;
    `;const n=document.createElement("button");n.textContent="开始对话",n.style.cssText=`
      padding: 8px 16px;
      background: ${((a=this.config.theme)==null?void 0:a.backgroundColor)||"#0066FF"};
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `,n.onclick=()=>{var s,h;this.hideInviteDialog(),this.showChat(),(h=(s=this.config.inviteConfig)==null?void 0:s.onAccept)==null||h.call(s)};const i=document.createElement("button");i.textContent="稍后再说",i.style.cssText=`
      padding: 8px 16px;
      background: #f5f5f5;
      color: #666;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `,i.onclick=()=>{var s,h;this.hideInviteDialog(),(h=(s=this.config.inviteConfig)==null?void 0:s.onReject)==null||h.call(s),this.handleInviteLoop()},e.appendChild(n),e.appendChild(i),this.inviteDialog.appendChild(e),document.body.appendChild(this.inviteDialog)}showInviteDialog(){var t,e;this.inviteDialog&&(this.inviteDialog.style.display="block",(e=(t=this.config.inviteConfig)==null?void 0:t.onOpen)==null||e.call(t))}hideInviteDialog(){var t,e;console.log("hideInviteDialog before"),this.inviteDialog&&(this.inviteDialog.style.display="none",(e=(t=this.config.inviteConfig)==null?void 0:t.onClose)==null||e.call(t),console.log("hideInviteDialog after"))}handleInviteLoop(){const{loop:t,loopDelay:e=3e3,loopCount:n=1/0}=this.config.inviteConfig||{};!t||this.loopCount>=n-1||(this.loopTimer&&window.clearTimeout(this.loopTimer),this.loopTimer=window.setTimeout(()=>{this.loopCount++,this.showInviteDialog()},e))}showButton(){this.bubble&&(this.bubble.style.display="inline-flex")}hideButton(){this.bubble&&(this.bubble.style.display="none")}showBubble(){if(this.bubble){const t=this.bubble.messageElement;t instanceof HTMLElement&&(t.style.display="block",setTimeout(()=>{t.style.opacity="1",t.style.transform="translateY(0)"},100))}}hideBubble(){if(this.bubble){const t=this.bubble.messageElement;t instanceof HTMLElement&&(t.style.opacity="0",t.style.transform="translateY(10px)",setTimeout(()=>{t.style.display="none"},300))}}createContextMenu(){this.contextMenu=document.createElement("div"),this.contextMenu.style.cssText=`
      position: fixed;
      background: white;
      border-radius: 4px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      padding: 4px 0;
      display: none;
      z-index: 10000;
      min-width: 150px;
    `;const t=[{text:"隐藏按钮和气泡",onClick:()=>{this.hideButton(),this.hideBubble()}}];t.forEach((e,n)=>{const i=document.createElement("div");if(i.style.cssText=`
        padding: 8px 16px;
        cursor: pointer;
        color: #333;
        font-size: 14px;
        
        &:hover {
          background: #f5f5f5;
        }
      `,i.textContent=e.text,i.onclick=()=>{e.onClick(),this.hideContextMenu()},this.contextMenu&&this.contextMenu.appendChild(i),n<t.length-1){const o=document.createElement("div");o.style.cssText=`
          height: 1px;
          background: #eee;
          margin: 4px 0;
        `,this.contextMenu&&this.contextMenu.appendChild(o)}}),document.body.appendChild(this.contextMenu)}showContextMenu(t){if(t.preventDefault(),this.contextMenu||this.createContextMenu(),this.contextMenu){this.contextMenu.style.visibility="hidden",this.contextMenu.style.display="block";const e=this.contextMenu.offsetWidth,n=this.contextMenu.offsetHeight;let i=t.clientX,o=t.clientY;i+e>window.innerWidth&&(i=i-e),o+n>window.innerHeight&&(o=o-n),i=Math.max(0,i),o=Math.max(0,o),this.contextMenu.style.left=`${i}px`,this.contextMenu.style.top=`${o}px`,this.contextMenu.style.visibility="visible"}}hideContextMenu(){this.contextMenu&&(this.contextMenu.style.display="none")}}return f});
