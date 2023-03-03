package com.leijendary.spring.template.iam

import com.leijendary.spring.template.container.JaegerContainerTest
import com.leijendary.spring.template.container.KafkaContainerTest
import com.leijendary.spring.template.container.PostgresContainerTest
import com.leijendary.spring.template.container.RedisContainerTest
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
