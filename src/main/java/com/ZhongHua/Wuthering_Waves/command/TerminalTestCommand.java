package com.ZhongHua.Wuthering_Waves.command;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TerminalTestCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("wutest")
                .then(Commands.literal("give")
                        .executes(TerminalTestCommand::giveEcho)
                )
        );
    }

    private static int giveEcho(CommandContext<CommandSourceStack> context)
    {
        CommandSourceStack source = context.getSource();
        if (source.getEntity() instanceof ServerPlayer player)
        {
            player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data ->
            {

            });
            return 1;
        }
        source.sendFailure(Component.literal("该命令只能由玩家执行"));
        return 0;
    }
}