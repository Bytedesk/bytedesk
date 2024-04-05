<template>
	<view class="bytedesk">
		<uni-search-bar @confirm="search" @input="input" @cancel="cancel" />
		<!-- 客服自己添加常用语 -->
		<view v-if="searchVal.length === 0" v-for="category in mineList" :key="category.cid">
			<uni-section :title="category.name" type="line"></uni-section>
			<uni-list>
				<uni-list-item clickable @click="selectCuw(cuw)" v-for="cuw in category.cuwChildren"  :key="cuw.cid" :title="cuw.name" :note="getDetail(cuw.type, cuw.content)"/>
			</uni-list>
		</view>
		<!-- 公司常用语 -->
		<view v-if="searchVal.length === 0" v-for="category in companyList" :key="category.cid">
			<uni-section :title="category.name" type="line"></uni-section>
			<uni-list>
				<uni-list-item clickable @click="selectCuw(cuw)" v-for="cuw in category.cuwChildren"  :key="cuw.cid" :title="cuw.name" :note="getDetail(cuw.type, cuw.content)"/>
			</uni-list>
		</view>
		<!-- 平台常用语 -->
		<view v-if="searchVal.length === 0" v-for="category in platformList" :key="category.cid">
			<uni-section :title="category.name" type="line"></uni-section>
			<uni-list>
				<uni-list-item clickable @click="selectCuw(cuw)" v-for="cuw in category.cuwChildren"  :key="cuw.cid" :title="cuw.name" :note="getDetail(cuw.type, cuw.content)"/>
			</uni-list>
		</view>
		<!-- 搜索结果 -->
		<uni-list-item v-if="searchVal.length > 0" clickable @click="selectCuw(cuw)" v-for="cuw in filteredCuw" :key="cuw.cid" :title="cuw.name" :note="getDetail(cuw.type, cuw.content)"/>
		<view class="nodata" v-if="filteredCuw.length === 0">
			当前无常用语
		</view>
	</view>
</template>

<script>
	// 引入js文件
	import * as httpApi from '@/components/bytedesk_kefu/js/api/httpapi.js'
	
	export default {
		data() {
			return {
				cuwList: [],
				//
				mineList: [],
				//
				companyList: [],
				//
				platformList: [],
				//
				searchVal: ''
			}
		},
		computed: {
			///
			filteredCuw () {
				return this.cuwList.filter(cuw => {
					if (cuw.name != null && cuw.content != null) {
						return cuw.name.includes(this.searchVal) || cuw.content.includes(this.searchVal)
					} else if (cuw.name != null) {
						return cuw.name.includes(this.searchVal)
					} else if (cuw.content != null) {
						return cuw.content.includes(this.searchVal)
					}
				})
			}
		},
		onLoad() {
			this.getCuws()
		},
		onPullDownRefresh() {
			// 加载数据
			// this.getCuws()
			//
			// setTimeout(function () {
			// 	uni.stopPullDownRefresh();
			// }, 1000);
		},
		onReachBottom() {
			// 加载数据
			// this.getCuws()
		},
		methods: {
			search(res) {
				console.log('search:', res, res.value)
				this.searchVal = res.value
			},
			input(res) {
				console.log('input:', res, res.value)
				this.searchVal = res.value
			},
			cancel(res) {
				console.log('cancel:', res, res.value)
			},
			selectCuw (cuw) {
				console.log('cuw', cuw);
				uni.$emit('cuw', cuw);
				uni.navigateBack();
			},
			getDetail(type, content) {
				if (type === "text") {
					return "[文字]" + content;
				} else if (type === "image") {
					return "[图片]";
				} else if (type === "file") {
					return "[文件]";
				} else if (type === "voice") {
					return "[语音]";
				} else if (type === "video") {
					return "[视频]";
				}
				return "[文字]" + content;
			},
			getCuws () {
				//
				uni.showLoading({title: '加载中', mask:true});
				let app = this
				httpApi.getCuws(function(response) {
					console.log('getCuws success:', response)
					//
					app.mineList = response.data.mine
					app.companyList = response.data.company
					app.platformList = response.data.platform
					//
					for (var i = 0; i < response.data.mine.length; i++) {
						let mineArray = response.data.mine[i]
						for (var j = 0; j < mineArray.cuwChildren.length; j++) {
							let cuw = mineArray.cuwChildren[j]
							// cuw.content = app.getDetail(cuw.type, cuw.content)
							app.cuwList.push(cuw)
						}
					}
					for (var i = 0; i < response.data.company.length; i++) {
						let companyArray = response.data.company[i]
						for (var j = 0; j < companyArray.cuwChildren.length; j++) {
							let cuw = companyArray.cuwChildren[j]
							// cuw.content = app.getDetail(cuw.type, cuw.content)
							app.cuwList.push(cuw)
						}
					}
					for (var i = 0; i < response.data.platform.length; i++) {
						let platformArray = response.data.platform[i]
						for (var j = 0; j < platformArray.cuwChildren.length; j++) {
							let cuw = platformArray.cuwChildren[j]
							// cuw.content = app.getDetail(cuw.type, cuw.content)
							app.cuwList.push(cuw)
						}
					}
					app.page += 1;
					uni.stopPullDownRefresh();
					uni.hideLoading();
				}, function(error) {
					console.log('getCuws error', error)
					uni.stopPullDownRefresh();
					uni.showToast({ title: error, duration: 2000 });
					uni.hideLoading();
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
