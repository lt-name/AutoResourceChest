package cn.lanink.autoresourcechest.chest

import cn.lanink.autoresourcechest.entity.EntityText
import cn.lanink.autoresourcechest.utils.Utils.Companion.formatTime
import cn.nukkit.block.Block
import cn.nukkit.blockentity.BlockEntityChest
import cn.nukkit.level.Position
import lombok.EqualsAndHashCode

/**
 * @author lt_name
 */
@EqualsAndHashCode
class Chest(private val chestManager: ChestManager, private val position: Position) {

    private var time = 0
    private var text: EntityText?
    var isCanOpen = true

    init {
        text = EntityText(position)
    }

    fun onUpdate() {
        time--
        if (time <= 0) {
            time = chestManager.refreshInterval
            refreshInventory()
            isCanOpen = true
        }
        if (text == null || text!!.isClosed) {
            text = EntityText(position)
        }
        text!!.setPosition(position.add(0.5, 1.0, 0.5))
        text!!.nameTag = chestManager.showName
            .replace("%time%", formatTime(time))
        for (player in text!!.getLevel().players.values) {
            if (player.getLevel() !== text!!.getLevel() || player.distance(position) > 5) {
                text!!.despawnFrom(player)
            } else {
                text!!.spawnTo(player)
            }
        }
    }

    fun refreshInventory() {
        var blockEntity = position.getLevel().getBlockEntity(position)
        if (blockEntity !is BlockEntityChest) {
            position.getLevel().setBlock(position, Block.get(Block.CHEST))
            blockEntity = position.getLevel().getBlockEntity(position)
            if (blockEntity !is BlockEntityChest) {
                return
            }
        }
        val inventory = blockEntity.inventory
        inventory.clearAll()
        inventory.addItem(*chestManager.getFixedItems().toTypedArray())
        inventory.addItem(*chestManager.getRandomItems().toTypedArray())
    }

}