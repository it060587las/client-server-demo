# Client-server-message-format

Format that is used in communication between server and client applications.
Server is based on JAVA NIO ,in communication between client and server binary format used.
It allows:
- Very effective to service big number of concurrent clients.
- Perform fast serialization to bytes array using Kryo java library
- No need additional deserialization (to JSON or XML), so it is save CPU resources.

