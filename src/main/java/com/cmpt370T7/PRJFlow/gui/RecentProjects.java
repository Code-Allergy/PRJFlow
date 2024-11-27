package com.cmpt370T7.PRJFlow.gui;

import com.cmpt370T7.PRJFlow.Project;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;

public class RecentProjects extends VBox {
    private static final double PADDING = 10;

    private final ListView<Project> projectsListView;

    private final Button newProjectButton;
    private final Button deleteProjectButton;

    public RecentProjects(List<Project> projects) {
        super(10);
        this.setPadding(new Insets(PADDING));
        this.setStyle("-fx-background-color: #f0f0f0;");

        Label projectsLabel = new Label("Recent Projects");
        projectsListView = new ListView<>();
        projectsListView.getItems().addAll(projects);

        newProjectButton = new Button("New Project");
        deleteProjectButton = new Button("Delete Project");

        this.getChildren().addAll(projectsLabel, projectsListView, newProjectButton, deleteProjectButton);
    }

    public ListView<Project> getProjectsListView() {
        return projectsListView;
    }

    public Project getSelectedProject() {
        return projectsListView.getSelectionModel().getSelectedItem();
    }

    public void setOnProjectSelected(Runnable action) {
        projectsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                action.run();
            }
        });
    }

    public void setOnNewProject(Runnable action) {
        newProjectButton.setOnAction(e -> action.run());
    }

    public void setOnDeleteProject(Runnable action) {
        deleteProjectButton.setOnAction(e -> action.run());
    }

}
