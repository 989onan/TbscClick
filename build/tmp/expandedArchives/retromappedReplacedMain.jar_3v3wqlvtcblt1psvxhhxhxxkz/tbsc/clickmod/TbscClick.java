package tbsc.clickmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod(modid = TbscClick.MODID, version = TbscClick.VERSION, guiFactory = TbscClick.GUI_FACTORY, canBeDeactivated = true)
public class TbscClick {

    public static final String MODID = "TbscClick";
    public static final String VERSION = "1.1.0";
    public static final String GUI_FACTORY = "tbsc.clickmod.gui.TCGuiFactory";
    public static boolean shouldLeftClick = false;
    public static boolean shouldRightClick = false;
    public static KeyBinding keyToggleRight;
    public static KeyBinding keyToggleLeft;
    public static Configuration config;
    public static float clickDelay = 0.5F;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide() == Side.SERVER) {
            FMLLog.bigWarning("CLIENT-SIDE MOD INSTALLED ON SERVER. REMOVE FROM SERVER.");
            FMLLog.bigWarning("You are lucky I am adding a safety-check, or you'll crash.");
            FMLCommonHandler.instance().exitJava(1, false);
        }
        MinecraftForge.EVENT_BUS.register(this);
        config = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();

        keyToggleRight = new KeyBinding("key.tbscclick.toggleright", Keyboard.KEY_G, "key.categories.misc");
        keyToggleLeft = new KeyBinding("key.tbscclick.toggleleft", Keyboard.KEY_H, "key.categories.misc");
        ClientRegistry.registerKeyBinding(keyToggleRight);
        ClientRegistry.registerKeyBinding(keyToggleLeft);
    }

    public static void syncConfig() {
        try {
            // Load config
            config.load();

            // Read props from config
            Property clickDelayProp = config.get(Configuration.CATEGORY_GENERAL, "clickDelay", 0.5, "Delay between auto-clicks. (Applies ONLY to RClick.)");
            clickDelay = (float) clickDelayProp.getDouble();
        } catch (Exception e) {
            // Exception
        } finally {
            // Save props to config
            if (config.hasChanged()) config.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent event) {
        syncConfig();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.func_71410_x();

        if (!minecraft.func_147113_T()) {

            if (keyToggleLeft.func_151468_f()) {
                shouldLeftClick = !shouldLeftClick;
            }
            if (keyToggleRight.func_151468_f()) {
                shouldRightClick = !shouldRightClick;
            }

            if (shouldLeftClick) {
                MovingObjectPosition rayTrace = minecraft.field_71439_g.func_174822_a(5, clickDelay);
                if (rayTrace.field_72313_a != MovingObjectPosition.MovingObjectType.MISS)
                    minecraft.field_71442_b.func_180511_b(rayTrace.func_178782_a(), rayTrace.field_178784_b);
            }
            if (shouldRightClick) {
                MovingObjectPosition rayTrace = minecraft.field_71439_g.func_174822_a(5, clickDelay);
                if (rayTrace.field_72313_a != MovingObjectPosition.MovingObjectType.MISS)
                    minecraft.field_71442_b.func_178890_a(minecraft.field_71439_g, minecraft.field_71441_e,
                            minecraft.field_71439_g.func_70694_bm(), rayTrace.func_178782_a(), rayTrace.field_178784_b, rayTrace.field_72307_f);
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        Minecraft minecraft = Minecraft.func_71410_x();

        if (shouldLeftClick && shouldRightClick) {
            minecraft.field_71466_p.func_78276_b(EnumChatFormatting.BOLD + "Auto-Clicking RClick + LClick", 6, 6, 0xFF0000);
        } else if (shouldLeftClick) {
            minecraft.field_71466_p.func_78276_b(EnumChatFormatting.BOLD + "Auto-Clicking LClick", 6, 6, 0x00FF00);
        } else if (shouldRightClick) {
            minecraft.field_71466_p.func_78276_b(EnumChatFormatting.BOLD + "Auto-Clicking RClick", 6, 6, 0xFF00FF);
        }
    }

}