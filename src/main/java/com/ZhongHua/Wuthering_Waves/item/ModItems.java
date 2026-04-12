package com.ZhongHua.Wuthering_Waves.item;

import com.ZhongHua.Wuthering_Waves.Wuthering_WavesMod;
import com.ZhongHua.Wuthering_Waves.echocrystal.CrownlessEcho;
import com.ZhongHua.Wuthering_Waves.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Wuthering_WavesMod.MOD_ID);

    public static final RegistryObject<Item> WUTHERING_WAVES_TABS_PIC = ITEMS.register("wuthering_waves_tabs_pic",()->new Item(new Item.Properties()));
    //物品
    public static final RegistryObject<Item> HUIMING_TERMINAL = ITEMS.register("huiming_terminal",
            () -> new HuimingTerminalItem(new Item.Properties().stacksTo(1)));  // 设置最大堆叠数为1，更像“终端”

    //声骸培养材料
    public static final RegistryObject<Item> TUNER = ITEMS.register("tuner",
            () -> new TunerItem(new Item.Properties()));
    public static final RegistryObject<Item> SEALED_TUBE_BASIC = ITEMS.register("sealed_tube_basic",
            () -> new SealedTubeItem(new Item.Properties(), 1));
    public static final RegistryObject<Item> SEALED_TUBE_MEDIUM = ITEMS.register("sealed_tube_medium",
            () -> new SealedTubeItem(new Item.Properties(), 2));
    public static final RegistryObject<Item> SEALED_TUBE_ADVANCED = ITEMS.register("sealed_tube_advanced",
            () -> new SealedTubeItem(new Item.Properties(), 3));
    public static final RegistryObject<Item> SEALED_TUBE_PREMIUM = ITEMS.register("sealed_tube_premium",
            () -> new SealedTubeItem(new Item.Properties(), 4));


    // 声骸结晶物品
    public static final RegistryObject<AbstractEchoCrystalItem> CROWNLESS_ECHO = ITEMS.register("crownless_echo",
            () -> new CrownlessEcho(new Item.Properties()));


    //怪物刷怪蛋
    public static final RegistryObject<Item> CROWNLESS_SPAWN_EGG = ITEMS.register("crownless_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.CROWNLESS, 0x2c2c2c, 0x8c8c8c, new Item.Properties()));//无冠者

    public static void register (IEventBus eventBus)
    {
        ITEMS.register(eventBus);

    }
}