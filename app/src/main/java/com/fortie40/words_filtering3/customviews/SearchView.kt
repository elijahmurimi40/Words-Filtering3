package com.fortie40.words_filtering3.customviews

import android.animation.Animator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import com.fortie40.words_filtering3.R
import kotlinx.android.synthetic.main.view_search.view.*

class SearchView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_search, this, true)

        open_search_button.setOnClickListener { openSearch() }
        close_search_button.setOnClickListener { closeSearch() }
    }

    private lateinit var circularAnim: Animator

    private fun openSearch() {
        set_focus.visibility = View.GONE
        search_input_text.setText("")
        search_open_view.visibility = View.VISIBLE
        searchViewCircularAnimation(0f, width.toFloat())
        search_input_text.requestFocus()
    }

    private fun closeSearch() {
        searchViewCircularAnimation(width.toFloat(), 0f)

        circularAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) = Unit
            override fun onAnimationCancel(animation: Animator?) = Unit
            override fun onAnimationStart(animation: Animator?) = Unit
            override fun onAnimationEnd(animation: Animator?) {
                search_open_view.visibility = View.INVISIBLE
                search_input_text.setText("")
                circularAnim.removeAllListeners()
            }
        })
    }

    private fun searchViewCircularAnimation(startRad: Float, endRad: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circularAnim = ViewAnimationUtils.createCircularReveal(
                search_open_view,
                (open_search_button.right + open_search_button.left) / 2,
                (open_search_button.top + open_search_button.bottom) / 2,
                startRad, endRad
            )
            circularAnim.duration = 300
            circularAnim.start()
        }
    }
}