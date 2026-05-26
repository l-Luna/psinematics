package luna.psinematics.operators;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import net.minecraft.core.Position;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceOperator;

public class ContainingSubLevelOp extends PieceOperator{
	
	private static final SpellParam<Vector3> location = new ParamVector(SpellParam.GENERIC_NAME_POSITION, SpellParam.BLUE, false, false);
	
	public ContainingSubLevelOp(Spell spell){
		super(spell);
	}
	
	public Class<?> getEvaluationType(){
		return SubLevelAccess.class;
	}
	
	public void initParams(){
		addParam(location);
	}
	
	public SubLevelAccess execute(SpellContext context) throws SpellRuntimeException{
		Vector3 locationVal = this.getParamValue(context, location);
		if(locationVal == null)
			throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
		// ...if the target is a block that is part of a sublevel
		SubLevelAccess direct = SableCompanion.INSTANCE.getContaining(context.focalPoint.level(), locationVal.toBlockPos());
		if(direct != null)
			return direct;
		BoundingBox3d box = new BoundingBox3d((Position)locationVal.toVec3D(), locationVal.toVec3D()).expand(1, 1, 1);
		// ...if the target is in the normal world, inside a sublevel
		for(SubLevelAccess sublevel : SableCompanion.INSTANCE.getAllIntersecting(context.focalPoint.level(), box))
			return sublevel;
		return null;
	}
}