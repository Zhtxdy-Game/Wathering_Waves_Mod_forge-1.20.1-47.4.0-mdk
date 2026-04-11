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
                    if (data instanceof PlayerTerminalDataImpl impl)
                    {
                        impl.recalculateAttributes(player);   // 重新计算属性
                        impl.syncToClient(player);            // 同步装备列表和属性缓存
                    }
                });

                player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data ->
                {
                    List<EchoInstance> equipped = data.getEquippedEchoes();
                    // 检查是否已在其他槽位装备（防止重复）
                    for (int i = 0; i < equipped.size(); i++)
                    {
                        EchoInstance e = equipped.get(i);
                        if (e != null && e.getId().equals(echo.getId()) && i != slotIndex)
                        {
                            player.sendSystemMessage(Component.literal("该声骸已装备在其他槽位"));
                            return;
                        }
                    }
                    // 检查总 Cost 限制
                    int currentTotal = data.getTotalEquippedCost();
                    int echoCost = echo.getCost();
                    EchoInstance old = equipped.get(slotIndex);
                    int oldCost = (old != null) ? old.getCost() : 0;
                    int newTotal = currentTotal - oldCost + echoCost;

                    if (newTotal > data.getMaxTotalCost())
                    {
                        player.sendSystemMessage(Component.literal("总 Cost 超过上限 (12)，无法装备"));

                        // 新增：发送拒绝包，让客户端刷新（防止客户端显示错误）
                        // 或者直接发送当前正确的装备列表给客户端强制同步
                        if (data instanceof PlayerTerminalDataImpl impl) {
                            impl.syncToClient(player);  // 强制同步正确状态
                        }
                        return;
                    }

                    // 执行装备
                    data.equipEcho(slotIndex, echo);
                    if (data instanceof PlayerTerminalDataImpl impl)
                    {
                        impl.recalculateAttributes(player);
                        impl.syncToClient(player);
                    }
                });

            }

        });
        ctx.get().setPacketHandled(true);
    }
}