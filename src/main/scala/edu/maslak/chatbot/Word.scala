package edu.maslak.chatbot

import scala.collection.mutable.ArrayBuffer

class Word(val id: Int, val value: String, var successors: ArrayBuffer[Word]){
  def this(value: String) = this(Word.nextIndex, value, ArrayBuffer())
  def this(id: Int, value:String) = this(id,value,ArrayBuffer())
  def add(word: Word): Unit = { successors += word }
  def toXML: String = {
    val successorsXML = successors.map((word: Word) => {
      "<next>" + word.id + "</next>"
    }).reduceLeft(_ + _)
    return "<word id='"+id+"' value='"+value+"'>"+System.lineSeparator()+successorsXML+System.lineSeparator()+"</word>"+System.lineSeparator()
  }
}

object Word {
  val START_WORD = "^"
  val END_WORD = "$"
  private var currentIndex = 0
  def nextIndex: Int = {
    currentIndex += 1
    return currentIndex
  }
}

trait StartWord extends Word
trait EndWord extends Word {
  override def toXML = "<word id='"+id+"' value='"+value+"'></word>"+System.lineSeparator()
}