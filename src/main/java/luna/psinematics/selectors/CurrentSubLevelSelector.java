package luna.psinematics.selectors;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.piece.PieceSelector;

public class CurrentSubLevelSelector extends PieceSelector{
	
	public CurrentSubLevelSelector(Spell spell){
		super(spell);
	}
	
	public Class<?> getEvaluationType(){
		return SubLevelAccess.class;
	}
	
	public SubLevelAccess execute(SpellContext context){
		return SableCompanion.INSTANCE.getTrackingOrVehicleSubLevel(context.focalPoint);
	}
}