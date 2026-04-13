package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.capability.PlayerTerminalDataImpl;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UpgradeEchoToMaxRequestPacket
{
    private final UUID echoId;
    private final boolean confirmed; // 是否确认升级（用于材料不足时弹窗确认）

    public UpgradeEchoToMaxRequestPacket(UUID echoId, boolean confirmed)
    {
        this.echoId = echoId;
        this.confirmed = confirmed;
    }

    public UpgradeEchoToMaxRequestPacket(FriendlyByteBuf buf)
    {
        this.echoId = buf.readUUID();
        this.confirmed = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUUID(echoId);
        buf.writeBoolean(confirmed);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data ->
                {
                    if (data instanceof PlayerTerminalDataImpl impl)
                    {
                        EchoInstance echo = impl.getEchoById(echoId);
                        if (echo != null)
                        {
                            impl.upgradeEchoToMax(player, echo, confirmed);
                        }
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}