package luna.psinematics.selectors;

import dev.ryanhcode.sable.companion.SubLevelAccess;
import luna.psinematics.AssemblyContext;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceSelector;

public class CurrentAssemblySelector extends PieceSelector{
	
	public CurrentAssemblySelector(Spell spell){
		super(spell);
	}
	
	public Class<?> getEvaluationType(){
		return SubLevelAccess.class;
	}
	
	public SubLevelAccess execute(SpellContext ctx){
		return AssemblyContext.getCompletedAssembly(ctx);
	}
}