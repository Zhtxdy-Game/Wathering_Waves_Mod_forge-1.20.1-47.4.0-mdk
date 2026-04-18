package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.client.gui.EchoCultivateScreen;
import com.ZhongHua.Wuthering_Waves.client.gui.EchoEquipScreen;
import com.ZhongHua.Wuthering_Waves.client.gui.EchoSelectScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncTerminalDataPacket
{
    private final CompoundTag dataNbt;

    // 新增：接受 CompoundTag 的构造函数（供 syncToClient 调用）
    public SyncTerminalDataPacket(CompoundTag dataNbt)
    {
        this.dataNbt = dataNbt;
    }

    // 原有的接受 IPlayerTerminalData 的构造函数（可选保留，但建议统一使用 CompoundTag）
    // 如果你在其他地方使用了这个构造，可以保留，否则删除
    public SyncTerminalDataPacket(com.ZhongHua.Wuthering_Waves.capability.IPlayerTerminalData data)
    {
        this.dataNbt = data.serializeNBT();
    }

    // 解码构造函数（供网络系统使用）
    public SyncTerminalDataPacket(FriendlyByteBuf buf)
    {
        this.dataNbt = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeNbt(dataNbt);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            // 更新客户端缓存
            ClientTerminalDataCache.setData(dataNbt);

            // 刷新当前打开的界面
            Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof EchoCultivateScreen)
            {
                // 直接调用刷新方法（内部有防抖）
                ((EchoCultivateScreen) screen).refreshFromCache();
            } else if (screen instanceof EchoSelectScreen)
            {
                ((EchoSelectScreen) screen).refreshFromCache();
            } else if (screen instanceof EchoEquipScreen)
            {
                ((EchoEquipScreen) screen).refreshFromCache();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}