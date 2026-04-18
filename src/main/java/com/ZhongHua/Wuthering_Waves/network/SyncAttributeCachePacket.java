package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.client.gui.EchoEquipScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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
            ClientAttributeCache.setData(cacheNbt);
            Screen screen = Minecraft.getInstance().screen;
            // 只刷新属性显示界面，不刷新培养界面（避免干扰）
            if (screen instanceof EchoEquipScreen equipScreen)
            {
                equipScreen.refreshAttributeDisplay();
            }
            // 注意：不要刷新 EchoCultivateScreen
        });
        ctx.get().setPacketHandled(true);
    }
}