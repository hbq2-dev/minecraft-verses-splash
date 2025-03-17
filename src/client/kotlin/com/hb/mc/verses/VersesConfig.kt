package com.hb.mc.verses

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class VersesConfig {
    var isEnabled: Boolean = true
        private set

    @Comment("Choose between Bible versions.")
    var version: String = "NKJV"
        private set

    @Comment("Saved verse of the day.")
    var votd: String? = null
        private set

    @Comment("Enables verse of the day.\nThe verse won't change until the next calendar day.")
    var isVOTDEnabled: Boolean = false
        private set

    var votdDate: String? = null
        private set

    @Comment("Splash text color.")
    var textColor: Int = 16776960
        private set

    @Comment("Randomizes words with §k")
    var isRandomizeWords: Boolean = false
        private set

    var percentageRandomized: Int = 15
        private set

    constructor()

    constructor(
        enabled: Boolean,
        isVOTDEnabled: Boolean,
        randomizeWords: Boolean,
        percentageRandomized: Int,
        version: String,
        textColor: Int,
        votdDate: String?,
        votd: String?
    ) {
        this.isEnabled = enabled
        this.isVOTDEnabled = isVOTDEnabled
        this.version = version
        this.isRandomizeWords = randomizeWords
        this.textColor = textColor
        this.votdDate = votdDate
        this.votd = votd
        this.percentageRandomized = percentageRandomized
    }

    companion object {
        @JvmStatic
        val DEFAULT: VersesConfig = VersesConfig()
    }
}
