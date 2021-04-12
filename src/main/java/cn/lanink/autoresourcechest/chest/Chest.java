package cn.lanink.autoresourcechest.chest;

import cn.lanink.autoresourcechest.entity.EntityText;
import cn.lanink.autoresourcechest.utils.Utils;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.level.Position;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * @author lt_name
 */
@EqualsAndHashCode
public class Chest {

    private final ChestManager chestManager;
    private final Position position;

    private int time;

    private EntityText text;

    private boolean canOpen = true;

    public Chest(@NotNull ChestManager chestManager, @NotNull Position position) {
        this.chestManager = chestManager;
        this.position = position;
        this.text = new EntityText(position);
    }

    public void onUpdate() {
        this.time--;
        if (this.time <= 0) {
            this.time = this.chestManager.getRefreshInterval();
            this.refreshInventory();
            this.setCanOpen(true);
        }

        if (this.text == null || this.text.isClosed()) {
            this.text = new EntityText(position);
        }
        this.text.setPosition(this.position.add(0.5, 1, 0.5));
        this.text.setNameTag(this.chestManager.getShowName()
                .replace("%time%", Utils.formatTime(this.time)));

        for (Player player : this.text.getLevel().getPlayers().values()) {
            if (player.getLevel() != this.text.getLevel() || player.distance(this.position) > 5) {
                this.text.despawnFrom(player);
            }else {
                this.text.spawnTo(player);
            }
        }
    }

    public void refreshInventory() {
        BlockEntity blockEntity = this.position.getLevel().getBlockEntity(this.position);
        if (!(blockEntity instanceof BlockEntityChest)) {
            this.position.getLevel().setBlock(this.position, Block.get(Block.CHEST));
            blockEntity = this.position.getLevel().getBlockEntity(this.position);
            if (!(blockEntity instanceof BlockEntityChest)) {
                return;
            }
        }
        BaseInventory inventory = ((BlockEntityChest) blockEntity).getInventory();
        inventory.clearAll();
        inventory.addItem(this.chestManager.getFixedItems());
        inventory.addItem(this.chestManager.getRandomItems());
    }

    public boolean isCanOpen() {
        return canOpen;
    }

    public void setCanOpen(boolean canOpen) {
        this.canOpen = canOpen;
    }

}
