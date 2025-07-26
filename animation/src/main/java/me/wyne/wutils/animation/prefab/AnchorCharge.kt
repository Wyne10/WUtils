package me.wyne.wutils.animation.prefab

import me.wyne.wutils.animation.AnimationRunnable
import org.bukkit.Location
import org.bukkit.block.data.type.RespawnAnchor
import kotlin.apply

class AnchorCharge(
    private val origin: Location,
    private val amount: Int
) : AnimationRunnable {

    override fun run() {
        (origin.block.blockData as? RespawnAnchor)?.apply {
            if (charges + amount > maximumCharges)
                return@apply
            charges += amount
            origin.block.blockData = this
        }
    }

}