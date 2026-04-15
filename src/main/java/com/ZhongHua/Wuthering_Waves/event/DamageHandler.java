package com.ZhongHua.Wuthering_Waves.event;

import com.ZhongHua.Wuthering_Waves.capability.ModCapabilities;
import com.ZhongHua.Wuthering_Waves.capability.EchoAttributeCache;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "wuthering_waves")
public class DamageHandler
{

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event)
    {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data ->
        {
            EchoAttributeCache cache = data.getAttributeCache();
            if (cache == null) return;

            float damage = event.getAmount();

            // 攻击加成：(原始 + 固定) * (1 + 百分比)
            damage = (damage + (float)cache.totalAttackFixed) * (1 + (float)cache.totalAttackPercent);

            // 暴击判定（与原版独立）
            if (player.getRandom().nextDouble() < cache.totalCritRate)
            {
                damage *= (1 + cache.totalCritDamage);
                // TODO: 发送暴击特效包给客户端
            }

            event.setAmount(damage);
        });

        double originalDamage = event.getAmount();
        System.out.println("[伤害计算] 原始伤害: " + originalDamage);

        player.getCapability(ModCapabilities.PLAYER_TERMINAL_DATA).ifPresent(data ->
        {
            EchoAttributeCache cache = data.getAttributeCache();
            double newDamage = (originalDamage + cache.totalAttackFixed) * (1 + cache.totalAttackPercent);
            System.out.println("[伤害计算] 攻击加成后: " + newDamage + " (固定+" + cache.totalAttackFixed + ", 百分比+" + (cache.totalAttackPercent * 100) + "%)");

            // 暴击判定
            if (player.getRandom().nextDouble() < cache.totalCritRate)
            {
                newDamage *= (1 + cache.totalCritDamage);
                System.out.println("[伤害计算] 暴击！暴击率: " + (cache.totalCritRate * 100) + "%, 爆伤: " + (cache.totalCritDamage * 100) + "%");
                System.out.println("[伤害计算] 暴击后伤害: " + newDamage);
            } else
            {
                System.out.println("[伤害计算] 未暴击");
            }
            event.setAmount((float) newDamage);
        });
    }


}