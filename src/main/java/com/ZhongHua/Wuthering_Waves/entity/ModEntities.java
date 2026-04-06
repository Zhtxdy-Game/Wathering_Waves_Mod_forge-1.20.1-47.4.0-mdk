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

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}