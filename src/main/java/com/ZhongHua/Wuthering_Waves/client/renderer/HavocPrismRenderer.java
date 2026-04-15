package com.ZhongHua.Wuthering_Waves.client.renderer;

import com.ZhongHua.Wuthering_Waves.Wuthering_WavesMod;
import com.ZhongHua.Wuthering_Waves.client.model.HavocPrismModel;
import com.ZhongHua.Wuthering_Waves.entity.HavocPrismEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HavocPrismRenderer extends MobRenderer<HavocPrismEntity, HavocPrismModel<HavocPrismEntity>>
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(Wuthering_WavesMod.MOD_ID, "havoc_prism"), "main");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Wuthering_WavesMod.MOD_ID, "textures/entity/havoc_prism.png");

    public HavocPrismRenderer(EntityRendererProvider.Context context)
    {
        super(context, new HavocPrismModel<>(context.bakeLayer(LAYER_LOCATION)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(HavocPrismEntity entity)
    {
        return TEXTURE;
    }
}
