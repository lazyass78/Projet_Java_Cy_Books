package Model;

/**
 * Represents the topics that a document can address.
 */
public class Topic {
    private String keyword;

    /**
     * Constructs a Topic object with the specified keyword.
     *
     * @param keyword the keyword representing the topic
     */
    public Topic(String keyword) {
        this.keyword = keyword;
    }
    /**
     * Returns the keyword representing the topic.
     *
     * @return the keyword representing the topic
     */
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
