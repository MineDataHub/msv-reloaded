package datahub.msv

import datahub.msv.Main.Companion.id
import eu.pb4.polymer.core.api.entity.PolymerEntity
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.data.DataTracker
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World

class BlackSneeze(world: World) : Entity(BLACK_SNEEZE, world), PolymerEntity {

    companion object {
        private val BLACK_SNEEZE: EntityType<BlackSneeze> = Registry.register(
            Registries.ENTITY_TYPE,
            id("black_sneeze"),
            EntityType.Builder.create(BlackSneezeEntityFactory, SpawnGroup.MISC)
                .dimensions(0.5F, 0.5F)
                .build()
        )

        fun init() {
            PolymerEntityUtils.registerType(BLACK_SNEEZE)
        }
    }

    object BlackSneezeEntityFactory : EntityType.EntityFactory<BlackSneeze> {
        override fun create(entityType: EntityType<BlackSneeze>, world: World): BlackSneeze {
            return BlackSneeze(world)
        }
    }

    override fun tick() {
        super.tick()
        for (i in 0..9) {
            val xOffset = (Math.random() - 0.5) * 0.5
            val yOffset = (Math.random() - 0.5) * 0.5
            val zOffset = (Math.random() - 0.5) * 0.5

            world.addParticle(
                ParticleTypes.SQUID_INK,
                pos.x + xOffset,
                pos.y + yOffset,
                pos.z + zOffset,
                0.0, 0.0, 0.0
            )
        }

        if (age >= 200) { // 200 тиков = 10 секунд
            kill()
        }
    }

    override fun initDataTracker(builder: DataTracker.Builder) {
        // Initialize your entity's data tracker here
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        // Read custom data from NBT here
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        // Write custom data to NBT here
    }

    override fun getPolymerEntityType(player: ServerPlayerEntity?): EntityType<*> {
        return EntityType.PIG
    }
}