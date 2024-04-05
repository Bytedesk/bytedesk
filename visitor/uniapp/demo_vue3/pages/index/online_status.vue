<template>
	<view class="bytedesk">
		<uni-section title="客服是否在线" type="line"></uni-section>
		<uni-list :border="true">
			<uni-list-item title="技能组" :note="workGroupOnlineStatus" />
			<uni-list-item title="指定客服" :note="agentOnlineStatus" />
		</uni-list>
	</view>
</template>

<script>
	// 引入js文件
	import * as httpApi from '@/components/bytedesk_kefu/js/api/httpapi.js'
	
	export default {
		data() {
			return {
				workGroupOnlineStatus: '', // 注：online 代表在线，offline 代表离线
				agentOnlineStatus: '', // 注：online 代表在线，offline 代表离线
				// 到 客服管理->技能组-有一列 ‘唯一ID（wId）’, 默认设置工作组wid
				// 说明：一个技能组可以分配多个客服，访客会按照一定的规则分配给组内的各个客服账号
				workGroupWid: '201807171659201', // 默认人工
				// 说明：直接发送给此一个客服账号，一对一会话
				agentUid: '201808221551193'
			}
		},
		onLoad() {
			// 获取技能组在线状态
			this.getWorkGroupStatus()
			// 获取指定客服在线状态
			this.getAgentStatus()
		},
		methods: {
			getWorkGroupStatus () {
				// 获取技能组在线状态：当技能组中至少有一个客服在线时，显示在线
				let app = this
				httpApi.getWorkGroupStatus(this.workGroupWid, function(response) {
					console.log('getWorkGroupStatus success:', response)
					// online代表在线，否则为离线
					app.workGroupOnlineStatus = response.data.status
				}, function(error) {
					console.log('getWorkGroupStatus error', error)
					uni.showToast({ title: error, duration: 2000 });
				})
			},
			getAgentStatus () {
				// 获取指定客服在线状态
				let app = this
				httpApi.getAgentStatus(this.agentUid, function(response) {
					console.log('getAgentStatus success:', response)
					// online代表在线，否则为离线
					app.agentOnlineStatus = response.data.status
				}, function(error) {
					console.log('getAgentStatus error', error)
					uni.showToast({ title: error, duration: 2000 });
				})
			}
		}
	}
</script>

<style>
</style>
