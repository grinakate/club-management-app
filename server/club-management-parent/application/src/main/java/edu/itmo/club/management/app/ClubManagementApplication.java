package edu.itmo.club.management.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {
		"edu.itmo.club.management.app",
		"edu.itmo.club.management.service"
})
@EntityScan(basePackages = "edu.itmo.club.management.domain.entity")
@EnableJpaRepositories(basePackages = "edu.itmo.club.management.domain.repository")
public class ClubManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClubManagementApplication.class, args);
	}
}
