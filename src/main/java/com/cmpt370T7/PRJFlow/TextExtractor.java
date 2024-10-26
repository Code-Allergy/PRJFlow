package com.cmpt370T7.PRJFlow;

import com.cmpt370T7.PRJFlow.util.ImageConverter;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.ETEXT_DESC;
import org.bytedeco.tesseract.TessBaseAPI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cmpt370T7.PRJFlow.TextExtractor.TextElementType.ELEMENT_WORD;
import static org.bytedeco.leptonica.global.leptonica.pixDestroy;
import static org.bytedeco.tesseract.global.tesseract.*;


/**
 * The {@code TextExtractor} class is responsible for extracting text and
 * its hierarchical structure from images using OCR.
 * It utilizes the Tesseract OCR engine to recognize text within images and
 * organize the extracted text into a structured format comprising
 * pages, paragraphs, lines, and words.
 *
 * <p>This class provides functionality to:</p>
 * <ul>
 *     <li>Initialize the Tesseract OCR engine and process images for text extraction.</li>
 *     <li>Parse the generated HOCR output to build a hierarchy of text elements.</li>
 *     <li>Render images of specific text elements based on their bounding boxes.</li>
 *     <li>Retrieve text elements containing specific substrings.</li>
 * </ul>
 *
 * <p>The text elements are represented by the nested {@link TextElement} class,
 * which includes information about the text content, its bounding box, type,
 * and confidence score.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     BufferedImage image = ImageIO.read(new File("path/to/image.png"));
 *     TextElement extractedText = TextExtractor.extractTextHierarchy(image);
 * </pre>
 *
 * @see TextElement
 * @see BoundingBox
 */
public class TextExtractor {
    private static final Logger logger = LoggerFactory.getLogger(TextExtractor.class);
    private final TessBaseAPI api;

    /// Regex pattern to extract bounding box coordinates, we can probably do this more efficiently without regex.
    private static final Pattern BBOX_PATTERN =
            Pattern.compile("bbox\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");

    /// Enum representing the different types of text elements.
    public enum TextElementType {
        ELEMENT_PAGE,
        ELEMENT_PARAGRAPH,
        ELEMENT_LINE,
        ELEMENT_WORD,
        ELEMENT_UNKNOWN
    }

    /// Represents a nested tree-like structure of text elements on a page.
    public static class TextElement {
        public final String text;
        public final BoundingBox bounds;
        public final List<TextElement> children;
        public final TextElementType type;
        public final float confidence;

        public TextElement(String text, BoundingBox bounds, TextElementType type, float confidence) {
            this.text = text;
            this.bounds = bounds;
            this.children = new ArrayList<>();
            this.type = type;
            this.confidence = confidence;
        }

        @Override
        public String toString() {
            return String.format("%s[%s] at %s (conf: %.2f)", type, text, bounds, confidence);
        }

        /// Returns a list of paragraphs if the element is a page; otherwise, returns null.
        public List<TextElement> getParagraphs() {
            return this.type == TextElementType.ELEMENT_PAGE ? this.children : null;
        }

        /// Filters and returns paragraphs containing the specified text if the element is a page.
        public List<TextElement> getParagraphsWith(String text) {
            return this.type == TextElementType.ELEMENT_PAGE
                    ? this.children.stream().filter(child -> child.text.toLowerCase().contains(text.toLowerCase())).toList()
                    : null;
        }

        /**
         * Filters and returns all TextElements (words, lines, paragraphs) containing the specified text.
         *
         * @param text The text to search for (case insensitive).
         * @return A list of matching TextElements or an empty list if none are found.
         */
        public List<TextElement> getAllMatchingWordElements(String text) {
            List<TextElement> matchingElements = new ArrayList<>();

            // Recursively check children if the current element is a page or paragraph
            if (this.type != TextElementType.ELEMENT_WORD) {
                for (TextElement child : this.children) {
                    matchingElements.addAll(child.getAllMatchingWordElements(text));
                }
            }

            // Check if the current element contains the search text
            if (this.type == TextElementType.ELEMENT_WORD && this.text.toLowerCase().contains(text.toLowerCase())) {
                matchingElements.add(this);
            }

            return matchingElements;
        }
    }

    /// Represents a bounding box with coordinates (x1, y1) as the top-left and (x2, y2) as the bottom-right.
    public record BoundingBox(int x1, int y1, int x2, int y2) {
        @Override
        public String toString() {
            return String.format("(%d,%d,%d,%d)", x1, y1, x2, y2);
        }
    }

    // Constructor to initialize Tesseract API and AppDataManager
    public TextExtractor() {
        AppDataManager appData = AppDataManager.getInstance();
        this.api = new TessBaseAPI();

        // Initialize Tesseract with specified data directory and language
        if (api.Init(appData.getTesseractDataDirectory().getPath(), "eng") != 0) {
            throw new RuntimeException("Could not initialize Tesseract.");
        }
    }

    /// Call to cleanup memory used by Tesseract API binding
    public void cleanup() {
        this.api.End();
    }


    /**
     * Extracts a hierarchical structure of text elements from an image using OCR.
     *
     * @param bufferedImage The input image to extract text from.
     * @return A {@link TextElement} representing the hierarchical structure of the extracted text.
     * @throws RuntimeException If OCR initialization fails or if the image cannot be read.
     */
    public TextElement extractTextHierarchy(BufferedImage bufferedImage) {
        AppDataManager appdata = AppDataManager.getInstance();

        // Monitor around API calls, calling unsafe C++ code.
        synchronized (api) {
            if (api.Init(appdata.getTesseractDataDirectory().getPath(), "eng") != 0) {
                throw new RuntimeException("Could not initialize tesseract.");
            }

            api.SetPageSegMode(PSM_AUTO_OSD);
            PIX image = ImageConverter.toPIX(bufferedImage);
            if (image == null) {
                throw new RuntimeException("Failed to read image: " );
            }
            api.SetImage(image);
            api.Recognize(new ETEXT_DESC());

            // Generate HOCR report
            BytePointer hocrText = api.GetHOCRText(0);
            if (hocrText == null) {
                throw new RuntimeException("Failed to read hocr text: " );
            }
            String hocrString = hocrText.getString();

            // Parse the generated HTML, send the document to be processed further
            Document doc = Jsoup.parse(hocrString);
            Element pageElement = doc.selectFirst(".ocr_page");
            assert pageElement != null;
            TextElement pageHierarchy = processElement(pageElement);

            hocrText.deallocate();
            api.End();
            pixDestroy(image);

            return pageHierarchy;
        }
    }

    /**
     * Processes a Jsoup Element and creates a corresponding TextElement.
     *
     * @param element The Jsoup Element to process.
     * @return A TextElement representing the processed content.
     */
    public static TextElement processElement(Element element) {
        // Extract type and class information
        String elementClass = element.className();
        TextElementType type = getElementType(elementClass);

        // Get bounding box and confidence
        String title = element.attr("title");
        BoundingBox bbox = extractBoundingBox(title);
        float confidence = extractConfidence(title);

        // Get text content based on element type
        String text = extractText(element, type);

        // Create text element
        TextElement textElement = new TextElement(text, bbox, type, confidence);

        // Process children based on element type
        switch (type) {
            case ELEMENT_PAGE:
                // Find paragraphs
                processChildren(element, "ocr_par", textElement);
                break;

            case ELEMENT_PARAGRAPH:
                // Find lines
                processChildren(element, "ocr_line", textElement);
                break;

            case ELEMENT_LINE:
                // Find words
                processChildren(element, "ocrx_word", textElement);
                break;

            case ELEMENT_WORD: // Words don't have children to process
                break;
        }

        return textElement;
    }

    /// Confidence for paragraphs and lines is always zero
    private static float extractConfidence(String title) {
        // Extract confidence from title attribute if present
        Pattern confPattern = Pattern.compile("x_wconf\\s+(\\d+)");
        Matcher matcher = confPattern.matcher(title);
        if (matcher.find()) {
            return Float.parseFloat(matcher.group(1));
        }
        return 0.0f; // fallback if not found or paragraph/line
    }

    /**
     * Extracts a bounding box from the title attribute string.
     *
     * @param title The title attribute containing bounding box information.
     * @return A BoundingBox object representing the coordinates, or (0,0,0,0) if not found.
     */
    private static BoundingBox extractBoundingBox(String title) {
        Matcher matcher = BBOX_PATTERN.matcher(title);
        if (matcher.find()) {
            return new BoundingBox(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4))
            );
        }
        return new BoundingBox(0, 0, 0, 0); // fallback, we should discard if bound at 0,0,0,0.
    }

    /**
     * Determines the TextElementType based on the class name of the element.
     *
     * @param className The class name to evaluate.
     * @return The corresponding TextElementType.
     */
    private static TextElementType getElementType(String className) {
        if (className.contains("ocr_page")) return TextElementType.ELEMENT_PAGE;
        if (className.contains("ocr_par")) return TextElementType.ELEMENT_PARAGRAPH;
        if (className.contains("ocr_line")) return TextElementType.ELEMENT_LINE;
        if (className.contains("ocrx_word")) return ELEMENT_WORD;
        return TextElementType.ELEMENT_UNKNOWN;
    }

    /**
     * Processes child elements of a given parent element and adds them to the parent TextElement.
     *
     * @param parent        The parent HTML Element.
     * @param className     The class name to filter child elements.
     * @param parentElement The TextElement to which child elements will be added.
     */
    private static void processChildren(Element parent, String className, TextElement parentElement) {
        Elements children = parent.getElementsByClass(className);
        for (Element child : children) {
            TextElement childElement = processElement(child);
            parentElement.children.add(childElement);
        }
    }

    /**
     * Extracts the text content from a Jsoup Element based on its type.
     *
     * @param element The Jsoup Element from which to extract text.
     * @param type    The TextElementType to determine how to extract the text.
     * @return The extracted text as a String.
     */
    private static String extractText(Element element, TextElementType type) {
        switch (type) {
            case ELEMENT_LINE:
            case ELEMENT_PARAGRAPH:
                // For lines and paragraphs, concatenate word texts
                StringBuilder text = new StringBuilder();
                Elements words = element.getElementsByClass("ocrx_word");
                for (Element word : words) {
                    if (!text.isEmpty()) text.append(" ");
                    text.append(word.text().trim());
                }
                return text.toString();

            case ELEMENT_PAGE: // For pages, return empty string, they're containers
                return "";

            case ELEMENT_WORD:
            default:
                return element.text().trim();
        }
    }

    /**
     * Renders images of paragraphs from the original image based on bounding box coordinates.
     * Temporary, we should move this functionality to another class
     *
     * @param paragraphs The list of paragraphs to render.
     * @param image      The original BufferedImage.
     * @param padding    The padding to apply around the bounding box.
     * @return A list of BufferedImages representing each rendered paragraph.
     */
    public static List<BufferedImage> renderParagraphImages(List<TextElement> paragraphs, BufferedImage image, int padding) {
        List<BufferedImage> paragraphImages = new ArrayList<>();
        for (TextElement paragraph : paragraphs) {
            BufferedImage paragraphImage = renderBoundingBox(image, paragraph.bounds, padding);
            paragraphImages.add(paragraphImage);
        }
        return paragraphImages;
    }

    /**
     * Renders a section of the original image based on the given bounding box and padding.
     *
     * @param image   The original BufferedImage.
     * @param bounds  The bounding box defining the section to render.
     * @param padding The padding to apply around the bounding box.
     * @return A BufferedImage representing the rendered section.
     */
    private static BufferedImage renderBoundingBox(BufferedImage image, BoundingBox bounds, int padding) {
        int x1 = Math.max(0, bounds.x1 - padding);
        int y1 = Math.max(0, bounds.y1 - padding);
        int x2 = Math.min(image.getWidth(), bounds.x2 + padding);
        int y2 = Math.min(image.getHeight(), bounds.y2 + padding);

        return image.getSubimage(x1, y1, x2 - x1, y2 - y1);
    }

    /// Utility method to walk through the hierarchy and print it all, for debugging
    public static void printHierarchy(TextElement element, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + element);
        for (TextElement child : element.children) {
            printHierarchy(child, depth + 1);
        }
    }
}
