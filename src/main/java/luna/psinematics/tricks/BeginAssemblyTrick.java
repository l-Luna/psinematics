package luna.psinematics.tricks;

import luna.psinematics.AssemblyContext;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.piece.PieceTrick;

public class BeginAssemblyTrick extends PieceTrick{
	
	public BeginAssemblyTrick(Spell spell){
		super(spell);
	}
	
	public Object execute(SpellContext context) throws SpellRuntimeException{
		AssemblyContext.beginAssembly(context);
		return super.execute(context);
	}
}