<div dir="rtl">

<!-- يرجى مراجعة قيود ترخيص BSL قبل تثبيت Bytedesk IM. -->

# بايت ديسك - المحادثة كخدمة

خدمة عملاء أومني-قناة مدعومة بالذكاء الاصطناعي مع تعاون فرق العمل

## اللغة

- [English](./README.md)
- [中文](./README.zh.md)
- [العربية](./README.ar.md)

## لوحة تحكم المشرف

![statistics](./images/admin/statistics.png)

## دردشة المشرف

![chat](./images/admin/chat.png)

## المشرف: النماذج الكبيرة + الوكلاء

![llm_agent](./images/admin/llm_agent.png)

## قنوات المشرف

![channel](./images/admin/channel.png)

## منصة الوكلاء

![agent](./images/agent/agent_chat.png)

## مقدمة

### [دردشة الفريق TeamIM](./modules/team/readme.md)

- هيكل تنظيمي هرمي متعدد المستويات
- إدارة الأدوار والسياسات
- التحكم في الأذونات والمراقبة
- ...

### [خدمة العملاء](./modules/service/readme.md)

- دعم قنوات متعددة (ويب، تطبيقات، متاجر، شبكات اجتماعية)
- استراتيجيات توزيع ومسارات ذكية مع مؤشرات أداء مفصلة
- مكتب عمل متكامل للوكلاء
- ...

### [قاعدة المعرفة](./modules/kbase/readme.md)

- مستندات داخلية ومركز مساعدة
- نشر الأسئلة الشائعة ومكتبات RAG
- تكامل مع الوكلاء الذكيين
- ...

### [نظام التذاكر](./modules/ticket/readme.md)

- إدارة دورة حياة التذكرة
- إدارة اتفاقيات مستوى الخدمة SLA
- تحليلات وتقارير تفصيلية
- ...

### [الوكيل الذكي AI Agent](./modules/ai/readme.md)

- محادثة مع Ollama / DeepSeek / ZhipuAI / ...
- تكامل قاعدة المعرفة (RAG)
- Function Calling و MCP
- ...

### [سير العمل](./modules/core/readme.workflow.md)

- نماذج مخصصة
- عمليات مرئية
- أتمتة عمليات التذكرة
- ...

### [صوت العميل](./modules/voc/readme.md)

- جمع الملاحظات
- الاستبيانات والمتابعة
- قياس جودة الخدمة
- ...

### [مركز الاتصال](./plugins/freeswitch/readme.zh.md)

- منصة احترافية مبنية على FreeSwitch
- دعم عرض بيانات المتصل، التوزيع الآلي، تسجيل المكالمات
- تقارير صوتية ودمج مع المحادثة النصية

### [خدمة الفيديو](./plugins/webrtc/readme.zh.md)

- مكالمات فيديو عالية الدقة عبر WebRTC
- مكالمات بنقرة واحدة ومشاركة شاشة
- مناسبة لسيناريوهات الخدمة التوضيحية

### [منصة مفتوحة](./plugins/readme.md)

- واجهات RESTful كاملة وأدوات SDK متعددة اللغات
- تكامل سلس مع أنظمة الطرف الثالث
- تسهيل عمليات التطوير والدمج

## بدء سريع

```bash
git clone https://github.com/Bytedesk/bytedesk.git
cd bytedesk/deploy/docker
# تشغيل بدون قدرات AI
docker compose -p bytedesk -f docker-compose-noai.yaml up -d
# أو استخدام ZhipuAI (يتطلب مفتاح API)
docker compose -p bytedesk -f docker-compose.yaml up -d
# أو الاعتماد على Ollama محلياً
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
```

- [نشر Docker](https://www.weiyuai.cn/docs/docs/deploy/docker/)
- [النشر عبر لوحة باوتا](https://www.weiyuai.cn/docs/docs/deploy/baota)
- [تشغيل من المصدر](https://www.weiyuai.cn/docs/docs/deploy/source)

## تجربة سريعة

```bash
# استبدل 127.0.0.1 بعنوان خادمك
http://127.0.0.1:9003/
# المنافذ المفتوحة: 9003، 9885
اسم المستخدم الافتراضي: admin@email.com
كلمة المرور الافتراضية: admin
```

## هيكل المشروع

مستودع أحادي يعتمد على Maven (ملف `pom.xml` في الجذر) ويضم وحدات متعددة وأصول نشر.

```text
bytedesk/
├─ channels/           # تكاملات القنوات (دوين، المتاجر، الشبكات الاجتماعية، WeChat)
├─ demos/              # المشاريع والأمثلة
├─ deploy/             # أصول النشر: Docker، K8s، إعدادات الخوادم
├─ enterprise/         # قدرات المؤسسات (ai، call، core، kbase، service، ticket)
├─ images/             # صور الوثائق وواجهات المعاينة
├─ jmeter/             # اختبارات الأداء والبرامج النصية
├─ logs/               # سجلات التشغيل البيئية المحلية
├─ modules/            # الوحدات الجوهرية (TeamIM، Service، KBase، Ticket، AI ...)
├─ plugins/            # إضافات اختيارية (freeswitch، webrtc، open platform)
├─ projects/           # مشاريع وتخصيصات إضافية
├─ starter/            # نقاط الانطلاق والمشاريع الجاهزة
```

## البنية

- [مخطط البنية](https://www.weiyuai.cn/architecture.html)

## العملاء مفتوحة المصدر

- [سطح المكتب](https://github.com/Bytedesk/bytedesk-desktop)
- [الهاتف المحمول](https://github.com/Bytedesk/bytedesk-mobile)
- [هاتف SIP](https://github.com/Bytedesk/bytedesk-phone)
- [المؤتمر](https://github.com/Bytedesk/bytedesk-conference)
- [FreeSwitch Docker](https://github.com/Bytedesk/bytedesk-freeswitch)
- [Jitsi Docker](https://github.com/Bytedesk/bytedesk-jitsi)

## العروض التجريبية و SDK مفتوحة المصدر

| المشروع | الوصف | Forks | Stars |
|---------|--------|-------|-------|
| [iOS](https://github.com/bytedesk/bytedesk-swift) | تطبيق iOS أصلي | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-swift) | ![GitHub Repo stars](https://img.shields.io/github/stars/Bytedesk/bytedesk-swift) |
| [Android](https://github.com/bytedesk/bytedesk-android) | تطبيق Android أصلي | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-android) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-android) |
| [Flutter](https://github.com/bytedesk/bytedesk-flutter) | حزمة Flutter | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-flutter) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-flutter) |
| [UniApp](https://github.com/bytedesk/bytedesk-uniapp) | مكون UniApp | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-uniapp) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-uniapp) |
| [Web](https://github.com/bytedesk/bytedesk-web) | واجهات Vue/React/Angular/Next.js | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-web) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-web) |
| [WordPress](https://github.com/bytedesk/bytedesk-wordpress) | إضافة WordPress | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-wordpress) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-wordpress) |
| [WooCommerce](https://github.com/bytedesk/bytedesk-woocommerce) | تكامل WooCommerce | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-woocommerce) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-woocommerce) |
| [Magento](https://github.com/bytedesk/bytedesk-magento) | ملحق Magento | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-magento) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-magento) |
| [PrestaShop](https://github.com/bytedesk/bytedesk-prestashop) | تكامل PrestaShop | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-prestashop) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-prestashop) |
| [Shopify](https://github.com/bytedesk/bytedesk-shopify) | تطبيق Shopify | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-shopify) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-shopify) |
| [OpenCart](https://github.com/bytedesk/bytedesk-opencart) | إضافة OpenCart | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-opencart) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-opencart) |
| [Laravel](https://github.com/bytedesk/bytedesk-laravel) | حزمة Laravel | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-laravel) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-laravel) |
| [Django](https://github.com/bytedesk/bytedesk-django) | تطبيق Django | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-django) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-django) |

## روابط مفيدة

- [التنزيل](https://www.weiyuai.cn/download.html)
- [المستندات](https://www.weiyuai.cn/docs/)

## الترخيص

حقوق الطبع والنشر (c) 2013-2025 Bytedesk.com، جميع الحقوق محفوظة.

يتم توزيع المشروع بموجب ترخيص GNU AFFERO GENERAL PUBLIC LICENSE (AGPL v3). يمكنك الاطلاع على نص الترخيص عبر الرابط:

<https://www.gnu.org/licenses/agpl-3.0.html>

يتم تقديم البرنامج "كما هو" دون أي ضمانات، صريحة أو ضمنية، ويقع على عاتقك التحقق من شروط الترخيص قبل الاستخدام التجاري.

## شروط الاستخدام

- **الاستخدام المسموح**: يمكن استخدامه تجارياً، لكن يحظر إعادة البيع بدون إذن مسبق
- **الاستخدام المحظور**: يمنع استخدامه لأي نشاط غير قانوني مثل البرمجيات الخبيثة أو المقامرة أو الاحتيال
- **إخلاء المسؤولية**: لا يتحمل البرنامج أي مسؤولية قانونية؛ يتحمل المستخدم المخاطر بالكامل

</div>