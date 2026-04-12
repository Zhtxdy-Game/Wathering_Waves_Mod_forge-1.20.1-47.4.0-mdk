package com.ZhongHua.Wuthering_Waves.item;

import net.minecraft.world.item.Item;

public class SealedTubeItem extends Item
{
    private final int tier; // 1=初级,2=中级,3=高级,4=特级

    public SealedTubeItem(Properties properties, int tier)
    {
        super(properties);
        this.tier = tier;
    }

    public int getTier()
    {
        return tier;
    }
}