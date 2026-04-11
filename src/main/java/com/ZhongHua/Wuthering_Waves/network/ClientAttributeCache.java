package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.capability.EchoAttributeCache;
import net.minecraft.nbt.CompoundTag;

public class ClientAttributeCache
{
    private static EchoAttributeCache cache = new EchoAttributeCache();

    public static void setData(CompoundTag nbt)
    {
        cache.deserializeNBT(nbt);
    }

    public static EchoAttributeCache getCache()
    {
        return cache;
    }
}