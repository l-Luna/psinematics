package luna.psinematics.tricks;

import dev.ryanhcode.sable.companion.SubLevelAccess;
import luna.psinematics.PhySpellHelper;
import luna.psinematics.SubLevelParam;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;

public class DisassembleTrick extends PieceTrick{
	
	private static final SpellParam<SubLevelAccess> targetParam = new SubLevelParam(SpellParam.GENERIC_NAME_TARGET, SpellParam.RED, false);
	private static final SpellParam<Number> maxParam = new ParamNumber(SpellParam.GENERIC_NAME_MAX, SpellParam.CYAN, false, true);
	
	public DisassembleTrick(Spell spell){
		super(spell);
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
	}
	
	public Object execute(SpellContext context) throws SpellRuntimeException{
		SubLevelAccess target = getParamValue(context, targetParam);
		if(target == null)
			throw new SpellRuntimeException(PhySpellHelper.NULL_CONSTRUCT);
		
		// TODO: move shared logic to sable?
		
		return null;
	}
}