package dev.hbq2.mc.verses.mixin.client;

import dev.hbq2.mc.verses.Utils;
import dev.hbq2.mc.verses.VersesConfig;
import dev.hbq2.mc.verses.VersesSplashClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(SplashTextRenderer.class)
public class SplashMixin {

    @Unique
    int y = 6;


    /**
     * @author HB
     * @reason Overwrite Splash Text
     */
    @Overwrite
    public void render(DrawContext context, int screenWidth, TextRenderer textRenderer, int alpha) {
        VersesConfig versesConfig = VersesSplashClient.INSTANCE.getConfig();

        if (!versesConfig.isEnabled() || Utils.INSTANCE.getCachedVerseData() == null) {
            return;
        }

        if (Utils.INSTANCE.getCachedFormattedVerse() == null) {
            String verseText = Utils.cleanString(Utils.INSTANCE.getCachedVerseData().getText());

            List<String> words = Utils.INSTANCE.getWords();
            int totalWords = words.size();
            int wordsRandomizedBasedOnPercentage = (totalWords * versesConfig.getPercentageRandomized()) / 100;

            int t = 1;
            if (wordsRandomizedBasedOnPercentage > 0) {
                t = wordsRandomizedBasedOnPercentage;
            }

            if (versesConfig.isRandomizeWords()) {
                List<String> randomWords = Utils.INSTANCE.getRandomWords(t);

                // Apply Minecraft character randomization to random words
                for (String randomWord : randomWords) {
                    verseText = verseText.replaceFirst(randomWord, "§k" + randomWord + "§r");
                }
            }

            Utils.INSTANCE.setCachedFormattedVerse(Utils.INSTANCE.wrapLine(verseText, 40) + "\n\n" + Utils.INSTANCE.getBookChapterVerse());
        } else {
            String verseText = Utils.INSTANCE.getCachedFormattedVerse();

            context.getMatrices().push();
            context.getMatrices().translate(screenWidth / 2.0f + 123.0f, 69.0f, 0.0f);
            context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(versesConfig.getTextRotation()));
            float f = 1.8f - Math.abs((float) Math.sin((Util.getMeasuringTimeMs() % 1000L) / 1000.0f * (Math.PI * 2f)) * 0.1f);
            f = f * 100.0f / (textRenderer.getWidth(verseText.lines().toList().isEmpty() ? "" : verseText.lines().toList().getFirst()) + 32);
            context.getMatrices().scale(f, f, f);

            long count = verseText.lines().count();

            int textHeight = textRenderer.fontHeight;

            for (int i = 0; i < count; i++) {
                context.drawCenteredTextWithShadow(textRenderer, verseText.lines().toList().get(i), 0, y + i * (textHeight + 2), versesConfig.getTextColor() | alpha);
            }
            context.getMatrices().pop();
        }
    }
}