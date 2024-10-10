package com.cmpt370T7.PRJFlow;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class HelloControllerTest {
    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     * @throws IOException 
     */
    @Start
    private void start(Stage stage) throws IOException {
        // this would ideally not be the application class, but a controller class
        HelloApplication app = new HelloApplication();
        Scene scene = app.getScene();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void should_contain_button_with_text(FxRobot robot) {
        // Assertions.assertThat(button).hasText("button");

        // or (lookup by css class):
        // Assertions.assertThat(robot.lookup(".button").queryAs(Button.class)).hasText("button");
        // or (query specific type):
        // Assertions.assertThat(robot.lookup(".button").queryButton()).hasText("button");
        // or (lookup by css id):
        Assertions.assertThat(robot.lookup("#myButton").queryAs(Button.class)).hasText("Button");
    }

        /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void should_start_with_empty_text(FxRobot robot) {
        Assertions.assertThat(robot.lookup("#myLabel").queryAs(Label.class)).hasText("");
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    void when_button_is_clicked_text_changes(FxRobot robot) {
        // when:
        robot.clickOn("#myButton");

        // then:
        Assertions.assertThat(robot.lookup("#myLabel").queryAs(Label.class)).hasText("Welcome to JavaFX Application!");
    }
}


