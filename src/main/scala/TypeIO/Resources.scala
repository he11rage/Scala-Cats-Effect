package TypeIO

/**
 * Класс Resource[F, A] в Cats Effect представляет ресурс, который может быть захвачен, использован и освобожден автоматически.
 * Ресурсы могут быть любыми ресурсами, которые должны быть освобождены после использования,
 * например, файлы, сокеты, базы данных и т.д.
 */

import cats.effect._
import cats.effect.unsafe.implicits.global

object Resources extends App {

  // Пример метода make: создает ресурс и использует его в блоке use
  val resourceMake: Resource[IO, Int] =
    Resource.make(IO(println("Resource created")) >> IO.pure(42))(_ => IO(println("Resource closed")))

  val programMake: IO[Unit] = resourceMake.use { value =>
    IO(println(s"Resource used with value: $value"))
  }

  programMake.unsafeRunSync()
  // Output:
  // Resource created
  // Resource used with value: 42
  // Resource closed

  // Пример метода eval: создает ресурс из IO-эффекта
  val resourceEval: Resource[IO, Int] = Resource.eval(IO(42))

  val programEval: IO[Unit] = resourceEval.use { value =>
    IO(println(s"Resource used with value: $value"))
  }

  programEval.unsafeRunSync()
  // Output:
  // Resource used with value: 42

  // Пример метода fromAutoCloseable: создает ресурс из объекта, реализующего AutoCloseable
  val resourceFromAutoCloseable: Resource[IO, java.io.BufferedReader] = Resource.fromAutoCloseable(IO(new java.io.BufferedReader(new java.io.StringReader("Hello World!"))))

  val programFromAutoCloseable: IO[Unit] = resourceFromAutoCloseable.use { reader =>
    IO(println(s"Resource used with value: ${reader.readLine()}"))
  }

  programFromAutoCloseable.unsafeRunSync()
  // Output:
  // Resource used with value: Hello world!

  // Пример метода bracket: создает ресурс, который обрабатывает ошибки и завершает работу даже в случае ошибки

  val resourceBracket: Resource[IO, java.io.PrintWriter] = Resource.make(
    IO(new java.io.PrintWriter("test.txt"))
  )(_ => IO(println("Resource closed")))

  val programBracket: IO[Unit] = resourceBracket.use { writer =>
    for {
      _ <- IO(println("Writing to file"))
      _ <- IO.raiseError(new Exception("Error writing to file"))
      _ <- IO(println("File write successful"))
    } yield ()
  }.handleErrorWith { error =>
    IO(println(s"Caught error: ${error.getMessage}"))
  }

  programBracket.unsafeRunSync
  // Output:
  // Writing to file
  // Resource closed
  // Caught error: Error writing to file
}
