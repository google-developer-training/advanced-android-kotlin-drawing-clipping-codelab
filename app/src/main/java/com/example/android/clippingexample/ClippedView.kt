/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.clippingexample

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.*
import android.os.Build


class ClippedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        // Smooth out edges of what is drawn without affecting shape.
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
    }
    private val path = Path()

    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)
    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    private val circleRadius = resources.getDimension(R.dimen.circleRadius)

    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val textSize = resources.getDimension(R.dimen.textSize)

    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight

    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    private val textRow = rowFour + (1.5f * clipRectBottom)
    private val rejectRow = rowFour + rectInset + 2*clipRectBottom

    private var rectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )

    /**
     * Factors out the actual drawing of the shapes.
     *
     * @param canvas The Canvas on which we are drawing.
     */
    private fun drawClippedRectangle(canvas: Canvas) {
        // Set the boundaries of the clipping rectangle for the whole picture.
        canvas.clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight,clipRectBottom
        )
        // Fill the canvas with white.
        // With the clipped rectangle, this only draws
        // inside the clipping rectangle.
        // The rest of the surface remains gray.
        canvas.drawColor(Color.WHITE)
        // Change the color to red and
        // draw a line inside the clipping rectangle.
        paint.color = Color.RED
        canvas.drawLine(
            clipRectLeft,clipRectTop,
            clipRectRight,clipRectBottom,paint
        )
        // Set the color to green and
        // draw a circle inside the clipping rectangle.
        paint.color = Color.GREEN
        canvas.drawCircle(
            circleRadius,clipRectBottom - circleRadius,
            circleRadius,paint
        )
        // Set the color to blue and draw text aligned with the right edge
        // of the clipping rectangle.
        paint.color = Color.BLUE
        // Align the RIGHT side of the text with the origin.
        paint.textSize = textSize
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            context.getString(R.string.clipping),
            clipRectRight,textOffset,paint
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackAndUnclippedRectangle(canvas)
        drawDifferenceClippingExample(canvas)
        drawCircularClippingExample(canvas)
        drawIntersectionClippingExample(canvas)
        drawCombinedClippingExample(canvas)
        drawRoundedRectangleClippingExample(canvas)
        drawOutsideClippingExample(canvas)
        drawTranslatedTextExample(canvas)
        drawSkewedTextExample(canvas)
        //drawQuickRejectExample(canvas)
    }

    /**
     * Fill the canvas with the background color
     * and draw the unclipped shapes.
     *
     * @param canvas The Canvas on which we are drawing.
     */
    private fun drawBackAndUnclippedRectangle(canvas: Canvas){
        canvas.drawColor(Color.GRAY)
        canvas.save()
        canvas.translate(columnOne,rowOne)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /**
     * Draw a rectangle that uses the difference between two
     * clipping rectangles to create a picture frame effect.
     *
     * @param canvas The Canvas on which we are drawing.
     */
    private fun drawDifferenceClippingExample(canvas: Canvas) {
        canvas.save()
        // Move the origin to the right for the next rectangle.
        canvas.translate(columnTwo,rowOne)
        // Use the subtraction of two clipping rectangles to create a frame.
        canvas.clipRect(
            2 * rectInset,2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .DIFFERENCE) was deprecated in API level 26. The recommended
        // alternative method is clipOutRect(float, float, float, float),
        // which is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            canvas.clipRect(
                4 * rectInset,4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset,
                 Region.Op.DIFFERENCE
            )
        else {
            canvas.clipOutRect(
                4 * rectInset,4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset
            )
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /**
     * Draw a rectangle that uses a circular clipping region
     * created from a circular path.
     *
     * @param canvas The Canvas on which we are drawing.
     */
    private fun drawCircularClippingExample(canvas: Canvas) {

        canvas.save()
        canvas.translate(columnOne, rowTwo)
        // Clears any lines and curves from the path but unlike reset(),
        // keeps the internal data structure for faster reuse.
        path.rewind()
        path.addCircle(
            circleRadius,clipRectBottom - circleRadius,
            circleRadius,Path.Direction.CCW
        )
        // The method clipPath(path, Region.Op.DIFFERENCE) was deprecated in
        // API level 26. The recommended alternative method is
        // clipOutPath(Path), which is currently available in
        // API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipPath(path, Region.Op.DIFFERENCE)
        } else {
            canvas.clipOutPath(path)
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /**
     * Use the intersection of two rectangles as the clipping region.
     *
     * @param canvas The Canvas on which we are drawing.
     */
    private fun drawIntersectionClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo,rowTwo)
        canvas.clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight - smallRectOffset,
            clipRectBottom - smallRectOffset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .INTERSECT) was deprecated in API level 26. The recommended
        // alternative method is clipRect(float, float, float, float), which
        // is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,clipRectBottom,
                Region.Op.INTERSECT
            )
        } else {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,clipRectBottom
            )
        }

        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /**
     * You can combine shapes and draw any path to define a clipping region.
     *
     * @param canvas The Canvas on which we are drawing.
     */
    private fun drawCombinedClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowThree)
        path.rewind()
        path.addCircle(
            clipRectLeft + rectInset + circleRadius,
            clipRectTop + circleRadius + rectInset,
            circleRadius,Path.Direction.CCW
        )
        path.addRect(
            clipRectRight / 2 - circleRadius,
            clipRectTop + circleRadius + rectInset,
            clipRectRight / 2 + circleRadius,
            clipRectBottom - rectInset,Path.Direction.CCW
        )
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /**
     * Use a rounded rectangle. Use clipRectRight/4 to draw a circle.
     *
     * @param canvas The Canvas on which we are drawing.
     */
    private fun drawRoundedRectangleClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo,rowThree)
        path.rewind()
        path.addRoundRect(
            rectF,clipRectRight / 4,
            clipRectRight / 4, Path.Direction.CCW
        )
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /**
     * Clip the outside around the rectangle.
     *
     * @param canvas The Canvas on which we are drawing.
     */
    private fun drawOutsideClippingExample(canvas: Canvas) {
        canvas.save()
        // Move the origin to the right for the next rectangle.
        canvas.translate(columnOne,rowFour)
        canvas.clipRect(2 * rectInset,2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset)
        drawClippedRectangle(canvas)
        canvas.restore()
    }

    /**
     * Draw translated text.
     *
     * @param canvas The Canvas on which we are drawing.
     */
    private fun drawTranslatedTextExample(canvas: Canvas) {
        canvas.save()
        paint.color = Color.GREEN
        // Align the RIGHT side of the text with the origin.
        paint.textAlign = Paint.Align.LEFT
        // Apply transformation to canvas.
        canvas.translate(columnTwo,textRow)
        // Draw text.
        canvas.drawText(context.getString(R.string.translated),
            clipRectLeft,clipRectTop,paint)
        canvas.restore()
    }


    /**
     * Draw Skewed text.
     *
     * @param canvas The Canvas on which we are drawing.
     */
    private fun drawSkewedTextExample(canvas: Canvas) {
        canvas.save()
        paint.color = Color.YELLOW
        paint.textAlign = Paint.Align.RIGHT
        // Position text.
        canvas.translate(columnTwo, textRow)
        // Apply skew transformation.
        canvas.skew(0.2f, 0.3f)
        canvas.drawText(context.getString(R.string.skewed),
            clipRectLeft, clipRectTop, paint)
        canvas.restore()
    }

    /**
     * Demonstrates how to use quickReject() to test whether two rectangles
     * overlap. quickReject() returns TRUE if there is no overlap.
     *
     * Using quickReject() with inClipRectangle returns false, and draws a black
     * rectangle with the clipped rectangle in yellow.
     * Using quickReject() with notInClipRectangle returns true, and draws a white
     * rectangle with nothing else drawn.
     *
     * @param canvas The Canvas on which we are drawing.
     */
//    private fun drawQuickRejectExample(canvas: Canvas) {
//        val inClipRectangle = RectF(clipRectRight / 2,
//            clipRectBottom / 2,
//            clipRectRight * 2,
//            clipRectBottom * 2)
//
//        val notInClipRectangle = RectF(RectF(clipRectRight+1,
//            clipRectBottom+1,
//            clipRectRight * 2,
//            clipRectBottom * 2))
//
//        canvas.save()
//        canvas.translate(columnOne, rejectRow)
//        canvas.clipRect(
//            clipRectLeft,clipRectTop,
//            clipRectRight,clipRectBottom
//        )
//        if (canvas.quickReject(
//                inClipRectangle, Canvas.EdgeType.AA)) {
//            canvas.drawColor(Color.WHITE)
//        }
//        else {
//            canvas.drawColor(Color.BLACK)
//            canvas.drawRect(inClipRectangle, paint
//            )
//        }
//            canvas.restore()
//    }
}