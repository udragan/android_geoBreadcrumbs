package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import ns.fajnet.android.geobreadcrumbs.R

class SignalQualityTransformation(val context: Context) : IDisplayTransformation {

    // public methods ------------------------------------------------------------------------------

    fun transform(satellites: Int) =
        if (satellites < 3) {
            getColor(context, R.color.warning)
        } else {
            getColor(context, R.color.gray_text)
        }
}
