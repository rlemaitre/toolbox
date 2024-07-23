package com.rlemaitre.toolbox

extension [K, V](items: Map[K, V])
    def topologicalSort(dependencies: V => Iterable[K]): Either[String, List[(K, V)]] =
        @annotation.tailrec
        def loop(
            remaining: Map[K, V],
            sorted: List[(K, V)],
            visited: Set[K],
            recursionStack: Set[(K, V)]
        ): Either[String, List[(K, V)]] =
            if remaining.isEmpty then Right(sorted)
            else
                val (noDeps, hasDeps) = remaining.partition: (_, value) =>
                    dependencies(value).forall(visited.contains)
                if noDeps.isEmpty then
                    if hasDeps.exists(recursionStack.contains) then Left("Cyclic dependency found")
                    else loop(hasDeps, sorted, visited, recursionStack ++ hasDeps)
                else loop(hasDeps, sorted ++ noDeps.toList, visited ++ noDeps.keySet, recursionStack)

        loop(items, List.empty, Set.empty, Set.empty)
