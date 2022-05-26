package concpar21final03

import instrumentation.*

class UpdateServer(fs: FileSystem) extends Monitor:
  val rcu = new RCU

  /** The name of the file containing the latest update.
    *
    * This is `@volatile` to guarantee that `fetchUpdate` always sees the latest
    * filename.
    */
  @volatile private var updateFile: Option[FileName] = None

  /** Return the content of the latest update if one is available, otherwise
    * None.
    *
    * This method is thread-safe.
    */
  def fetchUpdate(): Option[String] =
    // TODO: use `rcu`
    updateFile.map(fs.readFile)

  /** Define a new update, more precisely this will:
    *   - Create a new update file called `newName` with content `newContent`
    *   - Ensure that any future call to `fetchUpdate` returns the new update
    *     content.
    *   - Delete the old update file.
    *
    * This method is _NOT_ thread-safe, it cannot be safely called from multiple
    * threads at once.
    */
  def newUpdate(newName: FileName, newContent: String): Unit =
    // TODO: use `rcu`
    val oldFile = updateFile
    fs.createFile(newName, newContent)
    updateFile = Some(newName)
    oldFile.foreach(fs.deleteFile)

end UpdateServer
