---
name: Apex Silicon
colors:
  surface: '#f7f9fb'
  surface-dim: '#d8dadc'
  surface-bright: '#f7f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f6'
  surface-container: '#eceef0'
  surface-container-high: '#e6e8ea'
  surface-container-highest: '#e0e3e5'
  on-surface: '#191c1e'
  on-surface-variant: '#414753'
  inverse-surface: '#2d3133'
  inverse-on-surface: '#eff1f3'
  outline: '#727784'
  outline-variant: '#c1c6d5'
  surface-tint: '#005cba'
  primary: '#004e9f'
  on-primary: '#ffffff'
  primary-container: '#0066cc'
  on-primary-container: '#dfe8ff'
  inverse-primary: '#aac7ff'
  secondary: '#00677f'
  on-secondary: '#ffffff'
  secondary-container: '#00d2ff'
  on-secondary-container: '#00566a'
  tertiary: '#485066'
  on-tertiary: '#ffffff'
  tertiary-container: '#60687f'
  on-tertiary-container: '#e2e8ff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d7e3ff'
  primary-fixed-dim: '#aac7ff'
  on-primary-fixed: '#001b3e'
  on-primary-fixed-variant: '#00458e'
  secondary-fixed: '#b6ebff'
  secondary-fixed-dim: '#47d6ff'
  on-secondary-fixed: '#001f28'
  on-secondary-fixed-variant: '#004e60'
  tertiary-fixed: '#dae2fd'
  tertiary-fixed-dim: '#bec6e0'
  on-tertiary-fixed: '#131b2e'
  on-tertiary-fixed-variant: '#3f465c'
  background: '#f7f9fb'
  on-background: '#191c1e'
  surface-variant: '#e0e3e5'
typography:
  display:
    fontFamily: Space Grotesk
    fontSize: 56px
    fontWeight: '700'
    lineHeight: 64px
    letterSpacing: -0.03em
  display-mobile:
    fontFamily: Space Grotesk
    fontSize: 38px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 36px
    fontWeight: '600'
    lineHeight: 44px
    letterSpacing: -0.025em
  headline-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Space Grotesk
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.015em
  headline-sm:
    fontFamily: Space Grotesk
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Geist
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
    letterSpacing: -0.01em
  body-md:
    fontFamily: Geist
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: -0.005em
  body-sm:
    fontFamily: Geist
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0em
  spec-data:
    fontFamily: JetBrains Mono
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: -0.01em
  spec-metric:
    fontFamily: JetBrains Mono
    fontSize: 20px
    fontWeight: '700'
    lineHeight: 24px
    letterSpacing: -0.02em
  label-caps:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.08em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  gutter: 1.5rem
  gutter-mobile: 1rem
  margin: 2rem
  margin-mobile: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2.5rem
---

## Brand & Style

This design system establishes an architectural, high-precision aesthetic tailored for high-performance computing, advanced silicon components, and enthusiast-grade hardware retail. The design language avoids the dark, saturated cliches of typical "gamer" gear, opting instead for an immaculate, laboratory-grade clean light interface. It communicates surgical engineering, thermal efficiency, and raw computational throughput.

The visual style merges **Minimalist Precision** with **Technical Functionalism**:
- **Canvas & Form:** Pristine clinical whites, micro-milled zinc structural planes, and hairline delineation.
- **Atmosphere:** Controlled, high-velocity clarity akin to modern silicon cleanrooms and aerospace instrumentation.
- **Target Audience:** Hardware architects, systems builders, high-frequency traders, and tech enthusiasts who evaluate gear through strict benchmarks, thermal dynamics, and raw specifications.

## Colors

The palette is engineered around high luminance contrast, absolute legibility, and targeted kinetic accents.

### Palette Architecture
- **Canvas Base (`#ffffff`):** Pure optical white base providing maximum contrast and reflectance.
- **Structural Surfaces (`#f8fafc`, `#f1f5f9`):** Subdued zinc/slate container tiers providing surface separation without relying on heavy dropshadows.
- **Primary Energy (`#0066cc`):** Deep electric cobalt blue, anchored for primary calls-to-action, structural badges, and decisive interactive cues.
- **Secondary Accent (`#00d2ff`):** Ionized cyan; reserved for micro-accents, live benchmark metrics, active signal indicators, and high-performance spec callouts.
- **Deep Slate Typography (`#0f172a`, `#1e293b`, `#334155`):** Razor-sharp, high-density neutral tones replacing pure black to eliminate harsh fringing while keeping optical acuity high.
- **Borders & Dividers (`#e2e8f0`):** Precision hairlines that simulate anodized aluminum chassis chamfers.

## Typography

The typographic hierarchy uses a triad of specialized typefaces to balance hardware authority, reading comfort, and raw technical metrics:
- **Display & Headings (Space Grotesk):** Engineered geometric grotesque displaying sharp mechanical terminals and forward kinetic posture. Used strictly for naming hardware models, architecture lines, and section titles.
- **Editorial & UI Body (Geist):** Swiss-style, highly neutral, low-strain developer/engineer-grade sans for descriptions, spec summaries, and shopping flows.
- **Telemetry & Technical Specs (JetBrains Mono):** Dense, monospaced tabular data font calibrated for clocks, wattages, thermal tolerances, SKUs, and pricing. Tabular numeric figures prevent interface jitter during interactive config changes.

## Layout & Spacing

The layout is structured around an ultra-precise 12-column fluid grid locked inside a max container width of 1440px to ensure dense comparison capability across wide displays.

### Grid & Breakpoints
- **Desktop (≥ 1280px):** 12 columns, 1.5rem gutters, 2rem margins. Accommodates 3 or 4-column hardware cards alongside persistent architectural filter sidebars.
- **Tablet (768px – 1279px):** 8 columns, 1.25rem gutters, 1.5rem margins. Spec matrices collapse into swipeable horizontal planes.
- **Mobile (< 768px):** 4 columns, 1rem gutters, 1rem margins. Cards reflow into unified vertical modules with sticky configuration action trays.

### Spacing Philosophy
Rhythm follows strict 4px/8px multiples. Component interiors prioritize horizontal tension: compact vertical padding with roomier horizontal space to simulate physical rackmount hardware and circuit layouts.

## Elevation & Depth

This design system avoids blurry drop-shadows and heavy ambient occlusion, opting for structural planar stacking and hairline borders.

### Surface Tiers
- **Tier 0 (Canvas Base):** Solid `#ffffff`.
- **Tier 1 (Structural Card / Module):** `#f8fafc` background with a 1px solid `#e2e8f0` structural hairline border.
- **Tier 2 (Elevated Spec Flyout / Popover):** `#ffffff` with a double-ring perimeter: a 1px solid `#cbd5e1` inner line coupled with a crisp, low-diffusion shadow (`0 4px 12px -2px rgba(15, 23, 42, 0.06)`).
- **Tier 3 (Active Focus / Selection):** Border transitions to `#0066cc` paired with a focused 1px outer glow ring (`0 0 0 1px #0066cc, 0 1px 3px rgba(0, 102, 204, 0.15)`).
- **Surface Decals:** Subtle, micro-grid line textures (1px dot grid or 40px linear grid at 3% opacity `#0f172a`) can be deployed inside container backgrounds to underscore technical precision.

## Shapes

The design system operates strictly at **Soft Roundness (Level 1)**.

- **Base Radii (0.25rem / 4px):** Applied to buttons, input controls, spec chips, and badge components. This minimal corner radius retains the machined, crisp silhouette of heat spreaders and metallic enclosures.
- **Container Radii (0.5rem / 8px):** Applied to hardware product cards, benchmark panels, and modal containers.
- **Strict Prohibition:** Fully rounded pill shapes and circular buttons are avoided to keep the interface grounded in industrial precision rather than casual consumer softness.

## Components

### Buttons
- **Primary Action (Deploy / Add to Build):** Solid `#0066cc` fill, `#ffffff` label in `Geist` Medium (14px). 4px border radius. On hover: shifts to `#0052a3` with a subtle inset edge highlight (`inset 0 1px 0 rgba(255, 255, 255, 0.2)`).
- **Secondary Action (Configure / Compare):** `#ffffff` surface, 1px `#e2e8f0` border, `#0f172a` text. On hover: background shifts to `#f1f5f9` and border darkens to `#cbd5e1`.
- **Tertiary / Utility Button:** Pure transparent background, `#334155` text with `JetBrains Mono` formatting, showing technical disclosure carrots.

### Spec Badges & Chips
- **Telemetry Chips:** Monospaced (`JetBrains Mono`, 12px), background `#f1f5f9`, border 1px solid `#e2e8f0`, text `#1e293b`.
- **Performance Tier Badges:** High-contrast indicator featuring a 6px electric cyan (`#00d2ff`) dot pulse next to bold architecture labels (e.g., `4.5 GHz`, `PCIe 5.0`, `128GB ECC`).
- **Stock/Status Pill:** Ultra-clean indicators with `#0f172a` text, accompanied by crisp status pips (`#0066cc` for In Stock, `#f59e0b` for Backorder).

### Hardware Product Cards
- **Construction:** Crisp `#ffffff` or `#f8fafc` surface framed in 1px `#e2e8f0`. Padding strictly governed by `space-lg`.
- **Image Showcase:** Neutral, light grey canvas pocket (`#f1f5f9`) with 4px border-radius, highlighting component geometry without environmental distraction.
- **Spec Strip:** Structured horizontal row featuring 3 key hardware parameters (Clock, TDP, Cores) formatted in `spec-metric` over a muted `label-caps` label.
- **Interactive State:** Hover lifts the element by 2px with an immediate border shift to `#0066cc`.

### Input Fields & Selectors
- **Fields:** Surface `#ffffff`, border 1px solid `#cbd5e1`, 4px radius. Height fixed to 40px for dense UI alignment. Text set in `Geist` 14px.
- **Focus State:** Hairline border shift to `#0066cc` with a 2px offset box-shadow halo in `rgba(0, 102, 204, 0.15)`. No yellow or soft blue default browser rings.

### Checkboxes & Toggle Switches
- **Checkboxes:** 16x16px square, 3px border-radius, 1px `#cbd5e1` stroke. When checked: `#0066cc` fill displaying a razor-sharp white SVG checkmark.
- **Hardware Toggle:** Rectangular 36x20px channel with 2px radius. Inactive track `#e2e8f0`, active track `#0066cc`. Switch head is an exact 16x16px white block that slides laterally.

### Benchmark & Spec Matrices
- **Table System:** Alternating rows of `#ffffff` and `#f8fafc`. Header row features strict uppercase `JetBrains Mono` in `#64748b` with 1px border-b in `#e2e8f0`.
- **Comparative Bar:** Slim 6px height track (`#e2e8f0`) housing an electric cyan (`#00d2ff`) fill, terminating in a sharp perpendicular 2px cap for absolute visual precision.