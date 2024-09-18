# Exerce 1 step 5

## deploy you application in the cloud

1. ask the DNS of your VM ins the cloud and the private key to access it.
2. install java on your VM.
   In git bash: 
   1. ssh -i [PATH-TO-PEM-KEY] ec2-user@[YOUR-VM-DNS]
   sudo yum install java-21-amazon-corretto-devel
2. build your application in git bash.  
   From the root of your application run: mvn clean package.  
3. copy the resulting jar 