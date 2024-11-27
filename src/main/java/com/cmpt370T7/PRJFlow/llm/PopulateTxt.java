package com.cmpt370T7.PRJFlow.llm;

import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopulateTxt {
    private static final Logger logger = LoggerFactory.getLogger(PopulateTxt.class);

    /**
     * Appends data to the end of a text file, creating the file if it does not exist.
     *
     * @param data The data to append to the file.
     * @param filename The path to the text file.
     */
    public static void appendDataToTxt(String data, String filename) {
        File file = new File(filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            logger.error("Failed to append data to text file: {}", filename, e);
        }
    }

    /**
     * Generates a summary from input text using a local neural network model.
     *
     * @param input The input string to be processed by the model.
     * @return The response from the model as a string.
     */
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

    /**
     * Overwrites a text file with the given data.
     *
     * @param filePath The path to the text file.
     * @param data The data to write into the file.
     */
    public static void pasteToTxt(String filePath, String data) {
        File file = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(data);
        } catch (IOException e) {
            logger.error("Failed to overwrite text file: {}", filePath, e);
        }
    }

    /**
     * Extracts data from a PDF file, processes it with the model, and writes the result to a text file.
     *
     * @param inputFilePath The path to the input PDF file.
     * @param outputFilePath The path to the output text file.
     */
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