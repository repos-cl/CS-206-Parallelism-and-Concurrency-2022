package midterm

import midterm.instrumentation.Monitor

// Questions 22-24

// See tests in midterm.Part7Test.
// Run with `sbt "testOnly midterm.Part7Test"`.

class NIC(private val _index: Int, private var _assigned: Boolean)
    extends Monitor:
  def index = _index
  def assigned = _assigned
  def assigned_=(v: Boolean) = _assigned = v

class NICManager(n: Int):
  // Creates a list with n NICs
  val nics = (for i <- 0 until n yield NIC(i, false)).toList

  // This method might be called concurrently
  def assignNICs(limitRecvNICs: Boolean = false): (Int, Int) =
    var recvNIC: Int = 0
    var sendNIC: Int = 0
    var gotRecvNIC: Boolean = false
    var gotSendNIC: Boolean = false

    /// Obtaining receiving NIC...
    while !gotRecvNIC do
      nics(recvNIC).synchronized {
        if !nics(recvNIC).assigned then
          nics(recvNIC).assigned = true
          gotRecvNIC = true
        else recvNIC = (recvNIC + 1) % (if limitRecvNICs then n - 1 else n)
      }
    // Successfully obtained receiving NIC

    // Obtaining sending NIC...
    while !gotSendNIC do
      nics(sendNIC).synchronized {
        if !nics(sendNIC).assigned then
          nics(sendNIC).assigned = true
          gotSendNIC = true
        else sendNIC = (sendNIC + 1) % n
      }
    // Successfully obtained sending NIC

    return (recvNIC, sendNIC)
