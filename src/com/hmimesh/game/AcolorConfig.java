package com.hmimesh.game;

/**
 * Configuration helper for ANSI terminal color support.
 *
 * <p>Colors are only applied when the JVM is attached to a real terminal
 * ({@code System.console() != null}). In IDEs or piped environments the
 * escape codes are suppressed so the output remains readable.
 *
 * @author Ben Farjun
 */
class AcolorConfig {
    /** {@code true} when ANSI color codes are safe to emit. */
    public static final boolean USE_COLOR = System.console() != null;
}
