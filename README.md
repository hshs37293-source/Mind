# Mind

Baza wiedzy o programowaniu, głównie o tworzeniu modów i klientów do Minecrafta.

## Zasady
- Notatki są pisane własnymi słowami, bez kopiowania cudzych postów i kodu.
- Każda notatka podaje źródło i datę, żeby było wiadomo, jak świeża jest wiedza.
- Skupiamy się na materiałach z 2026 roku.
- **Wersja docelowa: Minecraft 26.3.** Informacje potwierdzone dla 26.3 są oznaczone [26.3], a pochodzące z dokumentacji starszych wersji [26.2] i wymagają weryfikacji.

## Zasady spójności
- Każda notatka podaje, której wersji Minecrafta dotyczy.
- Szczegóły techniczne tematu są w jednej notatce, a pozostałe tylko do niej linkują, żeby nie dublować wiedzy.
- Nazwy i pojęcia są zebrane w [slownik.md](minecraft-client-dev/slownik.md). Nowe pojęcie dodajemy tam.
- Rozbieżności między źródłami są opisane w notatkach, a nie wygładzane. Informacje od autorów wątków są oznaczone jako niezweryfikowane, dopóki nie sprawdzi się ich w kodzie lub dokumentacji.
- Dokumentacja oficjalna (Fabric, Mojang) ma pierwszeństwo przed forami i artykułami.

## Spis
### minecraft-client-dev/
- [slownik.md](minecraft-client-dev/slownik.md): słownik pojęć i nazw oraz wersje, do których odnoszą się notatki.
- [render-26-3.md](minecraft-client-dev/render-26-3.md): rendering w Minecrafcie 26.3 (SDL3, Renderpearl, shadery, OIT, GUI i HUD, Vulkan).
- [srodowisko-26-3.md](minecraft-client-dev/srodowisko-26-3.md): środowisko do modowania 26.3 (Java 25, Gradle, Loom, Fabric API, pułapki portowania).
- [rendering-podstawy.md](minecraft-client-dev/rendering-podstawy.md): czym jest rendering w Minecrafcie, shadery, OpenGL i Vulkan.
- [render4u-kod.md](minecraft-client-dev/render4u-kod.md): analiza kodu Render4U 1.3.2 (przebieg klatki GUI, potoki, shadery SDF, blur, MSDF, batcher, wejście SDL3, Vulkan).
- [render4u.md](minecraft-client-dev/render4u.md): biblioteka renderowania Render4U (Fabric, Vulkan/OpenGL, shadery, blur, czcionki MSDF).
