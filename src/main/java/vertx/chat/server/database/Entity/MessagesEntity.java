package vertx.chat.server.database.Entity;

import javax.persistence.*;

@Entity
@Table(name = "messages", schema = "chat")
public class MessagesEntity {
    private String content;
    private int id;
    private UserEntity userByFrom;
    private UserEntity userByTo;

    @Basic
    @Column(name = "content", nullable = true, length = 255)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessagesEntity that = (MessagesEntity) o;

        if (id != that.id) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + id;
        return result;
    }

    public void setUserByFrom(UserEntity userByFrom) {
        this.userByFrom = userByFrom;
    }

    @ManyToOne
    @JoinColumn(name = "from", referencedColumnName = "id")
    public UserEntity getUserByFrom() {
        return userByFrom;
    }


    @ManyToOne
    @JoinColumn(name = "to", referencedColumnName = "id")
    public UserEntity getUserByTo() {
        return userByTo;
    }

    public void setUserByTo(UserEntity userByTo) {
        this.userByTo = userByTo;
    }
}
