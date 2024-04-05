<template>
	<view class="bytedesk">
		<uni-list>
			<uni-list-item title="联系客服" :to="`./chat_type`" note="人工/机器人/电商/附言/H5" showArrow />
			<uni-list-item title="用户信息" :to="`./user_info`" note="自定义用户昵称/头像" showArrow />
			<uni-list-item title="在线状态" :to="`./online_status`" note="客服是否在线" showArrow />
			<uni-list-item title="历史会话" :to="`./history_thread`" note="历史会话记录" showArrow />
			<uni-list-item title="消息提示" :to="`./setting`" note="声音/振动提示开启/关闭" showArrow />
			<uni-list-item title="切换用户" :to="`./switch_user`" note="不同账号之间切换" showArrow />
			<!-- <uni-list-item title="意见反馈" :to="`./feedback`" note="意见反馈" showArrow /> -->
		</uni-list>
	</view>
</template>

<script>
// 萝卜丝第一步：引入js文件
import * as bytedesk from '@/components/bytedesk_kefu/js/api/bytedesk.js'
import * as constants from '@/components/bytedesk_kefu/js/constants.js'

export default {
	data() {
		return {
			uid: '',
			vibrate: true,
			playAudio: true
		}
	},
	onLoad() {
		// 萝卜丝第二步：初始化
		// 获取subDomain，也即企业号：登录后台->客服管理->客服账号->企业号
		let subDomain = 'vip'
		// 登录后台->渠道管理-》uniapp中创建应用获取appkey
		let appKey = 'f4970e52-8cc8-48fd-84f6-82390640549d'
		bytedesk.init(subDomain, appKey);
		// 注：如果需要多平台统一用户（用于同步聊天记录等），可使用:
		// bytedesk.initWithUsernameAndNicknameAndAvatar('myuniappusername', '我是美女', 'https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/girl.png', subDomain, appKey);
		// bytedesk.initWithUsername('myuniappusername',subDomain, appKey); // 其中：username为自定义用户名，可与开发者所在用户系统对接
		// 如果还需要自定义昵称/头像，可以使用 initWithUsernameAndNickname或initWithUsernameAndNicknameAndAvatar，
		// 具体参数可以参考 @/components/bytedesk_kefu/js/api/bytedesk.js 文件中接口
		
		// (可选)
		try {
			// 读取用户uid
			this.uid = uni.getStorageSync(constants.uid)
			// 读取振动、提示音设置
		    this.vibrate = uni.getStorageSync(constants.vibrate)
			if (this.vibrate === null || this.vibrate === '') {
				this.vibrate = true
			}
			this.playAudio = uni.getStorageSync(constants.playAudio)
			if (this.playAudio === null || this.playAudio === '') {
				this.playAudio = true
			}
		} catch (error) {
		    console.error('read vibrate/playAudio error', error)
		}
		
		// 监听连接状态（可选）
		uni.$on(constants.EVENT_BUS_STOMP_CONNECTION_STATUS, function(connectionStatus) {
			console.log('connectionStatus:', connectionStatus);
		})
	},
	onReady () {
		// 监听消息通知, 可选
		let app = this
		uni.$on('message',function(messageObject) {
			// console.log('messageObject index:', messageObject);
			if (messageObject.type === 'text') {
				console.log('receive text:', messageObject.text.content);
			} else if (messageObject.type === 'image') {
				console.log('receive image:', messageObject.image.imageUrl);
			} else if (messageObject.type === 'voice') {
				console.log('receive voice:', messageObject.voice.voiceUrl);
			} else if (messageObject.type === 'video') {
				console.log('receive video:', messageObject.video.videoOrShortUrl);
			} else if (messageObject.type === 'file') {
				console.log('receive file:', messageObject.file.fileUrl);
			} else {
				// console.log('其他类型消息')
			}
			//
			// try {
			// 	//
			// 	if (this.uid === null || this.uid === undefined || this.uid === '') {
			// 		this.uid = uni.getStorageSync(constants.uid)
			// 	}
			// 	// 判断是否是自己发送的消息，非自己发送的消息才会播放提示
			// 	if (messageObject.user.uid !== this.uid) {
			// 		console.log(messageObject.user.uid, this.uid);
			// 		// 仅支持APP
			// 		// #ifdef APP-PLUS
			// 		// 振动
			// 		app.doVibrate();
			// 		// 播放提示音
			// 		app.doPlayAudio();
			// 		// #endif
			// 	}
			// } catch (error) {
			//     console.error('read vibrate/playAudio error', error)
			// }
		})
	},
	methods: {
		// 振动
		doVibrate () {
			if (!this.vibrate) {
				return
			}
			// 仅支持APP
			// #ifdef APP-PLUS
			// 振动
			// iOS上只有长震动，没有短震动
			// iOS上需要手机设置“打开响铃时震动”或“静音时震动”，否则无法震动
			// vibrate只适用于钉钉小程序、支付宝小程序
			// 
			// uni.vibrate({
			//     success: function () {
			//         console.log('振动');
			//     }
			// });
			// #endif
		},
		// 播放提示音
		doPlayAudio () {
			if (!this.playAudio) {
				return
			}
			// 仅支持APP
			// #ifdef APP-PLUS
			// 播放提示音
			const innerAudioContext = uni.createInnerAudioContext();
			innerAudioContext.autoplay = true;
			innerAudioContext.src = 'https://cdn.bytedesk.com/assets/sound/dingdong.wav';
			innerAudioContext.onPlay(() => {
			  console.log('开始播放');
			});
			innerAudioContext.onError((res) => {
			  console.log(res.errMsg);
			  console.log(res.errCode);
			});
			// #endif
		}
	}
}
</script>

<style>

</style>
