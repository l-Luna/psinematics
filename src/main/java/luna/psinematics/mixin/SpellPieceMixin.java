package luna.psinematics.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import luna.psinematics.AssemblyContext;
import luna.psinematics.Psinematics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;

@Mixin(SpellPiece.class)
public abstract class SpellPieceMixin{

	@Shadow
	public abstract ResourceLocation getRegistryKey();
	
	@ModifyReturnValue(method = "getParamValue", at = @At("RETURN"))
	<T> T getParamValue(T original, SpellContext context, SpellParam<T> param){
		if(original instanceof Vector3 vec &&
				param.name.equals(SpellParam.GENERIC_NAME_POSITION) &&
				Psinematics.isOf(this.getRegistryKey(), Psinematics.BLOCK_PLACEMENT_TRICKS, PsiAPI.SPELL_PIECE_REGISTRY)){
			//noinspection unchecked
			return (T)Vector3.fromBlockPos(AssemblyContext.toAssemblyPos(context, vec.toBlockPos()));
		}
		return original;
	}
}