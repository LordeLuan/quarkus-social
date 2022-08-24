package br.com.govbr.quarkussocial.resource;


import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.govbr.quarkussocial.domain.User;
import br.com.govbr.quarkussocial.dto.CreateUserRequest;
import br.com.govbr.quarkussocial.repositories.UserRepositorie;
import br.com.govbr.quarkussocial.resource.exception.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Usuários", description = "Endpoints relacionados a operações referente a entidade User.")
public class UserResource {

	@Inject
	private Validator validator;
	
	@Inject
	private UserRepositorie repository;
	
	@POST
	@Transactional
	@Operation(summary = "Cria um usuário no banco de dados.")
	public Response createUser(@RequestBody CreateUserRequest userRequest) {
		
//		Para validar os campos mapeados no DTO com as annotations do hibernate validator, retorna uma lista de objetos do tipo constraintViolation
		Set<ConstraintViolation<CreateUserRequest>> violation =  validator.validate(userRequest);
		
		if(!violation.isEmpty()) {
//			ConstraintViolation<CreateUserRequest> erro = violation.stream().findAny().get();
//			String mensagemErro = erro.getMessage();
			
//			Formata os erros de validação que ocorrerem, utilizando as classes ResponseError e FieldError pegando o campo e a mensagem do erro
//			ResponseError responseErro = ResponseError.createFromValidation(violation);
			
			return ResponseError.createFromValidation(violation).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
		}
		
		User user = new User();
		user.setIdade(userRequest.getIdade());
		user.setNome(userRequest.getNome());
		
		repository.persist(user);
		
		return Response.status(Status.CREATED.getStatusCode()).entity(user).build();
	}
	
	@GET
	public Response listAllUser() {
		PanacheQuery<User> query =  repository.findAll();
		return Response.ok(query.list()).build();
	}
	
	@Path("{id}")
	@DELETE
	@Transactional
	public Response deleteUser(@PathParam("id") Long id) {
		User user = repository.findById(id);
		
		if(user != null) {
			repository.delete(user);
			return Response.noContent().build();
		}
		
		return Response.status(Response.Status.NOT_FOUND).build();
	}
	
	@Path("{id}")
	@PUT
	@Transactional
	public Response updateUser(@PathParam("id") Long id, CreateUserRequest userRequest) {
		User user = repository.findById(id);
		
		if(user != null) {
			user.setIdade(userRequest.getIdade());
			user.setNome(userRequest.getNome());
			
//			Atualiza mesmo sem mandar persistir novamente no banco, por causa da anotation Transactional
			repository.persist(user);
			return Response.ok(user).build();
		}
		
		return Response.status(Status.NOT_FOUND).build();
	}
	
}
