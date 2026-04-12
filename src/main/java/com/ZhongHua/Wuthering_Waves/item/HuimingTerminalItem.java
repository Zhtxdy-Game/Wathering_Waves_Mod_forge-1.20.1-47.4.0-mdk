package com.ZhongHua.Wuthering_Waves.item;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.capability.PlayerTerminalDataImpl;
import com.ZhongHua.Wuthering_Waves.client.gui.EchoEquipScreen;
import com.ZhongHua.Wuthering_Waves.client.gui.TerminalScreen;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HuimingTerminalItem extends Item
{
    public HuimingTerminalItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack offhandItem = player.getOffhandItem();

        // 判断副手是否为声骸结晶（所有结晶物品都继承自 AbstractEchoCrystalItem）
        if (offhandItem.getItem() instanceof AbstractEchoCrystalItem crystal)
        {
            if (!level.isClientSide)
            {
                player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data ->
                {
                    if (data.getCurrentCount() >= data.getMaxCapacity())
                    {
                        player.sendSystemMessage(Component.literal("终端已满，无法吸收声骸！"));
                        return;
                    }

                    // 根据结晶的信息生成一个随机属性的 EchoInstance
                    EchoInstance echo = EchoInstance.createRandom
                            (
                            crystal.getEchoDisplayName(),
                            crystal.getCost(),
                            crystal.getPossibleMainStats()
                    );

                    if (!level.isClientSide && player instanceof ServerPlayer serverPlayer)
                    {

                            if (data.addEcho(echo))
                            {
                                offhandItem.shrink(1);
                                if (data instanceof PlayerTerminalDataImpl impl)
                                {
                                    impl.syncToClient(serverPlayer);
                                }
                                player.sendSystemMessage(Component.literal("已吸收 " + echo.getName()));
                            };

                    }
                });
            }
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }

        // 没有副手声骸结晶时，打开终端主界面（仅客户端）
        if (level.isClientSide)
        {
            Minecraft.getInstance().setScreen(new TerminalScreen());
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}