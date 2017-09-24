# kafka test commands
./kafka-console-consumer.sh --bootstrap-server framework:9092 --topic measurements --from-begin
./kafka-topics.sh --create --topic measurements --zookeeper framework:2181 --partitions 1 --replication-factor 1
./kafka-topics.sh --zookeeper framework:2181 --describe --topic measurements
./kafka-console-producer.sh --broker-list framework:9092 --topic measurements
./kafka-topics.sh --zookeeper framework:2181 --alter --topic measurements --config retention.ms=1000

sudo tc qdisc add dev vboxnet0 root netem delay 1ms
sudo tc qdisc add dev loopback root netem delay 1ms

sudo tc qdisc del dev vboxnet0 root netem
sudo tc qdisc del dev lo root netem

apt-get install zookeeperd