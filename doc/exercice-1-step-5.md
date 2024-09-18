# Exercise 1 step 5

## deploy you application in the cloud

1. ask the DNS of your VM ins the cloud and the private key to access it.
2. install java on your VM.
   In git bash: 
   a. _ssh -i [PATH-TO-PEM-KEY] ec2-user@[YOUR-VM-DNS]_
   b. _sudo dnf install java-21-amazon-corretto-devel_
3. build your application in git bash.  
   From the root of your application run: _mvn clean package_  
4. copy the resulting jar to your VM:   
   _scp target/pxl-training-base-1.0-SNAPSHOT.jar -i [PATH-TO-PEM-KEY] ec2-user@[YOUR-VM-DNS]_
5. run your application:  
   _java -jar pxl-training-base-1.0-SNAPSHOT.jar_
6. test your application. In postman, change localhost with http://[YOUR-VM-DNS]:8080. 
   Send a request with a valid body.