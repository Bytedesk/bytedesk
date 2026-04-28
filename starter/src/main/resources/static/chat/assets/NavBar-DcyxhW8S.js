import{a as e}from"./rolldown-runtime-BJ9pB_0e.js";import{G as t,ct as n,d as r,p as i,v as a}from"./pro-components-vendor-Cpy3Rv0v.js";import{$t as o}from"./antdx-vendor-Dx2lixsX.js";import{C as s,Ct as c,D as l,Et as u,I as d,L as f,S as p,ct as m,dt as h,ft as g,i as _,lt as v,ot as y,st as b,t as x,ut as S}from"./vendor-DtoG-tLI.js";import{i as C,n as w,r as T}from"./intl-vendor-8dDWo8Zj.js";import{Q as E,fr as D,gt as O,p as k,r as A,u as j,yt as M}from"./configUtils-CPmlildI.js";import{t as N}from"./AppContext-CtmhCfIh.js";import{I as P}from"./utils-C2tNl7Iu.js";import{t as F}from"./organization-Cg0xNcUA.js";import{t as I}from"./auth-0mSiM0BA.js";import{t as L}from"./useTranslate-RcLUkNKe.js";import{r as R}from"./auth-DIXZ1s8p.js";import{t as z}from"./useEventBus-D3bHsgWy.js";import{t as B}from"./Login-LcLCjZ7k.js";import{t as V}from"./thread-DyzrDXYQ.js";var H=e(o(),1),U=x(({token:e,css:t})=>({app:t`
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
    `})),W=C(),G=({title:e=`微语`,logoUrl:t=`https://cdn.weiyuai.cn/logo.png`})=>{let{styles:n}=U();return(0,W.jsxs)(`div`,{className:n.logo,onClick:()=>window.open(`/chat/home`),children:[(0,W.jsx)(`img`,{src:t,draggable:!1,alt:`logo`}),(0,W.jsx)(`span`,{children:e})]})},K=({activeKey:e,onMenuClick:t})=>{let{styles:n}=U(),r=T(),[i,a]=(0,H.useState)([`afterSale`,`preSale`,`serviceConfig`,`suggest`]);return(0,W.jsx)(`div`,{className:n.menu,style:{margin:0,padding:0,width:`100%`,minWidth:0,height:`100%`,display:`flex`,flexDirection:`column`,overflow:`hidden`},children:(0,W.jsx)(c,{mode:`inline`,selectedKeys:[e],openKeys:i,onOpenChange:e=>{a(e)},onClick:({key:e})=>t(e),style:{borderRight:0,width:`100%`,minWidth:0,flex:1,minHeight:0,overflowY:`auto`,WebkitOverflowScrolling:`touch`},items:[{key:`myTicket`,label:r.formatMessage({id:`sidebar.afterSale.myTicket`,defaultMessage:`我的工单`}),icon:(0,W.jsx)(h,{})}]})})};async function q(e){return k(`/api/v1/robot/query/org`,{method:`GET`,params:{...e,channel:E}})}async function J(e){return k(`/api/v1/robot/create/llm/thread`,{method:`POST`,data:{...e}})}var Y=({open:e,onSubmit:o,onCancel:s})=>{let c=(0,H.useRef)(!1),l=T(),{translateString:u}=L(),{isLoggedIn:d}=(0,H.useContext)(N),[f,p]=(0,H.useState)(0),[h]=(0,H.useState)(5),[g,_]=(0,H.useState)(0),[x,S]=(0,H.useState)([]),[C,w]=(0,H.useState)(!1),[k,A]=(0,H.useState)(``),j=F(e=>e.currentOrg),M=V(e=>e.addThread),P=V(e=>e.setCurrentThread),I=(0,H.useCallback)(async(e=f)=>{if(!d)return;if(c.current){console.log(`isLoading: 1`,c.current);return}c.current=!0,w(!0);let t=b.loading({content:`loading`,duration:0}),n={pageNumber:e,pageSize:h,name:D,nickname:k,orgUid:j?.uid,categoryUid:``,type:`LLM`,level:O};try{let r=await q(n);console.log(`queryRobotsByOrg: `,r?.data,n),r?.data.code===200?(t(),S(r?.data.data.content),_(r?.data.data.totalElements),p(e)):(t(),b.error(r?.data.message))}catch{t(),b.error(l.formatMessage({id:`chat.error.request.failed`,defaultMessage:`Request failed, please try again later`}))}finally{c.current=!1,w(!1)}},[j?.uid,d,f,h,k]);(0,H.useEffect)(()=>{e&&I(0)},[I,e,k]);let R=e=>{A(e)},z=e=>{I(e-1)},B=()=>{s()},U=async e=>{console.log(`startRobotChat`,e);let t={uid:e?.name},n={robot:JSON.stringify(t),forceNew:!0,hide:!1,channel:E},r=await J(n);console.log(`startRobotChat response:`,n,r?.data),r?.data.code===200?(M(r?.data.data),P(r?.data.data),o()):(b.error(r?.data.message),s())};return(0,W.jsx)(W.Fragment,{children:(0,W.jsxs)(y,{title:l.formatMessage({id:`robot.create.title`,defaultMessage:`创建大模型对话`}),open:e,onCancel:B,footer:[(0,W.jsx)(t,{onClick:B,children:l.formatMessage({id:`common.cancel`,defaultMessage:`取消`})},`cancel`)],children:[(0,W.jsx)(i,{placeholder:l.formatMessage({id:`robot.search.placeholder`,defaultMessage:`搜索提示语昵称`}),prefix:(0,W.jsx)(n,{}),value:k,onChange:e=>R(e.target.value),style:{marginBottom:16,marginTop:8},allowClear:!0}),x.length===0&&k&&!C&&(0,W.jsx)(`div`,{style:{textAlign:`center`,padding:`20px 0`},children:l.formatMessage({id:`common.noSearchResults`,defaultMessage:`没有找到匹配的提示语`})}),(0,W.jsx)(`div`,{style:{height:250,overflowY:`auto`},children:(0,W.jsx)(m,{dataSource:x,style:{marginTop:10},renderItem:e=>(0,W.jsx)(m.Item,{actions:[(0,W.jsx)(t,{onClick:()=>U(e),children:l.formatMessage({id:`pages.robot.chat`,defaultMessage:`Chat`})})],children:(0,W.jsx)(m.Item.Meta,{style:{marginLeft:`10px`},title:u(e?.nickname),description:u(e?.description)})},e?.uid)})}),!C&&x.length>0&&(0,W.jsx)(`div`,{style:{textAlign:`center`,marginTop:16},children:(0,W.jsx)(v,{current:f+1,pageSize:h,total:g,onChange:z,size:`small`,simple:!0})}),C&&(0,W.jsx)(`div`,{style:{textAlign:`center`,marginTop:20},children:(0,W.jsxs)(a,{children:[(0,W.jsx)(r,{}),(0,W.jsx)(`span`,{children:l.formatMessage({id:`common.loading`,defaultMessage:`加载中...`})})]})})]})})},X=()=>{let e=T(),[n,r]=(0,H.useState)(!1),[i,a]=(0,H.useState)(!1),{isLoggedIn:o,userInfo:s}=(0,H.useContext)(N),{doLogout:c}=z();return o&&s?(0,W.jsxs)(W.Fragment,{children:[(0,W.jsx)(t,{type:`text`,icon:(0,W.jsx)(_,{}),onClick:()=>{a(!0)},children:s.nickname}),(0,W.jsx)(y,{title:e.formatMessage({id:`logout.modal.title`,defaultMessage:`退出登录`}),open:i,onOk:()=>{c(),a(!1)},onCancel:()=>{a(!1)},okText:e.formatMessage({id:`logout.confirm`,defaultMessage:`确认退出`}),cancelText:e.formatMessage({id:`logout.cancel`,defaultMessage:`取消`}),children:(0,W.jsx)(`p`,{children:e.formatMessage({id:`logout.confirmation`,defaultMessage:`确认要退出登录吗？`})})})]}):(0,W.jsxs)(W.Fragment,{children:[(0,W.jsx)(t,{type:`primary`,icon:(0,W.jsx)(_,{}),onClick:()=>{r(!0)},children:(0,W.jsx)(w,{id:`login.button`,defaultMessage:`登录`})}),(0,W.jsx)(y,{title:e.formatMessage({id:`login.modal.title`,defaultMessage:`用户登录`}),open:n,onCancel:()=>{r(!1)},footer:null,width:500,destroyOnHidden:!0,mask:{closable:!1},style:{top:20},children:(0,W.jsx)(B,{isModel:!0})})]})},{Header:Z}=S,Q=({isDarkMode:e,toggleDarkMode:n,language:r,changeLanguage:i,isMobile:a,onToggleSidebar:o,rightExtra:m})=>{let h=T(),[v,y]=(0,H.useState)(M),[x,S]=(0,H.useState)(``),[C,w]=(0,H.useState)(``),{userInfo:E}=(0,H.useContext)(N),{accessToken:D,removeAccessToken:O}=I(),k=P(e=>e.resetUserInfo),F=!!(D&&D.trim().length>0),L=[{key:`zh-cn`,label:h.formatMessage({id:`language.zh`})},{key:`en`,label:h.formatMessage({id:`language.en`})},{key:`zh-tw`,label:h.formatMessage({id:`language.zh-TW`})},{key:`vi-vn`,label:h.formatMessage({id:`language.vi-VN`})},{key:`ms-my`,label:h.formatMessage({id:`language.ms-MY`})},{key:`ko-kr`,label:h.formatMessage({id:`language.ko-KR`,defaultMessage:`한국어 (대한민국)`})},{key:`es-es`,label:h.formatMessage({id:`language.es-ES`,defaultMessage:`Español (España)`})},{key:`fr-fr`,label:h.formatMessage({id:`language.fr-FR`,defaultMessage:`Français (France)`})},{key:`th-th`,label:h.formatMessage({id:`language.th-TH`,defaultMessage:`ภาษาไทย (ประเทศไทย)`})}],z=[{key:`logout`,label:h.formatMessage({id:`user.logout`,defaultMessage:`退出登录`}),icon:(0,W.jsx)(s,{}),onClick:async()=>{try{await R(),O(),k(),b.success(h.formatMessage({id:`user.logout.success`,defaultMessage:`退出成功`})),window.location.reload()}catch{b.error(h.formatMessage({id:`user.logout.failed`,defaultMessage:`退出失败`}))}}}],B=[],V=e=>{w(e.key)},U=({key:e})=>{i(e)},K=H.useCallback(async()=>{let e=await A();e?.custom?.enabled&&j()?(e?.custom?.logo?y(e?.custom?.logo):y(M),e?.custom?.name?S(e?.custom?.name):S(h.formatMessage({id:`app.helpcenter.title`}))):(y(M),S(h.formatMessage({id:`app.helpcenter.title`})))},[h,y,S]);(0,H.useEffect)(()=>{K()},[K]);let q={headerStyle:{background:e?`#141414`:`#fff`,borderBottom:`1px solid ${e?`#303030`:`#f0f0f0`}`,boxShadow:`0 2px 8px rgba(0, 0, 0, 0.06)`,height:`64px`,position:`relative`,zIndex:1e3,display:`flex`,justifyContent:`space-between`,alignItems:`center`},logoStyle:{fontSize:`18px`,fontWeight:`bold`,cursor:`pointer`,marginRight:24,display:`flex`,alignItems:`center`,gap:`4px`},mainMenuStyle:{background:`transparent`,border:`none`,height:`64px`,minWidth:`350px`},rightToolsStyle:{display:`flex`,alignItems:`center`,gap:a?`4px`:`8px`},mobileButtonStyle:{padding:`0 4px`},sidebarToggle:{display:a?`inline-flex`:`none`,marginRight:`8px`}};return(0,W.jsxs)(Z,{className:`header`,style:q.headerStyle,children:[(0,W.jsxs)(`div`,{style:{display:`flex`,alignItems:`center`,width:`100%`},children:[(0,W.jsx)(t,{type:`text`,icon:(0,W.jsx)(p,{}),style:q.sidebarToggle,onClick:o}),(0,W.jsx)(`div`,{className:`logo`,style:q.logoStyle,children:(0,W.jsx)(G,{title:x,logoUrl:v})}),!a&&(0,W.jsx)(c,{mode:`horizontal`,items:B,selectedKeys:[C],onClick:V,style:q.mainMenuStyle,triggerSubMenuAction:`hover`,disabledOverflow:!0})]}),(0,W.jsxs)(`div`,{style:q.rightToolsStyle,children:[m,a?(0,W.jsx)(W.Fragment,{}):(0,W.jsxs)(W.Fragment,{children:[(0,W.jsx)(g,{menu:{items:L,onClick:U,selectedKeys:[r]},placement:`bottomRight`,children:(0,W.jsx)(t,{type:`text`,icon:(0,W.jsx)(l,{})})}),(0,W.jsx)(t,{type:`text`,icon:e?(0,W.jsx)(f,{}):(0,W.jsx)(d,{}),onClick:n}),F?(0,W.jsx)(g,{menu:{items:z},placement:`bottomRight`,children:(0,W.jsxs)(`div`,{style:{cursor:`pointer`,display:`flex`,alignItems:`center`,gap:`8px`},children:[(0,W.jsx)(u,{icon:(0,W.jsx)(_,{})}),(0,W.jsx)(`span`,{children:E?.username||E?.nickname||h.formatMessage({id:`user.anonymous`,defaultMessage:`用户`})})]})}):(0,W.jsx)(X,{})]})]})]})};export{U as i,Y as n,K as r,Q as t};