package com.ZhongHua.Wuthering_Waves.client.renderer;

import com.ZhongHua.Wuthering_Waves.Wuthering_WavesMod;
import com.ZhongHua.Wuthering_Waves.client.model.CrownlessModel;
import com.ZhongHua.Wuthering_Waves.entity.CrownlessEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CrownlessRenderer extends GeoEntityRenderer<CrownlessEntity>
{
    public CrownlessRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new CrownlessModel());
        this.shadowRadius = 0.5f; // 调整阴影大小
    }
}