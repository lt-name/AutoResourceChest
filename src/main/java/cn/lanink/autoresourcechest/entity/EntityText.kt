package cn.lanink.autoresourcechest.entity

import cn.nukkit.entity.Entity
import cn.nukkit.level.Position
import cn.nukkit.level.format.FullChunk
import cn.nukkit.nbt.tag.CompoundTag

/**
 * @author lt_name
 */
class EntityText : Entity {

    override fun getNetworkId(): Int {
        return 64
    }

    @Deprecated("只是为了兼容PN核心")
    constructor(chunk: FullChunk?, nbt: CompoundTag?) : super(chunk, nbt) {
        close()
    }

    constructor(position: Position) : super(position.chunk, getDefaultNBT(position))

    override fun initEntity() {
        super.initEntity()
        this.nameTag = ""
        this.isNameTagVisible = true
        this.isNameTagAlwaysVisible = true
        this.setImmobile()
    }

}