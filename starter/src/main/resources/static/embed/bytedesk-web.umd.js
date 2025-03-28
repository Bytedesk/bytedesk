(function(p,c){typeof exports=="object"&&typeof module<"u"?module.exports=c():typeof define=="function"&&define.amd?define(c):(p=typeof globalThis<"u"?globalThis:p||self,p.BytedeskWeb=c())})(this,function(){"use strict";var z=Object.defineProperty;var W=(p,c,t)=>c in p?z(p,c,{enumerable:!0,configurable:!0,writable:!0,value:t}):p[c]=t;var g=(p,c,t)=>W(p,typeof c!="symbol"?c+"":c,t);class p{constructor(t){g(this,"config");g(this,"bubble",null);g(this,"window",null);g(this,"inviteDialog",null);g(this,"contextMenu",null);g(this,"hideTimeout",null);g(this,"isVisible",!1);g(this,"isDragging",!1);g(this,"windowState","normal");g(this,"loopCount",0);g(this,"loopTimer",null);this.config={...this.getDefaultConfig(),...t}}getDefaultConfig(){return{isDebug:!1,isPreload:!1,baseUrl:"https://cdn.weiyuai.cn/chat",placement:"bottom-right",marginBottom:20,marginSide:20,autoPopup:!1,inviteConfig:{show:!1,text:"邀请您加入对话",acceptText:"开始对话",rejectText:"稍后再说"},tabsConfig:{home:!1,messages:!0,help:!1,news:!1},bubbleConfig:{show:!0,icon:"👋",title:"需要帮助吗？",subtitle:"点击开始对话"},buttonConfig:{show:!0,width:60,height:60,onClick:()=>{this.showChat()}},showSupport:!0,chatConfig:{org:"df_org_uid",t:"2",sid:"df_rt_uid"},animation:{enabled:!0,duration:300,type:"ease"},theme:{mode:"system",textColor:"#ffffff",backgroundColor:"#0066FF"},window:{width:380,height:640},draggable:!1,locale:"zh-cn"}}init(){var t;this.createBubble(),this.createInviteDialog(),this.setupMessageListener(),this.setupResizeListener(),this.preload(),this.config.autoPopup&&setTimeout(()=>{this.showChat()},this.config.autoPopupDelay||1e3),(t=this.config.inviteConfig)!=null&&t.show&&setTimeout(()=>{this.showInviteDialog()},this.config.inviteConfig.delay||3e3)}createBubble(){var u,b,m,l,h,r,T,M,I,k,D,$;const t=document.createElement("div");t.style.cssText=`
      position: fixed;
      ${this.config.placement==="bottom-left"?"left":"right"}: ${this.config.marginSide}px;
      bottom: ${this.config.marginBottom}px;
      display: flex;
      flex-direction: column;
      align-items: ${this.config.placement==="bottom-left"?"flex-start":"flex-end"};
      gap: 10px;
      z-index: 9999;
    `;let e=null;if((u=this.config.bubbleConfig)!=null&&u.show){e=document.createElement("div"),e.style.cssText=`
        background: ${((b=this.config.theme)==null?void 0:b.mode)==="dark"?"#1f2937":"white"};
        color: ${((m=this.config.theme)==null?void 0:m.mode)==="dark"?"#e5e7eb":"#1f2937"};
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
      `;const w=document.createElement("span");w.textContent=((l=this.config.bubbleConfig)==null?void 0:l.icon)||"",w.style.fontSize="20px",s.appendChild(w);const y=document.createElement("div"),x=document.createElement("div");x.textContent=((h=this.config.bubbleConfig)==null?void 0:h.title)||"",x.style.fontWeight="bold",x.style.color=((r=this.config.theme)==null?void 0:r.mode)==="dark"?"#e5e7eb":"#1f2937",x.style.marginBottom="4px",y.appendChild(x);const f=document.createElement("div");f.textContent=((T=this.config.bubbleConfig)==null?void 0:T.subtitle)||"",f.style.fontSize="0.9em",f.style.color=((M=this.config.theme)==null?void 0:M.mode)==="dark"?"#9ca3af":"#4b5563",y.appendChild(f),s.appendChild(y),e.appendChild(s);const C=document.createElement("div");C.style.cssText=`
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
      `,e.appendChild(C),e.appendChild(v),t.appendChild(e),setTimeout(()=>{e&&(e.style.opacity="1",e.style.transform="translateY(0)")},500)}this.bubble=document.createElement("button");const i=this.config.buttonConfig||{},n=i.width||60,o=i.height||60,d=Math.min(n,o)/2;this.bubble.style.cssText=`
      background-color: ${(D=this.config.theme)==null?void 0:D.backgroundColor};
      width: ${n}px;
      height: ${o}px;
      border-radius: ${d}px;
      border: none;
      cursor: ${this.config.draggable?"move":"pointer"};
      display: ${i.show===!1?"none":"flex"};
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
      transition: all 0.3s ease;
      outline: none;
      position: relative;
      user-select: none;
    `;const a=document.createElement("div");if(a.style.cssText=`
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    `,i.icon){const s=document.createElement("span");s.textContent=i.icon,s.style.fontSize=`${o*.4}px`,a.appendChild(s)}else{const s=document.createElement("div");s.innerHTML=`
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 14.663 3.04094 17.0829 4.73812 18.875L2.72681 21.1705C2.44361 21.4937 2.67314 22 3.10288 22H12Z" 
                fill="white"/>
        </svg>
      `,a.appendChild(s)}if(i.text){const s=document.createElement("span");s.textContent=i.text,s.style.cssText=`
        color: ${(($=this.config.theme)==null?void 0:$.textColor)||"#ffffff"};
        font-size: ${o*.25}px;
        white-space: nowrap;
      `,a.appendChild(s)}if(this.bubble.appendChild(a),this.bubble.addEventListener("mouseenter",()=>{this.bubble.style.transform="scale(1.1)"}),this.bubble.addEventListener("mouseleave",()=>{this.bubble.style.transform="scale(1)"}),t.appendChild(this.bubble),this.config.draggable){let s=0,w=0,y=0,x=0;this.bubble.addEventListener("mousedown",f=>{f.button===0&&(this.isDragging=!0,s=f.clientX,w=f.clientY,y=t.offsetLeft,x=t.offsetTop,t.style.transition="none")}),document.addEventListener("mousemove",f=>{if(!this.isDragging)return;f.preventDefault();const C=f.clientX-s,v=f.clientY-w,E=y+C,S=x+v,L=window.innerHeight-t.offsetHeight;E<=window.innerWidth/2?(t.style.left=`${Math.max(0,E)}px`,t.style.right="auto",this.config.placement="bottom-left"):(t.style.right=`${Math.max(0,window.innerWidth-E-t.offsetWidth)}px`,t.style.left="auto",this.config.placement="bottom-right"),t.style.bottom=`${Math.min(Math.max(0,window.innerHeight-S-t.offsetHeight),L)}px`}),document.addEventListener("mouseup",()=>{this.isDragging&&(this.isDragging=!1,t.style.transition="all 0.3s ease",this.config.marginSide=parseInt(this.config.placement==="bottom-left"?t.style.left:t.style.right)||20,this.config.marginBottom=parseInt(t.style.bottom||"20"))})}this.bubble.addEventListener("click",()=>{if(!this.isDragging){console.log("bubble click");const s=this.bubble.messageElement;s instanceof HTMLElement&&(s.style.display="none"),this.showChat()}}),this.bubble.messageElement=e,document.body.appendChild(t),this.bubble.addEventListener("contextmenu",s=>{this.showContextMenu(s)}),document.addEventListener("click",()=>{this.hideContextMenu()})}getSupportText(){var i;const t=((i=this.config)==null?void 0:i.locale)||"zh-cn",e={"zh-cn":"微语技术支持","zh-tw":"微語技術支援",en:"Powered by Bytedesk",ja:"Bytedeskによる技術支援",ko:"Bytedesk 기술 지원"};return e[t]||e["zh-cn"]}createChatWindow(){var a,u,b,m,l,h;this.window=document.createElement("div");const t=window.innerWidth<=768,e=window.innerWidth,i=window.innerHeight,n=Math.min(((a=this.config.window)==null?void 0:a.width)||e*.9,e*.9),o=Math.min(((u=this.config.window)==null?void 0:u.height)||i*.9,i*.9);t?this.window.style.cssText=`
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
        transition: all ${(b=this.config.animation)==null?void 0:b.duration}ms ${(m=this.config.animation)==null?void 0:m.type};
      `:this.window.style.cssText=`
        position: fixed;
        ${this.config.placement==="bottom-right"?"right":"left"}: ${this.config.marginSide}px;
        bottom: ${this.config.marginBottom}px;
        width: ${n}px;
        height: ${o}px;
        border-radius: 12px;
        box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        display: none;
        overflow: hidden;
        z-index: 10000;
        transition: all ${(l=this.config.animation)==null?void 0:l.duration}ms ${(h=this.config.animation)==null?void 0:h.type};
      `;const d=document.createElement("iframe");if(d.style.cssText=`
      width: 100%;
      height: ${this.config.showSupport?"calc(100% - 24px)":"100%"};
      border: none;
      display: block; // 添加这一行
      vertical-align: bottom; // 添加这一行
    `,d.src=this.generateChatUrl(),console.log("iframe.src: ",d.src),this.window.appendChild(d),this.config.showSupport){const r=document.createElement("div");r.style.cssText=`
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #666;
        font-size: 12px;
        line-height: 24px;
        background: #ffffff;
        padding: 0;
        margin: 0;
        border-top: none; // 确保没有边框
      `,r.innerHTML=`
        <a href="https://www.bytedesk.com" 
           target="_blank" 
           style="
             color: #666;
             text-decoration: none;
             display: flex;
             align-items: center;
             height: 100%;
             width: 100%;
             justify-content: center;
           ">
          ${this.getSupportText()}
        </a>
      `,this.window.appendChild(r)}document.body.appendChild(this.window)}generateChatUrl(t=!1,e="messages"){console.log("this.config: ",this.config,e);const i=new URLSearchParams;return Object.entries(this.config.chatConfig||{}).forEach(([n,o])=>{i.append(n,String(o))}),Object.entries(this.config.browseConfig||{}).forEach(([n,o])=>{i.append(n,String(o))}),Object.entries(this.config.theme||{}).forEach(([n,o])=>{i.append(n,String(o))}),i.append("lang",this.config.locale||"zh-cn"),t&&i.append("preload","1"),`${this.config.baseUrl}?${i.toString()}`}setupMessageListener(){window.addEventListener("message",t=>{switch(t.data.type){case"CLOSE_CHAT_WINDOW":this.hideChat();break;case"MAXIMIZE_WINDOW":this.toggleMaximize();break;case"MINIMIZE_WINDOW":this.minimizeWindow();break;case"RECEIVE_MESSAGE":console.log("RECEIVE_MESSAGE");break;case"INVITE_VISITOR":console.log("INVITE_VISITOR");break;case"INVITE_VISITOR_ACCEPT":console.log("INVITE_VISITOR_ACCEPT");break;case"INVITE_VISITOR_REJECT":console.log("INVITE_VISITOR_REJECT");break}})}preload(){if(console.log("preload"),this.config.isPreload){const t=this.generateChatUrl(!0);console.log("preLoadUrl: ",t);const e=document.createElement("iframe");e.src=t,e.style.display="none",document.body.appendChild(e)}}showChat(t){var e,i;if(t&&(this.config={...this.config,...t},this.window&&(document.body.removeChild(this.window),this.window=null)),this.window||this.createChatWindow(),this.window){const n=window.innerWidth<=768;if(this.window.style.display="block",this.setupResizeListener(),n&&this.window&&(this.window.style.transform="translateY(100%)",requestAnimationFrame(()=>{this.window&&(this.window.style.transform="translateY(0)")})),this.isVisible=!0,this.bubble){this.bubble.style.display="none";const o=this.bubble.messageElement;o instanceof HTMLElement&&(o.style.display="none")}}this.hideInviteDialog(),(i=(e=this.config).onShowChat)==null||i.call(e)}hideChat(){var t,e,i,n,o;if(this.window){if(window.innerWidth<=768?(this.window.style.transform="translateY(100%)",setTimeout(()=>{this.window&&(this.window.style.display="none")},((t=this.config.animation)==null?void 0:t.duration)||300)):this.window.style.display="none",this.isVisible=!1,this.bubble){this.bubble.style.display=((e=this.config.buttonConfig)==null?void 0:e.show)===!1?"none":"inline-flex";const a=this.bubble.messageElement;a instanceof HTMLElement&&(a.style.display=((i=this.config.bubbleConfig)==null?void 0:i.show)===!1?"none":"block")}(o=(n=this.config).onHideChat)==null||o.call(n)}}minimizeWindow(){this.window&&(this.windowState="minimized",this.window.style.display="none",this.hideChat())}toggleMaximize(){this.window&&window.open(this.generateChatUrl(),"_blank")}setupResizeListener(){const t=()=>{var d,a;if(!this.window||!this.isVisible)return;const i=window.innerWidth<=768,n=window.innerWidth,o=window.innerHeight;if(i)Object.assign(this.window.style,{left:"0",bottom:"0",width:"100%",height:"90vh",borderTopLeftRadius:"12px",borderTopRightRadius:"12px",borderBottomLeftRadius:"0",borderBottomRightRadius:"0"});else{let u=this.windowState==="maximized"?n:Math.min(((d=this.config.window)==null?void 0:d.width)||n*.9,n*.9),b=this.windowState==="maximized"?o:Math.min(((a=this.config.window)==null?void 0:a.height)||o*.9,o*.9);const m=this.config.placement==="bottom-right"?this.config.marginSide:void 0,l=this.config.placement==="bottom-left"?this.config.marginSide:void 0;Object.assign(this.window.style,{width:`${u}px`,height:`${b}px`,right:m?`${m}px`:"auto",left:l?`${l}px`:"auto",bottom:`${this.config.marginBottom}px`,borderRadius:this.windowState==="maximized"?"0":"12px"})}};let e;window.addEventListener("resize",()=>{clearTimeout(e),e=window.setTimeout(t,100)}),t()}destroy(){var e;const t=(e=this.bubble)==null?void 0:e.parentElement;t&&document.body.contains(t)&&(document.body.removeChild(t),this.bubble=null),this.window&&document.body.contains(this.window)&&(document.body.removeChild(this.window),this.window=null),window.removeEventListener("resize",this.setupResizeListener.bind(this)),this.loopTimer&&(window.clearTimeout(this.loopTimer),this.loopTimer=null),this.inviteDialog&&document.body.contains(this.inviteDialog)&&(document.body.removeChild(this.inviteDialog),this.inviteDialog=null),this.contextMenu&&document.body.contains(this.contextMenu)&&(document.body.removeChild(this.contextMenu),this.contextMenu=null),this.hideTimeout&&(clearTimeout(this.hideTimeout),this.hideTimeout=null)}createInviteDialog(){var o,d,a,u,b,m;if(!((o=this.config.inviteConfig)!=null&&o.show))return;if(this.inviteDialog=document.createElement("div"),this.inviteDialog.style.cssText=`
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
    `,this.config.inviteConfig.icon){const l=document.createElement("div");l.style.cssText=`
        font-size: 32px;
        margin-bottom: 12px;
      `,l.textContent=this.config.inviteConfig.icon,this.inviteDialog.appendChild(l)}const t=document.createElement("div");t.style.cssText=`
      margin-bottom: 16px;
      color: #333;
    `,t.textContent=this.config.inviteConfig.text||"需要帮助吗？点击开始对话",this.inviteDialog.appendChild(t);const e=document.createElement("div");e.style.cssText=`
      display: flex;
      gap: 10px;
      justify-content: center;
    `;const i=document.createElement("button");i.textContent=((a=(d=this.config)==null?void 0:d.inviteConfig)==null?void 0:a.acceptText)||"开始对话",i.style.cssText=`
      padding: 8px 16px;
      background: ${((u=this.config.theme)==null?void 0:u.backgroundColor)||"#0066FF"};
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `,i.onclick=()=>{var l,h,r;this.hideInviteDialog(),this.showChat(),(r=(h=(l=this.config)==null?void 0:l.inviteConfig)==null?void 0:h.onAccept)==null||r.call(h)};const n=document.createElement("button");n.textContent=((m=(b=this.config)==null?void 0:b.inviteConfig)==null?void 0:m.rejectText)||"稍后再说",n.style.cssText=`
      padding: 8px 16px;
      background: #f5f5f5;
      color: #666;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `,n.onclick=()=>{var l,h,r;this.hideInviteDialog(),(r=(h=(l=this.config)==null?void 0:l.inviteConfig)==null?void 0:h.onReject)==null||r.call(h),this.handleInviteLoop()},e.appendChild(i),e.appendChild(n),this.inviteDialog.appendChild(e),document.body.appendChild(this.inviteDialog)}showInviteDialog(){var t,e;this.inviteDialog&&(this.inviteDialog.style.display="block",(e=(t=this.config.inviteConfig)==null?void 0:t.onOpen)==null||e.call(t))}hideInviteDialog(){var t,e;console.log("hideInviteDialog before"),this.inviteDialog&&(this.inviteDialog.style.display="none",(e=(t=this.config.inviteConfig)==null?void 0:t.onClose)==null||e.call(t),console.log("hideInviteDialog after"))}handleInviteLoop(){const{loop:t,loopDelay:e=3e3,loopCount:i=1/0}=this.config.inviteConfig||{};!t||this.loopCount>=i-1||(this.loopTimer&&window.clearTimeout(this.loopTimer),this.loopTimer=window.setTimeout(()=>{this.loopCount++,this.showInviteDialog()},e))}showButton(){this.bubble&&(this.bubble.style.display="inline-flex")}hideButton(){this.bubble&&(this.bubble.style.display="none")}showBubble(){if(this.bubble){const t=this.bubble.messageElement;t instanceof HTMLElement&&(t.style.display="block",setTimeout(()=>{t.style.opacity="1",t.style.transform="translateY(0)"},100))}}hideBubble(){if(this.bubble){const t=this.bubble.messageElement;t instanceof HTMLElement&&(t.style.opacity="0",t.style.transform="translateY(10px)",setTimeout(()=>{t.style.display="none"},300))}}createContextMenu(){this.contextMenu=document.createElement("div"),this.contextMenu.style.cssText=`
      position: fixed;
      background: white;
      border-radius: 4px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      padding: 4px 0;
      display: none;
      z-index: 10000;
      min-width: 150px;
    `;const t=[{text:"隐藏按钮和气泡",onClick:()=>{this.hideButton(),this.hideBubble()}}];t.forEach((e,i)=>{const n=document.createElement("div");if(n.style.cssText=`
        padding: 8px 16px;
        cursor: pointer;
        color: #333;
        font-size: 14px;
        
        &:hover {
          background: #f5f5f5;
        }
      `,n.textContent=e.text,n.onclick=()=>{e.onClick(),this.hideContextMenu()},this.contextMenu&&this.contextMenu.appendChild(n),i<t.length-1){const o=document.createElement("div");o.style.cssText=`
          height: 1px;
          background: #eee;
          margin: 4px 0;
        `,this.contextMenu&&this.contextMenu.appendChild(o)}}),document.body.appendChild(this.contextMenu)}showContextMenu(t){if(t.preventDefault(),this.contextMenu||this.createContextMenu(),this.contextMenu){this.contextMenu.style.visibility="hidden",this.contextMenu.style.display="block";const e=this.contextMenu.offsetWidth,i=this.contextMenu.offsetHeight;let n=t.clientX,o=t.clientY;n+e>window.innerWidth&&(n=n-e),o+i>window.innerHeight&&(o=o-i),n=Math.max(0,n),o=Math.max(0,o),this.contextMenu.style.left=`${n}px`,this.contextMenu.style.top=`${o}px`,this.contextMenu.style.visibility="visible"}}hideContextMenu(){this.contextMenu&&(this.contextMenu.style.display="none")}}return p});
