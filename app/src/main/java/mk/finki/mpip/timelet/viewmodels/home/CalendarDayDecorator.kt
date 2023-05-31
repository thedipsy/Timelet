package mk.finki.mpip.timelet.viewmodels.home

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CalendarDayDecorator(private val drawable: Drawable?, private val calDay: CalendarDay) : DayViewDecorator {

  override fun shouldDecorate(day: CalendarDay?) = day == calDay

  override fun decorate(view: DayViewFacade) {
    drawable?.let {
      view.setSelectionDrawable(drawable)
    }
    val colorSpan = ForegroundColorSpan(Color.WHITE)
    view.addSpan(colorSpan)
  }
}