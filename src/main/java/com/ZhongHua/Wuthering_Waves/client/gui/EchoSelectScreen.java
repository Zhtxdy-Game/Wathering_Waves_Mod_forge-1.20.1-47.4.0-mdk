package com.ZhongHua.Wuthering_Waves.client.gui;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.capability.PlayerTerminalDataImpl;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import com.ZhongHua.Wuthering_Waves.echo.EchoSubStat;
import com.ZhongHua.Wuthering_Waves.network.ClientTerminalDataCache;
import com.ZhongHua.Wuthering_Waves.network.EquipEchoRequestPacket;
import com.ZhongHua.Wuthering_Waves.network.ModNetwork;
import com.ZhongHua.Wuthering_Waves.network.UnEquipEchoRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class EchoSelectScreen extends Screen
{
    private final int slotIndex;
    private final List<EchoInstance> equippedEcho;
    private int currentSlotIndex;          // 当前选中的槽位（0-4）
    private EchoInstance displayEcho;      // 当前右侧显示的声骸（可能为 null）
    private final int currentPageParam; // 新增：传入的当前页数
    private List<Button> slotButtons = new ArrayList<>(); // 保存当前槽位按钮
    private List<EchoListButton> listButtons = new ArrayList<>(); // 保存当前列表按钮
    // 移除 onEquipChanged，因为装备后不再自动返回

    private List<EchoInstance> allEcho = new ArrayList<>();
    private int currentPage = 0;
    private int totalPages = 1;
    private EchoInstance selectedEcho = null;

    private static final int LEFT_MARGIN_RATIO = 10;
    private static final int SLOT_BUTTON_WIDTH = 80;
    private static final int SLOT_BUTTON_HEIGHT = 50;
    private static final int SLOT_GAP = 15;
    private static final int LIST_COLS = 3;
    private static final int LIST_ROWS = 5;
    private static final int LIST_CELL_WIDTH = 60;
    private static final int LIST_CELL_HEIGHT = 60;
    private static final int LIST_GAP_X = 10;
    private static final int LIST_GAP_Y = 10;

    private int slotButtonsStartX, slotButtonsStartY;
    private int listStartX, listStartY;



    // 构造函数，增加 currentPage 参数
    public EchoSelectScreen(int slotIndex, List<EchoInstance> equippedEcho, int currentPage)
    {
        super(Component.literal("Select Echo"));
        this.slotIndex = slotIndex;
        this.equippedEcho = equippedEcho;
        this.currentPageParam = currentPage;
        this.currentSlotIndex = slotIndex;  // 初始选中传入的槽位
    }

    // 为了兼容原来的调用，可以保留无参版本，默认0
    public EchoSelectScreen(int slotIndex, List<EchoInstance> equippedEcho)
    {
        this(slotIndex, equippedEcho, 0);
    }

    @Override
    protected void init()
    {
        super.init();
        Player player = Minecraft.getInstance().player;
        allEcho = ClientTerminalDataCache.getEchoList();

        if (allEcho == null || allEcho.isEmpty())
        {
            allEcho = new ArrayList<>();  // 空列表，不再生成测试数据
        }

        // 先获取 allEcho 并计算 totalPages
        totalPages = (int) Math.ceil(allEcho.size() / (double)(LIST_ROWS * LIST_COLS));
        if (totalPages == 0) totalPages = 1;

        // 再设置 currentPage 并修正
        this.currentPage = currentPageParam;
        if (this.currentPage >= totalPages) this.currentPage = totalPages - 1;
        if (this.currentPage < 0) this.currentPage = 0;

        // 初始化显示声骸
        this.displayEcho = (currentSlotIndex >= 0 && currentSlotIndex < equippedEcho.size())
                ? equippedEcho.get(currentSlotIndex) : null;


        int screenWidth = this.width;
        int screenHeight = this.height;

        slotButtonsStartX = screenWidth * LEFT_MARGIN_RATIO / 100;
        slotButtonsStartY = (screenHeight - (SLOT_BUTTON_HEIGHT * 5 + SLOT_GAP * 4)) / 2;
        listStartX = slotButtonsStartX + SLOT_BUTTON_WIDTH + (screenWidth / 10);
        listStartY = (screenHeight - (LIST_CELL_HEIGHT * LIST_ROWS + LIST_GAP_Y * (LIST_ROWS-1))) / 2;

        // 创建左侧槽位按钮（动态刷新）
        refreshSlotButtons();

        // 分页按钮（点击时需要传递当前页数）
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

        // 装备按钮（不再关闭界面，只更新数据并刷新槽位）
        int rightBtnY = screenHeight - screenHeight / 10 - 30;
        int rightBtnX = screenWidth - 300;
        int btnWidth = 80;
        int gap = 10;
        int totalWidth = btnWidth * 3 + gap * 2;
        int startX = rightBtnX;  // 原来装备按钮的x
        Button equipBtn = Button.builder(Component.translatable("button.wuthering_waves.equip"), btn ->
        {
            if (selectedEcho != null)
            {

                // 客户端检查是否已被装备
                int existingSlot = -1;
                for (int i = 0; i < equippedEcho.size(); i++) {
                    EchoInstance e = equippedEcho.get(i);
                    if (e != null && e.getId().equals(selectedEcho.getId()))
                    {
                        existingSlot = i;
                        break;
                    }
                }
                if (existingSlot != -1 && existingSlot != currentSlotIndex)
                {
                    // 提示玩家
                    Minecraft.getInstance().player.displayClientMessage(Component.literal("该声骸已装备在槽位 " + (existingSlot + 1)), true);
                    return;
                }

                // 发送装备请求包，使用 currentSlotIndex
                ModNetwork.CHANNEL.sendToServer(new EquipEchoRequestPacket(currentSlotIndex, selectedEcho));
                // 立即更新本地列表（乐观更新）
                equippedEcho.set(currentSlotIndex, selectedEcho);
                // 刷新槽位按钮显示
                refreshSlotButtons();
                // 更新右侧显示为刚装备的声骸
                displayEcho = selectedEcho;
                // 可选：清空列表选中状态
                selectedEcho = null;
                // 提示装备成功（可选）
                // 不需要关闭界面，让玩家可以继续装备其他槽位
            }
        }).bounds(startX, rightBtnY, btnWidth, 30).build();

        Button unEquipBtn = Button.builder(Component.translatable("button.wuthering_waves.unequip"), btn -> {
            // 卸下逻辑
            if (currentSlotIndex >= 0 && currentSlotIndex < equippedEcho.size())
            {
                // 如果该槽位有声骸，发送卸下请求
                if (equippedEcho.get(currentSlotIndex) != null)
                {
                    ModNetwork.CHANNEL.sendToServer(new UnEquipEchoRequestPacket(currentSlotIndex));
                    // 乐观更新本地列表
                    equippedEcho.set(currentSlotIndex, null);
                    refreshSlotButtons();
                    displayEcho = null;
                    selectedEcho = null;
                    // 刷新列表按钮（如果列表中有该声骸，需要移除“已装备”标记）
                    refreshListButtons();
                } else
                {
                    // 提示无装备
                    if (Minecraft.getInstance().player != null)
                        Minecraft.getInstance().player.displayClientMessage(Component.literal("该槽位没有装备声骸"), true);
                }
            }
        }).bounds(startX + btnWidth + gap, rightBtnY, btnWidth, 30).build();

        Button cultivateBtn = Button.builder(Component.translatable("button.wuthering_waves.cultivate"), btn ->
        {}).bounds(startX + (btnWidth + gap) * 2, rightBtnY, btnWidth, 30).build();
        this.addRenderableWidget(equipBtn);
        this.addRenderableWidget(unEquipBtn);
        this.addRenderableWidget(cultivateBtn);

        // 添加返回按钮（右上角）
        int backBtnX = screenWidth - 60;
        int backBtnY = 10;
        Button backButton = Button.builder(Component.literal("←"), btn -> {
            Minecraft.getInstance().setScreen(new EchoEquipScreen());
        }).bounds(backBtnX, backBtnY, 40, 20).build();
        this.addRenderableWidget(backButton);

        refreshListButtons();
    }

    // 刷新左侧五个槽位按钮
    private void refreshSlotButtons() {
        // 移除旧的槽位按钮
        for (Button btn : slotButtons) {
            this.removeWidget(btn);
        }
        slotButtons.clear();

        // 重新添加五个槽位按钮
        for (int i = 0; i < 5; i++) {
            final int idx = i;
            int y = slotButtonsStartY + i * (SLOT_BUTTON_HEIGHT + SLOT_GAP);
            EchoInstance echo = (idx < equippedEcho.size()) ? equippedEcho.get(idx) : null;
            Component btnText = (echo == null) ?
                    Component.translatable("button.wuthering_waves.empty_slot", idx + 1) :
                    Component.literal(echo.getName() + " Lv." + echo.getLevel());
            Button slotBtn = Button.builder(btnText, button ->
            {
                // 点击槽位按钮：切换当前选中的槽位，并显示该槽位上的声骸
                currentSlotIndex = idx;
                displayEcho = (idx < equippedEcho.size()) ? equippedEcho.get(idx) : null;
                // 可选：清除列表选中状态（selectedEcho = null），以便突出显示槽位上的声骸
                selectedEcho = null;
            }).bounds(slotButtonsStartX, y, SLOT_BUTTON_WIDTH, SLOT_BUTTON_HEIGHT).build();
            this.addRenderableWidget(slotBtn);
            slotButtons.add(slotBtn);
        }
    }

    private void refreshListButtons()
    {
        // 移除旧的列表按钮
        for (EchoListButton btn : listButtons)
        {
            this.removeWidget(btn);
        }
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
        this.displayEcho = echo;   // 右侧显示选中的声骸
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data ->
            {
                String countStr = data.getCurrentCount() + "/" + data.getMaxCapacity();
                guiGraphics.drawString(this.font, Component.literal(countStr), listStartX, listStartY - 20, 0xFFFFFF);
            });
        }

        // 右侧详情区域
        int rightX = this.width * 2 / 3 + 20;
        int rightY = 40;
        int lineHeight = 20;

        if (displayEcho != null)
        {
            guiGraphics.drawString(this.font, Component.literal("名称: " + displayEcho.getName()), rightX, rightY, 0xFFFFFF);
            guiGraphics.drawString(this.font, Component.literal("COST: " + displayEcho.getCost()), rightX, rightY + lineHeight, 0xFFFFFF);
            guiGraphics.drawString(this.font, Component.literal("等级: " + displayEcho.getLevel()), rightX, rightY + lineHeight * 2, 0xFFFFFF);
            // 主属性
            String mainStatDisplay = String.format("%s: %.1f%%", displayEcho.getMainStat(), displayEcho.getMainStatValue() * 100);
            guiGraphics.drawString(this.font, Component.literal(mainStatDisplay), rightX, rightY + lineHeight * 3, 0xFFFFFF);
            // 副属性（显示全部，但已解锁高亮）
            List<EchoSubStat> allSubs = displayEcho.getAllSubStats();
            for (int i = 0; i < allSubs.size(); i++)
            {
                EchoSubStat sub = allSubs.get(i);
                String valueStr;
                if (sub.getName().contains("固定"))
                {
                    valueStr = String.format("%.0f", sub.getValue());
                } else
                {
                    valueStr = String.format("%.1f%%", sub.getValue() * 100);
                }
                String text = sub.getName() + " " + valueStr;
                int color = (i < displayEcho.getLevel()) ? 0xFFFFFF : 0x888888;
                guiGraphics.drawString(this.font, Component.literal(text), rightX, rightY + lineHeight * (4 + i), color);
            }
        } else
        {
            // 未装备任何声骸
            guiGraphics.drawString(this.font, Component.literal("未装备"), rightX, rightY, 0xAAAAAA);
        }

        int midX = this.width / 3;
        int midWidth = this.width / 3;
        int midY = this.height / 4;
        int midHeight = this.height / 2;
        guiGraphics.renderOutline(midX, midY, midWidth, midHeight, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, Component.literal("3D Model"), midX + midWidth/2, midY + midHeight/2, 0xAAAAAA);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    // 自定义控件（保持不变）
    private static class EchoListButton extends AbstractWidget
    {
        private final EchoInstance echo;
        private final Runnable onPress;
        private final List<EchoInstance> equippedEcho; // 外部装备列表引用
        private static final ResourceLocation DEFAULT_ICON = new ResourceLocation("minecraft", "textures/item/diamond.png");

        public EchoListButton(int x, int y, int width, int height, EchoInstance echo, Runnable onPress, List<EchoInstance> equippedEcho)
        {
            super(x, y, width, height, Component.literal(echo.getName() + " Lv." + echo.getLevel()));
            this.echo = echo;
            this.onPress = onPress;
            this.equippedEcho = equippedEcho;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            // 绘制背景
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF444444);
            // 绘制图标
            guiGraphics.blit(DEFAULT_ICON, this.getX() + 5, this.getY() + 5, 0, 0, 16, 16, 16, 16);
            // 绘制名称和等级
            guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + 5, this.getY() + 25, 0xFFFFFF);

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
            if (equippedSlot != -1) {
                String text = "已装备 " + (equippedSlot + 1);
                int textWidth = Minecraft.getInstance().font.width(text);
                guiGraphics.drawString(Minecraft.getInstance().font, text,
                        this.getX() + this.width - textWidth - 5, this.getY() + 5, 0xFFFF00);
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            onPress.run();
        }

        @Override
        public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput narrationElementOutput) {
            this.defaultButtonNarrationText(narrationElementOutput);
        }
    }

    public void refreshFromCache()
    {
        this.equippedEcho.clear();
        this.equippedEcho.addAll(ClientTerminalDataCache.getEquippedEchoes());
        while (this.equippedEcho.size() < 5) this.equippedEcho.add(null);
        this.displayEcho = (currentSlotIndex >= 0 && currentSlotIndex < equippedEcho.size())
                ? equippedEcho.get(currentSlotIndex) : null;
        refreshSlotButtons();
        refreshListButtons(); // 刷新列表，更新“已装备”显示
    }

}