package datahub.msv.sneeze

import datahub.msv.MSVReloaded.Companion.id
import datahub.msv.MSVStatusEffects
import eu.pb4.polymer.core.api.entity.PolymerEntity
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries.ENTITY_TYPE
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import java.util.*

class BlackSneeze(world: World) : Entity(BLACK_SNEEZE, world), PolymerEntity {

    companion object {
        val BLACK_SNEEZE: EntityType<BlackSneeze> = Registry.register(
            ENTITY_TYPE,
            id("black_sneeze"),
            EntityType.Builder.create(BlackSneezeEntityFactory, SpawnGroup.MISC)
                .dimensions(0.5F, 0.5F)
                .build()
        )

        fun spawn(player: PlayerEntity) {
            val entity = ENTITY_TYPE.get(id("black_sneeze")).create(player.world)
            val offsetX = Random().nextInt(3) - 1
            val offsetZ = Random().nextInt(3) - 1
            entity?.setPos(player.x + offsetX, player.y, player.z + offsetZ)
            player.world.spawnEntity(entity)
            entity?.refreshPositionAndAngles(player.x + offsetX, player.y, player.z + offsetZ, entity.yaw, entity.pitch)

            player.world.playSound(null, player.x, player.y, player.z, SoundEvents.ENTITY_PANDA_SNEEZE, SoundCategory.PLAYERS, 0.7f, player.world.random.nextFloat() * 0.1f + 0.4f)
        }
    }

    private fun collect(entity: Entity, player: PlayerEntity, items: ItemStack) {
        items.decrement(1)
        val item = ItemStack(Items.POTION)
        item.set(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent(MSVStatusEffects.CURSE_POTION))
        player.giveItemStack(item)
        player.world.playSound(null, player.x, player.y, player.z, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 0.5f, 1f)
        player.world.playSound(null, entity.x, entity.y, entity.z, SoundEvents.BLOCK_MUD_FALL, SoundCategory.PLAYERS, 0.5f, 1f)
        entity.kill()
    }

    object BlackSneezeEntityFactory : EntityType.EntityFactory<BlackSneeze> {
        override fun create(entityType: EntityType<BlackSneeze>, world: World): BlackSneeze {
            return BlackSneeze(world)
        }
    }

    override fun tick() {
        super.tick()

        (world as? ServerWorld)?.spawnParticles(ParticleTypes.SQUID_INK, pos.x, pos.y, pos.z, 3, 0.25, 0.5, 0.25, 0.001)

        if (age >= 200) { // 200 тиков = 10 секунд
            kill()
        }

        for (entity in world.getOtherEntities(this, boundingBox.expand(0.5))
            .filterIsInstance<LivingEntity>()) {
            entity.addStatusEffect(StatusEffectInstance(MSVStatusEffects.CURSE, 100, 1))
        }
    }

    override fun interact(player: PlayerEntity, hand: Hand?): ActionResult {
        if (player.mainHandStack.item.equals(Items.GLASS_BOTTLE)) {
            collect(this, player, player.mainHandStack)
            return ActionResult.SUCCESS
        } else if (player.offHandStack.item.equals(Items.GLASS_BOTTLE)) {
            collect(this,player, player.offHandStack)
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

    override fun initDataTracker(builder: DataTracker.Builder) {
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
    }

    override fun getPolymerEntityType(player: ServerPlayerEntity?): EntityType<*>? {
        return EntityType.INTERACTION
    }
}