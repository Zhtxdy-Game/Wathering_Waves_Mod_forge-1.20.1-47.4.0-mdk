package com.ZhongHua.Wuthering_Waves.item;

import net.minecraft.world.item.Item;

/**
 * 所有声骸结晶物品的抽象基类
 * 用于在终端吸收时识别并获取该声骸的元数据
 */
public abstract class AbstractEchoCrystalItem extends Item
{

    public AbstractEchoCrystalItem(Properties properties)
    {
        super(properties);
    }

    /**
     * 声骸的显示名称（如 "无冠者"）
     */
    public abstract String getEchoDisplayName();

    /**
     * 声骸的 COST (1/3/4)
     */
    public abstract int getCost();

    /**
     * 可选：该声骸可能的主属性列表（根据 COST 和具体类型）
     * 例如：4C 声骸可能的主属性有 暴击率、暴击伤害、攻击百分比、生命百分比、治疗加成等
     * 如果返回 null 或空列表，则由通用规则随机决定
     */
    public abstract java.util.List<String> getPossibleMainStats();

    /**
     * 可选：该声骸的专属技能 ID（后续扩展）
     */

}