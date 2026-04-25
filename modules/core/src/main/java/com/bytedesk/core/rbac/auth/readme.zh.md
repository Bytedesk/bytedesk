# auth

该包负责登录请求处理、令牌认证、认证过滤链以及登录成功事件集成。

## 实现要点

- AuthController、AuthRequest、AuthResponse 定义了面向登录入口的认证接口契约。
- AuthService、AuthToken、AuthTypeEnum 负责登录编排、令牌载荷处理与认证类型建模。
- AuthTokenFilter 与 AuthEntryPoint 集成到安全过滤链，用于令牌解析和未认证访问响应。
- AuthLoginRetryHelper 与 AuthEventListener 负责登录重试控制和认证侧事件处理。
- event 子包当前提供 AuthSuccessEvent，用于登录成功后的事件发布。
