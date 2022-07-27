package blog.braindose.kafka;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

import io.confluent.connect.avro.Account;
import io.confluent.connect.avro.Transaction;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.openjdk.jol.info.ClassLayout;

import java.util.Properties;

public class KafkaPerformance {

    private String kafkaMessage;
    private byte[] payload;
    private int recordSize;
    private long numRecords;
    private String bootstrapServer;
    private String compressionType;
    private int batchSize;
    private long lingerMS;
    private long bufferMemory;
    private String acks;
    private int sendBufferBytes;
    private int receiveBufferBytes;
    private long maxBlockMS;
    private int deliveryTimeoutMS;
    private Properties props;
    private String kafkaTopic;

    private int throttleSizeRate; // Throttle by message size per second
    private int throttleMessageRate; // Throttle by number message per second
    private KafkaStatistics stats;
    private Throttle throttle;

    public KafkaPerformance(String kafkaTopic, String kafkaMessage, int recordSize, long numRecords,
            String bootstrapServer, String schemaRegistryUrl, String schemaRegistryCred, String compressionType, int batchSize, long lingerMS, long bufferMemory,
            String acks, int sendBufferBytes, int receiveBufferBytes, long maxBlockMS, int deliveryTimeoutMS,
            int throttleSizeRate, int throttleMessageRate, String securityProtocol, String saslMechanism, String jaasConfig) {
        this.kafkaTopic = kafkaTopic;
        this.kafkaMessage = kafkaMessage;
        this.recordSize = recordSize;
        this.numRecords = numRecords;
        this.bootstrapServer = bootstrapServer;
        this.compressionType = compressionType;
        this.batchSize = batchSize;
        this.lingerMS = lingerMS;
        this.bufferMemory = bufferMemory;
        this.acks = acks;
        this.sendBufferBytes = sendBufferBytes;
        this.receiveBufferBytes = receiveBufferBytes;
        this.maxBlockMS = maxBlockMS;
        this.deliveryTimeoutMS = deliveryTimeoutMS;
        this.throttleMessageRate = throttleMessageRate;
        this.throttleSizeRate = throttleSizeRate;

        this.props = new Properties();
        System.out.println("JUDE ADDED::::" + jaasConfig);
        this.props.put("bootstrap.servers", this.bootstrapServer);
        this.props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        this.props.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
        this.props.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, schemaRegistryCred);
        this.props.put("key.serializer", StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        this.props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        this.props.put("sasl.mechanism", saslMechanism);
//        this.props.put("sasl.jaas.config", jaasConfig);
        this.props.put("compression.type", this.compressionType);
        this.props.put("batch.size", this.batchSize);
        this.props.put("linger.ms", this.lingerMS);
        this.props.put("buffer.memory", this.bufferMemory);
        this.props.put("acks", this.acks);
        this.props.put("send.buffer.bytes", this.sendBufferBytes);
        this.props.put("receive.buffer.bytes", this.receiveBufferBytes);
        this.props.put("max.block.ms", this.maxBlockMS);
        this.props.put("delivery.timeout.ms", this.deliveryTimeoutMS);

        initMessage();
    }
    public KafkaPerformance(String kafkaTopic, String kafkaMessage, int recordSize, BigDecimal numRecords,
            String bootstrapServer, String schemaRegistryUrl, String schemaRegistryCred, String compressionType, int batchSize, long lingerMS, long bufferMemory,
            String acks, int sendBufferBytes, int receiveBufferBytes, long maxBlockMS, int deliveryTimeoutMS,
            int throttleSizeRate, int throttleMessageRate, String securityProtocol, String saslMechanism, String jaasConfig) {
        this.kafkaTopic = kafkaTopic;
        this.kafkaMessage = kafkaMessage;
        this.recordSize = recordSize;
        this.numRecords = numRecords.toBigInteger().longValue();
        this.bootstrapServer = bootstrapServer;
        this.compressionType = compressionType;
        this.batchSize = batchSize;
        this.lingerMS = lingerMS;
        this.bufferMemory = bufferMemory;
        this.acks = acks;
        this.sendBufferBytes = sendBufferBytes;
        this.receiveBufferBytes = receiveBufferBytes;
        this.maxBlockMS = maxBlockMS;
        this.deliveryTimeoutMS = deliveryTimeoutMS;
        this.throttleMessageRate = throttleMessageRate;
        this.throttleSizeRate = throttleSizeRate;

        this.props = new Properties();
        System.out.println("JUDE ADDED::::" + jaasConfig);
        this.props.put("bootstrap.servers", this.bootstrapServer);
        this.props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        this.props.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
        this.props.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, schemaRegistryCred);
        this.props.put("key.serializer", StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        this.props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        this.props.put("sasl.mechanism", saslMechanism);
//        this.props.put("sasl.jaas.config", jaasConfig);
        this.props.put("compression.type", this.compressionType);
        this.props.put("batch.size", this.batchSize);
        this.props.put("linger.ms", this.lingerMS);
        this.props.put("buffer.memory", this.bufferMemory);
        this.props.put("acks", this.acks);
        this.props.put("send.buffer.bytes", this.sendBufferBytes);
        this.props.put("receive.buffer.bytes", this.receiveBufferBytes);
        this.props.put("max.block.ms", this.maxBlockMS);
        this.props.put("delivery.timeout.ms", this.deliveryTimeoutMS);

        initMessage();
    }

    public KafkaPerformance(String kafkaTopic, String kafkaMessage, int recordSize, int numRecords,
            String bootstrapServer, String schemaRegistryUrl, String schemaRegistryCred, String compressionType, int batchSize, long lingerMS, long bufferMemory,
            String acks, int sendBufferBytes, int receiveBufferBytes, long maxBlockMS, int deliveryTimeoutMS, String securityProtocol, String saslMechanism, String jaasConfig) {

        this(kafkaTopic, kafkaMessage, recordSize, numRecords, bootstrapServer, schemaRegistryUrl, schemaRegistryCred, compressionType, batchSize, lingerMS,
                bufferMemory, acks, sendBufferBytes, receiveBufferBytes, maxBlockMS, deliveryTimeoutMS, 0, 0, securityProtocol, saslMechanism, jaasConfig);
    }

    private void initMessage() {
        if (this.kafkaMessage != null && this.kafkaMessage.trim() != "") {
            payload = kafkaMessage.getBytes();
        } else {
            resizePayload();
        }
    }

    private void resizePayload() {
        if (this.recordSize > 0) {
            this.payload = new byte[this.recordSize];
            Random random = new Random(0);
            for (int i = 0; i < payload.length; ++i)
                this.payload[i] = (byte) (random.nextInt(26) + 65);
        }
    }

    public void send() {

        KafkaProducer<String, Account> accountKafkaProducer = new KafkaProducer<>(this.props);
        ProducerRecord<String, Account> accountProducerRecord;

        KafkaProducer<String, Transaction> transacKafkaProducer = new KafkaProducer<>(this.props);
        ProducerRecord<String, Transaction> transacProducerRecord;

        this.throttle = new Throttle(this.throttleSizeRate, this.throttleMessageRate);
        this.stats = new KafkaStatistics(System.currentTimeMillis(), this.numRecords);

        for (int i = 0; i < numRecords; i++) {
            this.throttle.throttle(this.stats.getAverageBytesRate(), this.stats.getAverageRecordRate());
            if (this.kafkaTopic.contains("account")) {
                final Account account = Account.newBuilder()
                        .setACCOUNTID("39207100000000")
                        .setPRODUCTID("ProfitLoss")
                        .setCUSTOMERCODE("1")
                        .setISOCURRENCYCODE("GBP")
                        .setCREDITLIMIT(ByteBuffer.allocate(4).putInt(0))
                        .setUBACCOUNTSTATUS("active")
                        .setDEBITLIMIT(ByteBuffer.allocate(4).putInt(0))
                        .setACCSTOPPED("N")
                        .setACCSTOPDATE(LocalDateTime.now().toString())
                        .setACCCLOSED("N")
                        .setACCCLOSUREDATE(LocalDateTime.now().toString())
                        .setACCRIGHTSINDICATOR(ByteBuffer.allocate(4).putInt(0))
                        .setACCRIGHTSINDCHANGEDT(LocalDateTime.now().toString())
                        .setACCLIMITEXCESSACTION(ByteBuffer.allocate(4).putInt(0))
                        .setACCLIMITINDICATOR(ByteBuffer.allocate(4).putInt(0))
                        .setACCLASTCREDITLIMITCHANGEDATE(LocalDateTime.now().toString())
                        .setACCLASTDEBITLIMITCHANGEDATE(LocalDateTime.now().toString())
                        .setBOOKBALANCE(ByteBuffer.allocate(16).putDouble(25979.77))
                        .setCLEAREDBALANCE(ByteBuffer.allocate(16).putDouble(25979.79))
                        .setBLOCKBALANCE(ByteBuffer.allocate(4).putInt(0))
                        .setLIMITEXPIRYDATE(LocalDateTime.now().toString())
                        .setLIMITREVIEWDATE(LocalDateTime.now().toString())
                        .setOPENDATE(LocalDateTime.now().toString())
                        .build();
                accountProducerRecord = new ProducerRecord<>(this.kafkaTopic, account.getACCOUNTID().toString(), account);

                long instanceSize = ClassLayout.parseInstance(account).instanceSize();
                long start = System.currentTimeMillis();

                accountKafkaProducer.send(accountProducerRecord, new KafkaRequestCallback(i, start, instanceSize, this.stats));
            } else {
                final Transaction transaction = Transaction.newBuilder()
                        .setINTACCDTODATECR(ByteBuffer.allocate(4).putInt(0))
                        .setINTACCDTODATEDR(ByteBuffer.allocate(4).putInt(0))
                        .setINTADJAMOUNTCR(ByteBuffer.allocate(4).putInt(0))
                        .setINTADJAMOUNTDR(ByteBuffer.allocate(4).putInt(0))
                        .setACCOUNTID("39209000000000")
                        .setCUSTOMERCODE("2")
                        .setREFERENCE("1")
                        .setNARRATION("UKFPSImmediatePayment011722d7fa923PC9OUTWARD_INITIATION123.00GBPdsfsdfdsdf")
                        .setTRANSACTIONCODE("CAD")
                        .setISOCURRENCYCODE("GBP")
                        .setBRANCHSORTCODE("1")
                        .setAMOUNTDEBIT(ByteBuffer.allocate(4).putInt(34550))
                        .setAMOUNTCREDIT(ByteBuffer.allocate(4).putInt(0))
                        .setVALUEDATE(LocalDateTime.now().toString())
                        .setTRANSACTIONDATE(LocalDateTime.now().toString())
                        .setPOSTINGDATE(LocalDateTime.now().toString())
                        .setBOOKBALANCE(ByteBuffer.allocate(4).putInt(25000))
                        .setCLEAREDBALANCE(ByteBuffer.allocate(4).putInt(10000))
                        .setTRANSACTIONSRID("011722b3a991eG1w")
                        .setBASEEQUIVALENT(ByteBuffer.allocate(4).putInt(123))
                        .setAMOUNT(ByteBuffer.allocate(4).putInt(25000))
                        .setUSERID("bankadmin1")
                        .setUBTYPE("N")
                        .setUBACCTRANSCOUNTER(ByteBuffer.allocate(4).putInt(6))
                        .setREVERSALINDICATOR(ByteBuffer.allocate(4).putInt(0))
                        .setSTATEMENTFLAG(ByteBuffer.allocate(4).putInt(0))
                        .setTRANSACTIONID("011722b3a9908G1p")
                        .setUBCHANNELID("BranchTeller")
                        .setCHEQUEDRAFTNUMBER(ByteBuffer.allocate(4).putInt(0))
                        .setDEBITCREDITFLAG("D")
                        .setCUSTSHORTNAME("ADDITIONAL SHORT TERM INSURANCE - GBP")
                        .setACCOUNTNAME("COMMITMENT CONTRA - GBP")
                        .setAUTHORISEDUSERID("bankadmin2")
                        .setEXCHANGERATE(ByteBuffer.allocate(4).putInt(1))
                        .setORIGINALAMOUNT(ByteBuffer.allocate(4).putInt(35000))
                        .setTABSOURCE("TX")
                        .build();
                transacProducerRecord = new ProducerRecord<>(this.kafkaTopic, transaction.getACCOUNTID().toString(), transaction);

                long instanceSize = ClassLayout.parseInstance(transaction).instanceSize();
                long start = System.currentTimeMillis();

                transacKafkaProducer.send(transacProducerRecord, new KafkaRequestCallback(i, start, instanceSize, this.stats));
            }
        }

        accountKafkaProducer.flush();
        transacKafkaProducer.flush();
        this.stats.printTotalResult();
        accountKafkaProducer.close();
        transacKafkaProducer.close();
    }

    public static class KafkaStatistics {

        private long totalLatency, totalWindowLatency;
        private long[] latencies;
        private long sampling, latencyIndex;
        private int totalRecords, totalWindowRecords;
        private long totalBytes, totalWindowBytes;
        private long startTime, startWindow;
        // private int minLatency = 60 * 60 * 1000, minWindowLatency = 60 * 60 * 1000;
        // // defaulted to 1 hour
        private int maxLatency, maxWindowLatency;
        // private double averageSizeRate, averageWindowSizeRate; // average bytes per
        // ms
        // private double averageMessageRate, averageWindowMessageRate; // average
        // number per ms

        public KafkaStatistics(long startTime, long numRecords) {
            this.startTime = startTime;
            this.startWindow = startTime;
            this.sampling = (long) (numRecords / Math.min(numRecords, 500000));
            this.latencies = new long[(int) (numRecords / this.sampling) + 1];
        }

        public void record(int index, int latency, long byteCount) {
            this.totalLatency += latency;
            this.totalWindowLatency += latency;
            this.totalRecords++;
            this.totalWindowRecords++;
            this.totalBytes += byteCount;
            this.totalWindowBytes += byteCount;
            this.maxLatency = Math.max(this.maxLatency, latency);
            this.maxWindowLatency = Math.max(this.maxWindowLatency, latency);
            // this.averageSizeRate = this.totalBytes / (double) this.totalLatency;
            // this.averageMessageRate = this.totalRecords / (double) this.totalLatency;
            // this.averageWindowSizeRate = this.totalWindowBytes / (double)
            // this.totalWindowLatency;
            // this.averageWindowMessageRate = this.totalWindowRecords / (double)
            // this.totalWindowLatency;
            // this.minLatency = Math.min(this.minLatency, latency);
            // this.minWindowLatency = Math.min(this.minLatency, latency);

            if (index % this.sampling == 0) {
                this.latencies[(int)latencyIndex] = latency;
                this.latencyIndex++;
            }

            int timePast = (int) (System.currentTimeMillis() - this.startWindow);
            if (timePast > 5000) {
                printWindowResult();
                newWindow();
            }
        }

        private void newWindow() {
            this.startWindow = System.currentTimeMillis();
            this.totalWindowLatency = 0;
            this.totalWindowRecords = 0;
            this.totalWindowBytes = 0;
            this.maxWindowLatency = 0;
            // this.averageWindowMessageRate = 0;
            // this.averageWindowSizeRate = 0;
            // this.minWindowLatency = 60 * 60 * 1000;
        }

        public void printWindowResult() {
            int duration = (int) (System.currentTimeMillis() - this.startWindow);
            System.out.printf(
                    "Average Latency(ms): %.2f, Max Latency(ms): %d, Average MB/sec: %.2f, Record Rate: %.0f, Total Record: %.0f %n",
                    this.totalWindowLatency / (double) this.totalWindowRecords, this.maxWindowLatency,
                    1000.0 * this.totalWindowBytes / (1024.0 * 1024.0) / duration,
                    1000.0 * this.totalWindowRecords / duration, (double) this.totalWindowRecords);
        }

        public void printTotalResult() {

            System.out.println("\nSummary of Overall Results:");

            long[] percs = percentiles(this.latencies, this.latencyIndex, 0.5, 0.95, 0.99, 0.999);
            int duration = (int) (System.currentTimeMillis() - this.startTime);
            System.out.printf(
                    "Average Latency(ms): %.2f, " +
                            "Max Latency(ms): %d, " +
                            "Average MB/sec: %.2f, " +
                            "Record Rate(per second how many records we sent): %.0f, " +
                            "Total Record: %.0f, " +
                            "50th: %d ms, 95th: %d ms, 99th: %d ms, 99.9th: %d ms %n\n",
                    this.totalLatency / (double) this.totalRecords,
                    this.maxLatency,
                    1000.0 * this.totalBytes / (1024.0 * 1024.0) / duration,
                    1000.0 * this.totalRecords / duration,
                    (double) this.totalRecords,
                    percs[0], percs[1], percs[2], percs[3]);
        }

        private long[] percentiles(long[] latencies, long count, double... percentiles) {
            int size = Math.min((int)count, latencies.length);
            Arrays.sort(latencies, 0, size);
            long[] values = new long[percentiles.length];
            for (int i = 0; i < percentiles.length; i++) {
                int index = (int) (percentiles[i] * size);
                values[i] = latencies[index];
            }
            return values;
        }

        public double getAverageBytesRate() {
            int duration = (int) (System.currentTimeMillis() - this.startTime);
            return 1000.0 * this.totalBytes / duration;
            // return this.averageSizeRate;
        }

        public double getAverageRecordRate() {
            int duration = (int) (System.currentTimeMillis() - this.startTime);
            return 1000.0 * this.totalRecords / duration;
            // return this.averageMessageRate;
        }
    }

    public static final class KafkaRequestCallback implements Callback {
        private final long bytes;
        private final long start;
        private final KafkaStatistics stats;
        private final int index;

        public KafkaRequestCallback(int index, long start, long recordSize, KafkaStatistics stats) {
            this.index = index;
            this.start = start;
            this.bytes = recordSize;
            this.stats = stats;
        }

        public void onCompletion(RecordMetadata metadata, Exception exception) {

            long end = System.currentTimeMillis();
            int duration = (int) (end - this.start);
            // System.out.println("duration = " + (duration/1000) + "s");

            if (exception != null) {
                System.out.println("Error received from kafka request: " + exception.getMessage());
                // System.out.println("Latency: %d ms." + (double)duration);
            } else {
                // stats.printResult();
                this.stats.record(this.index, duration, this.bytes);
            }

            // System.out.println("DURATION: " + duration + " ms");
        }
    }

}
