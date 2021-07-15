package f3

import instrumentation._

import scala.collection.mutable
import scala.collection.concurrent.TrieMap

type FileName = String

/** An API for manipulating files. */
trait FileSystem:
  /** Create a new file named `file` with the passed `content`. */
  def createFile(file: FileName, content: String): Unit
  /** If `file` exists, return its content, otherwise crashes. */
  def readFile(file: FileName): String
  /** If `file` exists, delete it, otherwise crash. */
  def deleteFile(file: FileName): Unit
end FileSystem

/** An in-memory file system for testing purposes implemented using a Map.
 *
 *  Every method in this class is thread-safe.
 */
class InMemoryFileSystem extends FileSystem:
  val fsMap: mutable.Map[FileName, String] = TrieMap()

  def createFile(file: FileName, content: String): Unit =
    assert(!fsMap.contains(file), s"$file already exists")
    fsMap(file) = content

  def readFile(file: FileName): String =
    fsMap.get(file) match
      case Some(content) => content
      case None => assert(false, s"Attempt to read non-existing $file")

  def deleteFile(file: FileName): Unit =
    fsMap.remove(file)

end InMemoryFileSystem
