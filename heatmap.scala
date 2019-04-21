import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
object heatmap {

  def help(): Unit ={
  println("Call heatmap.mapvis() function to render heatmap in notebook.")
    println()

//    println("heatmap.mapvis(Dataframe :df , String : latitide , String : longitude  , String : weight , List [String] : filters, String : metric)  \n\ni) Dataframe : df\nYou will get dataframe after querying your dataset.\n\nii) String : latitude\nName of the column having latitude.\n\niii) String : longitude\nName of the column having longitude.\n\niv) List [String] : filters\nPass all the columns on which you need to apply filters. \nA dropdown menu will be created for each column with options being the distinct values from that column. \n\nExample :\nIf you have column X having values a,b,a,b,c,a,c,b  . Then a dropdown will be created with 3 options : a,b,c.\n\nThere is a limit of maximum 4 filters that can be applied. If you pass list with size > 4 , first four filter values are extracted from list and a dropdown on UI is added.\n\nv) String : weight\nName of the column for intensity of heatmap.\n\n\nvi) String : metric (Optional Parameter)\n\nPass the name of the column whose top 5 values based on count is shown.\nThese values changes dynamically as the filter criteria changes.  ")

    println("""mapvis() require 6 parameters .Four parameters are necessary and remaining 2 parameters are optional.

              heatmap.mapvis(Dataframe :df , String : latitide , String : longitude , String : weight , List [String] : filters , String : metric)

              i) Dataframe : df
              You will get dataframe after querying your dataset.

              ii) String : latitude
              Name of the column having latitude.

              iii) String : longitude
              Name of the column having longitude.

              iv) String : weight
              Name of the column for intensity of heatmap.

              v) List [String] : filters (Optional Parameter)
              Pass all the columns on which you need to apply filters.
              A dropdown menu will be created for each column with options being the distinct values from that column.

              Example :

              If you have column X having values a,b,a,b,c,a,c,b . Then a dropdown will be created with 3 options : a,b,c.

              There is a limit of maximum 4 filters that can be applied. If you pass list with size > 4 , first four filter values are extracted from list and a dropdown on UI is added.

              vi) String : metric (Optional Parameter)
              Pass the name of the column whose top 5 values based on count is shown.

              These values changes dynamically as the filter criteria changes.

              Note : If mapvis() is called with only 4 parameters , then sampled data is rendered and no filters are added for interaction.  """)
    println()
    println(" Use print(s\"%html \")  to render HTML Iframe result in your zeppelin notebook.")
    println(""" print(s"%html ${heatmap.mapvis()}") """ )



  }
 
  def mapvis(df: sql.DataFrame , lat : String , lon : String  ,weight : String): String = {

    var x = List[String]()
    mapvis1(df,lat,lon,weight,x,"def")
  }

  def mapvis(df: sql.DataFrame , lat : String , lon : String  ,weight : String, filters : List[String]  , metric : String = "def" ): String = {

    mapvis1(df,lat,lon,weight,filters,metric)

  }



  def mapvis1(df: sql.DataFrame , lat : String , lon : String  ,weight : String, filters : List[String]  , metric :  String  ): String ={


    var filter = List[String]()

      if (filters.size > 4) {
        filter = filters.take(4)
      }
      else {
        filter = filters;
      }




    val appended_seq = filter.map{
      i => i
    }.toSeq
    val seq = Seq(s"${lat}",s"${lon}",s"${weight}")
    val columnNames = seq ++ appended_seq

    //Total Columns to be extracted from dataframe
    val result = df.selectExpr(columnNames: _*)


    //Dropdown options for each filter is calculated from distinct values of that column

    val distinctVal = filter.map{
      i => df.select(i).distinct().collect().map(row => row.getString(0)).mkString("[\"", "\", \"", "\"]")
    }.toList


//    println(df.where(s"${filter(0)} = 'Delhi' and ${filter(1)} = 'personal' " ).count())



    //Sampling limit is set 200k values
    def limit = 200000;

    val count: Double = result.count * columnNames.size


    // Data Sampling (with replacement)
    val sampledData = { if (count >= limit)
      result.sample(true, limit.asInstanceOf[Double] / count.asInstanceOf[Double]).toJSON.collect.mkString("[", "," , "]" ) else result.toJSON.collect.mkString("[", "," , "]" )
    }
//


    var seq1 = appended_seq
    var filter1 = filter

    if(metric != "def"){
       seq1 =  seq1 ++ Seq(metric)
        filter1 = filter1 ++ List(metric)
    }
    seq1 = seq1 ++ Seq("count")

    val Rollup = df
      .rollup(filter1.map(col) :_*)
      .agg(sum(lit(1)) as "count")
      .select(seq1.map(col): _*  )

    val rollupdata = Rollup.toJSON.collect.mkString("[", "," , "]" )

//    println(metric)
//    println(filter.size)
    var a = "";
      if(filter.size == 0){
        a = mapRenderer(sampledData,lat,lon,weight).frameHTML()
    }
      else if(filter.size == 1) {
         a = mapRenderer1(sampledData,rollupdata, lat, lon, filter(0), distinctVal(0), weight,metric).frameHTML()
      }
      else if(filter.size == 2) {
         a = mapRenderer2(sampledData,rollupdata, lat, lon, filter, distinctVal, weight,metric).frameHTML()
    }
      else if(filter.size == 3) {
       a =  mapRenderer3(sampledData,rollupdata, lat, lon, filter, distinctVal, weight ,metric).frameHTML()
    }
    else{
        a = mapRenderer4(sampledData,rollupdata, lat, lon, filter, distinctVal, weight,metric).frameHTML()
      }

    a
  }




}
