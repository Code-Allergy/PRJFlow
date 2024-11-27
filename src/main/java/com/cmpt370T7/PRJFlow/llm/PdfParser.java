package com.cmpt370T7.PRJFlow.llm;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PdfParser {

    /**
     * Extracts and cleans text content from a PDF file.
     * <p>
     * Uses Apache PDFBox to extract text from the specified PDF file and removes
     * newline (`\n`) and carriage return (`\r`) characters, returning the cleaned text.
     *
     * @param pdfPath The file path to the PDF document.
     * @return A single string containing the cleaned extracted text
     */
    public static String extractDataElementsFromPdf(String pdfPath) {
        StringBuilder extractedText = new StringBuilder();

        File file = new File(pdfPath);
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            // Removes newlines and carriage returns
            text = text.replace("\n", " ").replace("\r", " ");

            // Adds the cleaned text to the StringBuilder
            extractedText.append(text);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractedText.toString();
    }
}
