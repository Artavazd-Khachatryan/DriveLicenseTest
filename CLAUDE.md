# Project: Driving License Test App

Kotlin Multiplatform app targeting **Android** and **iOS** using **Compose Multiplatform** with Material Design 3.

## Module Structure

- `composeApp` — KMP app entry point (Android + iOS)
- `ui` — all shared screens and components (`commonMain`)
- `domain` — business logic and repositories
- `database` — SQLDelight persistence
- `androidApp` — Android wrapper

---

## UI/UX Guidelines

### Design System — always use these, never hardcode values

| Token | File | Usage |
|---|---|---|
| Colors | `ui/.../theme/Color.kt` → `AppColors` | Primary `#2563EB`, Secondary `#16A34A`, Error `#DC2626` |
| Typography | `ui/.../theme/Type.kt` → `AppTypography` | displayLarge 48sp → labelMedium 12sp |
| Shapes | `ui/.../theme/Shape.kt` → `AppShapes` | 4dp → 28dp radius tokens |
| Theme | `ui/.../theme/Theme.kt` → `AppTheme` | Wraps Material3, supports dark mode |

### Component Library — reuse before creating new

- `AppScaffold` — top bar + bottom bar + FAB wrapper
- `AppButton` — standard action button
- `AppCard` — card with customizable colors
- `ActionCard` — card with title, description, and action
- `StatChip` — value + label stat display
- `AnswerButton` — answer selection with visual state (default / selected / correct / incorrect)
- `QuestionCard` — question preview with status chip
- `ProgressRing` — animated circular progress, color-coded by score (≥80% primary, ≥50% tertiary, <50% error)

### Layout Rules

- Max content width: **720dp** (enforced on HomeScreen, apply consistently to all screens)
- Use `Modifier.widthIn(max = 720.dp)` + `Modifier.fillMaxWidth()` centered for tablet/landscape safety
- Padding: use multiples of **4dp** (4, 8, 12, 16, 24, 32)
- Avoid hardcoded pixel values; always use `dp`/`sp`

### Navigation

Current flow managed by `MainScreen.kt` via `TestSession` state:
```
home → question → results
     → stats
```
- Navigation is state-driven (no NavController yet) — update `TestSession` to add new routes
- Bottom navigation is defined in `AppScaffold`

### Material Design 3 Principles

- Use `MaterialTheme.colorScheme.*` tokens, not raw `AppColors.*`, inside composables
- Use `MaterialTheme.typography.*` tokens, not raw `AppTypography.*`
- Prefer `Surface`, `Card`, `Button`, `OutlinedButton` from Material3 over custom containers
- Apply `shape = MaterialTheme.shapes.*` on cards and buttons
- Use `tonal elevation` for depth — avoid drop shadows on mobile

### Animations

- Wrap state-driven visibility changes with `AnimatedVisibility`
- Use `animateFloatAsState` / `animateColorAsState` for smooth transitions (answer feedback, progress)
- Keep animation durations: **150ms** for micro (color/scale), **300ms** for layout transitions
- `ProgressRing` already uses `animateFloatAsState` — follow the same pattern

### Dark Mode

- `AppTheme` accepts `darkTheme` parameter — always test both modes
- Never hardcode `Color.White` or `Color.Black` in UI; use `colorScheme.background` / `colorScheme.onBackground`
- Dark color variants are defined in `Color.kt` — add new colors in pairs (light + dark)

### Accessibility

- All interactive elements must have `contentDescription` for screen readers
- Minimum touch target: **48dp × 48dp**
- Color is never the only indicator of state — pair color with icon or label (e.g., `AnswerButton` uses both color and text state)
- Use `semantics {}` modifier for custom components that need accessibility roles

### Platform Considerations (KMP)

- All UI code lives in `commonMain` — no platform-specific UI unless absolutely necessary
- `expect`/`actual` only for platform APIs (file access, haptics, etc.), not for layouts
- Test layouts on both Android (portrait + landscape) and iOS (portrait + landscape)
- Avoid `AndroidView` unless wrapping a native component with no Compose equivalent

### Adding New Screens

1. Create screen file in `ui/src/commonMain/kotlin/.../ui/`
2. Add route to `TestSession.kt`
3. Handle navigation in `MainScreen.kt`
4. Wrap content with `AppScaffold` or `AppTheme` appropriately
5. Reuse components from `components/` before building new ones

### Adding New Components

1. Place in `ui/src/commonMain/kotlin/.../ui/components/`
2. Accept `modifier: Modifier = Modifier` as first parameter
3. Use design tokens (no hardcoded colors/sizes)
4. Preview with `@Preview` annotation (Android preview)
5. Support both light and dark theme in previews
