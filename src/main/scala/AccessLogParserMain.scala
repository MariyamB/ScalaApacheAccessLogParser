object KafkaProducerScala extends App {

  import java.util.Properties
  import org.apache.spark.SparkContext
  import org.apache.spark.SparkContext._
  import org.apache.spark.SparkConf
  import org.apache.kafka.clients.producer._
  import com.ddos.accesslogparser._
  import org.apache.spark.sql.SparkSession
  val conf = new SparkConf().setAppName("AccessLogParser").setMaster("local[4]") //Running spark in the pseudo cluster mode.
  val sc = new SparkContext(conf)
  val spark = SparkSession
    .builder()
    .appName("Spark App")
    .config("spark.master", "local")
    .getOrCreate()
  val p = new AccessLogParser
  val log = sc.textFile("hdfs://quickstart.cloudera:8020/user/cloudera/apache-access-log.txt")
  val uris = log.map(p.parseRecordReturningNullObjectOnFailure(_).request).filter(_ != "").map(_.split(" ")(1))//Parsing the log file
  for {line <- log
       if p.parseRecord(line) == None
  } yield line
  val uriCount = log.map(p.parseRecordReturningNullObjectOnFailure(_).request)
                 .filter(request => request != "")  // filter out records that wouldn't parse properly
                 .map(_.split(" ")(1))              // get the uri field
                 .map(uri => (uri, 1))              // create a tuple for each record
                 .reduceByKey((a, b) => a + b)      // reduce to get this for each record: (/java/java_oo/up.png,2)
                 .collect                           // convert to Array[(String, Int)], which is Array[(URI, numOccurrences)]
  val uriCounts = log.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey((a, b) => a + b)//Taking the count of the fields
  uriCounts.take(10).foreach(println)//Printing the first en lines of the log file
  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")//Kafka Properties
  props.put("acks","1")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")//Serialize the key to bytes
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")//Serialize the value to objects

  val producer = new KafkaProducer[String, String](props)//Creating object for kafka producer

  val topic="ddos_ph"//Topic name

  uriCounts.take(100000).foreach(a => { //Pushing messages into the producer
    val message = new ProducerRecord[String, String](topic, null, a.toString)
    producer.send(message)})

  producer.close()//Closing the producer
  sc.stop//Stopping the Spark Context object
}