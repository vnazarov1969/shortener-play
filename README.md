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

  So, to build project you need:
  1. install SBT on you computer:  http://www.scala-sbt.org/download.html
    
    $brew install sbt 
  2. clone repository:
    
    $git clone https://github.com/vnazarov1969/shortener-play.git shortener-play 
  3. run functionality tests: 
    
    $sbt clean compile test
  4. run application: 
    
    $sbt run

Now you can browse this page: http://localhost:9000/help, configure service by curl   
    
    curl -H "Content-Type: application/json" -POST localhost:9000/account -d'{"AccountId": "test"}'
    curl -u test:test -H "Content-Type: application/json" -POST localhost:9000/register -d'{"url": "https://www.google.ru", "redirectType": "301"}' 
    curl -H "Content-Type: application/json" -GET 'localhost:9000/statistic/local'?pretty 
     
  or using redirect service http://localhost:9000/"shortUrl"
  

## API




##License

Shortener-Play is under MIT license. See [LICENSE](https://github.com/vnazarov1969/shortener-play/blob/master/LICENSE)
