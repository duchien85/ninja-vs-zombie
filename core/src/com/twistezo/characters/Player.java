package com.twistezo.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.twistezo.NinjaGame;

/**
 * @author twistezo (23.04.2017)
 */

public class Player extends Actor {
    private final String NINJA_IDLE_FILE = "ninja-idle.atlas";
    private final String NINJA_MOVE_FILE = "ninja-run-right.atlas";
    private final String NINJA_ATTACK_FILE = "ninja-attack.atlas";
    private final float MOVEMENT_DURATION = 1/10f;
    private final float FRAME_DURATION = 1/10f;
    private final int MOVEMENT_STEP = 10;
    private final float PLAYER_SCALE = 1/3f;
    private SpriteBatch spriteBatch;
    private TextureAtlas textureAtlasIdle;
    private TextureAtlas textureAtlasMove;
    private TextureAtlas textureAtlasAttack;
    private Animation<TextureRegion> animationIdle;
    private Animation<TextureRegion> animationMove;
    private Animation<TextureRegion> animationAttack;
    private TextureRegion textureRegion;
    private Rectangle bounds;
    private float stateTime = 0;
    private boolean isPlayerFlippedToLeft = false;
    private boolean isInEnemyBounds = false;
    private boolean moveToRight = false;
    private boolean isAttacking = false;

    public Player() {
        spriteBatch = new SpriteBatch();
        generateAnimations();
    }

    private void generateAnimations() {
        textureAtlasIdle = new TextureAtlas(Gdx.files.internal(NINJA_IDLE_FILE));
        animationIdle = new Animation<TextureRegion>(FRAME_DURATION, textureAtlasIdle.getRegions());
        textureAtlasMove = new TextureAtlas(Gdx.files.internal(NINJA_MOVE_FILE));
        animationMove = new Animation<TextureRegion>(FRAME_DURATION, textureAtlasMove.getRegions());
        textureAtlasAttack = new TextureAtlas(Gdx.files.internal(NINJA_ATTACK_FILE));
        animationAttack = new Animation<TextureRegion>(FRAME_DURATION, textureAtlasAttack.getRegions());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        /* Make animationIdle depends on frames */
        stateTime += delta;
        textureRegion = animationIdle.getKeyFrame(stateTime, true);

        /* Set correct width and height for different sizes of textureRegions */
        setPlayerWidthAndHeight();

        if(isInEnemyBounds) {
            //TODO bounds not working properly
        }

        /* Keyboard events */
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            textureRegion = animationMove.getKeyFrame(stateTime,true);
            isPlayerFlippedToLeft = true;
            setPlayerWidthAndHeight();
            this.addAction(Actions.moveTo(getX() - MOVEMENT_STEP, getY(), MOVEMENT_DURATION));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            textureRegion = animationMove.getKeyFrame(stateTime,true);
            isPlayerFlippedToLeft = false;
            setPlayerWidthAndHeight();
            moveToRight = true;
            this.addAction(Actions.moveTo(getX() + MOVEMENT_STEP, getY(), MOVEMENT_DURATION));
        }

        setAttacking(false);
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            setAttacking(true);
            if (moveToRight) {
                textureRegion = animationAttack.getKeyFrame(stateTime,true);
            } else if (!moveToRight) {
                isPlayerFlippedToLeft = true;
                textureRegion = animationAttack.getKeyFrame(stateTime,true);

            }
            animationAttack.isAnimationFinished(stateTime);
            setPlayerWidthAndHeight();
        }

        /* Mouse/Touch events */
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            this.addAction(Actions.moveTo(touchPos.x - this.getWidth()/2, getY(), MOVEMENT_DURATION));
        }

        /* Hold player in screen bounds */
        if (this.getX() < 0) {
            this.setPosition(0, 50);
        }
        if (this.getX() > NinjaGame.SCREEN_WIDTH - this.getWidth()) {
            this.setPosition(NinjaGame.SCREEN_WIDTH - this.getWidth(), getY());
        }

         /* Updater setter for player X position */
        getPlayerCurrentX();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (isPlayerFlippedToLeft) {
            batch.draw(textureRegion,
                    getX(),getY(),
                    getWidth()/2,getHeight()/2,
                    getWidth(), getHeight(),
                    getScaleX()*-1,getScaleY(),
                    getRotation());
        } else {
            batch.draw(textureRegion,
                    getX(),getY(),
                    getWidth()/2,getHeight()/2,
                    getWidth(), getHeight(),
                    getScaleX(),getScaleY(),
                    getRotation());
        }
    }

    private void setPlayerWidthAndHeight() {
        setWidth(textureRegion.getRegionWidth() * PLAYER_SCALE);
        setHeight(textureRegion.getRegionHeight() * PLAYER_SCALE);
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
    }

    public Rectangle getBounds() {
        bounds = new Rectangle((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
        return bounds;
    }

    public float getPlayerCurrentX() {
        return this.getX();
    }

    public boolean isInEnemyBounds() {
        return isInEnemyBounds;
    }

    public void setInEnemyBounds(boolean inEnemyBounds) {
        isInEnemyBounds = inEnemyBounds;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
    }
}

