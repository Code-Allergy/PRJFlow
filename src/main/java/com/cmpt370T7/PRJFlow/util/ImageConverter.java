package com.cmpt370T7.PRJFlow.util;

import org.bytedeco.leptonica.PIX;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.bytedeco.leptonica.global.leptonica.pixReadMemPng;


/// Utility methods to convert images into formats suitable for OCR processing with Tesseract.
public class ImageConverter {

    /**
     * Converts a BufferedImage into a PIX format image for use with Tesseract OCR
     * <p>
     * This method takes a BufferedImage, writes it into a byte array in PNG format,
     * and then converts the byte array into a Leptonica PIX image using the
     * Leptonica library's pixReadMemPng() function.
     * </p>
     * <p><strong>Note:</strong> The caller is responsible for freeing the
     * returned PIX object using pixDestroy() to avoid memory leaks.</p>
     * @param bufferedImage a buffered image in any format
     * @return a PIX image that must be freed by the caller.
     */
    public static PIX toPIX(BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return null;
        }
        try {
            // Write the BufferedImage to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageData = baos.toByteArray();

            // Convert byte array to PIX using Leptonica
            return pixReadMemPng(imageData, imageData.length);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
