package hcmute.edu.vn.chatbot_ec.response;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;
public class PageResponse<T> {
    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("totalElements")
    private long totalElements;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("content")
    private List<T> content = Collections.emptyList();

    // Getters and Setters
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}