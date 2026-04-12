package com.ZhongHua.Wuthering_Waves.echo;

import net.minecraft.nbt.CompoundTag;

public class EchoSubStat
{
    private final String name;
    private final double value;      // 如果是百分比，存储小数（如0.1表示10%）；固定值直接存数值

    public EchoSubStat(String name, double value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public double getValue() { return value; }

    public CompoundTag toNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putString("Name", name);
        tag.putDouble("Value", value);
        return tag;
    }

    public static EchoSubStat fromNBT(CompoundTag tag)
    {
        return new EchoSubStat(tag.getString("Name"), tag.getDouble("Value"));
    }
}