# SwitcherView

Toggle Switch Like UI component for Android.

![Switcher View Example](https://qiita-user-contents.imgix.net/https%3A%2F%2Fqiita-image-store.s3.ap-northeast-1.amazonaws.com%2F0%2F14359%2Fc57045ba-0775-7ffa-4b3d-41d448991382.gif?ixlib=rb-1.2.2&auto=format&gif-q=60&q=75&s=7de653411aca87ba2bba3fdc8f9d9b2b)

# Usage

## Install

Add maven repository into your application-level `build.gradle`:

```
repositories {
    ...
    maven {
        url "https://dl.bintray.com/pocket7878/maven"
    }
    ...
}

dependencies {
    ...
    implementation "jp.pocket7878.switcherview:switcherview:1.0.2"
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