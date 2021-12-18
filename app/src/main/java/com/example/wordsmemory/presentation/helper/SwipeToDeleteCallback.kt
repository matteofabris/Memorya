package com.example.wordsmemory.presentation.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsmemory.R
import kotlin.math.absoluteValue

abstract class SwipeToDeleteCallback(private val _context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val _icon = ContextCompat.getDrawable(_context, R.drawable.ic_delete_white_18dp)
    private val _intrinsicWidth = _icon?.intrinsicWidth ?: 0
    private val _intrinsicHeight = _icon?.intrinsicHeight ?: 0
    private var _backgroundPaint =
        Paint().apply { color = _context.getColor(R.color.delete_list_item_color) }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false // We don't want support moving items up/down
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        if (dX == 0f && !isCurrentlyActive) return

        val endReached = c.width == dX.toInt().absoluteValue
        if (endReached) setBackgroundPaint(R.color.wm_background)

        val itemView = viewHolder.itemView
        drawBackground(c, itemView, dX)
        if (dX < -_intrinsicWidth && !endReached) drawDeleteIcon(c, itemView)

        if (endReached) setBackgroundPaint(R.color.delete_list_item_color)
    }

    private fun setBackgroundPaint(colorId: Int) {
        _backgroundPaint = Paint().apply { color = _context.getColor(colorId) }
    }

    private fun drawDeleteIcon(c: Canvas, itemView: View) {
        val itemHeight = itemView.bottom - itemView.top

        // Calculate position of delete icon
        val iconTop = itemView.top + (itemHeight - _intrinsicHeight) / 2
        val iconMargin = (itemHeight - _intrinsicHeight) / 2
        val iconLeft = itemView.right - iconMargin - _intrinsicWidth
        val iconRight = itemView.right - iconMargin
        val iconBottom = iconTop + _intrinsicHeight

        // Draw the delete icon
        _icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        _icon?.draw(c)
    }

    private fun drawBackground(c: Canvas, itemView: View, dX: Float) {
        // Draw the red delete background
        val backgroundRect = RectF(
            (itemView.right + dX.toInt()).toFloat(),
            (itemView.top).toFloat(),
            (itemView.right).toFloat(),
            (itemView.bottom).toFloat()
        )

        c.drawRoundRect(backgroundRect, 10f, 10f, _backgroundPaint)
    }
}