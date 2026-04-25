# quartz

该包负责 Quartz 调度运行装配、定时作业与调度事件集成。

## 实现要点

- QuartzConfig、QuartzConsts、QuartzEventPublisher、QuartzJob 定义运行装配、共享常量、事件发布和基础调度抽象。
- event 子包发布固定频率的调度事件，例如五秒、一分钟、每小时、半小时和每日触发事件。
- job 子包包含与这些调度频率对应的具体 Quartz 作业实现。
