package br.com.govbr.quarkussocial.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

	@NotBlank(message = "O Nome é requerido!")
	private String nome;
	@NotNull(message = "A Idade é requerida!")
	private Integer idade;
	
}
