<template>
	<view class="bytedesk">
		<uni-list :border="true">
			<uni-list-item title="意见反馈" note="意见反馈" showArrow/>
		</uni-list>
	</view>
</template>

<script>
	// 引入js文件
	import * as httpApi from '@/components/bytedesk_kefu/js/api/httpapi.js'
	
	export default {
		data() {
			return {
				// 到 客服管理->技能组-有一列 ‘唯一ID（wId）’, 默认设置工作组wid
				// 说明：一个技能组可以分配多个客服，访客会按照一定的规则分配给组内的各个客服账号
				workGroupWid: '201807171659201', // 默认人工
			}
		},
		onLoad() {

		},
		methods: {

		}
	}
</script>

<style>
</style>
