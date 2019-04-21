


case class mapRenderer(data: String , lat: String , lon:String  , weight: String )  {


  def pageHTML():String = {
    s"""

  <!DOCTYPE html>
    <html>
      <head>
        <meta charset='utf-8' />
        <title>MAP</title>
        <meta name='viewport' content='initial-scale=1,maximum-scale=1,user-scalable=no' />
        <script src='https://api.tiles.mapbox.com/mapbox-gl-js/v0.52.0/mapbox-gl.js'></script>
       <link href='https://api.tiles.mapbox.com/mapbox-gl-js/v0.52.0/mapbox-gl.css' rel='stylesheet' />

        <style>
          body { margin:10; padding:0; }
          #map { position:absolute; top:0; bottom:0; width:83%; }
        </style>
      </head>
      <body>

        <div id='map'></div>


        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"> </script>

        <script>
          mapboxgl.accessToken = 'pk.eyJ1IjoiaW50Njg3IiwiYSI6ImNqcmhmbmp6ODF4dTA0M21yZ3VhYnFhd28ifQ.1cS8r1LDr-OmwoanSgYF-g';

          var data = ${data}






         var all_points = [];
               $$.each(data, function(i, f) {
                    all_points.push({
                       type: 'Feature',
                       geometry: {
                           type: 'Point',
                          coordinates: [f['${lon}'], f['${lat}']]
                       }

            });
              });
               var map = new mapboxgl.Map({
                   container: 'map',
                   style:'mapbox://styles/int687/cjtquzt3k1j9f1fobrx48ybow',
                   'center': [78.9025, 20.6141],
                  'zoom': 3.5
               });

         map.on('load', function() {





            map.addLayer({
                       "id": "earthquakes-heat",
                       "type": "heatmap",
                       "source": {
                           "type": "geojson",
                           "data": {
                               "type": "FeatureCollection",
                               "features": all_points
                           }
                       },
                       "maxzoom": 9,
                       "paint": {
       // Increase the heatmap weight based on frequency and property magnitude
                           "heatmap-weight": [
                               "interpolate",
                               ["linear"],
                               ["get", "${weight}"],
                               0, 0,
                               50, 1
                           ],
       // Increase the heatmap color weight weight by zoom level
       // heatmap-intensity is a multiplier on top of heatmap-weight
                           "heatmap-intensity": [
                               "interpolate",
                               ["linear"],
                               ["zoom"],
                               0, 1,
                               9, 3
                           ],
       // Color ramp for heatmap.  Domain is 0 (low) to 1 (high).
       // Begin color ramp at 0-stop with a 0-transparancy color
       // to create a blur-like effect.
                           "heatmap-color": [
                               "interpolate",
                               ["linear"],
                               ["heatmap-density"],
                               0, "rgba(33,102,172,0)",
                               0.2, "rgb(103,169,207)",
                               0.4, "rgb(209,229,240)",
                               0.6, "rgb(253,219,199)",
                               0.8, "rgb(239,138,98)",
                               1, "rgb(178,24,43)"
                           ],
       // Adjust the heatmap radius by zoom level
                           "heatmap-radius": [
                               "interpolate",
                               ["linear"],
                               ["zoom"],
                               0, 2,
                               9, 20
                           ],
       // Transition from heatmap to circle layer by zoom level
                           "heatmap-opacity": [
                               "interpolate",
                               ["linear"],
                               ["zoom"],
                               7, 1,
                               9, 0
                           ],
                       }
                   }, 'waterway-label');

             map.addLayer({
                       "id": "earthquakes-point",
                       "type": "circle",
                       "source": {
                           "type": "geojson",
                           "data": {
                               "type": "FeatureCollection",
                               "features": all_points
                           }
                       },
                       "minzoom": 7,
                       "paint": {
       // Size circle radius by earthquake magnitude and zoom level
                           "circle-radius": [
                               "interpolate",
                               ["linear"],
                              ["zoom"],
                               7, [
                                   "interpolate",
                                   ["linear"],
                                   ["get", "${weight}"],
                                   0, 1,
                                  50, 4
                               ],
                              16, [
                                   "interpolate",
                                   ["linear"],
                                   ["get", "${weight}"],
                                   0, 5,
                                   50, 50
                              ]
                           ],
       // Color circle by earthquake magnitude
                           "circle-color": [
                               "interpolate",
                               ["linear"],
                               ["get", "${weight}"],
                               1, "rgba(33,102,172,0)",
                               10, "rgb(103,169,207)",
                               20, "rgb(209,229,240)",
                               30, "rgb(253,219,199)",
                               40, "rgb(239,138,98)",
                               50, "rgb(178,24,43)"
                           ],
                           "circle-stroke-color": "white",
                           "circle-stroke-width": 1,
       // Transition from heatmap to circle layer by zoom level
                           "circle-opacity": [
                               "interpolate",
                               ["linear"],
                               ["zoom"],
                               7, 0,
                               8, 1
                           ]
                       }
                   }, 'waterway-label');
         });

  </script>

  </body>
  </html>

"""

  }

  /**
    * Typically you'll want to use this method to render your chart. Returns a full page of HTML wrapped in an iFrame
    * for embedding within existing HTML pages (such as Jupyter).
    * XXX Also contains an ugly hack to resize iFrame height to fit chart, if anyone knows a better way open to suggestions
    * @param name The name of the chart to use as an HTML id. Defaults to a UUID.
    * @return HTML containing iFrame for embedding
    */
  def frameHTML(name: String = "default") = {
    val frameName = "frame-" + name
    s"""
       |  <iframe id="${frameName}" height="600px" width = "100%" scrolling = "no" srcdoc="${xml.Utility.escape(pageHTML())}"></iframe>

    """.stripMargin
  }

}
