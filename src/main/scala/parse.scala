package com.rlemaitre.toolbox

import scala.annotation.targetName
import scala.compiletime.{erasedValue, summonInline}
import scala.deriving.Mirror
import scala.reflect.ClassTag
import scala.util.matching.Regex

object parse:

    @targetName("delimitedBy")
    opaque type -[A, B] <: A = A
    @targetName("regex")
    opaque type ~[A, B] <: A = A

    trait Read[A]:
        def read(input: String): A

    trait ReadProduct[T <: Product]:
        def readProduct(input: Array[String]): T

    trait ReadSeq[C[_]]:
        def readSeq[A: Read: ClassTag](input: Array[String]): C[A]

    object Read:
        def apply[T <: Product: ReadProduct](regex: Regex): Read[T] = (input: String) =>
            regex.unapplySeq(input) match
                case Some(fields) => summon[ReadProduct[T]].readProduct(fields.toArray)
                case None         => throw new Exception(s"Regex '${regex.regex}' did not match '$input'")
    end Read

    object ReadProduct:
        // noinspection ConvertExpressionToSAM
        inline given derived[T <: Product](using m: Mirror.ProductOf[T]): ReadProduct[T] =
            new ReadProduct[T]:
                def readProduct(input: Array[String]): T =
                    m.fromProduct(fold[m.MirroredElemTypes](input))

        private inline def fold[T <: Tuple](input: Array[String]): Tuple =
            inline erasedValue[T] match
                case _: EmptyTuple => EmptyTuple
                case _: (t *: ts)  => summonInline[Read[t]].read(input.head) *: fold[ts](input.tail)
    end ReadProduct

    given Read[Int] with
        def read(input: String): Int =
            input.toInt

    given Read[Long] with
        def read(input: String): Long =
            input.toLong

    given Read[BigInt] with
        def read(input: String): BigInt =
            BigInt(input)

    given Read[String] with
        def read(input: String): String = input

    given Read[Char] with
        def read(input: String): Char =
            if input.length == 1 then input.head else throw new Exception(s"Unable to parse $input into a Char")

    given delimitedProduct[B <: String: ValueOf, T <: Product: ReadProduct]: Read[-[T, B]] with
        def read(input: String): -[T, B] =
            summon[ReadProduct[T]]
                .readProduct(input.split(valueOf[B]))
                .asInstanceOf[-[T, B]]
    end delimitedProduct

    given regexProduct[B <: String: ValueOf, T <: Product: ReadProduct]: Read[~[T, B]] with
        def read(input: String): ~[T, B] =
            val regex = new Regex(valueOf[B])
            regex.unapplySeq(input) match
                case Some(fields) => summon[ReadProduct[T]].readProduct(fields.toArray)
                case None         => throw new Exception(s"Regex '${valueOf[B]}' did not match '$input'")
        end read
    end regexProduct

    given ReadSeq[List] with
        def readSeq[A: Read: ClassTag](input: Array[String]): List[A] =
            input.map(summon[Read[A]].read).toList

    given ReadSeq[Vector] with
        def readSeq[A: Read: ClassTag](input: Array[String]): Vector[A] =
            input.map(summon[Read[A]].read).toVector

    given ReadSeq[Set] with
        def readSeq[A: Read: ClassTag](input: Array[String]): Set[A] =
            input.map(summon[Read[A]].read).toSet

    given delimitedSeq[C[_]: ReadSeq, A: Read: ClassTag, B <: String: ValueOf]: Read[C[A] - B] with
        def read(input: String): C[A] - B =
            summon[ReadSeq[C]].readSeq[A](input.split(valueOf[B]))

    given regexSeq[C[_]: ReadSeq, A: ClassTag, B <: String: ValueOf](using Read[A ~ B]): Read[C[A] ~ B] with
        def read(input: String): C[A] ~ B =
            val regex    = new Regex(valueOf[B])
            val matches  = regex.findAllMatchIn(input)
            val elements = matches.map(_.matched).toArray
            summon[ReadSeq[C]].readSeq[A ~ B](elements).asInstanceOf[C[A] ~ B]
        end read
    end regexSeq
end parse
