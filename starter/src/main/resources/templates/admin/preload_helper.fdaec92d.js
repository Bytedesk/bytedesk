!function(){"use strict";var t="/admin/".replace(/([^/])$/,"$1/"),e=location.pathname,n=e.startsWith(t)&&decodeURI("/".concat(e.slice(t.length)));if(n){var a=document,c=a.head,r=a.createElement.bind(a),i=function(t,e,n){var a,c=e.r[t]||(null===(a=Object.entries(e.r).find((function(e){var n=e[0];return new RegExp("^".concat(n.replace(/\/:[^/]+/g,"/[^/]+").replace("/*","/.+"),"$")).test(t)})))||void 0===a?void 0:a[1]);return null==c?void 0:c.map((function(t){var a=e.f[t][1],c=e.f[t][0];return{type:c.split(".").pop(),url:"".concat(n.publicPath).concat(c),attrs:[["data-".concat(e.b),"".concat(e.p,":").concat(a)]]}}))}(n,{"p":"admin","b":"webpack","f":[["p__Dashboard__Service__Quality__QualityStatistic.7f089fe8.async.js",13],["p__Dashboard__Knowledge__Faq__FaqData.f9c908ef.chunk.css",79],["p__Dashboard__Knowledge__Faq__FaqData.4d20e8e4.async.js",79],["100.ec9fc81d.async.js",100],["136.a2755867.async.js",136],["p__Dashboard__Service__Black__BlackStatistic.996ab4d0.async.js",210],["p__Dashboard__Service__Monitor__index.c69c5403.async.js",296],["p__Dashboard__Knowledge__Taboo__TabooStatistic.856a31e5.async.js",319],["359.f2109a99.async.js",359],["p__Dashboard__Knowledge__index.4e0e2986.async.js",363],["498.c3ea4cba.async.js",498],["p__Dashboard__Marketing__Blog__BlogStatistic.9b6aef5f.async.js",510],["571.e2278dad.async.js",571],["814.7291a658.async.js",814],["p__Dashboard__Ai__LlmModel__index.b8a1c3ad.async.js",950],["p__Dashboard__Super__LlmModel__index.4d95f322.async.js",981],["p__Dashboard__Service__Transfer__TransferTable.8908b67a.async.js",1048],["1115.da31f196.async.js",1115],["p__Dashboard__Feedback__index.6acd88d6.async.js",1179],["p__Dashboard__Ai__Message__index.e733d68c.async.js",1206],["1276.ff05e817.async.js",1276],["p__Dashboard__Marketing__Blog__index.1c078c18.async.js",1312],["p__Dashboard__Team__Message__MessageTable.8dc5de56.async.js",1398],["p__Dashboard__Marketing__Video__VideoData.72c092d0.async.js",1426],["p__Dashboard__Service__Queue__QueueStatistic.2f154bd3.async.js",1488],["p__Dashboard__Service__Crm__CustomerStatistic.0a1be199.async.js",1490],["p__Dashboard__Team__Notification__index.77d83fcc.async.js",1531],["p__Dashboard__Setting__Money__index.eeb1c174.async.js",1589],["p__Dashboard__Service__Agent__index.240bafbb.async.js",1642],["p__Auth__Register__index.888c6210.async.js",1730],["p__Dashboard__Liangshibao__Course__index.1827311a.async.js",1742],["p__Dashboard__Super__Notification__index.5aef03a8.async.js",1926],["p__Dashboard__Service__Transfer__TransferStatistic.98a9c44c.async.js",2001],["p__Dashboard__Ticket__index.dbfe79cd.async.js",2005],["p__Dashboard__Service__Quality__QualityTable.aebb737f.async.js",2034],["p__Dashboard__Service__Thread__ThreadStatistic.a5aec551.async.js",2097],["2121.f8b9d686.async.js",2121],["p__Dashboard__Service__Thread__index.4cb98a19.async.js",2148],["p__Dashboard__Ai__Flow__index.0a848f75.async.js",2281],["p__Dashboard__Ai__Upload__index.6fdc8281.async.js",2310],["p__Dashboard__Super__Recharge__index.0b077952.async.js",2319],["p__Dashboard__Team__Thread__ThreadMonitor.05a1ea16.async.js",2375],["p__Dashboard__Plugins__index.f5c5a4a7.async.js",2494],["2519.4fee3097.async.js",2519],["p__Dashboard__Ai__index.19ba0040.chunk.css",2522],["p__Dashboard__Ai__index.aee589ca.async.js",2522],["p__404.a70afcac.async.js",2571],["p__Dashboard__Team__Thread__ThreadSummary.5631426f.async.js",2618],["p__Dashboard__Marketing__Blog__BlogData.f9c908ef.chunk.css",2647],["p__Dashboard__Marketing__Blog__BlogData.afa6268c.async.js",2647],["p__Dashboard__Team__Action__ActionStatistic.52bc2812.async.js",2658],["p__Dashboard__Service__Tag__TagTable.53e5768c.async.js",2701],["p__Dashboard__Team__Thread__index.3198182e.async.js",2708],["p__Dashboard__Ai__Thread__index.06314a7b.async.js",2728],["2847.0d856ae5.async.js",2847],["p__Dashboard__Knowledge__AutoReply__AutoReplyStatistic.9ec44268.async.js",2930],["p__Dashboard__Service__Message__index.4e22afd9.async.js",2949],["p__Dashboard__Flowbot__index.8ca2c944.async.js",2968],["p__Dashboard__Team__Message__MessageStatistic.d64b9985.async.js",2992],["3030.b2235a74.async.js",3030],["p__Dashboard__Marketing__Video__index.d8c694fe.async.js",3034],["p__Dashboard__Super__User__UserData.c0931534.async.js",3088],["3119.e43ba395.async.js",3119],["3120.3aeaacf1.async.js",3120],["p__Dashboard__Marketing__Email__EmailStatistic.99c5fc67.async.js",3151],["p__Dashboard__Liangshibao__Media__index.b90d6623.async.js",3165],["p__Dashboard__Service__Crm__index.2332b1c3.async.js",3186],["p__Dashboard__Knowledge__Keyword__KeywordData.f9c908ef.chunk.css",3340],["p__Dashboard__Knowledge__Keyword__KeywordData.0d6b2338.async.js",3340],["p__Dashboard__Service__Crm__VisitorStatistic.fd58f256.async.js",3395],["p__Dashboard__Service__Thread__ThreadTable.38ecfeab.async.js",3537],["p__Dashboard__Service__LeaveMsg__index.2d093355.async.js",3621],["p__Dashboard__Knowledge__QuickReply__QuickReplyStatistic.e4d0c1db.async.js",3656],["p__Dashboard__Quartz__index.2f2871f6.chunk.css",3848],["p__Dashboard__Quartz__index.577e54cf.async.js",3848],["3918.c972b38b.chunk.css",3918],["3918.0009d0d3.async.js",3918],["3959.09d35f42.async.js",3959],["p__Dashboard__Knowledge__Llm__LlmData.f9c908ef.chunk.css",4167],["p__Dashboard__Knowledge__Llm__LlmData.931c71f8.async.js",4167],["p__Dashboard__Super__Notification__NotificationTable.f9bd24ce.async.js",4204],["p__Dashboard__Super__Recharge__RechargeStatistic.342f7343.async.js",4215],["p__Dashboard__Marketing__Video__VideoStatistic.ec2ceb89.async.js",4262],["p__Dashboard__Team__Role__index.d8f031d9.async.js",4325],["p__Dashboard__Team__Action__ActionTable.0091deb6.async.js",4376],["p__Dashboard__Knowledge__Llm__LlmStatistic.2155f2d1.async.js",4422],["p__Dashboard__Service__Message__MessageStatistic.60054c41.async.js",4460],["4595.25d30f7c.async.js",4595],["4660.c80223b5.async.js",4660],["p__Dashboard__Super__User__index.994535b3.async.js",4693],["4744.0337ee90.async.js",4744],["p__Dashboard__Team__Company__index.b0b93cef.async.js",4747],["p__Dashboard__Ai__Upload__UploadStatistic.75c4a5d3.async.js",4871],["p__Dashboard__Ai__Thread__ThreadStatistic.0a822d17.async.js",4894],["4994.caecc91d.async.js",4994],["5081.2a6a2ba1.async.js",5081],["p__Dashboard__Service__Channel__index.8e76283f.async.js",5131],["p__Dashboard__Super__Push__index.1a245830.async.js",5159],["p__OAuth__Google.2e7cc6a4.async.js",5279],["p__Dashboard__Meiyu__index.9283dd69.async.js",5306],["p__Dashboard__Service__Tag__index.a7c17fd1.async.js",5387],["p__Dashboard__Doceditor__index.f9c908ef.chunk.css",5488],["p__Dashboard__Doceditor__index.c488c946.async.js",5488],["p__Dashboard__Liangshibao__Category__index.f4aeede6.async.js",5522],["p__Dashboard__Service__Rate__index.fa221632.async.js",5684],["p__Dashboard__Team__Notification__NotificationTable.3e15ad4a.async.js",5774],["p__Dashboard__Knowledge__Keyword__index.b04df325.async.js",5847],["p__Dashboard__Service__Transfer__index.c50f6abe.async.js",5906],["p__Dashboard__Knowledge__Article__ArticleData.f9c908ef.chunk.css",5957],["p__Dashboard__Knowledge__Article__ArticleData.865ca44a.async.js",5957],["5976.faae2491.async.js",5976],["p__Dashboard__Super__Role__index.239aa7d6.async.js",6021],["p__Dashboard__Knowledge__QuickReply__index.f2503ccf.async.js",6037],["6075.e7c05a40.async.js",6075],["6106.b99bd13a.async.js",6106],["6118.d334970b.async.js",6118],["p__Dashboard__Knowledge__AutoReply__AutoReplyData.f9c908ef.chunk.css",6130],["p__Dashboard__Knowledge__AutoReply__AutoReplyData.860249e9.async.js",6130],["6132.6a86081f.async.js",6132],["6159.eea317c5.async.js",6159],["t__plugin-layout__Layout.5012e1ab.chunk.css",6301],["t__plugin-layout__Layout.768c4f8f.async.js",6301],["p__Dashboard__Team__Notification__NotificationStatistic.d6789650.async.js",6545],["p__Dashboard__Service__Queue__QueueTable.b08f485d.async.js",6668],["p__Dashboard__Setting__Server__index.ec200e2f.async.js",6676],["p__Dashboard__Setting__Basic__index.effe2758.async.js",6766],["p__Dashboard__Team__Thread__ThreadStatistic.4358fa01.async.js",6771],["p__Dashboard__Knowledge__AutoReply__index.3f9da60f.async.js",6778],["p__Dashboard__Ai__Message__MessageStatistic.d3243774.async.js",6781],["6828.cd380545.async.js",6828],["6886.d9d70cdc.async.js",6886],["p__Dashboard__Service__Rate__RateTable.426e7820.async.js",6900],["p__Dashboard__Knowledge__Article__ArticleStatistic.d6d1fdfd.async.js",6906],["6907.097e3a53.async.js",6907],["p__Dashboard__Service__Tag__TagStatistic.4050e7cd.async.js",7080],["7084.cef6d000.async.js",7084],["p__Dashboard__Team__Group__GroupTable.5fa8abdb.async.js",7139],["p__Dashboard__Service__Thread__ThreadMonitor.68218579.async.js",7270],["p__Dashboard__Ai__Thread__ThreadTable.fafeb6ff.async.js",7357],["p__Dashboard__Super__Recharge__RechargeData.816ec033.async.js",7358],["p__Dashboard__Knowledge__Taboo__TabooData.f9c908ef.chunk.css",7445],["p__Dashboard__Knowledge__Taboo__TabooData.c7dec91b.async.js",7445],["p__Dashboard__Setting__index.da39b284.async.js",7469],["7602.78020514.async.js",7602],["p__Dashboard__Service__Message__MessageTable.44c7f36b.async.js",7788],["p__Dashboard__Ai__Message__MessageTable.c9516be5.async.js",7806],["p__Dashboard__Team__Member__index.f9c908ef.chunk.css",7960],["p__Dashboard__Team__Member__index.92bce71f.async.js",7960],["p__Dashboard__Knowledge__Faq__FaqStatistic.7b344d22.async.js",8041],["p__Dashboard__Knowledge__Taboo__index.2e151186.async.js",8158],["p__Dashboard__Marketing__index.f67e52fc.async.js",8164],["8183.03d51a64.async.js",8183],["p__Welcome.f68f2ebc.async.js",8185],["p__OAuth__Github.580071ed.async.js",8250],["p__Dashboard__Service__LeaveMsg__LeaveMsgTable.acfe61ac.async.js",8326],["p__Dashboard__Zhaobiao__index.92b446f7.async.js",8327],["8335.042844e5.async.js",8335],["p__Dashboard__Service__Thread__ThreadSummary.1bd7e25c.async.js",8390],["p__Dashboard__Service__LeaveMsg__LeaveMsgStatistic.41a43d32.async.js",8412],["p__Dashboard__Tiku__index.40401a5d.async.js",8455],["p__Dashboard__Ai__Robot__index.7e4f6c78.async.js",8472],["p__Dashboard__Team__Action__index.ab0f9a49.async.js",8483],["p__Dashboard__Team__Message__index.3c4a05e8.async.js",8491],["p__Dashboard__Knowledge__Article__index.1ab4c53b.async.js",8587],["p__Dashboard__Super__User__UserStatistic.7ab4a4b0.async.js",8622],["p__Dashboard__Service__Crm__VisitorTable.ea43827b.async.js",8655],["8686.639a29fc.async.js",8686],["p__Dashboard__Super__Notification__NotificationStatistic.ad606757.async.js",8700],["p__Dashboard__Knowledge__Faq__index.cdcca470.async.js",8801],["p__Dashboard__Marketing__Email__index.977ca82c.async.js",8873],["8949.5f921b9b.async.js",8949],["p__Dashboard__Service__Black__BlackTable.20a2de85.async.js",8956],["p__Dashboard__Service__Rate__RateStatistic.5298cf75.async.js",9043],["p__Dashboard__Team__Thread__ThreadTable.eab455eb.async.js",9077],["p__Dashboard__Super__Push__PushData.f56ee6d5.async.js",9137],["p__Dashboard__Super__Push__PushStatistic.7eb667c6.async.js",9212],["p__Dashboard__Setting__Profile__index.d73c49fa.async.js",9290],["p__Dashboard__Team__Group__GroupStatistic.5cf7efdb.async.js",9346],["p__Auth__Login__index.7c9aeeea.async.js",9368],["p__Dashboard__Knowledge__Keyword__KeywordStatistic.09041c94.async.js",9397],["p__Dashboard__Setting__Certification__index.357c8155.async.js",9400],["p__Dashboard__Setting__Notification__index.e316a8d2.async.js",9408],["p__Dashboard__Knowledge__Llm__index.b28a8302.async.js",9450],["9486.3fd0efbd.async.js",9486],["p__Dashboard__Service__Quality__index.bbf1f533.async.js",9505],["p__Dashboard__Knowledge__QuickReply__QuickReplyData.f9c908ef.chunk.css",9584],["p__Dashboard__Knowledge__QuickReply__QuickReplyData.b959184b.async.js",9584],["p__Dashboard__Marketing__Email__EmailData.f9c908ef.chunk.css",9590],["p__Dashboard__Marketing__Email__EmailData.a3063dcb.async.js",9590],["p__Dashboard__Ai__Upload__UploadTable.f1fb4d4a.async.js",9601],["9605.5f6618cd.async.js",9605],["p__Dashboard__Service__Queue__index.1177b848.async.js",9614],["p__Dashboard__Service__Black__index.12bd4ca3.async.js",9639],["p__Dashboard__Team__Company__CompanyInfo.3d486e9a.async.js",9814],["p__Dashboard__Service__Workgroup__index.64f54222.async.js",9828],["p__Dashboard__Team__Group__index.a51adc45.async.js",9870],["p__Dashboard__Service__Crm__CustomerTable.4a2901d2.async.js",9949],["p__Dashboard__Service__Statistic__index.fd4931fa.async.js",9967]],"r":{"/*":[46,63],"/":[20,63,113,120,121,133],"/welcome":[3,10,12,13,54,77,114,115,152,156,183,20,63,113,120,121,133],"/kb":[9,20,63,113,120,121,133],"/ticket":[33,20,63,113,120,121,133],"/feedback":[18,20,63,113,120,121,133],"/marketing":[150,20,63,113,120,121,133],"/plugins":[42,20,63,113,120,121,133],"/meiyu":[4,12,20,43,54,62,63,77,99,113,114,115,156,166,183,120,121,133],"/tiku":[12,20,43,54,62,63,77,113,114,115,156,159,166,183,120,121,133],"/zhaobiao":[12,20,43,54,62,63,77,113,114,115,155,156,166,183,120,121,133],"/quartz":[12,20,43,54,62,63,73,74,77,113,114,115,156,166,183,120,121,133],"/setting":[142,20,63,113,120,121,133],"/doceditor":[4,20,54,75,76,77,87,101,102,110,114,115,156,166],"/flowbot":[20,57,63,113,133],"/auth/login":[12,20,77,114,115,151,166,170,178],"/auth/register":[20,29,77,114,115,166,170],"/oauth/github":[153],"/oauth/google":[98],"/team/member":[12,20,43,54,62,63,77,113,114,115,129,146,147,156,166,183,120,121,133],"/team/role":[4,12,17,20,54,77,83,114,115,166,63,113,120,121,133],"/team/group":[195,20,63,113,120,121,133],"/team/thread":[52,20,63,113,120,121,133],"/team/message":[162,20,63,113,120,121,133],"/team/action":[161,20,63,113,120,121,133],"/team/notification":[26,20,63,113,120,121,133],"/team/company":[91,20,63,113,120,121,133],"/cs/agent":[4,8,12,20,28,43,54,62,63,77,113,114,115,119,129,130,156,166,183,120,121,133],"/cs/workgroup":[4,8,12,20,43,54,62,63,77,113,114,115,119,129,130,156,166,183,194,120,121,133],"/cs/queue":[191,20,63,113,120,121,133],"/cs/thread":[37,20,63,113,120,121,133],"/cs/message":[56,20,63,113,120,121,133],"/cs/leavemsg":[71,20,63,113,120,121,133],"/cs/rate":[104,20,63,113,120,121,133],"/cs/tag":[100,20,63,113,120,121,133],"/cs/channel":[4,12,20,54,62,77,88,96,113,114,115,130,166,63,120,121,133],"/cs/transfer":[107,20,63,113,120,121,133],"/cs/monitor":[6,20,63,113,120,121,133],"/cs/quality":[184,20,63,113,120,121,133],"/cs/crm":[66,20,63,113,120,121,133],"/cs/black":[192,20,63,113,120,121,133],"/cs/statistic":[197,20,63,113,120,121,133],"/ai/robot":[4,10,12,20,44,45,54,77,90,113,114,115,129,166,63,120,121,133],"/ai/model":[4,12,14,20,54,77,113,114,115,118,143,166,63,120,121,133],"/ai/flow":[4,20,38,54,115,63,113,120,121,133],"/ai/prompt":[4,20,54,77,87,113,114,115,129,160,166,63,120,121,133],"/ai/thread":[53,20,63,113,120,121,133],"/ai/message":[19,20,63,113,120,121,133],"/kb/article":[4,20,54,77,114,115,163,166,190,9,63,113,120,121,133],"/kb/llm":[4,20,54,77,114,115,166,182,190,9,63,113,120,121,133],"/kb/keyword":[4,20,54,77,106,114,115,166,190,9,63,113,120,121,133],"/kb/faq":[4,20,54,77,114,115,166,168,190,9,63,113,120,121,133],"/kb/autoreply":[4,20,54,77,114,115,127,166,190,9,63,113,120,121,133],"/kb/quickreply":[4,20,54,77,112,114,115,166,190,9,63,113,120,121,133],"/kb/taboo":[4,20,54,77,114,115,149,166,190,9,63,113,120,121,133],"/kb/upload":[39,9,20,63,113,120,121,133],"/marketing/blog":[4,20,21,54,77,114,115,166,190,150,63,113,120,121,133],"/marketing/email":[4,20,54,77,114,115,166,169,190,150,63,113,120,121,133],"/marketing/video":[60,150,20,63,113,120,121,133],"/liangshibao/category":[12,20,43,54,62,63,77,103,113,114,115,156,166,183,120,121,133],"/liangshibao/course":[12,20,30,43,54,62,63,77,113,114,115,156,166,183,120,121,133],"/liangshibao/media":[12,20,43,54,62,63,65,77,113,114,115,156,166,183,120,121,133],"/setting/profile":[20,77,114,115,129,166,176,142,63,113,120,121,133],"/setting/basic":[77,125,142,20,63,113,120,121,133],"/setting/certification":[12,20,77,114,115,166,180,142,63,113,120,121,133],"/setting/money":[12,20,27,43,54,62,63,77,113,114,115,151,156,166,183,142,120,121,133],"/setting/server":[124,142,20,63,113,120,121,133],"/setting/notification":[12,20,43,54,62,63,77,113,114,115,156,166,181,183,142,120,121,133],"/super/user":[89,20,63,113,120,121,133],"/super/role":[4,12,17,20,54,77,111,114,115,166,63,113,120,121,133],"/super/provider":[4,12,15,20,54,77,113,114,115,118,143,166,63,120,121,133],"/super/recharge":[40,20,63,113,120,121,133],"/super/notification":[31,20,63,113,120,121,133],"/super/push":[97,20,63,113,120,121,133],"/team/group/data":[12,20,43,54,62,63,77,113,114,115,136,156,166,183,195,120,121,133],"/team/group/statistic":[177,195,20,63,113,120,121,133],"/team/thread/data":[12,20,43,54,59,62,63,77,113,114,115,156,166,173,183,52,120,121,133],"/team/thread/monitor":[41,52,20,63,113,120,121,133],"/team/thread/statistic":[126,52,20,63,113,120,121,133],"/team/thread/summary":[12,20,43,47,54,62,63,77,113,114,115,156,166,183,52,120,121,133],"/team/message/data":[12,20,22,43,54,62,63,77,113,114,115,135,156,166,183,162,120,121,133],"/team/message/statistic":[58,162,20,63,113,120,121,133],"/team/action/data":[12,20,43,54,62,63,77,84,113,114,115,156,166,183,161,120,121,133],"/team/action/statistic":[50,161,20,63,113,120,121,133],"/team/notification/data":[12,20,43,54,62,63,77,105,113,114,115,156,166,183,26,120,121,133],"/team/notification/statistic":[122,26,20,63,113,120,121,133],"/team/company/info":[20,77,114,115,129,166,193,91,63,113,120,121,133],"/cs/queue/data":[12,20,43,54,62,63,77,113,114,115,123,156,166,183,191,120,121,133],"/cs/queue/statistic":[24,191,20,63,113,120,121,133],"/cs/thread/data":[12,20,43,54,59,62,63,70,77,113,114,115,156,166,183,37,120,121,133],"/cs/thread/monitor":[137,37,20,63,113,120,121,133],"/cs/thread/statistic":[35,37,20,63,113,120,121,133],"/cs/thread/summary":[12,20,43,54,62,63,77,113,114,115,156,157,166,183,37,120,121,133],"/cs/message/data":[12,20,43,54,62,63,77,113,114,115,135,144,156,166,183,56,120,121,133],"/cs/message/statistic":[86,56,20,63,113,120,121,133],"/cs/leavemsg/data":[12,20,43,54,62,63,77,113,114,115,154,156,166,183,71,120,121,133],"/cs/leavemsg/statistic":[158,71,20,63,113,120,121,133],"/cs/rate/data":[12,20,43,54,62,63,77,113,114,115,131,156,166,183,104,120,121,133],"/cs/rate/statistic":[172,104,20,63,113,120,121,133],"/cs/tag/data":[12,20,43,51,54,62,63,77,95,113,114,115,156,166,183,100,120,121,133],"/cs/tag/statistic":[134,100,20,63,113,120,121,133],"/cs/transfer/data":[12,16,20,43,54,62,63,77,113,114,115,156,166,183,107,120,121,133],"/cs/transfer/statistic":[32,107,20,63,113,120,121,133],"/cs/quality/data":[12,20,34,43,54,62,63,77,113,114,115,156,166,183,184,120,121,133],"/cs/quality/statistic":[0,184,20,63,113,120,121,133],"/cs/black/data":[12,20,43,54,62,63,77,113,114,115,156,166,171,183,192,120,121,133],"/cs/black/statistic":[5,192,20,63,113,120,121,133],"/ai/thread/data":[12,20,43,54,59,62,63,77,113,114,115,138,156,166,183,53,120,121,133],"/ai/thread/statistic":[93,53,20,63,113,120,121,133],"/ai/message/data":[12,20,43,54,62,63,77,113,114,115,135,145,156,166,183,19,120,121,133],"/ai/message/statistic":[128,19,20,63,113,120,121,133],"/kb/article/data":[12,20,43,54,62,63,77,87,108,109,113,114,115,156,166,183,4,163,190,9,120,121,133],"/kb/article/statistic":[132,4,20,54,77,114,115,163,166,190,9,63,113,120,121,133],"/kb/llm/data":[12,20,43,54,62,63,77,78,79,87,113,114,115,129,156,166,183,4,182,190,9,120,121,133],"/kb/llm/statistic":[85,4,20,54,77,114,115,166,182,190,9,63,113,120,121,133],"/kb/keyword/data":[3,12,20,36,43,54,62,63,67,68,77,87,110,113,114,115,119,129,156,166,183,4,106,190,9,120,121,133],"/kb/keyword/statistic":[179,4,20,54,77,106,114,115,166,190,9,63,113,120,121,133],"/kb/faq/data":[1,2,12,20,43,54,62,63,77,87,113,114,115,129,156,166,183,4,168,190,9,120,121,133],"/kb/faq/statistic":[148,4,20,54,77,114,115,166,168,190,9,63,113,120,121,133],"/kb/autoreply/data":[12,20,43,54,62,63,77,87,113,114,115,116,117,129,156,166,183,4,127,190,9,120,121,133],"/kb/autoreply/statistic":[55,4,20,54,77,114,115,127,166,190,9,63,113,120,121,133],"/kb/quickreply/data":[12,20,43,54,62,63,77,87,113,114,115,129,156,166,183,185,186,4,112,190,9,120,121,133],"/kb/quickreply/statistic":[72,4,20,54,77,112,114,115,166,190,9,63,113,120,121,133],"/kb/taboo/data":[12,20,43,54,62,63,77,87,113,114,115,129,140,141,156,166,183,4,149,190,9,120,121,133],"/kb/taboo/statistic":[7,4,20,54,77,114,115,149,166,190,9,63,113,120,121,133],"/kb/upload/data":[12,20,43,54,62,63,77,113,114,115,156,166,183,189,39,9,120,121,133],"/kb/upload/statistic":[92,39,9,20,63,113,120,121,133],"/marketing/blog/data":[12,20,43,48,49,54,62,63,77,87,113,114,115,156,166,183,4,21,190,150,120,121,133],"/marketing/blog/statistic":[11,4,20,21,54,77,114,115,166,190,150,63,113,120,121,133],"/marketing/email/data":[12,20,43,54,62,63,77,87,113,114,115,156,166,183,187,188,4,169,190,150,120,121,133],"/marketing/email/statistic":[64,4,20,54,77,114,115,166,169,190,150,63,113,120,121,133],"/marketing/video/data":[12,20,23,43,54,62,63,77,113,114,115,156,166,183,60,150,120,121,133],"/marketing/video/statistic":[82,60,150,20,63,113,120,121,133],"/super/user/data":[12,20,43,54,61,62,63,77,113,114,115,156,166,183,89,120,121,133],"/super/user/statistic":[164,89,20,63,113,120,121,133],"/super/recharge/data":[12,20,43,54,62,63,77,113,114,115,139,156,166,183,40,120,121,133],"/super/recharge/statistic":[81,40,20,63,113,120,121,133],"/super/notification/data":[12,20,43,54,62,63,77,80,113,114,115,156,166,183,31,120,121,133],"/super/notification/statistic":[167,31,20,63,113,120,121,133],"/super/push/data":[12,20,43,54,62,63,77,113,114,115,156,166,174,183,97,120,121,133],"/super/push/statistic":[175,97,20,63,113,120,121,133],"/cs/crm/visitor/data":[12,20,43,54,62,63,77,113,114,115,156,165,166,183,66,120,121,133],"/cs/crm/visitor/statistic":[69,66,20,63,113,120,121,133],"/cs/crm/customer/data":[12,20,43,54,62,63,77,113,114,115,156,166,183,196,66,120,121,133],"/cs/crm/customer/statistic":[25,66,20,63,113,120,121,133]}},{publicPath:"/admin/"});null==i||i.forEach((function(t){var e,n=t.type,a=t.url;if("js"===n)(e=r("script")).src=a,e.async=!0;else{if("css"!==n)return;(e=r("link")).href=a,e.rel="preload",e.as="style"}t.attrs.forEach((function(t){e.setAttribute(t[0],t[1]||"")})),c.appendChild(e)}))}}();