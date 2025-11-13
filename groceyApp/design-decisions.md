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
- **User Problem / Goal**: Maintain a stable "add" affordance in the same position so users don't hunt for the primary action when switching tabs, while reducing implementation drift between screens.
- **Justification**: Fitts's Law + consistency heuristics recommend keeping high-frequency actions spatially stable; component reuse also supports the course's maintainability and heuristic inspection guidelines.
- **Impact**: Users always know where to tap to add content, and future enhancements only need to update one component instead of three divergent implementations.

### Card Component Differentiation — 2024-11-13
- **Change**: Separated card components into three distinct types: `ListCard` (for shopping lists), `CategoryCard` (for Pantry/Products), and `ProductItemCard` (for individual products within lists). Each has unique visual hierarchy and functionality tailored to its specific use case.
- **User Problem / Goal**: Users need to quickly differentiate between collection types (lists vs. categories) and manage different types of content with appropriate controls. Lists require more management options (favorites, sharing, detailed item tracking) while categories need simpler organization.
- **Justification**: Gestalt principles of similarity and proximity — visually distinct cards help users instantly recognize content types. Nielsen's "Recognition rather than recall" heuristic supports providing contextually appropriate controls on each card type. Fitts's Law informs the larger touch targets on ListCard for more frequent interactions.
- **Impact**: Reduced cognitive load when switching between screens; users can immediately identify whether they're managing lists or browsing categories. Clear visual hierarchy supports task-appropriate interactions.

### Enhanced ListCard with Favorites and Actions — 2024-11-13
- **Change**: ListCard features larger size (20dp padding vs 16dp), 32dp icons (vs 24dp), prominent star toggle for favorites, and comprehensive three-dot menu with rename, share, and delete options. All actions are clearly labeled with icons and positioned on the right side for thumb-zone accessibility.
- **User Problem / Goal**: Power users need quick access to list management functions without navigating away. The favorite feature allows prioritizing frequently-used lists. Sharing enables collaboration, a key use case for grocery planning.
- **Justification**: Nielsen's "Flexibility and efficiency of use" — accelerators like favorites and context menus cater to frequent users. Material Design's recommendation to place primary actions in the thumb zone (right side on right-handed devices). Gestalt principle of common region groups related controls together. The star icon leverages universal symbolism for favorites (recognition over recall).
- **Impact**: Users can manage lists efficiently without leaving the main view, reducing navigation steps. Favorites provide quick access to most-used lists, improving task completion time for repeat users.

### ProductItemCard with Quantity Controls — 2024-11-13
- **Change**: Created a dedicated card for individual products featuring: checkbox (mark as bought) on the left, product name and category in a two-line layout, and quantity controls (minus/number/plus) on the right. Visual feedback includes grayed-out text for purchased items and disabled minus button when quantity = 1.
- **User Problem / Goal**: Users need to track what they've bought, see product details at a glance, and adjust quantities while shopping. The layout must work one-handed while pushing a cart and be scannable in busy store environments.
- **Justification**: Norman's Gulf of Execution — checkbox provides clear affordance for marking completion; immediate visual feedback (graying) confirms state change. Gestalt law of proximity places related information (name + category) together. Fitts's Law informs the larger quantity controls (32dp touch targets) for error-free adjustment. Nielsen's "Visibility of system status" — quantity display always shows current state, disabled controls prevent errors.
- **Impact**: Users can efficiently manage their shopping process with minimal errors. The checkbox pattern matches familiar todo-list mental models. Quantity controls reduce the need to delete and re-add items, streamlining the shopping experience.

### Expandable Card Pattern for Contextual Detail — 2024-11-13
- **Change**: Both ListCard and CategoryCard use a chevron that rotates 90° when expanded to reveal nested content. Expanded sections have a subtle green-tinted background (15% opacity of brand green) to visually separate them from the main card. ListCard shows the list name as a header above ProductItemCards; CategoryCard shows simple bullet points.
- **User Problem / Goal**: Users need to see overview information quickly (minimize screen clutter) while having the ability to dive into details when needed. The two-level hierarchy prevents information overload on the main screen.
- **Justification**: Nielsen's "Aesthetic and minimalist design" — show only essential info by default. Progressive disclosure (Norman) reveals complexity only when users need it. The chevron rotation provides continuous feedback about state (Gulf of Evaluation). Gestalt figure-ground principle — the tinted background clearly separates expanded content from the card container. The green tint maintains brand consistency while signaling a different content layer.
- **Impact**: Main screens remain scannable and uncluttered. Users can quickly survey all lists/categories, then expand only relevant items for deeper inspection. The animation provides satisfying feedback and reinforces the cause-effect relationship.

### Consistent Visual Language Across Card Types — 2024-11-13
- **Change**: All cards maintain the brand's 24dp rounded corners, primary color icons, and subtle border (30% opacity). Shared elements (badges, menu dots) use identical styling. However, ListCard is intentionally larger and more prominent than CategoryCard to reflect its higher complexity and user engagement.
- **User Problem / Goal**: Users need to understand the app's structure without explicit instruction, relying on subtle visual cues to differentiate content types while maintaining overall coherence.
- **Justification**: Gestalt law of similarity — consistent styling (corners, colors, borders) signals that all cards are part of the same system. Law of prägnanz (simplicity) — users perceive the overall card system as a unified whole despite functional differences. Nielsen's "Consistency and standards" applied at the component level. Size variation (ListCard 20% larger) provides visual hierarchy without breaking the design language.
- **Impact**: Users intuitively understand the card-based navigation model. Visual consistency reduces learning curve when new features are added. Size differences subtly guide users to more feature-rich components (lists) while maintaining design harmony.

### Bilingual Support for All New Features — 2024-11-13
- **Change**: Added comprehensive string resources in both English and Spanish for all new UI elements: "Rename", "Delete", "Share", "Add to favorites", "Remove from favorites", "More options", "Increase quantity", "Decrease quantity".
- **User Problem / Goal**: Maintain feature parity across languages so bilingual users get the same experience regardless of device locale.
- **Justification**: WCAG 3.1.2 Language of Parts, ISO 9241-110 suitability for internationalization. Nielsen's "Match between system and the real world" — users should see interface text in their preferred language. Consistency with earlier multilingual decisions in the project.
- **Impact**: Spanish-speaking users have equal access to all features without language barriers. Maintaining the bilingual commitment established in earlier development phases builds user trust and broadens potential audience.

### Navigation to Dedicated List Detail Screen — 2024-11-13
- **Change**: Replaced in-card expansion with full-screen navigation. Clicking a ListCard now navigates to a dedicated ListDetailScreen with a top app bar showing the list name and a back button. The detail screen displays ProductItemCards in a scrollable list with proper spacing and an empty state message.
- **User Problem / Goal**: Users need focused, distraction-free environment for managing shopping lists. In-card expansion cluttered the overview screen and made managing multiple products difficult. A dedicated screen provides more space for product cards and reduces accidental taps when scrolling.
- **Justification**: Nielsen's "Aesthetic and minimalist design" — the overview screen remains clean and scannable. Norman's Gulf of Execution — clicking a card clearly indicates "open this list" rather than the ambiguous expand/collapse pattern. Material Design navigation patterns recommend master-detail flows for collection-item relationships. Fitts's Law — larger touch targets for products in dedicated screen reduce errors. Gestalt law of common fate — all products in a list move together as a cohesive unit when navigating.
- **Impact**: Users can quickly scan all lists on the overview, then dive deep into a specific list without UI clutter. The back button provides clear navigation hierarchy (Nielsen's "User control and freedom"). Dedicated screen allows future features like list-specific filters, sorting, or bulk actions without overcrowding the main screen. Better supports one-handed use while shopping.

---

Add future decisions in chronological order so the report can reference this single source of truth.

