package com.rlemaitre.toolbox

import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop.*
import types.*

class MaybeTests extends ScalaCheckSuite:

    val absentGen: Gen[Maybe[Int]]     = Gen.const(Absent)
    val presentIntGen: Gen[Maybe[Int]] = Gen.choose(-1000, 1000)

    property("isAbsent should correctly identify Absent values"):
        forAll(absentGen)(absent => assertEquals(absent.isAbsent, true))
    
    property("isAbsent should correctly identify Present values"):
        forAll(presentIntGen)(value => assertEquals(value.isAbsent, false))
    
    property("isPresent should correctly identify Absent values"):
        forAll(absentGen)(absent => assertEquals(absent.isPresent, false))
    
    property("isPresent should correctly identify Present values"):
        forAll(presentIntGen)(value => assertEquals(value.isPresent, true))
    
    property("get should throw for Absent"):
        forAll(absentGen): absent =>
            val e = intercept[NoSuchElementException]:
                absent.get
            assertEquals(e.getMessage, "Absent.get")
            
    property("get should return the value for Present"):
        forAll(presentIntGen): value =>
            assertEquals(value.get, value)
    
    property("toOption should convert Absent to None"):
        forAll(absentGen): absent =>
            assertEquals(absent.toOption, None)
    
    property("toOption should convert Present to Some"):
        forAll(presentIntGen): value =>
            assertEquals(value.toOption, Some(value.get))
    
    property("toList should convert Absent to empty List"):
        forAll(absentGen): absent =>
            assertEquals(absent.toList, Nil)
    
    property("toList should convert Present to single-element List"):
        forAll(presentIntGen): value =>
            assertEquals(value.toList, List(value))
    
    property("map should not apply function to Absent"):
        forAll(absentGen): absent =>
            assertEquals(absent.map(_ + 1), Absent)
    
    property("map should apply function to Present"):
        forAll(presentIntGen): value =>
            assertEquals(value.map(_ + 1), value.get + 1)
    
    property("flatMap should not apply function to Absent"):
        forAll(absentGen): absent =>
            assertEquals(absent.flatMap(v => Maybe(v + 1)), Absent)
    
    property("flatMap should apply function to Present"):
        forAll(presentIntGen): value =>
            assertEquals(value.flatMap(v => Maybe(v + 1)), value.get + 1)
    
    property("fold should handle both Absent case"):
        forAll(absentGen): absent =>
            assertEquals(absent.fold("default")(_.toString), "default")
    
    property("fold should handle Present case"):
        forAll(presentIntGen): value =>
            assertEquals(value.fold("default")(_.toString), value.toString)
    
    property("orElse should return other for Absent"):
        forAll(absentGen): absent =>
            assertEquals(absent.orElse(42), 42)
    
    property("orElse should return self for Present"):
        forAll(presentIntGen): value =>
            assertEquals(value.orElse(100), value)
    
    property("or should return default for Absent"):
        forAll(absentGen): absent =>
            assertEquals(absent.or(42), 42)
    
    property("or should return self for Present"):
        forAll(presentIntGen): value =>
            assertEquals(value.or(100), value)
    
    property("filter should return Absent if predicate is false and self if true"):
        forAll(presentIntGen): value =>
            assertEquals(value.filter(_ > 1000), Absent)
            assertEquals(value.filter(_ <= 1000), value)
            
    property("exists should correctly report presence based on a predicate"):
        forAll(presentIntGen): value =>
            assertEquals(value.exists(_ == value), true)
            assertEquals(value.exists(_ != value), false)
            
    property("forall should return true for Absent"):
        forAll(absentGen): absent =>
            assertEquals(absent.forall(_ => false), true)
    
    property("forall should evaluate predicate for Present"):
        forAll(presentIntGen): value =>
            assertEquals(value.forall(_ == value), true)
    
    property("contains should correctly identify if Present contains a value"):
        forAll(presentIntGen): value =>
            assertEquals(value.contains(value), true)
            assertEquals(value.contains(value.get + 1), false)
            
    property("foreach should do nothing for Absent"):
        forAll(absentGen): absent =>
            var applied = false
            absent.foreach(_ => applied = true)
            assertEquals(applied, false)
            
    property("foreach should apply function for Present"):
        forAll(presentIntGen): value =>
            var result = 0
            value.foreach(result = _)
            assertEquals(result, value)
            
    property("toSeq should convert Absent to empty Seq"):
        forAll(absentGen): absent =>
            assertEquals(absent.toSeq, Seq.empty)
    
    property("toSeq should convert Present to single-element Seq"):
        forAll(presentIntGen): value =>
            assertEquals(value.toSeq, Seq(value))
    
    property("toSet should convert Absent to empty Set"):
        forAll(absentGen): absent =>
            assertEquals(absent.toSet, Set.empty)
    
    property("toSet should convert Present to single-element Set"):
        forAll(presentIntGen): value =>
            assertEquals(value.toSet, Set(value.get))
    
    property("toMap should convert Absent to empty Map"):
        forAll(absentGen): absent =>
            assertEquals(absent.toMap(value => value.toString -> value), Map.empty)
    
    property("toMap should convert Present to single-entry Map"):
        forAll(presentIntGen): value =>
            assertEquals(value.toMap(v => v.toString -> v), Map(value.get.toString -> value.get))
end MaybeTests
