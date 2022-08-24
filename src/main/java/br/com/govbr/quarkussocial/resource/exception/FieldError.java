package br.com.govbr.quarkussocial.resource.exception;

import lombok.Data;

@Data
public class FieldError {

	private String field;
	private String message;

	public FieldError(String field, String message) {
		super();
		this.field = field;
		this.message = message;
	}

}
