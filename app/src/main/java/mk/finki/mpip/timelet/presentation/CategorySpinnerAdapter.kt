package mk.finki.mpip.timelet.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import mk.finki.mpip.timelet.R
import mk.finki.mpip.timelet.network.models.Category


class CategorySpinnerAdapter(context: Context, items: List<Category>) :
ArrayAdapter<Category>(context, 0, items) {

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val view = convertView ?: LayoutInflater.from(context)
      .inflate(R.layout.dropdown_item, parent, false)

    val item = getItem(position)

    val imageView = view.findViewById<ImageView>(R.id.icon)
    if (item?.iconRes != null && item.iconRes != -1) {
      imageView.setImageResource(item.iconRes)
    } else {
      imageView.setImageDrawable(null)
    }

    val textView = view.findViewById<TextView>(R.id.text)
    textView.text = item?.name

    return view
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
    return getView(position, convertView, parent)
  }
}
