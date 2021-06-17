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

    private val item: Item
    private var probability = 50

    init {
        val split = string.split("&")
        val split2 = split[1].split("@")
        item = Item.fromString(split[0])
        item.setCount(split2[0].toInt())
        probability = split2[1].toInt()
    }

    fun getItem(): Item {
        return if (RANDOM.nextInt(100) < probability) item.clone() else Item.get(0);
    }

}