# API Documentation

このディレクトリには、Tapirで定義されたAPIのOpenAPI仕様書が格納されます。

## サーバー起動時に自動提供

サーバーを起動すると、以下のエンドポイントでAPIドキュメントにアクセスできます：

- **Swagger UI**: http://localhost:8081/docs
- **OpenAPI仕様書（YAML）**: http://localhost:8081/docs/docs.yaml

サーバーを起動して、ブラウザで http://localhost:8081/docs にアクセスするだけで、インタラクティブなAPI仕様書を確認できます。

## OpenAPI仕様書の生成

sbtコマンドで自動生成できます：

```bash
sbt generateApiDocs
```

このコマンドを実行すると、`docs/openapi.yaml` が生成されます。

## 生成されるファイル

- **openapi.yaml**: OpenAPI 3.0形式のAPI仕様書

## 仕様書の確認方法

### 1. Swagger Editorで確認

オンラインエディタで確認できます：

1. https://editor.swagger.io/ を開く
2. 生成された `openapi.yaml` の内容をコピー&ペースト

### 2. VSCodeで確認

VSCode拡張機能を使用：

- [OpenAPI (Swagger) Editor](https://marketplace.visualstudio.com/items?itemName=42Crunch.vscode-openapi)
- [Swagger Viewer](https://marketplace.visualstudio.com/items?itemName=Arjun.swagger-viewer)

### 3. Swagger UIをローカルで起動

Dockerを使用：

```bash
docker run -p 8080:8080 -e SWAGGER_JSON=/docs/openapi.yaml -v $(pwd)/docs:/docs swaggerapi/swagger-ui
```

ブラウザで http://localhost:8080 にアクセス
