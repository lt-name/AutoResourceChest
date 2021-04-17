package cn.lanink.autoresourcechest

import cn.lanink.autoresourcechest.chest.Chest
import cn.nukkit.block.Block
import cn.nukkit.blockentity.BlockEntityChest
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.player.PlayerInteractEvent
import cn.nukkit.event.player.PlayerQuitEvent

/**
 * @author lt_name
 */
class OnListener(val autoResourceChest: AutoResourceChest) : Listener {

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
            if (chest.isNeedRefresh) {
                if (chest.time <= chest.chestManager.refreshInterval/2) {
                    event.setCancelled()
                    player.sendMessage("§e>> §c请等待箱子刷新！")
                }
            }else {
                chest.isNeedRefresh = true
            }
        }
    }

}