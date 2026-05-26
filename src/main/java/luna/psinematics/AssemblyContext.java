package luna.psinematics;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;

public class AssemblyContext{
	
	private static final String ASSEMBLY_TAG = "psinematics:current_assembly";
	private static final String COMPLETE_ASSEMBLY_TAG = "psinematics:completed_assembly";
	
	private @Nullable BlockPos anchorGlobal, anchorLocal;
	private @Nullable SubLevel subLevel;
	
	public static @Nullable AssemblyContext getAssemblyStatus(SpellContext ctx){
		return ctx.customData.get(ASSEMBLY_TAG) instanceof AssemblyContext ac ? ac : null;
	}
	
	public static void beginAssembly(SpellContext ctx) throws SpellRuntimeException{
		if(ctx.customData.containsKey(ASSEMBLY_TAG))
			throw new SpellRuntimeException("psinematics.spellerror.assembly_already_begun");
		ctx.customData.put(ASSEMBLY_TAG, new AssemblyContext());
	}
	
	public static void endAssembly(SpellContext ctx) throws SpellRuntimeException{
		if(!ctx.customData.containsKey(ASSEMBLY_TAG))
			throw new SpellRuntimeException("psinematics.spellerror.assembly_not_begun");
		SubLevelAccess completed = getCurrentAssembly(ctx);
		if(completed != null){
			if(completed instanceof ServerSubLevel ssl){
				ssl.updateBoundingBox();
				// required for forces to be applied on the same tick
				SubLevelContainer.getContainer(ssl.getLevel()).physicsSystem().getPipeline().physicsTick(1/20d);
			}
			ctx.customData.put(COMPLETE_ASSEMBLY_TAG, completed);
		}
		ctx.customData.remove(ASSEMBLY_TAG);
	}
	
	public static SubLevelAccess getCurrentAssembly(SpellContext ctx){
		return ctx.customData.containsKey(ASSEMBLY_TAG) && ctx.customData.get(ASSEMBLY_TAG) instanceof AssemblyContext ac ? ac.subLevel : null;
	}
	
	public static void putCompletedAssembly(SpellContext ctx, SubLevelAccess subLevel){
		ctx.customData.put(COMPLETE_ASSEMBLY_TAG, subLevel);
	}
	
	public static SubLevelAccess getCompletedAssembly(SpellContext ctx){
		return ctx.customData.containsKey(COMPLETE_ASSEMBLY_TAG) && ctx.customData.get(COMPLETE_ASSEMBLY_TAG) instanceof SubLevelAccess sla ? sla : null;
	}
	
	public static BlockPos toAssemblyPos(SpellContext ctx, BlockPos pos){
		AssemblyContext ac = getAssemblyStatus(ctx);
		if(ac != null){
			if(ac.anchorGlobal == null){
				// create a new sublevel, and make this the anchor
				Level level = ctx.focalPoint.level();
				SubLevelContainer container = SubLevelContainer.getContainer(level);
				Pose3d pose = new Pose3d();
				pose.position().set(pos.getX(), pos.getY(), pos.getZ());
				SubLevel newConstruct = container.allocateNewSubLevel(pose);
				LevelPlot plot = newConstruct.getPlot();
				plot.newEmptyChunk(plot.getCenterChunk());
				BlockPos start = plot.getCenterBlock().atY(pos.getY());
				ac.anchorGlobal = pos;
				ac.anchorLocal = start;
				ac.subLevel = newConstruct;
				return start;
			}else{
				return ac.anchorLocal.offset(pos.subtract(ac.anchorGlobal));
			}
		}
		return pos;
	}
}