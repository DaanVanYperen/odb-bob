package net.mostlyoriginal.game.system.mechanics;

import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.All;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.component.PassiveSpawner;
import net.mostlyoriginal.game.manager.ItemRepository;
import net.mostlyoriginal.game.system.ParticleSystem;
import net.mostlyoriginal.game.system.control.PickupManager;
import net.mostlyoriginal.game.system.map.MapSpawnerSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
@All(PassiveSpawner.class)
public class PassiveSpawnSystem extends FluidIteratingSystem {
    private static final float UPDATE_EVERY_SECONDS = 10f;
    private MapSpawnerSystem mapSpawnerSystem;
    private GameScreenAssetSystem gameScreenAssetSystem;

    private float cooldown = 1f;
    private ParticleSystem particleSystem;
    private ItemRepository itemRepository;
    private PickupManager pickupManager;

    @Override
    protected boolean checkProcessing() {
        cooldown -= world.delta;
        if (cooldown <= 0) {
            cooldown = UPDATE_EVERY_SECONDS;
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void process(E e) {
        PassiveSpawner spawner = e.getPassiveSpawner();
        if (spawner.items.length > 0 && MathUtils.random(1, 1000) < 20) {
            spawn(e, spawner.items[0]);
            return;
        }
        // second item more rare.
        if (spawner.items.length > 1 && MathUtils.random(1, 1000) < 10) {
            spawn(e, spawner.items[1]);
            return;
        }
        // third item rarest.
        if (spawner.items.length > 2 && MathUtils.random(1, 1000) < 5) {
            spawn(e, spawner.items[2]);
        }
    }

    private void spawn(E e, String item) {

        if ( pickupManager.getOverlapping(e) == null ) {
            mapSpawnerSystem
                    .spawnItem(e.gridPosX(), e.gridPosY(), item);
            gameScreenAssetSystem.playSfx("sfx_putdown");
            particleSystem.poof(e.gridPosX() * GameRules.CELL_SIZE + 16, e.gridPosY() * GameRules.CELL_SIZE + 16, 40, 40, ParticleSystem.COLOR_WHITE_TRANSPARENT);
        }
    }
}
