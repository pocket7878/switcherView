package jp.poketo7878.switcherview

import android.content.res.Resources

internal val Int.dp
    get() = (Resources.getSystem().displayMetrics.density * this).toInt()
