package com.leijendary.spring.template.iam.core.config

import com.leijendary.spring.template.iam.core.config.properties.*
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    AuthProperties::class,
    AwsCloudFrontProperties::class,
    AwsS3Properties::class,
    DataSourcePrimaryProperties::class,
    DataSourceReadonlyProperties::class,
    InfoProperties::class,
    KafkaTopicProperties::class,
    VerificationProperties::class,
)
class PropertiesConfiguration 
