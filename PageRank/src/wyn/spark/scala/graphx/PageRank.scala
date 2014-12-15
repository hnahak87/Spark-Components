package wyn.spark.scala.graphx

import org.apache.spark.graphx._
import org.apache.spark._
import org.apache.spark.storage.StorageLevel
import java.util.Calendar
import org.apache.spark.rdd.RDD


import org.apache.spark.SparkContext._
import org.apache.spark.{SparkConf, SparkContext}

object PageRank { 
  
  def main(args: Array[String]): Unit = {
    
   
    if (args.length != 5 )
    {
      println("Need three arguments: <app-name> <vertex-input-file> <edge-input-file> <no. of iteration>")
      System.exit(-1)
    }
    
     var t1 = System.nanoTime()
     
    val conf = new SparkConf().setAppName(args(0)).setMaster("local") //In case of cluster/spark-submit remove .setMaster()  
    val sc =  new SparkContext(conf)
    
    
  /* Graph Laoding */
     
      // Laoding Users 
     val users: RDD[(VertexId, ( String))]  =  sc.textFile(args(1))
                                                          .map(line => line.split(","))
                                                            //.map(elem => elem.trim)) 
                                                              .map { line =>  (line(1).toLong, (line(2).toString())) }.persist(StorageLevel.MEMORY_AND_DISK)
     // Loading Calls
      
     val edges: RDD[Edge[String]] = sc.textFile(args(2))
                                        .map(line => line.split(","))
                                         // .map(elem => elem.trim)) 
                                            .map{line =>  Edge(line(0).toLong, line(1).toLong, line(2))}
     
     val calls: Graph[(String, String), String] = Graph.fromEdges(edges, defaultValue = ("",""),StorageLevel.MEMORY_AND_DISK,StorageLevel.MEMORY_AND_DISK ).partitionBy(PartitionStrategy.EdgePartition2D, args(4).toInt)                                                         
  /* val graph:Graph[(String, String), String] = calls.outerJoinVertices(users){
      case (uid, deg, Some(attrList)) => attrList
      case (uid, deg, None) => ("", "")
    } */

    var t3 = System.nanoTime() 
    val subgraph = calls.subgraph()

    val pagerankGraph = subgraph.staticPageRank(args(3).toInt, 0.001) 

   /* val userInfoWithPageRank = subgraph.outerJoinVertices(pagerankGraph.vertices) {
      case (uid, attrList, Some(pr)) => (pr, attrList._1, attrList._2)
      case (uid, attrList, None) => (0.0, attrList._1, attrList._2)
    }*/

    
   val result =  users.join(pagerankGraph.vertices).map
   {
       case (id, (name: String, rank: Double)) =>  (id, name, rank)
     }
    
    
    println(result.top(100)(Ordering.by(_._3)).mkString("\n"))

   
    var t2 = System.nanoTime() 
    
    println("Time taken to get Top 100 page rank " + (t2-t3)/1000000000.0 + " Seconds")
    println("Total Time taken " + (t2-t1)/1000000000.0 + " Seconds")
    sc.stop()
  }
 }