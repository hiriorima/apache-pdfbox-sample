# Apache PDFBox Sample Application

Apache PDFBoxを使用して、クエリパラメータから文字列を取得してPDFを生成するSpring Bootアプリケーションのサンプルです。
(コード、READMEともに、Claude Code生成)

## 特徴

- **日本語対応**: IPAゴシックフォントを使用して日本語テキストをPDFに出力
- **REST API**: シンプルなGETリクエストでPDF生成
- **エラーハンドリング**: フォント読み込みエラーやテキスト描画エラーに対応
- **複数行対応**: 改行文字（`\n`）による複数行テキストの出力

## 必要環境

- Java 21
- Gradle

## 使用方法

### アプリケーションの起動

```bash
./gradlew bootRun
```

### API の使用

```bash
# 英語テキストの例
curl "http://localhost:8080/api/pdf/generate?text=Hello%20World" -o output.pdf

# 日本語テキストの例
curl "http://localhost:8080/api/pdf/generate?text=%E3%81%93%E3%82%93%E3%81%AB%E3%81%A1%E3%81%AF" -o japanese.pdf

# 複数行テキストの例
curl "http://localhost:8080/api/pdf/generate?text=Line1%0ALine2%0ALine3" -o multiline.pdf
```

### API エンドポイント

**GET** `/api/pdf/generate`

- **パラメータ**: `text` (必須) - PDFに出力するテキスト
- **レスポンス**: PDF ファイル（`application/pdf`）
- **ファイル名**: `generated.pdf`

## 技術仕様

### 使用ライブラリ

- **Spring Boot 3.5.3**: Webアプリケーションフレームワーク
- **Apache PDFBox 3.0.2**: PDF生成ライブラリ
- **IPA Gothic Font**: 日本語フォント（同梱）

### フォントについて

このアプリケーションは以下のフォント戦略を使用しています：

1. **優先フォント**: IPAゴシック (`ipaexg.ttf`)
   - 日本語文字の完全サポート
   - `src/main/resources/fonts/` に同梱

2. **フォールバックフォント**: Helvetica
   - IPAフォント読み込み失敗時に使用
   - 英数字のみサポート

### Format 14 cmap テーブルについて

アプリケーション起動時に以下の警告が表示される場合がありますが、**これは正常な動作であり、機能に影響はありません**：

```
WARN org.apache.fontbox.ttf.CmapSubtable : Format 14 cmap table is not supported and will be ignored
```

#### 警告の詳細

- **Format 14 cmap テーブル**は異体字セレクタ（Variation Selector）をサポートするためのもの
- **異体字セレクタ**とは、同じ文字コードで異なる字体を選択する機能
  - 例：漢字の新字体・旧字体の選択
  - 例：絵文字の肌色バリエーション
- IPAフォントはFormat 14に対応していないため、この警告が表示される
- **基本的な日本語文字の表示には全く影響なし**

#### 対応状況

- ✅ **基本的な日本語文字**: 完全対応
- ❌ **異体字セレクタ**: 未対応（警告表示）
- ✅ **一般的な用途**: 問題なし

この警告を非表示にするため、`application.properties`で以下の設定を行っています：

```properties
logging.level.org.apache.fontbox.ttf.CmapSubtable=ERROR
```

## 実装詳細

### PDF生成処理

1. PDFドキュメントと新しいページを作成
2. IPAゴシックフォントを読み込み（失敗時はHelveticaにフォールバック）
3. テキストを複数行に分割して描画
4. ByteArrayOutputStreamでPDFデータを生成
5. HTTPレスポンスとして返却

### エラーハンドリング

- フォント読み込みエラー時の自動フォールバック
- 文字描画エラー時の代替文字表示
- 適切なHTTPステータスコードの返却

## プロジェクト構成

```
src/
├── main/
│   ├── java/com/example/pdfbox/
│   │   ├── PdfboxApplication.java      # メインクラス
│   │   └── PdfController.java          # PDFコントローラ
│   └── resources/
│       ├── application.properties      # 設定ファイル
│       └── fonts/
│           └── ipaexg.ttf             # IPAゴシックフォント
└── test/
    └── java/com/example/pdfbox/
        └── PdfboxApplicationTests.java # テストクラス
```

## ライセンス

### IPAフォント
このプロジェクトに含まれるIPAゴシックフォント（`ipaexg.ttf`）は、**IPA Font License v1.0**でライセンスされています。

- ✅ **再配布許可**: 商用・非商用問わず配布可能
- ✅ **改変許可**: 特定条件下での改変可能
- ✅ **使用制限なし**: 自由に使用可能
- ⚠️ **ライセンス表示義務**: 再配布時は元ライセンスの添付必須
- ⚠️ **名称保持**: フォント名の変更不可

[公式ライセンス](https://opensource.org/license/ipafont-html)をご確認ください。

**著作権表示**:
Copyright (c) Information-technology Promotion Agency, Japan (IPA), 2003-2010.