import moment from './moment.min.js'
const constants = require('../constants.js')
const Stomp = require('./stomp.min.js').Stomp;

//
let uid;
let username;
let nickname;
let avatar;
//
let stompClient;
let stompReconnectTimes = 0;
// 订阅主题topic
let subscribedTopics = []
// socket连接中
let socketConnecting = false
// socket是否连接
let socketConnected = false;
// socketTask实例
let socketTask = false
// 是否断线重连
let reconnect = true;
// 缓存消息
let messagesCache = {}
//
let currentThread = {
	tid: '',
	topic: ''
}
//
var stompApi = {

  connect: function(thread, callback) {
    stompApi.printLog('connect stomp')
	currentThread = thread;
	// 已经连接则无需重复连接
	if (socketConnected) {
		// 长连接成功回调
		callback();
		return
	}

    // 发送消息
    function sendSocketMessage(msg) {
	  // printLog('sendSocketMessage:', msg)
      // 如果socket已连接则发送消息
      if (socketConnected) {
        // uni.sendSocketMessage({
        //   data: msg
        // })
		socketTask.send({
			data: msg,
			success(res) {
				// console.log('socketTask send success:', res);
			},
			fail(err) {
				// TODO: 发送失败 待处理
				// console.log('socketTask send failed:', err);
			}
		})
      } else {
        stompApi.printLog('提示连接断开，无法发送消息')
		// 重连
		setTimeout(function () {
			connectWebSocket();
		}, 5000)
      }
    }

    // 关闭连接
    function close() {
	  stompApi.printLog('close')
      if (socketConnected) {
        // uni.closeSocket()
		if (socketTask && socketTask.close) {
			socketTask.close()
		}
        socketConnected = false;
		// 为断开重连做准备
		subscribedTopics = [];
      }
    }
	
	//
	function onopen() {
		stompApi.printLog('onopen');
		// stompApi.stompConnect(webSocket)
	}
	
	//
	function onmessage(message) {
		// printLog('onmessage:', message)
	}

    // 符合WebSocket定义的对象
    var webSocket = {
      send: sendSocketMessage,
      close: close,
	  onopen: onopen,
	  onmessage: onmessage
    }

    // 创建一个 WebSocket 连接
    function connectWebSocket() {
	  //
	  uid = uni.getStorageSync(constants.uid)
	  username = uni.getStorageSync(constants.username)
	  nickname = uni.getStorageSync(constants.nickname)
	  avatar = uni.getStorageSync(constants.avatar)
	  //
	  if (socketConnected) {
		return
	  }
	  //
	  socketTask = uni.connectSocket({
	  	url: constants.WEBSOCKET_URL + uni.getStorageSync(constants.accessToken),
	  	// #ifdef MP
	  	header: {
	  		'content-type': 'application/json'
	  	},
	  	// #endif
	  	// #ifdef MP-WEIXIN
	  	method: 'GET',
	  	// #endif
	  	success(res) {
	  		// 这里是接口调用成功的回调，不是连接成功的回调，请注意
			stompApi.printLog("socketTask success")
	  	},
	  	fail(err) {
	  		// 这里是接口调用失败的回调，不是连接失败的回调，请注意
			stompApi.printLog("socketTask failed")
	  	}
	  })
	  // console.log(socketTask);
	  
	  socketTask.onOpen((result) => {
	  	stompApi.printLog("SocketOpen:" + result)
		socketConnecting = false;
	  	socketConnected = true;
	  	webSocket.onopen();
	  })
	  
	  socketTask.onMessage((result) => {
	  	webSocket.onmessage(result);
	  	// console.log('onMessage', result)
	  })
	  
	  socketTask.onError((result) => {
	  	stompApi.printLog("SocketError:" + result)
		socketConnecting = false
	  	if (!socketConnected) {
	  	  // 为断开重连做准备
	  	  subscribedTopics = [];
	  	  // 断线重连
	  	  if (reconnect) {
	  	    setTimeout(function () {
			  connectWebSocket();
		    }, 5000)
	  	  }
	  	}
	  	// this.connecting = false
	  	// this.connected = false
	  })
	  
	  socketTask.onClose((result) => {
	  	stompApi.printLog("SocketClose:" + result)
	  	socketConnected = false;
		socketTask = false
	  	// 为断开重连做准备
	  	subscribedTopics = [];
	  	// 断线重连
	  	if (reconnect) {
	  	  setTimeout(function () {
	  	  	connectWebSocket();
	  	  }, 5000)
	  	}
	  })
    }
    //
    connectWebSocket();
    stompApi.stompConnect(webSocket, callback)
  },

  /**
   * stomp长连接
   * TODO: 加载所属工作组，添加订阅
   */
  stompConnect: function(webSocket, callback) {
    /**
     * 定期发送心跳或检测服务器心跳
     *  The heart-beating is using window.setInterval() to regularly send heart-beats and/or check server heart-beats.
     *  可看stomp.js的源码（195,207，489行），由于小程序没有window对象，所以我们要调用小程序的定时器api实现
     */
    Stomp.setInterval = function (interval, f) {
      return setInterval(f, interval);
    };

    // 结束定时器的循环调用
    Stomp.clearInterval = function (id) {
      return clearInterval(id);
    };

    stompClient = Stomp.over(webSocket);
    stompApi.printLog('stomp connecting...')
	// 更新连接状态：连接中...
    let connectionStatus = constants.STOMP_CONNECTION_STATUS_CONNECTING
    // commit(types.UPDATE_USER_CONNECTION, { connectionStatus }, { root: true })
    uni.$emit(constants.EVENT_BUS_STOMP_CONNECTION_STATUS, connectionStatus)
    // to disable logging, set it to an empty function:
    if (constants.IS_PRODUCTION) {
      stompClient.debug = function (value) {}
    }
    stompClient.connect({}, function (frame) {
      // printLog('Connected: ' + frame);
      // TODO: 断开重连成功之后，需要重新从服务器请求thread，然后添加sub订阅
      // 登录成功之后，尝试登录次数清零
      stompReconnectTimes = 0
	  // 订阅topic
	  if (currentThread != null && currentThread != undefined) {
		let topic = currentThread.topic.replace(/\//g, ".");
		stompApi.subscribeTopic(topic)
	  }
	  //
	  for (var i = 0; i < subscribedTopics.length; i++) {
	  	let tp = subscribedTopics[i]
		stompApi.subscribeTopic(tp)
	  }
      // 更新连接状态：连接成功
      let connectionStatus = constants.STOMP_CONNECTION_STATUS_CONNECTED
      // commit(types.UPDATE_USER_CONNECTION, { connectionStatus }, { root: true })
      uni.$emit(constants.EVENT_BUS_STOMP_CONNECTION_STATUS, connectionStatus)
	  // 长连接成功回调
	  callback()
    }, function (error) {
      stompApi.printLog('连接断开【' + error + '】')
      // 清空订阅topic，以便于重连后添加订阅
      // commit(types.CLEAR_TOPIC, { root: true })
      // 统计连接次数，如果短时间内重试连接次数过多，则说明有可能token过期，需要刷新页面重新登录一下
      if (stompReconnectTimes >= 10) {
        // bus.$emit(constants.EVENT_BUS_LOGOUT, 'logout')
      }
      // 更新连接状态: 断开
      let connectionStatus = constants.STOMP_CONNECTION_STATUS_DISCONNECTED
      // commit(types.UPDATE_USER_CONNECTION, { connectionStatus }, { root: true })
      uni.$emit(constants.EVENT_BUS_STOMP_CONNECTION_STATUS, connectionStatus)
      // 10秒后重新连接，实际效果：每10秒重连一次，直到连接成功
      setTimeout(function () {
        stompApi.printLog('reconnecting...')
        // 增加尝试连接次数
        stompReconnectTimes++
        // 重新连接
        stompConnect(webSocket)
      }, 5000)
    })
  },
  
  // 发送消息
  sendMessage: function(topic, jsonString) {
    // console.log('sendMessage:', topic, jsonString)
    stompClient.send("/app/" + topic, {},
      jsonString
    );
  },

  /**
   * 添加订阅，必须添加前缀 '/topic/'
   * FIXME: 初始登录之后，当需要订阅主题太多的情况容易造成界面卡顿
   * @param topic
   */
  subscribeTopic: function(topic) {
    // stompApi.printLog('subscribeTopic:' + topic)
    // 防止重复订阅
	if (subscribedTopics.includes(topic)) {
		return;
	}
	subscribedTopics.push(topic);
	// 判断是否为空
	if (stompClient === null || stompClient === undefined) {
		return
	}
    // 初始化
	messagesCache[topic] = []
	// 订阅主题
    stompClient.subscribe("/topic/" + topic, function (message) {
      // console.log('message :', message, 'body:', message.body);
      var messageObject = JSON.parse(message.body);
	  if (messageObject.status === 'sending') {
		 messageObject.status = 'stored' 
	  }
	  uni.$emit('message', messageObject);
	  // TODO: 缓存消息
	  let messageArray = messagesCache[topic]
	  messageArray.push(messageObject)
	  messagesCache[topic] = messageArray
	  // TODO: 发送送达回执
	  stompApi.sendReceiptMessage(messageObject)
    });
  },

  /**
   * 取消订阅，必须添加前缀 '/topic/'
   * https://stomp-js.github.io/stomp-websocket/codo/class/Client.html#unsubscribe-dynamic
   * @param topic
   */
  unsubscribeTopic: function(topic) {
    stompClient.unsubscribe('/topic/' + topic)
  },

  /**
   * 断开stomp连接
   * https://stomp-js.github.io/stomp-websocket/codo/extra/docs-src/Usage.md.html
   */
  stompDisconnect: function() {
    stompClient.disconnect(function () {
      stompApi.printLog('stomp disconnect')
    })
  },
  
  // 判断stomp是否已连接
  isConnected: function () {
	  return socketConnected;
  },
  
  // 获取本地缓存聊天记录
  getCacheMessages: function (topic) {
	  return messagesCache[topic]
  },
  
  guid: function() {
	  function s4 () {
	  	return Math.floor((1 + Math.random()) * 0x10000)
	  		.toString(16)
	  		.substring(1)
	  }
	  let timestamp = moment(new Date()).format('YYYYMMDDHHmmss'); 
	  return timestamp + s4() + s4() + s4() + s4() + s4() + s4() + s4() + s4()
  },
  currentTimestamp: function() {
  	return moment().format('YYYY-MM-DD HH:mm:ss')
  },
  
  //
  sendReceiptMessage: function(messageObject) {
	  if (messageObject.user.uid !== this.uid 
		&& messageObject.type !== 'robot' 
		&& messageObject.type !== "robot_result"
		&& messageObject.type !== 'notification_preview'
		&& messageObject.type !== 'notification_receipt'
		&& messageObject.type !== 'notification_recall'
		&& messageObject.type !== 'notification_form_request'
		&& messageObject.type !== 'notification_form_result'
		&& messageObject.type !== 'notification_connect'
		&& messageObject.type !== 'notification_disconnect') {
		//
		let topic = messageObject.thread.topic.replace(/\//g, ".");
		var json = {
			"mid": stompApi.guid(),
			"timestamp": stompApi.currentTimestamp(),
			"client": constants.client,
			"version": "1",
			"type": "notification_receipt",
			"status": constants.MESSAGE_STATUS_SENDING,
			"user": {
				"uid": uid,
				"username": username,
				"nickname": nickname,
				"avatar": avatar,
				"extra": {
					"agent": false
				}
			},
			"receipt": {
				"mid": messageObject.mid,
				"status": constants.MESSAGE_STATUS_RECEIVED
			},
			"thread": {
				"tid": messageObject.thread.tid,
				"type": messageObject.thread.type,
				// "content": content,
				"nickname": nickname,
				"avatar": avatar,
				"topic": topic,
				"client": constants.client,
				"timestamp": stompApi.currentTimestamp(),
				"unreadCount": 0
			}
		};
		stompApi.sendMessage(topic, JSON.stringify(json));
	  }
  },
  
  // 打印log
  printLog: function(content) {
	  if (!constants.IS_PRODUCTION) {
		  console.log(content)
	  }
  }

}

module.exports = {
  connect: stompApi.connect,
  subscribeTopic: stompApi.subscribeTopic,
  sendMessage: stompApi.sendMessage,
  isConnected: stompApi.isConnected,
  getCacheMessages: stompApi.getCacheMessages
}
