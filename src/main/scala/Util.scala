package com.eddsteel.msgpackpoc
import _root_.java.io.ByteArrayOutputStream
import _root_.org.msgpack.core

object Util {
  // https://github.com/msgpack/msgpback-java/blob/3f7ae96909a849fea6878367069f532bd8871bea/msgpack-core/src/test/scala/org/msgpack/core/MessagePackSpec.scala#L41-L49
  def packJava(f: core.MessagePacker => core.MessagePacker): Array[Byte] = {
    val b = new ByteArrayOutputStream()
    val packer = core.MessagePack.newDefaultPacker(b)
    val _ = f(packer)
    packer.close()
    b.toByteArray
  }
}
