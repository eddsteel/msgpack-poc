package com.eddsteel.msgpackpoc

trait PackingSyntax {
  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def decorateOps[A: Packing](a: A): PackingOps[A] =
    PackingOps(a)
}

final case class PackingOps[V: Packing](v: V) {
  def encode: Option[MPValue] = MessagePack.encode(v)

  def pack: Array[Byte] = MessagePack.pack(v)
}
