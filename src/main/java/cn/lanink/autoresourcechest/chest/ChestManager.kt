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
class ChestManager(val name: String, private val config: Config) {

    val showName: String = config.getString("showName")
    val refreshInterval: Int = config.getInt("刷新间隔(s)")
    private val restrictOpenCount: Int = config.getInt("限制打开次数", -1)
    private val maxRandomItemCount: Int = config.getInt("生成随机物品数量限制")
    private var fixedItems: ArrayList<Item> = ArrayList()
    private var randomItems = ArrayList<RandomItem>()
    val chests: MutableMap<Position, Chest> = HashMap()

    init {
        for (stringItem in config.getStringList("fixedItem")) {
            val split = stringItem.split("&")
            val item = Item.fromString(split[0])
            item.setCount(split[1].toInt())
            this.fixedItems.add(item)
        }
        for (stringItem in config.getStringList("randomItem")) {
            this.randomItems.add(RandomItem(stringItem!!))
        }
        for (pos in config.getStringList("pos")) {
            val split = pos.split(":")
            val position = Position(
                split[0].toDouble(), split[1].toDouble(), split[2].toDouble(),
                Server.getInstance().getLevelByName(split[3])
            )
            this.chests[position] = Chest(this, position)
        }
    }

    fun saveConfig() {
        val list = mutableListOf<String>()
        for (pos in this.chests.keys) {
            list.add("${pos.x}:${pos.y}:${pos.z}:${pos.level.name}")
        }
        this.config.set("pos", list)
        this.config.save()
    }

    fun addNewChest(position: Position): Boolean {
        for (pos in this.chests.keys) {
            if (pos.getLevel() === position.getLevel() && pos == position) {
                return false
            }
        }
        val newPos = position.clone()
        this.chests[newPos] = Chest(this, newPos)
        return true
    }

    fun removeChest(chest: Chest): Boolean {
        for ((key, value) in HashMap(this.chests).entries) {
            if (value == chest) {
                this.chests.remove(key)
                value.close()
                return true
            }
        }
        return false
    }

    fun closeAllChest() {
        this.chests.values.forEach {
            chest -> chest.close()
        }
        this.chests.clear()
    }

    fun getFixedItems(): List<Item> {
        return ArrayList<Item>(this.fixedItems)
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
        //需要判断level
        for ((key, value) in this.chests.entries) {
            if (key.getLevel() === position.getLevel() && key == position) {
                return value
            }
        }
        return null
    }

}