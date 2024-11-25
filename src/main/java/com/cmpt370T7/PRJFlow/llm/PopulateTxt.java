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

    public static void pasteToTxt(String filePath, String data) {
        File file = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(data);
        } catch (IOException e) {
            logger.error("Failed to overwrite text file: {}", filePath, e);
        }
    }

    public static void main(String[] args) {
        // Example usages
        String filename = "example.txt";

        //Appending to file
        pasteToTxt(filename, "This is the first line.");
        appendDataToTxt("This is the second line.", filename);

        // Overwriting or creating
        pasteToTxt(filename, "This text overwrites the entire file.");
    }
}