package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

import java.text.SimpleDateFormat
import java.util.*

class DurationTransformation {

    // members -------------------------------------------------------------------------------------

    private val simpleDateFormat = SimpleDateFormat("H:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    // public methods ------------------------------------------------------------------------------

    fun transform(durationInMilliseconds: Long) =
        if (durationInMilliseconds >= 0) {
            simpleDateFormat.format(durationInMilliseconds) ?: ""
        } else {
            "Negative duration: $durationInMilliseconds"
        }
}
