module com.cmpt370T7.PRJFlow {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Optional JavaFX
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires javafx.swing;
    requires org.kordamp.ikonli.javafx;

    // Logging
    requires org.slf4j;

    // PDF parsing & OCR
    requires org.apache.pdfbox;
    requires org.bytedeco.tesseract;

    // HTML parsing (used for Tesseract OCR output parsing)
    requires org.jsoup;

    // Global store
    requires java.sql;

    // Config
    requires toml4j;

    // Required for imageIO functions
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens com.cmpt370T7.PRJFlow to javafx.fxml;
    exports com.cmpt370T7.PRJFlow.util;
    exports com.cmpt370T7.PRJFlow;
}
