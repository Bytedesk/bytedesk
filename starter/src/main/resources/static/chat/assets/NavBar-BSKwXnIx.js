import{a as e}from"./rolldown-runtime-Ps8w5BGs.js";import{K as t,ct as n,f as r,m as i,y as a}from"./pro-components-vendor-cA1uCJ2r.js";import{tn as o}from"./antdx-vendor-XmAgoWAb.js";import{C as s,D as c,Et as l,I as u,L as d,S as f,a as p,ct as m,dt as h,ft as g,lt as _,n as v,pt as y,st as b,ut as x,wt as S}from"./vendor-Bt2c9iVd.js";import{i as C,n as w,r as T}from"./intl-vendor-DyDtXCMr.js";import{Q as E,gr as D,gt as O,p as k,r as A,u as j,yt as M}from"./configUtils-CPscH5T8.js";import{t as N}from"./AppContext-DaZLZtV4.js";import{P}from"./utils-DJD4x675.js";import{t as F}from"./organization-BJusrDoA.js";import{t as I}from"./auth-D3txfMgN.js";import{r as L}from"./auth-DL_ewhxM.js";import{s as R,u as z}from"./index-CT9xREOd.js";import{t as B}from"./Login-CrpSojtU.js";import{t as V}from"./thread-BKAllOxK.js";var H=e(o(),1),U=v(({token:e,css:t})=>({app:t`
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
    `})),W=C(),G=({title:e=`微语`,logoUrl:t=`https://cdn.weiyuai.cn/logo.png`})=>{let{styles:n}=U();return(0,W.jsxs)(`div`,{className:n.logo,onClick:()=>window.open(`/chat/home`),children:[(0,W.jsx)(`img`,{src:t,draggable:!1,alt:`logo`}),(0,W.jsx)(`span`,{children:e})]})},K=({activeKey:e,onMenuClick:t})=>{let{styles:n}=U(),r=T(),[i,a]=(0,H.useState)([`afterSale`,`preSale`,`serviceConfig`,`suggest`]);return(0,W.jsx)(`div`,{className:n.menu,style:{margin:0,padding:0,width:`100%`,minWidth:0,height:`100%`,display:`flex`,flexDirection:`column`,overflow:`hidden`},children:(0,W.jsx)(S,{mode:`inline`,selectedKeys:[e],openKeys:i,onOpenChange:e=>{a(e)},onClick:({key:e})=>t(e),style:{borderRight:0,width:`100%`,minWidth:0,flex:1,minHeight:0,overflowY:`auto`,WebkitOverflowScrolling:`touch`},items:[{key:`myTicket`,label:r.formatMessage({id:`sidebar.afterSale.myTicket`,defaultMessage:`我的工单`}),icon:(0,W.jsx)(g,{})}]})})};async function q(e){return k(`/api/v1/robot/query/org`,{method:`GET`,params:{...e,channel:E}})}async function J(e){return k(`/api/v1/robot/create/llm/thread`,{method:`POST`,data:{...e}})}var Y=({open:e,onSubmit:o,onCancel:s})=>{let c=(0,H.useRef)(!1),l=T(),{translateString:u}=z(),d=(e,t)=>e?u(e)||e:t,{isLoggedIn:f}=(0,H.useContext)(N),[p,h]=(0,H.useState)(0),[g]=(0,H.useState)(5),[v,y]=(0,H.useState)(0),[S,C]=(0,H.useState)([]),[w,k]=(0,H.useState)(!1),[A,j]=(0,H.useState)(``),M=F(e=>e.currentOrg),P=V(e=>e.addThread),I=V(e=>e.setCurrentThread),L=(0,H.useCallback)(async(e=p)=>{if(!f)return;if(c.current){console.log(`isLoading: 1`,c.current);return}c.current=!0,k(!0);let t=m.loading({content:`loading`,duration:0}),n={pageNumber:e,pageSize:g,name:D,nickname:A,orgUid:M?.uid,categoryUid:``,type:`LLM`,level:O};try{let r=await q(n);console.log(`queryRobotsByOrg: `,r?.data,n),r?.data.code===200?(t(),C(r?.data.data.content),y(r?.data.data.totalElements),h(e)):(t(),m.error(d(r?.data?.message,l.formatMessage({id:`chat.error.request.failed`,defaultMessage:`Request failed, please try again later`}))))}catch{t(),m.error(l.formatMessage({id:`chat.error.request.failed`,defaultMessage:`Request failed, please try again later`}))}finally{c.current=!1,k(!1)}},[M?.uid,f,p,g,A]);(0,H.useEffect)(()=>{e&&L(0)},[L,e,A]);let R=e=>{j(e)},B=e=>{L(e-1)},U=()=>{s()},G=async e=>{console.log(`startRobotChat`,e);let t={uid:e?.name},n={robot:JSON.stringify(t),forceNew:!0,hide:!1,channel:E},r=await J(n);console.log(`startRobotChat response:`,n,r?.data),r?.data.code===200?(P(r?.data.data),I(r?.data.data),o()):(m.error(d(r?.data?.message,l.formatMessage({id:`chat.error.request.failed`,defaultMessage:`Request failed, please try again later`}))),s())};return(0,W.jsx)(W.Fragment,{children:(0,W.jsxs)(b,{title:l.formatMessage({id:`robot.create.title`,defaultMessage:`创建大模型对话`}),open:e,onCancel:U,footer:[(0,W.jsx)(t,{onClick:U,children:l.formatMessage({id:`common.cancel`,defaultMessage:`取消`})},`cancel`)],children:[(0,W.jsx)(i,{placeholder:l.formatMessage({id:`robot.search.placeholder`,defaultMessage:`搜索提示语昵称`}),prefix:(0,W.jsx)(n,{}),value:A,onChange:e=>R(e.target.value),style:{marginBottom:16,marginTop:8},allowClear:!0}),S.length===0&&A&&!w&&(0,W.jsx)(`div`,{style:{textAlign:`center`,padding:`20px 0`},children:l.formatMessage({id:`common.noSearchResults`,defaultMessage:`没有找到匹配的提示语`})}),(0,W.jsx)(`div`,{style:{height:250,overflowY:`auto`},children:(0,W.jsx)(_,{dataSource:S,style:{marginTop:10},renderItem:e=>(0,W.jsx)(_.Item,{actions:[(0,W.jsx)(t,{onClick:()=>G(e),children:l.formatMessage({id:`pages.robot.chat`,defaultMessage:`Chat`})})],children:(0,W.jsx)(_.Item.Meta,{style:{marginLeft:`10px`},title:u(e?.nickname),description:u(e?.description)})},e?.uid)})}),!w&&S.length>0&&(0,W.jsx)(`div`,{style:{textAlign:`center`,marginTop:16},children:(0,W.jsx)(x,{current:p+1,pageSize:g,total:v,onChange:B,size:`small`,simple:!0})}),w&&(0,W.jsx)(`div`,{style:{textAlign:`center`,marginTop:20},children:(0,W.jsxs)(a,{children:[(0,W.jsx)(r,{}),(0,W.jsx)(`span`,{children:l.formatMessage({id:`common.loading`,defaultMessage:`加载中...`})})]})})]})})},X=()=>{let e=T(),[n,r]=(0,H.useState)(!1),[i,a]=(0,H.useState)(!1),{isLoggedIn:o,userInfo:s}=(0,H.useContext)(N),{doLogout:c}=R();return o&&s?(0,W.jsxs)(W.Fragment,{children:[(0,W.jsx)(t,{type:`text`,icon:(0,W.jsx)(p,{}),onClick:()=>{a(!0)},children:s.nickname}),(0,W.jsx)(b,{title:e.formatMessage({id:`logout.modal.title`,defaultMessage:`退出登录`}),open:i,onOk:()=>{c(),a(!1)},onCancel:()=>{a(!1)},okText:e.formatMessage({id:`logout.confirm`,defaultMessage:`确认退出`}),cancelText:e.formatMessage({id:`logout.cancel`,defaultMessage:`取消`}),children:(0,W.jsx)(`p`,{children:e.formatMessage({id:`logout.confirmation`,defaultMessage:`确认要退出登录吗？`})})})]}):(0,W.jsxs)(W.Fragment,{children:[(0,W.jsx)(t,{type:`primary`,icon:(0,W.jsx)(p,{}),onClick:()=>{r(!0)},children:(0,W.jsx)(w,{id:`login.button`,defaultMessage:`登录`})}),(0,W.jsx)(b,{title:e.formatMessage({id:`login.modal.title`,defaultMessage:`用户登录`}),open:n,onCancel:()=>{r(!1)},footer:null,width:500,destroyOnHidden:!0,mask:{closable:!1},style:{top:20},children:(0,W.jsx)(B,{isModel:!0})})]})},{Header:Z}=h,Q=({isDarkMode:e,toggleDarkMode:n,language:r,changeLanguage:i,isMobile:a,onToggleSidebar:o,rightExtra:h})=>{let g=T(),[_,v]=(0,H.useState)(M),[b,x]=(0,H.useState)(``),[C,w]=(0,H.useState)(``),{userInfo:E}=(0,H.useContext)(N),{accessToken:D,removeAccessToken:O}=I(),k=P(e=>e.resetUserInfo),F=!!(D&&D.trim().length>0),R=[{key:`zh-cn`,label:g.formatMessage({id:`language.zh`})},{key:`en`,label:g.formatMessage({id:`language.en`})},{key:`zh-tw`,label:g.formatMessage({id:`language.zh-TW`})},{key:`vi-vn`,label:g.formatMessage({id:`language.vi-VN`})},{key:`ms-my`,label:g.formatMessage({id:`language.ms-MY`})},{key:`ko-kr`,label:g.formatMessage({id:`language.ko-KR`,defaultMessage:`한국어 (대한민국)`})},{key:`es-es`,label:g.formatMessage({id:`language.es-ES`,defaultMessage:`Español (España)`})},{key:`fr-fr`,label:g.formatMessage({id:`language.fr-FR`,defaultMessage:`Français (France)`})},{key:`th-th`,label:g.formatMessage({id:`language.th-TH`,defaultMessage:`ภาษาไทย (ประเทศไทย)`})}],z=[{key:`logout`,label:g.formatMessage({id:`user.logout`,defaultMessage:`退出登录`}),icon:(0,W.jsx)(s,{}),onClick:async()=>{try{await L(),O(),k(),m.success(g.formatMessage({id:`user.logout.success`,defaultMessage:`退出成功`})),window.location.reload()}catch{m.error(g.formatMessage({id:`user.logout.failed`,defaultMessage:`退出失败`}))}}}],B=[],V=e=>{w(e.key)},U=({key:e})=>{i(e)},K=H.useCallback(async()=>{let e=await A();e?.custom?.enabled&&j()?(e?.custom?.logo?v(e?.custom?.logo):v(M),e?.custom?.name?x(e?.custom?.name):x(g.formatMessage({id:`app.helpcenter.title`}))):(v(M),x(g.formatMessage({id:`app.helpcenter.title`})))},[g,v,x]);(0,H.useEffect)(()=>{K()},[K]);let q={headerStyle:{background:e?`#141414`:`#fff`,borderBottom:`1px solid ${e?`#303030`:`#f0f0f0`}`,boxShadow:`0 2px 8px rgba(0, 0, 0, 0.06)`,height:`64px`,position:`relative`,zIndex:1e3,display:`flex`,justifyContent:`space-between`,alignItems:`center`},logoStyle:{fontSize:`18px`,fontWeight:`bold`,cursor:`pointer`,marginRight:24,display:`flex`,alignItems:`center`,gap:`4px`},mainMenuStyle:{background:`transparent`,border:`none`,height:`64px`,minWidth:`350px`},rightToolsStyle:{display:`flex`,alignItems:`center`,gap:a?`4px`:`8px`},mobileButtonStyle:{padding:`0 4px`},sidebarToggle:{display:a?`inline-flex`:`none`,marginRight:`8px`}};return(0,W.jsxs)(Z,{className:`header`,style:q.headerStyle,children:[(0,W.jsxs)(`div`,{style:{display:`flex`,alignItems:`center`,width:`100%`},children:[(0,W.jsx)(t,{type:`text`,icon:(0,W.jsx)(f,{}),style:q.sidebarToggle,onClick:o}),(0,W.jsx)(`div`,{className:`logo`,style:q.logoStyle,children:(0,W.jsx)(G,{title:b,logoUrl:_})}),!a&&(0,W.jsx)(S,{mode:`horizontal`,items:B,selectedKeys:[C],onClick:V,style:q.mainMenuStyle,triggerSubMenuAction:`hover`,disabledOverflow:!0})]}),(0,W.jsxs)(`div`,{style:q.rightToolsStyle,children:[h,a?(0,W.jsx)(W.Fragment,{}):(0,W.jsxs)(W.Fragment,{children:[(0,W.jsx)(y,{menu:{items:R,onClick:U,selectedKeys:[r]},placement:`bottomRight`,children:(0,W.jsx)(t,{type:`text`,icon:(0,W.jsx)(c,{})})}),(0,W.jsx)(t,{type:`text`,icon:e?(0,W.jsx)(d,{}):(0,W.jsx)(u,{}),onClick:n}),F?(0,W.jsx)(y,{menu:{items:z},placement:`bottomRight`,children:(0,W.jsxs)(`div`,{style:{cursor:`pointer`,display:`flex`,alignItems:`center`,gap:`8px`},children:[(0,W.jsx)(l,{icon:(0,W.jsx)(p,{})}),(0,W.jsx)(`span`,{children:E?.username||E?.nickname||g.formatMessage({id:`user.anonymous`,defaultMessage:`用户`})})]})}):(0,W.jsx)(X,{})]})]})]})};export{U as i,Y as n,K as r,Q as t};