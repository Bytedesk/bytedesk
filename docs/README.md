# docs

```bash
# 创建
npx create-docusaurus@latest my-website classic --typescript
# 安装依赖
yarn
# 翻译
yarn write-translations
# https://docusaurus.io/zh-CN/docs/i18n/git
# docusaurus.config.ts中i18n添加zh-CN
# yarn write-translations --locale en
yarn write-translations --locale zh-CN
# 
mkdir -p i18n/zh-CN/docusaurus-plugin-content-docs/current
cp -r docs/** i18n/zh-CN/docusaurus-plugin-content-docs/current

mkdir -p i18n/zh-CN/docusaurus-plugin-content-blog
cp -r blog/** i18n/zh-CN/docusaurus-plugin-content-blog

mkdir -p i18n/zh-CN/docusaurus-plugin-content-pages
cp -r src/pages/**.md i18n/zh-CN/docusaurus-plugin-content-pages
cp -r src/pages/**.mdx i18n/zh-CN/docusaurus-plugin-content-pages
# 只有正式发布到生产环境才能够切换语言, 所以需要下面命令测试中文
yarn run start --locale zh-CN
```
