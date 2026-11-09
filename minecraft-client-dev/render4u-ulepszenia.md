# Render4U 1.3.2: co jest dobre, co słabe i jak to ulepszyć

Ocena oparta na czytaniu kodu z archiwum 1.3.2 (zob. [render4u-kod.md](render4u-kod.md)). Nie budowałem ani nie uruchamiałem projektu, więc to przegląd kodu, nie wyniki pomiarów. Przy każdym punkcie jest dowód i stopień pewności.

**Czego nie zrobiłem:** nie zmieniałem kodu Render4U. To cudzy projekt (licencja MIT), a nie mam jego repozytorium ani działającego buildu 26.3. Poprawki, które dało się zrobić niezależnie od Minecrafta, są w katalogu [przyklady/java](../przyklady/java).

## Co jest dobrze zrobione (warto zachować i kopiować)
- Własne shadery w osobnej przestrzeni nazw, bez nadpisywania shaderów core.
- Elementy GUI jako `GuiElementRenderState` dodawane do kolejki gry, więc działają z przycinaniem i warstwami.
- Blur zgłaszany z góry, liczony raz na klatkę i przycinany do potrzebnego obszaru.
- Batcher zachowujący kolejność nakładania, z pulą obiektów i pomijaniem elementów poza ekranem.
- Parametry elementu upakowane w drugi zestaw UV zamiast dodatkowych uniformów.
- Atomowy zapis konfiguracji (plik tymczasowy i przeniesienie, z obejściem, gdy system nie obsługuje przeniesienia atomowego) w `ConfigManager`.
- Obejście dla Vulkana przy czyszczeniu cache potoków.

## Słabe punkty (zweryfikowane w kodzie)

| # | Problem | Dowód | Proponowana poprawka | Priorytet |
|---|---|---|---|---|
| 1 | Brak testów i CI | W projekcie nie ma żadnych plików testowych ani katalogu `.github` | Testy czystej logiki (kolory, gradienty, easing, upakowanie promieni, obliczanie obszaru przycinania blura, dopasowanie klawiszy) i prosty workflow budujący projekt | Wysoki |
| 2 | Połykanie wyjątków | 25 bloków `catch (Throwable)` lub `catch (Exception)` w 14 plikach. Część to `ignored` (m.in. `VulkanHelper` trzy razy, `Blur.toScreen`, `ConfigManager` linia 37) | Logować błąd raz na rodzaj, nie ignorować po cichu. Przykład w [GraphicsBackend.java](../przyklady/java/dev/mind/render/GraphicsBackend.java) | Wysoki |
| 3 | `Blur.close()` nigdy nie jest wywoływane | Wyszukiwanie po całym kodzie nie znajduje żadnego wywołania ani odwołania metodą. `GuiRendererMixin` woła tylko `Blur.endFrame()` (czyści listę próśb) | Wywołać `Blur.close()` przy zamykaniu klienta (np. zdarzenie `ClientLifecycleEvents.CLIENT_STOPPING` z Fabric API) i po przeładowaniu zasobów. Nie sprawdzałem w 26.3, kiedy dokładnie wywoływane jest `GuiRenderer#close` | Średni |
| 4 | Magiczne liczby w klawiszach | `KeyUtil.isKeyMatch` używa 344, 229, 340, 225 bez nazw, a porównuje liczby z różnych przestrzeni kodów (GLFW i SDL), więc możliwe są fałszywe dopasowania | Nazwane stałe i symetryczne dopasowanie dla Shift, Ctrl i Alt: [ModifierKeys.java](../przyklady/java/dev/mind/keys/ModifierKeys.java), test przechodzi (13 przypadków). Docelowo zapisywać przypisania w jednej przestrzeni kodów, po sprawdzeniu w jarze, co zwraca `InputConstants.KEY_*` w 26.3 | Średni |
| 5 | Przeciążenia `Draw.blur` różnią się tylko typem (`int` siła kontra `float` przezroczystość) | `Draw.java`, cztery przeciążenia `blur(...)` | Nazwane metody (`blurStrength`, `blurAlpha`) albo wyłącznie builder `Blur.of(...)`, żeby nie pomylić argumentów | Niski |
| 6 | Globalny stan statyczny bez zabezpieczenia | `Blur.REQUESTED`, tablica `Blur.SCISSOR`, listy w `DisplayBatcher` zakładają jeden wątek renderowania, nigdzie tego nie sprawdzając | Dopisać komentarz i asercję wątku renderowania (nazwę metody sprawdzić w 26.3) | Niski |
| 7 | Mixiny wymagane twardo | `render4u.mixins.json`: `"required": true` i `defaultRequire: 1` | Zostawić twardo tylko krytyczne (`Hud`, `GuiRenderer`), a poboczne (np. `Window` do ikony, `ShaderManager`) jako opcjonalne z logiem, żeby zmiana w grze nie zatrzymywała startu. Wpływ nie zbadany | Niski |
| 8 | Wersja Minecrafta na sztywno | `fabric.mod.json` wymaga dokładnie `26.3`, a opis wątku mówi „26.3+” | Po przetestowaniu ustawić zakres wersji albo poprawić opis | Niski |
| 9 | Zegar ścienny w animacjach | 24 użycia `System.currentTimeMillis()` lub `nanoTime()` w 10 plikach, m.in. `Draw.flow*` (cykl 2600 ms) | Dla animacji używać zegara monotonicznego (np. `nanoTime`) | Niski |
| 10 | Stała skala 2.0 | `DisplayUtil.SCALE = 2.0F` i jej użycia w `Blur` (jako zapas) | Wyprowadzić ze skali interfejsu (`uiScale` jest w konfiguracji) i trzymać w jednym miejscu | Niski |
| 11 | Dokumentacja | README wyłącznie po rosyjsku, brak Javadoc, w `docs` są tylko zrzuty ekranu | Wersja angielska README i krótkie Javadoc dla publicznego API (`Draw`, `Blur`, `Pipelines`) | Niski |

## Do sprawdzenia (nie umiem ocenić z samego czytania)
- Rozmiar jara i czas ładowania: w zasobach jest 1373 plików JSON (m.in. dane kosmetyków) i 696 plików PNG. Nie wiem, czy są używane ani ile ważą po spakowaniu.
- Wydajność blura i batchera w praktyce: brak pomiarów. Potrzebny profil w grze (np. liczniki batchy z `DisplayBatcher` i czas klatki z `DisplayUtil.lastFrameMs`).
- Czy mixiny trafiają w te same metody w kolejnych wersjach 26.3.x.

## Co dodałem
- [ModifierKeys.java](../przyklady/java/dev/mind/keys/ModifierKeys.java) i [ModifierKeysTest.java](../przyklady/java/dev/mind/keys/ModifierKeysTest.java): klasa bez zależności od Minecrafta, **przetestowana** (kompilacja bez ostrzeżeń na Javie 21, wszystkie testy przeszły). Uruchomienie: `javac -d out dev/mind/keys/*.java` i `java -cp out dev.mind.keys.ModifierKeysTest` z katalogu `przyklady/java`.
- [GraphicsBackend.java](../przyklady/java/dev/mind/render/GraphicsBackend.java): **nieprzetestowana**, bo zależy od klas Minecrafta, których tu nie mam. Nazwy klas pochodzą z kodu Render4U.

## Kolejność wdrażania (propozycja)
1. Testy i CI (punkt 1), bo dają bezpieczeństwo przy pozostałych zmianach.
2. Logowanie zamiast ignorowania wyjątków (punkt 2).
3. Zwalnianie celów blura (punkt 3).
4. Klawisze (punkt 4).
5. Reszta według potrzeby.

## Dalsze kroki, jeśli chcesz wdrożyć to w kodzie
Najlepiej zrobić fork Render4U (licencja MIT pozwala, trzeba zachować informację o prawach autorskich) i tam wprowadzać poprawki. Do budowy 26.3 potrzebny jest dostęp do sieci (Gradle pobiera Minecrafta), którego w tej sesji nie mam, więc zmiany wymagające kompilacji z Minecraftem trzeba by testować u Ciebie lokalnie.
