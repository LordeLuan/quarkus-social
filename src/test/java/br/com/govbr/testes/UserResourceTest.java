package br.com.govbr.testes;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import br.com.govbr.quarkussocial.dto.CreateUserRequest;
import br.com.govbr.quarkussocial.resource.exception.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Para definir a ordem de execução dos testes
public class UserResourceTest {

//	URL do endpoint para teste
	@TestHTTPResource("/users")
	URL apiURL;
	
	@Test
	@DisplayName("Deve criar um usuário com sucesso!")
	@Order(1)
	public void createUserTest() {
		var user = new CreateUserRequest();
		user.setNome("Moacir");
		user.setIdade(30);
		
		var response =
					given()
						.contentType(ContentType.JSON)
						.body(user)
					.with()
						.post(apiURL)
					.then()
						.extract().response();
					
		assertEquals(201, response.statusCode());
		assertNotNull(response.jsonPath().getString("id"));
	
	}
	
	@Test
	@DisplayName("should return error when json is not valid")
	@Order(2)
	public void createUserValidationErrorTest() {
		var user = new CreateUserRequest();
//		setando as info pra nula para assegurar que vai dar exceção
		user.setNome(null);
		user.setIdade(null);
//		Fazendo a requisição e pegando a resposta
		var response = given()
							.contentType(ContentType.JSON)
							.body(user)
						.when()
							.post(apiURL)
						.then()
							.extract().response();
//		Compara o status esperado e o status que retorou da resposta
		assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
//		Compara a mensagem de erro esperada e a mensagem que veio no campo message da resposta.;
		assertEquals("Validation Error", response.jsonPath().getString("message"));
		
//		Pega a lista errors da resposta da requisição
		List<Map<String, String>> errors = response.jsonPath().getList("errors");
//		Verifica se o campo message não é vazio nos itens da lista
		assertNotNull(errors.get(0).get("message"));
		assertNotNull(errors.get(1).get("message"));
//		Verifica se a mensagem que retornará do campo message é a esperada
		assertEquals("A Idade é requerida!", errors.get(0).get("message"));
		assertEquals("O Nome é requerido!", errors.get(1).get("message"));
	}
	
	@Test
	@DisplayName("Should list all users")
	@Order(3)
	public void listAllUsersTest() {
		
		given()
			.contentType(ContentType.JSON)
		.when()
			.get(apiURL)
		.then()
			.statusCode(200)
			.body("size()", Matchers.is(1)); // verifica se o tamanho da lista que retornou é igual a 1, só vai dar certo se o teste rodar dps do teste de criar o usuario
	}
	
	
	
}
