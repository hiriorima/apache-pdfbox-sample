package com.example.pdfbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @GetMapping("/generate")
    public ResponseEntity<ByteArrayResource> generatePdf(@RequestParam String text) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // フォントを設定（日本語対応）
            PDFont font;
            try {
                InputStream fontStream = getClass().getResourceAsStream("/fonts/ipaexg.ttf");
                if (fontStream != null) {
                    font = PDType0Font.load(document, fontStream);
                    System.out.println("IPAフォント読み込み成功");
                } else {
                    System.out.println("IPAフォントファイルが見つからないため、デフォルトフォントを使用");
                    font = new org.apache.pdfbox.pdmodel.font.PDType1Font(FontName.HELVETICA);
                }
            } catch (Exception e) {
                System.out.println("フォント読み込みエラー: " + e.getMessage());
                font = new org.apache.pdfbox.pdmodel.font.PDType1Font(FontName.HELVETICA);
            }

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 750);
                
                // 複数行に対応
                String[] lines = text.split("\n");
                for (String line : lines) {
                    try {
                        contentStream.showText(line);
                        contentStream.newLine();
                    } catch (Exception e) {
                        System.out.println("テキスト描画エラー: " + e.getMessage() + " - テキスト: " + line);
                        // 英数字のみに置換して描画を試行
                        String fallbackText = line.replaceAll("[^\\x00-\\x7F]", "?");
                        contentStream.showText(fallbackText);
                        contentStream.newLine();
                    }
                }
                
                contentStream.endText();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generated.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        }
    }

    public static class PdfRequest {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}