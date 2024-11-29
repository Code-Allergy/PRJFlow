# CMPT370T7 Document Tool

## Overview
This project is a document search tool, developed using JavaFX for the user interface and TestFX for testing. This tool
helps you parse through documents, utilizing Large Language Models (LLMs) to search for specific information. The tool
runs natively on your machine (Windows, Linux, MacOS) and is designed to be user-friendly. Supports both locally hosted
language models, as well as cloud-based models (via GroqCloud, but easily extendable).

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
- ** Before submission, ensure this list is up to date!!**
## Installation

### Dependencies
You should have a version of JDK21 installed locally on your system if you are building from source.
All other Java dependencies are installed via Maven, which is bundled in the repo. There
are no external dependencies, and everything required is managed through Maven.

### Project Setup
To get started with our project, clone it locally to your system.
Then, navigate to the root folder and run the command
```
./mvnw dependency:resolve
```
to preload all Maven dependencies. This step is not strictly necessary, however
it helps to separate issues.

The project will now have all dependencies installed and available.

The only other dependency is Ollama optionally, however the program will walk you
through setting it up if you choose to use it. Otherwise, you can use the program without
Ollama, using the default GroqCloud models (which the program will instruct you how to setup).


### Building and Running
To build our project and run in development mode, you can use the maven command
```
./mvnw javafx:run
```
This will compile all classes and launch the program on your system, without
any packaging for release.

### Building for Cross-Platform
To build the project for cross-platform distribution, you can use the command
```
./mvnw package -f pom-cross.xml
```
This will create a Jar file with all classes and dependencies integrated. This
jar file should work across several different system operating systems, as long
as the user has Java installed. To decrease the hassle for our stakeholder, we also
provide a simple installer for Windows.

### Building for Release (Windows)
First install [WiX Tools](https://github.com/wixtoolset/wix3/releases), as it is required by jpackage.


In order to build the project for distribution, we include a batch script to
create a simple installer. To use the batch script, you will first need to package
the application by running the command
```
./mvnw package
```
This will create a Jar file with all classes and dependencies integrated. However, for an easier experience without requiring
the user to install java themselves, we can create a simple installer for them,
by running the script at the root.
```
./build_windows.bat
```
This will bundle the Jar file using JPackage, creating a simple Windows installer. This installer creates a shortcut
on the desktop, and installs the application in the Program Files directory. The user can then run the application
from the start menu or desktop shortcut. It also enables the user to uninstall the application from the control panel.

## Getting Started (Development)
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

## Testing (Development)
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

TODO: segment about skipping UI based tests.

## Documentation
A live version of our documentation can be viewed at [this link](https://cmpt370.vada.life). This site includes
all javadocs, along with testing coverage and testing reports. If the website is unavailable, or you want a development version,
you can generate the documentation locally by running the following command:
```
./mvnw site
```
This will generate the documentation in the `target/site` directory.
You can then open the `index.html` file in your browser to view the documentation.

## Nix Support
This repo contains a nix flake with a development shell containing all dependencies. If you have the nix package manager installed, you can run this command to enter the shell:
```
nix develop
```

The shell contains all linked libraries required, along with including [Gluon Scene Builder](https://gluonhq.com/products/scene-builder/) for creating FXML templates.
