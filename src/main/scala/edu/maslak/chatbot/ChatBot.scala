package edu.maslak.chatbot

import scala.util.Random
import java.io.FileWriter
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.io.OutputStreamWriter
import java.io.FileOutputStream
import java.io.BufferedWriter
import scala.xml._

class ChatBot(escapeWord: String) {
  def this() = this(ChatBot.DEFAULT_ESCAPE_WORD)
  var dictionary: Map[String, Word] = Map()

  dictionary += (Word.START_WORD -> new Word(Word.START_WORD) with StartWord)
  dictionary += (Word.END_WORD -> new Word(Word.END_WORD) with EndWord)

  private val random = new Random
  private var _running = true;
  def running = _running;

  def answer(str: String) = str match {
    case `escapeWord` => { _running = false; ChatBot.BYE_WORD }
    case _ => {
      val startWord = dictionary.get(Word.START_WORD).get
      val endWord = dictionary.get(Word.END_WORD).get
      //split the statement into sentences
      val sentences = str.split(ChatBot.SENTENCE_SEPARATORS).filter(_.length() > 0)

      sentences foreach (sentence => {
        // split the sentence into words
        val words = sentence.split(ChatBot.WORD_SEPARATORS).filter(_.length() > 0)

        var first = true
        //update knowledge base
        val lastWordString = words reduce ((x: String, y: String) => {
          // check for words in the dictionary
          // if they are not there, add them
          val wordX = dictionary.get(x) match {
            case None => {
              val newWord = new Word(x)
              dictionary += (x -> newWord)
              newWord
            }
            case word => word.get
          }
          if (first) {
            first = false
            startWord.successors += wordX
          }

          val wordY = dictionary.get(y) match {
            case None => {
              val newWord = new Word(y)
              dictionary += (y -> newWord)
              newWord
            }
            case word => word.get
          }

          // add edge
          wordX.successors += wordY

          y
        })

        // add an edge to the terminal word
        val lastWord = dictionary.get(lastWordString) match {
          case None => {
            val newWord = new Word(lastWordString)
            dictionary += (lastWordString -> newWord)
            newWord
          }
          case word => word.get
        }
        lastWord.successors += endWord
        if (first) {
          startWord.successors += lastWord
          first = false
        }
      })

      //generate answer
      def getNextWord(current: Word): Word = {
        val index = random.nextInt(current.successors.size)
        current.successors(index)
      }
      def getFirstWord = getNextWord(startWord)

      // select the initial word
      var word = getFirstWord
      var answer = word.value

      // get the succeeding words and append to answer
      word = getNextWord(word)
      // walk through a path until the end word is reached
      while (word match { case x: EndWord => false; case _ => true }) {
        answer += " " + word.value
        word = getNextWord(word)
      }

      answer

    }
  }

  def export(fileName: String): String = {
    var msg = "Saving knowledge to " + fileName + "..."

    try {

      val out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
      out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
      out.newLine()
      out.write("<words>")
      out.newLine()
      dictionary foreach {
        case (key, value) => out.write(value.toXML)
      }
      out.write("</words>")
      out.close()
      msg += "\nSuccess"
    } catch {
      case e: Exception => msg += "\nSaving failed: " + e.getMessage()
    }
    msg
  }
  def export: String = export(ChatBot.DEFAULT_EXPORT_FILE_NAME)

  def load(fileName: String): String = {
    var msg = "Loading knowledge from " + fileName + "..."

    try {

      val xml = XML.load(new java.io.InputStreamReader(new java.io.FileInputStream(fileName), "UTF-8"))
      val words = xml \ "word"
      var wordsIdMap: Map[Int, Word] = Map()

      // add words to maps
      words foreach (wordNode => {
        val id = wordNode.attribute("id").get.toString.toInt
        val value = wordNode.attribute("value").get.toString
        val word = value match {
          case Word.`START_WORD` => new Word(id, value) with StartWord
          case Word.`END_WORD` => new Word(id, value) with EndWord
          case _ => new Word(id,value)
        }
        wordsIdMap += (id -> word)
        dictionary += (value -> word)
      })

      // add edges
      words foreach (wordNode => {
        val id = wordNode.attribute("id").get.toString.toInt
        val word = wordsIdMap.get(id).get
        val nodes = wordNode \ "next"
        nodes foreach (node => {
          val nextId = node.text.toInt
          val next = wordsIdMap.get(nextId).get
          word.add(next)
        })
      })

      //words foreach (x => println("id=" + x.attribute("id").get + "; value=" + x.attribute("value").get))
      //dictionary = ois.readObject.asInstanceOf[Map[String, Word]]

      msg += "\nSuccess"
    } catch {
      case e: Exception => msg += "\nLoading failed: " + e.getMessage()
    }
    msg
  }

}

object ChatBot {
  val DEFAULT_ESCAPE_WORD = "exit"
  val DEFAULT_EXPORT_FILE_NAME = "exported.xml"
  val WORD_SEPARATORS = " |,|;"
  val SENTENCE_SEPARATORS = "\\.|!|\\?"
  val BYE_WORD = "Finishing conversation..."
}
