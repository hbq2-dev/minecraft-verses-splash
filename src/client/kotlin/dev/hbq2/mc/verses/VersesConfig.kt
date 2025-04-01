package dev.hbq2.mc.verses

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
class VersesConfig {
    var isEnabled: Boolean = true
        private set

    @Comment("Choose between Bible versions.")
    var version: String = "NKJV"
        private set

    @Comment("Splash text color.")
    var textColor: Int = 16776960
        private set

    @Comment("Randomizes words with §k")
    var isRandomizeWords: Boolean = false
        private set

    var percentageRandomized: Int = 15
        private set

    var textRotation: Int = -20
        private set

    constructor()

    constructor(
        enabled: Boolean,
        randomizeWords: Boolean,
        percentageRandomized: Int,
        version: String,
        textColor: Int,
        textRotation: Int,
    ) {
        this.isEnabled = enabled
        this.version = version
        this.isRandomizeWords = randomizeWords
        this.textColor = textColor
        this.percentageRandomized = percentageRandomized
        this.textRotation = textRotation
    }

    companion object {
        @JvmStatic
        val DEFAULT: VersesConfig = VersesConfig()
    }
}
