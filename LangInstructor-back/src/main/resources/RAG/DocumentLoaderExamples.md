# DocumentLoaderExamples.java 示例

**示例目的:**
此文件演示了如何使用 LangChain4j 的文档加载工具批量加载本地文件、目录、递归目录、按通配符筛选文件，并支持通过 SPI 自动选择解析器。

**关键类和方法:**
- `loadDocument(Path, DocumentParser)`: 加载单个文档，支持自定义解析器（如 ApacheTika）。
- `loadDocuments(Path, DocumentParser)`: 加载目录下所有文档。
- `loadDocuments(Path, PathMatcher, DocumentParser)`: 按通配符（如 *.txt）加载目录下文档。
- `loadDocumentsRecursively(Path, DocumentParser)`: 递归加载目录及子目录下所有文档。
- `loadDocument(Path)`: 通过 SPI 自动选择解析器加载文档。
- `ApacheTikaDocumentParser`: 支持多种格式（PDF、Word、文本等）的通用解析器。
- `toPath(String)`: 辅助方法，将资源路径转为文件系统路径。

**注意事项:**
- 适合批量文档处理、知识库构建、RAG 场景等。
- 支持多种文件格式和目录结构，灵活性强。
- 日志输出便于调试和结果验证。

```java
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.*;

public class DocumentLoaderExamples {

    private static final Logger log = LoggerFactory.getLogger(DocumentLoaderExamples.class);

    public static void main(String[] args) {
        loadSingleDocument();
        loadMultipleDocuments();
        loadMultipleDocumentsWithGlob();
        loadMultipleDocumentsRecursively();
        loadUsingParserFromSPI();
    }

    private static void loadSingleDocument() {
        Path documentPath = toPath("example-files/story-about-happy-carrot.pdf");
        log.info("Loading single document: {}", documentPath);
        Document document = loadDocument(documentPath, new ApacheTikaDocumentParser());
        log(document);
        log.info("");
    }

    private static void loadMultipleDocuments() {
        Path directoryPath = toPath("example-files/");
        log.info("Loading multiple documents from: {}", directoryPath);
        List<Document> documents = loadDocuments(directoryPath, new ApacheTikaDocumentParser());
        documents.forEach(DocumentLoaderExamples::log);
        log.info("");
    }

    private static void loadMultipleDocumentsWithGlob() {
        Path directoryPath = toPath("example-files/");
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.txt");
        log.info("Loading *.txt documents from: {}", directoryPath);
        List<Document> documents = loadDocuments(directoryPath, pathMatcher, new ApacheTikaDocumentParser());
        documents.forEach(DocumentLoaderExamples::log);
        log.info("");
    }

    private static void loadMultipleDocumentsRecursively() {
        Path directoryPath = toPath("example-files/");
        log.info("Loading multiple documents recursively from: {}", directoryPath);
        List<Document> documents = loadDocumentsRecursively(directoryPath, new ApacheTikaDocumentParser());
        documents.forEach(DocumentLoaderExamples::log);
        log.info("");
    }

    private static void loadUsingParserFromSPI() {
        Path documentPath = toPath("example-files/story-about-happy-carrot.pdf");
        log.info("Loading using parser imported through SPI: {}", documentPath);
        Document document = loadDocument(documentPath); // we are not specifying a parser here, it is imported through SPI
        log(document);
        log.info("");
    }

    private static void log(Document document) {
        log.info("{}: {} ...", document.metadata().getString("file_name"), document.text().trim().substring(0, 50));
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = DocumentLoaderExamples.class.getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
