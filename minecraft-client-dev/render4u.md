# Render4U – biblioteka renderowania dla Minecrafta (Fabric)

Źródło: wątek na forum yougame.biz (post #1 z 26 października 2026), autor wątku: sanderswagboy. Notatka jest streszczeniem własnymi słowami na podstawie zrzutów ekranu wątku, a nie kopią treści.

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
- Nowoczesne klienty i mody coraz częściej wydzielają warstwę renderowania do osobnej biblioteki z własnymi shaderami zamiast używać wyłącznie wbudowanego `DrawContext`.
- Obsługa dwóch backendów (Vulkan i OpenGL) oznacza, że kod rysujący powinien być oddzielony od konkretnego API graficznego.
- Typowy zestaw elementów UI w takich projektach: zaokrąglone prostokąty, cienie, blur, własne czcionki, gotowe okno ustawień (ClickGUI) i znak wodny.

## Do uzupełnienia
- [ ] Struktura kodu i klasy publiczne (wymaga dostępu do repozytorium).
- [ ] Sposób dodania jako zależność do projektu Fabric.
- [ ] Zmiany w wydaniach v1.1.0–v1.3.2.
