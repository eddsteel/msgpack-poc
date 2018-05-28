package com.eddsteel.msgpackpoc
import _root_.org.scalacheck.Arbitrary.arbitrary
import _root_.org.scalacheck.Gen

trait MPFormatGenerators {
  val genIntFx = Gen.choose(-32, 127) :| "fixnum"
  val genInt8 = Gen.choose(-128, -33) :| "int8"
  val genInt16 = Gen.choose(-32768, -129) :| "int16"
  val genInt32 = Gen.choose(-2147483648, -32769) :| "int32"
  val genInt64 = arbitrary[Long].suchThat(_ < -2147483649L)
  val genUInt8 = Gen.choose(128, 2 * Byte.MaxValue - 1) :| "uint8"
  val genUInt16 = Gen.choose(2 * Byte.MaxValue, 2 * Short.MaxValue - 1) :| "uint16"
  val genUInt32 = Gen.choose(2 * Short.MaxValue.toLong, 2 * Int.MaxValue.toLong - 1) :| "uint32"
  val genUInt64 = arbitrary[BigInt].suchThat { bi =>
    2 * BigInt(Int.MaxValue) <= bi && bi <= 2 * BigInt(Long.MaxValue) * 2 - 1
  } :| "uint64"

  val genFloat32 = Gen.choose(Float.MinValue, Float.MaxValue) :| "float32"
  val genFloat64 = Gen.choose(Double.MinValue, Double.MaxValue) :| "float32"
}
