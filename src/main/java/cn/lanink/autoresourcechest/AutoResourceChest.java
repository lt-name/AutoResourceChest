package cn.lanink.autoresourcechest;

import cn.lanink.autoresourcechest.chest.ChestManager;
import cn.lanink.autoresourcechest.task.ChestUpdateTask;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author lt_name
 */
public class AutoResourceChest extends PluginBase {

    public static final Random RANDOM = new Random();
    public static final String VERSION = "0.0.2-Alpha git-417951a";
    public static boolean debug = false;

    private static AutoResourceChest autoResourceChest;

    @Getter
    private Config config;
    @Getter
    private Config playerLog;

    @Getter
    private Map<String, ChestManager> chestConfigMap = new HashMap<>();

    public static AutoResourceChest getInstance() {
        return autoResourceChest;
    }

    @Override
    public void onLoad() {
        autoResourceChest = this;

        this.saveDefaultConfig();
        this.saveResource("playerUseChestLog.yml");

        File file = new File(getDataFolder() + "/Chests");
        if (!file.exists() && !file.mkdirs()) {
            this.getLogger().error("Chests 文件夹初始化失败");
        }

        this.config = new Config(this.getDataFolder() + "/config.yml", Config.YAML);
        if (config.getBoolean("debug", false)) {
            debug = true;
            this.getLogger().warning("§c=========================================");
            this.getLogger().warning("§c 警告：您开启了debug模式！");
            this.getLogger().warning("§c Warning: You have turned on debug mode!");
            this.getLogger().warning("§c=========================================");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {

            }
        }
        this.playerLog = new Config(this.getDataFolder() + "/playerUseChestLog.yml", Config.YAML);
    }

    @Override
    public void onEnable() {
        this.loadAllChests();

        this.getServer().getScheduler().scheduleRepeatingTask(this,
                new ChestUpdateTask(this), 20);

        this.getLogger().info("加载完成！");
    }

    @Override
    public void onDisable() {
        //TODO
    }

    public void loadAllChests() {
        File[] files = (new File(getDataFolder() + "/Chests")).listFiles();
        int count = 0;
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (!file.isFile()) {
                    continue;
                }
                String name = file.getName().split("\\.")[0];
                this.getChestConfigMap().put(name,
                        new ChestManager(name, new Config(file, Config.YAML)));
                count++;
            }
        }
        this.getLogger().info("§a已加载 §e" + count + " §a个箱子配置");
    }

}
