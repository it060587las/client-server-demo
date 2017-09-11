# Client-Server demo

It is demo project which demonstrates communication between server and client, using Java NIO.

# Modules

| Module name | README |
| ------ | ------ |
| server | Contains server application. |
| client | Contains client application |
|client-server-message-format | message format to communicate between client and server|
### Notes
- Server was implemented using Java NIO. Using of this approach allows to work effectively with big concurrent number of consumers, without threads overhead.
- Client and server use binary format for communication. It allows remove the need in additional "deserialization" process (in JSON or XML), which save CPU resources. For transform to bytes KRYO library is used. It is faster then ObjectInputStream-ObjectOutputStream almost in 10 times and use memory and CPU effective.
### Assembly
run
```sh
$ mvn clean install
```
### Starting server
Go to directory path_to_project/runnable or to path_to_project/server/target
There will be jar file *runnable-server.jar*
You can start it with 
```sh
$ java -jar runnable-server.jar -port 3000 -data path_to_server_directory
```
Available client options
- **-port portNumber** - starts server on specific port. Port must be 1....65535 (Optional, default 3000)
- **-data directory_location** - directory to store files, must be absolute path. (Optional, by default ~/serverdata)

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