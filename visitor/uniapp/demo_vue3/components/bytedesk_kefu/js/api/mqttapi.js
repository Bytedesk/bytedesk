// https://github.com/mqttjs/MQTT.js/issues/1269
import * as constants from '../constants.js'
import * as mqtt from 'mqtt/dist/mqtt.min'
import { default as messageProto } from './protobuf/message_pb.js'

let mqttClient;
let mqttReconnectTimes = 0;
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

export function connect (thread, callback) {
    printLog('connect mqtt')
	currentThread = thread;
	mqttConnect(callback)
}

/**
* mqtt长连接
*/
export function mqttConnect (callback) {
	// 
	const uid = uni.getStorageSync(constants.uid);
	const username = uni.getStorageSync(constants.username);
	let clientId = uid + '/' + constants.client
	if (constants.IS_PRODUCTION) {
		// WSS
		var options = {
		  username: username,
		  password: '',
		  keepalive: 60, // seconds, set to 0 to disable
		  clientId: clientId,
		  protocolId: 'MQTT',
		  protocolVersion: 4,
		  clean: true,
		  reconnectPeriod: 1000, // milliseconds, interval between two reconnections. Disable auto reconnect by setting to 0.
		  connectTimeout: 10 * 1000, // milliseconds, time to wait before a CONNACK is received
		  reschedulePings: true,
		  rejectUnauthorized: false
		}
		mqttClient = mqtt.connect(constants.MQTT_HOST, options)
	} else {
		// WS连接
		mqttClient = mqtt.connect({
		  username: username,
		  password: '', // TODO: 填写真正密码
		  keepalive: 60, // seconds, set to 0 to disable
		  clientId: clientId,
		  protocolId: 'MQTT',
		  protocolVersion: 4,
		  clean: true,
		  reconnectPeriod: 1000, // milliseconds, interval between two reconnections. Disable auto reconnect by setting to 0.
		  connectTimeout: 10 * 1000, // milliseconds, time to wait before a CONNACK is received
		  port: constants.MQTT_PORT, // 通过websocket连接mqtt
		  host: constants.MQTT_WS_HOST,
		  path: '/websocket',
		  reschedulePings: true,
		  rejectUnauthorized: false
		})
	}
    printLog('mqtt connecting...')
	// 更新连接状态：连接中...
    let connectionStatus = constants.STOMP_CONNECTION_STATUS_CONNECTING
    uni.$emit(constants.EVENT_BUS_STOMP_CONNECTION_STATUS, connectionStatus)
    // to disable logging, set it to an empty function:
    mqttClient.on('connect', function () {
      printLog('connected');
	  socketConnected = true;
      // TODO: 断开重连成功之后，需要重新从服务器请求thread，然后添加sub订阅
      // 登录成功之后，尝试登录次数清零
      mqttReconnectTimes = 0
      // 更新连接状态：连接成功
      let connectionStatus = constants.STOMP_CONNECTION_STATUS_CONNECTED
      uni.$emit(constants.EVENT_BUS_STOMP_CONNECTION_STATUS, connectionStatus)
	  // 长连接成功回调
	  callback()
    }, function (error) {
      printLog('连接断开【' + error + '】')
	  socketConnected = false;
      // 清空订阅topic，以便于重连后添加订阅
      // commit(types.CLEAR_TOPIC, { root: true })
      // 统计连接次数，如果短时间内重试连接次数过多，则说明有可能token过期，需要刷新页面重新登录一下
      if (mqttReconnectTimes >= 10) {
        // bus.$emit(constants.EVENT_BUS_LOGOUT, 'logout')
      }
      // 更新连接状态: 断开
      let connectionStatus = constants.STOMP_CONNECTION_STATUS_DISCONNECTED
      uni.$emit(constants.EVENT_BUS_STOMP_CONNECTION_STATUS, connectionStatus)
      // 10秒后重新连接，实际效果：每10秒重连一次，直到连接成功
      setTimeout(function () {
        printLog('reconnecting...')
        // 增加尝试连接次数
        mqttReconnectTimes++
        // 重新连接
        mqttConnect()
      }, 5000)
    })
	//
	mqttClient.on('message', function (topic, messageBinary) {
		let messageProtobuf = messageProto.Message.deserializeBinary(messageBinary)
		console.log('on message:', topic)
		//
		let uid = messageProtobuf.getUser().getUid()
		// console.log('uid:', uid)
		let username = messageProtobuf.getUser().getUsername()
		let nickname = messageProtobuf.getUser().getNickname()
		let avatar = messageProtobuf.getUser().getAvatar()
		//
		let mid = messageProtobuf.getMid()
		// 判断是否加密
		let content = ''
		let type = messageProtobuf.getType()
		let timestamp = messageProtobuf.getTimestamp()
		let client = messageProtobuf.getClient()
		//
		var message = {
			mid: mid,
			timestamp: timestamp,
			client: client,
			version: "1",
			type: "text",
			status: constants.MESSAGE_STATUS_STORED,
			user: {
				uid: uid,
				username: username,
				nickname: nickname,
				avatar: avatar,
			},
			text: {
				content: ''
			},
			image: {
				imageUrl: ''
			},
			file: {
				fileUrl: ''
			},
			voice: {
				voiceUrl: '',
				length: 0,
				format: ''
			},
			video: {
				videoOrShortUrl: ''
			},
			thread: {
				tid: messageProtobuf.getThread().getTid(),
				type: messageProtobuf.getThread().getType(),
				content: messageProtobuf.getThread().getContent(),
				nickname: messageProtobuf.getThread().getNickname(),
				avatar: messageProtobuf.getThread().getAvatar(),
				topic: messageProtobuf.getThread().getTopic(),
				client: messageProtobuf.getThread().getClient(),
				timestamp: timestamp,
				unreadCount: 0
			}
		};
		// 是否发送消息回执
		let sendReceipt = false
		switch (type) {
		  //
		  case constants.MESSAGE_TYPE_TEXT: {
			console.log('text:', messageProtobuf.getText())
			// autoReply = true
			sendReceipt = true
			message.text.content = messageProtobuf.getText().array[0]
			break
		  }
		  case constants.MESSAGE_TYPE_IMAGE: {
			console.log('image:', messageProtobuf.getImage())
			// autoReply = true
			sendReceipt = true
			message.image.imageUrl = messageProtobuf.getImage().array[2]
			break
		  }
		  case constants.MESSAGE_TYPE_VOICE: {
			console.log('voice:', messageProtobuf.getVoice())
			// autoReply = true
			sendReceipt = true
			message.voice.voiceUrl = messageProtobuf.getVoice().array[2]
			message.voice.length = messageProtobuf.getVoice().array[3]
			break
		  }
		  case constants.MESSAGE_TYPE_VIDEO:
		  case constants.MESSAGE_TYPE_SHORT_VIDEO: {
			console.log('video:', messageProtobuf.getVideo())
			sendReceipt = true
			message.video.videoOrShortUrl = messageProtobuf.getVideo().array[2]
			break
		  }
		  case constants.MESSAGE_TYPE_FILE: {
			console.log('file:', messageProtobuf.getFile())
			sendReceipt = true
			message.file.fileUrl = messageProtobuf.getFile().array[0]
			break
		  }
		  case constants.MESSAGE_TYPE_NOTIFICATION_AGENT_CLOSE:
		  case constants.MESSAGE_TYPE_NOTIFICATION_AUTO_CLOSE: {
			// isCloseThread = true
			message.content = messageProtobuf.getText().array[0]
			break
		  }
		  case constants.MESSAGE_TYPE_NOTIFICATION_PREVIEW: {
			  // TODO:
			  let previewContent = messageProtobuf.getPreview().array[0]
			  // console.log('消息预知:', previewContent)
			  // 首先过滤掉自己发送的
			 //  if (uid !== rootState.user.info.uid) {
				// let previewMessage = {
				//   content: previewContent === undefined ? '' : previewContent,
				//   thread: {
				// 	tid: messageProtobuf.getThread().getTid()
				//   }
				// }
				// bus.$emit(constants.EVENT_BUS_MESSAGE_PREVIEW, previewMessage)
			 //  }
			  return
			}
			case constants.MESSAGE_TYPE_NOTIFICATION_RECEIPT: {
			  // TODO:
			  let mid = messageProtobuf.getReceipt().array[0]
			  let status = messageProtobuf.getReceipt().array[1]
			  // console.log('消息回执：送达/已读', mid, status)
			 //  if (uid !== rootState.user.info.uid) {
				// // 更新本地消息状态
				// commit(types.UPDATE_USER_MESSAGE, { mid, status }, { root: true })
			 //  }
			  return
			}
			case constants.MESSAGE_TYPE_NOTIFICATION_RECALL: {
			  // TODO: 从聊天记录中删除
			  let mid = messageProtobuf.getRecall().array[0]
			  // console.log('消息撤回', mid)
			  // commit(types.DELETE_USER_MESSAGE, { mid }, { root: true })
			  return
			}
			case constants.MESSAGE_TYPE_NOTIFICATION_FORM_REQUEST: {
			  // 表单消息
			  let formContent = messageProtobuf.getExtra().getContent()
			  // console.log('notification_form_request', formContent)
			  // let formContentObject = JSON.parse(formContent)
			  // 发送表单请求
			  message.content = formContent
			  break
			}
		  default:
			console.log('other message type ', type)
		}
		console.log('message:', message)
		//
		uni.$emit('message', message);
		// // 发送送达回执
		// stompApi.sendReceiptMessage(messageObject)
		// // 缓存消息
		// let messageArray = messagesCache[topic]
		// messageArray.push(messageObject)
		// messagesCache[topic] = messageArray
	});
	//
	mqttClient.on('close', function () {
		console.log('on close')
	});
	//
	mqttClient.on('disconnect', function () {
		console.log('on disconnect')
	});
	//
	mqttClient.on('offline', function () {
		console.log('on offline')
	});
	//
	mqttClient.on('error', function () {
		console.log('on error')
	});
	//
	mqttClient.on('reconnect', function () {
		console.log('on reconnect')
	});
}
  
// 发送消息
export function sendMessage(topic, jsonString) {
    console.log('sendMessage:', topic, jsonString)
	//
 //    var messageBytes = message.serializeBinary()
	// mqttClient.publish(topic, messageBytes, {
	// 	qos: 1,
	// 	retain: true
	// })
}

/**
 * 
* @param topic
*/
export function subscribeTopic(topic) {
    printLog('subscribeTopic:' + topic)
	// 判断是否为空
	if (mqttClient === null || mqttClient === undefined) {
		return
	}
	// 订阅主题
    mqttClient.subscribe(topic, { qos: 0 });
}

/**
* @param topic
*/
export function unsubscribeTopic(topic) {
    mqttClient.unsubscribe(topic)
}

/**
* 断开mqtt连接
*/
export function mqttDisconnect() {
    if (mqttClient) {
        mqttClient.end()
    }
}
  
// 判断mqtt是否已连接
export function isConnected () {
	return socketConnected;
}

// 获取本地缓存聊天记录
export function getCacheMessages (topic) {
	return messagesCache[topic]
}

// 打印log
export function printLog(content) {
    // if (!constants.IS_PRODUCTION) {
	  console.log(content)
    // }
}
