package net.datahub.msv

import eu.pb4.polymer.core.api.other.PolymerPotion
import eu.pb4.polymer.core.api.other.PolymerStatusEffect
import net.datahub.msv.MSVReloaded.Companion.id
import net.datahub.msv.access.MobAccess
import net.datahub.msv.access.PlayerAccess
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.potion.Potion
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.entry.RegistryEntry
import xyz.nucleoid.packettweaker.PacketContext

object ModStatusEffects {
    private val INFECTION: RegistryEntry<StatusEffect> =
        Registry.registerReference(Registries.STATUS_EFFECT, id("infection"), InfectionStatusEffect)
    val INFECTION_POTION: RegistryEntry<Potion> =
        Registry.registerReference(Registries.POTION, id("infection"), InfectionPotion)

    private val CURE: RegistryEntry<StatusEffect> =
        Registry.registerReference(Registries.STATUS_EFFECT, id("cure"), CureStatusEffect)
    val CURE_POTION: RegistryEntry<Potion> =
        Registry.registerReference(Registries.POTION, id("cure"), CurePotion)

    val CURSE: RegistryEntry<StatusEffect> =
        Registry.registerReference(Registries.STATUS_EFFECT, id("curse"), CurseStatusEffect)
    val CURSE_POTION: RegistryEntry<Potion> =
        Registry.registerReference(Registries.POTION, id("curse"), CursePotion)

    fun register() {
        MSVReloaded.LOGGER.info("Initializing status-effects...")
        INFECTION
        INFECTION_POTION

        CURE
        CURE_POTION

        CURSE
        CURSE_POTION
    }

    object InfectionStatusEffect : StatusEffect(StatusEffectCategory.HARMFUL, 4784020), PolymerStatusEffect {
        override fun getPolymerReplacement(context: PacketContext?): StatusEffect {
            return this
        }
    }
    object InfectionPotion : Potion("infection"), PolymerPotion {
        override fun getPolymerReplacement(context: PacketContext?): Potion {
            return this
        }
    }

    object CureStatusEffect : StatusEffect(StatusEffectCategory.BENEFICIAL, 13675367), PolymerStatusEffect {
        override fun getPolymerReplacement(context: PacketContext?): StatusEffect {
            return this
        }
    }
    object CurePotion : Potion("cure"), PolymerPotion {
        override fun getPolymerReplacement(context: PacketContext?): Potion {
            return this
        }
    }

    object CurseStatusEffect : StatusEffect(StatusEffectCategory.HARMFUL, 0), PolymerStatusEffect {
        override fun getPolymerReplacement(context: PacketContext?): StatusEffect {
            return this
        }

        override fun onApplied(entity: LivingEntity?, amplifier: Int) {
            if (amplifier == 1) {
                if (entity is MobEntity && !(entity as MobAccess).isInfected)
                    entity.isInfected = true
                if (entity is PlayerEntity && (entity as PlayerAccess).stage == 0)
                    entity.stage = 5
            }
        }
    }
    object CursePotion : Potion("curse"), PolymerPotion {
        override fun getPolymerReplacement(context: PacketContext?): Potion {
            return this
        }
    }
}