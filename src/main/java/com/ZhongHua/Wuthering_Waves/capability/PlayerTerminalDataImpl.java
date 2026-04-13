package com.ZhongHua.Wuthering_Waves.capability;

import com.ZhongHua.Wuthering_Waves.Config;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import com.ZhongHua.Wuthering_Waves.item.ModItems;
import com.ZhongHua.Wuthering_Waves.network.ModNetwork;
import com.ZhongHua.Wuthering_Waves.network.SyncAttributeCachePacket;
import com.ZhongHua.Wuthering_Waves.network.SyncTerminalDataPacket;
import com.ZhongHua.Wuthering_Waves.network.UpgradeConfirmPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class PlayerTerminalDataImpl implements IPlayerTerminalData
{
    private List<EchoInstance> echoList = new ArrayList<>();
    private List<EchoInstance> equippedEchoes = new ArrayList<>(Arrays.asList(null, null, null, null, null));
    private EchoAttributeCache attributeCache = new EchoAttributeCache();
    // 不在字段初始化时读取配置，改为在需要时获取
    private int cachedMaxCapacity = -1;  // 缓存值，-1表示未加载
    // 固定 UUID 用于修饰器
    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("a1b2c3d4-1234-5678-9abc-def012345678");
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("b2c3d4e5-2345-6789-0bcd-ef1234567890");

    public void syncToClient(ServerPlayer player)
    {
        // 方式1：如果 ModNetwork 中定义了 sendToPlayer 静态方法
        ModNetwork.sendToPlayer(new SyncTerminalDataPacket(this.serializeNBT()), player);

        // 方式2：直接使用 CHANNEL（需确保 ModNetwork.CHANNEL 是 public static）
        // ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
        //         new SyncTerminalDataPacket(this.serializeNBT()));
    }
    // 获取最大容量，延迟从 Config 读取
    private int getConfiguredMaxCapacity()
    {
        if (cachedMaxCapacity == -1)
        {
            // 确保 Config.SPEC 已加载，如果没有则使用默认值100
            try
            {
                cachedMaxCapacity = Config.TERMINAL_MAX_CAPACITY.get();
            } catch (Exception e)
            {
                cachedMaxCapacity = 200; // 回退默认值
            }
        }
        return cachedMaxCapacity;
    }

    @Override
    public int getMaxCapacity()
    {
        return Config.TERMINAL_MAX_CAPACITY.get();
    }

    @Override
    public void setMaxCapacity(int capacity)
    {
        // 允许动态设置容量，但注意此值不会持久化到 Config，仅用于运行时
        // 如果需要持久化，请在 NBT 中保存
        this.cachedMaxCapacity = capacity;
        // 截断超出容量的部分
        while (echoList.size() > capacity)
        {
            echoList.remove(echoList.size() - 1);
        }
    }

    @Override
    public List<EchoInstance> getEchoList()
    {
        return echoList;
    }

    @Override
    public void setEchoList(List<EchoInstance> list)
    {
        this.echoList = list != null ? list : new ArrayList<>();
        int max = getMaxCapacity();
        while (this.echoList.size() > max)
        {
            this.echoList.remove(this.echoList.size() - 1);
        }
    }

    @Override
    public boolean addEcho(EchoInstance instance)
    {
        if (echoList.size() >= getMaxCapacity())
        {
            return false;
        }
        echoList.add(instance);
        return true;
    }

    @Override
    public boolean removeEcho(int index)
    {
        if (index >= 0 && index < echoList.size())
        {
            echoList.remove(index);
            return true;
        }
        return false;
    }

    @Override
    public int getCurrentCount()
    {
        return echoList.size();
    }

    @Override
    public List<EchoInstance> getEquippedEchoes()
    {
        return equippedEchoes;
    }

    // 注意：equipEcho 和 unequipEcho 不再自动触发 recalculate，由调用者决定
    @Override
    public void equipEcho(int slot, EchoInstance echo)
    {
        if (slot >= 0 && slot < 5)
        {
            equippedEchoes.set(slot, echo);
        }
    }

    @Override
    public void unequipEcho(int slot)
    {
        if (slot >= 0 && slot < 5)
        {
            equippedEchoes.set(slot, null);
        }
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        // 保存 echoList
        ListTag listTag = new ListTag();
        for (EchoInstance echo : echoList)
        {
            listTag.add(echo.toNBT());
        }
        tag.put("EchoList", listTag);

        // 保存 equippedEchoes
        ListTag equipTag = new ListTag();
        for (EchoInstance echo : equippedEchoes)
        {
            if (echo != null)
            {
                equipTag.add(echo.toNBT());
            } else
            {
                equipTag.add(new CompoundTag());
            }
        }
        tag.put("EquippedEchoes", equipTag);
        tag.putInt("MaxCapacity", getMaxCapacity());
        return tag;
    }


    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        // 读取 echoList
        ListTag listTag = nbt.getList("EchoList", Tag.TAG_COMPOUND);
        List<EchoInstance> newList = new ArrayList<>();
        for (int i = 0; i < listTag.size(); i++)
        {
            CompoundTag entry = listTag.getCompound(i);
            EchoInstance echo = EchoInstance.fromNBT(entry);
            if (echo != null) newList.add(echo);
        }
        this.echoList = newList;

        // 读取 equippedEchoes
        ListTag equipTag = nbt.getList("EquippedEchoes", Tag.TAG_COMPOUND);
        List<EchoInstance> newEquip = new ArrayList<>();
        for (int i = 0; i < equipTag.size() && i < 5; i++)
        {
            CompoundTag entry = equipTag.getCompound(i);
            if (entry.isEmpty())
            {
                newEquip.add(null);
            } else
            {
                newEquip.add(EchoInstance.fromNBT(entry));
            }
        }
        while (newEquip.size() < 5) newEquip.add(null);
        this.equippedEchoes = newEquip;

        // 容量处理
        if (nbt.contains("MaxCapacity", Tag.TAG_INT))
        {
            this.cachedMaxCapacity = nbt.getInt("MaxCapacity");
        } else
        {
            this.cachedMaxCapacity = -1;
        }
        int max = getMaxCapacity();
        while (this.echoList.size() > max)
        {
            this.echoList.remove(this.echoList.size() - 1);
        }
    }

    @Override
    public EchoAttributeCache getAttributeCache()
    {
        return attributeCache;
    }

    @Override
    public void recalculateAttributes(ServerPlayer player)
    {


        if (player == null) return;
        // 重置缓存
        attributeCache.reset();

        // 累加属性
        for (EchoInstance echo : equippedEchoes)
        {
            if (echo == null) continue;
            Map<String, Double> stats = echo.getStats();
            attributeCache.totalHealthFixed += stats.getOrDefault("health_fixed", 0.0);
            attributeCache.totalHealthPercent += stats.getOrDefault("health_percent", 0.0);
            attributeCache.totalAttackFixed += stats.getOrDefault("attack_fixed", 0.0);
            attributeCache.totalAttackPercent += stats.getOrDefault("attack_percent", 0.0);
            attributeCache.totalDefenseFixed += stats.getOrDefault("defense_fixed", 0.0);
            attributeCache.totalDefensePercent += stats.getOrDefault("defense_percent", 0.0);
            attributeCache.totalCritRate += stats.getOrDefault("crit_rate", 0.0);
            attributeCache.totalCritDamage += stats.getOrDefault("crit_damage", 0.0);
        }

        // 暴击率上限
        if (attributeCache.totalCritRate > 1.0) attributeCache.totalCritRate = 1.0;

        // 应用修饰器
        applyAttributeModifiers(player);

        // 同步到客户端
        syncAttributeCacheToClient(player);
    }

    private void applyAttributeModifiers(ServerPlayer player)
    {
        AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);

        if (healthAttr != null)
        {
            double base = healthAttr.getBaseValue();
            double finalVal = (base + attributeCache.totalHealthFixed) * (1 + attributeCache.totalHealthPercent);
            double delta = finalVal - base;

            healthAttr.removeModifier(HEALTH_MODIFIER_UUID);
            if (Math.abs(delta) > 0.001)
            {
                healthAttr.addTransientModifier(new AttributeModifier(
                        HEALTH_MODIFIER_UUID, "echo_health", delta, AttributeModifier.Operation.ADDITION));
            }

            // 截断当前生命
            if (player.getHealth() > finalVal)
            {
                player.setHealth((float) finalVal);
            }
        }

        if (armorAttr != null)
        {
            double base = armorAttr.getBaseValue();
            double finalVal = (base + attributeCache.totalDefenseFixed) * (1 + attributeCache.totalDefensePercent);
            double delta = finalVal - base;

            armorAttr.removeModifier(ARMOR_MODIFIER_UUID);
            if (Math.abs(delta) > 0.001)
            {
                armorAttr.addTransientModifier(new AttributeModifier(
                        ARMOR_MODIFIER_UUID, "echo_armor", delta, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    private void syncAttributeCacheToClient(ServerPlayer player)
    {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new SyncAttributeCachePacket(attributeCache.serializeNBT()));
    }

    @Override
    public int getTotalEquippedCost()
    {
        int total = 0;
        for (EchoInstance echo : equippedEchoes)
        {
            if (echo != null) total += echo.getCost();
        }
        return total;
    }

    @Override
    public int getMaxTotalCost()
    {
        return 12; // 可配置
    }

    // 添加一个根据 ID 查找声骸的方法
    @Override
    public EchoInstance getEchoById(UUID id) {
        for (EchoInstance echo : echoList) {
            if (echo.getId().equals(id)) return echo;
        }
        return null;
    }

    // 单级升级
    @Override
    public boolean upgradeEcho(ServerPlayer player, EchoInstance echo)
    {
        // 验证声骸是否仍属于该玩家（防止已被删除）
        if (!echoList.contains(echo))
        {
            player.sendSystemMessage(Component.literal("声骸不存在或已被删除"));
            return false;
        }

        int currentLevel = echo.getLevel();
        if (currentLevel >= 5)
        {
            player.sendSystemMessage(Component.literal("声骸已达最高等级"));
            return false;
        }

        // 获取所需材料
        int tunerNeeded = 5;
        int tubeNeeded = 0;
        int tubeTier = 0; // 1=初级,2=中级,3=高级,4=特级
        switch (currentLevel) {
            case 0:
                tubeNeeded = 0;
                break;
            case 1:
                tubeNeeded = 5;
                tubeTier = 1;
                break;
            case 2:
                tubeNeeded = 5;
                tubeTier = 2;
                break;
            case 3:
                tubeNeeded = 5;
                tubeTier = 3;
                break;
            case 4:
                tubeNeeded = 5;
                tubeTier = 4;
                break;
        }

        // 检查背包材料
        Inventory inv = player.getInventory();
        if (inv.countItem(ModItems.TUNER.get()) < tunerNeeded) {
            player.sendSystemMessage(Component.literal("调谐器不足，需要 " + tunerNeeded + " 个"));
            return false;
        }
        Item requiredTube = getTubeItemByTier(tubeTier);
        if (tubeNeeded > 0 && inv.countItem(requiredTube) < tubeNeeded) {
            player.sendSystemMessage(Component.literal("密音筒不足，需要 " + tubeNeeded + " 个"));
            return false;
        }

        // 扣除材料
        removeItems(player, ModItems.TUNER.get(), tunerNeeded);
        if (tubeNeeded > 0) {
            removeItems(player, requiredTube, tubeNeeded);
        }

        // 升级声骸
        echo.setLevel(currentLevel + 1);
        // 重新计算属性（setLevel 内部已调用 buildStats）
        // 重新计算装备总属性
        recalculateAttributes(player);
        player.sendSystemMessage(Component.literal("声骸升级成功！当前等级 " + echo.getLevel()));
        return true;
    }

    // 一键升级（可确认）
    @Override
    public void upgradeEchoToMax(ServerPlayer player, EchoInstance echo, boolean confirmed)
    {
        int startLevel = echo.getLevel();
        if (startLevel >= 5)
        {
            player.sendSystemMessage(Component.literal("声骸已达最高等级"));
            return;
        }

        Inventory inv = player.getInventory();

        // 获取实际拥有材料数
        int haveTuner = inv.countItem(ModItems.TUNER.get());
        int haveBasic = inv.countItem(ModItems.SEALED_TUBE_BASIC.get());
        int haveMedium = inv.countItem(ModItems.SEALED_TUBE_MEDIUM.get());
        int haveAdvanced = inv.countItem(ModItems.SEALED_TUBE_ADVANCED.get());
        int havePremium = inv.countItem(ModItems.SEALED_TUBE_PREMIUM.get());

        // 计算最多能升到几级
        int maxLevel = startLevel;
        int needTuner = 0, needBasic = 0, needMedium = 0, needAdvanced = 0, needPremium = 0;

        for (int lvl = startLevel; lvl < 5; lvl++) {
            boolean can = true;
            int tmpTuner = needTuner + 5;

            if (tmpTuner > haveTuner)
            {
                can = false;
            } else
            {
                switch (lvl)
                {
                    case 1:
                        if (needBasic + 5 > haveBasic) can = false;
                        else needBasic += 5;
                        break;
                    case 2:
                        if (needMedium + 5 > haveMedium) can = false;
                        else needMedium += 5;
                        break;
                    case 3:
                        if (needAdvanced + 5 > haveAdvanced) can = false;
                        else needAdvanced += 5;
                        break;
                    case 4:
                        if (needPremium + 5 > havePremium) can = false;
                        else needPremium += 5;
                        break;
                }
            }

            if (can)
            {
                needTuner = tmpTuner;
                maxLevel = lvl + 1;
            } else
            {
                break;
            }
        }

        if (maxLevel == startLevel)
        {
            player.sendSystemMessage(Component.literal("材料不足，无法升级"));
            return;
        }

        // 如果到不了5级且未确认，发送确认请求
        if (maxLevel < 5 && !confirmed)
        {
            Map<String, Integer> materials = new HashMap<>();
            materials.put("tuner", needTuner);
            materials.put("basic", needBasic);
            materials.put("medium", needMedium);
            materials.put("advanced", needAdvanced);
            materials.put("premium", needPremium);

            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new UpgradeConfirmPacket(echo.getId(), maxLevel, materials));
            return;
        }

        // 执行升级（到 maxLevel 或 5）
        // 扣除材料
        removeItems(player, ModItems.TUNER.get(), needTuner);
        if (needBasic > 0) removeItems(player, ModItems.SEALED_TUBE_BASIC.get(), needBasic);
        if (needMedium > 0) removeItems(player, ModItems.SEALED_TUBE_MEDIUM.get(), needMedium);
        if (needAdvanced > 0) removeItems(player, ModItems.SEALED_TUBE_ADVANCED.get(), needAdvanced);
        if (needPremium > 0) removeItems(player, ModItems.SEALED_TUBE_PREMIUM.get(), needPremium);

        // 设置等级
        echo.setLevel(maxLevel);
        recalculateAttributes(player);

        player.sendSystemMessage(Component.literal(
                maxLevel == 5 ? "声骸已升至满级！" : "声骸已升至 " + maxLevel + " 级"));

        recalculateAttributes(player);
        syncToClient(player);  // 同步装备列表和声骸库
    }

    // 辅助方法：根据等级获取对应的密音筒物品
    private Item getTubeItemByTier(int tier)
    {
        switch (tier)
        {
            case 1: return ModItems.SEALED_TUBE_BASIC.get();
            case 2: return ModItems.SEALED_TUBE_MEDIUM.get();
            case 3: return ModItems.SEALED_TUBE_ADVANCED.get();
            case 4: return ModItems.SEALED_TUBE_PREMIUM.get();
            default: return null;
        }
    }

    // 辅助方法：从玩家背包移除指定数量的物品
    // 建议添加返回值，表示实际移除了多少
    private int removeItems(ServerPlayer player, Item item, int count)
    {
        if (item == null || count <= 0) return 0;
        Inventory inv = player.getInventory();
        int removed = 0;
        for (int i = 0; i < inv.getContainerSize() && removed < count; i++)
        {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == item)
            {
                int toRemove = Math.min(count - removed, stack.getCount());
                stack.shrink(toRemove);
                removed += toRemove;
            }
        }
        return removed; // 返回实际移除数量
    }

    // 辅助方法：计算从 start 到 end (不含 end) 所需的材料（用于确认对话框）
    private Map<String, Integer> getRequiredMaterialsForLevelRange(int start, int end)
    {
        Map<String, Integer> map = new HashMap<>();
        int tuner = 0;
        int basic = 0, medium = 0, advanced = 0, premium = 0;
        for (int lvl = start; lvl < end; lvl++)
        {
            tuner += 5;
            switch (lvl) {
                case 0: break;
                case 1: basic += 5; break;
                case 2: medium += 5; break;
                case 3: advanced += 5; break;
                case 4: premium += 5; break;
            }
        }
        map.put("tuner", tuner);
        map.put("basic", basic);
        map.put("medium", medium);
        map.put("advanced", advanced);
        map.put("premium", premium);
        return map;
    }

}