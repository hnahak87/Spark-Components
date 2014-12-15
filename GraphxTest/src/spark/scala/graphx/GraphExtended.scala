

import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

object GraphExtended {
  
   /**
   * Save this Graph as a SequenceFile of serialized objects.
   */
  def saveGraphAsObject(graph: Graph[(String, String),String], path: String) = {
   
  
  
   lazy val edges  = graph.edges
  
  lazy val vertices  = graph.vertices
    edges.saveAsObjectFile(path+"_Edges.obj")
    vertices.saveAsObjectFile(path+"_Vertices.obj")
   
  }

  def graphAsObject( sc: SparkContext , path: String) : Graph[(String, String), String] = {
   
         
     val vertices: RDD[(VertexId, (String, String))]  = sc.objectFile(path+"_Vertices.obj")
     val edges: RDD[Edge[String]] =  sc.objectFile(path+"_Edges.obj") 
     
     println("Total vertices ::" + vertices.count())
     
      println("Total edges ::" + edges.count())
    val graph = Graph(vertices, edges, null)
    graph
  }

  def saveGraphAsTextFile(graph: Graph[(String, String),String], path: String) = {
    graph.edges.saveAsTextFile(path+"_Edges.txt")
    graph.vertices.saveAsTextFile(path+"_Vertices.txt")
    
  }
  
  
  def graphAsText( sc: SparkContext , path: String) : Graph[(String, String), String] = {
   
         
     val vertices = sc.textFile(path+"_Vertices.txt")  
     val edges: RDD[Edge[String]] =  sc.objectFile(path+"_Edges.obj") 
     
     println("Total vertices ::" + vertices.count())
     
      println("Total edges ::" + edges.count())
    //val graph = Graph(vertices, edges, null)
  val graph:  Graph[(String, String), String] = null
  graph
  }

  
  
}

