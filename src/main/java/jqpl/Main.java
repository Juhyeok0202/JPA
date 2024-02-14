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
            String query = "select t from Team t join fetch t.members"; //지연로딩보다 fetch join이 우선
            List<Team> resultList = em.createQuery(query, Team.class)
                    .getResultList();

            for (Team team : resultList) {
                System.out.println("team = " + team.getName() + "|members=" + team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println("->  member = " + member);

                    /*OUTPUT

                    team = 팀A|members=2
                    ->  member = Member{id=3, username='회원1', age=0}
                    ->  member = Member{id=4, username='회원2', age=0}
                    team = 팀A|members=2 (위와 똑같은 팀 A임. 이미 영속성컨텍스트 위에 존재)
                    ->  member = Member{id=3, username='회원1', age=0}
                    ->  member = Member{id=4, username='회원2', age=0}
                    team = 팀B|members=1
                    ->  member = Member{id=5, username='회원3', age=0}
                     */
                }
            }

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