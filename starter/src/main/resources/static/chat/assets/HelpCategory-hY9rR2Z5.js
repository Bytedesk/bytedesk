import"./rolldown-runtime-BJ9pB_0e.js";import{$t as e}from"./antdx-vendor-Dx2lixsX.js";import{i as t}from"./intl-vendor-8dDWo8Zj.js";import{_ as n,g as r,h as i}from"./react-vendor-C5CfbVxg.js";import{n as a}from"./urlParams-DXnsKm_E.js";import{t as o}from"./styled-vendor-z9pn11Pz.js";e();var s=t(),c=o.div`
  padding: 20px;
  min-height: 100vh;
  background: #fff;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  scroll-behavior: smooth;
  box-sizing: border-box;
  width: 100%;
  align-items: center;
`,l=o.div`
  width: 100%;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
`,u=o.div`
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  width: 100%;
`,d=o.button`
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  transition: color 0.2s;

  &:hover {
    color: #0066FF;
  }

  svg {
    width: 20px;
    height: 20px;
  }
`,f=o.h1`
  font-size: 24px;
  color: #333;
  margin: 0;
  flex: 1;
`,p=o.div`
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 60px;
  width: 100%;
`,m=o.div`
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;

  &:hover {
    opacity: 0.8;
  }
`,h=o.span`
  color: ${e=>{switch(e.$index){case 1:return`#ff6b6b`;case 2:return`#ff922b`;case 3:return`#ffd43b`;default:return`#868e96`}}};
  font-weight: 500;
`,g=o.span`
  color: #333;
  flex: 1;
`,_=o.span`
  color: #ccc;
`,v=()=>(0,s.jsx)(`svg`,{viewBox:`0 0 24 24`,fill:`none`,stroke:`currentColor`,strokeWidth:`2`,children:(0,s.jsx)(`path`,{d:`M19 12H5M12 19l-7-7 7-7`,strokeLinecap:`round`,strokeLinejoin:`round`})}),y=()=>{let e=i(),{id:t}=r();console.log(`HelpCategory id: `,t);let[o]=n(),y=a(o,`category`),b=[`如何使用基础功能`,`如何配置高级设置`,`常见问题解答`,`功能使用技巧`,`系统使用指南`,`新手入门教程`,`进阶使用说明`,`问题排查指南`],x=()=>{e(`/helpcenter`)},S=t=>{e(`/helpdetail/${t+1}?question=${encodeURIComponent(b[t])}&from=category`)};return(0,s.jsx)(c,{children:(0,s.jsxs)(l,{children:[(0,s.jsxs)(u,{children:[(0,s.jsxs)(d,{onClick:x,children:[(0,s.jsx)(v,{}),`返回`]}),(0,s.jsx)(f,{children:y})]}),(0,s.jsx)(p,{children:b.map((e,t)=>(0,s.jsxs)(m,{onClick:()=>S(t),children:[(0,s.jsx)(h,{$index:t+1,children:t+1}),(0,s.jsx)(g,{children:e}),(0,s.jsx)(_,{children:`›`})]},t))})]})})};export{y as default};