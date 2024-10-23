package com.cmpt370T7.PRJFlow.util;

import org.bytedeco.leptonica.PIX;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bytedeco.leptonica.global.leptonica.*;

public class ImageConverterTest {
    private PIX pixImage;
    /**
     * Creates a sample BufferedImage for testing purposes.
     *
     * @return a simple BufferedImage
     */
    private BufferedImage createTestImage() {
        int width = 100;
        int height = 100;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // Fill the image with a solid color (e.g., white)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bufferedImage.setRGB(x, y, 0xFFFFFF); // White color
            }
        }
        return bufferedImage;
    }

    @AfterEach
    public void tearDown() {
        pixDestroy(pixImage);
        pixImage = null;
    }

    @Test
    public void test_toPIX_valid_BufferImage() {
        // Arrange
        BufferedImage bufferedImage = createTestImage();

        // Act
        pixImage = ImageConverter.toPIX(bufferedImage);

        // Assert
        assertThat(pixImage).isNotNull(); // Check that the PIX image is not null
        // Optionally check that the PIX image dimensions match the BufferedImage dimensions
        assertThat(pixGetWidth(pixImage)).isEqualTo(bufferedImage.getWidth());
        assertThat(pixGetHeight(pixImage)).isEqualTo(bufferedImage.getHeight());
    }

    @Test
    public void test_toPIX_null_BufferImage() {
        pixImage = ImageConverter.toPIX(null);
        assertThat(pixImage).isNull(); // Check that the PIX image is null
    }

    @Test
    public void test_toPIX_transparent_BufferImage() {
        // Arrange
        int width = 100;
        int height = 100;
        BufferedImage transparentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Fill the image with transparency
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                transparentImage.setRGB(x, y, 0x00000000); // Fully transparent
            }
        }

        // Act
        pixImage = ImageConverter.toPIX(transparentImage);

        // Assert
        assertThat(pixImage).isNotNull(); // Check that the PIX image is not null
        assertThat(pixGetWidth(pixImage)).isEqualTo(transparentImage.getWidth());
        assertThat(pixGetHeight(pixImage)).isEqualTo(transparentImage.getHeight());
    }
}
