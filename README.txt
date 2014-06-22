---------------------------------  The simple chatbot in Scala  -----------------------
------------------------------------  by Paweł Maślak (2014)  -------------------------
---------------------------------  maslak.pawel.1357@gmail.com  -----------------------

This is a an implementation of a chatbot using a simplified version of Markov's chain written in Scala.

To compile, you need to have the JDK and Maven installed on your computer. Maven will take care of downloading all the necessary dependencies.

To compile, from the root folder of the project run:
mvn compile

Then, to package the compiled classes along with the dependencies into a single executable jar file, run:
mvn package

If everything goes fine, in the target directory there should be a file called 'TheSimpleChatbot-1.0-jar-with-dependencies.jar'. To execute it, you only need to have JRE installed on your computer. If you do, run:
java -jar TheSimpleChatbot-1.0-jar-with-dependencies.jar

This will run the bot with clean knowledge base in the default configuration.

The app accepts command line arguments, which are as follows:
-i | --input
	specify the input file name with the knowledge base you want to load
-o | --output
	specify the output file name where you want to save the knowledge base after the conversation finishes
-e | --escape
	specify the "escape" word - typing this word in the conversation will terminate the bot and, if the output file name was specified, trigger the export of the knowledge base
	
Examples:
java -jar TheSimpleChatbot-1.0-jar-with-dependencies.jar -i example.xml -o export.xml
java -jar TheSimpleChatbot-1.0-jar-with-dependencies.jar --input example.xml --output export.xml --escape exit

The knowledge base is exported to a human-readable XML file.




TODO:
- implement the XML import using a SAX parser
	currently the whole XML structure is loaded into memory and processed therein, which could be problematic when the knowledge base gets large
- make it more context aware
- implement punctuation use
- deploy it on a server so it can be accessible via telnet or ssh