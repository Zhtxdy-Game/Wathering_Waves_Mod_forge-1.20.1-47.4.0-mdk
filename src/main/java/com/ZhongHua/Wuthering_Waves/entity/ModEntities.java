package com.ZhongHua.Wuthering_Waves.entity;

import com.ZhongHua.Wuthering_Waves.Wuthering_WavesMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//自定义实体通过EntityAttributeCreationEvent 注册属性
public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Wuthering_WavesMod.MOD_ID);


    public static final RegistryObject<EntityType<CrownlessEntity>> CROWNLESS = ENTITIES.register("crownless",
            () -> EntityType.Builder.of(CrownlessEntity::new, MobCategory.MONSTER)
                    .sized(1.2F, 3.0F)        // 宽度 1.2 格，高度 3.0 格（体现高大体型）
                    .build("crownless"));//无冠者

    public static final RegistryObject<EntityType<BabyRoseshroomEntity>> BABY_ROSE_SHROOM = ENTITIES.register("baby_roseshroom",
            () -> EntityType.Builder.of(BabyRoseshroomEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.2F)  // 体型较小
                    .build("baby_roseshroom"));

    public static final RegistryObject<EntityType<HavocPrismEntity>> Havoc_Prism = ENTITIES.register("havoc_prism",
            () -> EntityType.Builder.of(HavocPrismEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.5F)
                    .build("havoc_prism"));

    public static final RegistryObject<EntityType<RoseshroomEntity>> ROSE_SHROOM = ENTITIES.register("roseshroom",
            () -> EntityType.Builder.of(RoseshroomEntity::new, MobCategory.MONSTER)
                    .sized(1.2F, 1.8F)
                    .build("roseshroom"));

    public static final RegistryObject<EntityType<HavocDreadmaneEntity>> HAVOC_DREADMANE = ENTITIES.register("havoc_dreadmane",
            () -> EntityType.Builder.of(HavocDreadmaneEntity::new, MobCategory.MONSTER)
                    .sized(1.2F, 1.8F)
                    .build("havoc_dreadmane"));


    public static void register(IEventBus eventBus)
    {
        ENTITIES.register(eventBus);
    }
}