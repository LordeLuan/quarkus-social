package br.com.govbr.testes;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.govbr.quarkussocial.domain.Follower;
import br.com.govbr.quarkussocial.domain.User;
import br.com.govbr.quarkussocial.dto.CreateFollowerRequest;
import br.com.govbr.quarkussocial.repositories.FollowerRepositorie;
import br.com.govbr.quarkussocial.repositories.UserRepositorie;
import br.com.govbr.quarkussocial.resource.FollowerResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
public class FollowerResourceTest {

	Long userId;
	Long userFollowerId;
	
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
	}
	
	
	@Test
	@DisplayName("Should return 409 when followerId is equal to userId")
	public void sameUserAsFollowerTest() {
//		Seta o mesmo id do usuario para o seguidor
		var body = new CreateFollowerRequest();
		body.setFollowerId(userId);
		
		given()
			.contentType(ContentType.JSON)
			.body(body)
			.pathParam("userId", userId)
		.when()
			.put()
		.then()
			.statusCode(Response.Status.CONFLICT.getStatusCode())
			.body(Matchers.is("Não é possivel seguir você mesmo!"));
		
	}
	
	@Test
	@DisplayName("Should return 404 when user id doesn't exist")
	public void userNotFoundWhenTringToFollowTest() {
		
		var body = new CreateFollowerRequest();
		body.setFollowerId(userId);
		
		var inexistentUserId = 999;
		
		given()
			.contentType(ContentType.JSON)
			.body(body)
			.pathParam("userId",inexistentUserId)
		.when()
			.put()
		.then()
			.statusCode(Response.Status.NOT_FOUND.getStatusCode());
		
	}
	
	@Test
	@DisplayName("Should follow a user")
	public void followUserTest() {
		
		var body = new CreateFollowerRequest();
		body.setFollowerId(userFollowerId);
		
		given()
			.contentType(ContentType.JSON)
			.body(body)
			.pathParam("userId", userId)
		.when()
			.put()
		.then()
			.statusCode(Response.Status.NO_CONTENT.getStatusCode());
		
	}
	
	@Test
	@DisplayName("Should return 404 on list user follower and User id doesn't exist")
	public void userNotFoundTest() {

		var inexistentUserId = 999;
		
		given()
			.contentType(ContentType.JSON)
			.pathParam("userId",inexistentUserId)
		.when()
			.get()
		.then()
			.statusCode(Response.Status.NOT_FOUND.getStatusCode());
		
	}
	
    @Test
    @DisplayName("should list a user's followers")
    public void listFollowersTest(){
        var response =
                given()
                    .contentType(ContentType.JSON)
                    .pathParam("userId", userId)
                .when()
                    .get()
                .then()
                    .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("response");

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());

    }
	
	
	@Test
	@DisplayName("Should return 404 on unfollow user and User id doesn't exist")
	public void userNotFoundWhenUnfollowAUserTest() {
		var inexistentUserId = 999;
		
		given()
			.contentType(ContentType.JSON)
			.pathParam("userId",inexistentUserId)
			.queryParam("followerId", userFollowerId)
		.when()
			.delete()
		.then()
			.statusCode(Response.Status.NOT_FOUND.getStatusCode());
		
	}
	
	@Test
	@DisplayName("Should unfollow an user")
	public void unfollowAnUserTest() {
		
		given()
			.pathParam("userId", userId)
			.queryParam("followerId", userFollowerId)
		.when()
			.delete()
		.then()
			.statusCode(Response.Status.NO_CONTENT.getStatusCode());
		
	}
	
	
}
