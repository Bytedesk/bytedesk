<template>
	<view class="bytedesk">
		<uni-section title="自定义用户信息" type="line"></uni-section>
		<uni-list :border="true">
			<uni-list-item title="用户信息" :to="`./user_info`" showArrow />
			<uni-list-item title="用户1男" clickable @click="userBoyLogin()"/>
			<uni-list-item title="用户2女" clickable @click="userGirlLogin()"/>
			<uni-list-item title="退出登录" clickable @click="userLogout()"/>
		</uni-list>
	</view>
</template>

<script>
	// 引入js文件
	import * as constants from '@/components/bytedesk_kefu/js/constants.js'
	import * as bytedesk from '@/components/bytedesk_kefu/js/api/bytedesk.js'
	import * as httpApi from '@/components/bytedesk_kefu/js/api/httpapi.js'
	
	export default {
		data() {
			return {
				// 获取subDomain，也即企业号：登录后台->客服管理->客服账号->企业号
				subDomain: 'vip',
				// 登录后台->渠道管理-》uniapp中创建应用获取appkey
				appKey: 'f4970e52-8cc8-48fd-84f6-82390640549d'
			}
		},
		onLoad() {
			
		},
		methods: {
			userBoyLogin() {
				let isLogin = uni.getStorageSync(constants.isLogin);
				if (isLogin) {
					uni.showToast({ title:'请先退出登录', icon:'none', duration: 2000 })
					return
				}
				this.initWithUsernameAndNicknameAndAvatar('myuniappuserboy', '我是帅哥uniapp', 'https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/boy.png', this.subDomain, this.appKey)
			},
			userGirlLogin() {
				let isLogin = uni.getStorageSync(constants.isLogin);
				if (isLogin) {
					uni.showToast({ title:'请先退出登录', icon:'none', duration: 2000 })
					return
				}
				this.initWithUsernameAndNicknameAndAvatar('myuniappusergirl', '我是美女uniapp', 'https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/girl.png', this.subDomain, this.appKey)
			},
			initWithUsernameAndNicknameAndAvatar(username, nickname, avatar, subDomain, appKey) {
				bytedesk.initWithUsernameAndNicknameAndAvatar(username, nickname, avatar, subDomain, appKey)
				uni.showToast({ title:'登录成功', icon:'none', duration: 2000 })
			},
			userLogout() {
				uni.showLoading({ title: '退出登录中', icon:'none', duration: 2000 });
				// 退出登录
				httpApi.logout(response => {
					uni.hideLoading();
					uni.showToast({ title:'退出登录成功', icon:'none', duration: 2000 })
				}, error => {
					uni.hideLoading();
					uni.showToast({ title:'退出登录失败', icon:'none', duration: 2000 })
				})
			}
		}
	}
</script>

<style>
</style>
