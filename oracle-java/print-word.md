# [打印Word文档](http://stackoverflow.com/questions/2446721/how-to-get-print-out-of-a-ms-word-file-from-java-application)

This is probably not the most efficient method, but it works if you have MS Word. You can use this command to get Word print the file:

start /min winword <filename> /q /n /f /mFilePrint /mFileExit

Replace <filename> with the filename. It must be enclosed in double-quotation marks if it contains spaces. (e.g. file.rtf, "A File.docx")

Here is a Java method and C++ function that takes the filename as an argument and prints the file:

**Java**

public void printWordFile(String filename){
   System.getRuntime().exec("start /min winword \"" + filename +
     "\" /q /n /f /mFilePrint /mFileExit");
 }

**C++**

//Be sure to #include <string.h>    

void wordprint(char* filename){
   char* command = new char[64 + strlen(filename)];
   strcpy(command, "start /min winword \"");
   strcat(command, filename);
   strcat(command, "\" /q /n /f /mFilePrint /mFileExit");
   system(command);
   delete command;
 }

**Explanation of switches used**

start /min says to run the program that follows minimized. You must do this or Word will stay open after the file is opened.

winword tells the start program to run Microsoft Word.

/q tells Word not to display the splash screen.

/n says to open a new instance of Word so we don't interfere with other files the user has open.

/f says to open a copy of the file to prevent modification.

/mFilePrint tells Word to diplay its print dialog so the user can choose which printer they want to use and how many copies, etc.

/mFileExit says to close as soon as everything else is done. This will not work unless Word is minimized.