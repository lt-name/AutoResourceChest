package cn.lanink.autoresourcechest.chest

import cn.lanink.autoresourcechest.item.RandomItem
import cn.nukkit.Server
import cn.nukkit.item.Item
import cn.nukkit.level.Position
import cn.nukkit.utils.Config
import lombok.EqualsAndHashCode

/**
 * @author lt_name
 */
@EqualsAndHashCode
class ChestManager(val name: String, config: Config) {

    val showName: String = config.getString("showName")
    val refreshInterval: Int = config.getInt("刷新间隔(s)")
    private val restrictOpenCount: Int = config.getInt("限制打开次数", -1)
    private val maxRandomItemCount: Int = config.getInt("生成随机物品数量限制")
    private var fixedItems: ArrayList<Item> = ArrayList()
    var randomItems = ArrayList<RandomItem>()
    val chests: MutableMap<Position, Chest> = HashMap()

    init {
        for (stringItem in config.getStringList("fixedItem")) {
            val split = stringItem.split("&")
            val item = Item.fromString(split[0])
            item.setCount(split[1].toInt())
            fixedItems.add(item)
        }
        for (stringItem in config.getStringList("randomItem")) {
            randomItems.add(RandomItem(stringItem!!))
        }
        for (pos in config.getStringList("pos")) {
            val split = pos.split(":")
            val position = Position(
                split[0].toInt().toDouble(), split[1].toInt().toDouble(), split[2].toInt().toDouble(),
                Server.getInstance().getLevelByName(split[3])
            )
            chests[position] = Chest(this, position);
        }
    }

    fun getFixedItems(): List<Item> {
        return ArrayList<Item>(fixedItems)
    }

    fun getRandomItems(): List<Item> {
        val list = ArrayList<Item>()
        for (randomItem in this.randomItems) {
            val item = randomItem.getItem()
            if (item != null) {
                list.add(item.clone())
                if (list.size >= this.maxRandomItemCount) {
                    break
                }
            }
        }
        return list
    }

    fun getChestByPos(position: Position): Chest? {
        //我们需要比对level
        for ((key, value) in this.chests.entries) {
            if (key.getLevel() === position.getLevel() && key == position) {
                return value
            }
        }
        return null
    }

}