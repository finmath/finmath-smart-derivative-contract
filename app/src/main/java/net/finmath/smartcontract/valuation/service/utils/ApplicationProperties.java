package net.finmath.smartcontract.valuation.service.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implements list of sdcUsers from application.yml
 */
@Component
@ConfigurationProperties(prefix = "data.sdc")
public class ApplicationProperties {
	private List<SDCUser> users;

	public List<SDCUser> getUsers() {
		return users;
	}

	public void setUsers(List<SDCUser> users) {
		this.users = users;
	}

}
