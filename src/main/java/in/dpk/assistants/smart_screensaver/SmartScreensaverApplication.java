package in.dpk.assistants.smart_screensaver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartScreensaverApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartScreensaverApplication.class, args);
	}

}
