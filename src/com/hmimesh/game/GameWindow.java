package com.hmimesh.game;

import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;

/**
 * The Swing GUI window for Dungo.
 *
 * <p>Layout:
 * <ul>
 *   <li><b>Top (scene area)</b> — large monospaced text panel showing the current scene.</li>
 *   <li><b>Bottom (log area)</b> — scrolling combat/event log.</li>
 *   <li><b>Input strip</b> — text field + Send button + Options button.</li>
 * </ul>
 *
 * <p>The window is state-driven: {@link GameEngine} sets the active
 * {@link GameState} and registers callbacks for each state transition.
 * User input is routed to the correct callback by {@link #handleInput(String)}.
 *
 * <p>Thread safety: {@link #print(String)} and {@link #setScene(String)} are
 * safe to call from a SwingWorker background thread — they dispatch to the
 * Event Dispatch Thread via {@link SwingUtilities#invokeLater}.
 *
 * @author Ben Farjun
 */
class GameWindow {

    // ─── UI components ───────────────────────────────────────────────────────

    private final JFrame     _frame;
    private final JTextArea  _logArea;
    private final JTextArea  _sceneArea;
    private final JSplitPane _splitPane;
    private final JTextField _inputField;
    private final JScrollPane _logScroll;
    private final JScrollPane _sceneScroll;
    private final JPanel     _bottomPanel;
    private final JButton    _sendButton;
    private final JButton    _optionButton;

    // ─── State ───────────────────────────────────────────────────────────────

    private boolean   waitingForName = false;
    private GameState _state;
    private String    _scene;
    private Player    _player;

    // ─── Callbacks (set by GameEngine) ───────────────────────────────────────

    private Consumer<String> _onNameEntered;
    private Consumer<String> _onReady;
    private Consumer<String> _onDoorChoose;
    private Consumer<String> _onAfterBattles;
    private Consumer<String> _onGameOver;
    private Consumer<String> _onLevelUp;
    private Consumer<String> _onShop;

    // ─── Constants ───────────────────────────────────────────────────────────

    private static final String DEFAULT_NAME  = "hero";
    private static final String DEFAULT_SCENE =
        "[_________ \\    ||        ||   ||       ||   _=_=_=_=       =========\n"
      + "||        ||    ||        ||   ||\\\\     ||   ||           |/         \\|\n"
      + "||        ||    ||        ||   || \\\\    ||   ||   |___    ||         ||\n"
      + "||        ||    ||        ||   ||  \\\\   ||   ||   |===\\\\  ||         ||  \n"
      + "||        ||    ||        ||   ||   \\\\  //   ||       ||  |\\         /|\n"
      + "||_______/_/    |_\\_______||   ||    \\\\//    ||\\______||   \\\\_______//\n"
      + "\n"
      + " GOOD LUCK       HAVE FUN       A GAME MADE BY BEN FARJUN   @HMIMESH\n";

    // ─── Constructor ─────────────────────────────────────────────────────────

    /**
     * Builds and displays the game window.
     *
     * @param windowName    title shown in the OS window bar
     * @param visible       whether to make the window visible immediately
     * @param onNameEntered callback fired when the player submits their name
     */
    public GameWindow(String windowName, boolean visible, Consumer<String> onNameEntered) {
        _frame        = new JFrame(windowName);
        _logArea      = new JTextArea();
        _sceneArea    = new JTextArea();
        _inputField   = new JTextField();
        _sendButton   = new JButton("Send");
        _optionButton = new JButton("Options");
        _logScroll    = new JScrollPane(_logArea);
        _sceneScroll  = new JScrollPane(_sceneArea);
        _bottomPanel  = new JPanel(new BorderLayout());

        // Auto-scroll the log to the bottom whenever text is appended
        DefaultCaret caret = (DefaultCaret) _logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // Scene area — large, read-only, monospaced
        _sceneArea.setEditable(false);
        _sceneArea.setFont(new Font("Monospaced", Font.PLAIN, 21));

        // Log area — scrollable, read-only, monospaced, word-wrapped
        _logArea.setEditable(false);
        _logArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        _logArea.setLineWrap(true);
        _logArea.setWrapStyleWord(true);

        // Input field
        _inputField.setFont(new Font("Monospaced", Font.PLAIN, 16));

        // Split pane: scene on top, log on bottom
        _splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _sceneScroll, _logScroll);
        _splitPane.setDividerLocation(850);

        // Bottom strip: [Options] [input field] [Send]
        _bottomPanel.add(_inputField,   BorderLayout.CENTER);
        _bottomPanel.add(_sendButton,   BorderLayout.EAST);
        _bottomPanel.add(_optionButton, BorderLayout.WEST);

        _frame.setLayout(new BorderLayout());
        _frame.add(_bottomPanel, BorderLayout.SOUTH);
        _frame.add(_splitPane,   BorderLayout.CENTER);

        // Wire up input events
        _sendButton.addActionListener(e  -> submitInput());
        _inputField.addActionListener(e  -> submitInput());
        _optionButton.addActionListener(e -> optionMenu());

        // Default colour scheme (green-on-black scene, white-on-black log)
        applyColorScheme(
            java.awt.Color.BLACK, java.awt.Color.GREEN,   // scene
            java.awt.Color.BLACK, java.awt.Color.WHITE,   // log
            java.awt.Color.DARK_GRAY, java.awt.Color.WHITE // input
        );

        _frame.setSize(1980, 1200);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _frame.setVisible(visible);

        this._onNameEntered = onNameEntered;
    }

    // ─── Public API ──────────────────────────────────────────────────────────

    /**
     * Appends a line of text to the log area.
     * Safe to call from any thread (dispatches to the EDT via invokeLater).
     *
     * @param text the message to append
     */
    public void print(String text) {
        SwingUtilities.invokeLater(() -> _logArea.append(text + "\n"));
    }

    /**
     * Replaces the scene area content.
     * Safe to call from any thread.
     *
     * @param scene the new scene text
     */
    public void setScene(String scene) {
        this._scene = scene;
        SwingUtilities.invokeLater(() -> _sceneArea.setText(scene));
    }

    // ─── Getters ─────────────────────────────────────────────────────────────

    public GameState getState()        { return _state;        }
    public String    getDefaultScene() { return DEFAULT_SCENE; }

    // ─── Setters ─────────────────────────────────────────────────────────────

    public void setState(GameState state)              { this._state  = state;  }
    public void setIsWaitingForPlayer(boolean waiting) { this.waitingForName = waiting; }
    public void setPlayer(Player player)               { this._player = player; }
    public void setOnReady(Consumer<String> cb)        { this._onReady        = cb; }
    public void setOnDoorChoose(Consumer<String> cb)   { this._onDoorChoose   = cb; }
    public void setOnAfterBattles(Consumer<String> cb) { this._onAfterBattles = cb; }
    public void setOnGameOver(Consumer<String> cb)     { this._onGameOver     = cb; }
    public void setOnLevelUp(Consumer<String> cb)      { this._onLevelUp      = cb; }
    public void setOnShop(Consumer<String> cb)         { this._onShop         = cb; }

    public void setName(String name) {
        SwingUtilities.invokeLater(() -> _frame.setTitle(name));
    }

    // ─── Input handling ──────────────────────────────────────────────────────

    /**
     * Called when the player clicks Send or presses Enter.
     * Clears the field and routes the text to {@link #handleInput(String)}.
     */
    private void submitInput() {
        String text = _inputField.getText().trim();
        if (text.isEmpty()) return;
        print("> " + text);
        _inputField.setText("");
        handleInput(text);
    }

    /**
     * Routes the player's input to the correct handler based on the current
     * {@link GameState}.  Falls back to {@link Commands} for general commands.
     *
     * @param text raw player input
     */
    private void handleInput(String text) {
        if (_state == GameState.ENTER_NAME && _onNameEntered != null) {
            handleEnterName(text);
            return;
        }
        if (_state == GameState.READY && _onReady != null) {
            _onReady.accept(text);
            return;
        }
        if (_state == GameState.DOOR_CHOOSE && _onDoorChoose != null) {
            _onDoorChoose.accept(text);
            return;
        }
        if (_state == GameState.GAME_OVER && _onGameOver != null) {
            _onGameOver.accept(text);
            return;
        }
        if (_state == GameState.AFTER_BATTLES && _onAfterBattles != null) {
            _onAfterBattles.accept(text);
            return;
        }
        if (_state == GameState.FEAT_CHOOSE && _onLevelUp != null) {
            _onLevelUp.accept(text);
            return;
        }
        // General commands (help, stats, inventory, etc.)
        Commands cmd = Commands.fromInput(text);
        if (cmd != null && _player != null) {
            cmd.enable(this, _player);
            return;
        }
        print("Unknown command — type 'help' for a list.");
    }

    /** Processes the player's name entry. */
    private void handleEnterName(String name) {
        if (!waitingForName) return;
        if (name.isEmpty()) name = DEFAULT_NAME;
        waitingForName = false;
        _onNameEntered.accept(name);
    }

    // ─── Options menu ────────────────────────────────────────────────────────

    /** Shows a pop-up menu with Help and Colours sub-menus. */
    private void optionMenu() {
        JPopupMenu menu     = new JPopupMenu();
        JMenuItem helpItem  = new JMenuItem("Help");
        JMenuItem colorsItem= new JMenuItem("Colors");

        helpItem.addActionListener(ev -> Commands.HELP.enable(this, _player));

        colorsItem.addActionListener(ev -> {
            String[] options = {
                "Default", "Green on Black", "White on Black",
                "Dark Gray on Black", "Black on White", "Blue on Black", "Cyan on Black"
            };
            String choice = (String) JOptionPane.showInputDialog(
                _frame, "Choose a color scheme:", "Color Options",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]
            );
            if (choice == null) return;
            switch (choice) {
                case "Default":
                    applyColorScheme(Color.BLACK, Color.GREEN, Color.BLACK, Color.WHITE, Color.DARK_GRAY, Color.WHITE); break;
                case "Green on Black":
                    applyColorScheme(Color.BLACK, Color.GREEN, Color.BLACK, Color.GREEN, Color.BLACK, Color.GREEN); break;
                case "White on Black":
                    applyColorScheme(Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE); break;
                case "Dark Gray on Black":
                    applyColorScheme(Color.BLACK, Color.DARK_GRAY, Color.BLACK, Color.DARK_GRAY, Color.BLACK, Color.DARK_GRAY); break;
                case "Black on White":
                    applyColorScheme(Color.WHITE, Color.BLACK, Color.WHITE, Color.BLACK, Color.WHITE, Color.BLACK); break;
                case "Blue on Black":
                    applyColorScheme(Color.BLACK, Color.BLUE, Color.BLACK, Color.BLUE, Color.BLACK, Color.BLUE); break;
                case "Cyan on Black":
                    applyColorScheme(Color.BLACK, Color.CYAN, Color.BLACK, Color.CYAN, Color.BLACK, Color.CYAN); break;
            }
        });

        menu.add(helpItem);
        menu.add(colorsItem);
        menu.show(_optionButton, 0, _optionButton.getHeight());
    }

    // ─── Colour helper ───────────────────────────────────────────────────────

    /**
     * Applies a colour scheme to the three text areas.
     *
     * @param sceneBg  scene background
     * @param sceneFg  scene foreground
     * @param logBg    log background
     * @param logFg    log foreground
     * @param inputBg  input field background
     * @param inputFg  input field foreground
     */
    private void applyColorScheme(Color sceneBg, Color sceneFg,
                                   Color logBg,   Color logFg,
                                   Color inputBg, Color inputFg) {
        _sceneArea.setBackground(sceneBg);  _sceneArea.setForeground(sceneFg);
        _logArea.setBackground(logBg);      _logArea.setForeground(logFg);
        _inputField.setBackground(inputBg); _inputField.setForeground(inputFg);
    }
}
