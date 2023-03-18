package ru.imine.cases.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelItemCase extends ModelBase {
	ModelRenderer _asing_middle_01;
	ModelRenderer _asing_middle_left;
	ModelRenderer _asing_middle_right;
	ModelRenderer Casing_vert_01;
	ModelRenderer Casing_vert_02;
	ModelRenderer Casing_vert_03;
	ModelRenderer Casing_vert_04;
	ModelRenderer Case;
	ModelRenderer _asing_lock_01;
	ModelRenderer _asing_lock_02;
	ModelRenderer Handles;

	public ModelItemCase() {
		this(0.0F);
	}

	public ModelItemCase(float par1) {
		this._asing_middle_01 = new ModelRenderer(this, 72, 110);
		this._asing_middle_01.setTextureSize(128, 128);
		this._asing_middle_01.addBox(-5.5F, -0.5F, -8.5F, 11, 1, 17);
		this._asing_middle_01.setRotationPoint(0.0F, 13.0F, 0.0F);
		this._asing_middle_left = new ModelRenderer(this, 1, 72);
		this._asing_middle_left.setTextureSize(128, 128);
		this._asing_middle_left.addBox(-2.5F, -0.5F, -8.5F, 5, 1, 17);
		this._asing_middle_left.setRotationPoint(13.0F, 13.0F, 0.0F);
		this._asing_middle_right = new ModelRenderer(this, 0, 94);
		this._asing_middle_right.setTextureSize(128, 128);
		this._asing_middle_right.addBox(-2.5F, -0.5F, -8.5F, 5, 1, 17);
		this._asing_middle_right.setRotationPoint(-13.0F, 13.0F, 0.0F);
		this.Casing_vert_01 = new ModelRenderer(this, 1, 1);
		this.Casing_vert_01.setTextureSize(128, 128);
		this.Casing_vert_01.addBox(-0.5F, -6.5F, -8.5F, 1, 13, 17);
		this.Casing_vert_01.setRotationPoint(10.0F, 18.0F, 0.0F);
		this.Casing_vert_02 = new ModelRenderer(this, 81, 1);
		this.Casing_vert_02.setTextureSize(128, 128);
		this.Casing_vert_02.addBox(-0.5F, -6.5F, -8.5F, 1, 13, 17);
		this.Casing_vert_02.setRotationPoint(-10.0F, 18.0F, 0.0F);
		this.Casing_vert_03 = new ModelRenderer(this, 41, 1);
		this.Casing_vert_03.setTextureSize(128, 128);
		this.Casing_vert_03.addBox(-0.5F, -6.5F, -8.5F, 1, 13, 17);
		this.Casing_vert_03.setRotationPoint(6.0F, 18.0F, 0.0F);
		this.Casing_vert_04 = new ModelRenderer(this, 79, 53);
		this.Casing_vert_04.setTextureSize(128, 128);
		this.Casing_vert_04.addBox(-0.5F, -6.5F, -8.5F, 1, 13, 17);
		this.Casing_vert_04.setRotationPoint(-6.0F, 18.0F, 0.0F);
		this.Case = new ModelRenderer(this, 0, 35);
		this.Case.setTextureSize(128, 128);
		this.Case.addBox(-15.0F, -7.0F, -8.0F, 30, 14, 16);
		this.Case.setRotationPoint(0.0F, 18.0F, 0.0F);
		this._asing_lock_01 = new ModelRenderer(this, 49, 92);
		this._asing_lock_01.setTextureSize(128, 128);
		this._asing_lock_01.addBox(-1.0F, -1.5F, -8.5F, 2, 3, 17);
		this._asing_lock_01.setRotationPoint(8.0F, 14.0F, 0.0F);
		this._asing_lock_02 = new ModelRenderer(this, 49, 70);
		this._asing_lock_02.setTextureSize(128, 128);
		this._asing_lock_02.addBox(-1.0F, -1.5F, -8.5F, 2, 3, 17);
		this._asing_lock_02.setRotationPoint(-8.0F, 14.0F, 0.0F);
		this.Handles = new ModelRenderer(this, 1, 118);
		this.Handles.setTextureSize(128, 128);
		this.Handles.addBox(-15.5F, -1.0F, -2.5F, 31, 2, 5);
		this.Handles.setRotationPoint(0.0F, 15.0F, 0.0F);
	}

	public void renderModel(float par7) {
		this._asing_middle_01.rotateAngleX = 0.0F;
		this._asing_middle_01.rotateAngleY = 0.0F;
		this._asing_middle_01.rotateAngleZ = 0.0F;
		this._asing_middle_01.renderWithRotation(par7);
		this._asing_middle_left.rotateAngleX = 0.0F;
		this._asing_middle_left.rotateAngleY = 0.0F;
		this._asing_middle_left.rotateAngleZ = 0.0F;
		this._asing_middle_left.renderWithRotation(par7);
		this._asing_middle_right.rotateAngleX = 0.0F;
		this._asing_middle_right.rotateAngleY = 0.0F;
		this._asing_middle_right.rotateAngleZ = 0.0F;
		this._asing_middle_right.renderWithRotation(par7);
		this.Casing_vert_01.rotateAngleX = 0.0F;
		this.Casing_vert_01.rotateAngleY = 0.0F;
		this.Casing_vert_01.rotateAngleZ = 0.0F;
		this.Casing_vert_01.renderWithRotation(par7);
		this.Casing_vert_02.rotateAngleX = 0.0F;
		this.Casing_vert_02.rotateAngleY = 0.0F;
		this.Casing_vert_02.rotateAngleZ = 0.0F;
		this.Casing_vert_02.renderWithRotation(par7);
		this.Casing_vert_03.rotateAngleX = 0.0F;
		this.Casing_vert_03.rotateAngleY = 0.0F;
		this.Casing_vert_03.rotateAngleZ = 0.0F;
		this.Casing_vert_03.renderWithRotation(par7);
		this.Casing_vert_04.rotateAngleX = 0.0F;
		this.Casing_vert_04.rotateAngleY = 0.0F;
		this.Casing_vert_04.rotateAngleZ = 0.0F;
		this.Casing_vert_04.renderWithRotation(par7);
		this.Case.rotateAngleX = 0.0F;
		this.Case.rotateAngleY = 0.0F;
		this.Case.rotateAngleZ = 0.0F;
		this.Case.renderWithRotation(par7);
		this._asing_lock_01.rotateAngleX = 0.0F;
		this._asing_lock_01.rotateAngleY = 0.0F;
		this._asing_lock_01.rotateAngleZ = 0.0F;
		this._asing_lock_01.renderWithRotation(par7);
		this._asing_lock_02.rotateAngleX = 0.0F;
		this._asing_lock_02.rotateAngleY = 0.0F;
		this._asing_lock_02.rotateAngleZ = 0.0F;
		this._asing_lock_02.renderWithRotation(par7);
		this.Handles.rotateAngleX = 0.0F;
		this.Handles.rotateAngleY = 0.0F;
		this.Handles.rotateAngleZ = 0.0F;
		this.Handles.renderWithRotation(par7);
	}

	public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7) {
		this._asing_middle_01.rotateAngleX = 0.0F;
		this._asing_middle_01.rotateAngleY = 0.0F;
		this._asing_middle_01.rotateAngleZ = 0.0F;
		this._asing_middle_01.renderWithRotation(par7);
		this._asing_middle_left.rotateAngleX = 0.0F;
		this._asing_middle_left.rotateAngleY = 0.0F;
		this._asing_middle_left.rotateAngleZ = 0.0F;
		this._asing_middle_left.renderWithRotation(par7);
		this._asing_middle_right.rotateAngleX = 0.0F;
		this._asing_middle_right.rotateAngleY = 0.0F;
		this._asing_middle_right.rotateAngleZ = 0.0F;
		this._asing_middle_right.renderWithRotation(par7);
		this.Casing_vert_01.rotateAngleX = 0.0F;
		this.Casing_vert_01.rotateAngleY = 0.0F;
		this.Casing_vert_01.rotateAngleZ = 0.0F;
		this.Casing_vert_01.renderWithRotation(par7);
		this.Casing_vert_02.rotateAngleX = 0.0F;
		this.Casing_vert_02.rotateAngleY = 0.0F;
		this.Casing_vert_02.rotateAngleZ = 0.0F;
		this.Casing_vert_02.renderWithRotation(par7);
		this.Casing_vert_03.rotateAngleX = 0.0F;
		this.Casing_vert_03.rotateAngleY = 0.0F;
		this.Casing_vert_03.rotateAngleZ = 0.0F;
		this.Casing_vert_03.renderWithRotation(par7);
		this.Casing_vert_04.rotateAngleX = 0.0F;
		this.Casing_vert_04.rotateAngleY = 0.0F;
		this.Casing_vert_04.rotateAngleZ = 0.0F;
		this.Casing_vert_04.renderWithRotation(par7);
		this.Case.rotateAngleX = 0.0F;
		this.Case.rotateAngleY = 0.0F;
		this.Case.rotateAngleZ = 0.0F;
		this.Case.renderWithRotation(par7);
		this._asing_lock_01.rotateAngleX = 0.0F;
		this._asing_lock_01.rotateAngleY = 0.0F;
		this._asing_lock_01.rotateAngleZ = 0.0F;
		this._asing_lock_01.renderWithRotation(par7);
		this._asing_lock_02.rotateAngleX = 0.0F;
		this._asing_lock_02.rotateAngleY = 0.0F;
		this._asing_lock_02.rotateAngleZ = 0.0F;
		this._asing_lock_02.renderWithRotation(par7);
		this.Handles.rotateAngleX = 0.0F;
		this.Handles.rotateAngleY = 0.0F;
		this.Handles.rotateAngleZ = 0.0F;
		this.Handles.renderWithRotation(par7);
	}
}
