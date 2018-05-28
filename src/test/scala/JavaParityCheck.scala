package com.eddsteel.msgpackpoc
import _root_.cats.Eq
import _root_.cats.implicits._
import _root_.java.util.Arrays
import _root_.org.scalacheck.Gen
import _root_.org.scalacheck.Prop.forAll
import _root_.org.scalacheck.Properties
import implicits._
import Util._
import scala.collection.mutable.LinkedHashMap

object JavaParitySpecification extends Properties("Java Parity") with MPFormatGenerators {
  // Test byte arrays are of equal value using java's fast array equals
  implicit val EqByteArray: Eq[Array[Byte]] = Eq.instance((a, b) => Arrays.equals(a, b))

  property("nil") = forAll(Gen.const(null)) { n =>
    MessagePack.pack(n) === packJava(_.packNil)
  }

  property("boolean") = forAll { b: Boolean =>
    MessagePack.pack(b) === packJava(_.packBoolean(b))
  }

  property("fixnum") = forAll(Gen.choose(-32, 127)) { i =>
    MessagePack.pack(i) === packJava(_.packByte(i.toByte))
  }

  property("int8") = forAll(genInt8) { i =>
    MessagePack.pack(i) === packJava(_.packByte(i.toByte))
  }

  property("int16") = forAll(genInt16) { i =>
    MessagePack.pack(i) === packJava(_.packShort(i.toShort))
  }

  property("int32") = forAll(genInt32) { i =>
    MessagePack.pack(i) === packJava(_.packInt(i))
  }

  property("int64") = forAll(genInt64) { l =>
    MessagePack.pack(l) === packJava(_.packLong(l))
  }

  property("uint8") = forAll(genUInt8) { i =>
    MessagePack.pack(i) === packJava(_.packShort(i.toShort))
  }

  property("uint16") = forAll(genUInt16) { i =>
    MessagePack.pack(i) === packJava(_.packInt(i))
  }

  property("uint32") = forAll(genUInt32) { l =>
    MessagePack.pack(l) === packJava(_.packLong(l))
  }

  property("uint64") = forAll(genUInt64) { bi =>
    MessagePack.pack(bi) === packJava(_.packBigInteger(bi.bigInteger))
  }

  property("float32") = forAll(genFloat32) { f =>
    MessagePack.pack(f) === packJava(_.packFloat(f))
  }

  property("float64") = forAll(genFloat64) { d =>
    MessagePack.pack(d) === packJava(_.packDouble(d))
  }

  property("string") = forAll { s: String =>
    MessagePack.pack(s) === packJava(_.packString(s))
  }

  property("symbol") = forAll { s: String =>
    MessagePack.pack(Symbol(s)) === packJava(_.packString(s))
  }

  property("vector") = forAll { v: Vector[Int] =>
    MessagePack.pack(v) === packJava { packer =>
      packer.packArrayHeader(v.length)
      v.foreach(i => packer.packInt(i))
      packer
    }
  }

// this is currently a bit tricky to test, as order will be different.
//  property("map") = forAll { m: Map[Int, Int] =>
//    MessagePack.pack(m) === packJava { packer =>
//      packer.packMapHeader(m.size)
//      m.foreach {
//        case (k, v) =>
//          packer.packInt(k)
//          packer.packInt(v)
//      }
//      packer
//    }
//  }
}
