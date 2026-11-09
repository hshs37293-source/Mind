# Rendering w Minecrafcie 26.x (26.2 → 26.3) dla klientów i modów

Notatka zebrana 9 listopada 2026 z publicznych źródeł (lista na dole), opisana własnymi słowami. Dokumentacja Fabric, z której korzystam, dotyczy **26.2**, a nie 26.3. Tam, gdzie coś jest wnioskiem autora artykułu, a nie potwierdzonym faktem, jest to zaznaczone.

## Najważniejsze w skrócie
- Od **26.2** Minecraft ma wbudowany, opcjonalny backend **Vulkan** obok OpenGL.
- W **26.3** zmieniono sposób rysowania przezroczystości: stary system Improved Transparency zastąpiono techniką **OIT** (order-independent transparency).
- Kod rysujący w modach powinien używać abstrakcji **Blaze3D**, a nie surowych wywołań OpenGL.
- Rysowanie GUI działa w modelu „render state”. Dokumentacja Fabric dla 26.2 opisuje je przez klasę `GuiGraphicsExtractor` (nazewnictwo klas rozbiega się między źródłami, zob. [slownik.md](slownik.md)).
- Nadpisywanie shaderów rdzenia (core) jest oficjalnie niewspierane i może przestać działać po aktualizacji.

## Backend Vulkan (26.2)
- Opcjonalny i eksperymentalny. Mojang zaznacza, że może być mniej wydajny i stabilny niż OpenGL.
- Wymaga Vulkan 1.2 z funkcjami dynamic rendering i push descriptors. Preferuje dedykowane karty graficzne. Na macOS działa przez MoltenVK (warstwa tłumacząca na Metal).
- Włączanie: Options → Video Settings → opcja Graphics API. Wartości: Default, Prefer Vulkan (Experimental), Prefer OpenGL.
- Ustawienie jest lokalne dla gracza i nie wpływa na światy ani serwery.
- Przy awarii startu gra schodzi sama: Vulkan → Default → Prefer OpenGL.
- Aktywny backend widać na ekranie F3 (linia o trybie prezentacji).
- Mody zależne od wnętrzności OpenGL (np. Sodium i pochodne) mają osobną ścieżkę kodu dla Vulkana, więc zgodność trzeba sprawdzać u każdego moda osobno.
- Nieprzetestowany trop z tytułu jednego artykułu: wersja 26.4 miałaby domyślnie używać Vulkana. Nie sprawdziłem treści, więc traktuj jako plotkę.

## OIT w 26.3
- Zastępuje Improved Transparency przy rysowaniu przezroczystych powierzchni (szkło, woda, lód, liście).
- Stary system zależał od kolejności rysowania warstw, więc przy ruchu kamery mogły występować migotanie i błędy. OIT zbiera wszystkie przezroczyste fragmenty w danym pikselu i łączy je matematycznie, niezależnie od kolejności.
- Zmiany w shaderach: dodano m.in. `core/oit_composite.fsh` i zestaw plików `oit_*.glsl`, usunięto dawny łańcuch post-processingu przezroczystości. Pre-Release 1 dołożył `core/blit_depth.fsh` jako alternatywny sposób kopiowania głębi, bo pierwotny nie działał na części urządzeń.
- Zmieniono nazwy shaderów chmur i granicy świata, a w wielu shaderach core doszły nowe definicje związane z OIT.
- Wydajność: oficjalnie spodziewany jest wyższy koszt niż w starym systemie, szczególnie w widokach pełnych szkła. Brak publicznych benchmarków.
- Znane problemy w snapshocie 2: różowy ekran z Vulkanem na Macu i zbyt ciemne górne ściany w oświetleniu bytów.
- Mojang ostrzega, że nadpisywanie shaderów core jest niewspierane. Mody i paczki shaderów, które ingerują w pass przezroczystości, mogą wymagać aktualizacji (to wniosek autora artykułu, nie potwierdzona awaria).

## Zgodność modów z 26.3
Według jednego z artykułów stabilne wersje na 26.3 miały m.in. Fabric API 0.165.0, Sodium 0.9.2, Iris 1.11.7 i Lithium 0.26.2. Przed aktualizacją każdy mod trzeba sprawdzić osobno.

Uwaga o rozbieżności: snapshot 2 podaje wersje paczek danych 109.0 i zasobów 90.0, a artykuł o „co się psuje” podaje 121.0 i 97.1. Różnica może wynikać z późniejszych snapshotów, więc przed użyciem sprawdź aktualne wartości.

## Rysowanie GUI (dokumentacja Fabric, 26.2)
- Dokumentacja Fabric dla 26.2 nazywa główną klasę rysowania `GuiGraphicsExtractor`. Starsze nazwy z tej samej roli to `DrawContext` i `GuiGraphics`. Issue o migracji `blit` dla 26.2 nadal używa nazwy `GuiGraphics`, więc nie wiem na pewno, czy to zmiana nazwy tej samej klasy, czy zależy to od mapowania nazw. Przed użyciem sprawdź nazwę w jarze swojej wersji.
- Figury: `fill(...)` (wypełniony prostokąt), `outline(...)`, `horizontalLine(...)`, `verticalLine(...)`. Inne kształty wymagają `BufferBuilder`.
- Przycinanie: `enableScissor(...)` i `disableScissor()`, mogą być zagnieżdżane, każde włączenie wymaga wyłączenia.
- Tekstury: `blit(...)` z wieloma przeciążeniami. Zaleca się wersję z rozmiarem tekstury i potokiem, np. `RenderPipelines.GUI_TEXTURED`.
- Tekst: `text(...)` przyjmuje czcionkę, napis lub `Component`, pozycję, kolor i flagę cienia. Kolory są w formacie **ARGB**. Przekazanie samego RGB sprawi, że tekst będzie przezroczysty. Pomocna jest m.in. `ARGB.opaque(...)`.
- Macierze: stos `Matrix3x2fStack` (z `pose()`), operacje `pushMatrix` i `popMatrix` muszą być sparowane. Strona w tym miejscu jest niespójna, bo opis mówi o starszym `PoseStack`, a kod używa `Matrix3x2fStack`.

## Podstawy niskopoziomowe (dokumentacja Fabric, 26.2)
- Bezpośrednie OpenGL nie jest wspierane. Kod ma korzystać z warstwy **Blaze3D**, która stoi nad OpenGL i Vulkanem.
- `BufferBuilder` formatuje dane wierzchołków i wysyła je do backendu. Wierzchołek dodaje się przez `addVertex(Matrix4f, x, y, z)`, a potem dopisuje pozostałe atrybuty w kolejności formatu.
- `VertexFormat` i `DefaultVertexFormat` opisują układ wierzchołka (np. `POSITION_COLOR`, `POSITION_TEX`, `BLOCK`, `NEW_ENTITY`).
- `VertexFormat.Mode` określa sposób składania prymitywów: `TRIANGLES`, `TRIANGLE_STRIP`, `TRIANGLE_FAN`, `QUADS`, `LINES`, `DEBUG_LINES`.
- Zła kolejność wierzchołków (winding) może sprawić, że ściana zostanie odrzucona (culling).
- Od 1.21.6 rendering przechodzi na `RenderType`, `RenderPipeline` i `RenderState`. Dane gry są najpierw kopiowane do render state, żeby można było przygotowywać następną klatkę podczas rysowania obecnej. W 1.21.8 GUI działa już w tym modelu (`GuiGraphicsExtractor` dopisuje do render state, a `GuiRenderer#prepare` wysyła dane).

## HUD (dokumentacja Fabric, 26.2)
- Rejestracja przez `HudElementRegistry`. Element (`HudElement`) to zwykle lambda dostająca `GuiGraphicsExtractor` i `DeltaTracker`.
- Pozycjonowanie: np. `HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, id, element)` wstawia element tuż przed czatem. API samo zarządza kolejnością warstw.
- Animacje: `DeltaTracker#getGameTimeDeltaPartialTick(false)` zwraca postęp między tickami i zatrzymuje się przy `/tick freeze`. Dla animacji w czasie rzeczywistym użyj `Util.getMillis()`.
- Mieszanie kolorów: `ARGB.linearLerp`, często razem z `Mth.sin`.

## Migracja kodu ze starszych wersji
Zmiana z 26.2: `GuiGraphics.blit` wymaga teraz potoku renderowania jako pierwszego argumentu, a współrzędne U i V przyjmuje jako `float`. Wzorzec migracji: dopisać `RenderPipelines.GUI_TEXTURED` na początku, zrzutować U i V na `float`, a w krótszych przeciążeniach dopisać jawnie rozmiar tekstury (dawniej domyślnie 256×256). Wybór innego potoku niż `GUI_TEXTURED` może zmienić wygląd, bo ten odpowiada dawnemu domyślnemu mieszaniu. Każde przeciążenie warto zweryfikować na faktycznym jarze 26.2.

## Co to znaczy dla własnych klientów i modów
- Rysuj interfejs przez `GuiGraphicsExtractor` i potoki, bez surowego OpenGL. Wtedy kod działa na obu backendach.
- Własne shadery (zaokrąglenia, blur, cienie) są wrażliwe na zmiany core. Trzymaj je w osobnej warstwie i testuj po każdej aktualizacji.
- Kolory zawsze jako ARGB.
- Render4U ([render4u.md](render4u.md)) deklaruje wsparcie dla Fabric 26.3 i nowszych oraz dla Vulkana i OpenGL, co zgadza się z opisanym tu kierunkiem zmian. Dokumentacja Fabric jest jednak dla 26.2, więc różnice między 26.2 a 26.3 w API nie są zweryfikowane. Sprawdzaj, którą wersję Minecrafta biblioteka aktualnie wspiera.

## Do uzupełnienia
- [ ] Dokumentacja „Rendering in the World” (renderowanie w świecie 3D): strona nie była dostępna w mojej sesji.
- [ ] Dokładny opis Blaze3D, bo żadne z przejrzanych źródeł nie opisuje go szczegółowo.
- [ ] Sprawdzenie, co faktycznie zmienia się w 26.3 w Fabric API poza OIT (dokumentacja jest dla 26.2).
- [ ] Potwierdzenie plotki o Vulkanie jako domyślnym w 26.4.

## Źródła
- Snapshot 2 (26.3): https://feedback.minecraft.net/hc/en-us/articles/47030118645389-Minecraft-Java-Edition-26-3-Snapshot-2
- OIT w 26.3: https://syntaxmine.com/articles/oit-transparency-minecraft-26-3
- Co psuje się w 26.3: https://syntaxmine.com/articles/what-breaks-in-minecraft-26-3
- Vulkan w 26.2: https://syntaxmine.com/articles/minecraft-vulkan-renderer-26-2
- Fabric, podstawy renderowania: https://docs.fabricmc.net/develop/rendering/basic-concepts
- Fabric, rysowanie GUI: https://docs.fabricmc.net/develop/rendering/gui-graphics
- Fabric, HUD: https://docs.fabricmc.net/develop/rendering/hud
- Migracja blit w 26.2: https://github.com/jivinstev/mc-mod-version-upgrade/issues/55
