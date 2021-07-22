package cn.lanink.autoresourcechest.form.element

import cn.nukkit.Player
import cn.nukkit.form.element.ElementButton
import cn.nukkit.form.element.ElementButtonImageData
import java.util.function.Consumer

/**
 * @author lt_name
 */
class ResponseElementButton : ElementButton {

    private var clickedListener: Consumer<Player>? = null

    constructor(text: String?) : super(text)
    constructor(text: String?, image: ElementButtonImageData?) : super(text, image)

    fun onClicked(clickedListener: Consumer<Player>): ResponseElementButton {
        this.clickedListener = clickedListener
        return this
    }

    fun callClicked(player: Player): Boolean {
        if (clickedListener != null) {
            clickedListener!!.accept(player)
            return true
        }
        return false
    }
}