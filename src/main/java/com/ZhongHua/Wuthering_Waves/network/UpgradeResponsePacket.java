package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.client.gui.EchoCultivateScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpgradeResponsePacket
{
    private final boolean success;

    public UpgradeResponsePacket(boolean success)
    {
        this.success = success;
    }

    public UpgradeResponsePacket(FriendlyByteBuf buf)
    {
        this.success = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeBoolean(success);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            if (success && Minecraft.getInstance().screen instanceof EchoCultivateScreen screen)
            {
                // 强制刷新整个界面
                screen.refreshFromCache();
                // 立即刷新材料数量（重新从背包读取）
                screen.refreshMaterialCountsNow();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}