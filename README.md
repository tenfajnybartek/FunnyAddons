# FunnyAddons (DO NOT USE THIS, ITS ONLY FOR TESTING ON LOCALHOST SERVER!)
Addon do pluginu FunnyGuilds — dodaje per‑członek uprawnienia na terenie gildii oraz wygodne GUI do ich zarządzania.

- Kompatybilność serwera: Paper/Spigot 1.21.4 (testowane na Paper 1.21.4)
- Wymagany FunnyGuilds: **4.13.1-SNAPSHOT.1759** (kompatybilne z MC 1.8–1.21)
- JDK: Java 21 (zgodna z serwerami 1.21.x)

## Co dodaje ten plugin
- GUI do zarządzania uprawnieniami członków gildii:
  - lista członków gildii,
  - panel uprawnień dla pojedynczego gracza (toggle ON/OFF).
- Per‑członek uprawnienia oparte na UUID (bezpieczne względem zmiany nicków).
- Rozszerzone typy uprawnień:
  - BREAK — niszczenie bloków
  - PLACE — stawianie bloków
  - OPEN_CHEST — otwieranie zwykłych skrzyń
  - OPEN_ENDER_CHEST — otwieranie Ender Chest
  - INTERACT_BLOCK — używanie przycisków / dźwigni / drzwi / trapdoorów
  - USE_BUCKETS — używanie kubełków (wylewanie / zbieranie)
  - USE_FIRE — odpalenie (flint & steel)
  - FRIENDLY_FIRE — możliwość obrażania (atakowania) członków własnej gildii
- GUI konfigurowalne z poziomu `config.yml` (rozmiar, ikony, teksty).
- Integracja z FunnyGuilds w zakresie wykrywania gildii na terenie (regiony).
- Owner gildii ma domyślnie bypass — właściciel nie jest ograniczany przez per‑member flags.
- Dynamiczne wiadomości (bossbar / actionbar) zależne od relacji: MEMBER / ALLY / ENEMY.
- Funkcja zakupu kordów innej gildii za itemy (konfigurowalne).
- Komenda do wyświetlania wolnych miejsc na gildie (generacja koordynatów).

## Komendy
- /uprawnienia — otwiera panel uprawnień gildii (dostępne dla graczy z odpowiednimi prawami FG)
- /kupkordy (alias buycoords) — kupowanie kordów gildii (konfigurowalne w `config.yml`)
- /wolnemiejsce (alias freespace) — lista wolnych miejsc do założenia gildii
- /fgaddonsreload (alias fgareload) — przeładuj config addonu

## Główne założenia działania
- Uprawnienia dotyczą wyłącznie członków gildii — addon kontroluje, co członkowie mogą robić na terenie swojej gildii.
- Obcy gracze (nie‑członkowie) są domyślnie obsługiwani przez FunnyGuilds — addon nie "odblokowuje" eventów wcześniej zablokowanych przez FG.
- UUID jest używany do zapisu uprawnień (zabezpieczenie przed zmianami nicków).

