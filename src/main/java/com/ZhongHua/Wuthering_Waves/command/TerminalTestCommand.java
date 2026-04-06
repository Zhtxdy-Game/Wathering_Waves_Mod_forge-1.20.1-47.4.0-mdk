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
                EchoInstance testEcho = new EchoInstance("无冠者", 1);
                boolean added = data.addEcho(testEcho);
                if (added)
                {
                    player.sendSystemMessage(Component.literal("已添加声骸: 无冠者 (等级1)，当前总数: " + data.getCurrentCount()));
                } else
                {
                    player.sendSystemMessage(Component.literal("添加失败，终端已满 (容量: " + data.getMaxCapacity() + ")"));
                }
            });
            return 1;
        }
        source.sendFailure(Component.literal("该命令只能由玩家执行"));
        return 0;
    }
}