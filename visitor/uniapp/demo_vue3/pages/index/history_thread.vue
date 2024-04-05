<template>
	<view class="bytedesk">
		<uni-section title="历史会话" type="line"></uni-section>
		<uni-list :border="true">
			<uni-list-item title="访客端-历史会话" clickable @click="gotoVisitorHistoryThreads()" note="需要访客登录" showArrow />
			<uni-list-item title="客服端-历史会话" clickable @click="gotoAgentHistoryThreads()" note="需要登录客服账号" showArrow />
		</uni-list>
		<uni-section title="当前会话" type="line"></uni-section>
		<uni-list :border="true">
			<!-- 不仅支持对接访客端，也支持作为客服端集成 -->
			<uni-list-item title="客服端-进行中会话" clickable @click="gotoAgentCurrentThreads()" note="需要登录客服账号" showArrow />
		</uni-list>
	</view>
</template>

<script>
	export default {
		data() {
			return {
			}
		},
		onLoad() {
		},
		methods: {
			gotoVisitorHistoryThreads () {
				uni.navigateTo({
					url: './history_thread_visitor'
				});
			},
			gotoAgentHistoryThreads () {
				uni.navigateTo({
					url: './history_thread_agent'
				});
			},
			gotoAgentCurrentThreads () {
				uni.navigateTo({
					url: './current_thread_agent'
				});
			}
		}
	}
</script>

<style>
</style>
