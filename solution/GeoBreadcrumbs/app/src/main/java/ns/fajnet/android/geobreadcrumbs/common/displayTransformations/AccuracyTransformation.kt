package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

class AccuracyTransformation {

    // public methods -------------------------------------------------------------------------------

    fun transform(accuracy: Float): String {
        return if (accuracy >= 0) {
            "$accuracy m"
        } else {
            "invalid accuracy: $accuracy"

        }
    }
}
