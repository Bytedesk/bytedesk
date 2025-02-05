# Bytedesk - チャットサービス

AIを活用したマルチチャネルカスタマーサービスとチーム連携

:::tip
Bytedeskは現在、急速な開発段階にあり、ドキュメントが開発に追いつかない場合があります。そのため、機能の説明が最新のソフトウェアバージョンと一致しない可能性があります。
:::

## 主な機能

### チームIM

- 多層組織構造
- 役割管理
- 権限管理
- その他

### カスタマーサービス

- 複数チャネルのサポート
- 複数のルーティング戦略と詳細な評価指標
- オペレーター作業台
- その他

### ナレッジベース

- 内部文書
- ヘルプセンター
- その他

### チケット管理

- チケット管理
- チケットSLA管理
- チケット統計とレポート
- その他

### AIチャット

- LLMとのチャット
- ナレッジベースとのチャット(RAG)
- その他

## Dockerクイックスタート

### プロジェクトのクローンとDockerコンテナの起動

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose.yaml up -d
```

### コンテナの停止

```bash
docker compose -p bytedesk -f docker-compose.yaml stop
```

## プレビュー

ローカルプレビュー

```bash
http://127.0.0.1:9003/dev
```

- [オンラインプレビュー](https://www.weiyuai.cn/admin/)

## オープンソースクライアント

- [デスクトップ](https://github.com/Bytedesk/bytedesk-desktop)
- [モバイル](https://github.com/Bytedesk/bytedesk-mobile)

## ライセンス

- [Apache License 2.0](./LICENSE.txt) 