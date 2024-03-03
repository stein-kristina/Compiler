@echo off
javac -encoding UTF-8 -d .\bin .\src\hw1\*.java
cd bin
java hw1.Main
@pause