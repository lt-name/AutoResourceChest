package cn.lanink.autoresourcechest

import cn.lanink.autoresourcechest.chest.Chest
import cn.lanink.autoresourcechest.form.FormCreate
import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.Block
import cn.nukkit.block.BlockChest
import cn.nukkit.blockentity.BlockEntity
import cn.nukkit.blockentity.BlockEntityChest
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.inventory.InventoryTransactionEvent
import cn.nukkit.event.player.PlayerInteractEvent
import cn.nukkit.event.player.PlayerQuitEvent
import cn.nukkit.inventory.ChestInventory
import cn.nukkit.inventory.transaction.action.SlotChangeAction
import cn.nukkit.level.Position
import cn.nukkit.math.BlockFace

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
        if (!this.autoResourceChest.isSupportChest(block)) {
            return
        }

        //阻止大箱子
        if (block is BlockChest) {
            val face = intArrayOf(2, 5, 3, 4)[player.direction.horizontalIndex]
            for (side in 2..5) {
                if ((face == 4 || face == 5) && (side == 4 || side == 5)) {
                    continue
                } else if ((face == 3 || face == 2) && (side == 2 || side == 3)) {
                    continue
                }
                val c: Block = block.getSide(BlockFace.fromIndex(side))
                if (c is BlockChest && c.getDamage() == face) {
                    val blockEntity: BlockEntity = block.level.getBlockEntity(c)
                    if (blockEntity is BlockEntityChest && !blockEntity.isPaired) {
                        event.setCancelled()
                        player.sendMessage("§e>> §c资源箱不能合并为大箱子！")
                        return
                    }
                }
            }
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
        if (this.autoResourceChest.isSupportChest(block)) {
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
        if (this.autoResourceChest.isSupportChest(block) && event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            val chest: Chest = this.autoResourceChest.getChestByPos(block) ?: return
            val playerConfig = this.autoResourceChest.playerConfigManager.getPlayerConfig(player)
            if (player.isSneaking && player.isOp) {
                FormCreate.sendChestSetMenu(player, chest.chestManager)
                event.setCancelled()
                return
            }
            if (chest.chestManager.restrictOpenCount > 0) {
                if (playerConfig.getOpenCount(block) >= chest.chestManager.restrictOpenCount) {
                    event.setCancelled()
                    player.sendMessage("§e>> §c您已达到限制打开次数！")
                    return
                }
            }
            if (chest.isNeedRefresh) {
                val entity = block.level.getBlockEntity(block)
                if (chest.time <= chest.chestManager.refreshInterval/2 ||
                    (entity is BlockEntityChest && entity.inventory.isEmpty)) {
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

    @EventHandler
    fun onInventoryTransaction(event: InventoryTransactionEvent) {
        val transaction = event.transaction
        for (inv in transaction.inventories) {
            if (inv is ChestInventory) {
                val chest: Chest = this.autoResourceChest.getChestByPos(inv.holder) ?: return
                if (chest.chestManager.canBePutIn) {
                    return
                }
                for (action in transaction.actions) {
                    if (action is SlotChangeAction) {
                        if (action.inventory == inv && action.targetItem.id != 0) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }

}