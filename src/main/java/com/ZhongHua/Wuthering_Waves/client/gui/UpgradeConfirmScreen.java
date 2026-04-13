package com.ZhongHua.Wuthering_Waves.client.gui;

import com.ZhongHua.Wuthering_Waves.network.ModNetwork;
import com.ZhongHua.Wuthering_Waves.network.UpgradeEchoToMaxRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;

import java.util.UUID;

public class UpgradeConfirmScreen extends Screen
{
    private final UUID echoId;
    private final int targetLevel;
    private final int tuner, basic, medium, advanced, premium;

    public UpgradeConfirmScreen(UUID echoId, int targetLevel, int tuner, int basic, int medium, int advanced, int premium)
    {
        super(Component.literal("确认升级"));
        this.echoId = echoId;
        this.targetLevel = targetLevel;
        this.tuner = tuner;
        this.basic = basic;
        this.medium = medium;
        this.advanced = advanced;
        this.premium = premium;
    }

    @Override
    protected void init()
    {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        Button confirmBtn = Button.builder(Component.literal("确认升级"), btn ->
        {
            ModNetwork.CHANNEL.sendToServer(new UpgradeEchoToMaxRequestPacket(echoId, true));
            this.onClose();   // 关闭当前确认界面，返回培养界面
        }).bounds(centerX - 100, centerY + 20, 80, 20).build();
        Button cancelBtn = Button.builder(Component.literal("取消"), btn -> {
            Minecraft.getInstance().setScreen(null);
        }).bounds(centerX + 20, centerY + 20, 80, 20).build();
        this.addRenderableWidget(confirmBtn);
        this.addRenderableWidget(cancelBtn);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(guiGraphics);
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        guiGraphics.drawCenteredString(this.font, Component.literal("材料不足，是否消耗以下材料升级到 " + targetLevel + " 级？"), centerX, centerY - 60, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, Component.literal("调谐器 x" + tuner), centerX, centerY - 30, 0xFFFFFF);
        if (basic > 0) guiGraphics.drawCenteredString(this.font, Component.literal("初级密音筒 x" + basic), centerX, centerY - 10, 0xFFFFFF);
        if (medium > 0) guiGraphics.drawCenteredString(this.font, Component.literal("中级密音筒 x" + medium), centerX, centerY + 10, 0xFFFFFF);
        if (advanced > 0) guiGraphics.drawCenteredString(this.font, Component.literal("高级密音筒 x" + advanced), centerX, centerY + 30, 0xFFFFFF);
        if (premium > 0) guiGraphics.drawCenteredString(this.font, Component.literal("特级密音筒 x" + premium), centerX, centerY + 50, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}