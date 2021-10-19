package cangjie.scale.sorting.entity

data class Update(
    val versionCode: Int,
    val versionName: String,
    val forceUpdate: Boolean,
    val updateLog: String,
    val apkUrl: String
)
