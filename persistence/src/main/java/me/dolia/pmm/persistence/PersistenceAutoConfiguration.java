package me.dolia.pmm.persistence;

import me.dolia.pmm.persistence.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class PersistenceAutoConfiguration {

}
