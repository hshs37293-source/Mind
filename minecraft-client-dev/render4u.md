# Render4U – biblioteka renderowania dla Minecrafta (Fabric)

Źródło: wątek na forum yougame.biz (post #1 z 26 października 2026), autor wątku: sanderswagboy. Notatka jest streszczeniem własnymi słowami na podstawie zrzutów ekranu wątku, a nie kopią treści.

- **Wersja Minecrafta:** Fabric 26.3 i nowsze (deklaracja autora).
- **Status informacji:** opis funkcji pochodzi od autora biblioteki i nie był sprawdzany w kodzie.
- **Powiązane:** kontekst techniczny 26.x (Vulkan, OIT, API rysowania GUI) jest w [render-26-3.md](render-26-3.md), a pojęcia w [slownik.md](slownik.md).

## Czym jest
- Wydajny, lekki i nowoczesny system renderowania 2D/UI dla Minecrafta na Fabric (w opisie wersja 26.3 i nowsze).
- Natywne wsparcie dla dwóch backendów graficznych: **Vulkan** i **OpenGL**.
- Autor wskazuje, że bibliotekę zainspirowały rozwiązania z **Skija** (binding Skia dla JVM).
- Cel: pokryć wszystkie potrzeby wizualne klientów i modów, żeby nie pisać własnego renderera od zera.

## Funkcje wymienione w opisie
- Zaokrąglanie przy pomocy shaderów (zaokrąglone prostokąty bez tekstur).
- Konfigurowalne cienie.
- Rozmycie typu **Liquid Glass** (efekt szkła z blurem tła).
- Czcionki **MSDF** i **TTF** (MSDF = multi-channel signed distance field, ostre litery przy dowolnym skalowaniu).
- Gotowy **ClickGUI** i **Watermark**.

## Dodatkowe informacje od autora
- Renderer powstał przy ograniczonym czasie, więc mogą występować błędy. Autor prosi o zgłaszanie ich w Issues na GitHubie.
- Poprawki można wysyłać jako pull request do gałęzi głównej.
- Projekt ma być aktualizowany i przenoszony na kolejne wersje Minecrafta, zależnie od feedbacku.
- Repozytorium: Sanderovich2/Render4U na GitHubie. Do wątku dołączono też wideo z demonstracją.

## Historia aktualizacji (wg wątku)
| Data | Wersja |
|---|---|
| 2026-10-26 (23:00) | v1.1.0 |
| 2026-10-27 | v1.2.0 |
| 2026-10-31 | v1.3.0 |
| 2026-10-31 (19:47) | v1.3.1 |
| 2026-11-07 | v1.3.2 |

Szczegółowe listy zmian są w sekcji Releases repozytorium na GitHubie. Nie udało mi się ich pobrać, więc nie są tu opisane.

## Wnioski do nauki
- Takie biblioteki wydzielają warstwę renderowania z własnymi shaderami zamiast używać wyłącznie wbudowanego API rysowania GUI (w 26.x opisanego w dokumentacji Fabric jako `GuiGraphicsExtractor`, zob. [slownik.md](slownik.md)).
- Obsługa dwóch backendów (Vulkan i OpenGL) pasuje do tego, że od 26.2 gra ma opcjonalny backend Vulkan obok OpenGL, a kod powinien iść przez warstwę Blaze3D (zob. [render-26-3.md](render-26-3.md)).
- Własne shadery (zaokrąglenia, cienie, blur) są wrażliwe na zmiany w shaderach core. Mojang zaznacza, że ich nadpisywanie jest niewspierane, więc po każdej aktualizacji Minecrafta trzeba je sprawdzać.
- Typowy zestaw elementów UI w takich projektach: zaokrąglone prostokąty, cienie, blur, własne czcionki, gotowe okno ustawień (ClickGUI) i znak wodny.

## Linki (ze zrzutów wątku)
- Repozytorium: https://github.com/Sanderovich2/Render4U
- Wideo z demonstracją: https://imgur.com/a/HicixYw
- Wydania:
  - https://github.com/Sanderovich2/Render4U/releases/tag/v1.1.0
  - https://github.com/Sanderovich2/Render4U/releases/tag/v1.2.0
  - https://github.com/Sanderovich2/Render4U/releases/tag/v1.3.0
  - https://github.com/Sanderovich2/Render4U/releases/tag/v1.3.1
  - https://github.com/Sanderovich2/Render4U/releases/tag/v1.3.2

## Do uzupełnienia
- [ ] Struktura kodu i klasy publiczne (wymaga dostępu do repozytorium).
- [ ] Sposób dodania jako zależność do projektu Fabric.
- [ ] Zmiany w wydaniach v1.1.0–v1.3.2.
