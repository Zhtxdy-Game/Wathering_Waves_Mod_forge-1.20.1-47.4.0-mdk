package com.ZhongHua.Wuthering_Waves.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EchoAttributeCache
{
    // 原始累加值
    public double totalHealthFixed = 0;
    public double totalHealthPercent = 0;
    public double totalAttackFixed = 0;
    public double totalAttackPercent = 0;
    public double totalDefenseFixed = 0;
    public double totalDefensePercent = 0;
    public double totalCritRate = 0;
    public double totalCritDamage = 0;
    // 套装提供的元素伤害加成（键如 "aero", "spectro", "electro", "glacio", "fusion", "hydro", "physical"）
    public Map<String, Double> elementalBonus = new HashMap<>();
    // 套装效果描述（用于 GUI 显示）
    public List<String> activeSetBonuses = new ArrayList<>();
    // 最终应用到修饰器的差值（相对于基础值）
    public double finalHealthModifier = 0;
    public double finalArmorModifier = 0;

    // 重置所有累加器
    public void reset()
    {
        totalHealthFixed = 0;
        totalHealthPercent = 0;
        totalAttackFixed = 0;
        totalAttackPercent = 0;
        totalDefenseFixed = 0;
        totalDefensePercent = 0;
        totalCritRate = 0;
        totalCritDamage = 0;
        finalHealthModifier = 0;
        finalArmorModifier = 0;
        elementalBonus.clear();   // 清空元素加成
        activeSetBonuses.clear();   // 清空套装效果列表
    }

    // 序列化（用于网络同步）
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("HealthFixed", totalHealthFixed);
        tag.putDouble("HealthPercent", totalHealthPercent);
        tag.putDouble("AttackFixed", totalAttackFixed);
        tag.putDouble("AttackPercent", totalAttackPercent);
        tag.putDouble("DefenseFixed", totalDefenseFixed);
        tag.putDouble("DefensePercent", totalDefensePercent);
        tag.putDouble("CritRate", totalCritRate);
        tag.putDouble("CritDamage", totalCritDamage);
        tag.putDouble("HealthMod", finalHealthModifier);
        tag.putDouble("ArmorMod", finalArmorModifier);
        // 序列化 elementalBonus
        CompoundTag elementalTag = new CompoundTag();
        for (var entry : elementalBonus.entrySet())
        {
            elementalTag.putDouble(entry.getKey(), entry.getValue());
        }
        tag.put("ElementalBonus", elementalTag);
        // 序列化 activeSetBonuses（用于客户端显示）
        ListTag listTag = new ListTag();
        for (String s : activeSetBonuses)
        {
            listTag.add(StringTag.valueOf(s));
        }
        tag.put("ActiveSetBonuses", listTag);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag)
    {

        // 先清空所有集合
        elementalBonus.clear();
        activeSetBonuses.clear();

        totalHealthFixed = tag.getDouble("HealthFixed");
        totalHealthPercent = tag.getDouble("HealthPercent");
        totalAttackFixed = tag.getDouble("AttackFixed");
        totalAttackPercent = tag.getDouble("AttackPercent");
        totalDefenseFixed = tag.getDouble("DefenseFixed");
        totalDefensePercent = tag.getDouble("DefensePercent");
        totalCritRate = tag.getDouble("CritRate");
        totalCritDamage = tag.getDouble("CritDamage");
        finalHealthModifier = tag.getDouble("HealthMod");
        finalArmorModifier = tag.getDouble("ArmorMod");

        // 读取元素加成
        CompoundTag elementalTag = tag.getCompound("ElementalBonus");
        for (String key : elementalTag.getAllKeys())
        {
            elementalBonus.put(key, elementalTag.getDouble(key));
        }
        // 读取套装效果描述列表
        ListTag listTag = tag.getList("ActiveSetBonuses", Tag.TAG_STRING);
        for (int i = 0; i < listTag.size(); i++)
        {
            activeSetBonuses.add(listTag.getString(i));
        }

    }
}