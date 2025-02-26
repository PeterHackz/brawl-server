# brawl server
an old server I was working on, it is experimental, misses lot of features, and implements some stuff in a "bad" way that should be improved (or even rewritten).
# some info
I am no longer working on this project since months, or even year(s). Pull requests are welcome though.
Do not contact me asking how to run it if you do not have previous knowledge with java. the info in this readme should be enough.

I am open-sourcing this incase it will be helpful for someone. And honestly, there is no reason to keep it private.

If you have some questions about something in this project, confusing, or just wanna chat, dm me on discord: `@s.b`
# running the project
## configuration
check `src/main/java/com/brawl/server/ServerConfiguration.java`, you might want to change some stuff there. Also you **should** set a mongodb cluster URL.

You should also create a directory named `DataTables`, and place `csv_logic` and `csv_client` in it.
## compile & run
simply compile it with maven:
```
mvn install
```
then run it with:
```
java -jar target/Game-1.0.jar
```
# 🌟
give a 🌟 because why not ;)