# DO NOT USE THIS, ITS ONLY FOR TESTING ON LOCALHOST SERVER!

# FunnyAddons

> **Uwaga:** projekt jest w trakcie rozwoju, przeznaczony głównie do testów na localhost.

---

## Wymagania

- **Serwer:** Paper (testowane na Paper 1.21.4)
- **FunnyGuilds:** `4.13.1-SNAPSHOT.1759` (kompatybilne z MC 1.8–1.21)
- **JDK:** Java 21 (zgodna z serwerami 1.21.x)

---

## Co dodaje ten plugin

### 1. Per‑członek uprawnienia + GUI

- GUI do zarządzania uprawnieniami członków gildii:
  - lista członków gildii (`GuildMembersGUI`),
  - panel uprawnień dla pojedynczego gracza (toggle ON/OFF).
- Per‑członek uprawnienia oparte na UUID (bezpieczne względem zmiany nicków).
- Rozszerzone typy uprawnień (`PermissionType`):
  - `BREAK` — niszczenie bloków
  - `PLACE` — stawianie bloków
  - `OPEN_CHEST` — otwieranie zwykłych skrzyń
  - `OPEN_ENDER_CHEST` — otwieranie Ender Chest
  - `INTERACT_BLOCK` — używanie przycisków / dźwigni / drzwi / trapdoorów
  - `USE_BUCKETS` — używanie kubełków (wylewanie / zbieranie)
  - `USE_FIRE` — odpalenie (flint & steel)
  - `FRIENDLY_FIRE` — możliwość obrażania (atakowania) członków własnej gildii
- GUI konfigurowalne z poziomu `config.yml` (rozmiar, ikony, teksty, sloty).
- Integracja z FunnyGuilds w zakresie wykrywania gildii na terenie (regiony).
- Owner gildii ma domyślnie bypass — właściciel nie jest ograniczany przez per‑member flags.
- Dynamiczne wiadomości (bossbar / actionbar) zależne od relacji: `MEMBER` / `ALLY` / `ENEMY`.
- Funkcja zakupu kordów innej gildii za itemy (konfigurowalne).
- Komenda do wyświetlania wolnych miejsc na gildie (generacja koordynatów).

### 2. Panel gildii `/panel` / `/gpanel` (leader‑only)

Nowy, rozbudowany panel gildii, otwierany komendą:

- `/panel` (alias `/gpanel`) – **tylko dla lidera gildii**.

Panel jest w pełni konfigurowany z osobnego pliku `panel.yml` i opiera się na istniejącej infrastrukturze GUI (`GUIHolder`, `GUIContext`).

Główne funkcje panelu:

- **Powiększanie terenu gildii**
  - Osobne GUI z poziomami terenu (np. 50×50, 55×55, 60×60, 70×70, 75×75, etc... mozna dodawac wiecej).
  - Poziomy zdefiniowane w `panel.yml`:
    - aktualny poziom → zielony concrete,
    - kolejne poziomy → czerwony concrete,
    - koszt każdego poziomu w itemach (np. `EMERALD_BLOCK: 64`).
  - Po zakupie:
    - pobierane są itemy z eq lidera,
    - teren jest powiększany przez FunnyGuilds (region),
    - poziom jest aktualizowany.

- **Informacje o gildii**
  - Statyczny item w panelu pokazujący:
    - tag, nazwę, punkty, kille, lidera, zastępcę, liczbę członków itd.
  - Brak akcji po kliknięciu (czysto informacyjne GUI).

- **Przedłużenie ważności gildii**
  - Item pokazujący:
    - aktualną datę ważności,
    - o ile przedłużamy,
    - koszt (w itemach) za przedłużenie.
  - Po kliknięciu:
    - sprawdzane są wymagane itemy,
    - przedmioty są pobierane z eq lidera,
    - ważność gildii jest wydłużana poprzez FunnyGuilds.

- **Efekty dla gildii**
  - Kolejne GUI, w którym można wykupić efekty dla wszystkich **online** członków gildii:
    - np. Strength I/II, Speed, Haste I/II, Fire Resistance itd.
  - Każdy efekt:
    - ma slot, materiał, nazwę, typ (`PotionEffectType`), amplifier, czas trwania i koszt w itemach,
    - konfiguracja w `panel.yml`.
  - Po zakupie:
    - itemy są pobierane z eq lidera,
    - efekt jest nadawany członkom gildii na zadany czas.

- **Uprawnienia członków gildii (skrót do istniejącego GUI)**
  - Item w głównym panelu `Uprawnienia członków gildii`.
  - Po kliknięciu:
    - otwierany jest istniejący panel uprawnień:
      - `GuildMembersGUI` → `MemberPermissionsGUI`.
  - Dzięki temu panel jest hubem do zarządzania całą gildią.

---

## Komendy

- `/uprawnienia <nick>`  
  Otwiera panel uprawnień gildii (dostępne dla graczy z odpowiednimi prawami FG / lidera).

- `/kupkordy <tag>` (alias `/buycoords`)  
  Kupowanie kordów gildii (konfigurowalne w `config.yml`).

- `/wolnemiejsce` (alias `/freespace`)  
  Lista wolnych miejsc do założenia gildii.

- `/fgaddonsreload` (alias `/fgareload`)  
  Przeładowanie `config.yml` / `panel.yml`.

- `/panel` (alias `/gpanel`)  
  Otwiera **panel gildii** – GUI tylko dla lidera:
  - powiększanie terenu,
  - informacje,
  - przedłużanie ważności,
  - efekty,
  - skrót do „Uprawnienia członków gildii”.

---

## Główne założenia działania

- Uprawnienia dotyczą wyłącznie członków gildii — addon kontroluje, co członkowie mogą robić na terenie swojej gildii.
- Obcy gracze (nie‑członkowie) są domyślnie obsługiwani przez FunnyGuilds — addon nie „odblokowuje” eventów wcześniej zablokowanych przez FG.
- UUID jest używany do zapisu uprawnień (zabezpieczenie przed zmianami nicków).
- Panel gildii czyta całą konfigurację z `panel.yml`, co pozwala:
  - zmieniać sloty, nazwy, lore, koszty bez rekompilacji,
  - dodawać kolejne poziomy terenu / efekty.

---

