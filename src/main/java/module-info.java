module com.cmpt370T7.PRJFlow {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.slf4j;
    requires java.sql;
    requires toml4j;

    opens com.cmpt370T7.PRJFlow to javafx.fxml;
    exports com.cmpt370T7.PRJFlow;
}
