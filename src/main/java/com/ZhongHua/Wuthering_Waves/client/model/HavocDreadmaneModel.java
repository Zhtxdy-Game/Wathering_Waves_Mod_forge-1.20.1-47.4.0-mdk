package com.ZhongHua.Wuthering_Waves.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class HavocDreadmaneModel<T extends Entity> extends EntityModel<T>
{
    private final ModelPart body;

    public HavocDreadmaneModel(ModelPart root)
    {
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition body = partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-0.6F, -1.5F, -0.5F, 4.0F, 4.0F, 4.0F), // 稍小一点
                PartPose.offset(0.0F, 22.5F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        body.yRot = netHeadYaw * 0.017453292F;
        body.xRot = headPitch * 0.017453292F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}