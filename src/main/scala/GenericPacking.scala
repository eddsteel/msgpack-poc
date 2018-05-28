package com.eddsteel.msgpackpoc

import cats.implicits._
import shapeless._
import shapeless.labelled.FieldType

trait GenericPacking {

  implicit val hNilEncoder: Packing[HNil] = Packing.empty

  implicit def hListPacking[K <: Symbol, H, T <: HList](
    implicit witness: Witness.Aux[K],
    hPacking: Lazy[Packing[H]],
    tPacking: Packing[T],
    fieldPacking: Packing[String]): Packing[FieldType[K, H] :: T] = Packing.instance {
    case h :: t =>
      val fieldName = witness.value.name
      val key = fieldPacking.pack(fieldName)
      val value = hPacking.value.pack(h)
      tPacking.pack(t) match {
        case Some(MPMap32(values)) =>
          (key, value).mapN {
            case (k, v) =>
              MPMap32(values.updated(k, v))
          }
        case None =>
          (key, value).mapN {
            case (k, v) =>
              MPMap32(Map(k -> v))
          }
        case _ => None
      }
    case _ => None
  }

  implicit val cNilPacking: Packing[CNil] = Packing.empty

  implicit def coproductEncoder[K <: Symbol, H, T <: Coproduct](
    implicit
    witness: Witness.Aux[K],
    hPacking: Lazy[Packing[H]],
    tPacking: Packing[T],
    kPacking: Packing[String]): Packing[FieldType[K, H] :+: T] =
    Packing.instance {
      case Inl(h) =>
        (kPacking.pack(witness.value.name), hPacking.value.pack(h)).mapN {
          case (k, v) =>
            MPMap32(Map(k -> v))
        }
      case Inr(t) =>
        (kPacking.pack(witness.value.name), tPacking.pack(t)).mapN {
          case (k, v) =>
            MPMap32(Map(k -> v))
        }
    }

  implicit def genericPacking[A, R <: HList](
    implicit gen: LabelledGeneric.Aux[A, R],
    packing: Lazy[Packing[R]]): Packing[A] =
    Packing.instance(a => packing.value.pack(gen.to(a)))
}
