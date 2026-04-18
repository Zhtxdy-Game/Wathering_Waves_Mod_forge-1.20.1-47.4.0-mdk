package com.ZhongHua.Wuthering_Waves.client.model;

import com.ZhongHua.Wuthering_Waves.Wuthering_WavesMod;
import com.ZhongHua.Wuthering_Waves.entity.CrownlessEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CrownlessModel extends GeoModel<CrownlessEntity>
{
    @Override
    public ResourceLocation getModelResource(CrownlessEntity object)
    {
        return new ResourceLocation(Wuthering_WavesMod.MOD_ID, "geo/crownless.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CrownlessEntity object)
    {
        // 使用你的纹理文件
        return new ResourceLocation(Wuthering_WavesMod.MOD_ID, "textures/entity/crownless.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CrownlessEntity animatable)
    {
        // 暂时没有动画文件，返回空占位
        return new ResourceLocation(Wuthering_WavesMod.MOD_ID, "animations/crownless.animation.json");
    }
}