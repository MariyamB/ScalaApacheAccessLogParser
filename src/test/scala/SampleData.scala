package com.ddos.accesslogparser

object SampleCombinedAccessLogRecords {

  val data = """
 155.156.103.181 - - [25/May/2015:23:11:15 +0000] "GET / HTTP/1.0" 200 3557 "-" "Opera/9.00 (Windows NT 5.1; U; en)"
 155.156.140.104 - - [25/May/2015:23:11:15 +0000] "GET / HTTP/1.0" 200 3557 "-" "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.8.1.8) Gecko/20071004 Iceweasel/2.0.0.8 (Debian-2.0.0.6+2.0.0.8-Oetch1)"
 155.157.240.217 - - [25/May/2015:23:11:15 +0000] "GET / HTTP/1.0" 200 3557 "-" "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)"
 155.157.28.63 - - [25/May/2015:23:11:15 +0000] "GET / HTTP/1.0" 200 3557 "-" "Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; fr-FR; rv:1.7.8) Gecko/20050511 Firefox/1.0.4"
""".split("\n").filter(_ != "")

  val badRecord = """
66.249.70.10 - - [25/May/2015:23:11:15 -0000] "GET /blog/post/java/how-load-multiple-spring-context-files-standalone/ HTTP/1.0" 200 - "-" "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; ja-jp) AppleWebKit/417.9 (KHTML, like Gecko) Safari/417.8)"
""".split("\n").filter(_ != "")


}

