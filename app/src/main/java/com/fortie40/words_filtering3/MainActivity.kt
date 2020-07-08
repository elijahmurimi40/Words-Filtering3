package com.fortie40.words_filtering3

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import com.fortie40.words_filtering3.adapters.MainActivityAdapter
import com.fortie40.words_filtering3.adapters.SearchAdapter
import com.fortie40.words_filtering3.helperclasses.HelperFunctions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import com.fortie40.words_filtering3.helperclasses.PreferenceHelper.set
import com.fortie40.words_filtering3.helperclasses.PreferenceHelper.get
import com.fortie40.words_filtering3.interfaces.IClickListener

class MainActivity : AppCompatActivity(), IClickListener {
    private lateinit var searchView: SearchView
    private lateinit var mainAdapter: MainActivityAdapter
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var names: List<String>
    private lateinit var sharedPref: SharedPreferences
    private lateinit var recent: ArrayList<String>

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
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val searchItem = menu!!.findItem(R.id.app_bar_search)
        val view = searchItem.actionView
        searchView = view as SearchView

        val searchClose =
            searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        searchClose.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

        val searchGo =
            searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_go_btn)
        searchGo.setImageResource(R.drawable.ic_search_black_24dp)
        searchGo.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP)

        searchView.isSubmitButtonEnabled = true
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH or EditorInfo.IME_FLAG_NO_EXTRACT_UI
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint = getString(R.string.search_name)
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hideNoResultsFound()
                recent = getRecentSearches()
                searchAdapter =
                    SearchAdapter(
                        recent,
                        this
                    )
                names_item.adapter = searchAdapter
                if (recent.isEmpty()) {
                    showNoResultsFound(R.string.no_recent_search)
                }
            }
        }

        searchView.setOnCloseListener {
            hideNoResultsFound()
            names_item.adapter = mainAdapter
            false
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchName(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return super.onPrepareOptionsMenu(menu)
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
        progressBar.visibility = View.VISIBLE
        names_item.visibility = View.GONE
        saveToRecentSearch(p0!!)
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
            Log.i("MainActivityA", searchAdapter.string!!)
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

    private fun getRecentSearches(): ArrayList<String> {
        val queries = sharedPref[QUERY, ""]?.split(",")
        val queryList = HelperFunctions.listToArrayList(queries)
        Log.i(TAG,"$queryList")

        return queryList
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
}