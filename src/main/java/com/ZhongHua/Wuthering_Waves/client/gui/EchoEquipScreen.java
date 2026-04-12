package com.ZhongHua.Wuthering_Waves.client.gui;

import com.ZhongHua.Wuthering_Waves.capability.EchoAttributeCache;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import com.ZhongHua.Wuthering_Waves.network.ClientAttributeCache;
import com.ZhongHua.Wuthering_Waves.network.ClientTerminalDataCache;
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
    private  List<EchoInstance> equippedEcho;


    // 无参构造器
    public EchoEquipScreen()
    {
        super(Component.literal("Echo Equip"));
    }

    @Override
    protected void init()
    {

        // 每次打开界面时从客户端缓存获取最新的装备列表
        this.equippedEcho = new ArrayList<>(ClientTerminalDataCache.getEquippedEchoes());
        // 确保列表长度为5
        while (this.equippedEcho.size() < 5)
        {
            this.equippedEcho.add(null);
        }

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

        for (int i = 0; i < 5; i++)
        {
            final int slotIndex = i;
            int y = topMargin + i * (buttonHeight + gap);
            EchoInstance echo = equippedEcho.get(i);
            Component buttonText = (echo == null) ?
                    Component.translatable("button.wuthering_waves.empty_slot", i + 1) :
                    Component.literal(echo.getName() + " Lv." + echo.getLevel());
            // 在 EchoEquipScreen.init() 的槽位按钮点击事件中
            Button slotButton = Button.builder(buttonText, btn ->
            {
                Minecraft.getInstance().setScreen(new EchoSelectScreen(slotIndex, equippedEcho, 0));
            }).bounds(rightStartX, y, buttonWidth, buttonHeight).build();
            this.addRenderableWidget(slotButton);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        // 1. 绘制背景和所有已注册的组件（按钮等）
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 2. 在固定位置绘制属性加成文字
        EchoAttributeCache cache = ClientAttributeCache.getCache();
        int leftX = 20;   // 固定 X 坐标（与之前相同）
        int startY = 40;          // 固定起始 Y 坐标
        int lineHeight = 20;
        int y = startY;

        guiGraphics.drawString(font, Component.literal("属性加成"), leftX, y, 0xFFFFFF);
        y += lineHeight;
        guiGraphics.drawString(font, Component.literal(String.format("生命: +%.0f +(%.1f%%)", cache.totalHealthFixed, cache.totalHealthPercent * 100)), leftX, y, 0xFFFFFF);
        y += lineHeight;
        guiGraphics.drawString(font, Component.literal(String.format("攻击: +%.0f +(%.1f%%)", cache.totalAttackFixed, cache.totalAttackPercent * 100)), leftX, y, 0xFFFFFF);
        y += lineHeight;
        guiGraphics.drawString(font, Component.literal(String.format("防御: +%.0f +(%.1f%%)", cache.totalDefenseFixed, cache.totalDefensePercent * 100)), leftX, y, 0xFFFFFF);
        y += lineHeight;
        guiGraphics.drawString(font, Component.literal(String.format("暴击率: %.1f%%", cache.totalCritRate * 100)), leftX, y, 0xFFFFFF);
        y += lineHeight;
        guiGraphics.drawString(font, Component.literal(String.format("暴击伤害: +%.1f%%", cache.totalCritDamage * 100)), leftX, y, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void refreshFromCache()
    {
        this.equippedEcho.clear();
        this.equippedEcho.addAll(ClientTerminalDataCache.getEquippedEchoes());
        while (this.equippedEcho.size() < 5)
        {
            this.equippedEcho.add(null);
        }
        // 清空并重新初始化所有组件
        this.clearWidgets();
        this.init();
    }


    public void refreshAttributeDisplay()
    {
        // 属性显示是在 render 方法中实时从 ClientAttributeCache 读取的，
        // 所以不需要做任何事，只需要让界面重绘即可。
    }
}