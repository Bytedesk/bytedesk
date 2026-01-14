import{u as t,j as e}from"./index-CJncBc6B.js";const p={title:"下载录音",openapi:"get /api/v1/recording/download/{recordingId}"};function c(r){const n={code:"code",h1:"h1",h2:"h2",li:"li",p:"p",pre:"pre",ul:"ul",...t(),...r.components},{ApiPlayground:o,ParamField:s,RequestExample:a,ResponseExample:d}=n;return o||i("ApiPlayground"),s||i("ParamField"),a||i("RequestExample"),d||i("ResponseExample"),e.jsxs(e.Fragment,{children:[e.jsx(n.h1,{id:"下载录音",children:"下载录音"}),`
`,e.jsx(n.p,{children:"下载指定的呼叫录音文件。"}),`
`,e.jsxs(a,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-bash",children:`curl --location --request GET 'https://api.weiyuai.cn/api/v1/recording/download/rec_1234567890' \\
--header 'Authorization: Bearer YOUR_ACCESS_TOKEN' \\
--output recording.mp3
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-python",children:`import requests

url = "https://api.weiyuai.cn/api/v1/recording/download/rec_1234567890"
headers = {
    "Authorization": "Bearer YOUR_ACCESS_TOKEN"
}

response = requests.get(url, headers=headers, stream=True)
with open('recording.mp3', 'wb') as f:
    for chunk in response.iter_content(chunk_size=8192):
        f.write(chunk)
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-javascript",children:`const axios = require('axios');
const fs = require('fs');

const url = 'https://api.weiyuai.cn/api/v1/recording/download/rec_1234567890';
const headers = {
  'Authorization': 'Bearer YOUR_ACCESS_TOKEN'
};

axios.get(url, { headers, responseType: 'stream' })
  .then(response => {
    response.data.pipe(fs.createWriteStream('recording.mp3'));
  })
  .catch(error => console.error(error));
`})})]}),`
`,e.jsxs(d,{children:[e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-text",children:`Binary audio file content (MP3/WAV format)
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 404,
  "message": "Recording not found"
}
`})}),e.jsx(n.pre,{children:e.jsx(n.code,{className:"language-json",children:`{
  "code": 401,
  "message": "Invalid or expired access token"
}
`})})]}),`
`,e.jsx(n.h2,{id:"路径参数",children:"路径参数"}),`
`,e.jsx(s,{path:"recordingId",type:"string",required:!0,children:e.jsx(n.p,{children:"录音文件的唯一标识符"})}),`
`,e.jsx(n.h2,{id:"响应说明",children:"响应说明"}),`
`,e.jsx(n.p,{children:"成功时返回二进制音频文件流，文件格式由 Content-Type header 指定："}),`
`,e.jsxs(n.ul,{children:[`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"audio/mpeg"})," - MP3 格式"]}),`
`,e.jsxs(n.li,{children:[e.jsx(n.code,{children:"audio/wav"})," - WAV 格式"]}),`
`]}),`
`,e.jsx(n.h2,{id:"使用-apiplayground-测试",children:"使用 ApiPlayground 测试"}),`
`,e.jsx(o,{method:"GET",endpoint:"/api/v1/recording/download/rec_1234567890",description:"下载录音文件"})]})}function h(r={}){const{wrapper:n}={...t(),...r.components};return n?e.jsx(n,{...r,children:e.jsx(c,{...r})}):c(r)}function i(r,n){throw new Error("Expected component `"+r+"` to be defined: you likely forgot to import, pass, or provide it.")}export{h as default,p as frontmatter};
