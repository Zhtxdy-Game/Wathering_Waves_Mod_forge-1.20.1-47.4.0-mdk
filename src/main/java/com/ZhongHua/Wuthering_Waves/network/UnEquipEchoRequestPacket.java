package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.capability.PlayerTerminalDataImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UnEquipEchoRequestPacket
{
    private final int slotIndex;

    public UnEquipEchoRequestPacket(int slotIndex)
    {
        this.slotIndex = slotIndex;
    }

    public UnEquipEchoRequestPacket(FriendlyByteBuf buf) {
        this.slotIndex = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(slotIndex);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data ->
                {
                    data.unequipEcho(slotIndex);
                    if (data instanceof PlayerTerminalDataImpl impl)
                    {
                        impl.syncToClient(player);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}