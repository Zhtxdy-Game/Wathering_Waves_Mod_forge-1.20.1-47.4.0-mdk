package com.ZhongHua.Wuthering_Waves.client.gui;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class EchoSelectScreen extends Screen
{
    private final int slotIndex;
    private final List<EchoInstance> equippedEcho;
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

    private final int currentPageParam; // 新增：传入的当前页数
    private List<Button> slotButtons = new ArrayList<>(); // 保存当前槽位按钮
    private List<EchoListButton> listButtons = new ArrayList<>(); // 保存当前列表按钮

    // 构造函数，增加 currentPage 参数
    public EchoSelectScreen(int slotIndex, List<EchoInstance> equippedEcho, int currentPage)
    {
        super(Component.literal("Select Echo"));
        this.slotIndex = slotIndex;
        this.equippedEcho = equippedEcho;
        this.currentPageParam = currentPage;
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
        if (player != null)
        {
            player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data -> {
                allEcho = data.getEchoList();
            });
        }

        // 测试数据（如果为空）
        if (allEcho == null || allEcho.isEmpty())
        {
            allEcho = EchoInstance.createTestData();
        }
        // 先获取 allEcho 并计算 totalPages
        totalPages = (int) Math.ceil(allEcho.size() / (double)(LIST_ROWS * LIST_COLS));
        if (totalPages == 0) totalPages = 1;

        // 再设置 currentPage 并修正
        this.currentPage = currentPageParam;
        if (this.currentPage >= totalPages) this.currentPage = totalPages - 1;
        if (this.currentPage < 0) this.currentPage = 0;


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
        int rightBtnX = screenWidth - 200;
        Button equipBtn = Button.builder(Component.translatable("button.wuthering_waves.equip"), btn ->
        {
            if (selectedEcho != null)
            {
                equippedEcho.set(slotIndex, selectedEcho);
                refreshSlotButtons(); // 刷新左侧槽位按钮显示
                // 不清空 selectedEcho，允许连续装备多个槽位
            }
        }).bounds(rightBtnX, rightBtnY, 80, 30).build();
        Button cultivateBtn = Button.builder(Component.translatable("button.wuthering_waves.cultivate"), btn -> {}).bounds(rightBtnX + 90, rightBtnY, 80, 30).build();
        this.addRenderableWidget(equipBtn);
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
    private void refreshSlotButtons()
    {
        // 移除旧的槽位按钮
        for (Button btn : slotButtons)
        {
            this.removeWidget(btn);
        }
        slotButtons.clear();

        // 重新添加五个槽位按钮
        for (int i = 0; i < 5; i++)
        {
            final int idx = i;
            int y = slotButtonsStartY + i * (SLOT_BUTTON_HEIGHT + SLOT_GAP);
            EchoInstance echo = (idx < equippedEcho.size()) ? equippedEcho.get(idx) : null;
            Component btnText = (echo == null) ?
                    Component.translatable("button.wuthering_waves.empty_slot", idx+1) :
                    Component.literal(echo.getName() + " Lv." + echo.getLevel());
            Button slotBtn = Button.builder(btnText, button ->
            {
                // 点击槽位按钮时，传递当前页数
                Minecraft.getInstance().setScreen(new EchoSelectScreen(idx, equippedEcho, currentPage));
            }).bounds(slotButtonsStartX, y, SLOT_BUTTON_WIDTH, SLOT_BUTTON_HEIGHT).build();
            this.addRenderableWidget(slotBtn);
            slotButtons.add(slotBtn);
        }
    }

    private void refreshListButtons() {
        // 移除旧的列表按钮
        for (EchoListButton btn : listButtons) {
            this.removeWidget(btn);
        }
        listButtons.clear();

        int start = currentPage * (LIST_ROWS * LIST_COLS);
        int end = Math.min(start + LIST_ROWS * LIST_COLS, allEcho.size());
        List<EchoInstance> pageEcho = allEcho.subList(start, end);

        for (int row = 0; row < LIST_ROWS; row++) {
            for (int col = 0; col < LIST_COLS; col++) {
                int idx = row * LIST_COLS + col;
                if (idx < pageEcho.size()) {
                    EchoInstance echo = pageEcho.get(idx);
                    int x = listStartX + col * (LIST_CELL_WIDTH + LIST_GAP_X);
                    int y = listStartY + row * (LIST_CELL_HEIGHT + LIST_GAP_Y);
                    EchoListButton btn = new EchoListButton(x, y, LIST_CELL_WIDTH, LIST_CELL_HEIGHT, echo, () -> onEchoSelected(echo));
                    this.addRenderableWidget(btn);
                    listButtons.add(btn);
                }
            }
        }
    }

    private void onEchoSelected(EchoInstance echo) {
        this.selectedEcho = echo;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data -> {
                String countStr = data.getCurrentCount() + "/" + data.getMaxCapacity();
                guiGraphics.drawString(this.font, Component.literal(countStr), listStartX, listStartY - 20, 0xFFFFFF);
            });
        }

        if (selectedEcho != null)
        {
            int rightX = this.width * 2 / 3 + 20;
            int rightY = 40;
            int lineHeight = 20;
            guiGraphics.drawString(this.font, Component.literal("名称: " + selectedEcho.getName()), rightX, rightY, 0xFFFFFF);
            guiGraphics.drawString(this.font, Component.literal("COST: " + selectedEcho.getCost()), rightX, rightY + lineHeight, 0xFFFFFF);
            guiGraphics.drawString(this.font, Component.literal("等级: " + selectedEcho.getLevel()), rightX, rightY + lineHeight*2, 0xFFFFFF);
            guiGraphics.drawString(this.font, Component.literal("主属性: " + selectedEcho.getMainStat() + " " + (selectedEcho.getMainStatValue()*100) + "%"), rightX, rightY + lineHeight*3, 0xFFFFFF);
            List<String> subStats = selectedEcho.getSubStats();
            List<Double> subValues = selectedEcho.getSubStatValues();
            for (int i = 0; i < Math.min(5, subStats.size()); i++) {
                guiGraphics.drawString(this.font, Component.literal(subStats.get(i) + " " + (subValues.get(i)*100) + "%"), rightX, rightY + lineHeight*(4+i), 0xFFFFFF);
            }
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
        private static final ResourceLocation DEFAULT_ICON = new ResourceLocation("minecraft", "textures/item/diamond.png");

        public EchoListButton(int x, int y, int width, int height, EchoInstance echo, Runnable onPress) {
            super(x, y, width, height, Component.literal(echo.getName() + " Lv." + echo.getLevel()));
            this.echo = echo;
            this.onPress = onPress;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF444444);
            guiGraphics.blit(DEFAULT_ICON, this.getX() + 5, this.getY() + 5, 0, 0, 16, 16, 16, 16);
            guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + 5, this.getY() + 25, 0xFFFFFF);
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
}