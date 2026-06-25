package com.career.analytics.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCoverLetter is a Querydsl query type for CoverLetter
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoverLetter extends EntityPathBase<CoverLetter> {

    private static final long serialVersionUID = 157673213L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCoverLetter coverLetter = new QCoverLetter("coverLetter");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QResume resume;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QCoverLetter(String variable) {
        this(CoverLetter.class, forVariable(variable), INITS);
    }

    public QCoverLetter(Path<? extends CoverLetter> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCoverLetter(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCoverLetter(PathMetadata metadata, PathInits inits) {
        this(CoverLetter.class, metadata, inits);
    }

    public QCoverLetter(Class<? extends CoverLetter> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.resume = inits.isInitialized("resume") ? new QResume(forProperty("resume"), inits.get("resume")) : null;
    }

}

