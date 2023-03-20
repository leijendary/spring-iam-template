package com.leijendary.spring.template.iam.core.config

import com.leijendary.spring.template.iam.core.config.properties.KafkaTopicProperties
import com.leijendary.spring.template.iam.core.interceptor.KafkaInterceptor
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.RoundRobinAssignor
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.RoundRobinPartitioner
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.config.SaslConfigs.SASL_JAAS_CONFIG
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.*
import org.springframework.kafka.core.KafkaAdmin.NewTopics
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler

@Configuration
@EnableKafka
class KafkaConfiguration(
    private val kafkaProperties: KafkaProperties,
    private val kafkaTopicProperties: KafkaTopicProperties
) {
    companion object {
        private const val TOPIC_DEAD_LETTER_SUFFIX = ".error"
        private const val JAAS_OPTION_USERNAME = "username"
        private const val JAAS_OPTION_PASSWORD = "password"
    }

    @Bean
    fun topics(): NewTopics {
        val topics = kafkaTopicProperties.values.flatMap {
            val topic = TopicBuilder
                .name(it.name)
                .partitions(it.partitions)
                .replicas(it.replicas)
                .build()
            val deadLetterTopic = TopicBuilder
                .name("${it.name}$TOPIC_DEAD_LETTER_SUFFIX")
                .partitions(1)
                .replicas(1)
                .build()

            listOf(topic, deadLetterTopic)
        }

        return NewTopics(*topics.toTypedArray())
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        val config = mutableMapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to kafkaProperties.consumer.groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
            ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG to KafkaInterceptor::class.java.name,
            ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG to RoundRobinAssignor::class.java.name,
        )
        config.putAll(kafkaProperties.properties)

        configureSecurity(config)

        return DefaultKafkaConsumerFactory(config)
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val config = mutableMapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name,
            ProducerConfig.INTERCEPTOR_CLASSES_CONFIG to KafkaInterceptor::class.java.name,
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
            ProducerConfig.ACKS_CONFIG to "all",
            ProducerConfig.PARTITIONER_CLASS_CONFIG to RoundRobinPartitioner::class.java.name
        )
        config.putAll(kafkaProperties.properties)

        configureSecurity(config)

        return DefaultKafkaProducerFactory(config)
    }

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, String>,
        template: KafkaTemplate<String, String>
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        val recover = DeadLetterPublishingRecoverer(template) { record, _ ->
            TopicPartition(record.topic() + TOPIC_DEAD_LETTER_SUFFIX, record.partition())
        }
        val errorHandler = DefaultErrorHandler(recover)
        val concurrency = kafkaProperties.listener.concurrency
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory
        factory.containerProperties.isObservationEnabled = true
        factory.setCommonErrorHandler(errorHandler)
        factory.setConcurrency(concurrency)

        return factory
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory).apply {
            setMicrometerEnabled(true)
            setObservationEnabled(true)
        }
    }

    private fun configureSecurity(config: MutableMap<String, Any>) {
        val isEnabled = kafkaProperties.jaas.isEnabled

        if (!isEnabled) {
            return
        }

        val security = kafkaProperties.security.buildProperties()
        val jaas = kafkaProperties.jaas
        val options = jaas.options
        val controlFlag = jaas.controlFlag.name.lowercase()
        val username = options[JAAS_OPTION_USERNAME]!!
        val password = options[JAAS_OPTION_PASSWORD]!!

        config.putAll(security)
        config[SASL_JAAS_CONFIG] = "${jaas.loginModule} $controlFlag username=\"$username\" password=\"$password\";"
    }
}
