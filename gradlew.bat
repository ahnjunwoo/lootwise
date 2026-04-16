@ECHO OFF
SET DIRNAME=%~dp0
SET CLASSPATH=%DIRNAME%gradle\wrapper\gradle-wrapper.jar

IF NOT EXIST "%CLASSPATH%" (
  ECHO Missing gradle wrapper jar: %CLASSPATH%
  ECHO Run "gradle wrapper" once if Gradle is installed.
  EXIT /B 1
)

java -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
