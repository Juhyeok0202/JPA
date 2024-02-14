package jqpl;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = : username"
)
public class Member {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private int age;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @ManyToOne(fetch = FetchType.LAZY) // Proxy로 가져오게 됨.
    @JoinColumn(name = "TEAM_ID")
    private Team team;

//    @OneToMany(mappedBy = "member")
//    private List<Order> orders = new ArrayList<>();

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
//
//    public List<Order> getOrders() {
//        return orders;
//    }
//
//    public void setOrders(List<Order> orders) {
//        this.orders = orders;
//    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
