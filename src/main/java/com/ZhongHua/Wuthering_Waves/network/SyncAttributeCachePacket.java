package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.client.gui.EchoEquipScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncAttributeCachePacket
{
    private final CompoundTag cacheNbt;

    public SyncAttributeCachePacket(CompoundTag cacheNbt)
    {
        this.cacheNbt = cacheNbt;
    }

    public SyncAttributeCachePacket(FriendlyByteBuf buf)
    {
        this.cacheNbt = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeNbt(cacheNbt);
    }


    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            // 只在客户端执行
            if (ctx.get().getDirection().getReceptionSide().isClient())
            {
                ClientAttributeCache.setData(cacheNbt);
                // 不需要额外刷新，因为 render 会实时读取缓存
            }
        });
        ctx.get().setPacketHandled(true);
    }
}