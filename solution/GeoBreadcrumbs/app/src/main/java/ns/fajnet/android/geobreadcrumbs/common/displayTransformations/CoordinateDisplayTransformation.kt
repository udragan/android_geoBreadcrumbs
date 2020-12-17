package ns.fajnet.android.geobreadcrumbs.common.displayTransformations

import android.location.Location
import kotlin.math.abs

class CoordinateDisplayTransformation(var transformToDegMinSec: Boolean) {

    // public methods ------------------------------------------------------------------------------

    fun transform(coordinate: Double, orientation: Orientation): String {
        if (!transformToDegMinSec) {
            return coordinate.toString()
        }

        val direction: String = getDirection(coordinate, orientation)
        val splitCoordinates =
            arrayOf(Location.convert(abs(coordinate), Location.FORMAT_SECONDS).split(':'))

        return "${splitCoordinates[0][0]}º ${splitCoordinates[0][1]}\" ${splitCoordinates[0][2]}' $direction"
    }

    // private methods -----------------------------------------------------------------------------

    private fun getDirection(coordinate: Double, orientation: Orientation): String {
        return if (orientation == Orientation.HORIZONTAL) {
            if (coordinate < 0) {
                Direction.W.name
            } else {
                Direction.E.name
            }
        } else if (coordinate < 0) {
            Direction.S.name
        } else {
            Direction.N.name
        }
    }
}

// #################################################################################################

enum class Orientation {
    HORIZONTAL, VERTICAL
}

enum class Direction {
    N, S, E, W
}
