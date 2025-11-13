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

### Functional Search with Brand-Colored Input — 2024-11-13
- **Change**: Updated search bar across all screens (Lists, Pantry, Products) with rounded corners (16dp), light green background color (20% opacity of brand green), and real-time filtering functionality. As users type, the displayed items filter to show only matches based on title. The search icon uses the primary brand green color for visual consistency.
- **User Problem / Goal**: Users with large collections need to quickly locate specific lists, pantry items, or products without scrolling through entire collections. The search must feel responsive and integrated with the app's visual identity rather than generic.
- **Justification**: Nielsen's "Recognition rather than recall" — search reduces memory load by letting users type what they remember. Real-time filtering provides immediate feedback (visibility of system status heuristic). The brand-colored background strengthens visual identity and differentiates the search field from other inputs (Gestalt similarity principle). Rounded corners match the card design language (consistency heuristic). The 20% opacity provides sufficient contrast for readability while maintaining the gentle, approachable aesthetic. Case-insensitive matching follows user expectations (match between system and real world).
- **Impact**: Users can instantly find items in large collections, reducing task time significantly. The branded search field reinforces the app's identity and creates visual harmony across screens. Real-time feedback makes the search feel responsive and modern. Consistent implementation across all three screens reduces learning curve—users only need to learn the search pattern once.

### Deep Search for Nested Products in Categories — 2024-11-13
- **Change**: Enhanced search functionality in Pantry and Products screens to match not only category titles but also individual products within categories. For example, searching "Br" will show the "General" category if it contains "Bread", even though "General" doesn't contain "Br". The matching logic uses OR conditions: show category if title matches OR any product within matches.
- **User Problem / Goal**: Users often remember specific product names but not which category contains them. Searching only by category name would miss relevant results, forcing users to expand multiple categories manually to find what they need.
- **Justification**: Information foraging theory — users follow "information scent" by searching for specific items they remember. Deep search increases scent strength by surfacing categories containing matching products. Nielsen's "Flexibility and efficiency of use" — accommodates both novice users (who might search by category) and experts (who search by specific product). Reduces cognitive load by eliminating the need to remember organizational structure. Gestalt principle of closure — users see the category as containing the answer even before expanding it.
- **Impact**: Dramatically reduces search time for users looking for specific products. Eliminates frustration of "I know we have it, but where?" scenarios. Users can quickly verify product availability without memorizing the categorization scheme. Particularly valuable for shared lists where one user organized items and another is searching.

### Persistent Navigation in List Detail View — 2024-11-13
- **Change**: Added floating action button (FAB) and bottom navigation bar to the ListDetailScreen. The FAB shows "Add Product" and the bottom bar displays all three tabs (Pantry, Products, Lists) with the current tab highlighted. Switching tabs from the detail view returns to the main overview of the selected tab, closing the detail screen.
- **User Problem / Goal**: Users working within a list may need to quickly switch contexts (check pantry, browse products, switch lists) or add products without backing out multiple levels. The original design trapped users in the detail view with only a back button, creating a navigation dead end.
- **Justification**: Nielsen's "User control and freedom" — users can escape the current context easily via multiple routes. Material Design navigation patterns recommend maintaining primary navigation (bottom bar) even in child screens to prevent disorientation. Fitts's Law — the FAB provides a large, consistent target for the primary action (adding products) in the same position as other screens. Consistency heuristic — maintaining bottom nav across all screens creates predictable navigation model. The tab-switching behavior (closing detail and returning to overview) follows the principle of least surprise — users expect tab changes to show that tab's main view, not nested content from a different tab.
- **Impact**: Users never feel "stuck" in the detail view. Quick access to add products streamlines the shopping list creation workflow. Maintaining bottom navigation preserves spatial memory and reduces cognitive load. Switching tabs provides a quick escape without needing to understand the navigation hierarchy (back → lists → tab → destination). Supports multi-tasking workflows like "add to list while checking pantry."

---

### Componentize top search row — 2025-11-13
- **Change**: Extracted the in-file `SearchRow` into a shared component `TopSearchBar` located at `ui/components/TopSearchBar.kt`, and replaced the previous private implementation in `CollectionsScreens.kt` to use the component across Lists, Pantry and Products screens.
- **User Problem / Goal**: Reduce duplicated UI code and centralize styling/behavior so any change to the top search bar (visual, accessibility, or interaction) updates all collection screens consistently.
- **Justification**: Component reuse supports maintainability and consistency (Nielsen's "Consistency and standards") and reduces the chance of divergence between related screens. Centralization also simplifies future accessibility improvements and theming changes.
- **Impact**: All three collection screens now share the exact same search bar implementation, reducing maintenance burden and ensuring consistent behavior (search icon, placeholder handling, container color). This is a low-risk refactor confined to UI components and screen wiring.

### Integrate filter control into search field — 2025-11-13
- **Change**: Added an option to render the filter control inside the search `TextField` as a trailing icon (`showFilterInside` flag). Collections screens enable this so the filter appears within the input instead of as a separate right-hand button.
- **User Problem / Goal**: Reduce horizontal clutter and visually associate the filter action with the search input, making its purpose clearer and saving horizontal space on small screens.
- **Justification**: Gestalt principles of proximity and common region recommend placing related controls visually close; putting the filter inside the input strengthens the perceived association between search and filtering. Material guidelines accept inline affordances (trailing icons) for related quick actions.
- **Impact**: The UI becomes slightly tighter and more integrated; users will perceive the filter as part of search controls. Accessibility is preserved by keeping content descriptions and using a tappable IconButton anchor. The change is reversible via the `showFilterInside` flag for screens that need a separate control.

### Menu icon standardization — 2025-11-13
- **Change**: Replaced the three-dot `MoreVert` icon used in the left position of the collection search row with a hamburger `Menu` icon so the left control consistently indicates opening the navigation drawer.
- **User Problem / Goal**: Improve affordance clarity: a hamburger icon more clearly communicates "open navigation drawer" than a vertical overflow menu in the left-side, reducing confusion.
- **Justification**: Nielsen's heuristics on match between system and the real world and consistency recommend using platform-expected metaphors for common navigation affordances. The hamburger icon is a widely-recognized pattern for opening a navigation drawer.
- **Impact**: The left button on Lists/Pantry/Products now visually matches the expected drawer affordance. Existing three-dot menus used for per-card or per-item overflow remain unchanged (they still use `MoreVert`) to preserve their semantic meaning.

Add future decisions in chronological order so the report can reference this single source of truth.

### Local interactive product state (temporary) — 2025-11-13
- **Change**: Implemented ephemeral local state in `ListDetailScreen` so ProductItemCards immediately reflect user actions: checking an item marks it bought (grays + strikethrough), quantity can be incremented/decremented and edited manually via a small numeric input.
- **User Problem / Goal**: Users expect immediate visual feedback when marking items bought or changing quantities while shopping. Waiting for a network round-trip would make the UI feel sluggish.
- **Justification**: Nielsen's "Visibility of system status" and "Response time" heuristics — immediate local updates maintain perceived performance. Architectural pragmatism: keep UI responsive while preparing an explicit migration to a single source of truth (ViewModel + Repository) to avoid later refactors.
- **Impact**: Better perceived responsiveness and smoother UX while the backend is integrated. Implementation uses a local mutable list copied from the passed model for now; important persistence points are annotated with "API CHANGE" comments so migrating to ViewModel/Repository is straightforward.


### Physical reorganization: card components folder — 2025-11-13
- **Change**: Moved concrete card components into a physical subfolder `app/src/main/java/.../ui/components/cards/` (files: `ListCard.kt`, `CategoryCard.kt`, `ProductItemCard.kt`, `SharedBadge.kt`) while keeping the Kotlin package `com.example.groceyapp.ui.components` unchanged.
- **User Problem / Goal**: Improve filesystem organization for maintainability while preserving import paths so callers don't need immediate changes.
- **Justification**: Physical grouping of related components reduces cognitive overhead when navigating the codebase. Keeping the package stable avoids large-scale import churn and reduces the migration surface (safer refactor).
- **Impact**: Developers can now find card implementations under `ui/components/cards/`. The original files in `ui/components/` were left as small placeholders to avoid breaking imports during the transition. This is a low-risk refactor and is recorded here for traceability.

### Create List Dialog — Icon parity with web (MDI mapping) — 2025-11-13
- **Change**: While adding the Create List dialog and its icon picker, we intentionally preserved the web app's icon language by mapping the original MDI icon choices to the closest available Android Compose Material icons in the same order when possible. Exact matches were substituted with visually similar Material icons and all substitutions are documented in the code. A compilation-safe fallback set was applied so the feature could be delivered quickly; TODOs mark where exact SVG/vector assets may replace substitutes later.
- **User Problem / Goal**: Keep visual continuity between web and mobile so users instantly recognize familiar icons (cart, bread, coffee, etc.). This reduces friction when users switch platforms and supports recognition-based navigation.
- **Justification**: Cross-platform consistency is a strong usability heuristic (Nielsen: consistency and standards; recognition over recall). Reusing platform-native Material icons avoids shipping custom assets prematurely and respects platform conventions while still honoring the original web vocabulary.
- **Impact**: Users will see icons that are the same or very similar to the web product, improving recognition and reducing cognitive load. The codebase documents substitutions and contains TODOs for later replacement by exact MDI SVGs if full visual parity is required.

