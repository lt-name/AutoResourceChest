package cn.lanink.autoresourcechest.form

import cn.lanink.autoresourcechest.form.windows.AdvancedFormWindowCustom
import cn.lanink.autoresourcechest.form.windows.AdvancedFormWindowModal
import cn.lanink.autoresourcechest.form.windows.AdvancedFormWindowSimple
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerFormRespondedEvent

/**
 * @author lt_name
 */
class FormListener : Listener {

    @EventHandler
    fun onPlayerFormResponded(event: PlayerFormRespondedEvent) {
        if (AdvancedFormWindowSimple.onEvent(event.window, event.player)) {
            return
        }
        if (AdvancedFormWindowModal.onEvent(event.window, event.player)) {
            return
        }
        AdvancedFormWindowCustom.onEvent(event.window, event.player)
    }

}