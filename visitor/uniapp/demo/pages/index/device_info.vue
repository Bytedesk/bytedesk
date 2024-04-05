<template>
	<view class="bytedesk">
		<uni-section title="设备信息" type="line"></uni-section>
		<uni-list :border="true">
			<uni-list-item title="唯一Uid" :note="uid" />
			<uni-list-item title="设置昵称" clickable @click="setNickname()" :note="nickname" />
			<uni-list-item title="设置备注" clickable @click="setDescription()" :note="description" />
			<uni-list-chat title="设置头像" clickable @click="setAvatar()" :avatar="avatar" :avatar-circle="true" />
		</uni-list>
	</view>
</template>

<script>
	// 引入js文件
	import * as httpApi from '@/components/bytedesk_kefu/js/api/httpapi.js'
	
	export default {
		data() {
			return {
				uid: '',
				nickname: '',
				description: '',
				avatar: 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/admin_default_avatar.png' // 默认显示
			}
		},
		onLoad() {
			this.getProfile()
		},
		methods: {
			getProfile () {
				// 查询当前用户信息：昵称、头像
				let app = this
				httpApi.getProfile(function(response) {
					console.log('getProfile success:', response)
					app.uid = response.data.uid
					app.nickname = response.data.nickname
					app.description = response.data.description
					app.avatar = response.data.avatar
				}, function(error) {
					console.log('getProfile error', error)
					uni.showToast({ title: error, duration: 2000 });
				})
			},
			setNickname () {
				// 可自定义用户昵称-客服端可见
				let mynickname = '自定义APP昵称uniapp'
				let app = this
				httpApi.updateNickname(mynickname, function(response) {
					console.log('updateNickname success:', response)
					app.nickname = mynickname
				}, function(error) {
					console.log('updateNickname error', error)
					uni.showToast({ title: error, duration: 2000 });
				})
			},
			setDescription () {
				// 可自定义用户备注信息-客服端可见
				let mydescription = '自定义APP用户备注信息uniapp'
				let app = this
				httpApi.updateDescription(mydescription, function(response) {
					console.log('updateDescription success:', response)
					app.description = mydescription
				}, function(error) {
					console.log('updateDescription error', error)
					uni.showToast({ title: error, duration: 2000 });
				})
			},
			setAvatar () {
				// 可自定义用户头像url-客服端可见
				let myavatarurl = 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/visitor_default_avatar.png'; // 头像网址url
				let app = this
				httpApi.updateAvatar(myavatarurl, function(response) {
					console.log('updateAvatar success:', response)
					app.avatar = myavatarurl
				}, function(error) {
					console.log('updateAvatar error', error)
					uni.showToast({ title: error, duration: 2000 });
				})
			},
			setProfile () {
				let mynickname = '自定义APP昵称uniapp'
				let myavatarurl = 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/visitor_default_avatar.png'; // 头像网址url
				let mydescription = '自定义APP用户备注信息uniapp'
				let app = this
				httpApi.updateProfile(mynickname, myavatarurl, mydescription, response => {
					console.log('updateProfile success:', response)
					app.nickname = mynickname
					app.avatar = myavatarurl
					app.description = mydescription
				}, error => {
					console.log('updateAvatar error', error)
					uni.showToast({ title: error, duration: 2000 });
				}) 
			}
		}
	}
</script>

<style>
</style>
