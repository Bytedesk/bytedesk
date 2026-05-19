const __vite__mapDeps=(i,m=__vite__mapDeps,d=(m.f||(m.f=["assets/threadService-DVuq7cxu.js","assets/configUtils-BfyPDHAS.js","assets/rolldown-runtime-Ps8w5BGs.js","assets/vendor-Uoi4qs9k.js","assets/react-vendor-BlrekbrI.js","assets/antdx-vendor-RNrjs4ZW.js","assets/emoji-ui-vendor-CG5XS0aa.js","assets/pro-components-vendor-BfjPdQ0l.js","assets/intl-vendor-CR6WtLaI.js","assets/utils-vendor-8kdxj8ck.js","assets/thread-D2H5kXGO.js","assets/organization-BLKWvGob.js"])))=>i.map(i=>d[i]);
import{a as e}from"./rolldown-runtime-Ps8w5BGs.js";import{mn as t}from"./antdx-vendor-RNrjs4ZW.js";import{K as n,ct as r,f as i,m as a,y as o}from"./pro-components-vendor-BfjPdQ0l.js";import{Ct as s,E as c,F as l,I as u,S as d,Tt as f,ct as p,dt as m,ft as h,i as g,lt as _,ot as v,st as y,t as b,ut as x,x as S}from"./vendor-Uoi4qs9k.js";import{i as C,n as w,r as T}from"./intl-vendor-CR6WtLaI.js";import{Q as E,Tr as D,gr as O,gt as k,p as A,r as j,u as M,yt as N}from"./configUtils-BfyPDHAS.js";import{t as P}from"./AppContext-BexBFfte.js";import{P as F}from"./utils-B4V5M22a.js";import{S as I,b as L,v as R,x as z,y as B}from"./react-vendor-BlrekbrI.js";import{t as V}from"./organization-BLKWvGob.js";import{t as H}from"./auth-BRNJxxaB.js";import{r as U}from"./auth-BGy3vi3d.js";import{d as W,s as G}from"./index-CNvY4t78.js";import{t as K}from"./Login-CvT0Lmsh.js";var q=e(t(),1),J=b(({token:e,css:t})=>({app:t`
      background: ${e.colorBgLayout};
      min-height: 100vh;
      width: 100vw;
      display: flex;
      align-items: stretch;
      justify-content: stretch;
      padding: 0;
      margin: 0;
      overflow: hidden;
    `,layout:t`
      width: 100%;
      min-width: 1000px;
      height: 100vh;
      border-radius: 0;
      display: flex;
      background: ${e.colorBgContainer};
      font-family: AlibabaPuHuiTi, ${e.fontFamily}, sans-serif;
      box-shadow: none;

      .ant-prompts {
        color: ${e.colorText};
      }
    `,menu:t`
      background: ${e.colorBgLayout}80;
      width: 280px;
      height: 100%;
      display: flex;
      flex-direction: column;
      border-right: 1px solid ${e.colorBorderSecondary};
      margin-top: 20px;
    `,conversations:t`
      padding: 0 12px;
      flex: 1;
      overflow-y: auto;
    `,chat:t`
      height: 100%;
      width: 100%;
      // max-width: 700px;
      margin: 0 auto;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      padding: ${e.paddingLG}px;
      gap: 16px;
    `,messages:t`
      flex: 1;
    `,placeholder:t`
      padding-top: 32px;
    `,sender:t`
      box-shadow: ${e.boxShadow};
    `,logo:t`
      display: flex;
      height: 72px;
      align-items: center;
      justify-content: start;
      padding: 0 24px;
      box-sizing: border-box;

      img {
        width: 24px;
        height: 24px;
        display: inline-block;
      }

      span {
        display: inline-block;
        margin: 0 8px;
        font-weight: bold;
        color: ${e.colorText};
        font-size: 16px;
      }
    `,addBtn:t`
      background: ${e.colorPrimary}0f;
      border: 1px solid ${e.colorPrimary}34;
      width: calc(100% - 24px);
      margin: 0 12px 24px 12px;
      color: ${e.colorText};
    `,appWithNav:t`
      height: 100vh;
      display: flex;
      flex-direction: column;
    `,header:t`
      display: flex;
      align-items: center;
      padding: 0 24px;
      height: 64px;
      line-height: 64px;
    `,navMenu:t`
      border-bottom: none;
    `,desktopNav:t`
      flex: 1;
      display: flex;

      @media (max-width: 768px) {
        display: none;
      }
    `,mobileNav:t`
      display: none;

      @media (max-width: 768px) {
        display: flex;
        margin-left: auto;
      }
    `,tools:t`
      display: flex;
      align-items: center;
      gap: 8px;
    `})),Y=C(),X=({title:e=`微语`,logoUrl:t=`https://cdn.weiyuai.cn/logo.png`})=>{let{styles:n}=J();return(0,Y.jsxs)(`div`,{className:n.logo,onClick:()=>window.open(`/chat/home`),children:[(0,Y.jsx)(`img`,{src:t,draggable:!1,alt:`logo`}),(0,Y.jsx)(`span`,{children:e})]})},Z=({activeKey:e,onMenuClick:t})=>{let{styles:n}=J(),r=T(),[i,a]=(0,q.useState)([`afterSale`,`preSale`,`serviceConfig`,`suggest`]);return(0,Y.jsx)(`div`,{className:n.menu,style:{margin:0,padding:0,width:`100%`,minWidth:0,height:`100%`,display:`flex`,flexDirection:`column`,overflow:`hidden`},children:(0,Y.jsx)(s,{mode:`inline`,selectedKeys:[e],openKeys:i,onOpenChange:e=>{a(e)},onClick:({key:e})=>t(e),style:{borderRight:0,width:`100%`,minWidth:0,flex:1,minHeight:0,overflowY:`auto`,WebkitOverflowScrolling:`touch`},items:[{key:`myTicket`,label:r.formatMessage({id:`sidebar.afterSale.myTicket`,defaultMessage:`我的工单`}),icon:(0,Y.jsx)(m,{})}]})})},Q=I()(L(z(B((e,t)=>({threads:[],queuingThreads:[],invitedThreads:[],monitoringThreads:[],currentThread:{uid:``,user:{uid:``,nickname:``,avatar:``},topic:``,content:``,type:``,unreadCount:0,extra:``,updatedAt:``},currentQueuingThread:{uid:``,user:{uid:``,nickname:``,avatar:``}},currentTicketThread:{uid:``,user:{uid:``,nickname:``,avatar:``},topic:``,content:``,type:``,unreadCount:0,extra:``,updatedAt:``},threadResult:{data:{content:[],last:!0}},showQueueButton:!1,showQueueList:!1,showRightPanel:!1,loading:!1,error:null,searchText:``,pagination:{pageNumber:0,pageSize:100,total:0},filters:{},addThread(n){if(!t().threads.some(e=>e.uid===n.uid))return n.unreadCount=1,e({threads:[n,...t().threads]}),n.unreadCount;if(t().currentThread?.uid===``||t().currentThread?.uid!==n.uid){for(let e=0;e<t().threads.length;e++){let r=t().threads[e];r.uid===n.uid&&(n.unreadCount=r.unreadCount+1,n.top=r.top,n.mute=r.mute,n.unread=r.unread,n.agent=r.agent)}return e({threads:[n,...t().threads.filter(e=>e.uid!==n.uid)]}),n.unreadCount}else return e({threads:t().threads.map(e=>e.uid===n.uid?(n.top=e.top,n.mute=e.mute,n.unread=e.unread,n.agent=e.agent,n):e)}),0},addThreadWithMessage(n,r){let i=`topic`,a=t().threads.some(e=>e[i]===n[i]),o=t().currentThread[i]===n[i];return a?o?(e({threads:t().threads.map(e=>e[i]===n[i]?$(n,e):e)}),0):(e({threads:[n,...t().threads.filter(e=>e[i]!==n[i])]}),n.unreadCount):(e({threads:[n,...t().threads]}),n.unreadCount)},addQueuingThread(n){t().queuingThreads.some(e=>e.uid===n.uid)||e({queuingThreads:[n,...t().queuingThreads]})},updateThreadContent(n,r){let i=null;return e({threads:t().threads.map(e=>e.uid===n?(i={...e,unreadCount:e.unreadCount+1,content:r},i):e)}),i},updateThreadStatus(n,r){let i=null;return e({threads:t().threads.map(e=>e.uid===n?(i={...e,status:r},i):e)}),i},removeThread(n){e({threads:[...t().threads.filter(e=>e?.uid!==n?.uid)]})},removeThreadWithUid(n){e({threads:[...t().threads.filter(e=>e?.uid!==n)]})},closeThread(n){e({threads:t().threads.map(e=>e)})},addThreads(n){for(let r=0;r<n.length;r++){let i=n[r];t().threads.some(e=>e.uid===i.uid)?e({threads:t().threads.map(e=>e.uid===i.uid?{...i,unreadCount:e.unreadCount}:e)}):e({threads:[...t().threads,i]})}},setThreads(t){e(e=>{e.threads=t})},setQueuingThreads(t){e(e=>{e.queuingThreads=t})},setInvitedThreads(t){e(e=>{e.invitedThreads=t})},setMonitoringThreads(t){e(e=>{e.monitoringThreads=t})},setCurrentThread(n){e(e=>{e.showQueueList=!1});let r={...n,unreadCount:0},i=t().threads.map(e=>e.uid===r.uid?r:e);e(e=>{e.currentThread=r,e.threads=i})},setCurrentQueuingThread(t){e(e=>{e.currentQueuingThread=t})},setCurrentTicketThread(t){e(e=>{e.currentTicketThread=t})},setThreadResult(t){e(e=>{e.threadResult=t})},getUnreadCount(){return t().threads.reduce((e,n)=>n.unreadCount>0&&n.uid!==t().currentThread?.uid?e+n.unreadCount:e,0)},setShowQueueButton(t){e(e=>{e.showQueueButton=t})},setShowQueueList(t){e(e=>{e.showQueueList=t,e.showRightPanel=!1})},setShowRightPanel(t){e(e=>{e.showRightPanel=t})},resetThreads(){e(e=>{e.threads=[],e.queuingThreads=[],e.currentThread={uid:``,user:{uid:``,nickname:``,avatar:``},topic:``,content:``,type:``,unreadCount:0,extra:``,updatedAt:``},e.currentQueuingThread={uid:``,user:{uid:``,nickname:``,avatar:``}},e.threadResult={data:{content:[],last:!0}},e.showQueueButton=!1,e.showQueueList=!1,e.showRightPanel=!1})},setLoading:t=>e({loading:t}),setError:t=>e({error:t}),setSearchText:t=>e({searchText:t}),setFilter:(t,n)=>e(e=>{e.filters[t]=n}),clearFilters:()=>e({filters:{}}),refreshThreads:async()=>{let{currentOrg:e}=V.getState();if(e?.uid){let{threadService:e}=await R(async()=>{let{threadService:e}=await import(`./threadService-DVuq7cxu.js`);return{threadService:e}},__vite__mapDeps([0,1,2,3,4,5,6,7,8,9,10,11]));await e.loadThreads()}},setPagination:t=>e({pagination:t})})),{name:D}))),$=(e,t)=>(e.top=t.top,e.mute=t.mute,e.unread=t.unread,e.agent=t.agent,e);async function ee(e){return A(`/api/v1/robot/query/org`,{method:`GET`,params:{...e,channel:E}})}async function te(e){return A(`/api/v1/robot/create/llm/thread`,{method:`POST`,data:{...e}})}var ne=({open:e,onSubmit:t,onCancel:s})=>{let c=(0,q.useRef)(!1),l=T(),{translateString:u}=W(),d=(e,t)=>e?u(e)||e:t,{isLoggedIn:f}=(0,q.useContext)(P),[m,h]=(0,q.useState)(0),[g]=(0,q.useState)(5),[b,x]=(0,q.useState)(0),[S,C]=(0,q.useState)([]),[w,D]=(0,q.useState)(!1),[A,j]=(0,q.useState)(``),M=V(e=>e.currentOrg),N=Q(e=>e.addThread),F=Q(e=>e.setCurrentThread),I=(0,q.useCallback)(async(e=m)=>{if(!f)return;if(c.current){console.log(`isLoading: 1`,c.current);return}c.current=!0,D(!0);let t=y.loading({content:`loading`,duration:0}),n={pageNumber:e,pageSize:g,name:O,nickname:A,orgUid:M?.uid,categoryUid:``,type:`LLM`,level:k};try{let r=await ee(n);console.log(`queryRobotsByOrg: `,r?.data,n),r?.data.code===200?(t(),C(r?.data.data.content),x(r?.data.data.totalElements),h(e)):(t(),y.error(d(r?.data?.message,l.formatMessage({id:`chat.error.request.failed`,defaultMessage:`Request failed, please try again later`}))))}catch{t(),y.error(l.formatMessage({id:`chat.error.request.failed`,defaultMessage:`Request failed, please try again later`}))}finally{c.current=!1,D(!1)}},[M?.uid,f,m,g,A]);(0,q.useEffect)(()=>{e&&I(0)},[I,e,A]);let L=e=>{j(e)},R=e=>{I(e-1)},z=()=>{s()},B=async e=>{console.log(`startRobotChat`,e);let n={uid:e?.name},r={robot:JSON.stringify(n),forceNew:!0,hide:!1,channel:E},i=await te(r);console.log(`startRobotChat response:`,r,i?.data),i?.data.code===200?(N(i?.data.data),F(i?.data.data),t()):(y.error(d(i?.data?.message,l.formatMessage({id:`chat.error.request.failed`,defaultMessage:`Request failed, please try again later`}))),s())};return(0,Y.jsx)(Y.Fragment,{children:(0,Y.jsxs)(v,{title:l.formatMessage({id:`robot.create.title`,defaultMessage:`创建大模型对话`}),open:e,onCancel:z,footer:[(0,Y.jsx)(n,{onClick:z,children:l.formatMessage({id:`common.cancel`,defaultMessage:`取消`})},`cancel`)],children:[(0,Y.jsx)(a,{placeholder:l.formatMessage({id:`robot.search.placeholder`,defaultMessage:`搜索提示语昵称`}),prefix:(0,Y.jsx)(r,{}),value:A,onChange:e=>L(e.target.value),style:{marginBottom:16,marginTop:8},allowClear:!0}),S.length===0&&A&&!w&&(0,Y.jsx)(`div`,{style:{textAlign:`center`,padding:`20px 0`},children:l.formatMessage({id:`common.noSearchResults`,defaultMessage:`没有找到匹配的提示语`})}),(0,Y.jsx)(`div`,{style:{height:250,overflowY:`auto`},children:(0,Y.jsx)(p,{dataSource:S,style:{marginTop:10},renderItem:e=>(0,Y.jsx)(p.Item,{actions:[(0,Y.jsx)(n,{onClick:()=>B(e),children:l.formatMessage({id:`pages.robot.chat`,defaultMessage:`Chat`})})],children:(0,Y.jsx)(p.Item.Meta,{style:{marginLeft:`10px`},title:u(e?.nickname),description:u(e?.description)})},e?.uid)})}),!w&&S.length>0&&(0,Y.jsx)(`div`,{style:{textAlign:`center`,marginTop:16},children:(0,Y.jsx)(_,{current:m+1,pageSize:g,total:b,onChange:R,size:`small`,simple:!0})}),w&&(0,Y.jsx)(`div`,{style:{textAlign:`center`,marginTop:20},children:(0,Y.jsxs)(o,{children:[(0,Y.jsx)(i,{}),(0,Y.jsx)(`span`,{children:l.formatMessage({id:`common.loading`,defaultMessage:`加载中...`})})]})})]})})},re=()=>{let e=T(),[t,r]=(0,q.useState)(!1),[i,a]=(0,q.useState)(!1),{isLoggedIn:o,userInfo:s}=(0,q.useContext)(P),{doLogout:c}=G();return o&&s?(0,Y.jsxs)(Y.Fragment,{children:[(0,Y.jsx)(n,{type:`text`,icon:(0,Y.jsx)(g,{}),onClick:()=>{a(!0)},children:s.nickname}),(0,Y.jsx)(v,{title:e.formatMessage({id:`logout.modal.title`,defaultMessage:`退出登录`}),open:i,onOk:()=>{c(),a(!1)},onCancel:()=>{a(!1)},okText:e.formatMessage({id:`logout.confirm`,defaultMessage:`确认退出`}),cancelText:e.formatMessage({id:`logout.cancel`,defaultMessage:`取消`}),children:(0,Y.jsx)(`p`,{children:e.formatMessage({id:`logout.confirmation`,defaultMessage:`确认要退出登录吗？`})})})]}):(0,Y.jsxs)(Y.Fragment,{children:[(0,Y.jsx)(n,{type:`primary`,icon:(0,Y.jsx)(g,{}),onClick:()=>{r(!0)},children:(0,Y.jsx)(w,{id:`login.button`,defaultMessage:`登录`})}),(0,Y.jsx)(v,{title:e.formatMessage({id:`login.modal.title`,defaultMessage:`用户登录`}),open:t,onCancel:()=>{r(!1)},footer:null,width:500,destroyOnHidden:!0,mask:{closable:!1},style:{top:20},children:(0,Y.jsx)(K,{isModel:!0})})]})},{Header:ie}=x,ae=({isDarkMode:e,toggleDarkMode:t,language:r,changeLanguage:i,isMobile:a,onToggleSidebar:o,rightExtra:p})=>{let m=T(),[_,v]=(0,q.useState)(N),[b,x]=(0,q.useState)(``),[C,w]=(0,q.useState)(``),{userInfo:E}=(0,q.useContext)(P),{accessToken:D,removeAccessToken:O}=H(),k=F(e=>e.resetUserInfo),A=!!(D&&D.trim().length>0),I=[{key:`zh-cn`,label:m.formatMessage({id:`language.zh`})},{key:`en`,label:m.formatMessage({id:`language.en`})},{key:`zh-tw`,label:m.formatMessage({id:`language.zh-TW`})},{key:`vi-vn`,label:m.formatMessage({id:`language.vi-VN`})},{key:`ms-my`,label:m.formatMessage({id:`language.ms-MY`})},{key:`ko-kr`,label:m.formatMessage({id:`language.ko-KR`,defaultMessage:`한국어 (대한민국)`})},{key:`es-es`,label:m.formatMessage({id:`language.es-ES`,defaultMessage:`Español (España)`})},{key:`fr-fr`,label:m.formatMessage({id:`language.fr-FR`,defaultMessage:`Français (France)`})},{key:`th-th`,label:m.formatMessage({id:`language.th-TH`,defaultMessage:`ภาษาไทย (ประเทศไทย)`})}],L=[{key:`logout`,label:m.formatMessage({id:`user.logout`,defaultMessage:`退出登录`}),icon:(0,Y.jsx)(d,{}),onClick:async()=>{try{await U(),O(),k(),y.success(m.formatMessage({id:`user.logout.success`,defaultMessage:`退出成功`})),window.location.reload()}catch{y.error(m.formatMessage({id:`user.logout.failed`,defaultMessage:`退出失败`}))}}}],R=[],z=e=>{w(e.key)},B=({key:e})=>{i(e)},V=q.useCallback(async()=>{let e=await j();e?.custom?.enabled&&M()?(e?.custom?.logo?v(e?.custom?.logo):v(N),e?.custom?.name?x(e?.custom?.name):x(m.formatMessage({id:`app.helpcenter.title`}))):(v(N),x(m.formatMessage({id:`app.helpcenter.title`})))},[m,v,x]);(0,q.useEffect)(()=>{V()},[V]);let W={headerStyle:{background:e?`#141414`:`#fff`,borderBottom:`1px solid ${e?`#303030`:`#f0f0f0`}`,boxShadow:`0 2px 8px rgba(0, 0, 0, 0.06)`,height:`64px`,position:`relative`,zIndex:1e3,display:`flex`,justifyContent:`space-between`,alignItems:`center`},logoStyle:{fontSize:`18px`,fontWeight:`bold`,cursor:`pointer`,marginRight:24,display:`flex`,alignItems:`center`,gap:`4px`},mainMenuStyle:{background:`transparent`,border:`none`,height:`64px`,minWidth:`350px`},rightToolsStyle:{display:`flex`,alignItems:`center`,gap:a?`4px`:`8px`},mobileButtonStyle:{padding:`0 4px`},sidebarToggle:{display:a?`inline-flex`:`none`,marginRight:`8px`}};return(0,Y.jsxs)(ie,{className:`header`,style:W.headerStyle,children:[(0,Y.jsxs)(`div`,{style:{display:`flex`,alignItems:`center`,width:`100%`},children:[(0,Y.jsx)(n,{type:`text`,icon:(0,Y.jsx)(S,{}),style:W.sidebarToggle,onClick:o}),(0,Y.jsx)(`div`,{className:`logo`,style:W.logoStyle,children:(0,Y.jsx)(X,{title:b,logoUrl:_})}),!a&&(0,Y.jsx)(s,{mode:`horizontal`,items:R,selectedKeys:[C],onClick:z,style:W.mainMenuStyle,triggerSubMenuAction:`hover`,disabledOverflow:!0})]}),(0,Y.jsxs)(`div`,{style:W.rightToolsStyle,children:[p,a?(0,Y.jsx)(Y.Fragment,{}):(0,Y.jsxs)(Y.Fragment,{children:[(0,Y.jsx)(h,{menu:{items:I,onClick:B,selectedKeys:[r]},placement:`bottomRight`,children:(0,Y.jsx)(n,{type:`text`,icon:(0,Y.jsx)(c,{})})}),(0,Y.jsx)(n,{type:`text`,icon:e?(0,Y.jsx)(u,{}):(0,Y.jsx)(l,{}),onClick:t}),A?(0,Y.jsx)(h,{menu:{items:L},placement:`bottomRight`,children:(0,Y.jsxs)(`div`,{style:{cursor:`pointer`,display:`flex`,alignItems:`center`,gap:`8px`},children:[(0,Y.jsx)(f,{icon:(0,Y.jsx)(g,{})}),(0,Y.jsx)(`span`,{children:E?.username||E?.nickname||m.formatMessage({id:`user.anonymous`,defaultMessage:`用户`})})]})}):(0,Y.jsx)(re,{})]})]})]})};export{J as a,Z as i,ne as n,Q as r,ae as t};