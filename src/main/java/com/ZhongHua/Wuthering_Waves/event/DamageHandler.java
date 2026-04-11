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
    }
}