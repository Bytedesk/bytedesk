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
									<rich-text :nodes="replaceFace(message.content)"></rich-text>
								</view>
								<!-- 事件消息 -->
								<view v-if="is_type_event(message)" class="bubble">
									<rich-text :nodes="message.content"></rich-text>
								</view>
								<!-- 语音消息 -->
								<view v-if="message.type=='voice'" class="bubble voice" @tap="playVoice(message)" :class="playMsgid == message.mid ? 'play' : ''">
									<view class="length">{{message.length}}</view>
									<view class="icon my-voice"></view>
								</view>
								<!-- 图片消息 -->
								<view v-if="is_type_image(message)" class="bubble img" @tap="previewImageMessage(message)">
									<image :src="message.imageUrl"></image>
								</view>
								<!-- 文件消息 -->
								<div v-if="is_type_file(message)" class="bubble">
								    <view><a :href="message.fileUrl" target="_blank">查看文件</a></view>
								</div>
								<!-- 视频消息 -->
								<div v-if="is_type_video(message)" class="bubble">
								    <view><a :href="message.videoOrShortUrl" target="_blank">查看视频</a></view>
								</div>
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
									<view class="name">{{ message.user.nickname }}[{{ message.client }}]</view> 
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
								<!-- 文件消息 -->
								<div v-if="is_type_file(message)" class="bubble">
								    <view><a :href="message.fileUrl" target="_blank">查看文件</a></view>
								</div>
								<!-- 视频消息 -->
								<div v-if="is_type_video(message)" class="bubble">
								    <view><a :href="message.videoOrShortUrl" target="_blank">查看视频</a></view>
								</div>
								<view v-if="is_type_robot(message)" class="bubble">
									<!-- <rich-text :nodes="message.content"></rich-text> -->
									<view class="flex-column-start" style="color: #2fa39b;">
										<rich-text :nodes="message.content" style="color: black;font-size: 25rpx;cmargin-top: 20rpx;"></rich-text>
										<view class="flex-row-start  padding-top-sm" v-for="(item, index) in message.answers" :key="index">
											<text style="margin-top: 20rpx;">
												{{item.question}}
											</text>
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
			<swiper class="emoji-swiper" :class="{hidden:hideEmoji}" indicator-dots="true" duration="150">
				<swiper-item v-for="(page, pid) in emojis" :key="pid">
					<view v-for="(item, index) in page" :key="index" @tap="addEmoji(item)">
						<image mode="widthFix" :src="emotionUrl(item.file)"></image>
					</view>
				</swiper-item>
			</swiper>
			<!-- 更多功能 相册-拍照 -->
			<view class="more-layer" :class="{hidden:hideMore}">
				<view class="list">
					<view class="box" @tap="chooseImage"><view class="icon tupian2"></view></view>
					<view class="box" @tap="camera"><view class="icon paizhao"></view></view>
					<!-- 拉黑 -->
					<view v-if="isAgentClient" class="box" @tap="addBlock()">拉黑</view>
					<!-- 免打扰 -->
					<view v-if="isAgentClient" class="box" @tap="markNodisturb()">免打扰</view>
					<!-- TODO: 转接会话 -->
					<view v-if="isAgentClient" class="box" @tap="getOnlineAgents()">转接</view>
					<!-- TODO: 邀请评价 -->
					<view v-if="isAgentClient" class="box" @tap="inviteRate()">邀评</view>
					<!-- TODO: 关闭会话 -->
					<view v-if="isAgentClient" class="box" @tap="closeThread()">关闭</view>
				</view>
			</view>
		</view>
		<!-- 底部输入栏 -->
		<view class="input-box" :class="popupLayerClass" @touchmove.stop.prevent="discard">
			<!-- 常用语 -->
			<view class="more" @tap="showCuw">
				<!-- <image style="width: 60rpx; height: 60rpx;" width="30px" height="30px" src="./image/cuw/cuw_normal.png"></image> -->
				<image style="width: 60rpx; height: 60rpx;" width="30px" height="30px" src="/static/img/cuw/cuw_normal.png"></image>
			</view>
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
					<!-- 表情 -->
					<view class="em" @tap="chooseEmoji">
						<view class="icon biaoqing"></view>
					</view>
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
		<!-- 提交信息 -->
		<uni-popup id="dialogInput" ref="dialogInput" type="dialog" @change="blockDialogChange">
			<uni-popup-dialog mode="input" title="添加备注" value="垃圾用户" placeholder="添加备注" @confirm="blockDialogInputConfirm"></uni-popup-dialog>
		</uni-popup>
	</view>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
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
			onlineAgents: [],
			//
			isAgentClient: false,
			visitorUid: '',
			loadHistoryTimer: '',
			sendMessageTimer: '',
			//
			emotionBaseUrl: 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/emojis/gif/',
			// 表情
			emotionMap: {
				'[微笑]': '100.gif',
				'[撇嘴]': '101.gif',
				'[色]': '102.gif',
				'[发呆]': '103.gif',
				'[得意]': '104.gif',
				'[流泪]': '105.gif',
				'[害羞]': '106.gif',
				'[闭嘴]': '107.gif',
				'[睡]': '108.gif',
				'[大哭]': '109.gif',

				'[尴尬]': '110.gif',
				'[发怒]': '111.gif',
				'[调皮]': '112.gif',
				'[呲牙]': '113.gif',
				'[惊讶]': '114.gif',
				'[难过]': '115.gif',
				'[酷]': '116.gif',
				'[冷汗]': '117.gif',
				'[抓狂]': '118.gif',
				'[吐]': '119.gif',

				'[偷笑]': '120.gif',
				'[愉快]': '121.gif',
				'[白眼]': '122.gif',
				'[傲慢]': '123.gif',
				'[饥饿]': '124.gif',
				'[困]': '125.gif',
				'[惊恐]': '126.gif',
				'[流汗]': '127.gif',
				'[憨笑]': '128.gif',
				'[悠闲]': '129.gif',

				'[奋斗]': '130.gif',
				'[咒骂]': '131.gif',
				'[疑问]': '132.gif',
				'[嘘]': '133.gif',
				'[晕]': '134.gif',
				'[疯了]': '135.gif',
				'[衰]': '136.gif',
				'[骷髅]': '137.gif',
				'[敲打]': '138.gif',
				'[再见]': '139.gif',

				'[擦汗]': '140.gif',
				'[抠鼻]': '141.gif',
				'[鼓掌]': '142.gif',
				'[糗大了]': '143.gif',
				'[坏笑]': '144.gif',
				'[左哼哼]': '145.gif',
				'[右哼哼]': '146.gif',
				'[哈欠]': '147.gif',
				'[鄙视]': '148.gif',
				'[委屈]': '149.gif',

				'[快哭]': '150.gif',
				'[阴险]': '151.gif',
				'[亲亲]': '152.gif',
				'[吓]': '153.gif',
				'[可怜]': '154.gif',
				'[菜刀]': '155.gif',
				'[西瓜]': '156.gif',
				'[啤酒]': '157.gif',
				'[篮球]': '158.gif',
				'[乒乓]': '159.gif',

				'[咖啡]': '160.gif',
				'[饭]': '161.gif',
				'[猪头]': '162.gif',
				'[玫瑰]': '163.gif',
				'[凋谢]': '164.gif',
				'[嘴唇]': '165.gif',
				'[爱心]': '166.gif',
				'[心碎]': '167.gif',
				'[蛋糕]': '168.gif',
				'[闪电]': '169.gif',

				'[炸弹]': '170.gif',
				'[刀]': '171.gif',
				'[足球]': '172.gif',
				'[瓢虫]': '173.gif',
				'[便便]': '174.gif',
				'[月亮]': '175.gif',
				'[太阳]': '176.gif',
				'[礼物]': '177.gif',
				'[拥抱]': '178.gif',
				'[强]': '179.gif',

				'[弱]': '180.gif',
				'[握手]': '181.gif',
				'[胜利]': '182.gif',
				'[抱拳]': '183.gif',
				'[勾引]': '184.gif',
				'[拳头]': '185.gif',
				'[差劲]': '186.gif',
				'[爱你]': '187.gif',
				'[No]': '188.gif',
				'[OK]': '189.gif',

				'[爱情]': '190.gif',
				'[飞吻]': '191.gif',
				'[跳跳]': '192.gif',
				'[发抖]': '193.gif',
				'[怄火]': '194.gif',
				'[转圈]': '195.gif',
				'[磕头]': '196.gif',
				'[回头]': '197.gif',
				'[跳绳]': '198.gif',
				'[投降]': '199.gif',

				'[激动]': '201.gif',
				'[乱舞]': '202.gif',
				'[献吻]': '203.gif',
				'[左太极]': '204.gif',
				'[右太极]': '205.gif'
			},
			// emoji表情, code代表来自微信端的表情字符，目前已经在服务器端处理替换为title字段，故code字段暂无用途
			emojis: [
				[
					{ title: '[微笑]', file: '100.gif' },
					{ title: '[撇嘴]', file: '101.gif' },
					{ title: '[色]', file: '102.gif' },
					{ title: '[发呆]', file: '103.gif' },
					{ title: '[得意]', file: '104.gif' },
					{ title: '[流泪]', file: '105.gif' },
					{ title: '[害羞]', file: '106.gif' },
					{ title: '[闭嘴]', file: '107.gif' },
					{ title: '[睡]', file: '108.gif' },
					{ title: '[大哭]', file: '109.gif' },
					{ title: '[尴尬]', file: '110.gif' },
					{ title: '[发怒]', file: '111.gif' },
					{ title: '[调皮]', file: '112.gif' },
					{ title: '[呲牙]', file: '113.gif' },
					{ title: '[惊讶]', file: '114.gif' },
					{ title: '[难过]', file: '115.gif' },
					{ title: '[酷]', file: '116.gif' },
					{ title: '[冷汗]', file: '117.gif' },
					{ title: '[抓狂]', file: '118.gif' },
					{ title: '[吐]', file: '119.gif' },
					{ title: '[偷笑]', file: '120.gif' },
					{ title: '[愉快]', file: '121.gif' },
					{ title: '[白眼]', file: '122.gif' },
					{ title: '[傲慢]', file: '123.gif' },
				],
				
				[
					{ title: '[饥饿]', file: '124.gif' },
					{ title: '[困]', file: '125.gif' },
					{ title: '[惊恐]', file: '126.gif' },
					{ title: '[流汗]', file: '127.gif' },
					{ title: '[憨笑]', file: '128.gif' },
					{ title: '[悠闲]', file: '129.gif' },
					{ title: '[奋斗]', file: '130.gif' },
					{ title: '[咒骂]', file: '131.gif' },
					{ title: '[疑问]', file: '132.gif' },
					{ title: '[嘘]', file: '133.gif' },
					{ title: '[晕]', file: '134.gif' },
					{ title: '[疯了]', file: '135.gif' },
					{ title: '[衰]', file: '136.gif' },
					{ title: '[骷髅]', file: '137.gif' },
					{ title: '[敲打]', file: '138.gif' },
					{ title: '[再见]', file: '139.gif' },
					{ title: '[擦汗]', file: '140.gif' },
					{ title: '[抠鼻]', file: '141.gif' },
					{ title: '[鼓掌]', file: '142.gif' },
					{ title: '[糗大了]', file: '143.gif' },
					{ title: '[坏笑]', file: '144.gif' },
					{ title: '[左哼哼]', file: '145.gif' },
					{ title: '[右哼哼]', file: '146.gif' },
					{ title: '[哈欠]', file: '147.gif' },
				],
				
				[
					{ title: '[鄙视]', file: '148.gif' },
					{ title: '[委屈]', file: '149.gif' },
					{ title: '[快哭]', file: '150.gif' },
					{ title: '[阴险]', file: '151.gif' },
					{ title: '[亲亲]', file: '152.gif' },
					{ title: '[吓]', file: '153.gif' }
				]
			]
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
		uni.$on('cuw', function(cuw) {
			// console.log('send cuw:', cuw)
			if (cuw.type === 'text') {
				app.sendTextMessageSync(cuw.content)
			} else if (cuw.type === 'image') {
				app.sendImageMessageSync(cuw.content)
			} else if (cuw.type === 'file') {
				app.sendFileMessageSync(cuw.content)
			} else if (cuw.type === 'voice') {
				app.sendVoiceMessageSync(cuw.content)
			} else if (cuw.type === 'video') {
				app.sendVideoMessageSync(cuw.content)
			} else {
				uni.showToast({ title: '暂不支持消息类型', icon:'none', duration: 2000 });
			}
		})
	},
	onUnload() {  
	    // 移除监听事件  
		uni.$off('message'); 
		uni.$off('cuw'); 
	},
	onShow(){
		this.scrollTop = 9999999;
	},
	onPullDownRefresh() {
		this.loadMoreMessages()
	},
	onReady () {
		// 登录
		uni.setNavigationBarTitle({
		　　title:this.option.title
		})
		console.log('客服端打开会话')
		this.isAgentClient = true
		this.thread.tid = this.option.tid
		this.thread.topic = this.option.topic
		this.thread.type = this.option.type
		this.thread.visitor.uid = this.option.uid
		this.thread.visitor.nickname = this.option.nickname
		this.thread.visitor.avatar = this.option.avatar
		//
		this.visitorUid = this.option.topic.split('/')[1]
		// console.log('visitorUid:', this.visitorUid)
		this.loadHistoryMessages(this.visitorUid);
		// 添加订阅topic
		let topic = this.thread.topic.replace(/\//g, ".");
		stompApi.subscribeTopic(topic)
		//
	},
	computed: {
		...mapGetters([ 'userInfo' ]),
		threadTopic() {
			return this.thread.topic.replace(/\//g, ".");
		},
	},
	methods:{
		//
		is_self (message) {
			if (message.user == null) {
				return false
			}
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
				return '已读' // 对方已读
			} else if (status === 'received') {
				return '送达' // 已经发送到对方客户端
			} else if (status === 'stored') {
				return '' // 发送到服务器，发送成功
			} else if (status === 'error') {
				return '失败' // 发送失败
			} else if (status === 'sending') {
				return '发送中..' // 发送中
			} else {
				// return ''
				return status
			}
		},
		my_uid () {
			// 客服端
			if (this.option.agentclient === '1') {
				return this.userInfo.uid;
			}
			return this.uid
		},
		my_username () {
			// 客服端
			if (this.option.agentclient === '1') {
				return this.userInfo.username;
			}
			return this.username
		},
		my_nickname () {
			// 客服端
			if (this.option.agentclient === '1') {
				return this.userInfo.nickname;
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
			if (this.option.agentclient === '1') {
				return this.userInfo.avatar;
			}
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
		loadMoreMessages () {
			this.loadHistoryMessages(this.visitorUid)
		},
		// 加载更多聊天记录
		loadHistoryMessages (uid) {
			if (this.isManulRequestThread) {
				console.log('isManulRequestThread')
				return;
			}
			if (this.loadHistory === '0') {
				console.log('this.loadHistory === 0')
				return;
			}
			if(this.isHistoryLoading) {
				console.log('isHistoryLoading')
				return ;
			}
			// TODO: 加载历史聊天记录
			this.isHistoryLoading = true;//参数作为进入请求标识，防止重复请求
			this.scrollAnimation = false;//关闭滑动动画
			let app = this
			httpApi.loadHistoryMessages(uid, app.page, 10, function(response) {
				console.log('loadHistoryMessages: ', app.page, response)
				//
				if (response.status_code === 200) {
					// for (let i = 0; i < response.data.content.length; i++) {
					// 	const element = response.data.content[i]
					// 	app.messages.unshift(element)
					// }
					var length = response.data.content.length
					for (var i = 0; i < length; i++) {
						var message = response.data.content[i];
						if (message.type === 'notification_form_request' ||
						  message.type === 'notification_form_result') {
						  // 暂时忽略表单消息
						} else if (message.type === 'notification_thread_reentry') {
						  // 连续的 ‘继续会话’ 消息，只显示最后一条
						  if (i + 1 < length) {
							var nextmsg = response.data.content[i + 1];
							if (nextmsg.type === 'notification_thread_reentry') {
							  continue
							} else {
							  	app.pushToMessageArray(message);
							}
						  }
						} else {
						  	app.pushToMessageArray(message);
						}
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
				let uid = ''
				if (this.option.agentclient === '1') {
					uid = this.visitorUid
				} else {
					uid = this.uid
				}
				let app = this
				let count = this.loadHistory ? 10 : 1
				httpApi.loadHistoryMessages(uid, 0, count, function(response) {
					console.log('load recent Messages: ', response)
					if (response.status_code === 200) {
						// for (let i = 0; i < response.data.content.length; i++) {
						// 	const element = response.data.content[i]
						// 	app.pushToMessageArray(element)
						// }
						var length = response.data.content.length
						for (var i = 0; i < length; i++) {
							var message = response.data.content[i];
							if (message.type === 'notification_form_request' ||
							  message.type === 'notification_form_result') {
							  // 暂时忽略表单消息
							} if (message.type === 'notification_thread_reentry') {
							  // 连续的 ‘继续会话’ 消息，只显示最后一条
							  if (i + 1 < length) {
								var nextmsg = response.data.content[i + 1];
								if (nextmsg.type === 'notification_thread_reentry') {
								  continue
								} else {
								  	app.pushToMessageArray(message);
								}
							  }
							} else {
							  	app.pushToMessageArray(message);
							}
						}
					}
				}, function(error) {
					console.log('load recent messages error', error)
				})
			}
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
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"username": this.my_username(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
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
				alert('消息不能为空');
				return;
			}
			if (content.length >= 500) {
				alert('消息长度太长，请分多次发送');
				return;
			}
			//
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'text',
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"username": this.my_username(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
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
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'image',
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"username": this.my_username(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
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
		sendFileMessageSync(fileUrl) {
			// console.log('sendFileMessageSync:', fileUrl);
			//
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'file',
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"username": this.my_username(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": false
					}
				},
				"file": {
					"fileUrl": fileUrl
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					"content": "[文件]",
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
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'voice',
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"username": this.my_username(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
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
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'video',
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"username": this.my_username(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
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
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"username": this.my_username(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
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
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": "notification_preview",
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"username": this.my_username(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
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
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": "notification_receipt",
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"username": this.my_username(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
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
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": "notification_recall",
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"username": this.my_username(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
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
			// 发送/广播会话消息
			this.sendTextMessageSync(this.inputContent)
			// 清空输入框
			this.inputContent = "";
			// 设置焦点
			// setTimeout(function(){
			// 	$("input")[1].focus()
			// }, 100);
		},
		sendMessageJsonRest (mid, type, content) {
			//
			var json;
			if (type === constants.MESSAGE_TYPE_TEXT) {
				json = {
					"mid": mid,
					"timestamp": this.currentTimestamp(),
					"client": constants.client,
					"version": "1",
					"type": type,
					"status": constants.MESSAGE_STATUS_SENDING,
					"user": {
						"uid": this.my_uid(),
						"username": this.username,
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
						"avatar": this.thread_avatar(),
						"topic": this.threadTopic,
						"client": constants.client,
						"timestamp": this.currentTimestamp(),
						"unreadCount": 0
					}
				};
			} else if (type === constants.MESSAGE_TYPE_IMAGE) {
				json = {
					"mid": mid,
					"timestamp": this.currentTimestamp(),
					"client": constants.client,
					"version": "1",
					"type": 'image',
					"status": constants.MESSAGE_STATUS_SENDING,
					"user": {
						"uid": this.my_uid(),
						"username": this.username,
						"nickname": this.my_nickname(),
						"avatar": this.my_avatar(),
						"extra": {
							"agent": false
						}
					},
					"image": {
						"imageUrl": content
					},
					"thread": {
						"tid": this.thread.tid,
						"type": this.thread.type,
						"content": "[图片]",
						"nickname": this.thread_nickname(),
						"avatar": this.thread_avatar(),
						"topic": this.threadTopic,
						"client": constants.client,
						"timestamp": this.currentTimestamp(),
						"unreadCount": 0
					}
				};
			} else if (type === constants.MESSAGE_TYPE_FILE) {
				json = {
					"mid": mid,
					"timestamp": this.currentTimestamp(),
					"client": constants.client,
					"version": "1",
					"type": type,
					"status": constants.MESSAGE_STATUS_SENDING,
					"user": {
						"uid": this.my_uid(),
						"username": this.username,
						"nickname": this.my_nickname(),
						"avatar": this.my_avatar(),
						"extra": {
							"agent": false
						}
					},
					"file": {
						"fileUrl": content
					},
					"thread": {
						"tid": this.thread.tid,
						"type": this.thread.type,
						"content": "[文件]",
						"nickname": this.thread_nickname(),
						"avatar": this.thread_avatar(),
						"topic": this.threadTopic,
						"client": constants.client,
						"timestamp": this.currentTimestamp(),
						"unreadCount": 0
					}
				};
			} else if (type === constants.MESSAGE_TYPE_VOICE) {
				json = {
					"mid": mid,
					"timestamp": this.currentTimestamp(),
					"client": constants.client,
					"version": "1",
					"type": type,
					"status": constants.MESSAGE_STATUS_SENDING,
					"user": {
						"uid": this.my_uid(),
						"username": this.username,
						"nickname": this.my_nickname(),
						"avatar": this.my_avatar(),
						"extra": {
							"agent": false
						}
					},
					"voice": {
						"voiceUrl": content,
						"length": '0', // TODO:替换为真实值
						"format": 'wav',
					},
					"thread": {
						"tid": this.thread.tid,
						"type": this.thread.type,
						"content": "[语音]",
						"nickname": this.thread_nickname(),
						"avatar": this.thread_avatar(),
						"topic": this.threadTopic,
						"client": constants.client,
						"timestamp": this.currentTimestamp(),
						"unreadCount": 0
					}
				};
			} else if (type === constants.MESSAGE_TYPE_VIDEO) {
				json = {
					"mid": mid,
					"timestamp": this.currentTimestamp(),
					"client": constants.client,
					"version": "1",
					"type": type,
					"status": constants.MESSAGE_STATUS_SENDING,
					"user": {
						"uid": this.my_uid(),
						"username": this.username,
						"nickname": this.my_nickname(),
						"avatar": this.my_avatar(),
						"extra": {
							"agent": false
						}
					},
					"video": {
						"videoOrShortUrl": content
					},
					"thread": {
						"tid": this.thread.tid,
						"type": this.thread.type,
						"content": "[视频]",
						"nickname": this.thread_nickname(),
						"avatar": this.thread_avatar(),
						"topic": this.threadTopic,
						"client": constants.client,
						"timestamp": this.currentTimestamp(),
						"unreadCount": 0
					}
				};
			} else if (type === constants.MESSAGE_TYPE_COMMODITY) {
				json = {
					"mid": mid,
					"timestamp": this.currentTimestamp(),
					"client": constants.client,
					"version": "1",
					"type": type,
					"status": constants.MESSAGE_STATUS_SENDING,
					"user": {
						"uid": this.my_uid(),
						"username": this.username,
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
						"content": "[商品]",
						"nickname": this.thread_nickname(),
						"avatar": this.thread_avatar(),
						"topic": this.threadTopic,
						"client": constants.client,
						"timestamp": this.currentTimestamp(),
						"unreadCount": 0
					}
				};
			}
			// 
			// 长连接断开，则调用rest接口发送消息
			this.doSendMessageRest2(mid, json)
		},
		// 实际发送消息
		doSendMessage (json) {
			// console.log('doSendMessage:', json)
			if (this.my_uid() === '') {
				uni.showToast({ title: 'uid不能为空', icon:'none', duration: 2000 });
				return
			}
			// 发送消息
			if (stompApi.isConnected()) {
				// 通过长连接，发送消息
				stompApi.sendMessage(this.threadTopic, JSON.stringify(json));
			} else {
				// 如果长连接断开，则调用rest接口发送消息
				this.doSendMessageRest(json)
			}
			// 先插入本地
			this.onMessageReceived(json)
		},
		// 第一次长连接消息未发送成功，则会调用此rest接口尝试多次发送消息，如果发送成功，会更新本地消息发送状态
		doSendMessageRest(json) {
			// console.log('doSendMessageRest:', JSON.stringify(json))
			let app = this
			httpApi.sendMessageRest(JSON.stringify(json), function(response) {
				console.log('sendMessageRest success:', response)
				let message = JSON.parse(response.data)
				console.log('after parse json:', message.mid)
				// 遍历本地消息数组，查找当前消息，并更新数组中当前消息发送状态为发送成功，也即：'stored'
				for (let i = app.messages.length - 1; i >= 0; i--) {
					const msg = app.messages[i]
					console.log('mid:', msg.mid, message.mid)
					// 根据mid判断消息
					if (msg.mid === message.mid) {
						// 已读 > 送达 > 发送成功 > 发送中
						// 可更新顺序 read > received > stored > sending, 前面的状态可更新后面的
						console.log('before update status:', msg.mid)
						if (app.messages[i].status === 'read' ||
							app.messages[i].status === 'received') {
							return
						}
						console.log('update status:', msg.mid)
						// 重要：更新本地消息发送状态。如果消息发送‘失败’，请重点跟踪此语句是否被执行
						Vue.set(app.messages[i], 'status', 'stored') // 更新数组中当前消息发送状态为发送成功，也即：'stored'
						return
					}
				}
			}, function(error) {
				console.log('send message rest error:', error)
			})
		},
		// 第一次长连接消息未发送成功，则会调用此rest接口尝试多次发送消息，如果发送成功，会更新本地消息发送状态
		doSendMessageRest2(mid, json) {
			// console.log('doSendMessageRest2:', JSON.stringify(json))
			let app = this
			httpApi.sendMessageRest(JSON.stringify(json), function(response) {
				// console.log('sendMessageRest2 success:', response)
				// 遍历本地消息数组，查找当前消息，并更新数组中当前消息发送状态为发送成功，也即：'stored'
				for (let i = app.messages.length - 1; i >= 0; i--) {
					const msg = app.messages[i]
					// 根据mid判断消息
					if (msg.mid === mid) {
						// 已读 > 送达 > 发送成功 > 发送中
						// 可更新顺序 read > received > stored > sending, 前面的状态可更新后面的
						if (app.messages[i].status === 'read' ||
							app.messages[i].status === 'received') {
							return
						}
						// 重要：更新本地消息发送状态。如果消息发送‘失败’，请重点跟踪此语句是否被执行
						Vue.set(app.messages[i], 'status', 'stored') // 更新数组中当前消息发送状态为发送成功，也即：'stored'
						return
					}
				}
			}, function(error) {
				console.log('send message rest error:', error)
			})
		},
		// 重新发送
		resendButtonClicked(message) {
			// 5秒没有发送成功，则尝试使用http rest接口发送
			let content = ''
			if (message.type === constants.MESSAGE_TYPE_TEXT) {
				content = message.content
			} else if (message.type === constants.MESSAGE_TYPE_IMAGE) {
				content = message.imageUrl
			} else if (message.type === constants.MESSAGE_TYPE_FILE) {
				content = message.fileUrl
			} else if (message.type === constants.MESSAGE_TYPE_VOICE) {
				content = message.voiceUrl
			} else if (message.type === constants.MESSAGE_TYPE_VIDEO) {
				content = message.videoOrShortUrl
			} else if (message.type === constants.MESSAGE_TYPE_COMMODITY) {
				content = this.commodityInfo();
			}
			this.sendMessageJsonRest(message.mid, message.type, content)
		},
		pushToMessageArray(message) {
			// 判断是否已经存在
			let contains = false
			for (var i = 0; i < this.messages.length; i++) {
				let msg = this.messages[i]
				if (msg.mid === message.mid) {
					contains = true
					// 更新消息状态
					Vue.set(this.messages[i], 'status', message.status)
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
			this.scrollToBottom()
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
				  this.sendReceiptMessage(mid, constants.MESSAGE_STATUS_READ);
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
		},
		// 拉黑
		addBlock() {
			// 只有客服端
			if (this.isAgentClient) {
				this.$refs.dialogInput.open()
			}
		},
		blockDialogChange(e) {
			console.log('popup ' + e.type + ' 状态', e.show)
		},
		blockDialogInputConfirm(done, val) {
			console.log(val);
			//
			let type = this.thread.type
			let uuid = this.thread.topic.split('/')[0]
			httpApi.addBlock2(this.visitorUid, val, type, uuid, function(response) {
				console.log('add block success:', response)
				if (response.status_code === 200) {
					uni.showToast({ title: '拉黑成功', duration: 2000 });
				} else {
					uni.showToast({ title: response.message, icon:'none', duration: 2000 });
				}
				// 关闭窗口
				done()
			}, function(error) {
				console.log('add block error:', error)
				uni.showToast({ title: '拉黑失败', icon:'none', duration: 2000 });
			})
		},
		// 设置会话免打扰
		markNodisturb () {
			let uid = this.my_uid()
			let tid = this.thread.tid
			httpApi.markNodisturb(uid, tid, response => {
				uni.showToast({ title: '设置免打扰成功', duration: 2000 });
			}, error => {
				console.log('mark nodisturb error:', error);
				uni.showToast({ title: '设置免打扰失败', icon:'none', duration: 2000 });
			})
		},
		// 取消会话免打扰
		unmarkNodisturb () {
			let uid = this.my_uid()
			let tid = this.thread.tid
			httpApi.unmarkNodisturb(uid, tid, response => {
				uni.showToast({ title: '取消免打扰成功', duration: 2000 });
			}, error => {
				console.log('unmark nodisturb error:', error)
				uni.showToast({ title: '取消免打扰失败', icon:'none', duration: 2000 });
			})
		},
		// 加载当前在线客服
		getOnlineAgents () {
			// 
			let app = this
			httpApi.getOnlineAgents(function(response) {
				//
				app.onlineAgents = response.data
				if (app.onlineAgents.length > 0) {
					//
					let agentRealNames = []
					for (var i = 0; i < app.onlineAgents.length; i++) {
						let agent = app.onlineAgents[i]
						agentRealNames.push(agent.realName)
					}
					uni.showActionSheet({
						title:'请选择要转接的客服',
						itemList: agentRealNames,
						success: (e) => {
							// console.log(e.tapIndex);
							let topic = app.onlineAgents[e.tapIndex].uid
							app.sendTransferMessage(topic)
						}
					})
				} else {
					uni.showToast({ title: '无其他客服在线', icon:'none', duration: 2000 });
				}
			}, function(error) {
				console.log('getOnlineAgents error', error)
			})
		},
		// 发送转接会话信息
		sendTransferMessage (topic) {
			//
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'notification_transfer',
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
					}
				},
				"transfer": {
					"topic": topic,
					"content": "转接会话"
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					"content": '转接会话',
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.doSendMessage(json);
			uni.showToast({ title: '发送成功', duration: 2000 });
		},
		// 接受转接会话
		acceptTransfer (topic) {
			//
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'notification_transfer_accept',
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
					}
				},
				"transfer": {
					"topic": topic,
					"accept": true
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					"content": "接受转接",
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.doSendMessage(json);
			uni.showToast({ title: '发送成功', duration: 2000 });
		},
		// 拒绝转接会话
		rejectTransfer (topic) {
			//
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'notification_transfer_reject',
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
					}
				},
				"transfer": {
					"topic": topic,
					"accept": false
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					"content": "拒绝转接",
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.doSendMessage(json);
			uni.showToast({ title: '发送成功', duration: 2000 });
		},
		// 邀请评价
		inviteRate () {
			//
			var json = {
				"mid": this.guid(),
				"timestamp": this.currentTimestamp(),
				"client": this.client,
				"version": "1",
				"type": 'notification_invite_rate',
				"status": "sending",
				"user": {
					"uid": this.my_uid(),
					"nickname": this.my_nickname(),
					"avatar": this.my_avatar(),
					"extra": {
						"agent": true
					}
				},
				"extra": {
					"content": '请对客服的服务做出评价'
				},
				"thread": {
					"tid": this.thread.tid,
					"type": this.thread.type,
					"content": '请对客服的服务做出评价',
					"nickname": this.thread_nickname(),
					"avatar": this.thread.visitor.avatar,
					"topic": this.threadTopic,
					"timestamp": this.currentTimestamp(),
					"unreadCount": 0
				}
			};
			this.doSendMessage(json);
			uni.showToast({ title: '发送成功', duration: 2000 });
		},
		// 客服关闭会话
		closeThread () {
			//
			httpApi.agentCloseThread(this.thread.tid, function(response) {
				if (response.status_code === 200) {
					uni.showToast({ title: '关闭会话成功', duration: 2000 });
					uni.navigateBack()
				} else {
					uni.showToast({ title: response.message, icon:'none', duration: 2000 });
				}
			}, function(error) {
				console.log('agentCloseThread error', error)
			})
		},
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
			let timestamp = moment(new Date()).format('YYYYMMDDHHmmss'); 
			return timestamp + s4() + s4() + s4() + s4() + s4() + s4() + s4() + s4()
			// return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4()
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
		// 常用语页面
		showCuw () {
			uni.navigateTo({
				url: './cuw'
			});
		},
		// 选择表情
		chooseEmoji(){
			this.hideMore = true;
			if(this.hideEmoji){
				this.hideEmoji = false;
				this.openDrawer();
			}else{
				this.hideDrawer();
			}
		},
		//添加表情
		addEmoji(item){
			this.inputContent += item.title;
		},
		emotionUrl(file) {
		  return this.emotionBaseUrl + file;
		},
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
							let content = message.content.replaceAll('amp;', '')
							uni.setClipboardData({
							  data: content,//要被复制的内容
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
				let content = message.content.replaceAll('amp;', '')
				uni.setClipboardData({
				  data: content,//要被复制的内容
				  success:() => { //复制成功的回调函数
				    uni.showToast({ //提示
				      title:'复制成功'
				    })
				  }
				});
				// #endif
			}
		},
		checkTimeoutMessage() {
		  // 检测-消息是否超时发送失败
		  for (let i = 0; i < this.messages.length; i++) {
			const message = this.messages[i];
			if (this.is_self(message) && this.is_sending(message)) {
			  let timestamp = moment(message.createdAt);
			  let now = moment(new Date());
			  let diff = now.diff(timestamp, "seconds");
			  console.log('diff:', diff)
			 //  if (diff > 60) {
				// // 超时60秒，设置为消息状态为error
				// // this.messages[i].status = 'error'
				// Vue.set(this.messages[i], 'status', 'error')
			 //  }
			 if (diff > 15) {
				// 超时15秒，设置为消息状态为error
				Vue.set(this.messages[i], 'status', 'error')
			 } else if (diff > 5) {
				// 5秒没有发送成功，则尝试使用http rest接口发送
				this.resendButtonClicked(message)
			 }
			}
		  }
		}
	},
	mounted() {
	  // 如果长连接断开，则定时刷新聊天记录
	  this.loadHistoryTimer = setInterval(this.loadLatestMessage, 1000 * 10);
	  this.sendMessageTimer = setInterval(this.checkTimeoutMessage, 1000);
	},
	beforeDestroy() {
	  clearInterval(this.loadHistoryTimer);
	  clearInterval(this.sendMessageTimer);
	}
}
</script>

<style lang="scss">
	@import "css/style.scss";
</style>