package TypeIO

import cats.effect._
import cats.effect.unsafe.implicits.global

object IOComposition extends App {

  /**
   * map - применяет функцию к результату IO-эффекта и возвращает новый IO-эффект с преобразованным результатом.
   *
   * Например:
   */

  val ioStringMap: IO[String] = IO("Hello, world!")
  val ioLengthMap: IO[Int] = ioStringMap.map(_.length)

  val lengthMap: Int = ioLengthMap.unsafeRunSync()
  println(lengthMap)

  /**
   * flatMap - позволяет композировать два IO-эффекта вместе, где второй эффект зависит от результата первого эффекта.
   *
   * Например:
   */

  val ioIntFlatMap: IO[Int] = IO(5)
  val ioStringFlatMap: IO[String] = ioIntFlatMap.flatMap(i => IO(s"The number is $i"))

  val resultFlatMap: String = ioStringFlatMap.unsafeRunSync()
  println(resultFlatMap)

  /**
   * orElse -  позволяет задавать альтернативный IO-эффект, который будет выполнен в случае ошибки первого эффекта.
   *
   * Например:
   */

  val ioExceptionOrElse: IO[Throwable] = IO.raiseError(new RuntimeException("Oops!")) // raiseError позволяет создать эффект, который завершится с ошибкой при выполнении этого эффекта
  val ioFallbackOrElse: IO[String] = IO("Something went wrong").orElse(ioExceptionOrElse.map(_.getMessage))

  val resultOrElse: String = ioFallbackOrElse.unsafeRunSync()
  println(resultOrElse)

  /**
   * recover - позволяет обработать ошибку, возникшую в IO и вернуть результат по умолчанию.
   *
   * Например:
   */

  val ioExceptionRecover: IO[Throwable] = IO.raiseError(new OutOfMemoryError("Oops!"))
  val ioFallbackRecover: IO[String] = ioExceptionRecover.recover {
    case e: RuntimeException => "Recovered from " + e.getMessage
    case _ => "Something went wrong"
  }.map(_ + " Default")

  val resultRecover: String = ioFallbackRecover.unsafeRunSync()
  println(resultRecover) // "Recovered from Oops! Default"

  /**
   * redeem - принимает две функции - одну для обработки успешного результата, вторую для обработки исклчючения
   *
   * Например:
   */

  val ioSuccessRedeem: IO[Int] = IO(5)
  val ioFailureRedeem: IO[Int] = IO.raiseError(new RuntimeException("Oops!"))

  val ioHandledRedeem: IO[String] = ioSuccessRedeem.redeem(
    _ => "Failed",
    i => s"Success $i"
  )

  val ioHandledRedeem2: IO[String] = ioFailureRedeem.redeem(
    _ => "Failed",
    i => s"Success $i"
  )

  val resultRedeem1: String = ioHandledRedeem.unsafeRunSync()
  val resultRedeem2: String = ioHandledRedeem2.unsafeRunSync()

  println(resultRedeem1)
  println(resultRedeem2)

  /**
   * attempt - возвращает новый IO, который выполнит текущий эффект,
   * но вернет результат в виде Either, где Left содержит возникшее исключение,
   * если оно было, и Right содержит результат.
   *
   * Например:
   */

  def divAttempt(dividend: Double, divisor: Double): Double = {
    if (divisor == 0) throw new IllegalArgumentException("Cannot divide by zero")
    else dividend / divisor
  }
  // Мы можем использовать attempt для выполнения div и перехвата любых исключений

  val ioResultAttempt: IO[Either[Throwable, Double]] = IO(divAttempt(10, 0)).attempt
  val resultAttempt = ioResultAttempt.unsafeRunSync()
  resultAttempt match {
    case Right(value) => println(s"The result is $value")
    case Left(e) => println(s"An error occurred: ${e.getMessage}")
  }

  /**
   * onError - позволяет выполнить дополнительные действия, когда IO-эффект завершается с ошибкой.
   * Он принимает в качестве параметра функцию, которая будет вызвана в случае ошибки.
   *
   * Например:
   */

  val programOnError: IO[Unit] = IO.raiseError(new RuntimeException("Oops!"))
  programOnError.onError {
    case e => IO(println(s"Error occurred: ${e.getMessage}"))
  }//.unsafeRunSync()

}
