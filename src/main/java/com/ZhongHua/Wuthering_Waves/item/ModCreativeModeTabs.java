package com.ZhongHua.Wuthering_Waves.item;

import com.ZhongHua.Wuthering_Waves.Wuthering_WavesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs
{
    // 创建 DeferredRegister，用于注册创造模式标签
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Wuthering_WavesMod.MOD_ID);

    // 注册你自己的创造模式标签
    public static final RegistryObject<CreativeModeTab> WUTHERING_WAVES_TAB =
            CREATIVE_MODE_TABS.register("wuthering_waves_tab", () -> CreativeModeTab.builder()
                    // 设置标签的标题（显示名称），会在语言文件中翻译
                    .title(Component.translatable("creativetab.wuthering_waves_tab"))
                    // 设置标签的图标，这里使用你的终端物品
                    .icon(() -> new ItemStack(ModItems.WUTHERING_WAVES_TABS_PIC.get()))
                    // 设置标签的显示顺序（可选）
                    .displayItems((parameters, output) ->
                    {
                        // 将你的所有模组物品添加到这个标签中
                        output.accept(ModItems.HUIMING_TERMINAL.get());

                        output.accept(ModItems.CROWNLESS_ECHO.get());
                        output.accept(ModItems.BABY_ROSE_SHROOM_ECHO.get());
                        output.accept(ModItems.HAVOC_PRISM_ECHO.get());
                        output.accept(ModItems.ROSE_SHROOM_ECHO.get());
                        output.accept(ModItems.HAVOC_DREADMANE_ECHO.get());

                        output.accept(ModItems.TUNER.get());
                        output.accept(ModItems.SEALED_TUBE_BASIC.get());
                        output.accept(ModItems.SEALED_TUBE_MEDIUM.get());
                        output.accept(ModItems.SEALED_TUBE_ADVANCED.get());
                        output.accept(ModItems.SEALED_TUBE_PREMIUM.get());

                        output.accept(ModItems.CROWNLESS_SPAWN_EGG.get());
                        output.accept(ModItems.BABY_ROSE_SHROOM_SPAWN_EGG.get());
                        output.accept(ModItems.HAVOC_PRISM_SPAWN_EGG.get());
                        output.accept(ModItems.ROSE_SHROOM_SPAWN_EGG.get());
                        output.accept(ModItems.HAVOC_DREADMANE_SPAWN_EGG.get());
                        // 以后如果有更多物品，继续在这里添加
                    })
                    .build()
            );

    // 在模组事件总线上注册
    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}