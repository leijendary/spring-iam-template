package com.leijendary.spring.template.iam

import com.leijendary.spring.template.iam.container.JaegerContainerTest
import com.leijendary.spring.template.iam.container.KafkaContainerTest
import com.leijendary.spring.template.iam.container.PostgresContainerTest
import com.leijendary.spring.template.iam.container.RedisContainerTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(
    initializers = [
        JaegerContainerTest.Initializer::class,
        KafkaContainerTest.Initializer::class,
        PostgresContainerTest.Initializer::class,
        RedisContainerTest.Initializer::class,
    ]
)
@AutoConfigureMockMvc
class ApplicationTest {
    @Test
    fun contextLoads() {
    }
}
