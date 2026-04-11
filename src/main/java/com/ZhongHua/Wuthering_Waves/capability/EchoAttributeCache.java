package com.ZhongHua.Wuthering_Waves.capability;

import net.minecraft.nbt.CompoundTag;

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
        return tag;
    }

    public void deserializeNBT(CompoundTag tag)
    {
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
    }
}