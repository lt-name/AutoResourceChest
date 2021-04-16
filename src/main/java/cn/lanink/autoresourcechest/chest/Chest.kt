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
class Chest(val chestManager: ChestManager, private var position: Position) {

    private var closed: Boolean = false
    var time = 0
    private var text: EntityText
    var isNeedRefresh = false

    init {
        this.text = EntityText(this.position)
    }

    fun onUpdate() {
        if (this.closed) {
            return
        }
        if (this.isNeedRefresh) {
            this.time--
        }
        if (this.time <= 0) {
            this.time = this.chestManager.refreshInterval
            this.refreshInventory()
            this.isNeedRefresh = false
        }
        if (this.text.isClosed) {
            this.text = EntityText(this.position)
        }
        //仅调整浮空字的位置
        this.text.setPosition(position.add(0.5, 1.0, 0.5))
        if (this.isNeedRefresh) {
            this.text.nameTag = chestManager.showName.replace("%time%", formatTime(time))
        }else {
            this.text.nameTag = chestManager.showName.replace("%time%", "已刷新")
        }
        for (player in this.text.getLevel().players.values) {
            if (player.getLevel() !== this.text.getLevel() || player.distance(this.position) > 5) {
                this.text.despawnFrom(player)
            } else {
                this.text.spawnTo(player)
            }
        }
    }

    private fun refreshInventory() {
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

    fun close() {
        this.closed = true
        this.text.close()
    }

}