# Słownik pojęć i nazw

Jedno miejsce na terminy używane w pozostałych notatkach. Dzięki temu te same rzeczy nazywamy tak samo. Jeśli źródła rozchodzą się w nazwach, jest to zapisane tutaj.

## Nazwy klas rysowania GUI (rozbieżne między źródłami)
| Nazwa | Gdzie występuje | Uwagi |
|---|---|---|
| `DrawContext` | starsze wersje, mapowanie Yarn | dawna nazwa klasy do rysowania GUI |
| `GuiGraphics` | mapowanie Mojang, issue o migracji `blit` dla 26.2 | używana w opisie zmiany w 26.2 |
| `GuiGraphicsExtractor` | dokumentacja Fabric dla 26.2 | główna klasa rysowania w tej dokumentacji |

Nie potwierdziłem, czy to jedna klasa pod różnymi nazwami, czy faktyczna zmiana nazwy między wersjami. W notatkach pisz nazwę z dokumentacji danej wersji i sprawdzaj ją w jarze.

## Pojęcia
| Pojęcie | Znaczenie |
|---|---|
| Blaze3D | Warstwa abstrakcji graficznej gry nad OpenGL i Vulkanem. Kod modów ma iść przez nią, a nie przez surowe OpenGL |
| OpenGL | Starsze API graficzne, dotąd podstawowe w grze |
| Vulkan | Nowsze API graficzne, od 26.2 opcjonalny, eksperymentalny backend w grze |
| OIT | Order-independent transparency: sposób rysowania przezroczystości niezależny od kolejności, w 26.3 zastępuje Improved Transparency |
| Shadery core | Wbudowane shadery gry. Mojang oznacza ich nadpisywanie jako niewspierane |
| `RenderPipeline` | Opis potoku renderowania (np. `RenderPipelines.GUI_TEXTURED`) przekazywany do rysowania tekstur |
| Render state | Model, w którym dane gry są najpierw kopiowane do stanu renderowania, a dopiero potem rysowane |
| ARGB | Format koloru z kanałem alfa. W tekście RGB bez alfy daje przezroczysty tekst |
| MSDF | Multi-channel signed distance field: technika ostrych czcionek przy dowolnym skalowaniu |
| TTF | Zwykły format czcionek |
| ClickGUI | Okno ustawień klienta obsługiwane myszą |
| Watermark | Znak wodny klienta wyświetlany na ekranie |
| Liquid Glass | Efekt szkła z rozmyciem tła (nazwa z opisu Render4U) |
| Skija | Binding biblioteki Skia dla JVM. Render4U wymienia ją jako inspirację |
| Chunk | Fragment świata rysowany jako jedna jednostka |
| Mesh | Siatka trójkątów opisująca kształt |
| Draw call | Pojedyncze polecenie „narysuj to” dla GPU |

## Wersje
- **26.2:** wbudowany, opcjonalny Vulkan. Dokumentacja Fabric używana w notatkach dotyczy tej wersji.
- **26.3:** OIT zamiast Improved Transparency. Render4U deklaruje wsparcie od tej wersji.
- Różnice w API między 26.2 a 26.3 nie są zweryfikowane.
