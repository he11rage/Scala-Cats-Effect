package TypeIO

/**
 * Метод IO.apply в библиотеке Cats Effect позволяет создавать экземпляры типа IO для асинхронных вычислений
 * Метод IO.apply возвращает объект типа IO[A], где A – это тип значения, которое будет вычислено внутри IO эффекта.
 */

import cats.effect._
import cats.effect.unsafe.implicits.global

import scala.io.Source

/**
 * В данном примере IO.apply не возвращаем никакого значения, поэтому мы используем метод
 * as, чтобы преобразовать значение типа Unit в значение типа ExitCode.Success
 */
object Apply extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    IO.apply(println("Hello, world!")).as(ExitCode.Success)
  }
}

object DescribeAsyncCalculations extends App {

  /**
   * flatMap - используется для последовательного выполнения нескольких IO-операций, при этом результат
   * одной операции передается в следущую.
   *
   * Например:
   */

  val programFlatMap: IO[String] = IO("Hello, ").flatMap(hello => IO(s"$hello world!"))
  val resultFlatMap: String = programFlatMap.unsafeRunSync()
  println(resultFlatMap)

  /**
   * map - используется для трансформации результата IO-операции.
   *
   * Например:
   */
  val programMap: IO[String] = IO("hello").map(_.toUpperCase())
  val resultMap: String = programMap.unsafeRunSync()
  println(resultMap)

  /**
   * bracket - используется для обеспечения ресурсов до и после выполнения IO-операции.
   *
   * Например, чтобы гарантировать, что файл будет закрыт после чтения:
   */

  val fileResource: Resource[IO, java.io.BufferedReader] =
    Resource.fromAutoCloseable(IO {
      val file = new java.io.File("test.txt")
      val reader = new java.io.BufferedReader(new java.io.FileReader(file))
      reader
    })

  val programBracket: IO[String] = fileResource.use { reader =>
    IO(reader.readLine())
  }

  val resultBracket: String = programBracket.unsafeRunSync()
  println(resultBracket) // содержимое первой строки файла test.txt

  /**
   * delay - используется для выполнения вычислений вне IO-контекста и последующего получения
   * результата в IO-контексте.
   * delay оборачивает вычисления в отложенную операцию, откладывая её выполнение до момента вызова.
   * Например:
   */

  val programDelay: IO[String] = IO.delay {
    println("Вычисление началось")
    "Результат"
  }

  val resultDelay: String = programDelay.unsafeRunSync()
  println(resultDelay)

  /**
   * blocking - используется для выполнения блокирующих операций вне рабочего потока.
   * Например чтение из файла:
   */

  val programBlocking: IO[String] = IO.blocking {
    val file = new java.io.File("test.txt")
    val source = Source.fromFile(file)
    try source.mkString finally source.close()
  }

  val resultBlocking: String = programBlocking.unsafeRunSync()
  println(resultBlocking)
}
