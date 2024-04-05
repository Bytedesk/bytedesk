// https://chatui.io/sdk/custom-component
// data 即传给卡片的数据
// ctx 即暴露出来的对象，可用  appendMessage  展示消息、postMessage 发送消息，详细见 API
// meta 即传过来的辅助信息
export default function MyCard({ data, ctx, meta }) {
  console.log(data, ctx, meta);

  return <div>hello my card</div>;
}
