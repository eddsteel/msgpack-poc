package com.eddsteel.msgpackpoc
// arrays as case class parameters are problematic
// vectors are noticably slower.
import scala.collection.mutable.ArrayBuffer

sealed trait MPValue
final case object MPNil extends MPValue
final case object MPUnused extends MPValue

sealed trait MPInteger extends MPValue
final case class MPFixInt(value: Byte) extends MPInteger
final case class MPUInt8(value: Short) extends MPInteger
final case class MPUInt16(value: Int) extends MPInteger
final case class MPUInt32(value: Long) extends MPInteger
final case class MPUInt64(value: BigInt) extends MPInteger
final case class MPInt8(value: Byte) extends MPInteger
final case class MPInt16(value: Short) extends MPInteger
final case class MPInt32(value: Int) extends MPInteger
final case class MPInt64(value: Long) extends MPInteger

sealed trait MPBoolean extends MPValue
final case object MPTrue extends MPBoolean
final case object MPFalse extends MPBoolean

sealed trait MPFloat extends MPValue
final case class MPFloat32(value: Float) extends MPFloat
final case class MPFloat64(value: Double) extends MPFloat

sealed trait MPString extends MPValue // bytestring?
final case class MPStrFx(value: String) extends MPString
final case class MPStr8(length: Short, value: String) extends MPString
final case class MPStr16(length: Int, value: String) extends MPString
final case class MPStr32(length: Long, value: String) extends MPString

sealed trait MPBinary extends MPValue
final case class MPBin8(value: ArrayBuffer[Byte]) extends MPBinary
final case class MPBin16(value: ArrayBuffer[Byte]) extends MPBinary
final case class MPBin32(value: ArrayBuffer[Byte]) extends MPBinary

sealed trait MPArray extends MPValue
final case class MPArrFx(value: ArrayBuffer[MPValue]) extends MPArray
final case class MPArr16(value: ArrayBuffer[MPValue]) extends MPArray
final case class MPArr32(value: ArrayBuffer[MPValue]) extends MPArray

sealed trait MPMap extends MPValue
final case class MPMapFx(value: Map[MPValue, MPValue]) extends MPMap
final case class MPMap16(value: Map[MPValue, MPValue]) extends MPMap
final case class MPMap32(value: Map[MPValue, MPValue]) extends MPMap

sealed trait MPExtension extends MPValue
final case class MPExtFx(int: Int, value: ArrayBuffer[Byte]) extends MPExtension
final case class MPExt8(int: Int, value: ArrayBuffer[Byte]) extends MPExtension
final case class MPExt16(int: Int, value: ArrayBuffer[Byte]) extends MPExtension
final case class MPExt32(int: Int, value: ArrayBuffer[Byte]) extends MPExtension
