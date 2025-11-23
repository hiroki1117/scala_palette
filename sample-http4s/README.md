# sample-http4s

http4sを使用したScala 3のサンプルアプリケーション

## 機能

- `/hello` - シンプルなHelloエンドポイント
- `/users` - ユーザー管理API
- `/tasks` - タスク管理API

## 開発環境

- Scala 3.3.6
- http4s 0.23.30
- sbt 1.10.6

## ローカル実行

### sbtで実行

```bash
sbt run
```

アプリケーションは http://localhost:8081 で起動します。

### 動作確認

```bash
curl http://localhost:8081/hello
```

## Docker実行

### Dockerイメージのビルド

```bash
docker build -t sample-http4s:latest .
```

### Dockerコンテナの実行

```bash
docker run -p 8081:8081 sample-http4s:latest
```

### Docker Composeで実行（推奨）

```bash
# バックグラウンドで起動
docker-compose up -d

# ログを確認
docker-compose logs -f

# 停止
docker-compose down
```

### ヘルスチェック

```bash
curl http://localhost:8081/hello
```

## ビルド

### fat JARの作成

```bash
sbt assembly
```

生成されたJARは `target/scala-3.3.6/` に配置されます。

### JARの実行

```bash
java -jar target/scala-3.3.6/sample-http4s-assembly-*.jar
```

## プロジェクト構成

```
.
├── Dockerfile              # Dockerイメージ定義
├── docker-compose.yml      # Docker Compose設定
├── build.sbt              # sbtビルド設定
├── project/
│   └── plugins.sbt        # sbtプラグイン
└── src/
    └── main/
        └── scala/
            └── hiroki1117/
                └── samplehttp4s/
                    ├── Main.scala           # エントリポイント
                    ├── Server.scala         # サーバー設定
                    ├── UsersRoutes.scala    # ユーザーAPI
                    └── TasksRoutes.scala    # タスクAPI
```
