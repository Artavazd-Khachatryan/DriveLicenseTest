# Brand assets

| File | Use |
|------|-----|
| `logo.svg` | Source vector (web, docs, future in-app logo) |
| `app-icon-1024.png` | Master app icon (1024×1024) |

**Colors:** primary `#2563EB`, success `#16A34A`, on-primary `#FFFFFF` (see `ui/.../theme/Color.kt`).

**Wired in app:**
- **iOS:** `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png`
- **Android:** adaptive icon via `ic_launcher_background.xml` + `ic_launcher_foreground.xml`

Regenerate Android vectors from `logo.svg` if the mark changes.
