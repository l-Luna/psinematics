package luna.psinematics;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;

public class AssemblyContext{
	
	private static final String ASSEMBLY_TAG = "psinematics:current_assembly";
	
	private @Nullable BlockPos anchorGlobal, anchorLocal;
	// is this necessary?
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
		ctx.customData.remove(ASSEMBLY_TAG);
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