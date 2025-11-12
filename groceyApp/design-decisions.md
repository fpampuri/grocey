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

---

Add future decisions in chronological order so the report can reference this single source of truth.***
