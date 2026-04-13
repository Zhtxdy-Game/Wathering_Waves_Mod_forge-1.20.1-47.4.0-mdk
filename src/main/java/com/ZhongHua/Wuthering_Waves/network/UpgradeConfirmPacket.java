package com.ZhongHua.Wuthering_Waves.network;

import com.ZhongHua.Wuthering_Waves.client.gui.UpgradeConfirmScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class UpgradeConfirmPacket
{
    private final UUID echoId;
    private final int targetLevel;
    private final int tunerNeeded;
    private final int basicNeeded, mediumNeeded, advancedNeeded, premiumNeeded;

    public UpgradeConfirmPacket(UUID echoId, int targetLevel, Map<String, Integer> materials)
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
            // 在客户端打开确认对话框
            Minecraft.getInstance().setScreen(new UpgradeConfirmScreen(echoId, targetLevel,
                    tunerNeeded, basicNeeded, mediumNeeded, advancedNeeded, premiumNeeded));
        });
        ctx.get().setPacketHandled(true);
    }
}