package org.launchcode.whichwichcontactlist;

import org.launchcode.whichwichcontactlist.models.RequestOff;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class WhichwichContactListApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhichwichContactListApplication.class, args);
	}

	@Configuration
	public class AppConfig {

		@Bean
		public RequestOff requestOffDay() {
			return new RequestOff();
		}

	}
}

