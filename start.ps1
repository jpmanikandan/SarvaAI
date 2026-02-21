$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.1"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$jarPath = "target\sarva-bhasha-ai-with-core-0.0.1-SNAPSHOT.jar"
Write-Host "Starting Sarva Application..."
Start-Process -FilePath "java.exe" -ArgumentList "-Djavax.net.debug=ssl,handshake -Dhttps.protocols=TLSv1.2,TLSv1.3 -jar $jarPath" -RedirectStandardOutput "app.log" -RedirectStandardError "error.log" -WindowStyle Hidden
Write-Host "Application is starting in the background. Logs are being written to app.log"
  