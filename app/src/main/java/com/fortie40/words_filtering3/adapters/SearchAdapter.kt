package com.fortie40.words_filtering3.adapters

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fortie40.words_filtering3.R
import com.fortie40.words_filtering3.interfaces.IClickListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

class SearchAdapter(names: List<String>, listener: IClickListener):
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>(), Filterable {

    var originalList: List<String> = names
    private var mFilteredList: List<String> = names
    var string: String? = null
    private var searchString: String? = null
    private val clickHandler: IClickListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.search_layout, parent, false)
        return SearchViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val name = mFilteredList[position]
        holder.iPosition = position
        if (string != null && string!!.isNotEmpty()) {
            val startPos = name.toLowerCase(Locale.getDefault())
                .indexOf(searchString!!.toLowerCase(Locale.getDefault()))
            val endPos = startPos + searchString!!.length

            if (startPos != -1) {
                val spannable = SpannableString(name)
                spannable.setSpan(
                    BackgroundColorSpan(Color.YELLOW),
                    startPos,
                    endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                holder.bind(spannable)
            } else {
                holder.bind(name)
            }
        } else {
            holder.bind(name)
        }
    }

    override fun getFilter(): Filter {
        return this.filter
    }

    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults = runBlocking {
            val charString = constraint.toString()

            mFilteredList = if (charString.isEmpty()) {
                originalList
            } else {
                delay(2000)
                val filteredList = originalList
                    .filter { it.toLowerCase(Locale.getDefault()).contains(charString) }
                    .toMutableList()
                Log.i("adapter", "********")
                Log.i("adapter", "filtered")
                searchString = charString
                filteredList
            }
            val filterResults = FilterResults()
            filterResults.values = mFilteredList
            return@runBlocking filterResults
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            mFilteredList = results!!.values as List<String>
            notifyDataSetChanged()
        }
    }

    inner class SearchViewHolder(nItemView: View): RecyclerView.ViewHolder(nItemView) {
        private val history = nItemView.findViewById<ImageView>(R.id.history)
        private val results = nItemView.findViewById<TextView>(R.id.results)
        private val restore = nItemView.findViewById<ImageView>(R.id.restore)

        var iPosition = 0

        fun bind(nameA: String) {
            results.text = nameA
        }

        fun bind(nameA: Spannable) {
            history.visibility = View.GONE
            restore.visibility = View.GONE
            results.text = nameA
        }

        init {
            results.setOnClickListener {
                clickHandler.onResultsClick(iPosition)
            }

            restore.setOnClickListener {
                clickHandler.onRestoreClick(iPosition)
            }
        }
    }
}