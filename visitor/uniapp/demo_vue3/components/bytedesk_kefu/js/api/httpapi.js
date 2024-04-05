/* eslint-disable no-undef */
import * as constants from '../constants.js'
// import { username } from '../constants.js';

/**
 * 匿名登录，首先检测本地是否以及存在用户，如果没有，则注册，否则直接登录
 * @param {Object} subDomain 企业号，注：登录后台->客服管理->客服账号->企业号
 * @param {Object} appKey 渠道管理-》uniapp中创建应用获取
 * @param {Object} successcb 成功回调
 * @param {Object} failedcb 失败回调
 */
export function anonymousLogin(subDomain, appKey, successcb, failedcb) {
	// console.log('anonymousLogin: ' + subDomain);
	try {
	    const username = uni.getStorageSync(constants.username);
	    if (username != null && username.length > 0) {
	        // console.log(username);
			// 登录
			let password = username
			login(username, password, subDomain, successcb, failedcb);
	    } else {
			// 注册
			registerAnonymous(subDomain, function(result) {
				//
				let username = result.data.username;
				let password = username
				login(username, password, subDomain, successcb, failedcb);
			}, function(error) {
				console.log('anonymousLogin:', error)
			})
		}
	} catch (e) {
	    // error
		console.log('anonymous storage error:' + e);
	}
}

// 自定义用户名 + 昵称 + 头像初始化登录
export function userLogin(username, nickname, avatar, subDomain, appKey, successcb, failedcb) {
	// console.log('userLogin: ' + subDomain);
	try {
	    const usernameLocal = uni.getStorageSync(constants.username);
		const passwordLocal = uni.getStorageSync(constants.password);
	    if (usernameLocal != null && usernameLocal.length > 0) {
	        // console.log(usernameLocal, passwordLocal);
			// 登录
			login(usernameLocal, passwordLocal, subDomain, successcb, failedcb);
	    } else {
			// 注册
			let password = username
			registerUser(username, nickname, password, avatar, subDomain, function(result) {
				//
				let usernameCompose = username + '@' + subDomain; //result.data.username;
				// console.log(usernameCompose, password);
				login(usernameCompose, password, subDomain, successcb, failedcb);
			}, function(error) {
				console.log('userLogin:', error)
			})
		}
	} catch (e) {
	    // error
		console.log('anonymous storage error:' + e);
	}
}

// 自定义用户名登录
export function login(username, password, subDomain, successcb, failedcb) {
	// console.log('login:', username, password, subDomain);
	//
	oauth(username, password, subDomain, function (result) {
		// console.log('oauth:', result)
		console.log('login success')
		successcb(result.data)
	}, function(error) {
		console.log('login error')
		failedcb(error)
	})
}

export function code2Session (code, successcb, failedcb) {
  uni.request({
    url: constants.API_BASE_URL + '/visitors/api/v1/code2Session2',
    data: {
      'code': code,
    },
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

export function oauth(username, password, subDomain, successcb, failedcb) {
  // console.log('oauth/token: ', username, password)
  //
  uni.request({
    url: constants.API_BASE_URL + '/visitor/token', //'/oauth/token',
    data: {
      username: username,
	  password: password,
      grant_type: 'password',
      scope: 'all'
    },
    header: {
      'Authorization': 'Basic Y2xpZW50OnNlY3JldA==',
      'content-type': 'application/x-www-form-urlencoded'
    },
    method: 'POST',
    success(res) {
	  // TODO: token 持久化
	  // console.log('oauth:', res);
	  // {
	  // 	"data": {
	  // 		"access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MDU4NTkxMDQsInVzZXJfbmFtZSI6IjIwMjAxMDIxMTU1ODIzMyIsImp0aSI6IjQwZTNlY2M1LWRmOGYtNGJjNi05NDIyLTdjNmQ4MGFiYTE5YSIsImNsaWVudF9pZCI6ImNsaWVudCIsInNjb3BlIjpbImFsbCJdfQ.yRR9Tk6blGLWuvHJw3pmZphdEVzvMrwjRmvgHZdMYVo",
	  // 		"expires_in": 2591999,
	  // 		"jti": "40e3ecc5-df8f-4bc6-9422-7c6d80aba19a",
	  // 		"refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MDU4NTkxMDQsInVzZXJfbmFtZSI6IjIwMjAxMDIxMTU1ODIzMyIsImp0aSI6ImRhNDlkMTE1LTU4MGYtNGNhZC04Y2ZhLWYyZjlkMjc0MWY3MSIsImNsaWVudF9pZCI6ImNsaWVudCIsInNjb3BlIjpbImFsbCJdLCJhdGkiOiI0MGUzZWNjNS1kZjhmLTRiYzYtOTQyMi03YzZkODBhYmExOWEifQ.15bidIWrFhIa-tdFZYsTdglJi61t9W0XCAlNy2Wka0w",
	  // 		"scope": "all",
	  // 		"token_type": "bearer"
	  // 	},
	  // 	"errMsg": "request:ok",
	  // 	"statusCode": 200
	  // }
	  if (res.data != null && res.data.access_token != null) {
		try {
		    uni.setStorageSync(constants.isLogin, true);
			uni.setStorageSync(constants.username, username);
			uni.setStorageSync(constants.password, password);
			uni.setStorageSync(constants.subDomain, subDomain);
			uni.setStorageSync(constants.accessToken, res.data.access_token)
		} catch (e) {
		    // error
		}
		successcb(res.data)
	  } else {
		  failedcb(res)
	  }
    },
    fail(res) {
      failedcb(res.data)
    }
  })
}

export function smsOauth(mobile, code, successcb, failedcb) {
  // console.log('mobile/token: ', mobile)
  uni.request({
    url: constants.API_BASE_URL + '/mobile/token',
    data: {
      mobile: mobile,
	  code: code,
      grant_type: 'mobile',
      scope: 'all'
    },
    header: {
      'Authorization': 'Basic Y2xpZW50OnNlY3JldA==',
      'content-type': 'application/x-www-form-urlencoded'
    },
    method: 'POST',
    success(res) {
      if (res.data != null && res.data.access_token != null) {
		try {
			uni.setStorageSync(constants.isLogin, true);
			uni.setStorageSync(constants.isLoginMobile, true);
			uni.setStorageSync(constants.accessToken, res.data.access_token)
		} catch (e) {
			// error
		}
		successcb(res.data)
      } else {
        failedcb(res)
      }
    },
    fail(res) {
      failedcb(res.data)
    }
  })
}

export function wechatOauth(unionid, successcb, failedcb) {
  // console.log('wechat/token: ', unionid)
  uni.request({
    url: constants.API_BASE_URL + '/wechat/token',
    data: {
      unionid: unionid,
      grant_type: 'wechat',
      scope: 'all'
    },
    header: {
      'Authorization': 'Basic Y2xpZW50OnNlY3JldA==',
      'content-type': 'application/x-www-form-urlencoded'
    },
    method: 'POST',
    success(res) {
      if (res.data != null && res.data.access_token != null) {
      		try {
      			uni.setStorageSync(constants.isLogin, true);
				uni.setStorageSync(constants.isLoginMobile, true);
      			uni.setStorageSync(constants.accessToken, res.data.access_token)
      		} catch (e) {
      			// error
      		}
      		successcb(res.data)
      } else {
        failedcb(res)
      }
    },
    fail(res) {
      failedcb(res.data)
    }
  })
}

// 注册接口
export function emailRegister(email, password, successcb, failedcb) {
  // console.log('register:', email, password)
  uni.request({
    url: constants.API_BASE_URL + '/visitor/api/register',
    data: {
      'email': email,
	  'password': password,
      'client': constants.client
    },
    method: 'POST',
    header: {
      'content-type': 'application/json' // 默认值
    },
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 注册接口，自定义昵称
export function emailRegisterWithNickname(email, password, nickname, successcb, failedcb) {
  // console.log('register:', email, password)
  uni.request({
    url: constants.API_BASE_URL + '/visitor/api/register',
    data: {
      'email': email,
	  'password': password,
	  'nickname': nickname,
      'client': constants.client
    },
    method: 'POST',
    header: {
      'content-type': 'application/json' // 默认值
    },
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 手机号注册
export function mobileRegister(mobile, nickname, successcb, failedcb) {
  // console.log('register:', mobile, password)
  uni.request({
    url: constants.API_BASE_URL + '/visitor/api/register/mobile',
    data: {
      'mobile': mobile,
	  'nickname': nickname,
      'client': constants.client
    },
    method: 'POST',
    header: {
      'content-type': 'application/json' // 默认值
    },
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

export function wechatRegister(openid, unionid, nickname, avatar, successcb, failedcb) {
  // console.log('wechatRegister:', openid, unionid, nickname, avatar)
  uni.request({
    url: constants.API_BASE_URL + '/visitors/api/v1/register/wechat',
    data: {
      'openid': openid,
      'unionid': unionid,
      'nickname': nickname,
      'avatar': avatar,
      'client': constants.client
    },
    method: 'POST',
    header: {
      'content-type': 'application/json' // 默认值
    },
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

export function registerAnonymous(subDomain, successcb, failedcb) {
  // console.log('registerAnonymous:' + subDomain)
  //
  uni.request({
    url: constants.API_BASE_URL + '/visitor/api/username',
    data: {
      'subDomain': subDomain,
      'client': constants.client
    },
    method: 'GET',
    header: {
      'content-type': 'application/json' // 默认值
    },
    success (res) {
	  // console.log('registerAnonymous success:' + res)
	  // {
	  // 	"data": {
	  // 		"data": {
	  // 			"acceptStatus": null,
	  // 			"uid": "202010211558234",
	  // 			"agent": false, //...更多字段
	  // 		},
	  // 		"message": "创建用户名成功",
	  // 		"status_code": 200
	  // 	},
	  // 	"errMsg": "request:ok",
	  // 	"statusCode": 200
	  // }
	  try {
	      uni.setStorageSync(constants.uid, res.data.data.uid);
		  uni.setStorageSync(constants.username, res.data.data.username);
		  uni.setStorageSync(constants.nickname, res.data.data.nickname);
		  uni.setStorageSync(constants.avatar, res.data.data.avatar);
		  uni.setStorageSync(constants.description, res.data.data.description);
		  uni.setStorageSync(constants.subDomain, res.data.data.subDomain);
	  } catch (e) {
	      // error
	  }
      successcb(res.data)
    },
    fail (res) {
	  console.log('registerAnonymous failed:', res)
      failedcb(res.data)
    }
  })
}

// 自定义用户名 注册
export function registerUser(username, nickname, password, avatar, subDomain, successcb, failedcb) {
  //
  uni.request({
    url: constants.API_BASE_URL + '/visitor/api/register/user',
    data: {
	  'username': username,
	  'nickname': nickname,
	  'password': password,
	  'avatar': avatar,
      'subDomain': subDomain,
      'client': constants.client
    },
    method: 'POST',
    header: {
      'content-type': 'application/json' // 默认值
    },
    success (res) {
		// console.log('registerUser success:', res.data.data)
		if (res.data.status_code === 200) {
			try {
			  uni.setStorageSync(constants.uid, res.data.data.uid);
			  // console.log(res.data.data.uid)
			  uni.setStorageSync(constants.username, res.data.data.username);
			  uni.setStorageSync(constants.password, password);
			  uni.setStorageSync(constants.nickname, res.data.data.nickname);
			  uni.setStorageSync(constants.avatar, res.data.data.avatar);
			  uni.setStorageSync(constants.description, res.data.data.description);
			  uni.setStorageSync(constants.subDomain, res.data.data.subDomain);
			} catch (e) {
				// error
			}
		} else {
			try {
			  uni.setStorageSync(constants.uid, res.data.data);
			  uni.setStorageSync(constants.username, username + '@' + subDomain);
			  uni.setStorageSync(constants.password, password);
			  uni.setStorageSync(constants.nickname, nickname);
			  uni.setStorageSync(constants.avatar, avatar);
			  uni.setStorageSync(constants.description, '');
			  uni.setStorageSync(constants.subDomain, subDomain);
			} catch (e) {
				// error
			}
		}
		successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 请求客服会话
export function requestThread(wid, type, aid, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/thread/request',
    data: {
      wId: wid,
      type: type,
      aId: aid,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
	  // console.log('request thread:', res)
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 请求人工客服，不管此工作组是否设置为默认机器人，只要有人工客服在线，则可以直接对接人工
export function requestAgent(wid, type, aid, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/thread/request/agent',
    data: {
      wId: wid,
      type: type,
      aId: aid,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 请求客服会话-扫小程序码
export function requestThreadScan(id, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/thread/scan',
    data: {
      id: id,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
	  // console.log('request thread:', res)
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 请求技能组客服会话
export function requestWorkGroupThreadV2(wid, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/v2/thread/workGroup',
    data: {
      wId: wid,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 客服端：当前进行中会话
export function getThreads(page, size, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/thread/get',
    data: {
	  page: page,
	  size: size,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 客服端：客服获取某访客历史会话
export function getHistoryThreads(uid, page, size, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/thread/history',
    data: {
	  uid: uid,
	  page: page,
	  size: size,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 客服端分页加载-客服自己的历史会话：客服会话
export function getThreadHistoryRecords(page, size, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/thread/history/records',
    data: {
	  page: page,
	  size: size,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 访客端-历史会话
export function getVisitorThreads(page, size, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/thread/visitor/history',
    data: {
	  page: page,
	  size: size,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 加载排队列表
export function getQueues(page, size, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/queue/get',
    data: {
	  page: page,
	  size: size,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

/**
 * 接入排队访客
 * @param {*} successcb 成功回调
 * @param {*} failedcb 失败回调
 */
export function acceptQueue(qid, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
  uni.request({
    url: constants.API_BASE_URL + '/api/queue/accept',
    data: {
      'qid': qid,
      'client': constants.client
    },
	header: header,
    method: 'POST',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

/**
 * 忽略排队访客
 * @param {*} successcb 成功回调
 * @param {*} failedcb 失败回调
 */
export function ignoreQueue(qid, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
  uni.request({
    url: constants.API_BASE_URL + '/api/queue/ignore',
    data: {
      'qid': qid,
      'client': constants.client
    },
	header: header,
    method: 'POST',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

/**
 * 设置客服接待状态
 * @param {*} successcb 成功回调
 * @param {*} failedcb 失败回调
 */
export function setAcceptStatus(status, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
  uni.request({
    url: constants.API_BASE_URL + '/api/status/set',
    data: {
      'status': status,
      'client': constants.client
    },
	header: header,
    method: 'POST',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

/**
 * 设置 自动回复内容
 * @param {*} successcb 成功回调
 * @param {*} failedcb 失败回调
 */
export function updateAutoReply(autoReply, content, imageUrl, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
  uni.request({
	url: constants.API_BASE_URL + '/api/v1/autoreply/update',
	data: {
	  'autoReply': autoReply,
	  'content': content,
	  'imageUrl': imageUrl,
	  'client': constants.client
	},
	header: header,
	method: 'POST',
	success (res) {
	  successcb(res.data)
	},
	fail (res) {
	  failedcb(res.data)
	}
  })
}

/**
 * 客服关闭会话
 * @param {*} successcb 成功回调
 * @param {*} failedcb 失败回调
 */
export function agentCloseThread(tid, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
  uni.request({
	url: constants.API_BASE_URL + '/api/thread/agent/close',
	data: {
	  'tid': tid,
	  'client': constants.client
	},
	header: header,
	method: 'POST',
	success (res) {
	  successcb(res.data)
	},
	fail (res) {
	  failedcb(res.data)
	}
  })
}

// 查询技能组在线状态
export function getWorkGroupStatus(workGroupWid, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/status/workGroup',
    data: {
	  wid: workGroupWid,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 查询指定客服在线状态
export function getAgentStatus(agentUid, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/status/agent',
    data: {
	  uid: agentUid,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 加载同一个公司内-全部当前在线客服
export function getOnlineAgents(successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/user/online/list',
    data: {
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}


// 根据uid加载更多聊天记录
export function loadHistoryMessages(uid, page, size, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/messages/user',
    data: {
	  uid: uid,
	  page: page,
	  size: size,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
	  // console.log('loadHistoryMessages:', res)
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 根据topic加载更多聊天记录
export function loadHistoryMessagesByTopic(topic, page, size, successcb, failedcb) {
  // console.log('loadHistoryMessagesByTopic:', topic, page, size)
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/messages/topic',
    data: {
	  topic: topic,
	  page: page,
	  size: size,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
	  // console.log('loadHistoryMessages:', res)
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 根据用户id加载聊天记录
export function loadHistoryMessagesById(id, page, size, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/messages/user/id',
    data: {
	  id: id,
	  page: page,
	  size: size,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
	  // console.log('loadHistoryMessages:', res)
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 访客端-根据技能组wid或指定客服uid, 统一为wid-分页拉取聊天记录。注意：仅用于访客端
export function loadHistoryMessagesByWid(wid, page, size, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/messages/user/wid',
    data: {
	  wid: wid,
	  page: page,
	  size: size,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
	  // console.log('loadHistoryMessages:', res)
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 查询当前用户-某技能组wid或指定客服未读消息数目
// 注意：技能组wid或指定客服唯一id
// 适用于 访客 和 客服
export function getUnreadCount(wid, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/messages/unreadCount',
    data: {
	  wid: wid,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
	  // console.log('getUnreadCount:', res)
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 访客端-查询访客所有未读消息数目
export function getUnreadCountVisitor(successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/messages/unreadCount/visitor',
    data: {
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
	  // console.log('getUnreadCount:', res)
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 客服端-查询客服所有未读消息数目
export function getUnreadCountAgent(successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/messages/unreadCount/agent',
    data: {
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
	  // console.log('getUnreadCount:', res)
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 加载从某条消息记录之后的消息
export function loadMessagesFrom(uid, id, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/messages/user/from',
    data: {
	  uid: uid,
	  id: id,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
	  // console.log('loadMessagesFrom:', res)
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 加载用户个人资料
export function getProfile (successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/user/profile',
    data: {
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 通过aid，请求智能答案
export function queryAnswer (tid, aid, mid, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/v2/answer/query',
    data: {
	  tid: tid,
	  aid: aid,
	  mid: mid,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 查询某分类下 所有问题列表
export function queryCategoryAnswers (tid, cid, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/v2/answer/category',
    data: {
	  tid: tid,
	  cid: cid,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 输入内容，请求智能答案
export function messageAnswer (wid, content, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/elastic/robot/message', // '/api/v2/answer/message',
    data: {
	  wid: wid,
	  content: content,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 评价智能问答结果
export function rateAnswer(aid, mid, rate, successcb, failedcb) {
  // console.log('rateAnswer:', aid, mid, rate);
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/answer/rate',
    data: {
      'aid': aid,
	  'mid': mid,
	  'rate': rate,
      'client': constants.client
    },
  	header: header,
    method: 'POST',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

//
export function getCuws (successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/cuw/get',
    data: {
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 自定义用户昵称
export function updateNickname(nickname, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
  uni.request({
    url: constants.API_BASE_URL + '/api/user/nickname',
    data: {
      'nickname': nickname,
      'client': constants.client
    },
	header: header,
    method: 'POST',
    success (res) {
	  try {
	  	uni.setStorageSync(constants.nickname, nickname);
	  } catch (e) {
	      // error
	  }
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 自定义用户备注
export function updateDescription(description, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
  uni.request({
    url: constants.API_BASE_URL + '/api/user/description',
    data: {
      'description': description,
      'client': constants.client
    },
	header: header,
    method: 'POST',
    success (res) {
	  try {
	  	uni.setStorageSync(constants.description, description);
	  } catch (e) {
	      // error
	  }
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 自定义用户头像
export function updateAvatar(avatar, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
  uni.request({
    url: constants.API_BASE_URL + '/api/user/avatar',
    data: {
      'avatar': avatar,
      'client': constants.client
    },
	header: header,
    method: 'POST',
    success (res) {
		try {
			uni.setStorageSync(constants.avatar, avatar);
		} catch (e) {
		    // error
		}
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 访客端-设置个人资料，一次性调用同时设置：昵称、头像、备注
export function updateProfile(nickname, avatar, description, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
  uni.request({
    url: constants.API_BASE_URL + '/api/user/update/visitor/profile',
    data: {
	  'nickname': nickname,
      'avatar': avatar,
	  'description': description,
      'client': constants.client
    },
	header: header,
    method: 'POST',
    success (res) {
		try {
			uni.setStorageSync(constants.avatar, avatar);
		} catch (e) {
		    // error
		}
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}


// 在长连接断开的情况下，发送消息
export function sendMessageRest(json, successcb, failedcb) {
  // console.log('sendMessageRest:' + json)
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/messages/send',
    data: {
      'json': json,
      'client': constants.client
    },
  	header: header,
    method: 'POST',
    success (res) {
      successcb(res.data)
	  // successcb(json)
    },
    fail (res) {
      failedcb(res.data)
	  // failedcb(json)
    }
  })
}

// 满意度评价
export function rate(tid, score, note, invite, successcb, failedcb) {
  console.log('rate:', tid, score, note, invite);
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/rate/do',
    data: {
      'tid': tid,
	  'score': score,
	  'note': note,
	  'invite': invite,
      'client': constants.client
    },
  	header: header,
    method: 'POST',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 查询被评价会话详情
export function rateDetail(tid, successcb, failedcb) {
  console.log('rateDetail:', tid);
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/rate/detail',
    data: {
  	  tid: tid,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 客服端-加载黑名单列表
export function getBlocks (page, size, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/block/get',
    data: {
	  page: page,
	  size: size,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 客服端-拉黑访客
export function addBlock(uid, note, successcb, failedcb) {
  // console.log('add block:', uid, note);
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/block/add',
    data: {
      'uid': uid,
	  'note': note,
      'client': constants.client
    },
  	header: header,
    method: 'POST',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 客服端-拉黑访客2
export function addBlock2(uid, note, type, uuid, successcb, failedcb) {
  // console.log('add block:', uid, note);
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/block/add',
    data: {
      'uid': uid,
	  'note': note,
	  'type': type,
	  'uuid': uuid,
      'client': constants.client
    },
  	header: header,
    method: 'POST',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 客服端-取消拉黑访客
export function removeBlock(bid, successcb, failedcb) {
  // console.log('add block:', uid, note);
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/block/remove',
    data: {
      'bid': bid,
      'client': constants.client
    },
  	header: header,
    method: 'POST',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 将获取到的小程序openid绑定到当前用户
export function bindMiniOpenid(openid, successcb, failedcb) {
  console.log('bindMiniOpenid:' + openid)
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/user/bind/mini/openid',
    data: {
      'miniOpenId': openid,
      'client': constants.client
    },
  	header: header,
    method: 'POST',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 提交留言
export function saveLeaveMessage(workGroupWid, agentUid, type, mobile, email, content, successcb, failedcb) {
  // console.log('saveLeaveMessage:' + content)
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/leavemsg/save',
    data: {
      'wid': workGroupWid,
	  'aid': agentUid,
	  'type': type,
	  'mobile': mobile,
	  'email': email,
	  'content': content,
      'client': constants.client
    },
  	header: header,
    method: 'POST',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

/**
 * 会话免打扰
 * uid：当前用户uid
 * tid：会话tid
 * @param {*} successcb 成功回调
 * @param {*} failedcb 失败回调
 */
export function markNodisturb(uid, tid, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
	//
	uni.request({
	  url: constants.API_BASE_URL + '/api/v2/thread/mark/nodisturb',
	  data: {
	    'uid': uid,
		'tid': tid,
	    'client': constants.client
	  },
	  header: header,
	  method: 'POST',
	  success (res) {
	    successcb(res.data)
	  },
	  fail (res) {
	    failedcb(res.data)
	  }
	})
}

/**
 * 取消会话免打扰
 * uid：当前用户uid
 * tid：会话tid
 * @param {*} successcb 成功回调
 * @param {*} failedcb 失败回调
 */
export function unmarkNodisturb(uid, tid, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
	//
	uni.request({
	  url: constants.API_BASE_URL + '/api/v2/thread/unmark/nodisturb',
	  data: {
	    'uid': uid,
		'tid': tid,
	    'client': constants.client
	  },
	  header: header,
	  method: 'POST',
	  success (res) {
	    successcb(res.data)
	  },
	  fail (res) {
	    failedcb(res.data)
	  }
	})
}

/**
 * 上传设备信息-浏览器
 * 
 * @param {*} successcb 成功回调
 * @param {*} failedcb 失败回调
 */
export function uploadDeviceInfoBrowser(browserInfo, deviceType, fingerprint, language, os, osVersion, referrer, url, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
	//
	uni.request({
	  url: constants.API_BASE_URL + '/api/fingerprint2/browser',
	  data: {
	    'browserInfo': encodeURI(browserInfo), // 浏览器
		'deviceType': encodeURI(deviceType), // 设备类型
		'fingerprint': encodeURI(fingerprint), // 设备指纹
		'language': encodeURI(language), // 语言
		'os': encodeURI(os), // 操作系统
		'osVersion': encodeURI(osVersion), // 操作系统版本
		'referrer': encodeURI(referrer), // 来源网址
		'url': encodeURI(url), // 当前网址
	    'client': constants.client
	  },
	  header: header,
	  method: 'POST',
	  success (res) {
	    successcb(res.data)
	  },
	  fail (res) {
	    failedcb(res.data)
	  }
	})
}

/**
 * 上传设备信息-安卓
 * 
 * @param {*} successcb 成功回调
 * @param {*} failedcb 失败回调
 */
export function uploadDeviceInfoAndroid(sdkVersion, osVersion, deviceModel, brand, languageInfo, appVersion, appVersionName, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
	//
	uni.request({
	  url: constants.API_BASE_URL + '/api/fingerprint2/android/deviceInfo',
	  data: {
	    'sdkVersion': encodeURI(sdkVersion), // sdk版本
		'osVersion': encodeURI(osVersion), // 系统版本号
		'deviceModel': encodeURI(deviceModel), // 手机型号
		'brand': encodeURI(brand), // 手机厂商
		'languageInfo': encodeURI(languageInfo), // 系统语言
		'appVersion': encodeURI(appVersion), // 应用版本号
		'appVersionName': encodeURI(appVersionName), // 应用版本名
	    'client': constants.client
	  },
	  header: header,
	  method: 'POST',
	  success (res) {
	    successcb(res.data)
	  },
	  fail (res) {
	    failedcb(res.data)
	  }
	})
}

/**
 * 上传设备信息-iOS
 * 
 * @param {*} successcb 成功回调
 * @param {*} failedcb 失败回调
 */
export function uploadDeviceInfoIos(os, osVersion, deviceName, deviceModel, appName, languageInfo, appVersion, appCountry, successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
	//
	uni.request({
	  url: constants.API_BASE_URL + '/api/fingerprint2//ios/deviceInfo',
	  data: {
	    'os': encodeURI(os), // 操作系统
		'osVersion': encodeURI(osVersion), // 系统版本号
		'deviceName': encodeURI(deviceName), // 设备名称
		'deviceModel': encodeURI(deviceModel), // 手机型号
		'appName': encodeURI(appName), // 应用名
		'languageInfo': encodeURI(languageInfo), // 系统语言
		'appVersion': encodeURI(appVersion), // 应用版本号
		'appCountry': encodeURI(appCountry), // 所属国家
	    'client': constants.client
	  },
	  header: header,
	  method: 'POST',
	  success (res) {
	    successcb(res.data)
	  },
	  fail (res) {
	    failedcb(res.data)
	  }
	})
}


// 拉取技能组-快捷按钮
export function getQuickButtons (workGroupWid, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/quickbutton/query/workGroup',
    data: {
	  wid: workGroupWid,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 拉取技能组-转人工关键词
export function getTransferWords (workGroupWid, successcb, failedcb) {
  //
  let header = visitorApiHeader()
  if (header['Authorization'] === undefined) {
    failedcb('未登录，请务必确保提前调用init接口')
    return
  }
  //
  uni.request({
    url: constants.API_BASE_URL + '/api/transferword/query/workGroup',
    data: {
	  wid: workGroupWid,
      client: constants.client
    },
    header: header,
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

// 技能组设置-如：置顶语
export function getPrechatSettings (workGroupWid, successcb, failedcb) {
  //
  uni.request({
    url: constants.API_BASE_URL + '/visitor/api/prechat/settings',
    data: {
	  wid: workGroupWid,
      client: constants.client
    },
    method: 'GET',
    success (res) {
      successcb(res.data)
    },
    fail (res) {
      failedcb(res.data)
    }
  })
}

/**
 * 登出
 * @param {*} successcb 成功回调
 * @param {*} failedcb 失败回调
 */
export function logout(successcb, failedcb) {
	//
	let header = visitorApiHeader()
	if (header['Authorization'] === undefined) {
	  failedcb('未登录，请务必确保提前调用init接口')
	  return
	}
	uni.request({
		url: constants.API_BASE_URL + '/api/user/logout',
		data: {
		  'client': constants.client
		},
		header: header,
		method: 'POST',
		success(res) {
		  try {
		  	uni.setStorageSync(constants.isLogin, false);
		  	uni.setStorageSync(constants.isLoginMobile, false);
			uni.setStorageSync(constants.uid, '');
			uni.setStorageSync(constants.username, '');
			uni.setStorageSync(constants.password, '');
			uni.setStorageSync(constants.subDomain, '');
		  	uni.setStorageSync(constants.accessToken, '')
		  } catch (e) {
		  	// error
		  }
		  successcb(res.data)
		},
		fail(res) {
		  console.log('failed:' + res.data)
		  failedcb(res.data)
		},
		complete(res) {
		  console.log('completed:' + res.data)
		}
	})
}

export function visitorApiHeader() {
  let header = {}
  let accessToken = uni.getStorageSync(constants.accessToken)
  if (accessToken) {
    header = {
	  'content-type': 'application/json',
      'Authorization': 'Bearer ' + accessToken,
    }
  }
  return header
}