package com.fortie40.words_filtering3.helperclasses

import java.util.*
import kotlin.collections.ArrayList

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
}