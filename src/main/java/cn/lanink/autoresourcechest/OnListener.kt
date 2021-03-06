package cn.lanink.autoresourcechest

import cn.lanink.autoresourcechest.chest.Chest
import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.Block
import cn.nukkit.blockentity.BlockEntityChest
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.player.PlayerInteractEvent
import cn.nukkit.event.player.PlayerQuitEvent
import cn.nukkit.level.Position

/**
 * @author lt_name
 */
class OnListener(val autoResourceChest: AutoResourceChest) : Listener {

    private val logCache = HashMap<Player, ArrayList<Position>>()

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        this.autoResourceChest.placeChestPlayer.remove(event.player ?: return)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player ?: return
        val block = event.block ?: return
        if (block.id != Block.CHEST) {
            return
        }
        val chestManager = this.autoResourceChest.placeChestPlayer[player] ?: return
        chestManager.addNewChest(block)
        chestManager.saveConfig()
        this.autoResourceChest.placeChestPlayer.remove(player)
        player.sendMessage("§e>> §a成功放置一个资源箱！")
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player ?: return
        val block = event.block ?: return
        if (block.id == Block.CHEST) {
            val chest = this.autoResourceChest.getChestByPos(block) ?: return
            if (player.isOp) {
                val chestManager = chest.chestManager
                chestManager.removeChest(chest)
                chestManager.saveConfig()
                val blockEntity = block.level?.getBlockEntity(block)
                if (blockEntity != null && blockEntity is BlockEntityChest) {
                    blockEntity.inventory.clearAll()
                }
                player.sendMessage("§e>> §a成功移除一个资源箱！")
            }else {
                event.setCancelled()
                player.sendMessage("§e>> §c您没有权限破坏资源箱！")
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player ?: return
        val block = event.block ?: return
        if (block.id == Block.CHEST && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            val chest: Chest = this.autoResourceChest.getChestByPos(block) ?: return
            val playerConfig = this.autoResourceChest.playerConfigManager.getPlayerConfig(player)
            if (chest.chestManager.restrictOpenCount > 0) {
                if (playerConfig.getOpenCount(block) >= chest.chestManager.restrictOpenCount) {
                    event.setCancelled()
                    player.sendMessage("§e>> §c您已达到限制打开次数！")
                    return
                }
            }
            if (chest.isNeedRefresh) {
                if (chest.time <= chest.chestManager.refreshInterval/2) {
                    event.setCancelled()
                    player.sendMessage("§e>> §c请等待箱子刷新！")
                }
            }else {
                chest.isNeedRefresh = true
                val list = this.logCache.getOrDefault(player, ArrayList())
                if (!list.contains(block)) {
                    list.add(block)
                    this.logCache[player] = list
                    Server.getInstance().scheduler.scheduleDelayedTask(this.autoResourceChest, {
                        playerConfig.addOpenCount(block)
                        this.logCache[player]?.remove(block)
                    }, chest.chestManager.refreshInterval/2 * 20)
                }
            }
        }
    }

}