package entertainment;
import lombok.Getter;
@Getter

public final class MoiveSerialMostViewed {
    private final String name;
    private final int viewNumber;

    public MoiveSerialMostViewed(final String name, final int viewNumber) {
        this.name = name;
        this.viewNumber = viewNumber;
    }

    @Override
    public String toString() {
        return name;
    }
}
