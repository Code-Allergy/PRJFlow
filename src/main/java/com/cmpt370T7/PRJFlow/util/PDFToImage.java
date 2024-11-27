package com.cmpt370T7.PRJFlow.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The PDFToImage class provides methods for extracting images from a PDF file.
 */
public class PDFToImage {
    private static final Logger logger = LoggerFactory.getLogger(PDFToImage.class);
    /**
     * Extracts images of all pages from the specified PDF file.
     *
     * @param file the PDF file from which to extract images
     * @param dpi the dpi to render the images at
     * @return a list of BufferedImages representing the pages of the PDF
     * @throws IOException if there is an error loading the PDF file or rendering the images
     */
    public static List<BufferedImage> extractAll(File file, float dpi) throws IOException {
        ArrayList<BufferedImage> images = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, dpi);
                images.add(bufferedImage);
            }
            return images;

        } catch (IOException e) {
            logger.error("Error extracting images from PDF: {}", file.getName(), e);
            throw e;
        }
    }

    /**
     * Extracts a single image of the specified PDF file at the page specified.
     *
     * @param file the PDF file from which to extract images
     * @param dpi the dpi to render the images at
     * @param page The <strong>index</strong> of the page number (0 indexed)
     * @return A {@link BufferedImage}
     * @throws IOException if there is an error loading the PDF file or rendering the images
     * @throws IndexOutOfBoundsException if the page number is invalid.
     */
    public static BufferedImage extract(File file, float dpi, int page) throws IOException {
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            return pdfRenderer.renderImageWithDPI(page, dpi);
        } catch (IOException e) {
            logger.error("Error extracting images from PDF: {}", file.getName(), e);
            throw e;
        }
    }
}
