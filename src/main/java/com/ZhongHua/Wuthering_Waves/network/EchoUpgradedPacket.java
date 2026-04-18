package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.client.gui.EchoCultivateScreen;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import com.ZhongHua.Wuthering_Waves.network.ClientTerminalDataCache;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class EchoUpgradedPacket
{
    private final UUID echoId;
    private final int newLevel;

    public EchoUpgradedPacket(UUID echoId, int newLevel)
    {
        this.echoId = echoId;
        this.newLevel = newLevel;
    }

    public EchoUpgradedPacket(FriendlyByteBuf buf)
    {
        this.echoId = buf.readUUID();
        this.newLevel = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUUID(echoId);
        buf.writeInt(newLevel);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            // 更新客户端缓存中对应声骸的等级
            ClientTerminalDataCache.updateEchoLevel(echoId, newLevel);
            // 刷新当前打开的培养界面
            if (Minecraft.getInstance().screen instanceof EchoCultivateScreen screen)
            {
                screen.refreshFromCache();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}