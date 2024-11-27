package com.cmpt370T7.PRJFlow.llm;

import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopulateTxt {
    private static final Logger logger = LoggerFactory.getLogger(PopulateTxt.class);

    public static void appendDataToTxt(String data, String filename) {
        File file = new File(filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            logger.error("Failed to append data to text file: {}", filename, e);
        }
    }

    public static String promptFromDataTxt(String input) {
        // $env:OLLAMA_HOST="127.0.0.1:8000"
        // ollama serve
        // Args to start model 8000 is port num

        try {
            String response = AiEngine.getInstance().createTextSummary(input);
            logger.info("Response from Ollama Model: {}", response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Errored out";
    }

    public static void pasteToTxt(String filePath, String data) {
        File file = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(data);
        } catch (IOException e) {
            logger.error("Failed to overwrite text file: {}", filePath, e);
        }
    }

    public static void GenerateTxt(String inputFilePath, String outputFilePath) {
        String parsedData = PdfParser.extractDataElementsFromPdf(inputFilePath);
        String returnedPrompt = promptFromDataTxt(parsedData);
        pasteToTxt(outputFilePath, returnedPrompt);
    }

    public static void main(String[] args) {
        //String filename = "example.txt";

        //pasteToTxt(filename, "This is the first line.");
        //appendDataToTxt("This is the second line.", filename);

        //pasteToTxt(filename, "This text overwrites the entire file.");
    }
}