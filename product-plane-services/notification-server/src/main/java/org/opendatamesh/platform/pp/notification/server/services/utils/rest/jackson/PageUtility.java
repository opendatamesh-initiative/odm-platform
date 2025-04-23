package org.opendatamesh.platform.pp.notification.server.services.utils.rest.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

//This class extends the PageImpl with an empty constructor to allow
//Jackson to deserialize Paged results
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageUtility<T> extends PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PageUtility(@JsonProperty("content") List<T> content,
                       @JsonProperty("number") int number,
                       @JsonProperty("size") int size,
                       @JsonProperty("totalElements") Long totalElements,
                       @JsonProperty("pageable") JsonNode pageable,
                       @JsonProperty("last") boolean last,
                       @JsonProperty("totalPages") int totalPages,
                       @JsonProperty("sort") JsonNode sort,
                       @JsonProperty("first") boolean first,
                       @JsonProperty("numberOfElements") int numberOfElements) {

        super(content, PageRequest.of(number, size), totalElements);
    }

    public PageUtility(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public PageUtility(List<T> content) {
        super(content);
    }

    public PageUtility() {
        super(new ArrayList<>());
    }
}