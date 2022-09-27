package net.finmath.smartcontract.service.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Implements list of sdcUsers from application.yml
 */
@Component
@ConfigurationProperties(prefix="data")
public class ApplicationProperties {
	private List<SDCUser> sdcUsers;

	public List<SDCUser> getSdcUsers() {
		return sdcUsers;
	}

	public void setSdcUsers(List<SDCUser> sdcUsers) {
		this.sdcUsers = sdcUsers;
	}

}
