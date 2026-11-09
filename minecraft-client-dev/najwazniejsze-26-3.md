# Najważniejsze rzeczy: klient Fabric na Minecrafta 26.3

Krótka ściągawka na jedną stronę. Szczegóły i znaczniki pewności są w notatkach, do których odsyłam. **[kod]** znaczy sprawdzone w kodzie Render4U 1.3.2, **[26.3]** potwierdzone w źródłach o 26.3.

## 1. Środowisko ([srodowisko-26-3.md](srodowisko-26-3.md))
- Java 25, Gradle 9.6.0, Loom 1.17.21 (wtyczka `net.fabricmc.fabric-loom`), Fabric Loader 0.20.1, Fabric API 0.165.0+26.3. Konfiguracja z działającego projektu, której nie budowałem **[kod]**.
- W zależności `minecraft` nie ma wiersza z mapowaniami. Nazwy pochodzą z niezaciemnionej gry **[26.3]**.

## 2. Rysowanie interfejsu ([render4u-kod.md](render4u-kod.md))
- Główna klasa to `GuiGraphicsExtractor` **[kod]**.
- Własne elementy implementują `GuiElementRenderState` i trafiają do `guiRenderState.addGuiElement(...)`, czyli do kolejki gry **[kod]**.
- Punkt zaczepienia: mixin na końcu `Hud#extractRenderState` **[kod]**.
- Kolory zawsze w formacie ARGB **[26.2]**.

## 3. Shadery ([render-26-3.md](render-26-3.md))
- `#version 330`, rozszerzenie `GL_ARB_separate_shader_objects`, jawne `layout(location = ...)` **[kod]**, `#include` zamiast `#moj_import` **[26.3]**.
- Własne shadery trzymaj we własnej przestrzeni nazw. Nie nadpisuj shaderów core, bo Mojang uznaje to za niewspierane **[26.3]**.
- Ostre krawędzie zaokrągleń i cieni daje SDF z wygładzaniem przez `fwidth` **[kod]**.

## 4. Blur
- Zgłaszaj obszary z góry, przetwarzaj raz na klatkę na początku `GuiRenderer#draw` i przycinaj przebiegi do potrzebnego obszaru **[kod]**.
- Cele renderowania cachuj per poziom zmniejszenia i odtwarzaj po zmianie rozmiaru okna **[kod]**.

## 5. Wejście (SDL3)
- Okno i wejście idą przez SDL3, nie GLFW **[26.3]**. Pola `key` i `keycode` w `KeyEvent` mają nowe znaczenie.
- Przypisania Shift, Ctrl i Alt porównuj przez [ModifierKeys.java](../przyklady/java/dev/mind/keys/ModifierKeys.java) (przetestowane testem w katalogu obok).
- Własne pola tekstowe muszą wołać `onTextInputFocusChange` **[26.3]**.

## 6. Backendy
- Vulkan jest opcjonalny. W środowisku deweloperskim wybierasz go argumentem `-Dminecraft.graphicsBackend=vulkan` **[kod]**.
- Aktywny backend odczytasz z `RenderSystem.getDevice().getDeviceInfo().backendName()` **[kod]**.
- Przy przeładowaniu shaderów na Vulkanie przed `PipelineCache#clear()` wyślij zawartość command encodera (obejście z Render4U) **[kod]**.

## 7. Przezroczystość w świecie
- OIT (Wavelet) zastępuje stary system. Potoki grupuje `OitPipelineSet`, a przełącznik debugowy to F3 + X **[26.3]**.

## 8. Pułapki
- Mixiny na wnętrzności gry (`Hud`, `GuiRenderer`, `ShaderManager`) mogą się łamać po każdej aktualizacji.
- Dokumentacja Fabric w sieci nadal dotyczy 26.2. Importy i nazwy klas sprawdzaj w jarze.
- Różnice API między 26.2 a 26.3 nie są w pełni zweryfikowane.

## 9. Czego nie wiem
- Zmiany w wydaniach Render4U v1.1.0–v1.3.2 (brak historii).
- Czy projekty z tej ściągawki budują się z podanymi wersjami.
- Co dokładnie zmieniło się w samym backendzie Vulkan w 26.3.

Pełna lista rzeczy do poprawy w Render4U: [render4u-ulepszenia.md](render4u-ulepszenia.md).
