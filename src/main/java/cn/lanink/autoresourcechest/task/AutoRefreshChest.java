package cn.lanink.autoresourcechest.task;

import cn.lanink.autoresourcechest.AutoResourceChest;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.PluginTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class AutoRefreshChest extends PluginTask<AutoResourceChest> {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public AutoRefreshChest(AutoResourceChest owner) {
        super(owner);
    }

    @Override
    public void onRun(int i) {
        for (String worldName : this.owner.getWorlds()) {
            Level level = this.owner.getServer().getLevelByName(worldName);
            if (level == null) {
                continue;
            }
            for (BlockEntity blockEntity : level.getBlockEntities().values()) {
                if (blockEntity instanceof BlockEntityChest) {
                    BlockEntityChest chest = (BlockEntityChest) blockEntity;
                    String stringTime = this.owner.getChestLastRefreshTime()
                            .getOrDefault(chest.getLevel(), new ConcurrentHashMap<>())
                            .getOrDefault(chest.floor(), "2000-01-01");
                    Date nowTime = new Date();
                    Date lastTime = new Date();
                    try {
                        lastTime = simpleDateFormat.parse(stringTime);
                    } catch (Exception e) {
                        this.owner.getChestLastRefreshTime()
                                .getOrDefault(chest.getLevel(), new ConcurrentHashMap<>())
                                .remove(chest.floor());
                        e.printStackTrace();
                    }
                    long l = (nowTime.getTime() - lastTime.getTime()) / 60000;
                    if (l > this.owner.getRefreshInterval()) {
                        //TODO 检查箱子是否能放下所有东西
                        chest.getInventory().clearAll();
                        chest.getInventory().addItem(this.owner.getRandomItems());
                        stringTime = simpleDateFormat.format(nowTime);
                        this.owner.getChestLastRefreshTime()
                                .computeIfAbsent(chest.getLevel(), f -> new ConcurrentHashMap<>())
                                .put(chest.floor(), stringTime);
                    }
                }
            }
        }
    }


}
