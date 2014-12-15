package wyn.spark.scala

import org.apache.spark.SparkContext._
import org.apache.spark.{SparkConf, SparkContext}
import java.util.Calendar
import org.apache.spark.storage.StorageLevel

object PRLiveJournal {
  
    def main(args: Array[String]) {
         if (args.length != 3 )
        {
          println("Need arguments: <app-name> <input-file> <no. of iteration>")
          System.exit(-1)
        }
         
           var t1 = System.nanoTime()  
           
        val sparkConf = new SparkConf().setAppName(args(0)).setMaster("local")
        
        val iters = args(2).toInt   // 1 is special case to handle phonecalls data which is generated randomly for 200mb/500mb files 
        val ctx = new SparkContext(sparkConf)
        val cedFile = ctx.textFile(args(1),50)
        cedFile.persist(StorageLevel.MEMORY_AND_DISK)
        val calls = cedFile.map { s =>
          val parts = s.split("\\s+") //s.split(",") 
          (parts(0), parts(1))
        }.distinct().groupByKey().cache()
        var ranks = calls.mapValues(v => 1.0)
    
        
         var t3 = System.nanoTime() 
      for (i <- 1 to iters) {
          val contribs = calls.join(ranks).values.flatMap{ case (callMade, rank) =>
            val size = callMade.size
            callMade.map(url => (url, rank / size))
          }
          ranks = contribs.reduceByKey(_ + _).mapValues(0.15 + 0.85 * _)
        }
    
        /*// Join with phone Owners
        
         val users = ctx.textFile(args(1)).map { line =>
            val fields = line.split(",")
            (fields(1), fields(2))
        }
         
        
        val ccByUsername = users.join(ranks)
        
        .map {
            case (id, (username, rank)) => (id, username, rank)
        }
        
       // Print the result
        println(ccByUsername.top(100)(Ordering.by(_._3)).mkString("\n"))
        */
        
         println(ranks.top(100)(Ordering.by(_._2)).mkString("\n"))
         
        var t2 = System.nanoTime() 
        
        println("Time taken to get Top 100 page rank : " + (t2-t3)/1000000000.0 + " Seconds")
        println("Total Time taken : " + (t2-t1)/1000000000.0 + " Seconds")
        ctx.stop()
      }
}