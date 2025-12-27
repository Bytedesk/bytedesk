/**
 * 数据
 */
var data = {
    //
    HTTP_HOST: "http://127.0.0.1:8000",
    STOMP_HOST: "http://127.0.0.1:8000",
    // HTTP_HOST: "https://api.bytedesk.com",
    // STOMP_HOST: "https://stomp.bytedesk.com",
    // 知识库自定义二级域名
    kbSubdomain: '',
    // 后台管理员唯一uid
    adminUid: '',
    subDomain: 'vip',
    passport: {
      token: {
        access_token: "",
        expires_in: 0,
        jti: "",
        refresh_token: "",
        scope: "",
        token_type: ""
      }
    },
    // 当前访客用户名
    uid: "",
    username: "",
    password: "",
    nickname: "",
    // 本地存储access_token的key
    token: "token",
    client: "web",
    // 当前页面
    page: "",
    // 类别cid
    cid: "",
    // 文章aid
    aid: "",
    // 搜索内容
    searchContent: "",
    // 当前类别
    currentCategory: Object,
    // 当前文章
    currentArticle: Object,
    articleRate: false
};