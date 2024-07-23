package com.rlemaitre.toolbox

import munit.FunSuite
import munit.ScalaCheckSuite

class TopologicalSortSuite extends ScalaCheckSuite:
    test("topologicalSort returns sorted list for acyclic graph"):
        val dependencies: Map[Char, Iterable[Char]] = Map(
          'A' -> List('D'),
          'B' -> List('D'),
          'C' -> List('A', 'B'),
          'D' -> List('E'),
          'E' -> List()
        )
        end dependencies
        assertEquals(dependencies.topologicalSort(identity).map(_.map(_._1)), Right(List('E', 'D', 'A', 'B', 'C')))

    test("topologicalSort returns error for cyclic graph"):
        val dependencies: Map[Char, Iterable[Char]] = Map(
          'A' -> List('B'),
          'B' -> List('C'),
          'C' -> List('A')
        )
        assertEquals(dependencies.topologicalSort(identity).map(_.map(_._1)), Left("Cyclic dependency found"))

    test("topologicalSort returns sorted list for single node graph"):
        val dependencies: Map[Char, Iterable[Char]] = Map('A' -> List())
        assertEquals(dependencies.topologicalSort(identity).map(_.map(_._1)), Right(List('A')))

    test("topologicalSort returns sorted list for disconnected graph"):
        val dependencies: Map[Char, Iterable[Char]] = Map('A' -> Nil, 'B' -> Nil, 'C' -> Nil, 'D' -> Nil, 'E' -> Nil)
        assertEquals(dependencies.topologicalSort(identity), Right(dependencies.toList))

    test("topologicalSort should sort simple dependency graph correctly"):
        val graph  = Map(1 -> List(2), 2 -> List(3), 3 -> List())
        val result = graph.topologicalSort(identity)
        assertEquals(result, Right(List((3, Nil), (2, List(3)), (1, List(2)))))

    test("topologicalSort should detect cyclic dependencies"):
        val graph  = Map(1 -> List(2), 2 -> List(3), 3 -> List(1))
        val result = graph.topologicalSort(identity)
        assertEquals(result, Left("Cyclic dependency found"))

    test("topologicalSort should sort complex dependency graph correctly"):
        val graph      = Map(1 -> List(2, 3), 2 -> List(4), 3 -> List(4), 4 -> List())
        val result     = graph.topologicalSort(identity)
        assert(result.isRight)
        val sortedKeys = result.getOrElse(List()).map(_._1)
        assert(sortedKeys.indexOf(1) > sortedKeys.indexOf(2))
        assert(sortedKeys.indexOf(1) > sortedKeys.indexOf(3))
        assert(sortedKeys.indexOf(2) > sortedKeys.indexOf(4))
        assert(sortedKeys.indexOf(3) > sortedKeys.indexOf(4))

    test("topologicalSort should return empty list for empty graph"):
        val graph  = Map.empty[Int, List[Int]]
        val result = graph.topologicalSort(identity)
        assertEquals(result, Right(List.empty))

end TopologicalSortSuite
