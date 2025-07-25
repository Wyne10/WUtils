package me.wyne.wutils.animation.runnable

import me.wyne.wutils.animation.AnimationRunnable
import org.bukkit.Location
import org.bukkit.block.data.type.RespawnAnchor
import kotlin.apply

class AnchorCharge(
    private val location: Location,
    private val amount: Int
) : AnimationRunnable {

    override fun run() {
        (location.block.blockData as? RespawnAnchor)?.apply {
            if (charges + amount > maximumCharges)
                return@apply
            charges += amount
            location.block.blockData = this
        }
    }

}