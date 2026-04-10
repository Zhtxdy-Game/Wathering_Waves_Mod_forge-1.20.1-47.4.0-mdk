package com.ZhongHua.Wuthering_Waves.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = "wuthering_waves")
public class ModCapabilities
{
    public static final Capability<IPlayerTerminalData> PLAYER_TERMINAL_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ResourceLocation TERMINAL_CAP_ID = new ResourceLocation("wuthering_waves", "player_terminal_data");

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(IPlayerTerminalData.class);
    }

    @SubscribeEvent
    public static void attachPlayerCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof Player)
        {
            event.addCapability(TERMINAL_CAP_ID, new TerminalCapabilityProvider());
        }
    }

    private static class TerminalCapabilityProvider implements ICapabilitySerializable<CompoundTag>
    {
        private final IPlayerTerminalData instance = new PlayerTerminalDataImpl();
        private final LazyOptional<IPlayerTerminalData> lazyOptional = LazyOptional.of(() -> instance);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
        {
            if (cap == PLAYER_TERMINAL_DATA)
            {
                return lazyOptional.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT()
        {
            return instance.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            instance.deserializeNBT(nbt);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
        {
            serverPlayer.getCapability(PLAYER_TERMINAL_DATA).ifPresent(data ->
            {
                if (data instanceof PlayerTerminalDataImpl impl)
                {
                    impl.syncToClient(serverPlayer);
                }
            });
        }
    }

}