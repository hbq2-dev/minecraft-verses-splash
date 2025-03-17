package com.hb.mc.verses.mixin.client;

import com.hb.mc.verses.NetworkModule;
import com.hb.mc.verses.Utils;
import com.hb.mc.verses.VersesConfig;
import com.hb.mc.verses.VersesSplashClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

import static com.hb.mc.verses.NetworkModule.getVerse;

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

        if (!versesConfig.isEnabled()) {
            return;
        }

        if (Utils.INSTANCE.getExistingVerse() == null) {
            try {
                JSONObject o = (JSONObject) JSONValue.parse(getVerse());
                String text = o.getAsString("text").replaceAll("<[^>]*>", "");

                JSONObject bibleBooks = (JSONObject) JSONValue.parse(NetworkModule.INSTANCE.bibleListJsonString());
                JSONArray bibleBooksArray = (JSONArray) bibleBooks.get("books");
                int bookIndex = o.getAsNumber("book").intValue();
                JSONObject bibleBook = (JSONObject) bibleBooksArray.get(bookIndex - 1);

                String bookChapterVerse = bibleBook.getAsString("book") + " " + o.getAsNumber("chapter") + ":" + o.getAsNumber("verse") + " (" + versesConfig.getVersion() + ")";
                System.out.println("Got Verse!");

                int totalWords = Utils.INSTANCE.getWords(text).size();
                int wordsRandomizedBasedOnPercentage = (totalWords * versesConfig.getPercentageRandomized()) / 100;

                int t = 1;
                if (wordsRandomizedBasedOnPercentage > 0) {
                    t = wordsRandomizedBasedOnPercentage;
                }

                if (versesConfig.isRandomizeWords()) {
                    List<String> randomWords = Utils.INSTANCE.getRandomWords(Utils.INSTANCE.getWords(text), t);

                    // Apply Minecraft character randomization to random words
                    for (String randomWord : randomWords) {
                        text = text.replaceFirst(randomWord, "§k" + randomWord + "§r");
                    }
                }
                Utils.INSTANCE.setExistingVerse(Utils.INSTANCE.wrapLine(text, 40) + "\n\n" + bookChapterVerse);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            String verseText = Utils.INSTANCE.getExistingVerse();

            context.getMatrices().push();
            context.getMatrices().translate(screenWidth / 2.0f + 123.0f, 69.0f, 0.0f);
            context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20.0f));
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