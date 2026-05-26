package luna.psinematics.tricks;

import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import luna.psinematics.AssemblyContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class AssembleConnectedTrick extends PieceTrick{
	
	private static final SpellParam<Vector3> positionParam = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false);
	private static final SpellParam<Number> maxParam = new ParamNumber(SpellParam.GENERIC_NAME_MAX, SpellParam.CYAN, false, true);
	// specificity?
	
	public AssembleConnectedTrick(Spell spell){
		super(spell);
	}
	
	public void initParams(){
		addParam(positionParam);
		addParam(maxParam);
	}
	
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException, ArithmeticException{
		super.addToMetadata(meta);
		Double maxVal = this.<Double>getParamEvaluation(maxParam);
		if(maxVal == null || maxVal <= 0 || maxVal != maxVal.intValue())
			throw new SpellCompilationException(SpellCompilationException.NON_POSITIVE_VALUE, x, y);
		if(maxVal > 32)
			throw new SpellCompilationException(SpellCompilationException.INVALID_PARAM);
	}
	
	public Object execute(SpellContext context) throws SpellRuntimeException{
		double max = getParamValue(context, maxParam).doubleValue();
		Vector3 position = getParamValue(context, positionParam);
		if(position == null)
			throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
		if(SableCompanion.INSTANCE.getContaining(context.focalPoint.level(), position.toVec3i()) != null)
			throw new SpellRuntimeException("psinematics.spellerror.point_inside_sub_level");
		
		SubLevelAssemblyHelper.GatherResult result = SubLevelAssemblyHelper.gatherConnectedBlocks(position.toBlockPos(), (ServerLevel)context.focalPoint.level(), (int)max, null);
		switch(result.assemblyState()){
			case TOO_MANY_BLOCKS -> throw new SpellRuntimeException("psinematics.spellerror.too_many_blocks");
			case NO_BLOCKS -> throw new SpellRuntimeException("psinematics.spellerror.no_blocks");
		}
		for(BlockPos block : result.blocks())
			if(!context.isInRadius(Vector3.fromBlockPos(block)))
				throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
		ServerSubLevel completed = SubLevelAssemblyHelper.assembleBlocks((ServerLevel)context.focalPoint.level(), position.toBlockPos(), result.blocks(), result.boundingBox());
		completed.updateBoundingBox();
		AssemblyContext.putCompletedAssembly(context, completed);
		
		return null;
	}
}