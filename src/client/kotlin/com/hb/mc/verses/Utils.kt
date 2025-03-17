package com.hb.mc.verses

import java.util.*


object Utils {

    var existingVerse: String? = null

    fun wrapLine(line: String, width: Int): String {
        val words = line.trim().split(" ")
        val result = StringBuilder()
        var currentLineLength = 0

        for (word in words) {
            if (currentLineLength + word.length > width) {
                result.appendLine()
                currentLineLength = 0
            } else if (currentLineLength > 0) {
                result.append(" ")
                currentLineLength++
            }

            result.append(word)
            currentLineLength += word.length
        }

        return result.toString()
    }

    fun getWords(text: String): List<String> {
        return text.trim().split(" ")
    }

    fun getRandomWords(list: MutableList<String>, totalItems: Int): List<String> {
        val rand = Random()

        // create a temporary list for storing
        // selected element
        val newList: MutableList<String> = ArrayList()
        for (i in 0..<totalItems) {
            // take a random index between 0 to size
            // of given List

            val randomIndex: Int = rand.nextInt(list.size)


            // add element in temporary list
            newList.add(list[randomIndex])


            // Remove selected element from original list
            list.removeAt(randomIndex)
        }
        return newList
    }
}