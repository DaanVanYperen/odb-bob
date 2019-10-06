package net.mostlyoriginal.game.system.control;

import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import net.mostlyoriginal.api.event.common.Subscribe;
import net.mostlyoriginal.game.component.Player;
import net.mostlyoriginal.game.component.dialog.InDialog;
import net.mostlyoriginal.game.component.flags.Submerged;
import net.mostlyoriginal.game.events.EventItemPickup;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;

/**
 * @author Daan van Yperen
 */
@All(Submerged.class)
public class SubmergingSystem extends FluidIteratingSystem {

    GameScreenAssetSystem assetSystem;

    @Override
    protected void inserted(int entityId) {
        super.inserted(entityId);
        E e = E.E(entityId);

        String submergedAnim = e.animId() + "_water";
        if ( assetSystem.get(submergedAnim) != null ) {
            e.submergedSubmergedAnim(submergedAnim);
        }
        e.submergedOriginalAnim(e.animId());
    }

    @Subscribe
    public void onItemPickup(EventItemPickup event) {
        E item = E.E(event.id);
        if ( item.hasSubmerged() ) {
            item.anim(item.submergedOriginalAnim());
            item.removeSubmerged();
        }
    }

    @Override
    protected void process(E e) {
        if ( e.hasSubmerged() && e.getSubmerged().submergedAnim != null ) {
            e.anim(e.submergedSubmergedAnim());
        }
        if ( e.hasSubmerged() && !e.hasLocked()) {
            e.gridPosDeriveFromPos(true);
            e.posY(e.getPos().xy.y - world.delta*20f );
            if ( e.getPos().xy.y < -64 )
                e.deleteFromWorld();
        }
    }
}
