package com.swe573.socialhub.repository.activitystreams;

import java.util.Date;

public interface DateCountableRepository {
    Long countByDateBetween(Date createdGt, Date createdLt);
}
