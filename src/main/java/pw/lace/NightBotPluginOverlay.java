package pw.lace;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.TextComponent;

public class NightBotPluginOverlay extends Overlay {
    private final Client client;
    private final NightBotPluginConfig config;
    private final EventBus eventBus;
    private Point textComponentPosition;
    private TextComponent[] textComponents;
    private String[] currentTextLines = new String[0];
    private boolean isRoundedBackground;

    @Inject
    private NightBotPluginOverlay(Client client, NightBotPluginConfig config, EventBus eventBus) {
        this.client = client;
        this.config = config;
        this.eventBus = eventBus;
        this.isRoundedBackground = config.isRoundedBackground();
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGH);

        this.textComponentPosition = new Point(config.getXCoordinate(), config.getYCoordinate());

        eventBus.register(this);
    }

    private String[] getTextLinesFromFile(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return content.split("\\n");
        } catch (IOException e) {
            e.printStackTrace();
            return new String[] { "Error reading file" };
        }
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        String filePath = config.filePath();
        String[] lines = getTextLinesFromFile(filePath);

        if (textComponents == null || textComponents.length != lines.length) {
            textComponents = new TextComponent[lines.length];
            for (int i = 0; i < lines.length; i++) {
                textComponents[i] = new TextComponent();
                textComponents[i].setText(lines[i]);
                textComponents[i].setColor(Color.WHITE);
                textComponents[i].setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
            }
        } else {
            for (int i = 0; i < textComponents.length; i++) {
                textComponents[i].setText(lines[i]);
            }
        }

        int padding = 10;
        int lineGap = 5;
        int arcWidth = 10;
        int arcHeight = 10;

        FontMetrics fontMetrics = graphics.getFontMetrics();
        int textHeight = fontMetrics.getHeight();
        float maxWidth = 0;

        for (String line : lines) {
            int lineWidth = fontMetrics.stringWidth(line);
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth * 1.55f;
            }
        }

        int totalHeight = lines.length * textHeight + (lines.length - 1) * lineGap + 2 * padding;

        Shape backgroundShape;
        if (isRoundedBackground) {
            backgroundShape = new RoundRectangle2D.Float(
                    textComponentPosition.getX() - padding,
                    textComponentPosition.getY() - padding - textHeight,
                    maxWidth + 2 * padding,
                    totalHeight,
                    arcWidth,
                    arcHeight
            );
        } else {
            backgroundShape = new Rectangle(
                    textComponentPosition.getX() - padding,
                    textComponentPosition.getY() - padding - textHeight,
                    (int) (maxWidth + 2 * padding),
                    totalHeight
            );
        }

        // Draw the background
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
        graphics.setColor(new Color(102, 85, 64));
        graphics.fill(backgroundShape);

        // Draw the border
        graphics.setColor(new Color(51, 43, 32));
        graphics.setStroke(new BasicStroke(1));
        graphics.draw(backgroundShape);

        // Draw the text components
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        int yOffset = 0;
        for (int i = 0; i < textComponents.length; i++) {
            java.awt.Point awtPoint = toAWTPoint(textComponentPosition.getX(), textComponentPosition.getY() + yOffset);
            textComponents[i].setPosition(awtPoint);
            textComponents[i].render(graphics);
            yOffset += textHeight + lineGap;
        }

        return null;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        String filePath = config.filePath();
        String[] newTextLines = getTextLinesFromFile(filePath);

        if (!areTextLinesEqual(currentTextLines, newTextLines)) {
            currentTextLines = newTextLines;

            if (textComponents != null && textComponents.length == newTextLines.length) {
                for (int i = 0; i < newTextLines.length; i++) {
                    textComponents[i].setText(newTextLines[i]);
                }
            } else {
                textComponents = new TextComponent[newTextLines.length];
                for (int i = 0; i < newTextLines.length; i++) {
                    textComponents[i] = new TextComponent();
                    textComponents[i].setText(newTextLines[i]);
                    textComponents[i].setColor(Color.WHITE);
                    textComponents[i].setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
                }
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("nightbot")) {
            this.textComponentPosition = new Point(config.getXCoordinate(), config.getYCoordinate());
            this.isRoundedBackground = config.isRoundedBackground();
        }
    }


    private boolean areTextLinesEqual(String[] lines1, String[] lines2) {
        if (lines1.length != lines2.length) {
            return false;
        }
        for (int i = 0; i < lines1.length; i++) {
            if (!lines1[i].equals(lines2[i])) {
                return false;
            }
        }
        return true;
    }

    private java.awt.Point toAWTPoint(int x, int y) {
        return new java.awt.Point(x, y);
    }
}
