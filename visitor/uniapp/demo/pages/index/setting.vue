<template>
	<view class="bytedesk">
		<uni-section title="消息提示" type="line"></uni-section>
		<uni-list :border="true">
			<uni-list-item title="振动" :show-switch="true" :switch-checked="vibrate" @switchChange="switchVibrate" :note="vibrateNote" />
			<uni-list-item title="声音" :show-switch="true" :switch-checked="playAudio" @switchChange="switchPlayAudio" :note="playAudioNote" />
		</uni-list>
	</view>
</template>

<script>
	// 引入js文件
	import * as constants from '@/components/bytedesk_kefu/js/constants.js'
	
	export default {
		data() {
			return {
				vibrate: true,
				playAudio: true
			}
		},
		computed: {
			vibrateNote () {
				return this.vibrate ? '振动开启' : '禁用振动'
			},
			playAudioNote () {
				return this.playAudio ? '提示音开启' : '禁用提示音'
			}
		},
		onLoad() {
			try {
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
				uni.showToast({ title: error, duration: 2000 });
			}
		},
		methods: {
			switchVibrate (result) {
				// 振动  result：{"value":false}
				uni.setStorageSync(constants.vibrate, result.value)
			},
			switchPlayAudio (result) {
				// 播放提示音  result：{"value":false}
				uni.setStorageSync(constants.playAudio, result.value)
			}
		}
	}
</script>

<style>
</style>
