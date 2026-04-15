package com.ZhongHua.Wuthering_Waves.client.renderer;

import com.ZhongHua.Wuthering_Waves.Wuthering_WavesMod;
import com.ZhongHua.Wuthering_Waves.client.model.HavocDreadmaneModel;
import com.ZhongHua.Wuthering_Waves.entity.HavocDreadmaneEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HavocDreadmaneRenderer extends MobRenderer<HavocDreadmaneEntity, HavocDreadmaneModel<HavocDreadmaneEntity>>
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(Wuthering_WavesMod.MOD_ID, "havoc_dreadmane"), "main");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Wuthering_WavesMod.MOD_ID, "textures/entity/havoc_dreadmane.png");

    public HavocDreadmaneRenderer(EntityRendererProvider.Context context)
    {
        super(context, new HavocDreadmaneModel<>(context.bakeLayer(LAYER_LOCATION)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(HavocDreadmaneEntity entity)
    {
        return TEXTURE;
    }
}