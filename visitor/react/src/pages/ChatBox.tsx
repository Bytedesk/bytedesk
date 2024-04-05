import React, { useEffect, useState } from "react";
// import "@chatui/core/es/styles/index.less";
// 引入样式
// import "@chatui/core/dist/index.css";
// 引入组件
// import Chat, { Bubble, MessageProps, QuickReplyItemProps, ToolbarItemProps, useMessages } from "@chatui/core";
// 引入ChatUI组件
// https://chatui.io/sdk/getting-started
import Chat, {
  Bubble,
  MessageProps,
  QuickReplyItemProps,
  ToolbarItemProps,
  useMessages,
} from "src/components/ChatUI";
// 自定义主题颜色 https://market.m.taobao.com/app/chatui/theme-builder/index.html
import "src/components/ChatUI/styles/index.less";
// import './css/chatbox.css';
// import './css/chatui-theme-dark.css';
import { useSearchParams } from "react-router-dom";
import { useUserStore } from "src/store/user";
import { EVENT_BUS_TOKEN_INVALID } from "src/utils/constants";
// import PubSub from 'pubsub-js';
// import { useThreadStore } from "src/store/thread";
// import { useWorkGroupStore } from "src/store/workgroup";
// import { sendMessageSSE } from "src/apis/message";
// import emitter from "../utils/events";
import { Helmet } from "react-helmet";
import useTheme from "src/hooks/useTheme";
import { stompConnect } from "src/network/stompjs";
// https://www.npmjs.com/package/react-cookie
import { useCookies } from "react-cookie";
// https://www.npmjs.com/package/ua-parser-js
import UAParser from "ua-parser-js";

//
const initialToolbars = [
  {
    type: 'image',
    title: '图片',
    icon: 'image',
    // img: '',
    // render?: any;
  },
  {
    type: "file",
    title: "文件",
    icon: "file",
  }
];

//
export const ChatBox = () => {
  // const { toolbars } = useState(initialToolbars);
  const { isDarkMode } = useTheme();
  const [searchParams] = useSearchParams();
  // const location = useLocation();
  // const [wid, setWid] = useState('');
  const [cookies, setCookie] = useCookies(["v_vid"]);
  //
  useEffect(() => {
    // 
    let parser = new UAParser(); // you need to pass the user-agent for nodejs
    // console.log('parser', parser); // {}
    let parserResults = parser.getResult();
    console.log('parserResults', parserResults,
      JSON.stringify(parserResults.browser),
      JSON.stringify(parserResults.os),
      JSON.stringify(parserResults.device));
    // 
    // stompConnect()
    //
    console.log("cookies.v_vid:", cookies.v_vid);
  }, []);
  //
  // 消息列表 setTyping
  const { messages, appendMsg, updateMsg } = useMessages([]);
  const [typing, setTyping] = useState(false);
  const msgRef = React.useRef(null);
  // http://localhost:9006/?t=1&id=c293374081784c0e9dd1d57020048e37&
  useEffect(() => {
    console.log("第一次渲染时调用");
    const obj = {};
    for (let item of searchParams.entries()) {
      obj[item[0]] = item[1];
    }
    console.log("t:", obj["t"]);
    console.log("id:", obj["id"]);
    // console.log('location:', location);
    // setWid(obj['wid']);
  }, [searchParams]);

  // 发送回调
  function handleSend(type: string, content: string) {
    if (type === "text" && content.trim()) {
      // TODO: 发送请求
      appendMsg({
        type: "text",
        content: { text: content },
        position: "right",
      });

      setTyping(true);
      //
    }
  }

  function handleImageSend(file: File): Promise<any> {
    console.log("handleImageSend", file);

    return null;
  }

  // 快捷短语回调，可根据 item 数据做出不同的操作，这里以发送文本消息为例
  // function handleQuickReplyClick(item: QuickReplyItemProps, index: number) {
  //   handleSend('text', item.name);
  // }

  function renderMessageContent(message: MessageProps) {
    const { type, content } = message;

    // 根据消息类型来渲染
    switch (type) {
      case "text":
        return <Bubble content={content.text} />;
      case "image":
        return (
          <Bubble type="image">
            <img src={content.picUrl} alt="" />
          </Bubble>
        );
      default:
        return null;
    }
  }

  function handleToolbarClick(item: ToolbarItemProps, event: React.MouseEvent) {
    console.log('handleToolbarClick', item, event);
  }

  // function handleAccessoryToggle(isAccessoryOpen: boolean) {
  //   console.log('handleAccessoryToggle', isAccessoryOpen);
  // }

  // let rightActions = {
  //   img: '',
  // };

  return (
    <>
      {isDarkMode && (
        <Helmet>
          {/* <title>My Title</title> */}
          {/* <link rel="stylesheet" type="text/css" href="./assets/css/chatui/chatbox.css"></link> */}
          <link
            rel="stylesheet"
            type="text/css"
            href="./assets/css/chatui/chatui-theme-dark.css"
          ></link>
        </Helmet>
      )}
      <Chat
        // https://chatui.io/docs/i18n 阿拉伯语	ar-EG、英语	en-US、法语	fr-FR、简体中文	zh-CN
        locale="zh-CN"
        navbar={{ title: "chat" }}
        messages={messages}
        isTyping={typing}
        messagesRef={msgRef}
        renderMessageContent={renderMessageContent}
        // quickReplies={defaultQuickReplies}
        // onQuickReplyClick={handleQuickReplyClick}
        onSend={handleSend}
        onImageSend={handleImageSend}
        toolbar={initialToolbars}
        onToolbarClick={handleToolbarClick}
        // rightAction={rightActions}
        // onAccessoryToggle={handleAccessoryToggle}
      />
    </>
  );
};
