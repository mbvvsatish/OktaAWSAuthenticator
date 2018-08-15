package com.aws.sts.auth.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetaData {

	@JsonProperty(index=1)
	public String Code;
	@JsonProperty(index=2)
	public String LastUpdated;
	@JsonProperty(index=3)
	public String Type;
	@JsonProperty(index=4)
	public String AccessKeyId;
	@JsonProperty(index=5)
	public String SecretAccessKey;
	@JsonProperty(index=6)
	public String Token;
	@JsonProperty(index=7)
	public String Expiration;
}
