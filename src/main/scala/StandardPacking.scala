package com.eddsteel.msgpackpoc
import _root_.cats.implicits._
import _root_.scala.collection.immutable.NumericRange

trait IntegerPacking {
  final val Max: BigInt = BigInt(Long.MaxValue) * 2

  private def fixnums[A](implicit igl: Integral[A]) =
    NumericRange.inclusive[A](igl.fromInt(-31), igl.zero, igl.one)

  private def bytes[A](implicit igl: Integral[A]) =
    NumericRange
      .inclusive[A](igl.fromInt(Byte.MinValue.toInt), igl.fromInt(Byte.MaxValue.toInt), igl.one)

  private def shorts[A](implicit igl: Integral[A]) =
    NumericRange
      .inclusive[A](igl.fromInt(Short.MinValue.toInt), igl.fromInt(Short.MaxValue.toInt), igl.one)

  // Can't build ranges this big.
  private object ints {
    def contains[A](a: A)(implicit igl: Integral[A]): Boolean =
      igl.lteq(igl.fromInt(Int.MinValue), a) && igl.lteq(a, igl.fromInt(Int.MaxValue))
  }

  private object longs {
    def contains(bi: BigInt): Boolean =
      bi <= Long.MaxValue && bi >= Long.MinValue
  }

  private val fixPacking: Packing[Byte] = Packing.total(MPFixInt(_))

  implicit val bytePacking: Packing[Byte] = Packing.instance {
    case b if fixnums[Byte].contains(b) => fixPacking.pack(b)
    case b if b > 0                     => Some(MPUInt8(b.toShort))
    case b                              => Some(MPInt8(b))
  }

  implicit val shortPacking: Packing[Short] = Packing.instance {
    case s if fixnums[Short].contains(s) => fixPacking.pack(s.toByte)
    case s if bytes[Short].contains(s)   => bytePacking.pack(s.toByte)
    case s if s > 0                      => Some(MPUInt8(s))
    case s                               => Some(MPInt16(s))
  }

  implicit val intPacking: Packing[Int] = Packing.instance {
    case i if fixnums[Int].contains(i) => fixPacking.pack(i.toByte)
    case i if bytes[Int].contains(i)   => bytePacking.pack(i.toByte)
    case i if shorts[Int].contains(i)  => shortPacking.pack(i.toShort)
    case i if i > 0                    => Some(MPUInt16(i))
    case i                             => Some(MPInt32(i))
  }

  implicit val longPacking: Packing[Long] = Packing.instance {
    case l if fixnums[Long].contains(l) => fixPacking.pack(l.toByte)
    case l if bytes[Long].contains(l)   => bytePacking.pack(l.toByte)
    case l if shorts[Long].contains(l)  => shortPacking.pack(l.toShort)
    case l if ints.contains[Long](l)    => intPacking.pack(l.toInt)
    case l if l > 0                     => Some(MPUInt32(l))
    case l                              => Some(MPInt64(l))
  }

  implicit val bigIntegerPacking: Packing[BigInt] = Packing.instance {
    case bi if fixnums[BigInt].contains(bi) => fixPacking.pack(bi.toByte)
    case bi if bytes[BigInt].contains(bi)   => bytePacking.pack(bi.toByte)
    case bi if shorts[BigInt].contains(bi)  => shortPacking.pack(bi.toShort)
    case bi if ints.contains[BigInt](bi)    => intPacking.pack(bi.toInt)
    case bi if longs.contains(bi)           => longPacking.pack(bi.toLong)
    case bi if bi <= Max                    => Some(MPUInt64(bi))
    case _                                  => None
  }
}

trait FloatPacking {
  implicit val floatPacking: Packing[Float] = Packing.total(f => MPFloat32(f))
  implicit val doublePacking: Packing[Double] = Packing.total(d => MPFloat64(d))
}

trait StringPacking {
  // UTF-8 strings, so "length" in bits is 2 * chars
  // which brings us back to signed int boundaries, which is nice.
  implicit val stringPacking: Packing[String] = Packing.total {
    case s if s.length <= 15             => MPStrFx(s)
    case s if s.length <= Byte.MaxValue  => MPStr8((s.length * 2).toShort, s)
    case s if s.length <= Short.MaxValue => MPStr16(s.length * 2, s)
    case s if s.length <= Int.MaxValue   => MPStr32(s.length.toLong * 2, s)
    // if the string's longer you're not on the JVM
  }

  implicit val symbolPacking: Packing[Symbol] = stringPacking.contramap(_.name)
}

trait StandardPacking
    extends IntegerPacking
    with FloatPacking
    with StringPacking
    with CollectionPacking
    with GenericPacking {
  implicit val idPacking: Packing[MPValue] = Packing.total(identity)
  implicit val nullPacking: Packing[Null] = Packing.const(MPNil)
  implicit val booleanPacking: Packing[Boolean] =
    Packing.cond[Boolean](identity)(Packing.const(MPTrue), Packing.const(MPFalse))

}

object StandardPacking extends StandardPacking
