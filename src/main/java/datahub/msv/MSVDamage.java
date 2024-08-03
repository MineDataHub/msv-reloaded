package datahub.msv;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

import static datahub.msv.Main.id;

public class MSVDamage {

    public static final RegistryKey<DamageType> WATER = register("water");

    public static DamageSource createDamageSource(World world, RegistryKey<DamageType> damageTypeRegistryKey) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(damageTypeRegistryKey));
    }

    private static RegistryKey<DamageType> register(String id) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id(id));
    }
}
