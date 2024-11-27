//package com.cmpt370T7.PRJFlow.util;
//
//import org.junit.jupiter.api.Test;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//public class PDFToImageTest {
//
//    private static final String MULTIPAGE_PDFFILE = "sample-files/OneDrive_1_2024-10-21/Cree Nations/Cree Nation Shade specs.pdf";
//    private static final int EXPECTED_PAGE_COUNT = 6; // 6 pages
//    @Test
//    public void test_extract_all() throws IOException {
//        File pdfFile = new File(MULTIPAGE_PDFFILE);
//
//        // Act
//        List<BufferedImage> images = PDFToImage.extractAll(pdfFile, 300);
//
//        // Assert
//        assertThat(images).isNotNull();
//        assertThat(images).isNotEmpty();
//        assertThat(images.size()).isEqualTo(EXPECTED_PAGE_COUNT);
//        for (BufferedImage image : images) {
//            assertThat(image).isNotNull();
//            assertThat(image.getWidth()).isGreaterThan(0);
//            assertThat(image.getHeight()).isGreaterThan(0);
//        }
//    }
//
//
//    @Test
//    public void test_extract_single_page_valid() throws IOException {
//        File pdfFile = new File(MULTIPAGE_PDFFILE);
//        int pageNumber = 2;
//
//        BufferedImage image = PDFToImage.extract(pdfFile, 300, pageNumber);
//
//        assertThat(image).isNotNull(); // Image should not be null
//        assertThat(image.getWidth()).isGreaterThan(0); // Ensure valid width
//        assertThat(image.getHeight()).isGreaterThan(0); // Ensure valid height
//    }
//
//    @Test
//    public void test_extract_single_page_invalid_index() {
//        File pdfFile = new File(MULTIPAGE_PDFFILE);
//
//        assertThatThrownBy(() -> PDFToImage.extract(pdfFile, 300, EXPECTED_PAGE_COUNT))
//                .isInstanceOf(IndexOutOfBoundsException.class);
//    }
//
//    @Test
//    public void test_extract_all_invalid_file() {
//        File invalidPdfFile = new File("path/to/nonexistent.pdf");
//
//        // Act & Assert
//        assertThatThrownBy(() -> PDFToImage.extractAll(invalidPdfFile, 300))
//                .isInstanceOf(IOException.class);
//    }
//
//    @Test
//    public void test_extract_single_page_invalid_file() {
//        File invalidPdfFile = new File("path/to/nonexistent.pdf");
//
//        // Act & Assert
//        assertThatThrownBy(() -> PDFToImage.extract(invalidPdfFile, 300, 0))
//                .isInstanceOf(IOException.class);
//    }
//
//}
