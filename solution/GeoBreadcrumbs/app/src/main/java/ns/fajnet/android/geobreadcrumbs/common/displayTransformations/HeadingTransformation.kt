package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

class HeadingTransformation : IDisplayTransformation {

    // public methods ------------------------------------------------------------------------------

    fun transform(heading: Float) = when (heading) {
        in -180F..-157.5F -> Direction.S.name
        in -157.5F..-112.5F -> Direction.SW.name
        in -112.5F..-67.5F -> Direction.W.name
        in -67.5F..-22.5F -> Direction.NW.name
        in -22.5F..22.5F -> Direction.N.name
        in 22.5F..67.5F -> Direction.NE.name
        in 67.5F..112.5F -> Direction.E.name
        in 112.5F..157.5F -> Direction.SE.name
        in 157.5F..180F -> Direction.S.name
        else -> "invalid heading"
    }
}
