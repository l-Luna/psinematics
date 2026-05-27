package luna.psinematics.tricks;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import luna.psinematics.PhySpellHelper;
import luna.psinematics.SubLevelParam;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import org.joml.Vector3d;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

import java.util.List;

public class DisassembleTrick extends PieceTrick{
	
	private static final SpellParam<SubLevelAccess> targetParam = new SubLevelParam(SpellParam.GENERIC_NAME_TARGET, SpellParam.RED, false);
	private static final SpellParam<Number> maxParam = new ParamNumber(SpellParam.GENERIC_NAME_MAX, SpellParam.CYAN, false, true);
	
	public DisassembleTrick(Spell spell){
		super(spell);
		setStatLabel(EnumSpellStat.POTENCY, new StatLabel(SpellParam.GENERIC_NAME_MAX, true).mul(15));
		setStatLabel(EnumSpellStat.COST, new StatLabel(SpellParam.GENERIC_NAME_MAX, true).sub(1).parenthesize().mul(20).add(30));
	}
	
	public void initParams(){
		addParam(targetParam);
		addParam(maxParam);
	}
	
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException, ArithmeticException{
		super.addToMetadata(meta);
		Double maxVal = this.<Double>getParamEvaluation(maxParam);
		if(maxVal == null || maxVal <= 0 || maxVal != maxVal.intValue())
			throw new SpellCompilationException(SpellCompilationException.NON_POSITIVE_VALUE, x, y);
		if(maxVal > 32)
			throw new SpellCompilationException(SpellCompilationException.INVALID_PARAM);
		
		meta.addStat(EnumSpellStat.POTENCY, (int)(maxVal * 15));
		meta.addStat(EnumSpellStat.COST, (int)((maxVal - 1) * 20 + 30));
	}
	
	public Object execute(SpellContext context) throws SpellRuntimeException{
		SubLevelAccess target = getParamValue(context, targetParam);
		if(target == null)
			throw new SpellRuntimeException(PhySpellHelper.NULL_CONSTRUCT);
		
		ServerSubLevel ssl = (ServerSubLevel)target;
		ServerLevel level = ssl.getLevel();
		int max = getParamValue(context, maxParam).intValue();
		
		// TODO: move shared logic to sable?
		// TODO: rotate in world?
		BoundingBox3ic bounds = ssl.getPlot().getBoundingBox();
		int amount = (int)BlockPos.betweenClosedStream(bounds.toMojang())
				.filter(pos -> !level.getBlockState(pos).isAir())
				.limit(max + 1)
				.count();
		if(amount > max)
			throw new SpellRuntimeException("psinematics.spellerror.too_many_blocks");
		
		// treat the central block as an anchor
		BlockPos anchorLocal = ssl.getPlot().getCenterBlock();
		Vector3d anchorGlobalD = ssl.logicalPose().transformPosition(JOMLConversion.atCenterOf(anchorLocal));
		BlockPos anchorGlobal = new BlockPos((int)Math.floor(anchorGlobalD.x), (int)Math.floor(anchorGlobalD.y), (int)Math.floor(anchorGlobalD.z));
//		BlockPos diff = anchorGlobal.subtract(anchorLocal);
		SubLevelAssemblyHelper.AssemblyTransform transform = new SubLevelAssemblyHelper.AssemblyTransform(anchorLocal, anchorGlobal, 0, Rotation.NONE, level);
		List<BlockPos> blocks = BlockPos.betweenClosedStream(bounds.toMojang()).filter(x -> !level.getBlockState(x).isAir()).map(BlockPos::new).toList();
		
		ssl.getPlot().kickAllEntities();
		SubLevelAssemblyHelper.moveOtherStuff(level, transform, blocks, bounds);
		SubLevelAssemblyHelper.moveBlocks(level, transform, blocks);
		SubLevelAssemblyHelper.moveTrackingPoints(level, bounds, null, transform);
		return null;
	}
}