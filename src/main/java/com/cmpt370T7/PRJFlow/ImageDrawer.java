package com.cmpt370T7.PRJFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageDrawer {
    private static final Logger logger = LoggerFactory.getLogger(ImageDrawer.class);
    private static final int DEFAULT_PADDING = 4;
    private static final Color DEFAULT_HIGHLIGHT_COLOR = new Color(255, 255, 0, 100);
    // Example usage with visualization

    public static BufferedImage highlightParagraphText(BufferedImage pageImage, TextExtractor.TextElement paragraph, String term) {
        if (paragraph.type != TextExtractor.TextElementType.ELEMENT_PARAGRAPH) {
            logger.warn("Non paragraph element was passed into highlightParagraphText");
            return null;
        }
        TextExtractor.BoundingBox pgBounds = paragraph.bounds;
        BufferedImage paragraphImage = pageImage.getSubimage(pgBounds.x1(), pgBounds.y1(), pgBounds.x2() - pgBounds.x1(), pgBounds.y2() - pgBounds.y1());
        Graphics2D g2d = paragraphImage.createGraphics();

        // Set up the drawing properties
        Color color = DEFAULT_HIGHLIGHT_COLOR; // Semi-transparent yellow
        g2d.setColor(color);

        List<TextExtractor.TextElement> filteredElements = paragraph.getAllMatchingWordElements(term);
        // Draw each highlighted box
        for (TextExtractor.TextElement element : filteredElements) {
            TextExtractor.BoundingBox box = getOffsetBounds(pgBounds, element.bounds);
            int width = box.x2() - box.x1() + DEFAULT_PADDING * 2;
            int height = box.y2() - box.y1() + DEFAULT_PADDING * 2;
            g2d.fillRect(box.x1() - DEFAULT_PADDING, box.y1() - DEFAULT_PADDING, width, height);
            logger.debug("highlighting text at ({},{},{},{})", box.x1(), box.y1(), width, height);
        }

        return paragraphImage;
    }

    /// Offset bounding boxes by start of paragraph box
    public static TextExtractor.BoundingBox getOffsetBounds(TextExtractor.BoundingBox paragraph, TextExtractor.BoundingBox original) {
        int newX1 = original.x1() - paragraph.x1();
        int newY1 = original.y1() - paragraph.y1();
        int newX2 = original.x2() - paragraph.x1();
        int newY2 = original.y2() - paragraph.y1();

        return new TextExtractor.BoundingBox(newX1, newY1, newX2, newY2);
    }
}
