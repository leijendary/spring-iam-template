package com.leijendary.spring.template.iam.core.config

import com.leijendary.spring.template.iam.core.config.Header.CODE
import com.leijendary.spring.template.iam.core.config.Header.NAME
import com.leijendary.spring.template.iam.core.model.Countries
import org.apache.commons.csv.CSVFormat
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private enum class Header {
    NAME, CODE
}

@Configuration
class CountryConfiguration {
    @Bean
    fun countries(): Countries {
        val resource = this.javaClass.getResourceAsStream("/csv/countries.csv")!!
        val reader = resource.bufferedReader()
        val records = CSVFormat.Builder
            .create()
            .setHeader(Header::class.java)
            .setSkipHeaderRecord(true)
            .build()
            .parse(reader)
        val countries = Countries()

        records.use { parser ->
            parser.forEach { record ->
                val code = record[CODE]
                val name = record[NAME]
                countries[code] = name
            }
        }

        return countries
    }
}
