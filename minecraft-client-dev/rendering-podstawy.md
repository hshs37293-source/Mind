# Rendering w Minecrafcie – podstawy

Notatka ogólna, pisana własnymi słowami. Nie pochodzi z jednego źródła, więc szczegóły wersji warto weryfikować w dokumentacji Fabric.

## Czym jest rendering
Rendering to zamiana danych gry (bloki, postacie, interfejs) na obraz na ekranie. Pracę wykonuje karta graficzna (GPU), a gra wysyła jej polecenia, co narysować. Dzieje się to około 60 razy na sekundę lub częściej, a każdy taki obraz to jedna klatka.

## Z czego składa się klatka
- **Świat 3D**: świat jest podzielony na chunki. Z bloków buduje się siatki trójkątów (meshe), nakłada tekstury i oświetlenie, a wynik trafia do GPU.
- **Postacie i przedmioty**: modele złożone z kości i tekstur, rysowane osobno od terenu.
- **Interfejs (UI)**: menu, ekwipunek, pasek życia, czat. To rysowanie 2D na wierzchu obrazu świata.
- **Efekty końcowe**: np. rozmycie, mgła, post-processing.

## Shadery
Shadery to małe programy uruchamiane na GPU. Decydują o wyglądzie: kolor pikseli, cienie, woda, zaokrąglone rogi, rozmycie. Dzięki nim efekty da się liczyć szybko i bez tekstur.

## API graficzne
- **OpenGL**: starszy standard, przez lata podstawowy w Minecrafcie.
- **Vulkan**: nowszy, bliższy sprzętowi, zwykle wydajniejszy, ale bardziej złożony.
- Dobrze napisany kod rysujący oddziela logikę od konkretnego API, żeby działać na obu. Z takiego podejścia korzysta np. Render4U (zob. [render4u.md](render4u.md)).

## Rendering w modach i klientach
Najczęściej dotyczy części 2D, czyli własnych elementów interfejsu. Typowe potrzeby:
- zaokrąglone prostokąty,
- cienie,
- rozmycie tła (np. efekt szkła),
- własne czcionki (TTF, MSDF),
- gotowe okna ustawień i znaki wodne.

Biblioteki renderowania dają to jako gotowe narzędzia, więc autor modu nie pisze własnych shaderów.

## Pojęcia do zapamiętania
| Pojęcie | Znaczenie |
|---|---|
| Klatka (frame) | Jeden narysowany obraz |
| FPS | Liczba klatek na sekundę |
| Chunk | Fragment świata, rysowany jako jedna jednostka |
| Mesh | Siatka trójkątów opisująca kształt |
| Shader | Program działający na GPU |
| Draw call | Pojedyncze polecenie „narysuj to” dla GPU |

## Do uzupełnienia
- [ ] Jak działa potok renderowania w aktualnej wersji Minecrafta.
- [ ] Jak podpiąć własne rysowanie w Fabric (eventy i mixiny).
- [ ] Optymalizacja: liczba draw calli, batching.
