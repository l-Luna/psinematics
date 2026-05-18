package luna.psinematics.selectors;

import dev.ryanhcode.sable.sublevel.SubLevel;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.piece.PieceSelector;

public class SubLevelRaycastSelector extends PieceSelector{
	
	public SubLevelRaycastSelector(Spell spell){
		super(spell);
	}
	
	public Class<?> getEvaluationType(){
		return SubLevel.class;
	}
}