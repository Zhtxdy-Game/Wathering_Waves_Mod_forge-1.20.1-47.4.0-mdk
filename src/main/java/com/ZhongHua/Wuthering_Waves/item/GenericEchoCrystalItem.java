package com.ZhongHua.Wuthering_Waves.item;

import java.util.List;

public class GenericEchoCrystalItem extends AbstractEchoCrystalItem
{
    private final String displayName;
    private final int cost;
    private final List<String> possibleMainStats;

    public GenericEchoCrystalItem(Properties properties, String displayName, int cost, List<String> possibleMainStats)
    {
        super(properties);
        this.displayName = displayName;
        this.cost = cost;
        this.possibleMainStats = possibleMainStats;
    }

    @Override
    public String getEchoDisplayName()
    {
        return displayName;
    }

    @Override
    public int getCost()
    {
        return cost;
    }

    @Override
    public List<String> getPossibleMainStats()
    {
        return possibleMainStats;
    }
}