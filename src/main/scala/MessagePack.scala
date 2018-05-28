package com.eddsteel.msgpackpoc
import _root_.org.msgpack.core

object MessagePack {
  def encode[A: Packing](a: A): Option[MPValue] = Packing[A].pack(a)

  def pack[A: Packing](a: A): Array[Byte] =
    encode(a).fold(Array.empty[Byte])(defaultSerialization)

  // quick n dirty n inefficient -- we've already figured out which MP format to use and this will
  // go do that again. Convert to baos functions
  def defaultSerialization(value: MPValue): Array[Byte] =
    Util.packJava(javaSerializer(value))

  @SuppressWarnings(Array("org.wartremover.warts.Recursion")) // the hope is this is temporary
  private def javaSerializer: MPValue => core.MessagePacker => core.MessagePacker = {
    case MPNil             => _.packNil
    case MPUnused          => identity
    case MPFixInt(value)   => _.packByte(value)
    case MPUInt8(value)    => _.packShort(value)
    case MPUInt16(value)   => _.packInt(value)
    case MPUInt32(value)   => _.packLong(value)
    case MPUInt64(value)   => _.packBigInteger(value.bigInteger)
    case MPInt8(value)     => _.packByte(value)
    case MPInt16(value)    => _.packShort(value)
    case MPInt32(value)    => _.packInt(value)
    case MPInt64(value)    => _.packLong(value)
    case MPTrue            => _.packBoolean(true)
    case MPFalse           => _.packBoolean(false)
    case MPFloat32(value)  => _.packFloat(value)
    case MPFloat64(value)  => _.packDouble(value)
    case MPStrFx(value)    => _.packString(value)
    case MPStr8(_, value)  => _.packString(value)
    case MPStr16(_, value) => _.packString(value)
    case MPStr32(_, value) => _.packString(value)
    case MPArrFx(values) =>
      val serializeLength = (p: core.MessagePacker) => p.packArrayHeader(values.length)
      val serializeValues = values.map(javaSerializer)
      serializeLength.andThen(serializeValues.reduceRightOption(_.andThen(_)).getOrElse(identity))

    case MPArr16(values) =>
      val serializeLength = (p: core.MessagePacker) => p.packArrayHeader(values.length)
      val serializeValues = values.map(javaSerializer)
      serializeLength.andThen(serializeValues.reduceRightOption(_.andThen(_)).getOrElse(identity))

    case MPArr32(values) =>
      val serializeLength = (p: core.MessagePacker) => p.packArrayHeader(values.length)
      val serializeValues = values.map(javaSerializer)
      serializeLength.andThen(serializeValues.reduceRightOption(_.andThen(_)).getOrElse(identity))

    case MPMapFx(values) =>
      val serializeLength = (p: core.MessagePacker) => p.packMapHeader(values.size)
      val serializeValues = values.toIterable.map {
        case (k, v) => javaSerializer(k).andThen(javaSerializer(v))
      }
      serializeLength.andThen(serializeValues.reduceRightOption(_.andThen(_)).getOrElse(identity))

    case MPMap16(values) =>
      val serializeLength = (p: core.MessagePacker) => p.packMapHeader(values.size)
      val serializeValues = values.toIterable.map {
        case (k, v) => javaSerializer(k).andThen(javaSerializer(v))
      }
      serializeLength.andThen(serializeValues.reduceRightOption(_.andThen(_)).getOrElse(identity))

    case MPMap32(values) =>
      val serializeLength = (p: core.MessagePacker) => p.packMapHeader(values.size)
      val serializeValues = values.toIterable.map {
        case (k, v) => javaSerializer(k).andThen(javaSerializer(v))
      }
      serializeLength.andThen(serializeValues.reduceRightOption(_.andThen(_)).getOrElse(identity))

    case _ => ??? /*
    case MPBin8(value) =>
    case MPBin16(value) =>
    case MPBin32(value) =>
    case MPExtFx(int, value) =>
    case MPExt8(int, value) =>
    case MPExt16(int, value) =>
p    case MPExt32(int, value) =>*/
  }
}
