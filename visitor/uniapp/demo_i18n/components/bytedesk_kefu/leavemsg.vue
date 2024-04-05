<template>
    <view class="nvue-page-root">
		<view style="margin-left: 20rpx; height: 60rpx; color: gray;">{{ leaveMessageTip }}</view>
        <view class="uni-common-mt">
            <view class="uni-form-item uni-column">
                <view class="title"><text class="uni-form-item__title">{{ $t('mobile') }}</text></view>
                <view class="uni-input-wrapper">
                    <input v-model="mobile" class="uni-input"/>
                </view>
            </view>
        </view>
		<view class="uni-common-mt">
		    <view class="uni-form-item uni-column">
		        <view class="title"><text class="uni-form-item__title">{{ $t('leaveMessage') }}</text></view>
				<view class="uni-textarea">
					<textarea v-model="content"/>
				</view>
		    </view>
		</view>
		<button class="my-button" type="default" @click="subMit">{{ $t('submit') }}</button>
    </view>
</template>
<script>
// 
import * as httpApi from '@/components/bytedesk_kefu/js/api/httpapi.js'
// 
export default {
	data() {
		return {
			leaveMessageTip: this.$t('leaveMessageTip'), //'当前客服不在线，请留言',
			workGroupWid: '',
			agentUid: '',
			type: '',
			email: '',
			mobile: '',
			content: ''
		}
	},
	onLoad(option) {
		this.workGroupWid = option.wid
		this.agentUid = option.aid
		this.type = option.type
		this.leaveMessageTip = option.tip
	},
	methods: {
		subMit () {
			//
			uni.showLoading({title: this.$t('leaveMessing'), mask:true});
			// console.log('leavemsg:', this.workGroupWid, this.agentUid, 
			// 	this.type, this.mobile, this.email, this.content);
			httpApi.saveLeaveMessage(this.workGroupWid, this.agentUid, 
				this.type, this.mobile, this.email, this.content, function(response) {
				console.log('saveLeaveMessage success:', response)
				if (response.status_code === 200) {
					uni.showToast({ title: this.$t('leaveMessageSuccess'), duration: 2000 });
				} else {
					uni.showToast({ title: response.message, duration: 2000 });
				}
				uni.navigateBack();
			}, function(error) {
				console.log('saveLeaveMessage error', error)
				uni.showToast({ title: this.$t('leaveMessageFailed'), duration: 2000 });
			})
		}
	}
}
</script>

<style scoped>
	
    .nvue-page-root {
        background-color: #F8F8F8;
        padding-bottom: 20px;
		margin-top: 20rpx;
    }

    .title {
        padding: 5px 13px;
    }

    .uni-form-item__title {
        font-size: 16px;
        line-height: 24px;
    }

    .uni-input-wrapper {
        /* #ifndef APP-NVUE */
        display: flex;
        /* #endif */
        padding: 8px 13px;
        flex-direction: row;
        flex-wrap: nowrap;
        background-color: #FFFFFF;
    }

    .uni-input {
        height: 28px;
        line-height: 28px;
        font-size: 15px;
        padding: 0px;
        flex: 1;
        background-color: #FFFFFF;
    }
	
	.my-button {
		margin-top: 20rpx;
	}

</style>
