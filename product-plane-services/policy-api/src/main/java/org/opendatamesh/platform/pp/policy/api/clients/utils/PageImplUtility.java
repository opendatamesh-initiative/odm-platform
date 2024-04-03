package org.opendatamesh.platform.pp.policy.api.clients.utils;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

//This class extends the PageImpl with an empty constructor to allow
//Jackson to deserialize Paged results
public class PageImplUtility<T> extends PageImpl<T> {
    public PageImplUtility() {
        super(new ArrayList<>());
    }

    public PageImplUtility(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public PageImplUtility(List<T> content) {
        super(content);
    }
}
