package dev.hbq2.mc.verses

import ca.stellardrift.confabricate.Confabricate
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.Screen
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.reference.ConfigurationReference
import org.spongepowered.configurate.reference.ValueReference
import org.spongepowered.configurate.reference.WatchServiceListener
import java.io.IOException
import java.nio.file.Path

object VersesSplashClient : ClientModInitializer {
    private const val MOD_ID: String = "versesClient"

    private val LOGGER: Logger = LogManager.getLogger()

    private var configReference: ValueReference<VersesConfig, CommentedConfigurationNode>? = null

    override fun onInitializeClient() {
        try {
            val configPath: Path =
                FabricLoader.getInstance().configDir.resolve("$MOD_ID.conf")
            val rootRef: ConfigurationReference<CommentedConfigurationNode> =
                getOrCreateWatchServiceListener()
                    .listenToConfiguration(
                        { path -> HoconConfigurationLoader.builder().path(path).build() },
                        configPath,
                    )
            configReference = rootRef.referenceTo(VersesConfig::class.java)
            rootRef.saveAsync()

            NetworkModule.getVerseAsync(version = getConfig().version) { }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun getOrCreateWatchServiceListener(): WatchServiceListener =
        try {
            Confabricate.fileWatcher()
        } catch (ignored: NoClassDefFoundError) {
            createWatchServiceListener()
        } catch (ignored: NoSuchMethodError) {
            createWatchServiceListener()
        }

    @Throws(IOException::class)
    private fun createWatchServiceListener(): WatchServiceListener {
        val listener = WatchServiceListener.create()

        Runtime.getRuntime().addShutdownHook(
            Thread({
                try {
                    listener.close()
                } catch (e: IOException) {
                    LOGGER.catching(e)
                }
            }, "Configure shutdown thread (Verses)"),
        )

        return listener
    }

    fun getConfig(): VersesConfig = if (configReference != null) configReference?.get() as VersesConfig else VersesConfig.DEFAULT

    fun createConfigScreen(parent: Screen?): Screen? {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            return VersesConfigScreenFactory.createConfigScreen(parent, getConfig()) { value: VersesConfig? ->
                configReference?.setAndSaveAsync(
                    value,
                )
            }
        }
        return null
    }
}
