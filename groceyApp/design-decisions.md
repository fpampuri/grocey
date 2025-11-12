# GroceyApp UI/UX Design Decisions

This log captures each deliberate UI/UX choice made for the mobile app so that the reasoning can be cited later in reports. Update it any time you tweak the experience.

## How to Record New Decisions

Use the template below for every change:

```
### [Feature / Component] — [Date YYYY-MM-DD]
- **Change**: What changed in the UI.
- **User Problem / Goal**: What need or pain point this solves.
- **Justification**: Theory, heuristic, or guideline (cite source/lecture).
- **Impact**: Expected outcome (e.g., easier task completion, faster recognition).
```

Aim to link each justification to the specific theories covered in class (e.g., Nielsen heuristics, Gestalt principles, Norman’s Gulf of Evaluation, material design accessibility rules, etc.).

## Logged Decisions

### Theme & Color System — 2024-11-12
- **Change**: Replaced the default Compose palette with the same primary/secondary colors used by the Vue frontend (brand greens, gold accents, neutral light/dark surfaces).
- **User Problem / Goal**: Maintain cross-platform visual consistency so users instantly recognize the brand regardless of channel.
- **Justification**: Visual consistency supports recognition over recall and strengthens brand trust (Nielsen’s consistency heuristic; Material Design color system guidance on cross-surface coherence).
- **Impact**: Users perceive the Android app as part of the same ecosystem as the web app, reducing cognitive load when switching devices.

### Multilingual Copy for Onboarding Prompt — 2024-11-12
- **Change**: Moved the greeting prompt, inputs, and fallback values into localized string resources with English and Spanish translations.
- **User Problem / Goal**: Support bilingual audiences without forcing them through a single-language onboarding flow.
- **Justification**: ISO 9241-110 “Suitability for the task” plus WCAG 3.1.2 recommend presenting content in the user’s language; localization also aligns with the course’s accessibility guidelines.
- **Impact**: Device locale automatically shows the correct language, reducing translation friction and improving perceived inclusivity.

### Lists Screen Scaffolding — 2024-11-12
- **Change**: Built the Lists screen shell with the search bar, placeholder list cards, right-aligned floating “Add List” FAB, and Pantry/Lists/Products bottom navigation.
- **User Problem / Goal**: Mirror the web IA so mobile users can discover lists, add new ones, or jump to Pantry/Products without relearning navigation.
- **Justification**: Nielsen’s “Consistency and standards” and Material Design navigation guidance recommend persistent bottom bars for top-level destinations plus FABs for primary actions.
- **Impact**: Users immediately see available sections and the main call-to-action, reducing wayfinding time and supporting parity across channels.

### Bottom Nav Ordering Adjustment — 2024-11-12
- **Change**: Reordered the bottom navigation so Pantry sits left, Products in the center, and Lists on the right (matching the latest prototype).
- **User Problem / Goal**: Align muscle memory between mobile and the validated prototype so primary actions appear exactly where users expect them.
- **Justification**: Consistency and spatial mapping principles (Shneiderman’s rules) stress that control placement should remain predictable across surfaces.
- **Impact**: Users avoid relearning tab placement, reducing selection errors when switching from the prototype/web experience to the mobile app.

### Unified FAB & Shared Components — 2024-11-12
- **Change**: Introduced a reusable `PrimaryFab` component and kept it visible across Pantry, Products, and Lists while scoping its behavior per screen; also consolidated the card/search scaffolding so each collection view reuses the same building blocks.
- **User Problem / Goal**: Maintain a stable “add” affordance in the same position so users don’t hunt for the primary action when switching tabs, while reducing implementation drift between screens.
- **Justification**: Fitts’s Law + consistency heuristics recommend keeping high-frequency actions spatially stable; component reuse also supports the course’s maintainability and heuristic inspection guidelines.
- **Impact**: Users always know where to tap to add content, and future enhancements only need to update one component instead of three divergent implementations.

---

Add future decisions in chronological order so the report can reference this single source of truth.
