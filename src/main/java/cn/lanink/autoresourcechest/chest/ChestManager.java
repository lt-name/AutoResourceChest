package cn.lanink.autoresourcechest.chest;

import cn.lanink.autoresourcechest.item.RandomItem;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lt_name
 */
@EqualsAndHashCode
@Getter
public class ChestManager {

    private final String name;

    private final String showName;
    private final int refreshInterval;
    private final int restrictOpenCount;
    private final int maxRandomItemCount;

    public Item[] fixedItems;
    public ArrayList<RandomItem> randomItems = new ArrayList<>();

    private final Map<Position, Chest> chests = new HashMap<>();

    public ChestManager(@NotNull String name, @NotNull Config config) {
        this.name = name;

        this.showName = config.getString("showName");
        this.refreshInterval = config.getInt("刷新间隔(s)");
        this.restrictOpenCount = config.getInt("限制打开次数", -1);
        this.maxRandomItemCount = config.getInt("生成随机物品数量限制");

        ArrayList<Item> list = new ArrayList<>();
        for (String stringItem : config.getStringList("fixedItem")) {
            String[] split = stringItem.split("&");
            Item item = Item.fromString(split[0]);
            item.setCount(Integer.parseInt(split[1]));
            list.add(item);
        }
        this.fixedItems = list.toArray(new Item[0]);

        for (String stringItem : config.getStringList("randomItem")) {
            this.randomItems.add(new RandomItem(stringItem));
        }

        for (String pos : config.getStringList("pos")) {
            String[] split = pos.split(":");
            Position position = new Position(Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]),
                    Server.getInstance().getLevelByName(split[3]));
            this.chests.put(position, new Chest(this, position));
        }

    }

    public Item[] getFixedItems() {
        return this.fixedItems.clone();
    }

    public Item[] getRandomItems() {
        ArrayList<Item> list = new ArrayList<>();
        for (RandomItem randomItem : this.randomItems) {
            Item item = randomItem.getItem();
            if (item != null) {
                list.add(item);
                if (list.size() >= this.getMaxRandomItemCount()) {
                    break;
                }
            }
        }
        return list.toArray(new Item[0]);
    }

}
