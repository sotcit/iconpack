package app.iconpack

import android.graphics.drawable.Drawable

data class InstalledPackageInfo(
    val applicationLabel: String,
    val component: String,
    val drawable: String,
    var packageName: String,
    var applicationIcon: Drawable,
)
