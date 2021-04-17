package cn.lanink.autoresourcechest

import cn.lanink.autoresourcechest.chest.Chest
import cn.lanink.autoresourcechest.chest.ChestManager
import cn.lanink.autoresourcechest.player.PlayerConfigManager
import cn.lanink.autoresourcechest.task.ChestUpdateTask
import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.level.Position
import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.Config
import java.io.File
import java.util.*

/**
 * @author lt_name
 */
class AutoResourceChest : PluginBase() {

    val chestConfigMap: HashMap<String, ChestManager> = HashMap()
    val placeChestPlayer: HashMap<Player, ChestManager> = HashMap()

    val playerConfigManager = PlayerConfigManager(this)

    companion object {
        @JvmStatic
        val RANDOM = Random()
        const val VERSION = "?"
        var debug = false
        var instance: AutoResourceChest? = null
    }

    override fun onLoad() {
        instance = this
        this.saveDefaultConfig()
        this.saveResource("playerUseChestLog.yml")
        val file1 = File("$dataFolder/Chests")
        if (!file1.exists() && !file1.mkdirs()) {
            logger.error("Chests 文件夹初始化失败, 这可能导致插件无法正常运行！")
        }
        val file2 = File("$dataFolder/Players")
        if (!file2.exists() && !file2.mkdirs()) {
            logger.error("Players 文件夹初始化失败, 这可能导致插件无法正常运行！")
        }
        if (this.config.getBoolean("debug", false)) {
            debug = true
            this.logger.warning("§c=========================================")
            this.logger.warning("§c 警告：您开启了debug模式！")
            this.logger.warning("§c Warning: You have turned on debug mode!")
            this.logger.warning("§c=========================================")
            try {
                Thread.sleep(5000)
            } catch (ignored : InterruptedException) {

            }
        }
    }

    override fun onEnable() {
        this.loadAllChests()
        server.pluginManager.registerEvents(OnListener(this), this)
        server.scheduler.scheduleRepeatingTask(this, ChestUpdateTask(this), 20)
        logger.info("加载完成！版本:$VERSION")
        server.scheduler.scheduleTask(this) {
            logger.warning("AutoResourceChest 是一款免费插件，开源链接: https://github.com/lt-name/AutoResourceChest")
        }

    }

    override fun onDisable() {
        for (chestManager in this.chestConfigMap.values) {
            chestManager.saveConfig()
            chestManager.closeAllChest()
        }
        this.playerConfigManager.saveAllPlayerConfig()
    }

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        sender ?: return false
        command ?: return false
        if (command.name == "autoresourcechest" || command.name == "arc") {
            if (!sender.isPlayer) {
                sender.sendMessage("§e>> §c请在游戏内使用此命令")
                return true
            }
            if (args.isNullOrEmpty()) {
                this.sendCommandHelp(sender)
                return true
            }
            val player: Player = sender as Player

            when(args[0]) {
                "create" -> {
                    if (args.size > 1) {
                        val name = args[1]
                        if (File("$dataFolder/Chests/$name.yml").exists()) {
                            sender.sendMessage("§e>> §c已存在名为 $name 的资源箱配置！")
                            return true
                        }
                        this.saveResource("chest.yml", "Chests/$name.yml", true)
                        this.chestConfigMap[name] = ChestManager(name, Config("$dataFolder/Chests/$name.yml", Config.YAML))
                        sender.sendMessage("§e>> §a新的资源箱配置 $name 创建成功！")
                    }else {
                        sender.sendMessage("§e>> §c请输入资源箱名字！")
                    }
                }

                "place" -> {
                    if (args.size > 1) {
                        val name = args[1]
                        val chestManager = this.chestConfigMap[name]
                        if (chestManager == null) {
                            sender.sendMessage("§e>> §c不存在名为 $name 的资源箱配置，请先创建！")
                            return true
                        }
                        this.placeChestPlayer[player] = chestManager
                        sender.sendMessage("§e>> §a请放置一个箱子作为资源箱！")
                    }else {
                        sender.sendMessage("§e>> §c请输入资源箱名字！")
                    }
                }

                "reload" -> {
                    for (chestManager in this.chestConfigMap.values) {
                        chestManager.closeAllChest()
                    }
                    this.chestConfigMap.clear()
                    this.loadAllChests()
                }

                else -> {
                    this.sendCommandHelp(sender)
                }
            }
            return true
        }
        return false
    }

    private fun sendCommandHelp(sender: CommandSender) {
        sender.sendMessage("§a/arc create <配置名称> §e创建一个资源箱配置\n" +
                "§a/arc place <配置名称> §e放置一个资源箱\n" +
                "§a/arc reload §e从配置文件重新加载资源箱配置\n")
    }

    private fun loadAllChests() {
        val files = File("$dataFolder/Chests").listFiles()
        var count = 0
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (!file.isFile) {
                    continue
                }
                val name = file.name.split(".")[0]
                this.chestConfigMap[name] = ChestManager(name, Config(file, Config.YAML))
                count++
            }
        }
        logger.info("§a已加载 §e$count §a个资源箱配置")
    }

    fun getChestByPos(position: Position): Chest? {
        for (chestManager: ChestManager in this.chestConfigMap.values) {
            val chest = chestManager.getChestByPos(position)
            if (chest != null) {
                return chest
            }
        }
        return null
    }

}