<template>
	<view>
		<view class="content" @touchstart="hideDrawer">
			<scroll-view class="msg-list" scroll-y="true" :scroll-with-animation="scrollAnimation" :scroll-top="scrollTop" :scroll-into-view="scrollToView" @scrolltoupper="loadMoreMessages" upper-threshold="50">
				<!-- 加载历史数据 waitingUI -->
				<view class="loading" v-if="isHistoryLoading">
					<view class="spinner">
						<view class="rect1"></view>
						<view class="rect2"></view>
						<view class="rect3"></view>
						<view class="rect4"></view>
						<view class="rect5"></view>
					</view>
				</view>
				<view class="row" v-for="(message, index) in messages" :key="index" :id="'msg'+message.mid">
					<!-- 时间戳 -->
					<view class="system">
						<view class="text"> {{ message.createdAt }}</view>
					</view>
					<!-- 商品消息 -->
					<view v-if="is_type_commodity(message)" id="goods" class="goods-info" @click="commodityCallback(message)">
						<view class="goods-pic">
							<image id="goods-pic" alt="" style="width: 100rpx; height: 100rpx;" width="50px" height="50px" :src="jsonObject(message.content).imageUrl"></image>
						</view>
						<view class="goods-desc">
							<view id="goods-name" class="goods-name">{{ jsonObject(message.content).title }}</view>
							<view class="goods-more">
								<view id="goods-price" class="goods-price">￥{{ jsonObject(message.content).price }}</view>
								<view id="goods-sendlink" class="goods-sendlink" @click="sendCommodityMessageSync()">发送商品</view>
							</view>
						</view>
					</view>
					<!-- 系统消息 -->
					<view class="system">
						<view class="text" v-if="is_type_notification_agent_close(message)">{{ message.content }}</view>
						<view class="text" v-else-if="is_type_notification_visitor_close(message)">{{ message.content }}</view>
						<view class="text" v-else-if="is_type_notification_auto_close(message)">{{ message.content }}</view>
						<view class="text" v-else-if="is_type_notification_thread_reentry(message)">{{ message.content }}</view>
						<view class="text" v-else-if="is_type_notification_offline(message)">{{ message.content }}</view>
						<view class="text" v-else-if="is_type_notification_invite_rate(message)">{{ message.content }}</view>
						<view class="text" v-else-if="is_type_notification_rate_result(message)">{{ message.content }}</view>
						<view class="text" v-else-if="is_type_notification_queue_accept(message)">接入队列会话</view>
						<view class="text" v-else-if="is_type_notification(message)">{{ message.content }}</view>
					</view>
					<!-- 用户消息 -->
					<block v-if="!is_type_notification(message) && !is_type_commodity(message)">
						<!-- 自己发出的消息 -->
						<view class="my" v-if="is_self(message)">
							<!-- 左-消息 -->
							<view class="left">
								<!-- 发送状态 -->
								<view class="status">
									<view>{{ formatStatus(message.status) }}</view>
								</view>
								<!-- 文字消息 -->
								<view v-if="is_type_text(message)" class="bubble" @longtap="longtap(message)">
									<rich-text :nodes="message.content"></rich-text>
								</view>
								<!-- 事件消息 -->
								<view v-if="is_type_event(message)" class="bubble">
									<rich-text :nodes="message.content"></rich-text>
								</view>
								<!-- 语言消息 -->
								<view v-if="message.type=='voice'" class="bubble voice" @tap="playVoice(message)" :class="playMsgid == message.mid ? 'play' : ''">
									<view class="length">{{message.length}}</view>
									<view class="icon my-voice"></view>
								</view>
								<!-- 图片消息 -->
								<view v-if="is_type_image(message)" class="bubble img" @tap="previewImageMessage(message)">
									<image :src="message.imageUrl"></image>
								</view>
								<!-- 机器人消息 -->
								<view v-if="is_type_robot(message)" class="bubble">
									<rich-text :nodes="message.content"></rich-text>
								</view>
							</view>
							<!-- 右-头像 -->
							<view class="right">
								<image :src="message.user.avatar"></image>
							</view>
						</view>
						<!-- 收到别人的消息 -->
						<view class="other" v-if="!is_self(message)">
							<!-- 左-头像 -->
							<view class="left">
								<image :src="message.user.avatar"></image>
							</view>
							<!-- 右-用户名称-时间-消息 -->
							<view class="right">
								<view class="username">
									<view class="name">{{ message.user.nickname }}</view> 
								</view>
								<!-- 文字消息 -->
								<view v-if="is_type_text(message)" class="bubble" @longtap="longtap(message)">
									<rich-text :nodes="replaceFace(message.content)"></rich-text>
								</view>
								<!-- 事件消息 -->
								<view v-if="is_type_event(message)" class="bubble">
									<rich-text :nodes="message.content"></rich-text>
								</view>
								<!-- 语音消息 -->
								<view v-if="is_type_voice(message)" class="bubble voice" @tap="playVoice(message)" :class="playMsgid === message.mid ? 'play': ''">
									<view class="icon other-voice"></view>
									<view class="length">{{ message.length }}</view>
								</view>
								<!-- 图片消息 -->
								<view v-if="is_type_image(message)" class="bubble img" @tap="previewImageMessage(message)">
									<image :src="message.imageUrl"></image>
								</view>
								<view v-if="is_type_robot(message)" class="bubble">
									<!-- <rich-text :nodes="message.content"></rich-text> -->
									<view class="flex-column-start" style="color: #2fa39b;">
										<rich-text :nodes="message.content" style="color: black;font-size: 25rpx;cmargin-top: 20rpx;"></rich-text>
										<view class="flex-row-start  padding-top-sm" v-for="(item, index) in message.answers" :key="index">
											<text @click="queryAnswer(item)" style="margin-top: 20rpx;">
												{{item.question}}
											</text>
										</view>
										<!-- TODO: 首先选择是否有帮助，如果用户选择没有帮助，则出现‘人工客服’按钮-->
										<view class="flex-row-start  padding-top-sm">
											<text class="my-neirong-sm">没有你要的答案?</text>
											<text class="padding-left" style="color: #007AFF;" @click="requestAgent()">人工客服</text>
										</view>
									</view>
								</view>
							</view>
						</view>
					</block>
				</view>
			</scroll-view>
		</view>
		<!-- 抽屉栏 -->
		<view class="popup-layer" :class="popupLayerClass" @touchmove.stop.prevent="discard">
			<!-- 表情 --> 
			<!-- <swiper class="emoji-swiper" :class="{hidden:hideEmoji}" indicator-dots="true" duration="150">
				<swiper-item v-for="(page,pid) in emojiList" :key="pid">
					<view v-for="(em,eid) in page" :key="eid" @tap="addEmoji(em)">
						<image mode="widthFix" :src="'/image/emoji/'+em.url"></image>
					</view>
				</swiper-item>
			</swiper> -->
			<!-- 更多功能 相册-拍照 -->
			<view class="more-layer" :class="{hidden:hideMore}">
				<view class="list">
					<view class="box" @tap="chooseImage"><view class="icon tupian2"></view></view>
					<view class="box" @tap="camera"><view class="icon paizhao"></view></view>
				</view>
			</view>
		</view>
		<!-- 底部输入栏 -->
		<view class="input-box" :class="popupLayerClass" @touchmove.stop.prevent="discard">
			<!-- H5下不能录音，输入栏布局改动一下 -->
			<!-- #ifndef H5 -->
			<view class="voice">
				<view class="icon" :class="isVoice?'jianpan':'yuyin'" @tap="switchVoice"></view>
			</view>
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<view class="more" @tap="showMore">
				<view class="icon add"></view>
			</view>
			<!-- #endif -->
			<view class="textbox">
				<view class="voice-mode" :class="[isVoice?'':'hidden',recording?'recording':'']" @touchstart="voiceBegin" @touchmove.stop.prevent="voiceIng" @touchend="voiceEnd" @touchcancel="voiceCancel">{{voiceTis}}</view>
				<view class="text-mode"  :class="isVoice?'hidden':''">
					<view class="box">
						<textarea auto-height="true" v-model="inputContent" @focus="textareaFocus"/>
					</view>
<!-- 					<view class="em" @tap="chooseEmoji">
						<view class="icon biaoqing"></view>
					</view> -->
				</view>
			</view>
			<!-- #ifndef H5 -->
			<view class="more" @tap="showMore">
				<view class="icon add"></view>
			</view>
			<!-- #endif -->
			<view class="send" :class="isVoice?'hidden':''" @tap="sendTextMessage()">
				<view class="btn">发送</view>
			</view>
		</view>
		<!-- 录音UI效果 -->
		<view class="record" :class="recording?'':'hidden'">
			<view class="ing" :class="willStop?'hidden':''"><view class="icon luyin2" ></view></view>
			<view class="cancel" :class="willStop?'':'hidden'"><view class="icon chehui" ></view></view>
			<view class="tis" :class="willStop?'change':''">{{recordTis}}</view>
		</view>
	</view>
</template>

<script>
// import { mapActions, mapGetters } from 'vuex'
import * as constants from '@/components/bytedesk_kefu/js/constants.js'
import * as httpApi from '@/components/bytedesk_kefu/js/api/httpapi.js'
import * as stompApi from '@/components/bytedesk_kefu/js/api/stompapi.js'
import moment from '@/components/bytedesk_kefu/js/api/moment.min.js'
import Vue from 'vue'
// 
export default {
	data() {
		return {
			//消息列表
			isHistoryLoading:false,
			scrollAnimation:false,
			scrollTop:0,
			scrollToView:'',
			
			//录音相关参数
			// #ifndef H5
			//H5不能录音
			RECORDER:uni.getRecorderManager(),
			// #endif
			isVoice:false,
			voiceTis:'按住 说话',
			recordTis:"手指上滑 取消发送",
			recording:false,
			willStop:false,
			initPoint:{identifier:0,Y:0},
			recordTimer:null,
			recordLength:0,
			
			//播放语音相关参数
			AUDIO:uni.createInnerAudioContext(),
			playMsgid:null,
			VoiceTimer:null,
			// 抽屉参数
			popupLayerClass:'',
			// more参数
			hideMore:true,
			//表情定义
			hideEmoji:true,
			// 萝卜丝
			option: {
				id: '',
				sub: '',
				uid: '',
				wid: '',
				type: '',
				aid: '',
				nickname: '',
				//
				tid: '',
				avatar: '',
				topic: '',
				type: '',
				title: '',
				//
				goods: '0',
				goods_categoryCode: '',
				goods_content: '',
				goods_id: '',
				goods_imageUrl: '',
				goods_price: '',
				goods_title: '',
				goods_url: '',
				//
				history: '1',
				//
				postscript: '',
				agentclient: '0',
				lang: 'cn',
				//
				scan: ''
			},
			isInputingVisible: false,
			localPreviewContent: '',
			//
			imageDialogVisible: false,
			currentImageUrl: '',
			currentVoiceUrl: '',
			// show_emoji: false,
			// emojiBaseUrl: 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/emojis/gif/',
			inputContent: '',
			messages: [],
			loadMoreVisible: true,
			//
			isLogin: false,
			// 留言 + 表单
			realname: '',
			mobile: '',
			email: '',
			age: 0,
			job: '',
			content: '',
			//
			showRealname: false,
			showMobile: false,
			showEmail: false,
			showAge: false,
			showJob: false,
			showContent: false,
			// 仅允许评价一次
			isRated: false,
			// 是否客服邀请评价
			isInviteRate: false,
			// 满意度评分
			rateScore: 5,
			rateValue: '非常满意',
			// 满意度附言
			rateContent: '',
			//
			isLoading: false,
			stompClient: '',
			sessionId: '',
			preSessionId: '',
			browseInviteBIid: '',
			//
			adminUid: '',
			workGroupWid: '',
			subDomain: '',
			// TODO: 区分安卓、ios、小程序等
			client: 'uniapp',
			thread: {
				id: 0,
				tid: '',
				topic: '',
				visitor: {
					uid: '',
					nickname: 'visitor',
					avatar: 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/visitor_default_avatar.png'
				}
			},
			// 已经订阅的topic
			subscribedTopics: [],
			// 加载聊天记录offset
			page: 0,
			// 是否是最后一批聊天记录
			last: false,
			// workGroup/visitor/contact/group
			type: 'workGroup',
			// 指定客服
			agentUid: '',
			uid: '',
			username: '',
			// password: '',
			nickname: '',
			avatar: '',
			// 本地存储access_token的key
			token: 'bd_kfe_token',
			isConnected: false,
			answers: [],
			isRobot: false,
			isThreadStarted: false,
			isThreadClosed: false,
			isManulRequestThread: false,
			// focusStatus: true,
			leaveMessageTip: '',
			loadHistory: '1',
			//
			postscript: '',
			isPostscriptSend: false, // 是否已经发送过附言
			//
			hideNav: false,
			backUrl: '',
			topTip: '',
			showTopTip: false,
			//
			showMessage: true,
			showInputBar: true,
			showLeaveMessage: false,
			showRate: false,
			showForm: false,
			//
			visitorUid: '',
			timer: ''
		};
	},
	onLoad(option) {
		// this.getMsgList();
		//语音自然播放结束
		this.AUDIO.onEnded((res)=>{
			this.playMsgid=null;
		});
		// #ifndef H5
		//录音开始事件
		this.RECORDER.onStart((e)=>{
			this.recordBegin(e);
		})
		//录音结束事件
		this.RECORDER.onStop((e)=>{
			this.recordEnd(e);
		})
		// #endif
		this.option = option
		if (option.history === '0') {
			this.loadHistory = '0'
		}
		//
		let app = this;
		uni.$on('message',function(messageObject) {
			// console.log('uni on message');
			app.onMessageReceived(messageObject)
		})
	},
	onUnload() {
	    // 移除监听事件  
		uni.$off('message'); 
	},
	onShow(){
		this.scrollTop = 9999999;
	},
	onReady () {
		
		// 登录
		uni.setNavigationBarTitle({
		　　title:this.option.title
		})
		if (this.option.agentclient === '1') {
			console.log('客服端打开会话')
			this.thread.tid = this.option.tid
			this.thread.topic = this.option.topic
			this.thread.type = this.option.type
			this.thread.visitor.uid = this.option.uid
			this.thread.visitor.nickname = this.option.nickname
			this.thread.visitor.avatar = this.option.avatar
			//
			// this.visitorUid = this.option.topic.split('/')[1]
			// console.log('visitorUid:', this.visitorUid)
			// this.loadHistoryMessages(this.visitorUid);
			this.loadHistoryMessagesByTopic(this.option.topic)
			// 添加订阅topic
			let topic = this.thread.topic.replace(/\//g, ".");
			stompApi.subscribeTopic(topic)
			//
		} else if (this.option.scan != null &&
			(this.option.scan.startsWith('a') || this.option.scan.startsWith('w'))) {
			console.log('扫小程序码打开')
			// TODO: 调用微信授权登录
			// #ifdef MP-WEIXIN
			// #endif
			// #ifdef MP-QQ
			// #endif
			// TODO: 判断是否已经初始化，如果已经初始化则直接调用接口请求客服，否则首先初始化
			try {
				//
				this.isLogin = uni.getStorageSync(constants.isLogin);
				if (this.isLogin) {
					this.requestThreadScan()
				} else {
					// 获取subDomain，也即企业号：登录后台->客服管理->客服账号->企业号
					let subDomain = 'vip'
					// 登录后台->渠道管理-》uniapp中创建应用获取appkey
					let appKey = 'f4970e52-8cc8-48fd-84f6-82390640549d'
					//
					bytedesk.initWithCallback(subDomain, appKey, function(result){
						console.log('initWithCallback success:', result)
						// TODO: 初始化完毕之后，再调用
						this.requestThreadScan()
					}, function(error) {
						console.log('initWithCallback error:', error)
					});
				}
			} catch (e){
				//TODO handle the exception
			}
		} else {
			// 正常打开
			this.requestThread()
		}
	},
	computed: {
		// ...mapGetters([ 'userInfo' ]),
		threadTopic() {
			return this.thread.topic.replace(/\//g, ".");
		},
	},
	methods:{
		//
		is_self (message) {
			// return message.user.uid === this.uid;
			return message.user.uid === this.my_uid();
		},
		// 发送状态
		is_sending (message) {
			return message.status === 'sending'
		},
		is_stored (message) {
			return message.status === 'stored'
		},
		is_received (message) {
			return message.status === 'received'
		},
		is_error (message) {
			return message.status === 'error'
		},
		is_read (message) {
			return message.status === 'read'
		},
		// 消息类型
		is_type_text (message) {
			return message.type === 'text'
				|| message.type === 'notification_thread'
				|| message.type === 'notification_auto_close'
		},
		is_type_robot (message) {
			return message.type === 'robot'
		},
		is_type_image (message) {
			return message.type === 'image'
		},
		is_type_file (message) {
			return message.type === 'file'
		},
		is_type_voice (message) {
			return message.type === 'voice'
		},
		is_type_video (message) {
		  return message.type === 'video' || message.type === 'shortvideo'
		},
		// is_type_shortvideo (message) {
		//   return message.type === constants.MESSAGE_TYPE_SHORT_VIDEO
		// },
		is_type_location (message) {
		  return message.type === 'location'
		},
		is_type_link (message) {
		  return message.type === 'link'
		},
		is_type_event (message) {
		  return message.type === 'event'
		},
		is_type_custom (message) {
		  return message.type === 'custom'
		},
		is_type_commodity (message) {
			return message.type === 'commodity'
		},
		is_type_questionnaire (message) {
			return message.type === 'questionnaire'
		},
		is_type_company (message) {
			return message.type === 'company'
		},
		is_type_workGroup (message) {
			return message.type === 'workGroup'
		},
		is_type_form_request (message) {
			return message.type === 'notification_form_request'
		},
		is_type_form_result (message) {
			return message.type === 'notification_form_result'
		},
		is_type_thread(message) {
			return message.type === 'notification_thread'
		},
		is_type_notification (message) {
			return message.type !== 'notification_preview'
				&& message.type !== 'notification_thread'
				&& message.type.startsWith('notification')
		},
		is_type_close (message) {
			return message.type === 'notification_auto_close'
			|| message.type === 'notification_agent_close'
		},
		is_type_notification_agent_close(message) {
			return message.type === 'notification_agent_close'
		},
		is_type_notification_visitor_close(message) {
			return message.type === 'notification_visitor_close'
		},
		is_type_notification_auto_close(message) {
			return message.type === 'notification_auto_close'
		},
		is_type_notification_thread_reentry(message) {
			return message.type === 'notification_thread_reentry'
		},
		is_type_notification_connect(message) {
			return message.type === 'notification_connect'
		},
		is_type_notification_disconnect(message) {
			return message.type === 'notification_disconnect'
		},
		is_type_notification_offline(message) {
			return message.type === 'notification_offline'
		},
		is_type_notification_invite_rate(message) {
			return message.type === 'notification_invite_rate'
		},
		is_type_notification_rate_result(message) {
			return message.type === 'notification_rate_result'
		},
		is_type_notification_queue_accept(message) {
		  return message.type === 'notification_queue_accept'
		},
		formatStatus(status) {
			if (status === 'read') {
				return '已读'
			} else if (status === 'received') {
				return '送达'
			} else {
				return ''
			}
		},
		my_uid () {
			// 客服端
			// if (this.option.agentclient === '1') {
			// 	return this.userInfo.uid;
			// }
			return this.uid
		},
		my_nickname () {
			// 客服端
			// if (this.option.agentclient === '1') {
			// 	return this.userInfo.nickname;
			// }
			// 访客端
			if (this.option.nickname) {
				return this.option.nickname
			}
			return this.nickname.trim().length > 0 ? this.nickname : this.thread.visitor.nickname
		},
		thread_nickname () {
			// 访客端
			if (this.option.nickname) {
				return this.option.nickname
			}
			return this.nickname.trim().length > 0 ? this.nickname : this.thread.visitor.nickname
		},
		my_avatar () {
			// 客服端
			// if (this.option.agentclient === '1') {
			// 	return this.userInfo.avatar;
			// }
			// 访客端
			if (this.option.avatar) {
				return this.option.avatar
			}
			return this.avatar.trim().length > 0 ? this.avatar : this.thread.visitor.avatar
		},
		jsonObject (content) {
			// console.log('parse json:', content);
			return JSON.parse(content);
		},
		// 识别链接, FIXME: 对于不带http(s)前缀的url，会被识别为子链接，点击链接无法跳出
		replaceUrl(content) {
			if (!content) {
				return content;
			}
			const urlPattern = /(https?:\/\/|www\.)[a-zA-Z_0-9\-@]+(\.\w[a-zA-Z_0-9\-:]+)+(\/[()~#&\-=?+%/.\w]+)?/g;
			return content.replace(urlPattern, (url) => {
				// console.log('url:', url)
				return `<a href="${url}" target="_blank">${url}</a>`;
			})
		},
		//  在发送信息之后，将输入的内容中属于表情的部分替换成emoji图片标签
		//  再经过v-html 渲染成真正的图片
		replaceFace(content) {
			if (content === null || content === undefined) {
				return ''
			}
			// 识别链接
			let replaceUrl = this.replaceUrl(content)
			//
			var emotionMap = this.emotionMap;
			var reg = /\[[\u4E00-\u9FA5NoOK]+\]/g
			var matchresult = replaceUrl.match(reg)
			var result = replaceUrl
			if (matchresult) {
				for (var i = 0; i < matchresult.length; i++) {
					result = result.replace(matchresult[i], '<img height=\'25px\' width=\'25px\' style=\'margin-bottom:4px;\' src=\'' + this.emotionBaseUrl + emotionMap[matchresult[i]] + '\'>')
				}
			}
			return result
		},
		escapeHTML(content) {
			return content.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
		},
		scrollToBottom () {
			if (this.messages.length > 0) {
				// console.log('scroll to bottom', this.messages.length);
				let lastMessage = this.messages[this.messages.length - 1]
				this.scrollToMessage(lastMessage)
			}
			// 聊天记录滚动到最底部, FIXME: 不起作用？
			// this.$nextTick(function() {
			// 	//进入页面滚动到底部
			// 	this.scrollTop = 9999;
			// 	this.$nextTick(function() {
			// 		this.scrollAnimation = true;
			// 	});
			// });
		},
		scrollToMessage (message) {
			// console.log('scroll to ', message.mid);
			this.$nextTick(function() {
				this.scrollToView = 'msg'+message.mid;//跳转上次的第一行信息位置
				this.$nextTick(function() {
					this.scrollAnimation = true;//恢复滚动动画
				});
			});
		},
		// 请求会话
		requestThread () {
			//
			if (this.option.type === undefined || this.option.type === null) {
				return
			}
			try {
			    //
			    this.uid = uni.getStorageSync(constants.uid)
			    this.username = uni.getStorageSync(constants.username)
			    this.nickname = uni.getStorageSync(constants.nickname)
			    this.avatar = uni.getStorageSync(constants.avatar)
				// console.log('uid 1:', this.uid)
			} catch (error) {
			    console.error('read uid/username error', error)
			}
			// 在请求会话之前加载聊天记录，否则会重复显示最近会话的欢迎语
			// this.loadHistoryMessages(this.uid);
			//
			let app = this
			httpApi.requestThread(this.option.wid, this.option.type, this.option.aid, function(response) {
				console.log('request thread success', app.option.wid, app.option.type, app.option.aid, response)
				//
				app.dealWithThread(response);
			}, function(error) {
				console.log('request thread error', error)
			})
		},
		// 请求人工客服
		requestAgent () {
			//
			if (this.option.type === undefined || this.option.type === null) {
				return
			}
			//
			let app = this
			httpApi.requestAgent(this.option.wid, this.option.type, this.option.aid, function(response) {
				console.log('request agent success', app.option.wid, app.option.type, app.option.aid, response)
				//
				app.dealWithThread(response);
			}, function(error) {
				console.log('request agent error', error)
			})
		},
		// 请求客服会话-扫小程序码
		requestThreadScan () {
			//
			try {
			    //
			    this.uid = uni.getStorageSync(constants.uid)
			    this.username = uni.getStorageSync(constants.username)
			    this.nickname = uni.getStorageSync(constants.nickname)
			    this.avatar = uni.getStorageSync(constants.avatar)
			} catch (error) {
			    console.error('read uid/username error', error)
			}
			// 在请求会话之前加载聊天记录，否则会重复显示最近会话的欢迎语
			// this.loadHistoryMessages(this.uid);
			//
			let app = this
			httpApi.requestThreadScan(this.option.scan, function(response) {
				console.log('request thread scan success', app.option.id, response)
				//
				app.dealWithThread(response);
			}, function(error) {
				console.log('request thread scan error', error)
			})
		},
		loadMoreMessages () {
			// if (this.option.agentclient === '1') {
			// 	this.loadHistoryMessages(this.visitorUid)
			// } else {
			// 	this.loadHistoryMessages(this.uid)
			// }
			this.loadHistoryMessagesByTopic(this.thread.topic)
		},
		// 加载更多聊天记录
		loadHistoryMessages (uid) {
			if (this.isManulRequestThread || this.loadHistory === '0') {
				return;
			}
			if(this.isHistoryLoading){
				return ;
			}
			// TODO: 加载历史聊天记录
			this.isHistoryLoading = true;//参数作为进入请求标识，防止重复请求
			this.scrollAnimation = false;//关闭滑动动画
			let app = this
			httpApi.loadHistoryMessages(uid, this.page, 10, function(response) {
				console.log('loadHistoryMessages: ', response)
				if (response.status_code === 200) {
					for (let i = 0; i < response.data.content.length; i++) {
						const element = response.data.content[i]
						// console.log('element:', element);
						app.messages.unshift(element)
					}
				}
				app.scrollToBottom()
				app.page += 1
				app.isHistoryLoading = false;
			}, function(error) {
				console.log('load history messages error', error)
			})
		},
		loadHistoryMessagesByTopic (topic) {
			//
			if (this.isManulRequestThread || this.loadHistory === '0') {
				return;
			}
			if(this.isHistoryLoading){
				return ;
			}
			// TODO: 加载历史聊天记录
			this.isHistoryLoading = true;//参数作为进入请求标识，防止重复请求
			this.scrollAnimation = false;//关闭滑动动画
			let app = this
			httpApi.loadHistoryMessagesByTopic(topic, this.page, 10, function(response) {
				console.log('loadHistoryMessagesByTopic: ', response)
				//
				if (response.status_code === 200) {
					for (let i = 0; i < response.data.content.length; i++) {
						const element = response.data.content[i]
						// console.log('element:', element);
						app.messages.unshift(element)
					}
				}
				app.scrollToBottom()
				app.page += 1
				app.isHistoryLoading = false;
			}, function(error) {
				console.log('load history messages error', error)
			})
		},
		// 加载最新10条消息，用于定时拉取最新消息
		loadLatestMessage () {
			// 长连接断开的情况拉取
			if (!stompApi.isConnected()) {
				//
				// this.loadHistoryMessagesByTopic(this.thread.topic)
				let app = this
				let count = this.loadHistory ? 10 : 1
				httpApi.loadHistoryMessagesByTopic(this.thread.topic, 0, count, function(response) {
					console.log('loadHistoryMessagesByTopic: ', response)
					//
					if (response.status_code === 200) {
						for (let i = 0; i < response.data.content.length; i++) {
							const element = response.data.content[i]
							// console.log('element:', element);
							// app.messages.unshift(element)
							app.pushToMessageArray(element)
						}
					}
					// app.scrollToBottom()
				}, function(error) {
					console.log('load history messages error', error)
				})
				// let uid = ''
				// if (this.option.agentclient === '1') {
				// 	uid = this.visitorUid
				// } else {
				// 	uid = this.uid
				// }
				// let app = this
				// let count = this.loadHistory ? 10 : 1
				// httpApi.loadHistoryMessages(uid, 0, count, function(response) {
				// 	// console.log('load recent Messages: ', response)
				// 	if (response.status_code === 200) {
				// 		for (let i = 0; i < response.data.content.length; i++) {
				// 			const element = response.data.content[i]
				// 			// console.log('element:', element);
				// 			app.pushToMessageArray(element)
				// 			// app.scrollToBottom()
				// 		}
				// 	}
				// }, function(error) {
				// 	console.log('load recent messages error', error)
				// })
			}
		},
		// 加载从某条消息记录之后的消息
		// loadMessagesFrom (uid, id) {
		// 	let app = this
		// 	httpApi.loadMessagesFrom(uid, id, function(response) {
		// 		// console.log('loadMessagesFrom: ', response)
		// 		if (response.status_code === 200) {
		// 			for (let i = 0; i < response.data.content.length; i++) {
		// 				const element = response.data.content[i]
		// 				// console.log('element:', element);
		// 				// app.messages.unshift(element)
		// 				app.pushToMessageArray(element)
		// 			}
		// 		}
		// 		app.scrollToBottom()
		// 	}, function(error) {
		// 		console.log('load messages from error', error)
		// 	})
		// },
		// 
		dealWithThread (response) {
			// console.log('')
			let message = response.data;
			if (response.status_code === 200) {
				//
				// if (this.isManulRequestThread || this.loadHistory === '0') {
					this.pushToMessageArray(message);
				// }
				// // 1. 保存thread
				this.thread = message.thread;
				// // 设置当前为人工客服
				this.isRobot = false;
				// // 防止会话超时自动关闭，重新标记本地打开会话
				this.isThreadClosed = false;
				// // 显示商品信息
				// this.appendCommodityInfo()
			} else if (response.status_code === 201) {
				// message.content = '继续之前会话';
				// if (this.isManulRequestThread || this.loadHistory === '0') {
					this.pushToMessageArray(message);
				// }
				// // 1. 保存thread
				this.thread = message.thread;
				// // 设置当前为人工客服
				this.isRobot = false;
				// // 防止会话超时自动关闭，重新标记本地打开会话
				this.isThreadClosed = false;
				// // 显示商品信息
				// this.appendCommodityInfo()
			} else if (response.status_code === 202) {
				// 排队
				this.pushToMessageArray(message);
				// // 1. 保存thread
				this.thread = message.thread;
				//
			} else if (response.status_code === 203) {
				// 当前非工作时间，请自助查询或留言
				this.pushToMessageArray(message);
		// 		this.leaveMessageTip = message.content;
		// 		// 1. 保存thread
				this.thread = message.thread;
		// 		// 显示留言界面
		// 		this.switchLeaveMessage();
			} else if (response.status_code === 204) {
				// 当前无客服在线，请自助查询或留言
				this.pushToMessageArray(message);
				// this.leaveMessageTip = message.content;
				// // 1. 保存thread
				this.thread = message.thread;
				// // 显示留言界面
				// this.switchLeaveMessage();
			} else if (response.status_code === 205) {
				// 插入业务路由，相当于咨询前提问问卷（选择 或 填写表单）
				this.pushToMessageArray(message);
				// // 1. 保存thread
				this.thread = message.thread;
			} else if (response.status_code === 206) {
				// 返回机器人初始欢迎语 + 欢迎问题列表
				// if (this.isManulRequestThread || this.loadHistory === '0') {
				// 	this.pushToMessageArray(message);
				// }
				// 1. 保存thread
				// this.thread = message.thread;
				// 返回机器人初始欢迎语 + 欢迎问题列表
				this.pushToMessageArray(message);
				// 1. 保存thread
				this.thread = message.thread;
				// 2. 设置当前状态为机器人问答
				this.isRobot = true;
			} else if (response.status_code === -1) {
				this.login();
			} else if (response.status_code === -2) {
				// sid 或 wid 错误
				// this.$message.error('siteId或者工作组id错误');
			} else if (response.status_code === -3) {
				// alert('您已经被禁言')
			}
			this.scrollToBottom();
			// // 建立长连接
			this.byteDeskConnect();
		},
		appendCommodityInfo () {
			let goods = this.option.goods
			if (goods !== "1") {
				return
			}
			let jsonContent = this.commodityInfo();
			// 发送商品信息
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'commodity',
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": false
					}
				},
				"content": jsonContent,
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					"content": "[商品]",
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.pushToMessageArray(json)
		},
		commodityInfo () {
			//
			let commodidy = {
				"id": this.option.goods_id,
				"title": this.option.goods_title,
				"content":this.option.goods_content,
				"price": this.option.goods_price,
				"url": this.option.goods_url,
				"imageUrl": this.option.goods_imageUrl,
				"categoryCode": this.option.goods_categoryCode,
				"type":"commodity"
			}
			return JSON.stringify(commodidy)
		},
		onInputChange (content) {
			console.log('onInputChange:', content);
			// if (this.isRobot || this.isThreadClosed) {
			// 	return;
			// }
			// this.localPreviewContent = content
			// this.delaySendPreviewMessage()
		},
		sendTextMessageSync(content) {
			// this.sendMessageSync('text', content)
			if (content.length === 0) {
				// alert('消息不能为空');
				uni.showToast({ title: this.$t('msgshouldnotempty'), icon:'none', duration: 2000 });
				return;
			}
			if (content.length >= 500) {
				// alert('消息长度太长，请分多次发送');
				// alert(this.$t('msgtoolong'))
				uni.showToast({ title: this.$t('msgtoolong'), icon:'none', duration: 2000 });
				return;
			}
			if (this.thread.tid === '') {
				uni.showToast({ title: this.$t('pleaseWait'), icon:'none', duration: 2000 });
				return;
			}
			//
			let localId = this.guid();
			var json = {
				"mid": localId,
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'text',
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": false
					}
				},
				"text": {
					"content": content
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					"content": content,
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.doSendMessage(json);
		},
		sendImageMessageSync(imageUrl) {
			// console.log('sendImageMessageSync:', imageUrl);
			//
			let localId = this.guid();
			var json = {
				"mid": localId,
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'image',
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": false
					}
				},
				"image": {
					"imageUrl": imageUrl
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					"content": "[图片]",
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.doSendMessage(json);
		},
		sendVoiceMessageSync(voiceUrl, length, format) {
			// console.log('sendVoiceMessageSync:', voiceUrl);
			//
			let localId = this.guid();
			var json = {
				"mid": localId,
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'voice',
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": false
					}
				},
				"voice": {
					"voiceUrl": voiceUrl,
					"length": length,
					"format": format,
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					"content": "[语音]",
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.doSendMessage(json);
		},
		sendVideoMessageSync(videoUrl) {
			// console.log('sendVideoMessageSync:', videoUrl);
			//
			let localId = this.guid();
			var json = {
				"mid": localId,
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'video',
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": false
					}
				},
				"video": {
					"videoOrShortUrl": videoUrl
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					"content": "[视频]",
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.doSendMessage(json);
		},
		sendCommodityMessageSync() {
			let goods = this.option.goods
			if (goods !== "1") {
				return
			}
			let jsonContent = this.commodityInfo();
			// 发送商品信息
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'commodity',
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": false
					}
				},
				"text": {
					"content": jsonContent
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					"content": "[商品]",
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.doSendMessage(json);
		},
		sendPreviewMessage() {
			//
			var localId = this.guid();
			var json = {
				"mid": localId,
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": "notification_preview",
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": false
					}
				},
				"preview": {
					"content": this.localPreviewContent === undefined ? " " : this.localPreviewContent
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					// TODO: 根据内容类型设置不同, 如: [图片]
					"content": this.localPreviewContent,
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.doSendMessage(json);
		},
		sendReceiptMessage (mid, status) {
			var localId = this.guid();
			var json = {
				"mid": localId,
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": "notification_receipt",
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": false
					}
				},
				"receipt": {
					"mid": mid,
					"status": status
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					// "content": content,
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.doSendMessage(json);
		},
		sendRecallMessage (mid) {
			var localId = this.guid();
			var json = {
				"mid": localId,
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": "notification_recall",
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": false
					}
				},
				"recall": {
					"mid": mid
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					// "content": content,
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.doSendMessage(json);
		},
		sendTextMessage () {
			//
			if (this.inputContent.trim().length === 0) {
				return;
			}
			if (this.isRobot) {
				this.messageAnswer(this.inputContent);
			} else {
				// 发送/广播会话消息
				this.sendTextMessageSync(this.inputContent)
			}
			// 清空输入框
			this.inputContent = "";
			// 设置焦点
			// setTimeout(function(){
			// 	$("input")[1].focus()
			// }, 100);
		},
		doSendMessage (json) {
			if (stompApi.isConnected()) {
				stompApi.sendMessage(this.threadTopic, JSON.stringify(json));
			} else {
				httpApi.sendMessageRest(JSON.stringify(json), function(json) {
					// console.log('sendMessageRest success:', json)
				}, function(error) {
					console.log('send message rest error:', error)
				})
			}
			// 先插入本地
			this.onMessageReceived(json)
		},
		pushToMessageArray(message) {
			// 判断是否已经存在
			let contains = false
			for (var i = 0; i < this.messages.length; i++) {
				let msg = this.messages[i]
				if (msg.mid === message.mid) {
					contains = true
				}
			}
			// 如果不存在，则保存
			if (!contains) {
				this.messages.push(message);
				// 排序
				this.messages.sort(function (a, b) {
					if (a.createdAt > b.createdAt) {
					  return 1
					}
					if (a.createdAt < b.createdAt) {
					  return -1
					}
					return 0
				});
			}
		},
		// 建立长连接
		byteDeskConnect () {
			if (stompApi.isConnected()) {
				// 订阅topic
				let topic = this.thread.topic.replace(/\//g, ".");
				stompApi.subscribeTopic(topic);
			} else {
				let app = this
				stompApi.connect(this.thread, function() {
					// 长连接成功回调
					// 发送附言信息
					if (app.option.postscript !== null 
						&& app.option.postscript !== undefined 
						&& app.option.postscript !== '' && !app.isPostscriptSend) {
						app.sendTextMessageSync(app.option.postscript)
						app.isPostscriptSend = true
					}
					// 发送商品信息
					app.sendCommodityMessageSync()
				})
			}
		},
		onMessageReceived (messageObject) {
			// console.log('onMessageReceived:', messageObject)
			//
			if ((messageObject.type === 'text'
			  || messageObject.type === 'image'
			  || messageObject.type === 'file'
			  || messageObject.type === 'voice'
			  || messageObject.type === 'video'
			  || messageObject.type === 'commodity')
			  // && messageObject.user.uid !== this.uid
			  ) {
			  // 新protobuf转换json
			  messageObject.createdAt = messageObject.timestamp;
			  if (messageObject.type === "text") {
			    messageObject.content = messageObject.text.content;
			  } else if (messageObject.type === "image") {
			    messageObject.imageUrl = messageObject.image.imageUrl;
			  } else if (messageObject.type === "file") {
			    messageObject.fileUrl = messageObject.file.fileUrl;
			  } else if (messageObject.type === "voice") {
			    messageObject.voiceUrl = messageObject.voice.voiceUrl;
				messageObject.length = messageObject.voice.length;
			  } else if (messageObject.type === "video") {
			    messageObject.videoOrShortUrl = messageObject.video.videoOrShortUrl;
			  } else if (messageObject.type === "commodity") {
				messageObject.content = messageObject.text.content;
			  }
			  //
			  let mid = messageObject.mid;
			  // this.thread.topic = messageObject.thread.topic;
			  // 非自己发送的消息，发送消息回执: 消息已读
			  // console.log('uid 2:', messageObject.user.uid)
			  // console.log('uid 3:', this.uid)
			  if (messageObject.user.uid !== this.uid) {
				  // console.log('do send receipt');
				  this.sendReceiptMessage(mid, "read");
			  }
			}
			else if (messageObject.type === 'notification_browse_invite') {
			  //
			} else if (messageObject.type === 'notification_queue') {
			    // 排队
				this.isThreadClosed = false;
			} else if (messageObject.type === 'notification_queue_accept') {
				// 接入访客
				messageObject.createdAt = messageObject.timestamp;
				messageObject.content = messageObject.text.content;
			   // 1. 保存thread
			   // this.thread = messageObject.thread;
			   // 2. 订阅会话消息
			   // this.subscribeTopic(this.threadTopic);
			   this.isThreadClosed = false;
			} else if (messageObject.type === 'notification_invite_rate') {
			   // 邀请评价
			   messageObject.createdAt = messageObject.timestamp;
			   messageObject.content = messageObject.extra.content;
			   this.isInviteRate = true;
			   // this.switchRate()
			} else if (messageObject.type === 'notification_rate_result') { 
				// 访客评价结果
				messageObject.createdAt = messageObject.timestamp;
				messageObject.content = messageObject.extra.content;
            } else if (messageObject.type === 'notification_agent_close'
			    || messageObject.type === 'notification_auto_close') {
			  // // 新protobuf转换json
			  messageObject.createdAt = messageObject.timestamp;
			  messageObject.content = messageObject.text.content;
			  // // TODO: 会话关闭，添加按钮方便用户点击重新请求会话
			  this.isThreadClosed = true
			} else if (messageObject.type === 'notification_preview') {
			  //
			  if (!this.is_self(messageObject)) {
			      this.isInputingVisible = true;
			      setTimeout(function () {
			          this.isInputingVisible = false;
			      }, 5000)
			  }
			} else if (messageObject.type === 'notification_receipt') {
			  // 消息状态：送达 received、已读 read
			  if (!this.is_self(messageObject)) {
			    for (let i = this.messages.length - 1; i >= 0 ; i--) {
					const msg = this.messages[i]
					if (msg.mid === messageObject.receipt.mid) {
						// 可更新顺序 read > received > stored > sending, 前面的状态可更新后面的
						if (this.messages[i].status === 'read') {
							return
						}
						console.log('do update:', this.messages[i].mid, this.messages[i].content, messageObject.receipt.status)
						Vue.set(this.messages[i], 'status', messageObject.receipt.status)
					}
				}
			  }
			} else if (messageObject.type === 'notification_recall') {
			  // console.log('消息撤回');
			  for (let i = 0; i < this.messages.length; i++) {
				  const element = this.messages[i];
				  if (element.mid === messageObject.recall.mid) {
					  this.messages.splice(i, 1)
				  }
			  }
			} else if (messageObject.type === 'notification_form_request') {
                // 收到客服端表单请求
                messageObject.content = '表单请求'
                let formContent = messageObject.extra.content
                console.log('form:' + formContent)
                // let formContentObject = JSON.parse(formContent)
                if (formContent.indexOf('姓名') !== -1) {
                    console.log('showRealname')
                    this.showRealname = true
                }
                if (formContent.indexOf('手机') !== -1) {
                    this.showMobile = true
                }
                if (formContent.indexOf('邮箱') !== -1) {
                    this.showEmail = true
                }
                if (formContent.indexOf('年龄') !== -1) {
                    this.showAge = true
                }
                if (formContent.indexOf('职业') !== -1) {
                    this.showJob = true
                }
                // app.switchForm()
            } else if (messageObject.type === 'notification_form_result') {
                // 自己发送的表单结果
                messageObject.content = '发送表单'
            }
			//
			if (messageObject.type !== 'notification_preview'
			    && messageObject.type !== 'notification_receipt'
				&& messageObject.type !== 'notification_recall'
				&& messageObject.type !== 'notification_form_request'
				&& messageObject.type !== 'notification_form_result'
			    && messageObject.type !== 'notification_connect'
			    && messageObject.type !== 'notification_disconnect') {
			    this.isRobot = false;
			    this.pushToMessageArray(messageObject);
			    // this.scrollToBottom()
				this.scrollToMessage(messageObject)
			} else {
			    // TODO: 监听客服端输入状态
			}
		},
		//
		currentTimestamp () {
			return moment().format('YYYY-MM-DD HH:mm:ss')
			// return ''
		},
		///
		// 点击商品回调
		commodityCallback (message) {
			// console.log('commodity:', message)
			uni.$emit('commodity', message.content);
		},
		// 选择图片发送
		chooseImage(){
			this.getImage('album');
		},
		//拍照发送
		camera(){
			this.getImage('camera');
		},
		//选照片 or 拍照
		getImage (type) {
			// 拍照 + 相册
			console.log('take picture');
			let app = this;
			uni.chooseImage({
				count: 1, //默认9
				sourceType:[type],
			    success: (chooseImageRes) => {
			        const tempFilePaths = chooseImageRes.tempFilePaths;
			        uni.uploadFile({
			            url: constants.UPLOAD_IMAGE_URL,
			            filePath: tempFilePaths[0],
			            name: 'file',
			            formData: {
			                'file_name': app.guid(),
							'username': app.username,
							'client': constants.client
			            },
			            success: (response) => {
			                console.log(response.data);
							// 发送图片
							var imageUrl = JSON.parse(response.data).data;
							app.sendImageMessageSync(imageUrl)
			            }
			        });
			    }
			});
		},
		previewImageMessage (message) {
			uni.previewImage({
				urls: [message.imageUrl]
			});
		},
		guid() {
			function s4 () {
				return Math.floor((1 + Math.random()) * 0x10000)
					.toString(16)
					.substring(1)
			}
			return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4()
		},
		queryAnswer (answer) {
			console.log('answer:', answer);
			let app = this
			httpApi.queryAnswer(this.thread.tid, answer.aid, function(response) {
				console.log('queryAnswer success', response)
				//
				if (response.status_code === 200)  {
					//
					let queryMessage = response.data.query;
					let replyMessage = response.data.reply;
					//
					app.pushToMessageArray(queryMessage);
					app.pushToMessageArray(replyMessage);
					//
					// app.scrollToBottom()
					app.scrollToMessage(replyMessage)
				} else {
					// app.$message.warning(response.message)
					uni.showToast({ title: response.message, duration: 2000 });
				}
			}, function(error) {
				console.log('queryAnswer error', error)
			})
		},
		messageAnswer (content) {
			let app = this;
			// 包含’人工‘二字
			if (content.indexOf('人工') !== -1) {
				// 请求人工客服
				app.requestAgent()
				return;
			} 
			httpApi.messageAnswer(this.option.wid, this.option.type, this.option.aid, content, function(response) {
				console.log('messageAnswer success', response)
				if (response.status_code === 200 ||
					response.status_code === 201)  {
					//
					let queryMessage = response.data.query;
					let replyMessage = response.data.reply;
					//
					app.pushToMessageArray(queryMessage);
					app.pushToMessageArray(replyMessage);
					// app.scrollToBottom()
					app.scrollToMessage(replyMessage)
				} else {
					// app.$message.warning(response.data.message)
					uni.showToast({ title: response.message, duration: 2000 });
				}
			}, function(error) {
				console.log('messageAnswer error', error)
			})
		},
		//更多功能(点击+弹出) 
		showMore() {
			this.isVoice = false;
			this.hideEmoji = true;
			if(this.hideMore){
				this.hideMore = false;
				this.openDrawer();
			}else{
				this.hideDrawer();
			}
		},
		// 打开抽屉
		openDrawer(){
			this.popupLayerClass = 'showLayer';
		},
		// 隐藏抽屉
		hideDrawer(){
			this.popupLayerClass = '';
			setTimeout(()=>{
				this.hideMore = true;
				this.hideEmoji = true;
			},150);
		},
		// 选择表情
		// chooseEmoji(){
		// 	this.hideMore = true;
		// 	if(this.hideEmoji){
		// 		this.hideEmoji = false;
		// 		this.openDrawer();
		// 	}else{
		// 		this.hideDrawer();
		// 	}
		// },
		//添加表情
		// addEmoji(em){
		// 	this.inputContent+=em.alt;
		// },
		//获取焦点，如果不是选表情ing,则关闭抽屉
		textareaFocus(){
			if(this.popupLayerClass=='showLayer' && this.hideMore == false){
				this.hideDrawer();
			}
		},
		// 播放语音
		playVoice(message) {
			this.playMsgid = message.mid;
			// this.AUDIO.src = message.voice.voiceUrl;
			this.AUDIO.src = message.voiceUrl;
			this.$nextTick(function() {
				this.AUDIO.play();
			});
		},
		// 录音开始
		voiceBegin(e){
			if(e.touches.length>1){
				return ;
			}
			this.initPoint.Y = e.touches[0].clientY;
			this.initPoint.identifier = e.touches[0].identifier;
			this.RECORDER.start({format:"mp3"});//录音开始,
		},
		//录音开始UI效果
		recordBegin(e){
			this.recording = true;
			this.voiceTis='松开 结束';
			this.recordLength = 0;
			this.recordTimer = setInterval(()=>{
				this.recordLength++;
			},1000)
		},
		// 录音被打断
		voiceCancel(){
			this.recording = false;
			this.voiceTis='按住 说话';
			this.recordTis = '手指上滑 取消发送'
			this.willStop = true;//不发送录音
			this.RECORDER.stop();//录音结束
		},
		// 录音中(判断是否触发上滑取消发送)
		voiceIng(e){
			if(!this.recording){
				return;
			}
			let touche = e.touches[0];
			//上滑一个导航栏的高度触发上滑取消发送
			if(this.initPoint.Y - touche.clientY>=uni.upx2px(100)){
				this.willStop = true;
				this.recordTis = '松开手指 取消发送'
			}else{
				this.willStop = false;
				this.recordTis = '手指上滑 取消发送'
			}
		},
		// 结束录音
		voiceEnd(e){
			if(!this.recording){
				return;
			}
			this.recording = false;
			this.voiceTis='按住 说话';
			this.recordTis = '手指上滑 取消发送'
			this.RECORDER.stop();//录音结束
		},
		//录音结束(回调文件)
		recordEnd(e){
			clearInterval(this.recordTimer);
			if(!this.willStop){
				// e: {"tempFilePath":"_doc/uniapp_temp_1610607301718/recorder/1610607667800.mp3"}
				// console.log("e: " + JSON.stringify(e), ' length:', this.recordLength);
				let app = this;
				uni.uploadFile({
				    url: constants.UPLOAD_VOICE_URL,
				    filePath: e.tempFilePath,
				    name: 'file',
				    formData: {
				        'file_name': app.guid() + ".mp3",
						'username': app.username,
						'client': constants.client
				    },
				    success: (response) => {
				        console.log(response.data);
						// 发送图片
						var voiceUrl = JSON.parse(response.data).data;
						app.sendVoiceMessageSync(voiceUrl, this.recordLength, "mp3");
				    }
				});
				
			}else{
				console.log('取消发送录音');
			}
			this.willStop = false;
		},
		// 切换语音/文字输入
		switchVoice(){
			this.hideDrawer();
			this.isVoice = this.isVoice?false:true;
		},
		discard(){
			return;
		},
		can_recall(message) {
		  return (
			this.callRecallMessage(message) &&
			this.is_self(message)
		  );
		},
		// 检测是否在3分钟之内，允许撤回消息
		callRecallMessage(message) {
		  let now = moment(new Date(), "YYYY-MM-DD HH:mm:ss");
		  let createdAt = moment(message.createdAt, "YYYY-MM-DD HH:mm:ss");
		  let seconds = now.diff(createdAt, "seconds");
		  // 现在距消息发送的时间差
		  // console.log('seconds passed: ', seconds)
		  if (seconds < 180) {
			return true;
		  }
		  return false;
		},
		// 长按复制消息
		longtap (message){
		    console.log('longtap',message);
			let app = this
			if (this.can_recall(message)) {
				uni.showActionSheet({
					title:'选择操作',
					itemList: ['复制', '撤回'],
					success: (e) => {
						console.log(e.tapIndex);
						if (e.tapIndex === 0) {
							// #ifndef H5
							uni.setClipboardData({
							  data: message.content,//要被复制的内容
							  success:() => { //复制成功的回调函数
							    uni.showToast({ //提示
							      title:'复制成功'
							    })
							  }
							});
							// #endif
						} else {
							app.sendRecallMessage(message.mid)
						}
					}
				})
			} else {
				// #ifndef H5
				uni.setClipboardData({
				  data: message.content,//要被复制的内容
				  success:() => { //复制成功的回调函数
				    uni.showToast({ //提示
				      title:'复制成功'
				    })
				  }
				});
				// #endif
			}
		},
		/**
		 * 1. 首先判断是否已经注册过
		 * 2. 如果已经注册过，则直接调用登录接口
		 * 3. 如果没有注册过，则从服务器请求用户名
		 */
		requestUsername () {
			// this.username = localStorage.bd_kfe_username;
			// this.password = this.username;
			if (this.username) {
				// this.login();
			} else {
				//
				// $.ajax({
				// 	url: this.HTTP_HOST + '/visitor/api/username',
				// 	contentType: "application/json; charset=utf-8",
				// 	type: "get",
				// 	data: {
				// 		nickname: this.nickname,
				// 		subDomain: this.subDomain,
				// 		client: this.client
				// 	},
				// 	success: function (response) {
				// 		console.log('user:', response.data);
				// 		// 登录
				// 		app.uid = response.data.uid;
				// 		app.username = response.data.username;
				// 		app.password = app.username;
				// 		app.nickname = response.data.nickname;
				// 		// 本地存储
				// 		localStorage.bd_kfe_uid = app.uid;
				// 		localStorage.bd_kfe_username = app.username;
				// 		// 登录
				// 		app.login();
				// 	},
				// 	error: function (error) {
				// 		//Do Something to handle error
				// 		console.log(error);
				// 	}
				// });
			}
		},
	},
	mounted() {
	  // 如果长连接断开，则定时刷新聊天记录
	  this.timer = setInterval(this.loadLatestMessage, 1000 * 10);
	},
	beforeDestroy() {
	  clearInterval(this.timer);
	}
}
</script>

<style lang="scss">
	@import "css/style.scss";
</style>