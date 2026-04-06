package com.ZhongHua.Wuthering_Waves;

import com.ZhongHua.Wuthering_Waves.entity.CrownlessEntity;
import com.ZhongHua.Wuthering_Waves.entity.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Wuthering_WavesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents
{
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event)
    {
        event.put(ModEntities.CROWNLESS.get(), CrownlessEntity.createAttributes().build());
    }
}