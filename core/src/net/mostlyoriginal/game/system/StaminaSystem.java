package net.mostlyoriginal.game.system;

import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.All;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.operation.JamOperationFactory;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.game.component.StaminaIndicator;

import static net.mostlyoriginal.api.utils.Duration.seconds;

/**
 * @author Daan van Yperen
 */
@All(StaminaIndicator.class)
public class StaminaSystem extends FluidIteratingSystem {


    private float stamina = 0.5f;
    private float oldStamina = 0;
    private float recentlyChanged=0;

    public float getStamina() {
        return stamina;
    }

    @Override
    protected void process(E e) {
        E player = E.withTag("player");
        Pos pos = player.getPos();
        e.pos(pos.xy.x + player.getBounds().cx() - 4, pos.xy.y + player.getBounds().maxy + 10 + (player.hasHolding() ? 10:0));
        if ( stamina >= 0.95f) e.anim("indicator_power6");
        else if ( stamina >= 0.80f) e.anim("indicator_power5");
        else if ( stamina >= 0.60f) e.anim("indicator_power4");
        else if ( stamina >= 0.40f) e.anim("indicator_power3");
        else if ( stamina >= 0.10f) e.anim("indicator_power2");
        else e.anim("indicator_power1");

        // no indicator while blinking.
        if ( player.hasBlinking() )
             e.anim(null);

        if ( stamina < 0.10f ) {
            stamina=0.5f;
            E.E().playSound("drowned");
            player.blinking(3f).script(OperationFactory
                            .sequence(
                                    OperationFactory.delay(seconds(1.5f)),
                    JamOperationFactory.moveTo(20*16,13*16)));
            if (player.hasHolding() ) {
                player.actionDropTarget(player.holdingId());
            }
            recentlyChanged=0;
        }

        if ( (Math.abs(oldStamina - stamina) > 0.15f) || (oldStamina != stamina && stamina < 0.60f) ) {
            oldStamina = stamina;
            recentlyChanged=0;
        }
        recentlyChanged+=world.delta*2f;
        e.tint(1f,1f,1f,recentlyChanged < 1f ? 1f : 1f-Math.min(recentlyChanged-1f,1f));
    }

    public void staminaIncrease(float v) {
        stamina = MathUtils.clamp(stamina+v, 0f,1f);
    }

    public void drainStamina(float drainSpeed) {
        stamina = MathUtils.clamp(stamina- world.delta* drainSpeed, 0f,1f);
    }

    public void slowRegenStamina(float maxRegen, float regenSpeed) {
        if ( stamina <= maxRegen ) {
            stamina = MathUtils.clamp(stamina + world.delta * regenSpeed, 0f, maxRegen);
        }
    }

    public boolean isLowStamina() {
        return stamina < 25f;
    }
}
