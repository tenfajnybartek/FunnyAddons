# DO NOT USE THIS, ITS ONLY FOR TESTING ON LOCALHOST SERVER!

# FunnyAddons
Addon do pluginu FunnyGuilds — dodaje per‑członek uprawnienia na terenie gildii oraz wygodne GUI do ich zarządzania.

- Kompatybilność serwera: Paper(testowane na Paper 1.21.4)
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
- GUI konfigurowalne z poziomu `config.yml` (rozmiar, ikony, teksty, sloty).
- Integracja z FunnyGuilds w zakresie wykrywania gildii na terenie (regiony).
- Owner gildii ma domyślnie bypass — właściciel nie jest ograniczany przez per‑member flags.
- Dynamiczne wiadomości (bossbar / actionbar) zależne od relacji: MEMBER / ALLY / ENEMY.
- Funkcja zakupu kordów innej gildii za itemy (konfigurowalne).
- Komenda do wyświetlania wolnych miejsc na gildie (generacja koordynatów).

## Komendy
- /uprawnienia <nick> — otwiera panel uprawnień gildii (dostępne dla graczy z odpowiednimi prawami FG)
- /kupkordy <tag> (alias buycoords) — kupowanie kordów gildii (konfigurowalne w `config.yml`)
- /wolnemiejsce (alias freespace) — lista wolnych miejsc do założenia gildii
- /fgaddonsreload (alias fgareload) — przeładuj config addonu

## Główne założenia działania
- Uprawnienia dotyczą wyłącznie członków gildii — addon kontroluje, co członkowie mogą robić na terenie swojej gildii.
- Obcy gracze (nie‑członkowie) są domyślnie obsługiwani przez FunnyGuilds — addon nie "odblokowuje" eventów wcześniej zablokowanych przez FG.
- UUID jest używany do zapisu uprawnień (zabezpieczenie przed zmianami nicków).

## Konfiguracja GUI uprawnień

GUI uprawnień jest w pełni konfigurowalne z poziomu `config.yml`. Możesz zmienić:
- Pozycje slotów dla przycisków uprawnień
- Nazwy wyświetlane dla każdego uprawnienia
- Ikony (materiały) dla przycisków
- Lore (opisy) dla przycisków
- Prefiksy stanów ON/OFF

### Struktura konfiguracji

```yaml
permissions:
  gui:
    member-permissions-size: 27        # Rozmiar inventory (9, 18, 27, 36, 45, 54)
    members-title: "&cGildia: &e{GUILD} - członkowie"
    member-perms-title: "&cUprawnienia: &e{GUILD} - {NAME}"
    title-max-length: 32
    
    # Sloty dla przycisków uprawnień
    slots:
      break: 10
      place: 11
      interact_block: 12
      open_chest: 14
      open_ender_chest: 15
      use_buckets: 16
      use_fire: 19
      friendly_fire: 21
      back: 26
      info: 13
    
    # Nazwy wyświetlane (obsługują kody kolorów &)
    names:
      break: "&aNiszczenie bloków"
      place: "&aStawianie bloków"
      interact_block: "&aInterakcja z blokami"
      open_chest: "&aOtwieranie skrzyń"
      open_ender_chest: "&aOtwieranie ender chestów"
      use_buckets: "&aUżywanie kubełków"
      use_fire: "&aUżywanie flint & steel"
      friendly_fire: "&aFriendly fire"
      back: "&cPowrót"
      info: "&eKliknij aby ustawić uprawnienia"
    
    # Lore dla przycisków
    lore:
      toggle: "&7Kliknij aby przełączyć"
      info: "&7Kliknij aby ustawić uprawnienia"
    
    # Prefiksy stanów
    state-on: "&a[ON] "
    state-off: "&c[OFF] "

  # Ikony (materiały) dla przycisków
  icons:
    break: DIAMOND_PICKAXE
    place: OAK_PLANKS
    interact_block: LEVER
    open_chest: CHEST
    open_ender_chest: ENDER_CHEST
    use_buckets: WATER_BUCKET
    use_fire: FLINT_AND_STEEL
    friendly_fire: TIPPED_ARROW
    back: BARRIER
    info: PLAYER_HEAD
```

### Placeholdery
- `{GUILD}` — tag gildii
- `{NAME}` — nazwa gracza

### Kody kolorów
Używaj standardowych kodów kolorów Minecraft z `&`:
- `&a` — zielony
- `&c` — czerwony
- `&e` — żółty
- `&7` — szary
- itd.

