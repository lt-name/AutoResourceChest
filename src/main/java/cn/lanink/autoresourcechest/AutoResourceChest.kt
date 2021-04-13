package cn.lanink.autoresourcechest

import cn.lanink.autoresourcechest.chest.Chest
import cn.lanink.autoresourcechest.chest.ChestManager
import cn.lanink.autoresourcechest.task.ChestUpdateTask
import cn.nukkit.level.Position
import cn.nukkit.plugin.PluginBase
import cn.nukkit.utils.Config
import lombok.Getter
import java.io.File
import java.util.*

/**
 * @author lt_name
 */
class AutoResourceChest : PluginBase() {

    @Getter
    private var playerLog: Config? = null

    val chestConfigMap: HashMap<String, ChestManager> = HashMap()

    override fun onLoad() {
        instance = this
        this.saveDefaultConfig()
        this.saveResource("playerUseChestLog.yml")
        val file = File("$dataFolder/Chests")
        if (!file.exists() && !file.mkdirs()) {
            logger.error("Chests 文件夹初始化失败")
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
        playerLog = Config("$dataFolder/playerUseChestLog.yml", Config.YAML)
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
        //TODO
    }

    private fun loadAllChests() {
        val files = File("$dataFolder/Chests").listFiles()
        var count = 0
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (!file.isFile) {
                    continue
                }
                val name = file.name.split("\\.")[0]
                this.chestConfigMap.put(name, ChestManager(name, Config(file, Config.YAML)))
                count++
            }
        }
        logger.info("§a已加载 §e$count §a个箱子配置")
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

    companion object {
        @JvmStatic
        val RANDOM = Random()
        const val VERSION = "?"
        var debug = false
        var instance: AutoResourceChest? = null
    }

}