module com.cmpt370T7.PRJFlow {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Optional JavaFX
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires javafx.swing;
    requires org.kordamp.ikonli.javafx;

    // Logging
    requires org.slf4j;

    // PDF parsing & OCR
    requires org.bytedeco.tesseract;

    // HTML parsing (used for Tesseract OCR output parsing)
    requires org.jsoup;

    // Global store
    requires java.sql;

    // Config
    // Required for imageIO functions
    requires toml4j;

    opens com.cmpt370T7.PRJFlow to javafx.fxml;
    exports com.cmpt370T7.PRJFlow.util;
    exports com.cmpt370T7.PRJFlow;
}
