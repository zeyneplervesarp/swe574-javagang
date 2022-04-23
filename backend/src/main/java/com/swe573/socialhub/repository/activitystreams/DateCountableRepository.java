package com.swe573.socialhub.repository.activitystreams;

import java.util.Date;

public interface DateCountableRepository {
    long countByDateBetween(Date createdGt, Date createdLt);
}
