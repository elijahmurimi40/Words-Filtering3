package com.fortie40.words_filtering3.helperclasses

import android.content.Context
import android.view.inputmethod.InputMethodManager
import java.util.*

object HelperFunctions {
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
}