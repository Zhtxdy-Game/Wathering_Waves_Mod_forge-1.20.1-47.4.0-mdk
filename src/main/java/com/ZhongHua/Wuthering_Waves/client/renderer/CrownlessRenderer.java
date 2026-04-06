package com.ZhongHua.Wuthering_Waves.client.renderer;

import com.ZhongHua.Wuthering_Waves.Wuthering_WavesMod;
import com.ZhongHua.Wuthering_Waves.client.model.CrownlessModel;
import com.ZhongHua.Wuthering_Waves.entity.CrownlessEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CrownlessRenderer extends MobRenderer<CrownlessEntity, CrownlessModel<CrownlessEntity>>
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(Wuthering_WavesMod.MOD_ID, "crownless"), "main");

    private static final ResourceLocation TEXTURE = new ResourceLocation(Wuthering_WavesMod.MOD_ID, "textures/entity/crownless.png");

    public CrownlessRenderer(EntityRendererProvider.Context context) {
        super(context, new CrownlessModel<>(context.bakeLayer(LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(CrownlessEntity entity)
    {
        return TEXTURE;
    }
}