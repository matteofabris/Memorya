package com.example.wordsmemory.vocabulary.allwords

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.wordsmemory.R

abstract class SwipeToDeleteCallback(context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val _icon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_18dp)
    private val _intrinsicWidth = _icon?.intrinsicWidth
    private val _intrinsicHeight = _icon?.intrinsicHeight
    private val _clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    private val _background = ColorDrawable()
    private val _backgroundColor = Color.parseColor("#f44336")

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

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw the red delete background
        _background.color = _backgroundColor
        _background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        _background.draw(c)

        // Calculate position of delete icon
        val iconTop = itemView.top + (itemHeight - _intrinsicHeight!!) / 2
        val iconMargin = (itemHeight - _intrinsicHeight) / 2
        val iconLeft = itemView.right - iconMargin - _intrinsicWidth!!
        val iconRight = itemView.right - iconMargin
        val iconBottom = iconTop + _intrinsicHeight

        // Draw the delete icon
        _icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        _icon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, _clearPaint)
    }
}