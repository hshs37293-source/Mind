# Rendering w Minecrafcie 26.3 (wersja docelowa)

Zebrane 9 listopada 2026 z publicznych źródeł (lista na dole), opisane własnymi słowami. Wersja 26.3 („Wilderness Bound”) wyszła 15 września 2026.

Oznaczenia pewności:
- **[26.3]** potwierdzone w źródłach opisujących właśnie 26.3 (poradnik migracji NeoForged 26.2 → 26.3, oficjalny opis wydania, wpis Fabric o 26.3).
- **[26.2]** pochodzi z dokumentacji lub artykułów o 26.2. Dla 26.3 niezweryfikowane.
- **[autor]** twierdzenie autora artykułu, nie sprawdzone w kodzie.

Środowisko budowania (Java, Gradle, Loom, Fabric API) jest w [srodowisko-26-3.md](srodowisko-26-3.md). Pojęcia w [slownik.md](slownik.md).

## Skrót najważniejszych zmian [26.3]
- Okno i wejście przeszły z **GLFW na SDL3**. Zmieniły się stałe klawiszy i myszy.
- API renderowania zostało wydzielone z `com.mojang.blaze3d` do nowego projektu **Renderpearl**.
- Shadery: `#moj_import` → `#include`, jawne lokalizacje `in`/`out`, kompilacja przez Shaderc, zmienione nazwy plików.
- Przezroczystość: nowy algorytm **OIT** (Wavelet OIT) za ustawieniem Improved Transparency.
- Teren rysowany przez **MultiDrawIndirect** na obsługiwanych urządzeniach.
- Nowa komenda `/posteffect` do shaderów post-processingu z paczek zasobów.
- Wersje paczek: dane **121.0**, zasoby **97.1**.
- Wymagana jest Java 25.

## Okno i wejście: SDL3 [26.3]
To ważne dla klientów z własnym ClickGUI i bindami.
- Wejście idzie przez SDL, a nie GLFW.
- `KeyEvent#scancode` zmieniło nazwę na `keycode`. Pole `key` zawiera teraz scancody SDL, a `keycode` kody klawiszy SDL.
- Przyciski myszy mają stałe `SDLMouse`, a modyfikatory maski `SDLKeycode#SDL_KMOD_*`. Do bindów używaj `InputConstants.KEY_*` lub `SDLScancode`.
- `TextInputManager#startTextInput` i `stopTextInput` przyjmują teraz obiekt właściciela. Własne pola tekstowe powinny wołać `onTextInputFocusChange` z `GuiEventListener#setFocused`.
- `GLX` usunięto na rzecz `SdlDebug` i `SDLEventHandler`. `Window` ma `setFullscreen` i `setExclusiveFullscreen`, a `MonitorManager` identyfikuje monitory przez `int`, nie `long`.
- Linux preferuje Wayland, gdy jest dostępny (opis wydania).

## Renderpearl, czyli podział Blaze3D [26.3]
- API renderowania wyniesiono z `com.mojang.blaze3d` do nowego projektu `com.mojang.renderpearl` z pakietami `api`, `backend` i `frontend`.
- Wiele klas zmieniło lokalizację, np. `RenderPipeline`, `BlendFunction`, `GpuBuffer`, `GpuTexture`. `GpuBuffer` i `GpuTexture` stały się interfejsami z klasami bazowymi.
- Usunięto `RenderSystem#outputColorTextureOverride` i `outputDepthTextureOverride`. Cel renderowania podaje się teraz przy tworzeniu `RenderPass` przez command encoder.
- `FeatureRenderDispatcher#renderAllFeatures` jest statyczne i przyjmuje pass oraz `PreparedFrame`.
- `RenderPipeline` przyjmuje mapę typów shaderów do identyfikatorów oraz rozmiar push constants.
- `RenderTarget` i `TextureTarget` przyjmują opcjonalny `GpuFormat` głębi zamiast wartości logicznej.
- **[26.3, kod]** Potwierdzone w działającym projekcie (Render4U 1.3.2): pakiety `com.mojang.renderpearl.api.*` zawierają m.in. `pipeline` (`RenderPipeline`, `ColorTargetState`, `PrimitiveTopology`), `commands` (`CommandEncoder`, `RenderPass`), `textures` (`FilterMode`, `GpuSampler`, `GpuTextureView`), `device` (`GpuDevice`) oraz `GpuFormat`. Pakiet `com.mojang.blaze3d.*` nadal ma m.in. `RenderSystem`, `DefaultVertexFormat`, `VertexConsumer`, `RenderTarget`, `TextureTarget`, `PipelineCache`, `InputConstants` i `Window`. Przykłady użycia: [render4u-kod.md](render4u-kod.md).
- Starsze źródła (dokumentacja Fabric dla 26.2) mówią o warstwie Blaze3D, więc dla 26.3 importy zawsze sprawdzaj w jarze.

## Shadery [26.3]
- `#moj_import` zmieniono na `#include`.
- Zmienne `in` i `out` muszą mieć jawne lokalizacje (layout), a shadery vanilla wymagają rozszerzenia `GL_ARB_separate_shader_objects`.
- Kompilator GLSL używa Shaderc i udostępnia makra, m.in. `RENDERPEARL_DEPTH_IS_ZERO_TO_ONE` oraz `RENDERPEARL_INSTANCE_INDEX_INCLUDES_BASE_INSTANCE`.
- Zmiany nazw: `rendertype_clouds` → `clouds`, `rendertype_world_border` → `world_border`, a `text_background` został scalony z `text`. Doszły i zniknęły shadery OIT, głębi i przezroczystości.
- Shadery terenu (`terrain.vsh`, `terrain.fsh`) przebudowano pod multi-draw. Niektóre pola w `Globals` i `DynamicTransforms` poukładano inaczej, żeby oszczędzić pamięć.
- Mojang zaznacza, że **nadpisywanie shaderów core jest niewspierane** i mogą się zmienić w każdej chwili. Paczki podmieniające je najłatwiej się psują.

## OIT (przezroczystość) [26.3]
- Ustawienie Improved Transparency włącza teraz algorytm OIT. Rysuje on przezroczyste obiekty bez sortowania. Naprawia sporo błędów z kolejnością, ale dodaje drobne artefakty (np. kolor chmur za wieloma warstwami) i według deweloperów kosztuje więcej wydajności niż stary system.
- Przełącznik debugowy: F3 + X (przypisanie można zmienić).
- Technika to Wavelet OIT w trzech etapach: granice głębi, transmitancja i akumulacja. Dwa pierwsze etapy aktualizują tylko alfę, a akumulacja ustawia kolor.
- Shadery używają definicji `OIT_ALPHA_ONLY`, żeby w przebiegach alfa pominąć mgłę, lightmapę i nakładkę. Fragment shader dołącza `minecraft:oit.glsl`, woła `executeAlphaOnlyPhase`, a w akumulacji `sampleColorForAccumulation`.
- Potoki grupuje się przez `OitPipelineSet.builder` z metodami `with*Modifier`. `RenderType` podpina zestaw przez `RenderSetup#setOitPipelines`. Rysowanie: `RenderType#prepare` i `PreparedRenderType#drawFromBufferOit`.
- Magazyn `oitTranslucent` zastępuje dawne koszyki translucent, gdy włączone jest `SubmitNodeStorage#setUseImprovedTransparency`. W takim przypadku nie wolno wołać `renderAllFeatures` na tym magazynie, bo elementy rysowałyby się wielokrotnie.
- Pliki: `core/oit_composite.fsh`, `include/oit.glsl`, `core/blit_depth.fsh` (alternatywna kopia głębi na urządzeniach, gdzie pierwotna nie działa).

## Teren i post-processing [26.3]
- Teren używa MultiDrawIndirect na obsługiwanych urządzeniach.
- `/posteffect` stosuje shadery post-processingu zdefiniowane w paczkach zasobów. Deweloperzy zaznaczają, że mogą to jeszcze przeprojektować.

## Klasy renderujące [26.3]
- `GameRenderer` implementuje `ResourceManagerReloadListener`. Pole `itemInHandRenderer` stało się `firstPersonHandsAndItemsRenderer`, a `render` i `renderLevel` nie przyjmują argumentów.
- `ItemInHandRenderer` zmienił nazwę na `FirstPersonHandsAndItemsRenderer`.
- `LevelRenderer#render` przyjmuje wartość logiczną dla spójnej głębi. Blit konturów bytów nazywa się teraz `blitEntityOutline`.
- `CloudRenderer`, `WeatherEffectRenderer` i `WorldBorderRenderer` dzielą `render` na `prepare` i osobne `renderOit`.
- `RenderPipelines`: `ARMOR_TRANSLUCENT` → `WOLF_ARMOR_CRACKS`, `LINES_TRANSLUCENT` → `LINES_TRANSLUCENT_NO_DEPTH_WRITE`, usunięto potoki tła tekstu, dodano potoki terenu z multidraw.
- `SubmitNodeCollection`: `seeThroughNameTags` → `seeThrough`, `gizmos` zastąpiono przez `translucentGizmos` i `solid`, a `alwaysOnTop` → `alwaysOnTopGizmos`.
- `submitModel` i `submitModelPart` nie przyjmują parametru nakładki pękania (jest osobne `submitCrumblingOverlay`) i biorą `UvMapping` zamiast `TextureAtlasSprite`.
- Nowe palety tekstur: sekcja `palette` w `.png.mcmeta` zastępuje atlas wykończeń zbroi. `TrimMaterial` używa `palette_id` zamiast `asset_name`.
- Cząsteczki: `SoulParticle` → `EmissiveRisingParticle`.

## Rysowanie GUI i HUD
- **[26.3]** Klasa `GuiGraphicsExtractor` istnieje w 26.3 (poradnik NeoForged wymienia jej zmiany: `textWithWordWrap` zwraca teraz następną pozycję y, zmieniły się przeciążenia tooltipów). Doszło `Font#prepareBackground`.
- **[26.3]** Artykuł o porcie moda z 26.1.2 na 26.3 zgłasza, że `GuiGraphicsExtractor`, `HudElementRegistry` i architektura entity render state nie zmieniły się w sposób łamiący. To pojedynczy projekt, więc nie jest to gwarancja dla każdego kodu.
- **[26.2]** Figury: `fill(...)`, `outline(...)`, `horizontalLine(...)`, `verticalLine(...)`. Inne kształty wymagają `BufferBuilder`.
- **[26.2]** Przycinanie: `enableScissor(...)` i `disableScissor()`, zagnieżdżalne, każde włączenie wymaga wyłączenia.
- **[26.2]** Tekstury: `blit(...)` z potokiem jako pierwszym argumentem (np. `RenderPipelines.GUI_TEXTURED`) i współrzędnymi U, V jako `float`.
- **[26.2]** Tekst: `text(...)` przyjmuje czcionkę, napis lub `Component`, pozycję, kolor i flagę cienia. Kolory są w formacie ARGB, a samo RGB daje przezroczysty tekst (pomaga m.in. `ARGB.opaque(...)`).
- **[26.2]** Stos macierzy `Matrix3x2fStack` (z `pose()`), `pushMatrix` i `popMatrix` w parach.
- **[26.2]** HUD: `HudElementRegistry`, np. `attachElementBefore(VanillaHudElements.CHAT, id, element)`. Element dostaje `GuiGraphicsExtractor` i `DeltaTracker`. Dla animacji w czasie rzeczywistym użyj `Util.getMillis()`, a `getGameTimeDeltaPartialTick(false)` zatrzymuje się przy `/tick freeze`.
- **[26.2]** Model „render state”: dane gry są kopiowane do stanu renderowania, a `GuiRenderer#prepare` je wysyła, co pozwala przygotowywać następną klatkę podczas rysowania obecnej.
- **[26.2]** `BufferBuilder`: `addVertex(Matrix4f, x, y, z)`, potem pozostałe atrybuty w kolejności `VertexFormat`. Formaty m.in. `POSITION_COLOR`, `POSITION_TEX`. Tryby: `TRIANGLES`, `QUADS`, `LINES` itd. Zła kolejność wierzchołków może dać odrzucenie ściany (culling).

## Vulkan
- **[26.2]** Od 26.2 jest opcjonalny, eksperymentalny backend. Wybór w Options → Video Settings → Graphics API (Default, Prefer Vulkan, Prefer OpenGL). Mojang mówi, że może być mniej wydajny i stabilny niż OpenGL.
- **[26.3]** Oficjalny opis wydania 26.3 nie wspomina o Vulkanie. Notatki snapshotu wymieniają znaną usterkę Vulkana na Macu.
- **[26.3, kod]** Backend Vulkan istnieje w 26.3. Render4U wykrywa go przez `RenderSystem.getDevice().getDeviceInfo().backendName()` (wartość „Vulkan”), a w środowisku deweloperskim uruchamia go argumentem JVM `-Dminecraft.graphicsBackend=vulkan`. Ten sam projekt ma obejście dla Vulkana przy przeładowaniu shaderów (wysłanie command encodera przed czyszczeniem cache potoków). Szczegóły w [render4u-kod.md](render4u-kod.md). Co dokładnie zmieniło się w samym backendzie w 26.3, nie wiem.
- Plotka z tytułu jednego artykułu: Vulkan miałby być domyślny w 26.4. Nie sprawdzałem treści.

## Zgodność modów w 26.3 [autor]
- Według jednego artykułu stabilne wersje na 26.3 mają m.in. Fabric API 0.165.0, Sodium 0.9.2, Iris 1.11.7 i Lithium 0.26.2.
- Liczba modów na Modrinth dla 26.3 jest mniejsza niż dla 26.2. Sam wpis na liście nie dowodzi, że mod działa.
- Zmiany w Fabric API w 26.3 (wpis Fabric): usunięto rejestry kompostowania, paliw, warzenia oraz ogołacania, orania i spłaszczania na rzecz systemów danych vanilli. Doszły m.in. API płynów, callback przepływu płynu, rozszerzenie `TooltipFlag` i dostawca mocy zaklęć.

## Rozbieżności źródeł
| Temat | Co mówią źródła | Jak traktować |
|---|---|---|
| Wersje paczek | Snapshot 2: dane 109.0, zasoby 90.0. Wydanie 26.3: dane 121.0, zasoby 97.1 | Obowiązują wartości z wydania (121.0 i 97.1). Starsze były wcześniejszym snapshotem |
| Warstwa API renderowania | Dokumentacja Fabric 26.2: Blaze3D. Poradnik 26.3: Renderpearl | Dla 26.3 przyjmuj Renderpearl, a importy sprawdzaj w jarze |
| Nazwa klasy GUI | Issue o migracji `blit` dla 26.2: `GuiGraphics`. Dokumentacja 26.2 i poradnik 26.3: `GuiGraphicsExtractor` | Dla 26.3 przyjmuj `GuiGraphicsExtractor` |
| Przezroczystość | Snapshot 2: OIT „zastąpiło” ustawienie. Opis wydania: ustawienie Improved Transparency włącza OIT | Ustawienie zostało, algorytm jest nowy |

## Co to znaczy dla własnego klienta lub moda (26.3)
- Klawisze i mysz: używaj stałych SDL i pól `key`/`keycode` w nowym znaczeniu.
- Własne shadery: `#include`, jawne lokalizacje in/out, nowe nazwy plików, i nie polegaj na nadpisywaniu shaderów core.
- Przezroczystość: jeśli rysujesz przezroczyste elementy w świecie, uwzględnij potoki OIT (`OitPipelineSet`).
- UI: rysuj przez `GuiGraphicsExtractor` i potoki, kolory zawsze ARGB.
- Render4U ([render4u.md](render4u.md)) deklaruje Fabric 26.3+ oraz Vulkan i OpenGL, co zgadza się z kierunkiem zmian opisanym tu.

## Do uzupełnienia
- [ ] Ostatnia część poradnika NeoForged dla 26.3 (znaki po 300000) nie została przeczytana. Może zawierać dalsze zmiany klienta.
- [ ] Dokumentacja Fabric „Rendering in the World” była niedostępna w mojej sesji.
- [ ] Renderpearl: sprawdzone tylko klasy używane przez Render4U (pakiet `api`). Pakiety `backend` i `frontend` nie były czytane.
- [ ] Dokumentacja Fabric dla 26.3 (obecnie korzystam z wersji dla 26.2).
- [ ] Zmiany samego backendu Vulkan w 26.3 (potwierdzone tylko, że istnieje i jak się go wybiera).

## Źródła
- Poradnik migracji 26.2 → 26.3 (NeoForged): https://docs.neoforged.net/primer/docs/26.3/
- Opis wydania Java Edition 26.3: https://www.minecraft.net/en-us/article/minecraft-java-edition-26-3
- Fabric dla 26.3: https://fabricmc.net/2026/09/15/263.html
- Port moda Fabric do 26.3: https://spicesfire.com/blog/porting-a-fabric-mod-to-minecraft-26-3/
- Co zmienia 26.3 dla modów, shaderów i paczek: https://minecrafthub.io/articles/minecraft-26-3-mods-shaders-packs
- Snapshot 2 (26.3): https://feedback.minecraft.net/hc/en-us/articles/47030118645389-Minecraft-Java-Edition-26-3-Snapshot-2
- OIT w 26.3: https://syntaxmine.com/articles/oit-transparency-minecraft-26-3
- Co psuje się w 26.3: https://syntaxmine.com/articles/what-breaks-in-minecraft-26-3
- Vulkan w 26.2: https://syntaxmine.com/articles/minecraft-vulkan-renderer-26-2
- Fabric, rysowanie GUI (26.2): https://docs.fabricmc.net/develop/rendering/gui-graphics
- Fabric, HUD (26.2): https://docs.fabricmc.net/develop/rendering/hud
- Fabric, podstawy renderowania (26.2): https://docs.fabricmc.net/develop/rendering/basic-concepts
- Migracja blit w 26.2: https://github.com/jivinstev/mc-mod-version-upgrade/issues/55
