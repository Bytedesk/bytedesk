const __vite__mapDeps=(i,m=__vite__mapDeps,d=(m.f||(m.f=["assets/request-C-CxlrSd.js","assets/vendor-K2WcSF1T.js","assets/rolldown-runtime-Dw2cE7zH.js","assets/antd-alert-DwWnyq7n.js","assets/network-vendor-2uhgNB_Q.js","assets/logger-IFtnn69B.js","assets/visitor-BgsR2fqY.js","assets/message-DBCZUwik.js","assets/upload-Ck4f7jLj.js","assets/feedback-DRVM6--S.js"])))=>i.map(i=>d[i]);
import{r as e}from"./rolldown-runtime-Dw2cE7zH.js";import{$t as t}from"./antd-alert-DwWnyq7n.js";import{c as n}from"./vendor-K2WcSF1T.js";import{n as r,t as i}from"./react-vendor-C4iwnPb-.js";import{_ as a,a as o,b as s,c,f as l,g as u,h as d,m as f,n as p,o as m,p as h,s as g,t as _,v,x as y,y as b}from"./logger-IFtnn69B.js";var x=e(t(),1),S=e=>typeof e==`string`&&e.trim()||void 0,C=e=>{if(!e)return{};let t={},n=S(e.referer)||S(e.referrer),r=S(e.title),i=S(e.url);return n&&(t.referer=n),r&&(t.title=r),i&&(t.url=i),t},w=e=>{let t=C(e);return Object.keys(t).length>0?JSON.stringify(t):void 0},T=class{config;unreadBadgeMode=`hidden`;unreadBadgeCount=0;bubble=null;bubbleContainer=null;buttonElements=[];buttonPreviewElement=null;buttonPreviewHideTimer=null;window=null;inviteDialog=null;contextMenu=null;hideTimeout=null;isVisible=!1;isDragging=!1;windowState=`normal`;loopCount=0;loopTimer=null;isDestroyed=!1;initVisitorPromise=null;getUnreadMessageCountPromise=null;clearUnreadMessagesPromise=null;feedbackTooltip=null;feedbackDialog=null;selectedText=``;selectionDebounceTimer=null;isTooltipVisible=!1;lastSelectionText=``;lastMouseEvent=null;lastSelectionRect=null;bubbleMessages=[];bubbleMessageIndex=0;bubbleMessageTimer=null;bubbleMessageTransitionTimer=null;bubbleMessageViewportElement=null;bubbleMessageContentElement=null;bubblePendingMessageElement=null;bubbleTickerTrackElement=null;bubbleTickerStyleElement=null;bubbleIconElement=null;bubbleTitleElement=null;bubbleSubtitleElement=null;constructor(e){this.config={...this.getDefaultConfig(),...e},p(this.config),this.setupApiUrl()}async setupApiUrl(){try{let{setApiUrl:e}=await n(async()=>{let{setApiUrl:e}=await import(`./request-C-CxlrSd.js`);return{setApiUrl:e}},__vite__mapDeps([0,1,2,3,4,5])),t=this.config.apiUrl||`https://api.weiyuai.cn`;e(t),_.info(`API URL 已设置为:`,t)}catch(e){_.error(`设置API URL时出错:`,e)}}mergeConfig(e,t){let n=!!(t?.replaceChatConfig&&e.chatConfig);return{...this.config,...e,inviteConfig:{...this.config.inviteConfig||{},...e.inviteConfig||{}},tabsConfig:{...this.config.tabsConfig||{},...e.tabsConfig||{}},bubbleConfig:{...this.config.bubbleConfig||{},...e.bubbleConfig||{}},buttonConfig:{...this.config.buttonConfig||{},...e.buttonConfig||{}},feedbackConfig:{...this.config.feedbackConfig||{},...e.feedbackConfig||{}},chatConfig:n?e.chatConfig:e.chatConfig?{...this.config.chatConfig||{},...e.chatConfig}:this.config.chatConfig,browseConfig:{...this.config.browseConfig||{},...e.browseConfig||{}},animation:{...this.config.animation||{},...e.animation||{}},window:{...this.config.window||{},...e.window||{}},theme:{...this.config.theme||{},...e.theme||{}},buttonsConfig:e.buttonsConfig??this.config.buttonsConfig}}refreshFloatingUi(){let e=!!(this.inviteDialog&&document.body.contains(this.inviteDialog)&&this.inviteDialog.style.display!==`none`);this.stopBubbleMessageRotation(),this.stopBubbleMessageTransition(),this.destroyBubbleTicker(),this.hideButtonPreview(),this.bubbleContainer&&document.body.contains(this.bubbleContainer)&&this.bubbleContainer.remove(),this.bubbleContainer=null,this.bubble=null,this.buttonElements=[],this.bubbleMessageViewportElement=null,this.bubbleMessageContentElement=null,this.bubblePendingMessageElement=null,this.bubbleTickerTrackElement=null,this.bubbleTickerStyleElement=null,this.bubbleIconElement=null,this.bubbleTitleElement=null,this.bubbleSubtitleElement=null,this.bubbleMessages=[],this.bubbleMessageIndex=0,this.inviteDialog&&document.body.contains(this.inviteDialog)&&this.inviteDialog.remove(),this.inviteDialog=null,this.createBubble(),this.createInviteDialog(),e&&this.showInviteDialog()}updateChatWindowLayout(){if(!this.window)return;let e=window.innerWidth<=768,t=window.innerWidth,n=window.innerHeight;if(e){Object.assign(this.window.style,{left:`0`,right:`auto`,bottom:`0`,width:`100%`,height:`100vh`,borderTopLeftRadius:`12px`,borderTopRightRadius:`12px`,borderBottomLeftRadius:`0`,borderBottomRightRadius:`0`,boxSizing:`border-box`,paddingTop:`env(safe-area-inset-top)`,paddingBottom:`env(safe-area-inset-bottom)`}),this.window.style.height=`100dvh`;return}let r=Math.min(this.config.window?.width||t*.9,t*.9),i=Math.min(this.config.window?.height||n*.9,n*.9);Object.assign(this.window.style,{width:`${r}px`,height:`${i}px`,left:this.config.placement===`bottom-left`?`${this.config.marginSide}px`:`auto`,right:this.config.placement===`bottom-right`?`${this.config.marginSide}px`:`auto`,bottom:`${this.config.marginBottom}px`,borderRadius:`12px`,boxSizing:`border-box`,paddingTop:``,paddingBottom:``})}refreshChatIframeUrl(){if(!this.window)return;let e=this.window.querySelector(`iframe`);e&&(e.src=this.generateChatUrl())}setTheme(e){this.setConfig({theme:{...this.config.theme||{},...e}})}setConfig(e,t){let n=this.config;this.config=this.mergeConfig(e,t);let r=this.getPrimaryActionFromConfig(e),i=Object.prototype.hasOwnProperty.call(e,`chatPath`),a=Object.prototype.hasOwnProperty.call(e,`buttonConfig`);i||(r?this.syncChatPathByAction(r):a&&this.syncChatPathByAction(`chat`)),p(this.config),e.apiUrl&&e.apiUrl!==n.apiUrl&&this.setupApiUrl(),(this.bubbleContainer&&document.body.contains(this.bubbleContainer)||this.inviteDialog&&document.body.contains(this.inviteDialog))&&this.refreshFloatingUi(),this.window&&document.body.contains(this.window)&&(this.updateChatWindowLayout(),(e.theme||e.locale||e.chatConfig||e.htmlUrl||e.chatPath||e.threadPath||e.webrtcPath||e.callPath)&&this.refreshChatIframeUrl()),this.config.onConfigChange?.(this.config)}getPrimaryActionFromConfig(e){let t=e.buttonConfig?.action;return t&&[`chat`,`thread`,`webrtc`,`call`].includes(t)?t:null}syncChatPathByAction(e){switch(e){case`thread`:this.config.chatPath=this.normalizePath(this.config.threadPath,`/chat/thread`);break;case`webrtc`:this.config.chatPath=this.normalizePath(this.config.webrtcPath,`/webrtc`);break;case`call`:this.config.chatPath=this.normalizePath(this.config.callPath,`/call`);break;default:this.config.chatPath=`/chat`;break}}getDefaultConfig(){return{isDebug:!1,forceRefresh:!1,htmlUrl:`https://cdn.weiyuai.cn`,apiUrl:`https://api.weiyuai.cn`,chatPath:`/chat`,threadPath:`/chat/thread`,webrtcPath:`/webrtc`,callPath:`/call`,placement:`bottom-right`,marginBottom:20,marginSide:20,autoPopup:!1,inviteConfig:{show:!1,text:`邀请您加入对话`,acceptText:`开始对话`,rejectText:`稍后再说`},tabsConfig:{home:!1,messages:!0,help:!1,news:!1},bubbleConfig:{show:!0,icon:`👋`,title:`需要帮助吗？`,subtitle:`点击开始对话`},buttonConfig:{show:!0,width:60,height:60,onClick:()=>{this.showChat()}},feedbackConfig:{enabled:!1,trigger:`selection`,showOnSelection:!0,selectionText:`文档反馈`,buttonText:`文档反馈`,dialogTitle:`提交意见反馈`,placeholder:`请描述您的问题或优化建议`,submitText:`提交反馈`,cancelText:`取消`,successMessage:`反馈已提交，感谢您的意见！`,categoryNames:[`错别字、拼写错误`,`链接跳转有问题`,`文档和实操过程不一致`,`文档难以理解`,`建议或其他`],requiredTypes:!1,typesSectionTitle:`问题类型`,typesDescription:`（多选）`,submitScreenshot:!0},chatConfig:{org:`df_org_uid`,t:`2`,sid:`df_rt_uid`},animation:{enabled:!0,duration:300,type:`ease`},theme:{mode:`system`,textColor:`#ffffff`,backgroundColor:`#0066FF`},window:{width:380,height:640},draggable:!1,locale:`zh-cn`}}getEffectiveButtonConfigs(){let e=Array.isArray(this.config.buttonsConfig)?this.config.buttonsConfig.filter(e=>!!e):[];return e.length>0?e:[this.config.buttonConfig||{}]}hasVisibleButtons(){return this.getEffectiveButtonConfigs().some(e=>e.show!==!1)}isMultiButtonLayout(e){return(e||this.getEffectiveButtonConfigs()).filter(e=>e.show!==!1).length>1}applyConfiguredButtonVisibility(){let e=this.getEffectiveButtonConfigs();this.buttonElements.forEach((t,n)=>{let r=e[n];t.style.display=r?.show===!1?`none`:`flex`})}hideBubbleMessageElement(){let e=this.bubble?.messageElement;e instanceof HTMLElement&&(this.stopBubbleMessageTransition(),e.style.display=`none`,this.stopBubbleMessageRotation())}triggerButtonAction(e){if(e.onClick){e.onClick();return}switch(e.action){case`thread`:this.showThread();break;case`webrtc`:this.showWebrtc();break;case`call`:this.showCall();break;default:this.showChat();break}}hideButtonPreview(){this.buttonPreviewHideTimer&&=(window.clearTimeout(this.buttonPreviewHideTimer),null),this.buttonPreviewElement?.parentElement&&this.buttonPreviewElement.parentElement.removeChild(this.buttonPreviewElement),this.buttonPreviewElement=null}cancelButtonPreviewHide(){this.buttonPreviewHideTimer&&=(window.clearTimeout(this.buttonPreviewHideTimer),null)}scheduleHideButtonPreview(){this.cancelButtonPreviewHide(),this.buttonPreviewHideTimer=window.setTimeout(()=>{this.hideButtonPreview()},120)}showButtonPreview(e,t){if(!t.previewImageUrl){this.hideButtonPreview();return}this.hideButtonPreview();let n=document.createElement(`div`),r=this.config.theme?.mode===`dark`,i=document.createElement(`img`),a=document.createElement(`div`),o=e.getBoundingClientRect(),s=Math.min(Math.max(12,o.top+o.height/2-110),Math.max(12,window.innerHeight-232)),c=this.config.placement===`bottom-left`?Math.min(window.innerWidth-180-12,o.right+14):Math.max(12,o.left-180-14);n.style.cssText=`
      position: fixed;
      top: ${s}px;
      left: ${c}px;
      width: 180px;
      padding: 10px;
      border-radius: 16px;
      background: ${r?`rgba(17, 24, 39, 0.96)`:`rgba(255, 255, 255, 0.98)`};
      box-shadow: 0 12px 32px rgba(0, 0, 0, ${r?`0.34`:`0.18`});
      border: 1px solid ${r?`rgba(255,255,255,0.08)`:`rgba(15,23,42,0.08)`};
      z-index: 10001;
      pointer-events: auto;
      display: flex;
      flex-direction: column;
      gap: 8px;
      cursor: pointer;
    `,i.src=t.previewImageUrl,i.alt=t.previewImageAlt||t.text||`preview image`,i.style.cssText=`
      width: 100%;
      aspect-ratio: 1 / 1;
      object-fit: contain;
      background: white;
      border-radius: 12px;
      display: block;
    `,a.textContent=t.previewImageAlt||t.text||``,a.style.cssText=`
      color: ${r?`#e5e7eb`:`#0f172a`};
      font-size: 12px;
      line-height: 1.4;
      text-align: center;
      word-break: break-word;
    `,n.appendChild(i),a.textContent&&n.appendChild(a),n.addEventListener(`mouseenter`,()=>{this.cancelButtonPreviewHide()}),n.addEventListener(`mouseleave`,()=>{this.scheduleHideButtonPreview()}),n.addEventListener(`click`,()=>{window.open(t.previewImageUrl,`_blank`,`noopener,noreferrer`)}),document.body.appendChild(n),this.buttonPreviewElement=n}createButtonElement(e,t,n){let r=document.createElement(`button`),i=n?.isMultiLayout===!0,a=e.width||60,o=e.height||60,s=Math.max(a,o),c=i?s:a,l=i?s:o,u=i?0:Math.min(c,l)/2,d=this.config.theme?.mode===`dark`,f=d?`#3B82F6`:`#0066FF`,p=this.config.theme?.backgroundColor||f,m=this.config.theme?.textColor||`#ffffff`,h=i?`none`:`0 4px 16px rgba(0, 0, 0, ${d?`0.3`:`0.12`})`,g=i&&!n?.isLastButton?`1px solid rgba(255, 255, 255, ${d?`0.14`:`0.28`})`:`none`,v=i?`translateY(-1px)`:`scale(1.1)`;r.style.cssText=`
      background-color: ${i?`transparent`:p};
      width: ${c}px;
      height: ${l}px;
      border-radius: ${u}px;
      border: none;
      border-bottom: ${g};
      cursor: ${this.config.draggable?`move`:`pointer`};
      display: ${e.show===!1?`none`:`flex`};
      align-items: center;
      justify-content: center;
      box-shadow: ${h};
      transition: all 0.3s ease;
      outline: none;
      position: relative;
      user-select: none;
      padding: 0;
    `;let y=document.createElement(`div`);if(y.style.cssText=`
      display: flex;
      align-items: center;
      justify-content: center;
      flex-direction: ${i&&e.text?`column`:`row`};
      gap: ${i?`4px`:`8px`};
      width: 100%;
      height: 100%;
    `,e.icon){let t=document.createElement(`span`);t.textContent=e.icon,t.style.fontSize=`${l*(i?.34:.4)}px`,t.style.lineHeight=`1`,y.appendChild(t)}else{let e=document.createElement(`div`);e.innerHTML=`
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 14.663 3.04094 17.0829 4.73812 18.875L2.72681 21.1705C2.44361 21.4937 2.67314 22 3.10288 22H12Z" fill="white"/>
        </svg>
      `,y.appendChild(e)}if(e.text){let t=document.createElement(`span`);t.textContent=e.text,t.style.cssText=`
        color: ${m};
        font-size: ${l*(i?.16:.25)}px;
        white-space: nowrap;
        line-height: 1.1;
        text-align: center;
        max-width: ${i?`${c-8}px`:`none`};
        overflow: hidden;
        text-overflow: ellipsis;
      `,y.appendChild(t)}return r.appendChild(y),r.addEventListener(`mouseenter`,()=>{this.cancelButtonPreviewHide(),r.style.transform=v,i&&(r.style.backgroundColor=`rgba(255, 255, 255, 0.12)`),e.previewImageUrl&&this.showButtonPreview(r,e)}),r.addEventListener(`mouseleave`,()=>{r.style.transform=`scale(1)`,i&&(r.style.backgroundColor=`transparent`),e.previewImageUrl&&this.scheduleHideButtonPreview()}),r.addEventListener(`click`,()=>{this.isDragging||(_.debug(`bubble click`,e.action||`chat`),t instanceof HTMLElement&&this.hideBubbleMessageElement(),this.triggerButtonAction(e))}),r.addEventListener(`contextmenu`,e=>{this.showContextMenu(e)}),r.messageElement=t,r}async init(){if(this.isDestroyed){_.warn(`BytedeskWeb 已销毁，跳过初始化`);return}let e=this.hasVisibleButtons();if(await this._initVisitor(),!this.isDestroyed){if(e){if(await this._browseVisitor(),this.isDestroyed)return}else _.debug(`buttonConfig.show=false，跳过自动发送浏览记录`);if(this.createBubble(),!this.isDestroyed&&(this.createInviteDialog(),!this.isDestroyed&&(this.setupMessageListener(),this.setupResizeListener(),!this.isDestroyed))){if(this.config.feedbackConfig?.enabled&&(this.config.isDebug&&_.debug(`BytedeskWeb: 开始初始化文档反馈功能，document.readyState:`,document.readyState),this.initFeedbackFeature(),document.readyState!==`complete`)){this.config.isDebug&&_.debug(`BytedeskWeb: DOM未完全加载，设置备用初始化`);let e=()=>{this.config.isDebug&&_.debug(`BytedeskWeb: window load事件触发，重新初始化反馈功能`),this.initFeedbackFeature(),window.removeEventListener(`load`,e)};window.addEventListener(`load`,e);let t=()=>{this.config.isDebug&&_.debug(`BytedeskWeb: DOMContentLoaded事件触发，重新初始化反馈功能`),setTimeout(()=>this.initFeedbackFeature(),100),document.removeEventListener(`DOMContentLoaded`,t)};document.readyState===`loading`&&document.addEventListener(`DOMContentLoaded`,t)}if(e){if(this._getUnreadMessageCount(),this.isDestroyed)return}else _.debug(`buttonConfig.show=false，跳过自动获取未读消息数`);if(this.config.autoPopup){if(this.isDestroyed)return;setTimeout(()=>{this.showChat()},this.config.autoPopupDelay||1e3)}if(!this.isDestroyed&&this.config.inviteConfig?.show){if(this.isDestroyed)return;setTimeout(()=>{this.showInviteDialog()},this.config.inviteConfig.delay||3e3)}}}}async _initVisitor(){if(this.initVisitorPromise)return _.debug(`访客初始化请求正在进行中，返回现有Promise`),this.initVisitorPromise;let e=localStorage.getItem(g),t=localStorage.getItem(c);_.debug(`localUid: `,e),_.debug(`localVisitorUid: `,t);let r=this.config.chatConfig?.visitorUid&&t?this.config.chatConfig?.visitorUid===t:!0;return e&&t&&r?(_.debug(`访客信息相同，直接返回本地访客信息`),this.config.onVisitorInfo?.(e||``,t||``),{uid:e,visitorUid:t}):(_.debug(`开始创建访客初始化Promise`),this.initVisitorPromise=n(async()=>{let{initVisitor:e}=await import(`./visitor-BgsR2fqY.js`);return{initVisitor:e}},__vite__mapDeps([6,0,1,2,3,4,5])).then(async({initVisitor:n})=>{try{let r={uid:String(this.config.chatConfig?.uid||e||``),visitorUid:String(this.config.chatConfig?.visitorUid||t||``),orgUid:String(this.config.chatConfig?.org||``),nickname:String(this.config.chatConfig?.name||``),avatar:String(this.config.chatConfig?.avatar||``),mobile:String(this.config.chatConfig?.mobile||``),email:String(this.config.chatConfig?.email||``),note:String(this.config.chatConfig?.note||``),channel:String(this.config.chatConfig?.channel||``),extra:typeof this.config.chatConfig?.extra==`string`?this.config.chatConfig.extra:JSON.stringify(this.config.chatConfig?.extra||{}),vipLevel:String(this.config.chatConfig?.vipLevel||``),debug:this.config.chatConfig?.debug||!1,settingsUid:this.config.chatConfig?.settingsUid||``,loadHistory:this.config.chatConfig?.loadHistory||!1},i=await n(r);return _.debug(`访客初始化API响应:`,i.data,r),i.data?.code===200?(i.data?.data?.uid&&(localStorage.setItem(g,i.data.data.uid),_.debug(`已保存uid到localStorage:`,i.data.data.uid)),i.data?.data?.visitorUid&&(localStorage.setItem(c,i.data.data.visitorUid),_.debug(`已保存visitorUid到localStorage:`,i.data.data.visitorUid)),i.data?.data&&(_.debug(`触发onVisitorInfo回调`),this.config.onVisitorInfo?.(i.data.data.uid||``,i.data.data.visitorUid||``)),i.data.data):(_.error(`访客初始化失败:`,i.data?.message),null)}catch(e){return _.error(`访客初始化出错:`,e),null}finally{_.debug(`访客初始化Promise完成，清除引用`),this.initVisitorPromise=null}}),this.initVisitorPromise)}async _browseVisitor(){try{let e=localStorage.getItem(m);if(e){let t=parseInt(e),n=Date.now(),r=3600*1e3;if(!Number.isNaN(t)&&n-t<r){let e=Math.ceil((r-(n-t))/1e3/60);_.warn(`浏览记录1小时内最多发送一次，还需等待 ${e} 分钟`);return}}let t=localStorage.getItem(o);if(t){let e=parseInt(t),n=Date.now(),r=3600*1e3;if(n-e<r){let t=Math.ceil((r-(n-e))/1e3/60);_.warn(`浏览记录发送失败后1小时内禁止发送，还需等待 ${t} 分钟`);return}else localStorage.removeItem(o)}let r=window.location.href,i=document.title,a=document.referrer,s=navigator.userAgent,c=this.getBrowserInfo(s),l=this.getOSInfo(s),u=this.getDeviceInfo(s),d=`${screen.width}x${screen.height}`,f=new URLSearchParams(window.location.search),p=f.get(`utm_source`)||void 0,h=f.get(`utm_medium`)||void 0,v=f.get(`utm_campaign`)||void 0,y=localStorage.getItem(g),b={url:r,title:i,referrer:a,userAgent:s,operatingSystem:l,browser:c,deviceType:u,screenResolution:d,utmSource:p,utmMedium:h,utmCampaign:v,status:`ONLINE`,visitorUid:String(this.config.chatConfig?.uid||y||``),orgUid:this.config.chatConfig?.org||``,channel:String(this.config.chatConfig?.channel||``)};if(!b.visitorUid){_.warn(`访客uid为空，跳过browse操作`);return}localStorage.setItem(m,Date.now().toString());let{browse:x}=await n(async()=>{let{browse:e}=await import(`./visitor-BgsR2fqY.js`);return{browse:e}},__vite__mapDeps([6,0,1,2,3,4,5])),S=await x(b);S.data?.code===200?localStorage.removeItem(o):(_.error(`浏览记录发送失败:`,S.data?.message),localStorage.setItem(o,Date.now().toString()),_.warn(`已记录浏览记录发送失败时间，1小时内将禁止再次发送`))}catch(e){_.error(`发送浏览记录时出错:`,e),localStorage.setItem(o,Date.now().toString()),_.warn(`已记录浏览记录发送失败时间，1小时内将禁止再次发送`)}}getBrowserInfo(e){return e.includes(`Chrome`)?`Chrome`:e.includes(`Firefox`)?`Firefox`:e.includes(`Safari`)?`Safari`:e.includes(`Edge`)?`Edge`:e.includes(`Opera`)?`Opera`:`Unknown`}getOSInfo(e){return e.includes(`Windows`)?`Windows`:e.includes(`Mac`)?`macOS`:e.includes(`Linux`)?`Linux`:e.includes(`Android`)?`Android`:e.includes(`iOS`)?`iOS`:`Unknown`}getDeviceInfo(e){return e.includes(`Mobile`)?`Mobile`:e.includes(`Tablet`)?`Tablet`:`Desktop`}async _getUnreadMessageCount(){return this.getUnreadMessageCountPromise?(_.debug(`获取未读消息数请求正在进行中，返回现有Promise`),this.getUnreadMessageCountPromise):(this.getUnreadMessageCountPromise=n(async()=>{let{getUnreadMessageCount:e}=await import(`./message-DBCZUwik.js`);return{getUnreadMessageCount:e}},__vite__mapDeps([7,0,1,2,3,4,5])).then(async({getUnreadMessageCount:e})=>{try{let t=String(this.config.chatConfig?.visitorUid||``),n=localStorage.getItem(g),r=localStorage.getItem(c),i={uid:n||``,visitorUid:t||r||``,orgUid:this.config.chatConfig?.org||``};if(i.uid===``)return 0;let a=await e(i);return a.data?.code===200?(this.setUnreadMessageCount(a.data.data||0),a.data.data||0):0}catch(e){return _.error(`获取未读消息数出错:`,e),0}finally{this.getUnreadMessageCountPromise=null}}),this.getUnreadMessageCountPromise)}async getUnreadMessageCount(){return this._getUnreadMessageCount()}async initVisitor(){return this._initVisitor()}async browseVisitor(){return this._browseVisitor()}clearBrowseFailedLimit(){localStorage.removeItem(o),localStorage.removeItem(m),_.info(`已清除浏览记录发送失败的限制`)}clearVisitorInfo(){localStorage.removeItem(g),localStorage.removeItem(c),_.info(`已清除本地访客信息`)}async forceInitVisitor(){return this.clearVisitorInfo(),this.initVisitorPromise=null,this._initVisitor()}removeUnreadBadgeElement(){if(!this.bubble)return;let e=this.bubble.querySelector(`.bytedesk-unread-badge`);e&&e.remove()}renderUnreadBadge(){if(_.debug(`renderUnreadBadge() 被调用`,{mode:this.unreadBadgeMode,count:this.unreadBadgeCount}),!this.hasVisibleButtons()){this.removeUnreadBadgeElement(),_.debug(`renderUnreadBadge: 当前没有可见按钮，不显示角标`);return}if(!this.bubble){_.debug(`renderUnreadBadge: bubble 不存在`);return}if(this.unreadBadgeMode===`hidden`){this.removeUnreadBadgeElement();return}let e=this.bubble.querySelector(`.bytedesk-unread-badge`);e||(e=document.createElement(`div`),e.className=`bytedesk-unread-badge`,this.bubble.appendChild(e));let t=this.unreadBadgeMode===`count`;e.style.cssText=`
      position: absolute;
      top: -4px;
      right: 2px;
      min-width: ${t?`18px`:`10px`};
      width: ${t?`auto`:`10px`};
      height: ${t?`18px`:`10px`};
      padding: ${t?`0 4px`:`0`};
      background: #ff4d4f;
      color: white;
      font-size: 12px;
      font-weight: bold;
      line-height: 1;
      border-radius: 999px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
      border: 2px solid white;
      box-sizing: border-box;
      pointer-events: none;
      z-index: 1;
    `,e.textContent=t?this.unreadBadgeCount>99?`99+`:this.unreadBadgeCount.toString():``}setUnreadMessageCount(e){let t=Number.isFinite(e)?Math.max(0,Math.floor(e)):0;return this.unreadBadgeCount=t,this.unreadBadgeMode=t>0?`count`:`hidden`,this.renderUnreadBadge(),t}showUnreadDot(){this.unreadBadgeCount=0,this.unreadBadgeMode=`dot`,this.renderUnreadBadge()}clearUnreadBadge(){this.unreadBadgeCount=0,this.unreadBadgeMode=`hidden`,this.removeUnreadBadgeElement()}async clearUnreadMessages(){return this.clearUnreadMessagesPromise?(_.debug(`清空未读消息请求正在进行中，返回现有Promise`),this.clearUnreadMessagesPromise):(this.clearUnreadMessagesPromise=n(async()=>{let{clearUnreadMessages:e}=await import(`./message-DBCZUwik.js`);return{clearUnreadMessages:e}},__vite__mapDeps([7,0,1,2,3,4,5])).then(async({clearUnreadMessages:e})=>{try{let t=String(this.config.chatConfig?.visitorUid||``),n=localStorage.getItem(g),r=localStorage.getItem(c),i={uid:n||``,visitorUid:t||r||``,orgUid:this.config.chatConfig?.org||``},a=await e(i);return _.debug(`清空未读消息数:`,a.data,i),a.data.code===200?(_.info(`清空未读消息数成功:`,a.data),this.clearUnreadBadge(),a.data.data||0):(_.error(`清空未读消息数失败:`,a.data.message),0)}catch(e){return _.error(`清空未读消息数出错:`,e),0}finally{this.clearUnreadMessagesPromise=null}}),this.clearUnreadMessagesPromise)}getBubbleMessages(){let e=this.config.bubbleConfig?.messages;if(Array.isArray(e)&&e.length>0){let t=e.filter(e=>!!e&&(!!e.icon||!!e.title||!!e.subtitle));if(t.length>0)return t}return[{icon:this.config.bubbleConfig?.icon,title:this.config.bubbleConfig?.title,subtitle:this.config.bubbleConfig?.subtitle}]}getBubbleSwitchMode(){return this.config.bubbleConfig?.switchMode||`fade`}buildBubbleMessageContentNode(e){let t=document.createElement(`div`);t.style.cssText=`
      display: flex;
      align-items: center;
      gap: 8px;
      flex-direction: ${this.config.placement===`bottom-left`?`row`:`row-reverse`};
      box-sizing: border-box;
    `,t.setAttribute(`data-bytedesk-bubble-content`,`true`),t.setAttribute(`data-placement`,this.config.placement||`bottom-right`);let n=document.createElement(`span`);n.setAttribute(`data-bytedesk-bubble-role`,`icon`),n.style.fontSize=`20px`,n.textContent=e.icon||``,t.appendChild(n);let r=document.createElement(`div`);r.style.cssText=`min-width: 0; flex: 1;`;let i=document.createElement(`div`);i.setAttribute(`data-bytedesk-bubble-role`,`title`),i.style.fontWeight=`bold`,i.style.color=this.config.theme?.mode===`dark`?`#e5e7eb`:`#1f2937`,i.style.marginBottom=`4px`,i.style.textAlign=this.config.placement===`bottom-left`?`left`:`right`,i.textContent=e.title||``,r.appendChild(i);let a=document.createElement(`div`);return a.setAttribute(`data-bytedesk-bubble-role`,`subtitle`),a.style.fontSize=`0.9em`,a.style.color=this.config.theme?.mode===`dark`?`#9ca3af`:`#4b5563`,a.style.textAlign=this.config.placement===`bottom-left`?`left`:`right`,a.textContent=e.subtitle||``,r.appendChild(a),t.appendChild(r),{messageContent:t,iconSpan:n,title:i,subtitle:a}}buildBubbleTickerItemNode(e,t,n){let r=document.createElement(`div`);r.style.cssText=`
      position: relative;
      width: ${t?`${t}px`:`auto`};
      padding-bottom: 10px;
      box-sizing: border-box;
      display: block;
    `;let i=document.createElement(`div`);i.style.cssText=`
      background: ${this.config.theme?.mode===`dark`?`#1f2937`:`white`};
      color: ${this.config.theme?.mode===`dark`?`#e5e7eb`:`#1f2937`};
      padding: 12px 16px;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      max-width: 220px;
      position: relative;
      box-sizing: border-box;
      width: ${t?`${t}px`:`auto`};
      min-height: ${n?`${n-10}px`:`auto`};
    `;let{messageContent:a}=this.buildBubbleMessageContentNode(e);return t&&(a.style.width=`${Math.max(0,t-32)}px`),i.appendChild(a),r.appendChild(i),r}destroyBubbleTicker(){this.bubbleTickerStyleElement?.parentElement&&this.bubbleTickerStyleElement.parentElement.removeChild(this.bubbleTickerStyleElement),this.bubbleTickerStyleElement=null,this.bubbleTickerTrackElement?.parentElement&&this.bubbleTickerTrackElement.parentElement.removeChild(this.bubbleTickerTrackElement),this.bubbleTickerTrackElement=null}setBubbleTickerRunning(e){this.bubbleTickerTrackElement&&(this.bubbleTickerTrackElement.style.animationPlayState=e?`running`:`paused`)}initBubbleTicker(e){let t=this.bubbleMessageViewportElement,n=e||this.bubble?.messageElement||t?.parentElement;if(!(t instanceof HTMLElement))return;if(this.destroyBubbleTicker(),this.bubbleMessages.length<=1){this.bubbleMessageContentElement&&!t.contains(this.bubbleMessageContentElement)&&t.appendChild(this.bubbleMessageContentElement),this.renderBubbleMessage(0);return}if(!(n instanceof HTMLElement))return;let r=document.createElement(`div`);r.style.cssText=`
      position: absolute;
      visibility: hidden;
      pointer-events: none;
      left: 0;
      top: 0;
      z-index: -1;
      width: max-content;
      max-width: 220px;
    `,n.appendChild(r);let i=this.bubbleMessages.map(e=>{let t=this.buildBubbleTickerItemNode(e);return r.appendChild(t),t}),a=i.reduce((e,t)=>Math.max(e,t.offsetHeight),0),o=i.reduce((e,t)=>Math.max(e,t.offsetWidth),0);if(n.removeChild(r),!a||!o)return;t.style.width=`${o}px`,this.syncBubbleViewportHeight(a,!1);let s=document.createElement(`div`);s.style.cssText=`
      position: relative;
      display: flex;
      flex-direction: column;
      width: ${o}px;
      will-change: transform;
    `,[...this.bubbleMessages,...this.bubbleMessages].forEach(e=>{let t=this.buildBubbleTickerItemNode(e,o,a);t.style.height=`${a}px`,t.style.minHeight=`${a}px`,s.appendChild(t)});let c=a*this.bubbleMessages.length,l=Math.max(1.6,Number(this.config.bubbleConfig?.rotateInterval||3e3)/1e3)*this.bubbleMessages.length,u=`bytedeskBubbleTicker_${Date.now()}_${Math.random().toString(36).slice(2,8)}`,d=document.createElement(`style`);d.textContent=`
      @keyframes ${u} {
        from { transform: translateY(0); }
        to { transform: translateY(-${c}px); }
      }
    `,document.head.appendChild(d),s.style.animation=`${u} ${l}s linear infinite`,s.style.animationPlayState=`paused`,t.appendChild(s),this.bubbleTickerTrackElement=s,this.bubbleTickerStyleElement=d,this.bubbleMessageIndex=0}renderBubbleMessage(e){if(!this.bubbleMessages.length)return;if(this.getBubbleSwitchMode()===`ticker`){this.bubbleMessageIndex=(e%this.bubbleMessages.length+this.bubbleMessages.length)%this.bubbleMessages.length,this.syncBubbleViewportHeight();return}if(!this.bubbleIconElement||!this.bubbleTitleElement||!this.bubbleSubtitleElement)return;let t=this.bubbleMessages.length;this.bubbleMessageIndex=(e%t+t)%t;let n=this.bubbleMessages[this.bubbleMessageIndex];this.bubbleIconElement.textContent=n.icon||``,this.bubbleTitleElement.textContent=n.title||``,this.bubbleSubtitleElement.textContent=n.subtitle||``,this.syncBubbleViewportHeight()}syncBubbleViewportHeight(e,t=!1){if(!(this.bubbleMessageViewportElement instanceof HTMLElement))return;let n=e??this.bubbleMessageContentElement?.offsetHeight??0;n&&(this.bubbleMessageViewportElement.style.transition=t?`height 0.3s ease`:`none`,this.bubbleMessageViewportElement.style.height=`${n}px`)}cleanupPendingBubbleMessage(){this.bubblePendingMessageElement?.parentElement&&this.bubblePendingMessageElement.parentElement.removeChild(this.bubblePendingMessageElement),this.bubblePendingMessageElement=null}stopBubbleMessageTransition(){this.bubbleMessageTransitionTimer!==null&&(window.clearTimeout(this.bubbleMessageTransitionTimer),this.bubbleMessageTransitionTimer=null),this.setBubbleTickerRunning(!1),this.cleanupPendingBubbleMessage(),this.bubbleMessageViewportElement&&(this.bubbleMessageViewportElement.style.transition=``),this.bubbleMessageContentElement&&(this.bubbleMessageContentElement.style.transition=``,this.bubbleMessageContentElement.style.transform=`translateY(0)`,this.bubbleMessageContentElement.style.opacity=`1`)}transitionBubbleMessage(e){let t=this.bubble?.messageElement;if(!(t instanceof HTMLElement)||t.style.display===`none`){this.renderBubbleMessage(e);return}let n=this.getBubbleSwitchMode();if(n===`ticker`){this.renderBubbleMessage(e),this.setBubbleTickerRunning(!0);return}if(this.stopBubbleMessageTransition(),n===`slide-up`){let t=this.bubbleMessageViewportElement,n=this.bubbleMessageContentElement;if(!(t instanceof HTMLElement)||!(n instanceof HTMLElement)||!n.parentElement){this.renderBubbleMessage(e);return}let r=this.bubbleMessages[(e%this.bubbleMessages.length+this.bubbleMessages.length)%this.bubbleMessages.length],i=n.cloneNode(!0),a=i.querySelector(`[data-bytedesk-bubble-role="icon"]`),o=i.querySelector(`[data-bytedesk-bubble-role="title"]`),s=i.querySelector(`[data-bytedesk-bubble-role="subtitle"]`);a&&(a.textContent=r.icon||``),o&&(o.textContent=r.title||``),s&&(s.textContent=r.subtitle||``),i.style.position=`absolute`,i.style.left=`0`,i.style.top=`0`,i.style.width=`100%`,i.style.transform=`translateY(100%)`,i.style.opacity=`1`,i.style.transition=`transform 0.3s ease`,n.style.transition=`transform 0.3s ease`;let c=n.offsetHeight;n.parentElement.appendChild(i);let l=i.offsetHeight;this.syncBubbleViewportHeight(c,!1),this.bubblePendingMessageElement=i,window.requestAnimationFrame(()=>{this.syncBubbleViewportHeight(l,!0),n.style.transform=`translateY(-100%)`,i.style.transform=`translateY(0)`}),this.bubbleMessageTransitionTimer=window.setTimeout(()=>{this.renderBubbleMessage(e),n.style.transition=``,n.style.transform=`translateY(0)`,n.style.opacity=`1`,this.syncBubbleViewportHeight(l,!1),this.cleanupPendingBubbleMessage(),this.bubbleMessageTransitionTimer=null},320);return}let r=this.bubbleMessageContentElement?.offsetHeight??0;this.syncBubbleViewportHeight(r,!1),t.style.opacity=`0`,t.style.transform=`translateY(6px)`,this.bubbleMessageTransitionTimer=window.setTimeout(()=>{this.renderBubbleMessage(e);let n=this.bubbleMessageContentElement?.offsetHeight??r;this.syncBubbleViewportHeight(n,!0),t.style.opacity=`1`,t.style.transform=`translateY(0)`,this.bubbleMessageTransitionTimer=null},180)}stopBubbleMessageRotation(){this.bubbleMessageTimer!==null&&(window.clearInterval(this.bubbleMessageTimer),this.bubbleMessageTimer=null),this.setBubbleTickerRunning(!1)}startBubbleMessageRotation(){if(this.stopBubbleMessageRotation(),this.config.bubbleConfig?.autoRotate===!1||this.bubbleMessages.length<=1)return;if(this.getBubbleSwitchMode()===`ticker`){this.bubbleTickerTrackElement||this.initBubbleTicker(this.bubble?.messageElement||this.bubbleMessageViewportElement?.parentElement),this.setBubbleTickerRunning(!0);return}let e=Number(this.config.bubbleConfig?.rotateInterval||3e3),t=Number.isFinite(e)?Math.max(1e3,e):3e3;this.bubbleMessageTimer=window.setInterval(()=>{let e=this.bubble?.messageElement;e instanceof HTMLElement&&e.style.display!==`none`&&this.transitionBubbleMessage(this.bubbleMessageIndex+1)},t)}createBubble(){if(this.bubble&&document.body.contains(this.bubble)){_.debug(`createBubble: 气泡已存在，不重复创建`);return}this.bubble&&!document.body.contains(this.bubble)&&(_.debug(`createBubble: 清理已存在的 bubble 引用`),this.bubble=null),this.bubbleContainer&&!document.body.contains(this.bubbleContainer)&&(_.debug(`createBubble: 清理已存在的 bubbleContainer 引用`),this.bubbleContainer=null),this.buttonElements=[];let e=document.createElement(`div`);e.style.cssText=`
      position: fixed;
      ${this.config.placement===`bottom-left`?`left`:`right`}: ${this.config.marginSide}px;
      bottom: ${this.config.marginBottom}px;
      display: flex;
      flex-direction: column;
      align-items: ${this.config.placement===`bottom-left`?`flex-start`:`flex-end`};
      gap: 10px;
      z-index: 9999;
    `;let t=null;if(this.config.bubbleConfig?.show){let n=this.getBubbleSwitchMode()===`ticker`;t=document.createElement(`div`),t.style.cssText=`
        background: ${n?`transparent`:this.config.theme?.mode===`dark`?`#1f2937`:`white`};
        color: ${this.config.theme?.mode===`dark`?`#e5e7eb`:`#1f2937`};
        padding: ${n?`0`:`12px 16px`};
        border-radius: ${n?`0`:`8px`};
        box-shadow: ${n?`none`:`0 2px 12px rgba(0, 0, 0, 0.1)`};
        max-width: ${n?`none`:`220px`};
        margin-bottom: 8px;
        opacity: 0;
        transform: translateY(10px);
        transition: opacity 0.22s ease, transform 0.22s ease;
        position: relative;
      `;let r=document.createElement(`div`);r.style.cssText=`
        position: relative;
        overflow: hidden;
      `;let{messageContent:i,iconSpan:a,title:o,subtitle:s}=this.buildBubbleMessageContentNode({icon:this.config.bubbleConfig?.icon,title:this.config.bubbleConfig?.title,subtitle:this.config.bubbleConfig?.subtitle});if(n||r.appendChild(i),t.appendChild(r),!n){let e=document.createElement(`div`);e.style.cssText=`
          position: absolute;
          bottom: -6px;
          ${this.config.placement===`bottom-left`?`left: 24px`:`right: 24px`};
          width: 12px;
          height: 12px;
          background: ${this.config.theme?.mode===`dark`?`#1f2937`:`white`};
          transform: rotate(45deg);
          box-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
        `;let n=document.createElement(`div`);n.style.cssText=`
          position: absolute;
          bottom: 0;
          ${this.config.placement===`bottom-left`?`left: 18px`:`right: 18px`};
          width: 24px;
          height: 12px;
          background: ${this.config.theme?.mode===`dark`?`#1f2937`:`white`};
        `,t.appendChild(e),t.appendChild(n)}e.appendChild(t),this.bubbleMessages=this.getBubbleMessages(),this.bubbleMessageViewportElement=r,this.bubbleMessageContentElement=i,this.bubbleIconElement=a,this.bubbleTitleElement=o,this.bubbleSubtitleElement=s,this.bubbleMessageIndex=0,this.getBubbleSwitchMode()===`ticker`?this.initBubbleTicker(t):this.renderBubbleMessage(0),t.addEventListener(`mouseenter`,()=>{this.stopBubbleMessageRotation()}),t.addEventListener(`mouseleave`,()=>{this.startBubbleMessageRotation()}),setTimeout(()=>{t&&(t.style.opacity=`1`,t.style.transform=`translateY(0)`,this.startBubbleMessageRotation())},500)}let n=this.getEffectiveButtonConfigs(),r=document.createElement(`div`),i=this.isMultiButtonLayout(n),a=this.config.theme?.mode===`dark`,o=a?`#3B82F6`:`#0066FF`,s=this.config.theme?.backgroundColor||o;if(r.style.cssText=`
      display: flex;
      flex-direction: column;
      align-items: ${this.config.placement===`bottom-left`?`flex-start`:`flex-end`};
      gap: ${i?`0`:`10px`};
      background: ${i?s:`transparent`};
      border-radius: ${i?`18px`:`0`};
      overflow: ${i?`hidden`:`visible`};
      box-shadow: ${i?`0 10px 28px rgba(0, 0, 0, ${a?`0.32`:`0.16`})`:`none`};
    `,n.forEach((e,a)=>{let o=this.createButtonElement(e,t,{isMultiLayout:i,isLastButton:a===n.length-1});this.buttonElements.push(o),a===0&&(this.bubble=o),r.appendChild(o)}),this.renderUnreadBadge(),e.appendChild(r),this.config.draggable&&this.buttonElements.length>0){let t=0,n=0,r=0,i=0;this.buttonElements.forEach(a=>{a.addEventListener(`mousedown`,a=>{a.button===0&&(this.isDragging=!0,t=a.clientX,n=a.clientY,r=e.offsetLeft,i=e.offsetTop,e.style.transition=`none`)})}),document.addEventListener(`mousemove`,a=>{if(!this.isDragging)return;a.preventDefault();let o=a.clientX-t,s=a.clientY-n,c=r+o,l=i+s,u=window.innerHeight-e.offsetHeight;c<=window.innerWidth/2?(e.style.left=`${Math.max(0,c)}px`,e.style.right=`auto`,e.style.alignItems=`flex-start`,this.config.placement=`bottom-left`):(e.style.right=`${Math.max(0,window.innerWidth-c-e.offsetWidth)}px`,e.style.left=`auto`,e.style.alignItems=`flex-end`,this.config.placement=`bottom-right`),e.style.bottom=`${Math.min(Math.max(0,window.innerHeight-l-e.offsetHeight),u)}px`}),document.addEventListener(`mouseup`,()=>{this.isDragging&&(this.isDragging=!1,e.style.transition=`all 0.3s ease`,this.config.marginSide=parseInt(this.config.placement===`bottom-left`?e.style.left:e.style.right)||20,this.config.marginBottom=parseInt(e.style.bottom||`20`))})}document.body.appendChild(e),this.bubbleContainer=e,document.addEventListener(`click`,()=>{this.hideContextMenu()})}createChatWindow(){if(this.window&&document.body.contains(this.window)){_.debug(`createChatWindow: 聊天窗口已存在，不重复创建`);return}this.window&&!document.body.contains(this.window)&&(_.debug(`createChatWindow: 清理已存在的 window 引用`),this.window=null),this.window=document.createElement(`div`);let e=window.innerWidth<=768,t=window.innerWidth,n=window.innerHeight,r=Math.min(this.config.window?.width||t*.9,t*.9),i=Math.min(this.config.window?.height||n*.9,n*.9);e?this.window.style.cssText=`
        position: fixed;
        left: 0;
        bottom: 0;
        width: 100%;
        height: 100vh;
        height: 100dvh;
        display: none;
        z-index: 10000;
        border-top-left-radius: 12px;
        border-top-right-radius: 12px;
        overflow: hidden;
        box-sizing: border-box;
        padding-top: env(safe-area-inset-top);
        padding-bottom: env(safe-area-inset-bottom);
        transition: all ${this.config.animation?.duration}ms ${this.config.animation?.type};
      `:this.window.style.cssText=`
        position: fixed;
        ${this.config.placement===`bottom-right`?`right`:`left`}: ${this.config.marginSide}px;
        bottom: ${this.config.marginBottom}px;
        width: ${r}px;
        height: ${i}px;
        border-radius: 12px;
        box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
        display: none;
        overflow: hidden;
        z-index: 10000;
        transition: all ${this.config.animation?.duration}ms ${this.config.animation?.type};
      `;let a=document.createElement(`iframe`);a.setAttribute(`allow`,`microphone *; camera *; autoplay *; clipboard-write *`),a.style.cssText=`
      width: 100%;
      height: 100%;
      border: none;
      display: block;
      vertical-align: bottom;
    `,a.src=this.generateChatUrl(),_.debug(`iframe.src: `,a.src),this.window.appendChild(a),document.body.appendChild(this.window)}generateChatUrl(e=`messages`){_.debug(`this.config: `,this.config,e);let t=new URLSearchParams;Object.entries(this.config.chatConfig||{}).forEach(([e,n])=>{if(!(n==null||String(n).trim()===``))if(e===`debug`&&n===!0)t.append(`debug`,`1`);else if(e===`draft`&&n===!0)t.append(`draft`,`1`);else if(e===`loadHistory`&&n===!0)t.append(`loadHistory`,`1`);else if(e===`goodsInfo`||e===`orderInfo`)try{typeof n==`string`?t.append(e,n):t.append(e,JSON.stringify(n))}catch(t){_.error(`Error processing ${e}:`,t)}else if(e===`extra`)try{let r=typeof n==`string`?JSON.parse(n):n;r.goodsInfo&&delete r.goodsInfo,r.orderInfo&&delete r.orderInfo,Object.keys(r).length>0&&t.append(e,JSON.stringify(r))}catch(e){_.error(`Error processing extra parameter:`,e)}else e!==`debug`&&e!==`draft`&&e!==`loadHistory`&&t.append(e,String(n))});let n=w(this.config.browseConfig);n&&t.append(`browse`,n),Object.entries(this.config.theme||{}).forEach(([e,n])=>{t.append(e,String(n))}),t.append(`lang`,this.config.locale||`zh-cn`);let r=`${this.getChatPageBaseUrl()}?${t.toString()}`;return _.debug(`chat url: `,r),r}normalizePath(e,t=`/chat`){let n=(e||``).trim();return n?n.startsWith(`/`)?n:`/${n}`:t}getChatPageBaseUrl(){let e=this.normalizePath(this.config.chatPath,`/chat`),t=(this.config.htmlUrl||``).trim(),n=t.replace(/\/$/,``);if(!t)return e;if(n.match(/\/(chat(?:\/thread)?|webrtc|call)\/?$/))return n.replace(/\/(chat(?:\/thread)?|webrtc|call)\/?$/,e);try{let e=new URL(t,window.location.origin);if(e.pathname&&e.pathname!==`/`)return n}catch{if(n.startsWith(`/`))return n}return`${n}${e}`}setupMessageListener(){window.addEventListener(`message`,e=>{switch(e.data.type){case l:this.hideChat();break;case a:this.toggleMaximize();break;case b:this.minimizeWindow();break;case s:_.debug(`RECEIVE_MESSAGE`);break;case v:this.config.onMessageBubbleClick?.({uid:e.data.uid,type:e.data.clickedMessageType,content:e.data.content,extra:e.data.extra,position:e.data.position,status:e.data.status});break;case h:_.debug(`INVITE_VISITOR`);break;case f:_.debug(`INVITE_VISITOR_ACCEPT`);break;case d:_.debug(`INVITE_VISITOR_REJECT`);break;case u:this.handleLocalStorageData(e);break}})}handleLocalStorageData(e){let{uid:t,visitorUid:n}=e.data;_.debug(`handleLocalStorageData 被调用`,t,n,e.data);let r=localStorage.getItem(g),i=localStorage.getItem(c);if(r===t&&i===n){_.debug(`handleLocalStorageData: 值相同，跳过设置`);return}localStorage.setItem(g,t),localStorage.setItem(c,n),_.debug(`handleLocalStorageData: 已更新localStorage`,{uid:t,visitorUid:n}),this.config.onVisitorInfo?.(t,n)}sendMessageToIframe(e){let t=this.window?.querySelector(`iframe`);t&&t.contentWindow&&t.contentWindow.postMessage(e,`*`)}resetAnonymousVisitor(){localStorage.removeItem(g),localStorage.removeItem(c),this.sendMessageToIframe({type:y})}showChat(e){if(e&&(this.config={...this.config,...e},this.window&&=(document.body.removeChild(this.window),null)),this.window||this.createChatWindow(),this.window){let e=window.innerWidth<=768;if(this.window.style.display=`block`,this.config.forceRefresh){let e=this.window.querySelector(`iframe`);e&&(e.src=this.generateChatUrl())}if(this.setupResizeListener(),e&&this.window&&(this.window.style.transform=`translateY(100%)`,requestAnimationFrame(()=>{this.window&&(this.window.style.transform=`translateY(0)`)})),this.isVisible=!0,this.bubble){this.bubble.style.display=`none`;let e=this.bubble.messageElement;e instanceof HTMLElement&&(e.style.display=`none`)}}this.hideInviteDialog(),this.config.onShowChat?.()}hideChat(){if(this.window){if(window.innerWidth<=768?(this.window.style.transform=`translateY(100%)`,setTimeout(()=>{this.window&&(this.window.style.display=`none`)},this.config.animation?.duration||300)):this.window.style.display=`none`,this.isVisible=!1,this.buttonElements.length>0){this.applyConfiguredButtonVisibility();let e=this.bubble.messageElement;e instanceof HTMLElement&&(e.style.display=this.config.bubbleConfig?.show===!1?`none`:`block`)}this.config.onHideChat?.()}}showThread(e){return this.showChat({...e,chatPath:this.normalizePath(e?.threadPath||this.config.threadPath,`/chat/thread`)})}showWebrtc(e){return this.showChat({...e,chatPath:this.normalizePath(e?.webrtcPath||this.config.webrtcPath,`/webrtc`)})}showCall(e){return this.showChat({...e,chatPath:this.normalizePath(e?.callPath||this.config.callPath,`/call`)})}minimizeWindow(){this.window&&(this.windowState=`minimized`,this.window.style.display=`none`,this.hideChat())}toggleMaximize(){this.window&&window.open(this.generateChatUrl(),`_blank`)}setupResizeListener(){let e=()=>{if(!this.window||!this.isVisible)return;let e=window.innerWidth<=768,t=window.innerWidth,n=window.innerHeight;if(e)Object.assign(this.window.style,{left:`0`,bottom:`0`,width:`100%`,height:`100vh`,borderTopLeftRadius:`12px`,borderTopRightRadius:`12px`,borderBottomLeftRadius:`0`,borderBottomRightRadius:`0`,boxSizing:`border-box`,paddingTop:`env(safe-area-inset-top)`,paddingBottom:`env(safe-area-inset-bottom)`}),this.window.style.height=`100dvh`;else{let e=this.windowState===`maximized`?t:Math.min(this.config.window?.width||t*.9,t*.9),r=this.windowState===`maximized`?n:Math.min(this.config.window?.height||n*.9,n*.9),i=this.config.placement===`bottom-right`?this.config.marginSide:void 0,a=this.config.placement===`bottom-left`?this.config.marginSide:void 0;Object.assign(this.window.style,{width:`${e}px`,height:`${r}px`,right:i?`${i}px`:`auto`,left:a?`${a}px`:`auto`,bottom:`${this.config.marginBottom}px`,borderRadius:this.windowState===`maximized`?`0`:`12px`})}},t;window.addEventListener(`resize`,()=>{clearTimeout(t),t=window.setTimeout(e,100)}),e()}destroy(){this.isDestroyed=!0,this.stopBubbleMessageRotation(),this.stopBubbleMessageTransition(),this.destroyBubbleTicker(),this.bubbleMessageViewportElement=null,this.bubbleMessageContentElement=null,this.bubblePendingMessageElement=null,this.bubbleTickerTrackElement=null,this.bubbleTickerStyleElement=null,this.bubbleIconElement=null,this.bubbleTitleElement=null,this.bubbleSubtitleElement=null,this.bubbleMessages=[],this.bubbleMessageIndex=0,this.bubbleContainer&&document.body.contains(this.bubbleContainer)&&document.body.removeChild(this.bubbleContainer),this.hideButtonPreview(),this.bubbleContainer=null,this.bubble=null,this.buttonElements=[],this.window&&document.body.contains(this.window)&&(document.body.removeChild(this.window),this.window=null),window.removeEventListener(`resize`,this.setupResizeListener.bind(this)),this.loopTimer&&=(window.clearTimeout(this.loopTimer),null),this.inviteDialog&&document.body.contains(this.inviteDialog)&&(document.body.removeChild(this.inviteDialog),this.inviteDialog=null),this.contextMenu&&document.body.contains(this.contextMenu)&&(document.body.removeChild(this.contextMenu),this.contextMenu=null),this.hideTimeout&&=(clearTimeout(this.hideTimeout),null),this.selectionDebounceTimer&&=(clearTimeout(this.selectionDebounceTimer),null),this.destroyFeedbackFeature()}createInviteDialog(){if(this.inviteDialog&&document.body.contains(this.inviteDialog)){_.debug(`createInviteDialog: 邀请框已存在，不重复创建`);return}this.inviteDialog&&!document.body.contains(this.inviteDialog)&&(_.debug(`createInviteDialog: 清理已存在的 inviteDialog 引用`),this.inviteDialog=null);let e=this.config.theme?.mode===`dark`;if(this.inviteDialog=document.createElement(`div`),this.inviteDialog.style.cssText=`
      position: fixed;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      background: ${e?`#1f2937`:`white`};
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 4px 24px rgba(0, 0, 0, ${e?`0.3`:`0.15`});
      z-index: 10001;
      display: none;
      max-width: 300px;
      text-align: center;
    `,this.config.inviteConfig?.icon){let t=document.createElement(`div`);t.style.cssText=`
        font-size: 32px;
        margin-bottom: 12px;
        color: ${e?`#e5e7eb`:`#333`};
      `,t.textContent=this.config.inviteConfig.icon,this.inviteDialog.appendChild(t)}let t=document.createElement(`div`);t.style.cssText=`
      margin-bottom: 16px;
      color: ${e?`#e5e7eb`:`#333`};
    `,t.textContent=this.config.inviteConfig?.text||`需要帮助吗？点击开始对话`,this.inviteDialog.appendChild(t);let n=document.createElement(`div`);n.style.cssText=`
      display: flex;
      gap: 10px;
      justify-content: center;
    `;let r=document.createElement(`button`);r.textContent=this.config.inviteConfig?.acceptText||`开始对话`;let i=this.config.theme?.backgroundColor||(e?`#3B82F6`:`#0066FF`);r.style.cssText=`
      padding: 8px 16px;
      background: ${i};
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `,r.onclick=()=>{this.hideInviteDialog(),this.showChat(),this.config.inviteConfig?.onAccept?.()};let a=document.createElement(`button`);a.textContent=this.config.inviteConfig?.rejectText||`稍后再说`,a.style.cssText=`
      padding: 8px 16px;
      background: ${e?`#374151`:`#f5f5f5`};
      color: ${e?`#d1d5db`:`#666`};
      border: none;
      border-radius: 4px;
      cursor: pointer;
    `,a.onclick=()=>{this.hideInviteDialog(),this.config.inviteConfig?.onReject?.(),this.handleInviteLoop()},n.appendChild(r),n.appendChild(a),this.inviteDialog.appendChild(n),document.body.appendChild(this.inviteDialog)}showInviteDialog(){this.inviteDialog&&(this.inviteDialog.style.display=`block`,this.config.inviteConfig?.onOpen?.())}hideInviteDialog(){_.debug(`hideInviteDialog before`),this.inviteDialog&&(this.inviteDialog.style.display=`none`,this.config.inviteConfig?.onClose?.(),_.debug(`hideInviteDialog after`))}handleInviteLoop(){let{loop:e,loopDelay:t=3e3,loopCount:n=1/0}=this.config.inviteConfig||{};!e||this.loopCount>=n-1||(this.loopTimer&&window.clearTimeout(this.loopTimer),this.loopTimer=window.setTimeout(()=>{this.loopCount++,this.showInviteDialog()},t))}showButton(){if(this.buttonElements.length>0&&this.buttonElements.every(e=>e.style.display!==`none`)){_.debug(`showButton: 按钮已经显示，无需重复显示`);return}this.buttonElements.length>0?(this.buttonElements.forEach(e=>{e.style.display=`flex`}),_.debug(`showButton: 按钮已显示`)):_.debug(`showButton: bubble 不存在，需要先创建`)}hideButton(){this.buttonElements.length>0&&this.buttonElements.forEach(e=>{e.style.display=`none`})}showBubble(){if(this.bubble){let e=this.bubble.messageElement;if(e instanceof HTMLElement){if(e.style.display!==`none`&&e.style.opacity!==`0`){_.debug(`showBubble: 气泡已经显示，无需重复显示`);return}e.style.display=`block`,setTimeout(()=>{e.style.opacity=`1`,e.style.transform=`translateY(0)`,this.startBubbleMessageRotation()},100),_.debug(`showBubble: 气泡已显示`)}else _.debug(`showBubble: messageElement 不存在`)}else _.debug(`showBubble: bubble 不存在`)}hideBubble(){if(this.bubble){let e=this.bubble.messageElement;e instanceof HTMLElement&&(this.stopBubbleMessageRotation(),this.stopBubbleMessageTransition(),e.style.opacity=`0`,e.style.transform=`translateY(10px)`,setTimeout(()=>{e.style.display=`none`},300))}}createContextMenu(){this.contextMenu=document.createElement(`div`),this.contextMenu.style.cssText=`
      position: fixed;
      background: white;
      border-radius: 4px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      padding: 4px 0;
      display: none;
      z-index: 10000;
      min-width: 150px;
    `;let e=[{text:`隐藏按钮和气泡`,onClick:()=>{this.hideButton(),this.hideBubble()}},{text:`切换位置`,onClick:()=>{this.togglePlacement()}}];e.forEach((t,n)=>{let r=document.createElement(`div`);if(r.style.cssText=`
        padding: 8px 16px;
        cursor: pointer;
        color: #333;
        font-size: 14px;
        
        &:hover {
          background: #f5f5f5;
        }
      `,r.textContent=t.text,r.onclick=()=>{t.onClick(),this.hideContextMenu()},this.contextMenu&&this.contextMenu.appendChild(r),n<e.length-1){let e=document.createElement(`div`);e.style.cssText=`
          height: 1px;
          background: #eee;
          margin: 4px 0;
        `,this.contextMenu&&this.contextMenu.appendChild(e)}}),document.body.appendChild(this.contextMenu)}showContextMenu(e){if(e.preventDefault(),this.contextMenu||this.createContextMenu(),this.contextMenu){this.contextMenu.style.visibility=`hidden`,this.contextMenu.style.display=`block`;let t=this.contextMenu.offsetWidth,n=this.contextMenu.offsetHeight,r=e.clientX,i=e.clientY;r+t>window.innerWidth&&(r-=t),i+n>window.innerHeight&&(i-=n),r=Math.max(0,r),i=Math.max(0,i),this.contextMenu.style.left=`${r}px`,this.contextMenu.style.top=`${i}px`,this.contextMenu.style.visibility=`visible`}}hideContextMenu(){this.contextMenu&&(this.contextMenu.style.display=`none`)}togglePlacement(){if(!this.bubble)return;this.config.placement=this.config.placement===`bottom-left`?`bottom-right`:`bottom-left`;let e=this.bubble.parentElement;e&&(e.style.left=this.config.placement===`bottom-left`?`${this.config.marginSide}px`:`auto`,e.style.right=this.config.placement===`bottom-right`?`${this.config.marginSide}px`:`auto`,e.style.alignItems=this.config.placement===`bottom-left`?`flex-start`:`flex-end`,this.window&&this.isVisible&&(this.window.style.left=this.config.placement===`bottom-left`?`${this.config.marginSide}px`:`auto`,this.window.style.right=this.config.placement===`bottom-right`?`${this.config.marginSide}px`:`auto`),this.config.onConfigChange?.({placement:this.config.placement}))}initFeedbackFeature(){if(_.debug(`BytedeskWeb: 初始化文档反馈功能开始`),_.debug(`BytedeskWeb: feedbackConfig:`,this.config.feedbackConfig),_.debug(`BytedeskWeb: feedbackConfig.enabled:`,this.config.feedbackConfig?.enabled),!this.config.feedbackConfig?.enabled){_.debug(`BytedeskWeb: 文档反馈功能未启用，退出初始化`);return}(this.feedbackTooltip||this.feedbackDialog)&&(_.debug(`BytedeskWeb: 反馈功能已存在，先销毁再重新创建`),this.destroyFeedbackFeature()),this.config.feedbackConfig.trigger===`selection`||this.config.feedbackConfig.trigger===`both`?(_.debug(`BytedeskWeb: 触发器匹配，设置文本选择监听器`),_.debug(`BytedeskWeb: 触发器类型:`,this.config.feedbackConfig.trigger),this.setupTextSelectionListener()):(_.debug(`BytedeskWeb: 触发器不匹配，跳过文本选择监听器`),_.debug(`BytedeskWeb: 触发器类型:`,this.config.feedbackConfig.trigger)),_.debug(`BytedeskWeb: 开始创建反馈提示框`),this.createFeedbackTooltip(),_.debug(`BytedeskWeb: 开始创建反馈对话框`),this.createFeedbackDialog(),_.debug(`BytedeskWeb: 文档反馈功能初始化完成`),_.debug(`BytedeskWeb: 反馈提示框存在:`,!!this.feedbackTooltip),_.debug(`BytedeskWeb: 反馈对话框存在:`,!!this.feedbackDialog)}setupTextSelectionListener(){_.debug(`BytedeskWeb: 设置文本选择监听器`),document.addEventListener(`mouseup`,e=>{this.lastMouseEvent=e,_.debug(`BytedeskWeb: mouseup事件触发`,e),this.handleTextSelectionWithDebounce(e)},{capture:!0,passive:!0}),document.addEventListener(`selectionchange`,()=>{if(!this.lastMouseEvent){_.debug(`BytedeskWeb: selectionchange事件触发（无鼠标事件）`);let e=new MouseEvent(`mouseup`,{clientX:window.innerWidth/2,clientY:window.innerHeight/2});this.handleTextSelectionWithDebounce(e)}}),document.addEventListener(`keyup`,e=>{(e.shiftKey||e.ctrlKey||e.metaKey)&&(_.debug(`BytedeskWeb: keyup事件触发（带修饰键）`,e),this.handleTextSelectionWithDebounce(e))},{capture:!0,passive:!0}),document.addEventListener(`click`,e=>{e.target?.closest(`[data-bytedesk-feedback]`)||this.hideFeedbackTooltip()}),_.debug(`BytedeskWeb: 文本选择监听器设置完成`)}handleTextSelectionWithDebounce(e){this.config.isDebug&&_.debug(`BytedeskWeb: handleTextSelectionWithDebounce被调用 - 防抖机制生效`),this.selectionDebounceTimer&&(clearTimeout(this.selectionDebounceTimer),this.config.isDebug&&_.debug(`BytedeskWeb: 清除之前的防抖定时器`)),this.selectionDebounceTimer=setTimeout(()=>{this.config.isDebug&&_.debug(`BytedeskWeb: 防抖延迟结束，开始处理文本选择`),this.handleTextSelection(e)},200)}handleTextSelection(e){this.config.isDebug&&_.debug(`BytedeskWeb: handleTextSelection被调用`);let t=window.getSelection();if(this.config.isDebug&&(_.debug(`BytedeskWeb: window.getSelection()结果:`,t),_.debug(`BytedeskWeb: selection.rangeCount:`,t?.rangeCount)),!t||t.rangeCount===0){this.config.isDebug&&_.debug(`BytedeskWeb: 没有选择或范围为0，隐藏提示`),this.hideFeedbackTooltip();return}let n=t.toString().trim();if(this.config.isDebug&&(_.debug(`BytedeskWeb: 检测到文本选择:`,`"${n}"`),_.debug(`BytedeskWeb: 选中文本长度:`,n.length)),n===this.lastSelectionText&&this.isTooltipVisible){this.config.isDebug&&_.debug(`BytedeskWeb: 文本选择未变化且提示框已显示，跳过处理`);return}if(n.length===0){this.config.isDebug&&_.debug(`BytedeskWeb: 选中文本为空，隐藏提示`),this.hideFeedbackTooltip();return}if(n.length<3){this.config.isDebug&&_.debug(`BytedeskWeb: 选中文本太短，忽略:`,`"${n}"`),this.hideFeedbackTooltip();return}this.selectedText=n,this.lastSelectionText=n;try{this.lastSelectionRect=t.getRangeAt(0).getBoundingClientRect(),this.config.isDebug&&_.debug(`BytedeskWeb: 存储选中文本位置:`,this.lastSelectionRect)}catch(e){this.config.isDebug&&_.warn(`BytedeskWeb: 获取选中文本位置失败:`,e),this.lastSelectionRect=null}this.config.isDebug&&_.debug(`BytedeskWeb: 设置selectedText为:`,`"${n}"`),this.config.feedbackConfig?.showOnSelection?(this.config.isDebug&&_.debug(`BytedeskWeb: 配置允许显示选择提示，调用showFeedbackTooltip`),this.showFeedbackTooltip(this.lastMouseEvent||void 0)):this.config.isDebug&&(_.debug(`BytedeskWeb: 配置不允许显示选择提示`),_.debug(`BytedeskWeb: feedbackConfig.showOnSelection:`,this.config.feedbackConfig?.showOnSelection))}createFeedbackTooltip(){if(this.config.isDebug&&_.debug(`BytedeskWeb: createFeedbackTooltip被调用`),this.feedbackTooltip&&document.body.contains(this.feedbackTooltip)){this.config.isDebug&&_.debug(`BytedeskWeb: 反馈提示框已存在且在DOM中，跳过创建`);return}this.feedbackTooltip&&!document.body.contains(this.feedbackTooltip)&&(this.config.isDebug&&_.debug(`BytedeskWeb: 提示框变量存在但不在DOM中，重置变量`),this.feedbackTooltip=null),this.feedbackTooltip=document.createElement(`div`),this.feedbackTooltip.setAttribute(`data-bytedesk-feedback`,`tooltip`),this.feedbackTooltip.style.cssText=`
      position: fixed;
      background: #2e88ff;
      color: white;
      padding: 8px 16px;
      border-radius: 6px;
      font-size: 14px;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
      cursor: pointer;
      z-index: 999999;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      transform: translateY(-100%);
      margin-top: -8px;
      user-select: none;
      opacity: 0;
      transition: opacity 0.2s ease;
      display: none;
    `;let e=this.config.feedbackConfig?.selectionText||`文档反馈`;this.config.isDebug&&_.debug(`BytedeskWeb: 提示框文本:`,e),this.feedbackTooltip.innerHTML=`
      <span style="margin-right: 4px;">📝</span>
      ${e}
    `,this.feedbackTooltip.addEventListener(`click`,async e=>{this.config.isDebug&&(_.debug(`BytedeskWeb: 反馈提示框被点击`),_.debug(`BytedeskWeb: 点击时选中文字:`,this.selectedText)),e.stopPropagation(),e.preventDefault();try{await this.showFeedbackDialog(),this.config.isDebug&&_.debug(`BytedeskWeb: 对话框显示完成，现在隐藏提示框`),this.hideFeedbackTooltip()}catch(e){this.config.isDebug&&_.error(`BytedeskWeb: 显示对话框时出错:`,e)}}),document.body.appendChild(this.feedbackTooltip),this.config.isDebug&&(_.debug(`BytedeskWeb: 反馈提示框已创建并添加到页面`),_.debug(`BytedeskWeb: 提示框元素:`,this.feedbackTooltip))}showFeedbackTooltip(e){this.config.isDebug&&(_.debug(`BytedeskWeb: showFeedbackTooltip被调用`),_.debug(`BytedeskWeb: feedbackTooltip存在:`,!!this.feedbackTooltip),_.debug(`BytedeskWeb: selectedText存在:`,!!this.selectedText));let t=this.feedbackTooltip&&document.body.contains(this.feedbackTooltip);if(this.config.isDebug&&_.debug(`BytedeskWeb: feedbackTooltip在DOM中:`,t),(!this.feedbackTooltip||!t)&&(this.config.isDebug&&_.debug(`BytedeskWeb: 提示框不存在或已从DOM中移除，重新创建`),this.createFeedbackTooltip()),!this.feedbackTooltip||!this.selectedText){this.config.isDebug&&_.debug(`BytedeskWeb: 提示框或选中文本不存在，退出显示`);return}let n=window.getSelection();if(!n||n.rangeCount===0){this.config.isDebug&&_.debug(`BytedeskWeb: 无有效选择，无法计算位置`);return}let r=n.getRangeAt(0),i;try{let e=document.createRange();e.setStart(r.startContainer,r.startOffset);let t=r.startOffset,n=r.startContainer.textContent||``;if(r.startContainer.nodeType===Node.TEXT_NODE){for(;t<Math.min(n.length,r.endOffset);){let n=document.createRange();n.setStart(r.startContainer,r.startOffset),n.setEnd(r.startContainer,t+1);let i=n.getBoundingClientRect(),a=e.getBoundingClientRect();if(Math.abs(i.top-a.top)>5)break;t++}e.setEnd(r.startContainer,Math.max(t,r.startOffset+1)),i=e.getBoundingClientRect()}else i=r.getBoundingClientRect()}catch(e){this.config.isDebug&&_.debug(`BytedeskWeb: 获取第一行位置失败，使用整个选择区域:`,e),i=r.getBoundingClientRect()}this.config.isDebug&&_.debug(`BytedeskWeb: 选中文本第一行位置信息:`,{left:i.left,top:i.top,right:i.right,bottom:i.bottom,width:i.width,height:i.height});let a=i.left+5,o=i.top-40-15,s=window.innerWidth,c=window.innerHeight,l=window.scrollX,u=window.scrollY;a<10&&(a=10),a+120>s-10&&(a=s-120-10),o<u+10&&(o=i.bottom+15,this.config.isDebug&&_.debug(`BytedeskWeb: 上方空间不足，调整为显示在选中文字第一行下方`)),a+=l,o+=u,this.config.isDebug&&_.debug(`BytedeskWeb: 最终提示框位置:`,{x:a,y:o,说明:`显示在选中文字第一行左上角上方，增加间距避免遮挡`,verticalOffset:15,horizontalOffset:5,选中区域:i,视口信息:{viewportWidth:s,viewportHeight:c,scrollX:l,scrollY:u}}),this.feedbackTooltip.style.position=`absolute`,this.feedbackTooltip.style.left=a+`px`,this.feedbackTooltip.style.top=o+`px`,this.feedbackTooltip.style.display=`block`,this.feedbackTooltip.style.visibility=`visible`,this.feedbackTooltip.style.opacity=`0`,this.feedbackTooltip.style.zIndex=`999999`,this.config.isDebug&&_.debug(`BytedeskWeb: 提示框位置已设置，样式:`,{position:this.feedbackTooltip.style.position,left:this.feedbackTooltip.style.left,top:this.feedbackTooltip.style.top,display:this.feedbackTooltip.style.display,visibility:this.feedbackTooltip.style.visibility,opacity:this.feedbackTooltip.style.opacity,zIndex:this.feedbackTooltip.style.zIndex}),this.isTooltipVisible=!0,setTimeout(()=>{this.feedbackTooltip&&this.isTooltipVisible&&(this.feedbackTooltip.style.opacity=`1`,this.config.isDebug&&_.debug(`BytedeskWeb: 提示框透明度设置为1，应该可见了`))},10)}hideFeedbackTooltip(){let e=this.feedbackTooltip&&document.body.contains(this.feedbackTooltip);if(this.config.isDebug&&(_.debug(`BytedeskWeb: hideFeedbackTooltip被调用`),_.debug(`BytedeskWeb: feedbackTooltip存在:`,!!this.feedbackTooltip),_.debug(`BytedeskWeb: feedbackTooltip在DOM中:`,e)),!this.feedbackTooltip||!e){this.isTooltipVisible=!1,this.lastSelectionText=``,this.config.isDebug&&_.debug(`BytedeskWeb: 提示框不存在或不在DOM中，仅重置状态`);return}this.isTooltipVisible=!1,this.lastSelectionText=``,this.feedbackTooltip.style.opacity=`0`,setTimeout(()=>{this.feedbackTooltip&&document.body.contains(this.feedbackTooltip)&&!this.isTooltipVisible?(this.feedbackTooltip.style.display=`none`,this.feedbackTooltip.style.visibility=`hidden`,this.config.isDebug&&_.debug(`BytedeskWeb: 提示框已隐藏`)):this.config.isDebug&&this.isTooltipVisible&&_.debug(`BytedeskWeb: 跳过隐藏操作，提示框状态已改变为可见`)},100)}createFeedbackDialog(){if(this.config.isDebug&&_.debug(`BytedeskWeb: createFeedbackDialog被调用`),this.feedbackDialog&&document.body.contains(this.feedbackDialog)){this.config.isDebug&&_.debug(`BytedeskWeb: 反馈对话框已存在且在DOM中，跳过创建`);return}this.feedbackDialog&&!document.body.contains(this.feedbackDialog)&&(this.config.isDebug&&_.debug(`BytedeskWeb: 对话框变量存在但不在DOM中，重置变量`),this.feedbackDialog=null),this.feedbackDialog=document.createElement(`div`),this.feedbackDialog.setAttribute(`data-bytedesk-feedback`,`dialog`),this.feedbackDialog.style.cssText=`
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      z-index: 1000000;
      display: none;
      justify-content: center;
      align-items: center;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
    `;let e=document.createElement(`div`);e.style.cssText=`
      background: white;
      border-radius: 12px;
      padding: 24px;
      width: 90%;
      max-width: 600px;
      max-height: 80vh;
      overflow-y: auto;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
      position: relative;
    `,e.innerHTML=`
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h3 style="margin: 0; font-size: 18px; font-weight: 600; color: #333;">
          ${this.config.feedbackConfig?.dialogTitle||`提交意见反馈`}
        </h3>
        <button type="button" data-action="close" style="
          background: none;
          border: none;
          font-size: 24px;
          cursor: pointer;
          color: #999;
          line-height: 1;
          padding: 0;
          width: 24px;
          height: 24px;
          display: flex;
          align-items: center;
          justify-content: center;
        ">×</button>
      </div>
      
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500; color: #555;">选中的文字：</label>
        <div id="bytedesk-selected-text" style="
          background: #f5f5f5;
          padding: 12px;
          border-radius: 6px;
          border-left: 4px solid #2e88ff;
          font-size: 14px;
          line-height: 1.5;
          color: #333;
          max-height: 100px;
          overflow-y: auto;
        "></div>
      </div>

      ${this.config.feedbackConfig?.categoryNames&&this.config.feedbackConfig.categoryNames.length>0?`
      <div style="margin-bottom: 16px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500; color: #333;">
          <span style="color: #ff4d4f;">*</span> ${this.config.feedbackConfig?.typesSectionTitle||`问题类型`} ${this.config.feedbackConfig?.typesDescription||`（多选）`}
        </label>
        <div id="bytedesk-feedback-types" style="
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: 12px;
          margin-bottom: 8px;
        ">
          ${this.config.feedbackConfig.categoryNames.map(e=>`
            <label style="
              display: flex;
              align-items: flex-start;
              gap: 8px;
              cursor: pointer;
              padding: 8px;
              border-radius: 4px;
              transition: background-color 0.2s;
            " onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">
              <input type="checkbox" name="feedback-type" value="${e}" style="
                margin: 2px 0 0 0;
                cursor: pointer;
              ">
              <span style="
                font-size: 14px;
                line-height: 1.4;
                color: #333;
                flex: 1;
              ">${e}</span>
            </label>
          `).join(``)}
        </div>
      </div>
      `:``}

      ${this.config.feedbackConfig?.submitScreenshot===!1?``:`
      <div style="margin-bottom: 16px;">
        <label style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px; font-weight: 500; color: #555;">
          <input type="checkbox" id="bytedesk-submit-screenshot" checked style="cursor: pointer;">
          提交截图内容
        </label>
        <div id="bytedesk-screenshot-container" style="
          border: 2px dashed #ddd;
          border-radius: 6px;
          padding: 20px;
          text-align: center;
          color: #999;
          min-height: 80px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-direction: column;
          gap: 8px;
        ">
          <div style="font-size: 24px;">📷</div>
          <div>正在生成截图预览...</div>
          <div style="font-size: 12px; color: #666;">截图将在提交时上传到服务器</div>
        </div>
      </div>
      `}

      <div style="margin-bottom: 20px;">
        <label style="display: block; margin-bottom: 8px; font-weight: 500; color: #333;">
          <span style="color: #ff4d4f;">*</span> 问题描述
        </label>
        <textarea id="bytedesk-feedback-text" placeholder="${this.config.feedbackConfig?.placeholder||`请详细描述您的问题或优化建议`}" style="
          width: 100%;
          min-height: 120px;
          padding: 12px;
          border: 1px solid #ddd;
          border-radius: 6px;
          font-size: 14px;
          font-family: inherit;
          resize: vertical;
          box-sizing: border-box;
        "></textarea>
      </div>

      <div style="display: flex; justify-content: flex-end; gap: 12px;">
        <button type="button" data-action="cancel" style="
          background: #f5f5f5;
          color: #666;
          border: 1px solid #ddd;
          padding: 10px 20px;
          border-radius: 6px;
          cursor: pointer;
          font-size: 14px;
          font-family: inherit;
        ">${this.config.feedbackConfig?.cancelText||`取消`}</button>
        <button type="button" data-action="submit" style="
          background: #2e88ff;
          color: white;
          border: none;
          padding: 10px 20px;
          border-radius: 6px;
          cursor: pointer;
          font-size: 14px;
          font-family: inherit;
        ">${this.config.feedbackConfig?.submitText||`提交反馈`}</button>
      </div>

      <div style="margin-top: 12px; text-align: center; font-size: 12px; color: #999;">
        <a href="https://www.weiyuai.cn/" target="_blank" rel="noopener noreferrer" style="color: #aaaaaa; text-decoration: none;">
           微语技术支持
        </a>
      </div>
    `,e.addEventListener(`click`,e=>{switch(e.target.getAttribute(`data-action`)){case`close`:case`cancel`:this.hideFeedbackDialog(),this.config.feedbackConfig?.onCancel?.();break;case`submit`:this.submitFeedback();break}}),this.feedbackDialog.appendChild(e),this.feedbackDialog.addEventListener(`click`,e=>{e.target===this.feedbackDialog&&(this.hideFeedbackDialog(),this.config.feedbackConfig?.onCancel?.())}),document.addEventListener(`keydown`,e=>{e.key===`Escape`&&this.feedbackDialog?.style.display===`flex`&&(this.hideFeedbackDialog(),this.config.feedbackConfig?.onCancel?.())}),document.body.appendChild(this.feedbackDialog)}async showFeedbackDialog(){this.config.isDebug&&(_.debug(`BytedeskWeb: showFeedbackDialog被调用`),_.debug(`BytedeskWeb: feedbackDialog存在:`,!!this.feedbackDialog));let e=this.feedbackDialog&&document.body.contains(this.feedbackDialog);if(this.config.isDebug&&_.debug(`BytedeskWeb: feedbackDialog在DOM中:`,e),(!this.feedbackDialog||!e)&&(this.config.isDebug&&_.debug(`BytedeskWeb: 对话框不存在或已从DOM中移除，重新创建`),this.createFeedbackDialog()),!this.feedbackDialog){this.config.isDebug&&_.debug(`BytedeskWeb: 对话框创建失败，退出显示`);return}this.config.isDebug&&_.debug(`BytedeskWeb: 开始填充对话框内容`);let t=this.feedbackDialog.querySelector(`#bytedesk-selected-text`);t&&(t.textContent=this.selectedText||``,this.config.isDebug&&_.debug(`BytedeskWeb: 已填充选中文字:`,this.selectedText));let n=this.feedbackDialog.querySelector(`#bytedesk-feedback-text`);n&&(n.value=``),this.feedbackDialog.style.display=`flex`,this.config.isDebug&&(_.debug(`BytedeskWeb: 对话框已设置为显示状态`),_.debug(`BytedeskWeb: 对话框样式:`,{display:this.feedbackDialog.style.display,visibility:this.feedbackDialog.style.visibility,zIndex:this.feedbackDialog.style.zIndex}));try{await this.generateScreenshotPreview(),this.config.isDebug&&_.debug(`BytedeskWeb: 截图预览生成完成`)}catch(e){this.config.isDebug&&_.error(`BytedeskWeb: 截图预览生成失败:`,e)}}hideFeedbackDialog(){this.feedbackDialog&&(this.feedbackDialog.style.display=`none`)}async generateAndUploadScreenshot(){try{let e,t=this.feedbackDialog?.screenshotCanvas;if(t)this.config.isDebug&&_.debug(`BytedeskWeb: 使用已生成的截图canvas`),e=t;else{let t=await this.loadHtml2Canvas();if(!t)return this.config.isDebug&&_.debug(`BytedeskWeb: html2canvas加载失败，跳过截图`),null;this.config.isDebug&&_.debug(`BytedeskWeb: 重新生成截图`);let n=this.calculateScreenshotArea();e=await t(document.body,{height:n.height,width:n.width,x:n.x,y:n.y,useCORS:!0,allowTaint:!0,backgroundColor:`#ffffff`,scale:1,ignoreElements:e=>e.hasAttribute(`data-bytedesk-feedback`)||e.closest(`[data-bytedesk-feedback]`)!==null})}return new Promise(t=>{e.toBlob(async e=>{if(!e){_.error(`无法生成截图blob`),t(null);return}try{let r=`screenshot_${Date.now()}.jpg`,i=new File([e],r,{type:`image/jpeg`});this.config.isDebug&&_.debug(`BytedeskWeb: 截图生成成功，文件大小:`,Math.round(e.size/1024),`KB`);let{uploadScreenshot:a}=await n(async()=>{let{uploadScreenshot:e}=await import(`./upload-Ck4f7jLj.js`);return{uploadScreenshot:e}},__vite__mapDeps([8,0,1,2,3,4,5])),o=await a(i,{orgUid:this.config.chatConfig?.org||``,isDebug:this.config.isDebug});this.config.isDebug&&_.debug(`BytedeskWeb: 截图上传成功，URL:`,o),t(o)}catch(e){_.error(`截图上传失败:`,e),t(null)}},`image/jpeg`,.8)})}catch(e){return _.error(`生成截图失败:`,e),null}}async generateScreenshotPreview(){let e=this.feedbackDialog?.querySelector(`#bytedesk-screenshot-container`);if(e)try{let t=await this.loadHtml2Canvas();if(!t){e.innerHTML=`
          <div style="color: #999; text-align: center; padding: 20px; flex-direction: column; gap: 8px; display: flex; align-items: center;">
            <div style="font-size: 24px;">📷</div>
            <div>截图功能暂时不可用</div>
            <div style="font-size: 12px; color: #666;">网络连接问题或资源加载失败</div>
          </div>
        `;return}e.innerHTML=`正在生成截图预览...`,this.config.isDebug&&_.debug(`BytedeskWeb: 开始生成截图预览`);let n=this.calculateScreenshotArea(),r=await t(document.body,{height:n.height,width:n.width,x:n.x,y:n.y,useCORS:!0,allowTaint:!0,backgroundColor:`#ffffff`,scale:1,ignoreElements:e=>e.hasAttribute(`data-bytedesk-feedback`)||e.closest(`[data-bytedesk-feedback]`)!==null}),i=document.createElement(`img`);i.src=r.toDataURL(`image/jpeg`,.8),i.style.cssText=`
        max-width: 100%;
        max-height: 200px;
        border-radius: 4px;
        border: 1px solid #ddd;
        cursor: pointer;
      `,i.onclick=()=>{let e=document.createElement(`img`);e.src=i.src,e.style.cssText=`
          max-width: 90vw;
          max-height: 90vh;
          border-radius: 8px;
          box-shadow: 0 8px 32px rgba(0,0,0,0.3);
        `;let t=document.createElement(`div`);t.style.cssText=`
          position: fixed;
          top: 0;
          left: 0;
          width: 100vw;
          height: 100vh;
          background: rgba(0,0,0,0.8);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 1000001;
          cursor: pointer;
        `;let n=document.createElement(`div`);n.style.cssText=`
          position: absolute;
          top: 20px;
          right: 20px;
          color: white;
          font-size: 14px;
          background: rgba(0,0,0,0.6);
          padding: 8px 12px;
          border-radius: 4px;
          user-select: none;
        `,n.textContent=`点击任意位置关闭`,t.appendChild(n),t.appendChild(e),t.onclick=()=>document.body.removeChild(t),document.body.appendChild(t)};let a=document.createElement(`div`);a.style.cssText=`
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
      `,a.appendChild(i);let o=document.createElement(`div`);o.style.cssText=`
        font-size: 12px;
        color: #666;
        text-align: center;
      `,o.innerHTML=`点击图片可放大查看<br/>提交时将自动上传此截图`,a.appendChild(o),e.innerHTML=``,e.appendChild(a),this.feedbackDialog.screenshotCanvas=r,this.config.isDebug&&_.debug(`BytedeskWeb: 截图预览生成成功`)}catch(t){_.error(`生成截图预览失败:`,t),e.innerHTML=`
        <div style="color: #ff6b6b; text-align: center; flex-direction: column; gap: 8px; display: flex; align-items: center;">
          <div style="font-size: 24px;">⚠️</div>
          <div>截图预览生成失败</div>
          <div style="font-size: 12px; margin-top: 4px; color: #999;">请检查页面权限或网络连接</div>
        </div>
      `}}calculateScreenshotArea(){let e={height:window.innerHeight,width:window.innerWidth,x:0,y:0,scrollX:0,scrollY:0};try{let t=this.lastSelectionRect;if(!t){let e=window.getSelection();e&&e.rangeCount>0&&(t=e.getRangeAt(0).getBoundingClientRect())}if(t&&t.width>0&&t.height>0){let n=window.pageXOffset||document.documentElement.scrollLeft,r=window.pageYOffset||document.documentElement.scrollTop,i=t.left+n,a=t.top+r,o=Math.min(800,window.innerWidth),s=Math.min(600,window.innerHeight),c=i-o/2,l=a-s/2,u=document.documentElement.scrollWidth,d=document.documentElement.scrollHeight;c=Math.max(0,Math.min(c,u-o)),l=Math.max(0,Math.min(l,d-s)),e={height:s,width:o,x:c,y:l,scrollX:0,scrollY:0},this.config.isDebug&&_.debug(`BytedeskWeb: 选中文本截图区域:`,{selectedRect:t,absolutePosition:{left:i,top:a},captureArea:{x:c,y:l,width:o,height:s},pageSize:{width:u,height:d}})}else if(this.lastMouseEvent){let t=window.pageXOffset||document.documentElement.scrollLeft,n=window.pageYOffset||document.documentElement.scrollTop,r=this.lastMouseEvent.clientX+t,i=this.lastMouseEvent.clientY+n,a=Math.min(800,window.innerWidth),o=Math.min(600,window.innerHeight),s=r-a/2,c=i-o/2,l=document.documentElement.scrollWidth,u=document.documentElement.scrollHeight;s=Math.max(0,Math.min(s,l-a)),c=Math.max(0,Math.min(c,u-o)),e={height:o,width:a,x:s,y:c,scrollX:0,scrollY:0},this.config.isDebug&&_.debug(`BytedeskWeb: 鼠标位置截图区域:`,{mousePosition:{x:this.lastMouseEvent.clientX,y:this.lastMouseEvent.clientY},absolutePosition:{x:r,y:i},captureArea:{x:s,y:c,width:a,height:o}})}}catch(e){this.config.isDebug&&_.warn(`BytedeskWeb: 计算截图区域失败，使用默认区域:`,e)}return e}async loadHtml2Canvas(){try{return window.html2canvas?window.html2canvas:await this.loadHtml2CanvasFromCDN()}catch(e){return this.config.isDebug&&_.warn(`html2canvas 加载失败:`,e),null}}async loadHtml2CanvasFromCDN(){return new Promise((e,t)=>{if(window.html2canvas){e(window.html2canvas);return}let n=document.createElement(`script`);n.src=this.config.apiUrl+`/assets/js/html2canvas.min.js`,n.onload=()=>{window.html2canvas?e(window.html2canvas):t(Error(`html2canvas 加载失败`))},n.onerror=()=>{t(Error(`无法从CDN加载html2canvas`))},document.head.appendChild(n)})}async submitFeedback(){let e=this.feedbackDialog?.querySelector(`#bytedesk-feedback-text`),t=e?.value.trim()||``;if(!t){alert(`请填写反馈内容`),e?.focus();return}let n=[],r=this.feedbackDialog?.querySelectorAll(`input[name="feedback-type"]:checked`);if(r&&r.forEach(e=>{n.push(e.value)}),this.config.feedbackConfig?.requiredTypes&&n.length===0){alert(`请至少选择一个问题类型`);return}let i=this.feedbackDialog?.querySelector(`.bytedesk-feedback-submit`),a=i?.textContent||`提交反馈`;i&&(i.disabled=!0,i.textContent=`提交中...`,i.style.opacity=`0.6`);try{let e=this.feedbackDialog?.querySelector(`#bytedesk-submit-screenshot`)?.checked!==!1,r=[];if(e){this.config.isDebug&&_.debug(`BytedeskWeb: 开始生成和上传截图`),i&&(i.textContent=`正在生成截图...`);let e=await this.generateAndUploadScreenshot();e&&(r.push(e),this.config.isDebug&&_.debug(`BytedeskWeb: 截图上传成功:`,e)),i&&(i.textContent=`正在提交反馈...`)}let a={selectedText:this.selectedText,...r.length>0&&{images:r},content:t,url:window.location.href,title:document.title,userAgent:navigator.userAgent,visitorUid:localStorage.getItem(`bytedesk_uid`)||``,orgUid:this.config.chatConfig?.org||``,...n.length>0&&{categoryNames:n.join(`,`)}};this.config.feedbackConfig?.onSubmit?this.config.feedbackConfig.onSubmit(a):await this.submitFeedbackToServer(a),this.showFeedbackSuccess(),setTimeout(()=>{this.hideFeedbackDialog()},2e3)}catch(e){_.error(`提交反馈失败:`,e),alert(`提交失败，请稍后重试`)}finally{i&&(i.disabled=!1,i.textContent=a,i.style.opacity=`1`)}}async submitFeedbackToServer(e){try{let{submitFeedback:t}=await n(async()=>{let{submitFeedback:e}=await import(`./feedback-DRVM6--S.js`);return{submitFeedback:e}},__vite__mapDeps([9,0,1,2,3,4,5])),r=await t(e);return this.config.isDebug&&_.debug(`反馈提交响应:`,r),r}catch(e){throw _.error(`提交反馈到服务器失败:`,e),e}}showFeedbackSuccess(){if(!this.feedbackDialog)return;let e=this.feedbackDialog.querySelector(`div > div`);e&&(e.innerHTML=`
      <div style="text-align: center; padding: 40px 20px;">
        <div style="font-size: 48px; margin-bottom: 16px;">✅</div>
        <h3 style="margin: 0 0 12px 0; color: #28a745;">
          ${this.config.feedbackConfig?.successMessage||`反馈已提交，感谢您的意见！`}
        </h3>
        <div style="color: #666; font-size: 14px;">
          我们会认真处理您的反馈，不断改进产品体验
        </div>
      </div>
    `)}showDocumentFeedback(e){if(!this.config.feedbackConfig?.enabled){_.warn(`文档反馈功能未启用`);return}e&&(this.selectedText=e),this.showFeedbackDialog()}reinitFeedbackFeature(){this.config.isDebug&&_.debug(`BytedeskWeb: 重新初始化反馈功能`),this.destroyFeedbackFeature(),this.initFeedbackFeature()}forceInitFeedbackFeature(){return _.debug(`BytedeskWeb: 强制初始化反馈功能被调用`),_.debug(`BytedeskWeb: 当前配置:`,this.config.feedbackConfig),_.debug(`BytedeskWeb: isDebug:`,this.config.isDebug),this.config.feedbackConfig||(_.debug(`BytedeskWeb: 创建默认反馈配置`),this.config.feedbackConfig={enabled:!0,trigger:`selection`,showOnSelection:!0,selectionText:`📝 文档反馈`,dialogTitle:`提交意见反馈`,placeholder:`请详细描述您发现的问题、改进建议或其他意见...`,submitText:`提交反馈`,cancelText:`取消`,successMessage:`感谢您的反馈！我们会认真处理您的意见。`}),this.config.feedbackConfig.enabled||(_.debug(`BytedeskWeb: 启用反馈配置`),this.config.feedbackConfig.enabled=!0),_.debug(`BytedeskWeb: 销毁现有反馈功能`),this.destroyFeedbackFeature(),_.debug(`BytedeskWeb: 重新初始化反馈功能`),this.initFeedbackFeature(),_.debug(`BytedeskWeb: 强制初始化完成，检查结果:`),_.debug(`- showDocumentFeedback方法存在:`,typeof this.showDocumentFeedback==`function`),_.debug(`- testTextSelection方法存在:`,typeof this.testTextSelection==`function`),_.debug(`- 反馈提示框存在:`,!!this.feedbackTooltip),_.debug(`- 反馈对话框存在:`,!!this.feedbackDialog),_.debug(`- 反馈提示框DOM存在:`,!!document.querySelector(`[data-bytedesk-feedback="tooltip"]`)),_.debug(`- 反馈对话框DOM存在:`,!!document.querySelector(`[data-bytedesk-feedback="dialog"]`)),{success:!!(this.feedbackTooltip&&this.feedbackDialog),methods:{showDocumentFeedback:typeof this.showDocumentFeedback==`function`,testTextSelection:typeof this.testTextSelection==`function`},elements:{tooltip:!!this.feedbackTooltip,dialog:!!this.feedbackDialog,tooltipDOM:!!document.querySelector(`[data-bytedesk-feedback="tooltip"]`),dialogDOM:!!document.querySelector(`[data-bytedesk-feedback="dialog"]`)}}}testTextSelection(e=`测试选中文字`){this.config.isDebug&&_.debug(`BytedeskWeb: 测试文本选择功能，模拟选中文字:`,`"${e}"`),this.selectedText=e;try{let t=document.createElement(`div`);t.textContent=e,t.style.cssText=`
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        padding: 20px;
        background: #f0f0f0;
        border: 2px dashed #ccc;
        border-radius: 8px;
        font-size: 16px;
        z-index: 1000;
        pointer-events: none;
      `,document.body.appendChild(t);let n=document.createRange();n.selectNodeContents(t);let r=window.getSelection();r&&(r.removeAllRanges(),r.addRange(n),this.config.isDebug&&_.debug(`BytedeskWeb: 已创建模拟文本选择`),this.feedbackTooltip?this.showFeedbackTooltip():_.error(`BytedeskWeb: 反馈提示框不存在，无法测试`),setTimeout(()=>{r&&r.removeAllRanges(),document.body.contains(t)&&document.body.removeChild(t),this.hideFeedbackTooltip()},5e3))}catch(e){_.error(`BytedeskWeb: 创建测试选择失败:`,e)}}getDebugInfo(){return{config:this.config,feedbackConfig:this.config.feedbackConfig,feedbackTooltip:!!this.feedbackTooltip,feedbackDialog:!!this.feedbackDialog,selectedText:this.selectedText,methods:{showDocumentFeedback:typeof this.showDocumentFeedback,testTextSelection:typeof this.testTextSelection,forceInitFeedbackFeature:typeof this.forceInitFeedbackFeature}}}destroyFeedbackFeature(){this.feedbackTooltip&&=(this.feedbackTooltip.remove(),null),this.feedbackDialog&&=(this.feedbackDialog.remove(),null),this.selectedText=``}},E={"zh-cn":{title:`在线客服`,bubble:{title:`需要帮助吗？`,subtitle:`点击开始对话`},tabs:{home:`首页`,messages:`消息`,help:`帮助`,news:`新闻`},settings:{position:`位置`,tabs:`标签页`,bubble:`气泡`,navbar:`导航栏`,theme:`主题`,window:`窗口`,margins:`边距`,animation:`动画`,other:`其他`,embed:`嵌入代码`}},"zh-tw":{title:`線上客服`,bubble:{title:`需要幫助嗎？`,subtitle:`點擊開始對話`},tabs:{home:`首頁`,messages:`消息`,help:`幫助`,news:`新聞`},settings:{position:`位置`,tabs:`標籤頁`,bubble:`氣泡`,navbar:`導航欄`,theme:`主題`,window:`窗口`,margins:`邊距`,animation:`動畫`,other:`其他`,embed:`嵌入代碼`}},en:{title:`Online Support`,bubble:{title:`Need help?`,subtitle:`Click to start chat`},tabs:{home:`Home`,messages:`Messages`,help:`Help`,news:`News`},settings:{position:`Position`,tabs:`Tabs`,bubble:`Bubble`,navbar:`Navbar`,theme:`Theme`,window:`Window`,margins:`Margins`,animation:`Animation`,other:`Other`,embed:`Embed Code`}},ja:{title:`オンラインサポート`,bubble:{title:`お困りですか？`,subtitle:`クリックして会話を開始`},tabs:{home:`ホーム`,messages:`メッセージ`,help:`ヘルプ`,news:`ニュース`},settings:{position:`位置`,tabs:`タブ`,bubble:`バブル`,navbar:`ナビゲーション`,theme:`テーマ`,window:`ウィンドウ`,margins:`余白`,animation:`アニメーション`,other:`その他`,embed:`埋め込みコード`}},"ja-jp":{title:`オンラインサポート`,bubble:{title:`お困りですか？`,subtitle:`クリックして会話を開始`},tabs:{home:`ホーム`,messages:`メッセージ`,help:`ヘルプ`,news:`ニュース`},settings:{position:`位置`,tabs:`タブ`,bubble:`バブル`,navbar:`ナビゲーション`,theme:`テーマ`,window:`ウィンドウ`,margins:`余白`,animation:`アニメーション`,other:`その他`,embed:`埋め込みコード`}}},D=r(),O=e=>Array.isArray(e)?e.map(O):typeof e==`function`?e.toString():e&&typeof e==`object`?Object.keys(e).sort().reduce((t,n)=>(t[n]=O(e[n]),t),{}):e,k=e=>JSON.stringify(O(e)),A=({locale:e=`zh-cn`,...t})=>(0,D.jsx)(i,{messages:E[e],locale:e,defaultLocale:`zh-cn`,children:(0,D.jsx)(N,{...t,locale:e})}),j=null,M=0,N=e=>{let t=(0,x.useRef)(null),n=(0,x.useRef)(!1),{onInit:r,...i}=e;return(0,x.useEffect)(()=>(M++,j?(t.current=j,window.bytedesk=j,j.setConfig(i,{replaceChatConfig:!0}),n.current||(n.current=!0,r?.())):(j=new T(i),t.current=j,window.bytedesk=j,j.init().then(()=>{n.current=!0,r?.()}).catch(e=>{console.error(`BytedeskWeb 初始化失败:`,e),n.current=!0,r?.()})),()=>{M--,t.current=null,M<=0&&setTimeout(()=>{j&&M<=0&&(j.destroy(),j=null,delete window.bytedesk,M=0)},100)}),[k(i)]),null};export{A as t};