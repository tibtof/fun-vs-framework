package fvf4j.demo;

import org.springframework.boot.SpringApplication;

public class TestDemoApplication {

	public static void main(String[] args) {
		SpringApplication.from(TransactionCategorizationApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
