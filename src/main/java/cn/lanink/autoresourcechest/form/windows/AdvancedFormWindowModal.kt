package cn.lanink.autoresourcechest.form.windows

import cn.lanink.autoresourcechest.AutoResourceChest.Companion.GSON
import cn.nukkit.Player
import cn.nukkit.form.window.FormWindow
import cn.nukkit.form.window.FormWindowModal
import java.util.function.Consumer

/**
 * @author lt_name
 */
class AdvancedFormWindowModal(title: String, content: String, trueButtonText: String, falseButtonText: String) : FormWindowModal(title, content, trueButtonText, falseButtonText) {

    private var buttonTrueClickedListener: Consumer<Player>? = null
    private var buttonFalseClickedListener: Consumer<Player>? = null
    private var formClosedListener: Consumer<Player>? = null

    fun onClickedTrue(listener: Consumer<Player>): AdvancedFormWindowModal {
        this.buttonTrueClickedListener = listener
        return this
    }

    fun onClickedFalse(listener: Consumer<Player>): AdvancedFormWindowModal {
        this.buttonFalseClickedListener = listener
        return this
    }

    fun onClosed(listener: Consumer<Player>): AdvancedFormWindowModal {
        this.formClosedListener = listener
        return this
    }

    private fun callClickedTrue(player: Player) {
        this.buttonTrueClickedListener?.accept(player)
    }

    private fun callClickedFalse(player: Player) {
        this.buttonFalseClickedListener?.accept(player)
    }

    private fun callClosed(player: Player) {
        this.formClosedListener?.accept(player)
    }

    override fun getJSONData(): String {
        return GSON.toJson(this, FormWindowModal::class.java)
    }

    companion object {
        fun onEvent(formWindow: FormWindow, player: Player): Boolean {
            if (formWindow is AdvancedFormWindowModal) {
                if (formWindow.wasClosed() || formWindow.response == null) {
                    formWindow.callClosed(player)
                } else {
                    if (formWindow.response.clickedButtonId == 0) {
                        formWindow.callClickedTrue(player)
                    } else {
                        formWindow.callClickedFalse(player)
                    }
                }
                return true
            }
            return false
        }
    }
}