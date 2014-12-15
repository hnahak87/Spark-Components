

import org.apache.spark.graphx._
import org.apache.spark._
import org.apache.log4j.Logger
import org.apache.log4j.Level


object GraphLoad  {

  def main(args: Array[String]): Unit = {
    
     System.setProperty("hadoop.home.dir", "C:\\Hari\\env\\HadoopWinUtil")
     Logger.getLogger("org").setLevel(Level.WARN)
     Logger.getLogger("akka").setLevel(Level.WARN)
     val conf = new SparkConf().setAppName("MyAPP").setMaster("local")
     val sc =  new SparkContext(conf)
     
     val graph : Graph[(String, String), String] = GraphExtended.graphAsObject(sc, "C:\\Hari\\outputs\\GraphObject\\")      
     
     println(graph.vertices.count())
    
  }
}