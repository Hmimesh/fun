package com.hmimesh.game;

/**
 * Enum representing the possible states of the game at any given moment.
 *
 * <p>Replaces the raw {@code String} comparisons that were used previously
 * (e.g. {@code _state.equals("READY")}) with type-safe enum constants.
 * This makes the control flow easier to read and impossible to mistype.
 *
 * @author Ben Farjun
 */
public enum GameState {
    /** Waiting for the player to type their character name. */
    ENTER_NAME,

    /** Player has been created; waiting for "ready" confirmation. */
    READY,

    /** Player is choosing a door (easy or hard fight). */
    DOOR_CHOOSE,

    /** A combat round against a level-appropriate easy enemy. */
    EASY_FIGHT,

    /** A combat round against a level-appropriate hard enemy. */
    HARD_FIGHT,

    /** Fight is over; prompting the player to continue or quit. */
    AFTER_BATTLES,

    /** The player's HP reached 0; showing the death screen. */
    GAME_OVER,

    /** The player chose to rest  */
    REST,

    /** The player is in the shop between battles. */
    SHOP,

    /** Player must choose one of two feats offered after leveling up. */
    FEAT_CHOOSE
}
