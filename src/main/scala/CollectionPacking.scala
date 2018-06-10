package com.eddsteel.msgpackpoc
import _root_.cats.implicits._
import scala.collection.mutable.ArrayBuffer

trait CollectionPacking {
  implicit def optionPacking[A](implicit ev: Packing[A]): Packing[Option[A]] = Packing.instance {
    case Some(a) => ev.pack(a)
    case None    => Some(MPNil)
  }

  // TODO: put this in a lower priority implicits trait
  /*  implicit def traversable[A, T <: TraversableOnce[A]](implicit ev: Packing[A]): Packing[T] =
    vectorPacking(ev).contramap(_.toVector)
   */

  implicit def listPacking[A](implicit ev: Packing[A]): Packing[List[A]] =
    vectorPacking(ev).contramap(_.toVector)

  implicit def vectorPacking[A](implicit ev: Packing[A]): Packing[Vector[A]] =
    Packing.instance { as =>
      as.traverse(ev.pack).map(CollectionPacking.fitVector)
    }

  implicit def mapPacking[K, V](implicit evk: Packing[K], evv: Packing[V]): Packing[Map[K, V]] =
    Packing.instance { m =>
      val as: Option[Map[MPValue, MPValue]] = m.toList.traverse {
        case (k, v) =>
          (evk.pack(k), evv.pack(v)).mapN((k, v) => (k, v))
      }.map(_.toMap)
      as.map(CollectionPacking.fitMap)
    }
}

@SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures"))
object CollectionPacking extends CollectionPacking {
  def fitVector(elts: Vector[MPValue]): MPValue =
    if (elts.length < 16) MPArrFx(ArrayBuffer.concat(elts))
    else if (elts.length < 65536) MPArr16(ArrayBuffer.concat(elts))
    else MPArr32(ArrayBuffer.concat(elts)) // JVM can only do 2^31 -1 elements

  def fitMap(elts: Map[MPValue, MPValue]): MPValue =
    if (elts.size < 8) MPMapFx(elts)
    else if (elts.size < Short.MaxValue) MPMap16(elts)
    else MPMap32(elts)
}
