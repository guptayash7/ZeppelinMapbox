


case class mapRenderer3(data: String , rollup : String ,lat: String , lon:String , filter : List[String] , distinctVal : List[String] , weight: String , metric :String ="")  {


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
          var actual_data = ${rollup}
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
                   style: 'mapbox://styles/int687/cjtquzt3k1j9f1fobrx48ybow',
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




          var lat,lon,total,zoom;
          var all_points1 = []
          var val1;
          var val2;
          var val3;

          function myfunc0(v1){
              val1=v1;
          }
          function myfunc1(v2){
              val2 = v2;
          }
          function myfunc2(v3){
              val3 = v3;
          }
          function myfunc() {
              send(val1,val2,val3);
              render();
              var actual_count=0;
              for(var i=0 ; i<actual_data.length ; i++){
                if(actual_data[i].${filter(0)}==val1 && actual_data[i].${filter(1)}==val2 && actual_data[i].${filter(2)} === val3){
                   actual_count = actual_data[i].count
                }
              }
              document.getElementById("nav").innerHTML = "Sampled Count : " + total + " <br /> "+"Actual Count : " + actual_count;
              document.getElementById("nav").style.display = "inline-block"

             if("${metric}" != "def"){

               var newvar = actual_data.filter(v => (v.${filter(0)} == val1 && v.${filter(1)} == val2 && v.${filter(2)} == val3))
                     newvar.sort((a, b) => (b.count) - (a.count));
                     var f=0;
                     var top = "";

               for(var i=0;f!=5 && i<newvar.length;i++){
                       if(newvar[i].${metric} != null){
                         top += newvar[i].${metric}
                         top += " : "
                         top += newvar[i].count.toString()
                         top += "<br/>"
                         f++;
                       }
                     }
                     document.getElementById("met").innerHTML = "Top Result Based on Count <br/><br/> " + "${metric} :<br/><br/>" +   top;
                   }

  }

  function send(val1,val2,val3){

       lat = 0.0;
       lon = 0.0;
       total=0;
       zoom = 6;
        all_points1 = [];
        $$.each(data,function (i, f) {


           if(f.${filter(0)} == val1 && f.${filter(1)} == val2 && f.${filter(2)} == val3 ) {
                  lat += f['${lat}'];
                  lon += f['${lon}'];
                  total = total+1;
                  all_points1.push({
                 type: 'Feature',
                 geometry: {
                  type: 'Point',
                  coordinates: [f['${lon}'], f['${lat}']]
                    }
                  });
                  }
             });
        if(total > 0){
            lat = lat/total;
            lon = lon/total;
            zoom = 6;
         }
         else{
            lon = 78.9025;
             lat = 20.6141;
             zoom = 3;
         }
}


  function render() {

  var map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/int687/cjtquzt3k1j9f1fobrx48ybow',
    'center': [lon,lat],
    'zoom': zoom
  });


               map.on('load', function() {





                    map.addLayer({
                              "id": "earthquakes-heat",
                              "type": "heatmap",
                             "source": {
                                  "type": "geojson",
                                  "data": {
                                      "type": "FeatureCollection",
                                      "features": all_points1
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
                                     "features": all_points1
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




  }
  </script>


             <div style ="align : right;position: relative;align-items: right;padding-left: 85%;">
               <strong> ${filter(0)}</strong>

               <select onchange="myfunc0(this.value)" id = "sel0">

                  <option value="">Select</option>

           </select>
             </div>

        <script>
                var select = document.getElementById("sel0"),
                            arr = ${distinctVal(0)};

                    for(var i = 0; i < arr.length; i++)
                    {
                        var option = document.createElement("OPTION"),
                        txt = document.createTextNode(arr[i]);
                        option.appendChild(txt);
                        option.setAttribute("value",arr[i]);
                        sel0.insertBefore(option,sel0.lastChild);
                    }
           </script>


       <div style ="align : right;position: relative;align-items: right;padding-left: 85%;padding-top:20px;">

         <strong>  ${filter(1)}</strong>
      <select onchange="myfunc1(this.value)" id = "sel1">

        <option value="">Select</option>

      </select>

    </div>

      <script>
        var select = document.getElementById("sel1"),
        arr = ${distinctVal(1)};

        for(var i = 0; i < arr.length; i++)
        {
        var option = document.createElement("OPTION"),
    txt = document.createTextNode(arr[i]);
    option.appendChild(txt);
    option.setAttribute("value",arr[i]);
    sel1.insertBefore(option,sel1.lastChild);
    }
    </script>

    <div style ="align : right;position: relative;align-items: right;padding-left: 85%;padding-top:20px;">

      <strong>  ${filter(2)}</strong>
      <select onchange="myfunc2(this.value)" id = "sel2">

        <option value="">Select</option>

      </select>

    </div>

      <script>
        var select = document.getElementById("sel2"),
        arr = ${distinctVal(2)};

        for(var i = 0; i < arr.length; i++)
        {
        var option = document.createElement("OPTION"),
        txt = document.createTextNode(arr[i]);
        option.appendChild(txt);
        option.setAttribute("value",arr[i]);
        sel2.insertBefore(option,sel2.lastChild);
    }
    </script>

      <div class="btn-group" style ="align : right;position: relative;align-items: right;padding-left: 85% ; padding-top:40px">
      <Strong>Click to Apply Filters<Strong>
        <button onclick = "myfunc()">Apply</button>
        <br />
        <span id="nav" style="display:none; position: relative; background: rgba(255,255,255, 0.7); padding: 10px 20px; border-radius: 5px; box-shadow: 0px 0px 8px rgba(000,000,000,.5); margin-top: 10px;">
        </span>
      </div>
        <div id = "met" style ="align : right;position: relative;align-items: right;padding-left: 85% ; padding-top:40px">
             </div>
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
