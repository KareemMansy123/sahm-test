# Sahm Food POS — Design System

> Distilled from the full design spec. Source of truth for the visible layer.

## Audience

- **Cashier** at a quick-service counter. Speed-critical. Glanced at, not studied.
- **Owner** reviewing the day's orders. Density and clarity over speed.

## Brand

Warm saffron-amber primary signals food, confidence, and contrasts strongly in
bright kitchens. Deep charcoal surfaces. Vivid teal as a sparingly-used accent
for success states.

## Color tokens (Material 3)

### Light

| Token | Hex |
|---|---|
| primary | `#D4820A` |
| onPrimary | `#FFFFFF` |
| primaryContainer | `#FFE0B2` |
| secondary | `#1A6B7C` |
| tertiary (success) | `#2E7D32` |
| error | `#B3261E` |
| background | `#FFFBF5` |
| surface | `#FFFFFF` |
| surfaceVariant | `#F3E6D4` |
| outline | `#7F6E62` |

### Dark

| Token | Hex |
|---|---|
| primary | `#FFB951` |
| background | `#141210` |
| surface | `#1E1B18` |
| surfaceVariant | `#2E2924` |

## Typography

Sans-serif (system default) for UI. Monospace for receipts. M3 type scale:

| Role           | sp | Weight |
|----------------|----|--------|
| displaySmall   | 36 | 700    |
| headlineLarge  | 32 | 600    |
| headlineMedium | 28 | 600    |
| headlineSmall  | 24 | 600    |
| titleLarge     | 22 | 600    |
| titleMedium    | 16 | 600    |
| bodyLarge      | 16 | 400    |
| labelLarge     | 14 | 600    |

## Spacing scale (multiples of 4)

`xs=4, sm=8, md=12, lg=16, xl=24, xxl=32, huge=48`

## Touch targets

Minimum 48dp. Primary buttons (Charge, Confirm) 56dp. Keypad keys 72dp.

## Layout

- **Expanded (≥840dp)**: 60/40 split — catalog left, cart right rail
- **Compact (<600dp)**: full-screen grid + FAB → ModalBottomSheet cart

## Wow moments

1. **Living total** — grand total animates with an odometer roll-up
   (`animateFloatAsState`, 400ms emphasized decelerate)
2. **Confirmation halo** — payment success shows a tertiary-colored circle
   with a centered check icon

## Accessibility

- Contrast: primary on background ≥ 4.6:1 (AA)
- Body text ≥ 16sp
- All icon buttons have `contentDescription`
- All interactive surfaces ≥ 48dp
- One-handed reachability on phone (FAB and primary buttons bottom-right
  and bottom-center)

## Speed budget

3 taps minimum to complete an order: tap product → Charge → Confirm.
