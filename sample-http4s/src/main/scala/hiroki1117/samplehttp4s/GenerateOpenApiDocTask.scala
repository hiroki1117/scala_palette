package hiroki1117.samplehttp4s

/** sbt generateApiDocs タスク専用のエントリーポイント */
object GenerateOpenApiDocTask:
  def main(args: Array[String]): Unit =
    GenerateOpenApiDoc.generate()
