import Vue from 'vue'
import App from './App'

// 官方文档 https://uniapp.dcloud.net.cn/collocation/i18n
// 国际化 json 文件，文件内容详见下面的示例
import en from './components/bytedesk_kefu/i18n/en.json'
import cn from './components/bytedesk_kefu/i18n/cn.json'
const messages = {
    en,
    cn
}
let i18nConfig = {
  // 其中：中文填写 'cn', 英文填写 'en'
  locale: 'cn', // uni.getLocale(), // 获取已设置的语言
  messages
}
// 引入国际化设置插件
import VueI18n from 'vue-i18n'
Vue.use(VueI18n)
const i18n = new VueI18n(i18nConfig)

// 其他
Vue.config.productionTip = false
App.mpType = 'app'

const app = new Vue({
	i18n, // 国际化配置
    ...App
})
app.$mount()
