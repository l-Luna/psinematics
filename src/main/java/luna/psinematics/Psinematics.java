package luna.psinematics;

import luna.psinematics.operators.ContainingSubLevelOp;
import luna.psinematics.operators.LocateCenterOfMassOp;
import luna.psinematics.selectors.CurrentAssemblySelector;
import luna.psinematics.selectors.CurrentSubLevelSelector;
import luna.psinematics.tricks.AddMomentumTrick;
import luna.psinematics.tricks.AssembleConnectedTrick;
import luna.psinematics.tricks.BeginAssemblyTrick;
import luna.psinematics.tricks.EndAssemblyTrick;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import vazkii.psi.api.ClientPsiAPI;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.SpellPiece;

import java.util.ArrayList;
import java.util.Map;

@Mod(Psinematics.MODID)
public class Psinematics{
	public static final String MODID = "psinematics";
	
	private static final Map<String, Class<? extends SpellPiece>> PIECES = Map.of(
			"current_sub_level", CurrentSubLevelSelector.class,
			"current_assembly", CurrentAssemblySelector.class,
			
			"containing_sub_level", ContainingSubLevelOp.class,
			"locate_center_of_mass", LocateCenterOfMassOp.class,
			
			"begin_assembly", BeginAssemblyTrick.class,
			"end_assembly", EndAssemblyTrick.class,
			"assemble_connected", AssembleConnectedTrick.class,
			
			"add_momentum", AddMomentumTrick.class
	);
	
	public Psinematics(IEventBus modEventBus, ModContainer modContainer){
		modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
		modEventBus.addListener(this::register);
	}
	
	@SubscribeEvent
	public void register(RegisterEvent event){
		event.register(PsiAPI.SPELL_PIECE_REGISTRY_TYPE_KEY, helper ->
				PIECES.forEach((id, clazz) -> helper.register(psinId(id), clazz)));
		event.register(PsiAPI.ADVANCEMENT_GROUP_REGISTRY_KEY, helper ->
				helper.register(psinId("constructs"), new ArrayList<>(PIECES.values())));
	}
	
	public static ResourceLocation psinId(String path){
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
	
	@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
	public static class PsinematicsClient{
		
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event){}
		
		@SubscribeEvent
		public static void register(RegisterEvent event){
			event.register(ClientPsiAPI.SPELL_PIECE_MATERIAL, helper -> {
				for(String id : PIECES.keySet())
					helper.register(psinId(id), new Material(InventoryMenu.BLOCK_ATLAS, psinId("spell/" + id)));
			});
		}
		
	}
}
