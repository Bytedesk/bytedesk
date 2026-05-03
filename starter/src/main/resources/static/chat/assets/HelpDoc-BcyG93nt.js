import{a as e}from"./rolldown-runtime-BJ9pB_0e.js";import{$t as t}from"./antdx-vendor-Dx2lixsX.js";import{i as n}from"./intl-vendor-8dDWo8Zj.js";import{_ as r,h as i}from"./react-vendor-BOIGWeCs.js";import{n as a}from"./urlParams-DXnsKm_E.js";import{t as o}from"./styled-vendor-BNSxiypu.js";var s=e(t(),1),c=n(),l=o.div`
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  scroll-behavior: smooth;
`,u=o.div`
  display: flex;
  align-items: center;
  padding: 14px 16px;
  background: ${e=>e.$backgroundColor||`#fff`};
  color: ${e=>e.$textColor||`#333`};
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 100;
`,d=o.h1`
  margin: 0;
  margin-left: 8px;
  font-size: 16px;
  font-weight: 500;
  color: ${e=>e.$textColor||`#333`};
`,f=o.div`
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
`,p=o.div`
  background: #f0f8ff;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 24px;
  width: 100%;
  box-sizing: border-box;
`,m=o.input`
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 6px;
  background: #fff;
  font-size: 14px;
  color: #333;
  outline: none;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;

  &::placeholder {
    color: #999;
  }
`,h=o.div`
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
`,g=o.div`
  padding: 8px 4px;
  font-size: 14px;
  color: ${e=>e.$active?`#333`:`#999`};
  border-bottom: 2px solid ${e=>e.$active?`#0066FF`:`transparent`};
  cursor: pointer;
`,_=o.div`
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 60px;
`,v=o.div`
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;

  &:hover {
    opacity: 0.8;
  }
`,y=o.span`
  color: ${e=>{switch(e.$index){case 1:return`#ff6b6b`;case 2:return`#ff922b`;case 3:return`#ffd43b`;default:return`#868e96`}}};
  font-weight: 500;
`,b=o.span`
  color: #333;
  flex: 1;
`,x=o.span`
  color: #ccc;
`,S=o.div`
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 24px;
  width: 100%;
`,C=o.div`
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
`,w=o.div`
  font-size: 14px;
  color: #333;
  text-align: center;
`,T=o.div`
  font-size: 12px;
  color: #666;
  text-align: center;
`,E=o.button`
  border: none;
  background: none;
  padding: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  color: ${e=>e.$textColor||`#333`};
`,D=()=>(0,c.jsx)(`svg`,{viewBox:`0 0 1024 1024`,width:`20`,height:`20`,children:(0,c.jsx)(`path`,{d:`M563.8 512l262.5-312.9c4.4-5.2.7-13.1-6.1-13.1h-79.8c-4.7 0-9.2 2.1-12.3 5.7L511.6 449.8 295.1 191.7c-3-3.6-7.5-5.7-12.3-5.7H203c-6.8 0-10.5 7.9-6.1 13.1L459.4 512 196.9 824.9c-4.4 5.2-.7 13.1 6.1 13.1h79.8c4.7 0 9.2-2.1 12.3-5.7l216.5-258.1 216.5 258.1c3 3.6 7.5 5.7 12.3 5.7h79.8c6.8 0 10.5-7.9 6.1-13.1L563.8 512z`,fill:`currentColor`})}),O=o.div`
  margin-left: auto;
`,k=()=>{let e=i(),[t]=r(),n=a(t,`navbarBg`),o=a(t,`navbarText`),[k,A]=(0,s.useState)(`member`),j={member:[`如何查看/导出考勤数据`,`如何创建企业`,`如何设置子管理员`,`法人认证具体如何操作`,`如何进行年检认证`],admin:[`如何添加/删除管理员`,`如何设置管理员权限`,`如何查看管理日志`,`如何批量导入成员`,`如何设置部门架构`],developer:[`如何获取开发者密钥`,`如何对接第三方系统`,`如何使用API接口`,`如何配置开发环境`,`如何处理常见错误`],school:[`如何添加班级`,`如何管理学生信息`,`如何发布通知公告`,`如何查看考勤记录`,`如何与家长互动`]},M=()=>j[k]||[],N=[{id:`member`,label:`成员`},{id:`admin`,label:`管理员`},{id:`developer`,label:`开发者`},{id:`school`,label:`家校`}],P=[{icon:`微语创业版`,title:`微语创业版`,desc:`小微企业首选`},{icon:`专业版咨询`,title:`专业版咨询`,desc:`高效协作、灵活开放`},{icon:`微语会议`,title:`微语会议`,desc:`AI时代的开会方式`}],F=t=>{e(`/helpcategory/${t+1}?category=${encodeURIComponent(P[t].title)}`)},I=t=>{let n=M()[t];e(`/helpdetail/${t+1}?question=${encodeURIComponent(n)}`)};return(0,c.jsxs)(l,{children:[(0,c.jsxs)(u,{$backgroundColor:n,$textColor:o,children:[(0,c.jsx)(d,{$textColor:o,children:`帮助中心`}),(0,c.jsx)(O,{children:(0,c.jsx)(E,{onClick:()=>{window.parent===window?window.close():window.parent.postMessage({type:`CLOSE_CHAT_WINDOW`},`*`)},$textColor:o,children:(0,c.jsx)(D,{})})})]}),(0,c.jsxs)(f,{children:[(0,c.jsx)(p,{children:(0,c.jsx)(m,{placeholder:`输入关键词，搜索触手可及的服务`,"aria-label":`搜索帮助`})}),(0,c.jsx)(S,{children:P.map((e,t)=>(0,c.jsxs)(C,{onClick:()=>F(t),children:[(0,c.jsx)(w,{children:e.title}),(0,c.jsx)(T,{children:e.desc})]},t))}),(0,c.jsx)(h,{children:N.map(e=>(0,c.jsx)(g,{$active:k===e.id,onClick:()=>A(e.id),children:e.label},e.id))}),(0,c.jsx)(_,{children:M().map((e,t)=>(0,c.jsxs)(v,{onClick:()=>I(t),children:[(0,c.jsx)(y,{$index:t+1,children:t+1}),(0,c.jsx)(b,{children:e}),(0,c.jsx)(x,{children:`›`})]},t))})]})]})};export{k as default};