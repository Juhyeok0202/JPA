package jqpl;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.changeTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.changeTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.changeTeam(teamB);
            em.persist(member3);

            //FLUSH 자동 호출 commit, query, 강제호출
            //DB 동기화는 됐으나, 영속성 컨텍스트에는 20살 반영이 안되어있음 -> clear필요
            //Application과 DB의 값이 달라짐.
            // 벌크연산을 먼저 하거나 벌크연산 이후 영속성컨텍스트 clear 필요
            int resultCount = em.createQuery("update Member m set m.age = 20")
                    .executeUpdate();
            System.out.println("====BEFORE===="); //0
            Member findMember_before = em.find(Member.class, member1.getId());
            System.out.println("findMember.getAge() = " + findMember_before.getAge());

            em.clear(); //기존 것들 준영속으로 만드니 주의

            System.out.println("====AFTER===="); //20
            Member findMember_after = em.find(Member.class, member1.getId());
            System.out.println("findMember.getAge() = " + findMember_after.getAge());
            System.out.println("resultCount = " + resultCount);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}

