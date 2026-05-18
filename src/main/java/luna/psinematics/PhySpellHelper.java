package luna.psinematics;

import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import vazkii.psi.api.spell.SpellRuntimeException;

public class PhySpellHelper{
	
	public static final String NULL_CONSTRUCT = "psinematics.spellerror.nullconstruct";
	
	public static RigidBodyHandle getHandle(SubLevelAccess subLevel) throws SpellRuntimeException{
		ServerSubLevel ssl = (ServerSubLevel)subLevel;
		return SubLevelContainer.getContainer(ssl.getLevel()).physicsSystem().getPhysicsHandle(ssl);
	}
}