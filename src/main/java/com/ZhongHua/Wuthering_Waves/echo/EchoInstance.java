package com.ZhongHua.Wuthering_Waves.echo;

import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 声骸实例 - 极简骨架
 * 后续会扩展：套装、稀有度、主属性、辅音属性、技能等
 */
public class EchoInstance
{
    private String name;
    private int level;         // 等级 0~5
    private int cost;          // COST 1/3/4
    private String mainStat;   // 主属性名称
    private double mainStatValue;       //主属性数值
    private List<String> subStats;      // 副属性名称列表
    private List<Double> subStatValues;     //副属性数值
    // 技能描述等暂略


    // 构造器
    public EchoInstance(String name, int level)
    {
        this.name = name;
        this.level = level;
        // 临时默认值
        this.cost = 4;
        this.mainStat = "暴击";
        this.mainStatValue = 0.22;
        this.subStats = Arrays.asList("攻击", "生命", "防御");
        this.subStatValues = Arrays.asList(0.09, 0.05, 0.05);
    }

    // ----- getter & setter -----
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getCost()
    {
        return cost;
    }

    public void setCost(int cost)
    {
        this.cost = cost;
    }

    public String getMainStat()
    {
        return mainStat;
    }

    public void setMainStat(String mainStat)
    {
        this.mainStat = mainStat;
    }

    public double getMainStatValue()
    {
        return mainStatValue;
    }

    public void setMainStatValue(double mainStatValue)
    {
        this.mainStatValue = mainStatValue;
    }

    public List<String> getSubStats()
    {
        return subStats;
    }

    public void setSubStats(List<String> subStats)
    {
        this.subStats = subStats;
    }

    public List<Double> getSubStatValues()
    {
        return subStatValues;
    }

    public void setSubStatValues(List<Double> subStatValues)
    {
        this.subStatValues = subStatValues;
    }

    // ----- NBT 转换 -----
    /**
     * 将此声骸实例保存到 CompoundTag 中
     * @return 包含数据的 CompoundTag
     */
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Name", this.name);
        tag.putInt("Level", this.level);
        tag.putInt("Cost", this.cost);
        tag.putString("MainStat", this.mainStat);
        tag.putDouble("MainStatValue", this.mainStatValue);

        // 存储副属性列表
        ListTag subStatsTag = new ListTag();
        for (String stat : subStats) {
            subStatsTag.add(StringTag.valueOf(stat));
        }
        tag.put("SubStats", subStatsTag);

        ListTag subValuesTag = new ListTag();
        for (Double value : subStatValues) {
            subValuesTag.add(DoubleTag.valueOf(value));
        }
        tag.put("SubStatValues", subValuesTag);

        return tag;
    }

    public static EchoInstance fromNBT(CompoundTag tag)
    {
        if (tag == null || !tag.contains("Name", Tag.TAG_STRING))
        {
            return new EchoInstance("未知声骸", 0);
        }
        String name = tag.getString("Name");
        int level = tag.getInt("Level");
        EchoInstance instance = new EchoInstance(name, level);

        if (tag.contains("Cost", Tag.TAG_INT))
        {
            instance.setCost(tag.getInt("Cost"));
        }
        if (tag.contains("MainStat", Tag.TAG_STRING))
        {
            instance.setMainStat(tag.getString("MainStat"));
        }
        if (tag.contains("MainStatValue", Tag.TAG_DOUBLE))
        {
            instance.setMainStatValue(tag.getDouble("MainStatValue"));
        }

        // 读取副属性
        if (tag.contains("SubStats", Tag.TAG_LIST))
        {
            ListTag subStatsTag = tag.getList("SubStats", Tag.TAG_STRING);
            List<String> subStatsList = new ArrayList<>();
            for (int i = 0; i < subStatsTag.size(); i++) {
                subStatsList.add(subStatsTag.getString(i));
            }
            instance.setSubStats(subStatsList);
        }
        if (tag.contains("SubStatValues", Tag.TAG_LIST))
        {
            ListTag subValuesTag = tag.getList("SubStatValues", Tag.TAG_DOUBLE);
            List<Double> subValuesList = new ArrayList<>();
            for (int i = 0; i < subValuesTag.size(); i++)
            {
                subValuesList.add(subValuesTag.getDouble(i));
            }
            instance.setSubStatValues(subValuesList);
        }

        return instance;
    }

    // 可选：用于调试或显示
    @Override
    public String toString()
    {
        return String.format("EchoInstance{name='%s', level=%d}", name, level);
    }

    // 测试数据生成方法（可放在 EchoInstance 类中或单独工具类）
    public static List<EchoInstance> createTestData()
    {
        List<EchoInstance> list = new ArrayList<>();

        // 1. 无冠者 (4C)
        EchoInstance s1 = new EchoInstance("无冠者", 5);
        s1.setCost(4);
        s1.setMainStat("暴击伤害");
        s1.setMainStatValue(0.44); // 44%
        s1.setSubStats(List.of("攻击百分比", "暴击率", "生命百分比", "共鸣效率", "防御百分比"));
        s1.setSubStatValues(List.of(0.09, 0.06, 0.05, 0.08, 0.05));
        list.add(s1);

        // 2. 鸣钟之龟 (4C)
        EchoInstance s2 = new EchoInstance("鸣钟之龟", 3);
        s2.setCost(4);
        s2.setMainStat("暴击率");
        s2.setMainStatValue(0.22);
        s2.setSubStats(List.of("暴击伤害", "攻击百分比", "防御百分比", "生命百分比", "元素精通"));
        s2.setSubStatValues(List.of(0.15, 0.07, 0.06, 0.05, 0.08));
        list.add(s2);

        // 3. 绿熔蜥蜴 (3C)
        EchoInstance s3 = new EchoInstance("绿熔蜥蜴", 2);
        s3.setCost(3);
        s3.setMainStat("气动伤害加成");
        s3.setMainStatValue(0.30);
        s3.setSubStats(List.of("暴击率", "攻击百分比", "生命百分比", "防御百分比"));
        s3.setSubStatValues(List.of(0.05, 0.06, 0.04, 0.04));
        list.add(s3);

        // 4. 振铎乐师 (3C)
        EchoInstance s4 = new EchoInstance("振铎乐师", 1);
        s4.setCost(3);
        s4.setMainStat("共鸣效率");
        s4.setMainStatValue(0.32);
        s4.setSubStats(List.of("攻击百分比", "暴击伤害", "生命百分比"));
        s4.setSubStatValues(List.of(0.06, 0.08, 0.05));
        list.add(s4);

        // 5. 残星门徒 (1C)
        EchoInstance s5 = new EchoInstance("残星门徒", 0);
        s5.setCost(1);
        s5.setMainStat("生命百分比");
        s5.setMainStatValue(0.10);
        s5.setSubStats(List.of("防御百分比", "攻击百分比"));
        s5.setSubStatValues(List.of(0.05, 0.04));
        list.add(s5);

        // 6. 巡徊猎手 (1C)
        EchoInstance s6 = new EchoInstance("巡徊猎手", 4);
        s6.setCost(1);
        s6.setMainStat("攻击百分比");
        s6.setMainStatValue(0.12);
        s6.setSubStats(List.of("暴击率", "生命百分比", "防御百分比"));
        s6.setSubStatValues(List.of(0.04, 0.05, 0.05));
        list.add(s6);

        // 7. 寒霜陆龟 (1C)
        EchoInstance s7 = new EchoInstance("寒霜陆龟", 2);
        s7.setCost(1);
        s7.setMainStat("防御百分比");
        s7.setMainStatValue(0.15);
        s7.setSubStats(List.of("生命百分比", "攻击百分比"));
        s7.setSubStatValues(List.of(0.06, 0.05));
        list.add(s7);

        // 8. 雷翼飞鸟 (3C)
        EchoInstance s8 = new EchoInstance("雷翼飞鸟", 3);
        s8.setCost(3);
        s8.setMainStat("导电伤害加成");
        s8.setMainStatValue(0.30);
        s8.setSubStats(List.of("暴击伤害", "攻击百分比", "共鸣效率"));
        s8.setSubStatValues(List.of(0.12, 0.06, 0.07));
        list.add(s8);

        // 9. 熔岩巨人 (4C)
        EchoInstance s9 = new EchoInstance("熔岩巨人", 4);
        s9.setCost(4);
        s9.setMainStat("攻击百分比");
        s9.setMainStatValue(0.18);
        s9.setSubStats(List.of("暴击率", "暴击伤害", "生命百分比", "防御百分比", "元素精通"));
        s9.setSubStatValues(List.of(0.05, 0.10, 0.05, 0.05, 0.06));
        list.add(s9);

        // 10. 风蚀舞者 (1C)
        EchoInstance s10 = new EchoInstance("风蚀舞者", 1);
        s10.setCost(1);
        s10.setMainStat("元素精通");
        s10.setMainStatValue(0.08);
        s10.setSubStats(List.of("攻击百分比", "生命百分比"));
        s10.setSubStatValues(List.of(0.05, 0.05));
        list.add(s10);

        EchoInstance s11 = new EchoInstance("风蚀舞者", 1);
        s11.setCost(1);
        s11.setMainStat("元素精通");
        s11.setMainStatValue(0.08);
        s11.setSubStats(List.of("攻击百分比", "生命百分比"));
        s11.setSubStatValues(List.of(0.05, 0.05));
        list.add(s11);

        EchoInstance s12 = new EchoInstance("风蚀舞者", 1);
        s12.setCost(1);
        s12.setMainStat("元素精通");
        s12.setMainStatValue(0.08);
        s12.setSubStats(List.of("攻击百分比", "生命百分比"));
        s12.setSubStatValues(List.of(0.05, 0.05));
        list.add(s12);

        EchoInstance s13 = new EchoInstance("风蚀舞者", 1);
        s13.setCost(1);
        s13.setMainStat("元素精通");
        s13.setMainStatValue(0.08);
        s13.setSubStats(List.of("攻击百分比", "生命百分比"));
        s13.setSubStatValues(List.of(0.05, 0.05));
        list.add(s13);

        EchoInstance s14 = new EchoInstance("风蚀舞者", 1);
        s14.setCost(1);
        s14.setMainStat("元素精通");
        s14.setMainStatValue(0.08);
        s14.setSubStats(List.of("攻击百分比", "生命百分比"));
        s14.setSubStatValues(List.of(0.05, 0.05));
        list.add(s14);

        EchoInstance s15 = new EchoInstance("风蚀舞者", 1);
        s15.setCost(1);
        s15.setMainStat("元素精通");
        s15.setMainStatValue(0.08);
        s15.setSubStats(List.of("攻击百分比", "生命百分比"));
        s15.setSubStatValues(List.of(0.05, 0.05));
        list.add(s15);

        EchoInstance s16 = new EchoInstance("风蚀舞者", 1);
        s16.setCost(1);
        s16.setMainStat("元素精通");
        s16.setMainStatValue(0.08);
        s16.setSubStats(List.of("攻击百分比", "生命百分比"));
        s16.setSubStatValues(List.of(0.05, 0.05));
        list.add(s16);

        EchoInstance s17 = new EchoInstance("风蚀舞者", 1);
        s17.setCost(1);
        s17.setMainStat("元素精通");
        s17.setMainStatValue(0.08);
        s17.setSubStats(List.of("攻击百分比", "生命百分比"));
        s17.setSubStatValues(List.of(0.05, 0.05));
        list.add(s17);

        EchoInstance s18 = new EchoInstance("风蚀舞者", 1);
        s18.setCost(1);
        s18.setMainStat("元素精通");
        s18.setMainStatValue(0.08);
        s18.setSubStats(List.of("攻击百分比", "生命百分比"));
        s18.setSubStatValues(List.of(0.05, 0.05));
        list.add(s18);

        return list;
    }

}