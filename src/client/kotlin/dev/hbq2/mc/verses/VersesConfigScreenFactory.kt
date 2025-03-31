package dev.hbq2.mc.verses

import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry
import me.shedaniel.clothconfig2.gui.entries.ColorEntry
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import java.util.function.Consumer


object VersesConfigScreenFactory {
    fun createConfigScreen(parent: Screen?, config: VersesConfig, update: Consumer<VersesConfig?>): Screen {
        val defaultConfig: VersesConfig = VersesConfig.DEFAULT

        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.literal("Configuration"))

        val entryBuilder = builder.entryBuilder()

        val enabled: BooleanListEntry = entryBuilder
            .startBooleanToggle(Text.literal("Enabled"), config.isEnabled)
            .setTooltip(Text.literal("Enables the mod. If disabled the splash text will not display."))
            .setDefaultValue(defaultConfig.isEnabled)
            .build()

        val randomizeWords: BooleanListEntry = entryBuilder
            .startBooleanToggle(Text.literal("Random word formatting"), config.isRandomizeWords)
            .setTooltip(Text.literal("Random words formatted with §krandom."))
            .setDefaultValue(defaultConfig.isRandomizeWords)
            .build()

        val randomizedWordsPercentage: IntegerSliderEntry = entryBuilder
            .startIntSlider(Text.literal("Randomized words percentage."), config.percentageRandomized, 1, 50)
            .setTooltip(Text.literal("Percent of randomized words."))
            .setDefaultValue(defaultConfig.percentageRandomized)
            .build()

        val textColor: ColorEntry = entryBuilder
            .startColorField(Text.literal("Splash text color"), config.textColor)
            .setTooltip(Text.literal("Change splash text color."))
            .setDefaultValue(defaultConfig.textColor)
            .build()

        val versionNames = arrayOf("NKJV", "NLT", "ESV", "KJV")

        val version = entryBuilder
            .startSelector(Text.literal("Version"), versionNames, config.version)
            .setTooltip(Text.literal("Version of the Bible to use."))
            .setDefaultValue(defaultConfig.version)
            .build()

        val cachedVerseTitle = entryBuilder.startTextDescription(Text.literal("Cached Verse")).build()
        val cachedVerse = entryBuilder.startTextDescription(Text.literal(Utils.formattedResponse() ?: "")).build()
        val disclaimer = entryBuilder.startTextDescription(Text.literal("API provided by https://bolls.life/")).build()

        val general = builder.getOrCreateCategory(Text.literal("General"))
        general.addEntry(enabled)
        general.addEntry(randomizeWords)
        general.addEntry(randomizedWordsPercentage)
        general.addEntry(version)
        general.addEntry(textColor)
        general.addEntry(cachedVerseTitle)
        general.addEntry(cachedVerse)
        general.addEntry(disclaimer)

        builder.setSavingRunnable {
            update.accept(
                VersesConfig(
                    enabled = enabled.value,
                    randomizeWords = randomizeWords.value,
                    percentageRandomized = randomizedWordsPercentage.value,
                    version = version.value,
                    textColor = textColor.value
                )
            )
            Utils.cachedFormattedVerse = null
            NetworkModule.getVerseAsync(version = version.value) { }
        }

        return builder.build()
    }
}