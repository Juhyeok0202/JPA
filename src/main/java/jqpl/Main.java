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

            String query = "select m from Member m join fetch m.team"; //지연로딩보다 fetch join이 우선
            List<Member> resultList = em.createQuery(query, Member.class)
                    .getResultList();

            for (Member member : resultList) {
                System.out.println("member = " + member.getUsername() + member.getTeam().getName());

                /*일반 Inner Join*/
                //회원1, 팀A(SQL)
                //회원2, 팀A(1차캐시)
                //회원3, 팀B(SQL)

                //회원 100명 -> N + 1

                /*Fetch Join*/
                //회원1, 팀A(1차캐시)
                //회원2, 팀A(1차캐시)
                //회원3, 팀B(1차캐시)

                //회원 100명 -> 1 (Proxy가 아닌, 처음부터 Join하여 가져와 영속성 컨텍스트에 올려둠)

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