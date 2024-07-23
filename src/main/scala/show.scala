package com.rlemaitre.toolbox

object show:
    trait Show[A]:
        def show(output: A): String

    given Show[Char] with
        def show(output: Char): String = output.toString

    given Show[Int] with
        def show(output: Int): String = output.toString

    given Show[String] with
        def show(output: String): String = output

    given Show[Long] with
        def show(output: Long): String = output.toString

    given Show[BigInt] with
        def show(output: BigInt): String = output.toString
end show
