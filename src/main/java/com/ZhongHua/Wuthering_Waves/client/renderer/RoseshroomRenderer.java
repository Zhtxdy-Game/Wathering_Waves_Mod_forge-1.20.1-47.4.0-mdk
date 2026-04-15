package com.ZhongHua.Wuthering_Waves.client.renderer;

import com.ZhongHua.Wuthering_Waves.Wuthering_WavesMod;
import com.ZhongHua.Wuthering_Waves.client.model.BabyRoseshroomModel;
import com.ZhongHua.Wuthering_Waves.client.model.RoseshroomModel;
import com.ZhongHua.Wuthering_Waves.entity.RoseshroomEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RoseshroomRenderer extends MobRenderer<RoseshroomEntity, RoseshroomModel<RoseshroomEntity>>
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(Wuthering_WavesMod.MOD_ID, "roseshroom"), "main");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Wuthering_WavesMod.MOD_ID, "textures/entity/roseshroom.png");

    public RoseshroomRenderer(EntityRendererProvider.Context context)
    {
        super(context, new RoseshroomModel<>(context.bakeLayer(LAYER_LOCATION)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(RoseshroomEntity entity)
    {
        return TEXTURE;
    }
}