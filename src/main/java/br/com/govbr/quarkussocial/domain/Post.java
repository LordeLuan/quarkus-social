package br.com.govbr.quarkussocial.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Table(name = "posts")
@Data
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "texto")
	private String texto;
	
	@CreationTimestamp
	@Column(name = "dataCriacao")
	private LocalDateTime data;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
//	Annotation para antes de persistir qualquer post no banco, ser executado o código dentro do metódo.
//	@PrePersist
//	public void prePersist() {
//		setData(LocalDateTime.now());
//	}
	
	
}
