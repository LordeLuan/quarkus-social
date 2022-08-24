package br.com.govbr.testes;

import static io.restassured.RestAssured.given;

import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.govbr.quarkussocial.domain.Follower;
import br.com.govbr.quarkussocial.domain.Post;
import br.com.govbr.quarkussocial.domain.User;
import br.com.govbr.quarkussocial.dto.CreatePostRequest;
import br.com.govbr.quarkussocial.repositories.FollowerRepositorie;
import br.com.govbr.quarkussocial.repositories.PostRepositorie;
import br.com.govbr.quarkussocial.repositories.UserRepositorie;
import br.com.govbr.quarkussocial.resource.PostResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)  // Seta o endpoint que deverá ser feitos os testes
public class PostResourceTest {

	Long userId;
	Long userNotFollowerId;
	Long userFollowerId;
	
//	Rodará antes de cada teste ser executado
	@BeforeEach
	@Transactional
	public void setUP() {
//		Usuario padrão de teste
		var user = new User();
		user.setNome("Fulano");
		user.setIdade(22);
		UserRepositorie rep = new UserRepositorie();
		rep.persist(user);
		userId = user.getId();
		
//		Usuario não seguidor de teste
		var userNotFollower = new User();
		userNotFollower.setNome("Sicrano");
		userNotFollower.setIdade(22);
		rep.persist(userNotFollower);
		userNotFollowerId = userNotFollower.getId();
		
//		Seguidor de teste
		var userFollower = new User();
		userFollower.setNome("Beltrano");
		userFollower.setIdade(22);
		rep.persist(userFollower);
		userFollowerId = userFollower.getId();
		
//		registro de seguirdor 
		Follower follower = new Follower();
		follower.setUser(user);
		follower.setFollower(userFollower);
		FollowerRepositorie folRep = new FollowerRepositorie();
		folRep.persist(follower);
		
//		Criando postagem
		Post post = new Post();
		post.setTexto("Testando o teste");
		post.setUser(user);
		PostRepositorie postRep = new PostRepositorie();
		postRep.persist(post);
	}
	
	@Test
	@DisplayName("should create a post for a user")
	public void createPostTest() {
		var postRequest = new CreatePostRequest();
		postRequest.setTexto("Some text");

		given()
			.contentType(ContentType.JSON)
			.body(postRequest)
			.pathParam("userId", userId)
		.when()
			.post() // não precisa passar endpoint pq já tem definido
		.then()
			.statusCode(201);
	}
	
	@Test
	@DisplayName("should return 404 when trying to make a post for an inexistent user")
	public void postForAnInexistentUser() {
		var postRequest = new CreatePostRequest();
		postRequest.setTexto("Some text");
		
		var inexistenteUserId = 999;
//		Montando a requisição
		given()
			.contentType(ContentType.JSON)
			.body(postRequest)
			.pathParam("userId", inexistenteUserId)
//			Faz a requisição
		.when()
			.post() // não precisa passar endpoint pq já tem definido
//			Retorno da requisição
		.then()
			.statusCode(404);
	}
	
	@Test
	@DisplayName("Should return 404 when user doesn't exist")
	public void listPostUserNotFoundTest() {
		var inexistenteUserId = 999;
		
		given()
			.pathParam("userId", inexistenteUserId)
		.when()
			.get()
		.then()
			.statusCode(404);
	}
	
	@Test
	@DisplayName("Should return 400 when followerId header is not present")
	public void listPostFollwerHeaderNotSendTest() {
		
		given()
			.pathParam("userId", userId)
		.when()
			.get()
		.then()
			.statusCode(400)
			.body(Matchers.is("Id do seguidor está nulo, verifique o header da requisição."));
	}
	
	@Test
	@DisplayName("Should return 400 when followerId  doesn't exist")
	public void listPostFollwerNotFoundtest() {
		
		var inexistenteUserId = 999;
		
		given()
			.pathParam("userId", userId)
			.header("followerId", inexistenteUserId)
		.when()
			.get()
		.then()
			.statusCode(404);
		
	}
	
	@Test
	@DisplayName("Should return 403 when follower doesn't follow")
	public void listPostFollwerNotAFollowerTest() {
		
		given()
			.pathParam("userId", userId)
			.header("followerId", userNotFollowerId)
		.when()
			.get()
		.then()
			.statusCode(403)
			.body(Matchers.is("Você não pode ver esses posts"));
	}
	
	@Test
	@DisplayName("Should list posts")
	public void listPostTest() {
		
		given()
		.pathParam("userId", userId)
		.header("followerId", userFollowerId)
	.when()
		.get()
	.then()
		.statusCode(200)
		.body("size()", Matchers.is(1));
		
	}
	
}
