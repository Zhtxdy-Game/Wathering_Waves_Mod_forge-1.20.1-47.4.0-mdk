// UpgradeConfirmPacket.java
package com.ZhongHua.Wuthering_Waves.network;

import net.minecraft.client.gui.screens.ConfirmScreen;
import com.ZhongHua.Wuthering_Waves.client.gui.EchoCultivateScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UpgradeConfirmPacket
{
    private final UUID echoId;
    private final int targetLevel;
    private final int tunerNeeded;
    private final int basicNeeded, mediumNeeded, advancedNeeded, premiumNeeded;

    public UpgradeConfirmPacket(UUID echoId, int targetLevel, java.util.Map<String, Integer> materials)
    {
        this.echoId = echoId;
        this.targetLevel = targetLevel;
        this.tunerNeeded = materials.get("tuner");
        this.basicNeeded = materials.get("basic");
        this.mediumNeeded = materials.get("medium");
        this.advancedNeeded = materials.get("advanced");
        this.premiumNeeded = materials.get("premium");
    }

    public UpgradeConfirmPacket(FriendlyByteBuf buf)
    {
        this.echoId = buf.readUUID();
        this.targetLevel = buf.readInt();
        this.tunerNeeded = buf.readInt();
        this.basicNeeded = buf.readInt();
        this.mediumNeeded = buf.readInt();
        this.advancedNeeded = buf.readInt();
        this.premiumNeeded = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUUID(echoId);
        buf.writeInt(targetLevel);
        buf.writeInt(tunerNeeded);
        buf.writeInt(basicNeeded);
        buf.writeInt(mediumNeeded);
        buf.writeInt(advancedNeeded);
        buf.writeInt(premiumNeeded);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            // 构建确认消息
            StringBuilder msg = new StringBuilder();
            msg.append("确定要消耗以下材料将声骸升至 ").append(targetLevel).append(" 级吗？\n");
            msg.append("调谐器 x").append(tunerNeeded);
            if (basicNeeded > 0) msg.append("\n初级密音筒 x").append(basicNeeded);
            if (mediumNeeded > 0) msg.append("\n中级密音筒 x").append(mediumNeeded);
            if (advancedNeeded > 0) msg.append("\n高级密音筒 x").append(advancedNeeded);
            if (premiumNeeded > 0) msg.append("\n特级密音筒 x").append(premiumNeeded);

            // 弹出确认框
            Minecraft.getInstance().setScreen(new ConfirmScreen
                    (
                    confirmed ->
                    {
                        if (confirmed)
                        {
                            // 用户确认，发送真正的升级请求
                            ModNetwork.CHANNEL.sendToServer(new UpgradeEchoToMaxRequestPacket(echoId, true));
                        }
                        // 无论确认还是取消，都返回到培养界面（刷新数据）
                        Minecraft.getInstance().setScreen(new EchoCultivateScreen());
                    },
                    Component.literal("确认升级"),
                    Component.literal(msg.toString())
            ));
        });
        ctx.get().setPacketHandled(true);
    }
}