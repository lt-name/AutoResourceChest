package cn.lanink.autoresourcechest.form

import cn.lanink.autoresourcechest.chest.ChestManager
import cn.lanink.autoresourcechest.form.element.ResponseElementButton
import cn.lanink.autoresourcechest.form.windows.AdvancedFormWindowCustom
import cn.lanink.autoresourcechest.form.windows.AdvancedFormWindowModal
import cn.lanink.autoresourcechest.form.windows.AdvancedFormWindowSimple
import cn.nukkit.Player
import cn.nukkit.form.element.ElementInput
import cn.nukkit.item.Item

/**
 * @author lt_name
 */
class FormCreate {

    companion object {

        fun sendChestSetMenu(player: Player, chestManager: ChestManager) {
            val simple = AdvancedFormWindowSimple("设置资源箱")
            simple.addButton(ResponseElementButton("设置基础配置").onClicked{p -> sendChestSetConfig(p, chestManager)})
            simple.addButton(ResponseElementButton("设置固定刷新物品").onClicked{p -> sendChestSetFixedItem(p, chestManager)})
            //TODO

            player.showFormWindow(simple)
        }

        fun sendChestSetConfig(player: Player, chestManager: ChestManager) {
            val custom = AdvancedFormWindowCustom("设置资源箱基础配置")
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

        fun sendChestSetFixedItem(player: Player, chestManager: ChestManager) {
            val simple = AdvancedFormWindowSimple("设置资源箱固定刷新物品")
            simple.addButton(ResponseElementButton("添加新物品").onClicked{p -> sendChestSetAddFixedItem(p, chestManager)})
            for (item: Item in chestManager.getFixedItems()) {
                val text = "物品ID： ${item.id}:${item.damage} 数量： ${item.count}"
                simple.addButton(ResponseElementButton(text)
                    .onClicked{ cp ->
                        val modal = AdvancedFormWindowModal(
                            "删除固定刷新物品",
                            "确定要删除固定刷新物品 $text 吗？",
                            "确定",
                            "取消"
                        )
                        modal.onClickedTrue{cp2 ->
                            chestManager.fixedItems.remove(item)
                            chestManager.saveConfig()
                            val m = AdvancedFormWindowModal(
                                "删除成功",
                                "已成功删除固定刷新物品 $text !",
                                "确定",
                                "关闭"
                            )
                            m.onClickedTrue{cp3 -> sendChestSetFixedItem(cp3, chestManager)}
                            cp2.showFormWindow(m)
                        }
                        modal.onClickedFalse{cp2 -> sendChestSetFixedItem(cp2, chestManager)}
                        cp.showFormWindow(modal)
                    })
            }
            player.showFormWindow(simple)
        }

        fun sendChestSetAddFixedItem(player: Player, chestManager: ChestManager) {
            val custom = AdvancedFormWindowCustom("添加资源箱固定刷新物品")
            custom.addElement(ElementInput("物品ID", "id", "1"))
            custom.addElement(ElementInput("物品特殊值", "特殊值", "0"))
            custom.addElement(ElementInput("数量", "1", "1"))
            custom.onResponded{res, cp ->
                val id = res.getInputResponse(0).toInt()
                val damage = res.getInputResponse(1).toInt()
                val count = res.getInputResponse(2).toInt()
                val item = Item.get(id, damage, count)
                var isNewItem = true
                for (i : Item in chestManager.fixedItems) {
                    if (id == i.id && damage == i.damage) {
                        isNewItem = false
                        i.setCount(count)
                        break
                    }
                }
                val modal = AdvancedFormWindowModal(
                    "修改资源箱固定刷新物品成功",
                    "已经物品：物品 ID：${item.id}:${item.damage} 数量设置为： ${item.count}",
                    "确定",
                    "关闭"
                )
                if (isNewItem) {
                    chestManager.fixedItems.add(item)
                    modal.title = "添加资源箱固定刷新物品成功"
                    modal.content = "已成功添加物品：\n物品ID： ${item.id}:${item.damage} 数量： ${item.count}"
                }
                chestManager.saveConfig()
                modal.onClickedTrue{cp2 -> sendChestSetFixedItem(cp2, chestManager)}
                cp.showFormWindow(modal)
            }
            custom.onClosed{cp -> sendChestSetFixedItem(cp, chestManager)}
            player.showFormWindow(custom)
        }

    }


}