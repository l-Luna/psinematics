package luna.psinematics;

import luna.psinematics.operators.BlockMassOp;
import luna.psinematics.operators.ContainingSubLevelOp;
import luna.psinematics.operators.LocateCenterOfMassOp;
import luna.psinematics.operators.SubLevelMassOp;
import luna.psinematics.selectors.CurrentAssemblySelector;
import luna.psinematics.selectors.CurrentSubLevelSelector;
import luna.psinematics.tricks.*;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
import vazkii.psi.api.spell.SpellPieceType;

import java.util.Map;

@Mod(Psinematics.MODID)
public class Psinematics{
	public static final String MODID = "psinematics";
	
	private static final Map<String, Class<? extends SpellPiece>> PIECES = Map.ofEntries(
			Map.entry("current_sub_level", CurrentSubLevelSelector.class),
			Map.entry("current_assembly", CurrentAssemblySelector.class),
			
			Map.entry("containing_sub_level", ContainingSubLevelOp.class),
			Map.entry("locate_center_of_mass", LocateCenterOfMassOp.class),
			Map.entry("block_mass", BlockMassOp.class),
			Map.entry("sub_level_mass", SubLevelMassOp.class),
			
			Map.entry("begin_assembly", BeginAssemblyTrick.class),
			Map.entry("end_assembly", EndAssemblyTrick.class),
			Map.entry("assemble_connected", AssembleConnectedTrick.class),
			Map.entry("disassemble", DisassembleTrick.class),
			
			Map.entry("add_momentum", AddMomentumTrick.class)
	);
	
	public static final TagKey<SpellPieceType> BLOCK_PLACEMENT_TRICKS = TagKey.create(PsiAPI.SPELL_PIECE_REGISTRY_TYPE_KEY, psinId("block_placement_tricks"));
	
	public Psinematics(IEventBus modEventBus, ModContainer modContainer){
		modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
		modEventBus.addListener(this::register);
	}
	
	@SubscribeEvent
	public void register(RegisterEvent event){
		event.register(PsiAPI.SPELL_PIECE_REGISTRY_TYPE_KEY, helper ->
				PIECES.forEach((id, clazz) -> helper.register(psinId(id), SpellPieceType.ofClass(clazz))));
	}
	
	public static ResourceLocation psinId(String path){
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
	
	public static <T> boolean isOf(ResourceLocation id, TagKey<T> tag, Registry<T> registry){
		return registry.getHolder(registry.getId(id)).orElseThrow().is(tag);
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
