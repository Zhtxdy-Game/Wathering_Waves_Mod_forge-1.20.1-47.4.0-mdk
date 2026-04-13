package com.ZhongHua.Wuthering_Waves.client.gui;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import com.ZhongHua.Wuthering_Waves.echo.EchoSubStat;
import com.ZhongHua.Wuthering_Waves.item.ModItems;
import com.ZhongHua.Wuthering_Waves.network.ClientTerminalDataCache;
import com.ZhongHua.Wuthering_Waves.network.ModNetwork;
import com.ZhongHua.Wuthering_Waves.network.UpgradeEchoRequestPacket;
import com.ZhongHua.Wuthering_Waves.network.UpgradeEchoToMaxRequestPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EchoCultivateScreen extends Screen
{
    // 布局常量（与 EchoSelectScreen 保持一致）
    private static final int LEFT_MARGIN_RATIO = 10;
    private static final int SLOT_BUTTON_WIDTH = 80;
    private static final int SLOT_BUTTON_HEIGHT = 50;
    private static final int SLOT_GAP = 15;
    private static final int LIST_ROWS = 5;
    private static final int LIST_COLS = 3;
    private static final int LIST_CELL_WIDTH = 60;
    private static final int LIST_CELL_HEIGHT = 60;
    private static final int LIST_GAP_X = 10;
    private static final int LIST_GAP_Y = 10;

    // 数据
    private List<EchoInstance> allEcho;               // 所有声骸
    private List<EchoInstance> equippedEcho;          // 装备槽位（5个）
    private int currentPage = 0;
    private int totalPages = 1;
    private int currentSlotIndex = -1;                // 当前选中的槽位（用于显示）
    private EchoInstance selectedEcho = null;         // 当前选中的声骸（用于升级）

    // UI 组件
    private List<Button> slotButtons = new ArrayList<>();
    private List<EchoListButton> listButtons = new ArrayList<>();

    // 坐标变量
    private int slotButtonsStartX, slotButtonsStartY;
    private int listStartX, listStartY;

    // 材料栏位置
    private int materialStartX, materialStartY;
    private static final int MATERIAL_SLOT_SIZE = 24;
    private static final int MATERIAL_GAP = 10;

    //返回EchoSelectScreen界面的参数
    private final int originalSlotIndex;
    private final List<EchoInstance> originalEquippedEcho;
    private final int originalCurrentPage;

    // 按钮
    private Button upgradeBtn, upgradeAllBtn, backBtn;


    // 材料物品列表
    private static final ItemStack[] MATERIALS =
            {
            new ItemStack(ModItems.TUNER.get()),
            new ItemStack(ModItems.SEALED_TUBE_BASIC.get()),
            new ItemStack(ModItems.SEALED_TUBE_MEDIUM.get()),
            new ItemStack(ModItems.SEALED_TUBE_ADVANCED.get()),
            new ItemStack(ModItems.SEALED_TUBE_PREMIUM.get())
            };

    public EchoCultivateScreen(int slotIndex, List<EchoInstance> equippedEcho, int currentPage)
    {
        super(Component.literal("Cultivate Echo"));
        this.originalSlotIndex = slotIndex;
        this.originalEquippedEcho = equippedEcho;
        this.originalCurrentPage = currentPage;
        // 初始化数据
        this.equippedEcho = new ArrayList<>(ClientTerminalDataCache.getEquippedEchoes());
        while (this.equippedEcho.size() < 5) this.equippedEcho.add(null);
        this.allEcho = new ArrayList<>(ClientTerminalDataCache.getEchoList());
    }

    @Override
    protected void init()
    {
        super.init();

        // 计算页面总数
        totalPages = (int) Math.ceil(allEcho.size() / (double)(LIST_ROWS * LIST_COLS));
        if (totalPages == 0) totalPages = 1;
        if (currentPage >= totalPages) currentPage = totalPages - 1;
        if (currentPage < 0) currentPage = 0;

        // 默认选中第一个非空槽位的声骸（如果有）
        if (selectedEcho == null)
        {
            for (int i = 0; i < equippedEcho.size(); i++)
            {
                if (equippedEcho.get(i) != null)
                {
                    currentSlotIndex = i;
                    selectedEcho = equippedEcho.get(i);
                    break;
                }
            }
        }

        int screenWidth = this.width;
        int screenHeight = this.height;

        // 计算左侧区域坐标（与 EchoSelectScreen 一致）
        slotButtonsStartX = screenWidth * LEFT_MARGIN_RATIO / 100 - 70;
        slotButtonsStartY = (screenHeight - (SLOT_BUTTON_HEIGHT * 5 + SLOT_GAP * 4)) / 2;
        listStartX = slotButtonsStartX + SLOT_BUTTON_WIDTH + (screenWidth / 10) - 70;
        listStartY = (screenHeight - (LIST_CELL_HEIGHT * LIST_ROWS + LIST_GAP_Y * (LIST_ROWS-1))) / 2 + 15;

        // 创建左侧槽位按钮
        refreshSlotButtons();

        // 分页按钮
        int pageBtnY = listStartY + LIST_CELL_HEIGHT * LIST_ROWS + LIST_GAP_Y * (LIST_ROWS-1) + 10;
        Button prevPage = Button.builder(Component.literal("<"), btn ->
        {
            if (currentPage > 0)
            {
                currentPage--;
                refreshListButtons();
            }
        }).bounds(listStartX, pageBtnY, 40, 20).build();
        Button nextPage = Button.builder(Component.literal(">"), btn ->
        {
            if (currentPage < totalPages - 1)
            {
                currentPage++;
                refreshListButtons();
            }
        }).bounds(listStartX + LIST_CELL_WIDTH * LIST_COLS + LIST_GAP_X * (LIST_COLS-1) - 40, pageBtnY, 40, 20).build();
        this.addRenderableWidget(prevPage);
        this.addRenderableWidget(nextPage);

        // 刷新声骸列表
        refreshListButtons();

        // 材料栏位置：右侧详情区域下方
        int rightX = this.width * 2 / 3 + 100;
        int rightY = 40 + 20 * (5 + 5); // 假设属性显示占 5 行 + 标题等，动态计算更准确
        // 简化：材料栏放在右侧，Y 坐标固定为 250
        materialStartX = rightX;
        materialStartY = 260;

        // 升级按钮（位置类似装备按钮）
        int btnWidth = 80;
        int btnHeight = 30;
        int btnGap = 10;
        int btnStartX = this.width - 200;  // 右侧区域
        int btnStartY = this.height - 60;

        // 单级升级按钮
        upgradeBtn = Button.builder(Component.translatable("button.wuthering_waves.upgrade"), btn ->
        {
            if (selectedEcho != null)
            {
                ModNetwork.CHANNEL.sendToServer(new UpgradeEchoRequestPacket(selectedEcho.getId()));
            }
        }).bounds(btnStartX, btnStartY, btnWidth, btnHeight).build();

        // 一键升级按钮
        upgradeAllBtn = Button.builder(Component.translatable("button.wuthering_waves.upgrade_all"), btn ->
        {
            if (selectedEcho != null)
            {
                ModNetwork.CHANNEL.sendToServer(new UpgradeEchoToMaxRequestPacket(selectedEcho.getId(), false));
            }
        }).bounds(btnStartX + btnWidth + btnGap, btnStartY, btnWidth, btnHeight).build();
        this.addRenderableWidget(upgradeBtn);
        this.addRenderableWidget(upgradeAllBtn);

        // 返回按钮（右上角）
        backBtn = Button.builder(Component.literal("←"), btn ->
        {
            Minecraft.getInstance().setScreen(new EchoSelectScreen(originalSlotIndex, originalEquippedEcho, originalCurrentPage));
        }).bounds(this.width - 60, 10, 40, 20).build();
        this.addRenderableWidget(backBtn);
    }

    private void refreshSlotButtons()
    {
        for (Button btn : slotButtons) this.removeWidget(btn);
        slotButtons.clear();

        for (int i = 0; i < 5; i++)
        {
            final int idx = i;
            int y = slotButtonsStartY + i * (SLOT_BUTTON_HEIGHT + SLOT_GAP);
            EchoInstance echo = (idx < equippedEcho.size()) ? equippedEcho.get(idx) : null;
            Component btnText = (echo == null) ?
                    Component.translatable("button.wuthering_waves.empty_slot", idx + 1) :
                    Component.literal(echo.getName() + " Lv." + echo.getLevel());
            Button slotBtn = Button.builder(btnText, button ->
            {
                currentSlotIndex = idx;
                selectedEcho = (idx < equippedEcho.size()) ? equippedEcho.get(idx) : null;
                // 不清空列表选中，但可以保留
            }).bounds(slotButtonsStartX, y, SLOT_BUTTON_WIDTH, SLOT_BUTTON_HEIGHT).build();
            this.addRenderableWidget(slotBtn);
            slotButtons.add(slotBtn);
        }
    }

    private void refreshListButtons()
    {
        for (EchoListButton btn : listButtons) this.removeWidget(btn);
        listButtons.clear();

        int start = currentPage * (LIST_ROWS * LIST_COLS);
        int end = Math.min(start + LIST_ROWS * LIST_COLS, allEcho.size());
        List<EchoInstance> pageEcho = allEcho.subList(start, end);

        for (int row = 0; row < LIST_ROWS; row++)
        {
            for (int col = 0; col < LIST_COLS; col++)
            {
                int idx = row * LIST_COLS + col;
                if (idx < pageEcho.size())
                {
                    EchoInstance echo = pageEcho.get(idx);
                    int x = listStartX + col * (LIST_CELL_WIDTH + LIST_GAP_X);
                    int y = listStartY + row * (LIST_CELL_HEIGHT + LIST_GAP_Y);
                    EchoListButton btn = new EchoListButton(x, y, LIST_CELL_WIDTH, LIST_CELL_HEIGHT, echo, () -> onEchoSelected(echo), equippedEcho);
                    this.addRenderableWidget(btn);
                    listButtons.add(btn);
                }
            }
        }
    }

    private void onEchoSelected(EchoInstance echo)
    {
        this.selectedEcho = echo;
        // 不清空槽位选中，仅更新右侧显示
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // 右侧详情区域
        int rightX = this.width * 2 / 3 + 100;
        int rightY = 40;
        int lineHeight = 20;

        if (selectedEcho != null)
        {
            guiGraphics.drawString(this.font, Component.literal("名称: " + selectedEcho.getName()), rightX, rightY, 0xFFFFFF);
            guiGraphics.drawString(this.font, Component.literal("COST: " + selectedEcho.getCost()), rightX, rightY + lineHeight, 0xFFFFFF);
            guiGraphics.drawString(this.font, Component.literal("等级: " + selectedEcho.getLevel()), rightX, rightY + lineHeight * 2, 0xFFFFFF);
            String mainStatDisplay = String.format("%s: %.1f%%", selectedEcho.getMainStat(), selectedEcho.getMainStatValue() * 100);
            guiGraphics.drawString(this.font, Component.literal(mainStatDisplay), rightX, rightY + lineHeight * 3, 0xFFFFFF);

            // 副属性（只显示已解锁）
            List<EchoSubStat> activeSubs = selectedEcho.getActiveSubStats();
            if (!activeSubs.isEmpty())
            {
                guiGraphics.drawString(this.font, Component.literal("辅音属性:"), rightX, rightY + lineHeight * 4, 0xAAAAAA);
                for (int i = 0; i < activeSubs.size(); i++)
                {
                    EchoSubStat sub = activeSubs.get(i);
                    String valueStr = sub.getName().contains("固定") ?
                            String.format("%.0f", sub.getValue()) :
                            String.format("%.1f%%", sub.getValue() * 100);
                    String text = "• " + sub.getName() + " " + valueStr;
                    guiGraphics.drawString(this.font, Component.literal(text), rightX + 10, rightY + lineHeight * (5 + i), 0xFFFFFF);
                }
            } else
            {
                guiGraphics.drawString(this.font, Component.literal("辅音属性: 未解锁"), rightX, rightY + lineHeight * 4, 0x666666);
            }

            // 材料栏
            renderMaterialSlots(guiGraphics);
        } else
        {
            guiGraphics.drawString(this.font, Component.literal("未选中任何声骸"), rightX, rightY, 0xAAAAAA);
        }



    }

    private void renderMaterialSlots(GuiGraphics guiGraphics)
    {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        int x = materialStartX;
        int y = materialStartY;
        int slotSize = MATERIAL_SLOT_SIZE;
        int gap = MATERIAL_GAP;

        for (int i = 0; i < MATERIALS.length; i++)
        {
            ItemStack material = MATERIALS[i];
            // 获取玩家背包中该物品的数量
            int count = player.getInventory().countItem(material.getItem());
            // 绘制格子背景
            guiGraphics.fill(x, y, x + slotSize, y + slotSize, 0xFF333333);
            guiGraphics.renderOutline(x, y, slotSize, slotSize, 0xFFFFFFFF);
            // 绘制物品图标
            guiGraphics.renderItem(material, x, y);
            // 绘制数量
            if (count > 0)
            {
                String countStr = String.valueOf(count);
                guiGraphics.drawString(font, countStr, x + slotSize - 10, y + slotSize - 8, 0xFFFFFF);
            }
            x += slotSize + gap;
        }
    }

    // 自定义按钮类（与 EchoSelectScreen 中的一致）
    private static class EchoListButton extends AbstractWidget
    {
        private final EchoInstance echo;
        private final Runnable onPress;
        private final List<EchoInstance> equippedEcho;
        private final ResourceLocation iconLocation;

        public EchoListButton(int x, int y, int width, int height, EchoInstance echo, Runnable onPress, List<EchoInstance> equippedEcho)
        {
            super(x, y, width, height, Component.literal(echo.getName() + " Lv." + echo.getLevel()));
            this.echo = echo;
            this.onPress = onPress;
            this.equippedEcho = equippedEcho;
            this.iconLocation = EchoIconManager.getIcon(echo.getName());
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
        {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF444444);
            RenderSystem.setShaderTexture(0, iconLocation);
            guiGraphics.blit(iconLocation, this.getX(), this.getY() + 5, 0, 0, 16, 16, 16, 16);
            guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX(), this.getY() + 25, 0xFFFFFF);

            // 检查是否已装备
            int equippedSlot = -1;
            for (int i = 0; i < equippedEcho.size(); i++)
            {
                EchoInstance e = equippedEcho.get(i);
                if (e != null && e.getId().equals(echo.getId()))
                {
                    equippedSlot = i;
                    break;
                }
            }
            if (equippedSlot != -1)
            {
                String text = "已装备 " + (equippedSlot + 1);
                int textWidth = Minecraft.getInstance().font.width(text);
                guiGraphics.drawString(Minecraft.getInstance().font, text,
                        this.getX() + this.width - textWidth - 5, this.getY() + 5, 0xFFFF00);
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            onPress.run();
        }

        @Override
        public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput)
        {
            this.defaultButtonNarrationText(narrationElementOutput);
        }
    }

    public void refreshFromCache()
    {
        // 刷新装备槽位数据
        this.equippedEcho = new ArrayList<>(ClientTerminalDataCache.getEquippedEchoes());
        while (this.equippedEcho.size() < 5) this.equippedEcho.add(null);
        // 刷新声骸列表
        this.allEcho = new ArrayList<>(ClientTerminalDataCache.getEchoList());
        // 重新计算页数
        totalPages = (int) Math.ceil(allEcho.size() / (double)(LIST_ROWS * LIST_COLS));
        if (totalPages == 0) totalPages = 1;
        if (currentPage >= totalPages) currentPage = totalPages - 1;
        if (currentPage < 0) currentPage = 0;
        // 刷新当前选中的声骸（根据ID匹配）
        if (selectedEcho != null)
        {
            EchoInstance newEcho = allEcho.stream()
                    .filter(e -> e.getId().equals(selectedEcho.getId()))
                    .findFirst().orElse(null);
            selectedEcho = newEcho;
        }
        // 刷新UI组件
        refreshSlotButtons();
        refreshListButtons();
        // 材料栏数量会在下一帧 render 中自动从背包读取，无需额外处理
    }

}