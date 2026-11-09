package dev.mind.keys;

/**
 * Dopasowywanie klawiszy modyfikujących (Shift, Ctrl, Alt) między kodami GLFW i scancodami SDL.
 *
 * <p>Zastępuje magiczne liczby (344, 229, 340, 225) znane z Render4U 1.3.2 nazwanymi stałymi i
 * obejmuje także Ctrl i Alt. Klasa nie zależy od Minecrafta, więc da się ją testować samodzielnie.
 *
 * <p>UWAGA: stałe GLFW i SDL są standardowe, ale to, który zestaw wartości gra podaje w polach
 * {@code key} i {@code keycode} w wersji 26.3, trzeba sprawdzić w jarze. Poradnik migracji
 * NeoForged mówi, że {@code key} zawiera scancody SDL, a {@code keycode} kody klawiszy SDL.
 */
public final class ModifierKeys {
    // Kody klawiszy GLFW
    public static final int GLFW_LEFT_SHIFT = 340;
    public static final int GLFW_LEFT_CONTROL = 341;
    public static final int GLFW_LEFT_ALT = 342;
    public static final int GLFW_RIGHT_SHIFT = 344;
    public static final int GLFW_RIGHT_CONTROL = 345;
    public static final int GLFW_RIGHT_ALT = 346;

    // Scancody SDL
    public static final int SDL_LEFT_CONTROL = 224;
    public static final int SDL_LEFT_SHIFT = 225;
    public static final int SDL_LEFT_ALT = 226;
    public static final int SDL_RIGHT_CONTROL = 228;
    public static final int SDL_RIGHT_SHIFT = 229;
    public static final int SDL_RIGHT_ALT = 230;

    /** Pary {GLFW, SDL} opisujące ten sam fizyczny klawisz. */
    private static final int[][] EQUIVALENT = {
            {GLFW_LEFT_SHIFT, SDL_LEFT_SHIFT},
            {GLFW_RIGHT_SHIFT, SDL_RIGHT_SHIFT},
            {GLFW_LEFT_CONTROL, SDL_LEFT_CONTROL},
            {GLFW_RIGHT_CONTROL, SDL_RIGHT_CONTROL},
            {GLFW_LEFT_ALT, SDL_LEFT_ALT},
            {GLFW_RIGHT_ALT, SDL_RIGHT_ALT},
    };

    private ModifierKeys() {
    }

    /** Czy dwa kody opisują ten sam klawisz (identyczne albo para GLFW/SDL tego samego modyfikatora). */
    public static boolean sameKey(int a, int b) {
        if (a == b) {
            return true;
        }
        for (int[] pair : EQUIVALENT) {
            if ((a == pair[0] && b == pair[1]) || (a == pair[1] && b == pair[0])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Czy zdarzenie klawiatury pasuje do przypisanego klawisza.
     *
     * @param target   przypisany klawisz (wartość &lt;= 0 oznacza brak przypisania)
     * @param key      pierwszy kod ze zdarzenia
     * @param scancode drugi kod ze zdarzenia
     */
    public static boolean matches(int target, int key, int scancode) {
        if (target <= 0) {
            return false;
        }
        return sameKey(target, key) || sameKey(target, scancode);
    }
}
