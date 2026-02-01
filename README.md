# Brawl Server

An experimental Java-based implementation of a custom networking protocol and server architecture. This project was developed as a study of real-time communication between a server and a mobile client, focusing on packet serialization, custom cryptography, and state management.

### Technical Scope

This project is a prototype. It focuses on several core areas of backend engineering:

* Protocol Analysis: Implementation of a custom messaging system (`PiranhaMessage`) for handling structured binary data.
* Networking: Multi-threaded server logic using TCP/UDP for handling client connections and state synchronization.
* Data Persistence: Integration with MongoDB for player data and server-side configuration.
* Cryptography: Implementation of secure handshakes and data encryption using TweetNacl and custom crypto layers (`PepperCrypto`).

### Note

I am no longer actively maintaining this project. It is open-sourced as a reference for those interested in the implementation of custom game protocols in Java. While some sections are prototypes that could be optimized or rewritten, the core messaging and networking logic remain functional as a learning resource.

Pull requests are welcome. If you have questions about specific implementation details or the byte-stream logic, reach out.

### Contact

* Discord: `@s.b`
* Community: [discord.peterr.dev](https://discord.peterr.dev)

---

# Running the Project

## Configuration

Check `src/main/java/com/brawl/server/ServerConfiguration.java` to modify server parameters. **You must set a valid MongoDB cluster URL.**

You also need to create a directory named `DataTables` in the root and place your `csv_logic` and `csv_client` files there.

## Compile & Run

Simply compile it with Maven:

```bash
mvn install
```

Then run it with:

```bash
java -jar target/Game-1.0.jar
```

# 🌟

If this repository helped you understand custom packet handling or Java networking, feel free to leave a 🌟.
