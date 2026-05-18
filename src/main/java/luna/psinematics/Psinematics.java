package luna.psinematics;

import com.mojang.logging.LogUtils;
import luna.psinematics.operators.LocateCenterOfMassOp;
import luna.psinematics.selectors.CurrentSubLevelSelector;
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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import vazkii.psi.api.ClientPsiAPI;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.common.lib.LibPieceGroups;

import java.util.Arrays;
import java.util.Collection;

@Mod(Psinematics.MODID)
public class Psinematics{
	public static final String MODID = "psinematics";
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public static final DeferredRegister<Class<? extends SpellPiece>> SPELL_PIECES =
			DeferredRegister.create(PsiAPI.SPELL_PIECE_REGISTRY_TYPE_KEY, MODID);
	public static final DeferredRegister<Collection<Class<? extends SpellPiece>>> ADVANCEMENT_GROUPS =
			DeferredRegister.create(PsiAPI.ADVANCEMENT_GROUP_REGISTRY_KEY, MODID);
	
	// selectors
	public static final DeferredHolder<Class<? extends SpellPiece>, Class<CurrentSubLevelSelector>> CURRENT_SUB_LEVEL =
			SPELL_PIECES.register("current_sub_level", () -> CurrentSubLevelSelector.class);
	
	// operators
	public static final DeferredHolder<Class<? extends SpellPiece>, Class<LocateCenterOfMassOp>> LOCATE_CENTER_OF_MASS =
			SPELL_PIECES.register("locate_center_of_mass", () -> LocateCenterOfMassOp.class);
	
	// tricks
	
	// collections
	public static final DeferredHolder<Collection<Class<? extends SpellPiece>>, Collection<Class<? extends SpellPiece>>> CONSTRUCTS =
			ADVANCEMENT_GROUPS.register(LibPieceGroups.MEMORY_MANAGEMENT,
					() -> Arrays.asList(
							CurrentSubLevelSelector.class,
							LocateCenterOfMassOp.class
					));
	
	public Psinematics(IEventBus modEventBus, ModContainer modContainer){
		modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
		SPELL_PIECES.register(modEventBus);
		ADVANCEMENT_GROUPS.register(modEventBus);
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
				registerSpellMaterial(helper, "locate_center_of_mass");
				registerSpellMaterial(helper, "current_sub_level");
			});
		}
		
		private static void registerSpellMaterial(RegisterEvent.RegisterHelper<Material> helper, String name){
			helper.register(psinId(name), new Material(InventoryMenu.BLOCK_ATLAS, psinId("spell/" + name)));
		}
	}
}
