package com.ZhongHua.Wuthering_Waves.client.gui;

import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class EchoEquipScreen extends Screen
{
    private static final List<String> ATTRIBUTE_KEYS = List.of(
            "attribute.wuthering_waves.health",
            "attribute.wuthering_waves.attack",
            "attribute.wuthering_waves.defense",
            "attribute.wuthering_waves.resonance_efficiency",
            "attribute.wuthering_waves.crit_rate",
            "attribute.wuthering_waves.crit_damage",
            "attribute.wuthering_waves.resonance_skill_dmg",
            "attribute.wuthering_waves.normal_attack_dmg",
            "attribute.wuthering_waves.resonance_liberation_dmg",
            "attribute.wuthering_waves.physical_dmg",
            "attribute.wuthering_waves.glacio_dmg",
            "attribute.wuthering_waves.fusion_dmg",
            "attribute.wuthering_waves.electro_dmg",
            "attribute.wuthering_waves.aero_dmg",
            "attribute.wuthering_waves.spectro_dmg",
            "attribute.wuthering_waves.havoc_dmg",
            "attribute.wuthering_waves.healing_bonus"
    );

    private final double[] attributeValues = new double[ATTRIBUTE_KEYS.size()];
    private final List<EchoInstance> equippedEcho;

    // 构造函数1：无参，创建默认测试数据
    public EchoEquipScreen()
    {
        this(new ArrayList<>());
        // 初始化默认测试数据
        for (int i = 0; i < 5; i++) equippedEcho.add(null);
        equippedEcho.set(0, new EchoInstance("无冠者", 3));
        equippedEcho.set(1, new EchoInstance("鸣钟之龟", 2));
    }

    // 构造函数2：接受已有列表
    public EchoEquipScreen(List<EchoInstance> equippedEcho)
    {
        super(Component.literal("Echo Equip"));
        this.equippedEcho = equippedEcho;
        while (this.equippedEcho.size() < 5) this.equippedEcho.add(null);
    }

    @Override
    protected void init() {
        super.init();
        int screenWidth = this.width;
        int screenHeight = this.height;

        int rightPanelWidth = screenWidth / 3;
        int buttonWidth = 100;
        int buttonHeight = 50;
        int rightStartX = screenWidth - rightPanelWidth + (rightPanelWidth - buttonWidth) / 2;
        int topMargin = screenHeight / 10;
        int availableHeight = screenHeight - topMargin - topMargin;
        int gap = (availableHeight - buttonHeight * 5) / 4;

        for (int i = 0; i < 5; i++) {
            final int slotIndex = i;
            int y = topMargin + i * (buttonHeight + gap);
            EchoInstance echo = equippedEcho.get(i);
            Component buttonText = (echo == null) ?
                    Component.translatable("button.wuthering_waves.empty_slot", i + 1) :
                    Component.literal(echo.getName() + " Lv." + echo.getLevel());
            // 在 EchoEquipScreen.init() 的槽位按钮点击事件中
            Button slotButton = Button.builder(buttonText, btn ->
            {
                Minecraft.getInstance().setScreen(new EchoSelectScreen(slotIndex, equippedEcho));
            }).bounds(rightStartX, y, buttonWidth, buttonHeight).build();
            this.addRenderableWidget(slotButton);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int leftX = 20;
        int startY = 40;
        int lineHeight = 20;
        for (int i = 0; i < ATTRIBUTE_KEYS.size(); i++) {
            String key = ATTRIBUTE_KEYS.get(i);
            double value = attributeValues[i];
            String valueStr = (key.contains("rate") || key.contains("dmg") || key.contains("efficiency") || key.contains("bonus")) ?
                    String.format("%.1f%%", value * 100) : String.format("%.0f", value);
            Component line = Component.translatable(key + ".display", Component.translatable(key), valueStr);
            guiGraphics.drawString(this.font, line, leftX, startY + i * lineHeight, 0xFFFFFF, false);
        }

        int midX = this.width / 3;
        int midWidth = this.width / 3;
        int midY = this.height / 4;
        int midHeight = this.height / 2;
        guiGraphics.renderOutline(midX, midY, midWidth, midHeight, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, Component.literal("3D Model Placeholder"), midX + midWidth/2, midY + midHeight/2, 0xAAAAAA);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}