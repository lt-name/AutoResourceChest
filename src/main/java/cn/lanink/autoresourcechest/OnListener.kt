package cn.lanink.autoresourcechest

import cn.lanink.autoresourcechest.chest.Chest
import cn.nukkit.blockentity.BlockEntityChest
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerInteractEvent

/**
 * @author lt_name
 */
class OnListener(val autoResourceChest: AutoResourceChest) : Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player ?: return
        if (event.block == null) {
            return
        }
        val entity = event.block.level?.getBlockEntity(event.block)
        if (entity != null && entity is BlockEntityChest) {
            val chest: Chest = this.autoResourceChest.getChestByPos(entity) ?: return
            if (chest.isCanOpen) {
                chest.isCanOpen = false
            }else {
                event.setCancelled()
                player.sendMessage("§e>> §c请等待箱子刷新！")
            }
        }
    }

}