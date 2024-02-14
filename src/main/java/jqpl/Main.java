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

            em.flush();
            em.clear();

            // 컬렉션 페치 조인
            String query = "select distinct t from Team t join fetch t.members"; //지연로딩보다 fetch join이 우선
            List<Team> resultList = em.createQuery(query, Team.class)
                    .getResultList();

            System.out.println("resultList.size() = " + resultList.size());
            /*
            [select t from Team] t -> size 2
            [select t from Team t join fetch t.members] -> size 3 (join하며 TeamA의 data가 늘어남.중복.)
            [select distinct t from Team t join fetch t.members] -> size 2
            위 쿼리는 ID(PK)그리고 NAME까지 모~두 같아야 distinct가 적용된다.
            하지만, JPA에서 위 쿼리의 결과가 application으로 올라올 때, 중복된 엔티티를 제거한다.
            같은 식별자를 가진 것을 제거
             */

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