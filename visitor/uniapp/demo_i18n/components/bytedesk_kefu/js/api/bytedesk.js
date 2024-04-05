/* eslint-disable no-undef */
import * as constants from '../constants.js'
import * as httpApi from './httpapi.js'
import * as stompApi from './stompapi.js'

// 简单初始化
export function init(subDomain, appKey) {
	// console.log('bytedesk:', subDomain, appKey)
	// 本地持久化
	uni.setStorageSync(constants.subDomain, subDomain)
	uni.setStorageSync(constants.appKey, appKey)
	//
	httpApi.anonymousLogin(subDomain, appKey, function(result) {
		// console.log('login success:', result)
	}, function(error) {
		console.log('login error:', error)
	})
}

// 带有回调的初始化
export function initWithCallback(subDomain, appKey, successcb, failedcb) {
	// console.log('bytedesk:', subDomain, appKey)
	// 本地持久化
	uni.setStorageSync(constants.subDomain, subDomain)
	uni.setStorageSync(constants.appKey, appKey)
	//
	httpApi.anonymousLogin(subDomain, appKey, function(result) {
		console.log('anonymousLogin success:', result)
		successcb(result)
	}, function(error) {
		console.log('login error:', error)
		failedcb(error)
	})
}

// 自定义用户名初始化登录
export function initWithUsername(username, subDomain, appKey) {
	//
	initWithUsernameAndNickname(username, "", subDomain, appKey)
}

// 自定义用户名 + 昵称初始化登录
export function initWithUsernameAndNickname(username, nickname, subDomain, appKey) {
	//
	initWithUsernameAndNicknameAndAvatar(username, nickname, "", subDomain, appKey);
}

// 自定义用户名 + 昵称 + 头像初始化登录
export function initWithUsernameAndNicknameAndAvatar(username, nickname, avatar, subDomain, appKey) {
	// 本地持久化
	uni.setStorageSync(constants.subDomain, subDomain)
	uni.setStorageSync(constants.appKey, appKey)
	//
	httpApi.userLogin(username, nickname, avatar, subDomain, appKey, function(result) {
		// console.log('login success:', result)
	}, function(error) {
		console.log('login error:', error)
	})
}

// 建立长连接
export function connect() {
	//
	stompApi.connect(null, function() {
		// 长连接成功回调
	})
}