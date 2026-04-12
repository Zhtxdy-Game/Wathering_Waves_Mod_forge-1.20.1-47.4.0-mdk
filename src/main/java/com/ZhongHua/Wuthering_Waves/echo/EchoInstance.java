package com.ZhongHua.Wuthering_Waves.echo;

import net.minecraft.nbt.*;
import java.util.*;
import java.util.UUID;

public class EchoInstance
{
    private final String name;
    private int level;          // 0~5，非 final 因为可以升级
    private final int cost;
    private final String mainStat;
    private final double maxMainStatValue;
    private double mainStatValue;  // 非 final 因为随等级变化
    private final List<EchoSubStat> subStats;
    private final UUID id;  // final，必须在构造时确定
    private Map<String, Double> stats;  // 属性映射

    // ====================== 唯一的私有主构造器 ======================
    private EchoInstance(String name, int level, int cost, String mainStat,
                         double maxMainStatValue, List<EchoSubStat> subStats, UUID id)
    {
        this.id = Objects.requireNonNull(id, "UUID cannot be null");
        this.name = name;
        this.level = level;
        this.cost = cost;
        this.mainStat = mainStat;
        this.maxMainStatValue = maxMainStatValue;
        this.subStats = subStats != null ? subStats : new ArrayList<>();
        this.stats = new HashMap<>(); // 确保初始化
        recalcMainStat(); // 计算当前等级的主属性值
        buildStats(); // 初始构建属性映射
    }



    // ====================== 公开静态工厂方法 ======================

    // 根据当前的主属性和副属性生成 stats 映射
    public void buildStats()
    {
        stats.clear();
        // 添加主属性
        addStatFromAttribute(mainStat, mainStatValue);
        // 添加当前等级已解锁的副属性
        for (EchoSubStat sub : getActiveSubStats())
        {
            addStatFromAttribute(sub.getName(), sub.getValue());
        }
    }

    // 将游戏内属性名转换为 stats 键
    private void addStatFromAttribute(String attrName, double value)
    {
        switch (attrName)
        {
            case "暴击率":
                stats.put("crit_rate", value);
                break;
            case "暴击伤害":
                stats.put("crit_damage", value);
                break;
            case "攻击百分比":
                stats.put("attack_percent", value);
                break;
            case "生命百分比":
                stats.put("health_percent", value);
                break;
            case "防御百分比":
                stats.put("defense_percent", value);
                break;
            case "共鸣效率":
                stats.put("energy_recharge", value);
                break;
            case "治疗加成":
                stats.put("healing_bonus", value);
                break;
            case "固定攻击力":
                stats.put("attack_fixed", value);
                break;
            case "固定生命值":
                stats.put("health_fixed", value);
                break;
            case "固定防御力":
                stats.put("defense_fixed", value);
                break;
            // 元素伤害加成需要单独处理，可以放在 elementalBonus map 中，暂略
            default:
                // 其他伤害加成如 "冷凝伤害加成" 等，可以映射到 "elemental_glacio" 等
                if (attrName.contains("伤害加成"))
                {
                    String element = mapElementalDamage(attrName);
                    stats.put("elemental_" + element, value);
                }
                break;
        }
    }

    // 辅助方法：将中文元素名映射为英文 key
    private String mapElementalDamage(String name)
    {
        if (name.contains("冷凝")) return "glacio";
        if (name.contains("热熔")) return "fusion";
        if (name.contains("导电")) return "electro";
        if (name.contains("气动")) return "aero";
        if (name.contains("衍射")) return "spectro";
        if (name.contains("湮灭")) return "havoc";
        return "physical";
    }

    /**
     * 用于从 NBT 反序列化（保留原有 UUID）
     */
    public static EchoInstance fromNBT(CompoundTag tag)
    {
        UUID id = tag.hasUUID("Id") ? tag.getUUID("Id") : UUID.randomUUID();
        String name = tag.getString("Name");
        int level = tag.getInt("Level");
        int cost = tag.getInt("Cost");
        String mainStat = tag.getString("MainStat");
        double maxMainStatValue = tag.getDouble("MaxMainStatValue");

        // 读取副属性
        List<EchoSubStat> subStats = new ArrayList<>();
        ListTag subList = tag.getList("SubStats", Tag.TAG_COMPOUND);
        for (int i = 0; i < subList.size(); i++)
        {
            subStats.add(EchoSubStat.fromNBT(subList.getCompound(i)));
        }

        // 数据完整性检查
        while (subStats.size() < 5)
        {
            subStats.add(new EchoSubStat("攻击百分比", 0.06));
        }

        return new EchoInstance(name, level, cost, mainStat, maxMainStatValue, subStats, id);
    }

    public Map<String, Double> getStats()
    {
        return Collections.unmodifiableMap(stats); // 返回不可变视图，防止外部修改
    }

    /**
     * 创建新的随机声骸（生成新 UUID）
     */
    public static EchoInstance createRandom(String name, int cost, List<String> possibleMainStats)
    {
        List<String> pool = (possibleMainStats != null && !possibleMainStats.isEmpty())
                ? possibleMainStats : getMainStatPoolForCost(cost);

        if (pool.isEmpty())
        {
            pool = List.of("攻击百分比");
        }

        String mainStat = pool.get(RANDOM.nextInt(pool.size()));
        double maxValue = getMaxMainStatValue(mainStat, cost);
        List<EchoSubStat> subStats = generateRandomSubStats();

        // 统一使用主构造器，生成新 UUID
        return new EchoInstance(name, 0, cost, mainStat, maxValue, subStats, UUID.randomUUID());
    }

    // 删除之前的 public EchoInstance(String name, int level) 构造器
    // 如果外部需要创建空壳，使用 createRandom 或 fromNBT

    // ====================== Getter 方法 ======================

    public UUID getId() { return id; }
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getCost() { return cost; }
    public String getMainStat() { return mainStat; }
    public double getMainStatValue() { return mainStatValue; }
    public double getMaxMainStatValue() { return maxMainStatValue; }
    public List<EchoSubStat> getAllSubStats() { return subStats; }

    // 获取已解锁的副属性（前 level 条）
    public List<EchoSubStat> getActiveSubStats()
    {
        if (level <= 0) return List.of();
        int endIndex = Math.min(level, subStats.size());
        return List.copyOf(subStats.subList(0, endIndex));
    }

    // ====================== 等级系统 ======================

    public void setLevel(int newLevel)
    {
        if (newLevel < 0) newLevel = 0;
        if (newLevel > 5) newLevel = 5;
        this.level = newLevel;
        // 主属性值 = 最大值 * (0.5 + level * 0.1)
        this.mainStatValue = maxMainStatValue * (0.5 + level * 0.1);
        // 重新构建 stats（主属性 + 已解锁副属性）
        buildStats();
    }

    private void recalcMainStat()
    {
        this.mainStatValue = maxMainStatValue * (0.5 + level * 0.1);
    }


    // ====================== 属性计算 ======================

    /**
     * 计算当前声骸提供的所有属性加成（用于应用到玩家）
     */
    public Map<String, Double> recalcStats()
    {
        Map<String, Double> stats = new HashMap<>();

        // 主属性始终生效
        stats.put(mainStat, mainStatValue);

        // 副属性根据等级解锁
        int unlocked = Math.min(level, subStats.size());
        for (int i = 0; i < unlocked; i++)
        {
            EchoSubStat sub = subStats.get(i);
            stats.merge(sub.getName(), sub.getValue(), Double::sum);
        }

        return stats;
    }

    // ====================== NBT 序列化 ======================

    public CompoundTag toNBT()
    {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Id", this.id);
        tag.putString("Name", name);
        tag.putInt("Level", level);
        tag.putInt("Cost", cost);
        tag.putString("MainStat", mainStat);
        tag.putDouble("MaxMainStatValue", maxMainStatValue);
        tag.putDouble("MainStatValue", mainStatValue);

        ListTag subList = new ListTag();
        for (EchoSubStat sub : subStats)
        {
            subList.add(sub.toNBT());
        }
        tag.put("SubStats", subList);
        return tag;
    }

    // ====================== 副属性生成（保持不变）======================

    private static final Random RANDOM = new Random();

    private static class SubStatTemplate
    {
        String name; double min; double max; boolean isPercentage;
        SubStatTemplate(String name, double min, double max, boolean isPercentage)
        {
            this.name = name; this.min = min; this.max = max; this.isPercentage = isPercentage;
        }
        EchoSubStat generateRandom()
        {
            double value = min + (max - min) * RANDOM.nextDouble();
            if (!isPercentage) value = Math.round(value);
            return new EchoSubStat(name, value);
        }
    }

    private static final List<SubStatTemplate> SUB_STAT_TEMPLATES = List.of(
            new SubStatTemplate("暴击率", 0.063, 0.105, true),
            new SubStatTemplate("暴击伤害", 0.126, 0.21, true),
            new SubStatTemplate("攻击百分比", 0.06, 0.116, true),
            new SubStatTemplate("生命百分比", 0.06, 0.116, true),
            new SubStatTemplate("防御百分比", 0.081, 0.147, true),
            new SubStatTemplate("共鸣效率", 0.068, 0.124, true),
            new SubStatTemplate("普攻伤害加成", 0.06, 0.116, true),
            new SubStatTemplate("重击伤害加成", 0.06, 0.116, true),
            new SubStatTemplate("共鸣技能伤害加成", 0.06, 0.116, true),
            new SubStatTemplate("共鸣解放伤害加成", 0.06, 0.116, true),
            new SubStatTemplate("固定生命值", 2, 5, false),
            new SubStatTemplate("固定攻击力", 5, 10, false),
            new SubStatTemplate("固定防御力", 5, 10, false)
    );

    private static List<EchoSubStat> generateRandomSubStats()
    {
        List<EchoSubStat> result = new ArrayList<>();
        Set<String> usedNames = new HashSet<>();
        List<SubStatTemplate> shuffled = new ArrayList<>(SUB_STAT_TEMPLATES);
        Collections.shuffle(shuffled, RANDOM);

        for (SubStatTemplate template : shuffled)
        {
            if (result.size() >= 5) break;
            if (!usedNames.contains(template.name))
            {
                result.add(template.generateRandom());
                usedNames.add(template.name);
            }
        }

        while (result.size() < 5)
        {
            result.add(new EchoSubStat("攻击百分比", 0.06));
        }
        return result;
    }

    private static List<String> getMainStatPoolForCost(int cost)
    {
        return switch (cost)
        {
            case 4 -> List.of("暴击率", "暴击伤害", "攻击百分比", "防御百分比", "生命百分比", "治疗加成");
            case 3 -> List.of("冷凝伤害加成", "热熔伤害加成", "导电伤害加成", "气动伤害加成",
                    "衍射伤害加成", "湮灭伤害加成", "攻击百分比", "生命百分比",
                    "防御百分比", "共鸣效率");
            case 1 -> List.of("攻击百分比", "防御百分比", "生命百分比");
            default -> List.of("攻击百分比");
        };
    }

    private static double getMaxMainStatValue(String mainStat, int cost)
    {
        return switch (cost)
        {
            case 4 -> switch (mainStat)
            {
                case "暴击率" -> 0.22;
                case "暴击伤害" -> 0.44;
                case "攻击百分比" -> 0.33;
                case "防御百分比" -> 0.418;
                case "生命百分比" -> 0.33;
                case "治疗加成" -> 0.264;
                default -> 0.33;
            };
            case 3 -> switch (mainStat)
            {
                case "冷凝伤害加成", "热熔伤害加成", "导电伤害加成",
                     "气动伤害加成", "衍射伤害加成", "湮灭伤害加成" -> 0.30;
                case "攻击百分比", "生命百分比" -> 0.30;
                case "防御百分比" -> 0.38;
                case "共鸣效率" -> 0.32;
                default -> 0.30;
            };
            case 1 -> switch (mainStat)
            {
                case "攻击百分比", "防御百分比" -> 0.18;
                case "生命百分比" -> 0.228;
                default -> 0.18;
            };
            default -> 0.30;
        };
    }
}