package com.packtpub.libgdx.light.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.packtpub.libgdx.light.game.Assets;
import com.packtpub.libgdx.light.util.Constants;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.packtpub.libgdx.light.util.AudioManager;
import com.packtpub.libgdx.light.util.CharacterSkin;
import com.packtpub.libgdx.light.util.GamePreferences;
//import com.packtpub.libgdx.light.util.AudioManager;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/**
 * Menu Screen with loooooots of options.
 * 
 * @author Jacob Kole
 */
public class MenuScreen extends AbstractGameScreen {
	private final String TAG = MenuScreen.class.getName();
	private Stage stage;
	private Skin skinLight;
	// menu
	private Image imgBackground;
	private Image imgLogo;
	private Image imgInfo;
	private Image imgShards;
	private Image imgOrb;
	private Button btnMenuPlay;
	private Button btnMenuOptions;
	// options
	private Window winOptions;
	private TextButton btnWinOptSave;
	private TextButton btnWinOptCancel;
	private CheckBox chkSound;
	private Slider sldSound;
	private Slider sldMusic;
	private CheckBox chkMusic;
	private SelectBox<CharacterSkin> selCharSkin;
	private Image imgCharSkin;
	private CheckBox chkShowFpsCounter;
	// debug
	private final float DEBUG_REBUILD_INTERVAL = 5.0f;
	private boolean debugEnabled = false;
	private float debugRebuildStage;
	
	int[] scores;
	String[] names;

	private Skin skinLibgdx;

	/**
	 * Menu screen constructor
	 */
	public MenuScreen(Game game) {
		super(game);
		prepScores();
	}
	
	private void prepScores() {
		int i = 1;
		if (Assets.getHighScore().length > 10) {
			i = Assets.getHighScore().length - 10;
		}
		
		int[] sco = Assets.getHighScore();
		String[] nam = Assets.getScoreNames();
		
		scores = new int[sco.length - i];
		names = new String[sco.length - i];

		int j = 0;
		for (; i < sco.length; i++) {
			scores[j] = sco[i];
			names[j] = nam[i];
			//System.out.println("\t" + names[i] + " - " + scores[i]);
			j++;
		}
		
		// sorts the scores array
		for(int k = 0; k < scores.length - 1; k++) {
			int key = scores[k];
			String string = names[k];
			int l = k - 1;
			
			while(l >= 0 && scores[l] > key) {
				scores[l + 1] = scores[l];
				names[l + 1] = names[l];
				l--;
			}
			scores[l + 1] = key;
			names[l + 1] = string;
		}
	}

	/**
	 * load game preference settings
	 */
	private void loadSettings() {
		GamePreferences prefs = GamePreferences.instance;
		prefs.load();
		chkSound.setChecked(prefs.sound);
		sldSound.setValue(prefs.volSound);
		chkMusic.setChecked(prefs.music);
		sldMusic.setValue(prefs.volMusic);
		selCharSkin.setSelectedIndex(prefs.charSkin);
		onCharSkinSelected(prefs.charSkin);
		chkShowFpsCounter.setChecked(prefs.showFpsCounter);
	}

	/**
	 * Save settings for preferences
	 */
	private void saveSettings() {
		GamePreferences prefs = GamePreferences.instance;
		prefs.sound = chkSound.isChecked();
		prefs.volSound = sldSound.getValue();
		prefs.music = chkMusic.isChecked();
		prefs.volMusic = sldMusic.getValue();
		prefs.charSkin = selCharSkin.getSelectedIndex();
		prefs.showFpsCounter = chkShowFpsCounter.isChecked();
		prefs.save();
	}

	/**
	 * Set color of character skins to selected index
	 */
	private void onCharSkinSelected(int index) {
		CharacterSkin skin = CharacterSkin.values()[index];
		imgCharSkin.setColor(skin.getColor());
	}

	/**
	 * Save the settings
	 */
	private void onSaveClicked() {
		saveSettings();
		onCancelClicked();

		// starts music
		AudioManager.instance.onSettingsUpdated();
	}

	/**
	 * Set visibility values after saving
	 */
	private void onCancelClicked() {
		btnMenuPlay.setVisible(true);
		btnMenuOptions.setVisible(true);
		winOptions.setVisible(false);
		showMenuButtons(true);
		showOptionsWindow(false, true);

		// starts music
		AudioManager.instance.onSettingsUpdated();
	}

	/**
	 * Method to rebuild the stage/ select the skin from texture atlas
	 */
	private void rebuildStage() {
		skinLight = new Skin(Gdx.files.internal(Constants.SKIN_LIGHT_UI), new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
		skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));
		// build all layers
		Table layerBackground = buildBackgroundLayer();
		Table layerObjects = buildObjectsLayer();
		Table layerLogos = buildLogosLayer();
		Table layerControls = buildControlsLayer();
		Table layerOptionsWindow = buildOptionsWindowLayer();

		// assemble stage for menu screen
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
		stack.add(layerBackground);
		stack.add(layerObjects);
		stack.add(layerLogos);
		stack.add(layerControls);
		stage.addActor(layerOptionsWindow);
	}

	/**
	 * Render method for the game screens
	 */
	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (debugEnabled) {
			debugRebuildStage -= deltaTime;
			if (debugRebuildStage <= 0) {
				debugRebuildStage = DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}
		stage.act(deltaTime);
		stage.draw();
		stage.setDebugAll(debugEnabled); // THIS TURNS DEBUG ON AND OFF. Set to true to see debug lines
		
		BitmapFont fontScore = Assets.instance.fonts.defaultScores;
		//fontScore.setColor(1, 0.75f, 0.25f, 1);
		fontScore.setColor(1, 1, 1, 1);
		
		// draws the message
		// HAlignment.CENTER: means to draw the font horizontally centered
		// to the given position
		SpriteBatch batch = new SpriteBatch();
		int x = 100;
		int y = 375;
		batch.begin();
		fontScore.draw(batch, "High Scores!", x, y, 1,
				Align.center, false);
		
		fontScore.setColor(1, 1, 0.75f, 1);
		for(int i = scores.length - 1; i >= 0; i--) {
			fontScore.draw(batch, names[i] + " - " + scores[i], x - 80, y -= 25, 1,
					Align.left, false);
		}
		
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	/**
	 * Initialize the stage and sets it as libGdx's input processor
	 */
	@Override
	public void show() {
		AudioManager.instance.play(Assets.instance.music.song01);
		stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
		Gdx.input.setInputProcessor(stage);
		rebuildStage();
	}

	/**
	 * clear skins
	 */
	@Override
	public void hide() {
		AudioManager.instance.stopMusic();
		stage.dispose();
		skinLight.dispose();
	}

	@Override
	public void pause() {
	}

	/**
	 * Build the audio options table
	 */
	private Table buildOptWinAudioSettings() {
		Table tbl = new Table();
		// + Title: Audio
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE)).colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);
		// + Checkbox, "sound" label, volume slider
		chkSound = new CheckBox("", skinLibgdx);
		tbl.add(chkSound);
		tbl.add(new Label("Label", skinLibgdx));
		sldSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
		tbl.add(sldSound);
		tbl.row();
		// +Checkbox "Music" label, music vol slider
		chkMusic = new CheckBox("", skinLibgdx);
		tbl.add(new Label("Music", skinLibgdx));
		sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
		tbl.add(sldMusic);
		tbl.row();
		return tbl;

	}

	/**
	 * @return table of options
	 */
	private Table buildOptWinSkinSelection() {
		Table tbl = new Table();
		// + Title: "Character Skin"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Character Skin", skinLibgdx, "default-font", Color.ORANGE)).colspan(2);
		tbl.row();
		// + Drop down box filled with skin items
		selCharSkin = new SelectBox<CharacterSkin>(skinLibgdx);
		selCharSkin.setItems(CharacterSkin.values());
		selCharSkin.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCharSkinSelected(((SelectBox<CharacterSkin>) actor).getSelectedIndex());
			}
		});
		tbl.add(selCharSkin).width(120).padRight(20);
		// + Skin preview image
		imgCharSkin = new Image(Assets.instance.orb.orb);
		tbl.add(imgCharSkin).width(50).height(50);
		return tbl;
	}

	/**
	 * 
	 * @return table layer for background controls
	 */
	private Table buildBackgroundLayer() {
		Table layer = new Table();
		// + Background
		imgBackground = new Image(skinLight, "background");
		layer.add(imgBackground);
		return layer;
	}

	/**
	 * @return table of controls for building objects
	 */
	public Table buildObjectsLayer() {
		Table layer = new Table();
		// +Coins
		imgShards = new Image(skinLight, "shards");
		layer.addActor(imgShards);
		imgShards.setOrigin(imgShards.getWidth() / 2, imgShards.getHeight() / 2);
		imgShards.addAction(sequence(moveTo(135, -20), scaleTo(0, 0), fadeOut(0), delay(2.5f),
				parallel(moveBy(0, 100, 0.5f, Interpolation.swingOut), scaleTo(1.0f, 1.0f, 0.25f, Interpolation.linear),
						alpha(1.0f, 0.5f))));
		// +Bunny
		imgOrb = new Image(skinLight, "orb");
		layer.addActor(imgOrb);
		imgOrb.addAction(sequence(moveTo(655, 510), delay(4.0f), moveBy(-70, -100, 0.5f, Interpolation.fade),
				moveBy(-100, -50, 0.5f, Interpolation.fade), moveBy(-150, -300, 1.0f, Interpolation.elasticIn)));
		return layer;
	}

	/**
	 * @return table of controls for options window
	 */
	private Table buildOptionsWindowLayer() {
		winOptions = new Window("Options", skinLibgdx);
		// + Audio Settings: Sound/Music CheckBox and Volume Slider
		winOptions.add(buildOptWinAudioSettings()).row();
		// + Character Skin: Selection Box (White, Gray, Brown)
		winOptions.add(buildOptWinSkinSelection()).row();
		// + Debug: Show FPS Counter
		winOptions.add(buildOptWinDebug()).row();
		// + Separator and Buttons (Save, Cancel)
		winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);
		// Make options window slightly transparent
		winOptions.setColor(1, 1, 1, 0.8f);
		// Hide options window by default
		showOptionsWindow(false, false);
		if (debugEnabled)
			winOptions.debug();
		// Let TableLayout recalculate widget sizes and positions
		winOptions.pack();
		// Move options window to bottom right corner
		winOptions.setPosition(Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50, 50);
		return winOptions;
	}

	/**
	 * Build the table controls for option window
	 * 
	 * @return table of controls
	 */
	private Table buildOptWinDebug() {
		Table tbl = new Table();
		// + Title: "Debug"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Debug", skinLibgdx, "default-font", Color.RED)).colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);
		// + Checkbox, "Show FPS Counter" label
		chkShowFpsCounter = new CheckBox("", skinLibgdx);
		tbl.add(new Label("Show FPS Counter", skinLibgdx));
		tbl.add(chkShowFpsCounter);
		tbl.row();
		return tbl;
	}

	/**
	 * Build table of option window buttons,
	 * 
	 * @return table
	 */
	private Table buildOptWinButtons() {
		Table tbl = new Table();
		// + Separator
		Label lbl = null;
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.75f, 0.75f, 0.75f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
		tbl.row();
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.5f, 0.5f, 0.5f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
		tbl.row();
		// + Save Button with event handler
		btnWinOptSave = new TextButton("Save", skinLibgdx);
		tbl.add(btnWinOptSave).padRight(30);
		btnWinOptSave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onSaveClicked();
			}
		});
		// + Cancel Button with event handler
		btnWinOptCancel = new TextButton("Cancel", skinLibgdx);
		tbl.add(btnWinOptCancel);
		btnWinOptCancel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCancelClicked();
			}
		});
		return tbl;
	}

	/**
	 * Builds the controls behind the buttons in the game's menu screen. The "play"
	 * button and "options" button are given functionality here using
	 * ChangeListener.
	 * 
	 * @return the table layer for menu controls
	 */
	private Table buildControlsLayer() {
		Table layer = new Table();
		layer.right().bottom();
		// + Play Button
		btnMenuPlay = new Button(skinLight, "play");
		layer.add(btnMenuPlay);
		btnMenuPlay.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onPlayClicked();
			}
		});
		layer.row();
		// + Options Button
		btnMenuOptions = new Button(skinLight, "options");
		layer.add(btnMenuOptions);
		btnMenuOptions.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onOptionsClicked();
			}
		});
		if (debugEnabled)
			layer.debug();
		return layer;
	}

	/**
	 * Anchored in top-left corner of the screen. This adds an image logo to that
	 * position.
	 * 
	 * @return the table layer for logos
	 */
	private Table buildLogosLayer() {
		Table layer = new Table();
		layer.left().top();
		// + Game Logo
		imgLogo = new Image(skinLight, "logo");
		layer.add(imgLogo);
		layer.row().expandY();
		// + Info Logos
		imgInfo = new Image(skinLight, "info");
		layer.add(imgInfo).bottom();
		if (debugEnabled)
			layer.debug();
		return layer;
	}

	/**
	 * Sets the game screen when the play button is clicked.
	 */
	private void onPlayClicked() {
		game.setScreen(new GameScreen(game));
	}

	private void onOptionsClicked() {
		loadSettings();
		btnMenuPlay.setVisible(false);
		btnMenuOptions.setVisible(false);
		winOptions.setVisible(true);
		showMenuButtons(false);
		showOptionsWindow(true, true);
	}

	/**
	 * Method to show the menu buttons
	 * 
	 * @param visible
	 */
	private void showMenuButtons(boolean visible) {
		float moveDuration = 1.0f;
		Interpolation moveEasing = Interpolation.swing;
		float delayOptionsButton = 0.25f;
		float moveX = 300 * (visible ? -1 : 1);
		float moveY = 0 * (visible ? -1 : 1);
		final Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;

		btnMenuPlay.addAction(moveBy(moveX, moveY, moveDuration, moveEasing));
		btnMenuOptions.addAction(sequence(delay(delayOptionsButton), moveBy(moveX, moveY, moveDuration, moveEasing)));
		SequenceAction seq = sequence();

		if (visible)
			seq.addAction(delay(delayOptionsButton + moveDuration));

		seq.addAction(run(new Runnable() {
			public void run() {
				btnMenuPlay.setTouchable(touchEnabled);
				btnMenuOptions.setTouchable(touchEnabled);
			}
		}));
		stage.addAction(seq);
	}

	/**
	 * Method for showing the options window
	 * 
	 * @param visible
	 * @param animated
	 */
	private void showOptionsWindow(boolean visible, boolean animated) {
		float alphaTo = visible ? 0.8f : 0.0f;
		float duration = animated ? 1.0f : 0.0f;
		Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
		winOptions.addAction(sequence(touchable(touchEnabled), alpha(alphaTo, duration)));
	}
}