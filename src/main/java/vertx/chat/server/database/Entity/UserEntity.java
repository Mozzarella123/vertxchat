package vertx.chat.server.database.Entity;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "user", schema = "chat")
public class UserEntity {
    private int id;
    private String password;
    private String passwordSalt;
    private String username;
    private Byte emailConfirmed;
    private String email;
    private Collection<MessagesEntity> messagesById;
    private Collection<MessagesEntity> messagesById_0;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "password", nullable = true, length = 255)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "password_salt", nullable = true, length = 255)
    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    @Basic
    @Column(name = "username", nullable = true, length = 255)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "emailConfirmed", nullable = true)
    public Byte getEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(Byte emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    @Basic
    @Column(name = "email", nullable = true, length = 255)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (id != that.id) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (passwordSalt != null ? !passwordSalt.equals(that.passwordSalt) : that.passwordSalt != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (emailConfirmed != null ? !emailConfirmed.equals(that.emailConfirmed) : that.emailConfirmed != null)
            return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (passwordSalt != null ? passwordSalt.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (emailConfirmed != null ? emailConfirmed.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    public void setMessagesById(Collection<MessagesEntity> messagesById) {
        this.messagesById = messagesById;
    }

    @OneToMany(mappedBy = "userByFrom")
    public Collection<MessagesEntity> getMessagesById() {
        return messagesById;
    }

    @OneToMany(mappedBy = "userByTo")
    public Collection<MessagesEntity> getMessagesById_0() {
        return messagesById_0;
    }

    public void setMessagesById_0(Collection<MessagesEntity> messagesById_0) {
        this.messagesById_0 = messagesById_0;
    }
}
