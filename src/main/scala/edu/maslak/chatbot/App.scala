package edu.maslak.chatbot

/**
 * @author ${user.name}
 */

import scala.Predef

object App {
  val BANNER = "------------- The simple chatbot -------------\n----------- by Paweł Maślak (2014) -----------\n--------- maslak.pawel.1357@gmail.com --------\n"
  val PROMPT = ">> "
  val ANSWER_PREFIX = ">>> "

  def main(args: Array[String]) {

    println(BANNER);

    var importName: String = null
    var exportName: String = null
    var escapeWord: String = null
    var learnName: String = null

    // parse the command line arguments
    // e.g. chatbot --escape exit --input in.txt --output out.txt
    if (args.size > 0)
      args reduce ((arg1: String, arg2: String) => {
        arg1 match {
          case "--input" | "-i" => importName = arg2
          case "--output" | "-o" => exportName = arg2
          case "--escape" | "-e" => escapeWord = arg2
          case "--learn" | "-l" => learnName = arg2
          case _ =>
        }
        arg2
      })

    val bot = escapeWord match {
      case null => new ChatBot
      case word => new ChatBot(word)
    }


    if (importName != null)
      println(bot load importName)

    // main loop
    while (bot running) {
      print(PROMPT)
      val str = readLine
      println(ANSWER_PREFIX + (bot answer str))
    }

    if (exportName != null)
      println(bot export exportName)

  }
}
