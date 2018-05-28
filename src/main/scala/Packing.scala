package com.eddsteel.msgpackpoc
import _root_.cats.Contravariant

trait Packing[A] {
  def pack(value: A): Option[MPValue]
}

object Packing extends PackingInstances {
  def apply[A](implicit ev: Packing[A]): Packing[A] = ev

  def instance[A](packFn: A => Option[MPValue]): Packing[A] = new Packing[A] {
    override def pack(value: A): Option[MPValue] =
      packFn(value)
  }

  def empty[A]: Packing[A] = Packing.instance(_ => None)

  def total[A](packFn: A => MPValue): Packing[A] = Packing.instance(a => Some(packFn(a)))

  def const[A](as: MPValue): Packing[A] = Packing.total(_ => as)

  def cond[A](condition: A => Boolean)(ifTrue: Packing[A], ifFalse: Packing[A]): Packing[A] =
    Packing.instance { a =>
      if (condition(a)) ifTrue.pack(a) else ifFalse.pack(a)
    }
}

trait PackingInstances {

  implicit val contra: Contravariant[Packing] = new Contravariant[Packing] {
    override def contramap[A, B](packing: Packing[A])(f: B => A): Packing[B] =
      Packing.instance { a =>
        packing.pack(f(a))
      }
  }
}
