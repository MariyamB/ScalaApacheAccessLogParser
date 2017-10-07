package com.ddos.accesslogparser

import org.scalatest.FunSpec
import org.scalatest.BeforeAndAfter
import org.scalatest.GivenWhenThen
import java.util.Calendar

class ApacheCombinedAccessLogRecordSpec extends FunSpec with BeforeAndAfter with GivenWhenThen {

  var records: Seq[String] = _

  before {
      records = SampleCombinedAccessLogRecords.data
  }

  describe("Testing the first access log record ...") {
      it("the data fields should be correct") {
          Given("the first sample log record")
          records = SampleCombinedAccessLogRecords.data
          val parser = new AccessLogParser
          val rec = parser.parseRecord(records(0))
          println("IP ADDRESS: " + rec.get.clientIpAddress)
          Then("parsing record(0) should not return None")
              assert(rec != None)
          And("the ip address should be correct")
              assert(rec.get.clientIpAddress == "155.156.103.181")
          And("client identity")
              assert(rec.get.rfc1413ClientIdentity == "-")
          And("remote user")
              assert(rec.get.remoteUser == "-")
          And("date/time")
              assert(rec.get.dateTime == "[25/May/2015:23:11:15 +0000]")
          And("request")
              assert(rec.get.request == "GET / HTTP/1.0")
          And("status code should be 200")
              assert(rec.get.httpStatusCode == "200")
          And("bytes sent should be 16731")
              assert(rec.get.bytesSent == "3557")
          And("referer")
              assert(rec.get.referer == "-")
          And("user agent")
              assert(rec.get.userAgent == "Opera/9.00 (Windows NT 5.1; U; en)")
      }
  }

  describe("Testing a second access log record ...") {
      records = SampleCombinedAccessLogRecords.data
      val parser = new AccessLogParser
      val rec = parser.parseRecord(records(1))
      it("the result should not be None") {
          assert(rec != None)
      }
      it("the individual fields should be right") {
          rec.foreach { r =>
              assert(r.clientIpAddress == "155.156.140.104")
              assert(r.rfc1413ClientIdentity == "-")
              assert(r.remoteUser == "-")
              assert(r.dateTime == "[25/May/2015:23:11:15 +0000]")
              assert(r.request == "GET / HTTP/1.0")
              assert(r.httpStatusCode == "200")
              assert(r.bytesSent == "3557")
              assert(r.referer == "-")
              assert(r.userAgent == "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.8.1.8) Gecko/20071004 Iceweasel/2.0.0.8 (Debian-2.0.0.6+2.0.0.8-Oetch1)")
          }
      }
  }

  describe("Trying to parse a record I used to fail on ...") {
      records = SampleCombinedAccessLogRecords.badRecord
      val parser = new AccessLogParser
      val rec = parser.parseRecord(records(0))
      it("the result should not be None") {
          assert(rec != None)
      }
      it("the individual fields should be right") {
          rec.foreach { r =>
              assert(r.clientIpAddress == "66.249.70.10")
              assert(r.rfc1413ClientIdentity == "-")
              assert(r.remoteUser == "-")
              assert(r.dateTime == "[25/May/2015:23:11:15 -0000]")
              assert(r.request == "GET /blog/post/java/how-load-multiple-spring-context-files-standalone/ HTTP/1.0")
              assert(r.httpStatusCode == "200")
              assert(r.bytesSent == "-")
              assert(r.referer == "-")
              assert(r.userAgent == "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; ja-jp) AppleWebKit/417.9 (KHTML, like Gecko) Safari/417.8)")
          }
      }
  }

  describe("Testing the parseRecordReturningNullObjectOnFailure method with a valid record ...") {
      records = SampleCombinedAccessLogRecords.data
      val parser = new AccessLogParser
      val rec = parser.parseRecordReturningNullObjectOnFailure(records(2))
      it("the result should not be null") {
          assert(rec != null)
      }
      it("the individual fields should be right") {
          assert(rec.clientIpAddress == "155.157.240.217")
          assert(rec.rfc1413ClientIdentity == "-")
          assert(rec.remoteUser == "-")
          assert(rec.dateTime == "[25/May/2015:23:11:15 +0000]")
          assert(rec.request == "GET / HTTP/1.0")
          assert(rec.httpStatusCode == "200")
          assert(rec.bytesSent == "3557")
          assert(rec.referer == "-")
          assert(rec.userAgent == "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)")
      }
  }

  describe("Testing the parseRecordReturningNullObjectOnFailure method with an invalid record ...") {
      val parser = new AccessLogParser
      val rec = parser.parseRecordReturningNullObjectOnFailure("foo bar baz")
      it("the result should not be null") {
          assert(rec != null)
      }
      it("the individual fields should be blank strings") {
          assert(rec.clientIpAddress == "")
          assert(rec.rfc1413ClientIdentity == "")
          assert(rec.remoteUser == "")
          assert(rec.dateTime == "")
          assert(rec.request == "")
          assert(rec.httpStatusCode == "")
          assert(rec.bytesSent == "")
          assert(rec.referer == "")
          assert(rec.userAgent == "")
      }
  }

  describe("Parsing the request field ...") {
      it("a simple request should work") {
          val req = "GET / HTTP/1.0"
          val result = AccessLogParser.parseRequestField(req)
          assert(result != None)
          result.foreach { res =>
              val (requestType, uri, httpVersion) = res
              assert(requestType == "GET")
              assert(uri == "/")
              assert(httpVersion == "HTTP/1.0")
          }
      }
      it("an invalid request should return blanks") {
          val req = "foobar"
          val result = AccessLogParser.parseRequestField(req)
          assert(result == None)
      }
  }

  describe("Parsing the date field ...") {
      it("a valid date field should work") {
          val date = AccessLogParser.parseDateField("[25/May/2015:11:11:15 -0700]]")
          assert(date != None)
          date.foreach { d =>
              val cal = Calendar.getInstance
              cal.setTimeInMillis(d.getTime)
              assert(cal.get(Calendar.DAY_OF_MONTH) == 25)
              assert(cal.get(Calendar.MONTH) == 4)  // 0-based
              assert(cal.get(Calendar.YEAR) == 2015)
              assert(cal.get(Calendar.HOUR) == 11)
              assert(cal.get(Calendar.MINUTE) == 11)
              assert(cal.get(Calendar.SECOND) == 15)
          }
      }
      it("an invalid date field should return None") {
          val date = AccessLogParser.parseDateField("[foo bar]")
          assert(date == None)
      }
  }

}












