package com.twistezo.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private final String NINJA_DEAD_FILE = "ninja-dead.atlas";
    private final float MOVEMENT_DURATION = 1/10f;
    private final float FRAME_DURATION = 1/10f;
    private final int MOVEMENT_STEP = 10;
    private final float PLAYER_SCALE = 1/3f;
    private final int BOUNDS_SHIFT_IDLE = 10;
    private final int BOUNDS_SHIFT_ATTACK = 40;
    private SpriteBatch spriteBatch;
    private TextureAtlas textureAtlasIdle;
    private TextureAtlas textureAtlasMove;
    private TextureAtlas textureAtlasAttack;
    private TextureAtlas textureAtlasDead;
    private Animation<TextureRegion> animationIdle;
    private Animation<TextureRegion> animationMove;
    private Animation<TextureRegion> animationAttack;
    private Animation<TextureRegion> animationDead;
    private TextureRegion textureRegion;
    private Rectangle bounds;
    private ShapeRenderer shapeRenderer;
    private float stateTime = 0;
    private float stateAttackTime = 0;
    private float stateDeadTime = 0;
    private boolean isPlayerFlippedToLeft = false;
    private boolean isInEnemyBounds = false;
    private boolean moveToRight = false;
    private boolean isAttacking = false;
    private boolean isDebugMode = false;
    private boolean isDead = false;

    public Player() {
        spriteBatch = new SpriteBatch();
        generateAnimations();
        shapeRenderer = new ShapeRenderer();
    }

    private void generateAnimations() {
        textureAtlasIdle = new TextureAtlas(Gdx.files.internal(NINJA_IDLE_FILE));
        animationIdle = new Animation<>(FRAME_DURATION, textureAtlasIdle.getRegions());
        textureAtlasMove = new TextureAtlas(Gdx.files.internal(NINJA_MOVE_FILE));
        animationMove = new Animation<>(FRAME_DURATION, textureAtlasMove.getRegions());
        textureAtlasAttack = new TextureAtlas(Gdx.files.internal(NINJA_ATTACK_FILE));
        animationAttack = new Animation<>(FRAME_DURATION, textureAtlasAttack.getRegions());
        textureAtlasDead = new TextureAtlas(Gdx.files.internal(NINJA_DEAD_FILE));
        animationDead = new Animation<>(FRAME_DURATION, textureAtlasDead.getRegions());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        if(isDead) {
            stateDeadTime += delta;
            if(!animationDead.isAnimationFinished(stateDeadTime)) {
                textureRegion = animationDead.getKeyFrame(stateDeadTime);
                setPlayerWidthAndHeight();
            }
        }

        if(!isAttacking() && !isDead) {
            /* Idle animation */
            textureRegion = animationIdle.getKeyFrame(stateTime, true);
            /* Set correct width and height for different sizes of textureRegions */
            setPlayerWidthAndHeight();
            /* Keyboard events */
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                textureRegion = animationMove.getKeyFrame(stateTime, true);
                isPlayerFlippedToLeft = true;
                setPlayerWidthAndHeight();
                this.addAction(Actions.moveTo(getX() - MOVEMENT_STEP, getY(), MOVEMENT_DURATION));
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                textureRegion = animationMove.getKeyFrame(stateTime, true);
                isPlayerFlippedToLeft = false;
                setPlayerWidthAndHeight();
                moveToRight = true;
                this.addAction(Actions.moveTo(getX() + MOVEMENT_STEP, getY(), MOVEMENT_DURATION));
            }
        }

        setAttacking(false);
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            setAttacking(true);
            if (moveToRight) {
                textureRegion = animationAttack.getKeyFrame(stateTime, true);
            } else if (!moveToRight) {
                isPlayerFlippedToLeft = true;
                textureRegion = animationAttack.getKeyFrame(stateTime, true);
            }
            setPlayerWidthAndHeight();
        }

        /* Mouse/Touch events */
        mouseEvents();
        /* Hold player in screen bounds */
        holdPlayerInScreenBounds();
         /* Updater getter for player X position */
        getPlayerCurrentX();
    }

    private void mouseEvents() {
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            this.addAction(Actions.moveTo(touchPos.x - this.getWidth()/2, getY(), MOVEMENT_DURATION));
        }
    }

    private void holdPlayerInScreenBounds() {
        if (this.getX() < 0) {
            this.setPosition(0, 50);
        }
        if (this.getX() > NinjaGame.SCREEN_WIDTH - this.getWidth()) {
            this.setPosition(NinjaGame.SCREEN_WIDTH - this.getWidth(), getY());
        }
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
        if(isDebugMode) {
            batch.end();
            drawDebugBounds();
            batch.begin();
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
        if(isAttacking()) {
            bounds = new Rectangle((int)getX() + BOUNDS_SHIFT_ATTACK, (int)getY(), (int)getWidth() - 2 *BOUNDS_SHIFT_ATTACK, (int)getHeight());
        } else {
            bounds = new Rectangle((int)getX() + BOUNDS_SHIFT_IDLE, (int)getY(), (int)getWidth() - 2 * BOUNDS_SHIFT_IDLE, (int)getHeight());
        }
        return bounds;
    }

    private void drawDebugBounds() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(1, 0, 0, 0.5f)); // last argument is alpha channel
        if(isAttacking()) {
            shapeRenderer.rect((int)getX() + BOUNDS_SHIFT_ATTACK, (int)getY(), (int)getWidth() - 2 * BOUNDS_SHIFT_ATTACK, (int)getHeight());
        } else {
            shapeRenderer.rect((int)getX() + BOUNDS_SHIFT_IDLE, (int)getY(), (int)getWidth() - 2 * BOUNDS_SHIFT_IDLE, (int)getHeight());
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
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

    public void setDebugMode(boolean debugMode) {
        this.isDebugMode = debugMode;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }
}

