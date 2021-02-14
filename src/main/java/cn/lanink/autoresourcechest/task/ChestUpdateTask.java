package cn.lanink.autoresourcechest.task;

import cn.lanink.autoresourcechest.AutoResourceChest;
import cn.lanink.autoresourcechest.chest.Chest;
import cn.lanink.autoresourcechest.chest.ChestManager;
import cn.nukkit.scheduler.PluginTask;

/**
 * @author lt_name
 */
public class ChestUpdateTask extends PluginTask<AutoResourceChest> {

    public ChestUpdateTask(AutoResourceChest owner) {
        super(owner);
    }

    @Override
    public void onRun(int i) {
        for (ChestManager chestManager : this.owner.getChestConfigMap().values()) {
            for (Chest chest : chestManager.getChests().values()) {
                chest.onUpdate();
            }
        }
    }

}
