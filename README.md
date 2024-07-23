# Toolbox

## Description
This toolbox is a collection of useful tools for scala 3 projects.
It is a work in progress and will be updated as I find more useful tools.

## Types

### `Maybe[T]`

A type that represents a value that may or may not be present without any instantiation overhead.
It is a replacement for `Option[T]` that is more efficient and easier to use.

```scala
import toolbox.types.*

val maybeInt: Maybe[Int] = 5
val maybeString: Maybe[String] = "Hello"
val noValue: Maybe[Int] = Absent

val value: Int = maybeInt.or(0)

```