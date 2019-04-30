package net.mostlyoriginal.game.screen;

import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.link.EntityLinkManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.manager.FontManager;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.system.ItemRepository;
import net.mostlyoriginal.game.system.RecipeRepository;
import net.mostlyoriginal.game.system.*;
import net.mostlyoriginal.game.system.control.*;
import net.mostlyoriginal.game.system.logic.TransitionSystem;
import net.mostlyoriginal.game.system.map.*;
import net.mostlyoriginal.game.system.mechanics.*;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;
import net.mostlyoriginal.game.system.view.MyClearScreenSystem;
import net.mostlyoriginal.plugin.OperationsPlugin;
import net.mostlyoriginal.plugin.ProfilerPlugin;

/**
 * Example main game screen.
 *
 * @author Daan van Yperen
 */
public class GameScreen extends TransitionableWorldScreen {

    public static final String BACKGROUND_COLOR_HEX = "969291";

    Class nextScreen;

    @Override
    protected World createWorld() {
        RenderBatchingSystem renderBatchingSystem;
        return new World(new WorldConfigurationBuilder()
                .dependsOn(EntityLinkManager.class, ProfilerPlugin.class, OperationsPlugin.class)
                .with(
                        new SuperMapper(),
                        //new EmotionService(),
                        new FontManager(),
                        new TagManager(),
                        new ItemRepository(),
                        new RecipeRepository(),
                        new PickupManager()
                        //new TutorialService()
                )
                .with(
                        // Replace with your own systems!
                        new CameraSystem(2),
                        new MyClearScreenSystem(Color.valueOf(BACKGROUND_COLOR_HEX)),
                        new GameScreenAssetSystem(),

                        new MapSpawnerSystem(),
                        new MapSystem(),
                        new DialogSystem(),

                        new ShopperSpawnSystem(),
                        new NightShopperSpawnSystem(),
                        new TutorialSystem(),

                        new PlayerControlSystem(),
                        new MapCollisionSystem(),
                        new DeploySystem(),
                        new PassiveSpawnSystem(),

                        new ShopperControlSystem(),

                        new MyPhysicsSystem(),

                        new TradeSystem(), // must be before pickup system!
                        new PickupSystem(),

                        new DesireSystem(),
                        new ShadowSystem(),
                        new GridPosSystem(),
                        new GridPosFloatSystem(),

                        new PlayerAgeSystem(),

                        new PayingSystem(),

                        new HopperDetectionSystem(),
                        new MachineHopperDetectionSystem(),
                        new MachineRecipeSystem(),
                        new RecipeIngredientHintSystem(),
                        new SlotHighlightingSystem(),
                        new NightSystem(),

                        new ScoreSystem(),

                        new ParticleSystem(),
                        new MapRenderSystem(),
                        renderBatchingSystem = new RenderBatchingSystem(),
                        new MyAnimRenderSystem(renderBatchingSystem),
                        new MyLabelRenderSystem(renderBatchingSystem),
                        new MapRenderInFrontSystem(),

                        new TransitionSystem(GdxArtemisGame.getInstance(), this)
                ).build());
    }

}
