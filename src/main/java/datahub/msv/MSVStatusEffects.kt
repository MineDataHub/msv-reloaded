package datahub.msv

import datahub.msv.Main.Companion.id
import eu.pb4.polymer.core.api.other.PolymerPotion
import eu.pb4.polymer.core.api.other.PolymerStatusEffect
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.potion.Potion
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.network.ServerPlayerEntity


object MSVStatusEffects {
    val INFECTION: RegistryEntry<StatusEffect> =
        Registry.registerReference(Registries.STATUS_EFFECT, id("infection"), InfectionStatusEffect)
    val INFECTION_POTION: RegistryEntry<Potion> =
        Registry.registerReference(Registries.POTION, id("infection_potion"), InfectionPotion)

    val CURE: RegistryEntry<StatusEffect> =
        Registry.registerReference(Registries.STATUS_EFFECT, id("cure"), CureStatusEffect)
    val CURE_POTION: RegistryEntry<Potion> =
        Registry.registerReference(Registries.POTION, id("cure_potion"), CurePotion)

    val CURSE: RegistryEntry<StatusEffect> =
        Registry.registerReference(Registries.STATUS_EFFECT, id("curse"), CurseStatusEffect)
    val CURSE_POTION: RegistryEntry<Potion> =
        Registry.registerReference(Registries.POTION, id("curse_potion"), CursePotion)

    fun register() {
        Main.LOGGER.info("Registering Status-effects & Potions for" + Main.MOD_ID)
        INFECTION
        INFECTION_POTION

        CURE
        CURE_POTION

        CURSE
        CURSE_POTION
    }

    object InfectionStatusEffect : StatusEffect(StatusEffectCategory.HARMFUL, 4784020), PolymerStatusEffect {
        override fun getPolymerReplacement(player: ServerPlayerEntity?): StatusEffect {
            return this
        }
    }
    object InfectionPotion : Potion(StatusEffectInstance(INFECTION, 300)), PolymerPotion {
        override fun getPolymerReplacement(player: ServerPlayerEntity?): Potion {
            return this
        }
    }

    object CureStatusEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 13675367), PolymerStatusEffect {
        override fun getPolymerReplacement(player: ServerPlayerEntity?): StatusEffect {
            return this
        }
    }
    object CurePotion : Potion(StatusEffectInstance(CURE, 300)), PolymerPotion {
        override fun getPolymerReplacement(player: ServerPlayerEntity?): Potion {
            return this
        }
    }

    object CurseStatusEffect : StatusEffect(StatusEffectCategory.HARMFUL, 0), PolymerStatusEffect {
        override fun getPolymerReplacement(player: ServerPlayerEntity?): StatusEffect {
            return this
        }
    }
    object CursePotion : Potion(StatusEffectInstance(CURSE, 300)), PolymerPotion {
        override fun getPolymerReplacement(player: ServerPlayerEntity?): Potion {
            return this
        }
    }
}