package luna.psinematics.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ryanhcode.sable.companion.SableCompanion;
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
		Vector3d projected = SableCompanion.INSTANCE.projectOutOfSubLevel(focalPoint.level(), new Vector3d(x, y, z));
		return original.call(projected.x, projected.y, projected.z);
	}
}