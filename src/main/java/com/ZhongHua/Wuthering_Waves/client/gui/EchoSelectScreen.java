package com.ZhongHua.Wuthering_Waves.client.gui;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.capability.PlayerTerminalDataImpl;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import com.ZhongHua.Wuthering_Waves.echo.EchoSubStat;
import com.ZhongHua.Wuthering_Waves.network.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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

        slotButtonsStartX = screenWidth * LEFT_MARGIN_RATIO / 100 -70;
        slotButtonsStartY = (screenHeight - (SLOT_BUTTON_HEIGHT * 5 + SLOT_GAP * 4)) / 2;
        listStartX = slotButtonsStartX + SLOT_BUTTON_WIDTH + (screenWidth / 10) -70;
        listStartY = (screenHeight - (LIST_CELL_HEIGHT * LIST_ROWS + LIST_GAP_Y * (LIST_ROWS-1))) / 2 + 15;

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
        int rightBtnX = screenWidth - 275;
        int btnWidth = 80;
        int gap = 10;
        int totalWidth = btnWidth * 3 + gap * 2;
        int startX = rightBtnX;  // 原来装备按钮的x
        int row1Y = screenHeight - screenHeight / 10 - 30; // 第一行按钮的 Y
        int row2Y = row1Y + 30 + gap; // 第二行按钮的 Y
        Button equipBtn = Button.builder(Component.translatable("button.wuthering_waves.equip"), btn ->
        {
            if (selectedEcho != null)
            {
                // 客户端检查是否已被装备
                int existingSlot = -1;
                for (int i = 0; i < equippedEcho.size(); i++)
                {
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

                // 客户端 COST 预检查
                int currentTotal = 0;
                for (EchoInstance e : equippedEcho)
                {
                    if (e != null) currentTotal += e.getCost();
                }
                EchoInstance old = equippedEcho.get(currentSlotIndex);
                int oldCost = (old != null) ? old.getCost() : 0;
                int newTotal = currentTotal - oldCost + selectedEcho.getCost();

                if (newTotal > 12)
                {  // 或者从配置读取 getMaxTotalCost()
                    player.displayClientMessage(Component.literal("总 Cost 超过上限 (12)，无法装备"), true);
                    return;  // 直接返回，不发送网络包
                }

                // 发送请求（移除乐观更新，只发送请求）
                ModNetwork.CHANNEL.sendToServer(new EquipEchoRequestPacket(currentSlotIndex, selectedEcho));

            }
        }).bounds(startX, rightBtnY, btnWidth, 30).build();

        Button unEquipBtn = Button.builder(Component.translatable("button.wuthering_waves.unequip"), btn ->
        {
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
        {
            Minecraft.getInstance().setScreen(new EchoCultivateScreen(slotIndex, equippedEcho, currentPage));
        }).bounds(rightBtnX + (btnWidth + gap) * 2, rightBtnY, btnWidth, 30).build();

        // 第一行：装备、卸下、培养
        this.addRenderableWidget(equipBtn);
        this.addRenderableWidget(unEquipBtn);
        this.addRenderableWidget(cultivateBtn);

        // 第二行：删除按钮（放在卸下按钮正下方）
        Button deleteBtn = Button.builder(Component.translatable("button.wuthering_waves.delete"), btn ->
        {
            if (displayEcho != null)
            {
                ConfirmScreen confirmScreen = new ConfirmScreen(
                        confirmed ->
                        {
                            if (confirmed)
                            {
                                UUID deletedId = displayEcho.getId();

                                // 发送删除请求
                                ModNetwork.CHANNEL.sendToServer(new DeleteEchoRequestPacket(deletedId));

                                // 本地乐观更新
                                allEcho.removeIf(echo -> echo.getId().equals(deletedId));
                                if (displayEcho != null && displayEcho.getId().equals(deletedId))
                                {
                                    displayEcho = null;
                                }
                                if (selectedEcho != null && selectedEcho.getId().equals(deletedId))
                                {
                                    selectedEcho = null;
                                }

                                // 关键：删除后返回到 EchoSelectScreen（刷新数据）
                                Minecraft.getInstance().setScreen(
                                        new EchoSelectScreen(currentSlotIndex, equippedEcho, currentPage)
                                );
                            } else
                            {
                                // 用户取消删除，也要返回原界面
                                Minecraft.getInstance().setScreen(EchoSelectScreen.this);
                            }
                        },
                        Component.literal("确认删除"),
                        Component.literal("确定要删除声骸 " + displayEcho.getName() + " 吗？")
                );
                Minecraft.getInstance().setScreen(confirmScreen);
            }
        }).bounds(startX + btnWidth + gap, row2Y, btnWidth, 30).build();
        this.addRenderableWidget(deleteBtn);

        // 添加返回按钮（右上角）
        int backBtnX = screenWidth - 60;
        int backBtnY = 10;
        Button backButton = Button.builder(Component.literal("←"), btn ->
        {
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
        if (player != null)
        {
            // 显示已有声骸数量 / 容量
            int currentCount = ClientTerminalDataCache.getEchoList().size();
            int maxCapacity = ClientTerminalDataCache.getMaxCapacity();
            String countStr = currentCount + "/" + maxCapacity;
            guiGraphics.drawString(this.font, Component.literal(countStr), listStartX, listStartY - 20, 0xFFFFFF);

            int totalCost = ClientTerminalDataCache.getTotalEquippedCost();
            int maxCost = 12;
            guiGraphics.drawString(font, Component.literal("Cost: " + totalCost + "/" + maxCost), listStartX -80, listStartY -20, 0xFFFFFF);
        }

        // 右侧详情区域
        int rightX = this.width * 2 / 3 + 100;
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
            // 副属性 - 只显示已解锁的（营造盲盒感）
            List<EchoSubStat> activeSubs = displayEcho.getActiveSubStats(); // 只获取已解锁的
            if (!activeSubs.isEmpty())
            {
                guiGraphics.drawString(this.font, Component.literal("辅音属性:"),
                        rightX, rightY + lineHeight * 4, 0xAAAAAA);

                for (int i = 0; i < activeSubs.size(); i++)
                {
                    EchoSubStat sub = activeSubs.get(i);
                    String valueStr;
                    if (sub.getName().contains("固定"))
                    {
                        valueStr = String.format("%.0f", sub.getValue());
                    } else
                    {
                        valueStr = String.format("%.1f%%", sub.getValue() * 100);
                    }
                    String text = "• " + sub.getName() + " " + valueStr;
                    // 已解锁的使用白色高亮显示
                    guiGraphics.drawString(this.font, Component.literal(text), rightX + 10, rightY + lineHeight * (5 + i), 0xFFFFFF);
                }
            } else
            {
                // 0级时显示提示
                guiGraphics.drawString(this.font, Component.literal("辅音属性: 未解锁"), rightX, rightY + lineHeight * 4, 0x666666);
            }
        } else
        {
            // 未装备任何声骸
            guiGraphics.drawString(this.font, Component.literal("未装备"), rightX, rightY, 0xAAAAAA);
        }
        //3D模型
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
        // 移除静态默认图标，改为动态获取
        private final ResourceLocation iconLocation;

        public EchoListButton(int x, int y, int width, int height, EchoInstance echo, Runnable onPress, List<EchoInstance> equippedEcho)
        {
            super(x, y, width, height, Component.literal(echo.getName() + " Lv." + echo.getLevel()));
            this.echo = echo;
            this.onPress = onPress;
            this.equippedEcho = equippedEcho;
            // 根据声骸名称获取对应图标
            this.iconLocation = EchoIconManager.getIcon(echo.getName());
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
        {
            // 绘制背景
            guiGraphics.fill(this.getX() , this.getY() , this.getX() + this.width , this.getY() + this.height, 0xFF444444);
            // 绘制声骸专属图标（使用自定义纹理）
            RenderSystem.setShaderTexture(0, iconLocation);
            guiGraphics.blit(iconLocation, this.getX() , this.getY() + 5,
                    0, 0, 16, 16, 16, 16);
            // 绘制名称和等级
            guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() , this.getY() + 25, 0xFFFFFF);

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
        this.equippedEcho.clear();
        this.equippedEcho.addAll(ClientTerminalDataCache.getEquippedEchoes());
        while (this.equippedEcho.size() < 5) this.equippedEcho.add(null);
        // 重新计算当前显示的声骸
        if (currentSlotIndex >= 0 && currentSlotIndex < equippedEcho.size())
        {
            this.displayEcho = equippedEcho.get(currentSlotIndex);
        } else {
            this.displayEcho = null;
        }
        refreshSlotButtons();
        refreshListButtons();// 刷新列表，更新“已装备”显示
    }

}