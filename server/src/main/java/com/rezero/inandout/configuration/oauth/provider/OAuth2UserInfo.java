package com.rezero.inandout.configuration.oauth.provider;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}
