<@menuTag method="list">
    <#--
        ?sort_by('priority')：根据菜单的排序编号排序
    -->
    <#list menus?sort_by('priority') as menu>
        <li>
            <a href="${menu.url}" target="${menu.target!}">${menu.name} </a>
        </li>
    </#list>
</@menuTag>