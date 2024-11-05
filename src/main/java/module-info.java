module com.cmpt370T7.PRJFlow {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    // Optional JavaFX
    requires javafx.swing;

    // Icons
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign;

    // Logging
    requires org.slf4j;

    // PDF parsing & OCR
    requires org.apache.pdfbox;
    requires org.bytedeco.tesseract;

    // HTML parsing (used for Tesseract OCR output parsing)
    requires org.jsoup;

    // Config
    requires toml4j;

    // Required for imageIO functions
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires org.apache.pdfbox.io;

    opens com.cmpt370T7.PRJFlow to javafx.fxml;
    exports com.cmpt370T7.PRJFlow.util;
    exports com.cmpt370T7.PRJFlow;
}
