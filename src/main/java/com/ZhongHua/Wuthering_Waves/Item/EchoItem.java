package com.ZhongHua.Wuthering_Waves.Item;

import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EchoItem extends Item
{
    public EchoItem(Properties properties)
    {
        super(properties);
    }

    // 从 ItemStack 中获取 EchoInstance
    public static EchoInstance getEcho(ItemStack stack)
    {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("EchoData"))
        {
            return EchoInstance.fromNBT(tag.getCompound("EchoData"));
        }
        return null;
    }

    // 将 EchoInstance 保存到 ItemStack
    public static void setEcho(ItemStack stack, EchoInstance echo)
    {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put("EchoData", echo.toNBT());
    }

    // 创建一个带有特定声骸数据的物品栈
    public static ItemStack createStack(EchoInstance echo)
    {
        ItemStack stack = new ItemStack(ModItems.CROWNLESS_ECHO.get());
        setEcho(stack, echo);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag)
    {
        EchoInstance echo = getEcho(stack);
        if (echo != null) {
            tooltip.add(Component.literal("等级: " + echo.getLevel()));
            tooltip.add(Component.literal("COST: " + echo.getCost()));
            tooltip.add(Component.literal("主属性: " + echo.getMainStat() + " " + (int)(echo.getMainStatValue()*100) + "%"));
            // 可显示前两条副属性
            List<String> subStats = echo.getSubStats();
            List<Double> subValues = echo.getSubStatValues();
            for (int i = 0; i < Math.min(2, subStats.size()); i++)
            {
                tooltip.add(Component.literal(subStats.get(i) + " " + (int)(subValues.get(i)*100) + "%"));
            }
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}