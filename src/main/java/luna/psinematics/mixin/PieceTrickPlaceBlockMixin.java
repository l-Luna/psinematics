package luna.psinematics.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import luna.psinematics.AssemblyContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.spell.trick.block.PieceTrickPlaceBlock;

@Mixin(value = PieceTrickPlaceBlock.class, remap = false)
public class PieceTrickPlaceBlockMixin{

	@WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Lvazkii/psi/common/spell/trick/block/PieceTrickPlaceBlock;placeBlock(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;IZLnet/minecraft/core/Direction;Lnet/minecraft/core/Direction;)V"))
	void modifyPlacePosition(Player player, Level world, BlockPos pos, int slot, boolean particles, Direction direction, Direction horizontalDirection, Operation<Void> original, SpellContext context){
		original.call(player, world, AssemblyContext.toAssemblyPos(context, pos), slot, particles, direction, horizontalDirection);
	}
}