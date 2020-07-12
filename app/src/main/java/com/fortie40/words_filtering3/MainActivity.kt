package com.fortie40.words_filtering3

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.fortie40.words_filtering3.adapters.MainActivityAdapter
import com.fortie40.words_filtering3.adapters.SearchAdapter
import com.fortie40.words_filtering3.helperclasses.HelperFunctions
import com.fortie40.words_filtering3.helperclasses.PreferenceHelper.get
import com.fortie40.words_filtering3.helperclasses.PreferenceHelper.set
import com.fortie40.words_filtering3.interfaces.IClickListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), IClickListener {
    private lateinit var searchView: SearchView
    private lateinit var mainAdapter: MainActivityAdapter
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var names: List<String>
    private lateinit var sharedPref: SharedPreferences
    private lateinit var recent: ArrayList<String>
    private lateinit var setFocus: MenuItem
    private lateinit var voiceSearch: MenuItem
    private lateinit var close: MenuItem

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getPreferences(Context.MODE_PRIVATE)
        getNames()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu!!.findItem(R.id.app_bar_search)
        val view = searchItem.actionView
        searchView = view as SearchView

        setFocus = menu.findItem(R.id.set_focus)
        voiceSearch = menu.findItem(R.id.voice_search)
        close = menu.findItem(R.id.close)
        val searchClose =
            searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        searchClose.isEnabled = false
        searchClose.setImageDrawable(null)

        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH or EditorInfo.IME_FLAG_NO_EXTRACT_UI
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint = getString(R.string.search_name)

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val query = searchView.query
                hideShowVoiceCloseIcon(query.toString())
                setFocus.isVisible = false
                hideNoResultsFound()
                getRecentSearches()
                searchAdapter = SearchAdapter(recent, this)
                names_item.adapter = searchAdapter
            }
        }

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                getRecentSearches()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                hideNoResultsFound()
                setFocus.isVisible = false
                voiceSearch.isVisible = false
                close.isVisible = false
                names_item.adapter = mainAdapter
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchName(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (names_item.adapter == mainAdapter)
                    return false
                Log.i(TAG, "changed")
                hideShowVoiceCloseIcon(newText)
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.set_focus -> {
                searchView.requestFocus()
                HelperFunctions.showInputMethod(this)
                true
            }
            R.id.close -> {
                searchView.setQuery("", false)
                true
            }
            else -> super.onOptionsItemSelected(item)}
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.setQuery("", false)
            searchView.clearFocus()
            searchView.isIconified = true
        } else {
            super.onBackPressed()
        }
    }

    override fun onResultsClick(position: Int) {
        searchView.setQuery(recent[position], true)
    }

    override fun onRestoreClick(position: Int) {
        searchView.setQuery(recent[position], false)
    }

    private fun getNames() {
        names = arrayListOf(
            "Fortie40", "Java", "Kotlin", "C++", "PHP", "Javascript", "Objective-C", "Swift",
            "Groovy", "Haskell", "JQuery", "KRYPTON", "LotusScript", "Mortran", "NewLISP", "Orwell",
            "Hopscotch", "JScript", "AngelScript", "Bash", "Clojure", "C", "COBOL", "CSS",
            "Cybil", "Pascal", "Perl", "Smalltalk", "SQL", "Unicon", "Ubercode", "Fortran",
            "Hollywood", "SMALL", "Lisp", "PureScript", "R++", "XQuery", "YAML", "ZOPL"
        )

        mainAdapter =
            MainActivityAdapter(names)
        names_item.adapter = mainAdapter
    }

    private fun searchName(p0: String?) {
        if (p0.isNullOrEmpty()) {
            return
        }
        setFocus.isVisible = true
        hideShowVoiceCloseIcon()
        hideNoResultsFound()
        progressBar.visibility = View.VISIBLE
        names_item.visibility = View.GONE
        saveToRecentSearch(p0)
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
                names_item.visibility = View.VISIBLE
                searchView.clearFocus()
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

    private fun hideShowVoiceCloseIcon(p0: String?) {
        if (p0 == null) {
            return
        }
        voiceSearch.isVisible = p0.isEmpty()
        close.isVisible = p0.isNotEmpty()
    }

    private fun hideShowVoiceCloseIcon() {
        voiceSearch.isVisible = true
        close.isVisible = false
    }
}