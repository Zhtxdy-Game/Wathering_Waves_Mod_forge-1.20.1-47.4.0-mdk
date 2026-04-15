package com.ZhongHua.Wuthering_Waves;

import com.ZhongHua.Wuthering_Waves.entity.*;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Wuthering_WavesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents
{
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event)
    {
        event.put(ModEntities.CROWNLESS.get(), CrownlessEntity.createAttributes().build());//注册自定义实体"无冠者"
        event.put(ModEntities.BABY_ROSE_SHROOM.get(), BabyRoseshroomEntity.createAttributes().build());
        event.put(ModEntities.Havoc_Prism.get(), HavocPrismEntity.createAttributes().build());
        event.put(ModEntities.ROSE_SHROOM.get(), RoseshroomEntity.createAttributes().build());
        event.put(ModEntities.HAVOC_DREADMANE.get(), HavocDreadmaneEntity.createAttributes().build());
    }
}