# Client-Server demo

It is demo project which demonstrates communication between server and client, using Java NIO.

# Modules

| Module name | README |
| ------ | ------ |
| server | Contains server application. |
| client | Contains client application |
|client-server-message-format | message format to communicate between client and server|

### Lombok + Eclipse
In order to reduce boilerplate code, in demo application used Lombok project. 
In Eclipse all is works fine, bild passes successfully, but Eclipse highlights some cases of Lombok usage as error.
In order to fix it, install pluggin in Eclipse. Details [here](https://projectlombok.org/setup/eclipse).

### Assembly
run
```sh
$ mvn clean install
```
### Starting server
Go to directory path_to_project/server/target
There will be jar file *runnable-server.jar*
You can start it with 
```sh
$ java -jar runnable-server.jar -port 3000 -data path_to_server_directory
```
Available client options
- **-port portNumber** - starts server on specific port. Port must be 1....65535 (Optional, default 3000)
- **-data directory_location** - directory to store files, must be absolute path. (Optional, by default ~/serverdata)

### Starting client
Go to directory path_to_project/client/target
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