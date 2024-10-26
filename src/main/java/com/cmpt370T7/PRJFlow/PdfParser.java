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

    // Function that extracts all data from a PDF file
    public static List<String> extractDataElementsFromPdf(String pdfPath) {
        List<String> extractedElements = new ArrayList<>();

        File file = new File(pdfPath);
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            // Splits the text into lines
            String[] lines = text.split("\n");

            Collections.addAll(extractedElements, lines);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractedElements;
    }

}
