package com.eddsteel.msgpackpoc
import _root_.cats.Eq
import _root_.cats.implicits._
import _root_.java.util.Arrays
import _root_.org.scalacheck.Arbitrary
import _root_.org.scalacheck.Arbitrary.arbitrary
import _root_.org.scalacheck.Gen
import _root_.org.scalacheck.Prop.forAll
import _root_.org.scalacheck.Properties
import implicits._
import Util._

object ObjectSpecification extends Properties("Objects") {

  final case class Structure[A](
    value: A,
    children: List[Structure[A]],
    parent: Option[Structure[A]],
    weight: BigInt)

  def genStructure[A: Arbitrary]: Gen[Structure[A]] =
    for {
      a <- arbitrary[A]
      d <- arbitrary[Double]
      parent <- if (d < 0.2) genStructure.map(Some.apply) else Gen.const(None)
      weight <- arbitrary[BigInt]
      children = Nil
    } yield Structure(a, children, parent, weight)

  property("encodes arbitrary case classes") = {
    forAll(genStructure[Double]) { structure =>
      val enc = MessagePack.encode(structure)
      enc.isInstanceOf[Option[MPValue]]
    }
  }

}
