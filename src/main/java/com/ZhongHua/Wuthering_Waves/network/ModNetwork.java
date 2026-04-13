package com.ZhongHua.Wuthering_Waves.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork
{
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("wuthering_waves", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int id = 0;

    public static void register()
    {
        CHANNEL.registerMessage(id++, SyncTerminalDataPacket.class,
                SyncTerminalDataPacket::encode, SyncTerminalDataPacket::new,
                SyncTerminalDataPacket::handle);

        CHANNEL.registerMessage(id++, EquipEchoRequestPacket.class,
                EquipEchoRequestPacket::encode, EquipEchoRequestPacket::new,
                EquipEchoRequestPacket::handle);

        CHANNEL.registerMessage(id++, UnEquipEchoRequestPacket.class,
                UnEquipEchoRequestPacket::encode, UnEquipEchoRequestPacket::new,
                UnEquipEchoRequestPacket::handle);

        CHANNEL.registerMessage(id++, DeleteEchoRequestPacket.class,
                DeleteEchoRequestPacket::encode, DeleteEchoRequestPacket::new,
                DeleteEchoRequestPacket::handle);

        CHANNEL.registerMessage(id++, SyncAttributeCachePacket.class,
                SyncAttributeCachePacket::encode, SyncAttributeCachePacket::new,
                SyncAttributeCachePacket::handle);

        CHANNEL.registerMessage(id++, UpgradeEchoRequestPacket.class,
                UpgradeEchoRequestPacket::encode, UpgradeEchoRequestPacket::new,
                UpgradeEchoRequestPacket::handle);

        CHANNEL.registerMessage(id++, UpgradeEchoToMaxRequestPacket.class,
                UpgradeEchoToMaxRequestPacket::encode, UpgradeEchoToMaxRequestPacket::new,
                UpgradeEchoToMaxRequestPacket::handle);

        CHANNEL.registerMessage(id++, UpgradeConfirmPacket.class,
                UpgradeConfirmPacket::encode, UpgradeConfirmPacket::new,
                UpgradeConfirmPacket::handle);

    }

    public static void sendToPlayer(Object packet, ServerPlayer player)
    {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}