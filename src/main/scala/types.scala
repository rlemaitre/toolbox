package com.rlemaitre.toolbox

object types:
    type Maybe[T]             = T | Absent.type
    type Point[T, Dim <: Int] = (T, T)

    case object Absent

    extension [T](value: Maybe[T])
        inline def isAbsent: Boolean                      = value == Absent
        inline def isPresent: Boolean                     = !value.isAbsent
        inline def get: T                                 = value match
            case Absent => throw new NoSuchElementException("Absent.get")
            case v: T   => v
        inline def orElse(other: Maybe[T]): Maybe[T]      = value match
            case Absent => other
            case _      => value
        inline def or(default: T): T                      = value match
            case Absent => default
            case v: T   => v
        inline def toOption: Option[T]                    = value match
            case Absent => None
            case v: T   => Some(v)
        inline def toList: List[T]                        = value match
            case Absent => Nil
            case v: T   => List(v)
        inline def map[U](f: T => U): Maybe[U]            = value match
            case Absent => Absent
            case v: T   => f(v)
        inline def flatMap[U](f: T => Maybe[U]): Maybe[U] = value match
            case Absent => Absent
            case v: T   => f(v)
        inline def fold[U](ifAbsent: => U)(f: T => U): U  = value match
            case Absent => ifAbsent
            case v: T   => f(v)
        inline def filter(f: T => Boolean): Maybe[T]      = value match
            case Absent => Absent
            case v: T   => if f(v) then v else Absent
        inline def exists(f: T => Boolean): Boolean       = value match
            case Absent => false
            case v: T   => f(v)
        inline def forall(f: T => Boolean): Boolean       = value match
            case Absent => true
            case v: T   => f(v)
        inline def contains(elem: T): Boolean             = value match
            case Absent => false
            case v: T   => v == elem
        inline def foreach(f: T => Unit): Unit            = value match
            case Absent => ()
            case v: T   => f(v)
        inline def toSeq: Seq[T]                          = value match
            case Absent => Seq.empty
            case v: T   => Seq(v)
        inline def toSet: Set[T]                          = value match
            case Absent => Set.empty
            case v: T   => Set(v)
        inline def toMap[U](f: T => (U, T)): Map[U, T]    = value match
            case Absent => Map.empty
            case v: T   => Map(f(v))
    end extension

    object Maybe:
        def apply[T](value: T): Maybe[T] = value
        def absent[T]: Maybe[T]          = Absent
    end Maybe
end types
