package cn.lanink.autoresourcechest;

import cn.lanink.autoresourcechest.item.RandomItem;
import cn.lanink.autoresourcechest.task.AutoRefreshChest;
import cn.nukkit.item.Item;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class AutoResourceChest extends PluginBase {

    public static final Random RANDOM = new Random();
    private static AutoResourceChest autoResourceChest;
    private Config config;

    private int refreshInterval = 60;
    private int maximumNumberOfItems = 16;

    private final ArrayList<String> worlds = new ArrayList<>(); //刷新的世界
    private final ArrayList<RandomItem> randomItems = new ArrayList<>();
    private final ConcurrentHashMap<Level, ConcurrentHashMap<Vector3, String>> chestLastRefreshTime = new ConcurrentHashMap<>();


    public static AutoResourceChest getInstance() {
        return autoResourceChest;
    }

    @Override
    public void onLoad() {
        autoResourceChest = this;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.config = this.getConfig();
        this.worlds.addAll(this.config.getStringList("enableWorlds"));
        this.refreshInterval = this.config.getInt("refreshInterval", 60);
        this.maximumNumberOfItems = this.config.getInt("maximumNumberOfItems", 64);

        for (String string : this.config.getStringList("randomItems")) {
            try {
                this.randomItems.add(new RandomItem(string));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.getServer().getScheduler().scheduleRepeatingTask(this, new AutoRefreshChest(this), 100);
    }

    public ArrayList<String> getWorlds() {
        return this.worlds;
    }

    public int getRefreshInterval() {
        return this.refreshInterval;
    }

    public int getMaximumNumberOfItems() {
        return this.maximumNumberOfItems;
    }

    public Item[] getRandomItems() {
        return this.getRandomItems(this.maximumNumberOfItems);
    }

    public Item[] getRandomItems(int count) {
        Collections.shuffle(this.randomItems, AutoResourceChest.RANDOM);
        ArrayList<Item> items = new ArrayList<>();
        int nowCount = 0;
        for (RandomItem randomItem : this.randomItems) {
            Item item = randomItem.getItem();
            if (item.getId() != 0) {
                if (item.getCount() >= count) {
                    item.setCount(Math.max(count/3, 1));
                }
                items.add(item);
                nowCount += item.getCount();
            }
            if (nowCount >= count) {
                break;
            }
        }
        return items.toArray(new Item[0]);
    }

    public ConcurrentHashMap<Level, ConcurrentHashMap<Vector3, String>> getChestLastRefreshTime() {
        return this.chestLastRefreshTime;
    }

}
