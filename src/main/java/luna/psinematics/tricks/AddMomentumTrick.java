package luna.psinematics.tricks;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import luna.psinematics.PhySpellHelper;
import luna.psinematics.SubLevelParam;
import org.joml.Vector3d;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class AddMomentumTrick extends PieceTrick{
	
	private static final SpellParam<SubLevelAccess> targetParam = new SubLevelParam(SpellParam.GENERIC_NAME_TARGET, SpellParam.RED, false);
	private static final SpellParam<Vector3> pointParam = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false);
	private static final SpellParam<Number> powerParam = new ParamNumber(SpellParam.GENERIC_NAME_POWER, SpellParam.CYAN, false, true);
	private static final SpellParam<Vector3> directionParam = new ParamVector(SpellParam.GENERIC_NAME_DIRECTION, SpellParam.GREEN, false, false);
	
	public AddMomentumTrick(Spell spell){
		super(spell);
		setStatLabel(EnumSpellStat.POTENCY, new StatLabel(SpellParam.GENERIC_NAME_POWER, true).abs().add(1).parenthesize().square().mul(2));
		setStatLabel(EnumSpellStat.COST, new StatLabel(SpellParam.GENERIC_NAME_POWER, true).abs().add(1).parenthesize().square().mul(5));
	}
	
	public void initParams(){
		addParam(targetParam);
		addParam(pointParam);
		addParam(powerParam);
		addParam(directionParam);
	}
	
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException, ArithmeticException{
		super.addToMetadata(meta);
		Double powerVal = this.<Double>getParamEvaluation(powerParam);
		if(powerVal == null)
			throw new SpellCompilationException(SpellCompilationException.NON_POSITIVE_VALUE, x, y);
		double base = Math.abs(powerVal);
		base = base + 1;
		base = base * base;
		meta.addStat(EnumSpellStat.POTENCY, (int)(base * 2));
		meta.addStat(EnumSpellStat.COST, (int)(base * 5));
	}
	
	public Object execute(SpellContext context) throws SpellRuntimeException{
		SubLevelAccess target = getParamValue(context, targetParam);
		if(target == null)
			throw new SpellRuntimeException(PhySpellHelper.NULL_CONSTRUCT);
		Vector3 point = getParamValue(context, pointParam);
		if(point == null)
			throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
		Vector3d projected = SableCompanion.INSTANCE.projectOutOfSubLevel(context.focalPoint.level(), new Vector3d(point.x, point.y, point.z));
		point.set(projected.x, projected.y, projected.z);
		Vector3 direction = getParamValue(context, directionParam);
		if(direction == null)
			throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
		
		if(!context.isInRadius(point))
			throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
		if(!target.boundingBox().contains(point.x, point.y, point.z))
			throw new SpellRuntimeException("psinematics.spellerror.point_out_of_sub_level");
		
		Vector3d localPos = target.logicalPose().transformPositionInverse(new Vector3d(point.x, point.y, point.z));
		direction = direction.normalize();
		Vector3d localForce = target.logicalPose().transformNormalInverse(new Vector3d(direction.x, direction.y, direction.z));
		double power = getParamValue(context, powerParam).doubleValue();
		localForce.mul(power);
		
		PhySpellHelper.getHandle(target).applyImpulseAtPoint(localPos, localForce);
		return null;
	}
}