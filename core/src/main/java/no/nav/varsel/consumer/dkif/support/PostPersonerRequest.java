package no.nav.varsel.consumer.dkif.support;

import lombok.Builder;

import java.util.List;

@Builder
public class PostPersonerRequest {

	public List<String> personidenter;
}
