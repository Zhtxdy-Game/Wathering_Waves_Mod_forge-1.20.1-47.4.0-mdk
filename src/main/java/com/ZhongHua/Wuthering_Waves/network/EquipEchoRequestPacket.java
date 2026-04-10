package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.capability.PlayerTerminalDataImpl;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class EquipEchoRequestPacket
{
    private final int slotIndex;
    private final CompoundTag echoNbt;

    public EquipEchoRequestPacket(int slotIndex, EchoInstance echo)
    {
        this.slotIndex = slotIndex;
        this.echoNbt = echo.toNBT();
    }

    public EquipEchoRequestPacket(FriendlyByteBuf buf)
    {
        this.slotIndex = buf.readInt();
        this.echoNbt = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(slotIndex);
        buf.writeNbt(echoNbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && echoNbt != null)
            {
                EchoInstance echo = EchoInstance.fromNBT(echoNbt);
                player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data ->
                {
                    List<EchoInstance> equipped = data.getEquippedEchoes();
                    // 检查是否已在其他槽位装备
                    int existingSlot = -1;
                    for (int i = 0; i < equipped.size(); i++)
                    {
                        EchoInstance e = equipped.get(i);
                        if (e != null && e.getId().equals(echo.getId()))
                        {
                            existingSlot = i;
                            break;
                        }
                    }
                    if (existingSlot != -1 && existingSlot != slotIndex)
                    {
                        player.sendSystemMessage(Component.literal("该声骸已装备在槽位 " + (existingSlot + 1) + "，无法重复装备"));
                        return;
                    }
                    data.equipEcho(slotIndex, echo);
                    if (data instanceof PlayerTerminalDataImpl impl) {
                        impl.syncToClient(player);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}