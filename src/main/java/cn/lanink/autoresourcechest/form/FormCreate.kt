package cn.lanink.autoresourcechest.form

import cn.lanink.autoresourcechest.chest.ChestManager
import cn.nukkit.Player
import cn.nukkit.form.element.ElementInput
import cn.nukkit.form.window.FormWindow
import cn.nukkit.form.window.FormWindowCustom

/**
 * @author lt_name
 */
class FormCreate {

    companion object {

        val FORM_CACHE = HashMap<Player, HashMap<Int, FormType>>()

        fun sendChestConfigMenu(player: Player, chestManager: ChestManager) {
            val custom = FormWindowCustom("设置资源想配置")
            custom.addElement(ElementInput("显示名称", "", "§a这是一个测试资源箱\n§a将在: §e%time% §a后刷新"))
            custom.addElement(ElementInput("刷新间隔(秒)", "", "10"))
            custom.addElement(ElementInput("限制打开次数", "", "-1"))
            custom.addElement(ElementInput("随机物品种类数量限制", "", "3"))
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