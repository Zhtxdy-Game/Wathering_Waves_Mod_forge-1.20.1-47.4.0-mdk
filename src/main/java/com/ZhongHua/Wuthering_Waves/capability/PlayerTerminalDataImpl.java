package com.ZhongHua.Wuthering_Waves.capability;

import com.ZhongHua.Wuthering_Waves.Config;
import com.ZhongHua.Wuthering_Waves.echo.EchoInstance;
import com.ZhongHua.Wuthering_Waves.network.ModNetwork;
import com.ZhongHua.Wuthering_Waves.network.SyncAttributeCachePacket;
import com.ZhongHua.Wuthering_Waves.network.SyncTerminalDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

}