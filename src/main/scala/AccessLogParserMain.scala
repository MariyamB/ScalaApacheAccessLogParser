object KafkaProducerScala extends App {

  import java.util.Properties
  import org.apache.spark.SparkContext
  import org.apache.spark.SparkContext._
  import org.apache.spark.SparkConf
  import org.apache.kafka.clients.producer._
  import com.ddos.accesslogparser._
  import org.apache.spark.sql.SparkSession
  val conf = new SparkConf().setAppName("AccessLogParser").setMaster("local[4]")
  val sc = new SparkContext(conf)
  val spark = SparkSession
    .builder()
    .appName("Java Spark SQL basic example")
    .config("spark.master", "local")
    .getOrCreate()
  val p = new AccessLogParser
  val log = sc.textFile("hdfs://quickstart.cloudera:8020/user/cloudera/apache-access-log.txt")
  val uris = log.map(p.parseRecordReturningNullObjectOnFailure(_).request).filter(_ != "").map(_.split(" ")(1))
  for {line <- log
       if p.parseRecord(line) == None
  } yield line
  val uriCount = log.map(p.parseRecordReturningNullObjectOnFailure(_).request).filter(request => request != "").map(_.split(" ")(1)).map(uri => (uri, 1)).reduceByKey((a, b) => a + b).collect
  val wordCounts = log.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey((a, b) => a + b)
  wordCounts.take(10).foreach(println)


  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("acks","1")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  val topic="ddos_ph"


  for(i<- 0 to 150) {
    val record = new ProducerRecord(topic, "key"+i, "value"+i)
    producer.send(record)
  }

  /*wordCounts.collect().foreach(a => {
    val message = new ProducerRecord[String, String](kafkaOpTopic, null, a.toString)
    producer.send(message)})*/
  producer.close()
}