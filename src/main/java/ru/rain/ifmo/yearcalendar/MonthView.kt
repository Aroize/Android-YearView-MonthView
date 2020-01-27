package ru.rain.ifmo.yearcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.gridlayout.widget.GridLayout
import java.time.Month
import java.time.YearMonth

/**
 * @project Calendar
 * @author Ilia Ilmenskii created on 29.12.2019
 */
class MonthView(context: Context,
                attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val mainView = View.inflate(context, R.layout.month_view, null)

    private val gridLayout = mainView.findViewById<GridLayout>(R.id.gridlayout_month)

    var adapter: MonthAdapter = DefaultMonthAdapter(YearMonth.of(1970, Month.JANUARY))
        set(value) {
            field = value
            notifyDataChanged()
        }

    init {
        addView(mainView)
        notifyDataChanged()
    }

    private fun notifyDataChanged() {
        mainView.findViewById<TextView>(R.id.month_title).text = adapter.getTitle()
        gridLayout.removeAllViews()
        val rows = adapter.getRowCount()
        val cols = adapter.getColCount()
        gridLayout.rowCount = rows
        gridLayout.columnCount = cols
        for (i in 0..rows) {
            for (j in 0 until cols) {
                val view = adapter.bindDay(null, i, j)
                view ?: continue
                gridLayout.addView(view)
                view.apply {
                    val gridParams = layoutParams as GridLayout.LayoutParams
                    gridParams.setGravity(Gravity.CENTER)
                    gridParams.rowSpec = GridLayout.spec(i, 1f)
                    gridParams.columnSpec = GridLayout.spec(j, 1f)
                }
            }
        }
    }

    interface MonthAdapter {
        var month: YearMonth
        fun getRowCount(): Int
        fun getColCount(): Int
        fun getTitle(): String
        fun bindDay(parent: ViewGroup?, row: Int, col: Int): View?
    }

    open inner class DefaultMonthAdapter(month: YearMonth) : MonthAdapter {

        override var month: YearMonth = month
        set(value) {
            field = value
            this@MonthView.notifyDataChanged()
        }

        private var offset: Int = 0

        override fun getTitle(): String = month.month.name

        override fun getColCount(): Int = 7

        override fun getRowCount(): Int {
            offset = month.atDay(1).dayOfWeek.value - 1
            val atDayStart = getColCount() - month.atDay(1).dayOfWeek.value + 1
            val forOtherRows = month.lengthOfMonth() - atDayStart
            var addition = 1
            if (forOtherRows % getColCount() != 0) {
                addition = 2
            }
            return forOtherRows / getColCount() + addition
        }

        override fun bindDay(parent: ViewGroup?, row: Int, col: Int): View? {
            val date = calculateDate(row, col)
            if (date < 0)
                return null
            val tv = TextView(context)
            tv.gravity = Gravity.CENTER
            tv.text = date.toString()
            tv.textSize = 10f
            return tv
        }

        protected fun calculateDate(row: Int, col: Int): Int {
            val date  = (row*getColCount() + col - offset + 1)
            if (date > 0 && date <= month.lengthOfMonth()) {
                return date
            }
            return -1
        }
    }
}