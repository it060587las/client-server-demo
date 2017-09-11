# Client application

Demo client application, which sends commands to server, receive results and prints it to console.

### Assembly
run
```sh
$ mvn clean install
```

### Starting client
Go to directory path_to_project/runnable or path_to_project/client/target
There will be jar file *runnable-client.jar*
You can start it with 
```sh
$ java -jar runnable-client.jar options
```
Available client options
- **-serverPort portNumber** - starts client on specific port. Port must be 1....65535 (Optional, default 3000)
- **-serverHost host** - connect to specific server, (Optional, default "localhost")
- **-addbird** - add new bird item
- **-addsighting** - add new sighting option
- **-listsightings** - view list of sightings
- **-listbirds** - view list of birds
- **-remove** - remove bird
- **-quit** stop server and quit