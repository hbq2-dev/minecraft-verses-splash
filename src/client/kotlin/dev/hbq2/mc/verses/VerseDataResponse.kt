package dev.hbq2.mc.verses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
{
"pk": 2622941,
"translation": "KJV",
"book": 35,
"chapter": 3,
"verse": 1,
"text": "A prayer<S>8605</S> of Habakkuk<S>2265</S> the prophet<S>5030</S> upon Shigionoth<S>7692</S>. <sup>upon: or, according to variable songs, or, tunes, called in Hebrew, Shigionoth</sup>"
}
 */
@Serializable
data class VerseDataResponse(
    @SerialName("book")
    val book: Int, // 35
    @SerialName("chapter")
    val chapter: Int, // 3
    @SerialName("pk")
    val pk: Int, // 2622941
    @SerialName("text")
    val text: String, // A prayer<S>8605</S> of Habakkuk<S>2265</S> the prophet<S>5030</S> upon Shigionoth<S>7692</S>. <sup>upon: or, according to variable songs, or, tunes, called in Hebrew, Shigionoth</sup>
    @SerialName("translation")
    val translation: String, // KJV
    @SerialName("verse")
    val verse: Int, // 1
)

fun VerseDataResponse.cleanText(): String = text.replace("<[^>]*>".toRegex(), "")
