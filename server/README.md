# Server application

NIO based server, which can take messages, and return answer;

### Assembly
run
```sh
$ mvn clean install
```
### Starting server
After assembly go to directory  **path_to_project/server/target**
There will be jar file *runnable-server.jar*
You can start it with 
```sh
$ java -jar runnable-server.jar -port 3000 -data path_to_server_directory -proc_count proc
```
Available client options
- **-port portNumber** - starts server on specific port. Port must be 1....65535 (Optional, default 3000)
- **-data directory_location** - directory to store files, must be absolute path. (Optional, by default ~/serverdata)
- **-proc_count proc** - number of threads to process requests. (Optional, by default =2)