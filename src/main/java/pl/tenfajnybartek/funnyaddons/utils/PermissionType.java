package pl.tenfajnybartek.funnyaddons.utils;

public enum PermissionType {
    BREAK,            // niszczenie bloków
    PLACE,            // stawianie bloków
    OPEN_CHEST,       // otwieranie skrzyń (zwykłych)
    OPEN_ENDER_CHEST, // otwieranie enderchesta
    FRIENDLY_FIRE,    // możliwość obrażania członków własnej gildii (domyślnie wyłączone)
    INTERACT_BLOCK,   // używanie elementów interaktywnych: przyciski, dźwignie, drzwi
    USE_BUCKETS,      // używanie kubełków (wylewanie / zbieranie wody/lawy)
    USE_FIRE          // odpalenie pochodni / użycie flinta i stali / zapalanie bloków
}
