java -Djarmode=tools \
  -jar target/layers-dockerfile-cds-0.0.1-SNAPSHOT.jar \
  extract --launcher --layers --destination target/out --application-filename app.jar
