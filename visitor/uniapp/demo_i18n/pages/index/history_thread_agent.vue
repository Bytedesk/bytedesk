<template>
	<view class="bytedesk">
		<!-- 用于需要将SDK集成到客服端的开发者 -->
		<uni-section title="客服端-历史会话" type="line"></uni-section>
		<uni-list-chat clickable @click="startChat(thread)" v-for="thread in workGroupThreadList" :key="thread.tid" :avatar="thread.avatar" :title="thread.nickname" :note="thread.content" :time="thread.timestamp" showArrow/>
		<uni-list-chat clickable @click="startChat(thread)" v-for="thread in agentThreadList" :key="thread.tid" :avatar="thread.avatar" :title="thread.nickname" :note="thread.content" :time="thread.timestamp" showArrow/>
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
			this.getThreadHistoryRecords()
		},
		methods: {
			startChat (thread) {
				console.log('chat');
				uni.navigateTo({
					url: '../../components/bytedesk_kefu/chat-im?'
						+ 'tid=' + thread.tid
						+ '&uid=' + thread.uid 
						+ '&nickname=' + thread.nickname 
						+ '&avatar=' + thread.avatar 
						+ '&topic=' + thread.topic 
						+ '&type=' + thread.type 
						+ '&title=' + thread.nickname
						+ '&agentclient=1'
				});
			},
			getThreadHistoryRecords () {
				let page = 0;
				let size = 20;
				//
				let app = this
				httpApi.getThreadHistoryRecords(page, size, function(response) {
					console.log('getThreadHistoryRecords success:', response)
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
					console.log('getThreadHistoryRecords error', error)
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
