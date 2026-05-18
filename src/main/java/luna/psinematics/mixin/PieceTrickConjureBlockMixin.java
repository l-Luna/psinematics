package luna.psinematics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import luna.psinematics.AssemblyContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.spell.trick.block.PieceTrickConjureBlock;

@Mixin(value = PieceTrickConjureBlock.class, remap = false)
public class PieceTrickConjureBlockMixin{

	@WrapMethod(method = "conjure(Lvazkii/psi/api/spell/SpellContext;Ljava/lang/Number;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;)V")
	private static void modifyConjurePosition(SpellContext context, Number timeVal, BlockPos pos, Level world, BlockState state, Operation<Void> original){
		original.call(context, timeVal, AssemblyContext.toAssemblyPos(context, pos), world, state);
	}
}