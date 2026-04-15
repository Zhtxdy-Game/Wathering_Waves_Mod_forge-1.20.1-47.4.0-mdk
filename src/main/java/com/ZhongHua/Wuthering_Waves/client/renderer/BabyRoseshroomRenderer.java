package com.ZhongHua.Wuthering_Waves.client.renderer;

import com.ZhongHua.Wuthering_Waves.Wuthering_WavesMod;
import com.ZhongHua.Wuthering_Waves.client.model.BabyRoseshroomModel;
import com.ZhongHua.Wuthering_Waves.entity.BabyRoseshroomEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BabyRoseshroomRenderer extends MobRenderer<BabyRoseshroomEntity, BabyRoseshroomModel<BabyRoseshroomEntity>>
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(Wuthering_WavesMod.MOD_ID, "baby_roseshroom"), "main");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Wuthering_WavesMod.MOD_ID, "textures/entity/baby_roseshroom.png");

    public BabyRoseshroomRenderer(EntityRendererProvider.Context context)
    {
        super(context, new BabyRoseshroomModel<>(context.bakeLayer(LAYER_LOCATION)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(BabyRoseshroomEntity entity)
    {
        return TEXTURE;
    }
}