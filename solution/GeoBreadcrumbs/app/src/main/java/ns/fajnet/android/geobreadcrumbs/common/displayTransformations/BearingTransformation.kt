package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

class BearingTransformation {

    // public methods -------------------------------------------------------------------------------

    fun transform(bearing: Float): String {
        return when (bearing) {
            in 0F..22.5F -> Direction.N.name
            in 22.5F..67.5F -> Direction.NE.name
            in 67.5F..112.5F -> Direction.E.name
            in 112.5F..157.5F -> Direction.SE.name
            in 157.5F..202.5F -> Direction.S.name
            in 202.5F..247.5F -> Direction.SW.name
            in 247.5F..292.5F -> Direction.W.name
            in 292.5F..337.5F -> Direction.NW.name
            in 337.5F..360F -> Direction.N.name
            else -> "invalid heading"
        }
    }
}
