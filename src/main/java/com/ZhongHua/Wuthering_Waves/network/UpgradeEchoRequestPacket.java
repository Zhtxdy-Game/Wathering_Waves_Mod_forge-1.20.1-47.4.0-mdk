package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.capability.PlayerTerminalDataImpl;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UpgradeEchoRequestPacket
{
    private final UUID echoId;  // 声骸唯一ID

    public UpgradeEchoRequestPacket(UUID echoId)
    {
        this.echoId = echoId;
    }

    public UpgradeEchoRequestPacket(FriendlyByteBuf buf)
    {
        this.echoId = buf.readUUID();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUUID(echoId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayer player = ctx.get().getSender();
            if (player != null)
            {
                player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data ->
                {
                    if (data instanceof PlayerTerminalDataImpl impl)
                    {
                        EchoInstance echo = impl.getEchoById(echoId); // 需要实现此方法
                        if (echo != null && echo.getLevel() < 5)
                        {
                            if (impl.upgradeEcho(player, echo))
                            {
                                // 升级成功，同步数据
                                impl.syncToClient(player);
                            }
                        }
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}