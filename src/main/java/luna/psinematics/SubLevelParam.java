package luna.psinematics;

import dev.ryanhcode.sable.companion.SubLevelAccess;
import vazkii.psi.api.spell.param.ParamSpecific;

public class SubLevelParam extends ParamSpecific<SubLevelAccess>{
	
	public SubLevelParam(String name, int color, boolean canDisable){
		super(name, color, canDisable, false);
	}
	
	protected Class<SubLevelAccess> getRequiredType(){
		return SubLevelAccess.class;
	}
}