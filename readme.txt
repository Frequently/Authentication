Prerequisite: Java should be preinstalled in the machine and enivrnoment variables should be set in that machine


Unzip the zip file then go inside the unzip folder.
You should be able to see few java file
Open terminal in that folder

Run the Following commands:

Step1: Code Compilation

javac *.java

If the above commands fails then run following two commands:

javac Client.java Client_Main.java

javac Server.java Server_Main.java


Step2: Running the code

Now in the same terminal write the below command

java -cp ./classes;. Server_Main

Now open one more terminal at the same location and run the following command:

java -cp ./classes;. Client_Main


If the above command doesn't works then try:

In one terminal type: java Server_Main
In Second terminal type: java Client_main


Note: Server should be up before you start Client