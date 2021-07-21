package cn.lanink.autoresourcechest.form.windows

import cn.lanink.autoresourcechest.AutoResourceChest.Companion.GSON
import cn.lanink.autoresourcechest.form.element.ResponseElementButton
import cn.nukkit.Player
import cn.nukkit.form.element.ElementButton
import cn.nukkit.form.window.FormWindow
import cn.nukkit.form.window.FormWindowSimple
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * @author lt_name
 */
class AdvancedFormWindowSimple : FormWindowSimple {

    private var buttonClickedListener: BiConsumer<ElementButton, Player>? = null
    private var formClosedListener: Consumer<Player>? = null

    constructor(title: String, content: String = "") : super(title, content)
    constructor(title: String, content: String, buttons: List<ElementButton>) : super(title, content, buttons)

    fun addButton(text: String, listener: Consumer<Player>) {
        this.addButton(ResponseElementButton(text).onClicked(listener))
    }

    fun onClicked(listener: BiConsumer<ElementButton, Player>): AdvancedFormWindowSimple {
        this.buttonClickedListener = listener
        return this
    }

    fun onClosed(listener: Consumer<Player>): AdvancedFormWindowSimple {
        this.formClosedListener = listener
        return this
    }

    private fun callClicked(elementButton: ElementButton, player: Player) {
        this.buttonClickedListener?.accept(elementButton, player)
    }

    private fun callClosed(player: Player) {
        this.formClosedListener?.accept(player)
    }

    override fun getJSONData(): String {
        return GSON.toJson(this, FormWindowSimple::class.java)
    }

    companion object {
        fun onEvent(formWindow: FormWindow, player: Player): Boolean {
            if (formWindow is AdvancedFormWindowSimple) {
                if (formWindow.wasClosed() || formWindow.response == null) {
                    formWindow.callClosed(player)
                } else {
                    val elementButton = formWindow.response.clickedButton
                    if (elementButton is ResponseElementButton && elementButton.callClicked(player)) {
                        return true
                    } else {
                        formWindow.callClicked(elementButton, player)
                    }
                }
                return true
            }
            return false
        }
    }
}