package io.github.yin.residencestoragebungee

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream


class ResidenceStorageBungeeMain : Plugin(), Listener {
    companion object {
        lateinit var instance: ResidenceStorageBungeeMain
        const val prefix = "§f[§7领地储存§f] "
        const val pluginChannel = "residencestorage:channel"
    }

    override fun onEnable() {
        instance = this
        proxy.console.sendMessage(TextComponent(prefix + "插件开始加载 " + description.version))

        proxy.registerChannel(pluginChannel)
        proxy.pluginManager.registerListener(this, this)
    }

    override fun onDisable() {
        proxy.console.sendMessage(TextComponent(prefix + "插件开始卸载 " + description.version))
    }


    @EventHandler(priority = 3)
    fun onPluginMessage(event: PluginMessageEvent) {
        if (event.tag != pluginChannel) {
            return
        }

        val proxiedPlayer = event.receiver as? ProxiedPlayer ?: return

        DataInputStream(ByteArrayInputStream(event.data)).use { input ->
            val residenceName = input.readUTF()
            val serverName = input.readUTF()

            val serverInfo = ProxyServer.getInstance().getServerInfo(serverName)
            proxiedPlayer.connect(serverInfo) { result: Boolean, error: Throwable? ->
                if (result) {
                    ProxyServer.getInstance().scheduler.runAsync(instance) {
                        Thread.sleep(1000)
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        val output = DataOutputStream(byteArrayOutputStream)
                        output.writeUTF(residenceName)
                        proxiedPlayer.server.sendData(pluginChannel, byteArrayOutputStream.toByteArray())
                    }
                }
            }
        }
    }






}