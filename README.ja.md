# Bytedesk - チャットサービス

AIを活用したオムニチャネル顧客対応とチームコラボレーション

## 言語

- [English](./README.md)
- [中文](./README.zh.md)
- [日本語](./README.ja.md)

## 管理ダッシュボード

![statistics](./images/admin/statistics.png)

## 管理チャット

![chat](./images/admin/chat.png)

## 管理 LLM + Agent

![llm_agent](./images/admin/llm_agent.png)

## チャネル管理

![channel](./images/admin/channel.png)

## エージェントワークベンチ

![agent](./images/agent/agent_chat.png)

## 製品概要

### [TeamIM](./modules/team/readme.md)

- 多階層の組織構造
- ロール・権限管理
- 監査ログと可視化
- ...

### [カスタマーサービス](./modules/service/readme.md)

- Web/アプリ/ソーシャル/EC などマルチチャネル
- インテリジェントなルーティングと豊富なKPI
- 統合エージェントデスク
- ...

### [ナレッジベース](./modules/kbase/readme.md)

- 社内ドキュメント & ヘルプセンター
- FAQ と RAG ナレッジ
- AI エージェントとの連携
- ...

### [チケット管理](./modules/ticket/readme.md)

- チケットライフサイクル管理
- SLA 定義とトラッキング
- リアルタイム分析・レポート
- ...

### [AI Agent](./modules/ai/readme.md)

- Ollama / DeepSeek / ZhipuAI / ... とチャット
- ナレッジベース連携 (RAG)
- Function Calling と MCP
- ...

### [ワークフロー](./modules/core/readme.workflow.md)

- カスタムフォーム
- ビジュアルプロセスデザイナー
- チケットフロー自動化
- ...

### [Voice of Customer](./modules/voc/readme.md)

- フィードバック・アンケート・苦情
- 顧客満足度の継続的モニタリング
- ...

### [コールセンター](./plugins/freeswitch/readme.zh.md)

- FreeSwitch ベースのプロフェッショナル基盤
- ポップアップ表示、オートアサイン、録音
- 音声とテキストの統合ビュー

### [ビデオサポート](./plugins/webrtc/readme.zh.md)

- WebRTC による HD ビデオ通話
- ワンクリックのビデオ会話と画面共有
- 実演が必要なシーンに最適

### [オープンプラットフォーム](./plugins/readme.md)

- 完全な RESTful API とマルチ言語 SDK
- 外部システムとのシームレスな統合
- 開発・導入を高速化

## クイックスタート

```bash
git clone https://github.com/Bytedesk/bytedesk.git
cd bytedesk/deploy/docker
# AI なしで起動
docker compose -p bytedesk -f docker-compose-noai.yaml up -d
# ZhipuAI を利用（API Key 必須）
docker compose -p bytedesk -f docker-compose.yaml up -d
# ローカル Ollama を利用
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
```

- [Docker デプロイ](https://www.weiyuai.cn/docs/docs/deploy/docker/)
- [Baota デプロイ](https://www.weiyuai.cn/docs/docs/deploy/baota)
- [ソースから起動](https://www.weiyuai.cn/docs/docs/deploy/source)

## デモアクセス

```bash
# 127.0.0.1 をサーバーIPに置き換え
http://127.0.0.1:9003/
# 開放ポート: 9003, 9885
初期ユーザー: admin@email.com
初期パスワード: admin
```

## プロジェクト構成

Maven ベースのモノレポ (ルート `pom.xml`) で、複数モジュールとデプロイ資産を収録。

```text
bytedesk/
├─ channels/           # チャネル統合 (Douyin, ショップ, SNS, WeChat)
├─ demos/              # デモプロジェクト / サンプルコード
├─ deploy/             # Docker, K8s, サーバー設定
├─ enterprise/         # エンタープライズ機能 (ai, call, core, kbase, service, ticket)
├─ images/             # ドキュメント・UI 画像
├─ jmeter/             # パフォーマンステスト
├─ logs/               # ローカル / 開発ログ
├─ modules/            # コアモジュール (TeamIM, Service, KBase, Ticket, AI ...)
├─ plugins/            # オプションプラグイン (freeswitch, webrtc, open platform)
├─ projects/           # カスタムプロジェクト
├─ starter/            # スターター / エントリーポイント
```

## アーキテクチャ

- [アーキテクチャ図](https://www.weiyuai.cn/architecture.html)

## オープンソースクライアント

- [デスクトップ](https://github.com/Bytedesk/bytedesk-desktop)
- [モバイル](https://github.com/Bytedesk/bytedesk-mobile)
- [SipPhone](https://github.com/Bytedesk/bytedesk-phone)
- [Conference](https://github.com/Bytedesk/bytedesk-conference)
- [FreeSwitch Docker](https://github.com/Bytedesk/bytedesk-freeswitch)
- [Jitsi Docker](https://github.com/Bytedesk/bytedesk-jitsi)

## オープンソースデモ & SDK

| プロジェクト | 説明 | Forks | Stars |
|--------------|------|-------|-------|
| [iOS](https://github.com/bytedesk/bytedesk-swift) | ネイティブ iOS アプリ | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-swift) | ![GitHub Repo stars](https://img.shields.io/github/stars/Bytedesk/bytedesk-swift) |
| [Android](https://github.com/bytedesk/bytedesk-android) | ネイティブ Android アプリ | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-android) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-android) |
| [Flutter](https://github.com/bytedesk/bytedesk-flutter) | Flutter SDK | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-flutter) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-flutter) |
| [UniApp](https://github.com/bytedesk/bytedesk-uniapp) | UniApp パッケージ | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-uniapp) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-uniapp) |
| [Web](https://github.com/bytedesk/bytedesk-web) | Vue/React/Angular/Next.js フロントエンド | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-web) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-web) |
| [WordPress](https://github.com/bytedesk/bytedesk-wordpress) | WordPress プラグイン | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-wordpress) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-wordpress) |
| [WooCommerce](https://github.com/bytedesk/bytedesk-woocommerce) | WooCommerce 連携 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-woocommerce) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-woocommerce) |
| [Magento](https://github.com/bytedesk/bytedesk-magento) | Magento 拡張 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-magento) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-magento) |
| [PrestaShop](https://github.com/bytedesk/bytedesk-prestashop) | PrestaShop モジュール | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-prestashop) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-prestashop) |
| [Shopify](https://github.com/bytedesk/bytedesk-shopify) | Shopify アプリ | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-shopify) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-shopify) |
| [OpenCart](https://github.com/bytedesk/bytedesk-opencart) | OpenCart プラグイン | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-opencart) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-opencart) |
| [Laravel](https://github.com/bytedesk/bytedesk-laravel) | Laravel パッケージ | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-laravel) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-laravel) |
| [Django](https://github.com/bytedesk/bytedesk-django) | Django アプリ | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-django) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-django) |

## リンク

- [ダウンロード](https://www.weiyuai.cn/download.html)
- [ドキュメント](https://www.weiyuai.cn/docs/)

## ライセンス

Copyright (c) 2013-2025 Bytedesk.com.

GNU AFFERO GENERAL PUBLIC LICENSE (AGPL v3) に基づき公開：

<https://www.gnu.org/licenses/agpl-3.0.html>

ソフトウェアは「現状有姿」で提供され、明示または黙示の保証はありません。

## 利用規約

- **許可される用途**: 商用利用は可能、無断転売は禁止
- **禁止用途**: マルウェア、詐欺、ギャンブル等の違法行為
- **免責事項**: 利用は自己責任で行ってください