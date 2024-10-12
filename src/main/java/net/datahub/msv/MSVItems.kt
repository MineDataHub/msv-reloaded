package net.datahub.msv

import com.mojang.serialization.Codec
import net.datahub.msv.MSVReloaded.Companion.id
import eu.pb4.polymer.core.api.item.PolymerItem
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils
import net.fabricmc.fabric.api.item.v1.EnchantingContext
import net.minecraft.component.ComponentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.potion.Potion
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

object MSVItems {
    object UmbrellaItem : Item(Settings().maxCount(1).maxDamage(250)), PolymerItem {
        private val UMBRELLA_STATE: ComponentType<Boolean> = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            "umbrella_state",
            ComponentType.builder<Boolean>().codec(Codec.BOOL).build()
        )

        override fun use(world: World, user: PlayerEntity, hand: Hand?): TypedActionResult<ItemStack> {
            val currentState = user.getStackInHand(hand).components.get(UMBRELLA_STATE) ?: false
            user.getStackInHand(hand).set(UMBRELLA_STATE, !currentState)
            world.playSound(user, user.blockPos, SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.PLAYERS)
            return TypedActionResult.success(user.getStackInHand(hand))
        }

        private var tickCounter = 0
        private var closingCD = 0
        private fun check(entity: LivingEntity, stack: ItemStack): Boolean {
            return if (entity.mainHandStack == stack) {
                false
            } else if (entity.offHandStack == stack) {
                false
            } else {
                true
            }
        }

        override fun inventoryTick(stack: ItemStack, world: World?, entity: Entity?, slot: Int, selected: Boolean) {
            tickCounter++
            if (tickCounter >= 20) {
                tickCounter = 0
                if (entity is LivingEntity) {
                    if (stack.components.get(UMBRELLA_STATE) == true && world?.isSkyVisibleAllowingSea(entity.blockPos) == true) {
                        stack.damage(1, entity, entity.getPreferredEquipmentSlot(stack))
                    }

                    if (stack.components.get(UMBRELLA_STATE) == true && check(entity, stack)) {
                        closingCD++
                        if (closingCD >= 5) {
                            closingCD = 0
                            stack.set(UMBRELLA_STATE, false)
                        }
                    } else {
                        closingCD = 0
                    }
                }
            }
        }

        override fun getPolymerItem(itemStack: ItemStack, player: ServerPlayerEntity?): Item {
            return if (itemStack.components.get(UMBRELLA_STATE) == true) Items.EMERALD else Items.DIAMOND
        }

        override fun isEnchantable(stack: ItemStack?): Boolean {
            return true
        }

        override fun canBeEnchantedWith(
            stack: ItemStack,
            enchantment: RegistryEntry<Enchantment>,
            context: EnchantingContext?
        ): Boolean {
            return enchantment == Enchantments.MENDING
        }

        fun check(player: PlayerEntity): Boolean {
            return player.mainHandStack.components.get(UMBRELLA_STATE) == true || player.offHandStack.components.get(
                UMBRELLA_STATE
            ) == true
        }
    }

    private fun potion(potion: RegistryEntry<Potion>): ItemStack {
        val item = ItemStack(Items.POTION)
        item.set(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent(potion))
        return item
    }
    private val MSV_TAB: ItemGroup =
            PolymerItemGroupUtils.builder().icon{ potion(MSVStatusEffects.INFECTION_POTION) }.displayName(Text.translatable("itemGroup.msv")).entries { _, entries ->
                entries.add(ItemStack(UMBRELLA))
                entries.add(potion(MSVStatusEffects.CURE_POTION))
                entries.add(potion(MSVStatusEffects.CURSE_POTION))
                entries.add(potion(MSVStatusEffects.INFECTION_POTION))
                    }.build()

    private val UMBRELLA: RegistryEntry.Reference<Item> =
        Registry.registerReference(Registries.ITEM, id("umbrella"), UmbrellaItem)

    fun register() {
        MSVReloaded.LOGGER.info("Initializing items...")

        UMBRELLA

        PolymerItemGroupUtils.registerPolymerItemGroup(RegistryKey.of(Registries.ITEM_GROUP.key, id("msv_tab")), MSV_TAB)
    }
}