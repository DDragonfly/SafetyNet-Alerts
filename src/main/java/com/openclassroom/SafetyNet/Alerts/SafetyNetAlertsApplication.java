package com.openclassroom.SafetyNet.Alerts;

import com.openclassroom.SafetyNet.Alerts.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SafetyNetAlertsApplication {

	public static void main(String[] args) {

		SpringApplication.run(SafetyNetAlertsApplication.class, args);
	}

}
