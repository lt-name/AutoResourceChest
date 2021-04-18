package cn.lanink.autoresourcechest.form

import cn.nukkit.Player
import cn.nukkit.form.window.FormWindow
import cn.nukkit.form.window.FormWindowCustom

/**
 * @author lt_name
 */
class FormCreate {

    companion object {

        val FORM_CACHE = HashMap<Player, HashMap<Int, FormType>>()

        fun sendChestConfigMenu(player: Player) {
            val custom = FormWindowCustom("设置资源想配置")
            //TODO

            showFormWindow(player, custom, FormType.CHEST_CONFIG_MENU)
        }

        fun showFormWindow(player: Player, window: FormWindow, formType: FormType) {
            FORM_CACHE.computeIfAbsent(player) { HashMap() }[player.showFormWindow(window)] = formType
        }

    }

    enum class FormType {
        CHEST_CONFIG_MENU

    }

}