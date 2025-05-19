# LogUtils - Lightweight Async Logging for Java ✨

**LogUtils** is a modular, async-ready logging utility designed to be simple to use, flexible to configure, and easy to drop into any Java project. It's ideal for tools, CLI apps, and multi-module projects that need clean logs without the bloat of larger frameworks.

---

## 🚀 Features

* Asynchronous logging with background flush
* Custom log formatters via lambdas or predefined modes
* Multiple log files via key-based routing
* Clean lifecycle management with shutdown hooks
* File naming strategies (static, date-based, timestamped, or custom)
* Console output with adjustable level filtering
* Pluggable design (custom log levels, future JSON support, etc.)

---

## Basic Usage

```java
LogHandler logger = new LogBuilder()
    .setKey("MAIN")
    .setLogDir("logs")
    .setLogBaseName("KoboldLog")
    .setNamingMode(NamingMode.DATE_PER_RUN)
    .setMinConsoleLevel(Level.INFO)
    .setMinLogLevel(Level.ALL)
    .buildAndInit();

logger.log(new LogEntry(Level.INFO, "Hello World!"));
```

This creates a log file like `KoboldLog-2025-05-19.txt` in `/logs` and writes to it asynchronously.

---

## 🧹 Advanced Features

### 🏛️ Multiple Log Streams

Use different `keys` to write to separate files:

```java
new LogBuilder()
    .setKey("NETWORK")
    .buildAndInit();

logger.log(new LogEntry(Level.INFO, "Main log"));
logger.log(new LogEntry(Level.INFO, "Net status", "NETWORK"));
```

### 🌎 Custom Naming Format

```java
LogBuilder builder = new LogBuilder()
    .setKey("CUSTOM")
    .setFileNameFormatter(ctx -> ctx.getKey() + "_" + ctx.getBaseName() + ".log");
```

### And preset Naming Format

```java
LogBuilder builder = new LogBuilder()
    .setKey("CUSTOM")
    .setFileNameFormatter(FileNameFormatPreset.DATE);
```

### 📊 Custom Log Format

```java
logger.setEntryFormatter(entry -> String.format("[%s] %s", entry.getLevel(), entry.getMessage()));
```
### And preset Log Format

```java
logger.setEntryFormatter(EntryFormatPreset.VERBOSE);
```

---

## ♻️ Graceful Shutdown

```java
Runtime.getRuntime().addShutdownHook(new Thread(LogLifecycleManager::shutdown));
```

Or manually:

```java
logger.shutdown();
```

---

## 🌟 Coming Soon (in 0.2.0)

* Ring buffer / crash-safe memory mode
* JSON + structured log support
* Testing utilities

---

## 📅 Example Output

```
[WARNING] this should log
[INFO] Now this should log too
[INFO] Now this should log an error
java.lang.IllegalAccessError: Failed to access protected resource
	at dev.tokage.App.handler(App.java:58)
	at dev.tokage.App.main(App.java:18)
[INFO] Logger is going to sleep. Goodbye
```

---

## License / Credit

Built with kobold magic and fueled by tea. MIT License.

Contributions welcome. Pip hoards PRs like they’re dice. 🦄
