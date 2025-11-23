# sample-http4s

http4sを使用したScala 3のサンプルアプリケーション

## 開発環境

- Scala 3.7.4
- http4s 0.23.30
- Tapir 1.12.3（OpenAPI + Swagger UI）
- Circe 0.14.14（JSON処理）
- sbt 1.10.6

## 使用技術

### Webフレームワーク
- **http4s**: 純粋関数型HTTPライブラリ
- **Tapir**: 型安全なAPIエンドポイント定義

### コード品質ツール
- **scalafmt**: コードフォーマッター
- **scalafix**: リファクタリング・リンティングツール

## ローカル実行

### sbtで実行

```bash
sbt run
```

アプリケーションは http://localhost:8081 で起動します。

### APIドキュメント

起動後、以下のURLでSwagger UIにアクセスできます：
- http://localhost:8081/docs

### OpenAPI仕様書の生成

```bash
sbt generateApiDocs
```

生成された仕様書は `docs/openapi.yaml` に保存されます。


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


## ビルド

### fat JARの作成

```bash
sbt assembly
```

生成されたJARは `target/scala-3.7/` に配置されます。

### JARの実行

```bash
java -jar target/scala-3.7/sample-http4s-assembly-*.jar
```
