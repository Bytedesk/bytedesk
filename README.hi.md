# Bytedesk - चैट सेवा

टीम सहयोग के साथ एआई संचालित ओमनीचैनल ग्राहक सेवा

## भाषा

- [English](./README.md)
- [中文](./README.zh.md)
- [हिंदी](./README.hi.md)

## एडमिन डैशबोर्ड

![statistics](./images/admin/statistics.png)

## एडमिन चैट

![chat](./images/admin/chat.png)

## एडमिन LLM + एजेंट

![llm_agent](./images/admin/llm_agent.png)

## चैनल प्रबंधन

![channel](./images/admin/channel.png)

## एजेंट वर्कबेंच

![agent](./images/agent/agent_chat.png)

## परिचय

### [टीम IM](./modules/team/readme.md)

- बहु-स्तरीय संगठनात्मक संरचना
- भूमिका व अनुमति प्रबंधन
- ऑडिट लॉग और आर्काइविंग
- ...

### [ग्राहक सेवा](./modules/service/readme.md)

- वेब, ऐप, सोशल, ई-कॉमर्स आदि चैनलों का एकीकरण
- स्मार्ट रूटिंग रणनीतियाँ और KPI
- एकीकृत एजेंट डेस्क
- ...

### [ज्ञान आधार](./modules/kbase/readme.md)

- आंतरिक दस्तावेज़ और हेल्प सेंटर
- FAQ तथा RAG नॉलेज बेस
- AI एजेंट के साथ समन्वय
- ...

### [टिकट सिस्टम](./modules/ticket/readme.md)

- टिकट जीवनचक्र प्रबंधन
- SLA ट्रैकिंग और अलर्ट
- रिपोर्ट और डैशबोर्ड
- ...

### [AI एजेंट](./modules/ai/readme.md)

- Ollama / DeepSeek / ZhipuAI / ... से चैट
- नॉलेज बेस (RAG) से उत्तर
- Function Calling व MCP
- ...

### [वर्कफ़्लो](./modules/core/readme.workflow.md)

- कस्टम फॉर्म
- विज़ुअल प्रोसेस डिज़ाइनर
- टिकट वर्कफ़्लो ऑटोमेशन
- ...

### [वॉइस ऑफ कस्टमर](./modules/voc/readme.md)

- फीडबैक, सर्वे, शिकायतें
- संतुष्टि मॉनिटरिंग
- ...

### [कॉल सेंटर](./plugins/freeswitch/readme.zh.md)

- FreeSwitch आधारित प्रोफेशनल प्लेटफ़ॉर्म
- कॉल पॉपअप, ऑटो असाइनमेंट, रिकॉर्डिंग
- आवाज़ व टेक्स्ट सेवा का एकीकरण

### [वीडियो客服](./plugins/webrtc/readme.zh.md)

- WebRTC आधारित HD वीडियो कॉल्स
- एक-क्लिक वीडियो/स्क्रीन शेयर
- उच्च गुणवत्ता डेमो सीनारियो

### [ओपन प्लेटफ़ॉर्म](./plugins/readme.md)

- RESTful API और बहुभाषी SDK
- थर्ड-पार्टी सिस्टम इंटीग्रेशन
- तेजी से विकास एवं परिनियोजन

## त्वरित प्रारंभ

```bash
git clone https://github.com/Bytedesk/bytedesk.git
cd bytedesk/deploy/docker
# बिना AI फ़ीचर के प्रारंभ
docker compose -p bytedesk -f docker-compose-noai.yaml up -d
# ZhipuAI (API Key आवश्यक)
docker compose -p bytedesk -f docker-compose.yaml up -d
# लोकल Ollama के साथ
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
```

- [Docker डिप्लॉयमेंट](https://www.weiyuai.cn/docs/docs/deploy/docker/)
- [BaoTa डिप्लॉयमेंट](https://www.weiyuai.cn/docs/docs/deploy/baota)
- [सोर्स से रन](https://www.weiyuai.cn/docs/docs/deploy/source)

## डेमो/एक्सेस

```bash
# 127.0.0.1 को अपने सर्वर IP से बदलें
http://127.0.0.1:9003/
# खुले पोर्ट: 9003, 9885
डिफ़ॉल्ट उपयोगकर्ता: admin@email.com
डिफ़ॉल्ट पासवर्ड: admin
```

## प्रोजेक्ट संरचना

Maven आधारित मोनोरेपो (रूट `pom.xml`) जिसमें कई मॉड्यूल और डिप्लॉय संसाधन हैं।

```text
bytedesk/
├─ channels/           # चैनल इंटीग्रेशन (डौईन, स्टोर, सोशल, WeChat)
├─ demos/              # डेमो प्रोजेक्ट और उदाहरण
├─ deploy/             # Docker, K8s, सर्वर कॉन्फिग्स
├─ enterprise/         # एंटरप्राइज़ क्षमताएँ (ai, call, core, kbase, service, ticket)
├─ images/             # डॉक्यूमेंटेशन व UI इमेज
├─ jmeter/             # परफ़ॉर्मेंस टेस्ट स्क्रिप्ट्स
├─ logs/               # लोकल/डेव लॉग्स
├─ modules/            # कोर मॉड्यूल (TeamIM, Service, KBase, Ticket, AI ...)
├─ plugins/            # वैकल्पिक प्लगइन्स (freeswitch, webrtc, open platform)
├─ projects/           # कस्टम प्रोजेक्ट्स
├─ starter/            # स्टार्टर/एंट्री प्रोजेक्ट्स
```

## आर्किटेक्चर

- [आर्किटेक्चर आरेख](https://www.weiyuai.cn/architecture.html)

## ओपन सोर्स क्लाइंट

- [डेस्कटॉप](https://github.com/Bytedesk/bytedesk-desktop)
- [मोबाइल](https://github.com/Bytedesk/bytedesk-mobile)
- [SipPhone](https://github.com/Bytedesk/bytedesk-phone)
- [Conference](https://github.com/Bytedesk/bytedesk-conference)
- [FreeSwitch Docker](https://github.com/Bytedesk/bytedesk-freeswitch)
- [Jitsi Docker](https://github.com/Bytedesk/bytedesk-jitsi)

## ओपन सोर्स डेमो + SDK

| प्रोजेक्ट | विवरण | Forks | Stars |
|-----------|--------|-------|-------|
| [iOS](https://github.com/bytedesk/bytedesk-swift) | नेटिव iOS ऐप | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-swift) | ![GitHub Repo stars](https://img.shields.io/github/stars/Bytedesk/bytedesk-swift) |
| [Android](https://github.com/bytedesk/bytedesk-android) | नेटिव Android ऐप | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-android) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-android) |
| [Flutter](https://github.com/bytedesk/bytedesk-flutter) | Flutter SDK | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-flutter) | ![GitHub Repo stars](https://img.shields.io/github/stars/Bytedesk/bytedesk-flutter) |
| [UniApp](https://github.com/bytedesk/bytedesk-uniapp) | UniApp पैकेज | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-uniapp) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-uniapp) |
| [Web](https://github.com/bytedesk/bytedesk-web) | Vue/React/Angular/Next.js फ्रंटएंड | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-web) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-web) |
| [WordPress](https://github.com/bytedesk/bytedesk-wordpress) | WordPress प्लगइन | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-wordpress) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-wordpress) |
| [WooCommerce](https://github.com/bytedesk/bytedesk-woocommerce) | WooCommerce इंटीग्रेशन | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-woocommerce) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-woocommerce) |
| [Magento](https://github.com/bytedesk/bytedesk-magento) | Magento एक्सटेंशन | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-magento) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-magento) |
| [PrestaShop](https://github.com/bytedesk/bytedesk-prestashop) | PrestaShop मॉड्यूल | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-prestashop) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-prestashop) |
| [Shopify](https://github.com/bytedesk/bytedesk-shopify) | Shopify ऐप | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-shopify) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-shopify) |
| [OpenCart](https://github.com/bytedesk/bytedesk-opencart) | OpenCart प्लगइन | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-opencart) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-opencart) |
| [Laravel](https://github.com/bytedesk/bytedesk-laravel) | Laravel पैकेज | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-laravel) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-laravel) |
| [Django](https://github.com/bytedesk/bytedesk-django) | Django ऐप | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-django) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-django) |

## लिंक

- [डाउनलोड](https://www.weiyuai.cn/download.html)
- [दस्तावेज़](https://www.weiyuai.cn/docs/)

## लाइसेंस

कॉपीराइट (c) 2013-2025 Bytedesk.com, सर्वाधिकार सुरक्षित।

यह प्रोजेक्ट GNU AFFERO GENERAL PUBLIC LICENSE (AGPL v3) के अंतर्गत वितरित है:

<https://www.gnu.org/licenses/agpl-3.0.html>

सॉफ़्टवेयर "जैसा है" आधार पर उपलब्ध है, किसी भी प्रकार की वारंटी के बिना।

## उपयोग की शर्तें

- **अनुमत उपयोग**: व्यावसायिक उपयोग की अनुमति है, परन्तु बिना अनुमति पुनर्विक्रय वर्जित है
- **निषिद्ध उपयोग**: किसी भी अवैध गतिविधि (मैलवेयर, धोखाधड़ी, जुआ इत्यादि) हेतु उपयोग निषिद्ध
- **अस्वीकरण**: उपयोग पूर्णतः आपके जोखिम पर; किसी भी कानूनी दायित्व की जिम्मेदारी उपयोगकर्ता की होगी