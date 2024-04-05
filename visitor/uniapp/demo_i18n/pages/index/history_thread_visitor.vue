<template>
	<view class="bytedesk">
		<uni-section title="历史会话" type="line"></uni-section>
		<uni-list-chat v-for="thread in workGroupThreadList" :key="thread.tid" :avatar="thread.workGroup.avatar" :title="thread.workGroup.nickname" :note="thread.content" :time="thread.timestamp" showArrow/>
		<uni-list-chat v-for="thread in agentThreadList" :key="thread.tid" :avatar="thread.agent.avatar" :title="thread.agent.nickname" :note="thread.content" :time="thread.timestamp" showArrow/>
		<view class="nodata" v-if="workGroupThreadList.length === 0 && agentThreadList.length === 0">
			当前无进行中会话
		</view>
	</view>
</template>

<script>
	// 引入js文件
	import * as httpApi from '@/components/bytedesk_kefu/js/api/httpapi.js'
	
	export default {
		data() {
			return {
				workGroupThreadList: [],
				agentThreadList: []
			}
		},
		onLoad() {
			this.getVisitorThreads()
		},
		methods: {
			getVisitorThreads () {
				let page = 0;
				let size = 20;
				//
				let app = this
				httpApi.getVisitorThreads(page, size, function(response) {
					console.log('getVisitorThreads success:', response)
					// for (var i = 0; i < response.data.content.length; i++) {
					// 	let thread = response.data.content[i]
					// 	if (thread.type === 'workgroup') {
					// 		app.workGroupThreadList.push(thread)
					// 	} else {
					// 		app.agentThreadList.push(thread)
					// 	}
					// }
					for (var i = 0; i < response.data.content.length; i++) {
						let thread = response.data.content[i]
						if (thread.type === 'workgroup') {
							let contains = false
							for (var j = 0; j < app.workGroupThreadList.length; j++) {
								let wthread = app.workGroupThreadList[j]
								if (wthread.topic === thread.topic) {
									contains = true
								}
							}
							if (!contains) {
								app.workGroupThreadList.push(thread)
							}
						} else {
							let contains = false
							for (var j = 0; j < app.agentThreadList.length; j++) {
								let wthread = app.agentThreadList[j]
								if (wthread.topic === thread.topic) {
									contains = true
								}
							}
							if (!contains) {
								app.agentThreadList.push(thread)
							}
						}
					}
				}, function(error) {
					console.log('getVisitorThreads error', error)
					uni.showToast({ title: error, duration: 2000 });
				})
			}
		}
	}
</script>

<style>
	.nodata {
		display: flex;
		flex-direction: column;
		justify-content: center;
		height: 400rpx;
		width: 100%;
		align-items: center;
		color: #555555;
		font-size: 28rpx;
	}
</style>
