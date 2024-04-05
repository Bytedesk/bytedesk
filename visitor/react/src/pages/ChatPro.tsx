import React, { useEffect, useRef } from "react";
import "./css/sdk.css";
import "./css/chatpro.css";
import MyCard from "src/components/MyCard";

export const ChatPro = () => {
  const wrapper = useRef();

  useEffect(() => {
    const bot = new window.ChatSDK({
      root: wrapper.current,
      config: {
        // lang: 'en-US',
        // lang: 'zh-CN',
        navbar: {
          //   logo: "https://gw.alicdn.com/tfs/TB1Wbldh7L0gK0jSZFxXXXWHVXa-168-33.svg",
          title: "微语智能客服",
        },
        // 头像白名单
        // avatarWhiteList: ["knowledge", "recommend"],
        // 机器人信息
        robot: {
          avatar:
            "https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg",
        },
        toolbar: [
          {
            type: "plus type", // 类型
            icon: "plus", // 图标（svg），与下面的 img 二选一即可
            // img: "", // 图片（img），推荐用 56x56 的图，会覆盖 icon
            title: "plus title", // 名称
          },
        ],
        // 首屏消息
        messages: [
          {
            type: "system",
            content: {
              text: "智能助理进入对话，为您服务",
            },
          },
          {
            type: "text",
            content: {
              text: "浙江智能助理为您服务，请问有什么可以帮您？",
            },
          },
          {
            type: "card",
            content: {
              code: "switch-location",
            },
          },
        ],
        // 快捷短语
        quickReplies: [
          { name: "健康码颜色" },
          { name: "入浙通行申报" },
          { name: "健康码是否可截图使用" },
          { name: "健康通行码适用范围" },
        ],
        // 输入框占位符
        placeholder: "输入任何您想办理的服务",
        components: {
          // 本地模版
          "my-card": MyCard,
        },
        // 侧边栏
        sidebar: [
          //   {
          //     code: 'my-card',
          //     data: {
          //         list: [
          //             {
          //                 title: "如何办理准生证？",
          //             },
          //             {
          //                 title: "生育登记成功后在哪里看",
          //             },
          //             {
          //                 title: "链接形式",
          //                 url: "https://www.baidu.com/",
          //             },
          //         ],
          //     }
          // },
          {
            code: "recommend",
            data: {
              list: [
                {
                  title: "如何办理准生证？",
                },
                {
                  title: "生育登记成功后在哪里看",
                },
                {
                  title: "链接形式",
                  url: "https://www.baidu.com/",
                },
              ],
            },
          },
          //   {
          //     title: "公告",
          //     code: "richtext",
          //     data: {
          //       text: '<p>这里是富文本内容，支持<a href="https://chatui.io/sdk/getting-started">链接</a>，可展示图片<img src="https://gw.alicdn.com/tfs/TB17TaySSzqK1RjSZFHXXb3CpXa-80-80.svg" /></p>',
          //     },
          //   },
          // {
          //     code: "sidebar-suggestion",
          //     data: [
          //         {
          //             label: "疫情问题",
          //             list: [
          //             "身边有刚从湖北来的人，如何报备",
          //             "接触过武汉人，如何处理？",
          //             "发烧或咳嗽怎么办？",
          //             "去医院就医时注意事项",
          //             "个人防护",
          //             "传播途径",
          //             "在家消毒",
          //             ],
          //         },
          //         {
          //             label: "法人问题",
          //             list: [
          //             "企业设立",
          //             "企业运行",
          //             "企业变更",
          //             "企业服务",
          //             "企业注销",
          //             "社会团体",
          //             "民办非企业",
          //             ],
          //         },
          //     ],
          // },
          // {
          //     code: "sidebar-tool",
          //     title: "常用工具",
          //     data: [
          //     {
          //         name: "咨询表单",
          //         icon: "clipboard-list",
          //         url: "http://www.zjzxts.gov.cn/wsdt.do?method=suggest&xjlb=0&ymfl=1&uflag=1",
          //     },
          //     {
          //         name: "投诉举报",
          //         icon: "exclamation-shield",
          //         url: "http://www.zjzxts.gov.cn/wsdt.do?method=suggest&xjlb=1",
          //     },
          //     {
          //         name: "办事进度",
          //         icon: "history",
          //         url: "http://www.zjzwfw.gov.cn/zjzw/search/progress/query.do?webId=1",
          //     },
          //     ],
          // },
          // {
          //     code: "sidebar-phone",
          //     title: "全省统一政务服务热线",
          //     data: ["12345"],
          // },
        ],
      },
      requests: {
        send: function (msg) {
          console.log("send", msg);
          //   if (msg.type === "text") {
          //     return {
          //       url: "//api.server.com/ask",
          //       data: {
          //         q: msg.content.text,
          //       },
          //     };
          //   }
        },
      },
      handlers: {
        onToolbarClick: function (item, ctx) {
          // item 即为上面 toolbar 中被点击的那一项，可通过 item.type 区分
          // ctx 为上下文，可用 ctx.appendMessage 渲染消息等
          console.log("onToolbarClick", item, ctx);
        },
        parseResponse: function (res, requestType) {
          if (requestType === "send" && res.Messages) {
            // 解析 ISV 消息数据
            // return isvParser({ data: res });
          }
          return res;
        },
        track: function (data) {
          // 埋点 https://chatui.io/sdk/track
          console.log(data.eventType);

          // 传给后端，具体可用第三方工具或得自己实现
          // sendRequest(data);
        },
      },
    });

    bot.run();
  }, []);

  // 注意 wrapper 的高度
  return <div style={{ height: "100%" }} ref={wrapper} />;
};
