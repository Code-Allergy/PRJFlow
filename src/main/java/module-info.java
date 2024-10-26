module com.cmpt370T7.PRJFlow {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires javafx.swing;
    requires org.kordamp.ikonli.javafx;

    opens com.cmpt370T7.PRJFlow to javafx.fxml;
    exports com.cmpt370T7.PRJFlow;
}
