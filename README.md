# Redis Clone - Java Implementation

A single-threaded in-memory key-value store inspired by Redis, built in Java with support for concurrent clients, persistence, and key expiration.

## 📋 Features

### Core Functionality
- **GET/SET/DEL Commands** - Basic key-value operations
- **Key Expiration (TTL)** - Automatic removal of expired keys using EX parameter
- **Multi-Client Support** - Concurrent client handling using thread pools
- **Persistence (AOF)** - Append-only file for crash recovery and durability
- **Thread-Safe Storage** - ConcurrentHashMap for safe concurrent access

### Network & Protocol
- **TCP Server** - Listens on port 6379 (default Redis port)
- **Text-Based Protocol** - Simple command parsing (space-separated arguments)
- **Connection Management** - Graceful client disconnection handling

### Data Management
- **Memory Efficient** - Expired keys are lazily removed during access
- **Concurrent Command Processing** - Each client runs in its own thread
- **Atomic Operations** - Using Java's ConcurrentHashMap for thread safety

## 🏗️ Architecture

### Project Structure
```
src/main/java/com/osama/redisclone/
├── Main.java                 # Application entry point
├── command/
│   ├── CommandProcessor.java # Command parsing and execution
│   └── ParsedCommand.java    # Parsed command representation
├── model/
│   └── Entry.java            # Key-value entry with expiration
├── persistence/
│   └── PersistenceManager.java # AOF file management
├── server/
│   └── RedisServer.java      # TCP server with thread pool
└── store/
    └── InMemoryStore.java    # Thread-safe in-memory storage
```

### Key Design Decisions

#### 1. **Thread Pool for Concurrent Clients**
```java
ExecutorService clientPool = Executors.newCachedThreadPool();
```
- Uses `newCachedThreadPool()` to dynamically create threads as needed
- Each client connection runs in its own thread
- Server's main thread continues accepting new connections
- Provides scalability for multiple simultaneous clients

#### 2. **Persistence Strategy (Append-Only File)**
- Commands are logged to `redis-clone.aof` for durability
- On startup, all persisted commands are replayed to restore state
- Non-persistence variant (`processWithoutPersistence`) used during replay to avoid duplicate logging
- Ensures data survives server crashes

#### 3. **Lazy Key Expiration**
```java
if (entry.isExpired()) {
    data.remove(key, entry);
    return null;
}
```
- Keys are checked for expiration during access (lazy deletion)
- No background cleanup thread needed
- Memory efficient and avoids thundering herd problem

#### 4. **Thread-Safe Store**
- `ConcurrentHashMap` instead of `HashMap` for thread safety
- No explicit synchronization needed for concurrent operations
- Atomic operations maintain consistency across threads

## 📦 Technology Stack

- **Language:** Java 17
- **Build Tool:** Maven 3.6+
- **Testing:** JUnit 5
- **Concurrency:** Java ExecutorService Thread Pool

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Building the Project
```bash
mvn clean compile
```

### Running the Server
```bash
mvn exec:java -Dexec.mainClass="com.osama.redisclone.Main"
```

The server will start on port 6379 and automatically replay any persisted commands from `redis-clone.aof`.

### Testing with netcat
```bash
# In another terminal
nc localhost 6379

# Try these commands:
SET mykey "Hello, Redis!"
GET mykey
DEL mykey
GET mykey
SET counter 5 EX 10
GET counter
EXIT
```

## 🧪 Running Tests

Execute all unit tests:
```bash
mvn test
```

### Test Coverage
- **CommandProcessorTest** - Tests command parsing and execution
- **InMemoryStoreTest** - Tests concurrent access and expiration

## 📝 Command Reference

| Command | Syntax | Description |
|---------|--------|-------------|
| SET | `SET key value [EX seconds]` | Set a key with optional TTL |
| GET | `GET key` | Retrieve value for a key |
| DEL | `DEL key` | Delete a key (returns 1 if deleted, 0 if not found) |
| EXIT | `EXIT` | Close client connection |

### Examples
```
SET name osama                 # Set a key
GET name                       # Get the value
SET temp "temporary" EX 60     # Set with 60-second expiration
DEL name                       # Delete the key
GET name                       # Returns (nil) - key was deleted
```

## 🔄 Persistent Data

The append-only file (`redis-clone.aof`) stores all write commands. On server restart:
1. All persisted commands are read from the file
2. Commands are replayed in order
3. Server state is restored exactly as it was before shutdown

This ensures no data loss in case of unexpected crashes.

## 🎯 Performance Characteristics

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| GET | O(1) | HashMap lookup, with expiry check |
| SET | O(1) | HashMap insertion |
| DEL | O(1) | HashMap removal |
| Expiry Check | O(1) | Performed during key access |

## 🔐 Thread Safety

- **ConcurrentHashMap** ensures atomic read/write operations
- **ExecutorService** provides proper thread pool management
- **No race conditions** because:
  - Store operations are atomic
  - Each client runs independently
  - Expiration checks are thread-safe

## 📚 Learning Outcomes

This project demonstrates:
- **Concurrent Programming:** Thread pools and ExecutorService
- **Data Structures:** HashMap vs ConcurrentHashMap
- **Networking:** TCP servers and socket programming
- **File I/O:** Persistence and data recovery
- **Clean Architecture:** Separation of concerns across modules
- **Software Design:** Factory pattern for command processing

## 🛠️ Future Enhancements

Potential features for future versions:
- **INCR/DECR** - Atomic counters
- **KEYS** - Pattern-based key search
- **TTL** - Query remaining time-to-live
- **FLUSHDB** - Clear all data
- **INFO** - Server statistics
- **CONFIG** - Configuration management
- **RESP Protocol** - Redis Serialization Protocol for compatibility
- **Pub/Sub** - Message publishing and subscription
- **Data Structures** - Lists, Sets, Hashes (beyond strings)
- **LRU Eviction** - Memory limit with eviction policy

## 📖 Development Notes

### Connecting with Real Redis Clients
Currently, this implementation uses a simplified text protocol. To use with Redis CLI or other clients that expect RESP (Redis Serialization Protocol), the protocol layer would need to be updated.

### Memory Management
The current implementation has no built-in memory limits. As an enhancement, LRU (Least Recently Used) eviction could be implemented to manage memory when limits are exceeded.

### Persistence Details
- Write-only operations (SET, DEL) are persisted
- Read-only operations (GET) are not logged
- AOF entries are in plain text for debugging

## 📄 License

Educational project for university coursework.

## 👤 Author

Osama Alnajar

## 📞 Support

For issues or questions about the implementation, review the architecture section above or examine the source code comments.