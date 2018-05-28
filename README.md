# msgpack-poc

[![Build Status](https://travis-ci.org/eddsteel/msgpack4s-poc.svg?branch=master)](https://travis-ci.org/eddsteel/msgpack-poc)

This is a proof-of-concept generic message pack library for
scala. Unlike the official java library it works with arbitrary case
classes automatically, and provides an expression-oriented API. Nested
and recursive types are supported, and the codec is constructed at
compile-time. On the other hand, it is very likely to be slower, less
tested and is completely unsupported.

`MessagePack` supports two operations. `encode` which translates a
given value into the AST, and `pack` which encodes and then packs
using `msgpack-core`.

This program

```scala
import com.eddsteel.msgpackpoc.MessagePack
import com.eddsteel.msgpackpoc.implicits._

object Main extends App {
  final case class Arbitrary(i: Int, b: Boolean)

  val values: List[Arbitrary] = List(Arbitrary(1, true), Arbitrary(2, false))

  println(MessagePack.encode(values))
  println(MessagePack.pack(values).mkString("[", ",", "]"))
}
```

Produces
```
Some(MPArrFx(Vector(MPMap32(Map(MPStrFx(b) -> MPTrue, MPStrFx(i) -> MPUInt8(1))), MPMap32(Map(MPStrFx(b) -> MPFalse, MPStrFx(i) -> MPUInt8(2))))))
[-110,-126,-95,98,-61,-95,105,1,-126,-95,98,-62,-95,105,2]
```


## ~Slice 1~

- AST to represent messagepack types.
- Provide generic packing of case classes and support for primitives.
- Use msgpack-core to do the actual packing (i.e. fitting sizes twice over)

## Slice 2

Add some stdlib types.

## Slice 3

Add unpacking.

## Slice 4

Support AST->Bytes more directly.
