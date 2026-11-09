# Środowisko do modowania Minecrafta 26.3 (Fabric)

Zebrane 9 listopada 2026, własnymi słowami. Wersje narzędzi szybko się zmieniają, więc przed startem projektu sprawdź aktualne wartości w szablonie moda Fabric i na stronie Fabric. Rendering opisuje [render-26-3.md](render-26-3.md), a pojęcia [slownik.md](slownik.md).

## Wymagania
- **Java 25** (wymagana przez Minecrafta 26.3).
- **Gradle:** artykuł o porcie moda podaje 9.7.1. Wpis Fabric z 15 września wskazywał 9.6.0 (aktualną w dniu publikacji). Ten sam artykuł ostrzega, że plik `gradle-9.7-bin.zip` nie istnieje, a Gradle 9.5.1 kończy się błędem „No matching variant”.
- **Fabric Loom:** artykuł o porcie podaje 1.18-SNAPSHOT, wpis Fabric 1.17.
- **Fabric Loader:** wpis Fabric z 15 września podawał 0.19.5 jako najnowszy stabilny.
- **Fabric API:** artykuł o porcie podaje 0.161.0+26.3. Na listach wydań istnieją nowsze budowy (np. 0.165.0+26.3), więc bierz najnowszą dla 26.3.

Rozbieżności wynikają z dat publikacji źródeł. Bezpiecznie jest skopiować wersje z aktualnego szablonu Fabric.

**Konfiguracja z prawdziwego projektu pod 26.3 (Render4U 1.3.2, z pliku `gradle.properties` i wrappera):** Minecraft `26.3`, Fabric Loader 0.20.1, Loom 1.17.21 (identyfikator wtyczki `net.fabricmc.fabric-loom`), Fabric API 0.165.0+26.3, Gradle 9.6.0, Java 25. W zależności `minecraft` nie ma wiersza z mapowaniami. Nie budowałem tego projektu, więc to konfiguracja zadeklarowana, nie potwierdzona buildem. Szczegóły w [render4u-kod.md](render4u-kod.md).

## Mapowania
- Wpis Fabric mówi, że 26.3 używa nazw z niezaciemnionej gry. Nazwy z dokumentacji dla starszych wersji (np. z mapowań Yarn) mogą więc nie pasować.
- W starszych gałęziach (1.21.10) artykuł wspomina o `loom.officialMojangMappings()`. Konfigurację mapowań sprawdź w swoim buildzie.

## Zmiany w Fabric API w 26.3 (wpis Fabric)
- Usunięto rejestry kompostowania, paliw, warzenia oraz ogołacania, orania i spłaszczania. Zastąpiły je systemy oparte na danych vanilli.
- Nowe lub zmienione: API płynów, callback przepływu płynu, rozszerzenie `TooltipFlag`, dostawca mocy zaklęć.
- Wpis nie opisuje szczegółowo zmian renderowania i odsyła do poradnika NeoForged.

## Pułapki przy portowaniu (artykuł o porcie moda)
- Pracuj na kopii projektu, a oryginał zostaw jako zapasowy.
- Zmień wersje w `gradle.properties` i wersję Gradle w wrapperze, a potem przeczytaj pełną listę błędów kompilacji. W opisanym projekcie 56 błędów składało się z dziesięciu powtarzalnych wzorców, więc łatwiej naprawiać wzorcami niż plik po pliku.
- Kompilator nie wyłapie wszystkiego: wyniki loot tabel, cele mixinów i ścieżki zasobów trzeba sprawdzić ręcznie w kliencie deweloperskim.
- Przykłady zmian: `EntityType.VILLAGER` → `EntityTypes.VILLAGER`, `state.blocksMotion()` → `state.isSolid()`, `Blocks.WHITE_CARPET` → `Blocks.CARPET.white()`, `hurtMarked` usunięto (gracze wymagają ręcznie wysłanego `ClientboundSetEntityMotionPacket`).
- `SignText` buduje się z dwóch list po cztery komponenty, a `setText` przyjmuje `SignTextSlot`. Błędna długość listy daje puste linie w grze, a nie błąd kompilacji.
- `ConstantValue` przeniesiono do `providers.number.ints` i trzeba go opakować w `Holder.direct(...)`.
- `displayItemActivation` przeniesiono z renderera gry do gracza.
- Mixiny klienckie przy rozdzielonych zestawach źródeł wymagają wpisu `"environment": "client"` w `fabric.mod.json`, inaczej serwer deweloperski się wywraca.
- Według tego artykułu `GuiGraphicsExtractor`, `HudElementRegistry` i architektura entity render state nie zmieniły się w sposób łamiący.

## Inne zmiany wydania 26.3
- Paczki: dane 121.0, zasoby 97.1.
- Serwer: właściwość `white-list` ma domyślnie wartość `true`, a część logów zastąpiło Minecraft Server Management Protocol 3.1.0.

## Źródła
- Fabric dla 26.3: https://fabricmc.net/2026/09/15/263.html
- Port moda Fabric do 26.3: https://spicesfire.com/blog/porting-a-fabric-mod-to-minecraft-26-3/
- Co zmienia 26.3 dla modów, shaderów i paczek: https://minecrafthub.io/articles/minecraft-26-3-mods-shaders-packs
- Opis wydania 26.3: https://www.minecraft.net/en-us/article/minecraft-java-edition-26-3
