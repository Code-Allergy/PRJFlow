package com.cmpt370T7.PRJFlow;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PdfParser {

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
