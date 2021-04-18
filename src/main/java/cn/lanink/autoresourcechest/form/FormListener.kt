package cn.lanink.autoresourcechest.form

import cn.lanink.autoresourcechest.AutoResourceChest
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerFormRespondedEvent
import cn.nukkit.form.response.FormResponseCustom

/**
 * @author lt_name
 */
class FormListener(val autoResourceChest: AutoResourceChest) : Listener {

    @EventHandler
    fun onPlayerFormResponded(event: PlayerFormRespondedEvent) {
        val player = event.player ?: return
        val formType = FormCreate.FORM_CACHE[player]?.get(event.formID) ?: return
        FormCreate.FORM_CACHE[player]?.remove(event.formID)

        val window = event.window ?: return
        val response = event.response ?: return
        if (response is FormResponseCustom) {
            if (formType == FormCreate.FormType.CHEST_CONFIG_MENU) {
                //TODO

            }
        }
    }

}