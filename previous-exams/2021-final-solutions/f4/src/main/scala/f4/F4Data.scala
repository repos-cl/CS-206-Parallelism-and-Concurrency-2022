package f4

import scala.io.Source
import scala.io.Codec

object F4Data {
  val punctuationRx = "[,:;?!.'\"]+".r

  def linesIterator: Iterator[String] = {
    Option(getClass.getResourceAsStream("/f4/shakespeare.txt")) match {
      case Some(resource) =>
        DoubleLineIterator(Source.fromInputStream(resource)(Codec.UTF8))
      case None =>
        throw new RuntimeException("The resource with the corpus is unexpectedly missing, please inform the staff.")
    }
  }

  def lines = linesIterator.toSeq
}

class DoubleLineIterator(underlying: Iterator[Char]) extends scala.collection.AbstractIterator[String] with Iterator[String] {
  private[this] val sb = new StringBuilder

  lazy val iter: BufferedIterator[Char] = underlying.buffered

  def getc(): Boolean = iter.hasNext && {
    val ch = iter.next()
    if (ch == '\n') {
      val has = iter.hasNext
      val ch2 = if has then iter.next() else ' '
      if (has && ch2 == '\n')
        false
      else {
        sb append ' '
        sb append ch2
        true
      }
    } else {
      sb append ch
      true
    }
  }
  def hasNext: Boolean = iter.hasNext
  def next(): String = {
    sb.clear()
    while (getc()) { }
    sb.toString
  }
}
