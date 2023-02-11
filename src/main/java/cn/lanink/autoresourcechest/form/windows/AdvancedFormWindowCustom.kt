package cn.lanink.autoresourcechest.form.windows

import cn.nukkit.Player
import cn.nukkit.form.element.Element
import cn.nukkit.form.element.ElementButtonImageData
import cn.nukkit.form.response.FormResponseCustom
import cn.nukkit.form.window.FormWindow
import cn.nukkit.form.window.FormWindowCustom
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * @author lt_name
 */
class AdvancedFormWindowCustom : FormWindowCustom {

    private var buttonClickedListener: BiConsumer<FormResponseCustom, Player>? = null
    private var formClosedListener: Consumer<Player>? = null

    constructor(title: String) : super(title)
    constructor(title: String, contents: List<Element>) : super(title, contents)
    constructor(title: String, contents: List<Element>, icon: String) : super(title, contents, icon)
    constructor(title: String, contents: List<Element>, icon: ElementButtonImageData) : super(title, contents, icon)

    fun onResponded(listener: BiConsumer<FormResponseCustom, Player>): AdvancedFormWindowCustom {
        this.buttonClickedListener = listener
        return this
    }

    fun onClosed(listener: Consumer<Player>): AdvancedFormWindowCustom {
        this.formClosedListener = listener
        return this
    }

    private fun callResponded(formResponseCustom: FormResponseCustom, player: Player) {
        this.buttonClickedListener?.accept(formResponseCustom, player)
    }

    private fun callClosed(player: Player) {
        this.formClosedListener?.accept(player)
    }

    override fun getJSONData(): String {
        return GSON.toJson(this, FormWindowCustom::class.java)
    }

    companion object {
        fun onEvent(formWindow: FormWindow, player: Player): Boolean {
            if (formWindow is AdvancedFormWindowCustom) {
                if (formWindow.wasClosed() || formWindow.response == null) {
                    formWindow.callClosed(player)
                } else {
                    formWindow.callResponded(formWindow.response, player)
                }
                return true
            }
            return false
        }
    }
}