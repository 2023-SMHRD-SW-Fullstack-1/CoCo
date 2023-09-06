package com.smhrd.coco.domain;

import com.smhrd.coco.domain.GoogleProfile;

import lombok.Data;

@Data
public class GoogleProfile {
	private String iss;
	private String azp;
	private String aud;
	private String sub;
	private String email;
	private String email_verified;
	private String at_hash;
	private String name;
	private String picture;
	private String given_name;
	private String family_name;
	private String locale;
	private String iat;
	private String exp;
	private String alg;
	private String kid;
	private String typ;

}
