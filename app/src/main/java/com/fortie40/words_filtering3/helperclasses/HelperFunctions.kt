package com.fortie40.words_filtering3.helperclasses

import android.content.Context
import android.content.Intent
import android.os.Build
import android.speech.RecognizerIntent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

object HelperFunctions {
    fun getNames(): ArrayList<String> {
        return arrayListOf(
            "Fortie40", "Java", "Kotlin", "C++", "PHP", "Javascript", "Objective-C", "Swift",
            "Groovy", "Haskell", "JQuery", "KRYPTON", "LotusScript", "Mortran", "NewLISP", "Orwell",
            "Hopscotch", "JScript", "AngelScript", "Bash", "Clojure", "C", "COBOL", "CSS",
            "Cybil", "Pascal", "Perl", "Smalltalk", "SQL", "Unicon", "Ubercode", "Fortran",
            "Hollywood", "SMALL", "Lisp", "PureScript", "R++", "XQuery", "YAML", "ZOPL"
        )
    }

    fun listToArrayList(list: List<String>?, name: String = ""): ArrayList<String> {
        val nameToLower = name.toLowerCase(Locale.getDefault())
        val queryList = arrayListOf<String>()
        if (name.isEmpty()) {
            list?.forEach {
                if (it.isNotEmpty())
                    queryList.add(it)
            }
        } else {
            queryList.add(nameToLower)
            list?.forEach {
                if (it != nameToLower)
                    queryList.add(it)
            }
        }

        return queryList
    }

    fun arrayListToString(list: ArrayList<String>): String {
        return list.toString()
            .replace("[", "")
            .replace("]", "")
            .replace(" ", "")
    }

    fun showInputMethod(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideInputMethod(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun promptSpeechInput(string: String): Intent {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, string)

        return intent
    }

    fun changeStatusBarColor(context: AppCompatActivity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.window.statusBarColor = ContextCompat.getColor(context, color)
        }
    }
}