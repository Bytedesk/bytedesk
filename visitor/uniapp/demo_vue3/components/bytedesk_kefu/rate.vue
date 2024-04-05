<template>
	<!-- TODO: 修改为标签 可自定义，支持后台可配置 -->
	<view class='issue'>
		<view class="issue-head">
			<slot name="headPic"></slot>
			<image v-if="headPicShow" :src="thread.agent.avatar" class="issue-head-pic" mode=""></image>
			<text v-if="headTitleShow" class="issue-head-title">{{ thread.agent.nickname.slice(0,5) }}</text>
			<view class="issue-head-star-box" v-if="starsShow">
				<image v-for="(item,index) in starsMax" :key="index" :src="(index+1)>formatScore?starDefault:starActive" :class="formatScore==index+1?'active':''" mode="" @click="setScore(index+1)"></image>
			</view>
			<text style="margin-left: 50rpx; font-size: 12px;">{{ tip }}</text>
		</view>
		<view class="example-body">
			<!-- 好 -->
			<view v-if="greaterThan3" class="tag-view">
				<button class="mini-btn" size="mini" @click="clickButton1">服务态度好</button>
			</view>
			<view v-if="greaterThan3" class="tag-view">
				<button class="mini-btn" size="mini" @click="clickButton2">回复速度快</button>
			</view>
			<view v-if="greaterThan3" class="tag-view">
				<button class="mini-btn" size="mini" @click="clickButton3">问题已解决</button>
			</view>
			<!-- 坏 -->
			<view v-if="!greaterThan3" class="tag-view">
				<button class="mini-btn" size="mini" @click="clickButton4">服务态度差</button>
			</view>
			<view v-if="!greaterThan3" class="tag-view">
				<button class="mini-btn" size="mini" @click="clickButton5">回复不及时</button>
			</view>
			<view v-if="!greaterThan3" class="tag-view">
				<button class="mini-btn" size="mini" @click="clickButton6">没解决问题</button>
			</view>
			<view v-if="!greaterThan3" class="tag-view">
				<button class="mini-btn" size="mini" @click="clickButton7">不礼貌</button>
			</view>
		</view>
		 <textarea v-if="textareaShow" @blur="blur" :value="infoReceive.textareaValue" :placeholder="textareaPlaceholder"/>
		 <view class="issue-btn-box">
		 	<button v-if="submitShow" class="submit-btn" type="primary" @click="doSubmit" :disabled="thread.rated">{{ rateButtonText }}</button>
			<slot name="submit"></slot>
		 </view>
	</view>
</template>

<script>
	// import * as constants from '@/components/bytedesk_kefu/js/constants.js'
	import * as httpApi from '@/components/bytedesk_kefu/js/api/httpapi.js'
	
	export default {
		props:{
			headPicShow:{ //图片
				type:[String,Boolean],
				default:true,
			},
			headPicValue:{
				type:String,
				// default:require('@/components/bytedesk_kefu/image/rate/rate_pic.png')
			},
			headTitleShow:{ //标题
				type:[String,Boolean],
				default:true,
			},
			headTitleValue:{
				type:String,
				default:"评价客服"
			},
			starsShow:{
				type:[String,Boolean],
				default:true,
			},
			starsMax:{ // 星星最大个数
				type:[String,Number],
				default:5,
			},
			starDefault:{ //未选中
				type:String,
				// default:require('@/components/bytedesk_kefu/image/rate/rate_star.png'),
			},
			starActive:{
				type:String,
				// default:require('@/components/bytedesk_kefu/image/rate/rate_star_active.png'),
			},
			score:{  //默认分数
				type:[Number,String],
				default:5
			},
			starsDisabled:{ //是否禁用star
				type:[Boolean],
				default:false
			},
			textareaShow:{ // 多行文本显示
				type:[String,Boolean],
				default:true,
			},
			textareaPlaceholder:{
				type:[String],
				default:"说点什么吧"
			},
			submitShow:{ // 发布按钮
				type:[String,Boolean],
				default:true,
			},
			submitText:{
				type:String,
				default:"提交",
			},
			infoReceive:{ // 获取值
				type:Object,
				default:function(){
					return {
						score:0,
						textareaValue:""
					}
				}
			}
		},
		data () {
			return {
				//
				thread: {
					agent: {
						nickname: '评价客服',
						avatar: 'https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/admin_default_avatar.png'
					},
					rated: false
				},
				greaterThan3: true,
				tip: '非常满意，完美',
				// 满意，仍可改善
				// 一般，还需改善
				// 不满意，有点失望
				// 非常不满意
				// 
				option: {
					tid: '',
					invite: false
				}
			}
		},
		onLoad(option) {
			// #ifdef MP-QQ
			qq.showShareMenu({
				showShareItems: ['qq', 'qzone', 'wechatFriends', 'wechatMoment']
			});
			// #endif
			//
			this.option = option;
			console.log('option:', this.option);
			//
			this.getRateDetail()
		},
		computed:{
			formatScore() {
				return this.infoReceive.score
			},
			rateButtonText () {
				return this.thread.rated ? '已经评价过' : '提交评价'
			}
		},
		methods: {
			setScore(score) {
				if(this.starsDisabled!==false)return
				this.infoReceive.score=score
				// this.$emit("scoreChange",score)
				if (score === 5) {
					this.tip = '非常满意，完美'
					this.greaterThan3 = true
				} else if (score === 4) {
					this.tip = '满意，仍可改善'
					this.greaterThan3 = true
				} else if (score === 3) {
					this.tip = '一般，还需改善'
					this.greaterThan3 = false
				} else if (score === 2) {
					this.tip = '不满意，有点失望'
					this.greaterThan3 = false
				} else if (score === 1) {
					this.tip = '非常不满意'
					this.greaterThan3 = false
				}
			},
			clickButton1 () {
				console.log('服务态度好')
				this.infoReceive.textareaValue += '服务态度好'
			},
			clickButton2 () {
				console.log('回复速度快')
				this.infoReceive.textareaValue += '回复速度快'
			},
			clickButton3 () {
				console.log('问题已解决')
				this.infoReceive.textareaValue += '问题已解决'
			},
			clickButton4 () {
				console.log('服务态度差')
				this.infoReceive.textareaValue += '服务态度差'
			},
			clickButton5 () {
				console.log('回复不及时')
				this.infoReceive.textareaValue += '回复不及时'
			},
			clickButton6 () {
				console.log('没解决问题')
				this.infoReceive.textareaValue += '没解决问题'
			},
			clickButton7 () {
				console.log('不礼貌')
				this.infoReceive.textareaValue += '不礼貌'
			},
			blur(e) {
				this.infoReceive.textareaValue=e.detail.value
			},
			getRateDetail () {
				//
				let app = this
				uni.showLoading({title: '加载中', mask:true});
				httpApi.rateDetail(this.option.tid, function(response) {
					console.log('rateDetail success:', response);
					// uni.showToast({ title: response.message, duration: 2000 });
					// uni.navigateBack();
					if (response.status_code === 200) {
						app.thread = response.data
					}
					uni.hideLoading();
				}, function(error) {
					console.log('rateDetail error:', error)
					// uni.showToast({ title: "提交评价错误", icon: "none"});
					uni.hideLoading();
				});
			},
			doSubmit() {
				// this.$emit('submit',this.infoReceive)
				console.log('submit:', this.infoReceive);
				// uni.navigateBack();
				if (this.thread.rated) {
					uni.showToast({ title: "意见评价，无需重复评价", icon: "none" });
					return
				}
				//
				uni.showLoading({title: '提交中', mask:true});
				httpApi.rate(this.option.tid, this.infoReceive.score, this.infoReceive.textareaValue, this.option.invite, function(response) {
					console.log('rate success:', response);
					uni.showToast({ title: response.message, duration: 2000 });
					if (response.status_code === 200) {
						uni.navigateBack();
					}
					uni.hideLoading();
				}, function(error) {
					console.log('rate error:', error)
					uni.showToast({ title: "提交评价错误", icon: "none"});
					uni.hideLoading();
				});
			}
		},
		created() {
			this.infoReceive.score = this.score
		}
	}
</script>

<style lang='scss'>
	$backgroundC:#f9f9f9;
	$borderColor:#f5f5f5;
	$white:#ffffff;
	$fontSize:28upx;
	
	.issue {
		background-color: $backgroundC;
		
		&-head{
			background-color: $white;
			height: 100upx;
			border-top: 1upx solid $borderColor;
			border-bottom: 1upx solid $borderColor;
			padding: 0 25upx;
			
			&-pic{
				width: 70upx;
				height: 70upx;
				border-radius: 50%;
				vertical-align: middle;
				margin-right: 17upx;
			}
			&-title{
				line-height: 100upx;
				font-size: 30upx;
				vertical-align: middle;
				margin-right: 35upx;
			}
			
			&-star-box{
				display: inline-block;
				
				image{
					width: 32upx;
					height: 32upx;
					vertical-align: middle;
					margin-right: 14upx;
				}
				image.active{
					animation: star_move ease-in 1 1s,star_rotate ease 1.5s infinite 1s;
				}
			}
		}
		textarea{
			width: 100%;
			height: 220upx;
			background-color: $white;
			font-size: $fontSize;
			color: #898989;
			padding: 24upx;
			box-sizing: border-box;
			line-height: 40upx
		}
		&-btn-box{
			padding: 54upx 30upx;
			
			button{
				width: 100%;
				height: 80upx;
				border-radius: 80upx;
				font-size: $fontSize;
				background-color: #3682FF;
				line-height: 80upx
			}
		}
	}
	
	@keyframes star_move{
		from{
			width: 50upx;
			height: 50upx;
			transform: rotate(180deg)
		}
		to{
			width: 32upx;
			height: 32upx;
			transform: rotate(0)
		}
	}
	@keyframes star_rotate{
		0%{
			transform: rotateY(360deg)
		}
		100%{
			transform: rotateY(180deg)
		}
	}
	.example-body {
		/* #ifndef APP-PLUS-NVUE */
		display: flex;
		/* #endif */
		flex-direction: row;
		justify-content: flex-start;
		flex-wrap: wrap;
		padding: 20rpx;
	}
	.tag-view {
		/* #ifndef APP-PLUS-NVUE */
		display: flex;
		/* #endif */
		flex-direction: column;
		margin: 10rpx 8rpx;
		justify-content: center;
	}
</style>
