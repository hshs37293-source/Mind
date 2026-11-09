package dev.mind.keys;

/** Prosty test bez frameworka: uruchom przez {@code java dev.mind.keys.ModifierKeysTest}. */
public final class ModifierKeysTest {
    private static int failures;

    public static void main(String[] args) {
        // Ten sam kod
        check("identyczny kod", ModifierKeys.matches(65, 65, 0), true);
        // Pary GLFW <-> SDL w obie strony
        check("RShift GLFW -> SDL", ModifierKeys.matches(344, 229, 0), true);
        check("RShift SDL -> GLFW", ModifierKeys.matches(229, 344, 0), true);
        check("LShift przez drugi kod", ModifierKeys.matches(340, 0, 225), true);
        check("RCtrl", ModifierKeys.matches(345, 228, 0), true);
        check("LCtrl", ModifierKeys.matches(224, 341, 0), true);
        check("RAlt", ModifierKeys.matches(346, 0, 230), true);
        check("LAlt", ModifierKeys.matches(226, 342, 0), true);
        // Brak pomyłek między stronami i modyfikatorami
        check("Prawy Shift != lewy Shift", ModifierKeys.matches(344, 225, 0), false);
        check("Shift != Ctrl", ModifierKeys.matches(344, 228, 0), false);
        check("Alt != Shift", ModifierKeys.matches(342, 225, 0), false);
        // Brak przypisania
        check("target 0", ModifierKeys.matches(0, 0, 0), false);
        check("target -1", ModifierKeys.matches(-1, -1, -1), false);
        // Zwykły klawisz nie pasuje do innego
        check("A != B", ModifierKeys.matches(65, 66, 0), false);

        if (failures > 0) {
            System.out.println("NIEPOWODZENIE: " + failures);
            System.exit(1);
        }
        System.out.println("Wszystkie testy przeszły.");
    }

    private static void check(String name, boolean actual, boolean expected) {
        if (actual != expected) {
            failures++;
            System.out.println("BŁĄD: " + name + " (oczekiwano " + expected + ")");
        }
    }
}
