package ru.rain.ifmo.yearcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.gridlayout.widget.GridLayout
import java.time.Year

/**
 * @project Calendar
 * @author Ilia Ilmenskii created on 12.01.2020
 */
class YearView(context: Context,
               attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    var adapter: YearAdapter = YearAdapter(Year.of(1970))

    private val mainView = View.inflate(context, R.layout.year_view, null)

    private val yearTitle = mainView.findViewById<TextView>(R.id.textView).apply { text = adapter.title() }

    private val gridLayout = mainView.findViewById<GridLayout>(R.id.gridLayout_year)

    init {
        requestAdapters()
        addView(mainView)
    }

    fun requestAdapters() {
        for (i in 1..12) {
            adapter.bindMonthView(gridLayout[i - 1] as MonthView, i)
        }
    }

    private fun notifyDataChanged() {
        yearTitle.text = adapter.title()
        for (i in 1..12) {
            val month = adapter.year.atMonth(i)
            (gridLayout[i - 1] as MonthView).adapter.month = month
        }
    }

    open inner class YearAdapter(year: Year) {

        open fun title() = year.value.toString()

        var year = year
        set(value) {
            field = value
            notifyDataChanged()
        }

        open fun bindMonthView(monthView: MonthView, month: Int) {
            monthView.adapter = monthView.DefaultMonthAdapter(year.atMonth(month))
        }
    }
}