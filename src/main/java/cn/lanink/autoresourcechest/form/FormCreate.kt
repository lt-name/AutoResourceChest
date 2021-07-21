package cn.lanink.autoresourcechest.form

import cn.lanink.autoresourcechest.chest.ChestManager
import cn.lanink.autoresourcechest.form.element.ResponseElementButton
import cn.lanink.autoresourcechest.form.windows.AdvancedFormWindowCustom
import cn.lanink.autoresourcechest.form.windows.AdvancedFormWindowSimple
import cn.nukkit.Player
import cn.nukkit.form.element.ElementInput

/**
 * @author lt_name
 */
class FormCreate {

    companion object {

        fun sendChestSetMenu(player: Player, chestManager: ChestManager) {
            val simple = AdvancedFormWindowSimple("设置资源箱")
            simple.addButton(ResponseElementButton("设置基础配置").onClicked{p -> sendChestSetConfig(p, chestManager)})
            //TODO

            player.showFormWindow(simple)
        }

        fun sendChestSetConfig(player: Player, chestManager: ChestManager) {
            val custom = AdvancedFormWindowCustom("设置资源箱配置")
            custom.addElement(ElementInput("显示名称", "§a这是一个测试资源箱\n§a将在: §e%time% §a后刷新", chestManager.showName))
            custom.addElement(ElementInput("刷新间隔(秒)", "60", chestManager.refreshInterval.toString()))
            custom.addElement(ElementInput("限制打开次数", "-1", chestManager.restrictOpenCount.toString()))
            custom.addElement(ElementInput("随机物品种类数量限制", "3", chestManager.maxRandomItemCount.toString()))
            custom.onResponded { res, p ->
                chestManager.showName = res.getInputResponse(0)
                chestManager.refreshInterval = res.getInputResponse(1).toInt()
                chestManager.restrictOpenCount = res.getInputResponse(2).toInt()
                chestManager.maxRandomItemCount = res.getInputResponse(3).toInt()
                chestManager.saveConfig()
                p.sendMessage("${chestManager.name} 设置已保存")
            }
            player.showFormWindow(custom)
        }

    }


}