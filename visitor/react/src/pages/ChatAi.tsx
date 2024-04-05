import React, { useEffect, useState } from "react";
// 引入ChatUI组件
// https://chatui.io/sdk/getting-started
import Chat, {
  Bubble,
  MessageProps,
  QuickReplyItemProps,
  ToolbarItemProps,
  useMessages,
} from "src/components/ChatUI";
// https://chatui.io/docs/customize-theme
// 自定义主题颜色: https://market.m.taobao.com/app/chatui/theme-builder/index.html
// 自定义主题颜色: http://localhost:8908/
import "src/components/ChatUI/styles/index.less";
import "./css/chatbox.css";
// import './css/chatui-theme-dark.css'

import { useSearchParams } from "react-router-dom";
import { useUserAiStore } from "src/store/ai/visitor";
import { EVENT_BUS_TOKEN_INVALID } from "../utils/constants";
// import PubSub from 'pubsub-js';
import { useThreadAiStore } from "src/store/ai/thread";
import { sendMessageSSEPolyfill } from "src/apis/ai/message";
import emitter from "../utils/events";

//
const initialToolbars = [
  {
    type: "plus",
    title: "plus",
    icon: "plus",
    // img: '',
    // render?: any;
  },
];

// 默认快捷短语，可选
const defaultQuickReplies = [
  // {
  //   icon: 'message',
  //   name: '联系人工服务',
  //   isNew: true,
  //   isHighlight: true,
  // },
  // {
  //   name: '进度落后如何处理',
  // },
  {
    name: "考试日期",
  },
];

export const ChatAi = () => {
  // const { toolbars } = useState(initialToolbars);
  const [searchParams] = useSearchParams();
  // const location = useLocation();
  const [flag, setFlag] = useState(false);
  const [rid, setRid] = useState("");
  const { isLogin, register, loginToRefreshToken } = useUserAiStore((state) => {
    return {
      isLogin: state.isLogin,
      register: state.register,
      loginToRefreshToken: state.loginToRefreshToken,
    };
  });
  const initialMessages = [
    {
      _id: "initialMessagesId",
      type: "text",
      content: { text: "123" },
      // user: {
      //   avatar: message.user.avatar,
      // },
    },
  ];
  const { thread, getThread } = useThreadAiStore((state) => {
    return { thread: state.thread, getThread: state.getThread };
  });
  useThreadAiStore.subscribe((curThread, preThread) => {
    // console.log('curThread', curThread)
    if (!flag) {
      setFlag(true);
    }
  });
  // 消息列表 setTyping
  const { messages, appendMsg, updateMsg } = useMessages(initialMessages);
  const [add, setAdd] = useState(true);
  const [content, setContent] = useState("");
  const [jsonObject, setJsonObject] = useState({
    id: "",
    answer: "",
    type: "",
  });
  const [typing, setTyping] = useState(false);
  const msgRef = React.useRef(null);
  //
  useEffect(() => {
    updateMsg("initialMessagesId", {
      type: "text",
      content: { text: thread.content },
      user: {
        avatar: thread.robot.avatar,
      },
    });
  }, [flag]);

  useEffect(() => {
    console.log("useEffect json", jsonObject);
    //
    if (jsonObject.type === "add") {
      if (add) {
        setAdd(false);
        setContent(jsonObject.answer);
        console.log("add");
        appendMsg({
          _id: jsonObject.id,
          type: "text",
          content: { text: content },
        });
      } else {
        setContent(content + jsonObject.answer);
      }
    } else if (jsonObject.type === "finish") {
      setAdd(true);
      setContent("");
    }
  }, [jsonObject]);

  useEffect(() => {
    console.log("useEffect add", add, content, jsonObject);

    if (jsonObject.type === "add") {
      if (add) {
      } else {
        console.log("update");
        updateMsg(jsonObject.id, {
          type: "text",
          content: { text: content },
          user: {
            avatar: thread.robot.avatar,
          },
        });
      }
    } else {
      console.log("other");
    }

    msgRef.current.scrollToEnd();
    console.log("scrollToEnd:", msgRef);
  }, [add, content]);

  // onmessage: {"id": "8059178098561234419", "answer": " 根据", "type": "add"}
  // onmessage: {"id": "8059178098561234419", "answer": "", "type": "finish"}
  const sendmessagesse_callback = (json: string) => {
    console.log("callback:", json);
    setTyping(false);
    let jsonObject = JSON.parse(json);
    setJsonObject(jsonObject);
  };

  // http://localhost:3036/?rid=201812200005351&type=wg
  useEffect(() => {
    console.log("第一次渲染时调用");
    const obj = {};
    for (let item of searchParams.entries()) {
      obj[item[0]] = item[1];
    }
    console.log("rid:", obj["rid"]);
    // console.log('location:', location);
    setRid(obj["rid"]);
  }, [searchParams]);

  useEffect(() => {
    if (rid) {
      // 监听token失效
      var handleTokenInvalid = function (data: string) {
        console.log("token过期，强制刷新登录", data);
        loginToRefreshToken();
      };
      emitter.on(EVENT_BUS_TOKEN_INVALID, handleTokenInvalid);
      // 取消订阅
      // const token = emitter.on('topic', mySubscriber);
      // PubSub.unsubscribe(token);
      // console.log("rid:", rid);
      if (isLogin()) {
        // 请求会话
        getThread(rid);
      } else {
        // 注册用户
        register(rid);
      }
      //
      // getRobot(rid);
    }
  }, [getThread, isLogin, loginToRefreshToken, register, rid]);

  // useEffect(() => {
  //   console.log('useEffect currentRobot:', currentRobot);
  // }, [currentRobot]);

  // 发送回调
  function handleSend(type: string, content: string) {
    if (type === "text" && content.trim()) {
      // 发送请求
      appendMsg({
        type: "text",
        content: { text: content },
        position: "right",
      });

      setTyping(true);

      //
      let message: MESSAGE_AI.knowledge_base_chat = {
        query: content,
        rid: rid,
      };
      sendMessageSSEPolyfill(message, sendmessagesse_callback);
    }
  }

  function handleImageSend(file: File): Promise<any> {
    console.log("handleImageSend", file);

    return null;
  }

  // 快捷短语回调，可根据 item 数据做出不同的操作，这里以发送文本消息为例
  function handleQuickReplyClick(item: QuickReplyItemProps, index: number) {
    handleSend("text", item.name);
  }

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
    console.log("handleToolbarClick", item, event);
  }

  function handleAccessoryToggle(isAccessoryOpen: boolean) {
    console.log("handleAccessoryToggle", isAccessoryOpen);
  }

  let rightActions = {
    img: "",
  };

  return (
    <Chat
      // https://chatui.io/docs/i18n 阿拉伯语	ar-EG、英语	en-US、法语	fr-FR、简体中文	zh-CN
      locale="zh-CN"
      navbar={{ title: thread.robot.name }}
      messages={messages}
      isTyping={typing}
      messagesRef={msgRef}
      renderMessageContent={renderMessageContent}
      quickReplies={defaultQuickReplies}
      onQuickReplyClick={handleQuickReplyClick}
      onSend={handleSend}
      onImageSend={handleImageSend}
      toolbar={initialToolbars}
      onToolbarClick={handleToolbarClick}
      rightAction={rightActions}
      onAccessoryToggle={handleAccessoryToggle}
    />
  );
};
