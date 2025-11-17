package com.lunartag.app.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

/**
 * A utility class with static methods for rendering the watermark onto a photo.
 */
public class WatermarkUtils {

    // Private constructor to prevent instantiation
    private WatermarkUtils() {}

    /**
     * Renders the complete watermark block onto the provided Bitmap.
     * @param originalBitmap The original, mutable photo bitmap.
     * @param mapBitmap The small, pre-rendered bitmap of the map preview.
     * @param lines An array of strings, with each string representing one line of the watermark text.
     */
    public static void addWatermark(Bitmap originalBitmap, Bitmap mapBitmap, String[] lines) {
        if (originalBitmap == null || lines == null || lines.length == 0) {
            return;
        }

        Canvas canvas = new Canvas(originalBitmap);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // --- Configure Paint objects ---
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(width / 35.0f); // Dynamic text size based on image width
        textPaint.setShadowLayer(3f, 2f, 2f, Color.BLACK);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);
        backgroundPaint.setAlpha(128); // 50% transparent

        // --- Calculate Dimensions ---
        float textHeight = textPaint.descent() - textPaint.ascent();
        float blockHeight = (textHeight * lines.length) + (lines.length * 10) + 20; // Add padding
        if (mapBitmap != null && mapBitmap.getHeight() + 20 > blockHeight) {
            blockHeight = mapBitmap.getHeight() + 20; // Ensure block is tall enough for the map
        }

        // --- Draw Background ---
        Rect backgroundRect = new Rect(0, (int)(height - blockHeight), width, height);
        canvas.drawRect(backgroundRect, backgroundPaint);

        // --- Draw Map Bitmap (if provided) ---
        float mapLeft = 20;
        float mapTop = height - blockHeight + 10;
        if (mapBitmap != null) {
            canvas.drawBitmap(mapBitmap, mapLeft, mapTop, null);
        }

        // --- Draw Text Lines ---
        float textLeft = (mapBitmap != null) ? mapBitmap.getWidth() + 40 : 20;
        float currentY = height - blockHeight + textHeight + 5;

        for (String line : lines) {
            if (line != null) {
                canvas.drawText(line, textLeft, currentY, textPaint);
                currentY += textHeight;
            }
        }
    }
            }
