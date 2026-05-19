package luna.psinematics.tricks;

import luna.psinematics.AssemblyContext;
import vazkii.psi.api.spell.*;
import vazkii.psi.api.spell.piece.PieceTrick;

public class EndAssemblyTrick extends PieceTrick{
	
	public EndAssemblyTrick(Spell spell){
		super(spell);
		setStatLabel(EnumSpellStat.PROJECTION, new StatLabel(0));
	}
	
	public void addToMetadata(SpellMetadata meta) throws SpellCompilationException, ArithmeticException{
		// does not add projection
		meta.addStat(EnumSpellStat.COMPLEXITY, 1);
	}
	
	public Object execute(SpellContext context) throws SpellRuntimeException{
		AssemblyContext.endAssembly(context);
		return super.execute(context);
	}
}