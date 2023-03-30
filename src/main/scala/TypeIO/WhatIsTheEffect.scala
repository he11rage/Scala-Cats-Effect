package TypeIO

/**
 * Эффект в функциональном программировании описывает побочные эффекты, такие как ввод-вывод, изменение
 * переменных, чтение из базы данных. Эффекты могут изменять состояние программы.
 *
 * Концепция IO в библиотеке Cats Effect предназачена для работы с эффектами, которые могут
 * взаимодействовать с внешним миром. IO обеспечивает безопасность и консистентность выполнения этих
 * эффектов, даже в многопоточной среде.
 */

import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.implicits._

import scala.io.Source

object WhatIsTheEffect extends App {

  /**
   * Рассмотрим простой пример, где мы считываем данные из файла и выводим их на экран.
   * Сначала мы определим функцию readFile, которая принимает путь к файлу и возвращает IO,
   * который считывает данные из файла. Мы используем метод Source.fromFile из стандартной библиотеки Scala.
   */

  def readFile(path: String): IO[String] = {
    // Определяем IO, который будет считывать данные из файла
    IO(Source.fromFile(path).getLines().mkString("\n"))
  }

  val result: IO[Unit] = readFile("test.txt").flatMap  { contents =>
    // Выводим содержимое файла на экран
    IO(println(contents))
  }

  // Запускаем IO-действие, используя unsafeRunSync
  result.unsafeRunSync()

}
