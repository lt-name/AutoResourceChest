package cn.lanink.autoresourcechest.item;

import cn.lanink.autoresourcechest.AutoResourceChest;
import cn.nukkit.item.Item;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * @author lt_name
 */
@AllArgsConstructor
@EqualsAndHashCode
public class RandomItem {

    private final Item item;
    private int probability = 50;

    public RandomItem(String string) {
        String[] split = string.split("&");
        String[] split2 = split[1].split("@");
        this.item = Item.fromString(split[0]);
        this.item.setCount(Integer.parseInt(split2[0]));

        this.probability = Integer.parseInt(split2[1]);
    }

    public Item getItem() {
        if (AutoResourceChest.RANDOM.nextInt(100) < this.probability) {
            return this.item.clone();
        }
        return null;
    }

}
