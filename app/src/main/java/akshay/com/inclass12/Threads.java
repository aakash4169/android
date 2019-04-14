package akshay.com.inclass12;

public class Threads {
    String name, user_id, id, title, created_at;

    public Threads(String name, String user_id, String title, String created_at) {
        this.name = name;
        this.user_id = user_id;
        this.title = title;
        this.created_at = created_at;
    }

    public Threads() {
    }

    @Override
    public String toString() {
        return "Threads{" +
                "name='" + name + '\'' +
                ", user_id='" + user_id + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
