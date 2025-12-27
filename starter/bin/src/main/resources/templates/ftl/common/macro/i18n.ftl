<#--
  Simple i18n macro: lookup i18n[key]; if missing, render nested default text
  Usage: <@i18n.t key="nav.home">首页</@i18n.t>
-->
<#macro t key>
  <#if i18n?? && i18n[key]?? && i18n[key]?has_content>
    ${i18n[key]}
  <#else>
    <#nested>
  </#if>
</#macro>
