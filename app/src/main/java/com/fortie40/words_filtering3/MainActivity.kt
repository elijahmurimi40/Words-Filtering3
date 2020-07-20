package com.fortie40.words_filtering3

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.fortie40.words_filtering3.adapters.MainActivityAdapter
import com.fortie40.words_filtering3.adapters.SearchAdapter
import com.fortie40.words_filtering3.customviews.FortieSearchView
import com.fortie40.words_filtering3.helperclasses.HelperFunctions
import com.fortie40.words_filtering3.helperclasses.PreferenceHelper.get
import com.fortie40.words_filtering3.helperclasses.PreferenceHelper.set
import com.fortie40.words_filtering3.interfaces.IClickListener
import com.fortie40.words_filtering3.interfaces.ISearchViewListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.view_search.*
import java.util.*

class MainActivity : AppCompatActivity(), IClickListener, ISearchViewListener {
    private lateinit var mainAdapter: MainActivityAdapter
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var names: List<String>
    private lateinit var sharedPref: SharedPreferences
    private lateinit var recent: ArrayList<String>
    private lateinit var moveUp: Animation
    private lateinit var moveDown: Animation

    private var history: ArrayList<String>? = null
    private var onBackPressed = true
    private var save = false

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FortieSearchView.setListener(this)

        sharedPref = getPreferences(Context.MODE_PRIVATE)
        getNames()

        moveUp = AnimationUtils.loadAnimation(this, R.anim.move_up)
        moveDown = AnimationUtils.loadAnimation(this, R.anim.move_down)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULTS_SPEECH -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    if (!result[0].isNullOrEmpty()) {
                        search_input_text.setText(result[0])
                        search_open_view.requestFocus()
                        save = true
                        searchName(result[0])
                    }
                }
                return
            }
        }
    }

    override fun onResultsClick(position: Int) {
        search_input_text.setText(recent[position])
        search_open_view.requestFocus()
        save = true
        searchName(recent[position])
    }

    override fun onRestoreClick(position: Int) {
        search_input_text.setText(recent[position])
        search_input_text.requestFocus()
        search_input_text.setSelection(search_input_text.text.length)
    }

    override fun onPromptSpeechInput() {
        HelperFunctions.hideInputMethod(this, search_input_text)
        val string = getString(R.string.speech_prompt)
        val intent = HelperFunctions.promptSpeechInput(string)

        try {
            startActivityForResult(intent, RESULTS_SPEECH)
        } catch (a: ActivityNotFoundException) {
            Snackbar.make(search_item, getString(R.string.speech_not_supported), Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override fun onOpenSearchView(
        inputText: EditText,
        viewToReveal: View,
        startView: View,
        width: Float
    ) {
        super.onOpenSearchView(inputText, viewToReveal, startView, width)
        history = arrayListOf()
        onBackPressed = true
        showSearchAdapter(inputText)
    }

    override fun onCloseSearchView(
        inputText: EditText,
        viewToReveal: View,
        startView: View,
        width: Float
    ) {
        super.onCloseSearchView(inputText, viewToReveal, startView, width)
        closeSearchView(inputText)
    }

    override fun onFocusChange(hasFocus: Boolean, string: String, voice: View, close: View) {
        super.onFocusChange(hasFocus, string, voice, close)
        if (hasFocus) {
            set_focus.isVisible = false
            hideNoResultsFound()
            getRecentSearches()
            searchAdapter = SearchAdapter(recent, this)
            search_item.adapter = searchAdapter
        }

    }

    override fun onSubmitQuery(actionId: Int, view: EditText): Boolean {
        val query = view.text.toString()
        save = true
        searchName(query)
        return super.onSubmitQuery(actionId, view)
    }

    override fun onBackPressed() {
        if (history != null && onBackPressed && history!!.size > 0) {
            onBackPressed = false
            history!!.removeAt(history!!.size - 1)
        }

        val length = history?.size
        if (length != null) {
            when {
                length >= 1 -> {
                    save = false
                    search_input_text.setText(history!![length - 1])
                    searchName(history!![length - 1])
                    hideShowVoiceCloseIcon()
                    history!!.removeAt(length - 1)
                }
                length == 0 -> {
                    onCloseSearchView(search_input_text,
                        search_open_view,
                        open_search_button,
                        search_open_view.width.toFloat())
                    history = null
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun getNames() {
        names = HelperFunctions.getNames()

        mainAdapter =
            MainActivityAdapter(names)
        names_item.adapter = mainAdapter
    }

    private fun searchName(p0: String?) {
        if (p0.isNullOrEmpty()) {
            return
        }

        if (save) {
            saveToRecentSearch(p0)
            addToHistory(p0)
        }

        HelperFunctions.hideInputMethod(this, search_input_text)
        searchAdapter = SearchAdapter(arrayListOf(), this)
        search_item.adapter = searchAdapter
        hideShowVoiceCloseIcon()
        hideNoResultsFound()
        progressBar.visibility = View.VISIBLE
        searchAdapter.originalList = names
        searchAdapter.string = p0
        searchAdapter.filter.filter(p0.toLowerCase(Locale.getDefault())) {
            when(searchAdapter.itemCount) {
                0 -> {
                    showNoResultsFound(R.string.no_results_found, text = p0)
                }
                else -> {
                    hideNoResultsFound()
                }
            }

            Log.i("MainActivity", p0)
            if (searchAdapter.string == "" || searchAdapter.string == p0) {
                Log.i("MainActivity", "done")
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun saveToRecentSearch(name: String) {
        val queries = sharedPref[QUERY, ""]?.split(",")

        val queryList = HelperFunctions.listToArrayList(queries, name = name)
        if (queryList.size == 6)
            queryList.removeAt(5)

        val query = HelperFunctions.arrayListToString(queryList)

        sharedPref[QUERY] = query
    }

    private fun getRecentSearches() {
        val queries = sharedPref[QUERY, ""]?.split(",")
        val queryList = HelperFunctions.listToArrayList(queries)
        Log.i(TAG,"$queryList")

        recent = queryList
        if (recent.isEmpty()) {
            showNoResultsFound(R.string.no_recent_search)
        } else {
            recent.add(0, HEADER_TITLE)
        }
    }

    private fun hideNoResultsFound() {
        no_results_found.visibility = View.GONE
    }

    private fun showNoResultsFound(resource: Int, text: String = "") {
        no_results_found.visibility = View.VISIBLE
        if (text.isEmpty()) {
            no_results_found.text = getString(resource)
        } else {
            no_results_found.text = getString(resource, text)
        }
    }

    private fun hideShowVoiceCloseIcon() {
        set_focus.isVisible = true
        voice_search.isVisible = true
        close.isVisible = false
    }

    private fun showSearchAdapter(inputText: EditText) {
        HelperFunctions.changeStatusBarColor(this, R.color.black)
        search_item.clearAnimation()
        getRecentSearches()
        search_item.visibility = View.VISIBLE
        search_item.startAnimation(moveUp)
        moveUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) = Unit
            override fun onAnimationStart(animation: Animation?) = Unit
            override fun onAnimationEnd(animation: Animation?) {
                HelperFunctions.showInputMethod(inputText.context)
            }
        })
    }

    private fun closeSearchView(inputText: EditText) {
        hideNoResultsFound()
        HelperFunctions.changeStatusBarColor(this, R.color.colorPrimaryDark)
        search_item.startAnimation(moveDown)
        moveDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) = Unit
            override fun onAnimationStart(animation: Animation?) {
                HelperFunctions.hideInputMethod(inputText.context, inputText)
            }
            override fun onAnimationEnd(animation: Animation?) {
                search_item.clearAnimation()
                search_item.visibility = View.GONE
            }
        })
    }

    private fun addToHistory(p0: String) {
        if (history!!.contains(p0)) {
            history!!.remove(p0)
        }

        history!!.add(p0)
    }
}