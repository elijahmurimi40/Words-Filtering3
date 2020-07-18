package com.fortie40.words_filtering3.customviews

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.fortie40.words_filtering3.R
import com.fortie40.words_filtering3.helperclasses.HelperFunctions
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

        search_input_text.setOnFocusChangeListener { _, hasFocus ->
            listener!!.onFocusChange(hasFocus, search_input_text.text.toString(), voice_search, close)
        }

        set_focus.setOnClickListener { setFocus() }

        close.setOnClickListener { close() }

        voice_search.setOnClickListener { listener!!.onPromptSpeechInput() }
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
                listener!!.hideShowVoiceCloseIcon(s.toString(), voice_search, close)
            }
        }
    }

    private fun setFocus() {
        search_input_text.requestFocus()
        HelperFunctions.showInputMethod(context)
    }

    private fun close() {
        search_input_text.setText("")
    }
}