package cn.lanink.autoresourcechest.item;

import cn.lanink.autoresourcechest.AutoResourceChest;
import cn.nukkit.item.Item;

import java.util.Objects;

/**
 * @author lt_name
 */
public class RandomItem {

    public Item item;
    public int minCount = 1;
    public int maxCount = 1;
    public int probability = 50;

    public RandomItem(String string) throws Exception {
        String[] split = string.split("&");
        if (split.length < 3) {
            return;
        }
        String[] stringID = split[0].split(":");
        int id = Integer.parseInt(stringID[0]);
        int meta = 0;
        if (stringID.length > 1) {
            meta = Integer.parseInt(stringID[1]);
        }
        this.item = Item.get(id, meta);
        String[] stringCount = split[1].split("-");
        this.minCount = Integer.parseInt(stringCount[0]);
        if (stringCount.length > 1) {
            this.maxCount = Integer.parseInt(stringCount[1]);
        } else {
            this.maxCount = this.minCount;
        }
        this.probability = Integer.parseInt(split[2]);
    }

    public Item getItem() {
        if (AutoResourceChest.RANDOM.nextInt(100) < this.probability) {
            int count = this.minCount + AutoResourceChest.RANDOM.nextInt(Math.max(this.maxCount - this.minCount, 1));
            Item item = this.item.clone();
            item.setCount(count);
            return item;
        }
        return Item.get(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RandomItem that = (RandomItem) o;
        return minCount == that.minCount && maxCount == that.maxCount &&
                probability == that.probability && Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, minCount, maxCount, probability);
    }

}
