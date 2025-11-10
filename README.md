# capacitor-android-e2e

Provide edge to edge android feature to capacitor realm. Contains solution for safe-area on Android, supports coloring statusbar background and navigation bar background, with support of semi-transparent colours.

## Install

```bash
npm install capacitor-android-e2e
npx cap sync
```

## API

<docgen-index>

* [`enable()`](#enable)
* [`disable()`](#disable)
* [`getInsets()`](#getinsets)
* [`setBackgroundColor(...)`](#setbackgroundcolor)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### enable()

```typescript
enable() => Promise<void>
```

Enable the edge-to-edge mode.

Only available on Android.

**Since:** 7.2.0

--------------------


### disable()

```typescript
disable() => Promise<void>
```

Disable the edge-to-edge mode.

Only available on Android.

**Since:** 7.2.0

--------------------


### getInsets()

```typescript
getInsets() => Promise<GetInsetsResult>
```

Return the insets that are currently applied to the webview.

Only available on Android.

**Returns:** <code>Promise&lt;<a href="#getinsetsresult">GetInsetsResult</a>&gt;</code>

**Since:** 7.2.0

--------------------


### setBackgroundColor(...)

```typescript
setBackgroundColor(options: SetBackgroundColorOptions) => Promise<void>
```

Set the background color of the status bar and navigation bar.

Only available on Android.

| Param         | Type                                                                            |
| ------------- | ------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#setbackgroundcoloroptions">SetBackgroundColorOptions</a></code> |

**Since:** 7.0.0

--------------------


### Interfaces


#### GetInsetsResult

| Prop         | Type                | Description                                                                  | Since |
| ------------ | ------------------- | ---------------------------------------------------------------------------- | ----- |
| **`bottom`** | <code>number</code> | The bottom inset that was applied to the webview. Only available on Android. | 7.2.0 |
| **`left`**   | <code>number</code> | The left inset that was applied to the webview. Only available on Android.   | 7.2.0 |
| **`right`**  | <code>number</code> | The right inset that was applied to the webview. Only available on Android.  | 7.2.0 |
| **`top`**    | <code>number</code> | The top inset that was applied to the webview. Only available on Android.    | 7.2.0 |


#### SetBackgroundColorOptions

| Prop                     | Type                | Description                                                                                | Since |
| ------------------------ | ------------------- | ------------------------------------------------------------------------------------------ | ----- |
| **`statusBarColor`**     | <code>string</code> | The hexadecimal color to set as the background color of the status bar and navigation bar. | 7.0.0 |
| **`navigationBarColor`** | <code>string</code> |                                                                                            |       |

</docgen-api>
