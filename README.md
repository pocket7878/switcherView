# SwitcherView

Toggle Switch Like UI component for Android.

![Switcher View Example](https://camo.qiitausercontent.com/6e624e5e62e4acfdd176b5defb2fad243f118d1c/68747470733a2f2f71696974612d696d6167652d73746f72652e73332e61702d6e6f727468656173742d312e616d617a6f6e6177732e636f6d2f302f31343335392f66623264633562642d316139662d613933642d646132322d3461393831353761333166642e706e67)

# Usage

## Install

Add maven repository into your application-level `build.gradle`:

```
repositories {
    ...
    maven {
        url "http://raw.github.com/pocket7878/SwitcherView/master/repository/"
    }
    ...
}
```

Add view into your layout:

```xml
<jp.pocket7878.switcherview.SwitcherView
    app:sv_background_color="@color/white"
    app:sv_leftmost_hover_color="@color/primary"
    app:sv_rightmost_hover_color="@color/alert"
    app:sv_left_choice_icon_src="@drawable/ic_good"
    app:sv_left_choice_text="@string/switcher_good_text"
    app:sv_right_choice_icon_src="@drawable/ic_discontent"
    app:sv_right_choice_text="@string/switcher_discontent_text"
    app:sv_disable_choice_tint_color="@color/icon_default"
    app:sv_enable_choice_tint_color="@color/white"
    />
```

## Layout Attributes

![Attributes guide image](https://camo.qiitausercontent.com/74af1289f92a6a8c917d729759f903d24186e626/68747470733a2f2f71696974612d696d6167652d73746f72652e73332e61702d6e6f727468656173742d312e616d617a6f6e6177732e636f6d2f302f31343335392f35366538613432622d383332362d333739302d653736632d3162616462323562373530652e706e67)

## API

- `switchToLeftChoice()` : Select left choice programatically.
- `switchToRightChoice()` : Select right choice programatically.
- `setOnSwitchSelectChangeListener`
    - `onLeftChoiceSelected()`
    - `onRightChoiceSelected()`
    - `onStartSwitchUserControl()` : Called when user start interact with hover.
    - `onFinishSwitchUserControl()` : Called when user finish interact with hover.