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
import com.fortie40.words_filtering3.HEADER
import com.fortie40.words_filtering3.HEADER_TITLE
import com.fortie40.words_filtering3.NAMES
import com.fortie40.words_filtering3.R
import com.fortie40.words_filtering3.interfaces.IClickListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class SearchAdapter(names: List<String>, listener: IClickListener):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    companion object {
        private var searchString: String? = null

        private fun viewInflater(parent: ViewGroup, layout: Int): View {
            val layoutInflater = LayoutInflater.from(parent.context)
            return layoutInflater.inflate(layout, parent, false)
        }
    }

    var originalList: List<String> = names
    var string: String? = null
    var resultsFound: Boolean = false
    private var mFilteredList: List<String> = names
    private val clickHandler: IClickListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> HeaderViewHolder.createHeaderViewHolder(parent)
            NAMES -> SearchViewHolder.createSearchViewHolder(parent)
            else -> SearchViewHolder.createSearchViewHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val name = mFilteredList[position]
        val itemViewType = getItemViewType(position)
        if (itemViewType == NAMES) {
            val h = holder as SearchViewHolder
            h.bind(name, string)
            h.iPosition = position
        } else {
            (holder as HeaderViewHolder).bind(resultsFound, (mFilteredList.size - 1))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mFilteredList[position] == HEADER_TITLE) {
            HEADER
        } else {
            NAMES
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
            resultsFound = true
            val allResults = results!!.values as ArrayList<String>
            if (allResults.size >= 1) {
                allResults.add(0, HEADER_TITLE)
            }
            mFilteredList = allResults
            notifyDataSetChanged()
        }
    }

    class HeaderViewHolder private constructor(nItemView: View): RecyclerView.ViewHolder(nItemView) {
        companion object {
            fun createHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
                val itemView = viewInflater(parent, R.layout.header_layout)
                return HeaderViewHolder(itemView)
            }
        }

        private val header = nItemView.findViewById<TextView>(R.id.header)
        private val divider = nItemView.findViewById<View>(R.id.divider)
        private val results = nItemView.findViewById<TextView>(R.id.results_found)
        private val context = itemView.context

        fun bind(resultsFound: Boolean, itemCount: Int = 0) {
            if (!resultsFound) {
                header.text = context.getString(R.string.recent_searches)
                results.text = ""
            } else {
                val resultsString = context.getString(R.string.results)
                header.text = resultsString.toUpperCase(Locale.getDefault())
                results.text = context.getString(R.string.results_found, itemCount)
            }
        }
    }

    class SearchViewHolder private constructor(nItemView: View): RecyclerView.ViewHolder(nItemView) {
        companion object {
            fun createSearchViewHolder(parent: ViewGroup): SearchViewHolder {
                val itemView = viewInflater(parent, R.layout.search_layout)
                return SearchViewHolder(itemView)
            }
        }

        private val history = nItemView.findViewById<ImageView>(R.id.history)
        private val results = nItemView.findViewById<TextView>(R.id.results)
        private val restore = nItemView.findViewById<ImageView>(R.id.restore)

        var iPosition = 0

        private fun bind(name: String) {
            results.text = name
        }

        private fun bind(name: Spannable) {
            history.visibility = View.GONE
            restore.visibility = View.GONE
            results.setPadding(8, 0, 8, 0)
            results.text = name
        }

        fun bind(name: String, string: String?) {
            if (string != null) {
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
                    bind(spannable)
                } else {
                    bind(name)
                }
            } else {
                bind(name)
            }
        }

        init {
//            results.setOnClickListener {
//                clickHandler.onResultsClick(iPosition)
//            }
//
//            restore.setOnClickListener {
//                clickHandler.onRestoreClick(iPosition)
//            }
        }
    }
}