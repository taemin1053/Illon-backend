package com.career.analytics.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAward is a Querydsl query type for Award
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAward extends EntityPathBase<Award> {

    private static final long serialVersionUID = -850390371L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAward award = new QAward("award");

    public final DatePath<java.time.LocalDate> awardDate = createDate("awardDate", java.time.LocalDate.class);

    public final StringPath awardName = createString("awardName");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath organization = createString("organization");

    public final QResume resume;

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QAward(String variable) {
        this(Award.class, forVariable(variable), INITS);
    }

    public QAward(Path<? extends Award> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAward(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAward(PathMetadata metadata, PathInits inits) {
        this(Award.class, metadata, inits);
    }

    public QAward(Class<? extends Award> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.resume = inits.isInitialized("resume") ? new QResume(forProperty("resume"), inits.get("resume")) : null;
    }

}

