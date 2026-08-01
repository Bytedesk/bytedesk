# rpc

该包负责内部 gRPC 服务适配与跨模块远程调用支撑。

## 实现要点

- GrpcServerService 是核心的服务端 RPC 适配器，用于跨服务边界暴露内部模块能力。
