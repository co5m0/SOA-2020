package it.unisa.soa

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.input.PortableDataStream
import org.apache.spark.ml._
import org.apache.spark.ml.feature.{HashingTF, IDF}
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.evaluation.ClusteringEvaluator
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
//import org.apache.tika.metadata._
//import org.apache.tika.parser._
//import org.apache.tika.parser.pdf.PDFParser
//import org.apache.tika.sax.WriteOutContentHandler
import com.johnsnowlabs.nlp.base._
import com.johnsnowlabs.nlp.annotator._

import java.io._

val stopwordSourceText = "/user/soa/conseccio/stopwords.txt"
val lemmatizerModelSoruce = "/user/soa/conseccio/lemma_antbnc_en_2.0.2_2.4_1556480454569"
val outputDestinationFolder = "/user/soa/conseccio/output"

object App {
  def main(args: Array[String]) {
    //val start = System.nanoTime
    // create Spark context with Spark configuration
    val sc = new SparkContext(new SparkConf().setAppName("wordcount"))
    val spark = SparkSession.builder().config(sc.getConf).getOrCreate()
    import spark.implicits._
    val path = args(0)
    //Initialize pipes
    val stopwords = sc.textFile(stopwordSourceText).collect()
    val document = new DocumentAssembler()
      .setInputCol("value")
      .setOutputCol("document")
    val token = new Tokenizer()
      .setInputCols("document")
      .setOutputCol("token")
    val normalizer = new Normalizer()
      .setInputCols("token")
      .setOutputCol("normal")
    val stopWordsCleaner = new StopWordsCleaner()
      .setInputCols("normal")
      .setOutputCol("clean")
      .setStopWords(stopwords)
      .setCaseSensitive(false)
    val stemmer = new Stemmer()
      .setInputCols("clean")
      .setOutputCol("stem")
    val lemmatizer = LemmatizerModel.load(lemmatizerModelSoruce)
      .setInputCols("stem")
      .setOutputCol("lem")
    val finisher = new Finisher()
      .setInputCols("lem")
    val hashingTF = new HashingTF()
      .setInputCol("finished_lem")
      .setOutputCol("raw_features")
    val idf = new IDF()
      .setInputCol("raw_features")
      .setOutputCol("features")
    val kmeans = new KMeans()
      .setK(9)
      .setSeed(42L)
      .setFeaturesCol("features")
      .setPredictionCol("prediction")
    val pipeline = new Pipeline().setStages(Array(document, token, normalizer, stopWordsCleaner, stemmer, lemmatizer, finisher, hashingTF, idf, kmeans))
    
    //Execute processing
    val fileData = sc.binaryFiles(path)
    val pdfRdd = fileData.map{f => pdfRetrieval(f)}
    val df = pdfRdd.toDF
    //Get values
    val model = pipeline.fit(df)
    val result = model.transform(df)
    //Print the predeictions on files
    val kmeansdframe = result.select("prediction")
    val pairs = kmeansdframe.rdd.map{row => (row(0))}
    pairs.saveAsTextFile(outputDestinationFolder)
  }

  //PDF retrieving
  def pdfRetrieval(a: (String, PortableDataStream)) : String = {
    var document : PDDocument = null
    val pdfTextStripper = new PDFTextStripper
    var string = ""
    try {
      document = PDDocument.load(a._2.toArray)
      string = pdfTextStripper.getText(document)
    } catch {
      case e : Exception => e.printStackTrace
    }
    if(document != null) {
      document.close
    }
    return string
  }

}
