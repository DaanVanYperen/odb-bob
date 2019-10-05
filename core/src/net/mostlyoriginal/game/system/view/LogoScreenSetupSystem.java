package net.mostlyoriginal.game.system.view;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.ui.Label;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.game.GameRules;
import net.mostlyoriginal.game.screen.GameScreen;
import net.mostlyoriginal.game.system.logic.TransitionSystem;
import net.mostlyoriginal.game.util.Anims;
import net.mostlyoriginal.api.util.Cooldown;

import static net.mostlyoriginal.api.operation.JamOperationFactory.moveBetween;
import static net.mostlyoriginal.api.operation.JamOperationFactory.scaleBetween;
import static net.mostlyoriginal.api.operation.OperationFactory.*;
import static net.mostlyoriginal.api.utils.Duration.seconds;

/**
 * @author Daan van Yperen
 */
@Wire
public class LogoScreenSetupSystem extends BaseSystem {

    public static final int FEATURE_BORDER_MARGIN = 1;
    public static final int NO_SCORE = -1;

    LogoScreenSetupSystem assetSystem;
    TagManager tagManager;

    private int iconIndex;
    private E message;
    private Color COLOR_PRESS_KEY_MESSAGE = new Color(0.3f, 0.3f, 0.3f, 1f);
    private E logo;
    private E message2;
    private E message3;

    private Cooldown logoCooldown = Cooldown.withInterval(seconds(0.5f)).autoReset(false);

    public static final Tint COLOR_LOGO_FADED = new Tint(1.0f, 1.0f, 1.0f, 0.0f);
    public static final Tint COLOR_LOGO_FULL = new Tint(1.0f, 1.0f, 1.0f, 1.0f);

    private boolean finished = false;


    @Override
    protected void processSystem() {
        if (logoCooldown.ready(world.delta) && !finished && (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY) || Gdx.input.isTouched())) {
            finished = true;
            fadeLogoAndMessage();
            world.getSystem(TransitionSystem.class).transition(GameScreen.class, 0.55f);
        }
    }

    private void fadeLogoAndMessage() {
        fadeMessage(message);
        if (message2 != null) fadeMessage(message2);
        if (message3 != null) fadeMessage(message3);
        logo
                .script(
                        OperationFactory.sequence(
                                delay(1f),
                                tween(new Tint(COLOR_LOGO_FULL), Tint.TRANSPARENT, 0.5f, Interpolation.fade)
                        )
                );
    }

    private void fadeMessage(E e) {
        e
                .script(
                        tween(new Tint(COLOR_PRESS_KEY_MESSAGE), Tint.TRANSPARENT, 2f, Interpolation.fade)
                );
    }

    @Override
    protected void initialize() {
        super.initialize();

        boolean hasScore = GameRules.lastScore != NO_SCORE;
        addLogo(0.8f);
        if (hasScore) addScore(GameRules.lastScore);
        addPressbutton(hasScore ? "Press any key to try again" : "Tap or press any key to start",
                hasScore ? 280 : 220);
        //scheduleTransitionToGameScreen();
        GameRules.lastScore = NO_SCORE;
        GameRules.level = 0;
    }

    private void addScore(int lastScore) {
        String title = scoreToTitle(lastScore);
        message2 = E.E()
                .pos((Gdx.graphics.getWidth() / 2) - 16 - title.length() * 15, Gdx.graphics.getHeight() - 64 - 16)
                .labelText(title)
                .tint(COLOR_PRESS_KEY_MESSAGE)
                .fontFontName("5x5")
                .renderLayer(10)
                .fontScale(6f);
        String scoreText = "You earned " + lastScore + " gold! ";
        message3 = E.E()
                .pos((Gdx.graphics.getWidth() / 2) - scoreText.length() * 15, 32 + 64 + 16)
                .labelText(scoreText)
                .tint(COLOR_PRESS_KEY_MESSAGE)
                .fontFontName("5x5")
                .renderLayer(10)
                .fontScale(6f);
    }

    private String scoreToTitle(int lastScore) {
        if (lastScore > 100) return "***I CLEAN TOILET***";
        if (lastScore > 90) return "**Godlike**";
        if (lastScore > 80) return "Keeper of Toilets";
        if (lastScore > 70) return "TP Wizard";
        if (lastScore > 60) return "Mop Guardian";
        if (lastScore > 50) return "Piddle Knight";
        if (lastScore > 40) return "Lavatory Supervisor";
        if (lastScore > 20) return "Urinal Warden";
        if (lastScore > 15) return "Toilet Lady";
        if (lastScore > 8) return "Cleaner";

        return "Slouch";
    }

    private void addPressbutton(String text, int width) {
        message = E.E()
                .pos((Gdx.graphics.getWidth() / 2), 16)
                .labelText(text)
                .labelAlign(Label.Align.RIGHT)
                .tint(COLOR_PRESS_KEY_MESSAGE)
                .fontFontName("5x5")
                .renderLayer(10)
                .fontScale(4f);
    }

    public void addLogo(float scale) {

        // approximate percentage of screen size with logo. Use rounded numbers to keep the logo crisp.

        float zoom = 1.4f;

        logo = Anims.createCenteredAt(
                LogoScreenAssetSystem.LOGO_WIDTH,
                LogoScreenAssetSystem.LOGO_HEIGHT,
                "logo",
                zoom)
                .tint(COLOR_LOGO_FADED)
                .script(
                        scaleBetween(zoom * 2, zoom, 2f, Interpolation.bounceOut),
                        tween(new Tint(COLOR_LOGO_FADED), new Tint(COLOR_LOGO_FULL), 2f, Interpolation.fade)
                );

    }

    public static final int DISPLAY_SECONDS = 2;


    private void scheduleTransitionToGameScreen() {
        world.getSystem(TransitionSystem.class).transition(GameScreen.class, DISPLAY_SECONDS);
    }

}
