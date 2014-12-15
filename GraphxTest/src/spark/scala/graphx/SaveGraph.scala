

import org.apache.spark.graphx._
import org.apache.spark._
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.rdd.RDD

object SaveGraph  {
  
  def main(args: Array[String]): Unit = {
    
      System.setProperty("hadoop.home.dir", "C:\\Hari\\env\\HadoopWinUtil")
   Logger.getLogger("org").setLevel(Level.WARN)
   Logger.getLogger("akka").setLevel(Level.WARN)
    val conf = new SparkConf().setAppName("MyAPP").setMaster("local")
    val sc =  new SparkContext(conf)
   
   val users: RDD[(VertexId, (String, String))] =
  sc.parallelize(Array((3L, ("rxin", "student")), (7L, ("jgonzal", "postdoc")),
                       (5L, ("franklin", "prof")), (2L, ("istoica", "prof")),
                       (4L, ("rxion", "postdoc")), (1L, ("rynold", "postdoc")),
                       (6L, ("shiva", "postdoc")), (8L, ("jhon", "student"))))
                       
    val relationships: RDD[Edge[String]] =
  sc.parallelize(Array(Edge(3L, 7L, "collab"),    Edge(5L, 3L, "advisor"),
                       Edge(2L, 5L, "colleague"), Edge(5L, 7L, "pi"),
                       Edge(4L, 7L, "colleague"), Edge(1L, 7L, "colleague"),
                       Edge(6L, 7L, "colleague"), Edge(8L, 7L, "colleague")))
                       
    // Define a default user in case there are relationship with missing user
    val defaultUser = ("John Doe", "Missing")


    // Build the initial Graph
   val graph = Graph(users, relationships, defaultUser)
   
  // GraphExtended.saveGraphAsObject(graph, "C:\\Hari\\outputs\\GraphObject\\")  
   
   GraphExtended.saveGraphAsTextFile(graph, "C:\\Hari\\outputs\\GraphText\\")  
   
   println("Complete")
  }
}