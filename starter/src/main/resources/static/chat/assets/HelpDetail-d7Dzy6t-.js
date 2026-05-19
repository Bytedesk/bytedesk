import{a as e}from"./rolldown-runtime-Ps8w5BGs.js";import{mn as t}from"./antdx-vendor-RNrjs4ZW.js";import{i as n}from"./intl-vendor-CR6WtLaI.js";import{_ as r,h as i}from"./react-vendor-BlrekbrI.js";import{n as a}from"./urlParams-DXnsKm_E.js";import{t as o}from"./styled-vendor-4-Msa-gK.js";var s=e(t(),1),c=n(),l=o.div`
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  scroll-behavior: smooth;
  box-sizing: border-box;
  padding-bottom: 80px;
`,u=o.div`
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
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
  font-size: 16px;
  color: #666;
  line-height: 1.6;
  flex: 1;
`,m=o.div`
  margin-top: 40px;
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
`,h=o.div`
  display: flex;
  gap: 16px;
`,g=o.button`
  padding: 8px 24px;
  border-radius: 20px;
  border: 1px solid ${e=>e.$primary?`#0066FF`:`#ddd`};
  background: ${e=>e.$primary?`#0066FF`:`#fff`};
  color: ${e=>e.$primary?`#fff`:`#666`};
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    opacity: 0.8;
  }
`,_=o.textarea`
  width: 100%;
  height: 100px;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: none;
  margin-top: 16px;
  font-size: 14px;

  &:focus {
    outline: none;
    border-color: #0066FF;
  }
`,v=o.button`
  padding: 8px 24px;
  border-radius: 20px;
  background: #0066FF;
  color: #fff;
  border: none;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    opacity: 0.8;
  }
`,y=()=>(0,c.jsx)(`svg`,{viewBox:`0 0 24 24`,fill:`none`,stroke:`currentColor`,strokeWidth:`2`,children:(0,c.jsx)(`path`,{d:`M19 12H5M12 19l-7-7 7-7`,strokeLinecap:`round`,strokeLinejoin:`round`})}),b=()=>{let e=i(),[t]=r(),n=a(t,`question`),[o,b]=(0,s.useState)(!1),[x,S]=(0,s.useState)(``),C=()=>{a(t,`from`)===`category`?e(-1):e(`/helpcenter`)},w=`这是问题 "${n}" 的详细解答...

    1. 第一步
    首先，您需要了解这个功能的基本使用方法...
    
    2. 重要说明
    在操作过程中，请注意以下几点重要事项...
    
    3. 具体步骤
    - 打开设置页面
    - 找到相关选项
    - 按照提示进行配置
    
    4. 常见问题
    在使用过程中可能遇到以下问题...
    
    5. 注意事项
    请确保在操作时遵循以下建议...
    
    6. 更多帮助
    如果您需要更多帮助，可以...
  `,T=e=>{e?console.log(`Helpful feedback submitted`):b(!0)};return(0,c.jsxs)(l,{children:[(0,c.jsxs)(u,{children:[(0,c.jsxs)(d,{onClick:C,children:[(0,c.jsx)(y,{}),`返回`]}),(0,c.jsx)(f,{children:n})]}),(0,c.jsx)(p,{children:w}),(0,c.jsxs)(m,{children:[(0,c.jsx)(`div`,{children:`这个答案对您有帮助吗？`}),(0,c.jsxs)(h,{children:[(0,c.jsx)(g,{$primary:!0,onClick:()=>T(!0),children:`有帮助`}),(0,c.jsx)(g,{onClick:()=>T(!1),children:`没帮助`})]}),o&&(0,c.jsxs)(c.Fragment,{children:[(0,c.jsx)(_,{value:x,onChange:e=>S(e.target.value),placeholder:`请告诉我们您遇到了什么问题...`}),(0,c.jsx)(v,{onClick:()=>{console.log(`Feedback submitted:`,x),b(!1),S(``)},children:`提交反馈`})]})]})]})};export{b as default};