/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package s07particles;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.effect.ParticleEmitter;
import com.almasb.fxgl.effect.ParticleEmitters;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

/**
 * Example of using particles.
 * When left mouse button is clicked, an explosion will spawn at cursor position.
 */
public class ParticlesSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ParticlesSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Spawn Explosion") {
            @Override
            protected void onActionBegin() {
                // 1. create entity
                Entity explosion = new Entity();
                explosion.addComponent(new PositionComponent(input.getMousePositionWorld()));

                // 2. create and configure emitter + control
                ParticleEmitter emitter = ParticleEmitters.newExplosionEmitter();
                ParticleControl control = new ParticleControl(emitter);

                // 3. add control to entity
                explosion.addControl(control);

                // we also want the entity to destroy itself after 3 seconds
                explosion.addControl(new ExpireCleanControl(Duration.seconds(3)));

                // 4. add entity to game world
                getGameWorld().addEntities(explosion);
            }
        }, MouseButton.PRIMARY);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
