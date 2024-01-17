package fun.freechat.model;

import java.util.Objects;

public class HotTag {
    private String content;
    private Long count;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HotTag hotTag)) return false;
        return Objects.equals(getContent(), hotTag.getContent()) && Objects.equals(getCount(), hotTag.getCount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContent(), getCount());
    }

    @Override
    public String toString() {
        return "HotTag{" +
                "content='" + content + '\'' +
                ", count=" + count +
                '}';
    }
}
