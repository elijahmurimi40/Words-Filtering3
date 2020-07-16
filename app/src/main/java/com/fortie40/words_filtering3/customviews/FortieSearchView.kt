package com.fortie40.words_filtering3.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.fortie40.words_filtering3.R
import com.fortie40.words_filtering3.interfaces.ISearchViewListener
import kotlinx.android.synthetic.main.view_search.view.*

class FortieSearchView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_search, this, true)

        open_search_button.setOnClickListener {
            listener!!.onOpenSearchView(
                search_input_text,
                search_open_view,
                open_search_button,
                width.toFloat()
            )
        }

        close_search_button.setOnClickListener {
            listener!!.onCloseSearchView(
                search_input_text,
                search_open_view,
                open_search_button,
                width.toFloat()
            )
        }

        search_input_text.setOnEditorActionListener {
                _, actionId, _ -> listener!!.onSubmitQuery(actionId, search_input_text)
        }
    }

    companion object {
        private var listener: ISearchViewListener? = null
        fun setListener(listener: ISearchViewListener) {
            this.listener = listener
        }
    }
}