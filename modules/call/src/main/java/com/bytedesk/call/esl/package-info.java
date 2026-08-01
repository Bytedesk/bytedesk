/**
 * FreeSWITCH ESL（Event Socket Library）客户端实现的内嵌版本。
 *
 * 背景与动机：
 * <ul>
 *   <li>上游 {@code org.freeswitch.esl.client} 长期未更新，存在兼容性与维护风险；</li>
 *   <li>为降低外部依赖带来的不确定性，将相关实现收拢至本仓库便于按需修复与扩展；</li>
 *   <li>实现参考并吸收了社区方案（如 esl-client、thingscloud/freeswitch-esl 等）的设计思路，
 *       具体出处与差异请参见各源文件头部注释。</li>
 * </ul>
 *
 * 主要能力：
 * <ul>
 *   <li>Inbound/Outbound 连接管理（见 {@code client.inbound} 与 {@code outbound} 包）；</li>
 *   <li>ESL 消息编解码与命令发送（见 {@code transport.message}、{@code transport.SendMsg} 等）；</li>
 *   <li>事件解析与分发（见 {@code transport.event.EslEvent}、{@code client.inbound.IEslEventListener}）；</li>
 *   <li>基于 Apache MINA 的网络与协议处理（依赖 {@code org.apache.mina:mina-core}）。</li>
 * </ul>
 *
 * 使用提示：
 * <ul>
 *   <li>建议通过 {@code client.inbound.Client} 发起到 FreeSWITCH 的 ESL 连接，并注册
 *       {@code client.inbound.IEslEventListener} 监听事件；</li>
 *   <li>Outbound 模式可通过 {@code outbound.IClientHandler}（及其工厂）处理 FS 回拨到应用侧的会话；</li>
 *   <li>若遇到协议字段差异，优先在本包内做兼容与适配，避免直接新增外部实现作为依赖；</li>
 *   <li>注意资源释放：在应用关闭时优雅关闭连接与线程池，避免潜在的资源泄露。</li>
 * </ul>
 *
 * 许可与版权：
 * <ul>
 *   <li>本目录中的实现遵循上游项目的开源许可条款；如与本仓库 LICENSE 存在差异，以各源文件声明为准；</li>
 *   <li>原作者与相关社区项目保留其各自版权；如有侵权或许可疑义，请联系维护者处理。</li>
 * </ul>
 *
 * 兼容性：
 * <ul>
 *   <li>已在 JDK 17 环境下编译与运行；</li>
 *   <li>与 FreeSWITCH 1.10+ 的 ESL 协议字段保持兼容，若上游变更请在本包内同步适配。</li>
 * </ul>
 *
 * 包级约定：
 * <ul>
 *   <li>通过 {@link org.springframework.lang.NonNullApi} 约定：除非另有标注，参数与返回值默认非空。</li>
 * </ul>
 */
@NonNullApi
package com.bytedesk.call.esl;

import org.springframework.lang.NonNullApi;
