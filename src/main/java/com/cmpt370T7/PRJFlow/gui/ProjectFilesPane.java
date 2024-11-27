package com.cmpt370T7.PRJFlow.gui;

import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;

public class ProjectFilesPane extends FlowPane {
    private static final double VSPACING = 5;
    private static final double HSPACING = 5;
    private static final double PADDING = 10;

    private static final String CONFIG_FILE = "prjflowconfig.toml";

    public ProjectFilesPane() {
        super();
        this.setStyle("-fx-background-color: #ffffff;");
        this.setPadding(new Insets(PADDING));
        this.setHgap(HSPACING);
        this.setVgap(VSPACING);
    }
}
