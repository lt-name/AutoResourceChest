package cn.lanink.autoresourcechest.item

import cn.lanink.autoresourcechest.AutoResourceChest
import cn.lanink.autoresourcechest.utils.Utils
import cn.nukkit.item.Item

/**
 * @author lt_name
 */
class FixedItem: BaseItem {

    constructor(string: String) {
        val split = string.split("&")
        val split1 = split[0].split(":")
        if (split1[1] == "nbt") {
            this.nbtItemName = split1[0]
            val nbtItemString = AutoResourceChest.instance?.getNbtConfig()?.getString(this.nbtItemName)
            if (nbtItemString == null || nbtItemString == "") {
                AutoResourceChest.instance?.logger?.error("NBT物品：${this.nbtItemName} 配置不存在，无法加载！")
                return
            }
            val split2 = nbtItemString.split(":")
            this.item = Item.fromString("${split2[0]}:${split2[1]}")
            this.item.compoundTag = Utils.base64ToBytes(split2[2])
        } else {
            this.item = Item.fromString(split[0])
        }
        this.item.setCount(split[1].toInt())
    }

    constructor(item: Item) {
        this.item = item
    }

    override fun toString(): String {
        if (this.isNbtItem()) {
            return "${this.nbtItemName}:nbt&${this.item.count}"
        }else{
            return "${this.item.id}:${this.item.damage}&${this.item.count}"
        }
    }

}