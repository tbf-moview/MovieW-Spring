package com.moview.repository;



import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.moview.model.entity.Member;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	private Member testMember;

	@BeforeEach
	public void setUp() {
		testMember = new Member("test@test.com","테스트");
		memberRepository.save(testMember);
	}

	@AfterEach
	public void tearDown() {
		memberRepository.deleteAll();
	}

	@Test
	public void testFindById() {
		// given
		String memberEmail = testMember.getEmail();

		// when
		Member foundMember = memberRepository.findByEmail(memberEmail).orElse(null);

		// then
		assertThat(foundMember).isNotNull();
		assertThat(foundMember.getNickname()).isEqualTo(testMember.getNickname());
		assertThat(foundMember.getEmail()).isEqualTo(testMember.getEmail());
	}

	@Test
	public void testDeleteById() {
		// given
		String memberEmail = testMember.getEmail();

		// when
		memberRepository.deleteMemberByEmail(memberEmail);

		// then
		assertThat(memberRepository.findByEmail(memberEmail)).isEmpty();
	}
}