# Shortener-Play - example of typical REST service 

Shortener-Play implements usual functionality which make possible use short url instead of long ones.

Service gives possibility to:
  * Registration Web address (API)  
  * Redirect client in accordance with the shortened URL  
  * Usage Statistics (API)  

Shortener-Play was implemented in the MVC pattern by Java PlayFramework 2.5:
  * Service uses temporary storage is implemented by inmemory Database H2 over JDBC 
  * Service caches database operations and use async approach to critical calls
  * Service supports altenative way for storing data by java.collections, you need configure DI before.
   
## How to run
  Project has prepared as zip file to run standalone service easy.
  You need only:
  
  1. download and unzip file: [shortener-play25-1.0-SNAPSHOT.zip](https://github.com/vnazarov1969/shortener-play/raw/master/dist/shortener-play25-1.0-SNAPSHOT.zip) 
  
  2. run script
  
   $sh bin/shortener-play25

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
  
    Notes: service returns password "test" for "AccountID": "local".
    
    Notes 2: to switch mode to Java Collection, comment line in application.conf:
    #play.modules.enabled += "ModuleJDBC"
  

## API

### Configuration

#### Openning Account

     Uri : /account
     HTTP Method: POST
     Request Type: {application/json}\
     Request Body: AccountId: String. Mandatory
                   Example: {AccountId:"test"}
     Response Type: {application/json}
     Response Status: 
         CREATED(201): Success 
         CONFLICT(409): Account with same ID exists
         BAD_REQUEST(400): Wrong request
     Response Body: 
               success: true or false
               description: account is opened | account with that ID already exists
               password: Automatically generated password 8 symbols, returns if account was created
               Example {success: 'true', description: 'Your account is opened', password: 'xC345Fc0'} 


#### Register Url

     Uri : /register
     HTTP Method: POST
     Request Type: {application/json}
     Request Body: 
          url: String. mandatory
          redirectType: 301|302, not mandatory, default 302
                   Example: {"url":"http://google.com", "redirectType":301}
     Request Header: Basic Authentication - "(user:password)" is base 64 encoded
     Response Type: {application/json}
     Response Status: 
         CREATED(201): Success 
         BAD_REQUEST(400): Wrong request
         UNAUTHORIZED(401): Unauthorized
     Response Body: 
               shortUrl (shortened URL)
               Example: { shortUrl: 'http://short.com/xYswlE'}

### Statistic

     Uri : /statistic/"accountId"
     HTTP Method: GET
     Request Header: Basic Authentication - "(user:password)" is base 64 encoded
     Response Type: {application/json}
     Response Status: 
         OK(200): Success 
     Response Body: 
         The server responds with a JSON object, key:value map, where the key is the registered URL, 
         and the value is the number of this URL redirects.. 
         Example:
         {
          'http://myweb.com/someverylongurl/thensomedirectory/: 10,
          'http://myweb.com/someverylongurl2/thensomedirectory2/: 4,
          'http://myweb.com/someverylongurl3/thensomedirectory3/: 91,
          }

### Redirect

    Uri : /"shortUrl"
    HTTP Method: GET
    Response Status: 
        MOVED_PERMANENTLY(301): Success
        FOUND(302):Success
        BAD_REQUEST(400): Wrong short Url
    Response Body: 
        Redirect on stored site

### Help

    Uri : /help
    HTTP Method: GET
    Response Status: 
        OK(200)
    Response Body: 
        This info



##License

Shortener-Play is under MIT license. See [LICENSE](https://github.com/vnazarov1969/shortener-play/blob/master/LICENSE)
