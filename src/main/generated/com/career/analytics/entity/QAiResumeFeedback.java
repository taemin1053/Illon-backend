package com.career.analytics.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAiResumeFeedback is a Querydsl query type for AiResumeFeedback
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAiResumeFeedback extends EntityPathBase<AiResumeFeedback> {

    private static final long serialVersionUID = -1371484966L;

    public static final QAiResumeFeedback aiResumeFeedback = new QAiResumeFeedback("aiResumeFeedback");

    public final StringPath feedbackResult = createString("feedbackResult");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> resumeId = createNumber("resumeId", Long.class);

    public QAiResumeFeedback(String variable) {
        super(AiResumeFeedback.class, forVariable(variable));
    }

    public QAiResumeFeedback(Path<? extends AiResumeFeedback> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAiResumeFeedback(PathMetadata metadata) {
        super(AiResumeFeedback.class, metadata);
    }

}

