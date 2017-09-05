# kafka test commands
./kafka-console-consumer.sh --bootstrap-server framework:9092 --topic test --from-begin
./kafka-topics.sh --zookeeper framework:2181 --create test
./kafka-topics.sh --zookeeper framework:2181 --list
./kafka-topics.sh --zookeeper framework:2181 --describe --topic test
./kafka-console-producer.sh --broker-list framework:9092 --topic test
./kafka-topics.sh --zookeeper framework:2181 --alter --topic test --config retention.ms=1000
