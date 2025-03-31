package dev.hbq2.mc.verses

import dev.hbq2.mc.verses.NetworkModule.bibleListJsonString
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import net.minidev.json.JSONValue
import java.util.*

object Utils {
    const val API_URL = "https://bolls.life/get-random-verse"
    const val API_USER_AGENT_HEADER = "User-Agent"
    const val API_USER_AGENT_VALUE = "Mozilla/5.0"

    var cachedVerseData: VerseDataResponse? = null
    var cachedFormattedVerse: String? = null

    private var randomizedWords: MutableList<String> = mutableListOf()
    private val versesConfig = VersesConfig.DEFAULT

    fun wrapLine(
        line: String,
        width: Int,
    ): String {
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

    fun formattedResponse(): String? {
        cachedVerseData?.let { cachedVerseData ->
            var text = cachedVerseData.text.cleanString()

            val words = getWords()
            val totalWords = words.size
            val wordsRandomizedBasedOnPercentage: Int = (totalWords * versesConfig.percentageRandomized) / 100

            var t = 1
            if (wordsRandomizedBasedOnPercentage > 0) {
                t = wordsRandomizedBasedOnPercentage
            }

            if (versesConfig.isRandomizeWords) {
                val randomWords = getRandomWords(t)

                // Apply Minecraft character randomization to random words
                for (randomWord in randomWords) {
                    println("Some Random Work: $randomWord")
                    text = text.replaceFirst(randomWord.toRegex(), "§k$randomWord§r")
                }
            }

            return wrapLine(text, 40).plus("\n\n").plus(getBookChapterVerse())
        } ?: run {
            return null
        }
    }

    fun getBookChapterVerse(): String? {
        cachedVerseData?.let { cachedVerseData ->

            val bibleBooks = JSONValue.parse(bibleListJsonString()) as JSONObject
            val bibleBooksArray = bibleBooks["books"] as JSONArray?
            val bookIndex: Int = cachedVerseData.book
            val bibleBook = bibleBooksArray?.get(bookIndex - 1) as JSONObject

            val bookChapterVerse =
                bibleBook.getAsString("book") + " " + cachedVerseData.chapter + ":" + cachedVerseData.verse + " (" +
                    cachedVerseData.translation +
                    ")"
            return bookChapterVerse
        } ?: run {
            return null
        }
    }

    fun getWords(): List<String> {
        cachedVerseData?.let {
            return cachedVerseData?.text?.trim()?.split(" ") ?: emptyList()
        } ?: run {
            return emptyList()
        }
    }

    fun getRandomWords(totalItems: Int): List<String> {
        val rand = Random()
        val words = getWords().toMutableList()

        val newList: MutableList<String> = ArrayList()
        for (i in 0..<totalItems) {
            val randomIndex: Int = rand.nextInt(words.size)

            newList.add(words[randomIndex])

            words.removeAt(randomIndex)
        }
        randomizedWords = newList
        return newList
    }

    @JvmStatic
    fun String.cleanString(): String = replace("<[^>]*>".toRegex(), "")
}
