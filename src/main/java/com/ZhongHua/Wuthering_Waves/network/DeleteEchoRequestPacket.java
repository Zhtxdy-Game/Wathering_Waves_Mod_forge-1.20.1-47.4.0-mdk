package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.capability.PlayerTerminalDataImpl;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class DeleteEchoRequestPacket
{
    private final UUID echoId;

    public DeleteEchoRequestPacket(UUID echoId)
    {
        this.echoId = echoId;
    }

    public DeleteEchoRequestPacket(FriendlyByteBuf buf)
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
            if (player != null && echoId != null)
            {
                player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data ->
                {
                    // 查找并删除
                    List<EchoInstance> list = data.getEchoList();
                    boolean removed = list.removeIf(echo -> echo.getId().equals(echoId));
                    if (removed)
                    {
                        // 如果该声骸已装备，需要从装备槽位中卸下
                        List<EchoInstance> equipped = data.getEquippedEchoes();
                        for (int i = 0; i < equipped.size(); i++)
                        {
                            EchoInstance e = equipped.get(i);
                            if (e != null && e.getId().equals(echoId))
                            {
                                equipped.set(i, null);
                                break;
                            }
                        }
                        if (data instanceof PlayerTerminalDataImpl impl)
                        {
                            impl.syncToClient(player);
                        }
                        player.sendSystemMessage(Component.literal("已删除声骸"), false);
                    } else
                    {
                        player.sendSystemMessage(Component.literal("删除失败：未找到该声骸"), false);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}