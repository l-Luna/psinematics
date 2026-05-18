package luna.psinematics.operators;

import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import luna.psinematics.PhySpellHelper;
import luna.psinematics.SubLevelParam;
import org.joml.Vector3dc;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.piece.PieceOperator;

public class LocateCenterOfMassOp extends PieceOperator{
	
	private static final SpellParam<SubLevelAccess> target = new SubLevelParam(SpellParam.GENERIC_NAME_TARGET, SpellParam.RED, false);
	
	public LocateCenterOfMassOp(Spell spell){
		super(spell);
	}
	
	public Class<?> getEvaluationType(){
		return Vector3.class;
	}
	
	public void initParams(){
		addParam(target);
	}
	
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException{
		// more expensive than usual
		meta.addStat(EnumSpellStat.COMPLEXITY, 4);
	}
	
	public Vector3 execute(SpellContext context) throws SpellRuntimeException{
		SubLevelAccess subLevel = getParamValue(context, target);
		if(!(subLevel instanceof ServerSubLevel ssl))
			throw new SpellRuntimeException(PhySpellHelper.NULL_CONSTRUCT);
		Vector3dc com = ssl.getMassTracker().getCenterOfMass();
		return com != null ? new Vector3(com.x(), com.y(), com.z()) : null;
	}
}