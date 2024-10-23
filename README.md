# CMPT370T7 Document Tool

## Overview
This project is a document search tool, developed using JavaFX for the user interface and TestFX for testing. We utilize JUnit 5 for unit testing, ensuring robust and maintainable code.

## Technologies Used
- **JavaFX**: A framework for building rich client applications in Java.
  - [JavaFX Documentation](https://openjfx.io/)
  - [JavaFX GitHub Repository](https://github.com/openjdk/jfx)

- **JUnit 5**: A popular testing framework for Java that allows for easy writing and execution of tests.
  - [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
  - [JUnit 5 GitHub Repository](https://github.com/junit-team/junit5)

- **TestFX**: A testing framework for JavaFX applications, providing a fluent API for writing tests.
  - [TestFX Documentation](https://testfx.github.io/)
  - [TestFX GitHub Repository](https://github.com/TestFX/TestFX)

- **Tesseract**: Image OCR library for parsing images of text (via JavaCPP-tesseract).
  - [JavaCPP Tesseract Documentation](http://bytedeco.org/javacpp-presets/tesseract/apidocs/)
  - [JavaCPP Tesseract Github Repository](https://github.com/bytedeco/javacpp-presets/blob/master/tesseract/README.md)


- **Team: Maintain this list with all technologies used.**

- **GUI Team: I would recommend using the scene builder built into IntelliJ IDEA or installing [Gluon Scene Builder](https://gluonhq.com/products/scene-builder/) to help with building your layouts.**



## Getting Started
1. **Clone the repository**:
   ```bash
   git clone git@git.cs.usask.ca:wwj033/cmpt370t7.git
   cd cmpt370t7

2. Set up your development environment:
    Ensure you have JDK 21 installed.
    Configure your IDE to include JavaFX libraries.

3. Run the application:

    Windows:
    ```
    .\mvnw javafx:run
    ```
    Linux:
    ```
    ./mvnw javafx:run
    ```

## Testing
I have added two testing files so you can see how they work. The `HelloControllerTest` tests the GUI of the application, to make sure it performs as expected.
The second file `JUnitTest` has two trivial tests, to show the assertion library that is being used (AssertJ).


## Writing Tests
Ideally, name your test classes as `ClassNameTest` and name your test methods VERY descriptively (with snake_case), that way we can keep track of tests easier and read results easier.

1. UI Tests:
   * Create new test classes extending ApplicationExtension
   * Use @Start annotated method to set up the JavaFX environment as needed
   * Utilize FxRobot for simulating user interactions
   * Use TestFX assertions for verifying UI state

     ```java
     @ExtendWith(ApplicationExtension.class)
     class NewUITest {
         @Start
         private void start(Stage stage) throws IOException {
             // Set up whatever you need for your scene here, 
             // it will be executed before any methods with the @test annotation 
         }
     
         @Test
         void label_should_change_after_button_press(FxRobot robot) {
             robot.clickOn("#newButton"); // This targets an element with a CSS id matching "newButton"
             Assertions.assertThat(robot.lookup("#resultLabel").queryAs(Label.class)).hasText("Expected After Change");
         }
     }
     ```
2. Unit Tests:
   * Create new test classes for the logic you are testing
   * Use [JUnit 5 annotations](https://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations) (@Test, @BeforeEach, etc.)
   * Use [AssertJ](https://assertj.github.io/doc/#assertj-core-assertions-guide) for clear and expressive assertions

     ```java
     class NewLogicTest {
         @Test
         void test_some_feature() {
             SomeClassToTest logic = new SomeClassToTest();
             Assertions.assertThat(logic.performOperation()).isEqualTo(expectedResult);
         }
     }
     ```

## Running Tests
**When running tests, make sure to not interact with your mouse.** The test runner will interact with the program, performing actions in the application using your pointer. Moving your mouse during these tests could cause some to fail!

To run all the tests using JUnit 5 and TestFX, execute the following command:

Windows:

```
.\mvnw test
```
Linux:
```
./mvnw test
```

## Nix Support
This repo contains a nix flake with a development shell containing all dependencies. If you have the nix package manager installed, you can run this command to enter the shell:
```
nix develop
```

The shell contains all linked libraries required, along with including [Gluon Scene Builder](https://gluonhq.com/products/scene-builder/) for creating FXML templates.
