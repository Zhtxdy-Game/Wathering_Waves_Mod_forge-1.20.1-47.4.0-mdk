package com.ZhongHua.Wuthering_Waves.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CrownlessEntity extends Monster
{
    public CrownlessEntity(EntityType<CrownlessEntity> entityType, Level level)
    {
        super(entityType, level);
        // 设置经验值（可选）
        this.xpReward = 100;
    }

    @Override
    protected void registerGoals()
    {
        // 添加 AI 目标（按优先级排序）
        this.goalSelector.addGoal(1, new FloatGoal(this));                           // 游泳
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));        // 近战攻击，移动速度 1.0，连续攻击
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.8D));             // 随机移动，速度 0.8
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F)); // 看向玩家
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));                // 随机环顾

        // 目标选择器：攻击最近的玩家
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // 自定义属性：血量、攻击力、移动速度、护甲
    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)      // 生命值 20(临时数据,方便测试)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)   // 攻击力 12
                .add(Attributes.MOVEMENT_SPEED, 0.3D)   // 移动速度 0.3
                .add(Attributes.ARMOR, 6.0D);           // 护甲值 6
    }
}