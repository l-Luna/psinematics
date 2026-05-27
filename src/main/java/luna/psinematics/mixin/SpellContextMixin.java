package luna.psinematics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import luna.psinematics.AssemblyContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.psi.api.spell.SpellContext;

@Mixin(value = SpellContext.class, remap = false)
public class SpellContextMixin{
	
	@Shadow
	public Entity focalPoint;
	
	@WrapMethod(method = "isInRadius(DDD)Z")
	boolean isInRadius(double x, double y, double z, Operation<Boolean> original){
		// we might also have started a new sublevel, and attempting to place a new block there
		// this position will be outside its bounds, so projecting won't handle it for us; check it as well
		// this doesn't necessarily distinguish whether the coordinates given were projected into the new assembly,
		// but these coordinates are random and not exposed to spells
		SpellContext ctx = (SpellContext)(Object)this;
		SubLevelAccess assembly = AssemblyContext.getCurrentAssembly(ctx);
		if(assembly != null){
			BlockPos offset = AssemblyContext.getCurrentAssemblyOffset(ctx);
			if(original.call(x - offset.getX(), y - offset.getY(), z - offset.getZ()))
				return true;
		}
		Vector3d projected = SableCompanion.INSTANCE.projectOutOfSubLevel(focalPoint.level(), new Vector3d(x, y, z));
		return original.call(projected.x, projected.y, projected.z);
	}
}