# Słownik pojęć i nazw

Jedno miejsce na terminy używane w pozostałych notatkach. Dzięki temu te same rzeczy nazywamy tak samo. Wersja docelowa bazy to **Minecraft 26.3**. Jeśli źródła rozchodzą się w nazwach, jest to zapisane tutaj.

## Nazwy klasy rysowania GUI
| Nazwa | Gdzie występuje | Uwagi |
|---|---|---|
| `DrawContext` | starsze wersje, mapowanie Yarn | dawna nazwa, dla 26.3 nieużywana |
| `GuiGraphics` | mapowanie Mojang, issue o migracji `blit` dla 26.2 | nazwa z opisu zmian w 26.2 |
| `GuiGraphicsExtractor` | dokumentacja Fabric 26.2 oraz poradnik migracji 26.3 | nazwa obowiązująca w 26.3 |

Wpis Fabric o 26.3 mówi, że wersja używa nazw z niezaciemnionej gry, więc nazwy z dokumentacji dla starszych wersji mogą nie pasować. Nazwę w razie wątpliwości sprawdzaj w jarze.

## Warstwa API renderowania
| Nazwa | Znaczenie |
|---|---|
| Blaze3D | Dawna warstwa abstrakcji graficznej gry (pakiet `com.mojang.blaze3d`), opisywana w dokumentacji dla 26.2 |
| Renderpearl | Nowy projekt z 26.3 (`com.mojang.renderpearl`, pakiety `api`, `backend`, `frontend`), do którego przeniesiono API renderowania |

## Pojęcia
| Pojęcie | Znaczenie |
|---|---|
| OpenGL | Starsze API graficzne, dotąd podstawowe w grze |
| Vulkan | Nowsze API graficzne, od 26.2 opcjonalny, eksperymentalny backend |
| SDL3 | Biblioteka okna i wejścia, w 26.3 zastępuje GLFW |
| OIT | Order-independent transparency: przezroczystość niezależna od kolejności rysowania. W 26.3 włącza ją ustawienie Improved Transparency |
| Wavelet OIT | Technika OIT użyta w vanilli, w trzech etapach: granice głębi, transmitancja, akumulacja |
| `OitPipelineSet` | Zestaw potoków dla poszczególnych etapów OIT, podpinany do `RenderType` |
| MultiDrawIndirect | Technika rysowania wielu elementów jednym poleceniem. W 26.3 używana dla terenu na obsługiwanych urządzeniach |
| Shaderc | Kompilator GLSL używany w 26.3 |
| `#include` | Nowa nazwa dyrektywy `#moj_import` w shaderach |
| Shadery core | Wbudowane shadery gry. Mojang oznacza ich nadpisywanie jako niewspierane |
| `/posteffect` | Komenda z 26.3 stosująca shadery post-processingu z paczek zasobów |
| `RenderPipeline` | Opis potoku renderowania (np. `RenderPipelines.GUI_TEXTURED`) |
| Render state | Model, w którym dane gry są najpierw kopiowane do stanu renderowania, a dopiero potem rysowane |
| ARGB | Format koloru z kanałem alfa. W tekście samo RGB daje przezroczysty tekst |
| MSDF | Multi-channel signed distance field: technika ostrych czcionek przy dowolnym skalowaniu |
| TTF | Zwykły format czcionek |
| ClickGUI | Okno ustawień klienta obsługiwane myszą |
| Watermark | Znak wodny klienta wyświetlany na ekranie |
| Liquid Glass | Efekt szkła z rozmyciem tła (nazwa z opisu Render4U) |
| Skija | Binding biblioteki Skia dla JVM. Render4U wymienia ją jako inspirację |
| Loom | Wtyczka Gradle (Fabric Loom) do budowania modów Fabric |
| Chunk | Fragment świata rysowany jako jedna jednostka |
| Mesh | Siatka trójkątów opisująca kształt |
| Draw call | Pojedyncze polecenie „narysuj to” dla GPU |

## Wersje
- **26.2:** wbudowany, opcjonalny Vulkan. Dokumentacja Fabric używana w notatkach dotyczy tej wersji.
- **26.3 (wersja docelowa):** premiera 15 września 2026. SDL3, Renderpearl, OIT, MultiDrawIndirect, paczki danych 121.0 i zasobów 97.1, wymagana Java 25.
- Szczegóły w [render-26-3.md](render-26-3.md) i [srodowisko-26-3.md](srodowisko-26-3.md).
