package com.ZhongHua.Wuthering_Waves.config;

import java.util.List;
import java.util.Map;

public class EchoSetConfig
{
    private String id;           // 唯一标识符
    private String name;         // 显示名称
    private List<String> members; // 成员声骸名称列表
    private Map<String, Map<String, Double>> bonuses; // "2"或"5" -> 属性映射

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getMembers() { return members; }
    public Map<String, Double> getBonus2Piece() { return bonuses.get("2"); }
    public Map<String, Double> getBonus5Piece() { return bonuses.get("5"); }

    // 检查声骸是否属于此套装
    public boolean contains(String echoName)
    {
        return members.contains(echoName);
    }
}