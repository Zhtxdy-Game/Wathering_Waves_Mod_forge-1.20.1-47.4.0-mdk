package com.ZhongHua.Wuthering_Waves.echocrystal;

import com.ZhongHua.Wuthering_Waves.item.AbstractEchoCrystalItem;

import java.util.List;

public class CrownlessEchoCrystal extends AbstractEchoCrystalItem
{

    public CrownlessEchoCrystal(Properties properties) {
        super(properties);
    }

    @Override
    public String getEchoDisplayName()
    {
        return "无冠者";
    }

    @Override
    public int getCost()
    {
        return 4; // 4 COST
    }

    @Override
    public List<String> getPossibleMainStats()
    {
        // 4C 声骸可能的主属性
        return List.of("暴击率", "暴击伤害", "攻击百分比", "生命百分比","防御百分比", "治疗加成");
    }
}