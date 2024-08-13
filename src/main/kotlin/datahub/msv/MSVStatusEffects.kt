package datahub.msv

import eu.pb4.polymer.core.api.other.PolymerStatusEffect
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.server.network.ServerPlayerEntity

class InfectedStatusEffect : StatusEffect(StatusEffectCategory.HARMFUL, 0xFF0000), PolymerStatusEffect {
    override fun getPolymerReplacement(player: ServerPlayerEntity): StatusEffect {
        return this
    }
}
class CuredStatusEffect : StatusEffect(StatusEffectCategory.HARMFUL, 0xFF0000), PolymerStatusEffect {
    override fun getPolymerReplacement(player: ServerPlayerEntity): StatusEffect {
        return this
    }
}