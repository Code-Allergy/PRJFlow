package com.cmpt370T7.PRJFlow.gui;

import com.cmpt370T7.PRJFlow.Project;
import com.cmpt370T7.PRJFlow.util.AlertHelper;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class RecentProjects extends VBox {
    private static final double PADDING = 10;

    private final ListView<Project> projectsListView;

    private final Button newProjectButton;
    private final Button deleteProjectButton;
    private final Button editProjectNameButton;

    private final MenuItem renameProjectItem;
    private final MenuItem deleteProjectItem;
    private final ContextMenu contextMenu;




    public RecentProjects(List<Project> projects) {
        super(10);
        this.setPadding(new Insets(PADDING));
        this.setStyle("-fx-background-color: #E5E1DA;");

        Label projectsLabel = new Label("Recent Projects");
        projectsListView = new ListView<>();
        projectsListView.getItems().addAll(projects);

        newProjectButton = new Button("New Project");
        newProjectButton.getStyleClass().add("accent-button");

        deleteProjectButton = new Button("Delete Project");
        deleteProjectButton.getStyleClass().add("accent-button");

        editProjectNameButton = new Button("Edit Project Name");
        editProjectNameButton.getStyleClass().add("accent-button");

        contextMenu = new ContextMenu();
        renameProjectItem = new MenuItem("Rename Project");
        deleteProjectItem = new MenuItem("Delete Project");
        contextMenu.getItems().addAll(renameProjectItem, deleteProjectItem);

        // Attach context menu to ListView items
        projectsListView.setCellFactory(lv -> {
            ListCell<Project> cell = new ListCell<>() {
                @Override
                protected void updateItem(Project project, boolean empty) {
                    super.updateItem(project, empty);
                    setText(empty ? null : project.getName());
                }
            };

            cell.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY && !cell.isEmpty()) {
                    contextMenu.show(cell, e.getScreenX(), e.getScreenY());
                } else {
                    contextMenu.hide();
                }
            });

            return cell;
        });

        this.getChildren().addAll(projectsLabel, projectsListView, newProjectButton, deleteProjectButton, editProjectNameButton);
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

    public void setOnEditProjectName(Runnable action) {
        editProjectNameButton.setOnAction(e -> action.run());
    }

    public void setOnContextEditProjectName(Runnable action) {
        renameProjectItem.setOnAction(e -> action.run());
    }

    public void setOnContextDeleteProjectName(Runnable action) {
        deleteProjectItem.setOnAction(e -> action.run());
    }


}
