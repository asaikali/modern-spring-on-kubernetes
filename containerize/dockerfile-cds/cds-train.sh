java -Dspring.aot.enabled=true -Dspring.context.exit=onRefresh -XX:ArchiveClassesAtExit=./target/out/app.jsa -jar ./target/out/app.jar
ls -lah ./target/out
