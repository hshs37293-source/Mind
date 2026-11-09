# Render4U 1.3.2: analiza kodu (Minecraft 26.3)

Źródło: archiwum ZIP z kodem wydania 1.3.2, które przysłał użytkownik. Licencja projektu: MIT. Opis własnymi słowami, z krótkimi wzorcami użycia API napisanymi przeze mnie.

**Czego nie robiłem:** nie budowałem i nie uruchamiałem projektu. Wszystko poniżej wynika z czytania kodu. Znaczniki: **[kod]** przeczytane w plikach, **[README]** tylko z README projektu (po rosyjsku), **[wniosek]** moja interpretacja.

Powiązane: [render4u.md](render4u.md) (opis z wątku), [render-26-3.md](render-26-3.md) (zmiany w 26.3), [srodowisko-26-3.md](srodowisko-26-3.md), [slownik.md](slownik.md).

## Co to jest
- Baza klienta do Fabric z własnym silnikiem renderowania 2D: moduły, ClickGUI, szyna zdarzeń, shadery SDF, blur, fonty MSDF i TTF, obrazy i GIF-y, głowy graczy.
- Około 12 tysięcy linii Java. W archiwum jest 2232 pliki, ale większość to zasoby: 1373 pliki json (w tym dane kosmetyków w `assets/render4u/cosmetics/`) i 696 png. Zasobów kosmetyków nie analizowałem.
- Tylko klient: w `fabric.mod.json` jest `"environment": "client"`.
- Dwa języki interfejsu: `en_us.json` i `ru_ru.json`.

## Konfiguracja budowania [kod]
To działająca konfiguracja projektu pod 26.3 (nie sprawdzałem, czy się buduje).

| Element | Wartość |
|---|---|
| Minecraft | `26.3` (w `fabric.mod.json` wymagana dokładnie ta wersja) |
| Fabric Loader | 0.20.1 |
| Fabric Loom | 1.17.21, identyfikator wtyczki `net.fabricmc.fabric-loom` |
| Fabric API | 0.165.0+26.3 |
| Gradle (wrapper) | 9.6.0 |
| Java | 25 (`options.release` = 25, mixiny z `compatibilityLevel` JAVA_25) |
| Zależność gry | `minecraft "com.mojang:minecraft:${minecraft_version}"`, bez wiersza z mapowaniami |
| Uruchomienie z Vulkanem | zadanie `clientVulkan` z argumentem JVM `-Dminecraft.graphicsBackend=vulkan` |

W `build.gradle` jest wtyczka `maven-publish`, ale nie ma bloku publikacji. README opisuje projekt jako bazę do sklonowania i zbudowania, więc nie wiem, czy da się go dodać jako zależność z repozytorium Maven.

## Struktura pakietów (`dev.render4u`) [kod]
- `Render4U`: punkt wejścia klienta. Inicjuje potoki, fonty TTF i MSDF, menedżer modułów i konfigurację.
- `core/event`: szyna zdarzeń, priorytety, adnotacja `@EventTarget`, zdarzenia (tick, klawisze, znaki, mysz, kółko, pakiety, wejście ruchu, 2D, 3D).
- `module`: moduły, kategorie i siedem typów ustawień (Boolean, Number, Mode, MultiMode, Color, Keybind, String). Wbudowane moduły to ClickGUI, HUD i AutoSprint.
- `gui`: `ClickGuiScreen` (najdłuższy plik, 702 linie) i rendery elementów (karty modułów, panel ustawień, pasek boczny, okno profilu).
- `render/display`: API rysowania 2D, potoki, kształty, blur, obrazy, głowy, batcher.
- `render/font`: fonty MSDF i TTF.
- `render/world`: projekcja współrzędnych świata na ekran.
- `render/vulkan`: wykrywanie backendu.
- `render/drag`: przeciąganie elementów interfejsu.
- `mixin`: 10 mixinów (lista niżej).
- `util`: animacje (sprężyny, easing), kolory, gradienty, tryby mieszania, lokalizacja, klawisze.
- `theme`, `config`.

## Przebieg klatki interfejsu [kod]
1. Mixin `HudMixin` wstrzykuje się na końcu `Hud#extractRenderState` (klasa `net.minecraft.client.gui.Hud`). Działa tylko po zakończeniu ładowania gry.
2. `DisplayUtil.render` tworzy **własną instancję** `GuiGraphicsExtractor` na tym samym `guiRenderState` co gra, a stos macierzy skaluje o stałą `SCALE = 2.0` podzieloną przez GUI scale gry. Dzięki temu współrzędne w bibliotece nie zależą od ustawienia skali GUI.
3. `DisplayBatcher.begin` otwiera sesję batchowania. Potem szyna zdarzeń dostaje kolejno `EventDisplay` (rysowanie 2D) i `EventRender3D`.
4. Każdy element (prostokąt, tekst, blur) jest obiektem implementującym `GuiElementRenderState` i trafia do `guiRenderState.addGuiElement(...)`, czyli do standardowego potoku GUI gry. Biblioteka więc nie rysuje obok gry, tylko dokłada własne elementy do jej kolejki.
5. Mixin `GuiRendererMixin` na początku `GuiRenderer#draw` aktualizuje przeciąganie i uruchamia `Blur.process()`, na końcu `draw` recyklinguje batche, a na początku `GuiRenderer#close` czyści prośby o blur.

Wzorzec elementu: obiekt-opis zawiera pozycję, rozmiar, macierz, kolory rogów i obszar przycinania, a metody interfejsu zwracają potok (`pipeline()`), teksturę (`textureSetup()`), prostokąt ograniczający (`bounds()`), przycinanie (`scissorArea()`) i budują wierzchołki (`buildVertices(VertexConsumer)`).

## Potoki i shadery [kod]
- Jest 16 potoków (`Pipelines`): zaokrąglony prostokąt, kontur, cień, linia, okrąg, obraz, głowa, tekst (MSDF), font, panel blur oraz sześć fullscreenowych do blura (downsample, upsample, Gauss poziomy i pionowy, box poziomy i pionowy).
- Potoki GUI buduje się na bazie `RenderPipelines.GUI_SNIPPET` lub `GUI_TEXTURED_SNIPPET`. Podaje się lokalizację, shader wierzchołków i fragmentów o tej samej nazwie (`render4u:core/<nazwa>`), wiązanie wierzchołków z formatem `DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR`, a rejestruje przez `RenderPipelines.register(...)`.
- Potoki fullscreenowe używają vanillowego shadera wierzchołków `core/screenquad`, topologii `TRIANGLES`, `ColorTargetState.DEFAULT` i układów bind group skopiowanych z `RenderPipelines.TRACY_BLIT`. Warianty blura (pion/poziom, box/Gauss) to definicje shaderów `BLUR_VERTICAL` i `KERNEL_BOX`.
- Shadery leżą w `assets/render4u/shaders/core/` (13 plików `.fsh`, 10 `.vsh`) we własnej przestrzeni nazw. **Biblioteka nie nadpisuje shaderów core vanilli**, tylko dodaje własne, co jest zgodne z ostrzeżeniem Mojang o niewspieraniu nadpisywania.
- Shadery używają `#version 330`, rozszerzenia `GL_ARB_separate_shader_objects` i jawnych `layout(location = ...)`, jak wymaga 26.3. Uniformy to bloki `DynamicTransforms` i `Projection`.
- **Zaokrąglony prostokąt:** wierzchołki niosą lokalne współrzędne w UV0, a shader liczy odległość znakowaną (SDF) do zaokrąglonego prostokąta. Krawędź wygładza przez `fwidth`, więc jest ostra przy każdej skali. Promienie czterech rogów są upakowane w dwóch liczbach całkowitych przekazanych jako drugi zestaw UV (`UV2`).
- **Cień:** ta sama idea SDF, ale z miękkim zanikiem (`smoothstep`) i dodanym szumem (dithering), żeby uniknąć pasów.
- **Tekst MSDF:** shader bierze medianę trzech kanałów próbki, wylicza zakres w pikselach ekranu i z niego krawędź. Obsługuje obrys i rozmycie (glow) z parametrów w `UV2`. Atlasy to pary png + json (typ `mtsdf`, zakres odległości 20, rozmiar glifu 64).
- **Panel blur:** rysuje zaokrąglony prostokąt, którego kolor to próbka już rozmytej tekstury ekranu (współrzędne z `gl_FragCoord`). Tryb „glass” podbija nasycenie (mieszanie z luminancją o współczynniku 1,2), a tint miesza się według alfy koloru. Poziom blura, flaga glass i 5-bitowa przezroczystość są upakowane w parametrach `UV2`.

## Blur [kod]
- Typy: `GAUSSIAN`, `KAWASE`, `BOX`. Siła 1–6 (domyślnie 3). Domyślnie włączony tryb glass (globalna flaga `globalGlass`).
- Elementy blura **zgłaszają zapotrzebowanie** już przy tworzeniu (`Blur.request`), a przetwarzanie dzieje się raz, na początku `GuiRenderer#draw`. Zgłoszone obszary scalają się w jeden prostokąt.
- Łańcuch: kolejne zmniejszanie (downsample) głównego celu renderowania o połowę, a potem dla Gaussa i boxa przebiegi poziomy i pionowy (dwa powtórzenia przy sile powyżej 3), a dla Kawase łańcuch upsample.
- Przebiegi są przycinane (scissor) do obszaru potrzebnego plus margines jądra filtra, więc koszt zależy od rozmiaru paneli, a nie całego ekranu.
- Cele (`TextureTarget` z formatem `GpuFormat.RGBA8_UNORM` i bez głębi) są cachowane per poziom i odtwarzane po zmianie rozmiaru okna.

Wzorzec przebiegu na nowym API (nazwy z kodu):
- `CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();`
- `try (RenderPass pass = encoder.createRenderPass(() -> "etykieta", widokKoloru, Optional.empty())) { ... }`
- w środku: `setPipeline(RenderSystem.getCompiledPipeline(potok))`, `RenderSystem.bindDefaultUniforms(pass)`, `enableScissor(x, y, w, h)`, `setUniform("InSampler", widok, sampler)`, `draw(3, 1, 0, 0)`, `disableScissor()`.
- sampler: `RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)`.

## Batcher [kod]
- `DisplayBatcher` łączy kolejne elementy o tym samym potoku, teksturze i obszarze przycinania w jeden batch, żeby zmniejszyć liczbę wywołań rysowania.
- Przy dołączaniu szuka pasującego batcha od najnowszego wstecz i przerywa na pierwszym, który nachodzi na element, więc kolejność rysowania (nakładanie) zostaje zachowana.
- Elementy poza ekranem lub o zerowym rozmiarze są pomijane. Obiekty batchy są w puli i recyklingowane po klatce. Są liczniki batchy i elementów z ostatniej klatki.
- `DisplayUtil.nextStratum` opróżnia batcher i przechodzi do następnej warstwy (`graphics.nextStratum()`), gdy trzeba rysować nad wcześniejszymi elementami.

## Wejście i SDL3 [kod]
- `KeyboardHandlerMixin` wstrzykuje się na początku `KeyboardHandler#keyPress(long, int, KeyEvent)` i `charTyped(long, CharacterEvent)`. Zdarzenie dostaje `event.key()`, `event.keycode()`, akcję i modyfikatory. Zdarzenie można anulować, co przerywa dalszą obsługę przez grę.
- `MouseHandlerMixin` obsługuje `MouseHandler#onButton(long, MouseButtonInfo, int)` i `onScroll`. Współrzędne kursora są przeliczane przez `MouseHandler.getScaledXPos/YPos`. Najpierw klik dostaje menedżer przeciągania.
- `KeyUtil.isKeyMatch` porównuje zarówno pole `key`, jak i `scancode` oraz traktuje jako równoważne pary 344/229 i 340/225. **[wniosek]** To wygląda na obejście różnicy między kodami GLFW a scancodami SDL dla prawego i lewego Shifta (zgodne ze zmianą na SDL3 z notatki [render-26-3.md](render-26-3.md)).
- Moduł włącza się klawiszem tylko wtedy, gdy żaden ekran nie jest otwarty.

## Mixiny [kod]
| Mixin | Cel |
|---|---|
| `HudMixin` | `Hud#extractRenderState` (koniec): uruchamia całe rysowanie biblioteki |
| `GuiRendererMixin` | `GuiRenderer#draw` (początek i koniec) oraz `close`: blur, recykling batchy |
| `ShaderManagerMixin` | `ShaderManager#apply`: owija `PipelineCache#clear()` przez `@WrapOperation` (MixinExtras) |
| `KeyboardHandlerMixin`, `MouseHandlerMixin` | zdarzenia wejścia |
| `WindowMixin` | podmienia ikony okna w `Window#setIcon(List)` |
| `MinecraftMixin`, `ConnectionMixin`, `KeyboardInputMixin`, `ClientInputAccessor` | tick, sieć i wejście ruchu (nie analizowałem ich szczegółowo) |

`ShaderManagerMixin` ma obejście dla Vulkana: przed wyczyszczeniem cache potoków (przy przeładowaniu shaderów) wysyła zawartość świeżego command encodera, w try/catch z jednym ostrzeżeniem w logu. **[wniosek]** To zabezpieczenie przed błędami przy przeładowaniu zasobów z backendem Vulkan.

## Vulkan [kod]
- `VulkanHelper` pyta `RenderSystem.getDevice().getDeviceInfo().backendName()` i porównuje z tekstem „Vulkan”. Wynik jest cachowany.
- To potwierdza, że w 26.3 backend Vulkan nadal istnieje i że wybiera się go argumentem `-Dminecraft.graphicsBackend=vulkan`.
- Kod rysujący nie rozgałęzia się po backendzie (poza obejściem powyżej), bo korzysta z API potoków i command encodera.

## Projekcja 3D → 2D [kod]
`Render3D` liczy raz na klatkę macierz widoku i projekcji, zapamiętuje pozycję kamery i udostępnia projekcję punktów i ramek świata na współrzędne ekranu. Nie rysuje geometrii w świecie: wszystko jest kształtami 2D rysowanymi tym samym API na warstwie HUD (zdarzenie `EventRender3D` jest wysyłane z przebiegu HUD). Dlatego nie dotyczą tego zmiany w OIT i potokach terenu z 26.3.

## Moduły, ustawienia, zdarzenia, konfiguracja
- Szyna zdarzeń z adnotacją `@EventTarget` i priorytetami (`Priority`) **[kod]**. README dodaje, że używa `MethodHandle` **[README]**.
- Siedem typów ustawień **[kod]**. Konfiguracja w `.minecraft/render4u/config.json` z atomowym zapisem **[README]**. Sześć motywów kolorystycznych w `themes.json` **[README]**.
- Świadomie nie opisuję szczegółów zachowań modułów dotyczących rozgrywki i sieci. Dla bazy wiedzy o renderowaniu ważna jest sama architektura.

## Co z tego wynika dla własnego klienta pod 26.3
- Rysuj własne elementy jako `GuiElementRenderState` dodawane do `guiRenderState`. Wtedy działają z kolejką, przycinaniem i warstwami gry.
- Własne shadery trzymaj we własnej przestrzeni nazw, a nie nadpisuj shaderów core.
- Parametry per-element pakuj w drugi zestaw UV, żeby nie potrzebować dodatkowych uniformów.
- Blur zgłaszaj z góry i licz raz na klatkę z przycinaniem do potrzebnego obszaru.
- Przy mixinach na `GuiRenderer`, `Hud` i `ShaderManager` licz się z łamaniem po aktualizacjach Minecrafta.
- Wejście obsługuj z założeniem SDL3: porównuj `key` i `keycode` i sprawdź stałe klawiszy.

## Do uzupełnienia
- [ ] Zmiany w wydaniach v1.1.0–v1.3.2: archiwum nie zawiera historii git ani changelogu.
- [ ] Nieprzeczytane części: `ClickGuiScreen`, renderery ustawień, `TTFFontRenderer`, `GifDecoder`, implementacja szyny zdarzeń, `ConfigManager`, `Theme`, dane kosmetyków.
- [ ] Sprawdzenie, czy projekt buduje się z podanymi wersjami (Gradle 9.6.0, Loom 1.17.21).
