package com.example.batchadmin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.dataflow.server.EnableDataFlowServer
import org.springframework.cloud.task.configuration.MetricsAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import javax.sql.DataSource


@SpringBootApplication(
    exclude = [
        FlywayAutoConfiguration::class,
        MetricsAutoConfiguration::class,
        SessionAutoConfiguration::class
    ]
)
@EnableDataFlowServer
@EnableConfigurationProperties(value = [AdminInfoConfig::class])
class BatchAdminApplication

fun main(args: Array<String>) {
    runApplication<BatchAdminApplication>(*args)
}

@Bean
@Primary
fun entityManager(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
    return LocalContainerEntityManagerFactoryBean().apply {
        this.dataSource = dataSource
        this.persistenceUnitName = DefaultPersistenceUnitManager.ORIGINAL_DEFAULT_PERSISTENCE_UNIT_NAME
        jpaVendorAdapter = HibernateJpaVendorAdapter()
    }
}


@ConstructorBinding
@ConfigurationProperties("admin")
data class AdminInfoConfig(
    val username: String,
    val password: String
)

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val adminInfoConfig: AdminInfoConfig
) : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .defaultSuccessUrl(LOGIN_SUCCESS_URI, true)
            .permitAll()
            .and()
            .logout()
            .logoutSuccessUrl(LOGOUT_AFTER_URI)
            .permitAll()
            .and()
            .httpBasic()
            .and()
            .csrf().disable()
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
            .withUser(adminInfoConfig.username)
            .password(passwordEncoder().encode(adminInfoConfig.password))
            .roles(USER_ROLE)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    companion object {
        const val LOGOUT_AFTER_URI = "/login"
        const val LOGIN_SUCCESS_URI = "/dashboard"
        const val USER_ROLE = "ADMIN"
    }
}
