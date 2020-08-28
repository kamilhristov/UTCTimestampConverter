# Debezium MYSQL UTCTimestamp Converter  
Currently debezium converts all of timestamp fields into UTC and sometimes we need local timestamp in upstream tools (Apache Hive in our cases). 

Fixes "Temporal values without time zones"

> The DATETIME type represents a local date and time such as "2018-01-13 09:48:27". As you can see, there is no time zone information. Such columns are converted into epoch milli-seconds or micro-seconds based on the column’s precision by using UTC. The TIMESTAMP type represents a timestamp without time zone information and is converted by MySQL from the server (or session’s) current time zone into UTC when writing and vice versa when reading back the value. For example:

### Before:

`DATETIME` with a value of `2018-06-20 06:37:03` becomes `1529476623000`

### Now:

`DATETIME` with a value of `2018-06-20 06:37:03` becomes `2018-06-20T06:37:03.000-0400`

Above is suitable for Elasticsearch sink.

## Make Package
```
mvn package -Dmaven.test.skip=true
```

## Usage
in docker file, just copy library into mysql path  
```
COPY UTCTimestampConverter-1.0.0-SNAPSHOT.jar /kafka/connect/debezium-connector-mysql/
```
### Configuration
```json
"converters": "utcTimestampConverter",
"utcTimestampConverter.type": "snapp.kafka.connect.util.UTCTimestampConverter"
```
