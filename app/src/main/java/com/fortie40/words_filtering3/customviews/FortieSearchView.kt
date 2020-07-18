package com.fortie40.words_filtering3.customviews

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
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

        search_input_text.addTextChangedListener(textWatcher())

        search_input_text.setOnFocusChangeListener { _, hasFocus -> focusChange(hasFocus) }
    }

    companion object {
        private var listener: ISearchViewListener? = null
        fun setListener(listener: ISearchViewListener) {
            this.listener = listener
        }
    }

    private fun textWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hideShowVoiceCloseIcon(s.toString())
            }
        }
    }

    private fun focusChange(hasFocus: Boolean) {
        if (hasFocus)
            hideShowVoiceCloseIcon(search_input_text.text.toString())
    }

    private fun hideShowVoiceCloseIcon(p0: String?) {
        if (p0 == null)
            return

        voice_search.isVisible = p0.isEmpty()
        close.isVisible = p0.isNotEmpty()
    }

    inner class GetFortieSearchView {

    }
}