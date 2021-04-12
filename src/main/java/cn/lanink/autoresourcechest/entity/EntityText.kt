package cn.lanink.autoresourcechest.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author lt_name
 */
public class EntityText extends Entity {

    @Override
    public int getNetworkId() {
        return 64;
    }

    @Deprecated
    public EntityText(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.close();
    }

    public EntityText(Position position) {
        super(position.getChunk(), getDefaultNBT(position));
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setNameTag("");
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setImmobile();
    }

}