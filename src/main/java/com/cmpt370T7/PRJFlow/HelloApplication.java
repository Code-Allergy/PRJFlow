package com.cmpt370T7.PRJFlow;

import com.cmpt370T7.PRJFlow.util.PDFToImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class HelloApplication extends Application {

    public Scene getScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
            HelloApplication.class.getResource("hello-view.fxml")
        );
        return new Scene(fxmlLoader.load(), 320, 240);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = getScene();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    /// Example code
    public static void test_parse_pdf(File file, String query)
        throws IOException {
        List<BufferedImage> images = PDFToImage.extractAll(file, 300);
        List<BufferedImage> extracted = new ArrayList<>();
        TextExtractor textExtractor = new TextExtractor();
        for (BufferedImage image : images) {
            TextExtractor.TextElement element =
                textExtractor.extractTextHierarchy(image);
            List<TextExtractor.TextElement> filtered =
                element.getParagraphsWith(query);
            for (TextExtractor.TextElement textElement : filtered) {
                BufferedImage highlighted = ImageDrawer.highlightParagraphText(
                    image,
                    textElement,
                    query
                );
                extracted.add(highlighted);
            }
        }

        for (int i = 0; i < extracted.size(); i++) {
            ImageIO.write(
                extracted.get(i),
                "png",
                new File("sample-files/images/test-img-out/" + i + ".png")
            );
        }
    }

    public static void main(String[] args) {
//        AppDataManager.instantiate(); broken on windows :(

//        try {
//            test_parse_pdf(
//                new File(
//                    "sample-files/OneDrive_1_2024-10-21/Cree Nations/22039 Addendum No. 4.pdf"
//                ),
//                "Window"
//            );
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        launch();
    }
}
