package cn.lanink.autoresourcechest.task

import cn.lanink.autoresourcechest.AutoResourceChest
import cn.nukkit.Server
import cn.nukkit.blockentity.BlockEntity
import cn.nukkit.blockentity.BlockEntityChest
import cn.nukkit.scheduler.PluginTask

/**
 * @author LT_Name
 */
class WorldChestCheckTask(owner: AutoResourceChest, private val worlds : HashMap<String, String>) : PluginTask<AutoResourceChest>(owner) {


    @Override
    override fun onRun(i: Int) {
        //检查并移除已失效的箱子
        if (i%100 == 0) {
            val values = AutoResourceChest.instance?.chestConfigMap?.values
            if (values != null) {
                for (chestManager in values) {
                    var hasChange = false
                    for (pos in chestManager.chests.keys) {
                        if (pos.level.getBlockEntity(pos) !is BlockEntityChest) {
                            chestManager.removeChest(chestManager.chests[pos] ?: continue)
                            hasChange = true
                        }
                    }
                    if (hasChange) {
                        chestManager.saveConfig()
                    }
                }
            }
        }
        //检查并添加新的箱子
        for (entry in this.worlds.entries) {
            val world = Server.getInstance().getLevelByName(entry.key)
            for (blockEntity : BlockEntity in world.blockEntities.values) {
                if (blockEntity is BlockEntityChest) {
                    if (AutoResourceChest.instance!!.getChestByPos(blockEntity) != null) {
                        continue
                    }
                    val chestManager = AutoResourceChest.instance?.chestConfigMap?.get(entry.value) ?: continue
                    var hasChange = false
                    if (chestManager.getChestByPos(blockEntity) == null) {
                        chestManager.addNewChest(blockEntity)
                        hasChange = true
                    }
                    if (hasChange) {
                        chestManager.saveConfig()
                    }
                }
            }
        }
    }

}