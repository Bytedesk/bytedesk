<template>
	<view>
		<web-view :src="url"></web-view>
	</view>
</template>

<script>
	export default {
		onLoad(option) {
			this.url = option.url
		},
		data() {
			return {
				url:""
			}
		},
		methods: {
			
		}
	}
</script>

<style>

</style>
