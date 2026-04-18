package com.ZhongHua.Wuthering_Waves;

import com.ZhongHua.Wuthering_Waves.client.model.*;
import com.ZhongHua.Wuthering_Waves.client.renderer.*;
import com.ZhongHua.Wuthering_Waves.item.ModCreativeModeTabs;
import com.ZhongHua.Wuthering_Waves.item.ModItems;
import com.ZhongHua.Wuthering_Waves.command.TerminalTestCommand;
import com.ZhongHua.Wuthering_Waves.entity.ModEntities;
import com.ZhongHua.Wuthering_Waves.network.ModNetwork;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Wuthering_WavesMod.MOD_ID)
public class Wuthering_WavesMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "wuthering_waves";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Wuthering_WavesMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        //鸣潮相关注册
        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        // 注册网络
        ModNetwork.register();


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
        // 注册Wuthering_WavesMod测试命令
        TerminalTestCommand.register(event.getServer().getCommands().getDispatcher());
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() ->
            {
                // 替换为 GeckoLib 渲染器
                EntityRenderers.register(ModEntities.CROWNLESS.get(), CrownlessRenderer::new);
                EntityRenderers.register(ModEntities.BABY_ROSE_SHROOM.get(), BabyRoseshroomRenderer::new);
                EntityRenderers.register(ModEntities.Havoc_Prism.get(), HavocPrismRenderer::new);
                EntityRenderers.register(ModEntities.ROSE_SHROOM.get(), RoseshroomRenderer::new);
                EntityRenderers.register(ModEntities.HAVOC_DREADMANE.get(), HavocDreadmaneRenderer::new);
            });
        }

        @SubscribeEvent
        public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
        {
            event.registerLayerDefinition(BabyRoseshroomRenderer.LAYER_LOCATION, BabyRoseshroomModel::createBodyLayer);
            event.registerLayerDefinition(HavocPrismRenderer.LAYER_LOCATION, HavocPrismModel::createBodyLayer);
            event.registerLayerDefinition(RoseshroomRenderer.LAYER_LOCATION, RoseshroomModel::createBodyLayer);
            event.registerLayerDefinition(HavocDreadmaneRenderer.LAYER_LOCATION, HavocDreadmaneModel::createBodyLayer);
        }



    }

}
