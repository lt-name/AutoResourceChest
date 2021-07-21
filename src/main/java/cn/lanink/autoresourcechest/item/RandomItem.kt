package cn.lanink.autoresourcechest.item

import cn.lanink.autoresourcechest.AutoResourceChest.Companion.RANDOM
import cn.nukkit.item.Item
import lombok.AllArgsConstructor
import lombok.EqualsAndHashCode

/**
 * @author lt_name
 */
@AllArgsConstructor
@EqualsAndHashCode
class RandomItem(string: String) {

    var item: Item
    var probability = 50

    init {
        val split = string.split("&")
        val split2 = split[1].split("@")
        this.item = Item.fromString(split[0])
        this.item.setCount(split2[0].toInt())
        this.probability = split2[1].toInt()
    }

    fun getRandomItem(): Item {
        return if (RANDOM.nextInt(100) < probability) item.clone() else Item.get(0)
    }

    override fun toString(): String {
        return "${this.item.id}:${this.item.damage}&${this.item.count}@${this.probability}"
    }

}