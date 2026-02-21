$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.1"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
mvn clean compile "-Dmaven.test.skip=true"
mvn spring-boot:run "-Dmaven.test.skip=true"
