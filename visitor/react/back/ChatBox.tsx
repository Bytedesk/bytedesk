// import React, { useEffect, useState } from 'react';
// // import "@chatui/core/es/styles/index.less";
// // 引入样式
// // import "@chatui/core/dist/index.css";
// // 引入组件
// // import Chat, { Bubble, MessageProps, QuickReplyItemProps, ToolbarItemProps, useMessages } from "@chatui/core";
// // 引入ChatUI组件
// // https://chatui.io/sdk/getting-started
// import Chat, {
//   Bubble,
//   MessageProps,
//   QuickReplyItemProps,
//   ToolbarItemProps,
//   useMessages,
// } from 'src/components/ChatUI';
// // 自定义主题颜色 https://market.m.taobao.com/app/chatui/theme-builder/index.html
// import 'src/components/ChatUI/styles/index.less';
// // import './css/chatbox.css';
// // import './css/chatui-theme-dark.css';
// import { useSearchParams } from 'react-router-dom';
// import { useUserStore } from 'src/store/user';
// import { EVENT_BUS_TOKEN_INVALID } from 'src/utils/constants';
// // import PubSub from 'pubsub-js';
// import { useThreadStore } from 'src/store/thread';
// import { useWorkGroupStore } from 'src/store/workgroup';
// import { sendMessageSSE } from 'src/apis/message';
// import emitter from '../utils/events';
// import { Helmet } from "react-helmet";
// import useTheme from 'src/hooks/useTheme';
// import { stompConnect } from 'src/network/stompjs';
// // https://www.npmjs.com/package/react-cookie
// import { useCookies } from 'react-cookie';

// //
// const initialToolbars = [
//   {
//     type: 'plus',
//     title: 'plus',
//     icon: 'plus',
//     // img: '',
//     // render?: any;
//   },
// ];
// // 默认快捷短语，可选
// const defaultQuickReplies = [
//   // {
//   //   icon: 'message',
//   //   name: '联系人工服务',
//   //   isNew: true,
//   //   isHighlight: true,
//   // },
//   {
//     name: '进度落后如何处理',
//   },
//   {
//     name: '考试日期',
//   },
// ];

// export const ChatBox = () => {
//   // const { toolbars } = useState(initialToolbars);
//   const { isDarkMode } = useTheme();
//   const [searchParams] = useSearchParams();
//   // const location = useLocation();
//   const [wid, setWid] = useState('');
//   const [cookies, setCookie] = useCookies(['v_vid']);
//   //
//   useEffect(() => {

//     // stompConnect()
//     //
//     console.log('cookies', cookies, cookies.v_vid);

//   }, []);
//   //
//   const { isLogin, register, loginToRefreshToken } = useUserStore((state) => {
//     return {
//       isLogin: state.isLogin,
//       register: state.register,
//       loginToRefreshToken: state.loginToRefreshToken,
//     };
//   });
//   const { thread, message, getThread } = useThreadStore((state) => {
//     return { thread: state.thread, message: state.message, getThread: state.getThread };
//   });
//   //
//   const { currentWorkGroup, getWorkGroup } = useWorkGroupStore((state) => {
//     return {
//       currentWorkGroup: state.currentWorkGroup,
//       getWorkGroup: state.getWorkGroup,
//     };
//   });
//   //
//   const initialMessages = [
//     {
//       _id: message.mid,
//       type: 'text',
//       content: { text: message.content },
//       // user: {
//       //   avatar: message.user.avatar,
//       // },
//     },
//   ];
//   // 消息列表 setTyping
//   const { messages, appendMsg, updateMsg } = useMessages(initialMessages);
//   const [add, setAdd] = useState(true);
//   const [content, setContent] = useState('');
//   const [jsonObject, setJsonObject] = useState({
//     id: '',
//     answer: '',
//     type: '',
//   });
//   const [typing, setTyping] = useState(false);
//   const msgRef = React.useRef(null);
//   //
//   useEffect(() => {
//     console.log('useEffect json', jsonObject);
//     //
//     if (jsonObject.type === 'add') {
//       if (add) {
//         setAdd(false);
//         setContent(jsonObject.answer);
//         console.log('add');
//         appendMsg({
//           _id: jsonObject.id,
//           type: 'text',
//           content: { text: content },
//         });
//       } else {
//         setContent(content + jsonObject.answer);
//       }
//     } else if (jsonObject.type === 'finish') {
//       setAdd(true);
//       setContent('');
//     }
//   }, [jsonObject]);

//   useEffect(() => {
//     console.log('useEffect add', add, content, jsonObject);

//     if (jsonObject.type === 'add') {
//       if (add) {
//       } else {
//         console.log('update');
//         updateMsg(jsonObject.id, {
//           type: 'text',
//           content: { text: content },
//         });
//       }
//     } else {
//       console.log('other');
//     }

//     msgRef.current.scrollToEnd();
//     console.log('scrollToEnd:', msgRef);
//   }, [add, content]);

//   // onmessage: {"id": "8059178098561234419", "answer": " 根据", "type": "add"}
//   // onmessage: {"id": "8059178098561234419", "answer": "", "type": "finish"}
//   const sendmessagesse_callback = (json: string) => {
//     console.log('callback:', json);
//     setTyping(false);
//     let jsonObject = JSON.parse(json);
//     setJsonObject(jsonObject);
//   };

//   // http://localhost:3036/?wid=201812200005351&type=wg
//   useEffect(() => {
//     console.log('第一次渲染时调用');
//     const obj = {};
//     for (let item of searchParams.entries()) {
//       obj[item[0]] = item[1];
//     }
//     console.log('wid:', obj['wid']);
//     // console.log('location:', location);
//     setWid(obj['wid']);
//   }, [searchParams]);

//   useEffect(() => {
//     if (wid) {
//       // 监听token失效
//       var handleTokenInvalid = function (data: string) {
//         console.log('token过期，强制刷新登录',  data);
//         loginToRefreshToken();
//       };
//       emitter.on(EVENT_BUS_TOKEN_INVALID, handleTokenInvalid);
//       // 取消订阅
//       // const token = emitter.on('topic', mySubscriber);
//       // PubSub.unsubscribe(token);
//       // console.log("wid:", wid);
//       if (isLogin()) {
//         // 请求会话
//         getThread(wid);
//       } else {
//         // 注册用户
//         register(wid);
//       }
//       //
//       getWorkGroup(wid);
//     }
//   }, [getThread, getWorkGroup, isLogin, loginToRefreshToken, register, wid]);

//   useEffect(() => {
//     console.log('useEffect currentWorkGroup:', currentWorkGroup);
//   }, [currentWorkGroup]);

//   // 发送回调
//   function handleSend(type: string, content: string) {
//     if (type === 'text' && content.trim()) {
//       // TODO: 发送请求
//       appendMsg({
//         type: 'text',
//         content: { text: content },
//         position: 'right',
//       });

//       setTyping(true);

//       let message: MESSAGE.knowledge_base_chat = {
//         query: content,
//         knowledge_base_name: wid, //"gaoxiang",
//         top_k: 3,
//         score_threshold: 1,
//         history: [],
//         stream: true,
//         model_name: 'zhipu-api',
//         temperature: 0.7,
//         prompt_name: 'knowledge_base_chat',
//         // local_doc_url: false,
//       };

//       sendMessageSSE(message, sendmessagesse_callback);
//     }
//   }

//   function handleImageSend(file: File): Promise<any> {
//     console.log('handleImageSend', file);

//     return null;
//   }

//   // 快捷短语回调，可根据 item 数据做出不同的操作，这里以发送文本消息为例
//   function handleQuickReplyClick(item: QuickReplyItemProps, index: number) {
//     handleSend('text', item.name);
//   }

//   function renderMessageContent(message: MessageProps) {
//     const { type, content } = message;

//     // 根据消息类型来渲染
//     switch (type) {
//       case 'text':
//         return <Bubble content={content.text} />;
//       case 'image':
//         return (
//           <Bubble type="image">
//             <img src={content.picUrl} alt="" />
//           </Bubble>
//         );
//       default:
//         return null;
//     }
//   }

//   function handleToolbarClick(item: ToolbarItemProps, event: React.MouseEvent) {
//     console.log('handleToolbarClick', item, event);
//   }

//   function handleAccessoryToggle(isAccessoryOpen: boolean) {
//     console.log('handleAccessoryToggle', isAccessoryOpen);
//   }

//   let rightActions = {
//     img: '',
//   };

//   return (
//     <>
//       {
//         isDarkMode &&
//         <Helmet>
//           {/* <title>My Title</title> */}
//           {/* <link rel="stylesheet" type="text/css" href="./assets/css/chatui/chatbox.css"></link> */}
//           <link rel="stylesheet" type="text/css" href="./assets/css/chatui/chatui-theme-dark.css"></link>
//         </Helmet>
//       }
//       <Chat
//         // https://chatui.io/docs/i18n 阿拉伯语	ar-EG、英语	en-US、法语	fr-FR、简体中文	zh-CN
//         locale="zh-CN"
//         navbar={{ title: 'chat' }}
//         messages={messages}
//         isTyping={typing}
//         messagesRef={msgRef}
//         renderMessageContent={renderMessageContent}
//         quickReplies={defaultQuickReplies}
//         onQuickReplyClick={handleQuickReplyClick}
//         onSend={handleSend}
//         onImageSend={handleImageSend}
//         toolbar={initialToolbars}
//         onToolbarClick={handleToolbarClick}
//         rightAction={rightActions}
//         onAccessoryToggle={handleAccessoryToggle}
//       />
//     </>
//   );
// };
