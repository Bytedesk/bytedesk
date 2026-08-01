# 节假日

## 作用

HolidayEntity 用于统一维护平台或组织维度的节假日、调休日和自定义日期规则，为客服日程、时间条件、自动回复等场景提供基础日期数据。

当前它已经直接接入统一的 HolidayService / WorktimeService 判定链路，用于判断某一天是否属于法定节假日、调休上班日或组织自定义特殊日期。

## 核心实体

HolidayEntity 对应表 bytedesk_service_holiday，当前以按天判定为主，关键字段如下：

- name：节假日名称，例如元旦、春节、清明节。
- description：说明。
- type：节假日类型，当前可用于区分官方或自定义来源。
- holidayDate：节假日对应日期。
- holidayYear：年份，便于按年查询。
- countryCode：国家或地区代码，默认 CN。
- offDay：该日期是否为休息日。false 可表示调休上班日。
- official：是否为官方初始化数据。
- sourceUrl：数据来源地址。
- holidayKey：节日唯一业务标识，便于精确匹配。

## 当前建模边界

当前 HolidayEntity 已从“模板实体”收敛到“按日期判定”的可运行模型，优先满足统一工作时间与呼叫中心营业时间判定需求。

因此目前聚焦的是：

- 某一天是不是官方休息日。
- 某一天是不是某个具体节日。
- 某一天是不是调休上班日。

原先 startDate、endDate、repeatType、customerNotice 等模板字段暂未启用，后续如果需要支撑更复杂的客服留言或服务暂停策略，可再按真实业务重新建模。

## 初始化机制

HolidayInitializer 在系统启动后会自动执行两类初始化：

- 初始化 HOLIDAY_* 权限。
- 初始化 2026 年中国法定节假日与调休日数据。

初始化数据当前来源于 HolidayInitData，sourceUrl 指向：

- <https://github.com/NateScarlet/holiday-cn/blob/master/2026.json>

初始化时会跳过已存在的 countryCode + holidayDate 记录，避免重复写入。

## 与统一工作时间的关系

HolidayService 基于 HolidayRepository 读取 HolidayEntity，并按平台级与组织级作用域聚合生效节假日；WorktimeService 再据此决定当前日期是走 regularWorktimes 还是 specialWorktimes。

当前约定如下：

- offDay=true：表示该日期应作为节假日或休息日参与判定。
- offDay=false：表示该日期属于调休上班日，不自动切到节假日时段。
- 当 holiday scope 同时包含平台级与组织级时，组织级同日记录优先覆盖平台级记录。

因此 HolidayEntity 不只是后台维护数据，同时也是统一营业时间判定的直接数据源。

## 典型使用场景

### 节假日自动切换营业时段

在 WorktimeSettingEntity 中配置 holiday 感知的 specialWorktimes。

可在法定节假日时自动切到节假日营业时段，并由各渠道路由层决定进入 IVR、公告音、留言或非工作时间处理。

### 调休日正常上班

对于被国家安排为补班的日期，HolidayEntity 中 offDay=false。

这样在 holiday 规则判定时不会被当作休息日，可继续命中正常工作时间规则。

## 使用建议

- 官方节假日数据建议保留 platform 级初始化记录，业务侧不要直接覆盖。
- 如果组织存在自定义停服日，可新增 organization 级 HolidayEntity 做补充。
- 如果后续扩展更多国家，请优先保持 countryCode + holidayDate + holidayKey 这套可查询结构一致。
