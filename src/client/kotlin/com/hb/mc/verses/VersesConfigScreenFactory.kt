package com.hb.mc.verses

import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry
import me.shedaniel.clothconfig2.gui.entries.ColorEntry
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import java.text.DateFormat
import java.util.*
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

        val isVOTDEnabled: BooleanListEntry = entryBuilder
            .startBooleanToggle(Text.literal("Enable Verse of the Day"), config.isVOTDEnabled)
            .setTooltip(Text.literal("Enables verse of the day."))
            .setDefaultValue(defaultConfig.isVOTDEnabled)
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

        val general = builder.getOrCreateCategory(Text.literal("General"))
        general.addEntry(enabled)
        general.addEntry(randomizeWords)
        general.addEntry(randomizedWordsPercentage)
        general.addEntry(version)
        general.addEntry(textColor)

        builder.setSavingRunnable {
            Utils.existingVerse = null

            val now = Date()
            val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())

            update.accept(
                VersesConfig(
                    enabled = enabled.value,
                    isVOTDEnabled = isVOTDEnabled.value,
                    randomizeWords = randomizeWords.value,
                    percentageRandomized = randomizedWordsPercentage.value,
                    version = version.value,
                    textColor = textColor.value,
                    votdDate = if (isVOTDEnabled.value && defaultConfig.votdDate != dateFormat.format(now)) {
                        dateFormat.format(now)
                    } else {
                        null
                    },
                    votd = null
                )
            )
        }

        return builder.build()
    }
}