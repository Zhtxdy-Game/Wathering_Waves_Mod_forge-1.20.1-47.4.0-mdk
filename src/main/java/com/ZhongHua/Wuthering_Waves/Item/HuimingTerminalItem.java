package com.ZhongHua.Wuthering_Waves.Item;

import com.ZhongHua.Wuthering_Waves.client.gui.TerminalScreen;
import net.minecraft.client.Minecraft;
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
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide)
        {
            // 仅在客户端打开GUI
            Minecraft.getInstance().setScreen(new TerminalScreen());
        }
        // 返回成功，不消耗物品，不触发额外动作
        return InteractionResultHolder.success(itemStack);
    }
}