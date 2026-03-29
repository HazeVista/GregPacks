package com.astrogreg.gregpacks.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

@SuppressWarnings("all")
public class OmniPackModel<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("gregpacks", "item/advanced_omnipack"), "main");
	private final ModelPart bb_main;

	public OmniPackModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(22, 26).addBox(-4.5F, -14.75F, -1.7F, 9.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(21, 31).addBox(-4.0F, -6.0F, -4.9F, 8.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(34, 46).addBox(3.0F, -13.0F, 3.8F, 2.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(22, 40).addBox(-4.0F, -12.0F, -4.3F, 8.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(38, 0).addBox(-7.5F, -7.0F, -1.7F, 2.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(38, 11).addBox(5.5F, -7.0F, -1.7F, 2.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(40, 40).addBox(6.0F, -13.0F, -1.7F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(22, 46).addBox(-7.0F, -13.0F, -1.7F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(38, 22).addBox(-3.0F, -16.3F, 0.3F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.001F))
		.texOffs(38, 24).addBox(-3.0F, -15.75F, 0.3F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(42, 24).addBox(2.0F, -15.75F, 0.3F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(42, 32).addBox(-7.75F, -6.0F, 0.3F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(42, 35).addBox(-7.25F, -12.0F, 0.3F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(46, 32).addBox(6.75F, -6.0F, 0.3F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(46, 35).addBox(6.25F, -12.0F, 0.3F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 49).addBox(-5.0F, -13.0F, 3.8F, 2.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(18, 49).addBox(0.0F, -6.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 1.35F, -5.15F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r2 = bb_main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(14, 49).addBox(0.0F, -6.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 1.35F, -5.15F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r3 = bb_main.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(10, 49).addBox(0.0F, -6.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -5.0F, -4.55F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r4 = bb_main.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(6, 49).addBox(0.0F, -6.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -5.0F, -4.55F, 0.0F, -1.5708F, 0.0F));

		PartDefinition wide_front_pouch_r1 = bb_main.addOrReplaceChild("wide_front_pouch_r1", CubeListBuilder.create().texOffs(0, 26).addBox(-5.0F, -13.0F, -5.0F, 1.0F, 13.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.7F, 0.0F, -1.5708F, 0.0F));

		PartDefinition main_body_r1 = bb_main.addOrReplaceChild("main_body_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -14.0F, -6.0F, 7.0F, 14.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.3F, 0.0F, -1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}