package io.github.yin.residencestoragebungee

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
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

        DataInputStream(ByteArrayInputStream(event.data)).use { input ->
            val action = input.readUTF().lowercase()
            if (action != ("teleport")) {
                return
            }

            val proxiedPlayer = ProxyServer.getInstance().getPlayer(input.readUTF())
            val residenceName = input.readUTF()
            val serverName = input.readUTF()

            val serverInfo = ProxyServer.getInstance().getServerInfo(serverName)
            proxiedPlayer.connect(serverInfo)
            ProxyServer.getInstance().scheduler.runAsync(instance) {
                Thread.sleep(1000)
                val byteArrayOutputStream = ByteArrayOutputStream()
                DataOutputStream(byteArrayOutputStream).use { out ->
                    out.writeUTF("teleport")
                    out.writeUTF(residenceName)
                }
                proxiedPlayer.server.sendData(pluginChannel, byteArrayOutputStream.toByteArray())
            }
        }
    }


}