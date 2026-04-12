package com.ZhongHua.Wuthering_Waves.client.gui;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class TerminalScreen extends Screen
{
    private static final Component TITLE = Component.literal("Terminal");
    private static final int BG_WIDTH = 210;
    private static final int BG_HEIGHT = 180;
    private static final int BG_COLOR = 0xAAAAAAAA;

    // 控制是否显示按钮面板
    private boolean showButtons = false;
    private int tickCounter = 0;

    public TerminalScreen()
    {
        super(TITLE);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!showButtons)
        {
            tickCounter++;
            if (tickCounter >= 20)
            { // 1秒 = 20 ticks (20 ticks/秒)
                showButtons = true;
                initButtons(); // 初始化按钮
            }
        }
    }

    private void initButtons()
    {
        // 计算网格参数：3x3 网格，每个格子大小相同
        int margin = 10; // 内边距
        int usableWidth = BG_WIDTH - 2 * margin;
        int usableHeight = BG_HEIGHT - 2 * margin;
        int cellWidth = usableWidth / 3;
        int cellHeight = usableHeight / 3;
        int startX = (this.width - BG_WIDTH) / 2 + margin;
        int startY = (this.height - BG_HEIGHT) / 2 + margin;

        // 创建9个按钮
        for (int row = 0; row < 3; row++)
        {
            for (int col = 0; col < 3; col++)
            {
                int index = row * 3 + col;
                int x = startX + col * cellWidth;
                int y = startY + row * cellHeight;
                Button button;
                if (index == 0)
                {
                    // 第一个按钮：声骸
                    button = Button.builder(Component.translatable("button.wuthering_waves.echo"),
                                    btn ->
                                    {
                                        // 打开声骸装备界面
                                        Player player = Minecraft.getInstance().player;
                                        if (player != null)
                                        {
                                            player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data ->
                                            {
                                                // 获取装备列表（注意：这里返回的是内部列表的引用，可以直接用于界面）
                                                List<EchoInstance> equipped = data.getEquippedEchoes();
                                                Minecraft.getInstance().setScreen(new EchoEquipScreen());
                                            });
                                        }
                                    })
                            .bounds(x, y, cellWidth, cellHeight)
                            .build();
                } else
                {
                    // 其余按钮：占位符（显示空字符串或可选的占位文本）
                    button = Button.builder(Component.translatable("button.wuthering_waves.placeholder"),
                                    btn -> {})
                            .bounds(x, y, cellWidth, cellHeight)
                            .build();
                }
                this.addRenderableWidget(button);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        // 绘制背景板
        int bgX = (this.width - BG_WIDTH) / 2;
        int bgY = (this.height - BG_HEIGHT) / 2;
        guiGraphics.fill(bgX, bgY, bgX + BG_WIDTH, bgY + BG_HEIGHT, BG_COLOR);
        // 绘制黑色边框
        guiGraphics.fill(bgX, bgY, bgX + BG_WIDTH, bgY + 1, 0xFF000000);
        guiGraphics.fill(bgX, bgY + BG_HEIGHT - 1, bgX + BG_WIDTH, bgY + BG_HEIGHT, 0xFF000000);
        guiGraphics.fill(bgX, bgY, bgX + 1, bgY + BG_HEIGHT, 0xFF000000);
        guiGraphics.fill(bgX + BG_WIDTH - 1, bgY, bgX + BG_WIDTH, bgY + BG_HEIGHT, 0xFF000000);

        if (!showButtons)
        {
            // 显示初始化文字
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            guiGraphics.drawCenteredString(this.font, Component.translatable("Terminal_UI_Text_Chinese"), centerX, centerY - 10, 0x000000);
            guiGraphics.drawCenteredString(this.font, Component.translatable("Terminal_UI_Text_English"), centerX, centerY + 10, 0x000000);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}