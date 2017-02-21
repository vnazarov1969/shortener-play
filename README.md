# Shortener-Play - example of typical REST service 

Shortener-Play implements usual functionality which make possible use short url instead of long ones.

Service gives possibility to:
  * Registration Web address (API)  
  * Redirect client in accordance with the shortened URL  
  * Usage Statistics (API)  

Shortener-Play was implemented in the MVC pattern by Java PlayFramework 2.5:
  * Service does not have persistence storage, 
  * Temporary storage bases on java.collections, we do not use DataBase to demonstrate capabilities of collections
  * Views do not use standard PlayFramework2.5 template manager to make project easier  

## How to run
  Project has prepared zip file to run standalone service easy, you need only download file, unzip and run script

## How to build
  PlayFramework uses SBT as build tool. 
  SBT is compatible with Maven repository,so it can be integrate to building complex applications.
  
  So to build project you need:
  
    1. install SBT on you computer: http://www.scala-sbt.org/download.html
    2. clone repository 
    3. compile project: sbt clean compile
    3. run functionality tests: sbt test
    4. run application: sbt run
    5. build new standalone package: sbt dist
  
  

## API




##License

Shortener-Play is under MIT license. See [LICENSE](https://github.com/vnazarov1969/shortener-play/master/LICENSE)
