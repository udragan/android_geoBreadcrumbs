package ns.fajnet.android.geobreadcrumbs.dtos

data class TrackDto(var duration: String = "",
                    var distance: Float = 0F,
                    var currentSpeed: Float = 0F,
                    var averageSpeed: Float = 0F,
                    var maxSpeed: Float = 0F,
                    var currentBearing: Float = 0F,
                    var overallBearing: Float = 0F,
                    var noOfPlaces: Int = 0,
                    var noOfPoints: Int = 0)
