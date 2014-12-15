package wyn.spark.scala.graphx
import org.apache.spark.graphx._
import org.apache.spark._
import org.apache.spark.storage.StorageLevel
import java.util.Calendar
import org.apache.spark.SparkContext._
import org.apache.spark.{SparkConf, SparkContext}
object PRLiveJournal  {

   
  def main(args: Array[String]): Unit = {
    
   
    if (args.length != 3 )
    {
      println("Need three arguments: <app-name> <edge-input-file> <no. of iterations>")
      System.exit(-1)
    }
    
    val conf = new SparkConf().setAppName(args(0)).setMaster("local").set("spark.executor.memory", "4g")  //In case of cluster/spark-submit remove .setMaster()  
    val sc =  new SparkContext(conf)
    
    
    
    var t1 = System.nanoTime()  
   //--> val users = (sc.textFile(args(1)).map(line => line.split(",")).map( parts => (parts.head.toLong, parts.tail) ))
 
    
   // http://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.graphx.GraphLoader 
    val unpartgraph = GraphLoader.edgeListFile(sc,args(1),false, 50,
                            StorageLevel.MEMORY_AND_DISK,
                            StorageLevel.MEMORY_AND_DISK).cache()
   
  
    val graph = unpartgraph.partitionBy(PartitionStrategy.RandomVertexCut)
    
    
   /* val graph = calls.outerJoinVertices(users) {
      case (uid, deg, Some(attrList)) => attrList
      case (uid, deg, None) => Array.empty[String]
    }

    val subgraph = graph.subgraph()*/

     var t3 = System.nanoTime() 
    val pagerankGraph = graph.staticPageRank(args(2).toInt, 0.001)

  /*  val userInfoWithPageRank = subgraph.outerJoinVertices(pagerankGraph.vertices) {
      case (uid, attrList, Some(pr)) => (pr, attrList.toList)
      case (uid, attrList, None) => (0.0, attrList.toList)
    }*/

  //  println(userInfoWithPageRank.vertices.top(5)(Ordering.by(_._2._1)).mkString("\n"))

    println(pagerankGraph.vertices.top(100)(Ordering.by(_._2)).mkString("\n"))
    
    
    var t2 = System.nanoTime() 
    
    println("Time taken to get Top 100 page rank : " + (t2-t3)/1000000000.0 + " Seconds")
    println("Total Time taken : " + (t2-t1)/1000000000.0 + " Seconds")
    
   sc.stop()
  }
}