# SpecialSMS server/client android app

##Description:
An app to intercept special SMS. Intercepted SMS are sent to a server, and stored.

##Assumptions:
* A special SMS is a SMS containing a sequence of numbers 43110 where each A-Z/a-z is mapped to 1-26 respecively. An example special SMS would be "nealdcajneal"
* Any other characters are ignored from the sequence i.e "nealdc5ajneal" would still be intercepted.  
* Any message that ends with the character 'z'/'Z' is not a special SMS.  

###Design Details and Decisions can be found in design.txt

##Installation:

###Android Application
For android app, import project into Android Studio.  

###Server Application:
To compile:
    mvn compile
To run:
    java -cp target/Server-1.0-SNAPSHOT.jar com.knowroaming.Server



![alt text](https://raw.githubusercontent.com/nealmanaktola/specialsms/master/specialsms.gif "Logo Title Text 1")
