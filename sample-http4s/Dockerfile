# マルチステージビルド: ビルドステージ
FROM sbtscala/scala-sbt:eclipse-temurin-21.0.8_9_1.11.7_3.3.6 AS builder

WORKDIR /app

# 依存関係の解決を先に行い、キャッシュを活用
COPY build.sbt .
COPY project ./project
RUN sbt update

# ソースコードをコピーしてビルド
COPY src ./src
RUN sbt assembly

# 実行ステージ: 軽量なJREイメージ
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# ビルドステージからfat JARをコピー
COPY --from=builder /app/target/scala-3.3.6/sample-http4s-assembly-*.jar /app/app.jar

# ポート8081を公開
EXPOSE 8081

# アプリケーション実行
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
